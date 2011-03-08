Minecraft Save Seed Reader

------------------------------


This program reads and displays the seed from all found minecraft Save files.   Just select a world's name from the dropdown list that shows up to get that world's seed.

If you do not see your save files, or if you get an error stating the saves aren't found, try locating the Minecraft saves folder manually, either in the popup or by opening the Help menu and choosing "Select MC Save Location".   Do NOT use the folder containing the level.dat file; (i.e. if your world is named World1, do NOT choose the World1 folder).   Instead, choose the folder that THAT folder is in(in our example, choose the folder that World1 is in).






The way the program works is as follows:


First, it determines what the default location of the .minecraft/saves folder would be, depending on what OS is found.

If it finds Windows, it uses the path %APPDATA%\.minecraft\saves\.

If it finds Mac OS X, it uses the path /users/<name>/Library/Application Support/.minecraft/saves/.

If it finds Linux, it uses ~/.minecraft/saves/.



Next, it looks into each sub-folder it finds in the .minecraft/saves/ folder, and looks for a level.dat file.  If it finds one, it assumes that this folder is a valid Minecraft save folder, and gets the LevelName tag from the level.dat file to use as the world's name.  If the level.dat file doesn't have a LevelName tag(older ones may not), it instead uses the folder's name as the world name.

When a world is selected from the dropdown list, it looks up the RandomSeed tag from that folder's level.dat file.

If you choose a save folder manually, it searches the sub-folders of the folder you chose, which is why you need to choose the folder that holds the sub-folders, not the folder that holds a level.dat file.