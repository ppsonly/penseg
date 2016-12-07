package penseg;

import java.io.*;
import java.net.MalformedURLException;
import java.util.*;

import com.acuitus.math.recognition.TrainingDataIO;
import com.acuitus.math.recognition.segmentation.Shape;

import falcon.client.pen.data.Stroke;
import falcon.core.tuple.Tuple2;

public class TrainingBuilder {
	
	public static final List<String> testFiles 
	= Arrays.asList("fractMult.lash", "109_initial_sample.lash", "110_recognition_test.lash");

	private FeatureExtraction fex = new FeatureExtraction();
	
	
	// load lash files one by one
	// for every lash files group shapes by page
	// for every page sort shapes by time?
	// for every shape
	//   get all the strokes
	//	 compare 1st stroke with previous shape , answer is 0
	//  for all subsequent strokes in the shape answer is 1 
	// if 

	
	public void buildTraining(String lashroot, String dest) throws Exception {
		File lashes = new File(lashroot);
		List<String> trainList = new ArrayList<String>();
		List<String> testList = new ArrayList<String>();
		for (File lash : lashes.listFiles()) {
			System.out.println("Processing "+lash.getName());
			List<String> feat = getData(lash);
			if (testFiles.contains(lash.getName()))
				testList.addAll(feat);
			else
				trainList.addAll(feat);
		}
		stringsToFile(dest, "train.txt", trainList);
		stringsToFile(dest, "test.txt", testList);
	}
	
	
	public List<String> getData(File lash) {
		
		List<Tuple2<Shape, Character>> tuples;
		try {
			tuples = TrainingDataIO.readTrainingData(lash.toURI().toURL());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return Collections.emptyList();
		} catch (IOException e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
		
		List<String> data = new ArrayList<>();
		
		List<List<Tuple2<Shape, Character>>> groups = Utils.groupByPageBreak(tuples);
		System.out.println("Pages: "+groups.size());
		for (List<Tuple2<Shape,Character>> group : groups) {
			List<Shape> shapes = getOrderedShapeList(group);
			Shape prevShape = null;
			List<Stroke> prevStrokes = new ArrayList<>();
			// for every shape
			// -> prev shape + first stroke = 0
			// if more strokes 
			//   first stroke + next stroke = 1
			//   add next stroke
			//   see if there are more strokes
			for (Shape shape : shapes) {
				for (Stroke s : shape.strokes) {
					if (prevShape != null) data.add(generateFeatures(prevShape, s, 0));
					if (!prevStrokes.isEmpty()) data.add(generateFeatures(prevStrokes, s, 1));
					prevStrokes.add(s);
				}
				prevShape = shape;
			}
		}
		
		Collections.shuffle(data);
		
		return data;
		
	}
	
	private String generateFeatures(Shape shape, Stroke stroke, int belongs) {
		return generateFeatures(shape.strokes, stroke, belongs);
	}
	
	private String generateFeatures(List<Stroke> strokes, Stroke stroke, int belongs ) {
		StringBuilder sb = new StringBuilder();
		List<Integer> bothStrokesFeatures = new ArrayList<Integer>();
		bothStrokesFeatures.addAll(fex.points2features(fex.extract(strokes)));
		bothStrokesFeatures.addAll(fex.points2features(fex.extract(stroke)));
		for (Integer i : bothStrokesFeatures)
			sb.append(i).append(", ");
		
		sb.append(belongs);
		return sb.toString();
	}
	
	public List<Shape> getOrderedShapeList(List<Tuple2<Shape,Character>> tuples) {
		List<Shape> shapes = new ArrayList<Shape>();
		for (Tuple2<Shape, Character> t : tuples)
			shapes.add(t._1());
		Collections.sort(shapes, new Comparator<Shape>() {
			public int compare(Shape o1, Shape o2) {
				return Integer.compare(o1.strokes.get(0).getRelativeTime(), o2.strokes.get(0).getRelativeTime());
			}
			
		});
		return shapes;
	}

	public static void stringsToFile(String destPath, String filename, List<String> strings) throws IOException {
		PrintWriter pw = new PrintWriter(new FileWriter(new File(destPath,filename)));
		for (String s : strings) 
			pw.println(s);
		pw.close();
	}
	
	public static void main(String[] args) throws Exception {
		TrainingBuilder tb = new TrainingBuilder();
		tb.buildTraining("/Users/pps/dev/pen-data/lash", "/Users/pps/dev/pen-data/train");
	}
	
}
