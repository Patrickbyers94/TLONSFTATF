package graphics;
import java.awt.Point;


public class Camera {

	public int x;
	public int y;
	private int screenWidth;
	private int screenHeight;




	/**
	 * This represents the area of the map that the player is looking at
	 * @param
	 */
	public Camera(int x, int y, int screenWidth, int screenHeight){
		this.x = x;
		this.y = y;
		this.screenHeight = screenHeight;
		this.screenWidth = screenWidth;
	}

	
	/**
	 * given two x and y coordinates in the game world, returns true if they are on screen.
	 * Note: the values it takes should be the position[][] index * the tileSize(32)
	 * @param dx
	 * @param dy
	 * @return
	 */

	public boolean isOnscreen(int dx, int dy){
		if(dx<x || dy < y || dx > x +screenWidth || dy > y + screenHeight ){
			return false;
		}
		return true;
	}
	/**
	 * 
	 * takes an objects position ( x and y values) and returns the position on screen.
	 * Note: the values it takes should be the position[][] index * the tileSize(32)
	 * @param x
	 * @param y
	 * @return
	 */
	public Point getPosOnScreen(int x, int y){
		int px = x- this.x;
		int py = y - this.y;
		if (px < 0) {
			px = 0;
		}
		if (py < 0) {
			py = 0;
		}
		
		return new Point(px, py);
		
	}



}
