// Copyright 2013 Marc Bernardini.
package com.daqifi.common.devices;

import main.java.com.daqifi.io.DeviceMessageInterpreter;
import main.java.com.daqifi.io.MessageConsumer;
import main.java.com.daqifi.io.SocketConnector;
import main.java.com.daqifi.io.messages.Message;
import com.daqifi.common.components.DataBuffer;
import com.daqifi.common.devices.channels.AnalogInputChannel;
import com.daqifi.common.devices.channels.AnalogOutputChannel;
import com.daqifi.common.devices.channels.Channel;
import com.daqifi.common.devices.channels.ChannelInterface;
import com.daqifi.common.devices.channels.DigitalInputChannel;
import com.daqifi.common.devices.scpi.Command;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

public class AD7195W extends Device implements DeviceInterface {

    private static Logger log = Logger.getLogger(AD7195W.class.getName());

    public static final int ANALOG_IN_CHANNELS = 8;
    public static final int ANALOG_OUT_CHANNELS = 8;

    public static final int DIGITAL_IO_CHANNELS = 1;

    private SocketConnector connection;
    private int samplesPerSecond;
    private final DeviceMessageInterpreter dataInterpreter = new DeviceMessageInterpreter();

    /**
     * Inner instance class used to send delayed commands. All commands on the
     *  Queue are sent once the SocketConnector changes to connected.
     */
    private class DelayedCommandSender implements PropertyChangeListener{
        private Queue<Command> q;
        DelayedCommandSender(Queue<Command> q){
            this.q = q;
        }

        @Override
        public void propertyChange(PropertyChangeEvent event) {
            if(event.getNewValue() == SocketConnector.State.Connected){
                while (!q.isEmpty() && connection != null && connection.isConnected()){
                    AD7195W.this.send(q.poll());
                }
            }
        }
    }

    private Queue<Command> msgQueue = new ConcurrentLinkedQueue<Command>();

    private MessageChannelRouter channelRouter = null;
    private final int numberOfAnalogInChannels;
    private final int numberOfAnalogOutChannels;
    private final int numberOfDigitalIoChannels;
    private List<ChannelInterface> analogChannels;
    private List<ChannelInterface> analogOutChannels;
    private List<ChannelInterface> digitalChannels;

    public AD7195W(int numberOfAnalogInChannels, int numberOfAnalogOutChannels, int numberOfDigitalIoChannels) {
        this.numberOfAnalogInChannels = numberOfAnalogInChannels;
        this.numberOfAnalogOutChannels = numberOfAnalogOutChannels;
        this.numberOfDigitalIoChannels = numberOfDigitalIoChannels;
        samplesPerSecond = DEFAULT_SAMPLES_PER_SECOND;
        analogChannels = initializeAnalogInChannels();
        analogOutChannels = initializeAnalogOutChannels();
        digitalChannels = initializeDigitalChannels();

        channelRouter = new MessageChannelRouter(Channel.filter(analogChannels, ChannelInterface.Type.ANALOG_IN),
                digitalChannels, analogMathChannels, this);
        dataInterpreter.registerObserver(channelRouter);
    }

    public AD7195W(){
        this(ANALOG_IN_CHANNELS, ANALOG_OUT_CHANNELS, DIGITAL_IO_CHANNELS);
    }

    @Override
    public String getDeviceType() {
        return "AD7195W";
    }


    @Override
    public Collection<ChannelInterface> getChannels() {
        Collection<ChannelInterface> ch = new ArrayList<ChannelInterface>(
                getNumberOfAnalogInChannels() + getNumberOfAnalogOutChannels() + getNumberOfDigitalInChannels() + getNumberOfMathChannels());
        ch.addAll(analogChannels);
        ch.addAll(analogOutChannels);
        ch.addAll(digitalChannels);
        ch.addAll(analogMathChannels);
        return ch;
    }

    @Override
    public int getNumberOfAnalogInChannels() {
        return numberOfAnalogInChannels;
    }


    @Override
    public int getNumberOfAnalogOutChannels() {
        return numberOfAnalogOutChannels;
    }

    @Override
    public int getNumberOfDigitalInChannels() {
        return numberOfDigitalIoChannels;
    }

    @Override
    public void connect() {
        msgQueue.add(new Command("system:echo -1"));
        msgQueue.add(new Command("SYSTem:SYSInfoPB?"));

        this.connection = new SocketConnector(
                getNetworkAddress().getHostName(), getNetworkAddress()
                .getPort(), dataInterpreter);

        this.connection.addChangeListener(new DelayedCommandSender(msgQueue));
    }

    @Override
    public void disconnect(){
        if(this.connection != null) this.connection.close();
    }

    @Override
    public SocketConnector getConnection() {
        // TODO: This method probably needs to be refactored to not have to pass
        // out the connection.
        return connection;
    }

    @Override
    public void addOutputBuffer(DataBuffer buffer) {
        channelRouter.addOutputBuffer(buffer);
    }

    @Override
    public void removeOutputBuffer(DataBuffer buffer) {
        channelRouter.removeOutputBuffer(buffer);
    }


    private List<ChannelInterface>  initializeAnalogInChannels() {
        List<ChannelInterface> analogChannels = new ArrayList<ChannelInterface>(getNumberOfAnalogInChannels());
        for (int ii = 0; ii != getNumberOfAnalogInChannels(); ii++) {
            analogChannels.add(ii, new AnalogInputChannel("AI" + ii, ii, this));
        }
        return analogChannels;
    }

    private List<ChannelInterface>  initializeAnalogOutChannels() {
        List<ChannelInterface> channels = new ArrayList<ChannelInterface>(getNumberOfAnalogOutChannels());
        for (int ii = 0; ii != getNumberOfAnalogOutChannels(); ii++) {
            channels.add(ii, new AnalogOutputChannel("AO" + ii, ii, this));
        }
        return channels;
    }

    private List<ChannelInterface> initializeDigitalChannels() {
        List<ChannelInterface> digitalChannels = new ArrayList<ChannelInterface>(numberOfDigitalIoChannels);
        for (int ii = 0; ii != numberOfDigitalIoChannels; ii++) {
            digitalChannels.add(ii, new DigitalInputChannel("DIO" + ii, ii,
                    this));
        }
        return digitalChannels;
    }

    @Override
    public void send(Command command) {
        if (connection != null && connection.isConnected()) {
            try {
                connection.send(command.getBytes());
                connection.send("\r\n".getBytes());
                log.info(connection.toString() + "> " + command.toString());
                Thread.sleep(100);
            } catch (IOException e) {
                log.warning("Unable to send command to device: " + e.toString());
            } catch (InterruptedException ie) {
                log.warning("Sleep interrupted: " + ie.toString());
            }
        } else {
            msgQueue.offer(command);
            log.info(String.format(
                    "Command [%s] delayed due to device not being connected", command.toString()));
        }
    }

    /**
     * Starts data streaming on the device.
     */
    public void startStreaming() {
        int activeChannelBitMask = getActiveChannelMask();
        Command command = new Command("ENAble:VOLTage:DC %d", activeChannelBitMask);
        send(command);

        if(isDiStreaming()) {
            // Enables all DI channels for streaming.
            Command dioCommand = new Command("DIO:PORt:ENAble 1");
            send(dioCommand);
        }
//        Command adccommand = new Command(String.format("CONFigure:ADC:range %d",
//                getAdcResolution()));
//        send(adccommand);

        channelRouter.setNumberOfMessages(0);
        Command streamCommand = new Command("SYSTem:StartStreamData %d", samplesPerSecond);
        send(streamCommand);

        setIsStreaming(true);
    }

    public void stopStreaming() {
        send(new Command("SYSTem:StopStreamData"));
        setIsStreaming(false);
    }

    @Override
    public void updateNetworkSettings(String ssid, int securityType, String password){
        StringBuilder sb = new StringBuilder();
        sb.append("SYSTem:COMMunicate:LAN:NETType 1\r\n");
        sb.append(String.format("SYSTem:COMMunicate:LAN:SSID %s\r\n", ssid));
        sb.append(String.format("SYSTem:COMMunicate:LAN:SECURITY %d\r\n", securityType));
        if(securityType != 0) {
            sb.append(String.format("SYSTem:COMMunicate:LAN:PASs %s\r\n", password));
        }
        sb.append("SYSTem:COMMunicate:LAN:APPLY\r\n");
        sb.append("SYSTem:COMMunicate:LAN:SAVE\r\n");
        sb.append("SYSTem:REBoot");

        send(new Command(sb.toString()));
    }

    /**
     * Returns the total message count received from the device.
     *
     * @return
     */
    public int getTotalMessageCount() {
        if (channelRouter != null) {
            return channelRouter.getNumberOfMessages();
        }
        return 0;
    }

    @Override
    public void registerObserver(MessageConsumer o) {
        dataInterpreter.registerObserver(o);

    }

    @Override
    public void removeObserver(MessageConsumer o) {
        dataInterpreter.removeObserver(o);

    }

    @Override
    public void notifyObservers(Message message) {
        dataInterpreter.notifyObservers(message);
    }

    @Override
    public void setSampleFrequency(int frequency) {
        this.samplesPerSecond = frequency;
    }

    @Override
    public int getSampleFrequency() {
        return samplesPerSecond;
    }

}
