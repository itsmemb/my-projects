/**
 * Class: CS 405
 * Names: Ben, Phil, Max
 * Date: 3/13/21
 * Project: Simulate scheduling algorithms and their statistics.
 * 			Reads an input file and prints to results files.
 * 			Use processExample.txt as the input file when running the program.
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import javax.swing.JFileChooser;
import java.io.FileWriter;
import java.io.PrintWriter;

public class CpuSchedulingSimulator {

    /**
     * @param args the command line arguments
     */
    static LinkedList<PCB> pcbList = new LinkedList<PCB>();
    static int mode = -2;	// NEW FOR MODE
    static int unit = -2;	// unit is for ms or fps
    static Scanner console = new Scanner(System.in);
    static JFileChooser open = new JFileChooser("./");
    
    public static void main(String[] args) throws FileNotFoundException {

        int algorithm = -2;
        
        //loop in case user wants to run a different algorithm
        LOOP: // label loop so we can quit later
        while (algorithm != -1) {
        	// initialize here to reset variables
        	pcbList = new LinkedList<PCB>();
        	int quantum = -2;
        	int simUnitTime = -1;
        	mode = -2;
        	unit = -2;
        	
        	// get auto or manual mode
            while (mode < -1 || mode > 1) {
                try {
                	System.out.print("Please select mode of execution (Auto = 0, Manual = 1, Exit = -1): ");
                    mode = console.nextInt();
                    
                    if (mode == -1) {
                    	break LOOP;
                    }
                    else if (mode < 0) {
                        System.out.println("Choice too low, try again.");
                    }
                    else if (mode > 1) {
                        System.out.println("Choice too high, try again.");
                    }
                } catch(InputMismatchException e) {
                    System.out.println("Invalid input, try again");
                    console.next();
                }
                
            } // end get mode
            
            // get sim unit time
            if (mode == 0) {
            	while (unit < 0 || unit > 1) {
            		try {
            			System.out.print("Would you like to simulate each step in milliseconds or frames per seconds?(0 for ms, 1 for fps): ");
            			unit = console.nextInt();
            			
            			if (unit < 0) {
            				System.out.println("Choice too low, try again.");
            			}
            			if (unit > 1) {
            				System.out.println("Choice too high, try again.");
            			}
                    
            			while (simUnitTime <= 0) {
	            			try {
		            			System.out.print("\nPlease enter simulation time: ");
		            			simUnitTime = console.nextInt();
		            			
		            			if (simUnitTime <= 0) {
		            				System.out.println("Choice too low, try again.");
		            			}
	            			} catch (InputMismatchException e) {
	            				System.out.println("Invalid input, try again");
	                			console.next();
	            			}
            			}
            		} catch(InputMismatchException e) {
            			System.out.println("Invalid input, try again");
            			console.next();
            		}
            	}
            } // end get sim unit time
            
            readFile(); // call readFile
            
            // get user algorithm choice
            try {
            	System.out.println("\n****[Select scheduling algorithm]****"
                		+ "\n"
                        + "\n 0:	First Come First Serve"
                        + "\n 1:	Shortest Job First"
                        + "\n 2:	Priority Scheduling"
                        + "\n 3:	Round-Robin"
                        + "\n-1:	Exit Program"
                        + "\n"
                        + "\n*************************************");
                System.out.print(">>> ");
                algorithm = console.nextInt();
                
                if (algorithm < -1) {
                    System.out.println("Choice too low, try again.");
                }
                else if (algorithm > 3) {
                    System.out.println("Choice too high, try again.");
                }
            } catch(InputMismatchException e) {
                System.out.println("Invalid input, try again");
                console.next();
            } // end get user algorithm choice
            
            // switch based on algorithm choice
			switch (algorithm) {
	
			// call first come first served
			case 0:
				fcfs(simUnitTime);
				break;
	
			// call shortest job first
			case 1:
				sjf(simUnitTime);
				break;

			// call priority scheduling
			case 2:
				ps(simUnitTime);
				break;

			// call round robin
			case 3:
				while (quantum != -1 && quantum < 1) {
					try {
						System.out.print("Enter quantum time in ms(-1 to quit): ");
						quantum = console.nextInt();

						if (quantum < -1)
							System.out.println("Quantum time can't be negative, try again.");

						else if (quantum == -1) {
							System.out.println("Program exiting.");
							System.exit(0);
						} else if (quantum == 0)
							System.out.println("Quantum time can't equal 0 ms, try again.");

					} catch (InputMismatchException e) {
						System.out.println("Invalid input, try again.");
						console.next();
					}
				}
				rr(simUnitTime, quantum);
				break;

			// quit
			case -1:
				System.out.println("Program exiting.");
				break;

			// unrecognized
			default:
				System.out.print("unrecognized command");

			} // end switch(algorithm)
        }
        if (mode == -1) {
        	System.out.println("Program exiting.");
        }
        console.close();

    }

    // reads file, uses each line as parameters to create an instance of pcb
    // stores each pcb object in a LinkedList
    // assumes file stored in project folder
    public static void readFile() throws FileNotFoundException {

    	String file = "";
    	//select the file to be read
    	while (true) {
    		int status = open.showOpenDialog(null);
	    	  
    		if (status == JFileChooser.APPROVE_OPTION) {
    			file = open.getSelectedFile().getAbsolutePath();
    			break;
    		}
		}
        BufferedReader readFile = new BufferedReader(new FileReader(file));

        String process;

        try {
        	
        	int count = 0;
            while ((process = readFile.readLine()) != null) {

                // break apart input line into process parameters and store in array
                process = process.strip();

                String[] processValues = process.split(" ");

                // create new pcb object using parameters from input line
                PCB processState = new PCB(processValues);
                processState.setPID(count);
                count++;

                // store each pcb object into linkedlist for queue parameters
                pcbList.add(processState);

            }
            // sort the LL by arrival time
            pcbList.sort(Comparator.comparing(PCB:: getArvTime));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    } // end readFile()
    
    public static void fcfs(long simUnitTime) {
    	Queue<PCB> readyQueue = new LinkedList<PCB>();
    	Queue<PCB> waitQueue = new LinkedList<PCB>();
    	PCB cpu = null;
    	PCB io = null;
    	int simStep = 0; // each simStep represents our runtime
    	double utilization = -1;
		double avgWait = 0;
		double avgTurn = 0;
		double throughput = 0;
    	
    	FileWriter fWriter;
        PrintWriter pWriter = null;
		try {
			fWriter = new FileWriter ("fcfsResults.txt");
			pWriter = new PrintWriter (fWriter);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
 	
    	// if haven't gotten to first process yet, change the runtime
    	if (pcbList.getFirst().getArvTime() > simStep)
	 		simStep = pcbList.getFirst().getArvTime();
 	
    	// one loop = one simulation step
    	while (true) {
    		// if all process terminated, done
    		int termCounter = 0;
    		for (int i = 0; i < pcbList.size(); i++) {
    			if (pcbList.get(i).getState() == 5) 
    				termCounter++;
    			else
    				break; // exit for loop if one of the processes are not terminated.
    		}
    		if (termCounter == pcbList.size()) {
    			simStep -= 1;
    			if (pWriter != null) {
    				pWriter.println("Simulation finished."
    						+ "\n\nSimulation statistics"
    						+ "\n------------------------------"
    						+ "\nSimulation steps: " + simStep
    						+ "\nCPU utilization: " + Math.round(utilization / simStep * 100) + "%"
    						+ "\nAverage wait time: " + avgWait + " ms"
    						+ "\nAverage turnaround time: " + avgTurn + " ms"
    						+ "\nThroughput: " + throughput + " processes per ms");
				}
    			break; // done
    		}
    		
    		// move all processes that finish their CPU burst to IO wait queue
    		if (cpu != null) {
    			if (cpu.getCpuBurstList()[cpu.getCpuBurstIndex()] == 0) { // if our current burst is 0
    				if (cpu.getCpuBurstIndex() == cpu.getCpuBurstList().length - 1) { // if we finished our last CPU burst
    					cpu.setFinishTime(simStep); // set the finish time of the process
    					cpu.setState(5); // terminated
    					
    					if (pWriter != null) {
        					pWriter.println(cpu.getName() + " has terminated.\n"
        							+ cpu.getName() + " turnaround time: " + (cpu.getFinishTime() - cpu.getArvTime()) + "ms.\n"
        							+ cpu.getName() + " total CPU wait time: " + cpu.getCpuWaitTime() + "ms.\n"
        							+ cpu.getName() + " total I/O wait time: " + cpu.getIOWaitTime() + "ms.");
        				}
    					cpu = null;
    				}
    				else {
    					cpu.setState(4); // set it to waiting
    					waitQueue.add(cpu);
    					cpu.setCpuBurstIndex(cpu.getCpuBurstIndex() + 1); // done with that burst
    					
    					if (pWriter != null) {
        					pWriter.println(cpu.getName() + " put into I/O queue.");
        				}
    					cpu = null;
    				}
    			}
    		}
    		// move all processes that finish their IO burst to ready queue
    		if (io != null) {
    			if (io.getIOBurstList()[io.getIOBurstIndex()] == 0) { // if our current burst is 0
    				io.setState(2); // set it to ready
    				readyQueue.add(io);
    				
    				if (pWriter != null) {
    					pWriter.println(io.getName() + " completed I/O, returned to ready queue.");
    				}
    				if (io.getIOBurstIndex() == io.getIOBurstList().length - 1) {
    					io = null;
    				}
    				else {
    					io.setIOBurstIndex(io.getIOBurstIndex() + 1); // done with that burst
    					io = null;
    				}
    			}
    		}
    		
    		// populate readyQueue with processes that have arrived
    		for (int i = 0; i < pcbList.size(); i++) {
    			if (pcbList.get(i).getArvTime() == simStep) {
        			PCB newProc = pcbList.get(i);
        			readyQueue.add(newProc);
            		newProc.setState(2);
            		
            		if (pWriter != null) {
    					pWriter.println(newProc.getName() + " put in ready queue.");
    				}
        		}
    		}
    		
    		// fill the cpu and io
    		if (!readyQueue.isEmpty() && cpu == null) {
    			cpu = readyQueue.remove();
    			if (cpu.getStarted() == false) {
    				cpu.setStarted(); // set it to true
    				cpu.setStartTime(simStep); // got the start time
    			}
    			cpu.setState(3);
    			
    			if (pWriter != null) {
					pWriter.println(cpu.getName() + " dispatched from the ready queue to use the CPU.");
				}
    		}

    		if (!waitQueue.isEmpty() && io == null) {
    			io = waitQueue.remove();
    		}
    		
    		// reduce 1 from both bursts
    		if (cpu != null)
    			cpu.getCpuBurstList()[cpu.getCpuBurstIndex()]--; // decrement the current burst by 1
    		if (io != null)
    			io.getIOBurstList()[io.getIOBurstIndex()]--;

    		// all processes in ready queue will increase waiting time
    		Iterator<PCB> itr = readyQueue.iterator();
    		while (itr.hasNext()) {
    			itr.next().incCpuWaitTime();
    		}
    		//src: https://www.techiedelight.com/iterate-through-queue-java/ 
    		// all processes in wait queue will increase waiting time
    		Iterator<PCB> itr2 = waitQueue.iterator();
    		while (itr2.hasNext()) {
    			itr2.next().incIOWaitTime();
    		}

    		// here, need to display the current state of the CPU, IO Device, ready queue, IO waiting queue, and all processes
    		String cpuState;
    		String ioState;
    		if (cpu == null) {
    			cpuState = "idle";
    		}
    		else {
    			cpuState = "running " + cpu.getName();
    			utilization += 1;
    		}
    		if (io == null) ioState = "idle";
    		else ioState = "running " + io.getName();
    		
    		if (utilization <= 0) {
    			System.out.println("\nSimulation step: " + simStep 
    					+ "\n\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af" 
    					+ "\n| CPU: "  + cpuState + ", utilization: 0%" + "\n____________________________" 
    					+ "\n\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af" 
    					+ "\n| I/O Device: " + ioState + " |" + "\n____________________");
    		}
    		else {
    			System.out.print("\nSimulation step: " + simStep 
    					+ "\n\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af" 
    					+ "\n| CPU: "  + cpuState + ", utilization: " + Math.round(utilization / simStep * 100) + "%" 
    					+ "\n____________________________");
    			
    			if( ioState.compareToIgnoreCase("idle") == 0 ) {
    				System.out.println("\n\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af"  + "\n| I/O Device: " + ioState + " |" + "\n____________________");
    			}else {
    				System.out.println("\n\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af"  
    						+ "\n| I/O Device: "  + ioState + "\n____________________________");
    			}
    			
    		}
    		
    		double totalWait = 0;
    		double totalTurnaround = 0;
    		double totalCompleted = 0;
    		for (int i = 0; i < pcbList.size(); i++) {
    			totalWait += pcbList.get(i).getCpuWaitTime();
    			if (pcbList.get(i).getState() == 5) // only want to get turnaround if process is terminated
    				totalTurnaround += pcbList.get(i).getFinishTime() - pcbList.get(i).getArvTime();
    			if (pcbList.get(i).getState() == 5)
    				totalCompleted += 1;		
    		}
    		avgWait = totalWait / pcbList.size();
    		avgTurn = totalTurnaround / pcbList.size();
    		throughput = totalCompleted / simStep;
    		System.out.println("Average Wait Time: " + avgWait + "\nAverage Turnaround Time: " + avgTurn + "\nThroughput: " + throughput);
    		
    		
    		System.out.print("Ready Queue: ");
    		Iterator<PCB> itr3 = readyQueue.iterator();
    		while (itr3.hasNext()) {
    			System.out.print(itr3.next().getName() + ", ");
    		}
    		
    		System.out.print("\nWait Queue: ");
    		Iterator<PCB> itr4 = waitQueue.iterator();
    		while (itr4.hasNext()) {
    			System.out.print(itr4.next().getName() + ", ");
    		}
    		
    		// Print the algorithm statistics: 
        	for (int i = 0; i < pcbList.size(); i++) {
    			PCB temp = pcbList.get(i);
    			int num = temp.getState();
    			String state = null;
    			if (num == 1) state = "new";
    			else if (num == 2) state = "ready";
    			else if (num == 3) state = "running";
    			else if (num == 4) state = "waiting";
    			else if (num == 5) state = "terminated";
    			System.out.print("\nProcess: " + temp.getName() + "\tState: " + state);
    			
    			System.out.print("\tId: " + temp.getPID() + "\tArrival: " + temp.getArvTime() + "\tPriority: " + temp.getPriority() + "\tCPU Bursts: ");
    			for (int j = 0; j < temp.getCpuBurstList().length; j++) {
    				System.out.print(temp.getCpuBurstList()[j] + " ");
    			}
    			System.out.print("\tIO Bursts: ");
    			for (int k = 0; k < temp.getIOBurstList().length; k++) {
    				System.out.print(temp.getIOBurstList()[k] + " ");
    			}
    			System.out.print("\tStart Time: " + temp.getStartTime() + "\tFinishTime: " + temp.getFinishTime() + "\tWait Time: " + temp.getCpuWaitTime() + "\tWait IO Time: " + temp.getIOWaitTime());
    			if (temp.getStarted() == false)
    				System.out.print("\tResponse Time: not started");
    			else
    				System.out.print("\tResponse Time: " + (temp.getStartTime() - temp.getArvTime()));
        	}
        	System.out.println();
        	// Manual handling
    		if( mode == 1 ) {
    			while (true) {
	            	System.out.println("\nContinue to next simulation step?(Enter to continue): "
							+ "\nToggle execution mode?(a to toggle mode):");
	            	System.out.print(">>> ");
	            	String modeOption = console.nextLine();
	            	
	            	if (!modeOption.isEmpty()) {
						if(modeOption.trim().equals("")) {
							System.out.println("Program exiting.");
							System.exit(0);
						}
						if (modeOption.trim().equalsIgnoreCase("a")) {
							mode = 0;
							break;
						}
					}
					else if (modeOption.isEmpty()) {
						break;
					}
    	        }
    			
    		} // end manual handling
    		
    		if (mode == 0) {
    			if (unit == 0) {
    				try {
    					Thread.sleep(simUnitTime);
    				} catch (InterruptedException e) {
    					e.printStackTrace();
    				}
    			}
    			if (unit == 1 && (simStep + 1) % simUnitTime == 0) {
    				try {
    					Thread.sleep(simUnitTime * 1000);
    				} catch (InterruptedException e) {
    					e.printStackTrace();
    				}
    			}
    		}
    		
    		// next simulation step
    		simStep++;
		} // end while loop
    	
    	String response = "";
    	while (!response.equalsIgnoreCase("yes") && !response.equalsIgnoreCase("y") && !response.equalsIgnoreCase("no") && !response.equalsIgnoreCase("n")) {
    		System.out.print("\nWould you like to save the results to file? (y/n): ");
    		response = console.next();
    	}
    	
    	if (pWriter != null && response.toLowerCase().charAt(0) == 'y') {
    		pWriter.close();
    	}
    } // end fcfs
    
    public static void sjf(long simUnitTime) {
    	Queue<PCB> readyQueue = new LinkedList<PCB>();
    	Queue<PCB> waitQueue = new LinkedList<PCB>();
    	PCB cpu = null;
    	PCB io = null;
    	int simStep = 0; // each simStep represents our runtime
    	double utilization = -1;
		double avgWait = 0;
		double avgTurn = 0;
		double throughput = 0;
    	
    	FileWriter fWriter;
        PrintWriter pWriter = null;
		try {
			fWriter = new FileWriter ("sjfResults.txt");
			pWriter = new PrintWriter (fWriter);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
 	
    	// if haven't gotten to first process yet, change the runtime
    	if (pcbList.getFirst().getArvTime() > simStep)
	 		simStep = pcbList.getFirst().getArvTime();
 	
    	// one loop = one simulation step
    	while (true) {
    		// if all process terminated, done
    		int termCounter = 0;
    		for (int i = 0; i < pcbList.size(); i++) {
    			if (pcbList.get(i).getState() == 5) 
    				termCounter++;
    		}
    		if (termCounter == pcbList.size()) {
    			simStep -= 1;
    			if (pWriter != null) {
    				pWriter.println("Simulation finished."
    						+ "\n\nSimulation statistics"
    						+ "\n------------------------------"
    						+ "\nSimulation steps: " + simStep
    						+ "\nCPU utilization: " + Math.round(utilization / simStep * 100) + "%"
    						+ "\nAverage wait time: " + avgWait + " ms"
    						+ "\nAverage turnaround time: " + avgTurn + " ms"
    						+ "\nThroughput: " + throughput + " processes per ms");
				}
    			break; // done
    		}
    		
    		
    		// move all processes that finish their CPU burst to IO wait queue
    		if (cpu != null) {
    			if (cpu.getCpuBurstList()[cpu.getCpuBurstIndex()] == 0) { // if our current burst is 0
    				if (cpu.getCpuBurstIndex() == cpu.getCpuBurstList().length - 1) { // if we finished our last CPU burst
    					cpu.setFinishTime(simStep); // set the finish time of the process
    					cpu.setState(5); // terminated
    					
    					if (pWriter != null) {
        					pWriter.println(cpu.getName() + " has terminated.\n"
        							+ cpu.getName() + " turnaround time: " + (cpu.getFinishTime() - cpu.getArvTime()) + "ms.\n"
        							+ cpu.getName() + " total CPU wait time: " + cpu.getCpuWaitTime() + "ms.\n"
        							+ cpu.getName() + " total I/O wait time: " + cpu.getIOWaitTime() + "ms.");
        				}
    					cpu = null;
    				}
    				else {
    					cpu.setState(4); // set it to waiting
    					waitQueue.add(cpu);
    					cpu.setCpuBurstIndex(cpu.getCpuBurstIndex() + 1); // done with that burst
    					
    					if (pWriter != null) {
        					pWriter.println(cpu.getName() + " put into I/O queue.");
        				}
    					cpu = null;
    				}
    			}
    		}
    		// move all processes that finish their IO burst to ready queue
    		if (io != null) {
    			if (io.getIOBurstList()[io.getIOBurstIndex()] == 0) { // if our current burst is 0
    				io.setState(2); // set it to ready
    				
    				// START OF ADDED SECTION - have to add io into ready queue sorted by sjf
    				if (readyQueue.isEmpty()) {
    					readyQueue.add(io);
    					io.setState(2);
    					
    					if (pWriter != null) {
        					pWriter.println(io.getName() + " completed I/O, returned to ready queue.");
        				}
    				}
    				else {
    					int size = readyQueue.size();
    					for (int j = 0; j < size; j++) { // might have issue here
    						if (readyQueue.peek().getNextCpuBurst() < io.getNextCpuBurst()) { // if head is more prioritized, simply add
    							readyQueue.add(readyQueue.remove()); // add head of queue to end
    						}
    						else { // if io is more prioritized
    							if (!readyQueue.contains(io)) { // stops us from adding it more than once
        							readyQueue.add(io);
        							if (pWriter != null) {
            	    					pWriter.println(io.getName() + " completed I/O, returned to ready queue.");
            	    				}
        							io.setState(2);
        						}
    							readyQueue.add(readyQueue.remove());
    						}
    					}
    					if (!readyQueue.contains(io)) { // this happens if newProc's priority is greater than all in ready queue. It wouldn't have gotten added, so add it in now.
    						readyQueue.add(io);
    						io.setState(2);
    						
    						if (pWriter != null) {
    	    					pWriter.println(io.getName() + " completed I/O, returned to ready queue.");
    	    				}
    					}
    				}
            		// END OF ADDED SECTION
            		
    				if (io.getIOBurstIndex() == io.getIOBurstList().length - 1) { // if that was last io burst, we don't want to increment index
    					io = null;
    				}
    				else {
    					io.setIOBurstIndex(io.getIOBurstIndex() + 1); // done with that burst
    					io = null;
    				}
    			}
    		}
    		
    		// add in the new process, if there is one
    		for (int i = 0; i < pcbList.size(); i++) {
    			if (pcbList.get(i).getArvTime() == simStep) {
        			PCB newProc = pcbList.get(i);
        			newProc.setState(2);
        			// START OF ADDED SECTION - have to add new process into ready queue sorted by priority
        			if (readyQueue.isEmpty()) {
        				readyQueue.add(newProc);
        				newProc.setState(2);
        				
        				if (pWriter != null) {
        					pWriter.println(newProc.getName() + " put in ready queue.");
        				}
        			}
        			else {
        				int size = readyQueue.size();
        				//readyQueue.add(newProc);
        				for (int j = 0; j < size; j++) { // might have issue here
        					if (readyQueue.peek().getNextCpuBurst() < newProc.getNextCpuBurst()) { // if head is more prioritized, simply add
        						readyQueue.add(readyQueue.remove()); // add head of queue to end
        					}
        					else { // if newProc is more prioritized
        						if (!readyQueue.contains(newProc)) { // stops us from adding it more than once
        							readyQueue.add(newProc);
        							newProc.setState(2);
        							
        							if (pWriter != null) {
                    					pWriter.println(newProc.getName() + " put in ready queue.");
                    				}
        						}
        						readyQueue.add(readyQueue.remove());
        					}
        				}
        				if (!readyQueue.contains(newProc)) { // this happens if newProc's priority is greater than all in ready queue. It wouldn't have gotten added, so add it in now.
        					readyQueue.add(newProc);
        					newProc.setState(2);
        					
        					if (pWriter != null) {
            					pWriter.println(newProc.getName() + " put in ready queue.");
            				}
        				}
        			}
            		// END OF ADDED SECTION
        		}
        		
    		}
    		
    		// fill the cpu and io
    		if (!readyQueue.isEmpty() && cpu == null) {
    			cpu = readyQueue.remove();
    			if (cpu.getStarted() == false) {
    				cpu.setStarted(); // set it to true
    				cpu.setStartTime(simStep);
    			}
    			if (pWriter != null) {
					pWriter.println(cpu.getName() + " dispatched from the ready queue to use the CPU.");
				}
    			cpu.setState(3);
    		}
    		
    		// START OF ADDED SECTION
    		if (!readyQueue.isEmpty() && readyQueue.peek().getNextCpuBurst() < cpu.getNextCpuBurst()) {
    			int size = readyQueue.size();
    			for (int j = 0; j < size; j++) { // might have issue here
        			if (readyQueue.peek().getNextCpuBurst() < cpu.getNextCpuBurst()) { // if head is more prioritized, simply add
        				readyQueue.add(readyQueue.remove()); // add head of queue to end
        			}
        			else { // if newProc is more prioritized
        				if (!readyQueue.contains(cpu)) { // stops us from adding it more than once
							readyQueue.add(cpu);
							cpu.setState(2);
							if (pWriter != null) {
		    					pWriter.println(cpu.getName() + " put in ready queue.");
		    				}
						}
        				readyQueue.add(readyQueue.remove());
        			}
        		}
        		if (!readyQueue.contains(cpu)) { // this happens if newProc's priority is greater than all in ready queue. It wouldn't have gotten added, so add it in now.
        			readyQueue.add(cpu);
        			cpu.setState(2);
        			
        			if (pWriter != null) {
    					pWriter.println(cpu.getName() + " put in ready queue.");
    				}
        		}
    			cpu = readyQueue.remove();
    			cpu.setState(3);
    			
    			if (pWriter != null) {
					pWriter.println(cpu.getName() + " dispatched from the ready queue to use the CPU.");
				}
    		}
    		// END OF ADDED SECTION
    		
    		if (!waitQueue.isEmpty() && io == null) {
    			io = waitQueue.remove();
    		}
    		
    		// reduce 1 from both bursts
    		if (cpu != null)
    			cpu.getCpuBurstList()[cpu.getCpuBurstIndex()]--; // decrement the current burst by 1
    		if (io != null)
    			io.getIOBurstList()[io.getIOBurstIndex()]--;

    		// all processes in ready queue will increase waiting time
    		Iterator<PCB> itr = readyQueue.iterator();
    		while (itr.hasNext()) {
    			itr.next().incCpuWaitTime();
    		}
    		//src: https://www.techiedelight.com/iterate-through-queue-java/ 
    		// all processes in wait queue will increase waiting time
    		Iterator<PCB> itr2 = waitQueue.iterator();
    		while (itr2.hasNext()) {
    			itr2.next().incIOWaitTime();
    		}

    		// here, need to display the current state of the CPU, IO Device, ready queue, IO waiting queue, and all processes
    		String cpuState;
    		String ioState;
    		if (cpu == null) {
    			cpuState = "idle";
    		}
    		else {
    			cpuState = "running " + cpu.getName();
    			utilization += 1;
    		}
    		if (io == null) ioState = "idle";
    		else ioState = "running " + io.getName();
    		
    		if (utilization <= 0) {
    			System.out.println("\nSimulation step: " + simStep 
    					+ "\n\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af" 
    					+ "\n| CPU: "  + cpuState + ", utilization: 0%" + "\n____________________________" 
    					+ "\n\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af" 
    					+ "\n| I/O Device: " + ioState + " |" + "\n____________________");
    		}
    		else {
    			System.out.print("\nSimulation step: " + simStep 
    					+ "\n\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af" 
    					+ "\n| CPU: "  + cpuState + ", utilization: " + Math.round(utilization / simStep * 100) + "%" 
    					+ "\n____________________________");
    			
    			if( ioState.compareToIgnoreCase("idle") == 0 ) {
    				System.out.println("\n\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af"  + "\n| I/O Device: " + ioState + " |" + "\n____________________");
    			}else {
    				System.out.println("\n\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af"  
    						+ "\n| I/O Device: "  + ioState + "\n____________________________");
    			}
    			
    		}
    		
    		double totalWait = 0;
    		double totalTurnaround = 0;
    		double totalCompleted = 0;
    		for (int i = 0; i < pcbList.size(); i++) {
    			totalWait += pcbList.get(i).getCpuWaitTime();
    			if (pcbList.get(i).getState() == 5) // only want to get turnaround if process is terminated
    				totalTurnaround += pcbList.get(i).getFinishTime() - pcbList.get(i).getArvTime();
    			if (pcbList.get(i).getState() == 5)
    				totalCompleted += 1;		
    		}
    		avgWait = totalWait / pcbList.size();
    		avgTurn = totalTurnaround / pcbList.size();
    		throughput = totalCompleted / simStep;
    		System.out.println("Average Wait Time: " + avgWait + "\nAverage Turnaround Time: " + avgTurn + "\nThroughput: " + throughput);
    		
    		
    		
    		System.out.print("Ready Queue: ");
    		Iterator<PCB> itr3 = readyQueue.iterator();
    		while (itr3.hasNext()) {
    			System.out.print(itr3.next().getName() + ", ");
    		}
    		
    		System.out.print("\nWait Queue: ");
    		Iterator<PCB> itr4 = waitQueue.iterator();
    		while (itr4.hasNext()) {
    			System.out.print(itr4.next().getName() + ", ");
    		}
    		
    		// Print the algorithm statistics: 
        	for (int i = 0; i < pcbList.size(); i++) {
    			PCB temp = pcbList.get(i);
    			int num = temp.getState();
    			String state = null;
    			if (num == 1) state = "new";
    			else if (num == 2) state = "ready";
    			else if (num == 3) state = "running";
    			else if (num == 4) state = "waiting";
    			else if (num == 5) state = "terminated";
    			System.out.print("\nProcess: " + temp.getName() + "\tState: " + state);
    			
    			System.out.print("\tId: " + temp.getPID() + "\tArrival: " + temp.getArvTime() + "\tPriority: " + temp.getPriority() + "\tCPU Bursts: ");
    			for (int j = 0; j < temp.getCpuBurstList().length; j++) {
    				System.out.print(temp.getCpuBurstList()[j] + " ");
    			}
    			System.out.print("\tIO Bursts: ");
    			for (int k = 0; k < temp.getIOBurstList().length; k++) {
    				System.out.print(temp.getIOBurstList()[k] + " ");
    			}
    			System.out.print("\tStart Time: " + temp.getStartTime() + "\tFinishTime: " + temp.getFinishTime() + "\tWait Time: " + temp.getCpuWaitTime() + "\tWait IO Time: " + temp.getIOWaitTime());
    			if (temp.getStarted() == false)
    				System.out.print("\tResponse Time: not started");
    			else
    				System.out.print("\tResponse Time: " + (temp.getStartTime() - temp.getArvTime()));
        	}
        	System.out.println();
        	// manual handling
    		if( mode == 1 ) {
    			while (true) {
	            	System.out.println("\nContinue to next simulation step?(Enter to continue): "
							+ "\nToggle execution mode?(a to toggle mode):");
	            	System.out.print(">>> ");
	            	String modeOption = console.nextLine();
	            	
	            	if (!modeOption.isEmpty()) {
						if(modeOption.trim().equals("")) {
							System.out.println("Program exiting.");
							System.exit(0);
						}
						if (modeOption.trim().equalsIgnoreCase("a")) {
							mode = 0;
							break;
						}
					}
					else if (modeOption.isEmpty()) {
						break;
					}
    	        }
    			
    		} // end manual handling
    		
    		if (mode == 0) {
    			if (unit == 0) {
    				try {
    					Thread.sleep(simUnitTime);
    				} catch (InterruptedException e) {
    					e.printStackTrace();
    				}
    			}
    			if (unit == 1 && (simStep + 1) % simUnitTime == 0) {
    				try {
    					Thread.sleep(simUnitTime * 1000);
    				} catch (InterruptedException e) {
    					e.printStackTrace();
    				}
    			}
    		}
    		
    		// next simulation step
    		simStep++;
		} // end while loop
    	
    	String response = "";
    	while (!response.equalsIgnoreCase("yes") && !response.equalsIgnoreCase("y") && !response.equalsIgnoreCase("no") && !response.equalsIgnoreCase("n")) {
    		System.out.print("\nWould you like to save the results to file? (y/n): ");
    		response = console.next();
    	}
    	
    	if (pWriter != null && response.toLowerCase().charAt(0) == 'y') {
    		pWriter.close();
    	}
    } // end sjf
    
    public static void ps(long simUnitTime) {
    	Queue<PCB> readyQueue = new LinkedList<PCB>();
    	Queue<PCB> waitQueue = new LinkedList<PCB>();
    	PCB cpu = null;
    	PCB io = null;
    	int simStep = 0; // each simStep represents our runtime
    	double utilization = -1;
		double avgWait = 0;
		double avgTurn = 0;
		double throughput = 0;
    	
    	FileWriter fWriter;
        PrintWriter pWriter = null;
		try {
			fWriter = new FileWriter ("psResults.txt");
			pWriter = new PrintWriter (fWriter);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
 	
    	// if haven't gotten to first process yet, change the runtime
    	if (pcbList.getFirst().getArvTime() > simStep)
	 		simStep = pcbList.getFirst().getArvTime();
 	
    	// one loop = one simulation step
    	while (true) {
    		// if all process terminated, done
    		int termCounter = 0;
    		for (int i = 0; i < pcbList.size(); i++) {
    			if (pcbList.get(i).getState() == 5) 
    				termCounter++;
    		}
    		if (termCounter == pcbList.size()) {
    			simStep -= 1;
    			if (pWriter != null) {
    				pWriter.println("Simulation finished."
    						+ "\n\nSimulation statistics"
    						+ "\n------------------------------"
    						+ "\nSimulation steps: " + simStep
    						+ "\nCPU utilization: " + Math.round(utilization / simStep * 100) + "%"
    						+ "\nAverage wait time: " + avgWait + " ms"
    						+ "\nAverage turnaround time: " + avgTurn + " ms"
    						+ "\nThroughput: " + throughput + " processes per ms");
				}
    			break; // done
    		}
    		
    		
    		// move all processes that finish their CPU burst to IO wait queue
    		if (cpu != null) {
    			if (cpu.getCpuBurstList()[cpu.getCpuBurstIndex()] == 0) { // if our current burst is 0
    				if (cpu.getCpuBurstIndex() == cpu.getCpuBurstList().length - 1) { // if we finished our last CPU burst
    					cpu.setFinishTime(simStep); // set the finish time of the process
    					cpu.setState(5); // terminated
    					
    					if (pWriter != null) {
        					pWriter.println(cpu.getName() + " has terminated.\n"
        							+ cpu.getName() + " turnaround time: " + (cpu.getFinishTime() - cpu.getArvTime()) + "ms.\n"
        							+ cpu.getName() + " total CPU wait time: " + cpu.getCpuWaitTime() + "ms.\n"
        							+ cpu.getName() + " total I/O wait time: " + cpu.getIOWaitTime() + "ms.");
        				}
    					cpu = null;
    				}
    				else {
    					cpu.setState(4); // set it to waiting
    					waitQueue.add(cpu);
    					cpu.setCpuBurstIndex(cpu.getCpuBurstIndex() + 1); // done with that burst
    					
    					if (pWriter != null) {
        					pWriter.println(cpu.getName() + " put into I/O queue.");
        				}
    					cpu = null;
    				}
    			}
    		}
    		// move all processes that finish their IO burst to ready queue
    		if (io != null) {
    			if (io.getIOBurstList()[io.getIOBurstIndex()] == 0) { // if our current burst is 0
    				io.setState(2); // set it to ready
    				
    				// START OF ADDED SECTION - have to add io into ready queue sorted by priority
    				if (readyQueue.isEmpty()) {
    					readyQueue.add(io);
    					io.setState(2);
    					
    					if (pWriter != null) {
        					pWriter.println(io.getName() + " completed I/O, returned to ready queue.");
        				}
    				}
    				else {
    					int size = readyQueue.size();
    					for (int j = 0; j < size; j++) { // might have issue here
    						if (readyQueue.peek().getPriority() < io.getPriority()) { // if head is more prioritized, simply add
    							readyQueue.add(readyQueue.remove()); // add head of queue to end
    						}
    						else { // if io is more prioritized
    							if (!readyQueue.contains(io)) { // stops us from adding it more than once
        							readyQueue.add(io);
        							io.setState(2);
        							if (pWriter != null) {
            	    					pWriter.println(io.getName() + " completed I/O, returned to ready queue.");
            	    				}
        						}
    							readyQueue.add(readyQueue.remove());
    						}
    					}
    					if (!readyQueue.contains(io)) { // this happens if newProc's priority is greater than all in ready queue. It wouldn't have gotten added, so add it in now.
    						readyQueue.add(io);
    						io.setState(2);
    						
    						if (pWriter != null) {
    	    					pWriter.println(io.getName() + " completed I/O, returned to ready queue.");
    	    				}
    					}
    				}
            		// END OF ADDED SECTION
            		
    				if (io.getIOBurstIndex() == io.getIOBurstList().length - 1) { // if that was last io burst, we don't want to increment index
    					io = null;
    				}
    				else {
    					io.setIOBurstIndex(io.getIOBurstIndex() + 1); // done with that burst
    					io = null;
    				}
    			}
    		}
    		
    		// add in the new process, if there is one
    		for (int i = 0; i < pcbList.size(); i++) {
    			if (pcbList.get(i).getArvTime() == simStep) {
        			PCB newProc = pcbList.get(i);
        			newProc.setState(2);
        			// START OF ADDED SECTION - have to add new process into ready queue sorted by priority
        			if (readyQueue.isEmpty()) {
        				readyQueue.add(newProc);
        				newProc.setState(2);
        				
        				if (pWriter != null) {
        					pWriter.println(newProc.getName() + " put in ready queue.");
        				}
        			}
        			else {
        				int size = readyQueue.size();
        				//readyQueue.add(newProc);
        				for (int j = 0; j < size; j++) { // might have issue here
        					if (readyQueue.peek().getPriority() < newProc.getPriority()) { // if head is more prioritized, simply add
        						readyQueue.add(readyQueue.remove()); // add head of queue to end
        					}
        					else { // if newProc is more prioritized
        						if (!readyQueue.contains(newProc)) { // stops us from adding it more than once
        							readyQueue.add(newProc);
        							newProc.setState(2);
        							
        							if (pWriter != null) {
                    					pWriter.println(newProc.getName() + " put in ready queue.");
                    				}
        						}
        						readyQueue.add(readyQueue.remove());
        					}
        				}
        				if (!readyQueue.contains(newProc)) { // this happens if newProc's priority is greater than all in ready queue. It wouldn't have gotten added, so add it in now.
        					readyQueue.add(newProc);
        					newProc.setState(2);
        					
        					if (pWriter != null) {
            					pWriter.println(newProc.getName() + " put in ready queue.");
            				}
        				}
        			}
            		// END OF ADDED SECTION
        		}
        		
    		}
    		
    		// fill the cpu and io
    		if (!readyQueue.isEmpty() && cpu == null) {
    			cpu = readyQueue.remove();
    			if (cpu.getStarted() == false) {
    				cpu.setStarted(); // set it to true
    				cpu.setStartTime(simStep);
    			}
    			if (pWriter != null) {
					pWriter.println(cpu.getName() + " dispatched from the ready queue to use the CPU.");
				}
    			cpu.setState(3);
    		}
    		
    		// START OF ADDED SECTION
    		if (!readyQueue.isEmpty() && readyQueue.peek().getPriority() < cpu.getPriority()) {
    			int size = readyQueue.size();
    			for (int j = 0; j < size; j++) { // might have issue here
        			if (readyQueue.peek().getPriority() < cpu.getPriority()) { // if head is more prioritized, simply add
        				readyQueue.add(readyQueue.remove()); // add head of queue to end
        			}
        			else { // if newProc is more prioritized
        				if (!readyQueue.contains(cpu)) { // stops us from adding it more than once
							readyQueue.add(cpu);
							cpu.setState(2);
							
							if (pWriter != null) {
								pWriter.println(cpu.getName() + " moved from cpu to ready queue.");
							}
						}
        				readyQueue.add(readyQueue.remove());
        			}
        		}
        		if (!readyQueue.contains(cpu)) { // this happens if newProc's priority is greater than all in ready queue. It wouldn't have gotten added, so add it in now.
        			readyQueue.add(cpu);
        			cpu.setState(2);
        			
        			if (pWriter != null) {
						pWriter.println(cpu.getName() + " moved from cpu to ready queue.");
					}
        		}
    			cpu = readyQueue.remove();
    			cpu.setState(3);
    			
    			if (pWriter != null) {
					pWriter.println(cpu.getName() + " dispatched from the ready queue to use the CPU.");
				}
    		}
    		// END OF ADDED SECTION
    		
    		if (!waitQueue.isEmpty() && io == null) {
    			io = waitQueue.remove();
    		}
    		
    		// reduce 1 from both bursts
    		if (cpu != null)
    			cpu.getCpuBurstList()[cpu.getCpuBurstIndex()]--; // decrement the current burst by 1
    		if (io != null)
    			io.getIOBurstList()[io.getIOBurstIndex()]--;

    		// all processes in ready queue will increase waiting time
    		Iterator<PCB> itr = readyQueue.iterator();
    		while (itr.hasNext()) {
    			itr.next().incCpuWaitTime();
    		}
    		//src: https://www.techiedelight.com/iterate-through-queue-java/ 
    		// all processes in wait queue will increase waiting time
    		Iterator<PCB> itr2 = waitQueue.iterator();
    		while (itr2.hasNext()) {
    			itr2.next().incIOWaitTime();
    		}

    		// here, need to display the current state of the CPU, IO Device, ready queue, IO waiting queue, and all processes
    		String cpuState;
    		String ioState;
    		if (cpu == null) {
    			cpuState = "idle";
    		}
    		else {
    			cpuState = "running " + cpu.getName();
    			utilization += 1;
    		}
    		if (io == null) ioState = "idle";
    		else ioState = "running " + io.getName();
    		
    		if (utilization <= 0) {
    			System.out.println("\nSimulation step: " + simStep 
    					+ "\n\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af" 
    					+ "\n| CPU: "  + cpuState + ", utilization: 0%" + "\n____________________________" 
    					+ "\n\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af" 
    					+ "\n| I/O Device: " + ioState + " |" + "\n____________________");
    		}
    		else {
    			System.out.print("\nSimulation step: " + simStep 
    					+ "\n\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af" 
    					+ "\n| CPU: "  + cpuState + ", utilization: " + Math.round(utilization / simStep * 100) + "%" 
    					+ "\n____________________________");
    			
    			if( ioState.compareToIgnoreCase("idle") == 0 ) {
    				System.out.println("\n\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af"  + "\n| I/O Device: " + ioState + " |" + "\n____________________");
    			}else {
    				System.out.println("\n\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af"  
    						+ "\n| I/O Device: "  + ioState + "\n____________________________");
    			}
    			
    		}
    		
    		double totalWait = 0;
    		double totalTurnaround = 0;
    		double totalCompleted = 0;
    		for (int i = 0; i < pcbList.size(); i++) {
    			totalWait += pcbList.get(i).getCpuWaitTime();
    			if (pcbList.get(i).getState() == 5) // only want to get turnaround if process is terminated
    				totalTurnaround += pcbList.get(i).getFinishTime() - pcbList.get(i).getArvTime();
    			if (pcbList.get(i).getState() == 5)
    				totalCompleted += 1;		
    		}
    		avgWait = totalWait / pcbList.size();
    		avgTurn = totalTurnaround / pcbList.size();
    		throughput = totalCompleted / simStep;
    		System.out.println("Average Wait Time: " + avgWait + "\nAverage Turnaround Time: " + avgTurn + "\nThroughput: " + throughput);
    		
    		
    		
    		System.out.print("Ready Queue: ");
    		Iterator<PCB> itr3 = readyQueue.iterator();
    		while (itr3.hasNext()) {
    			System.out.print(itr3.next().getName() + ", ");
    		}
    		
    		System.out.print("\nWait Queue: ");
    		Iterator<PCB> itr4 = waitQueue.iterator();
    		while (itr4.hasNext()) {
    			System.out.print(itr4.next().getName() + ", ");
    		}
    		
    		// Print the algorithm statistics: 
        	for (int i = 0; i < pcbList.size(); i++) {
    			PCB temp = pcbList.get(i);
    			int num = temp.getState();
    			String state = null;
    			if (num == 1) state = "new";
    			else if (num == 2) state = "ready";
    			else if (num == 3) state = "running";
    			else if (num == 4) state = "waiting";
    			else if (num == 5) state = "terminated";
    			System.out.print("\nProcess: " + temp.getName() + "\tState: " + state);
    			
    			System.out.print("\tId: " + temp.getPID() + "\tArrival: " + temp.getArvTime() + "\tPriority: " + temp.getPriority() + "\tCPU Bursts: ");
    			for (int j = 0; j < temp.getCpuBurstList().length; j++) {
    				System.out.print(temp.getCpuBurstList()[j] + " ");
    			}
    			System.out.print("\tIO Bursts: ");
    			for (int k = 0; k < temp.getIOBurstList().length; k++) {
    				System.out.print(temp.getIOBurstList()[k] + " ");
    			}
    			System.out.print("\tStart Time: " + temp.getStartTime() + "\tFinishTime: " + temp.getFinishTime() + "\tWait Time: " + temp.getCpuWaitTime() + "\tWait IO Time: " + temp.getIOWaitTime());
    			if (temp.getStarted() == false)
    				System.out.print("\tResponse Time: not started");
    			else
    				System.out.print("\tResponse Time: " + (temp.getStartTime() - temp.getArvTime()));
        	}
        	System.out.println();
        	// manual handling
    		if( mode == 1 ) {
    			while (true) {
	            	System.out.println("\nContinue to next simulation step?(Enter to continue): "
							+ "\nToggle execution mode?(a to toggle mode):");
	            	System.out.print(">>> ");
	            	String modeOption = console.nextLine();
	            	
	            	if (!modeOption.isEmpty()) {
						if(modeOption.trim().equals("")) {
							System.out.println("Program exiting.");
							System.exit(0);
						}
						if (modeOption.trim().equalsIgnoreCase("a")) {
							mode = 0;
							break;
						}
					}
					else if (modeOption.isEmpty()) {
						break;
					}
    	        }
    			
    		} // end manual handling
        		
    		if (mode == 0) {
    			if (unit == 0) {
    				try {
    					Thread.sleep(simUnitTime);
    				} catch (InterruptedException e) {
    					e.printStackTrace();
    				}
    			}
    			if (unit == 1 && (simStep + 1) % simUnitTime == 0) {
    				try {
    					Thread.sleep(simUnitTime * 1000);
    				} catch (InterruptedException e) {
    					e.printStackTrace();
    				}
    			}
    		}
    		
    		// next simulation step
    		simStep++;
		} // end while loop
    	
    	String response = "";
    	while (!response.equalsIgnoreCase("yes") && !response.equalsIgnoreCase("y") && !response.equalsIgnoreCase("no") && !response.equalsIgnoreCase("n")) {
    		System.out.print("\nWould you like to save the results to file? (y/n): ");
    		response = console.next();
    	}
    	
    	if (pWriter != null && response.toLowerCase().charAt(0) == 'y') {
    		pWriter.close();
    	}
    } // end ps

    public static void rr(long simUnitTime, int quantumTime) {
    	Queue<PCB> readyQueue = new LinkedList<PCB>();
    	Queue<PCB> waitQueue = new LinkedList<PCB>();
    	PCB cpu = null;
    	PCB io = null;
    	int simStepOfCurProcessStart = 0; // simulation time for when the running process started
    	int simStep = 0; // each simStep represents our runtime
    	double utilization = -1;
		double avgWait = 0;
		double avgTurn = 0;
		double throughput = 0;
		
    	FileWriter fWriter;
        PrintWriter pWriter = null;
		try {
			fWriter = new FileWriter ("rrResults.txt");
			pWriter = new PrintWriter (fWriter);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
    	// one loop = one simulation step
    	while (true) {
    		// if all process terminated, done
    		int termCounter = 0;
    		for (int i = 0; i < pcbList.size(); i++) {
    			if (pcbList.get(i).getState() == 5) 
    				termCounter++;
    			else
    				break; // exit for loop if one of the processes are not terminated.
    		}
    		if (termCounter == pcbList.size()) {
    			simStep -= 1;
    			if (pWriter != null) {
    				pWriter.println("Simulation finished."
    						+ "\n\nSimulation statistics"
    						+ "\n------------------------------"
    						+ "\nSimulation steps: " + simStep
    						+ "\nCPU utilization: " + Math.round(utilization / simStep * 100) + "%"
    						+ "\nAverage wait time: " + avgWait + " ms"
    						+ "\nAverage turnaround time: " + avgTurn + " ms"
    						+ "\nThroughput: " + throughput + " processes per ms"
    						+ "\nQuantum: " + quantumTime + " ms");
				}
    			break; // done
    		}
    		
    		// move all processes that finish their CPU burst to IO wait queue
    		if (cpu != null) {
    			if (cpu.getCpuBurstList()[cpu.getCpuBurstIndex()] == 0) { // if our current burst is 0
    				simStepOfCurProcessStart = simStep; // reset simStepOfCurProcessStart
    				
    				if (cpu.getCpuBurstIndex() == cpu.getCpuBurstList().length - 1) { // if we finished our last CPU burst
    					cpu.setFinishTime(simStep); // set the finish time of the process
    					cpu.setState(5); // terminated
    					
    					if (pWriter != null) {
        					pWriter.println(cpu.getName() + " has terminated.\n"
        							+ cpu.getName() + " turnaround time: " + (cpu.getFinishTime() - cpu.getArvTime()) + "ms.\n"
        							+ cpu.getName() + " total CPU wait time: " + cpu.getCpuWaitTime() + "ms.\n"
        							+ cpu.getName() + " total I/O wait time: " + cpu.getIOWaitTime() + "ms.");
        				}
    					cpu = null;
    				}
    				else {
    					cpu.setState(4); // set it to waiting
    					waitQueue.add(cpu);
    					cpu.setCpuBurstIndex(cpu.getCpuBurstIndex() + 1); // done with that burst
    					
    					if (pWriter != null) {
        					pWriter.println(cpu.getName() + " put into I/O queue.");
        				}
    					cpu = null;
    				}
    			}
    			// if running process has been running for 1 quantum time, and its current cpu burst isn't finished
    			if (simStep - simStepOfCurProcessStart == quantumTime && cpu.getCpuBurstList()[cpu.getCpuBurstIndex()] != 0) {
    				cpu.setState(2); // set to ready
    				readyQueue.add(cpu); // add to end of readyQueue
    				
    				if (pWriter != null) {
    					pWriter.println(cpu.getName() + " is preempted and put back in ready queue.");
    				}
    				cpu = null; // clear cpu
    				simStepOfCurProcessStart = simStep; // reset simStepOfCurProcessStart
    			}
    		}
    		// move all processes that finish their IO burst to ready queue
    		if (io != null) {
    			if (io.getIOBurstList()[io.getIOBurstIndex()] == 0) { // if our current burst is 0
    				io.setState(2); // set it to ready
    				readyQueue.add(io);
    				
    				if (pWriter != null) {
    					pWriter.println(io.getName() + " completed I/O, returned to ready queue.");
    				}
    				if (io.getIOBurstIndex() == io.getIOBurstList().length - 1) {
    					io = null;
    				}
    				else {
    					io.setIOBurstIndex(io.getIOBurstIndex() + 1); // done with that burst
    					io = null;
    				}
    			}
    		}
    		
    		// populate readyQueue with processes that have arrived
    		for (int i = 0; i < pcbList.size(); i++) {
    			if (pcbList.get(i).getArvTime() == simStep) {
        			PCB newProc = pcbList.get(i);
        			readyQueue.add(newProc);
            		newProc.setState(2);
            		
            		if (pWriter != null) {
    					pWriter.println(newProc.getName() + " put in ready queue.");
    				}
        		}
    		}
    		
    		// fill the cpu and io
    		if (!readyQueue.isEmpty() && cpu == null) {
    			cpu = readyQueue.remove();
    			if (cpu.getStarted() == false) {
    				cpu.setStarted(); // set it to true
    				cpu.setStartTime(simStep);
    			}
    			if (pWriter != null) {
					pWriter.println(cpu.getName() + " dispatched from the ready queue to use the CPU.");
				}
    			cpu.setState(3);
    		}

    		if (!waitQueue.isEmpty() && io == null) {
    			io = waitQueue.remove();
    		}
    		
    		// reduce 1 from both bursts
    		if (cpu != null)
    			cpu.getCpuBurstList()[cpu.getCpuBurstIndex()]--; // decrement the current burst by 1
    		if (io != null)
    			io.getIOBurstList()[io.getIOBurstIndex()]--;

    		// all processes in ready queue will increase waiting time
    		Iterator<PCB> itr = readyQueue.iterator();
    		while (itr.hasNext()) {
    			itr.next().incCpuWaitTime();
    		}
    		//src: https://www.techiedelight.com/iterate-through-queue-java/ 
    		// all processes in wait queue will increase waiting time
    		Iterator<PCB> itr2 = waitQueue.iterator();
    		while (itr2.hasNext()) {
    			itr2.next().incIOWaitTime();
    		}

    		// here, need to display the current state of the CPU, IO Device, ready queue, IO waiting queue, and all processes
    		String cpuState;
    		String ioState;
    		if (cpu == null) {
    			cpuState = "idle";
    		}
    		else {
    			cpuState = "running " + cpu.getName();
    			utilization += 1;
    		}
    		if (io == null) ioState = "idle";
    		else ioState = "running " + io.getName();
    		
    		if (utilization <= 0) {
    			System.out.println("\nSimulation step: " + simStep 
    					+ "\n\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af" 
    					+ "\n| CPU: "  + cpuState + ", utilization: 0%" + "\n____________________________" 
    					+ "\n\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af" 
    					+ "\n| I/O Device: " + ioState + " |" + "\n____________________");
    		}
    		else {
    			System.out.print("\nSimulation step: " + simStep 
    					+ "\n\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af" 
    					+ "\n| CPU: "  + cpuState + ", utilization: " + Math.round(utilization / simStep * 100) + "%" 
    					+ "\n____________________________");
    			
    			if( ioState.compareToIgnoreCase("idle") == 0 ) {
    				System.out.println("\n\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af"  + "\n| I/O Device: " + ioState + " |" + "\n____________________");
    			}else {
    				System.out.println("\n\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af"  
    						+ "\n| I/O Device: "  + ioState + "\n____________________________");
    			}
    			
    		}
    		
    		double totalWait = 0;
    		double totalTurnaround = 0;
    		double totalCompleted = 0;
    		for (int i = 0; i < pcbList.size(); i++) {
    			totalWait += pcbList.get(i).getCpuWaitTime();
    			if (pcbList.get(i).getState() == 5) // only want to get turnaround if process is terminated
    				totalTurnaround += pcbList.get(i).getFinishTime() - pcbList.get(i).getArvTime();
    			if (pcbList.get(i).getState() == 5)
    				totalCompleted += 1;		
    		}
    		avgWait = totalWait / pcbList.size();
    		avgTurn = totalTurnaround / pcbList.size();
    		throughput = totalCompleted / simStep;
    		System.out.println("Average Wait Time: " + avgWait + "\nAverage Turnaround Time: " + avgTurn + "\nThroughput: " + throughput);
    		
    		System.out.print("Ready Queue: ");
    		Iterator<PCB> itr3 = readyQueue.iterator();
    		while (itr3.hasNext()) {
    			System.out.print(itr3.next().getName() + ", ");
    		}
    		
    		System.out.print("\nWait Queue: ");
    		Iterator<PCB> itr4 = waitQueue.iterator();
    		while (itr4.hasNext()) {
    			System.out.print(itr4.next().getName() + ", ");
    		}
    		
    		// Print the algorithm statistics: 
        	for (int i = 0; i < pcbList.size(); i++) {
    			PCB temp = pcbList.get(i);
    			int num = temp.getState();
    			String state = null;
    			if (num == 1) state = "new";
    			else if (num == 2) state = "ready";
    			else if (num == 3) state = "running";
    			else if (num == 4) state = "waiting";
    			else if (num == 5) state = "terminated";
    			System.out.print("\nProcess: " + temp.getName() + "\tState: " + state);
    			
    			System.out.print("\tId: " + temp.getPID() + "\tArrival: " + temp.getArvTime() + "\tPriority: " + temp.getPriority() + "\tCPU Bursts: ");
    			for (int j = 0; j < temp.getCpuBurstList().length; j++) {
    				System.out.print(temp.getCpuBurstList()[j] + " ");
    			}
    			System.out.print("\tIO Bursts: ");
    			for (int k = 0; k < temp.getIOBurstList().length; k++) {
    				System.out.print(temp.getIOBurstList()[k] + " ");
    			}
    			System.out.print("\tStart Time: " + temp.getStartTime() + "\tFinishTime: " + temp.getFinishTime() + "\tWait Time: " + temp.getCpuWaitTime() + "\tWait IO Time: " + temp.getIOWaitTime());
    			if (temp.getStarted() == false)
    				System.out.print("\tResponse Time: not started");
    			else
    				System.out.print("\tResponse Time: " + (temp.getStartTime() - temp.getArvTime()));
        	}
        	System.out.println();
        	// manual handling
    		if( mode == 1 ) {
    			while (true) {
	            	System.out.println("\nContinue to next simulation step?(Enter to continue): "
							+ "\nToggle execution mode?(a to toggle mode):");
	            	System.out.print(">>> ");
	            	String modeOption = console.nextLine();
	            	
	            	if (!modeOption.isEmpty()) {
						if(modeOption.trim().equals("")) {
							System.out.println("Program exiting.");
							System.exit(0);
						}
						if (modeOption.trim().equalsIgnoreCase("a")) {
							mode = 0;
							break;
						}
					}
					else if (modeOption.isEmpty()) {
						break;
					}
    	        }
    			
    		} // end manual handling
        		
    		if (mode == 0) {
    			if (unit == 0) {
    				try {
    					Thread.sleep(simUnitTime);
    				} catch (InterruptedException e) {
    					e.printStackTrace();
    				}
    			}
    			if (unit == 1 && (simStep + 1) % simUnitTime == 0) {
    				try {
    					Thread.sleep(simUnitTime * 1000);
    				} catch (InterruptedException e) {
    					e.printStackTrace();
    				}
    			}
    		}
    		
    		// next simulation step
    		simStep++;
		} // end while loop
    	
    	String response = "";
    	while (!response.equalsIgnoreCase("yes") && !response.equalsIgnoreCase("y") && !response.equalsIgnoreCase("no") && !response.equalsIgnoreCase("n")) {
    		System.out.print("\nWould you like to save the results to file? (y/n): ");
    		response = console.next();
    	}
    	
    	if (pWriter != null && response.toLowerCase().charAt(0) == 'y') {
    		pWriter.close();
    	}
    } // end rr
    
} // end class
