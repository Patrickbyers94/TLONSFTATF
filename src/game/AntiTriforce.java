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

/** An anti-triforce, whatever that is. */

public class AntiTriforce implements GameObject {

	BufferedImage image;

	public AntiTriforce() {
		try {
			image = ImageIO.read(new File("assets/sprites/antriforce.png"));

		} catch (IOException e) {
			System.out
					.println("error reading in image files in the Antitriforce Class");
			e.printStackTrace();
		}

	}

	@Override
	public String getInspected(Player p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void takeHit(int attack) {
		// TODO Auto-generated method stub

	}

	@Override
	public Position getPosition() {
		return null;
	}

	@Override
	public void setPosition(Position p) {
		// TODO Auto-generated method stub

	}

	public String toString() {
		return "AntiTriforce";
	}

	@Override
	public void draw(Graphics2D g, Camera cam) {
		int x = this.getPosition().getCol() * GameCanvas.TILESIZE;
		int y = this.getPosition().getRow() * GameCanvas.TILESIZE;
		if (cam.isOnscreen(x, y)) {

			Point pn = cam.getPosOnScreen(x, y);

			g.setPaint(SpecialEffects.getGradientPaint(pn.x, pn.y));

			// draw shadow for triforce!
			int[] xs = { pn.x + 10, pn.x + 20, pn.x + 50 };
			int[] ys = { pn.y + 30, pn.y + 20, pn.y + 20 };
			Polygon p = new Polygon(xs, ys, 3);

			g.fillPolygon(p);

			g.drawImage(image, pn.x, pn.y, null);

		}

	}

	@Override
	public Element toElement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void draw(Graphics2D g, int x, int y, int width, int height) {
		// TODO Auto-generated method stub

	}

}
