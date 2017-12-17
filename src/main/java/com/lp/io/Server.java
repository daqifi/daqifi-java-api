package com.lp.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.google.protobuf.ByteString;
import com.lp.io.generators.CompositeGenerator;
import com.lp.io.generators.FuzzGenerator;
import com.lp.io.generators.Generator;
import com.lp.io.generators.Limiter;
import com.lp.io.generators.SineGenerator;
import com.tacuna.common.components.DtoAConverter;
import com.tacuna.common.devices.AD7195W;
import com.tacuna.common.messages.ProtoMessage.WiFiDAQOutMessage;
import com.tacuna.common.messages.ProtoMessage.WiFiDAQOutMessage.Builder;
import com.tacuna.common.messages.ProtoMessageV2;

/**
 * @author marc
 */
public class Server extends Thread {
  private static Logger log = Logger.getLogger(Server.class.getName());
  private int port;
  private DataInterpreter clientConnectionInterpreter;
  static int SAMPLES_PER_SEC = 100;

  private DataThread dt;

  public Server(int port, DataInterpreter dataInterpreter) {
    this.port = port;
    this.clientConnectionInterpreter = dataInterpreter;

    log.info(String.format("Listening on port %d", port));
    start();
  }

  @Override
  public void run() {
    try {
      ServerSocket sserver = new ServerSocket(port);
      BufferedReader in = null;
      while (sserver.isBound()) {
        Socket clientSocket = sserver.accept();
        log.info("Accepting connection...");
        // clientSocket.setTcpNoDelay(true);
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
            dt.running = false;
            data = "Stop streaming";
          } else if (command.contains("configure:adc:channel")) {
            String[] split = command.split("[ ]");
            channelMask = Integer.parseInt(split[1]);
            data = String.format("Set channel mask to %s",
                    Integer.toBinaryString(channelMask));
          } else if (command.contains("configure:adc:range")) {
            String[] split = command.split("[ ]");
            adcRange = Integer.parseInt(split[1]);
            data = String.format("Set range to %d", adcRange);
          } else if (command.contains("system:sysinfopb")) {
            WiFiDAQOutMessage msg = getWifiDAQOutMessage();
            msg.writeDelimitedTo(clientSocket.getOutputStream());
            data = msg.toString();
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

  public WiFiDAQOutMessage getWifiDAQOutMessage() {
    Builder builder = WiFiDAQOutMessage.newBuilder();
    builder.setAdcBytes(2);

    builder.setAnalogPortEnabled(toByteString(getChannelMask(), 1));

    builder.setAnalogPortRange(toByteString(getAdcRange(), 1));
    builder.setAnalogPortRse(toByteString(1, 1));
    builder.setBatLevel(1);
    builder.setBoardTemp(1);
    builder.setDacBytes(2);
    builder.setDeviceStatus(1);

    builder.setDigitalPortDir(toByteString(0, 1));
    builder.setHostName(getHostName());
    builder.addIpAddr(ByteString.copyFrom(getIpAddress()));
    builder.addMacAddr(ByteString.copyFrom(getMacAddress()));

    builder.setPwrStatus(1);
    builder.setSsid("1111");
    builder.setDevicePn("WFD-AI8-DIO8-AO8");
    builder.setDevicePort(port);

    return builder.build();
  }

  public ProtoMessageV2.DaqifiOutMessage getOutMessage(){
    ProtoMessageV2.DaqifiOutMessage.Builder builder = ProtoMessageV2.DaqifiOutMessage.newBuilder();

    //builder.setAnalogPortEnabled(toByteString(getChannelMask(), 1));

    builder.setAnalogInRes(getAdcRange());
    //builder.setAnalogInP(toByteString(1, 1));
    builder.setBattStatus(1);
    builder.setTempStatus(1);
    //builder.setDacBytes(2);
    builder.setDeviceStatus(1);

    builder.setDigitalPortDir(toByteString(0, 1));
    builder.setHostName(getHostName());
    builder.setIpAddr(ByteString.copyFrom(getIpAddress()));
    builder.setMacAddr(ByteString.copyFrom(getMacAddress()));

    builder.setPwrStatus(1);
    builder.setSsid("1111");
    builder.setDevicePn("WFD-AI8-DIO8-AO8");
    builder.setDevicePort(port);

    return builder.build();
  }

  int adcRange = 1;

  protected int getAdcRange() {
    return adcRange;
  }

  int channelMask = 0;

  protected int getChannelMask() {
    return channelMask;
  }

  byte[] macAddr = initMacAddr();

  protected byte[] initMacAddr() {
    try {
      Enumeration<NetworkInterface> networks = NetworkInterface
              .getNetworkInterfaces();
      while (networks.hasMoreElements()) {
        NetworkInterface network = networks.nextElement();
        byte[] mac = network.getHardwareAddress();
        if (mac != null) {
          return mac;
        }
      }
    } catch (SocketException e) {
      e.printStackTrace();
    }

    byte[] mac = new byte[6];
    SecureRandom random = new SecureRandom();
    random.nextBytes(mac);
    return mac;
  }

  private InetAddress ip = initIp();

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
    return macAddr;
  }

  public class DataThread extends Thread {
    private final OutputStream out;
    private boolean running;
    private final int saplesPerSecond;
    private int dt;
    private final double TOL = 0.9;
    private final Generator dataGen;

    public DataThread(OutputStream os, int samplesPerSecond) {
      this.out = os;
      this.running = true;
      this.saplesPerSecond = samplesPerSecond;
      this.dt = convertSampleRateToDt(samplesPerSecond);

      float max = (getAdcRange() == 1) ? 10.0f : 5.0f;
      this.dataGen = new SineGenerator(max * 0.99f, (float) (2 * Math.PI / 10f), 0f);
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
                                1000f * SAMPLES_PER_SEC / delta));
              }
              lastTimeLogged = now;
              startTimeSeq = sequence;

              if (dt > 0) {
                dt--;
              }
            } else {
              // We've already sent the number of samples for the
              // current second. Sleep and continue until its the
              // next second.
              try {
                Thread.sleep(1);
              } catch (InterruptedException e) {
                log.warning(e.getMessage());
              }
              continue;
            }
          }

          sequence += 1;
          buildDataMessageV2(System.nanoTime());
          Thread.sleep(dt);
        }
      } catch (IOException err) {
        log.warning("Exception caught. Stopping data generation. Error: "
                + err.toString());
      } catch (InterruptedException err) {
        log.warning("Exception caught. Stopping data generation. Error: "
                + err.toString());
      }
    }

    private void buildDataMessageV1(int sequence) throws IOException {
      Builder builder = WiFiDAQOutMessage.newBuilder()
              .setMsgSeq(sequence);

      for (int jj = 0; jj < 8; jj++) {
        int bit = 1 << jj;
        if ((bit & channelMask) == bit) {
          builder.addAnalogInDataI(DtoAConverter.convertVoltageToInt(
                  dataGen.getValue(sequence * (jj + 1)), AD7195W.ADC_RESOLUTION, getAdcRange()));
        }
      }
      byte[] di = new byte[1];
      di[0] = (sequence % 2 == 0) ? (byte) -1 : 0;

      builder.setDigitalData(ByteString.copyFrom(di));
      builder.build().writeDelimitedTo(out);
    }

      private void buildDataMessageV2(long time) throws IOException {
          ProtoMessageV2.DaqifiOutMessage.Builder builder = ProtoMessageV2.DaqifiOutMessage.newBuilder();

          builder.setMsgTimeStamp((int)time);
          builder.setTimestampFreq((int)TimeUnit.NANOSECONDS.convert(1, TimeUnit.SECONDS));
          for (int jj = 0; jj < 8; jj++) {
              int bit = 1 << jj;
              if ((bit & channelMask) == bit) {
                  builder.addAnalogInData(DtoAConverter.convertVoltageToInt(
                          dataGen.getValue(time * (jj + 1)), AD7195W.ADC_RESOLUTION, getAdcRange()));
              }
          }
          byte[] di = new byte[1];
          di[0] = (time % 2 == 0) ? (byte) -1 : 0;

          builder.setDigitalData(ByteString.copyFrom(di));
          builder.build().writeDelimitedTo(out);
      }
  }
}
