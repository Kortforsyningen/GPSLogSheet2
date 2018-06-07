# GPSLogSheet2
Android app that facilitates the metadata collection for GPS measurements

## Table of Contents

- [Introduction](#introduction)
- [Installation](#installation)
- [Usage](#usage)
- [Development](#development)

## Introduction

GPSLogSheet2 is a metadata collector and script generator for the height measurements performed for
the reference network of GNNS stations.

The goal is to upload meta-data and generated GNNS post processing scripts to a FTP server, for use
with GNNS measurements made in the field.

## Installation

On your Android device, allow installation of apps from unknows sources:

// Settings -> to Security -> Unknown sources //

and check "Allow installation of apps from unknown sources"

The file gpslogsheet2.apk can now be sideloaded onto the device.

## Usage

### Initial setup:

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
    Contains a comma seperated list of alarms with ID number (integer) and alarm name.
    Example: "1, Alarm1"

- **antennas.csv**:
    Contains a comma seperated list of antennas with ID number (integer), antenna name
    and antenna serial number.
    Example: "1, ASH701941.B, CRL20011301"

- **instruments.csv**:
    Contains a comma seperated list of instruments with ID number (integer) and instrument name.
    Example: "17, Javad_Sigma 1"

- **rods.csv**:
    Contains a comma seperated list of rods with ID number (integer), rod name and rod length.
    Example: "1, Roddie McRodFace, 1.89"
    
- **recipe.txt**:  The recipe is used to generate each line in the batch script used for
    post processing of the GNNS data.
    Here is an example recipe:
    
      // recipe.txt
      //
      // Everything behind a comment is ignored
      // "${<TOKEN>}" are tokens that act as placeholders for data to be inserted by GPSLogSheet2
      // Example: "-O.r "${operator}"" Will become: "-O.r "OLDJO"" if operator is OLDJO
      //
      // Valid tokens are:
      //  project_name
      //  operator
      //  gps_name
      //  hs_name
      //  instrument
      //  antenna_name
      //  antenna_height
      //  antenna_serial
      //  
      //
      //  YYYY
      //  YY (year, last two)
      //  day_of_year
      //  MM  (month)
      //  dd (day of month)
      //  YYYmmdd
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
    
### Using the app in the field.

The user can start a new Project by pressing the "New project" button.
The user is now asked to name the project and also name the operator on the project.
The end date can be set, but is optional, but needed to generate batch scripts.

To the right of the project settings, the project setups are presented as tabs and more can
be added using the "Add setup" button.
If the device GPS is ready, the fixed point can be found suing the "Use location" button.
Alternatively a fixed point can be chosen from the drop-down menu.
Instrument, antenna, and alarm can be chosen from drop down menus as well.

Photos can be taken using the capture photo button, and are associated to each setup.



## Development

Current progress:

- [ ] Initial layout
    - [x] Settings
    - [x] MainActivity 
- [x] Create SQLite database
- [x] Settings activity
    - [ ] TODO: auto remove spaces after username
    - [ ] optional: add Agency setting (See recipe above)
- [x] Interface with the server
- [x] Create ETL (Extract, Transform, Load) functions
    - [ ] TODO: 
- [x] Ability to save projects (locally)
- [x] Ability to load projects (locally)
- [x] Ability to delete projects (locally)
    - [ ] TODO: Also delete photos associated with project. 
- [x] GUI for project
    - [ ] GUI for setup
    - [ ] TODO: Name setups
    - [ ] TODO: Delete setups
        - [ ] GUI for observation (measurement)
        - [ ] TODO: BUG: Does not change to correct setup yet (has to do with "lifetime" of
        observation fragment I think.)
        - [ ] TODO: Bug that makes new observations not appear. Relates to using hashmap with array
        adapter. Unclear how to solve this.
- [ ] Generation of .bat scripts from recipe 
    - [x] Single line generated from recipe 
    - [ ] Multiple lines
    - [x] (Support for other postprocessing tool than TEQC, yes via recipe change)
- [ ] Upload of .bat scripts to server
- [ ] Upload of projects to server
- [x] Camera integration
    - [x] Takes and saves pictures locally
    - [ ] Allow user to comment on, delete, rename pictures.
    - [ ] Upload of pictures with project.
- [x] GPS integration
- [ ] GUI reevaluation and optimization


Target Android SDK is 25 and min SDK is ~~22~~ 24 (Updated to be able to use datePickerDialog).

This app uses SQLite to handle its data. FTP (SFTP) for connectivity. GPS and the camera for ease of
use and data collection.

Projects are JSON (JavaScript Object Notation) objects and converted into strings and saved in the
database as CLOBs (Character Large Object).

Images taken with the camera may also be saved in the database, as BLOBs (Binary Large Object).
Currently they are saved as files on the device.

AppCompatPreferenceActivity is used for user preferences, such as username, passwords, update
preferences etc.

Settings files on the server are stored as comma separated values, in separate files for each table.

## App structure

Here is a quick overview of the different classes in GPSLogSheet2. Some important methods are also
describes. See comments in code for more depth.

### Visible to user (GUI):

- [x] __MainActivity.java__ First activity the user is greeted with if no project is open.            
    - [x] __SettingsActivity.java__ Here ftp settings can be set, and parameters can be downloaded.
        - [x] __UpdateActivity.java__ Shows a 
    - [x] __ProjectActivity.java__ Shown when loading or starting a new project
        - [x] __ProjectSettingsFragment__ Subclass of ProjectActivity, here user can set project
                                          name, operator, (__TODO: end date__), and delete project. 
            - [ ] __SetupsFragment__ Here user can add setups and set fixedpoint, instrument,
                                     antenna, alarm and take photos.
                - [ ] __PhotoList.java__ List of photos taken, photos can be opened, (__TODO: delete,
                                         rename, comment.__)
        - [ ] __ObservationsFragment.java__ Here user can (__TODO: fix add measurements, select rods, see
                                            list of past measurements in project__) 
        - [ ] optional __MapsFragment.java__ TODO: Show a map of the surroundings based on device GPS or
                                        chosen fixedpoint.
        - [ ] __ExtrasFragment.java__ Placeholder fragment

        
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
         

### Database, functionality etc.
The app uses both [*Shared Preferences*](https://developer.android.com/reference/android/content/SharedPreferences.html)
and SQLite to store data and settings. GPS is handled by LocationHandler.java that uses [*LocationManager*](https://developer.android.com/reference/android/location/LocationManager.html).
Communication with the server uses FTP and is handled by the SyncDataFTP class and uses
[*Apache Commons FTPClient*](https://commons.apache.org/proper/commons-net/apidocs/org/apache/commons/net/ftp/FTPClient.html). 


- [x] __DataBaseHandler.java__ Uses
[*SQLiteDatabase*](https://developer.android.com/reference/android/database/sqlite/SQLiteDatabase.html)

- [x] __LocationHandler.java__ uses [*LocationManager*](https://developer.android.com/reference/android/location/LocationManager.html).
- [x] __AppCompatPreferenceActivity.java__ Used by SettingsActivity.java
- [x] __SyncDataFTP__ (subclass of UpdateActivity.java)
        Uses [*Apache Commons FTPClient*](https://commons.apache.org/proper/commons-net/apidocs/org/apache/commons/net/ftp/FTPClient.html).
        

## Android udvikling på SIA’s PC’er *med* tablet/telefon til rådighed

Bemærk: Inden programmer kan installeres med hjælp af en konto med administrator rettigheder, kræves det at denne konto først logger på computeren en enkelt gang. Herefter kan denne logge på midlertidigt ved installering af programmer.

- Android Studio
    - Hent Android Studio fra (https://developer.android.com/studio/index.html)
    - Kør installeren (kræver administrator rettigheder)
    - Android Studio foreslår muligvis at installere HAXM, spring dette over.
- Git (versions styring)
    - Hent Git (https://git-scm.com/download/win)
    - Kør Installeren
    - I Android Studio: Åbn ”Settings” (Ctrl + Alt + S)
    - Navigér til ”Version Control” - > ”Git”
    - I feltet ”Path to Git executable:” indtast da enten:
    `C:\Users\<B-NUMMER>\AppData\Local\Programs\Git\cmd\git.exe`
    eller
    `C:\Brugere\<B-NUMMER>\AppData\Local\Programs\Git\cmd\git.exe`
    - Brug knappen ”Test” for at sikre at det virker
    - Man kan nu checke et projekt ud fra f.eks. Github:
    ”Check out project from version control” -> ”Git”
    ved at indtaste dennes adresse under feltet ”Git Repository URL:”.
    Det anbefales at ”Parent Directory” sættes til en folder på roden af det lokale C-drev for bedst ydelse,
    f.eks: `C:\dev\`
    
- Klargør Android tablet/telefon
    - Sæt Android tablet/telefon i[developer mode](https://www.google.com/search?q=android+developer+mode)
    - Tænd for USB debugging under *Developer Options* i enhedens indstillinger.
    - Forbind enheden til PC'en med USB.
    
- (Valgfrit) Android Debug Bridge (ADB) + Database Browser
    - __Følgende er kun nødvendigt hvis man vil inspicere database filen manuelt__.
    - Find hvor Android Studio har placeret ADB, det kan f.eks. være:
    - `C:\Users\<B-NUMMER>\AppData\Local\Android\sdk\platform-tools`
    - I dette tilfælde kør følgende fra kommando prompten:
    - `set PATH=%PATH%;C:\Users\<B-NUMMER>\AppData\Local\Android\sdk\platform-tools"`  
    Det gør at man kan køre adb.exe hvor som helst.  
    Specifikt gør det os i stand til at køre følgende, der kopierer appens SQLite database til en folder på C:
    - `adb pull /data/data/ref.sdfe.gpslogsheet2/databases/GPSLogSheet2.db C:\dev\`
    - Det her virker på virtuelle maskiner med API 22, men ikke på API 25. Der er blevet indført permissions.
    *.db filen kan nu åbnes i f.eks. [*DB Browser for SQLite*](http://sqlitebrowser.org).    

## Android udvikling på SIA’s PC’er *uden* tablet/telefon (Windows 7)
For at kunne køre virtuelle Android enheder direkte fra Windows kræves, ud over punkterne foroven, følgende:

- Windows 7 (ikke Windows 10, da Hyper-V blokerer for virtualization og ikke kan slåes fra med de gældende sikkerhedsreger.
- Virtualization slået til i BIOS
    - Check først om det allerede er slået til i *Task Manager* / *Jobliste*, under *Performance/Ydelse*, *CPU*, *Virtualization*. Ellers gør følgende:
    - Dette punkt kræver en adgangkode til BIOS som kun Statens IT har.
    - Genstart PC'en og tryk gentagne gange på F1 indtil et lås-ikon vises.
    - Adgangkoden indtastes efterfulgt af enter
    - Under "Security"->"Virtualization" ændres underpunkterne fra disabled til enabled.
- Intel HAXM
    - Hent [*Intel HAXM*](https://software.intel.com/en-us/android/articles/intel-hardware-accelerated-execution-manager)
    - Kør installeren (kræver administrator rettigheder)
    - Man kan nu oprette et "virtual device" i Android Studio vha. Android Virtual Device Manager. Her er det nu vigtigt at man under "System Image" vælger et x86 eller x86-64 image. 