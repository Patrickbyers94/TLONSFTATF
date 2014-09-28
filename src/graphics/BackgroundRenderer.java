package graphics;

import game.Ice;
import game.Position;
import game.Sokoban;
import game.World;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.List;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class BackgroundRenderer {


	private boolean debugMode = false;
	private World world;
	private Position[][] grid;

	TileSheet tsheet = new TileSheet("assets/sprites/sheet2.png", 32, 32);
	

	BackgroundRenderer(World world) {
		this.world = world;
		this.grid = world.getGrid();
	}

	/**
	 * draws the background to the Graphics object it is passe
	 * @param g2d
	 * @param camX
	 * @param camY
	 * @param screenWidth
	 * @param screenHeight
	 */
	public void drawWorld(Graphics2D g2d, int camX, int camY, int screenWidth, int screenHeight, Camera cam) {
		// work out where to start and end drawing from
		int xStartTile = camX / 32;
		int yStartTile = camY / 32;
		int xEndTile = (camX + screenWidth) / 32;
		int yEndTile = (camY + screenHeight) / 32;

		g2d.setColor(Color.white);

		for (int x = xStartTile; x < xEndTile; x++) {
			for (int y = yStartTile; y < yEndTile; y++) {
				int xCoord = (x * 32) - camX;
				int yCoord = (y * 32) - camY;
				if (grid[x][y] instanceof Ice) {
					g2d.drawImage(tsheet.getIce(), xCoord, yCoord, null);
				} else {
					g2d.drawImage(tsheet.get(grid[x][y].getType()), xCoord,
							yCoord, null);
				}
				// draws tile coords to the screen in pixels
				if (debugMode) {
					g2d.drawRect(x * 32 - camX, y * 32 - camY, x * 32 + 32
							- camX, y * 32 + 32 - camY);
					String sx = (x * 32 - camX) + " ";
					String sy = (y * 32 - camY) + " ";
					g2d.drawString(sx, x * 32 - camX, y * 32 - camY + 16);
					g2d.drawString(sy, x * 32 - camX, y * 32 - camY + 28);
				}
			}
		}
		// super hacky hack of hackness
		if (world instanceof Sokoban) {

			List<Position> goals = ((Sokoban) world).getGoals();

			for (Position g : goals) {
				Point p = cam.getPosOnScreen(g.getCol() * 32, g.getRow() * 32);
				g2d.setColor(Color.red);
				g2d.fillRect(p.x, p.y, 32, 32);
			}

		}

	}

	public void setWorld(World w){
		this.grid = w.getGrid();
		this.world = w;
	}



	/**
	 * toggles debug mode on and off. If debug mode is on it draws a grid to the screen with their pixel values
	 */
	public void toggleDebugMode() {
		this.debugMode = !debugMode;
	}
}
