package graphics;

import java.awt.Graphics2D;
import java.awt.Image;

public abstract class Sprite {

	private Image img;
	private int x;
	private int y;

	public void drawImage(Graphics2D dest, int camX, int camY, int frame) {
	}

	public int getX() {
		return 0;
	}

	public int getY() {
		return 0;
	}
	
}

