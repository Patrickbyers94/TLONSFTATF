package game;

import graphics.Camera;
import graphics.GameCanvas;
import graphics.SpecialEffects;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.jdom2.Element;

/** A box. Used for decoration and for sokoban */

public class Box extends MovableObject {
	BufferedImage image;

	public Box() {
		super();
		try {
			image = ImageIO.read(new File("assets/sprites/box.png"));
		} catch (IOException e) {
			System.out
					.println("error reading in image files in the spriteFactory Class");
			e.printStackTrace();
		}
	}

	public Box(Position p) {
		super();
		try {

			image = ImageIO.read(new File("assets/sprites/box.png"));
		} catch (IOException e) {
			System.out
					.println("error reading in image files in the spriteFactory Class");
			e.printStackTrace();
		}

		this.position = p;
		p.setGameObject(this);
	}

	@Override
	public String getInspected(Player p) {
		System.out.println("A box...");
		return "A box...";
	}

	@Override
	public void takeHit(int attack) {
		System.out.println("Attacking a box?");

	}

	@Override
	public String toString() {
		return ("box(" + position.getCol() + "," + position.getRow() + ")");
	}

	@Override
	public Element toElement() {
		Element me = new Element("box");
		me.addContent(new Element("room").setText(room.getName()));
		me.addContent(new Element("x").setText(Integer.toString(this.position
				.getCol())));
		me.addContent(new Element("y").setText(Integer.toString(this.position
				.getRow())));
		return me;
	}

	public static Box fromElement(Element e) {
		Box b = new Box();
		// b.room = TODO Need a method for getting the room that this object is
		// in. Maybe Game needs a Map<String,Room> to get rooms from their
		// names?
		int x = Integer.parseInt(e.getChild("x").getText());
		int y = Integer.parseInt(e.getChild("y").getText());
		b.position = new Position(x, y);
		return b;
	}

	public void draw(Graphics2D g, Camera cam) {
		int x = this.getPosition().getCol() * GameCanvas.TILESIZE;
		int y = this.getPosition().getRow() * GameCanvas.TILESIZE;
		if (cam.isOnscreen(x, y)) {
			// int px = x - cam.x;
			// int py = y - cam.y;
			//
			// if (px < 0) {
			// px = 0;
			// }
			// if (py < 0) {
			// py = 0;
			// }
			Point pn = cam.getPosOnScreen(x, y);

			g.setPaint(SpecialEffects.getGradientPaint(pn.x, pn.y));

			// draw shadow for triforce!
			// int[] xs = {pn.x+10, pn.x +20, pn.x+50 };
			// int[] ys = {pn.y+30 , pn.y+20, pn.y+20 };
			// Polygon p = new Polygon(xs, ys, 3);

			int[] xs = { pn.x, pn.x + 40, pn.x + 40, pn.x };
			int[] ys = { pn.y + 5, pn.y + 5, pn.y + 32, pn.y + 32 };
			Polygon p = new Polygon(xs, ys, 4);
			g.fillPolygon(p);

			g.drawImage(image, pn.x, pn.y, null);

			// System.out.println("ONSCREEN");
		}
	}

	@Override
	public void draw(Graphics2D g, int x, int y,int width, int height) {
		// TODO Auto-generated method stub

	}

}
