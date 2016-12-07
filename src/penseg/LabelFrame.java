package penseg;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.LineBorder;

public class LabelFrame extends JFrame implements ActionListener {
	
	JPanel labelPanel;
	boolean useBorders = false;
	
	public LabelFrame() {
		this("LabelFrame", false);
	}
	
	public LabelFrame(String name) {
		this(name, false);
	}
	
	public LabelFrame(String name, boolean useBorders) {
		super(name);
		this.useBorders = useBorders;
		labelPanel = new JPanel();
		labelPanel.setLayout(new BoxLayout(labelPanel,BoxLayout.X_AXIS));
		JScrollPane jscp = new JScrollPane(labelPanel);
		jscp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		jscp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(jscp, BorderLayout.CENTER);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	
	public void addImage(Image img, String label) {
		JLabel jlabel = new JLabel(label);
		jlabel.setIcon(new ImageIcon(img));
		
		if (useBorders) 
			jlabel.setBorder(new LineBorder(Color.green, 1));
		labelPanel.add(jlabel);
	}
	


	@Override
	public void actionPerformed(ActionEvent e) {
		if ("save".equals(e.getActionCommand()))
			System.out.println("Saving shit");
	}

}