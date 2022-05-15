/**
 * @Author rennat47
 * @Date 5/15/2022
 */
package SaveFileManager;

import javax.swing.*;
import java.awt.*;

public class HelpWindow extends JFrame
{

    private String helpText = "LOADING SAVES:\n" +
            "Do not try and load a save while playing because it will just get instantly overridden the next time the game auto saves." +
            "To properly load a new save you must close the game and load your selected save for the game to recognize the change.\n\n" +
            "MAKING BACKUPS: \n" +
            "You can make a backup at any time while playing. Drop an item and wait for the save logo in the corner to finish or quick to the main menu. Then just click new and create your backup\n\n" +
            "WHAT EACH BUTTON DOES: \n" +
            "[NEW]: Creates a new backup save by coping your elden ring appdata folder to your backup save folder\n\n" +
            "[COPY]: Creates a copy of a selected save with it's name + Copy #\n\n" +
            "[LOAD]: This loads the currently selected save into your main elden ring appdata folder. An automatic backup is made called \"[Auto Generated] Previous Save\" in case you misclicked" +
            "this gets overwritten each time you load.\n\n" +
            "[RENAME]: Prompts you to enter a new name for the selected save\n\n" +
            "[DELETE]: Deletes the selected save forever\n\n" +
            "\n" +
            "IMPORTING PRE-EXISTING SAVES:\n" +
            "The save file manager works by keeping a backup folder full of subfolders of all your saves. The subfolder names is the name of the save. If you already have a directory with a bunch of save folders" +
            "you can select that to be your backup save location, or you can copy your subsave folders directly into where you selected your backup folder to be for the save file manager";


    public HelpWindow(JFrame parent)
    {
        this.setLocation(parent.getX(), parent.getY());
        this.setTitle("Help");
        this.setSize(new Dimension(600, 600));
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setResizable(false);
        JPanel p = new JPanel();
        JTextArea textBox = new JTextArea(helpText);
        textBox.setEditable(false);
        textBox.setLineWrap(true);
        textBox.setWrapStyleWord(true);
        textBox.setMaximumSize(new Dimension(580, 580));
        textBox.setMinimumSize(new Dimension(580, 580));
        textBox.setPreferredSize(new Dimension(580, 580));
        p.add(textBox);
        this.add(p);
        this.setVisible(true);
    }

}
