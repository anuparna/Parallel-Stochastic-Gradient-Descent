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

public class ParallelSGDTest {
	private List<UserData> dataSet;
	private Accumulator accumulator;
	private int num_processors = Runtime.getRuntime().availableProcessors();

	public ParallelSGDTest(List<UserData> dataSet,Accumulator accumulator) {
		super();
		this.dataSet = dataSet;
		this.accumulator = accumulator;
	}
	public Accumulator run() throws IOException, InterruptedException{
		BufferedWriter bufferedWriter = null;
		File lossFile = new File (Constants.outputTestLossFileName);
		if(!lossFile.exists()){
			lossFile.createNewFile();
		}
		FileWriter fileWriter = new FileWriter(lossFile);
		bufferedWriter=new BufferedWriter(fileWriter);
		System.out.println("The number of processors = "+num_processors);
		double loss = executeIteration();
		bufferedWriter.write(loss+"\n");
		bufferedWriter.close();
		return accumulator;
	}
	private double executeIteration() throws InterruptedException{
		ExecutorService executor=Executors.newFixedThreadPool(num_processors);
		int n = dataSet.size();
		TestThread[] tthreads=new TestThread[num_processors];
		double loss = 0;
		int subSize = (n / num_processors) + 1;
		for (int t = 0; t < num_processors; t++) {
			final int iStart = t * subSize;
			final int iEnd = Math.min((t + 1) * subSize, n);
			if (iEnd>=iStart){
				List<UserData> subList = dataSet.subList(iStart, iEnd);
				System.out.println("Processor no="+t+",start index="+iStart+
						", end index="+iEnd+", SubList Size : "+subList.size());
				tthreads[t] = new TestThread(subList
						,accumulator,String.valueOf(t));
				executor.execute(tthreads[t]);
			}
		}
		executor.shutdown();
		executor.awaitTermination(30, TimeUnit.MINUTES);
		for (TestThread thread: tthreads) {
			System.out.println("Loss from "+thread.getThread_Name()+" is :"+thread.getResult());
			loss += thread.getResult();
		}
		loss /= n;
        loss = Math.sqrt(loss);
		return loss;
	}
}