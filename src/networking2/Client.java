package networking2;

import game.Game;
import game.GameObject;
import game.Item;
import game.Job;
import game.Monster;
import game.MovableObject;
import game.NPC;
import game.Player;
import game.Position;
import game.Weapon;
import game.World;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import javax.swing.JOptionPane;

import networking2.ServerFinder;

public class Client {

	private ClientConnection connection;
	private boolean joined = false;
	private Game gameState = new Game();
	private Player localPlayer;
	private InetAddress serverAddress;


	public static void main(String[] args) {
		Client test = new Client();
		Game testGame = test.getGameState();
		Player fred = new Player("Fred", new Job("a", 0, 0, 0));
		fred.setPosition(new Position(10, 10));
		test.connect(fred);
		testGame.addPlayer(fred);
		// test.playerMove(testGame);
	}

	public Client() {
		findServer();
	}

	/**
	 * creates a new ServerFinder object to allow the user to choose which
	 * server to play on
	 */
	private void findServer(){
		ServerFinder s = new ServerFinder();
		//wait until the user has chosen a server, change this to when button is clicked to join server then setup connection to server
		while(s.getServerAddress() == null){}
		serverAddress = s.getServerAddress();
		System.out.println("Server Address: " + serverAddress);

	}

	public Player connect(Player p) {

		try {
			while (serverAddress == null) {
			}
			Socket s = new Socket(serverAddress, Server.ServerListener.PORT);
			connection = new ClientConnection(s, this);
			localPlayer = p;
			return localPlayer;
		} catch (IOException e) {
			System.out.println("Error connecting client");
			e.printStackTrace();
		}
		return null;
	}

	public void setPlayer(Player p){
		localPlayer = p;
	}

	public synchronized void messageRecived(String message) {
		String[] messageArray = message.split(",");
		// System.out.println(">>>>>"+message); Prints out the received message,
		// use for debugging
		if (messageArray[0].equals("JOINED_SERVER")) {
			if(messageArray.length > 1)
				System.out.println(messageArray[0] + " " + messageArray[1]);
			joined = true;
			for(int i = 1; i < messageArray.length; i++){
				String[] player = messageArray[i].split(";");
				System.out.println("Player Name: " + player[0]);
				if(player[0].equals(localPlayer.getName())){
					System.out.println("got a player with the same name as me back from the server");
					localPlayer.setPosition(new Position(Integer.parseInt(player[1]), Integer.parseInt(player[2])));
					localPlayer.setOrientation(Integer.parseInt(player[3]));
					localPlayer.setHealth(Integer.parseInt(player[6]));
					localPlayer.setWorld(gameState.getWorlds().get(player[7]));
					localPlayer.setMoney(Integer.parseInt(player[8]));
					localPlayer.setWeapon(new Weapon(player[5], Integer.parseInt(player[9])));
				}
				else{
					//the string now contains online and offline players which need to be dealt with seperately
					//so that they can be added to the appropriate list in the gameState.
					if(player.length > 9){
					Player p = null;
					if (player[4].equals("Soldier"))
						p = new Player(player[0], Job.Soldier());
					if (player[4].equals("Archer"))
						p = new Player(player[0], Job.Archer());
					p.setPosition(new Position(Integer.parseInt(player[1]), Integer.parseInt(player[2])));
					p.setOrientation(Integer.parseInt(player[3]));
					p.setHealth(Integer.parseInt(player[6]));
					p.setWorld(gameState.getWorlds().get(player[7]));
					p.setMoney(Integer.parseInt(player[8]));
					p.setWeapon(new Weapon(player[5], Integer.parseInt(player[9])));
					gameState.addPlayer(p);
					for(Player p1 : gameState.getOnlinePlayers()){
						System.out.println("Player: "+p1.getName());
					}
					}
				}
			}
		}
		else if (message.equals("FULL_SERVER,")) {
			JOptionPane.showMessageDialog(null, "Server Full");
			System.exit(0);
		}
		else if(messageArray[0].equals("LOADED_GAME")){
			String[] gameData = messageArray[1].split("#");
			String[] players = gameData[0].split("$");
			String[] items = gameData[1].split(";");
			for(int i = 0; i < players.length; i++){
				String[] player = players[i].split(";");

			}
		}
		else if (messageArray[0].equals("PLAYER_JOINED")) {
			//System.out.println("DO I GET HERE?");
			System.out.println("PLAYER JOINED MESSAGE: "+message);
			String[] temp = messageArray[1].split(";");
			Player player = null;
			if (temp[3].equals("Soldier"))
				player = new Player(temp[0], Job.Soldier());
			if (temp[3].equals("Archer"))
				player = new Player(temp[0], Job.Archer());
			player.setPosition(new Position(Integer.parseInt(temp[1]), Integer
					.parseInt(temp[2])));
			System.out.println("Player joined: " + player);
			/** Begin test code **/
			gameState.addPlayer(player);
			World playerWorld = gameState.getWorlds().get(player.getWorld().getName());
			Position pos = player.getPosition();
			Position newP = playerWorld.getPosition(pos.getRow(), pos.getCol());
			pos.setGameObject(null);
			newP.setGameObject(player);
			player.setPosition(newP);
			/** End test code **/
			for(Player p1 : gameState.getOnlinePlayers()){
				System.out.println("Player: "+p1.getName());
			}
			//connection.sendMessage("PLAYER_NOTIFY," + player.getName()+";"+player.getPosition().getCol()+";"+player.getPosition().getRow()+  ";" + player.getJob().getName());
		}
		else if(messageArray[0].equals("PLAYER_NOTIFY")) {
			System.out.println("DO I GET HERE V2?");
			String[] temp = messageArray[1].split(";");
			Player fred = new Player(temp[0], Job.Soldier());
			fred.setPosition(new Position(Integer.parseInt(temp[1]), Integer
					.parseInt(temp[2])));
			System.out.println("Player in game before i was" + fred.getName());
			//gameState.addPlayer(fred);
		}
		else if (messageArray[0].equals("UPDATE_TIME")) {
			// need a method to update the game time for this client
		}
		else if (messageArray[0].equals("NEW_ITEM")) {
			int row = Integer.parseInt(messageArray[3]);
			int col = Integer.parseInt(messageArray[2]);
			Item temp;
			for (GameObject g : gameState.getWorld().getAllGameObjects()) {
				if (g.getPosition().getRow() == row
						&& g.getPosition().getCol() == col && g instanceof Item) {
					temp = (Item) g;
					for (Player p : gameState.getOnlinePlayers()) {
						if (p.getName().equals(messageArray[1]))
							temp.getPickedUp(p);
					}
				}
			}
		} else if (messageArray[0].equals("PLAYER_ATTACK")) {
			/*
			 * HI PETER I DID THIS - CHRIS
			 */
			String[] temp = messageArray[1].split(";");
			for (Player p : gameState.getOnlinePlayers()) {
				int prow = p.getPosition().getRow();
				int pcol = p.getPosition().getCol();
				if(pcol==Integer.parseInt(temp[1]) && prow == Integer.parseInt(temp[2]) ){
					//localPlayer.takeHit(Integer.parseInt(temp[3]));
					p.attack();
				}
			}


		} else if (messageArray[0].equals("MONSTER_ATTACK")) {
			Monster m = null;
			for (GameObject g : gameState.getWorld().getAllGameObjects()) {
				// TODO Monsters are random
			}
		} else if (messageArray[0].equals("NPC_MOVE")) {
			NPC n = null;
			int row = Integer.parseInt(messageArray[3]);
			int col = Integer.parseInt(messageArray[2]);
			for (GameObject g : gameState.getWorld().getAllGameObjects()) {
				if (g.getPosition().getCol() == col
						&& g.getPosition().getRow() == row) {
					n = (NPC) g;
					// NPC move method is still random
				}
			}
		} else if (messageArray[0].equals("PLAYER_MOVE")) {
			String[] temp = messageArray[1].split(";");
			int col = Integer.parseInt(temp[1]);
			int row = Integer.parseInt(temp[2]);
			for(Player p : gameState.getOnlinePlayers()){
				if(p.getName().equals(temp[0])){
//					if(gameState.getWorld().getGrid()[col][row].hasMovableObject()){
//						MovableObject object = null;
//						for(GameObject g : gameState.getWorld().getAllGameObjects()){
//							if(g.getPosition().getCol() == col && g.getPosition().getRow() == row){
//								object = (MovableObject) g;
//							}
//						}
//						World w = gameState.getWorlds().get(temp[4]);
//						w.moveGameObject(object, Integer.parseInt(temp[3]));
//					}
					//					p.getPosition().setCol(Integer.parseInt(temp[1]));
					//					p.getPosition().setRow(Integer.parseInt(temp[2]));
					p.setOrientation(Integer.parseInt(temp[3]));
					gameState.movePlayer(p, Integer.parseInt(temp[3])); // move the other player in your game
					if(p.getPosition().getCol() != Integer.parseInt(temp[1]) || p.getPosition().getCol() != Integer.parseInt(temp[2])){
						// Check if the player's position in your game is where they think they should be (coordinates from message sent)
						// If they don't match up, put them into the position where they think they are and clean up after them
						int posX = Integer.parseInt(temp[1]);
						int posY = Integer.parseInt(temp[2]);
						// remove the player from the position where your game thought they were
						p.getPosition().setGameObject(null);
						// get the position in YOUR game where the other player thinks they should be and put them into it
						Position updatePos = gameState.getWorlds().get(p.getWorld().getName()).getPosition(posY, posX);
						p.setPosition(updatePos);
						updatePos.setGameObject(p);
					}
				}
			}
		}
		else if (messageArray[0].equals("CHANGE_WORLD")) {
			Player p = null;
			for (Player p1 : gameState.getOnlinePlayers()) {
				if (p1.getName().equals(messageArray[1]))
					p = p1;
			}
			//p.setWorld(gameState.getWorlds().get(messageArray[2]));
			gameState.getWorlds().get(messageArray[2]).acceptPlayer(p);
		}

	}

	public synchronized void connectionRemotlyClosed() {
		// TODO implement this
		JOptionPane.showMessageDialog(null, "Server Closed");
		System.exit(0);
	}

	public void sendJoinMessage(Player p) {
		connection.sendMessage("PLAYER_JOINED," + p.getName() + ";"
				+ p.getPosition().getCol() + ";" + p.getPosition().getRow()
				+ ";" + p.getJob().getName());
	}

	/**
	 * send a message to the server with the players new position
	 * @param s
	 */
	public synchronized void playerMove(Player p, World w){
		String message = "PLAYER_MOVE," + p.getName() +";" + p.getPosition().getCol() + ";" + p.getPosition().getRow() + ";" + p.getOrientation() + ";" + w.getName();
		connection.sendMessage(message);
	}

	public synchronized void updateTime(long time) {
		String message = "UPDATE_TIME," + Long.toString(time);
		connection.sendMessage(message);
	}

	public synchronized void pickUpItem(Player p1, Item i) {
		String message = "NEW_ITEM," + p1.getName() + ","
				+ i.getPosition().getCol() + "," + i.getPosition().getRow();
		connection.sendMessage(message);
	}

	public synchronized void playerAttack(Player p1, Position pos, int damage) {
		String message = "PLAYER_ATTACK," + p1.getName() + ";" + pos.getCol() + ";" + pos.getRow() + ";" + damage ;
		connection.sendMessage(message);
	}

	public synchronized void attackedByMonster(Player p1, Monster m) {
		String message = "MONSTER_ATTACK," + p1.getName() + "," + m.getName();
		connection.sendMessage(message);
	}

	public synchronized void moveNPC(NPC n, Position p) {
		String message = "NPC_MOVE," + n.getName() + "," + p.getCol() + ","
				+ p.getRow();
		connection.sendMessage(message);
	}

	public synchronized void changeWorld(Player p, World w) {
		String message = "CHANGE_WORLD," + p.getName() + "," + w.getName();
		connection.sendMessage(message);
	}

	private String createPlayersString() {
		String players = "";
		for (Player p : gameState.getOnlinePlayers()) {
			// System.out.println(p.getName());
			players += p.getName() + ";" + p.getPosition().getCol() + ";"+ p.getPosition().getRow() + ";" + p.getOrientation() + ",";
		}
		// System.out.println("Players: " + players);
		return "PLAYER_MOVE," + players;
	}

	public void disconnectFromServer() {
		connection.closeLocaly();
	}

	public boolean connectedToServer() {
		return joined;
	}

	public Game getGameState() {
		return gameState;
	}

	public void setServer(InetAddress i){
		serverAddress = i;
	}

	public List<InetAddress> getServers() {
		//return servers;
		return null;	//for compilation
	}

	class ClientConnection extends Thread {
		public static final String TERMINATE_MESSAGE = "#TERMINATE#"; // never send this message from your program... internal network use only
		private DataInputStream input;
		private DataOutputStream output;
		private Socket socket;
		private Client client;
		private int connectionID = -1;

		public int getConnectionID() {
			return connectionID;
		}

		private boolean running = true;

		public ClientConnection(Socket s, Client master) {
			socket = s;
			client = master;
			try {
				input = new DataInputStream(socket.getInputStream());
				output = new DataOutputStream(socket.getOutputStream());
				connectionID = input.readInt();
				start();
			} catch (IOException e) {
				System.err.println("Failed to start client connection");
				e.printStackTrace();
			}
		}

		public void sendMessage(String message) { // TODO method you will need
			// to use
			byte[] bytes = message.getBytes();
			try {
				output.writeInt(bytes.length);
				output.write(bytes);
				output.flush();
			} catch (IOException e) {
				System.err.println("Error sending message: " + message);
			}

		}

		public void closeLocaly() {//	System.out.println(player.getWorld().getName().equals(p.getWorld().getName())); TODO method you will need to use
			running = false;
			sendMessage(TERMINATE_MESSAGE);
		}

		private String lastMessageRecived = "";

		public void run() {
			while (!lastMessageRecived.equals(TERMINATE_MESSAGE)) {
				try {
					int messageSize = input.readInt();
					byte[] bytes = new byte[messageSize];
					input.readFully(bytes);
					lastMessageRecived = new String(bytes);
					if (lastMessageRecived.equals(TERMINATE_MESSAGE) && running) {
						sendMessage(TERMINATE_MESSAGE); // we check running so
						// if we were the one
						// that sent the
						// message, we dont want
						// to send a second
						// disconnect
						client.connectionRemotlyClosed();
					} else {
						client.messageRecived(lastMessageRecived);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			try {
				input.close();
				output.close();
				socket.close();
			} catch (Exception e) {
			} // we are closing and destroying, who cares if there is a problem
		}
	}

	//public void setChosenServer(InetAddress c) {
	//	s.setChosenServer(c);
	//	this.chosenServer = c;
	//}

	//	public InetAddress getChosen(){
	//	return s.getServerAddress();
	//}
}

/*class serverFindThread extends Thread {
	Client client;

	public serverFindThread(Client c) {
		client = c;
	}

	public void run() {
		while (client.s.getServerAddress() == null) {
			try {
<<<<<<< .mine
				this.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
=======
			input.close();
			output.close();
			socket.close();
			} catch (Exception e) {} //we are closing and destroying, who cares if there is a problem
=======
				input.close();
				output.close();
				socket.close();
			} catch (Exception e) {
			} // we are closing and destroying, who cares if there is a problem
		}
	}

	public void setChosenServer(InetAddress c) {
		s.setChosenServer(c);
		this.chosenServer = c;
	}

	public InetAddress getChosen(){
		return s.getServerAddress();
	}
}

class serverFindThread extends Thread {
	Client client;

	public serverFindThread(Client c) {
		client = c;
	}

	public void run() {
		while (client.s.getServerAddress() == null) {
			try {
				this.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
>>>>>>> .r288
>>>>>>> .r283
		}
		client.serverAddress = client.s.getServerAddress();
		System.out.println(client.serverAddress);
	}
}*/
