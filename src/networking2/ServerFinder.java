package networking2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ServerFinder {

	private List<InetAddress> serverAddress = new ArrayList<InetAddress>();
	private List<Integer> playerCount = new ArrayList<Integer>();
	private InetAddress chosenServer;

	public ServerFinder() {
		setupGUI();
		broadcast();
		displayServers();
	}

	/**
	 * create a GUI to display the servers.
	 */
	private void setupGUI() {

	}

	private void broadcast() {
		// Find the server using UDP broadcast
		try {
			// Open a random port to send the package
			DatagramSocket c = new DatagramSocket();
			c.setSoTimeout(2000);
			c.setBroadcast(true);

			// may need comma at the end of the string
			byte[] sendData = "DISCOVER_REQUEST".getBytes();

			// Try the 255.255.255.255 first
			try {
				DatagramPacket sendPacket = new DatagramPacket(sendData,
						sendData.length,
						InetAddress.getByName("255.255.255.255"), 55548);
				c.send(sendPacket);
				System.out
						.println("Request packet sent to: 255.255.255.255 (DEFAULT)");
			} catch (Exception e) {
			}

			// Broadcast the message over all the network interfaces
			Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface networkInterface = (NetworkInterface) interfaces
						.nextElement();

				if (networkInterface.isLoopback() || !networkInterface.isUp()) {
					continue; // Don't want to broadcast to the loopback
								// interface
				}

				for (InterfaceAddress interfaceAddress : networkInterface
						.getInterfaceAddresses()) {
					InetAddress broadcast = interfaceAddress.getBroadcast();
					if (broadcast == null) {
						continue;
					}

					// Send the broadcast package!
					try {
						DatagramPacket sendPacket = new DatagramPacket(
								sendData, sendData.length, broadcast, 55548);
						c.send(sendPacket);
					} catch (Exception e) {
					}

					System.out.println("Request packet sent to: "
							+ broadcast.getHostAddress() + "; Interface: "
							+ networkInterface.getDisplayName());
				}
			}

			System.out
					.println("Done looping over all network interfaces. Now waiting for a reply!");
			long start = System.currentTimeMillis();
			long finish = 0;

			while (finish < (start + 1000)) {
				finish = System.currentTimeMillis();
				// Wait for a response
				byte[] recvBuf;
				recvBuf = new byte[15000];
				DatagramPacket receivePacket;
				receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
				try {
					c.receive(receivePacket);
				} catch (SocketTimeoutException e) {
					break;
				}

				// We have a response
				// Check if the message is correct
				String message = new String(receivePacket.getData()).trim();
				String[] temp = message.split(",");
				if (temp[0].equals("DISCOVER_RESPONSE")) {
					// add server address to the list of addresses
					if (!serverAddress.contains(receivePacket.getAddress())) {
						serverAddress.add(receivePacket.getAddress());
					}
				}
			}
			// Close the port!
			c.close();
		} catch (IOException ex) {
			System.out.println("The socket timed out");
		}
		for (InetAddress i : serverAddress) {
			System.out.println("Server Found: " + i.getHostAddress());
		}
	}

	/**
	 * display the servers, may need to change this to return list of servers
	 */
	private void displayServers() {
		int index = 0;
		JFrame frame = new JFrame();

		System.out
				.println("----------------START OF SERVER SELECTION-------------------");
		List<String> stringAdd = new ArrayList<String>();
		for (InetAddress i : serverAddress) {
			stringAdd.add(i.toString());
		}
		System.out.println("Number of Servers Found: " + stringAdd.size());
		System.out.println(stringAdd.toString());
		String[] stringAddress = new String[stringAdd.size()];
		for (int i = 0; i < stringAdd.size(); i++) {
			stringAddress[i] = stringAdd.get(i);
		}
		JComboBox serversList = new JComboBox(stringAddress);
		serversList.setSelectedIndex(0);
		chosenServer = serverAddress.get(0);
		serversList.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				JComboBox cb = (JComboBox) e.getSource();
				int selectedIndex = cb.getSelectedIndex();
				// System.out.println(selectedIndex);
				chosenServer = serverAddress.get(selectedIndex);
				System.out.println("Chosen Server: "
						+ chosenServer.getHostAddress());
			}

		});

		int value = JOptionPane.showConfirmDialog(frame, serversList,
				"Server Selection", JOptionPane.OK_CANCEL_OPTION);
		if (value == JOptionPane.OK_OPTION) {
			chosenServer = serverAddress.get(serversList.getSelectedIndex());
			System.out.println("Setting server");
		}
		/*
		 * List<String> stringAdd = new ArrayList<String>(); for(InetAddress i :
		 * serverAddress){ stringAdd.add(i.toString()); } String[] stringAddress
		 * = (String[]) stringAdd.toArray(); JComboBox serversList = new
		 * JComboBox(stringAddress); serversList.setSelectedIndex(1);
		 * serversList.addActionListener(new ActionListener() { ======= //get
		 * input from user, they choose which server they want to play on
		 * >>>>>>> .merge-right.r278
		 * 
		 * <<<<<<< .working
		 * 
		 * @Override public void actionPerformed(ActionEvent e){ JComboBox cb =
		 * (JComboBox) e.getSource(); int selectedIndex = cb.getSelectedIndex();
		 * chosenServer = serverAddress.get(selectedIndex-1); }
		 * 
		 * });
		 */
		// chosenServer = serverAddress.get(index);

		// save server address
		// chosenServer = getServer(index);
	}

	public List<InetAddress> getServers() {
		return serverAddress;
	}

	public List<Integer> getNumPlayers() {
		return playerCount;
	}

	public InetAddress getServerAddress() {
		return chosenServer;
	}

	private InetAddress getServer(int i) {
		if (serverAddress.size() > 0) {
			return serverAddress.get(i);
		}
		return null;
	}

	public void setChosenServer(InetAddress chosenServer) {
		this.chosenServer = chosenServer;
		System.out.println("Server address is set");
	}

	public static void main(String[] args) {
		new ServerFinder();
	}
}
