## embARC

### About
embARC is a new open source tool for metadata embedding and validation.
Named embARC for Metadata Embedded for Archival Content, this beta release includes flexibility functionality for Digital Picture Exchange (DPX) files to support FADGI’s Guidelines for Embedded Metadata within DPX File Headers for Digitized Motion Picture Film as well as required SMPTE 268 metadata rules.
embARC enables users to audit and correct internal metadata of both individual files or an entire DPX sequence while not impacting the image data.

### How to build
Do the following three tasks first.
 1. Gradle Tasks -> build -> clean
 2. Gradle Tasks -> launch4j -> createExe
 3. Gradle Tasks -> macappbundle -> createApp

DMG
 1. Navigate to build/macApp
 2. Right click embARC.app, choose Show Package Contents, open Contents/Java
 3. Delete embARC-1.0.jar, leave this finder window open
 4. Open another Finder window, navigate to build/launch4j/lib
 5. Copy embARC.jar and paste it into the Contents/Java folder from step 5
 6. Run command “appdmg embARCdmg.json ~/Desktop/embARC.dmg”
 
EXE ~ You will need Inno Setup Compiler installed on Windows
 1. In Windows, open a file explorer, navigate to MAC side build/launch4j 
 2. Create a folder on your Windows desktop called “embARC-prebuild“ and copy the exe and lib folder from step 1 and place them in the new folder.
 3. Create an empty folder on your Windows desktop entitled “Output”
 4. Open embARC_exe_installer.iss and update MyAppVersion appropriately and run the iss script
 5. App exe will now be in the Output folder you created on the Desktop
 