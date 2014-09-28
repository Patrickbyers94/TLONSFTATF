package game;

import gui.ShopScreen;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/** A room where players can buy items for a sum of money. */

public class Shop extends World {

	private Position goal;
	List<PurchasableItem> onSale = new ArrayList<PurchasableItem>();
	final JFrame buy = new JFrame("The Legend of Nelda - Shop Screen");
	boolean buying = false;

	public Shop(File file) {
		super(file);
		onSale.add(new Torch());
		onSale.add(new Potion(30));
		onSale.add(new Revive());
		onSale.add(new Sword("Sword", 3));
		onSale.add(new Sword("Long Sword", 5));
		onSale.add(new Bow("Bow", 2));
		onSale.add(new Bow("Long Bow", 3));
	}

	@Override
	public void movePlayer(Player p, int direction) {
		if (!buying) {
			super.movePlayer(p, direction);
			if (p.getPosition().equals(goal)) {
				buying = true;
				showFrame(p);
			}
		}

	}

	/**
	 * ParseObjects for a shop (copy+pasted from World but adds one goal)
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

		// Read goal. If you've screwed up and placed more than one goal in a
		// maze, it will use the first one in the list.
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
	}

	private void showFrame(Player p) {
		buy.setSize(310, 425);
		final ShopScreen shop = new ShopScreen(onSale, p, buy);
		JPanel bPanel = new JPanel();
		buy.add(shop, BorderLayout.CENTER);

		JButton purchase = new JButton("Purchase");
		purchase.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				shop.purchase();
			}
		});
		bPanel.add(purchase, BorderLayout.WEST);

		JButton done = new JButton("Finished");
		done.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				buy.dispose();
				buying = false;
			}
		});
		bPanel.add(done, BorderLayout.EAST);
		buy.add(bPanel, BorderLayout.SOUTH);
		buy.setVisible(true);
	}
}

class ShopThread extends Thread {
	Player p;
	JFrame buy;
	List<PurchasableItem> onSale;
	boolean buying;

	public ShopThread(Player slayer, JFrame f, List<PurchasableItem> l,
			boolean b) {
		p = slayer;
		buy = f;
		onSale = l;
		buying = b;
	}

	public void run() {
		buy.setSize(310, 425);
		final ShopScreen shop = new ShopScreen(onSale, p, buy);
		JPanel bPanel = new JPanel();
		buy.add(shop, BorderLayout.CENTER);

		JButton purchase = new JButton("Purchase");
		purchase.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				shop.purchase();
			}
		});
		bPanel.add(purchase, BorderLayout.WEST);

		JButton done = new JButton("Finished");
		done.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				buy.dispose();
				buying = false;
			}
		});
		bPanel.add(done, BorderLayout.EAST);
		buy.add(bPanel, BorderLayout.SOUTH);
		buy.setVisible(true);
	}
}
