package penseg;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.acuitus.math.recognition.TrainingDataIO;
import com.acuitus.math.recognition.TrainingDataIO.FailedToReadTrainingDataException;
import com.acuitus.math.recognition.segmentation.Shape;

import falcon.client.pen.data.Point;
import falcon.client.pen.data.Stroke;
import falcon.core.tuple.Tuple2;
import falcon.core.tuple.Tuples;

public class MainStub {

	public static void main(String[] args) throws Exception {
//		System.out.println("Hola");
//		List<Tuple2<Shape, Character>> shapeList = TrainingDataIO.readTrainingData(
//				new File("/Users/pps/dev/pen-data/frank/demos.lash").toURI().toURL());
//		TestStub.printShapeList(shapeList);
		
		Utils.printLashes("/Users/pps/dev/pen-data/lash");

		
//		System.out.println(shapeList.size());
//		for (Tuple2<Shape, Character> t : shapeList) {
//			System.out.println(t._1.pageBreak.toString());
//		}
		
//		FeatureExtraction fex = new FeatureExtraction();
//		
//		for (int i = 0; i < 10; i ++) {
//			Shape shape = shapeList.get(i)._1();
//			LabelFrame lf = new LabelFrame("Shape strokes: "+shape.strokes.size()+" c="+shapeList.get(i)._2(), true);
//			lf.addImage(createImageFromShape(shape, 1), "shape");
//			for (Stroke s : shape.strokes) {
//				lf.addImage(imageFromStroke(s), "stroke");
//				lf.addImage(imageFromFeatures(fex.extract(s)), "features");
//			}
//			showFrame(lf);
//		}
		
		
		
//		List<PagePanel> panels = buildPages(new File("/Users/pps/dev/pen-data/frank/demos.lash").toURI());
		
/*		final JFrame frame = new JFrame("Hola");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel main = new JPanel();

		main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
		for (PagePanel panel : panels) {
			main.add(panel);
			System.out.println("adding size: "+panel.getSize()+ " max "+panel.getMaximumSize()
					+ " min "+panel.getMinimumSize());
		}

		JScrollPane scrollPane = new JScrollPane(main);
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		
//		for (PagePanel panel : panels)
//			main.add(panel, BorderLayout.PAGE_END);
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				frame.pack();
				frame.revalidate();
				frame.setVisible(true);
			}
		});

		//		JFrame f = new JFrame("Hola");
//		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		JPanel p = new JPanel();
//		JLabel l = new JLabel();
//		l.setIcon(new ImageIcon(image));
//		f.getContentPane().add(l);
////		p.getGraphics().drawImage(image, 0, 0, null);
//		f.setSize(1000, 800);
//		f.pack();
//		f.setVisible(true);
//		
//		JOptionPane.showM
		
		*/
	}
	
	public static void showFrame(JFrame frame) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				frame.pack();
				frame.setVisible(true);
			}
		});
	}
	
	public static List<Image> buildImages(URI uri) throws Exception {
		List<Tuple2<Shape, Character>> shapeList = TrainingDataIO.readTrainingData(uri.toURL());
		System.out.println("Loaded list of "+shapeList.size()+ " from "+uri);
	
		List<Image> images = new ArrayList<>();
			
		shapeList.stream().map(s -> s._1.pageBreak.pageID).forEach(System.out::println);
		long max = shapeList.stream().max((p1,p2) 
				-> Long.compare(p1._1.pageBreak.pageID, p2._1.pageBreak.pageID)).
				get()._1().pageBreak.pageID;
//		System.out.println("pid: "+max);
		
//		int max = shapeList.stream().mapToInt(p -> (int)p._1.pageBreak.pageID).
//				max((int p1,int p2) -> return Integer.compare(p1,p2)).get();
		
//		List<List<Tuple2<Shape,Character>>> sList = new ArrayList<>();
		for (long pid = 0; pid <= max; pid++) {
			final long sss = pid;
			List<Tuple2<Shape, Character>> list = 
					shapeList.stream().filter(s -> (int)s._1.pageBreak.pageID == sss)
					.collect(Collectors.toList());
			if (list.isEmpty()) continue;
			images.add(createImageFromShapes(list));
		}
		
		
		return images;
		
	}
	
	public static List<PagePanel> buildPages(URI uri) throws Exception {
		List<Tuple2<Shape, Character>> shapeList = TrainingDataIO.readTrainingData(uri.toURL());
		System.out.println("Loaded list of "+shapeList.size()+ " from "+uri);
	
		List<PagePanel> panels = new ArrayList<>();
			
		shapeList.stream().map(s -> s._1.pageBreak.pageID).forEach(System.out::println);
		long max = shapeList.stream().max((p1,p2) 
				-> Long.compare(p1._1.pageBreak.pageID, p2._1.pageBreak.pageID)).
				get()._1().pageBreak.pageID;
//		System.out.println("pid: "+max);
		
//		int max = shapeList.stream().mapToInt(p -> (int)p._1.pageBreak.pageID).
//				max((int p1,int p2) -> return Integer.compare(p1,p2)).get();
		
//		List<List<Tuple2<Shape,Character>>> sList = new ArrayList<>();
		for (long pid = 0; pid <= max; pid++) {
			final long sss = pid;
			List<Tuple2<Shape, Character>> list = 
					shapeList.stream().filter(s -> (int)s._1.pageBreak.pageID == sss)
					.collect(Collectors.toList());
			if (list.isEmpty()) continue;
			panels.add(new PagePanel(list));
		}
		
		
		return panels;
		
	}
	
	public static Image createImageFromShapes(List<Tuple2<Shape, Character>> tuples) {
		BufferedImage image = new BufferedImage(900,700, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		System.out.println("TOTAL: "+getRectangle(tuples));
		for (Tuple2<Shape, Character> t : tuples) {
			Shape s = t._1();
			System.out.println("full page: "+s.pageBreak.width+ " by "+s.pageBreak.height +
					" shape rec: "+s.getRectangle());
			for (Stroke stroke : t._1.strokes)
				for (int i = 0; i < stroke.getPoints().size()-1; i++) {
//					System.out.println(stroke.getPoints().get(i).x +" "+ stroke.getPoints().get(i).y);
					g2.setBackground(Color.WHITE);
					g2.setColor(Color.BLACK);
					g2.drawLine(stroke.getPoints().get(i).x, stroke.getPoints().get(i).y-700,
							stroke.getPoints().get(i+1).x, stroke.getPoints().get(i+1).y-700);
				}
		}
		
		return image;
		
		
	}
	
	public static Image createImageFromShape(Shape shape, int scale) {
		Rectangle rec = shape.getRectangle();
		if (rec.width==0) rec.width=1;
		if (rec.height==0) rec.height =1;
		System.out.println("Drawing shape with rec: "+rec);
		
		BufferedImage image = new BufferedImage(rec.width,rec.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		for (Stroke stroke : shape.strokes)
			for (int i = 0; i < stroke.getPoints().size() - 1; i++) {
				// System.out.println(stroke.getPoints().get(i).x +" "+
				// stroke.getPoints().get(i).y);
				g2.setBackground(Color.WHITE);
				g2.setColor(Color.BLACK);
				g2.drawLine(stroke.getPoints().get(i).x-rec.x, stroke.getPoints()
						.get(i).y - rec.y, stroke.getPoints().get(i + 1).x-rec.x,
						stroke.getPoints().get(i + 1).y - rec.y);
			}
		
		return image;
	}
	
	public static Image imageFromStroke(Stroke stroke) {
		Rectangle rec = Shape.getRectangle(stroke);
		if (rec.width==0) rec.width=1;
		if (rec.height==0) rec.height =1;
		System.out.println("Drawing shape with rec: "+rec);
		
		BufferedImage image = new BufferedImage(rec.width,rec.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		for (int i = 0; i < stroke.getPoints().size() - 1; i++) {
			// System.out.println(stroke.getPoints().get(i).x +" "+
			// stroke.getPoints().get(i).y);
			g2.setBackground(Color.WHITE);
			g2.setColor(Color.BLACK);
			g2.drawLine(stroke.getPoints().get(i).x - rec.x, stroke.getPoints()
					.get(i).y - rec.y, stroke.getPoints().get(i + 1).x - rec.x,
					stroke.getPoints().get(i + 1).y - rec.y);
		}
		
		return image;
	}
	
	public static Image imageFromFeatures(List<Point> features) {
		BufferedImage image = new BufferedImage(FeatureExtraction.M, FeatureExtraction.N,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		// System.out.println(stroke.getPoints().get(i).x +" "+
		// stroke.getPoints().get(i).y);
		g2.setBackground(Color.WHITE);
		g2.setColor(Color.BLACK);
		for (int i = 0; i < features.size()-1; i++)
		g2.drawLine(features.get(i).x, features.get(i).y, features.get(i+1).x, features.get(i+1).y);
		return image;
	}
	
	public static Rectangle getRectangle(List<Tuple2<Shape, Character>> tuples) {
		
//		if (rectangle == null) 
			Rectangle rectangle = tuples.stream().
				map(t -> t._1().getRectangle()).
				reduce(Rectangle::union).
				get();
		return rectangle;
	}
	
	public static List<Tuple2<Shape,Character>> saveTrainingData(URL u) throws IOException {
		List<Tuple2<Shape, Character>> result = new ArrayList<>();
		try (ObjectInputStream ois = new ObjectInputStream(u.openStream())) {
			while (true)
				result.add(Tuples.of((Shape)ois.readObject(), Character.valueOf(ois.readChar())));
		}
		catch (EOFException e) {
			//Good, all done.
		}
		catch (ClassNotFoundException ce) {
			throw new FailedToReadTrainingDataException(ce);
		}
		return result;
	}

}
