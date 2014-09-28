package gui;

import game.Game;
import game.Item;
import game.Key;
import game.Player;
import game.World;
import graphics.GameCanvas;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

public class WorldListener implements KeyListener {
	private Game game;
	private GameCanvas world;
	private Player p;
	private Movement movement;
	private PlayerGui playerGui;

	public WorldListener(Game g, GameCanvas w, Player p, Movement m,
			PlayerGui playerGui) {
		game = g;
		world = w;
		this.p = p;
		movement = m;
		this.playerGui = playerGui;
	}

	@Override
	/**
	 * register keys pressed
	 */
	public void keyPressed(KeyEvent arg0) {
		int keyCode = arg0.getKeyCode();
		switch (keyCode) {
		// left
		case KeyEvent.VK_LEFT:
			//if(!movement.isMoving()){
			movement.setLeft(true);
			//}
			break;
		// right
		case KeyEvent.VK_RIGHT:
			//if(!movement.isMoving()){
			movement.setRight(true);
			//}
			break;
		case KeyEvent.VK_DOWN:
			//if(!movement.isMoving()){
			movement.setDown(true);
			//}
			break;
		case KeyEvent.VK_UP:
			//if(!movement.isMoving()){
			movement.setUp(true);
			//}
			break;
		case KeyEvent.VK_N:
			world.toggleNightTime();
			break;
		case KeyEvent.VK_D:
			// world.toggleDebugMode();
			break;
		case KeyEvent.VK_M:
			world.toggleMiniMap();
			break;
		case KeyEvent.VK_A:
			playerGui.attack();
			break;
		case KeyEvent.VK_F:
			world.toggleFlashLight();
			break;
		case KeyEvent.VK_X:
			System.out.println(p.getWorld() + " " + p.getPosition());
			break;
		case KeyEvent.VK_Z:
			System.out.println(p.getName() + " is a(n) " + p.getJob());
		case KeyEvent.VK_E:
			game.inspect(p);
			playerGui.inventoryUpdate();
			break;
		case KeyEvent.VK_K:
			List<Item> items = p.getInventory();
			for (Item i : items) {
				if (i instanceof Key) {
					game.useItem(p, (Key) i);
					break;
				}
			}
			break;
		default:
			break;
		}
	}

	@Override
	/**
	 * register keys released
	 */
	public void keyReleased(KeyEvent arg0) {
		int keyCode = arg0.getKeyCode();
		switch (keyCode) {
		// left
		case KeyEvent.VK_LEFT:
			movement.setLeft(false);
			// movePlayer();
			break;
		// right
		case KeyEvent.VK_RIGHT:
			movement.setRight(false);
			// movePlayer();
			break;
		case KeyEvent.VK_DOWN:
			movement.setDown(false);
			// movePlayer();
			break;
		case KeyEvent.VK_UP:
			movement.setUp(false);
			// movePlayer();
			break;
		default:
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

}
