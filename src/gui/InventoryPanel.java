package gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import game.Item;
import game.Job;
import game.Player;
import graphics.Camera;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class InventoryPanel extends JPanel implements MouseListener, MouseMotionListener {
	private Player player;
	int tileSize = 32;
	int selectedSquare = -1;
	int selected = 0;
	Point point;
	BufferedImage backgroundImage;

	public InventoryPanel(Player p){
		player  = p;
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.setSize(180, 180);
		try {
			backgroundImage = ImageIO.read(new File("assets/invTexture.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//this.setPreferredSize(new Dimension(300, 300));
	}


	@Override
	protected void paintComponent(Graphics g){
		Graphics2D g2d = (Graphics2D )g;
	//	g2d.scale(4, 4);
		super.paintComponent(g2d);
		g2d.setColor(new Color(123, 123, 123 ));
		g2d.drawImage(backgroundImage, 0, 0, null);
		g2d.setColor(Color.black);
		g2d.drawRect(0, 0, 300, 300);


		for(int i = 0; i < player.getInventory().size(); i++){
			//System.out.println(i);
			player.getInventory().get(i).draw((Graphics2D)g2d, (i*32)%160, (i/5)*32, 32,32 );
		}
		for(int i =0 ; i< 5; i++){
			for(int j = 0; j< 5; j++){
				g2d.drawRect((i*32), j*32, 32, 32);
			}
		}
		if(selectedSquare>=0 && selectedSquare < 25){
			BufferedImage b = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
			Graphics2D gOfB = (Graphics2D) b.getGraphics();
			AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f);
			gOfB.setComposite(ac);
			gOfB.fillRect(0, 0, 32, 32);
			gOfB.dispose();
			g2d.drawImage(b, (selectedSquare%5 * tileSize), (selectedSquare/5) * tileSize , null);
		}

		if(selected < 25&& selected>=0&&point != null&&selected<player.getInventory().size()){
			

			g2d.setColor(Color.white);
			String text = player.getInventory().get(selected).toString();

			g2d.fillRect(5, 200, 180, 100);


			g2d.setColor(Color.black);
			g2d.drawRect(5, 200, 180, 100);
			//g2d.drawRect(offsetX, point.y-15, (text.length() * 9), 30 );
			g2d.setFont(new Font("Helvetica", Font.BOLD,16));

			g2d.drawString(text, 7, 250);



		}
	}





	public static void main(String[] args) {
		JFrame frame = new JFrame("The Legend of Nelda - Search for something like the anti-Triforce but not the anti-Triforce");
		frame.setName("The Legend of Nelda");
		frame.setLocationRelativeTo(null);
		frame.setSize(165, 160+25);
		frame.setResizable(false);
		Player p = new Player("BOB", Job.Soldier());
		frame.add(new InventoryPanel(new Player("BOB", Job.Soldier())));
		frame.setVisible(true);

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		int x = e.getX()/tileSize;
		int y = e.getY()/tileSize;
		if(x < player.getInventory().size() &&  player.getInventory().get(x)!=null){
			selectedSquare = x+(y*5);

		}else{
			selectedSquare = -1;
		}
		this.repaint();
		//TODO remove the below print
		System.out.println(x + " " + y + " " + selectedSquare);
	}


	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}


	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}


	@Override
	public void mouseEntered(MouseEvent e) {
		int x = e.getX() / tileSize;
		int y = e.getY() / tileSize;

	}


	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}


	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub

	}


	@Override
	public void mouseMoved(MouseEvent e) {
		point = e.getPoint();
		int x = e.getX() / tileSize;
		int y = e.getY() / tileSize;
		Boolean diff = false;
		if (selected != x+(y*5)){diff = true;}
		selected = x+(5*y);
		if(diff){this.repaint();}
	}

}
