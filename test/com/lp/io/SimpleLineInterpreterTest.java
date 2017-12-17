/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lp.io;

import static org.junit.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

import com.lp.io.messages.Message;

/**
 * 
 * @author marc
 */
public class SimpleLineInterpreterTest implements MessageConsumer {

    public SimpleLineInterpreterTest() {
    }

    private Message lastMessageReceived = null;
    private int numberOfMessagesReceived = 0;

    @Override
    public void onMessage(Message message) {
	lastMessageReceived = message;
	numberOfMessagesReceived++;
    }

    @Before
    public void clear() {
	lastMessageReceived = null;
	numberOfMessagesReceived = 0;
    }

    /**
     * Test of setEncoding method, of class SimpleLineInterpreter.
     */
    @Test
    public void testSetGetEncoding() {
	String encoding = "";
	SimpleLineInterpreter instance = new SimpleLineInterpreter();
	instance.setEncoding("US-ASCII");
	assertEquals("US-ASCII", instance.getEncoding());
    }

    /**
     * Test of setLineTerminator method, of class SimpleLineInterpreter.
     */
    @Test
    public void testSetGetLineTerminator() {
	String lineTerminator = "\r\n";
	SimpleLineInterpreter instance = new SimpleLineInterpreter();
	instance.setLineTerminator(lineTerminator);
	assertEquals(lineTerminator, instance.getLineTerminator());
    }

    /**
     * Test of addRawData method, of class SimpleLineInterpreter.
     */
    @Test
    public void testAddRawData() throws Exception {
	String testLine = "This is a test line\n";
	InputStream in = new BufferedInputStream(new ByteArrayInputStream(
		testLine.getBytes("UTF-8")));
	SimpleLineInterpreter instance = new SimpleLineInterpreter();
	instance.registerObserver(this);
	instance.parseData(in);

	assertEquals("This is a test line", lastMessageReceived.getData());
    }

    /**
     * Test of addRawData method, of class SimpleLineInterpreter.
     */
    @Test
    public void testAddRawDataMultipleLines() throws Exception {
	String testLine = "This is a test line\n";
	String testLine2 = "This is a second test line\n";
	String data = testLine + testLine2;
	InputStream in = new ByteArrayInputStream(data.getBytes("UTF-8"));
	SimpleLineInterpreter instance = new SimpleLineInterpreter();
	instance.registerObserver(this);
	instance.parseData(in);
	instance.parseData(in);

	assertEquals("This is a second test line",
		lastMessageReceived.getData());
	assertEquals(2, numberOfMessagesReceived);
    }

    /**
     * Test of addRawData method, of class SimpleLineInterpreter.
     */
    @Test
    public void testAddRawDataLineSpansBuffers() throws Exception {
	System.out.println("testAddRawDataLineSpansBuffers");
	String startOfData = "This is a test line with out terminator. ";
	String endOfData = "This is a second line with a \n";
	String data = startOfData + endOfData;
	InputStream in = new ByteArrayInputStream(data.getBytes("UTF-8"));
	SimpleLineInterpreter instance = new SimpleLineInterpreter();
	instance.registerObserver(this);
	instance.parseData(in);

	assertEquals(
		"This is a test line with out terminator. This is a second line with a ",
		lastMessageReceived.getData());
	assertEquals(1, numberOfMessagesReceived);
    }
}