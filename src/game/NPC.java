package game;

import graphics.Camera;

import java.awt.Graphics2D;
import java.util.List;
import java.util.Random;

import org.jdom2.Element;

/**
 * A non-playable character. It has a predetermined list of dialogs trigerred by
 * talking to it and a predetermined set of positions it can move within.
 */

public class NPC implements InteractiveObject {

	String name;
	private List<String> dialogs;
	private Position position;
	private List<Position> possiblePositions;
	private Random random = new Random();

	public NPC(String name, Position position, List<String> dialogs) {
		this.name = name;
		this.position = position;
		this.dialogs = dialogs;
	}

	public void move() {
		while (true) {
			int number = random.nextInt(possiblePositions.size());
			Position target = possiblePositions.get(number);
			// TODO: collision detection
			if (true) {
				position = target;
				break;
			}
		}
	}

	/**
	 * get the name of the NPC
	 *
	 * @return String: the name String
	 */
	public String getName() {
		return this.name;
	}

	@Override
	public String getInspected(Player p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void takeHit(int attack) {
		// TODO Auto-generated method stub

	}

	@Override
	public Position getPosition() {
		return this.position;
	}

	@Override
	public void setPosition(Position p) {
		this.position = p;
	}

	@Override
	public void draw(Graphics2D g, Camera cam) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update() {
		move();
	}

	@Override
	public Element toElement() {
		// TODO Auto-generated method stub
		/*
		 * Not sure this class needs to be stored in xml Upon loading the game
		 * they could just be put into their starting position
		 */
		return null;
	}

	/**
	 * creates a string that stores the information about the NPC used by the
	 * server to update clients when a game is loaded
	 *
	 * @return
	 */
	public String npcString() {
		return name + ";" + position.getCol() + ";" + position.getRow();
	}

	@Override
	public void draw(Graphics2D g, int x, int y, int width, int height) {
		// TODO Auto-generated method stub

	}

}

class NPCThread extends Thread {
	private NPC npc;

	public NPCThread(NPC npc) {
		this.npc = npc;
	}

	public void run() {
		while (true) {

			// TODO: need to check if a player is interacting with npc
			npc.move();
			try {
				this.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
