## 0.11.1 (2013-01-26)

* Added compatibility with screens with basic touch capabilities
* Added compatibility with devices that have no rear camera available
* Removed vertical white line from action bar
* Test tags are now ordered alphabetically
* Modified text of date field in dialog of file options
* Modified some strings
* Changed the encryption method in order to fix a decryption bug in notifications module
* Fixed prevention of activity restart when rotating the screen on Android 4.x platforms
* Fixed group selection in rollcall module
* Fixed database corruption and error message when updating the tests descriptors

## 0.11 (2012-12-15)

* Added downloads module
* Added encryption to notifications
* Added database version checks in upgradeDB method to apply only required changes to the database structure
* Modified the order of modules in main menu to coincide with the order of functions in SWAD
* Refactored main package classes. Moved GUI and utilities classes to separate packages

## 0.10.1 (2012-11-17)

* Added Android 4.2 compatibility
* Added red highlight for groups without free spot
* Changed to lighter blue of background of groups with real membership, darker gray on text about vacants
* Fixed available choose for groups with real membership
* Fixed problem with uppercase letter in notifications without summary

## 0.10 (2012-11-09)

* Added module for enrollment to course groups
* Added missing javadoc packages info
* Updated Android DataFramework library. Removed the binary version and added the latest source code version from GitHub
* GUI improvements
* Fixed DNI processing when using a NIE instead of a DNI (NIE format X5264085Y)
* Fixed a misspelling in english language file
* Fixed some bugs in course selection spinner
* Fixed bug when checking available connections

## 0.9.3 (2012-07-20)

* Added folders support in server URL
* Updated ksoap2-android library
* Improved error messages

## 0.9.2 (2012-07-10)

* camera.autofocus feature marked as optional in order to fix device incompatibilities

## 0.9.1 (2012-07-09)

* Added DNI with letter (first and last) support and DNI with zeros support
* Added Android 4.1 compatibility
* Fixed xlarge screens support (accidentally removed during last merge)

## 0.9 (2012-07-01)

* Added RollCall module

## 0.8.1 (2012-05-20)

* Fixed closed cursor access exception on Android 4.x platforms
* Added message replys from open notifications

## 0.8 (2012-05-01)

* Added automatic synchronization of notifications
* Added server URL configuration in preferences
* Added function to publish new notices (yellow notes) (only teachers)
* Integrated new SWAD roles system
* Adapted all Modules to work with a global actual course
* Improved visualization of notifications
* Integrated ZXing library for attendance module
* Added error handler for all modules
* Added rotating refresh image when updating notifications
* Updated ksoap2-android library
* Updated minimum SDK version to 7 (Android 2.1)
* Updated Market link
* Fixed date and time format
* Fixed uninplemented method error isEmpty() on earlier API versions
* Fixed HTML bug in location field of notifications
* Fixed br tag bug in notifications

## 0.7.2 (2012-02-22)

* Added notification alerts on status bar
* Added navigation ability for HTML links in notifications
* Added WebView activity for all notifications
* Improved query statements

## 0.7.1 (2012-01-11)

* Fixed rendering errors in marks function
* Improved rendering speed in marks function

## 0.7 (2012-01-10)

* Added marks function in notifications module

## 0.6.2 (2011-12-09)

* Added Blog URL to preferences screen
* Added Catalan language by Francisco Manuel Herrero Pérez
* Updated ksoap2-android library
* Reinitialized last course selected on database cleaning
* Minor changes on error messages

## 0.6.1 (2011-11-16)

* Added Google+ account to preferences screen
* Optimized database access
* Fixed menu operation in all activities
* Minor improvements

## 0.6 (2011-11-06)

* Added Android 4.0 compatibility
* Added sharing options to application menu and preferences
* Added clean database option to application menu
* Added name of SWAD's creator to author preferences

## 0.5.2 (2011-09-29)

* Optimized questions syncronization
* Added real names of receivers in sendedMessageMsg
* Fixed bug on test questions syncronization

## 0.5.1 (2011-09-26)

* Fixed bug in reply messages function
* Updated ksoap2-android library

## 0.5 (2011-09-26)

* Added messages module
* Minor fixes

## 0.4.5 (2011-07-08)

* Optimized questions syncronization

## 0.4.4 (2011-07-05)

* Added Write message function on main menu
* Updated ksoap2-android library
* Minor fixes

## 0.4.3 (2011-06-15)

* Minor fixes
* Improved tests GUI

## 0.4.2 (2011-06-15)

* Now not answered questions score as 0
* Now is allowed to uncheck a checked answer on T/F and unique choice questions
* Improved tests GUI

## 0.4.1 (2011-06-14)

* Allowed negative scores on tests
* Fixed bug on questions syncronization

## 0.4 (2011-06-13)

* Added tests module
* Updated ksoap2-android library
* Minor fixes

## 0.3.10 (2011-05-19)

* Added assignment, survey and unknown notifications
* Added empty notifications message
* Added forced relogin if connection time exceeds a certain period
* Added incorrect user or password error message

## 0.3.9 (2011-05-03)

* Fixed empty fields bug on notifications module

## 0.3.8 (2011-04-27)

* Fixed bug on cleaning old notifications

## 0.3.7 (2011-04-14)

* Fixed notifications bug in surname

## 0.3.6 (2011-04-13)

* Added notification details
* Added upgrade dialog

## 0.3.5 (2011-04-05)

* Improved GUI
* Improved performance
* Added action bar
* Added category organization on main menu

## 0.3.4 (2011-03-27)

* Improved GUI
* Fixed minor errors on error messages

## 0.3.3 (2011-03-27)

* Redesigned GUI

## 0.3.2 (2011-03-24)

* Added first run dialog
* Improved preferences screen

## 0.3.1 (2011-03-21)

* Added automatic saving of preferences
* Added automatic notifications cleaning when username or password changes
* Changed background colors of notifications
* Fixed compatibility issues with some devices

## 0.3 (2011-03-08)

* Added notifications module
* Added Android 3.0 Honeycomb compatibility
* Updated ksoap2-android library

## 0.2.2 (2011-01-18)

* Added connection check
* Added functions list to main activity
* Added first run configuration
* Added automatic termination of login module
* Added login successful message
* Fixed launcher icons
* Fixed Android 1.6 compatibility issues
* Updated ksoap2-android library
* Disabled application restart on orientation change

## 0.2.1 (2010-12-15)

* Added Android 2.3 Gingerbread compatibility
* Completed login module

## 0.2 (2010-12-08)

* Added auto logout when user id or password changes
* Added initial database
* Added Android JUnit tests project
* Added Android DataFrameWork license in README.md
* Removed install location for Android 1.6 compatibility

## 0.1.1 (2010-11-06)

* Added workaround for Android emulator bug
* Added background image to main activity
* Added development notice in README.md
* Added ksoap2-android license in README.md
* Minor fixes in main layout
* Established install location to auto

## 0.1 (2010-11-03)

* First release
