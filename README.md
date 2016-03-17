# Beacon-Scanner

Scans, transmits and shows details of every iBeacon, Eddystone (UID, URL frames) and AltBeacon in your area.

This app displays the following information of a beacon:
* UUID, Minor and Major values
* Distance to beacon
* TX and RSSI values
* Bluetooth information like MAC address and more

In the settings screen you can customise scanning and transmitting settings. Some of the settings include:
* Scan period time
* Between scan periods time
* How long beacons should remain in the cache
* UUID, major, minor, frequency etc.

This application is an open source project. We're students in our 2nd year so any feedback is much appreciated!

We're working hard on making this app as awesome as it could be. Some of the features we're working on right now are:
* Saving and sending log files
* Adding beacon formats for scanning

# Credits

A huge thanks to the guys from Radius Networks for providing a great beacon library and lots of information! Be sure to check them out!

##Libraries

* [AltBeacon](https://altbeacon.github.io/android-beacon-library/index.html): Beacon scan/transmit library.
* [Butterknife](http://jakewharton.github.io/butterknife/): View injection library.
* [Assent](https://github.com/afollestad/assent): Permissions library.
