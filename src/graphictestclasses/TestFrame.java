package graphictestclasses;
import game.Box;
import game.Chest;
import game.Game;
import game.Job;
import game.Maze;
import game.Player;
import game.Position;
import graphics.GameCanvas;

import java.awt.Dimension;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;


public class TestFrame extends JFrame implements Runnable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	GameCanvas gameCanvas;
	boolean running;
	Thread gameloop;


	public TestFrame(GameCanvas gc){
		this.gameCanvas = gc;
		add(gc);

		setTitle("Mygame");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(gc.getScreenWidth(), gc.getScreenHeight()));
		pack();
		setVisible(true);

		gameloop = new Thread(this);
	//	gameloop.start();


	}

	public static void main(String[] args) {



		Game game = new Game();
		Player p = new Player("Player 1", Job.Soldier());
		Player p2 = new Player("Player 2", Job.Soldier());
		game.addPlayer(p);

		game.addPlayer(p2);
		// player 2
		p2.setPosition(game.getWorld().getPosition(38,38));
		game.getWorld().getPosition(38, 38).setGameObject(p2);
	//	game.
		// player 1

//		p.setPosition(game.getWorld().getPosition(5, 5));
//		game.getWorld().getPosition(5, 5).setGameObject(p);
//		p.setWorld(game.getWorld());
		GameCanvas gc = new GameCanvas(game, p);
		TestFrame testo = new TestFrame(gc);

		Controller c = new Controller(gc);
		gc.setController(c);
		testo.addKeyListener(c);
		testo.startLoop();
	}

	public void startLoop(){
		gameloop.start();
	}

	@Override
	public void run() {
		running= true;
		Thread t = Thread.currentThread();

		while(running){
			  try {
	                Thread.sleep(30);
	            }
	            catch (InterruptedException e) {
	                e.printStackTrace();
	            }
			gameCanvas.checkForInput();
			gameCanvas.render();

		}

	}
}
