package game;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 * Sokoban. On getting a box to every goal position in the world, the player
 * obtains a key.
 */

public class Sokoban extends World {

	private List<Position> goals = new ArrayList<Position>();
	private Chest chest;

	public Sokoban(File file) {
		super(file);
	}

	/** Returns true if and only if every goal in the puzzle has a box on it */

	public boolean isSolved() {
		for (Position goal : goals) {
			if (!(goal.hasGameObject() && goal.getGameObject() instanceof Box)) {
				return false;
			}
		}
		return true;
	}

	/** Moves the object and checks if the puzzle has been solved */

	@Override
	public void moveGameObject(MovableObject g, int direction) {
		super.moveGameObject(g, direction);
		if (isSolved()) {
			chest.unlock();
		}

	}

	/**
	 * ParseObjects for Sokoban (copy+pasted from World but adds multiple goals and one chest)
	 */
	@Override
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

		// Read goals
		List goalsElem = rootNode.getChild("goals").getChildren();
		if (goalsElem != null) {
			for (int i = 0; i < goalsElem.size(); i++) {
				Element goal = (Element) goalsElem.get(i);
				x = Integer.parseInt(goal.getChild("x").getText());
				y = Integer.parseInt(goal.getChild("y").getText());
				Position p = grid[x][y];
				goals.add(p);
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

		// Read chest
		Element chestElem = rootNode.getChild("chest");
		chest = Chest.fromElement(chestElem);
		x = Integer.parseInt(chestElem.getChild("x").getText());
		y = Integer.parseInt(chestElem.getChild("y").getText());
		Position p = grid[x][y];
		chest.setPosition(p);
		p.setGameObject(chest);
		gameObjects.add(chest);
	}

	public List<Position> getGoals(){
		return this.goals;
	}


	/**
	 * Set chest, used for loading
	 * @param c
	 */
	public void setChest(Chest c){
		this.chest = c;
	}

}