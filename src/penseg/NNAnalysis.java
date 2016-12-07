package penseg;

import java.io.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

public class NNAnalysis {


	
	private static final String OUTPUT_STRING = "Output:";
	private static final String DESIRED_STRING = "Desired output:";
	private static final String ERROR_STRING = "Error:";
	private static final String INPUT_STRING = "Input:";

	
	private static final double EPSILON = 0.02;



	public static void main(String[] args)  throws Exception {
		readErrorFile("/Users/pps/dev/pen-data/train/error.txt");
	}
		

	
	public static void readErrorFile(String errorFile) throws Exception {
		
		List<String> errorList = new ArrayList<String>();
		
		LineNumberReader lnr = new LineNumberReader(new FileReader(errorFile));
		for (String line; (line = lnr.readLine()) != null;)
			errorList.add(line);
		
		System.out.println(errorList.get(errorList.size()-1));
		errorList.remove(errorList.size()-1);
		lnr.close();
		
		System.out.println("Read lines: "+errorList.size());
		

		
		int errorCount = 0;
		int smallErrorCount = 0; //in case when we have erros, but there is still >0.95 probability present
		
		System.out.println("Total entries in report: "+errorList.size()+" total test files: "
				/*+testFileList.size()*/);
		LabelFrame lf = new LabelFrame("ZB", true);
		
//		Map<String,String> 
		for (int index = 0; index < errorList.size(); index++) {
			String line = errorList.get(index);
			String inputString = line.substring(line.indexOf(INPUT_STRING)+INPUT_STRING.length(), 
					line.indexOf(OUTPUT_STRING)).trim();
			String outputString = line.substring(line.indexOf(OUTPUT_STRING)+OUTPUT_STRING.length(), 
											line.indexOf(DESIRED_STRING)).trim();
			String desiredString = line.substring(line.indexOf(DESIRED_STRING)+DESIRED_STRING.length(), 
												line.indexOf(ERROR_STRING)).trim();
			String errorString = line.substring(line.indexOf(ERROR_STRING)+ERROR_STRING.length()).trim();
			
			List<Integer> input = parseToInteger(inputString);
			List<Double> output = parseToDouble(outputString);
			List<Integer> desired = parseToInteger(desiredString);
			List<Double> error = parseToDouble(errorString);
			
			System.out.println(error);
			double err = error.get(0);
			if (Math.abs(err) > 0.05) {
				errorCount++;
				System.out.println("Error: "+err+" expected: "+desired.get(0)
						+" actual: "+output.get(0));
				List<Integer> stroke1 = input.subList(0, 77);
				List<Integer> stroke2 = input.subList(77, input.size());
				Utils.printFeatures(stroke1, System.out);
				Utils.printFeatures(stroke2, System.out);
			}
			

			
			
//			if (checkErrors2(error, 0.05) != null && '@'!=getChar(desiredString)) {
//				
//				
//				errorCount++;
//				for (double d : output) if (d > 0.94) { smallErrorCount++; break; }
//				String errLabel = getChar(desiredString)+": "+output+" "+testFileList.get(index);
//				System.out.println(errLabel);
//				String imgFileName = new File(
//						new File(errorFile).getParent() , String.valueOf(getChar(desiredString))
//						).getAbsolutePath().concat(File.separator).concat(testFileList.get(index));
////				File f = new File(new File("/Users/pps/dev/NNTrain/full1020"), 
////						String.valueOf(readIndexFromOutput(desiredString)), testFileList.get(index));
//				System.out.println(imgFileName);
//				
////				int desIndex = readIndexFromOutput(desiredString);
//				
//				String sss = getChar(desIndex)+" - "+(desIndex<0?"NA" : output.get(desIndex));
//				
//				
//			}

		}
		
		System.out.println("Total errors: "+errorCount);
		System.out.println("Small errors: "+smallErrorCount);
		int size = errorList.size();
		System.out.println("Total: "+size);

		double percentage = (double)(size-errorCount)/size;
		System.out.println("Correct percentage: "+percentage);
		
		System.out.println("Small error percentage: "+(double)(size-errorCount+smallErrorCount)/size);
		lf.pack();
		lf.setVisible(true);

	}
	
	public static List<Integer> parseToInteger(String stringOfDoubles) {
		List<Integer> doubles = new ArrayList<Integer>();
		StringTokenizer st = new StringTokenizer(stringOfDoubles, ";");
		NumberFormat nf = NumberFormat.getInstance(); //work around, neuroph uses ',' as decimal separator
		while (st.hasMoreTokens()) {
			String s = st.nextToken().trim();
			try {
				Number n = nf.parse(s);
				doubles.add(n.intValue());
			} catch (ParseException e) {
				System.err.println("Could not parse '"+s+"' in a string '"+stringOfDoubles+"'");
				e.printStackTrace();
			}
		}
		return doubles;
	}
	
	public static List<Double> parseToDouble(String stringOfDoubles) {
		List<Double> doubles = new ArrayList<Double>();
		StringTokenizer st = new StringTokenizer(stringOfDoubles, ";");
		NumberFormat nf = NumberFormat.getInstance(); //work around, neuroph uses ',' as decimal separator
		while (st.hasMoreTokens()) {
			String s = st.nextToken().trim();
			try {
				Number n = nf.parse(s);
				doubles.add(n.doubleValue());
			} catch (ParseException e) {
				System.err.println("Could not parse '"+s+"' in a string '"+stringOfDoubles+"'");
				e.printStackTrace();
			}
		}
		return doubles;
	}
	

	
	public static boolean checkErrors(List<Double> errors, double epsilon) {
		for (double d : errors)
			if (d > epsilon) return false;
		return true;
	}


}
