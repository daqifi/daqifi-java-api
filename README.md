# DAQiFi Java API and Device Emulator

Java API for interacting with DAQiFi devices.

## Getting started
  * Install Java
    * Target: 1.8  
    * Current version: 18.0

## Build
```sh
# Generate protobuf classes, compile java and build a runnable jar
./gradlew build
```
Note: to force specific JDK installation, add the following to build.gradle (change path as needed):
```sh
compileJava.options.fork = true
compileJava.options.forkOptions.executable = 'C:\\Program Files\\Java\\jdk1.8.0_181\\bin\\javac.exe'
```

## Run
```sh
# Run the DAQ emulator
java -jar ./build/libs/daqifi-java-api-0.1.1.jar 9760
```

## Useful Stuff
Useful Classes:
   * Server can be run as a stand alone Java application 
     that emulates the DAQiFi SCPI Measure command. 
     Data generated is a saw wave with a vertical offset 
     equal to the channel and a period of 1 second.
   * UdpResponder can be run as a stand alone Java application 
     that emulates the DAQiFi device discovery feature.

## Third Party Dependencies
   * Java JRE 1.8
   * Google Protocol Buffers
   * JMockit (included in /libs)
      * Used for unit test mocks and code coverage.
   * JUnit (not included)
      * Used for unit tests.


## Generating Messages classes from .proto files:
   1 At the project root run (Mac/Linux):
      * protoc --java_out src/ resources/WiFiDAQOutMessage.proto
