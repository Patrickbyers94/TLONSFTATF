package mapeditor;

import game.Door;
import game.Position;
import game.World;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class DoorDialog extends JDialog {

	private MapEd mapEd;
	private JPanel panel;
	private Position pos;
	private JTextField worldName;
	JComboBox locked;

	public DoorDialog(MapEd med, Position p){
		setTitle("Enter dimensions & filename");
		setSize(250, 125);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.mapEd = med;
		this.pos = p;
		setupBox();
	}

	private void setupBox(){
		panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		JLabel destination = new JLabel("Destination:");
		panel.add(destination, c);
		
		c.gridx = 1;
		c.gridy = 0;
		worldName = new JTextField(10);
		panel.add(worldName, c);
		
		String[] booleans = { "False" , "True" };
		
		locked = new JComboBox(booleans);
		c.gridx = 0;
		c.gridy = 1;
		panel.add(locked, c);
		
		c.gridx = 1;
		c.gridy = 1;
		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				mapEd.doors.add(getDoor());
				mapEd.gui.redraw();
				dispose();
			}});
		panel.add(ok, c);
		
		add(panel);
		setVisible(true);
	}
	
	public Door getDoor(){
		Door d = new Door(new World(new File("assets/"+worldName.getText())));
		if(locked.getSelectedItem().toString().equals("True")){
			d.lock();
		} else { d.unlock(); }
		d.setPosition(pos);
		return d;
	}

}
