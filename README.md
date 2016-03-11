# Beacon-Scanner

Scans and shows details of every iBeacon, Eddystone (TLM, UID, URL frames) and AltBeacon in your area.

This application is an open source project. We're students in our 2nd year so any feedback is much appreciated! If you have a great idea and would like to help us, just submit a pull request!

This app displays the following information of a beacon:
* UUID, Minor and Major values for iBeacons/Altbeacons. TLM, URL and UID frames for Eddystone.
* Distance to beacon
* TX and RSSI values
* Bluetooth information like MAC address and more

For version 1.0 we kept it simple and clean. A new version is already in the works, some of the features we're working on are:
* Better designed beacon detail view
* Customisable scan settings
* Adding your own beacon formats
* Saving and sending log files
* Transmitting as a beacon


# Credits

A huge thanks to the guys from Radius Networks for providing a great beacon library and lots of information! Be sure to check them out!

##Libraries

* [AltBeacon](https://altbeacon.github.io/android-beacon-library/index.html)
* [Butterknife](http://jakewharton.github.io/butterknife/)
* [Assent](https://github.com/afollestad/assent)
