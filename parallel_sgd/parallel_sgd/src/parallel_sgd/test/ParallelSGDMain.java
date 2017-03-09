package parallel_sgd.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import constants.Constants;
import parallel_sgd.ParallelSGDTest;
import parallel_sgd.ParallelSGDTrain;
import parallel_sgd.dataObjects.Accumulator;
import parallel_sgd.dataObjects.UserData;

public class ParallelSGDMain {
	
	
	private static List<UserData> loadDataSet(boolean isTrain) throws IOException
	{
		FileInputStream fileInputStream=null;
		String[] data = null;
		List<UserData> dataSet = new ArrayList<UserData>();
		if(isTrain){
			fileInputStream = new FileInputStream(new File(Constants.trainFileName));
		}
		else{
			fileInputStream = new FileInputStream(new File(Constants.testFileName));
		}
		InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		String strLine;
		while ((strLine = bufferedReader.readLine()) != null)   {
			data = strLine.split(Constants.inputSeparator);
			int user_id = Integer.parseInt(data[0]);
			int item_id = Integer.parseInt(data[1]);
			int rating = Integer.parseInt(data[2]);
			//System.out.println("user_id="+user_id+",item_id="+item_id+",rating="+rating);
			UserData userData = new UserData(user_id,item_id,rating);
			dataSet.add(userData);
		}
		bufferedReader.close();
		return dataSet;
	}
	public static void main(String [] args) throws IOException, InterruptedException{
		//Train data
		List<UserData> trainingData = loadDataSet(true);
		ParallelSGDTrain sgd = new ParallelSGDTrain(trainingData);
		Accumulator accumulator= sgd.run();
		
		//Test data
		List<UserData> testData = loadDataSet(false);
		ParallelSGDTest sgdTest = new ParallelSGDTest(testData,accumulator);
		accumulator= sgdTest.run();
	}
}
