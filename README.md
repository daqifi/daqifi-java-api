# IO
IO project to handle various io and non-Android related tasks. This library is currently shared between
the Android application and a desktop Java application.

## Getting started
  * Install Gradle
    * On Mac with hombrew: `brew install gradle`

## Build
```sh
# Generate protobuf classes, compile java and build a runnable jar
gradle build
```

## Run
```sh
# Run the DAQ emulator
java -jar ./build/libs/io-1.0.jar 9760
```

## Useful Stuff
Useful Classes:
   * com.lp.io.Server can be run as a stand alone Java application that emulates the Wifi DAQ SCPI Measure command. Data generated is a saw wave with a vertical offset equal to the channel and a period of 1 second.
   * com.lp.io.UdpResponder can be run as a stand alone Java application that emulates the Wifi DAQs discovery feature.

## Third Party Dependencies
   * Java JRE 1.6
   * JMockit (included in /libs)
      * Used for unit test mocks and code coverage.
   * JUnit   (not included)
      * Used for unit tests.
   * Google Protocol Buffers (source included in com.google.protobuf)
   * Google Protocol Buffers protoc (.proto compilier) must be installed to build the proto files into source.

## Generating Messages classes from .proto files:
   1 At the project root run (Mac/Linux):
      * protoc --java_out src/ resources/WiFiDAQOutMessage.proto
