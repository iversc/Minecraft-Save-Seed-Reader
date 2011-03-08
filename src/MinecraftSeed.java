import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import javax.swing.*;

public class MinecraftSeed implements ActionListener {

	/**
	 * @param args
	 */
	
	private Tag main;
	private Tag seed;
	JFrame frame;
	JPanel panel;
	private JTextField text;
	String path;
	
	private final Double version = 1.1;
	
	public MinecraftSeed()
	{
		frame = new JFrame("Minecraft Save Seed Reader v" + version.toString());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		panel = new JPanel();
		panel.setLayout(new FlowLayout());
		
		JButton button = new JButton("Open...");
		button.addActionListener(this);
		button.setActionCommand("open");
		
		text = new JTextField();
		text.setColumns(20);		
		
		JMenuBar menuBar = new JMenuBar();
		JMenu helpMenu = new JMenu("Help");
		
		JMenuItem howToUse = new JMenuItem("How to use");
		howToUse.setActionCommand("use");
		howToUse.addActionListener(this);
		helpMenu.add(howToUse);
		
		JMenuItem about = new JMenuItem("About");
		about.setActionCommand("about");
		about.addActionListener(this);
		helpMenu.add(about);
		
		menuBar.add(helpMenu);
		frame.setJMenuBar(menuBar);
		
		panel.add(button);
		panel.add(text);
		frame.getContentPane().add(panel);
		frame.pack();
		
		//Center on screen
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		path = new String();
	}
	
	public static void main(String[] args) {

		new MinecraftSeed();
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		JFileChooser fc = new JFileChooser();
		String cmd = arg0.getActionCommand();
		
		//Open button
		if(cmd.equals("open"))
		{
			String fileName = "level.dat";
			//Windows
			if(System.getProperty("os.name").toLowerCase().contains("windows"))
			{
				path = System.getenv("APPDATA") + "\\.minecraft\\saves\\";
				fileName = "\\level.dat";
			}
			
			//Mac
			if(System.getProperty("os.name").toLowerCase().contains("mac os x"))
			{
				path = System.getProperty("user.home") + "/Library/Application Support/minecraft/saves/";
				fileName = "/level.dat";
			}
			
			//Linux(hopefully)
			if(System.getProperty("os.name").toLowerCase().contains("linux"))
			{
				path = System.getProperty("user.home") + "/.minecraft/saves/";
				fileName = "/level.dat";
			}
			
			fc.setCurrentDirectory(new File(path));
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int ret = fc.showDialog(panel, "Open World Folder");
			if(ret == JFileChooser.APPROVE_OPTION) {
				File file = new File(fc.getSelectedFile().getPath() + fileName);
				
				if(!file.getName().equalsIgnoreCase("level.dat")) {
					JOptionPane.showMessageDialog(panel, "Error: Please select the level.dat file");
				} else {
					try {
						FileInputStream fis = new FileInputStream(file);
						main = Tag.readFrom(fis);
						seed = main.findTagByName("RandomSeed");
						text.setText(seed.getValue().toString());
						fis.close();
					} catch (FileNotFoundException e) {
						JOptionPane.showMessageDialog(panel, "Unable to find level.dat file. Did you open a World folder?",
								"Error", JOptionPane.ERROR_MESSAGE);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
		} // ActionCommand(open)
		
		//"How to use" menu item
		if(cmd.equals("use"))
		{
			String message = "1. Press 'Open' button\n" +
							 "2. Select a Minecraft World folder\n" +
							 "3. Press 'Open World Folder'\n" +
							 "\nThe world's seed will show up in the textbox.";
			
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
	}

}
