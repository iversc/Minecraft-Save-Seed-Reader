Minecraft Save Seed Reader

------------------------------

Check the GitHub wiki for more info!
https://github.com/thedarkfreak/Minecraft-Save-Seed-Reader/wiki

This program reads and displays the seed from all found minecraft Save files.   Just select a world's name from the dropdown list that shows up to get that world's seed.

If the save file is a 1.8+ save file, you can switch the world between Creative and Survival mode by checking the box and hitting 'Save'.

If you do not see your save files, or if you get an error stating the saves aren't found, try locating the Minecraft saves folder manually, either in the popup or by opening the Help menu and choosing "Select MC Save Location".  You have to select the 'saves' folder, not any of the world folders in the 'saves' folder.



The way the program works is as follows:


First, it determines what the default location of the .minecraft/saves folder would be, depending on what OS is found.

If it finds Windows, it uses the path %APPDATA%\.minecraft\saves\.

If it finds Mac OS X, it uses the path /users/<name>/Library/Application Support/.minecraft/saves/.

If it finds Linux, it uses ~/.minecraft/saves/.



Next, it looks into each sub-folder it finds in the .minecraft/saves/ folder, and looks for a level.dat file.  If it finds one, it assumes that this folder is a valid Minecraft save folder, and gets the LevelName tag from the level.dat file to use as the world's name.  If the level.dat file doesn't have a LevelName tag(older ones may not), it instead uses the folder's name as the world name.

When a world is selected from the dropdown list, it looks up the RandomSeed tag from that folder's level.dat file.

If you choose a save folder manually, it first checks the folder you choose for the existence of 'level.dat', 'session.lock', and the 'region' folder.  If it finds all three, it guesses that the user chose a world folder instead, and searches the parent folder for other saves.