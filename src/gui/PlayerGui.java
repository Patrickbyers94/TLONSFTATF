package gui;

import game.Game;
import game.GameObject;
import game.Job;
import game.Player;
import game.Time;
import game.Position;
import graphics.GameCanvas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;


import networking2.Client;

public class PlayerGui implements Runnable {
	private JFrame frame;
	private JPanel mainPanel, chatPanel;

	private GameCanvas world;
	private JMenuBar menuBar;
	private JMenu fileMenu, optionMenu, helpMenu, rules;
	private JMenuItem menuItem;
	private Client client;
	private JTextArea log, chat;
	private BorderLayout manager;
	// private XMLmanager xMan = new XMLmanager(null);

	private Movement movement;
	private static Job job = Job.Soldier();
	private boolean running;

	private Thread gameloop;
	private Player p;
	private Game game;
	private SaveDialog sd; // Just for integration to demonstrate saving
	public InetAddress chosenServer;
	private Time time;
	private InventoryPanel inv;

	public PlayerGui() {
		client = new Client();
		//selectServer();
		game = client.getGameState();
		time = game.getTime();
		Player c = null;
		while (c == null) {
			c = enterNames();
		}
		p = c;
		client.connect(p);
		int ran1 = (int) (Math.random() * game.getWorld().getGrid().length);
		int ran2 = (int) (Math.random() * game.getWorld().getGrid().length);
		game.addPlayer(p);
		gameloop = new Thread();
		client.sendJoinMessage(p);
		movement = new Movement();
		setupGui();
		setupGameLoop();
		world.render();

	}

	public void setupGameLoop() {
		gameloop = new Thread(this);
		gameloop.start();

	}

	/**
	 * \ create a new frame and fill it with the game elements
	 */
	public void setupGui() {
		frame = new JFrame("The Legend of Nelda - Search for the anti triforce");
		frame.setName("The Legend of Nelda - Search for the anti triforce");
		frame.setSize(1000, 800);

		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				client.disconnectFromServer();
				System.exit(0);
			}
		});

		mainPanel = new JPanel();

		//mainPanel.setLayout(new BorderLayout());
		BoxLayout box = new BoxLayout(mainPanel, BoxLayout.LINE_AXIS);
		mainPanel.setLayout(box);

	//	mainPanel.setComponentOrientation(java.awt.ComponentOrientation.RIGHT_TO_LEFT);

		// setup the main pane as a gameCanvas
		world = new GameCanvas(game, p);
		world.setPreferredSize(new Dimension(600, 600));

		mainPanel.add(world, BorderLayout.CENTER);

		mainPanel.add(inv = new InventoryPanel(this.p));
	//	mainPanel.setSize(new Dimension(800, 600));
		System.out.println("Main panel  " + mainPanel.getSize());
		// setup the chat log

		setUpMenuBars();
	//	initChatPanel();


		// add mouseListener to respond to the user clicking on a position
		world.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				// do something
			}
		});
		world.setFocusable(true);
		world.addKeyListener(new WorldListener(game, world, p, movement, PlayerGui.this));

		frame.add(mainPanel);
		frame.setVisible(true);

	}

	private void initChatPanel(){

		chatPanel = new JPanel();
		BoxLayout boxlayout = new BoxLayout(chatPanel, BoxLayout.PAGE_AXIS );
		chatPanel.setLayout(boxlayout);

		log = new JTextArea();

		log.setRows(8);
		log.setEditable(false);
		JScrollPane scroll = new JScrollPane(log);

		chatPanel.add(scroll);


		// setup the chat bar
		chat = new JTextArea();
		//chat.setBackground(Color.RED);
		chat.setRows(1);
		Border chatBorder = BorderFactory.createLineBorder(Color.BLACK);
		chat.setBorder(BorderFactory.createCompoundBorder(chatBorder,
		            BorderFactory.createEmptyBorder(2, 2, 2, 2)));
		chatPanel.add(chat);
		chat.addKeyListener(new ChatListener(log, chat));
		mainPanel.add(chatPanel, BorderLayout.SOUTH);

	}

	private void setUpMenuBars() {
		// setup menu bar
		menuBar = new JMenuBar();
		// setup file menu
		fileMenu = new JMenu("File");

		// setup save menu option
		menuItem = new JMenuItem("Leave Game(L)");
		menuItem.getAccessibleContext().setAccessibleDescription(
				"Leave the current game");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				client.disconnectFromServer();
			}
		});
		fileMenu.add(menuItem);

		menuItem = new JMenuItem("Save Game");
		menuItem.getAccessibleContext().setAccessibleDescription(
				"Save the current game");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				sd = new SaveDialog(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						game.save(sd.getFilename());
						sd.dispose();
					}
				});

			}
		});
		fileMenu.add(menuItem);

		menuItem = new JMenuItem("Add player");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				Player p2 = new Player(Double.toString(Math.random()), Job
						.Soldier());
				game.addPlayer(p2);
			}
		});
		fileMenu.add(menuItem);

		menuItem = new JMenuItem("Load");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				File load;
				JFileChooser fc = new JFileChooser("holla");
				if (fc.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
				} else {
					String playerName = p.getName();
					load = fc.getSelectedFile();
					String prefix = load.getName().substring(0,
							load.getName().length() - 12);
					System.out.println("FILE IS :::: " + prefix);
					game.load(prefix);
					Player tempPlayer = p;
					for(Player player : game.getPlayers()){
						if(player.getName().equals(playerName)){
							tempPlayer = player;
						}
					}
					p = tempPlayer;
					game.addPlayer(p);
					frame.validate();
					world.setPlayer(p);
					world.render();
				}
			}
		});
		fileMenu.add(menuItem);

		// setup Settings option
		// menuItem = new JMenuItem("Options(O)");
		// menuItem.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent ev) {
		// // TODO display window to change game settings (NOT AT ALL SURE
		// // WHAT THESE SETTINGS WOULD BE)
		// }
		// });
		// fileMenu.add(menuItem);

		fileMenu.addSeparator();

		// setup Exit menu option
		menuItem = new JMenuItem("Exit (ESC) ");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				endProgram();
			}
		});
		fileMenu.add(menuItem);
		menuBar.add(fileMenu);
		frame.setJMenuBar(menuBar);
	}

	/**
	 * end the program
	 */
	protected void endProgram() {
		client.disconnectFromServer();
		System.exit(0);
	}

	public void inventoryUpdate(){
		inv.repaint();
	}

	public static void main(String[] args) {
		new PlayerGui();
	}

//	/**
//	 * register keys pressed
//	 */
//	public void keyPressed(KeyEvent arg0) {
//		int keyCode = arg0.getKeyCode();
//		switch (keyCode) {
//		// left
//		case KeyEvent.VK_LEFT:
//			left = true;
//			break;
//		// right
//		case KeyEvent.VK_RIGHT:
//			right = true;
//			break;
//		case KeyEvent.VK_DOWN:
//			down = true;
//			break;
//		case KeyEvent.VK_UP:
//			up = true;
//			break;
//		case KeyEvent.VK_N:
//			world.toggleNightTime();
//			break;
//		default:
//			break;
//		}
//	}
//
//	/**
//	 * register keys released
//	 */
//	public void keyReleased(KeyEvent arg0) {
//		int keyCode = arg0.getKeyCode();
//		switch (keyCode) {
//		// left
//		case KeyEvent.VK_LEFT:
//			left = false;
//
//			// movePlayer();
//			break;
//		// right
//		case KeyEvent.VK_RIGHT:
//			right = false;
//			// movePlayer();
//			break;
//		case KeyEvent.VK_DOWN:
//			down = false;
//			// movePlayer();
//			break;
//		case KeyEvent.VK_UP:
//			up = false;
//			// movePlayer();
//			break;
//		case KeyEvent.VK_D:
//			world.toggleDebugMode();
//			break;
//		case KeyEvent.VK_M:
//			world.toggleMiniMap();
//			break;
//		case KeyEvent.VK_A:
//			game.attack(p);
//			break;
//		case KeyEvent.VK_F:
//			world.toggleFlashLight();
//			break;
//		case KeyEvent.VK_X:
//			System.out.println(p.getWorld() + " " + p.getPosition());
//			break;
//		case KeyEvent.VK_Z:
//			System.out.println(p.getName() + " is a(n) " + p.getJob());
//		case KeyEvent.VK_I:
//			game.inspect(p);
//			break;
//		case KeyEvent.VK_ENTER:
//			// send Chat message across server
//			log.append(chat.getText() + "\n");
//		default:
//			break;
//		}
//	}
//
//	public void keyTyped(KeyEvent arg0) {
//		// TODO Auto-generated method stub
//
//	}

	/**
	 * check the keys pressed and move in that direction
	 */
	public void movePlayer() {
		Game g = client.getGameState(); // these should be in run() I think
		// i check the old positions and then reference the new position to stop the moveplayer spamming the client
		// at the moment it sends the client moveplayer every tick of the update
		int oldCol = p.getPosition().getCol();
		int oldRow = p.getPosition().getRow();
		if (movement.isUp()) {
			g.movePlayer(p, 1);
		}
		if (movement.isRight()) {
			g.movePlayer(p, 2);
		}
		if (movement.isDown()) {
			g.movePlayer(p, 3);
		}
		if (movement.isLeft()) {
			g.movePlayer(p, 4);
		}
		// if they have changed then move the player
		if(oldCol!=p.getPosition().getCol() || oldRow != p.getPosition().getRow()){
			client.playerMove(p, p.getWorld());// these should be in run() I think
		}
		// world.render();

		checkMoving();
	}

	public void attack(){
		// SUper hacky attack method. So hacky it hurts - Chris
		Game g = client.getGameState();
		Position o = g.attack(p);

		client.playerAttack(p, o, p.getDamage());

	}

	private void menuBars(){






	}


	public void otherActions(){
		Game g = client.getGameState();
	}


	public void checkMoving() {
		if (movement.isMoving()) {
			p.setMoving(true);
		} else {
			p.setMoving(false);
		}
	}

	@Override
	public void run() {

		running = true;
		Thread t = Thread.currentThread();

		while (running) {
			try {
				movePlayer();
				time.update();
		//		otherActions();
				world.render();
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}

	public void checkForInput() {

	}

	public Player enterNames() {
		JPanel panel = new JPanel();
		panel.setLayout((LayoutManager) new BoxLayout(panel,
				BoxLayout.PAGE_AXIS));

		// player names

		JLabel label = new JLabel("Choose a name and job");
		JTextField input = new JTextField(10);
		panel.add(label);
		panel.add(input);

		JRadioButton Soldier = new JRadioButton("Soldier");
		Soldier.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				job = Job.Soldier();
			}
		});
		JRadioButton Archer = new JRadioButton("Archer");
		Archer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				job = Job.Archer();
			}
		});
		ButtonGroup group = new ButtonGroup();
		group.add(Soldier);
		group.add(Archer);
		panel.add(Soldier);
		panel.add(Archer);
		Soldier.setSelected(true);
		int value = JOptionPane.showConfirmDialog(frame, panel,
				"Character Creation", JOptionPane.OK_CANCEL_OPTION);
		if (value == JOptionPane.OK_OPTION && checkName(input.getText())
				&& !input.getText().isEmpty()) {
			return new Player(input.getText(), job);
		} else if (value == JOptionPane.CANCEL_OPTION) {
			endProgram();
		} else if (value == JOptionPane.CLOSED_OPTION) {
			endProgram();
		}
		return null;
	}

	private boolean checkName(String name) {
		List<Player> players = client.getGameState().getPlayers();
		for (Player p : players) {
			if (p.getName().toLowerCase().equals(name.toLowerCase())) {
				return false;
			}
		}
		return true;

	}

	private void selectServer(){
		final List<InetAddress> serverAddress = client.getServers();
		System.out.println("----------------START OF SERVER SELECTION-------------------");
		List<String> stringAdd = new ArrayList<String>();
		for(InetAddress i : serverAddress){
			stringAdd.add(i.toString());
		}
		System.out.println("address number  is "+stringAdd.size());
		System.out.println(stringAdd.toString());
		String[] stringAddress = new String[stringAdd.size()];
		for(int i = 0;i<stringAdd.size();i++){
			stringAddress[i] = stringAdd.get(i);
		}
		JComboBox serversList = new JComboBox(stringAddress);
		serversList.setSelectedIndex(0);
		serversList.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e){
				JComboBox cb = (JComboBox) e.getSource();
				int selectedIndex = cb.getSelectedIndex();
				chosenServer = serverAddress.get(selectedIndex);
			}

		});
		int value = JOptionPane.showConfirmDialog(frame, serversList,
				"Server Selection", JOptionPane.OK_CANCEL_OPTION);
		if (value == JOptionPane.OK_OPTION){
			System.out.println("Setting server");
			client.setServer(chosenServer);
		}
	}


}


