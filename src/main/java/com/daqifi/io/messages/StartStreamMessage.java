package com.daqifi.io.messages;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Marc on 4/20/15.
 */
public class StartStreamMessage extends Message {

  public static class ChannelConfig{
    public String channelId;
    public String channelName;
    public String scalingAlgorithm;
    public String scalingParams;

    public ChannelConfig(String channelId, String channelName, String scalingAlgorithm, String scalingParams) {
      this.channelId = channelId;
      this.channelName = channelName;
      this.scalingAlgorithm = scalingAlgorithm;
      this.scalingParams = scalingParams;
    }

    public static String coefficientString(float[] x){
      StringBuilder bld = new StringBuilder();
      for(float xx:x){
        if(bld.length() != 0){
          bld.append(",");
        }
        bld.append(xx);
      }
      return bld.toString();
    }
  }

  public StartStreamMessage(float sampleRate, long startTime, String name, int deviceId, int numberOfAnalogChannels) {
    super(null);
    this.sampleRate = sampleRate;
    this.startTime = startTime;
    this.name = name;
    this.deviceId = deviceId;
    this.numberOfAnalogChannels = numberOfAnalogChannels;

  }

  public float sampleRate = 0.0f;
  public long startTime = 0;
  public String name = "";
  public int deviceId = 0;
  public int numberOfAnalogChannels;
  public Collection<ChannelConfig> channelConfigs = new ArrayList<ChannelConfig>();

}
