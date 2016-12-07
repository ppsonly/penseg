package penseg;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import com.acuitus.math.recognition.TrainingDataIO;
import com.acuitus.math.recognition.segmentation.Shape;

import falcon.client.pen.data.*;
import falcon.core.tuple.Tuple2;

public class Utils {
	
	public static List<List<Tuple2<Shape, Character>>> groupByPageBreak(List<Tuple2<Shape, Character>> shapeList) {
	
		List<List<Tuple2<Shape, Character>>> groups = new ArrayList<>();
			
//		shapeList.stream().map(s -> s._1.pageBreak.pageID).forEach(System.out::println);
//		long max = shapeList.stream().max((p1,p2) 
//				-> Long.compare(p1._1.pageBreak.pageID, p2._1.pageBreak.pageID)).
//				get()._1().pageBreak.pageID;
		Set<Long> ids = new HashSet<>();
		for (Tuple2<Shape, Character> t : shapeList )
			if (!ids.contains(t._1().pageBreak.pageID)) ids.add(t._1().pageBreak.pageID);

//		for (long pid = 0; pid <= max; pid++) {
		for (long pid : ids) {
			final long sss = pid;
			List<Tuple2<Shape, Character>> list = 
					shapeList.stream().filter(s -> (int)s._1.pageBreak.pageID == sss)
					.collect(Collectors.toList());
			if (list.isEmpty()) continue;
			groups.add(list);
		}
		
		return groups;
	}
	
	public static BufferedImage createImageFromShapes(List<Tuple2<Shape, Character>> tuples) {
		PageBreak pb = tuples.get(0)._1().pageBreak;
		BufferedImage image = new BufferedImage(pb.width, pb.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		g2.setBackground(Color.WHITE);
		g2.setColor(Color.BLACK);
//		System.out.println("TOTAL: "+getRectangle(tuples));
		for (Tuple2<Shape, Character> t : tuples) {
			Shape s = t._1();
			System.out.println("full page: "+s.pageBreak.width+ " by "+s.pageBreak.height +
					" shape rec: "+s.getRectangle());
			for (Stroke stroke : t._1.strokes)
				for (int i = 0; i < stroke.getPoints().size()-1; i++) {
//					System.out.println(stroke.getPoints().get(i).x +" "+ stroke.getPoints().get(i).y);
					g2.drawLine(stroke.getPoints().get(i).x, stroke.getPoints().get(i).y,
							stroke.getPoints().get(i+1).x, stroke.getPoints().get(i+1).y);
				}
		}
		return image;
	}

	public static void printLashes(String rootstring) throws Exception {
			File root = new File(rootstring);
			int totalpagec = 0;
			int totalshapec = 0;
			int totalstrokec = 0;
			for (File f : root.listFiles()) {
				System.out.println("************************Reading file "+ f.getName());
				List<Tuple2<Shape, Character>> shapeList = TrainingDataIO.readTrainingData(
						f.toURI().toURL());
				System.out.println("Shapes: "+shapeList.size());
				totalshapec += shapeList.size();
				int pbc = 0;
				int strokec = 0;
				PageBreak pb = null;
				for (Tuple2<Shape, Character> t : shapeList) {
					Shape shape = t._1();
	//				shapec++;
					if (!shape.pageBreak.equals(pb)) {
						pb = shape.pageBreak;
						pbc++;
					}
					strokec = strokec+ shape.strokes.size();
				}
				System.out.println("PBs: "+ pbc);
				System.out.println("Strokes: "+strokec);
				totalpagec += pbc;
				totalstrokec += strokec;
				
			}
			
			System.out.println("Total shapes: "+totalshapec);
			System.out.println("Total pages: "+totalpagec);
	
			System.out.println("Total strokes: "+totalstrokec);
	
		}
	
	public static void lash2jpg(String imgroot, File lashFile) throws Exception {
		List<Tuple2<Shape, Character>> shapeList = TrainingDataIO.readTrainingData(
				lashFile.toURI().toURL());
		
		File rootDest = new File(imgroot);
		if (!rootDest.exists()) rootDest.mkdir();
		
		List<List<Tuple2<Shape,Character>>> groups = groupByPageBreak(shapeList);
		for (List<Tuple2<Shape,Character>> onePage : groups) {
			String filename = lashFile.getName()+".p"+onePage.get(0)._1().pageBreak.pageID+".jpg";
			System.out.println("Writing to "+filename);
			BufferedImage img = createImageFromShapes(onePage);
			ImageIO.write(img, "jpeg", new File(rootDest, filename));
		}
	}
	
	public static void penDataList(String file) throws Exception {
		File f = new File(file);
		List<PenDataItem> items = PenDataIO.readPenData(f);
		for (PenDataItem item : items)
			System.out.println(item);
	}
	
	public static void pinkdir2jpg(File pinkDir, String dest) throws Exception {
		for (File pink : pinkDir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".pink");
			}
		}))
			pink2jpg(pink, dest);
			
	}
	
	public static void pink2jpg(File pink, String dest) throws Exception {
		//TODO check if 
		List<PenDataItem> all = PenDataIO.readPenData(pink);
//		List<List<PenDataItem>> groups = new ArrayList<List<PenDataItem>>();
		Map<PageBreak, List<PenDataItem>> pageMap = new HashMap<>(); 
//		List<PenDataItem> list = null;
		PageBreak currentPage = null;
		for (PenDataItem item : all) {
			if (item instanceof PageBreak) {
				System.out.println(item);
				currentPage = (PageBreak)item;
				pageMap.put(currentPage, new ArrayList<PenDataItem>());
			} else 
				pageMap.get(currentPage).add(item);
		}
		
		System.out.println(pageMap.size());
		
		File rootDest = new File(dest);
		if (!rootDest.exists()) rootDest.mkdir();
		int count = 0;
		for (PageBreak pb : pageMap.keySet()) {
			String filename = pink.getName()+"."+count+".jpg";
			System.out.println("Writing to "+filename);
			
			BufferedImage image = new BufferedImage(pb.width, pb.height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = image.createGraphics();
			g2.setBackground(Color.WHITE);
			g2.setColor(Color.BLACK);
			for (PenDataItem item : pageMap.get(pb)) {
				if (item instanceof Erasure)
					System.out.println(item);
				if (item instanceof Stroke) {
					Stroke stroke = (Stroke) item;
					for (int i = 0; i < stroke.getPoints().size()-1; i++) 
						g2.drawLine(stroke.getPoints().get(i).x, stroke.getPoints().get(i).y,
								stroke.getPoints().get(i+1).x, stroke.getPoints().get(i+1).y);
				}
			}
			
			ImageIO.write(image, "jpeg", new File(rootDest, filename));
			
			count++;
		}
		
	}
	
	public static void printFeatures(List<Integer> features, PrintStream ps) {
	    ps.println("Total features: "+features.size());
	    int count = 0;
	    while (count < FeatureExtraction.M*FeatureExtraction.N) {
	    	ps.print(features.get(count)+" ");
	    	if ((count+1)%FeatureExtraction.M == 0) ps.println();
	    	count++;
	    }
	}
	
	public static void main(String args[]) throws Exception {
		
//		File lashroot = new File("/Users/pps/dev/pen-data/lash");
//		for (File f : lashroot.listFiles())
//			lash2jpg("/Users/pps/dev/pendataimg", f);
		
//		printLashes("/Users/pps/dev/pen-data/lash");
//		penDataList("/Users/pps/dev/mathFrontendPinks-e/tutor-WS-writings-161129-1042.pink");
//		pink2jpg(new File("/Users/pps/dev/mathFrontendPinks-e/tutor-WS-writings-161129-1042.pink"), 
//				"/Users/pps/dev/pen-data/pink");
		
		pinkdir2jpg(new File("/Users/pps/dev/notebookPinks-e"), 
				"/Users/pps/dev/pen-data/pink");
		
		

		
	}

}
