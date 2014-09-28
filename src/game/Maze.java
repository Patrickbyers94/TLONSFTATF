package game;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/** A maze. On reaching the goal, the user obtains a key. */

public class Maze extends World {

	private Position goal;
	private Chest chest;

	public Maze(File file) {
		super(file);
	}

	@Override
	public void movePlayer(Player p, int direction) {
		super.movePlayer(p, direction);
		if (p.getPosition().equals(goal)) {
			chest.unlock();
		}
	}

	/**
	 * ParseObjects for a Maze (copy+pasted from World but adds one goal and one chest)
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

		// Read goal. If you've screwed up and placed more than one goal in a maze, it will use the first one in the list.
		List goalsElem = rootNode.getChild("goals").getChildren();
		if (goalsElem != null) {
			Element goalElem = (Element) goalsElem.get(0);
			x = Integer.parseInt(goalElem.getChild("x").getText());
			y = Integer.parseInt(goalElem.getChild("y").getText());
			Position p = grid[x][y];
			goal = p;
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

	/**
	 * Set chest, used for loading
	 */
	public void setChest(Chest c){
		this.chest = c;
	}
}
