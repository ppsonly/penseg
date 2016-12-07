package penseg;

import java.awt.Rectangle;
import java.util.*;

import com.acuitus.math.recognition.segmentation.Shape;

import falcon.client.pen.data.Point;
import falcon.client.pen.data.Stroke;

public class FeatureExtraction {
	
	public static final short M = 7; 
	public static final short N = 11; 
	
	public List<Integer> points2features(List<Point> points) {
		Integer[] farr = new Integer[M*N];
		Arrays.fill(farr, 0);
		for (Point p : points) {
//			System.out.println("pxpy "+p.x+" "+p.y);
			farr[p.x+p.y*M] = 1;
		}
		return Arrays.asList(farr);
	}
	
	public List<Point> extract(List<Point> points, Rectangle r) {
		
		double k = 0;
		if (r.width < 2 && r.height < 2)
			k = 1;
		else if (r.width == 0)
			k = (N-1)/(double)r.height;
		else if (r.height == 0)
			k = (M-1)/ (double)r.width;
		else
			k = Math.min((M-1) / (double)r.width, (N-1)/(double)r.height);
		
		double xc = r.getCenterX();
		double yc = r.getCenterY();
		
		short x0 = (M-1)/2; //assuming odd M;
		short y0 = (N-1)/2; //assuming odd N;
		
		List<Point> newPoints = new ArrayList<>();
		for (Point p : points) {
			double fx = k * (p.x - xc);
			double fy = k * (p.y - yc);
			Point fp = new Point((short)Math.round(fx+x0), (short)Math.round(fy+y0), 
					p.getRelativePointTime(), p.pressure);
			newPoints.add(fp);
		}
		
		return newPoints;
	}
	
	public List<Point> extract(Stroke s) {
		return extract(s.getPoints(), Shape.getRectangle(s));
	}
	
	public List<Point> extract(List<Stroke> strokes) {
		Rectangle r = strokes.stream().
				map(Shape::getRectangle).
				reduce(Rectangle::union).
				get();
		List<Point> points = new ArrayList<Point>();
		for (Stroke s : strokes) points.addAll(s.getPoints());
		return extract(points, r);
	}

}
