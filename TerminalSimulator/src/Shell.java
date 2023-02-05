/**
 * Assignment: Project 1
 * Name: Max Bauer
 * Date: 2/2/2021
 * Description: Has main method which starts threads.
 * 				Global variables initialized here.
 * 				Get user input here.
 */

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Shell {

	public static boolean exit = false;
	public static Path path = Paths.get(System.getProperty("user.home"));
	
	public static void main(String[] args) {
		Scanner console = new Scanner(System.in);
		
		String input;
		path = Paths.get(System.getProperty("user.home"));

		while (exit != true) {
			System.out.print("shell>"); // output the prompt
			input = console.nextLine(); // get user input
			input.trim(); // remove leading and trailing white spaces
			String[] commandList = input.split(";"); // read entire command line into a string
			
			int numberOfCommands = commandList.length; // how many commands
			
			Thread t[] = new Thread[numberOfCommands];
			
			for (int i=0; i < numberOfCommands; i++) {
				commandList[i] = commandList[i].trim();
		        String c = commandList[i]; // String c = next command
		        
		        t[i] = new Thread(new Command(c)); // Create thread and store in array 
		        t[i].start();   // Fire the next thread off to execute c
		    }
			/* Wait for threads to finish before prompting again */
		      for (int i=0; i < numberOfCommands; i++) {
		         try {
					t[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		      }
		} // end while (exit != true)
		
		console.close();
		
	}// end main
	
}// end class
