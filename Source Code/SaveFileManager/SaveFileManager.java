/**
 * @Author rennat47
 * @Date 5/15/2022
 */
package SaveFileManager;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.*;

public class SaveFileManager
{
    //GUI Object
    private SaveFileManagerGUI GUI;
    private String backupSaveDirectory;
    private String eldenRingSaveDirectory;
    //Map that ties file names to the file
    private Map<String, File> fileMap;
    //Vector that is passed to the GUI to display file names
    private Vector<String> fileNames;

    //Enum of filter values for sorting
    private enum Filters
    {
        ALPHABETICAL_ASCENDING,
        ALPHABETICAL_DESCENDING,
        DATE_NEW_TO_OLD,
        DATE_OLD_TO_NEW
    }

    //Filter enum
    private Filters filter = Filters.ALPHABETICAL_ASCENDING;

    /**
     * Default Constructor
     */
    public SaveFileManager()
    {
        //Populate the GUI
        GUI = new SaveFileManagerGUI();
        //Initialize file map
        fileMap = new HashMap<String, File>();
        //Initialize Display Vector
        fileNames = new Vector<String>();
        //Load the Elden Ring appdata folder
        eldenRingSaveDirectory = System.getenv("APPDATA") + "\\EldenRing";
        //Load the config data
        loadConfigData();
        //Pass the GUI the Display Vector to render and update it
        GUI.saveList.setListData(fileNames);
        updateSaveListGUI();
        //Method call to create all the listeners for the event thread
        createListeners();
    }

    /**
     * loadConfigData()
     * Checks to see if the config folder and file already exits in the appdata location for this program.
     * If it doesn't exist it will prompt the user to select a backup save folder and close if they do not select a folder.
     * Otherwise, the folder already exist, and it will retrieve the saved backup save folder path and load it into the class variable backupSaveFolder
     */
    private void loadConfigData()
    {
        //System.out.println(System.getenv("APPDATA"));
        //Program Data Folder
        File dataFolder = new File(System.getenv("APPDATA") + "\\EldenRingSaveFileManager");
        //Program data File
        File dataFile = new File(dataFolder.getPath() + "\\data.txt");
        //Check if data folder exists (mostly for first time setup)
        if (!dataFolder.isDirectory())
        {
            //Create the directory
            dataFolder.mkdir();
            try
            {
                //Create the data file
                dataFile.createNewFile();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            //Prompt user to select a backup save folder
            JOptionPane.showMessageDialog(GUI.window, "Backup folder not detected \nClick okay to select a backup folder", "First time setup", JOptionPane.INFORMATION_MESSAGE);
            if (!selectNewSaveFolder())
            {
                dataFile.delete();
                dataFolder.delete();
                System.exit(0);
            }
        } else
        {
            try
            {
                Scanner s = new Scanner(dataFile);
                //Move the scanner to the SaveFolder Line
                s.findInLine("SaveFolder:");
                //Retrieve the path from the save folder line
                String path = s.nextLine();
                //Trim the " "
                path = path.substring(1, path.length() - 1);
                //Point the backupSaveDirectory to the selected path
                backupSaveDirectory = path;
                s.close();
                loadSavesToMap();
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }

    }

    /**
     * updateSaveListGIU() - updates the visual saveList JList to display only the names of saves
     * that contain the current search input and sorts the search
     */
    private void updateSaveListGUI()
    {
        //Clear current selected value
        GUI.saveList.clearSelection();
        //Clear Display Vector
        fileNames.clear();
        //Refill Display Vector
        fileNames.addAll(fileMap.keySet());
        //Filter out search results
        fileNames.removeIf(s -> (!(s.toLowerCase().contains(GUI.searchBar.getText().toLowerCase()))));
        //Sort
        fileNames.sort(new Comparator<String>()
        {
            @Override
            public int compare(String o1, String o2)
            {
                //Initialize return value r to zero to make the compiler happy
                int r = 0;
                boolean flip = false;
                switch (filter)
                {
                    case ALPHABETICAL_DESCENDING:
                        //Set flip flag
                        flip = true;
                    case ALPHABETICAL_ASCENDING:
                        //Compare the strings using the String compareTo method
                        r = o1.compareTo(o2);
                        break;
                    case DATE_NEW_TO_OLD:
                        //Set flip flag
                        flip = true;
                    case DATE_OLD_TO_NEW:
                        //Compare the files' creation date using the FileTime compareTo method
                        File f1 = fileMap.get(o1);
                        File f2 = fileMap.get(o2);
                        try
                        {
                            FileTime fileTime1 = Files.readAttributes(f1.toPath(), BasicFileAttributes.class).creationTime();
                            FileTime fileTime2 = Files.readAttributes(f2.toPath(), BasicFileAttributes.class).creationTime();
                            r = fileTime1.compareTo(fileTime2);
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }
                if (flip)
                    r *= -1;
                return r;
            }
        });
        //Force update GUI
        GUI.saveList.repaint();
        GUI.saveList.revalidate();
    }

    /**
     * @param fileName file name to be validated
     * @return false if the file name contains and illegal character or is only whitespace
     */
    private boolean validateFileName(String fileName)
    {
        String[] invalidValues = {"<", ">", ":", "\"", "/", "\\", "|", "?", "*"};
        //Check for empty string
        if (fileName.trim().equals(""))
        {
            return false;
        }
        //Check for invalid characters
        for (String c : invalidValues)
        {
            if (fileName.contains(c))
            {
                return false;
            }
        }
        return true;
    }


    /**
     * copyDirectoryChildren() - recursively copies all the contents of a directory into another one (with its subdirectories)
     *
     * @param source      the source directory
     * @param destination the new directory
     */
    private void copyDirectoryContentTo(File source, File destination)
    {
        try
        {
            //Ensure the directory exits
            if (!destination.exists())
            {
                destination.mkdir();
            }
            //Retrieve directory's children
            File[] children = source.listFiles();
            //Iterate through its children
            for (File child : children)
            {
                //Create the path for the new copied file
                Path newPath = Paths.get(destination.getPath() + "\\" + child.getName());
                //System.out.println(newPath);
                //If the main directory has a subdirectory child we must recursively call this function on that
                if (child.isDirectory())
                {
                    copyDirectoryContentTo(child, newPath.toFile());
                }
                //Otherwise, copy the file. Replace if a file there already exits
                else
                {
                    Files.copy(child.toPath(), newPath, StandardCopyOption.REPLACE_EXISTING);
                }

            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a directory and all of it's contents
     *
     * @param directory the directory that will be deleted
     */
    private void deleteDirectory(File directory)
    {
        if (!directory.exists())
        {
            return;
        }
        //Get Directory's Children
        File[] children = directory.listFiles();
        //Iterate through its children
        for (File child : children)
        {
            //If the main directory has a subdirectory child we must recursively call this function on that
            if (child.isDirectory())
            {
                deleteDirectory(child);
            } else
            {
                try
                {
                    //Delete File
                    Files.delete(child.toPath());
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        try
        {
            //Delete Directory After it's been emptied
            Files.delete(directory.toPath());
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * selectNewSaveFolder() - Prompts the user to select a save folder for their preference and updated the backupSaveFolder class variable
     *
     * @return true if the user selected a folder false if they canceled
     */
    private boolean selectNewSaveFolder()
    {
        File dataFile = new File(System.getenv("APPDATA") + "\\EldenRingSaveFileManager\\data.txt");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setDialogTitle("Select A Folder For Your Saves");
        int option = fileChooser.showOpenDialog(GUI.window);
        fileChooser.setCurrentDirectory(Paths.get("").toFile());
        if (option == JFileChooser.APPROVE_OPTION)
        {
            File file = fileChooser.getSelectedFile();
            backupSaveDirectory = file.getPath();
            //System.out.println("Folder Selected: " + file.getName());
            try
            {
                String content = "SaveFolder:\"" + backupSaveDirectory + "\"";
                Files.write(dataFile.toPath(), content.getBytes(StandardCharsets.UTF_8));
                loadSavesToMap();

            } catch (IOException e)
            {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    /**
     * loadSavesToMap() - loads all the directories into the hashmap of saves. Assumes that the saveDirectory is where all the saves are located
     */
    private void loadSavesToMap()
    {
        fileMap.clear();
        for (File subFile : Objects.requireNonNull(new File(backupSaveDirectory).listFiles()))
        {
            if (subFile.isDirectory())
            {
                fileMap.put(subFile.getName(), subFile);
            }
        }
    }


    /**
     * createListeners() - Method that adds and implements all the event listeners
     */
    private void createListeners()
    {

        /*
         * Search Bar Actions
         * Call the updateSaveListGUI every time the text in the bar changes in any way
         */
        GUI.searchBar.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                updateSaveListGUI();
            }

            @Override
            public void removeUpdate(DocumentEvent e)
            {
                updateSaveListGUI();
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {
                updateSaveListGUI();
            }
        });


        /*
         * Clear Button Action
         * Set the search bar text to an empty string when pressed
         */
        GUI.clearSearchButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                GUI.searchBar.setText("");
                //System.out.println(e.getActionCommand());
            }
        });

        /*
         * New Button Action
         * Prompts the user for a save name, then if a valid file name copies
         * the save from the Elden Ring appdata folder to the users chosen backup save folder
         */
        GUI.newButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                //System.out.println(e.getActionCommand());
                String newFileName = JOptionPane.showInputDialog(GUI.window, "Enter a file name");
                if (newFileName != null)
                {
                    if (validateFileName(newFileName))
                    {
                        //System.out.println(newFileName);
                        File newSave = new File(backupSaveDirectory + "\\" + newFileName);
                        copyDirectoryContentTo(new File(eldenRingSaveDirectory), newSave);
                        fileMap.put(newSave.getName(), newSave);
                        //System.out.println(newSave.getPath() + "   " + newSave.getName());
                        updateSaveListGUI();
                    } else
                    {
                        //System.out.println("Invalid name");
                    }
                }
            }
        });

        /*
         * Copy Button Action
         * If a save is selected it creates a copy of the save with the name
         * of the copied save + "Copy #" where # starts at 1 and counts up until a no save with the same name
         */
        GUI.copyButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                //System.out.println(e.getActionCommand());
                String selected = GUI.saveList.getSelectedValue();
                if (selected != null)
                {

                    int copyAttempt = 1;
                    File newCopy = new File(backupSaveDirectory + "\\" + selected + " Copy " + copyAttempt);
                    while (newCopy.isDirectory())
                    {
                        //System.out.println(copyAttempt);
                        copyAttempt++;
                        newCopy = new File(backupSaveDirectory + "\\" + selected + " Copy " + copyAttempt);
                    }
                    File oldName = fileMap.get(selected);
                    copyDirectoryContentTo(oldName, newCopy);
                    fileMap.put(newCopy.getName(), newCopy);
                    updateSaveListGUI();
                }
            }
        });

        /*
         * Load Button Action
         * Loads the currently selected save into the main elden ring appdata folder
         * it also creates an automatic backup of the main elden ring save folder,
         * and it gets named [Auto Generated] Previous save. It always overrides the last
         * auto generated backup
         */
        GUI.loadButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                //System.out.println(e.getActionCommand());

                String selection = GUI.saveList.getSelectedValue();
                if (selection != null)
                {
                    String autoName = "[Auto Generated] Previous Save";
                    try
                    {
                        File autoSave = new File(backupSaveDirectory + "\\" + autoName);
                        File mainSave = new File(eldenRingSaveDirectory);
                        File loadSave = new File(backupSaveDirectory + "\\" + selection);
                        File tempFile = Files.createTempDirectory("SaveManagerTemp").toFile();
                        tempFile.deleteOnExit();
                        //System.out.println("temp dir at:  " + tempFile.getPath());
                        copyDirectoryContentTo(mainSave, tempFile);
                        copyDirectoryContentTo(loadSave, mainSave);
                        copyDirectoryContentTo(tempFile, autoSave);
                        if (fileMap.containsKey(autoName))
                        {
                            fileMap.replace(autoName, autoSave);
                        } else
                        {
                            fileMap.put(autoSave.getName(), autoSave);
                        }
                        deleteDirectory(tempFile);
                        updateSaveListGUI();
                    } catch (IOException ex)
                    {
                        ex.printStackTrace();
                    }

                }

            }
        });

        /*
         * Rename Button Action
         * If a save is selected the user is prompted to enter a new save name
         * if the name is valid the selected save is changed to that new name
         */
        GUI.renameButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                //System.out.println(e.getActionCommand());
                String selected = GUI.saveList.getSelectedValue();
                if (selected != null)
                {
                    String newFileName = JOptionPane.showInputDialog(GUI.window, "Enter a save file name");
                    if (newFileName != null)
                    {
                        if (validateFileName(newFileName))
                        {
                            //System.out.println(newFileName);
                            File newName = new File(backupSaveDirectory + "\\" + newFileName);
                            File oldName = fileMap.get(selected);
                            oldName.renameTo(newName);
                            fileMap.remove(selected);
                            fileMap.put(newName.getName(), newName);
                            updateSaveListGUI();
                        } else
                        {
                            //System.out.println("Invalid name");
                        }
                    }
                }

            }
        });

        /*
         * Delete Button Action
         * If a save is selected the entire directory of the save gets deleted
         */
        GUI.deleteButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                //System.out.println(e.getActionCommand());
                String selection = GUI.saveList.getSelectedValue();
                if (selection != null)
                {
                    File saveToBeDeleted = fileMap.get(selection);
                    if (saveToBeDeleted.isDirectory())
                    {
                        deleteDirectory(saveToBeDeleted);
                        fileMap.remove(selection);
                        updateSaveListGUI();
                    }
                }
            }
        });

        /*
         * Ascending Alphabetical Action
         * changes filter
         */
        GUI.ascendingAlphabetical.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {

                //System.out.println(e.getActionCommand());
                filter = Filters.ALPHABETICAL_ASCENDING;
                updateSaveListGUI();

            }
        });
        /*
         * Descending Alphabetical Action
         * changes filter
         */
        GUI.descendingAlphabetical.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                //System.out.println(e.getActionCommand());
                filter = Filters.ALPHABETICAL_DESCENDING;
                updateSaveListGUI();

            }
        });

        /*
         * Date New To Old Action
         * changes filter
         */
        GUI.dateNewtoOld.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                //System.out.println(e.getActionCommand());
                filter = Filters.DATE_NEW_TO_OLD;
                updateSaveListGUI();
            }
        });

        /*
         * Date Old To New Action
         * changes filter
         */
        GUI.dateOldToNew.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                //System.out.println(e.getActionCommand());
                filter = Filters.DATE_OLD_TO_NEW;
                updateSaveListGUI();

            }
        });

        /*
         * Change Save Location Action
         * Prompts the user to choose a directory where their backup saves are stored at
         */
        GUI.changeSaveLocation.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                //System.out.println(e.getActionCommand());
                if (selectNewSaveFolder())
                {
                    updateSaveListGUI();
                }
            }
        });

        /*
         * Run Windows file explorer starting the root directory at the backup save location
         */
        GUI.openSaveLocation.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    Runtime.getRuntime().exec("explorer.exe /root," + backupSaveDirectory);
                } catch (IOException ex)
                {
                    ex.printStackTrace();
                }
            }
        });

        /*
         * Opens the help window
         */
        GUI.help.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                new HelpWindow(GUI.window);
            }
        });
    }


}
