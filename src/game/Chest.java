package game;

import graphics.Camera;
import graphics.GameCanvas;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import org.jdom2.Element;

/** A chest. It can hold a single item. */
public class Chest implements Container {

	private Item item;
	protected Position position;
	private boolean locked = true;

	BufferedImage image;
	BufferedImage openChest;

	public Chest() {
		super();
		try {

			image = ImageIO.read(new File("assets/sprites/chest.png"));
			openChest = ImageIO.read(new File("assets/sprites/openchest.png"));
		} catch (IOException e) {
			System.out
					.println("error reading in image files in the spriteFactory Class");
			e.printStackTrace();
		}
	}

	@Override
	public String getInspected(Player p) {
		if (locked)
			System.out.println("A chest... It might contain something.");
		else {
			if (item != null) {
				System.out.println(p.getName() + "obtained " + item);
				p.getInventory().add(item);
				this.item = null;

			} else {
				System.out.println("It was empty...");
			}
		}
		return null;
	}

	/** Puts an item into the chest. */
	@Override
	public void add(Item i) {
		if (item == null)
			item = i;
	}

	/** Removes the item in the chest, if any. */
	@Override
	public Item remove() {
		Item i = item;
		item = null;
		return i;
	}

	@Override
	public void takeHit(int attack) {
		Toolkit.getDefaultToolkit().beep();
		System.out.println("Attacking a chest?");
	}

	/** Unlocks the chest. */
	public void unlock() {
		locked = false;
	}

	@Override
	public Position getPosition() {
		return this.position;
	}

	public void setPosition(Position position) {
		this.position = position;

	}

	public String toString() {
		return "Chest";
	}

	/**
	 * Create a JDOM Element that represents this chest
	 */
	public Element toElement() {
		Element me = new Element("chest");
		me.addContent(new Element("x").setText(Integer.toString(position
				.getCol())));
		me.addContent(new Element("y").setText(Integer.toString(position
				.getRow())));
		me.addContent(new Element("locked").setText(Boolean.toString(locked)));
		if (item != null) {
			me.addContent(new Element("contents").addContent(item.toElement()));
		} else {
			me.addContent(new Element("contents"));
		}
		return me;
	}

	/**
	 * Create a chest from a JDOM Element
	 *
	 * @param e
	 * @return
	 */
	public static Chest fromElement(Element e) {
		Chest chest = new Chest();
		Element contents = e.getChild("contents");
		if (contents != null) {
			List list = contents.getChildren();
			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					Element elem = (Element) list.get(i);
					if (elem.getName().equals("potion")) {
						chest.item = Potion.fromElement(elem);
					} else if (elem.getName().equals("revive")) {
						chest.item = Revive.fromElement(elem);
					} else if (elem.getName().equals("weapon")) {
						chest.item = Weapon.fromElement(elem);
					} else if (elem.getName().equals("key")) {
						chest.item = Key.fromElement(elem);
					} else if (elem.getName().equals("torch")) {
						chest.item = Torch.fromElement(elem);
					}
				}
			}
		}
		chest.locked = Boolean.parseBoolean(e.getChild("locked").getText());
		return chest;
	}

	@Override
	public void draw(Graphics2D g, Camera cam) {
		int x = this.getPosition().getCol() * GameCanvas.TILESIZE;
		int y = this.getPosition().getRow() * GameCanvas.TILESIZE;
		if (cam.isOnscreen(x, y)) {
			Point pn = cam.getPosOnScreen(x, y);
			if (locked)
				g.drawImage(image, pn.x, pn.y, null);
			else {
				g.drawImage(openChest, pn.x, pn.y, null);
			}
		}
	}

	@Override
	public void draw(Graphics2D g, int x, int y,int width, int height) {
		// TODO Auto-generated method stub

	}

}
