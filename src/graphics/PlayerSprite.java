package graphics;
import game.Player;

import java.awt.Color;
import java.awt.Graphics2D;


public class PlayerSprite extends Sprite implements Animation {

	private int x;
	private int y;
	SpriteSheet imgs;
	int speed =5;
	private int direction=0;
	private boolean moving;
	private Player player;

	/**
	 *
	 * @param name
	 * @param x
	 * @param y
	 */
	public PlayerSprite(String name, Player player, int x, int y){
		this.player = player;
		this.x = (int)player.getX();
		this.y = (int)player.getY();

		imgs = new SpriteSheet("assets/sprites/dudeguy.png", 32, 32);

	}

	public int getDirection(){
		return player.getOrientation()-1;
	}


	public boolean moving(){
		return moving;
	}

	public void setMoving(boolean move){
		this.moving = move;
	}

	public void setPos(int x, int y){
		this.x = x;
		this.y= y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	/**
	 * draws the sprites Image relative to the camera 0x and 0y. ie the top left pixel
	 * @param dest
	 * @param camX
	 * @param camY
	 * @param frame
	 */
	public void drawImage(Graphics2D dest, int camX, int camY, int frame){

		
		// get positions and orientation
		int playerX = (x-camX);
		int playerY = (y-camY)-10;
		int direction = player.getOrientation()-1;// 1 = up 2 = right 3 = down 4 = left
		
		// draw shadow underneath
		dest.setPaint(SpecialEffects.getGradientPaint(x, y));
		dest.fillOval(playerX,playerY +22, 30, 16);
		
		
		
		
		// draws the sprite image here
		if(player.isMoving()){
			imgs.drawFrame(dest, playerX, playerY, 3, frame, direction);
		}else if(player.isDead()){
			//imgs.drawFrame(dest, destX, destY, cols, frame)
			imgs.drawDeadPlayer(dest, playerX, playerY);
			
		}else{
			imgs.drawFrame(dest, playerX, playerY, 3, 1, direction); // 1 is default not moving frame
		}
		drawHealth(dest, playerX, playerY, player);	
	}


	
	/**
	 * draws all players that are not this player
	 * @param dest
	 * @param x
	 * @param y
	 * @param direction
	 * @param p
	 */
	public void drawOtherImage(Graphics2D dest, int x, int y, int direction, Player p){

		dest.setPaint(SpecialEffects.getGradientPaint(x, y));
		dest.fillOval(x,y +12, 32, 16);
		direction = player.getOrientation()-1;// 1 = up 2 = right 3 = down 4 = left
		if(moving){

			imgs.drawFrame(dest, x, y-10, 3, 1, direction);
		}else if(player.isDead()){
			imgs.drawDeadPlayer(dest, x, y);		
		}
		else
		{
			imgs.drawFrame(dest, x, y-10, 3, 1, direction); // 1 is default not moving frame
		}
		drawHealth(dest, x, y, p);
	}

	
	
	private void drawHealth(Graphics2D dest, int x, int y, Player p) {
		if(p.getHealth() > p.getMaxHealth() * .7){
			dest.setColor(Color.GREEN);
		}else
		if(p.getHealth()> p.getMaxHealth() * .4){
			dest.setColor(new Color(255,140,0));
		}else{
			dest.setColor(Color.red);
		}

		int width = p.getHealth() / 2 ;
		dest.fillRect(x, y-10, width, 5);
	}
	
	
	/**
	 *  TODO probably have to get rid of these methods as they are more logic than Graphics
	 *   and only used for testing my class
	 */
	public void moveLeft() {

		if(direction!=1) {
			this.direction = 1;
		}
	}

	public void moveRight() {

		if(this.direction!=2)
			this.direction = 2;
	}

	public void moveUp() {

		if(this.direction!=3)
			this.direction = 3;
	}

	public void moveDown() {

		if(this.direction!=0)
			this.direction =0;
	}

}
