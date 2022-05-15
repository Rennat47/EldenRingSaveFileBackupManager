/**
 * @Author rennat47
 * @Date 5/15/2022
 */
package SaveFileManager;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

public class SaveFileManagerGUI
{

    public JFrame window;
    public JPanel mainPanel;
    public JMenuBar menuBar;
    public JMenu settingMenu;
    public JMenu sortMenu;
    public JMenuItem changeSaveLocation;
    public JMenuItem openSaveLocation;
    public JMenuItem help;
    public JMenuItem ascendingAlphabetical;
    public JMenuItem descendingAlphabetical;
    public JMenuItem dateNewtoOld;
    public JMenuItem dateOldToNew;
    public JPanel topPanel;
    public JPanel bottomPanel;
    public JTextField searchBar;
    public JButton clearSearchButton;
    public JScrollPane listScrollPane;
    public JList<String> saveList;
    public JButton newButton;
    public JButton copyButton;
    public JButton loadButton;
    public JButton renameButton;
    public JButton deleteButton;

    public SaveFileManagerGUI()
    {
        window = new JFrame("Elden Ring Save Manger");
        window.setSize(500, 600);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setLocationRelativeTo(null); //centers the window

        menuBar = createMenuBar();
        window.setJMenuBar(menuBar);

        mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.CENTER;
        c.weighty = 0.4;

        c.gridx = 0;
        c.gridy = 0;
        topPanel = createTopPanel();
        mainPanel.add(topPanel, c);

        c.gridx = 0;
        c.gridy = 2;
        listScrollPane = createScrollPlane();
        mainPanel.add(listScrollPane, c);

        c.gridx = 0;
        c.gridy = 3;
        bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, c);

        window.add(mainPanel);
        window.setVisible(true);
    }

    private JMenuBar createMenuBar()
    {
        //JMenuBar To be Returned
        JMenuBar menuBarBuilder = new JMenuBar();

        //Settings Tab
        settingMenu = new JMenu("Settings");

        //View Tab
        sortMenu = new JMenu("View");

        //Items for settings tab
        changeSaveLocation = new JMenuItem("Change Save Location");
        openSaveLocation = new JMenuItem("Open Save Location");
        help = new JMenuItem("Help");

        //Items for View Tab
        ascendingAlphabetical = new JMenuItem("a-z");
        descendingAlphabetical = new JMenuItem("z-a");
        dateNewtoOld = new JMenuItem("Newest");
        dateOldToNew = new JMenuItem("Oldest");

        //Add Items to settings tab
        settingMenu.add(changeSaveLocation);
        settingMenu.add(openSaveLocation);
        settingMenu.add(help);

        //Add Items to view tab
        sortMenu.add(ascendingAlphabetical);
        sortMenu.add(descendingAlphabetical);
        sortMenu.add(dateNewtoOld);
        sortMenu.add(dateOldToNew);

        //Add Tabs to menubar
        menuBarBuilder.add(settingMenu);
        menuBarBuilder.add(sortMenu);

        //return constructed menubar
        return menuBarBuilder;
    }

    private JPanel createTopPanel()
    {
        //JPanel to be returned
        JPanel returnPanel = new JPanel();

        //Panel parameters
        topPanel = new JPanel();
        topPanel.setPreferredSize(new Dimension(400, 40));
        topPanel.setMaximumSize(new Dimension(400, 40));
        topPanel.setMinimumSize(new Dimension(400, 40));

        //Create search bar
        searchBar = new JTextField();
        searchBar.setMinimumSize(new Dimension(300, 30));
        searchBar.setPreferredSize(new Dimension(300, 30));
        searchBar.setMaximumSize(new Dimension(300, 30));

        //Create clear search button
        clearSearchButton = createButton("Clear", 80, 30);
        clearSearchButton.setBackground(Color.lightGray);

        //Add components to panel
        returnPanel.add(searchBar);
        returnPanel.add(clearSearchButton);

        //Return constructed panel
        return returnPanel;
    }

    private JScrollPane createScrollPlane()
    {
        //First we build the JList for the scrollPane
        saveList = new JList<>();
        saveList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        saveList.setLayoutOrientation(JList.VERTICAL);
        saveList.setFixedCellHeight(40);
        saveList.setVisibleRowCount(-1);
        saveList.setFont(new Font("Courier",Font.BOLD, 18));
        saveList.setListData(new Vector<String>());
        saveList.setBackground(new Color(222,222,222));

        //Build the scrollPane with the constructed JList
        JScrollPane returnPlane = new JScrollPane(saveList);
        returnPlane.setPreferredSize(new Dimension(400, 425));
        returnPlane.setMinimumSize(new Dimension(400, 425));
        returnPlane.setMaximumSize(new Dimension(400, 425));

        //Return constructed scrollPane
        return returnPlane;
    }

    private JPanel createBottomPanel()
    {
        //Construct the panel
        JPanel returnPanel = new JPanel();
        returnPanel.setPreferredSize(new Dimension(490, 50));
        returnPanel.setMaximumSize(new Dimension(490, 50));
        returnPanel.setMinimumSize(new Dimension(490, 50));

        //Create buttons
        newButton = createButton("New", 80, 40);
        newButton.setBackground(Color.lightGray);
        copyButton = createButton("Copy", 80, 40);
        copyButton.setBackground(Color.lightGray);
        loadButton = createButton("Load", 80, 40);
        loadButton.setBackground(Color.lightGray);
        renameButton = createButton("Rename", 80, 40);
        renameButton.setFont(new Font("Dialog", Font.BOLD, 11));
        renameButton.setBackground(Color.lightGray);
        deleteButton = createButton("Delete", 80, 40);
        deleteButton.setBackground(Color.lightGray);

        //Add buttons
        returnPanel.add(newButton);
        returnPanel.add(copyButton);
        returnPanel.add(loadButton);
        returnPanel.add(renameButton);
        returnPanel.add(deleteButton);

        //Return constructed panel
        return returnPanel;
    }

    private JButton createButton(String name, int w, int h)
    {
        JButton button = new JButton(name);
        Dimension d = new Dimension(w, h);
        button.setPreferredSize(d);
        button.setMinimumSize(d);
        button.setMaximumSize(d);
        return button;
    }



}
