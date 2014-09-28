package gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextArea;

public class ChatListener implements KeyListener{
	private JTextArea log;
	private JTextArea chat;
	private boolean shift = false;
	
	public ChatListener(JTextArea l, JTextArea c){
		log = l;
		chat = c;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		switch (keyCode) {
		case (KeyEvent.VK_ENTER):
			if(shift){
		// send Chat message across server
		log.append(chat.getText() + "\n");
		chat.setText(null);
			}
		break;
		case KeyEvent.VK_SHIFT:
			shift = true;
			break;
		default:
			break;
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		switch (keyCode) {
		case KeyEvent.VK_SHIFT:
			shift = false;
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}
