package game;

import graphics.Camera;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.jdom2.Element;

/**
 * A weapon. It increases the attack of the player who wields it by a
 * predetermined amount.
 */

public class Weapon implements PurchasableItem {

	protected String name; // Can set this back to private if you need to
	private int attack;
	private int cost;

	Image image;

	private Weapon() {
	}

	public Weapon(String name, int attack) {
		this.name = name;
		this.attack = attack;
		this.cost = attack * 10;

		try {
			//image = ImageIO.read(new File("assets/sprites/"
				//	+ name.toLowerCase() + ".png"));
			image = ImageIO.read(new File("assets/sprites/sword.png"));

		} catch (IOException e) {
			try {
				image = ImageIO.read(new File("assets/sprites/sword.png"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}

	public int getAttack() {
		return this.attack;
	}

	@Override
	public boolean execute(Player p) {
		System.out.println(p.getName() + " equipped " + name + "!");
		return true;
	}

	public void getPickedUp(Player p) {
		System.out.println(p.getName() + " picked up " + name + "!");
	}

	public int getCost() {
		return cost;
	}

	@Override
	public String getInspected(Player p) {
		return null;
	}

	@Override
	public void takeHit(int attack) {
		// TODO Auto-generated method stub

	}

	@Override
	public Position getPosition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPosition(Position p) {
		// TODO Auto-generated method stub

	}

	@Override
	public void draw(Graphics2D g, Camera cam) {
		// TODO Auto-generated method stub

	}

	@Override
	public Element toElement() {
		Element me = new Element("weapon");
		me.addContent(new Element("name").setText(name));
		me.addContent(new Element("attack").setText(Integer.toString(attack)));
		return me;
	}

	/**
	 * Create a Weapon object from an XML Element
	 *
	 * @param e
	 *            The Element
	 * @return A Weapon with those stats
	 */
	public static Weapon fromElement(Element e) {
		Weapon w = new Weapon();
		w.name = e.getChild("name").getText();
		w.attack = Integer.parseInt(e.getChild("attack").getText());
		return w;
	}

	@Override
	public String toString() {
		return name + "(attack: " + attack + ")";
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
		return "Weapon";
	}

}
