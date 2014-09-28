package graphics;

import game.Game;
import game.GameObject;
import game.GameObjectComparator;
import game.Player;
import game.Position;
import game.Time;
import game.World;
import graphictestclasses.Controller;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class GameCanvas extends JPanel {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private BufferedImage backbuffer;
	private Graphics2D g2d;
	private int screenWidth = 800;
	private int screenHeight = 576;
	private Point centre; // centre of screen
	private Thread gameloop;
	List<LightSource> lightsources;

	public static int TILESIZE = 32;

	// private List<Sprite> onscreenSprites;
	// private List<Player> onlinePlayers;

	// To Measure FPS
	private long now;
	private int framesCount = 0;
	private int framesCountAvg = 0;
	private long framesTimer = 0;
	private int currentFrame = 0;
	private int frameDelay = 10;

	private boolean drawMiniMap = false;
	private boolean nightTime = false;
	private boolean onDrugs = false;
	private boolean flashLight = false;

	private boolean fog = false;

	// private TileSheet tsheet;

	private Player player;
	private float lightlevel = .8f;

	private Controller control; // TODO test thing
	private MiniMap miniMap;
	private Time time;
	private PlayerSprite sprite; // the main

	private Game game;
	private World currentWorld;

	private Camera viewPort;
	private BackgroundRenderer bg;
	private List<GameObject> gameObjects;
	private SpriteFactory sf;
	private BufferedImage fogImg;

	private boolean printLocations;

	/**
	 * This is the main class that does the rendering of the game to the screen.
	 *
	 * @param game
	 *            - current Game
	 * @param player
	 *            - player
	 */
	public GameCanvas(Game game, Player player) {
		this.game = game;
		this.player = player;
		this.currentWorld = player.getWorld();
		bg = new BackgroundRenderer(game.getWorld());
		init();
		// tsheet = new TileSheet("assets/sprites/sheet2.png", 32, 32); //TODO
		// delete this its just for testing
		gameObjects = game.getWorld().getAllGameObjects();
		sf = new SpriteFactory();

		try {
			fogImg = ImageIO.read(new File("assets/fog.png"));
			Graphics2D g = (Graphics2D) fogImg.getGraphics();
			AlphaComposite ac = AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, .2f);
			g.setComposite(ac);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * over written paint method.
	 */
	public void paint(Graphics g) {
		if (backbuffer != null) {
			g.drawImage(backbuffer, 0, 0, this);
		}
	}

	/**
	 * Initializes the Canvas
	 */
	private void init() {
		setDoubleBuffered(true);

		int x = player.getPosition().getCol() * TILESIZE - screenWidth / 2;
		int y = player.getPosition().getRow() * TILESIZE - screenHeight / 2;

		// onscreenSprites = new LinkedList<Sprite>();

		// for(Player pp : game.getOnlinePlayers()){
		// Sprite s = new PlayerSprite(pp.getName(), pp, 0, 0);
		// }

		viewPort = new Camera(x, y, screenWidth, screenHeight);
		centre = new Point(x + (screenWidth - screenWidth / 2), y
				+ (screenHeight - screenHeight / 2));

		// set up image to draw to
		backbuffer = new BufferedImage(screenWidth, screenHeight,
				BufferedImage.TYPE_INT_ARGB);
		g2d = backbuffer.createGraphics();

		// set up mini map
		miniMap = new MiniMap(game.getWorld(), viewPort, 0,
				game.getOnlinePlayers());
		loadSprite();
		// light sources
		lightsources = new LinkedList<LightSource>();
		lightsources.add(new LightSource(.5f, 10, 10, new Color(0, 0, 0)));
	}

	/**
	 * if debug mode is on draws the tile outlines on the screen
	 */
	public void toggleDebugMode() {
		bg.toggleDebugMode();
	}

	/**
	 * main render function. This method gets called every tick of the update
	 * method.
	 */
	public void render() {
		// updates camera
		int px = player.getPosition().getCol() * TILESIZE;
		int py = player.getPosition().getRow() * TILESIZE;
		this.updateCam(px, py);
		this.sprite.setPos(px, py);

		// g2d.setBackground(new Color(0, 0, 0, 0));
		g2d.clearRect(0, 0, screenWidth, screenHeight);
		g2d = (Graphics2D) backbuffer.getGraphics();

		// DRAw BACKGROUND

		// We need to check if the world has changed. There are a few things that need to be changed
		if(!player.getWorld().getName().equals(currentWorld.getName())){
			this.handleChangeWorld();
		}

		bg.drawWorld(g2d, viewPort.x, viewPort.y, screenWidth, screenHeight, viewPort);

	//	drawSprites();
		drawGameObjects();

		drawSpecialEffects();

		drawMiniMap();
		drawAdditionalThings();

		drawFrameCount();

		repaint();
		revalidate();
	}

	/**
	 * if the player has changed world, this method updates the references to the world and the objects in that world.
	 */
	private void handleChangeWorld(){

		this.currentWorld = player.getWorld();
		if(!currentWorld.getName().equals("overworld") && !currentWorld.getName().equals("treetest")
				&& !currentWorld.getName().equals("shop")){
			this.nightTime=true;
		}else{

			this.nightTime = false;
		}
		bg = new BackgroundRenderer(currentWorld);
		this.miniMap = new MiniMap(currentWorld, viewPort, currentFrame, this.game.getOnlinePlayers());

		//gameObjects = player.get
		gameObjects = player.getWorld().getAllGameObjects();
	}


	/**
	 * draws the frame count
	 */
	private void drawFrameCount() {
		now = System.currentTimeMillis();

		framesCount++;

		if (framesCount / 4 > frameDelay) {
			// System.out.println(framesCount /3 );
			currentFrame += 1;
		}
		if (currentFrame > 2) {
			currentFrame = 0;
		}
		if (now - framesTimer > 1000) {
			framesTimer = now;
			framesCountAvg = framesCount;
			framesCount = 0;
		}
		time = game.getTime();
		int hour = time.getHour();
		int minutes = time.getMinute();
		int seconds = time.getSecond();
		g2d.setColor(Color.yellow);
		g2d.drawString(( "TIME " + hour + " : "  +minutes + ": " + seconds       ), screenWidth - 100, 30);
	}

	/**
	 * checks if any special effects are on and draws them to the screen.
	 *  Relies on the game thread to make these decisions
	 */
	private void drawSpecialEffects() {
		// TODO might need to change screenPos method
		if (nightTime) {
			SpecialEffects.nightTimeLight(g2d, screenWidth, screenHeight,
					lightlevel, getScreenPos(sprite), getLightSources());
		}
		if (onDrugs) {
			SpecialEffects.onDrugsMode(g2d, screenWidth, screenHeight);
		}
		if (flashLight) {
			SpecialEffects.nightTimeWithFlashLight(g2d, screenWidth,
					screenHeight, getScreenPos(sprite), sprite.getDirection());
		}
		if (fog) {
			g2d.drawImage(fogImg, null, 0, 0);
		}
	}

	private List<Point> getLightSources(){
		List<Point> points = new ArrayList<Point>();
		for(GameObject o : gameObjects){
			if(o instanceof Player){
				Player p = (Player)o;
				if(p!=this.player && inSameWorld(player, p)){
					int px = p.getPosition().getCol() * 32;
					int py = p.getPosition().getRow() * 32;

					if(viewPort.isOnscreen(px, py)){
						points.add(this.getScreenPos(px, py));
					}
				}
			}
		}
		return points;

	}

	/**
	 * updates the mini map and draws it
	 */
	private void drawMiniMap() {
		if (this.drawMiniMap) {
			miniMap.draw(g2d, 0, 0, sprite.getX(), sprite.getY());
		}
	}

	/**
	 *
	 * draws all the sprites on screen
	 */
	private synchronized void drawSprites() {
		// draw Sprite
		// TODO test that we don't need this method
		List<Player> onlinePlayers = Collections.synchronizedList(game.getOnlinePlayers());
		// draw other players
		for (Player p : onlinePlayers) {
			if(p!=this.player && inSameWorld(player, p)){
				int px = p.getPosition().getCol() * TILESIZE;
				int py = p.getPosition().getRow() * TILESIZE;

				if(printLocations){
					System.out.println("player : " + p.getName() + "world : " + p.getWorld()  );
					if(p!=player){
						System.out.println(player.getWorld().getName().equals(p.getWorld().getName()));
					}
				}
				if (viewPort.isOnscreen(px, py)) {
					PlayerSprite s = new PlayerSprite(p.getName(), p, 0, 0);
					Point pn = getScreenPos(px, py);
					if (p != this.player)  {
						//System.out.println("Other player");
					//	System.out.println(p.getPosition().getCol());
						s.drawOtherImage(g2d, pn.x, pn.y, p.getOrientation() - 1, p);
					}
				}
			}
			// TODO if player is in same world

		}
		// draw this player
		int frame = (player.getPosition().getCol() + player.getPosition().getRow()) % 4;

		sprite.drawImage(g2d, viewPort.x, viewPort.y, frame);
	}
	/**
	 * draws all objects that are onscreen in the game
	 */
	private void drawGameObjects() {

		//gameObjects = Collections.synchronizedCollection(gameObjects);

		Collections.sort(gameObjects, new GameObjectComparator());

		for (GameObject o : gameObjects) {
			int bx = o.getPosition().getCol() * TILESIZE;
			int by = o.getPosition().getRow() * TILESIZE;
			if (viewPort.isOnscreen(bx, by)) {
				//draw player sprites
				if(o instanceof Player){
					if(o!=this.player && inSameWorld(player, (Player) o)){
						// draw other player
						o.draw(g2d, viewPort);
					}else{
						// draw this player based on screen position
						int frame = (player.getPosition().getCol() + player.getPosition().getRow()) % 4;
						sprite.drawImage(g2d, viewPort.x, viewPort.y, frame);
					}
				}else{
					o.draw(g2d, viewPort);
				}
			}
		}
	}


	private boolean inSameWorld(Player  p1, Player p2){
		return p1.getWorld().getName().equals(p2.getWorld().getName());
	}


	/**
	 * given a sprite returns a point representing where that item would be
	 * onscreen Note does not check it object is offscreen so it will return a
	 * point offscreen if this is the case
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	private Point getScreenPos(Sprite sprite) {
		int px = sprite.getX() - viewPort.x;
		int py = sprite.getY() - viewPort.y;

		if (px < 0) {
			px = 0;
		}
		if (py < 0) {
			py = 0;
		}
		return new Point(px, py);
	}

	public Game getGame(){
		return this.game;
	}

	public void attack(){
		this.game.attack(player);
	}

	/**
	 * given two x y coordinates returns a point representing where that item
	 * would be onscreen Note does not check it object is offscreen so it will
	 * return a point offscreen if this is the case
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	private Point getScreenPos(int x, int y) {
		int px = x - viewPort.x;
		int py = y - viewPort.y;

		if (px < 0) {
			px = 0;
		}
		if (py < 0) {
			py = 0;
		}
		return new Point(px, py);
	}

	/**
	 * given a position object returns a point representing where that item
	 * would be onscreen Note does not check it object is offscreen so it will
	 * return a point offscreen if this is the case
	 *
	 * @Position p
	 * @return
	 */
	private Point getScreenPos(Position p) {
		int px = (p.getCol() * GameCanvas.TILESIZE) - viewPort.x;
		int py = (p.getRow() * GameCanvas.TILESIZE) - viewPort.y;

		if (px < 0) {
			px = 0;
		}
		if (py < 0) {
			py = 0;
		}
		return new Point(px, py);
	}


	/**
	 *
	 * increases the light level. Only visible if night time mode is on
	 */
	public void increaseLight() {
		float newlevel = lightlevel + 0.1f;
		if (newlevel < 1.0f) {
			lightlevel = newlevel;
		} else {
			lightlevel = 1.0f;
		}
	}

	/**
	 * decreases the light level. Only visible if night time mode is on
	 */
	public void decreaseLight() {
		float newlevel = lightlevel - 0.1f;
		if (newlevel > 0) {
			lightlevel = newlevel;
		} else {
			lightlevel = 0.0f;
		}

	}

	/**
	 * draw extra non essential stuff such as frame rate
	 *
	 */
	private void drawAdditionalThings() {

		// draw FPS
		g2d.setColor(Color.yellow);
		g2d.drawString("FPS: " + framesCountAvg, screenWidth - 100, 10);

		// draws the sprites position
		// g2d.drawString("Sprite:  " + currentFrame, screenWidth - 100, 30);
	}

	/**
	 *
	 */
	public void checkForInput() {
		// TODO just a method for testing the scrolling
		if (control != null && control.isMoving()) {
			if (control.left()) {
				sprite.moveLeft();
				game.movePlayer(player, 4);
			}
			if (control.right()) {
				sprite.moveRight();
				game.movePlayer(player, 2);
			}
			if (control.down()) {
				sprite.moveDown();
				game.movePlayer(player, 3);
			}
			if (control.up()) {
				sprite.moveUp();
				game.movePlayer(player, 1);
			}
			int px = player.getPosition().getCol() * TILESIZE;
			int py = player.getPosition().getRow() * TILESIZE;
			// toggles the animation of sprite
			updateCam(px, py);
			sprite.setPos(px, py);

			sprite.setMoving(true);
		} else {
			sprite.setMoving(false);
		}
	}

	/**
	 * updates the view of the Screen based on the player's position
	 *
	 * @param x
	 * @param y
	 */
	private void updateCam(int x, int y) {
		// this is how much we are moving by
		int dvX = x + 16 - screenWidth / 2;
		int dvY = y + 16 - screenHeight / 2;
		// check we are not out of bounds at left and to
		if (dvX < 0) {
			dvX = 0;
		}
		if (dvY < 0) {
			dvY = 16;
		}

		int worldWidth = player.getWorld().getGrid()[0].length;
		int worldHeight = player.getWorld().getGrid().length;
		// now check the bounds for the right and the bottom
		// int edgeX = (fakeGame.map.getNumCols()-1) *32 -screenWidth; // old
		// code
		int edgeX = ((worldWidth) * TILESIZE - screenWidth);
		int edgeY = ((worldHeight)) * TILESIZE - screenHeight;

		if (dvX > edgeX) {
			dvX = edgeX;
		}
		if (dvY > edgeY) {
			dvY = edgeY;
		}
		// now set the camera to the new position
		viewPort.x = dvX;
		viewPort.y = dvY + 16;
	}

	/**
	 * returns the width of the screen
	 *
	 * @return
	 */
	public int getScreenWidth() {
		return screenWidth;
	}

	/**
	 * sets the width of the screen
	 *
	 * @param screenWidth
	 */
	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	/**
	 * returns the height of the screen
	 *
	 * @return
	 */
	public int getScreenHeight() {
		return screenHeight;
	}

	/**
	 * sets the screenHeight
	 *
	 * @param screenHeight
	 */
	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}

	/**
	 * toggles the miniMap on and off
	 */
	public void toggleMiniMap() {
		this.drawMiniMap = !this.drawMiniMap;
	}

	/**
	 * toggles NightTime mode
	 */
	public void toggleNightTime() {
		this.nightTime = !nightTime;
	}


	public void toggleFlashLight(){
		this.flashLight = !flashLight;
	}


	/**
	 *
	 */
	public void toggleFog() {
		this.fog = !this.fog;
	}


	/**
	 * returns if it is nightTime or not
	 *
	 * @return
	 */
	public boolean isNightTime() {
		return this.nightTime;
	}

	/**
	 * loads a temporary Sprite for testing puposes
	 */
	private void loadSprite() {
		sprite = new PlayerSprite(player.getName(), player, centre.x - 16,
				centre.y - 16);
	}

	/**
	 * Test method for frame TODO delete
	 *
	 * @param c
	 */
	public void setController(Controller c) {
		this.control = c;
	}

	/**
	 * Sets this current player
	 *
	 * @param p
	 */
	public void setPlayer(Player p) {
		player = p;
	}

}
