# Pico Theme Manager

Pico Theme Manager is a tool to help you easily switch between custom Pico OS themes on your Pico device.

> [!WARNING]
> This software is under development, the author is not responsible for any damage caused by the software.

https://github.com/user-attachments/assets/222cc5fb-82ae-4bf7-9c99-302cb2249d69

## Installation

You can download latest version of the apk from [here](https://github.com/Nyabsi/PicoThemeManager/releases). 

You will need Android Debug Bridge to complete the installation.

> [!IMPORTANT]
> PicoThemeManager requires permission `android.permission.WRITE_SECURE_SETTINGS` to be granted.

Grant `android.permission.WRITE_SECURE_SETTINGS` to PicoThemeManager with Android Debug Bridge:

```
adb shell pm grant cc.sovellus.picothememanager android.permission.WRITE_SECURE_SETTINGS
```

## Compatibility

Compatibility with Pico OS 5.2.7 - 5.13.3 has been tested.

### Tested Devices

  - Pico 4 ✔️
  - Pico 4 Pro ✔️
  - Pico 4 Enterprise ✔️
  - Pico 4 Ultra ✔️
  - Pico Neo 3 Link ✔️
  - Pico 3 Pro ✔️

## Creating Custom Themes

You can find guide for making your own custom themes [here](https://gist.github.com/Nyabsi/c14bd38d03d6dc44721779c182762627).

## Contributing

Feel free to contribute through Pull Request, if you think this project needs something else, feel free to make an issue.

## License

This project is licensed under MIT which can be found in `LICENSE` file in the root folder.
