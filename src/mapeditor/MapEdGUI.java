package mapeditor;

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
import java.util.Map;

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
 * GUI for the Nelda map editor
 * @author Christian Evaroa
 *
 */
public class MapEdGUI extends JFrame implements MouseListener, MouseMotionListener {

	private MapEd mapEd;
	private JScrollPane viewScroll;
	private JPanel buttonPanel;
	private JComponent view;
	private JMenuBar menubar;
	private JPanel filenamePanel;
	private JTextField filenameField;
	private int TILE_WIDTH = 32;
	protected char brush = 'G';
	private boolean dragging;
	private boolean coords;
	private boolean grid = true;
	private Map<Integer, Character> brushmap;

	int clickedX;
	int clickedY;
	int lastX;
	int lastY;

	int startX;
	int startY;
	int endX;
	int endY;

	TileSheet tsheet = new TileSheet("assets/sheet2.png", 32, 32);

	/**
	 * Constructor for MapEdGUI
	 * A GUI for the Map editor in case you couldn't tell.
	 * @param ed The MapEd that this MapEdGUI belongs to.
	 */
	public MapEdGUI(MapEd ed){ 
		super("Nelda Map Editor");
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
		view.setPreferredSize(new Dimension(mapEd.mapwidth*32, mapEd.mapheight*32));
		viewScroll = new JScrollPane(view);
		view.addMouseListener(this);
		view.addMouseMotionListener(this);

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

		JButton grassButton = new JButton(new ImageIcon(tsheet.get(0)));
		grassButton.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent arg0) {
				brush = 'G';
			}});
		buttonPanel.add(grassButton);

		JButton tButton = new JButton(new ImageIcon(tsheet.get(1)));
		tButton.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent arg0) {
				brush = 'T';
			}});
		buttonPanel.add(tButton);

		JButton pathButton = new JButton(new ImageIcon(tsheet.get(2)));
		pathButton.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent arg0) {
				brush = 'P';
			}});
		buttonPanel.add(pathButton);
		JButton lWallButton = new JButton(new ImageIcon(tsheet.get(7)));
		lWallButton.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent arg0) {
				brush = 'q';
			}});
		buttonPanel.add(lWallButton);

		JButton rButton = new JButton(new ImageIcon(tsheet.get(8)));
		rButton.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent arg0) {
				brush = 'R';
			}});
		buttonPanel.add(rButton);
		JButton rWallButton = new JButton(new ImageIcon(tsheet.get(9)));
		rWallButton.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent arg0) {
				brush = 'r';
			}});
		buttonPanel.add(rWallButton);
		JButton lRoofButton = new JButton(new ImageIcon(tsheet.get(13)));
		lRoofButton.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent arg0) {
				brush = 't';
			}});
		buttonPanel.add(lRoofButton);
		JButton roofButton = new JButton(new ImageIcon(tsheet.get(14)));
		roofButton.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent arg0) {
				brush = 'u';
			}});
		buttonPanel.add(roofButton);
		JButton rRoofButton = new JButton(new ImageIcon(tsheet.get(15)));
		rRoofButton.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent arg0) {
				brush = 'v';
			}});
		buttonPanel.add(rRoofButton);
		
		
		JButton doorButton = new JButton(new ImageIcon(tsheet.get(19)));
		doorButton.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent arg0) {
				brush = 'D';
			}});
		buttonPanel.add(doorButton);
		JButton windowButton = new JButton(new ImageIcon(tsheet.get(22)));
		windowButton.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent arg0) {
				brush = 'w';
			}});
		buttonPanel.add(windowButton);
		
		
		
		
		
		
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
			JButton butt = new JButton(new ImageIcon(tsheet.get(i)));
			butt.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					
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
		view.setPreferredSize(new Dimension(mapEd.mapwidth*32, mapEd.mapheight*32));
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
		BufferedImage bi = new BufferedImage(mapEd.mapwidth*32, mapEd.mapheight*32, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) bi.getGraphics();

		// Tile coords will be drawn in white
		g2d.setColor(Color.WHITE);
		
		for(int x = 0; x < mapEd.mapwidth; x++){
			for(int y = 0; y < mapEd.mapheight; y++){
				switch (mapEd.grid[x][y]){
				case '\0':
					//Do nothing
					break;
				case 'T':
					g2d.drawImage(tsheet.get(1), x*TILE_WIDTH, y*TILE_WIDTH,null);
					break;
				case 'P':
					g2d.drawImage(tsheet.get(2), x*TILE_WIDTH, y*TILE_WIDTH,null);
					break;
				case 'q':
					g2d.drawImage(tsheet.get(7), x*TILE_WIDTH, y*TILE_WIDTH,null);
					break;
				case 'R':
					g2d.drawImage(tsheet.get(8), x*TILE_WIDTH, y*TILE_WIDTH,null);
					break;
				case 'r':
					g2d.drawImage(tsheet.get(9), x*TILE_WIDTH, y*TILE_WIDTH,null);
					break;
				case 'D':
					g2d.drawImage(tsheet.get(19), x*TILE_WIDTH, (y*TILE_WIDTH)-TILE_WIDTH,null);
					break;
				case 'G':
					g2d.drawImage(tsheet.get(0), x*TILE_WIDTH, y*TILE_WIDTH,null);
					break;
				case 't':
					g2d.drawImage(tsheet.get(13), x*TILE_WIDTH, y*TILE_WIDTH - (TILE_WIDTH *2),null);
					// left roof
					break;
				case 'u':
					//center roof
					g2d.drawImage(tsheet.get(14), x*TILE_WIDTH, y*TILE_WIDTH - (TILE_WIDTH *2),null);
					break;
				case 'v':
					//right roof
					g2d.drawImage(tsheet.get(15), x*TILE_WIDTH, y*TILE_WIDTH - (TILE_WIDTH *2),null);
					break;
				case 'w':
					g2d.drawImage(tsheet.get(22), x*TILE_WIDTH, y*TILE_WIDTH,null);
					break;
				}	
				if(coords){
					g2d.drawString(x+", "+y, x*32, (y*32)+10);
				}
			}
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
					g.drawRect(x*32, y*32, TILE_WIDTH, TILE_WIDTH);
				}
			}
		}
	}

	/**
	 * Fill an area with the selected brush
	 */
	protected void fillArea(){
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

	/**
	 * Fill a single square with the current brush
	 * (and set up clicked points for dragging)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		clickedX = e.getX();
		lastX = clickedX;
		startX = clickedX/32;

		clickedY = e.getY();
		lastY = clickedY;
		startY = clickedY/32;

		int x = e.getX()/32;
		int y = e.getY()/32;
		if(x < mapEd.mapwidth && y < mapEd.mapheight){
			mapEd.grid[x][y] = brush;
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
		endX = e.getX()/32;
		endY = e.getY()/32;
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
