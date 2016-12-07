package penseg;

import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.knowm.xchart.*;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.LegendPosition;

import com.acuitus.math.recognition.TrainingDataIO;
import com.acuitus.math.recognition.segmentation.Shape;

import falcon.client.pen.data.*;
import falcon.core.tuple.Tuple2;

public class TestStub {

	public static void printShapeList(List<Tuple2<Shape, Character>> shapeList) {
		for (Tuple2<Shape, Character> tuple : shapeList) {
			System.out.println("Shape : t= "+tuple._1().pageBreak.absoluteTime+" char='"+tuple._2()+"' #strokes: "
					+tuple._1().strokes.size()+" rec: "+tuple._1().getRectangle());
			List<Stroke> strokes = tuple._1.strokes;
			for (Stroke s : strokes) {
				Rectangle rec = Shape.getRectangle(s);
				System.out.println("reltime: "+s.getRelativeTime()+" Rec: "+rec+ " points: "+s.getPoints().size());
				if (rec.width == 0 || rec.height == 0) 
					System.out.println(s.getPoints());
			}
		}
	}
	
	
	public static void main(String[] args) throws Exception {
		List<Tuple2<Shape, Character>> shapeList = TrainingDataIO.readTrainingData(
				new File("/Users/pps/dev/pen-data/frank/demos.lash").toURI().toURL());
		printShapeList(shapeList);
		
		
		List<Integer> xdata = new ArrayList<>();
		List<Integer> ydata = new ArrayList<>();
		
		Shape first = shapeList.get(0)._1();
		Shape third = shapeList.get(3)._1();

		Stroke s = third.strokes.get(0);
		Rectangle r = Shape.getRectangle(s);
		for (Point p : s.getPoints()) {
			xdata.add(p.x - r.x);
			ydata.add(-(p.y - r.y));
		}
		

		FeatureExtraction fe = new FeatureExtraction();

		
		XYChart chart = QuickChart.getChart("YO", "X", "Y", "WHATEVE", xdata, ydata);
		
		new SwingWrapper(chart).displayChart();
		
		List<Point> fep = fe.extract(s);
		
		
		List<Integer> xxdata = new ArrayList<>();
		List<Integer> yydata = new ArrayList<>();
		for (Point p : fep) {
			xxdata.add((int)p.x);
			yydata.add(-(p.y));
		}
		
		
		System.out.println(fep);
		
	    XYChart fchart = new XYChartBuilder().width(200).height(300).build();
	    
	    List<Integer> features = fe.points2features(fep);
	    Utils.printFeatures(features, System.out);
//	    System.out.println("Total features: "+features.size());
//	    int count = 0;
//	    while (count < FeatureExtraction.M*FeatureExtraction.N) {
//	    	System.out.print(features.get(count));
//	    	if ((count+1)%FeatureExtraction.M == 0) System.out.println();
//	    	count++;
//	    }
	    
	    // Customize Chart
	    fchart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter);
	    fchart.getStyler().setChartTitleVisible(false);
	    fchart.getStyler().setYAxisMin(-25.0);
	    fchart.getStyler().setYAxisMax(1.0);
	    fchart.getStyler().setXAxisMin(0.0);
	    fchart.getStyler().setXAxisMax(20.0);

	    fchart.getStyler().setLegendPosition(LegendPosition.InsideSW);
	    fchart.getStyler().setMarkerSize(10);
	 
	    // Series
	    fchart.addSeries("fev", xxdata, yydata);
	 
		new SwingWrapper(fchart).displayChart();
		
		
		
		
		
//		printShapeList(shapeList);
		
		/*
		
		shapeList.stream().map(s -> s._1.pageBreak.pageID).forEach(System.out::println);
		long max = shapeList.stream().max((p1,p2) 
				-> Long.compare(p1._1.pageBreak.pageID, p2._1.pageBreak.pageID)).
				get()._1().pageBreak.pageID;
		System.out.println("pid: "+max);
		
//		int max = shapeList.stream().mapToInt(p -> (int)p._1.pageBreak.pageID).
//				max((int p1,int p2) -> return Integer.compare(p1,p2)).get();
		
		List<List<Tuple2<Shape,Character>>> sList = new ArrayList<>();
		for (long pid = 0; pid <= max; pid++) {
			final long sss = pid;
			sList.add(shapeList.stream().filter(s -> (int)s._1.pageBreak.pageID == sss)
					.collect(Collectors.toList()));
		}


		System.out.println(sList);

*/
	}

}
