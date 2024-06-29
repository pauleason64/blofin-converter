The purpose of this program is to support filing your crypto taxes via Koinly for the BLOFIN exchange. Blofin do not have an API and only support a limited csv export file which is not compatible with Koinly.
Additionally , Blofin file is bugged in that it does not consistently display the USDT amount for a trade, only the crypto pair amount and the USDT rate used in the trade.

This program addresses these issues as follows:
1) it will reformat the csv file into Koinly format
2) It will calculate the USDT amount by multiplying the coins by the rate used and deduct fees too (converting fees as well if needed)

The applicationcan be run in 2 modes, interactive and batch.
1) In interactive mode, a form will be displayed for you to select a file to be imported and will then convert as highlighted above.
You have the option to then save the file or import additional ones and they will be merged. This is usefull because Blofin only allows a maximum of 90 days data to be downloaded at once.
Additionally, you may have multiple accounts or sub accounts but want to manage them as a single entity inside Koinly.

Further instructions are detailed in the Interactive section below

2) In batch mode you can provide a source folder to import file(s) from and a destination folder to save the processed files. Please see the command line options section on how to set these

RUNNING THE PROGRAM
-------------------

You will need Java 17 or higher installed on your machine to run this program. The JAR file present contains all the dependencies so there are no additional requirement
You might want to create a desktop shortcut for this porgram especially if using batch mode due to the command line options.
Example shortcut:
java -jar blofin2koinly-1.0-SNAPSHOT-jar-with-dependencies.jar -spc:/temp/testing/batch -dpc:/temp/testing/batch/processed -lpc:/temp/testing/logs -bi -del

COMMAND LINE OPTIONS
-------------------

-b         Indicates you want to run in batch mode, no user form displayed.
-bi         As above plus instructs program to ignore warning if destination folder is not empty
-sp[path]  Default source path for importing files *
-dp[path]  Default destination path for the converted files *
-m[filename*]  Batch Mode - merge all files in source folder into one file. Optional filename (no path) to save as otherwise is saved as blofin-merged.csv
-lp[path]  Default path for log files *
-del        Delete files after conversion (batch mode only)

* indicates mandatory when setting batch mode

INTERACTIVE MODE
----------------
The form is in 3 sections.
1. Top panel in blue.
    Use the import file button to select the file from file system.
    After you have selected the first file then the folder will be remembered for reuse in the current session
    If you want to load more files then when selecting a subsequent filw the system will warn you changes arent saved.
    You will get these options:
    a) Merge files. In the case all new records get added to the existing ones
    b) Save changes first. In this case the unsaved file is saved and the next file will load on its own, not merged

    NOTE: The system detects if the file being imported is Blofin or Koinly format and will load both

2.  Centre panel,
    Shows the imported transaction. At present these are editable but any changes are not saved

3.  Bottom panel.
    Status of progress.
    Use the save changes button to output the file(s) in koinly format.
    After you have selected the first file then the folder will be remembered for reuse in the current session
    By default the saved file name will have .koinly.csv appended but you can change the name as needed

It is highly recommended you perform all processing in a separate directory or drive and use backups of the blofin files to prevent accidental overwriting or deletion


POTENTIAL FOR IMPROVEMENTS
--------------------------
1. It was originally intended to provide support for deposits and withdrawals but i decided it was quicker to directly add deposits and withdrawals to Koinly.
A placeholder has been left for future support if Blofin later provide a csv export option for these transaction types

2. No editing of the imported files is supported. This really shouldn't be necessary ,but its possible rounding error may creep in on the converted USDT amount due to Blofins inconsistent handling of decimal places in the fill price.

NB yes i know there's no test suite.If you want to develop it further (eg maybe to support other exchanges) you may want to create some but i didnt bother as i hope blofin will eventually address this or Koinly will add support directly
-----------------------------------------------------------------------------------------------------------
Author:Paul Eason
Email: p3ason@gmail.com

