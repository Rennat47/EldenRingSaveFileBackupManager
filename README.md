# EldenRingSaveFileManager
Simple Java program that makes managing your Elden Ring backup saves a tad easier 

LOADING SAVES:
Do not try and load a save while playing because it will just get instantly overridden the next time the game auto saves.
To properly load a new save you must close the game and load your selected save for the game to recognize the change.

MAKING BACKUPS: 
You can make a backup at any time while playing. Drop an item and wait for the save logo in the corner to finish or quick to the main menu. 
Then just click new and create your backup

WHAT EACH BUTTON DOES:
[NEW]: Creates a new backup save by coping your elden ring appdata folder to your backup save folder

[COPY]: Creates a copy of a selected save with it's name + Copy #

[LOAD]: This loads the currently selected save into your main elden ring appdata folder. An automatic backup is made called "[Auto Generated] Previous Save" in case you misclickedthis gets overwritten each time you load.

[RENAME]: Prompts you to enter a new name for the selected save

[DELETE]: Deletes the selected save forever


IMPORTING PRE-EXISTING SAVES:
The save file manager works by keeping a backup folder full of subfolders of all your saves. 
The subfolder names is the name of the save.
If you already have a directory with a bunch of save foldersyou can select that to be your backup save location, 
or you can copy your subsave folders directly into where you selected your backup folder to be for the save file manager
