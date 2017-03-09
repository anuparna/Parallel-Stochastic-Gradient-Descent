package parallel_sgd;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import constants.Constants;
import parallel_sgd.dataObjects.Accumulator;
import parallel_sgd.dataObjects.UserData;

public class ParallelSGDTrain {
	

	private List<UserData> dataSet;
	private Accumulator accumulator;
	private int num_processors = Runtime.getRuntime().availableProcessors();

	public ParallelSGDTrain(List<UserData> dataSet) {
		super();
		this.dataSet = dataSet;
		this.accumulator = new Accumulator(Constants.num_Features);
	}
	public Accumulator run() throws IOException, InterruptedException{
		BufferedWriter bufferedWriter = null;
		File lossFile = new File (Constants.outputLossFileName);
		if(!lossFile.exists()){
			lossFile.createNewFile();
		}
		FileWriter fileWriter = new FileWriter(lossFile);
		bufferedWriter=new BufferedWriter(fileWriter);
		System.out.println("The number of processors = "+num_processors);
		for(int iteration=0;iteration<Constants.noOfIterations;iteration++){
			double loss = executeIteration();
			bufferedWriter.write(iteration+"\t"+loss+"\n");
		}
		bufferedWriter.close();
		return accumulator;
	}
	private double executeIteration() throws InterruptedException{
		
		ExecutorService executor=Executors.newFixedThreadPool(num_processors);
		int n = dataSet.size();
		TrainThread[] tthreads=new TrainThread[num_processors];
		double loss = 0;
		int subSize = (n / num_processors) + 1;
		for (int t = 0; t < num_processors; t++) {
			final int iStart = t * subSize;
			final int iEnd = Math.min((t + 1) * subSize, n);
			if (iEnd>=iStart){
				List<UserData> subList = dataSet.subList(iStart, iEnd);
				System.out.println("Processor no="+t+",start index="+iStart+
						", end index="+iEnd+", SubList Size : "+subList.size());
				tthreads[t] = new TrainThread(subList
						,accumulator,String.valueOf(t));
				executor.execute(tthreads[t]);
				
			}
		}
		executor.shutdown();
		executor.awaitTermination(30, TimeUnit.MINUTES);
		for (TrainThread thread: tthreads) {
			System.out.println("Loss from "+thread.getThread_Name()+" is :"+thread.getResult());
			loss += thread.getResult();
		}
		loss /= n;
        loss = Math.sqrt(loss);
		return loss;
	}
}