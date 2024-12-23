# DAQiFi Java API and Device Emulator

Java API for interacting with DAQiFi devices.

## Getting started
- Java Development Kit (JDK) 17: Ensure that JDK 17 is installed on your system.

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

Single emulator
```sh
java -jar ./build/libs/daqifi-java-api-0.3.0.jar 9760
```

Multiple emulators
Note that the serial number and MAC address are deterministic and based on the port number.
```sh
# starts three device emulations
java -jar ./build/libs/daqifi-java-api-0.3.0.jar 9760 9761 9762
```

## Useful Stuff
Useful Classes:
   * Server can be run as a stand-alone Java application 
     that emulates the DAQiFi SCPI Measure command. 
     Data generated is a saw wave with a vertical offset 
     equal to the channel and a period of 1 second.
   * UdpResponder can be run as a stand-alone Java application 
     that emulates the DAQiFi device discovery feature.

## Third Party Dependencies
   * Java 17
   * Google Protocol Buffers

## Generating Messages classes from .proto files:
   1 At the project root run (Mac/Linux):
      * protoc --java_out src/ resources/WiFiDAQOutMessage.proto
