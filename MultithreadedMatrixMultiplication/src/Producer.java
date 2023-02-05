
import java.util.Iterator;

public class Producer implements Runnable {
	public static boolean pDoneFlag = false;
	private SharedBuffer myBuffer;
	private int id;
	private int[][] matrixA;
	private int[][] matrixB;
	private int[][] matrixC;
	private int splitSize;
	private int maxProducerSleepTime;
	private int totalProducerSleepTime = 0;
	private int producedWorkItems = 0;
	
	public Producer(SharedBuffer myBuffer, int id, int[][] matrixA, int[][] matrixB,
					int splitSize, int maxProducerSleepTime) {
		this.myBuffer = myBuffer;
		this.id = id;
		this.matrixA = matrixA;
		this.matrixB = matrixB;
		this.splitSize = splitSize;
		this.maxProducerSleepTime = maxProducerSleepTime;
	}
	
	/**
	 * Get how many milliseconds the producer has slept
	 * @return Total amount of time the producer has slept
	 */
	public int getProducerSleepTime() {
		return totalProducerSleepTime;
	}
	
	/**
	 * Get Procuder's copy of matrix C
	 * @return Producer's copy of matrix C
	 */
	public int[][] getMatrixC() {
		return matrixC;
	}
	
	/**
	 * Get the total number of items Producer has produced
	 * @return The number of produced work items
	 */
	public int getProducedWorkItems() {
		return producedWorkItems;
	}
	
	@Override
	public void run() {
		// set some initial variables
		int lowA = 0;
		int highA = lowA + splitSize;
		int lowB = 0;
		int highB = 0;
		boolean bIsDone = false;
		boolean aIsDone = false;
		int remainingA = splitSize;
		int[][] subA;
		int[][] subB;
		
		// loop enough times through to go through the whole of both arrays
		for (int x=0; x<(Math.floor(matrixA.length/splitSize) + 1)*(Math.floor((matrixB[0].length)/splitSize) + 1); x++) {
			lowB = highB;
			int remainingB = matrixB[0].length - highB; // how many more columns need to be processed
			
			if (bIsDone == true) { // all columns in matrix B added to buffer for current iteration of matrix A's rows
				// reset matrixB stuff
				lowB = 0;
				highB = 0;
				remainingB = matrixB[0].length;
				bIsDone = false;
				
				// go to next splitSize in matrixA
				lowA = highA;
				remainingA = matrixA.length - highA; // how many more rows need to be processed
			
				// if split size > how many rows there are left to process
				if (splitSize > remainingA) {
					highA += remainingA; // we only need however many rows are left
					aIsDone = true; // we have gone through all of matrix a's rows after this iteration
				}
				else {
					highA = lowA + splitSize; // increment highA to get splitSize more rows
				}
			} // end if bIsDone
			
			if (splitSize > remainingB) { // if split size > how many columns there are left to process
				highB += remainingB; // we only need however many rows are left
				bIsDone = true; // we have gone through all of matrix b's columns after this iteration
			}
			else {
				highB = lowB + splitSize; // increment highB to get splitSize more columns
			}
			
			// initialize sub arrays so we can insert fresh ones to the buffer every loop
			// if we have gone through all of both arrays except the last rows/columns
			if (aIsDone == true && bIsDone == true) {
				subA = new int[remainingA][matrixA[0].length];
				subB = new int[matrixB.length][remainingB];
			}
			// if we have gone through all of the columns of matrix b, except the last couple, for the current iteration of subA
			else if (bIsDone == true) {
				subA = new int[splitSize][matrixA[0].length];
				subB = new int[matrixB.length][remainingB];
			}
			// if we have gone through all of the row of matrix a, except the last couple
			else if (aIsDone == true) {
				subA = new int[remainingA][matrixA[0].length];
				subB = new int[matrixB.length][splitSize];
			}
			// we haven't gone through enough rows/columns to need to go to the next rows/columns
			else {
				subA = new int[splitSize][matrixA[0].length]; // splitSize # rows, normal # columns
				subB = new int[matrixB.length][splitSize]; // normal # rows, splitSize # columns
			}
			
			// get lowA to highA rows of matrixA
			int subRow = 0;
			for (int row=lowA; row<highA; row++) { // go through the rows of matrix A (from the low index to high index, determined by split size)
				for (int col=0; col<matrixA[0].length; col++) { // go through the columns of matrix A
					subA[subRow][col] = matrixA[row][col];
				}
				subRow++;
			}
			// get lowB to highB columns of matrix B
			for (int row=0; row<matrixB.length; row++) { // go through the rows of matrix B
				int subCol=0;
				for (int col=lowB; col<highB; col++) { // go through the columns of matrix B (from the low index to high index, determined by split size)
					subB[row][subCol] = matrixB[row][col];
					subCol++;
				}
			}
			
			// add work to buffer and increment item count
			myBuffer.put(new WorkItem(subA, subB, lowA, highA, lowB, highB, producedWorkItems, false), id);
			producedWorkItems++;
			
			try {
				// keep track of total sleep time of this thread
				int t = (int)(Math.random()*maxProducerSleepTime);
				totalProducerSleepTime += t;
				
				Thread.sleep(t); // put thread to sleep
			} catch(InterruptedException ie){
				
			} 
		} // end for loop
		
		matrixC = new int[matrixA.length][matrixB[0].length]; // matrixC's size = MxP where matrixA is MxN and matrixB is NxP
		
		// get all item's subC matrices and put them together into one matrix C
		while (pDoneFlag == false) {
			while (myBuffer.isDone() == false) {
				// do nothing if the consumers aren't finished.
				// once the consumers finish, they will set the buffer to done.
			}
			int colOffset = 0; // used to go to next appropriate column, according to split size
			int rowOffset = 0;// used to go to next appropriate row, according to split size
			
			// iterate through the list of done items, and add them to matrix C. Used the link below for synchronized list help.
			// https://howtodoinjava.com/java/collections/arraylist/synchronize-arraylist/
			synchronized(MatrixMultiplier.doneList) {
				Iterator<WorkItem> iterator = MatrixMultiplier.doneList.iterator(); // create iterator
				int nextDoneIndex = 0;
				while (iterator.hasNext()) {
					if (MatrixMultiplier.doneList.get(nextDoneIndex) != null) { // make sure next item isn't null
						nextDoneIndex++;
						WorkItem nextItem = iterator.next(); // get next item
						if (nextItem.isDone) { // check that item is done
							for (int row=0; row<nextItem.subC.length; row++) { // go through the rows of subC matrix
								for (int col=0; col<nextItem.subC[0].length; col++) { // go through the columns of subC matrix
									matrixC[row + (splitSize * rowOffset)][col + (splitSize * colOffset)] = nextItem.subC[row][col]; // put values in matrixC
								}
							}
						}
						colOffset++;
						
						// make sure column offset doesn't go too far.
						// Ex. matrix B 4x10, splitSize = 3 -> floor(10/3) + 1 = 4
						// Ex. contd. 4 * 3 = 12; we don't want to offset to 12 (we would want to go to 10 at most), so reset to 0. 
						if (colOffset == (Math.floor((matrixB[0].length)/splitSize) + 1)) {
							colOffset = 0;
							rowOffset++;
						}
						// make sure row offset doesn't go too far.
						// Ex. matrix A 10x4, splitSize = 3 -> floor(10/3) + 1 = 4
						// ex contd. 4 * 3 = 12; we don't want to offset to 12 (we would want to go to 10 at most), so reset to 0.
						if (rowOffset == (Math.floor(matrixA.length/splitSize) + 1)) {
							rowOffset = 0;
						}
					} // end if next item on doneList is not null
					else {
						break; // next item on doneList is null
					}
				} // end while (iterator.hasNext())
			} // end combining subC matrices into matrix C
			
			pDoneFlag = true; // producer has finished its job
			
		} // end while(pDoneFlag == false)
		System.out.println("Producer " + id + " finished");
	} // end run()
	
} // end Producer class
