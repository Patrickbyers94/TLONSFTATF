package mapeditor;

import game.Box;
import game.Chest;
import game.Door;
import game.MovableObject;
import game.Position;
import game.Tree;
import graphics.TileSheet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
/**
 * Map editor that automatically makes buttons from tiles in a TileSheet.
 * Allows you to place boxes and goals for sokoban / mazes
 * @author Christian Evaroa
 *
 */
public class MapEdGUI_AutoButtons extends JFrame implements MouseListener, MouseMotionListener {

	private MapEd mapEd;
	private JScrollPane viewScroll;
	private JPanel buttonPanel;
	private JPanel southButtonPanel;
	private JComponent view;
	private JMenuBar menubar;
	private JPanel filenamePanel;
	private JTextField filenameField;
	private int TILE_WIDTH = 32;
	private boolean dragging;
	private boolean coords;
	private boolean grid = true;

	protected int brush = 0; // -1 = Goal, -2 = MovableObject, -3 = starting position, -4 = door, -5 = chest, -6 = tree, -7 = object eraser

	int clickedX;
	int clickedY;
	int lastX;
	int lastY;

	int startX;
	int startY;
	int endX;
	int endY;

	TileSheet tsheet = new TileSheet("assets/sprites/sheet2.png", 32, 32);

	/**
	 * Constructor for MapEdGUI
	 * A GUI for the Map editor in case you couldn't tell.
	 * @param ed The MapEd that this MapEdGUI belongs to.
	 */
	public MapEdGUI_AutoButtons(MapEd ed){
		super("Nelda Map Editor - "+ed.fname);
		mapEd = ed;
		setupWindow();
	}

	/**
	 * Set up the GUI.
	 * JMenubar for saving & loading.
	 * JPanel for brush buttons.
	 * JComponent in a JScrollPane for drawing the map.
	 */
	private void setupWindow() {
		setSize(800, 600);
		setLayout(new BorderLayout());
		view = new JComponent(){protected void paintComponent(Graphics g){draw(g);}};
		view.setPreferredSize(new Dimension(mapEd.mapwidth*TILE_WIDTH, mapEd.mapheight*TILE_WIDTH));
		viewScroll = new JScrollPane(view);
		view.addMouseListener(this);
		view.addMouseMotionListener(this);

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

		southButtonPanel = new JPanel();
		southButtonPanel.setLayout(new BoxLayout(southButtonPanel, BoxLayout.X_AXIS));

		JButton startPos = new JButton("Place start");
		startPos.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				brush = -3;
			}});
		southButtonPanel.add(startPos);
		startPos.setToolTipText("Place a starting position");

		JButton goal = new JButton("Place Goal");
		goal.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				brush = -1;
			}});
		southButtonPanel.add(goal);
		goal.setToolTipText("Place a goal");

		JButton removeGoal = new JButton("Remove Goal");
		removeGoal.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(!mapEd.goals.isEmpty())
					mapEd.goals.remove(mapEd.goals.size()-1);
				redraw();
			}});
		southButtonPanel.add(removeGoal);
		removeGoal.setToolTipText("Remove the last goal you added");

		JButton box = new JButton("Place Box");
		box.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				brush = -2;
			}});
		southButtonPanel.add(box);
		box.setToolTipText("Place a pushable box");

		JButton removeBox = new JButton("Remove Box");
		removeBox.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(!mapEd.boxes.isEmpty())
					mapEd.boxes.remove(mapEd.boxes.size()-1);
				redraw();
			}});
		southButtonPanel.add(removeBox);
		removeBox.setToolTipText("Remove the last box you added");

		JButton door = new JButton("Place Door");
		door.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				brush = -4;
			}});
		southButtonPanel.add(door);
		door.setToolTipText("Place a door");

		JButton removeDoor = new JButton("Remove Door");
		removeDoor.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!mapEd.doors.isEmpty())
					mapEd.doors.remove(mapEd.doors.size()-1);
				redraw();
			}});
		southButtonPanel.add(removeDoor);
		removeDoor.setToolTipText("Remove the last door you added");

		JButton chest = new JButton("Place Chest");
		chest.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				brush = -5;
			}});
		southButtonPanel.add(chest);
		chest.setToolTipText("Place a chest (with no contents)");

		JButton removeChest = new JButton("Remove Chest");
		removeChest.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				mapEd.chest = null;
				redraw();
			}});
		southButtonPanel.add(removeChest);
		removeChest.setToolTipText("Remove the last chest you added");

		JButton tree = new JButton("Place Tree");
		tree.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				brush = -6;
			}});
		southButtonPanel.add(tree);
		tree.setToolTipText("Add a tree. So gangsta.");

		JButton removeTree = new JButton("Treeraser");
		removeTree.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				brush = -7;
			}});
		removeTree.setToolTipText("Erase trees.");
		
		JScrollPane southScroll = new JScrollPane(southButtonPanel);

		menubar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem save = new JMenuItem("Save");
		save.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent arg0) {
				mapEd.save();
			}});
		fileMenu.add(save);

		JMenuItem load = new JMenuItem("Load");
		load.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent arg0) {
				mapEd.load();
				viewScroll.validate();
			}});
		fileMenu.add(load);

		JMenuItem newMap = new JMenuItem("New");
		newMap.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent arg0) {
				mapEd.newMap();
			}});
		fileMenu.add(newMap);
		menubar.add(fileMenu);

		JMenu optionsMenu = new JMenu("Options");
		JMenuItem filename = new JMenuItem("Change Filename");
		filename.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent arg0) {
				changeFilename();
			}});
		optionsMenu.add(filename);

		JMenuItem coords = new JMenuItem("Show tile co-ords");
		coords.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent arg0) {
				coords();
				redraw();
			}});
		optionsMenu.add(coords);

		JMenuItem showGrid = new JMenuItem("Show grid");
		showGrid.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent arg0) {
				grid();
				redraw();
			}});
		optionsMenu.add(showGrid);

		menubar.add(optionsMenu);

		setJMenuBar(menubar);
		add(viewScroll, BorderLayout.CENTER);
		buttonadder();
		JScrollPane buttonScroll = new JScrollPane(buttonPanel);
		add(buttonScroll, BorderLayout.EAST);
		add(southScroll, BorderLayout.SOUTH);

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		setVisible(true);
	}

	/**
	 * Test buttonadder
	 * This only adds buttons so far, it doesn't actually let you paint with them.
	 * you can only paint with the first 5 buttons.
	 */
	public void buttonadder(){
		int size = tsheet.size();
		for(int i = 0; i < size; i++){
			final int buttonIndex = i;
			JButton butt = new JButton(new ImageIcon(tsheet.get(i)));
			butt.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					brush = buttonIndex;
				}});
			buttonPanel.add(butt);
		}
	}

	/**
	 * Not the boolean for drawing the grid.
	 */
	protected void grid() {
		grid = !grid;
	}

	/**
	 * Not the boolean for drawing tile co ordinates.
	 */
	protected void coords() {
		coords = !coords;
	}

	/**
	 * Show filename change panel
	 */
	public void changeFilename() {
		filenamePanel = new JPanel();
		filenameField = new JTextField(20);
		filenameField.setText(mapEd.fname);
		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				mapEd.fname = filenameField.getText();
				setTitle("Nelda map editor - "+mapEd.fname);
				remove(filenamePanel);
				validate();
			}});
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent e) {
				remove(filenamePanel);
				validate();
			}});
		filenamePanel.add(filenameField);
		filenamePanel.add(ok);
		filenamePanel.add(cancel);
		add(filenamePanel, BorderLayout.NORTH);
		validate();
	}

	/**
	 * Just gets the graphics and then calls draw with it.
	 */
	protected void redraw(){
		view.setPreferredSize(new Dimension(mapEd.mapwidth*TILE_WIDTH, mapEd.mapheight*TILE_WIDTH));
		Graphics g = view.getGraphics();
		validate();
		draw(g);
	}

	/**
	 * Draws a representation of mapEd's grid with the
	 * tiles that each character corresponds to (then calls the grid drawing method)
	 * @param g The graphics object to draw to.
	 */
	protected void draw(Graphics g){
		BufferedImage bi = new BufferedImage(mapEd.mapwidth*TILE_WIDTH, mapEd.mapheight*TILE_WIDTH, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) bi.getGraphics();

		// Tile coords will be drawn in white
		g2d.setColor(Color.WHITE);

		for(int x = 0; x < mapEd.mapwidth; x++){
			for(int y = 0; y < mapEd.mapheight; y++){
				g2d.drawImage(tsheet.get(mapEd.grid[x][y]), x*TILE_WIDTH, y*TILE_WIDTH, null);
				if(coords){
					g2d.drawString(x+", "+y, x*TILE_WIDTH, (y*TILE_WIDTH)+10);
				}
			}
		}
		// draw goals
		for(Position p : mapEd.goals){
			g2d.setColor(new Color(0, 255, 0, 128));
			g2d.fillRect(p.getCol()*TILE_WIDTH, p.getRow()*TILE_WIDTH, TILE_WIDTH, TILE_WIDTH);
			g2d.setColor(Color.BLACK);
			g2d.drawString("goal", p.getCol()*TILE_WIDTH, (p.getRow()*TILE_WIDTH+10));
		}
		// draw movables
		for(MovableObject b : mapEd.boxes){
			Position p = b.getPosition();
			g2d.setColor(new Color(0, 0, 255, 128));
			g2d.fillRect(p.getCol()*TILE_WIDTH, p.getRow()*TILE_WIDTH, TILE_WIDTH, TILE_WIDTH);
			g2d.setColor(Color.WHITE);
			g2d.drawString("box", p.getCol()*TILE_WIDTH, (p.getRow()*TILE_WIDTH+10));
		}
		// draw starting position(s?)
		if(mapEd.startPos != null){
			g2d.setColor(new Color(255, 0, 0, 128));
			g2d.fillRect(mapEd.startPos.getCol()*TILE_WIDTH, mapEd.startPos.getRow()*TILE_WIDTH, TILE_WIDTH, TILE_WIDTH);
			g2d.setColor(Color.WHITE);
			g2d.drawString("start", mapEd.startPos.getCol()*TILE_WIDTH, (mapEd.startPos.getRow()*TILE_WIDTH+10));
		}
		// draw doors
		for(Door d : mapEd.doors){
			Position p = d.getPosition();
			g2d.setColor(new Color(255, 0, 255, 128));
			g2d.fillRect(p.getCol()*TILE_WIDTH, p.getRow()*TILE_WIDTH, TILE_WIDTH, TILE_WIDTH);
			g2d.setColor(Color.WHITE);
			g2d.drawString("door", p.getCol()*TILE_WIDTH, (p.getRow()*TILE_WIDTH+10));
			g2d.drawString(d.getDestination().getName(), p.getCol()*TILE_WIDTH, (p.getRow()*TILE_WIDTH+25));
		}

		// Draw Chest
		if(mapEd.chest != null){
			Position p = mapEd.chest.getPosition();
			g2d.setColor(new Color(255, 255, 0, 128));
			g2d.fillRect(p.getCol()*TILE_WIDTH, p.getRow()*TILE_WIDTH, TILE_WIDTH, TILE_WIDTH);
			g2d.setColor(Color.BLACK);
			g2d.drawString("chest", p.getCol()*TILE_WIDTH, (p.getRow()*TILE_WIDTH+10));
		}

		// Draw Trees
		for(Tree t : mapEd.trees){
			Position p = t.getPosition();
			g2d.setColor(new Color(85, 107, 47, 128));
			g2d.fillRect(p.getCol()*TILE_WIDTH, p.getRow()*TILE_WIDTH, TILE_WIDTH, TILE_WIDTH);
			g2d.setColor(Color.WHITE);
			g2d.drawString("tree", p.getCol()*TILE_WIDTH, (p.getRow()*TILE_WIDTH+10));
		}

		drawGrid(g2d);
		g.drawImage(bi, 0, 0, null);
	}

	/**
	 * Draws a black grid over the top of everything so you
	 * can tell where you're clicking.
	 * @param g The graphics object to draw to
	 */
	protected void drawGrid(Graphics g) {
		if(grid){
			g.setColor(Color.BLACK);
			for(int x = 0; x < mapEd.mapwidth; x++){
				for(int y = 0; y < mapEd.mapheight; y++){
					g.drawRect(x*TILE_WIDTH, y*TILE_WIDTH, TILE_WIDTH, TILE_WIDTH);
				}
			}
		}
	}

	/**
	 * Fill an area with the selected brush
	 */
	protected void fillArea(){
		if(brush >= 0){
			int startx = Math.min(this.startX, endX);
			int starty = Math.min(this.startY, endY);
			int endx = Math.max(this.endX, this.startX);
			int endy = Math.max(this.endY, this.startY);

			if(endy >= mapEd.mapwidth){ endX = mapEd.mapwidth-1; }
			if(endx >= mapEd.mapheight){ endY = mapEd.mapheight-1; }

			for(int x = startx; x <= endx; x++){
				for(int y = starty; y <= endy; y++){
					mapEd.grid[x][y] = brush;
				}
			}
			redraw();
		}
	}

	/**
	 * Fill a single square with the current brush
	 * (and set up clicked points for dragging)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		clickedX = e.getX();
		lastX = clickedX;
		startX = clickedX/TILE_WIDTH;

		clickedY = e.getY();
		lastY = clickedY;
		startY = clickedY/TILE_WIDTH;

		int x = e.getX()/TILE_WIDTH;
		int y = e.getY()/TILE_WIDTH;
		if(x < mapEd.mapwidth && y < mapEd.mapheight){
			if(brush >= 0){
				mapEd.grid[x][y] = brush;
			} else if (brush == -1){
				// Place goal
				Position p = new Position(x, y);
				mapEd.goals.add(p);
			} else if (brush == -2){
				// Place movable
				Position p = new Position(x, y);
				Box b = new Box(p);
				mapEd.boxes.add(b);
			} else if (brush == -3){
				Position p = new Position(x, y);
				mapEd.startPos = p;
			} else if (brush == -4){
				Position p = new Position(x, y);
				DoorDialog dd = new DoorDialog(mapEd, p);
			} else if (brush == -5){
				Position p = new Position(x, y);
				Chest c = new Chest();
				c.setPosition(p);
				mapEd.chest = c;
			} else if (brush == -6){
				Position p = new Position(x, y);
				Tree t = new Tree();
				t.setPosition(p);
				mapEd.trees.add(t);
			} else if (brush == -7){
				int toRemove = -1;
				Position p  = new Position(x, y);
				for(int i = 0; i < mapEd.trees.size(); i++){
					if(mapEd.trees.get(i).getPosition().getCol() == p.getCol()
							&& mapEd.trees.get(i).getPosition().getRow() == p.getRow()){
						toRemove = i;
						break;
					}
				}
				if(toRemove != -1){
					mapEd.trees.remove(toRemove);
				}
			}
			redraw();
		}
	}

	/**
	 * If the user is dragging the mouse and releases it,
	 * fill an area from where they started dragging to
	 * where they stopped.
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		endX = e.getX()/TILE_WIDTH;
		endY = e.getY()/TILE_WIDTH;
		if(dragging){ fillArea(); }
		dragging = false;
	}

	/**
	 * Detect if the user is dragging the mouse to draw a box.
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		dragging = true;
		lastX = e.getX();
		lastY = e.getY();
	}

	/*
	 * These aren't needed
	 */
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mouseMoved(MouseEvent e) {}
}
