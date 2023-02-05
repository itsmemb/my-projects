import java.util.LinkedList;
import java.util.Queue;

public class SharedBuffer {
	private Queue<WorkItem> buffer = new LinkedList<>();
	private WorkItem curItem;
	private int maxBufferSize;
	private int emptyCount = 0;
	private int fullCount = 0;
	private boolean bAvailable = false;

	/**
	 * Constructs a SharedBuffer object
	 * @param maxBufferSize Maximum size of the buffer
	 */
	public SharedBuffer(int maxBufferSize) {
		this.maxBufferSize = maxBufferSize;
	}

	/**
	 * Returns the number of time the buffer has been empty when an object tries to get from it
	 * @return The buffer's empty count
	 */
	public int getEmptyCount() {
		return emptyCount;
	}
	
	/**
	 * Returns the number of time the buffer has been full when an object tries to add to it
	 * @return The buffer's full count
	 */
	public int getFullCount() {
		return fullCount;
	}

	/**
	 * Checks if the buffer is done and returns a boolean value
	 * @return True if the buffer is done, False if the buffer is empty
	 */
	public synchronized boolean isDone() {
		if (MatrixMultiplier.consumerStop == true && buffer.isEmpty()) {
			return true;
		}
		else {
			return false;
		}
	} // end isDone
	
	/**
	 * Synchronously removes and returns a WorkItem from the buffer
	 * @param id The id of the object getting a WorkItem from the buffer
	 * @return The head of this buffer
	 */
	public synchronized WorkItem get(int id) {
		 while (bAvailable == false || buffer.size() == 0 ) {
			 // if the buffer is done return a null
			 if (isDone() == true) {
				 return null;
			 }
			 try {
				 emptyCount++; // increment emptyCount
	            if (buffer.size() == 0)
	           		System.out.println("The queue is empty. Consumer " + id + " is waiting");
	            wait(200); // wait for 1000 ms
			 } catch (InterruptedException e) { }
		 }
		curItem = buffer.remove(); // get the head of the buffer
     	System.out.println("Consumer " + id + " gets row " + curItem.lowA + "-" + curItem.highA + " of matrix A and column "
     		+ curItem.lowB + "-" + curItem.highB + " of B from buffer");
     	bAvailable = false;
     	notifyAll();
     	return curItem; // return the head of the buffer
	} // end get
	
	/**
	 * Synchronously adds an item to the tail of this buffer
	 * @param item WorkItem to be added to the buffer
	 * @param id The id of the object putting a WorkItem in the buffer
	 */
    public synchronized void put(WorkItem item, int id) {
        while (bAvailable == true || buffer.size() == maxBufferSize) {
            try {
            	fullCount++;
            	if (buffer.size() == maxBufferSize)
            		System.out.println("The queue is full. Producer " + id + " is waiting");
                wait();
            } catch (InterruptedException e) { }
        }
        buffer.add(item); // add to the tail of the buffer
        System.out.println("Producer " + id + " puts row " + item.lowA + "-" + item.highA + " of matrix A and column "
				+ item.lowB + "-" + item.highB + " of B to buffer");
        bAvailable = true;
        notifyAll();
    } // end put

} // end SharedBuffer class
