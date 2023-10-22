# ROBO Platform | Desktop App

Control an Android-powered robot with a remote desktop computer through a wireless connection.

This project has three components:

- The [desktop application](https://github.com/m-dayani/robo-platform-desktop) (this module) interacts with an Android phone to send and receive data and commands.
- The [Android app](https://github.com/m-dayani/robo-platform-android) captures the instructions from the remote desktop, processes them, and transmits signals to an AVR control board through a USB connection.
- The [AVR device](https://github.com/m-dayani/robo-platform-avr) receives the commands from the phone and executes low-level tasks.


## Dependencies:

1. Javafx (available in JDK < 11)
2. The Bluecove wireless library
3. [Optional] IntelliJ IDEA Community Edition (compiled and tested with IntelliJ 2019, JDK 11 on Windows 8.1)


## Usage

To run this application on your development machine, you need JRE 8 **with JavaFX** (but you also need JDK 8 for development). You can download it from [official Oracle download page](https://www.oracle.com/java/technologies/downloads/).

Once you have JRE 8, you can locate the `java` command, download the provided `robo-platform-desktop.jar` from this repo, and issue the following command in a terminal to launch the application:

```bash
path/to/java -jar robo-platform-desktop.jar
```

Alternatively, you can install a development environment (like [IntelliJ IDEA Community Edition](https://www.jetbrains.com/help/idea/installation-guide.html)) on your system and open, compile, and run the source code.

