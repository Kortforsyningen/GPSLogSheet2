# GPSLogSheet2
Android app that facilitates the metadata collection for GPS measurements

## Table of Contents

- [Introduction](#introduction)
- [Installation](#installation)
- [Usage](#usage)
- [Development](#developing)

## Introduction

DRAFT!
This is very much a work in progress.

GPSLogSheet2 is a metadata collector and script generator for the height measurements performed for the reference network of GNNS stations.

## Installation

On your Android device, allow installation of apps from unknows sources:

// Settings -> to Security -> Unknown sources //

and check "Allow installation of apps from unknown sources"

The file gpslogsheet2.apk can now be sideloaded onto the device.

## Usage

First time the app is run, it is necessary to update the internal database with settings from a server.
The server address, path (optional), username and password, must be set. The app will then attempt to fill the database 


## Development

Current progress:

- [x] Initial layout
- [x] Create SQLite database
- [x] Settings activity
- [ ] Interface with the server
- [ ] Create ETL (Extract, Transform, Load) functions
- [ ] Ability to save projects
- [ ] Ability to load projects
- [ ] Generation of .bat scripts
- [ ] Upload of projects to server
- [ ] Camera integration
- [ ] GPS integraton
- [ ] Upload of projects (with pictures) to server
- [ ] GUI reevaluation and optimization
- [ ] (Support for other postprocessing tool than TEQC)

Target Android SDK is 25 and min SDK is 22.

This app uses SQLite to handle its data. FTP (SFTP) for connectivity. GPS and the camera for ease of use and datacollection.

AppCompatPreferenceActivity is used for user preferences, such as username, passwords, update preferences etc.

Settings files on the server are stored as comma seperated values, in seperate files for each table.

## Android udvikling på SIA’s PC’er
For at kunne køre Android Studio samt virtuelle Android enheder direkte fra Windows kræves følgende:

- 1.	Virtualization slået til i BIOS
- 2.	Android Studio
- 3.	Intel HAXM
- 4.	Git (versions styring)

##Tænd for virtualization i BIOS.

- 5.	Dette punkt kræver en adgangkode til BIOS som kun Statens IT har.
- 6.	Genstart PC'en og tryk gentagne gange på F1 indtil et lås-ikon vises.
- 7.	Adgangkoden indtastes efterfulgt af enter
- 8.	Under "Security"->"Virtualization" ændres underpunkterne fra disabled til enabled.

Inden programmer kan installeres med hjælp af en konto med administrator rettigheder, kræves det at denne konto først logger på computeren en enkelt gang. Herefter kan denne logge på midlertidigt ved installering af programmer.

## Android Studio

- 9.	Hent Android Studio fra (https://developer.android.com/studio/index.html)
- 10.	Kør installeren (kræver administrator rettigheder)
- 11.	Android Studio foreslår muligvis at installere HAXM, spring dette over.

## Intel HAXM

 - 12.	 Hent Intel HAXM (https://software.intel.com/en-us/android/articles/intel-hardware-accelerated-execution-manager)
 - 13.	Kør installeren (kræver administrator rettigheder)

Man kan nu oprette et "virtual device" i Android Studio vha. Android Virtual Device Manager. Her er det nu vigtigt at man under "System Image" vælger et x86 eller x86-64 image.

## GIT

- 14.	Hent Git (https://git-scm.com/download/win)
- 15.	Kør Installeren
- 16.	I Android Studio: Åbn ”Settings” (Ctrl + Alt + S)
- 17.	Navigér til ”Version Control” - > ”Git”
- 18.	I feltet ”Path to Git executable:” indtast da enten:
C:\Users\<B-NUMMER>\AppData\Local\Programs\Git\cmd\git.exe
eller
C:\Brugere\<B-NUMMER>\AppData\Local\Programs\Git\cmd\git.exe
- 19.	Brug knappen ”Test” for at sikre at det virker

 Man kan nu checke et projekt ud fra f.eks. Github:
”Check out project from version control” -> ”Git”
ved at indtaste dennes adresse under feltet ”Git Repository URL:”.
Det anbefales at ”Parent Directory” sættes til en folder på roden af det lokale C-drev for bedst ydelse.
