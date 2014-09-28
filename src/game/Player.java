package game;

import graphics.Camera;
import graphics.GameCanvas;
import graphics.PlayerSprite;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;

/**
 * A player. It can hold items and money. It can use and drop items, attack
 * other players and monsters and move within and between worlds.
 */

public class Player implements GameObject {

	private String name;
	private Job job;

	private int x;
	private int y; // need to delete these but some graphics classes use them
					// for some reason

	private World world;
	private Position position;
	private int orientation = 2;
	private List<Item> inventory = new ArrayList<Item>();
	private Weapon weapon;
	private int health;
	private int money;
	private BufferedImage image;
	PlayerSprite sprite;

	private boolean lightMode = false;
	private boolean isMoving;
	private boolean dead = false;

	private Player() {
	}

	public Player(String name, Job job) {
		// setup fields
		this.name = name;
		this.job = job;
		health = job.getHealth();
		money = 100;

		// initialise inventory
		inventory.add(new Potion(20));
		inventory.add(new Torch());

		inventory.add(new Key()); // for debugging, remove later
		inventory.add(new Key());

		if (job.getName().equals("Soldier")) {
			weapon = new Sword("Sword", 10);
		} else {
			weapon = new Bow("Bow", 10);
		}
		inventory.add(weapon);
		this.sprite = new PlayerSprite(name, this, 0, 0);
//		try {
//			image = ImageIO.read(new File("assets/sprites/dudeguy.png"));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}

	/** Pick up the item at the position the user is facing, if there is one */
	public void pickUp() {
		// get position where item should be
		Position target = world.getAdjacentPosition(position, orientation);
		// check if position has item
		if (target.hasItem()) {
			Item i = (Item) target.getGameObject();
			// add to inventory if there is space
			if (inventory.add(i)) {
				i.getPickedUp(this);
				target.setGameObject(null);
			}
		}
	}

	/** Use an item, if the player has it. */
	public void use(Item i) {
		System.out.println(name + " is using " + i);
		if (inventory.contains(i)) {
			if (i.execute(this))
				inventory.remove(i);
		}
	}

	/** Drop an item to the position in front of the player, if possible. */
	public void drop(Item i) {
		Position target = world.getAdjacentPosition(position, orientation);
		if (!target.hasGameObject() && target.isPassable()) {
			world.getAllGameObjects().add(i);
			inventory.remove(i);
			target.setGameObject(i);
			i.setPosition(target);
		}
	}

	/**
	 * Move in a given direction, if that position is passable and doesn't
	 * already have a gameObject.
	 */
	public void move(int direction) {
		orientation = direction;
		Position newP = world.getAdjacentPosition(position, direction);
		if (newP == null || !newP.isPassable() || newP.hasPlayer()
				|| newP.hasItem())
			return;
		if (newP.isDoor() && !((Door) newP.getGameObject()).isLocked()) {
			((Door) newP.getGameObject()).getDestination().acceptPlayer(this);
			return;
		}
		if (newP.hasMovableObject()) {
			world.moveGameObject((MovableObject) newP.getGameObject(),
					direction);
			// to move after pushing
			if (!newP.hasMovableObject())
				move(direction);
			return;
		} else if (newP.hasGameObject())
			return;

		switchPositions(newP);
	}

	private void switchPositions(Position newP) {
		position.setGameObject(null);
		position = newP;
		newP.setGameObject(this);

		// player gets a coin for every successful move
		money++;

	}

	/**
	 * Attack the player or monster at the position the player is facing, if
	 * there is one.
	 */
	public Position attack() {
		Position reference = position;
		Position target = reference;
		// look at the x = range positions in front of the player for something
		// to attack
		for (int i = 0; i < job.getRange(); i++) {
			target = world.getAdjacentPosition(reference, orientation);
			if (target.hasGameObject()) {
				target.getGameObject().takeHit(
						job.getAttack() + weapon.getAttack());
				break;
			}

			reference = target;
		}

		return target;
	}

	/**
	 * Get more information about the gameObject at the position the player is
	 * facing, if there is one.
	 */
	public void inspect() {
		Position target = world.getAdjacentPosition(position, orientation);
		//System.out.println(this + " thinks he is in the world: " + world);
		System.out.println(this + " inspected something at " + target
				+ " and thinks it is a " + target.getGameObject());
		if (target.hasGameObject()) {
			target.getGameObject().getInspected(this);
		}
	}

	/**
	 * Buy an item, if the player has enough money.
	 */
	public void purchase(PurchasableItem i) {
		if (i.getCost() > money) {
			System.out.println("You're too poor mate!");
			return;
		}
		inventory.add(i);
		money -= i.getCost();
	}

	/**
	 * Adds an item to the inventory, if it is not full.
	 */
	public boolean addItem(Item i) {
		if (inventory.size() >= 25) {
			System.out.println("Inventory is full!");
			return false;
		}
		inventory.add(i);
		return true;

	}

	@Override
	public String getInspected(Player p) {
		System.out.println("This player is: " + name);
		return name;
	}

	/**
	 * Get attacked and adjust the player's health accordingly.
	 */
	public void takeHit(int damage) {
		if (dead)
			return;
		health -= damage;
		if (health <= 0) {
			die();
			return;
		}
		System.out.println(name + ": Save me!!! Health: " + health);
	}

	/**
	 * Die and generate a chest containing the inventory of this player.
	 */

	private void die() {
		dead = true;
		Chest chest = new Chest();
		chest.unlock();
		Bag bag = new Bag();
		for (Item i : inventory) {
			bag.add(i);
		}
		inventory.clear();
		chest.add(bag);
		for (int i = 1; i < 5; i++) {
			Position target = world.getAdjacentPosition(position, i);
			if (!target.hasGameObject()) {
				target.setGameObject(chest);
				world.getAllGameObjects().add(chest);
				chest.setPosition(target);
				return;
			}
		}
		// TODO: unlikely case where player is completely surrounded
	}

	public void revive() {
		dead = false;
		health = job.getHealth();

	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public boolean isMoving() {
		return this.isMoving;
	}

	public int getDamage() {
		return this.weapon.getAttack();
	}

	public void setMoving(boolean moving) {
		isMoving = moving;
	}

	public World getWorld() {
		return this.world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public List<Item> getInventory() {
		return this.inventory;
	}

	public String getName() {
		return this.name;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public int getHealth() {
		return this.health;
	}

	public int getMaxHealth() {
		return this.job.getHealth();
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public int getMoney() {
		return this.money;
	}

	public void setWeapon(Weapon w) {
		this.weapon = w;
	}

	public int getOrientation() {
		return this.orientation;
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

	public Job getJob() {
		return this.job;
	}

	public void activateLightMode() {
		this.lightMode = true;
	}

	public void deactivateLightMode() {
		this.lightMode = false;
	}

	public boolean isDead() {
		return dead;
	}

	public Element toElement() {
		Element player = new Element("player");
		// player.setAttribute(new Attribute("name", name));
		player.addContent(new Element("name").setText(name));
		player.addContent(job.toElement());
		player.addContent(new Element("room").setText(world.getName()));
		player.addContent(position.toElement());
		player.addContent(new Element("orientation").setText(Integer
				.toString(orientation)));
		Element inv = new Element("inventory");
		for (Item i : inventory) {
			inv.addContent(i.toElement());
		}
		player.addContent(inv);

		player.addContent(weapon.toElement());
		player.addContent(new Element("health").setText(Integer
				.toString(health)));
		return player;
	}

	public static Player fromElement(Element e) {
		Player p = new Player();
		p.name = e.getChildText("name");
		p.job = Job.fromElement(e.getChild("job"));
		// p.room = TODO How should the room be set... probably not in here.
		// Will have to call player.setRoom(world) from Game or something
		p.position = null; // p.position =
		// Position.fromElement(e.getChild("position"));
		// Same deal here as above. Will need to be passed
		// to this player rather than loaded from the file
		p.orientation = Integer.parseInt(e.getChildText("orientation"));

		ArrayList<Item> inv = new ArrayList<Item>();
		List items = e.getChild("inventory").getChildren();
		if (!items.isEmpty()) {
			for (int i = 0; i < items.size(); i++) {
				Element item = (Element) items.get(i);
				if (item.getText().equals("weapon")) {
					inv.add(Weapon.fromElement(item));
				} else if (item.getText().equals("potion")) {
					inv.add(Potion.fromElement(item));
				} else if (item.getText().equals("revive")) {
					inv.add(Revive.fromElement(item));
				} else if (item.getText().equals("key")) {
					inv.add(Key.fromElement(item));
				}
			}
		}
		p.inventory = new ArrayList<Item>(inv);

		p.weapon = Weapon.fromElement(e.getChild("weapon"));
		// p.weapon.setPlayer(p);
		p.health = Integer.parseInt(e.getChildText("health"));

		return p;
	}

	@Override
	public void draw(Graphics2D g, Camera cam) {


		int x = this.getPosition().getCol() * GameCanvas.TILESIZE;
		int y = this.getPosition().getRow() * GameCanvas.TILESIZE;
		if (cam.isOnscreen(x, y)) {
			Point pn = cam.getPosOnScreen(x, y);
			sprite.drawOtherImage(g, pn.x, pn.y, this.orientation, this);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name + ", is in world: " + world + ", at position: "
				+ position);
		return sb.toString();
	}

	/**
	 * creates a string that stores the information about the Player used by the
	 * server to update clients when a game is loaded
	 *
	 * @return
	 */
	public String playerString() {
		StringBuilder s = new StringBuilder();
		s.append(name + ";" + position.getCol() + ";" + position.getRow() + ";"
				+ orientation + ";" + job.getName() + ";" + weapon.name + ";"
				+ health + ";" + world + ";" + money + ";" + weapon.getAttack() + ";");
		// loop for Items
		// for(Item i : inventory){
		//
		// }
		return s.toString();
	}

	@Override
	public void draw(Graphics2D g, int x, int y, int width, int height) {

		// TODO Auto-generated method stub

	}
}
