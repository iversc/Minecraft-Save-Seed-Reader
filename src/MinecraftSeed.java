import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import javax.swing.*;

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
	private int validNames;
	private JComboBox combo;
	
	private final Double version = 1.2;
	
	public MinecraftSeed()
	{
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
		panel.setLayout(new FlowLayout());
		
		combo = new JComboBox();
		combo.addActionListener(this);
		
		text = new JTextField();
		text.setColumns(20);
		
		setupData();
		
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
		
		panel.add(combo);
		panel.add(text);
		frame.getContentPane().add(panel);
		frame.pack();
		
		//Center on screen
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public static void main(String[] args) {

		new MinecraftSeed();
		
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
						
						try {
							fis = new FileInputStream(file);
							main = Tag.readFrom(fis);
							fis.close();
							
							combo.addItem((String)main.findTagByName("LevelName").getValue());
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
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
			return true;
		}
		
		return false;
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		//If a combobox item was selected
		if(arg0.getSource() instanceof JComboBox) {
			JComboBox cb = (JComboBox)arg0.getSource();
			int val = cb.getSelectedIndex();
			
			if(val > -1)
			{
				try {
					FileInputStream fis = new FileInputStream(new File(filePaths[val]));
					main = Tag.readFrom(fis);
					fis.close();
					
					text.setText(main.findTagByName("RandomSeed").getValue().toString());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else { //One of the menu items was chosen
			String cmd = arg0.getActionCommand();
			
			//"How to use" menu item
			if(cmd.equals("use"))
			{
				String message = "Choose the world's name from the dropdown list." +
								 "\nThe world's seed will show up in the textbox." + 
								 "\n\nIf the list is empty, click on Help > Select MC Save Folder," +
								 "\nand find and open the saves folder in the file chooser.";
				
				JOptionPane.showMessageDialog(panel, message, "How to Use", JOptionPane.INFORMATION_MESSAGE);
			}
			
			//"About" menu item
			if(cmd.equals("about"))
			{
				String message = "Written by Chris Iverson. \n\n" +
								 "Source code for this program available on request.\n" +
								 "Send e-mail to cj.no.one@gmail.com.";
				
				JOptionPane.showMessageDialog(panel, message, "About Minecraft Save Seed Reader v" + version.toString(),
						JOptionPane.INFORMATION_MESSAGE);
			}
			
			//"Select MC Save Folder" menu item
			if(cmd.equals("folder"))
			{
				//Only load the data if the user selected a folder
				if(chooseSaveFolder()) setupData();
			}
		}
		
	}

}
