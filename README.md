# ReVanced-CLI-GUI
Portable GUI for ReVanced-Cli.

<p align="center">
	<img src="https://github.com/FFDA/ffda.github.storage/blob/main/images/ReVanced-CLI-GUI.png?raw=true" width="400">
</p>

## Features
* On start up fetches and prints supported YouTube versions by latest [ReVanced-Patches](https://github.com/revanced/revanced-patches)
* On button click it can check for latest versions of [ReVanced-Cli](https://github.com/revanced/revanced-cli), [ReVanced-Patches](https://github.com/revanced/revanced-patches), [ReVanced-Integrations](https://github.com/revanced/revanced-integrations) and [Vanced MicroG](https://github.com/inotia00/VancedMicroG) on GitHub. If newer version found - downloads them.
* List all connected ADB devices, user can choose the device to install the patched app too.
* Patches can be included or excluded from patching process

## Requirements
* **Supports only Revanced-CLI 4.x.x**
* Java 17 (or any other version compatible with Revanced-Cli)
* ADB should be in the path, however it's possible to choose an option in Settings to use embedded ADB. Program will create a bin folder in root and save it there.

## Instalation & Usage
Download release for your OS, extract.

For Windows launch the exe in root folder

For Linux launch the executable in bin folder

At the start up program will create bunch of folder where it will store all dependencies. While it can and will download [ReVanced-Cli](https://github.com/revanced/revanced-cli), [ReVanced-Patches](https://github.com/revanced/revanced-patches), [ReVanced-Integrations](https://github.com/revanced/revanced-integrations) and [Vanced MicroG](https://github.com/inotia00/VancedMicroG) user will have to download YouTube apk by himself ant place it in youtube-apk folder.

## Compiling
### Requirements:
* Java 17
* JavaFX 17
* Maven

### Compile:
1. Clone the repo
2. depending on OS run `mvn clean javafx:jlink && jpackage @jpackage-linux` or `mvn clean javafx:jlink && jpackage @jpackage-windows` in terminal or command prompt appropriately.