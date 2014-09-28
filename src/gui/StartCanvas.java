package gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class StartCanvas extends JPanel {

	private BufferedImage image, op1, op1S, op2, op2S, op3, op3S;


	int selectedItem = -1;

	@SuppressWarnings("serial")
	public StartCanvas() {

		try {
			// read in the images of the options
			image = ImageIO.read(new File("assets/nelda_heading.png"));
			op1 = ImageIO.read(new File(
					"assets/Start_Options/Start_Server_Unselected.jpg"));
			op1S = ImageIO.read(new File(
					"assets/Start_Options/Start_Server_Selected.jpg"));
			op2 = ImageIO.read(new File(
					"assets/Start_Options/Start_Client_Unselected.jpg"));
			op2S = ImageIO.read(new File(
					"assets/Start_Options/Start_Client_Selected.jpg"));
			op3 = ImageIO.read(new File(
					"assets/Start_Options/Exit_Unselected.jpg"));
			op3S = ImageIO.read(new File(
					"assets/Start_Options/Exit_Selected.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/**
		 * SET UP BUTTON 1
		 *
		 */
		JButton b1 = new JButton(){
			@Override
			protected void paintComponent(Graphics g){
				super.paintComponent(g);
				if(selectedItem==0){
					g.drawImage(op1S, 0, 0, null);
				}else{
					g.drawImage(op1, 0, 0, null);
				}
			}
			@Override
			public Dimension getPreferredSize(){
				return new Dimension(op1.getWidth(), op1.getHeight());
			}

		};
		b1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
			//frame.dispose();
			ServerThread t = new ServerThread();
			t.start();
			}
		});
		b1.addMouseListener(new MouseAdapter(){
			public void mouseEntered(java.awt.event.MouseEvent evt) {
			selectedItem = 0;
			}
			public void mouseExited(java.awt.event.MouseEvent evt) {
				selectedItem = -1;
			}
		});
		this.add(b1);

		/**
		 * SET UP BUTTON 2
		 *
		 */
		JButton b2 = new JButton(){
			@Override
			protected void paintComponent(Graphics g){
				super.paintComponent(g);
				if(selectedItem==2){
					g.drawImage(op2S, 0, 0, null);
				}else{
					g.drawImage(op2, 0, 0, null);
				}
			}
			@Override
			public Dimension getPreferredSize(){
				return new Dimension(op2.getWidth(), op2.getHeight());
			}
		};
		b2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				ClientThread t = new ClientThread();
				t.start();
			}
		});
		b2.addMouseListener(new MouseAdapter(){
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				selectedItem = 2;
			}
			public void mouseExited(java.awt.event.MouseEvent evt) {
				selectedItem = -1;
			}
		});
		this.add(b2);

		/**
		 * SET UP BUTTON 3
		 *
		 */
		JButton b3 = new JButton(){
			@Override
			protected void paintComponent(Graphics g){
				super.paintComponent(g);
				if(selectedItem==3){
					g.drawImage(op3S, 0, 0, null);
				}else{
					g.drawImage(op3, 0, 0, null);
				}
			}

			@Override
			public Dimension getPreferredSize(){
				return new Dimension(op3.getWidth(), op3.getHeight());
			}
		};
		b3.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);

			}


		});
		b3.addMouseListener(new MouseAdapter(){
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				selectedItem = 3;
			}
			public void mouseExited(java.awt.event.MouseEvent evt) {
				selectedItem = -1;
			}
		});
		this.add(b3);


	}

	@Override
	protected void paintComponent(Graphics g){
		//redraw(selectedItem, g);
	}

	public void optionUp(){
		if(selectedItem > 0){
			selectedItem--;
		}
	}

	public void optionDown(){
		if(selectedItem < 2){
			selectedItem++;
		}
	}

//	public void redraw(int selected, Graphics g){
//		if(selected == 0){
//			g.drawImage(op1, 0, 100, null);
//			g.drawImage(op2, 0, 200, null);
//			g.drawImage(op3, 0, 300, null);
//		}
//		else if (selected == 1){
//			g.drawImage(op1, 0, 100, null);
//			g.drawImage(op2S, 0, 200, null);
//			g.drawImage(op3, 0, 300, null);
//		}
//		else if (selected == 2){
//			g.drawImage(op1, 0, 100, null);
//			g.drawImage(op2, 0, 200, null);
//			g.drawImage(op3S, 0, 300, null);
//		}
//		else { System.out.print("ERROR IN PAINT COMPONENT");}
//	}

	public static void main(String[] args) {

		JFrame frame = new JFrame();
		frame.add(new StartCanvas());
		frame.setLocationRelativeTo(null);
		frame.setSize(400, 400);
		frame.setResizable(false);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

}
