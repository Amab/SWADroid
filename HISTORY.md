##1.0 (2014-03-26)

* Amab (88):
      Re-enable Information module
      Fixed NullPointerException on log messages
      Added project documentation
      Updated gitignore
      Ensure that periodic synchronization is disabled before enabling
      Added thread synchronization to database transactions. Closes #92
      Modified Logcat tag for DataBaseHelper class
      Fixed display of file licenses
      Finished error management of database transactions
      Added transactions management to groups and group types database     operations
      Added server certificate error management
      Completed server certificate error management
      Added database rollback when an error occurs
      Fixed Logcat tag
      Finish background activity when an error occurs
      Fixed refresh button display on main activity
      Fixed launching of Notifications activity when an action bar alert is     clicked
      Fixed encoding issues between GNU/Linux (UTF-8) and other systems
      Optimize imports
      Modified error logcat
      Optimize imports
      Fixed hint of server field
      Modified text appearance of "why_password" dialog
      Removed end point of messages
      Added missing code in Information module
      Untrack library jar
      Now the services are launched in the main application process
      Updated ksoap2-android library to 3.2.0
      Updated ES Translations
      Removed log message
      Added periodic synchronization removal on logout
      Added management of HTTP status code 503 (Service Unavailable)
      Alert about unread notifications only
      Modified log messages
      Reorganized the main menu
      Used DialogFactory for WhyMyPasswordNotWork dialog
      Added cancel button to password recovery dialog
      Removed unused translations
      Now the back button goes to the parent directory in downloads module. If     the current directory is the root, exits the module
      Completed Information module
      Added parsing of links
      Untracked project.properties
      Fixed
      Added grouping of folders in downloads module
      Updated target SDK version to 19
      Added course name as subtitle (incomplete) and customized icons by     module in ActionBar
      Completed groups ActionBar
      Completed groups GUI
      Save icon updated with local version
      Added comments
      Completed RollCall ActionBar
      Updated icons to set Font Awesome
      Added buttons to Tests ActionBar (incomplete)
      Fixed icon references
      Fixed syllabusPracticals option menu
      Added course name as subtitle in downloads module
      Added padding to image
      Completed recover password module
      Added links to web pages of authors. Added José Antonio Guerrero Avilés     as contributor
      Removed old title bar from Tests module
      Fixed a misspelling
      Added CA translations for RecoverPassword Module
      Added built-in NumberPicker for Android 3.0 or higher
      Code cleaning
      Now all properties are readed by name
      Added log messages
      Added SQLException error message
      Modified synchronization of tests data. Now the old data is erased. All     data from the tests are always downloaded
      Added package.html to Information module
      Completed Tests ActionBar
      Reversed the changes in the synchronization of data from tests
      Migrated TestsMake from Module to MenuActivity and removed specific     Module code
      Migrated Tests from Module to MenuActivity and removed specific Module     code
      Added images to Tests main menu
      Updated enrollment and enrollment request icons
      Fixed wrong management for Users items
      Completed Groups GUI
      Modified icons
      Modified appearance of categories on main menu
      Code cleaning
      Fixed launching of Notifications Activity from a notification alert
      Modified appearance of categories on Groups module
      Disabled Rollcall module
      Optimized imports
      Fixed setActionView incompatibility with Android versions < 3.0
      Modified icon of Evaluation category
      Removed mailing list from preferences
      Completed Information module
      
* Alejandro Alcalde (67):
      Added Catalan translations
      Fixed typo in English strigs
      Fixed translation typos
      Added German translations
      Added French translations
      Added Italian translations
      Added Polish Translations
      Added Portuguese Translations
      Added licence field to file information dialog
      Remembering the user to introduce swad password, Not PRADO
      Refactoring Preferences class
      Fix sync bug when Android sync is disabled
      Organize imports
      Fixed NullPointerException in Preferences
      Added PRADO logo in Login Screen
      Redesign login activity
      Resolved merge conflict
      Adding additional info to login screen
      Fixed views overlap in login screen
      Added CA Translations
      Added DE Translations
      Added FR Translations
      Added IT Translations
      Added PL Translations
      Added PT Translations
      Organize onclicks events in one method
      Added log out option in preferences
      Updated CA Translations
      Updated DE Translations
      Updated FR Translations
      Updated IT Translations
      Updated PL Translations
      Updated PT Translations
      Remove back stack on logout
      Added recover password option
      Optimize imports
      Starting support ActionBar
      Added refresh item to ActionBar
      Added ActionBar to Notifications Activity
      Added actionBar to NotificationSingle
      Optimize imports
      Added ActionBar To DownloadsManager
      Removing old action_bar from Tests
      Bringing action bar to Groups Activity
      Fixed gui problem
      Added Up Button for Groups Activity
      Changed visibility of MenuActivity
      Replaced getActionBar with getSupportActionBar
      Hide Modules Activities
      Hide modules performing network connections
      Added Up button in all activiies
      Saving message data between activity states
      Fixed menu items for teachers
      Organize imports
      Added LoginActivity separated from SWADMain
      Fixed NullPointerException in Information
      Autoring LoginActivity class
      Fixed recoverPassword activity transparency
      Finished RecoverPassword Module
      Optimized main menu list
      Added support for teacher inscribing in groups
      Reduced text size of Groups Inscription Activity
      Fixed notification icon in single notifications
      Added ActionBar to Login Activity
      Added Progress animation when getting courses
      Fixed code formating
      Optimized notification item layout

* Jose Antonio Guerrero Aviles (23):
      Commit for can do pull
      Added processing the information received on information module
      Finished information module
      Commit for can do pull
      Finished implementation of information module and added new translates.
      Deleted some notes
      Fixed some bugs at functions on information modules (because was added     new information at Web Service getCourseInfo)
      Fixed some errors ar information module and EN and CA translations     (because translation at WS getCourseInfo was not equal).
      Fixed some errors at DE translations (because translation at WS     getCourseInfo was not equal).
      Fixed some errors at ES translations (because translation at WS     getCourseInfo was not equal).
      Fixed some errors at FR translations (because translation at WS     getCourseInfo was not equal).
      Fixed some errors at IT, PL and PT translations (because translation at     WS getCourseInfo was not equal).
      Errors correction  caused for the translations at WS getCourseInfo     finished
      Fixed some bugs, changed some variable names and added assessment into     Evaluation.
      Added assessment translations.
      Fixed some bugs at information module
      Fixed some bugs at information module
      Fixed some bugs at information modules
      Changed information module order. Updated icons.
      Added new icons
      Fixed bugs.     Now when change orientation screen, module dont recharge the information
      Fixed bugs
      Fixed some bugs at information module

##0.13 (2014-02-02)

* Added login screen
* Added SWAD certificate validation for SSL connections
* Added initial support for RESTful webservices
* Added Alejandro Alcalde as contributor
* If the web service key is invalid, force logout and reset password
  (this will show again the login screen)
* Try to detect if user is logging with e-Administration credentials
* Now Terena built-in certificate is used on Android API >= 11 (HONEYCOMB)
* Updated target API version to 10 (GINGERBREAD_MR1)
* Enabled MODE_MULTI_PROCESS in SharedPreferences for Android API >= 11
  (HONEYCOMB) in order to allow access from components running on a
  separate process (notifications service)
* Modified "user or password incorrect" error message

##0.12.7 (2013-12-14)

* Added notification read info from SWAD
* Refactored preferences store
* Updated BugSense library to version 3.6
* Updated ksoap2-library to version 3.1.1
* Fixed preferences reading on automatic synchronization
* Fixed bug on preferences saving. Modified automatic synchronization configuration
* Fixed bug on retrieving students list
* Fixed NullPointerException on String2Boolean parsing
* Fixed wrong field on tests config query

##0.12.6 (2013-11-24)

* Added sending of "seen notifications" info to SWAD
* Added management of seen notifications
* Updated Notifications table and added "mark all notifications as read" feature
* Synchronized code from Notifications class to NotificationsSyncAdapterService class
* Remove ignored files
* Changed action bar background color to black
* Improved error management
* Fixed a bug on BugSense initialization
* Fixed automatic synchronization on application upgrade
* Fixed black background color when the notifications list is being pulled

##0.12.5 (2013-11-10)

* Fixed incorrect layout and NullPointerException in message replys

##0.12.4 (2013-11-09)

* Fixed ParseException in sessions date

##0.12.3 (2013-11-09)

* Added authors and changelog WebView dialogs and reordered the preferences items
* Added PullToRefresh-ListView library project
* Added pull-to-refresh update system to the notifications ListView
* Added encryption of users table
* Added changelog dialog on application update
* Added sound, vibration and led configuration for alert notifications on preferences screen
* Added confirmation of successful transactions in the database
* User password is now stored encrypted
* Updated ksoap2-android library to version 3.1.0
* Updated Android Framework library
* Updated BugSense library
* Updated sendRequest method on Module class
* Now user password change doesn't clean the database
* Now the entire database is cleaned when the username changes in preferences screen
* Notice and Message dialogs now stay open when sending fails
* Refactored dialogs code into DialogFactory class
* Improved display of number questions screen in tests module
* Refactored alerts notification code to AlertNotification class
* Refactored fixLinks method to Utils class
* Improved error management
* Removed IOException error control in SOAP calls
* Refactored swadroid.widget into swadroid.gui.widget package
* Fixed bug on BugSense initialization
* Fixed misspellings on translation strings
* Fixed a bug when trying to download an user picture from an empty URL
* Fixed display bug on number of questions input of the tests

##0.12.2 (2013-06-19)

* Updated ksoap2-android library with 3.0.1-SNAPSHOT version from git repository

##0.12.1 (2013-06-09)

* Added forumPostCourse notification type
* Added groups package description to Javadocs
* Added icons in more resolutions
* Added wsdl folder
* Removed all references to University of Granada (UGR) in authors section
* Now the user input is maintained on rotation of the device
* Inspected and improved the source code with Android Studio
* Optimized processing of downloaded tests configurations
* Replaced Vector class with ArrayList class for optimization purposes
* Replaced the answers ListView with a LinearLayout in order to avoid issues with the parent
  ScrollView in TestMake
* Updated BugSense library
* Avoid overriding of synchronization user preferences on application upgrades
* Fixed activity restart on TestMake
* Fixed NullPointerException exceptions when inserting null values into not null database fields
* Reverted to old ksoap2-android version in order to recover SoapFault processing
* Now handled exceptions are always shown in logcat
* Fixed IllegalStateException: Can not perform this action after onSaveInstanceState
  Added WeakReference to activity in order to fix this issue
* Modified NULL value detection
* Bugfixes

##0.12 (2013-04-20)

* Added new notification types "documentFile", "sharedFile", "enrollment"
  and "enrollmentRequest". Changed notifications icon
* Added questions feedback and answers feedback in tests module
* Added all languages that are present in SWAD
* Added configuration of max limit of stored notifications on preferences screen
* Added checkbox preference for enable/disable auto sync from preferences screen
* Added periodic synchronization. Added synchronization interval selection on preferences screen
* Updated minimum required API to 8 (FROYO 2.2)
* Updated Downloads Module. Added DownloadsManager for API >= GINGERBREAD
* Updated the regular expression for nickname validation. Added "@" character at the beginning
  and adjusted the max length of the nickname
* Updated icons for notifications
* Updated BugSense and ksoap2-android libraries
* Disabled "Clean database" option
* Now devices with no rear camera can launch the Rollcall module in manual mode
* Changed position of question score in tests
* Refactored code
* Fixed IllegalArgumentException exception on some activities when changing orientation
* Fixed wrong score calculation in multiple choice questions. The correct
  answers not checked by the user are NOT an error and score 0 points
* Fixed non decimal keyboard type for decimal numbers on some devices

##0.11.3 (2013-03-03)

* Added Bugsense error reporting plugin for track unhandled and handled exceptions. Error reports are automatically sended when
  the exception is catched
* Shows default server in preference summary when the preference value is empty
* Updated Google Play references
* Updated ksoap2-android library
* Code cleaning
* Fixed NullPointer exceptions when cleaning notifications and courses on preferences change

##0.11.2 (2013-02-24)

* Added refresh button on main screen to update courses without clean the whole database
* Hided reply button when notification type is not a message
* Fixed wrong storage of database key when closing preferences screen
* Fixed hided soft menu button on Android ICS
* Fixed parsing of marksFile notifications when the student doesn't exists in the marks file
* Fixed activity restart when TestMake returns from background to foreground
* Fixed wrong behaviour of checkboxes in Android 4.2 (isChecked() method behaviour is exactly the opposite in Android 4.2)

##0.11.1 (2013-01-26)

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

##0.11 (2012-12-15)

* Added downloads module
* Added encryption to notifications
* Added database version checks in upgradeDB method to apply only required changes to the database structure
* Modified the order of modules in main menu to coincide with the order of functions in SWAD
* Refactored main package classes. Moved GUI and utilities classes to separate packages

##0.10.1 (2012-11-17)

* Added Android 4.2 compatibility
* Added red highlight for groups without free spot
* Changed to lighter blue of background of groups with real membership, darker gray on text about vacants
* Fixed available choose for groups with real membership
* Fixed problem with uppercase letter in notifications without summary

##0.10 (2012-11-09)

* Added module for enrollment to course groups
* Added missing javadoc packages info
* Updated Android DataFramework library. Removed the binary version and added the latest source code version from GitHub
* GUI improvements
* Fixed DNI processing when using a NIE instead of a DNI (NIE format X5264085Y)
* Fixed a misspelling in english language file
* Fixed some bugs in course selection spinner
* Fixed bug when checking available connections

##0.9.3 (2012-07-20)

* Added folders support in server URL
* Updated ksoap2-android library
* Improved error messages

##0.9.2 (2012-07-10)

* camera.autofocus feature marked as optional in order to fix device incompatibilities

##0.9.1 (2012-07-09)

* Added DNI with letter (first and last) support and DNI with zeros support
* Added Android 4.1 compatibility
* Fixed xlarge screens support (accidentally removed during last merge)

##0.9 (2012-07-01)

* Added RollCall module

##0.8.1 (2012-05-20)

* Fixed closed cursor access exception on Android 4.x platforms
* Added message replys from open notifications

##0.8 (2012-05-01)

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

##0.7.2 (2012-02-22)

* Added notification alerts on status bar
* Added navigation ability for HTML links in notifications
* Added WebView activity for all notifications
* Improved query statements

##0.7.1 (2012-01-11)

* Fixed rendering errors in marks function
* Improved rendering speed in marks function

##0.7 (2012-01-10)

* Added marks function in notifications module

##0.6.2 (2011-12-09)

* Added Blog URL to preferences screen
* Added Catalan language by Francisco Manuel Herrero Pérez
* Updated ksoap2-android library
* Reinitialized last course selected on database cleaning
* Minor changes on error messages

##0.6.1 (2011-11-16)

* Added Google+ account to preferences screen
* Optimized database access
* Fixed menu operation in all activities
* Minor improvements

##0.6 (2011-11-06)

* Added Android 4.0 compatibility
* Added sharing options to application menu and preferences
* Added clean database option to application menu
* Added name of SWAD's creator to author preferences

##0.5.2 (2011-09-29)

* Optimized questions syncronization
* Added real names of receivers in sendedMessageMsg
* Fixed bug on test questions syncronization

##0.5.1 (2011-09-26)

* Fixed bug in reply messages function
* Updated ksoap2-android library

##0.5 (2011-09-26)

* Added messages module
* Minor fixes

##0.4.5 (2011-07-08)

* Optimized questions syncronization

##0.4.4 (2011-07-05)

* Added Write message function on main menu
* Updated ksoap2-android library
* Minor fixes

##0.4.3 (2011-06-15)

* Minor fixes
* Improved tests GUI

##0.4.2 (2011-06-15)

* Now not answered questions score as 0
* Now is allowed to uncheck a checked answer on T/F and unique choice questions
* Improved tests GUI

##0.4.1 (2011-06-14)

* Allowed negative scores on tests
* Fixed bug on questions syncronization

##0.4 (2011-06-13)

* Added tests module
* Updated ksoap2-android library
* Minor fixes

##0.3.10 (2011-05-19)

* Added assignment, survey and unknown notifications
* Added empty notifications message
* Added forced relogin if connection time exceeds a certain period
* Added incorrect user or password error message

##0.3.9 (2011-05-03)

* Fixed empty fields bug on notifications module

##0.3.8 (2011-04-27)

* Fixed bug on cleaning old notifications

##0.3.7 (2011-04-14)

* Fixed notifications bug in surname

##0.3.6 (2011-04-13)

* Added notification details
* Added upgrade dialog

##0.3.5 (2011-04-05)

* Improved GUI
* Improved performance
* Added action bar
* Added category organization on main menu

##0.3.4 (2011-03-27)

* Improved GUI
* Fixed minor errors on error messages

##0.3.3 (2011-03-27)

* Redesigned GUI

##0.3.2 (2011-03-24)

* Added first run dialog
* Improved preferences screen

##0.3.1 (2011-03-21)

* Added automatic saving of preferences
* Added automatic notifications cleaning when username or password changes
* Changed background colors of notifications
* Fixed compatibility issues with some devices

##0.3 (2011-03-08)

* Added notifications module
* Added Android 3.0 Honeycomb compatibility
* Updated ksoap2-android library

##0.2.2 (2011-01-18)

* Added connection check
* Added functions list to main activity
* Added first run configuration
* Added automatic termination of login module
* Added login successful message
* Fixed launcher icons
* Fixed Android 1.6 compatibility issues
* Updated ksoap2-android library
* Disabled application restart on orientation change

##0.2.1 (2010-12-15)

* Added Android 2.3 Gingerbread compatibility
* Completed login module

##0.2 (2010-12-08)

* Added auto logout when user id or password changes
* Added initial database
* Added Android JUnit tests project
* Added Android DataFrameWork license in README.md
* Removed install location for Android 1.6 compatibility

##0.1.1 (2010-11-06)

* Added workaround for Android emulator bug
* Added background image to main activity
* Added development notice in README.md
* Added ksoap2-android license in README.md
* Minor fixes in main layout
* Established install location to auto

##0.1 (2010-11-03)

* First release
