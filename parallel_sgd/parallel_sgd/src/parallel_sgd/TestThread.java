package parallel_sgd;

import java.util.List;

import constants.Constants;
import parallel_sgd.dataObjects.Accumulator;
import parallel_sgd.dataObjects.Features;
import parallel_sgd.dataObjects.UserData;

public class TestThread implements Runnable {
	private List<UserData> dataSet;
	private Accumulator accumulator;
	private int num_features;
	private double total_Loss=0;
	private String thread_Name;
	private boolean isComplete=false;
	

	TestThread(List<UserData> dataSet,Accumulator accumulator,String threadName){
		this.thread_Name=threadName;
		this.dataSet = dataSet;
		this.accumulator=accumulator;
		this.num_features=accumulator.getNum_Features();
	}

	@Override
	public void run() {
		System.out.println("thread - "+thread_Name);
		if(dataSet != null && !dataSet.isEmpty()){
			for(UserData userData : dataSet){
				Features user_features=accumulator.getUserFeatures(userData.getUser_id());
				Features item_features=accumulator.getItemFeatures(userData.getItem_id());
				update(user_features,item_features,userData.getRating());
				//System.out.println(total_Loss);
			}
			isComplete = true;
		}
	}
	private void update(Features user_features, Features item_features , int rating){
		double prediction = Features.dotproduct(user_features, item_features);
		double err = rating - prediction;

		//Loss calculation
		double local_loss = Math.pow(err, 2);
		local_loss /= 2;

		double regularization = Constants.lambda;
		regularization /=2;
		regularization *= (FrobeniusNorm(user_features) + FrobeniusNorm(item_features));
		local_loss += regularization;

		total_Loss+=local_loss;
	}

	private double FrobeniusNorm(Features features){
		double sum = 0;
		for (int k = 0; k < num_features; k++) {
			sum += Math.pow(features.getFeatureVectors()[k],2);
		}
		return sum;
	}

	public double getResult() {
		if(isComplete){
			return total_Loss;
		}
		else{
			System.out.println("execution not complete");
			return getResult();
		}
	}

	public String getThread_Name() {
		return thread_Name;
	}

	public void setThread_Name(String thread_Name) {
		this.thread_Name = thread_Name;
	}

}
