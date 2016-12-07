package penseg;

import java.awt.*;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.acuitus.math.recognition.segmentation.Shape;

import falcon.client.pen.data.Stroke;
import falcon.core.tuple.Tuple2;

public class PagePanel extends JPanel {

	private List<Tuple2<Shape, Character>> tuples;
	
	public PagePanel(List<Tuple2<Shape, Character>> tuples) {
		this.tuples = tuples;
		
		setSize(findSize());
//		setMinimumSize(findSize());
		setBorder(BorderFactory.createLineBorder(Color.green));
		
		setBackground(Color.white);
		
	}
	
	public Dimension getMinimumSize() {
		return findSize();
	}
	
	public Dimension getPreferredSize() {
		return findSize();
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		for (Tuple2<Shape, Character> t : tuples) {
			Shape s = t._1();
//			System.out.println("full page: "+s.pageBreak.width+ " by "+s.pageBreak.height +
//					" shape rec: "+s.getRectangle());
			for (Stroke stroke : t._1.strokes)
				for (int i = 0; i < stroke.getPoints().size()-1; i++) {
//					System.out.println(stroke.getPoints().get(i).x +" "+ stroke.getPoints().get(i).y);
//					g2.setColor(Color.BLACK);
					g.drawLine(stroke.getPoints().get(i).x, stroke.getPoints().get(i).y,
							stroke.getPoints().get(i+1).x, stroke.getPoints().get(i+1).y);
				}
		}
		
	}
	
	private Dimension findSize() {
		Shape s = tuples.get(0)._1();
		Dimension d = new Dimension(s.pageBreak.width, s.pageBreak.height);
		System.out.println("Panel with size "+d);
		return d;
	}
	
	
	
	
	
}
