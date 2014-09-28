package game;

import graphics.Camera;
import graphics.GameCanvas;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.jdom2.Element;

/**
 * A key. Used for unlocking doors.
 */

public class Key implements Item {

	private Image image;

	public Key() {
		try {
			image = ImageIO.read(new File("assets/sprites/key.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Unlock the door at the position the player is facing, if there is one.
	 */
	@Override
	public boolean execute(Player p) {
		Position target = p.getWorld().getAdjacentPosition(p.getPosition(),
				p.getOrientation());
		if (!(target.isDoor() && ((Door) target.getGameObject()).isLocked()))
			return false;
		((Door) target.getGameObject()).unlock();
		return true;
	}

	@Override
	public void getPickedUp(Player p) {
		System.out.println(p.getName() + " picked up a key!");
	}

	@Override
	public String getInspected(Player p) {
		System.out.println("A key... It should unlock something.");
		return "A key... It should unlock something.";
	}

	@Override
	public void takeHit(int attack) {
		System.out.println("Attacking a key?");

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

	public String toString() {
		return "Key";
	}

	@Override
	public Element toElement() {
		return new Element("key");
	}

	public static Key fromElement(Element e) {
		return new Key();
	}

	@Override
	public void draw(Graphics2D g, Camera cam) {
		int x = this.getPosition().getCol() * GameCanvas.TILESIZE;
		int y = this.getPosition().getRow() * GameCanvas.TILESIZE;
		if (cam.isOnscreen(x, y)) {
			int px = x - cam.x;
			int py = y - cam.y;

			if (px < 0) {
				px = 0;
			}
			if (py < 0) {
				py = 0;
			}
			g.drawImage(image, px, py, null);
		}
	}

	@Override
	public void draw(Graphics2D g, int x, int y, int width, int height) {
		image = image.getScaledInstance(width, height, Image.SCALE_FAST);
		BufferedImage buffered = new BufferedImage(width, height,  BufferedImage.TYPE_INT_ARGB);
		buffered.getGraphics().drawImage(image, 0, 0 , null);
		g.drawImage(image, x, y, null);
		buffered.getGraphics().dispose();
	}

	@Override
	public String getType() {
		return "Key";
	}

}
