public class PCB {

    private String name;

    private int pid;
    private int state;
    private int arvTime;
    private int startTime;
    private int finTime;
    private int cpuWaitTime;
    private int ioWaitTime;
    private int priority;
    private int[] burstList;
    private int index; // index we are at in the burstList
    private int[] cpuBurstList;
    private int cpuBurstIndex;
    private int[] IOBurstList;
    private int IOBurstIndex;
    private boolean started;
   
    
 

    // constructor
    public PCB(String[] processArray) {

        this.state = 1;
        this.name = processArray[0];
        this.arvTime = Integer.parseInt(processArray[1]);
        this.priority = Integer.parseInt(processArray[2]);

        this.burstList = new int[processArray.length - 3];
        
        this.index = 0;
        this.cpuBurstIndex = 0;
        this.IOBurstIndex = 0;
        
        this.cpuWaitTime = 0;
        this.ioWaitTime = 0;
        this.startTime = 0;
        this.finTime = 0;
        
        this.started = false;
        
        
        for (int i = 3; i < processArray.length; i++) {
            this.burstList[i - 3] = Integer.parseInt(processArray[i]);
        }
        
        if (burstList.length % 2 == 0) {
            this.cpuBurstList = new int[burstList.length / 2];
        }
        else {
            this.cpuBurstList = new int[(burstList.length + 1) / 2];
            this.IOBurstList = new int[(burstList.length - 1) / 2];
        }
        
        int j = 0;
        for (int i = 0; i < burstList.length; i++) {
        	if (i % 2 == 0) {
        		cpuBurstList[j] = burstList[i];
        		j++;
        	}
        	
        }
        
        int k = 0;
        for (int i = 0; i < burstList.length; i++) {
        	if (i % 2 == 1) {
        		IOBurstList[k] = burstList[i];
        		k++;
        	}
        }
        
   
      
    }// end constructor

    //	turnaround time	= finish time - arrival time
    //	waiting time	= turnaround time - burst time
    //	calculate here or in client?  need finish time from somewhere.  a Gantt?  won't have until after scheduled or run?
    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
    
    public int getPID() {
        return pid;
    }

    public void setPID(int PID) {
        this.pid = PID;
    }
    

    public int[] getBurstList() {
        return burstList;
    }

    public void setBurstList(int[] burstList) {
        this.burstList = burstList;
    }
    
    public int getArvTime() {
    	return this.arvTime;
    }
    
    public void setCurIndex(int num) {
    	this.index = num;
    }
    
    public int getCurIndex() {
    	return this.index;
    }
    
    public int[] getCpuBurstList() {
    	return this.cpuBurstList;
    }
    
    public int getCpuBurstIndex() {
    	return this.cpuBurstIndex;
    }
    
    public void setCpuBurstIndex(int num) {
    	this.cpuBurstIndex = num;
    }
    
    public int[] getIOBurstList() {
    	return this.IOBurstList;
    }
    
    public int getIOBurstIndex() {
    	return this.IOBurstIndex;
    }
    
    public void setIOBurstIndex(int num) {
    	this.IOBurstIndex = num;
    }
    
    public void incCpuWaitTime() {
    	this.cpuWaitTime++;
    }
    
    public int getCpuWaitTime() {
    	return this.cpuWaitTime;
    }
    
    public void incIOWaitTime() {
    	this.ioWaitTime++;
    }
    
    public int getIOWaitTime() {
    	return this.ioWaitTime;
    }
    
    public void setFinishTime(int time) {
    	this.finTime = time;
    }
    
    public int getFinishTime() {
    	return this.finTime;
    }
    
    public String getName() {
    	return this.name;
    }
    
    public int getPriority() {
    	return this.priority;
    }
    
    public int getNextCpuBurst() {
    	return this.getCpuBurstList()[this.cpuBurstIndex];
    }
    
    public boolean getStarted() {
    	return this.started;
    }
    
    public void setStarted() {
    	this.started = true;
    }
    
    public void setStartTime(int start) {
    	this.startTime = start;
    }
    
    public int getStartTime() {
    	return this.startTime;
    }
}
