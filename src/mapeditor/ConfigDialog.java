package mapeditor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
/**
 * Dialog for setting up the Nelda map editor
 * allows the user to input map dimensions and file name
 * @author Christian Evaroa
 *
 */
public class ConfigDialog extends JDialog {

	private ActionListener actList;
	private JPanel panel;
	public JTextField width;
	public JTextField height;
	public JTextField fname;
	public JButton done;


	public ConfigDialog(ActionListener act){
		setTitle("Enter dimensions & filename");
		setSize(200, 125);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.actList = act;
		setupBox();
	}

	private void setupBox() {
		panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		panel.add(new JLabel("width:"), c);
		
		width = new JTextField(10);
		c.gridx = 1;
		c.gridy = 0;
		panel.add(width, c);

		c.gridx = 0;
		c.gridy = 1;
		panel.add(new JLabel("height:"), c);
		
		height = new JTextField(10);
		c.gridx = 1;
		c.gridy = 1;
		panel.add(height, c);

		c.gridx = 0;
		c.gridy = 2;
		panel.add(new JLabel("file:"), c);
		
		fname = new JTextField(10);
		c.gridx = 1;
		c.gridy = 2;
		panel.add(fname, c);

		done = new JButton("OK");
		done.addActionListener(actList);
		c.gridx = 0;
		c.gridy = 3;
		panel.add(done, c);

		JButton load = new JButton("Load");
		load.addActionListener(actList);
		c.gridx = 1;
		c.gridy = 3;
		panel.add(load, c);
		
		add(panel);

		setVisible(true);
	}

	public int returnWidth(){
		try{
			return Integer.parseInt(width.getText());
		} catch (Exception e) {
			System.out.println("boo");
		}
		return 0;
	}

	public int returnHeight(){
		try{
			return Integer.parseInt(height.getText());
		} catch (Exception e) {
			System.out.println("boo");
		}
		return 0;
	}

	public String getfname(){
		return fname.getText();
	}

}
