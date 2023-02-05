public class Consumer implements Runnable {
	private SharedBuffer myBuffer;
	private int id;
	private int maxConsumerSleepTime;
	private int consumedWorkItems;
	private int totalConsumerSleepTime;
	
	/**
	 * Constructs a Consumer with a shared buffer, id, and max sleep time (per cycle)
	 * @param myBuffer The shared buffer of WorkItems
	 * @param id The id of this Consumer
	 * @param maxConsumerSleepTime How long the Consumer can sleep per cycle
	 */
	public Consumer(SharedBuffer myBuffer, int id, int maxConsumerSleepTime) {
		this.myBuffer = myBuffer;
		this.id = id;
		this.maxConsumerSleepTime = maxConsumerSleepTime;
	}
	
	/**
	 * Get how many milliseconds the Consumer has slept
	 * @return Total time in milliseconds this Consumer has slept
	 */
	public int getConsumerSleepTime() {
		return totalConsumerSleepTime;
	}
	
	/**
	 * Get how many times the Consumer has taken a non-null WorkItem from the buffer
	 * @return Number of consumed work items for this Consumer
	 */
	public int getConsumedWorkItems() {
		return consumedWorkItems;
	}
	
	/**
	 * Get the id of the Consumer
	 * @return Id of the Consumer
	 */
	public int getConsumerId() {
		return id;
	}
	
	@Override
	public void run() {
		WorkItem item;
		StringBuffer sb = null;
		
		// loop while the consumer should be looping
		while (MatrixMultiplier.consumerStop == false) {
			// if buffer isn't done
			if (myBuffer.isDone() == false) {
				item = myBuffer.get(id); // get next item in buffer
				if (item == null) // if we got a null item (timeout waiting and buffer is done)
					break;
				consumedWorkItems++; // we got a valid WorkItem, increment
				
				// Multiply matrices together
				item.subC = new int[item.subA.length][item.subB[0].length];
				for (int m=0; m<item.subA.length; m++) {
					for (int p=0; p<item.subB[m].length; p++) {
						int sum = 0;
						for (int n=0; n<item.subA[m].length; n++) {
							// get sum of subA row m * subB column p
							sum += item.subA[m][n] * item.subB[n][p];
						}
						item.subC[m][p] = sum; // put the sum into subC
					}
				} // end multiplication of matrices
				
				// initialize a String buffer and add to it.
				sb = new StringBuffer();
				sb.append("Consumer " + id + " finishes calculating\n");
				
				item.setDone(true); // set item to done
				MatrixMultiplier.doneList.set(item.itemId, item); // add item to doneList
				
				// append String for the multiplication of the matrices to StringBuffer
				sb.append("\t[ ");
				for (int r=0; r<item.subA.length; r++) {
					sb.append("[");
					for (int c=0; c<item.subA[0].length; c++) {
						if (c != item.subA[0].length -1) {
							sb.append(item.subA[r][c] + " ");
						}
						else {
							sb.append(item.subA[r][c]);
						}
					}
					if (r != item.subA.length -1) {
						sb.append("], \n\t");
					}
					else {
						sb.append("]");
					}
				}
				sb.append(" ]\n\n");
				
				sb.append("x\t[ ");
				for (int r=0; r<item.subB.length; r++) {
					sb.append("[");
					for (int c=0; c<item.subB[0].length; c++) {
						if (c != item.subB[0].length -1) {
							sb.append(item.subB[r][c] + " ");
						}
						else {
							sb.append(item.subB[r][c]);
						}
					}
					if (r != item.subB.length -1) {
						sb.append("], \n\t");
					}
					else {
						sb.append("]");
					}
				}
				sb.append(" ]\n\n");
				
				sb.append("=>\t[ ");
				for (int r=0; r<item.subC.length; r++) {
					sb.append("[");
					for (int c=0; c<item.subC[0].length; c++) {
						if (c != item.subC[0].length -1) {
							sb.append(item.subC[r][c] + " ");
						}
						else {
							sb.append(item.subC[r][c]);
						}
					}
					if (r != item.subC.length -1) {
						sb.append("], \n\t");
					}
					else {
						sb.append("]");
					}
				}
				sb.append(" ]\n");
			} // we have added all of the multiplication parts to a StringBuffer
			System.out.println(sb.toString()); // print the contents of StringBuffer
			
			try {
				// put Consumer to sleep and keep track of how long
				int t = (int)(Math.random()*maxConsumerSleepTime);
				totalConsumerSleepTime += t;
				Thread.sleep(t);
			} catch(InterruptedException ie){
				
			}
		} // end while (stopFlag == false)
		System.out.println("Consumer " + id + " finished");
	} // end run
	
} // end Consumer class
