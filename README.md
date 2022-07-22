# Robo Platform

Control an Android-powered robot with a remote desktop computer through a wireless connection.

This project has three components:

- The desktop application (this module) interacts with an Android phone to send and receive data and commands.
- The Android app receives the commands from the remote desktop, processes them, and signals an AVR-powered control board through a USB connection.
- The AVR device receives the commands from the phone and executes low-level tasks.


## Dependencies:

1. Javafx (available in JDK < 11)
2. The Bluecove wireless library
3. [Optional] Intellij IDEA Community Edition (compiled and tested with Intellij 2019, JDK 11 in Window 8.1)
