package graphictestclasses;

import graphics.GameCanvas;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class Controller implements KeyListener {

	private boolean right;
	private boolean left;
	private boolean down;
	private boolean up;
	private boolean p;
	//GameState game;
	GameCanvas canvas;

	public Controller(GameCanvas c){
		this.canvas = c;
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		int keyCode = arg0.getKeyCode();
		System.out.println(keyCode + " " + KeyEvent.VK_PLUS);
		switch(keyCode){

			// left
			case KeyEvent.VK_LEFT:
				left = true;
				pressedLeft();
				break;
			// right
			case KeyEvent.VK_RIGHT:
				right = true;
				pressedRight();
				break;
			case KeyEvent.VK_DOWN:
				down = true;
				break;
			case KeyEvent.VK_UP:
				up = true;
				break;
			case KeyEvent.VK_M:
				canvas.toggleMiniMap();

				break;
			case KeyEvent.VK_N:
				canvas.toggleNightTime();
				break;
			case KeyEvent.VK_D:
				canvas.toggleDebugMode();
				break;
			case 61: // should be plus sign
				canvas.increaseLight();
				break;
			case KeyEvent.VK_MINUS:
				canvas.decreaseLight();
				break;
			case KeyEvent.VK_F:
				canvas.toggleFog();
				break;
			case KeyEvent.VK_A:
				canvas.attack();
				break;
			default:
				break;
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		int keyCode = arg0.getKeyCode();
		switch(keyCode){
			// left
			case KeyEvent.VK_LEFT:
				left = false;
				break;
			// right
			case KeyEvent.VK_RIGHT:
				right = false;
				break;
			case KeyEvent.VK_DOWN:
				down = false;
				break;
			case KeyEvent.VK_UP:
				up = false;
				break;
			default:
				break;
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	public boolean isMoving(){
		return left || right || up || down;
	}


	public void pressedLeft(){
		left = true;
	//	game.moveLeft();
	}

	public void pressedRight(){
		right = true;
	}

	public boolean left(){
		return this.left;
	}

	public boolean right(){
		return this.right;
	}

	public boolean up(){
		return this.up;
	}

	public boolean down(){
		return this.down;
	}

}
