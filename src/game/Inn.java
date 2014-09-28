package game;

import java.io.File;

/**
 * A room where players can heal themselves for a sum of money and are not
 * allowed to attack.
 */

public class Inn extends World {

	private Position goal;

	public Inn(File file) {
		super(file);
	}

	/**
	 * If the player isn't at max health, deduct a sum of money and heal him.
	 */
	public void movePlayer(Player p, int direction) {
		super.movePlayer(p, direction);
		if (p.getPosition().equals(goal)) {
			if (p.getHealth() == p.getMaxHealth())
				return;
			p.setMoney(Math.max(0, p.getMoney() - 100));
			p.setHealth(p.getMaxHealth());
		}
	}

}
