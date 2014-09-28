package game;

import graphics.TileSheet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/** A world. It may have players and other kinds of gameObjects. */

public class World {

	private String name;
	protected Position[][] grid;
	protected Position startingPosition;
	private TileSheet tileSheet = new TileSheet("assets/sprites/sheet2.png",
			32, 32);

	protected List<GameObject> gameObjects = new ArrayList<GameObject>();
	protected File file;

	private Random random = new Random();

	public World(File file) {
		this.file = file;
		parseWorld(file);
	}

	public World() {
		System.out
				.println("***************you shouldn't be using the blank constructor for world unless you're making maps!!!!!!!!!************\nif you see this outside of the map editor ya dun goofed");
	}

	/** Accepts an incoming player and places him at the right position */

	public void acceptPlayer(Player p) {
		// bump other player first, if necessary
		if (startingPosition.hasPlayer()) {
			Player toBump = (Player) startingPosition.getGameObject();
			// maybe not the smartest way to do this but it works, might get
			// back to this if I have time
			while (startingPosition.hasGameObject()) {
				int number = random.nextInt(4) + 1;
				if (!getAdjacentPosition(startingPosition, number)
						.hasGameObject())
					movePlayer(toBump, number);
			}
		}
		if (p.getPosition() != null)
			p.getPosition().setGameObject(null);
		p.setPosition(startingPosition);
		if (p.getWorld() != null)
			p.getWorld().getAllGameObjects().remove(p);
		p.setWorld(this);
		startingPosition.setGameObject(p);
		gameObjects.add(p);

	}

	public void update() {
		for (GameObject g : gameObjects) {
			if (g instanceof InteractiveObject)
				((InteractiveObject) g).update();
		}
	}

	/**
	 * The two methods below look like redundant intermediate steps but are in
	 * fact critical to the mechanics of the puzzles
	 */

	public void movePlayer(Player p, int direction) {
		p.move(direction);
	}

	public void moveGameObject(MovableObject g, int direction) {
		g.move(direction);
	}

	/** Gets the position at the given row and column */

	public Position getPosition(int newRow, int newCol) {
		assert newRow >= 0;
		assert newCol >= 0;
		assert newRow < grid[0].length;
		assert newCol < grid.length;
		return grid[newCol][newRow];
	}

	/** Gets the position adjacent to a given position in a given direction. */
	public Position getAdjacentPosition(Position p, int direction) {
		int newRow = p.getRow();
		int newCol = p.getCol();
		if (!grid[newCol][newRow].equals(p)) {
			return null;
		}
		if (direction == 1)
			newRow--;
		else if (direction == 2)
			newCol++;
		else if (direction == 3)
			newRow++;
		else if (direction == 4)
			newCol--;
		if (newRow < 0 || newCol < 0 || newRow >= grid[0].length
				|| newCol >= grid.length)
			return null;
		return grid[newCol][newRow];
	}

	/** Parses and constructs the world from a file */

	private void parseWorld(File file) {
		try {
			Scanner scan = new Scanner(file);
			name = file.getName();
			grid = new Position[scan.nextInt()][scan.nextInt()];
			for (int row = 0; row < grid[0].length; row++) {
				for (int col = 0; col < grid.length; col++) {
					if (scan.hasNext("\n")) {
						scan.nextLine();
					}
					int tile = scan.nextInt();
					grid[col][row] = new Position(col, row);
					Position p = grid[col][row];
					p.setType(tile);
					if (!tileSheet.getPassable(tile)) {
						p.setImpassable();
					}
				}
			}

			// Dummy teleporters
			System.out.println(name);
			if (name.equals("overworld")) {
				for (int i = 0; i < 74; i++) {
					grid[i][2] = new Teleporter(i, 2, grid[30][16]);

				}
			}

			// Dummy ice
			System.out.println(name);
			if (name.equals("overworld")) {
				for (int i = 25; i < 50; i++) {
					grid[i][20] = new Ice(i, 20);
					grid[i][21] = new Ice(i, 21);
					grid[i][23] = new Ice(i, 23);
					grid[i][22] = new Ice(i, 22);
				}
			}

			scan.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void parseObjects(Map<String, World> worldsMap)
			throws JDOMException, IOException {
		// Read starting position, boxes, goals from xml file
		File xmlFile = new File(file.getPath() + ".xml");
		SAXBuilder builder = new SAXBuilder();
		Document document = (Document) builder.build(xmlFile);
		Element rootNode = document.getRootElement();

		int x = Integer.parseInt(rootNode.getChild("startingPosition")
				.getChild("position").getChild("x").getText());
		int y = Integer.parseInt(rootNode.getChild("startingPosition")
				.getChild("position").getChild("y").getText());
		startingPosition = grid[x][y];

		// Read boxes
		List boxes = rootNode.getChild("boxes").getChildren();
		if (boxes != null) {
			for (int i = 0; i < boxes.size(); i++) {
				Element b = (Element) boxes.get(i);
				x = Integer.parseInt(b.getChild("x").getText());
				y = Integer.parseInt(b.getChild("y").getText());
				Box box = new Box(grid[x][y]);
				box.setRoom(this);
				gameObjects.add(box);
			}
		}

		// Read doors
		List doors = rootNode.getChild("doors").getChildren();
		if (doors != null) {
			for (int i = 0; i < doors.size(); i++) {
				Element door = (Element) doors.get(i);
				World dest = worldsMap.get(door.getChild("destination")
						.getText());
				Door d = new Door(dest);
				if (door.getChild("locked").getText().equals("true")) {
					d.lock();
				}
				Position p = grid[Integer.parseInt(door.getChild("position")
						.getChild("x").getText())][Integer.parseInt(door
						.getChild("position").getChild("y").getText())];
				d.setPosition(p);
				p.setGameObject(d);
				gameObjects.add(d);
			}
		}

		// Read trees
		Element t = rootNode.getChild("trees");
		if (t != null) {
			List trees = t.getChildren();
			for (int i = 0; i < trees.size(); i++) {
				Element treeElem = (Element) trees.get(i);
				Tree newTree = new Tree();
				x = Integer.parseInt(treeElem.getChild("position")
						.getChild("x").getText());
				y = Integer.parseInt(treeElem.getChild("position")
						.getChild("y").getText());
				Position p = grid[x][y];
				newTree.setPosition(p);
				p.setGameObject(newTree);
				gameObjects.add(newTree);
			}
		}

	}

	/** Prints out the world to the console */

	public void print() {
		for (int row = 0; row < grid[0].length; row++) {
			for (int col = 0; col < grid.length; col++) {
				Position p = grid[col][row];
				if (p.hasItem())
					System.out.print("I ");
				else if (p.hasPlayer())
					System.out.print(((Player) p.getGameObject()).getName()
							.substring(0, 1) + " ");
				else if (p.hasGameObject()) {
					System.out.print("M ");
				} else
					System.out.print(p.getType() + " ");
			}
			System.out.println();
		}
		System.out.println();
	}

	public Position[][] getGrid() {
		return this.grid;
	}

	public String getName() {
		return name;
	}

	public List<GameObject> getAllGameObjects() {
		return this.gameObjects;
	}

	public List<GameObject> getGameObjects(int startRow, int endRow,
			int startCol, int endCol) {
		List<GameObject> objects = new ArrayList<GameObject>();
		for (GameObject object : gameObjects) {
			int row = object.getPosition().getRow();
			int col = object.getPosition().getCol();
			if (row < startRow || row > endRow || col < startCol
					|| col > endCol)
				continue;
			objects.add(object);
		}
		return objects;
	}

	public Position getStartingPosition() {
		return startingPosition;
	}

	/**
	 * This removes all Boxes and Chests from gameObjects, leaving doors etc
	 */
	public void clearGameObjects() {
		ArrayList<GameObject> newList = new ArrayList<GameObject>();
		for (int i = 0; i < gameObjects.size(); i++) {
			if (gameObjects.get(i) instanceof Box
					|| gameObjects.get(i) instanceof Chest) {
				Position p = gameObjects.get(i).getPosition();
				if (p != null) {
					p.setGameObject(null);
				}
			} else {
				newList.add(gameObjects.get(i));
			}
		}
		gameObjects = newList;
	}

	public void setGameObjects(List<GameObject> obj) {
		gameObjects = obj;
	}

	public String toString() {
		return this.name;
	}

}