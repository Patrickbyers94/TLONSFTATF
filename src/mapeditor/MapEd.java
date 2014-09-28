package mapeditor;

import game.Box;
import game.Chest;
import game.Door;
import game.Position;
import game.Tree;
import game.World;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
/**
 * Map editor for our game. Allows the user to draw a map on a grid
 * of a specified width and height and saves it to a file.
 * Much easier than putting it in a text file by hand.
 * @author Christian Evaroa
 *
 */
public class MapEd implements WindowListener {

	protected MapEdGUI_AutoButtons gui;

	protected int mapwidth;
	protected int mapheight;
	protected String fname;
	private ConfigDialog config;
	protected int[][] grid;

	protected Position startPos;
	protected List<Position> goals;
	protected List<Box> boxes;
	protected List<Door> doors;
	protected List<Tree> trees;
	protected Chest chest;

	/**
	 * MapEd constructor, show a dialog asking for map dimensions and a filename
	 * When user clicks OK, the MapEdGUI appears.
	 */
	public MapEd() {
		this.goals = new ArrayList<Position>();
		this.boxes = new ArrayList<Box>();
		this.doors = new ArrayList<Door>();
		this.trees = new ArrayList<Tree>();
		config = new ConfigDialog(configListener);
		config.addWindowListener(this);
	}

	/**
	 * Make a new MapEdGUI to draw on
	 */
	public void setupWindow() {
		gui = new MapEdGUI_AutoButtons(this);
		gui.addWindowListener(this);
	}

	/**
	 * Save the grid contents to "assets/[fname]" where [fname] is the filename you specified.
	 * Save Box & Goal locations to "assets/[fname].xml"
	 */
	protected void save() {
		File saveFile = new File("assets/"+fname);
		File xmlFile = new File("assets/"+fname+".xml");
		try {
			if(!saveFile.exists()){ saveFile.createNewFile(); }
			else{ saveFile.delete(); saveFile.createNewFile(); }
			BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile));
			// Write the dimensions to the file to start with
			writer.write(mapwidth+" "+mapheight+"\n");
			// Write the contents of the array to the file
			for(int y = 0; y < mapheight; y++){
				for(int x = 0; x < mapwidth; x++){
					writer.write(Integer.toString(grid[x][y]));
					writer.write(" ");
				}
				// Next Line
				if(y != mapheight-1){
					writer.write('\n');
				}
			}
			writer.close();

			// Save boxes, goals and start position
			if(!xmlFile.exists()){ saveFile.createNewFile(); }
			else{ xmlFile.delete(); saveFile.createNewFile(); }
			Element root = new Element(fname);
			Document mapDoc = new Document(root);

			// Save boxes
			Element mov = new Element("boxes");
			for(Box b : boxes){
				b.setRoom(new World(){ String name = fname; });
				mov.addContent(b.toElement());
			}
			root.addContent(mov);

			// Save goals
			Element gs = new Element("goals");
			for(Position p : goals){
				gs.addContent(p.toElement());
			}
			root.addContent(gs);

			// Save start position
			Element stPos = new Element("startingPosition");
			if(startPos!=null){
				stPos.addContent(startPos.toElement());
			} else {
				stPos.addContent(new Position(3,3).toElement()); // Default to 3,3 and hope for the best.
			}
			root.addContent(stPos);

			// Save doors
			Element ds = new Element("doors");
			for(Door d : doors){
				ds.addContent(d.toElement());
			}
			root.addContent(ds);

			// Save chest
			Element cs = new Element("chests");
			
			// Save trees
			Element ts = new Element("trees");
			for(Tree t : trees){
				ts.addContent(t.toElement());
			}
			root.addContent(ts);

			XMLOutputter xmlOutput = new XMLOutputter();

			xmlOutput.setFormat(Format.getPrettyFormat());
			xmlOutput.output(mapDoc,
					new FileWriter(xmlFile));

			System.out.println("File Saved!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}




	/**
	 * Load a map from a file
	 */
	protected void load(){
		File load;
		File xmlFile;
		JFileChooser fc = new JFileChooser("assets/");
		if(fc.showOpenDialog(gui)!=JFileChooser.APPROVE_OPTION){ }
		else{
			load = fc.getSelectedFile();
			xmlFile = new File("assets/"+load.getName()+".xml");
			try {
				Scanner sc = new Scanner(load);

				// Get the map dimensions
				int xdimension = sc.nextInt();
				int ydimension = sc.nextInt();

				// Set up a temporary array to read values into
				int[][] temp = new int[xdimension][ydimension];

				// Read the characters into the temporary array
				for(int y = 0; y < ydimension; y++){
					for(int x = 0; x < xdimension; x++){
						temp[x][y] = sc.nextInt();
						if(sc.hasNext(" "))
							sc.next();
						if(x == xdimension-1 && sc.hasNextLine()){ sc.nextLine(); }
					}
				}

				// Load boxes, goals and doors
				boxes.clear();
				goals.clear();
				doors.clear();
				trees.clear();
				chest = null;

				if(xmlFile.exists()){
					SAXBuilder builder = new SAXBuilder();
					Document document = (Document) builder.build(xmlFile);
					Element rootNode = document.getRootElement();

					// Load boxes
					List boxList = rootNode.getChild("boxes").getChildren();
					for (int i = 0; i < boxList.size(); i++) {
						Element e = (Element) boxList.get(i);
						boxes.add(Box.fromElement(e));
					}

					// Load goals
					List goalList = rootNode.getChild("goals").getChildren();
					for (int i = 0; i < goalList.size(); i++) {
						Element e = (Element) goalList.get(i);
						int x = Integer.parseInt(e.getChild("x").getText());
						int y = Integer.parseInt(e.getChild("y").getText());
						Position p = new Position(x, y);
						goals.add(p);
					}

					// Load starting position
					Element stPos = rootNode.getChild("startingPosition").getChild("position");
					if(stPos != null){
						int x = Integer.parseInt(stPos.getChild("x").getText());
						int y = Integer.parseInt(stPos.getChild("y").getText());
						startPos = new Position(x,y);
					}

					// Load doors
					if(rootNode.getChild("doors") != null){
						List doorList = rootNode.getChild("doors").getChildren();
						for(int i = 0; i < doorList.size(); i++){
							Element e = (Element) doorList.get(i);
							final String worldName = e.getChild("destination").getText();
							Door d = new Door(new World(new File("assets/"+worldName)));
							int x = Integer.parseInt(e.getChild("position").getChild("x").getText());
							int y = Integer.parseInt(e.getChild("position").getChild("y").getText());
							Position p = new Position(x, y);
							d.setPosition(p);
							if(e.getChild("locked").getText().equals("True")){
								d.lock();
							} else { d.unlock(); }
							doors.add(d);
						}}

					// Load chests
					if(rootNode.getChild("chest") != null){
						Element e = rootNode.getChild("chest");
						chest = Chest.fromElement(e);
						int x = Integer.parseInt(e.getChild("x").getText());
						int y = Integer.parseInt(e.getChild("y").getText());
						Position p = new Position(x, y);
						chest.setPosition(p);
					}
					
					// Load trees
					if(rootNode.getChild("trees") != null){
						List treeList = rootNode.getChild("trees").getChildren();
						for(int i = 0; i < treeList.size(); i++){
							Element e = (Element) treeList.get(i);
							Tree tree = new Tree();
							int x = Integer.parseInt(e.getChild("position").getChild("x").getText());
							int y = Integer.parseInt(e.getChild("position").getChild("y").getText());
							Position p = new Position(x, y);
							tree.setPosition(p);
							trees.add(tree);
						}
					}

				}

				// Point grid at new array and update fname, width & height
				grid = temp;
				mapwidth = xdimension;
				mapheight = ydimension;
				fname = load.getName();
				if(gui!= null){
					gui.setTitle("Nelda Map Editor - "+fname);
					gui.validate();
					gui.redraw();
				}

			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showConfirmDialog(gui, "File load error (not found/wrong format etc)",
						"Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
			}
		}
	}



	/**
	 * Create a new blank map
	 */
	protected void newMap(){
		gui.dispose();
		goals.clear();
		boxes.clear();
		startPos = null;
		config = new ConfigDialog(configListener);
		config.addWindowListener(this);
	}

	/**
	 * Listener for ConfigDialog
	 */
	private ActionListener configListener = new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() instanceof JButton){
				JButton b = (JButton)(e.getSource());
				if(b.getText().equals("Load")){
					load();
					setupWindow();
					config.dispose();
				}
				else{
					mapwidth = config.returnWidth();
					mapheight = config.returnHeight();
					fname = config.getfname();
					grid = new int[mapwidth][mapheight];
					System.out.println(mapwidth+", "+mapheight+", "+fname);
					setupWindow();
					config.dispose();
				}
			}
		}
	};

	/**
	 * Ask user before closing window
	 */
	@Override
	public void windowClosing(WindowEvent arg0) {
		int r = JOptionPane.showConfirmDialog(gui, new JLabel(
				"Are you sure you want to quit?"), "Confirm Exit",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (r == JOptionPane.YES_OPTION) {
			System.exit(0);
		}
	}



	public static void main(String[] args){
		new MapEd();
	}

	/*
	 * These aren't needed
	 */
	@Override
	public void windowActivated(WindowEvent arg0) {}
	@Override
	public void windowClosed(WindowEvent arg0) {}
	@Override
	public void windowDeactivated(WindowEvent arg0) {}
	@Override
	public void windowDeiconified(WindowEvent arg0) {}
	@Override
	public void windowIconified(WindowEvent arg0) {}
	@Override
	public void windowOpened(WindowEvent arg0) {}
}
