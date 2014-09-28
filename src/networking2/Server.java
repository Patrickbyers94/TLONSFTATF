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
import game.World;

import java.awt.BorderLayout;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import javax.swing.*;



public class Server {

	private Game gameState = new Game();

	private HashMap<Integer, ServerConnection> connections = new HashMap<Integer, ServerConnection>();
	private int numOfConnectionAttempts = 0; //this is used to generate connection id's
	public int getNextConnectionID() { numOfConnectionAttempts++; return numOfConnectionAttempts; }
	private ServerListener listener;
	private boolean gameLoaded = false;


	private JTextArea text;


	public static void main(String[] args){
		JFrame frame = new JFrame("lol");
		frame.setSize(200,200);
		frame.setLayout(new BorderLayout());
		JTextArea area = new JTextArea();
		frame.add(area, BorderLayout.CENTER);
		frame.setVisible(true);
		new Server(area);
	}



	public Server(JTextArea t) {
		text = t;
		discoveryServer();
		listener = new ServerListener(this);
	}

	/**
	 * runs a seperate server on a different thread for network discovery
	 */
	private void discoveryServer(){
		Thread server = new Thread(new BroadcastServerRunnable(text));
		server.start();
	}

	/**
	 * called when a message is received from a client, it then processes the message for its gamestate
	 * then sends the message to all the other clients
	 * @param connectionID
	 * @param message
	 */
	public synchronized void messageRecived(int connectionID, String message) {
		//TODO implement this
		processMessage(message);
		//text.append("\n"+connectionID+": "+message);
		for(ServerConnection con : connections.values()) {
			if(con.getConnectionID() != connectionID) {
				con.sendMessage(message);
			}
		}
		//System.out.println("Updated OTher Players: " + message);

	}

	/**
	 * called when a new client tries to connect to the server
	 * replies if the server is full or not, if necessary then adds the connection to the list of client connections
	 * if a game has been loaded from a file, sends the data to the client.
	 * @param connectionID
	 * @param con
	 */
	public void newConnection(int connectionID, ServerConnection con) {
		if(connections.size() > 5) {
			con.sendMessage("FULL_SERVER,");
			con.closeLocaly();
			return;
		}
		con.sendMessage("JOINED_SERVER," + createGameString());
		if(gameLoaded){
			String message = "LOADED_GAME," + createGameString();
			con.sendMessage(message);
		}

		text.append("\n"+getClass().getName() + ">>>" + con.getIPAddress() + " has joined\n");
		connections.put(new Integer(connectionID), con);

	}

	/**
	 * the client closes their connection to the server, the connection is then removed from the list
	 * @param connectionID
	 */
	public synchronized void connectionRemotlyClosed(int connectionID) {
		connections.remove(new Integer(connectionID)); //don't try to send any messages to the closed connection
		gameState.getOnlinePlayers().remove(connectionID);
	}

	/**
	 * closes the server
	 */
	public void closeServer() {
		listener.stopListening();
		for(ServerConnection con2 : connections.values()) {
			con2.closeLocaly();
		}
	}


	private String createGameString(){
		if(gameState.getOnlinePlayers().size() > 0){
			String game = "";
			for(Player p : gameState.getOnlinePlayers()){
				game += p.playerString() + "$";
			}
			game.subSequence(0, game.length()-2);
			game += "|";
			for(Player p : gameState.getPlayers()){
				game += p.playerString() + "$";
			}
			game.subSequence(0, game.length()-2);
			game += "#" + createGameObjectsString();
			return game;
		}
		System.out.println("I'm returning null (createGameString)");
		return null;
	}

	private String createGameObjectsString(){
		StringBuilder objectString = new StringBuilder();
		for(GameObject g : gameState.getWorld().getAllGameObjects()){
			if(g instanceof Box){
				objectString.append(g.getPosition().getCol() + ";" + g.getPosition().getRow() + ";");
			}
		}
		//objectString.deleteCharAt(objectString.length()-1);
		return objectString.toString();
	}

	public void saveGame(){

	}

	public void loadGame(){

		gameLoaded = true;
	}

	/**
	 * processes the message received to update the server's gamestate (used for saving and loading games)
	 * @param message: the message received
	 */
	private void processMessage(String message){
		String[] messageArray = message.split(",");

		if(messageArray[0].equals("PLAYER_JOINED")){
			System.out.println("DO I GET HERE?");
			System.out.println("Player Joined :" + messageArray[1]);
			String[] temp = messageArray[1].split(";");
			Player player = null;
			if(temp[3].equals("Soldier")){
				System.out.println("This Player is a Soldier");
				player = new Player(temp[0], Job.Soldier());
			}
			if(temp[3].equals("Archer")){
				System.out.println("This Player is not a Soldier");
				player = new Player(temp[0], Job.Archer());
			}
			player.setPosition(new Position(Integer.parseInt(temp[1]),Integer.parseInt(temp[2])));
			System.out.println("New Player: "+ player);
			try{
				/** Begin test code **/
				gameState.addPlayer(player);
				World playerWorld = gameState.getWorlds().get(player.getWorld().getName());
				Position pos = player.getPosition();
				Position newP = playerWorld.getPosition(pos.getRow(), pos.getCol());
				pos.setGameObject(null);
				newP.setGameObject(player);
				player.setPosition(newP);
				/** End test code **/
			}catch(NullPointerException e){ e.printStackTrace(); } //added by christian (test)
		}
		else if(messageArray[0].equals("UPDATE_TIME")){
			//need a method to update the game time for this client
		}
		else if(messageArray[0].equals("NEW_ITEM")){
			int row = Integer.parseInt(messageArray[3]);
			int col = Integer.parseInt(messageArray[2]);
			Item temp;
			for(GameObject g : gameState.getWorld().getAllGameObjects()){
				if(g.getPosition().getRow() == row && g.getPosition().getCol() == col && g instanceof Item){
					temp = (Item) g;
					for(Player p : gameState.getOnlinePlayers()){
						if(p.getName().equals(messageArray[1]))temp.getPickedUp(p);
					}
				}
			}
		}
		else if(messageArray[0].equals("PLAYER_ATTACK")){
			System.out.println("ATTACK");
			String[] temp = messageArray[1].split(";");

			for(Player p : gameState.getOnlinePlayers()){
				//				if(p.getName().equals(messageArray[1])){
				//					p.attack();
				//				}
				int prow = p.getPosition().getRow();
				int pcol = p.getPosition().getCol();
				if(pcol==Integer.parseInt(temp[1]) && prow == Integer.parseInt(temp[2]) ){
					//p.takeHit(Integer.parseInt(temp[3]));
					p.attack();
					//System.out.println(p + " " + p.getHealth());
				}

			}
		}
		else if(messageArray[0].equals("MONSTER_ATTACK")){
			Monster m = null;
			for(GameObject g : gameState.getWorld().getAllGameObjects()){
				//TODO Monsters are random
			}
		}
		else if(messageArray[0].equals("NPC_MOVE")){
			NPC n = null;
			int row = Integer.parseInt(messageArray[3]);
			int col = Integer.parseInt(messageArray[2]);
			for(GameObject g : gameState.getWorld().getAllGameObjects()){
				if(g.getPosition().getCol() == col && g.getPosition().getRow() == row){
					n = (NPC) g;
					//NPC move method is still random
				}
			}
		}
		else if(messageArray[0].equals("PLAYER_MOVE")){
			String[] temp = messageArray[1].split(";");
			for(Player p : gameState.getOnlinePlayers()){
				if(p.getName().equals(temp[0])){
					if(!(p.getPosition().getCol() == Integer.parseInt(temp[1])) || !(p.getPosition().getRow() == Integer.parseInt(temp[2]))){
						//						p.getPosition().setCol(Integer.parseInt(temp[1]));
						//						p.getPosition().setRow(Integer.parseInt(temp[2]));
						p.setOrientation(Integer.parseInt(temp[3]));
						gameState.movePlayer(p, Integer.parseInt(temp[3]));
						if(p.getPosition().getCol() != Integer.parseInt(temp[1]) || p.getPosition().getCol() != Integer.parseInt(temp[2])){
							int posX = Integer.parseInt(temp[1]);
							int posY = Integer.parseInt(temp[2]);
							p.getPosition().setGameObject(null);
							Position updatePos = gameState.getWorlds().get(p.getWorld().getName()).getPosition(posY, posX);
							p.setPosition(updatePos);
							updatePos.setGameObject(p);
						}
						//System.out.println(p.getName() + " moved to: " + temp[1] +", " + temp[2]);
					}
				}
			}

			/*for(int i = 0; i < messageArray.length; i++) {

				String[] player = messageArray[i].split(";");
				//System.out.println("looking for player: "+player[0]);
				Player currentPlayer = null;
				//System.out.println("Online Players array size: "+getGameState().getOnlinePlayers().size());
				for(Player p : gameState.getOnlinePlayers()){
					System.out.println("Checking player: "+ p.getName());
					if(p.getName().compareTo(player[0]) == 0)currentPlayer = p;
				}
				if(currentPlayer.getPosition().getCol() != Integer.parseInt(player[1]))currentPlayer.getPosition().setCol(Integer.parseInt(player[1]));
				if(currentPlayer.getPosition().getRow() != Integer.parseInt(player[2]))currentPlayer.getPosition().setRow(Integer.parseInt(player[2]));
				currentPlayer.setOrientation(Integer.parseInt(player[3]));
			}*/
		}
		else if(messageArray[0].equals("CHANGE_WORLD")){
			Player p = null;
			for(Player p1 : gameState.getOnlinePlayers()){
				if(p1.getName().equals(messageArray[1]))p = p1;
			}
			//p.setWorld(gameState.getWorlds().get(messageArray[2]));
			gameState.getWorlds().get(messageArray[2]).acceptPlayer(p);
		}
	}


























	class ServerConnection extends Thread {
		public static final String TERMINATE_MESSAGE = "#TERMINATE#"; //never send this message from your program... internal network use only

		private DataInputStream input;
		private DataOutputStream output;
		private Socket socket;
		private Server server;
		private int connectionID;
		private boolean running = true;

		public ServerConnection(Server serv, Socket s) {
			socket = s;
			server = serv;
			try {
				input = new DataInputStream(socket.getInputStream());
				output = new DataOutputStream(socket.getOutputStream());
				connectionID = server.getNextConnectionID();
				output.writeInt(connectionID);
				server.newConnection(connectionID, this);
				start();
			} catch (IOException e) {
				System.err.println("Failed to start server connection");
				e.printStackTrace();
			}

		}

		public int getConnectionID() { return connectionID; }

		public InetAddress getIPAddress() {
			return socket.getInetAddress();
		}
		public void sendMessage(String message) {
			//System.out.println(message);
			byte[] bytes = message.getBytes();
			try {
				output.writeInt(bytes.length);
				output.write(bytes);
				output.flush();
			} catch (IOException e) {
				System.err.println("Error sending message: "+message);
			}

		}
		public void closeLocaly() {
			running = false;
			sendMessage(TERMINATE_MESSAGE);
		}

		private String lastMessageRecived = "";
		public void run() {
			while(!lastMessageRecived.equals(TERMINATE_MESSAGE)) {
				try {
					int messageSize = input.readInt();
					byte[] bytes = new byte[messageSize];
					input.readFully(bytes);
					lastMessageRecived = new String(bytes);
					if(lastMessageRecived.equals(TERMINATE_MESSAGE) && running) {
						sendMessage(TERMINATE_MESSAGE); //we check running so if we were the one that sent the message, we don't want to send a second disconnect
						server.connectionRemotlyClosed(connectionID);
					}
					else {
						server.messageRecived(connectionID, lastMessageRecived);
					}
				} catch(Exception e) {e.printStackTrace();}
			}
			try {
				input.close();
				output.close();
				socket.close();
			} catch (Exception e) {e.printStackTrace();} //added by christian (test)
		}

		@Override
		public boolean equals(Object o) {
			if(o instanceof ServerConnection) {
				ServerConnection con2 = (ServerConnection) o;
				if(connectionID == con2.getConnectionID()) return true;
			}
			return false;
		}
		public int hashCode() { return connectionID; }
	}
	class ServerListener extends Thread {
		public static final int PORT = 3423;
		private boolean running = true;
		private Server server;
		public ServerListener(Server serv) {
			server = serv;
			start();
		}
		public void run() {
			try {
				ServerSocket serverSocket = new ServerSocket(PORT);
				while(running) {
					Socket s = serverSocket.accept();
					if(running) new ServerConnection(server, s);
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		public void stopListening() {
			running = false;
			try {
				Socket s = new Socket("localhost", PORT);
			} catch (IOException e) {e.printStackTrace();} //added by christian(test)
		}
	}

	public void saveGame(String prefix) {
		text.append("Saving with prefix"+prefix);
		gameState.save(prefix);
	}

	public void loadGame(String prefix) {
		text.append("Loading with prefix"+prefix);
		gameState.load(prefix);
		gameLoaded = true;
	}
}