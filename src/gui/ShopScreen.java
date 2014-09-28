package gui;

import game.Item;
import game.Job;
import game.Player;
import game.PurchasableItem;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ShopScreen extends JPanel implements MouseListener, MouseMotionListener{
	private Player player;
	int tileSize = 60;
	int selectedSquare = -1;
	int selected = 0;
	Point point;
	JFrame parent;
	List<PurchasableItem> wares;

	public ShopScreen(List<PurchasableItem> i, Player p,final JFrame parent){
		player  = p;
		this.parent = parent;
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.setSize(300, 350);
		wares = i;
	}


	@Override
	protected void paintComponent(Graphics g){
		Graphics2D g2d = (Graphics2D )g;
	//	g2d.scale(4, 4);
		super.paintComponent(g2d);
		g2d.setColor(new Color(123, 123, 123 ));
		try {
			g2d.drawImage(ImageIO.read(new File("assets/invTexture.jpg")), 0, 0, null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		g2d.setColor(Color.red);
		g2d.drawRect(0, 0, 300, 300);
		for(int i = 0; i < wares.size(); i++){
			//System.out.println(i);
			wares.get(i).draw((Graphics2D)g2d, (i*60)%300, (i/5)*60,60,60 );
		}
		for(int i =0 ; i< 5; i++){
			for(int j = 0; j< 5; j++){
				g2d.drawRect(i*60, j*60, 60, 60);
			}
		}
		if(selectedSquare>=0){
			BufferedImage b = new BufferedImage(60, 60, BufferedImage.TYPE_INT_ARGB);
			Graphics2D gOfB = (Graphics2D) b.getGraphics();
			AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f);
			gOfB.setComposite(ac);
			gOfB.fillRect(0, 0, 60, 60);
			gOfB.dispose();
			g2d.drawImage(b, (selectedSquare%5)*tileSize, (selectedSquare/5)*tileSize, null);

		}
		if(selected < 25&& selected>=0&&point != null&&selected<wares.size()){
			g2d.setColor(Color.black);
			g2d.setFont(new Font("TimesRoman", Font.BOLD,16));
		g2d.drawString(wares.get(selected).toString(), 0, 316);
		g2d.setFont(new Font("TimesRoman", Font.PLAIN,12));
		g2d.drawString("Costs : "+Integer.toString(wares.get(selected).getCost()), 0, 330);
		}
		else if((selected >25 || selected<0) && selectedSquare<25 && selectedSquare>0){

		}


	}

	public void purchase(){
		if (selectedSquare != -1){
			//add to inventory
			//remove coin
		}
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

	@Override
	public void mouseClicked(MouseEvent e) {
		int x = e.getX()/tileSize;
		int y = e.getY()/tileSize;
		if(x < wares.size() &&  wares.get(x)!=null){
			selectedSquare = x+(y*5);

		}else{
			selectedSquare = -1;
		}
		this.repaint();
		//TODO remove the below print
		System.out.println(x + " " + y + " " + selectedSquare);
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("The Legend of Nelda - Search for something like the anti-Triforce but not the anti-Triforce");
		frame.setName("The Legend of Nelda");
		frame.setLocationRelativeTo(null);
		frame.setSize(300, 400);
		frame.setResizable(false);
		Player p = new Player("BOB", Job.Soldier());
		frame.add(new ShopScreen(null,new Player("BOB", Job.Soldier()),frame));
		frame.setVisible(true);

	}
}

