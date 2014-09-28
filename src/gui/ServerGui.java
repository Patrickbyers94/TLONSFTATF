package gui;

import graphics.GameCanvas;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.PrintStream;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import networking2.Server;

public class ServerGui {
	private JFrame frame;
	private JMenuBar menuBar;
	private JMenuItem menuItem;
	private JMenu menu;
	private JTextArea textArea;

	private SaveDialog sd;

	private Server server;

	/**
	 * create a new server gui
	 */
	public ServerGui(){
		setupGui();
		server = new Server(textArea);//create the server gui giving it the text area to print to
	}

	/**
	 * create a frame and give it the server options and a place to print servers output
	 */
	private void setupGui(){
		//setup the frame
		frame = new JFrame("Legend of Nelda - Server Manager");
		frame.setSize(400, 400);
		frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent evt){
					server.closeServer();
					System.exit(0);
			}
		});

		//create a new textArea for the server to print to
		textArea = new JTextArea();
		textArea.setSize(400, 400);
		textArea.setEditable(false);
		JScrollPane scroll = new JScrollPane(textArea);
		frame.add(scroll);
		textArea.append("HI I AM A SERVER \n");
		//create the menu bar
		menuBar = new JMenuBar();
		//create the menu
		menu = new JMenu("File");

		//add the save option
		menuItem = new JMenuItem("Save");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				sd = new SaveDialog(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						server.saveGame(sd.getFilename());
						sd.dispose();
					}});}});
		menu.add(menuItem);

		//add the load option
		menuItem = new JMenuItem("Load");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				File load;
				JFileChooser fc = new JFileChooser("holla");
				if (fc.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
				} else {
					load = fc.getSelectedFile();
					String prefix = load.getName().substring(0,
							load.getName().length() - 12);
					textArea.append("loading with prefix: "+prefix+"\n");
					server.loadGame(prefix);
				}
			}
		});
		menu.add(menuItem);

		//menu option for exit
		menuItem = new JMenuItem("Exit");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
					server.closeServer();
				System.exit(0);
			}
		});
		menu.add(menuItem);

		menuBar.add(menu);
		frame.setJMenuBar(menuBar);
		frame.setVisible(true);

		ServerOutputStream out = new ServerOutputStream(textArea);
		System.setOut (new PrintStream (out));
	}

	public static void main(String[] args) {
		ServerGui s = new ServerGui();
	}
}


