package gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextField;

public class SaveDialog extends JDialog {

	private String filename;
	private JTextField textField;
	
	public SaveDialog(ActionListener al){
		setTitle("enter save name");
		setSize(new Dimension(200, 100));
		setLayout(new FlowLayout());
		textField = new JTextField("enter file name");
		JButton ok = new JButton("OK");
		ok.addActionListener(al);
		add(textField);
		add(ok);
		setVisible(true);
	}
	
	public String getFilename(){
		filename = textField.getText().replaceAll("\\s+","");
		return filename;
	}
}
