package game;

import graphics.Camera;
import graphics.GameCanvas;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.jdom2.Element;

/** A tree. */
public class Tree implements DecorativeObject {

	private BufferedImage image;
	private Position position;

	public Tree() {
		try {
			image = ImageIO.read(new File("assets/sprites/tree.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getInspected(Player p) {
		System.out.println("A beautiful tree.");
		return "A beautiful tree.";
	}

	@Override
	public void takeHit(int attack) {
		System.out.println("The tree seems unfazed.");
	}

	@Override
	public Position getPosition() {
		return position;
	}

	@Override
	public void setPosition(Position p) {
		this.position = p;
	}

	public String toString() {
		return "tree";
	}

	@Override
	public void draw(Graphics2D g, Camera cam) {
		int x = this.getPosition().getCol() * GameCanvas.TILESIZE;
		int y = this.getPosition().getRow() * GameCanvas.TILESIZE;
		if (cam.isOnscreen(x, y)) {
			Point pn = cam.getPosOnScreen(x, y);
			g.drawImage(image, pn.x, pn.y-32, null);
		}

	}

	@Override
	public Element toElement() {
		return new Element("tree").addContent(position.toElement());
	}

	public Tree fromElement(Element e){
		return new Tree();
	}

	@Override
	public void draw(Graphics2D g, int x, int y, int width,int height) {
		// TODO Auto-generated method stub

	}

}
