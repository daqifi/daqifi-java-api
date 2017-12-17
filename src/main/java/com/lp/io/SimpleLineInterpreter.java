/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lp.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.logging.Logger;

import com.lp.io.messages.Message;

/**
 * @author marc
 */
public class SimpleLineInterpreter extends DataInterpreter {

  private static Logger log = Logger.getLogger(SimpleLineInterpreter.class
          .getName());
  private String encoding = "UTF-8";

  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }

  public void setLineTerminator(String lineTerminator) {
    this.lineTerminator = lineTerminator;
    this.lineTerminatorLength = lineTerminator.length();
  }

  public String getEncoding() {
    return encoding;
  }

  public String getLineTerminator() {
    return lineTerminator;
  }

  private final StringBuilder buffer = new StringBuilder();
  private String lineTerminator = "\n";
  private int lineTerminatorLength = 1;

  public SimpleLineInterpreter() {

  }

  public SimpleLineInterpreter(String lineTerminator) {
    setLineTerminator(lineTerminator);
  }

  @Override
  public int parseData(InputStream in) throws IOException {
    in.mark(512);
    Scanner scan = new Scanner(in);
    scan.useDelimiter(lineTerminator);
    String line = scan.hasNext() ? scan.next() : "";
    if (!line.equals("")) {
      in.reset();
      in.skip(line.length() + lineTerminatorLength);
      try {
        notifyObservers(new Message(line));
      } catch (Exception err) {
        log.warning("An error occured trying to convert data to a expected message type. Error: "
                + err.toString());
      }
    }
    return 0;
  }

  private void parseData() {
    int startOfLineTerminator = buffer.indexOf(lineTerminator);
    final int NOT_FOUND = -1;
    while (NOT_FOUND != startOfLineTerminator) {
      // Since indexOf is the start of the line terminator sequence,
      // the actual end of line is the
      // indexOf(lineTerminator)+lineTerminator.length()
      int eol = startOfLineTerminator + lineTerminator.length();
      notifyObservers(new Message(buffer.substring(0, eol)));
      buffer.delete(0, eol);
      startOfLineTerminator = buffer.indexOf(lineTerminator);
    }
  }
}
