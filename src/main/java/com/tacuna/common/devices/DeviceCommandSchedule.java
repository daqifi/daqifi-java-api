package com.tacuna.common.devices;

import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.tacuna.common.devices.scpi.Command;

/**
 * A class used to create scheduled commands. This class creates a thread pool
 * for executing the command at a regular interval.
 *
 * @author Marc
 */
public class DeviceCommandSchedule {
  public static final int DEFAULT_THREAD_POOL_SIZE = 1;
  private final DeviceInterface device;
  private ScheduledThreadPoolExecutor executor;
  private final HashMap<Command, ScheduledFuture<?>> commandHash = new HashMap<Command, ScheduledFuture<?>>();

  /**
   * Constructs the command schedule with the passed in device. The thread
   * pool size is used to configure the command executor.
   *
   * @param device
   * @param threadPoolSize
   */
  public DeviceCommandSchedule(DeviceInterface device, int threadPoolSize) {
    super();
    this.device = device;
    initializeExecutor(threadPoolSize);
  }

  /**
   * Constructs the command schedule with the passed in device. The default
   * thread pool size is used to configure the command executor.
   *
   * @param device
   */
  public DeviceCommandSchedule(DeviceInterface device) {
    super();
    this.device = device;
    initializeExecutor(DEFAULT_THREAD_POOL_SIZE);
  }

  /**
   * Utility for initializing the thread pool executor.
   *
   * @param threadPoolSize
   */
  private void initializeExecutor(int threadPoolSize) {
    executor = new ScheduledThreadPoolExecutor(threadPoolSize);
  }

  /**
   * Schedules a command to be run at the specified period.
   *
   * @param command
   * @param periodMs
   */
  public void schedule(Command command, int periodMs) {
    ScheduledCommand scheduled = new ScheduledCommand(command, device);

    ScheduledFuture<?> future = executor.scheduleAtFixedRate(scheduled,
            100, periodMs, TimeUnit.MILLISECONDS);
    commandHash.put(command, future);

  }

  /**
   *
   */
  public void remove(Command command) {
    ScheduledFuture<?> future = commandHash.remove(command);
    if (null != future) {
      future.cancel(true);
    }
  }

  /**
   * A simple runnable that calls send on a device with a specified command
   * when run.
   *
   * @author Marc
   */
  public class ScheduledCommand implements Runnable {

    private final DeviceInterface device;
    private final Command command;

    /**
     * Constructs the Scheduled command using the specified command and
     * device.
     *
     * @param command
     * @param device
     */
    public ScheduledCommand(Command command, DeviceInterface device) {
      this.command = command;
      this.device = device;
    }

    @Override
    public void run() {
      device.send(command);
    }

    /**
     * @return the device
     */
    public DeviceInterface getDevice() {
      return device;
    }

    /**
     * @return the command
     */
    public Command getCommand() {
      return command;
    }
  }
}
