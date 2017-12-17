package com.tacuna.common;

import static junit.framework.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;

import org.junit.Test;

import com.google.protobuf.InvalidProtocolBufferException;
import com.tacuna.common.messages.ProtoMessage.WiFiDAQOutMessage;

public class ProtoMessageByteStreamTest {

    @Test(expected = IOException.class)
    public void testParseScpiData() throws IOException {
	String scpiData = "9.29\r\n";
	InputStream in = new ByteArrayInputStream(scpiData.getBytes());

	System.out.println(bytesToHex(scpiData.getBytes()));
	WiFiDAQOutMessage.parseDelimitedFrom(in);
    }

    @Test
    public void testParse() throws IOException {
	WiFiDAQOutMessage msg = WiFiDAQOutMessage.newBuilder().setMsgSeq(1)
		.addAnalogInDataI(123).addAnalogInDataI(987).build();

	InputStream in = new ByteArrayInputStream(msg.toByteArray());
	System.out.println(bytesToHex(msg.toByteArray()));
	WiFiDAQOutMessage parsedMsg = WiFiDAQOutMessage.parseFrom(in);
	assertEquals(1, parsedMsg.getMsgSeq());
    }

    public static byte[] concat(byte[] first, byte[] second) {
	byte[] result = Arrays.copyOf(first, first.length + second.length);
	System.arraycopy(second, 0, result, first.length, second.length);
	return result;
    }

    public static String bytesToHex(byte[] in) {
	final StringBuilder builder = new StringBuilder();
	for (byte b : in) {
	    builder.append(String.format("%02x", b));
	}
	return builder.toString();
    }

    @Test
    public void testParseTwoMessagesUsingInputStream() throws IOException {
	WiFiDAQOutMessage msg = WiFiDAQOutMessage.newBuilder().setMsgSeq(1)
		.addAnalogInDataI(123).addAnalogInDataI(987).build();
	WiFiDAQOutMessage msg2 = WiFiDAQOutMessage.newBuilder().setMsgSeq(2)
		.addAnalogInDataI(124).addAnalogInDataI(986).build();
	InputStream in = new ByteArrayInputStream(concat(msg.toByteArray(),
		msg2.toByteArray()));

	WiFiDAQOutMessage parsedMsg1 = WiFiDAQOutMessage.parseFrom(in);
	WiFiDAQOutMessage parsedMsg2 = WiFiDAQOutMessage.parseFrom(in);

	assertEquals(2, parsedMsg1.getMsgSeq());
	assertEquals(0, parsedMsg2.getMsgSeq());
	assertEquals(0, parsedMsg2.toByteString().size());
    }

    @Test
    public void testParseTwoMessagesUsingByteArrays() throws IOException {
	WiFiDAQOutMessage msg = WiFiDAQOutMessage.newBuilder().setMsgSeq(1)
		.addAnalogInDataI(123).addAnalogInDataI(987).build();
	WiFiDAQOutMessage msg2 = WiFiDAQOutMessage.newBuilder().setMsgSeq(2)
		.addAnalogInDataI(124).addAnalogInDataI(986).build();
	byte[] bytes = concat(msg.toByteArray(), msg2.toByteArray());

	WiFiDAQOutMessage parsedMsg1 = WiFiDAQOutMessage.parseFrom(bytes);
	WiFiDAQOutMessage parsedMsg2 = WiFiDAQOutMessage.parseFrom(bytes);

	assertEquals(2, parsedMsg1.getMsgSeq());
	assertEquals(2, parsedMsg2.getMsgSeq());
	assertEquals(12, parsedMsg2.toByteString().size());
    }

    @Test
    public void testParseTwoMessagesSeperatedByScpi() throws IOException {
	WiFiDAQOutMessage msg = WiFiDAQOutMessage.newBuilder().setMsgSeq(1)
		.addAnalogInDataI(123).addAnalogInDataI(987).build();
	String scpiData = "9.29\r\n";
	WiFiDAQOutMessage msg2 = WiFiDAQOutMessage.newBuilder().setMsgSeq(2)
		.addAnalogInDataI(124).addAnalogInDataI(986).build();
	ByteArrayOutputStream output = new ByteArrayOutputStream();

	msg.writeDelimitedTo(output);
	output.write(scpiData.getBytes());
	msg2.writeDelimitedTo(output);

	byte[] data = output.toByteArray();
	InputStream istream = new ByteArrayInputStream(output.toByteArray());
	InputStream in = new BufferedInputStream(istream);

	System.out.println("data:");
	System.out.println(bytesToHex(data));

	WiFiDAQOutMessage parsedMsg1 = WiFiDAQOutMessage.parseDelimitedFrom(in);
	assertEquals(1, parsedMsg1.getMsgSeq());

	in.mark(1000);
	try {
	    WiFiDAQOutMessage parsedMsg2 = WiFiDAQOutMessage
		    .parseDelimitedFrom(in);
	    assertEquals(2, parsedMsg2.getMsgSeq());
	} catch (InvalidProtocolBufferException err) {
	    in.reset();
	    in.mark(1000);
	    Scanner scan = new Scanner(in);
	    scan.useDelimiter("\r\n");
	    String scpiResult = scan.hasNext() ? scan.next() : "";
	    in.reset();
	    in.skip(scpiResult.length() + 2);
	    assertEquals("9.29", scpiResult);
	}
	// in.close();

	// in = new BufferedInputStream(istream);
	WiFiDAQOutMessage parsedMsg3 = WiFiDAQOutMessage.parseDelimitedFrom(in);
	assertEquals(2, parsedMsg3.getMsgSeq());
    }

    @Test
    public void testBitwise() {

	assertEquals(true, (1 & 1) == 1);
	assertEquals(true, (4 & 5) == 4);
    }
}
