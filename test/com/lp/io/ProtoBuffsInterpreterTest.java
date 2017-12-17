package com.lp.io;

import org.junit.Test;

public class ProtoBuffsInterpreterTest {

    @Test
    public void testConversion() {
	int txUnackedTail = 32767; // Maximum signed 16bit integer
	int txTail = 0;
	for (; txUnackedTail < 32769; txUnackedTail++) {
	    short result = (short) (txUnackedTail - txTail);
	    System.out
		    .println(String
			    .format("txUnackedTail: %d\t txTail: %d\t (short)(txUnackedTail- txTail): %d",
				    txUnackedTail, txTail, result));

	}
    }

    @Test
    public void testConversionTwo() {
	int txUnackedTail = 0;
	int txTail = 32767;// Maximum signed 16bit integer
	for (; txTail < 32770; txTail++) {
	    short result = (short) (txUnackedTail - txTail);
	    System.out
		    .println(String
			    .format("txUnackedTail: %d\t txTail: %d\t (short)(txUnackedTail- txTail): %d",
				    txUnackedTail, txTail, result));

	}
    }
}
