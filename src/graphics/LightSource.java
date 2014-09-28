package graphics;

import java.awt.Color;

public class LightSource {

	private float brightness;
	private int x;
	private int y;
	private Color color;
	

	/**
	 * represents a source of light and the brightness. Can be used to have multiple Light sources on screen
	 * at once
	 * @param brightness
	 * @param x
	 * @param y
	 * @param col
	 */
	public LightSource(float brightness, int x, int y, Color col) {
		this.brightness = brightness;
		this.x = x;
		this.y = y;
		this.color = col;
	}

	public float getBrightness() {
		return brightness;
	}


	public void setBrightness(float brightness) {
		this.brightness = brightness;
	}


	public int getX() {
		return x;
	}


	public void setX(int x) {
		this.x = x;
	}


	public int getY() {
		return y;
	}


	public void setY(int y) {
		this.y = y;
	}


	public Color getColor() {
		return color;
	}


	public void setColor(Color color) {
		this.color = color;
	}
	
	
	
}
