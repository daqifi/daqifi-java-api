/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lp.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.Socket;

import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Tested;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 
 * @author marc
 */
public class SocketConnectorTest extends DataInterpreter implements
	PropertyChangeListener {

    private boolean propertyChangeWasCalled = false;

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
	propertyChangeWasCalled = true;
    }

    public SocketConnectorTest() {
    }

    @Tested
    SocketConnector sc;

    private Socket mockSocket;

    @Before
    public void setUp() {
	propertyChangeWasCalled = false;
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of run method, of class SocketConnector.
     */
    @Ignore
    @Test
    public void testRun() throws Exception {
	/*
	 * System.out.println("run"); SocketConnector instance = new
	 * SocketConnector("localhost", 8080,this);
	 * instance.addChangeListener(this);
	 * 
	 * assertTrue(instance.isConnected());
	 * assertEquals(SocketConnector.State.Connected,
	 * instance.getConnectionState()); assertTrue(propertyChangeWasCalled);
	 */
	// Record the desired results for method invocations, *if* any are
	// needed.
	new NonStrictExpectations() {
	    // An internal dependency ("new-ed" later) can be mocked as well:
	    @Mocked
	    Socket anotherMock;
	    {
		// Simply invoke a mocked method/constructor to record an
		// expectation.
		anotherMock.getInputStream();
		result = null; // assign results (values to return, exceptions
			       // to throw)
	    }
	};

	sc.run();
    }

    @Ignore
    @Test
    public void testRun_NoServer() throws Exception {
	SocketConnector instance = new SocketConnector("localhost", 9999, this);
	instance.addChangeListener(this);

	Thread.sleep(1000);
	assertFalse(instance.isConnected());
	assertEquals(SocketConnector.State.Failed,
		instance.getConnectionState());
	assertTrue(propertyChangeWasCalled);
    }

    @Ignore
    @Test
    public void testGetConnectionTime_neverConnected() {
	SocketConnector instance = new SocketConnector("localhost", 9999, this);
	assertEquals(0, instance.getConnectionTime());
    }
}