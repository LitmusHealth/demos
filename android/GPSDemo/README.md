# GPSDemo

This demo was used as a simplification of the Android mobile client for more
rapid testing/development of the location data collection feature.

It utilizes/implements several features:

* Requesting location permissions and handling denial.
* Long running, foreground, Service to collect GPS data that wont be garbage collected by Android.
* Boot receiver to persist/restart the location service across reboots.
