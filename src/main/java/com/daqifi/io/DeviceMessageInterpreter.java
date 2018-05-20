package main.java.com.daqifi.io;

import com.google.protobuf.InvalidProtocolBufferException;
import main.java.com.daqifi.io.messages.SimpleDeviceMessage;
import main.java.com.daqifi.io.messages.SimpleProtobufMessage;
import com.daqifi.common.messages.ProtoMessageV2;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DeviceMessageInterpreter extends DataInterpreter {

    private static Logger log = Logger.getLogger(DeviceMessageInterpreter.class
            .getName());
    private final String encoding = "UTF-8";


    public String getEncoding() {
        return encoding;
    }

    public String getLineTerminator() {
        return lineTerminator;
    }

    /**
     * Device line terminator.
     */
    private final String lineTerminator = "\r\n";

    private Thread notifierThread = null;

    public DeviceMessageInterpreter() {
        notifierThread = new Thread(new Notifier());
        notifierThread.start();
    }

    BlockingQueue<ProtoMessageV2.DaqifiOutMessage> messageQueue = new LinkedBlockingQueue<>(10000);

    class Notifier implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    addProtobufData(messageQueue.take());
                } catch (InterruptedException e) {
                    log.log(Level.WARNING, "Interrupted exception", e);
                }
            }
        }
    }

    public void addProtobufData(ProtoMessageV2.DaqifiOutMessage message) {
        try {
            notifyObservers(new SimpleProtobufMessage(message));
        } catch (Exception err) {
            log.log(Level.WARNING, "An error occured trying to notify data observers.",
                    err);
        }
    }

    public static String print(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        for (byte b : bytes) {
              sb.append(String.format("0x%02X ", b));
        }
        sb.append("]");
        return sb.toString();
    }

    private boolean hasReadTermPrompt = false;
    @Override
    public int parseData(InputStream in) throws IOException {
        try {
            if(!hasReadTermPrompt){
                in.mark(11);
                byte[] buff = new byte[11];
                in.read(buff, 0, 11);
                in.reset();
                if(Arrays.equals("DAQIFI> s\r\n".getBytes(encoding), buff)){
                    in.skip(11);
                    hasReadTermPrompt = true;
                }
            }

            in.mark(2);
            byte[] buff = new byte[2];
            in.read(buff, 0, 2);
            in.reset();
            if(Arrays.equals("\r\n".getBytes(encoding), buff)){
                in.skip(2);
            }

            ProtoMessageV2.DaqifiOutMessage message = ProtoMessageV2.DaqifiOutMessage.parseDelimitedFrom(in);

            if (message == null) {
                return -1;
            }

            if(!messageQueue.offer(message, 1, TimeUnit.SECONDS)){
                log.warning("Buffer full. Dropping message.");
            }
        } catch (InvalidProtocolBufferException e) {
            log.log(Level.WARNING, "Invalid protocol buffer exception", e);
        } catch (InterruptedException e){
            log.log(Level.WARNING, "Interrupted exception", e);
        }
        return 0;
    }

    protected void parseScpi(InputStream in) throws IOException {
        in.mark(512);
        Scanner scan = new Scanner(in);
        scan.useDelimiter(lineTerminator);
        String scpiResult = scan.hasNext() ? scan.next() : "";
        if (!scpiResult.equals("")) {
            in.reset();
            in.skip(scpiResult.length() + 2);
            try {
                notifyObservers(new SimpleDeviceMessage(scpiResult));
            } catch (Exception err) {
                log.warning("An error occured trying to convert data to a expected message type. Error: "
                        + err.toString());
            }
        }

    }
}
