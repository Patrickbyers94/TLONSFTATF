package game;

import graphics.Camera;

import java.awt.Graphics2D;
import java.util.List;

/** A monster. It tries to attack any players nearby. If a player kills it he/she obtains some
 * money and possibly a drop object. */

import java.util.Random;

import org.jdom2.Element;

/**
 * A monster. It can attack players and be attacked by players. On being killed
 * the player obtains a sum of money.
 */
public class Monster implements InteractiveObject {

	private String name;
	private World room;
	private Position position;
	private int orientation;
	private int attack = 5;
	private int health = 100;
	private Random random = new Random();
	private GameObject drop;
	private int money = random.nextInt(50);

	public Monster() {

	}

	public Position getPosition() {
		return this.position;
	}

	public void setPosition(Position p) {
		this.position = p;
	}

	public void setRoom(World room) {
		this.room = room;
	}

	public int getOrientation() {
		return this.orientation;
	}

	public String getName() {
		return this.name;
	}

	public void takeTurn() {
		// if there is a player nearby, attack him
		for (int i = 1; i < 5; i++) {
			// System.out.println(position + " "+i);
			Position adjacent = room.getAdjacentPosition(position, i);
			if (adjacent.hasPlayer()) {
				attack((Player) adjacent.getGameObject());
				return;
			}
		}
		// otherwise move one square in a random direction
		int direction = random.nextInt(4) + 1;
		orientation = direction;
		Position newP = room.getAdjacentPosition(position, direction);
		if (newP == null || !newP.isPassable() || newP.hasPlayer()
				|| newP.hasGameObject() || newP.hasItem())
			return;
		position.setGameObject(null);
		newP.setGameObject(this);
		position = newP;
		// System.out.println("New position: " + newP);
	}

	public void attack(Player p) {
		p.takeHit(attack);
	}

	public void takeHit(int attack) {
		this.health -= attack;
		if (health <= 0)
			;
		// TODO: death
		System.out.println("Monster's health: " + health);
	}

	@Override
	public String getInspected(Player p) {
		p.takeHit(attack);
		System.out.println("Ouch!!");
		return null;
	}

	@Override
	public void draw(Graphics2D g, Camera cam) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update() {
		takeTurn();
	}

	@Override
	public Element toElement() {
		// TODO Auto-generated method stub
		/*
		 * Not sure we need to save monsters. Depends if we want to re-generate
		 * them on loading or if we do want to save their health, position etc.
		 */
		return null;
	}

	/**
	 * creates a string that stores the information about the Monster used by
	 * the server to update clients when a game is loaded
	 *
	 * @return
	 */
	public String monsterString() {
		return name + ";" + room + ";" + position.getCol() + ";"
				+ position.getRow() + ";" + orientation + ";" + attack + ";"
				+ health;
	}

	@Override
	public void draw(Graphics2D g, int x, int y, int width, int height) {
		// TODO Auto-generated method stub

	}

}

class MonsterThread extends Thread {

	private Monster monster;

	public MonsterThread(Monster monster) {
		this.monster = monster;
	}

	public void run() {
		while (true) {
			monster.takeTurn();
			try {
				this.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
