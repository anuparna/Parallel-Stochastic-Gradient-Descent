package parallel_sgd;

import java.util.List;

import constants.Constants;
import parallel_sgd.dataObjects.Accumulator;
import parallel_sgd.dataObjects.Features;
import parallel_sgd.dataObjects.UserData;

public class TrainThread implements Runnable {
	private List<UserData> dataSet;
	private Accumulator accumulator;
	private int num_features;
	private double total_Loss=0;
	private String thread_Name;
	private boolean isComplete=false;
	
	TrainThread(List<UserData> dataSet,Accumulator accumulator,String threadName){
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
			isComplete=true;
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

		for (int k = 0; k < num_features; k++) {
			double userFeature = user_features.getFeatureVectors()[k];
			double itemFeature = item_features.getFeatureVectors()[k];

			double user_std_increment = err * itemFeature;
			double item_std_increment = err * userFeature;
			double user_regularization_increment = Constants.lambda * userFeature;
			double item_regularization_increment = Constants.lambda * itemFeature;
			double user_increment = Constants.gamma * (user_std_increment - user_regularization_increment);
			double item_increment = Constants.gamma * (item_std_increment - item_regularization_increment);

			user_features.getFeatureVectors()[k]= user_features.getFeatureVectors()[k]+user_increment;
			item_features.getFeatureVectors()[k]=item_features.getFeatureVectors()[k]+item_increment;

			//CHECK IF THE USER VECTOR OVERFLOWS
			if (Double.isNaN(userFeature) || 
					userFeature==Double.POSITIVE_INFINITY || 
					userFeature==Double.NEGATIVE_INFINITY){
				long id = Thread.currentThread().getId();
				System.out.println("T"+id+": ERROR: user_Features.vector["+k+"]="+userFeature);
				System.out.println("T"+id+": old_user_Features.vector["+k+"]="+userFeature);
				System.out.println("T"+id+": err="+err);
				System.out.println("T"+id+": increment="+user_increment);
				System.out.println("T"+id+": user_std_increment="+user_std_increment);
				System.out.println("T"+id+": user_regularization_increment="+user_regularization_increment);
				System.out.println("T"+id+": itemFeature="+itemFeature);
				System.out.println();
				System.exit(-1);
			}

			//CHECK IF THE ITEM VECTOR OVERFLOWS
			if (Double.isNaN(itemFeature) ||
					itemFeature==Double.POSITIVE_INFINITY || 
					itemFeature==Double.NEGATIVE_INFINITY){
				long id = Thread.currentThread().getId();
				System.out.println("T"+id+": ERROR: item_Features.vector["+k+"] Nan");
				System.out.println("T"+id+": old_item_Features.vector["+k+"]="+itemFeature);
				System.out.println("T"+id+": err="+err);
				System.out.println("T"+id+": increment="+item_increment);
				System.out.println("T"+id+": item_std_increment="+item_std_increment);
				System.out.println("T"+id+": item_regularization_increment="+item_regularization_increment);
				System.out.println("T"+id+": userFeature="+userFeature);
				System.out.println();
				System.exit(-1);
			}
		}  
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
			System.out.println("The thread is still running");
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
