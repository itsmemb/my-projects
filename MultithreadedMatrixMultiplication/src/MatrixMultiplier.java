/**
 * @author Phil, Ben, Max
 * Assignment: Project 3, Multi-threaded matrix multiplication
 * Date: 4/10/2021
 * Description: Input files for different configurations included, edit file variable name.
 * 				Multiply randomly generated matrices by having a thread split them
 * 				into sub matrices and other threads multiply the sub matrices into one.
 * 				Then the thread that split them will compile the results.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class MatrixMultiplier {
	
	public static List<WorkItem> doneList = Collections.synchronizedList(new ArrayList<WorkItem>());
	public static boolean consumerStop = false;
	
	public static void main(String[] args) {
		// Initialize all our values to 0. File will set them
		int M = 0;
		int N = 0;
		int P = 0;
		int mainThreadSleepTime = 0;
		int numConsumer = 0;
		int maxConsumerSleepTime = 0;
		int maxProducerSleepTime = 0;
		int splitSize = 0;
		int maxBufferSize = 0;
		
		String file = "inputfile.txt"; // file we read text from
		Scanner sc = null;
		try {
			sc = new Scanner(new File(file));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		// Read through each line and set the values
		while (sc.hasNext()) {
			String line = sc.nextLine();
			String key = line.substring(line.indexOf('<') + 1, line.indexOf('>')); // get key in file
			String value = line.substring(line.lastIndexOf('<') + 1, line.lastIndexOf('>')); // get value of key
			if (key.contentEquals("M")) {
				M = Integer.parseInt(value);
			}
			if (key.contentEquals("N")) {
				N = Integer.parseInt(value);
			}
			if (key.contentEquals("P")) {
				P = Integer.parseInt(value);
			}
			if (key.contentEquals("MaxBuffSize")) {
				maxBufferSize = Integer.parseInt(value);
			}
			if (key.contentEquals("SplitSize")) {
				splitSize = Integer.parseInt(value);
			}
			if (key.contentEquals("NumConsumer")) {
				numConsumer = Integer.parseInt(value);
			}
			if (key.contentEquals("MaxProducerSleepTime")) {
				maxProducerSleepTime = Integer.parseInt(value);
			}
			if (key.contentEquals("MaxConsumerSleepTime")) {
				maxConsumerSleepTime = Integer.parseInt(value);
			}
		}

		ArrayList<Consumer> consumerList = new ArrayList<Consumer>(); // list of consumer objects
		ArrayList<Thread> threadList = new ArrayList<Thread>(); // list of threads
		
		SharedBuffer theBuffer = new SharedBuffer(maxBufferSize); // shared buffer passed to all objects
		
		int[][] matrixA = new int[M][N]; // initialize matrix A
		int[][] matrixB = new int[N][P]; // initialize matrix B
		Random rand = new Random(); // random object to generate numbers
		
		// Set up the matrices with random numbers between 0-9, and print each one
		System.out.println("First matrix:");
		for (int i = 0; i < M; i++) {
			System.out.print("\t[");
			for (int j = 0; j < N; j++) {
				matrixA[i][j] = rand.nextInt(9);
				if (j == N-1)
					System.out.print(matrixA[i][j]);
				else
					System.out.print(matrixA[i][j] + " ");
			}
			System.out.println("]");
		}
		
		System.out.println("Second matrix:");
		for (int i = 0; i < N; i++) {
			System.out.print("\t[");
			for (int j = 0; j < P; j++) {
				matrixB[i][j] = rand.nextInt(9);
				if (j == P-1)
					System.out.print(matrixB[i][j]);
				else
					System.out.print(matrixB[i][j] + " ");
			}
			System.out.println("]");
		} // end of setting matrices up
		  						   
		// create a producer, we only support 1
		Producer p1 = new Producer(theBuffer, 1, matrixA, matrixB, splitSize, maxProducerSleepTime);
		
		//create consumers, we support more than 1
		for (int i=0; i<numConsumer; i++) {
			consumerList.add(new Consumer(theBuffer, i + 1, maxConsumerSleepTime));
		}
		
		// create threads
		threadList.add(new Thread(p1)); // first thread is the 1 producer
		for (int i=0; i<consumerList.size(); i++) {
			threadList.add(new Thread(consumerList.get(i))); // the rest of the threads are consumers
		}
		
		// fill doneList with null so we can use doneList.set(item.itemId, item) in consumer
		while(doneList.size() < (Math.floor(matrixA.length/splitSize) + 1)*(Math.floor((matrixB[0].length)/splitSize) + 1)) {
			doneList.add(null);
		}
		
		long startTime = System.nanoTime(); // get time right before threads are started
		
		// start threads
		threadList.get(0).start();
		for (int i=1; i<threadList.size(); i++) {
			if (theBuffer.isDone() == false) {
				threadList.get(i).start();
			}
		} // done starting threads
		
		// while producer is not done. Which waits for the shared buffer to be "done" (consumerStop == true and buffer.isEmpty)
		while (Producer.pDoneFlag == false) {
			// if the last item in the donList is not null
			if (doneList.get(doneList.size() - 1) != null) {
				consumerStop = true; // consumers should stop
			}
			try {
				Thread.sleep(500); // sleep 500 ms
				mainThreadSleepTime += 500;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} // end while
		
		// make sure all threads have died before continuing
		for (int i = 0; i < threadList.size(); i++) {
			try {
				threadList.get(i).join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} // end for
		
		// all threads have died, set endTime now
		long endTime = System.nanoTime();
		
		// print final result matrix
		System.out.println("------------------------------------------------"
						+ "\nFinal result of parallel matrix multiplication:");
		int[][] matrixC = p1.getMatrixC();
		System.out.print("\t[");
		for (int r=0; r<matrixC.length; r++) {
			System.out.print("[");
			for (int c=0; c<matrixC[0].length; c++) {
				if (c != matrixC[0].length -1) {
					System.out.print(matrixC[r][c] + ", ");
				}
				else {
					System.out.print(matrixC[r][c]);
				}
			}
			if (r != matrixC.length -1) {
				System.out.print("], \n\t");
			}
			else {
				System.out.print("]");
			}
		}
		
		System.out.print("]\n"
				+ "------------------------------------------------");
		
		// verify result with normal sequential method
		int[][] productMatrix = new int[M][P];
		for(int i = 0; i < M; i++){    
            for(int j = 0; j < P; j++){    
                for(int k = 0; k < N; k++){    
                   productMatrix[i][j] = productMatrix[i][j] + matrixA[i][k] * matrixB[k][j];     
                }    
            }    
        } 
		System.out.println("\nResult of sequential matrix multiplication:\n\t" 
                + Arrays.deepToString(productMatrix).replace("], ",  "],\n\t"));
		
		// get average thread sleep time and consumed work items
		StringBuffer sb = new StringBuffer(); 
		int totalConsumed = 0;
		int totalSleep = p1.getProducerSleepTime() + mainThreadSleepTime;
		int count = 2; // we have producer and main thread counted already
		for (int i = 0; i < consumerList.size(); i++) {
			totalSleep += consumerList.get(i).getConsumerSleepTime(); // add each consumer's sleep time
			totalConsumed += consumerList.get(i).getConsumedWorkItems(); // add each consumer's number of consumed work items
			sb.append("\n\tThread " + (i+1) +": " + consumerList.get(i).getConsumedWorkItems()); // make a string for number of consumed work items
			count++;
		}
		
		// print simulation results
		System.out.print("------------------------------------------------"
				+ "\nPRODUCER / CONSUMER SIMULATION RESULT"
				+ "\nSimulation time: " + (endTime - startTime) / 1000000 + " ms"
				+ "\nAverage thread sleep time: " + (totalSleep/count)
				+ "\nNumber of producer threads: 1"
				+ "\nNumber of consumer threads: " + numConsumer
				+ "\nSize of buffer: " + maxBufferSize
				+ "\nTotal number of items produced: " + p1.getProducedWorkItems()
				+ "\n\tThread 0: " + p1.getProducedWorkItems()
				+ "\nTotal number of items consumed: " + totalConsumed + sb.toString()
				+ "\nNumber of times buffer was full: " + theBuffer.getFullCount()
				+ "\nNumber of times buffer was empty: " + theBuffer.getEmptyCount());
		
	} // end main

} // end MatrixMultiplier class
