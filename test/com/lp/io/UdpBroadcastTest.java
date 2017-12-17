// Copyright 2013 Marc Bernardini.
package com.lp.io;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import mockit.Injectable;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.lp.io.messages.DeviceBroadcastMessage;

/**
 * Unit test for the UdpBroadcast class.
 * 
 * @author marc
 * 
 */
public class UdpBroadcastTest {

	private int TEST_DISCOVERY_PORT = 33333;

	@Before
	public void setup() throws Exception {

	}

	@After
	public void teardown() throws Exception {

	}

	@Test
	public void testSendWithPort(@Injectable final InetAddress address)
			throws Exception {
		// Record
		new NonStrictExpectations() {
			@Mocked
			DatagramSocket socket;

			@Mocked
			DatagramPacket packet;
			{
				new DatagramSocket(anyInt);
				result = socket;

				new DatagramPacket("hello".getBytes(), 5, address, 1234);
				result = packet;
			}
		};

		// Execute:
		UdpBroadcast broadcaster = new UdpBroadcast(TEST_DISCOVERY_PORT,
				address);
		broadcaster.send("Hello", 1234);

		// Verify:
		new Verifications() {
			DatagramSocket socket;
			{
				new DatagramSocket(TEST_DISCOVERY_PORT);
				times = 1;

				socket.setBroadcast(true);
				times = 1;

				socket.send((DatagramPacket) any);
				times = 1;
			}
		};
	}

	@Test
	public void testSend(@Injectable final InetAddress address)
			throws Exception {
		// Record
		new NonStrictExpectations() {
			@Mocked
			DatagramSocket socket;

			@Mocked
			DatagramPacket packet;
			{
				new DatagramSocket(anyInt);
				result = socket;

				new DatagramPacket("hello".getBytes(), 5, address,
						TEST_DISCOVERY_PORT);
				result = packet;
			}
		};

		// Execute:
		UdpBroadcast broadcaster = new UdpBroadcast(TEST_DISCOVERY_PORT,
				address);
		broadcaster.send("Hello");

		// Verify:
		new Verifications() {
			DatagramSocket socket;
			{
				new DatagramSocket(TEST_DISCOVERY_PORT);
				times = 1;

				socket.setBroadcast(true);
				times = 1;

				socket.send((DatagramPacket) any);
				times = 1;
			}
		};
	}

	@Test
	public void testReceiveData(@Injectable final InetAddress address,
			@Injectable final MessageConsumer consumer) throws Exception {
		// Record
		new NonStrictExpectations() {
			@Mocked
			DatagramSocket socket;

			@Mocked
			DatagramPacket packet;
			{
				new DatagramSocket(anyInt);
				result = socket;

				new DatagramPacket((byte[]) any, anyInt);
				result = packet;

				socket.receive(packet);

				packet.getData();
				result = "".getBytes();

				packet.getAddress();
				result = address;

				address.toString();
				result = "";
			}
		};

		// Execute:
		UdpBroadcast broadcaster = new UdpBroadcast(TEST_DISCOVERY_PORT,
				address);
		broadcaster.registerObserver(consumer);
		broadcaster.send("hello");
		broadcaster.receiveData();

		// Verify:
		new Verifications() {
			DatagramSocket socket;
			{
				socket.receive((DatagramPacket) any);
				times = 1;

				consumer.onMessage((DeviceBroadcastMessage) any);
				times = 1;
			}
		};
	}

	@Test
	public void testReceiveMessageISent(@Injectable final InetAddress address,
			@Injectable final MessageConsumer consumer) throws Exception {
		// Record
		new NonStrictExpectations() {
			@Mocked
			DatagramSocket socket;

			@Mocked
			DatagramPacket packet;
			{
				new DatagramSocket(anyInt);
				result = socket;

				new DatagramPacket((byte[]) any, anyInt);
				result = packet;

				socket.receive(packet);

				packet.getData();
				result = "hello".getBytes();

				packet.getAddress();
				result = address;

				address.toString();
				result = "";
			}
		};

		// Execute:
		UdpBroadcast broadcaster = new UdpBroadcast(TEST_DISCOVERY_PORT,
				address);
		broadcaster.registerObserver(consumer);
		broadcaster.send("hello");
		broadcaster.receiveData();

		// Verify:
		new Verifications() {
			DatagramSocket socket;
			{
				socket.receive((DatagramPacket) any);
				times = 1;

				consumer.onMessage((DeviceBroadcastMessage) any);
				times = 0;
			}
		};
	}

	@Test
	public void testStope(@Injectable final InetAddress address)
			throws Exception {
		// Record
		new NonStrictExpectations() {
			@Mocked
			DatagramSocket socket;

			@Mocked
			DatagramPacket packet;
			{
				new DatagramSocket(anyInt);
				result = socket;
			}
		};

		// Execute:
		UdpBroadcast broadcaster = new UdpBroadcast(TEST_DISCOVERY_PORT,
				address);
		broadcaster.stop();

		// Verify:
		new Verifications() {
			DatagramSocket socket;
			{
				socket.close();
				times = 1;
			}
		};
	}
}
