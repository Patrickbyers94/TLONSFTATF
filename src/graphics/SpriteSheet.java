package graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class SpriteSheet  {

	private Image image;
	private int tileWidth;  // might need better names for these
	private int tileHeight; //
	BufferedImage dead;


	/**
	 * filename is path of SpriteSheet file, tile Width and heights are each individual image
	 * of the spriteSheet
	 * @param filename
	 * @param tileWidth
	 * @param tileHeight
	 */
	public SpriteSheet(String filename, int tileWidth , int tileHeight){
		try {
			image = ImageIO.read(new File(filename));
			dead = ImageIO.read(new File("assets/sprites/deadguy.png"));
			this.tileWidth =tileWidth;
			this.tileHeight = tileHeight;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Couldn't find image!!!!");
			e.printStackTrace();
		}
	}

	/**
	 *
	 * @param dest
	 * @param destX
	 * @param destY
	 * @param cols
	 * @param frame
	 */
	public void drawFrame(Graphics2D dest, int destX, int destY, int cols, int frame){
		int frameX = (frame % cols) * tileWidth;
		int frameY = (frame / cols) * tileHeight;

		dest.drawImage(image, destX, destY, destX + tileWidth, destY + tileHeight,
				frameX, frameY, frameX + tileWidth, frameY + tileHeight, null );
	}
	/**
	 * draws frame with direction of sprite
	 * @param dest
	 * @param destX
	 * @param destY
	 * @param cols
	 * @param frame
	 * @param direction
	 */
	public void drawFrame(Graphics2D dest, int destX, int destY, int cols, int frame, int direction){
		int frameX = (frame % cols) * tileWidth;
		//int frameY = (frame / cols) * tileHeight;
		int frameY = direction * tileHeight;

		dest.drawImage(image, destX, destY, destX + tileWidth, destY + tileHeight,
				frameX, frameY, frameX + tileWidth, frameY + tileHeight, null );
	}
	
	
	
	public void drawDeadPlayer(Graphics2D dest, int x, int y){
		
		dest.drawImage(dead, x, y, null);	
		
	}




}
