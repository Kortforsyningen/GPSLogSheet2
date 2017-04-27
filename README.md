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

