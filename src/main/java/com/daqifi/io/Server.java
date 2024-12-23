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
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static com.daqifi.common.devices.Nyquist1.ANALOG_RES;


/**
 * Emulator Server.
 */
public class Server extends Thread {
    private static final long BASE_SERIAL_NUMBER = 4788544735461581972L;
    private final InetAddress ip = initIp();
    private final Logger log;
    private final int port;
    private final long serialNumber;
    private DataInterpreter clientConnectionInterpreter;
    static int SAMPLES_PER_SEC = 100;
    private DataThread dt;

    public Server(int port, DataInterpreter dataInterpreter) {
        log = getServerLogger(port);
        this.port = port;

        this.clientConnectionInterpreter = dataInterpreter;
        this.serialNumber = getSerialNumberForPort(port);

        log.info(String.format("Listening on port %d\nSerial number: %d", port, serialNumber));
        start();
    }

    private Logger getServerLogger(int port) {
        Logger logger = Logger.getLogger(Server.class.getName() + ":" + port);

        // Remove the use of parent handlers to prevent duplicate logs
        logger.setUseParentHandlers(false);

        // Create a new console handler
        ConsoleHandler consoleHandler = new ConsoleHandler();

        // Set a custom formatter to include the logger's name (which has the port number)
        consoleHandler.setFormatter(new SimpleFormatter() {
            private static final String format = "[%1$tF %1$tT] [%2$s] %4$s: %5$s%6$s%n";

            @Override
            public synchronized String format(LogRecord lr) {
                return String.format(format,
                        new Date(lr.getMillis()),
                        lr.getLoggerName(),
                        lr.getLevel().getLocalizedName(),
                        lr.getSourceMethodName(),
                        lr.getMessage(),
                        lr.getThrown() == null ? "" : "\n" + lr.getThrown());
            }
        });

        // Add the handler to the logger
        logger.addHandler(consoleHandler);

        return logger;
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
                        log.info(port + ":" + command);
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

                            data = String.format("Protobuf streaming requested at %dHz", samplesPerSecond);
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
        private volatile boolean running;
        private final int samplesPerSecond;
        private final Generator dataGen;
        private final float SINE_WAVE_PERIOD = 1;  // 1 second period
        private int sequence;
        private final long TIMESTAMP_FREQUENCY = Device.DEFAULT_DEVICE_TIMESTAMP_FREQUENCY;

        public DataThread(OutputStream os, int samplesPerSecond) {
            if (samplesPerSecond <= 0 || samplesPerSecond > 1_000_000) {
                throw new IllegalArgumentException("Sample rate must be between 1 and 1,000,000 Hz");
            }
            
            this.out = os;
            this.running = true;
            this.samplesPerSecond = samplesPerSecond;
            this.sequence = 0;
            
            float max = getAdcRange();
            // For a 1Hz sine wave:
            float frequency = 1.0f;  // Hz
            float angularFrequency = (float)(2.0 * Math.PI * frequency);  // radians/second
            this.dataGen = new SineGenerator(
                max * 0.99f / 2,    // amplitude
                angularFrequency,   // angular frequency in radians/second
                max / 2             // offset to center the sine wave
            );
            start();
        }

        @Override
        public void run() {
            try {
                long startTimeNanos = System.nanoTime();
                long nextSampleTime = startTimeNanos;
                long samplePeriodNanos = 1_000_000_000L / samplesPerSecond;
                
                while (running) {
                    while (System.nanoTime() < nextSampleTime && running) {
                        long waitTime = nextSampleTime - System.nanoTime();
                        if (waitTime > 1_000_000) {
                            Thread.sleep(waitTime / 1_000_000);
                        }
                    }

                    if (!running) break;

                    sequence++;
                    long elapsedNanos = System.nanoTime() - startTimeNanos;
                    double elapsedSeconds = elapsedNanos / 1_000_000_000.0;
                    int timestamp = (int)(elapsedSeconds * TIMESTAMP_FREQUENCY);
                    buildDataMessageV2(timestamp);
                    
                    nextSampleTime = startTimeNanos + (sequence * samplePeriodNanos);
                }
            } catch (IOException | InterruptedException err) {
                log.warning("Exception caught. Stopping data generation. Error: " + err.toString());
            } finally {
                try {
                    out.close();
                } catch (IOException e) {
                    log.warning("Error closing output stream: " + e.toString());
                }
            }
        }

        private void buildDataMessageV2(int timestamp) throws IOException {
            ProtoMessageV2.DaqifiOutMessage.Builder builder = ProtoMessageV2.DaqifiOutMessage.newBuilder();
            builder.setMsgTimeStamp(timestamp);
            
            // Calculate the time in seconds for this sample
            double timeInSeconds = sequence / (double)samplesPerSecond;
            
            for (int jj = 0; jj < Nyquist1.ANALOG_IN_CHANNELS; jj++) {
                int bit = 1 << jj;
                if ((bit & channelMask) == bit) {
                    // Add phase offset for each channel (evenly distributed over one period)
                    double channelPhaseOffset = (jj * SINE_WAVE_PERIOD) / Nyquist1.ANALOG_IN_CHANNELS;
                    double channelTimeInSeconds = timeInSeconds + channelPhaseOffset;
                    
                    // Convert to nanoseconds for the generator
                    long timeNanos = (long)(channelTimeInSeconds * 1_000_000_000L);
                    float value = dataGen.getValue(timeNanos);
                    
                    builder.addAnalogInData(DtoAConverter.convertVoltageToInt(value, ANALOG_RES, getAdcRange()));
                    builder.addAnalogInPortRange(getAdcRange());
                    builder.addAnalogInIntScaleM(1f);
                    builder.addAnalogInCalB(0);
                    builder.addAnalogInCalM(1);
                }
            }
            
            byte[] di = new byte[1];
            // Create 1Hz square wave (1 second on, 1 second off)
            // Floor division of seconds gives us 0 for first second, 1 for second second, etc.
            boolean isSecondEven = ((int)timeInSeconds % 2) == 0;
            di[0] = isSecondEven ? (byte) -1 : 0;  // -1 is all bits set (on), 0 is all bits clear (off)

            builder.setDigitalData(ByteString.copyFrom(di));
            builder.build().writeDelimitedTo(out);
            out.flush();
        }
    }

    private long getSerialNumberForPort(int port) {
        return BASE_SERIAL_NUMBER + port;
    }
}
