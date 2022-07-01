# HomeOffice - Philips Hue

Application for notification of household members about the status of working from home. It has an automatic process that, based on working hours and the registry, sets the appropriate light depending on whether you are available, working, having a meeting with or without a camera. Only available for Windows.

![](https://github.com/Patresss/HomeOffice/blob/master/readme-resources/application.png)

## Download - 1.0.0 version
* Installer exe (windows) - [download exe](https://github.com/Patresss/HomeOffice/raw/master/release/1.0.0/HomeOffice%20-%20Philips%20Hue-1.0.0.exe)
* Executable jar - [download zip](https://github.com/Patresss/HomeOffice/raw/master/release/1.0.0/HomeOffice%20-%20Philips%20Hue-1.0.0.zip)

## Video
[![IMAGE ALT TEXT](http://img.youtube.com/vi/yGT-E0wikoc/0.jpg)](http://www.youtube.com/watch?v=yGT-E0wikoc "Home Office - Philips Hue")

## Installation
1. Install the application
2. Run the application as administrator (it will create the `config/settings.yaml` file)
3. Set up the `config/settings.yaml` file:
   * `light.phlipsHueIp`
   * `light.phlipsHueRoomName`
   * `light.phlipsHueLightName` if you want to manage a specific light and not the entire room
4. Run the application again, and it will ask you to press the button on the Philips Hie Bridge, then press the button (you will have 30 seconds).


## Options
* AVAILABLE - turn on the green light
* WORKING - turn on the yellow light
* MEETING (MICROPHONE ONLY) - turn on the red light
* MEETING (WITH WEBCAM) - turn on the purple light
* AUTOMATION - activation of automatic mode
* TURN OFF - turn off the light

## Automatic mode
* AVAILABLE - outside working hours
* WORKING - during working hours
* MEETING (MICROPHONE ONLY) - when a microphone in use is detected
* MEETING (WITH WEBCAM) - when a webcam in use is detected

## Settings
### light
```yaml
light:
  phlipsHueIp: <Philips Hue Ip> # Example: "192.168.1.12"
  phlipsHueApiKey: <Philips Hue API Key - If you don't know it, leave it blank. The application will ask you to press the button on the Philips Hie Bridge and it will automatically fill in the field> # Example: "cNjRdZ3-9GMDeNNF5rcKYElKawdFzXh6JMd9o4GM"
  phlipsHueRoomName: <Philips Hue Room Name - the same as in the application> # Example: "Office"
  phlipsHueLightName: <Philips Hue Room Name - the same as in the application (If empty it will change the lights for the whole room)> # Example: Lightstrip"
  brightnes: <Brightnes> # Example: 30
  lightMode: <Currently used Light Mode> # Example: "AUTOMATION"
  automationFrequencySeconds: <Automation Frequency Seconds - The frequency at which the automatic process checks, e.g. whether there are still working hours or whether the camera is plugged in> # Example: 1
```

### window
```yaml
window:
  pinned: <pinned - true if you want the application to always be displayed on the screen> # Example: true
  enablePreviousPosition: <pinned - true if you want the application to be in the same position after restart> # Example: true
  positionX: <Position X - X position on the screen> # Example: 200
  positionY: <Position Y - Y position on the screen> # Example: 200
```
### workingTime
```yaml
workingTime:
  days: <working days of the week> # Example: - "MONDAY" \n - "TUESDAY"
  start: <start - working hour> # Example: "09:00"
  end: <end - working hour> # Example: "17:00"
```

## Built With

* [JFoenix](https://github.com/jfoenixadmin/JFoenix)
* [log4j](https://logging.apache.org/log4j/2.x/)
* [slf4j](http://www.slf4j.org/)
* [FontAwesomeFx](https://www.jensd.de/wordpress/?tag=fontawesomefx)
* [Jackson](https://github.com/FasterXML/jackson-core)
* [Java Native Access (JNA)](https://github.com/java-native-access/jna)
* [Yet Another Hue API](https://github.com/ZeroOne3010/yetanotherhueapi)

## License

This project is licensed under the Apache License 2.0 
