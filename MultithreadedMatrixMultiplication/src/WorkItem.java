public class WorkItem {
	int[][] subA;
	int[][] subB;
	int[][] subC;
	int lowA;
	int highA;
	int lowB;
	int highB;
	int itemId;
	boolean isDone = false;
	
	public WorkItem(int[][] subA, int[][] subB, int lowA, int highA, int lowB, int highB, int itemId, boolean isDone) {
		this.subA = subA;
		this.subB = subB;
		this.lowA = lowA;
		this.highA = highA;
		this.lowB = lowB;
		this.highB = highB;
		this.itemId = itemId;
		this.isDone = isDone;
	}
	
	public void setDone(boolean isDone) {
		this.isDone = isDone;
	}
	
	public boolean isDone() {
		return isDone;
	}
}
