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

GPSLogSheet2 is a metadata collector and script generator for the height measurements performed for
the reference network of GNNS stations.

## Installation

On your Android device, allow installation of apps from unknows sources:

// Settings -> to Security -> Unknown sources //

and check "Allow installation of apps from unknown sources"

The file gpslogsheet2.apk can now be sideloaded onto the device.

## Usage

First time the app is run, it is necessary to update the internal database with settings from a
server (ftp). The server address, path (optional), username and password, must be set. The app will
then attempt to fill the database 

In the specified path, or root if no path specified, folders called 'settings', 'projects' and
'images' must exist. In the settings folder the following files must exist:

- **fixedpoints.csv**:
    Contains a comma seperated list of fixedpoints with the fields Name (four character,
    alphanumeric), hs_name, easting and northing.
    Example: "KMS3, K-01-02584, 12.5362469, 55.7046712"

- **alarms.csv**:

- **antennas.csv**:

- **instruments.csv**:

- **rods.csv**:

- **recipe.txt**:  
    
      // recipe.txt
      //
      // Everything behind a comment is ignored
      // "${<TOKEN>}" are tokens that act as placeholders for data to be inserted by GPSLogSheet2
      // Example: "-O.r "${operator}"" Will become: "-O.r "OLDJO"" if operator is OLDJO
      //
      // Valid tokens are:
      //  operator
      //  gps_name
      //  hs_name
      //  antenna_name
      //  antenna_height
      //
      //  YYYYmmdd
      //  YYYY-mm-dd
      //  YYYY (year)
      //  YY (year, last two)
      //  mm  (month)
      //  ddd (day_of_year)
      //  dd (day of month)
      //  
      //
      //  The below (default) recreates the line from the original gpslogsheet
      teqc -javad jps -O.obs L1L2C1P1P2D1D2S1S2
      -O.r "${operator}"
      -O.ag "KMS" //(agency?)
      -O.o "${operator}"
      -O.int 15.0 
      -O.mo "${gps_name}" //(GPS_point name)  
      -O.mn "${hs_name}" //(hs name)
      -O.pe ${antenna_height} 0.000 0.000 //(antenna height followed by 0.000 0.000)
      -O.an "${antenna_serial}" //(antena serial number)
      -O.at "${antenna_name}" //(antenna name, spaces, NONE?)
      -st ${YYYYmmdd}000015.000 //(set windowing start time YYYYmmdd + 000015.000)
      -e ${YYYYmmdd}235945.000 //(set windowing end time YYYYmmdd + 235945.000) 
      ${gps_name}.jps > ${gps_name}0${day_of_year}0.${YY}0 //(gps_point name, gps_point name dayofyear.year 0)
    

## Development

Current progress:

- [x] Initial layout
- [x] Create SQLite database
- [x] Settings activity
- [x] Interface with the server
- [x] Create ETL (Extract, Transform, Load) functions
- [x] Ability to save projects
- [x] Ability to load projects
- [ ] (Support for other postprocessing tool than TEQC)
    - [x] Single line generated from recipe 
    - [ ] Multiple lines
- [ ] Generation of .bat scripts from the above recipe
- [ ] Upload of .bat scripts
- [ ] Upload of projects to server
- [x] Camera integration
    - [x] Takes and saves pictures locally
    - [ ] Allow user to comment on, delete, rename pictures. 
- [x] GPS integration
- [ ] Upload of projects (with pictures) to server
- [ ] GUI reevaluation and optimization


Target Android SDK is 25 and min SDK is 22.

This app uses SQLite to handle its data. FTP (SFTP) for connectivity. GPS and the camera for ease of
use and data collection.

Projects are JSON (JavaScript Object Notation) objects and converted into strings and saved in the
database as CLOBs (Character Large Object).

Images taken with the camera may also be saved in the database, as BLOBs (Binary Large Object)

AppCompatPreferenceActivity is used for user preferences, such as username, passwords, update
preferences etc.

Settings files on the server are stored as comma separated values, in separate files for each table.

## App structure

Here is a quick overview of the different classes in GPSLogSheet2

### Visible to user (GUI):

- [x] __MainActivity.java__ First activity the user is greeted with if no project is open.            
    - [x] __SettingsActivity.java__ Here ftp settings can be set, and parameters can be downloaded.
    - [x] __ProjectActivity.java__ Shown when loading or starting a new project
        - [x] __ProjectSettingsFragment__ Subclass of ProjectActivity, here user can set project
                                          name, operator, (__TODO: end date__), and delete project. 
            - [ ] __SetupsFragment__ Here user can add setups and set fixedpoint, instrument,
                                     antenna, alarm and take photos.
                - [ ] __PhotoList.java__ List of photos taken, photos can be opened, (__TODO: delete,
                                         rename, comment.__)
        - [ ] __ObservationsFragment.java__ Here user can (__TODO: add measurements, select rods, see
                                            list of past measurements in project__) 
        - [ ] __MapsFragment.java__ TODO: Show a map of the surroundings based on device GPS or
                                        chosen fixedpoint.
        - [ ] __ExtrasFragment.java__ Placeholder fragment
    - [ ] __Huh__
        
### Data structures
The following classes mostly contain fields with corresponding getters and setters.
- [x] __AlarmEntry.java__ 
- [x] __AntennaEntry.java__ 
- [x] __FixedpointEntry.java__ 
- [x] __InstrumentEntry.java__ 
- [x] __RodEntry.java__ 
- [x] __ProjectEntry.java__ Contains project name, operator, start-, modification-, and end-dates,
      as well as a list of *Setups*.
      Implements [*Clonable*](https://docs.oracle.com/javase/7/docs/api/java/lang/Cloneable.html)
      for backup purposes and uses [*gson*](https://github.com/google/gson) to generate
      [*JSON*](https://www.w3schools.com/js/js_json_intro.asp) that can be stored in the database.                            
    - [x] __Setup__ (subclass) Contains setup fields as well as a list of *Observations*. Has
    method *generateBatchString* that returns a string suitable for a batch file, created from
    project- and setup fields and rules specified in the *recipe.txt* file downloaded from the ftp
    server.
        - [x] __Observation__ (subclass)
         

### 
        

## Android udvikling på SIA’s PC’er
For at kunne køre Android Studio samt virtuelle Android enheder direkte fra Windows kræves følgende:

- 0.    Windows 7 (ikke Windows 10, da Hyper-V blokerer for virtualization og ikke kan slåes fra med de gældende sikkerhedsreger.
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

## Android Debug Bridge (ADB) & Database Browser

Find hvor Android Studio har placeret ADB, det kan f.eks. være:

C:\Users\<B-NUMMER>\AppData\Local\Android\sdk\platform-tools

I dette tilfælde kør følgende fra kommando prompten:

"set PATH=%PATH%;C:\Users\<B-NUMMER>\AppData\Local\Android\sdk\platform-tools"

Det gør at man kan køre adb.exe hvor som helst.

Specifikt gør det os i stand til at køre følgende, der kopierer appens SQLite database til en folder på C:
"adb pull /data/data/ref.sdfe.gpslogsheet2/databases/GPSLogSheet2.db C:\dev\"
Det her virker på virtuelle maskiner med API 22, men ikke på API 25. Der er blevet indført permissions.

*.db filen kan nu åbnes i f.eks. "DB Browser for SQLite".
