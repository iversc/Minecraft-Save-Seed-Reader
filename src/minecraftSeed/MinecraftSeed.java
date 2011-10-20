package minecraftSeed;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import javax.swing.*;
import net.miginfocom.swing.MigLayout;

class DirFilter implements FilenameFilter {

	@Override
	public boolean accept(File dir, String name) {
		return new File(dir.getPath() + File.separator + name).isDirectory();
	}
	
}

public class MinecraftSeed implements ActionListener {
	private Tag main;
	private JFrame frame;
	private JPanel panel;
	private JTextField text;
	private String savePath;
	private String[] filePaths;
	private String lastFolder;
	private int validNames;
	private JComboBox<String> combo;
	private JCheckBox cbCreative;
	private boolean creativeEnabled;
	private boolean hardcoreEnabled;
	private JButton btnSave;
	private String selectedFilePath;
	
	private final Double version = 1.6;
	private JCheckBox cbHardcore;
	
	public MinecraftSeed()
	{
		//Set the system look and feel
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		
		//Windows
		if(System.getProperty("os.name").toLowerCase().contains("windows"))
		{
			savePath = System.getenv("APPDATA") + "\\.minecraft\\saves\\";
		}
		
		//Mac
		if(System.getProperty("os.name").toLowerCase().contains("mac os x"))
		{
			savePath = System.getProperty("user.home") + "/Library/Application Support/minecraft/saves/";
		}
		
		//Linux(hopefully)
		if(System.getProperty("os.name").toLowerCase().contains("linux"))
		{
			savePath = System.getProperty("user.home") + "/.minecraft/saves/";
		}
		
		frame = new JFrame("Minecraft Save Seed Reader v" + version.toString());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		panel = new JPanel();
		
		combo = new JComboBox<String>();
		combo.addActionListener(this);
		
		text = new JTextField();
		text.setColumns(20);
		text.setEditable(false);
		
		
		JMenuBar menuBar = new JMenuBar();
		JMenu helpMenu = new JMenu("Help");
		
		JMenuItem howToUse = new JMenuItem("How to use");
		howToUse.setActionCommand("use");
		howToUse.addActionListener(this);
		helpMenu.add(howToUse);
		
		JMenuItem newFolder = new JMenuItem("Select MC Save Folder");
		newFolder.setActionCommand("folder");
		newFolder.addActionListener(this);
		helpMenu.add(newFolder);
		
		helpMenu.addSeparator();
		
		JMenuItem about = new JMenuItem("About");
		about.setActionCommand("about");
		about.addActionListener(this);
		helpMenu.add(about);
		
		menuBar.add(helpMenu);
		frame.setJMenuBar(menuBar);
		panel.setLayout(new MigLayout("", "[28px][166px][95px]", "[23px][][]"));
		
		panel.add(combo, "cell 0 0,alignx left,aligny center");
		panel.add(text, "cell 1 0 2 1,growx");
		frame.getContentPane().add(panel);
		
		cbCreative = new JCheckBox("Creative Mode");
		cbCreative.setEnabled(false);
		cbCreative.setActionCommand("creativetoggle");
		cbCreative.addActionListener(this);
		panel.add(cbCreative, "flowx,cell 1 1,alignx left,aligny top");
		
		cbHardcore = new JCheckBox("Hardcore Mode");
		cbHardcore.setEnabled(false);
		cbHardcore.setActionCommand("hardcoretoggle");
		cbHardcore.addActionListener(this);
		panel.add(cbHardcore, "cell 1 2");
		
		btnSave = new JButton("Save Changes");
		btnSave.setEnabled(false);
		btnSave.setActionCommand("save");
		btnSave.addActionListener(this);
		panel.add(btnSave, "cell 2 2");
		
		//Try to load the save data
		setupData();
		
		frame.pack();
		
		//Center on screen
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public static void main(String[] args) {

		try {
			new MinecraftSeed();
			
		} catch(Exception e) {  //If anything unexpected goes wrong in the main program,
			     				//write it to an error log
			try {
				FileOutputStream fos = new FileOutputStream("MinecraftSeed.error.log");
				e.printStackTrace(new PrintStream(fos));
				fos.close();
				
				JOptionPane.showMessageDialog(null, "An error occured." +
						"\nCheck the MinecraftSeed.error.log file for more information.", 
						"ERROR", JOptionPane.ERROR_MESSAGE);
				
			} catch (FileNotFoundException e1) {  //If the error log messes up,
												  //fall back to console
				e.printStackTrace();
			} catch (IOException e2) {
				e.printStackTrace();
			}
			e.printStackTrace();
		}
		
	}

	private void setupData()
	{
		boolean loop = false;
		
		//Check to reload data, used when checking folders with no valid save files
		do {
			loop = false;
			
			File file = new File(savePath);
			
			//If the save folder selected actually exists
			if(file.exists())
			{
				//Grab a list of sub-directories in the saves directory
				String[] tempPaths = file.list(new DirFilter());
				filePaths = new String[tempPaths.length];
		
				validNames = 0;
				FileInputStream fis;
		
				combo.removeAllItems();
				for(String s : tempPaths)
				{
					//Check each sub-directory for a 'level.dat' file
					//If one is found, then assume it's a valid MC save folder
					file = new File(savePath + File.separator + s + File.separator + "level.dat");
					if(file.exists()) {
						filePaths[validNames] = file.getPath();
						
						lastFolder = file.getParent();
						
						try {
							fis = new FileInputStream(file);
							main = Tag.readFrom(fis);
							fis.close();
							
							//If the level.dat doesn't have a 'LevelName' entry,
							//go by the folder's name
							Tag name = main.findTagByName("LevelName");
							if(name == null) {
								combo.addItem(file.getParentFile().getName());
							} else {
								combo.addItem((String)name.getValue());								
							}
							
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (Exception e)
						{
							//Unknown problem with loading save file.  Corrupted?
							JOptionPane.showMessageDialog(frame, "There was a problem reading the save file in " + lastFolder
									+ ".\n It will not be loaded in this program.", "Error", JOptionPane.ERROR_MESSAGE);
							continue;
						}
						
						validNames++;
					}
				}
				
				if(validNames > 0) 
				{
					combo.setSelectedIndex(0);
				} else { //No valid save files were found
					JOptionPane.showMessageDialog(panel, "No valid save files were detected in current folder." +
							"\n\nPlease choose a new one.", "Error", JOptionPane.ERROR_MESSAGE);
					
					if(chooseSaveFolder()) loop = true;
					//This won't loop if the user cancels out of the folder selection dialog.
				}
			} else {  //Save folder doesn't exist
				JOptionPane.showMessageDialog(panel, "Save folder doesn't exist.\n\nPlease choose a new one.",
						"Error", JOptionPane.ERROR_MESSAGE);
				
				if(chooseSaveFolder()) loop = true;
				//Again, won't loop if the user cancels out of the folder selection dialog.
			}
		} while (loop);
	}
	
	private boolean chooseSaveFolder()
	{
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		int ret = fc.showDialog(panel, "Open Minecraft Saves Folder");
		if(ret == JFileChooser.APPROVE_OPTION) {
			savePath = fc.getSelectedFile().getPath();
			
			//Three tests to see if the folder chosen was a world folder, not the saves folder
			//Test one: check for existence of 'level.dat'
			if((new File(savePath + File.separator + "level.dat")).exists())
			{
				//'level.dat' exists, now check for 'session.lock'
				if((new File(savePath + File.separator + "session.lock")).exists())
				{
					//'session.lock' exists, now the final check: region subfolder.
					if((new File(savePath + File.separator + "region")).exists())
					{
						//At this point, we've almost indisputably gotten a world folder instead
						//of the saves folder.  Let's set 'savePath' to the parent directory.
						savePath = new File(savePath).getParent();
					}
				}
			}
			return true;
		}
		
		return false;
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		//If a combobox item was selected
		if(arg0.getSource() == combo)
		{
			int val = combo.getSelectedIndex();
			
			//Make sure something was actually chosen
			if(val > -1)
			{
				//Disable the save button
				btnSave.setEnabled(false);
				
				try {
					selectedFilePath = filePaths[val];
					FileInputStream fis = new FileInputStream(new File(selectedFilePath));
					main = Tag.readFrom(fis);
					fis.close();
					
					text.setText(main.findTagByName("RandomSeed").getValue().toString());
					
					//Only if the save file has a 'GameType' entry will
					//we allow the user to switch between Creative on and off.
					Tag creative = main.findTagByName("GameType");
					if(creative==null)
					{
						cbCreative.setEnabled(false);
						cbCreative.setSelected(false);
					}
					else
					{
						cbCreative.setEnabled(true);
						
						//Check the box if creative is enabled
						creativeEnabled = ((Integer)creative.getValue() == 1);
						cbCreative.setSelected(creativeEnabled);
					}
					
					//Same as above, only with the "hardcore" toggle.
					Tag hardcore = main.findTagByName("hardcore");
					if(hardcore==null)
					{
						cbHardcore.setEnabled(false);
						cbHardcore.setSelected(false);
					}
					else
					{
						cbHardcore.setEnabled(true);
						
						//Check the box if hardcore is enabled
						hardcoreEnabled = ((Byte)hardcore.getValue() == 1);
						cbHardcore.setSelected(hardcoreEnabled);
					}
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else { //One of the menu items was chosen, or the checkbox was checked,
			     //or "save" was pressed
			String cmd = arg0.getActionCommand();
			
			//"How to use" menu item
			if(cmd.equals("use"))
			{
				String message = "Choose the world's name from the dropdown list." +
								 "\nThe world's seed will show up in the textbox." + 
								 "\n\nIf the list is empty, click on Help > Select MC Save Folder," +
								 "\nand find and open the saves folder in the file chooser.\n" +
								 "\nIf the save is a version 1.8+ save, you can switch the world" +
								 "\nbetween Creative and Survival by checking the checkbox, then" +
								 "\nhitting Save.";
				
				JOptionPane.showMessageDialog(panel, message, "How to Use", JOptionPane.INFORMATION_MESSAGE);
			}
			
			//"About" menu item
			if(cmd.equals("about"))
			{
				String message = "Written by Chris Iverson. \n\n" +
								 "Source code available here:\n" +
								 "https://github.com/thedarkfreak/Minecraft-Save-Seed-Reader\n" +
								 "\nProblems? \nReport them either to the github site, in the forum topic,\n" +
								 "or send me an e-mail at cj.no.one@gmail.com.\n" +
								 "\nEnjoy!";
				
				JOptionPane.showMessageDialog(panel, message, "About Minecraft Save Seed Reader v" + version.toString(),
						JOptionPane.INFORMATION_MESSAGE);
			}
			
			//"Select MC Save Folder" menu item
			if(cmd.equals("folder"))
			{
				//Only load the data if the user selected a folder
				if(chooseSaveFolder()) setupData();
			}
			
			//Creative mode toggled
			if(cmd.equals("creativetoggle"))
			{
				//File manipulation here
				creativeEnabled = !creativeEnabled;
								
				Tag creative = main.findTagByName("GameType");
				creative.setValue(creativeEnabled ? 1 : 0);
				
				//Enable the "Save" button
				btnSave.setEnabled(true);
			}
			
			//Hardcore mode toggled
			if(cmd.equals("hardcoretoggle"))
			{
				hardcoreEnabled = !hardcoreEnabled;
				
				Tag hardcore = main.findTagByName("hardcore");
				hardcore.setValue((byte)(hardcoreEnabled? 1 : 0));
				
				//Enable the "save" button
				btnSave.setEnabled(true);
			}
			
			//Save button pressed
			if(cmd.equals("save"))
			{
				int chosen = JOptionPane.showConfirmDialog(frame, "Are you sure you want to overwrite your save file?", 
						"Overwrite", JOptionPane.YES_NO_OPTION);
				
				if(chosen == JOptionPane.YES_OPTION);
				{
					try {
						FileOutputStream fos = new FileOutputStream(new File(selectedFilePath));
						main.writeTo(fos);
						fos.close();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						JOptionPane.showMessageDialog(null, "Problem saving file: File Not Found", "File Not Found", JOptionPane.ERROR_MESSAGE);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						JOptionPane.showMessageDialog(null, "Problem saving file: IO Error", "IO Error", JOptionPane.ERROR_MESSAGE);
					}
					
					JOptionPane.showMessageDialog(frame, "File saved!");
					
					btnSave.setEnabled(false);
				}
			}
		} // else
		
	} //actionPerformed

}
