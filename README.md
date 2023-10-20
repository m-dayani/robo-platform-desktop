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
