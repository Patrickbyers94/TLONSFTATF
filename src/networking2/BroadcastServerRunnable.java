package networking2;

import java.io.IOException;
import java.net.*;

import javax.swing.JTextArea;

public class BroadcastServerRunnable implements Runnable{

	private DatagramSocket socket;
	private JTextArea text;

	public BroadcastServerRunnable(JTextArea text){
		this.text = text;
	}

	  public void run() {
	    try {
	      //Keep a socket open to listen to all the UDP traffic that is destined for this port
	      socket = new DatagramSocket(55548, InetAddress.getByName("0.0.0.0"));
	      socket.setBroadcast(true);

	      while (true) {
	        //text.append("\n" + getClass().getName() + ">>>Ready to receive broadcast packets!");

	        //Receive a packet, maybe change buffer size
	        byte[] recvBuf = new byte[15000];
	        DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
	        socket.receive(packet);

	        //Packet received
	        text.append("\n" + getClass().getName() + ">>>Discovery packet received from: " + packet.getAddress().getHostAddress());
	        text.append("\n" + getClass().getName() + ">>>Packet received; data: " + new String(packet.getData()));

	        //See if the packet holds the right command (message)
	        String message = new String(packet.getData()).trim();
	        if (message.equals("DISCOVER_REQUEST")) {
			      String temp = "DISCOVER_RESPONSE,";
			      byte[] sendData =  temp.getBytes();
			      //Send a response
			      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
			      socket.send(sendPacket);

			      text.append("\n" + getClass().getName() + ">>>Sent packet to: " + sendPacket.getAddress().getHostAddress());

	        }
	      }
	    } catch (IOException ex) {
	    	text.append("\n" + "Broadcast Server Broke");
	    }
	  }
}
