package parallel_sgd.dataObjects;

import java.util.Random;

public class Features {
	private double[] featureVectors;
	private static int num_Features;
	
	Features(int num_Features){
		Features.num_Features=num_Features;
		featureVectors=new double[num_Features];
		Random random = new Random();
        for (int f = 0; f < num_Features; f++) { 
            //samples from the Uniform(-0.5,0.5) distribution
            //nextDouble() Returns the next pseudorandom, uniformly distributed double value between 0.0 and 1.0 from this random number generator's sequence.
        	featureVectors[f] = random.nextDouble() - 0.5; 
        }
	}

	public double[] getFeatureVectors() {
		return featureVectors;
	}

	public void setFeatureVectors(double[] featureVectors) {
		this.featureVectors = featureVectors;
	}

	public static double dotproduct(Features user_vectors, Features item_vectors) {
		double sum = 0;
		for (int counter = 0; counter < num_Features; counter++) {
			sum += user_vectors.getFeatureVectors()[counter] * item_vectors.getFeatureVectors()[counter];
		}
		return sum;
	}  

}
