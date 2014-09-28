package graphics;

import game.Player;
import game.Position;
import game.World;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.List;

public class MiniMap {

	BufferedImage img;
	private int imgW;
	private int imgH;
	World world;
	Camera view;
	Position[][] grid;
	List<Player> players;

	/**
	 * This class represents a MiniMap.
	 *
	 * @param map
	 * @param scale
	 */
	public MiniMap(World world, Camera view, int scale, List<Player> players){
		this.view = view;
		this.world = world;
		this.grid = world.getGrid();
		this.imgW = grid[0].length *2;
		this.imgH = grid.length *2;
		img = new BufferedImage(imgW, imgH,  BufferedImage.TYPE_INT_RGB);
		setImage();
		this.players = players;
	}


	/**
	 * creates a pixel representation of the game map.
	 */
	public void setImage() {
		if (img != null) {
			//System.out.println(img.getWidth(null));
			for (int x = 0; x < imgW; x++) {
				for (int y = 0; y < imgH; y++) {
					int tileX = x / 2;
					int tileY = y / 2;
					if(world.getPosition(tileX, tileY).getType()==1){
						img.setRGB(y, x, 12961477); // green
					}else if (world.getPosition((int) tileX, (int) tileY)
							.isPassable()) {
						img.setRGB(y, x, 7569720);
					} else {
						img.setRGB(y, x, 12961477); // gray
					}
				}
			}
		}
	}

	/**
	 * draws the Map to the Screen
	 * @param g2d
	 * @param x
	 * @param y
	 * @param spriteX
	 * @param spriteY
	 */
	public void draw(Graphics2D g2d, int x, int y, int spriteX, int spriteY){


		g2d.drawImage(img, x, y, null);
		g2d.setColor(Color.red);

		int sx = spriteX / 16;
		int sy = spriteY / 16;


		g2d.drawRect(x+sx, y+sy, 1, 1);

		// draw other players
		g2d.setColor(Color.pink);
		for(Player p : players){
			int px = p.getPosition().getCol() * GameCanvas.TILESIZE;
			int py = p.getPosition().getRow() * GameCanvas.TILESIZE;

			g2d.drawRect(x + ( px/16), y + (py / 16), 1, 1);
		}
	}


	/**
	 * changes the world that the mini map draws. This needs to be updated whenever player changes worlds
	 * @param w
	 */
	public void setWorld(World w){
		this.world = w;
	}


	/**
	 * returns the Image of the map
	 * @return
	 */
	public BufferedImage getImage(){
		return img;
	}




}
