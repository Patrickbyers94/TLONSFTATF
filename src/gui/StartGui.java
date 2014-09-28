package gui;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import networking2.Server;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class StartGui {
	private JFrame frame;
	private JButton button, button1;
	//private StartCanvas Screen;
	private JComponent Screen;
	private BufferedImage image,op1,op1S,op2,op2S,op3,op3S;
	public int selected;

	public StartGui() {
		setupGui2();
	}

	/**
	 * create a new frame and fill it with the elements of the game
	 */
	public void setupGui() {
		//setup a frame
		frame = new JFrame("The Legend of Nelda - Search for something like the anti-Triforce but not the anti-Triforce");
		frame.setName("The Legend of Nelda");
		frame.setLocationRelativeTo(null);
		frame.setSize(400, 400);
		frame.setResizable(false);

		try {
			//read in the images of the options
			image = ImageIO.read(new File("assets/nelda_heading.png"));
			op1 = ImageIO.read(new File("assets/Start_Options/Start_Server_Unselected.jpg"));
			op1S = ImageIO.read(new File("assets/Start_Options/Start_Server_Selected.jpg"));
			op2 = ImageIO.read(new File("assets/Start_Options/Start_Client_Unselected.jpg"));
			op2S = ImageIO.read(new File("assets/Start_Options/Start_Client_Selected.jpg"));
			op3 = ImageIO.read(new File("assets/Start_Options/Exit_Unselected.jpg"));
			op3S = ImageIO.read(new File("assets/Start_Options/Exit_Selected.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//setup a panel to draw the options on
		Screen = new JComponent(){

			@Override
			public void paintComponent(Graphics g){
				g.drawImage(image, 0,0, null);
			}
		};

		//add a button for starting a server
		frame.add(Screen,BorderLayout.NORTH);
		Screen.repaint();
		button = new JButton("Create game");
		frame.add(button, BorderLayout.CENTER);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
			frame.dispose();
			ServerThread t = new ServerThread();
			t.start();
			}
		});

		// add a button for starting a client
		button1 = new JButton("Join a game");
		frame.add(button1, BorderLayout.SOUTH);
		button1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				frame.dispose();
				ClientThread t = new ClientThread();
				t.start();
			}
		});
		frame.setVisible(true);
	}

	public void setupGui2() {
		frame = new JFrame("The Legend of Nelda - Search for something like the anti-Triforce but not the anti-Triforce");
		frame.setName("The Legend of Nelda");
		frame.setLocationRelativeTo(null);
		frame.setSize(400, 400);
		frame.setResizable(false);
		Screen = new StartCanvas();
		frame.add(Screen);
		frame.setVisible(true);
		//Screen.draw(selected, Screen.getGraphics());
	}

	public static void main(String[] args) {
		StartGui g = new StartGui();
	}
}

//new thread for the server gui
class ServerThread extends Thread {
	public void run(){
		ServerGui ser = new ServerGui();
	}
}
//new thread for the player gui
class ClientThread extends Thread {
	public void run(){
		PlayerGui play = new PlayerGui();
	}
}
