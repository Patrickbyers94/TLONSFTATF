package game;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * The game state.
 */

public class Game {

	private List<Player> players = new ArrayList<Player>();
	private List<Player> onlinePlayers = new ArrayList<Player>();
	private World overworld = new World(new File("assets/overworld"));
	private Time time = new Time();
	private int weather = 0;
	private Random random = new Random();

	protected Map<String, World> worldsMap = new HashMap<String, World>();

	public Game() {

		// make worlds without objects and put them into map
		worldsMap.put("overworld", overworld);
		worldsMap.put("sokoban1", new Sokoban(new File("assets/sokoban1")));
		worldsMap.put("sokoban2", new Sokoban(new File("assets/sokoban2")));
		worldsMap.put("sokoban3", new Sokoban(new File("assets/sokoban3")));
		worldsMap.put("maze1", new Maze(new File("assets/maze1")));
		worldsMap.put("maze2", new Maze(new File("assets/maze2")));
		worldsMap.put("maze3", new Maze(new File("assets/maze3")));
		worldsMap.put("bastardmaze", new Maze(new File("assets/bastardmaze")));
		worldsMap.put("finalWorld", new World(new File("assets/finalWorld")));
		worldsMap.put("finalWorld1", new World(new File("assets/finalWorld1")));
		worldsMap.put("treetest", new World(new File("assets/treetest")));
		worldsMap.put("shop", new Shop(new File("assets/shop")));
		worldsMap.put("inn", new Inn(new File("assets/inn")));

		// add objects to worlds
		// The reason for doing it in two stages is because putting a door into
		// a world requires the world it points to, to exist, which it obviously
		// doesn't if you're currently making the first world for example.
		try {
			for (World world : worldsMap.values()) {
				world.parseObjects(worldsMap);
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void update() {
		for (World world : worldsMap.values()) {
			world.update();
		}
		time.update();
		// TODO: weather;
		if (true) {
			weather = random.nextInt(4);
		}
	}

	/** Adds a player to the game. */
	public void addPlayer(Player p) {
		if (onlinePlayers.contains(p))
			return;
		if (players.contains(p))
			players.remove(p);
		onlinePlayers.add(p);
		System.out.println(p.getWorld());
		if (p.getWorld() != null) {
			p.getWorld().acceptPlayer(p);
		} else {
			overworld.acceptPlayer(p);
		}
	}

	/** Removes a player from the game. */
	public void removePlayer(Player p) {
		players.add(p);
		onlinePlayers.remove(p);
	}

	/** Moves a player in a particular direction. */
	public void movePlayer(Player p, int direction) {
		if (!p.isDead())
			p.getWorld().movePlayer(p, direction);
	}

	/**
	 * Gets a player to try to attack.
	 *
	 * @param player
	 *            The player that attacks
	 * @return The position of the target, if there is one
	 */
	public Position attack(Player p) {
		if (p.isDead() || p.getWorld().getName().equals("inn"))
			return null;
		return p.attack();
	}

	/** Gets the player to inspect the position in front of him. */
	public void inspect(Player p) {
		if (!p.isDead())
			p.inspect();
	}

	/** Gets a player to drop a particular item. */
	public void drop(Player p, Item i) {
		if (!p.isDead())
			p.drop(i);
	}

	/** Gets a player to equip a particular weapon. */
	public void equip(Player p, Weapon w) {
		if (!p.isDead())
			p.use(w);
	}

	/** Gets a player to use a particular item. */
	public void useItem(Player p, Item i) {
		System.out.println(p + "is trying to use a " + i);
		if (!p.isDead())
			p.use(i);
	}

	/** Gets a player to purchase a particular item. */
	public void purchaseItem(Player p, PurchasableItem i) {
		if (!p.isDead())
			p.purchase(i);
	}
	
	

	public List<Player> getOnlinePlayers() {
		return onlinePlayers;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public World getWorld() {
		return this.overworld;
	}

	public Map<String, World> getWorlds() {
		return this.worldsMap;
	}

	public Time getTime() {
		return this.time;
	}

	/**
	 * Save game state to xml with specified prefix. Just calls other save
	 * methods (which haven't been written yet - TODO) eg save players, save
	 * items etc.
	 *
	 * @param prefix
	 *            The prefix you want to use
	 */
	public void save(String prefix) {
		// Save players
		savePlayers(prefix);
		// Save GameObjects (eg boxes and stuff)
		saveObjects(prefix);
	}

	/**
	 * Load game state from xml file with specified prefix.
	 *
	 * @param prefix
	 *            The prefix you want to use
	 */

	public void load(String prefix) {
		loadPlayers(prefix);
		loadObjects(prefix);
	}

	/**
	 * Saves the list of players to an XML file with a prefix specified by the
	 * user. Eg "cooldudes-players.xml"
	 *
	 * @param prefix
	 *            The prefix you want to use
	 */
	public void savePlayers(String prefix) {
		System.out.println("Saving players to " + prefix + "-players.xml...");
		try {
			Element listOfPlayers = new Element("players");

			/*
			 * Add each player as an element to the list of players
			 */
			for (Player p : onlinePlayers) {
				listOfPlayers.addContent(p.toElement());
			}
			for (Player p : players) {
				listOfPlayers.addContent(p.toElement());
			}
			Document playersDoc = new Document(listOfPlayers);

			XMLOutputter xmlOutput = new XMLOutputter();

			xmlOutput.setFormat(Format.getPrettyFormat());
			xmlOutput.output(playersDoc,
					new FileWriter(prefix + "-players.xml"));
			System.out.println("Players saved!");
		} catch (IOException io) {
			io.printStackTrace();
			return;
		}
	}

	/**
	 * Load players from an xml file
	 *
	 * @param prefix
	 */
	public void loadPlayers(String prefix) {
		System.out
				.println("Loading players from " + prefix + "-players.xml...");
		SAXBuilder builder = new SAXBuilder();
		File xmlFile = new File(prefix + "-players.xml");
		onlinePlayers = new ArrayList<Player>();
		try {
			Document document = (Document) builder.build(xmlFile);
			Element rootNode = document.getRootElement();
			List list = rootNode.getChildren();

			for (int i = 0; i < list.size(); i++) {
				Element e = (Element) list.get(i);
				Player pl = Player.fromElement(e);
				World pw = worldsMap.get(e.getChild("room").getText());
				pl.setWorld(pw);
				int x = Integer.parseInt(e.getChild("position").getChild("x")
						.getText());
				int y = Integer.parseInt(e.getChild("position").getChild("y")
						.getText());
				pl.setPosition(pw.getPosition(y, x));
				players.add(pl);
			}
			System.out.println("... done!");
		} catch (IOException io) {
			io.printStackTrace();
		} catch (JDOMException jdomex) {
			jdomex.printStackTrace();
		}
	}

	/**
	 * Saves all the GameObjects to an XML file.
	 *
	 * @param prefix
	 *            The prefix you want to use
	 */
	public void saveObjects(String prefix) {
		System.out.println("Saving objects to " + prefix + "-objects.xml...");
		try {
			Element listOfWorlds = new Element("worlds");
			Document worldsDoc = new Document(listOfWorlds);

			/*
			 * Make an Element for each world, make an Element for the list of
			 * objects in that world, add each object in the world to that list
			 * as an Element.
			 */
			for (World world : worldsMap.values()) {
				Element thisWorldElement = new Element(world.getName());
				Element thisWorldObjects = new Element("gameObjects");
				List<GameObject> objects = world.getAllGameObjects();
				for (GameObject obj : objects) {
					// Special cases first
					if (obj instanceof Door) {
					} // Do nothing with Doors for now (TODO save their locked
						// status)
					else if (obj instanceof Player) {
					} // Do nothing with Players because they have their own
						// save method
					else if (obj instanceof Monster) {
					} // Do we need to save monsters? Or should they just be
						// re-generated when you start the game again?
					else if (obj instanceof NPC) {
					} // Same question as for Monsters
					else if (obj instanceof Key) {
					} // Shouldn't need to do anything for Keys as they can only
						// exist in a container or a Player's inventory
					else if (obj instanceof Potion) {
					} // Ditto for Potions
					else if (obj instanceof Torch) {
					} // Ditto for torches
					else if (obj instanceof Map) {
					} // If we end up using Map, it will be in a
						// container/player's inventory
					else {
						thisWorldObjects.addContent(obj.toElement());
					}
				}
				thisWorldElement.addContent(thisWorldObjects);
				listOfWorlds.addContent(thisWorldElement);
			}

			XMLOutputter xmlOutput = new XMLOutputter();

			xmlOutput.setFormat(Format.getPrettyFormat());
			xmlOutput
					.output(worldsDoc, new FileWriter(prefix + "-objects.xml"));
			System.out.println("Objects saved!");
		} catch (IOException io) {
			io.printStackTrace();
			return;
		}
	}

	/**
	 * Load objects from an xml file
	 *
	 * @param prefix
	 */
	public void loadObjects(String prefix) {
		System.out.println("Loading players from " + prefix + "-objects.xml");

		SAXBuilder builder = new SAXBuilder();
		File xmlFile = new File(prefix + "-objects.xml");
		try {
			Document document = (Document) builder.build(xmlFile);
			Element rootNode = document.getRootElement();
			List worldElements = rootNode.getChildren();

			for (Player p : onlinePlayers) {
				p.getPosition().setGameObject(null);
			}

			for (int i = 0; i < worldElements.size(); i++) {
				Element thisWorldElement = (Element) worldElements.get(i);
				ArrayList<GameObject> objects = new ArrayList<GameObject>();
				World world = worldsMap.get(thisWorldElement.getName());

				world.clearGameObjects();

				List objectElements = thisWorldElement.getChild("gameObjects")
						.getChildren();
				for (int j = 0; j < objectElements.size(); j++) {

					Element elem = (Element) objectElements.get(j);
					if (elem.getName().equals("potion")) {
						objects.add(Potion.fromElement(elem));
					} else if (elem.getName().equals("weapon")) {
						objects.add(Weapon.fromElement(elem));
					} else if (elem.getName().equals("chest")) {
						Chest c = Chest.fromElement(elem);
						int x = Integer.parseInt(elem.getChild("x").getText());
						int y = Integer.parseInt(elem.getChild("y").getText());
						Position p = world.getPosition(y, x);
						p.setGameObject(c);
						c.setPosition(p);
						objects.add(c);
						world.grid[x][y].setGameObject(c);
						if (world instanceof Sokoban) {
							((Sokoban) world).setChest(c);
						} else if (world instanceof Maze) {
							((Maze) world).setChest(c);
						}
					} else if (elem.getName().equals("torch")) {
						objects.add(Torch.fromElement(elem));
					} else if (elem.getName().equals("box")) {
						int x = Integer.parseInt(elem.getChild("x").getText());
						int y = Integer.parseInt(elem.getChild("y").getText());
						Position p = world.getPosition(y, x);
						Box b = new Box(p);
						b.setRoom(world);
						objects.add(b);
						world.grid[x][y].setGameObject(b);
					}
					// TODO other object types
				}
				world.getAllGameObjects().addAll(objects);
				if (this.overworld.getName().equals(world.getName())) {
					overworld = world;
				}
			}
			System.out.println("... done!");
		} catch (IOException io) {
			io.printStackTrace();
		} catch (JDOMException jdomex) {
			jdomex.printStackTrace();
		}
	}

	/**
	 * Just for testing my XML stuff - ignore this TODO: get rid of this before
	 * submitting
	 */
	public static void main(String[] args) {
		// File f = new File("assets/overworld");
		Game g = new Game();
		Player p1 = new Player("testPlayer", Job.Soldier());
		Player p2 = new Player("testPlayer2", Job.Archer());
		// p1.setWorld(g.getWorld());
		// p1.setPosition(g.getWorld().getPosition(5, 5));
		// p2.setWorld(g.getWorld());
		// p2.setPosition(g.getWorld().getPosition(10, 15));
		g.addPlayer(p1);
		g.addPlayer(p2);
		g.savePlayers("test");
		Game g2 = new Game();
		g2.loadPlayers("test");
		g2.printPlayers();
	}

	private void printPlayers() {
		for (Player p : onlinePlayers) {
			System.out.println("online player: " + p);
		}
		for (Player p : players) {
			System.out.println("offline player: " + p);
		}
	}
}
