package game;

import graphics.Camera;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.jdom2.Element;

/** A torch. It is used during night-time/in dark worlds to help the user see. */

public class Torch implements PurchasableItem {
	Image image;
	private int cost = 10;

	public Torch() {
		try {
			image = ImageIO.read(new File("assets/sprites/torch.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getInspected(Player p) {
		System.out.println("A torch... Could come in handy at night.");
		return "A torch... Could come in handy at night.";
	}

	/** Activates light-mode */
	@Override
	public boolean execute(Player p) {
		p.activateLightMode();
		return true;
	}

	@Override
	public void getPickedUp(Player p) {
		System.out.println(p.getName() + " picked up a torch!");
	}

	@Override
	public void takeHit(int attack) {
		System.out.println("Attacking a torch?");

	}

	@Override
	public Position getPosition() {
		return null;
	}

	public int getCost() {
		return cost;
	}

	@Override
	public void setPosition(Position p) {
		// TODO Auto-generated method stub

	}

	public String toString() {
		return "Torch";
	}

	@Override
	public void draw(Graphics2D g, Camera cam) {
		// TODO Auto-generated method stub

	}

	@Override
	public Element toElement() {
		Element me = new Element("torch");
		return me;
	}

	public static Torch fromElement(Element e) {
		if (!e.getText().equals("torch")) {
			return null;
		}
		return new Torch();
	}

	@Override
	public void draw(Graphics2D g, int x, int y, int width, int height) {
		image = image.getScaledInstance(width, height, Image.SCALE_FAST);
		BufferedImage buffered = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		buffered.getGraphics().drawImage(image, 0, 0, null);
		g.drawImage(image, x, y, null);
		buffered.getGraphics().dispose();
	}

	@Override
	public String getType() {
		return "Torch";
	}

}
