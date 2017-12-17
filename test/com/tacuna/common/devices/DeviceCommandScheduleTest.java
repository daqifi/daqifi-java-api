/**
 * 
 */
package com.tacuna.common.devices;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import mockit.Injectable;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;

import org.junit.Test;

import com.tacuna.common.devices.DeviceCommandSchedule.ScheduledCommand;
import com.tacuna.common.devices.scpi.Command;

/**
 * @author Marc
 * 
 */
public class DeviceCommandScheduleTest {

    @Test
    public void testConstructor(@Injectable DeviceInterface device) {
	// Record:
	new NonStrictExpectations() {
	    @Mocked
	    ScheduledThreadPoolExecutor exec;
	    {
		new ScheduledThreadPoolExecutor(anyInt);
		result = exec;
	    }
	};

	// Execute:
	new DeviceCommandSchedule(device, 3);
	new DeviceCommandSchedule(device);

	// Verify:
	new Verifications() {
	    ScheduledThreadPoolExecutor exec;
	    {
		new ScheduledThreadPoolExecutor(3);
		times = 1;

		new ScheduledThreadPoolExecutor(
			DeviceCommandSchedule.DEFAULT_THREAD_POOL_SIZE);
		times = 1;
	    }
	};

    }

    @Test
    public void testSchedule(@Injectable DeviceInterface device) {
	// Record:
	final Command command = new Command("");
	new NonStrictExpectations() {
	    @Mocked
	    ScheduledThreadPoolExecutor exec;
	    {
		new ScheduledThreadPoolExecutor(anyInt);
		result = exec;
	    }
	};

	// Execute:
	DeviceCommandSchedule schedule = new DeviceCommandSchedule(device);
	schedule.schedule(command, 100);

	// Verify:
	new Verifications() {
	    ScheduledThreadPoolExecutor exec;
	    {
		ScheduledCommand schedulecommand;
		exec.scheduleAtFixedRate(schedulecommand = withCapture(),
			anyInt, 100, TimeUnit.MILLISECONDS);
		times = 1;
		assertEquals(schedulecommand.getCommand(), command);
	    }
	};
    }

    @Test
    public void testScheduledCommandRun(@Injectable final DeviceInterface device) {
	// Record:
	final Command command = new Command("");
	new NonStrictExpectations() {
	    {
	    }
	};

	// Execute:
	DeviceCommandSchedule schedule = new DeviceCommandSchedule(device);
	ScheduledCommand scheduledCommand = schedule.new ScheduledCommand(
		command, device);
	scheduledCommand.run();

	// Verify:
	new Verifications() {
	    {
		Command executedCommand;
		device.send(executedCommand = withCapture());
		times = 1;
		assertEquals(executedCommand, command);
	    }
	};
    }

    @Test
    public void testRemove(@Injectable DeviceInterface device) {
	// Record:
	final Command command = new Command("");
	new NonStrictExpectations() {
	    @Mocked
	    ScheduledThreadPoolExecutor exec;

	    @Mocked
	    ScheduledFuture<?> future;
	    {
		new ScheduledThreadPoolExecutor(anyInt);
		result = exec;

		exec.scheduleAtFixedRate((Runnable) any, anyInt, anyInt,
			TimeUnit.MILLISECONDS);
		result = future;
	    }
	};

	// Execute:
	DeviceCommandSchedule schedule = new DeviceCommandSchedule(device);
	schedule.schedule(command, 100);
	schedule.remove(command);

	// Verify:
	new Verifications() {
	    ScheduledThreadPoolExecutor exec;
	    ScheduledFuture<?> future;
	    {
		exec.scheduleAtFixedRate((Runnable) any, anyInt, anyInt,
			TimeUnit.MILLISECONDS);
		times = 1;

		future.cancel(true);
		times = 1;
	    }
	};
    }
}
