/**
 * Assignment: Project 1
 * Name: Max Bauer
 * Date: 2/2/2021
 * Description: Simulate Linux terminal (works on Windows and Linux OS).
 * 				The quick sort came from a previous class at RCTC (2247 I believe).
 * 				I adjusted the sort to work with Strings instead of ints.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Command implements Runnable {
	private String theCommand;

	public Command(String c) {
		theCommand = c;
	}

	@Override
	public void run() {
		
		String[] cWords = theCommand.trim().replaceAll(" +", " ").split(" ");
		String addToPath = "";
		String separator = "";
		Path testPath;
		
		if (System.getProperty("os.name").startsWith("Windows")) {
			separator = "\\";
		} else if (System.getProperty("os.name").startsWith("Linux")) {
			separator = "/";
		}
		
		if (cWords[0].trim().equals("pwd")) { // pwd prints the working directory
			System.out.println(Shell.path);
		}else if (cWords[0].trim().equals("cd")) { // cd changes directory
			if (cWords.length == 1) {
				Shell.path = Paths.get(System.getProperty("user.home")); // change global Path variable path to home directory
			} else if (cWords.length > 2) {
				System.out.println(cWords[0] + ": too many arguments"); // 3 arguments is too many
			} else {
				addToPath = cWords[1];

				testPath = Paths.get(Shell.path + separator + addToPath); // use a testPath in case it isn't a file
				if (Files.notExists(testPath)) { // check if path is a file
					System.out.println(cWords[0] + ": " + addToPath + ": No such file or directory");
				} else {
					Shell.path = testPath; // set path to testPath
				}
			}
		} else if (cWords[0].equals("ls")) { // ls lists all directory contents
			if (cWords.length == 1) { // current path is already valid
				File f = new File(Shell.path.toString()); // make a file object with current path
				ArrayList<String> names = new ArrayList<String>(Arrays.asList(f.list())); // list of file & dir names

				for (int i = 0; i < names.size(); i++) {
					System.out.println(names.get(i)); // print names while going through list
				}
			} else {
				int x = 1;
				while (x < cWords.length) {
					testPath = Paths.get(Shell.path + separator + cWords[x]); // test each file path

					if (Files.notExists(testPath)) {
						System.out.println(cWords[0] + ": cannont access '" + cWords[x] + "': No such file or directory");
					} else {
						File f = new File(testPath.toString()); // make a file object with current path
						if (f.isFile()) {
							System.out.println(f.getName()); // print file name
						} else if (f.isDirectory()) {
							ArrayList<String> names = new ArrayList<String>(Arrays.asList(f.list()));

							for (int i = 0; i < names.size(); i++) {
								System.out.println(names.get(i)); // print file & dir names in valid dir
							}
						}
					}
					x++;
				} // end while (x < cWords.length)
			}
		} // end ls command
		else if (cWords[0].equals("cat")) { // cat prints the contents of named files
			if (cWords.length > 1) {
				int x = 1;
				while (x < cWords.length) {
					testPath = Paths.get(Shell.path + separator + cWords[x]);

					if (Files.notExists(testPath)) { // check potential path
						System.out.println(cWords[0] + ": '" + cWords[x] + "': No such file or directory");
					} else {
						File printFile = new File(testPath.toString()); // make file object
						try {
							Scanner inFile = new Scanner(printFile); // add Scanner to file
							String printString = ""; // String to hold file contents
							while (inFile.hasNext()) {
								printString += inFile.nextLine() + "\n"; // add contents of file while Scanner has next token
							}
							printString = printString.substring(0, printString.length() - 1); // delete last new line
							System.out.println(printString); // print file contents
							inFile.close(); // close Scanner
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					}
					x++;
				} // end while (x < cWords.length)
			}
		} // end cat
		else if (cWords[0].equals("cmp")) { // compare two file's contents
			if (cWords.length == 1) {
				System.out.println(cWords[0] + ": missing operand after '" + cWords[0] + "'");
			} else if (cWords.length == 2) {
				System.out.println(cWords[0] + ": missing operand after '" + cWords[1] + "'");
			} else if (cWords.length == 3) {
				int x = 1;
				String[] compareFiles = new String[2]; // make String array to hold 2 Strings for comparison later
				while (x < 3) {
					testPath = Paths.get(Shell.path + separator + cWords[x]);

					if (Files.notExists(testPath)) { // check potential path
						System.out.println(cWords[0] + ": '" + cWords[x] + "': No such file or directory");
					} else {
						File textFile = new File(testPath.toString()); // make file object
						try {
							Scanner inFile = new Scanner(textFile); // add Scanner to file
							String cmpString = ""; // goes in compareFiles String array
							while (inFile.hasNext()) {
								cmpString += inFile.nextLine() + "\n"; // add contents of file while Scanner has next token
							}
							cmpString = cmpString.substring(0, cmpString.length() - 1); // remove last new line
							compareFiles[x - 1] = cmpString; // load array, 0 = first time through, 1 = second time through
							
							inFile.close(); // close Scanner
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					}
					x++;
				} // end while (x < 3)
				
				if (compareFiles[0] != null && compareFiles[1] != null) { // avoid null pointer exception
					int index = 0;
					int line = 1;
					while (compareFiles[0].length() > index && compareFiles[1].length() > index) {
						if (compareFiles[0].charAt(index) == compareFiles[1].charAt(index)) {
							if (compareFiles[0].charAt(index) == '\n' || compareFiles[0].charAt(index) == '\r') {
								line++; // increment line number if there is a new line character
							}
							index++; // increment index if characters at index are equal.
						}
						else if(compareFiles[0].charAt(index) != compareFiles[1].charAt(index)) {
							break; // leave while loop if files aren't the same
						}
					} // end while
				
					if (compareFiles[0].charAt(index) != compareFiles[1].charAt(index)) {
						// file1 file2 differ: byte x (if the text files are different)
						System.out.println(cWords[1] + " " + cWords[2] + " differ: byte " + (index + 1) + ", line " + line);
					}
				}
			} else {
				System.out.println(cWords[0] + ": invalid, too many operands");
			}
		} // end cmp
		else if (cWords[0].equals("sort")) { // sort prints file in sorted (lexicographical) order: Nums -> UPPER -> lower
			if (cWords.length == 1) {
				System.out.println(cWords[0] + ": missing operand after '" + cWords[0] + "'");
			} else if (cWords.length == 2) {
				String text = ""; // String to sort
				testPath = Paths.get(Shell.path + separator + cWords[1]);

				if (Files.notExists(testPath)) { // check potential path
					System.out.println(cWords[0] + ": '" + cWords[1] + "': No such file or directory");
				} else {
					File textFile = new File(testPath.toString()); // make file object
					try {
						Scanner inFile = new Scanner(textFile); // add Scanner to file
						while (inFile.hasNext()) {
							text += inFile.nextLine() + "\n"; // add contents of file to sortThis while Scanner has next
						}
						text = text.substring(0, text.length() - 1); // remove last new line

						inFile.close(); // close Scanner
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					String[] sortThis = text.split("\n", 1000); // split by new line, limit of 1000
					quickSortByString(sortThis, 0, sortThis.length - 1); // call quick sort
					for (int i = 0; i < sortThis.length; i++) {
						System.out.println(sortThis[i]); // print sorted Strings
					}
				}
			} // end if there are 2 operands
			else {
				System.out.println(cWords[0] + ": invalid, too many operands");
			}
		} // end sort
		else if (cWords[0].equals("exit")) { // exit shell simulation
			Shell.exit = true;
		} // end exit
		else { // any  non command input
			System.out.println(theCommand + ": command not found");
		}

	} // end run

	public static void quickSortByString(String[] a, int p, int r) {
		// array a[p ... r]

		if (p < r) {
			int q = partitionByID(a, p, r);
			quickSortByString(a, p, q);
			quickSortByString(a, q + 1, r);
		}
	}// end quickSort

	public static int partitionByID(String[] a, int p, int r) {

		String x = a[p];
		int i = p - 1;
		int j = r + 1;

		while (true) {

			do {
				j--;
			} while (a[j].compareTo(x) > 0);

			do {
				i++;
			} while (a[i].compareTo(x) < 0);

			if (i < j) {
				String temp = a[j];
				a[j] = a[i];
				a[i] = temp;
				temp = a[j];
			}

			else {
				return j;
			}
		} // end while
	}// end partition

}
