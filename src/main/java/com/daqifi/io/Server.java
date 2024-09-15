package com.daqifi.io;

import com.daqifi.common.components.DtoAConverter;
import com.daqifi.common.devices.Device;
import com.daqifi.common.devices.Nyquist1;
import com.daqifi.common.messages.ProtoMessageV2;
import com.daqifi.io.generators.Generator;
import com.daqifi.io.generators.SineGenerator;
import com.google.protobuf.ByteString;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static com.daqifi.common.devices.Nyquist1.ANALOG_RES;


/**
 * Emulator Server.
 */
public class Server extends Thread {
    private static Logger log = Logger.getLogger(Server.class.getName());
    private static final long BASE_SERIAL_NUMBER = 4788544735461581972L;
    private final InetAddress ip = initIp();
    private int port;
    private long serialNumber;
    private DataInterpreter clientConnectionInterpreter;
    static int SAMPLES_PER_SEC = 100;
    private DataThread dt;

    public Server(int port, DataInterpreter dataInterpreter) {
        this.port = port;
        this.clientConnectionInterpreter = dataInterpreter;
        this.serialNumber = getSerialNumberForPort(port);

        log.info(String.format("Listening on port %d\nSerial number: %d", port, serialNumber));
        start();
    }

    @Override
    public void run() {
        try {
            ServerSocket sserver = new ServerSocket(port);
            BufferedReader in;
            while (sserver.isBound()) {
                try {
                    Socket clientSocket = sserver.accept();
                    log.info("Accepting connection...");
                    clientSocket.setTcpNoDelay(true);
                    clientSocket.setSendBufferSize(50000);

                    in = new BufferedReader(new InputStreamReader(
                            clientSocket.getInputStream()));
                    while (clientSocket.isConnected()) {
                        String command = in.readLine();
                        if (command == null) {
                            log.info("Connection Closed");
                            break;
                        }
                        command = command.toLowerCase();
                        log.info(command);
                        String[] splitString = command.split("[?]");
                        String data;
                        if (command.contains("system:startstreamdata")) {
                            log.info("enabling streaming");
                            log.info(String.format("TCP send buffer size: %d",
                                    clientSocket.getSendBufferSize()));
                            int samplesPerSecond = SAMPLES_PER_SEC;
                            String[] split = command.split("[ ]");
                            if (split.length == 2) {
                                samplesPerSecond = Integer.parseInt(split[1]);
                            }
                            dt = new DataThread(clientSocket.getOutputStream(),
                                    samplesPerSecond);

                            data = "Protobuf streaming";
                        } else if (command.contains("system:stopstreamdata")) {
                            if (dt != null) {
                                dt.running = false;
                            }
                            data = "Stop streaming";
                        } else if (command.contains("configure:adc:channel") || command.contains("enable:voltage:dc")) {
                            String[] split = command.split("[ ]");
                            channelMask = Integer.parseInt(split[1]);
                            data = String.format("Set channel mask to %s",
                                    Integer.toBinaryString(channelMask));
                        } else if (command.contains("configure:adc:range")) {
                            String[] split = command.split("[ ]");
                            adcRange = Integer.parseInt(split[1]);
                            data = String.format("Set range to %d", adcRange);
                        } else if (command.contains("system:sysinfopb")) {
                            //WiFiDAQOutMessage msg = getWifiDAQOutMessage();
                            ProtoMessageV2.DaqifiOutMessage msg = getOutMessage();
                            msg.writeDelimitedTo(clientSocket.getOutputStream());
                            data = msg.toString();
                        } else if (command.contains("system:echo")) {
                            data = "Not implemented";
                        } else if (splitString.length == 2) {
                            Date time = new Date();
                            String scpicommand = splitString[0];
                            // Check the commands to see what we are measuring:
                            if (scpicommand.equals("measure:ext:adc")) {
                                // Return an analog channel measurement value
                                double channel = Double.parseDouble(splitString[1]
                                        .trim()) + time.getTime() % 1000 / 1000d;
                                data = String.format("+%9.8f\r\n", channel);
                            } else if (scpicommand.equals("input:port:state")) {
                                // Return a digital channel measurement value
                                data = String.format("%d\r\n", time.getTime() % 2);
                            } else {
                                data = "Invalid Command.";
                            }
                            clientSocket.getOutputStream().write(data.getBytes());
                        } else {
                            data = "Unknown command";
                        }
                        log.info("\t" + data);
                    }
                }
                catch (IOException err) {
                    log.warning(err.toString());
                }
            }
            sserver.close();
        } catch (IOException err) {
            log.warning(err.toString());
        }
    }

    /**
     * Converts an int value to a bytestring with the specified number of bytes.
     *
     * @param value         value to convert
     * @param numberOfBytes must be less than or equal to 4
     * @return
     */
    public static ByteString toByteString(int value, int numberOfBytes) {
        int bufferSize = 4;
        byte[] bytes = ByteBuffer.allocate(bufferSize).putInt(value).array();
        return ByteString.copyFrom(bytes, bufferSize - numberOfBytes, numberOfBytes);
    }

    public ProtoMessageV2.DaqifiOutMessage getOutMessage() {
        ProtoMessageV2.DaqifiOutMessage.Builder builder = ProtoMessageV2.DaqifiOutMessage.newBuilder();

        //builder.setAnalogPortEnabled(toByteString(getChannelMask(), 1));

        builder.setAnalogInPortNum(Nyquist1.ANALOG_IN_CHANNELS);
        builder.setAnalogInPortNumPriv(8);
        // builder.setAnalogInPortType("");

        for(int i =0; i < Nyquist1.ANALOG_IN_CHANNELS; i++) {
            builder.addAnalogInPortAvRange(4096);
            builder.addAnalogInPortAvRangePriv(4096);
        }
        builder.setDeviceFwRev("1.0.2");
        builder.setDeviceHwRev("1.0");
        builder.setDeviceSn(serialNumber);

        builder.setDigitalPortNum(Nyquist1.DIGITAL_IO_CHANNELS);
        builder.setDigitalPortType(toByteString(0, 1));

        builder.setAnalogOutPortNum(Nyquist1.ANALOG_OUT_CHANNELS);
        builder.setAnalogOutPortType(toByteString(0, 1));
        builder.setAnalogOutPortRange(5);
        builder.setAnalogOutRes(4096);

        //builder.setAnalogInP(toByteString(1, 1));
        builder.setDeviceStatus(1);
        builder.setPwrStatus(1);
        builder.setBattStatus(1);
        builder.setTempStatus(1);
        //builder.setDacBytes(2);

        builder.setDigitalPortDir(toByteString(0, 1));
        builder.setHostName(getHostName());
        builder.setIpAddr(ByteString.copyFrom(getIpAddress()));
        builder.setMacAddr(ByteString.copyFrom(getMacAddress()));
        builder.setNetMask(ByteString.copyFrom("255.255.255.255".getBytes()));
        builder.setPrimaryDns(ByteString.copyFrom("8.8.8.8".getBytes()));
        builder.setSecondaryDns(ByteString.copyFrom("1.1.1.1".getBytes()));

        builder.setSsid("1111");
        builder.setDevicePn("Nq1");
        builder.setDevicePort(port);

        builder.setAnalogInRes(ANALOG_RES);
        builder.setAnalogInResPriv(ANALOG_RES);

        builder.setTimestampFreq(Device.DEFAULT_DEVICE_TIMESTAMP_FREQUENCY);

        return builder.build();
    }

    float adcRange = 5.0f;

    protected float getAdcRange() {
        return adcRange;
    }

    int channelMask = 0;

    protected int getChannelMask() {
        return channelMask;
    }

    protected InetAddress initIp() {
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException err) {
            err.printStackTrace();
        }
        return null;
    }

    protected String getHostName() {
        if (ip != null) {
            return ip.getHostName();
        }
        return "Unknown";
    }

    protected byte[] getIpAddress() {
        if (ip != null) {
            return ip.getAddress();
        }
        byte[] unknownIp = new byte[4];
        for (int i = 0; i < unknownIp.length; i++) {
            unknownIp[i] = (byte) 0;
        }
        return unknownIp;
    }

    protected byte[] getMacAddress() {
        byte[] mac = new byte[6];
        mac[0] = (byte) 0x02;
        mac[1] = (byte) 0x00;
        mac[2] = (byte) 0x00;
        mac[3] = (byte) ((port >> 16) & 0xFF);
        mac[4] = (byte) ((port >> 8) & 0xFF);
        mac[5] = (byte) (port & 0xFF);
        return mac;
    }

    public class DataThread extends Thread {
        private final OutputStream out;
        private boolean running;
        private final int saplesPerSecond;
        private int dt;
        private final double TOL = 0.9;
        private final Generator dataGen;
        private final float SINE_WAVE_PERIOD = 1;

        public DataThread(OutputStream os, int samplesPerSecond) {
            this.out = os;
            this.running = true;
            this.saplesPerSecond = samplesPerSecond;
            this.dt = convertSampleRateToDt(samplesPerSecond);

            float max = getAdcRange();
            this.dataGen = new SineGenerator(max * 0.99f / 2, (float) (2 * Math.PI / (1_000_000 * SINE_WAVE_PERIOD)), max / 2);
            //this.dataGen = new Limiter(new CompositeGenerator(
            //        new SineGenerator(max*0.99f, (float) (2 * Math.PI / 10f), 0f),
            //        new SineGenerator(1f, (float) (2 * Math.PI / 1000f), 0f)), -1*max, max);
            start();
        }

        private int convertSampleRateToDt(int sps) {
            return (int) Math.floor((1.0 / sps) * 1000 * TOL);
        }

        @Override
        public void run() {
            double dtPerSample = 1d / saplesPerSecond;
            long waitInMicros = Math.round(Math.floor(dtPerSample * 1_000_000));
            try {
                int sequence = 0;
                long lastTimeLogged = System.currentTimeMillis();
                int startTimeSeq = 0;
                while (running) {

                    if (sequence - startTimeSeq == saplesPerSecond) {
                        long now = System.currentTimeMillis();
                        long delta = now - lastTimeLogged;
                        if (delta > 1000) {
                            // When the lag goes above 10%, display an error
                            // message with the actual data rate
                            if (delta > 1100) {
                                log.severe(String
                                        .format("Unable to stream at requested rate. Actual data rate: %f Hz",
                                                1000f * saplesPerSecond / delta));
                            }
                            lastTimeLogged = now;
                            startTimeSeq = sequence;

                            if (waitInMicros > 0) {
                                waitInMicros = waitInMicros - 10;
                            }
                        } else {
                            // We've already sent the number of samples for the
                            // current second. If we are sending too fast, slow down the wait time
                            if (delta < 900) {
                                waitInMicros = waitInMicros + 10;
                            }
                        }
                    }

                    sequence += 1;
                    buildDataMessageV2(System.nanoTime() / 1000);
                    waitFor(waitInMicros);
                }
            } catch (IOException err) {
                log.warning("Exception caught. Stopping data generation. Error: "
                        + err.toString());
            } catch (InterruptedException err) {
                log.warning("Exception caught. Stopping data generation. Error: "
                        + err.toString());
            }
        }

        private void waitFor(long micros) throws InterruptedException {
            if (micros <= 0) return;

            if (micros > 10_000) {
                Thread.sleep(TimeUnit.MILLISECONDS.convert(micros, TimeUnit.MICROSECONDS));
            } else {
                long waitUntil = System.nanoTime() + (micros * 1_000);
                while (waitUntil > System.nanoTime()) {
                    ;
                }
            }
        }

        private void buildDataMessageV2(long time) throws IOException {
            ProtoMessageV2.DaqifiOutMessage.Builder builder = ProtoMessageV2.DaqifiOutMessage.newBuilder();

            builder.setMsgTimeStamp((int) time);
            for (int jj = 0; jj < Nyquist1.ANALOG_IN_CHANNELS; jj++) {
                int bit = 1 << jj;
                if ((bit & channelMask) == bit) {
                    long t = time + Math.round(jj * (SINE_WAVE_PERIOD / (float) Nyquist1.ANALOG_IN_CHANNELS) * 1_000_000);
                    float value = dataGen.getValue(t);
                    builder.addAnalogInData(DtoAConverter.convertVoltageToInt(value, ANALOG_RES, getAdcRange()));
                    builder.addAnalogInPortRange(getAdcRange());
                    builder.addAnalogInIntScaleM(1f);
                    builder.addAnalogInCalB(0);
                    builder.addAnalogInCalM(1);
                }
            }
            byte[] di = new byte[1];
            di[0] = (time % 2 == 0) ? (byte) -1 : 0;

            builder.setDigitalData(ByteString.copyFrom(di));
            builder.build().writeDelimitedTo(out);
            out.flush();
        }
    }

    private long getSerialNumberForPort(int port) {
        return BASE_SERIAL_NUMBER + port;
    }
}
