package maxflow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import maxflow.Net.Flow;


/**
 * class Shell
 * implements command line interface that allows user to read graph data from file and run maxflow algorithm
 * contains main method
 */

public final class Shell {
	
	private static final String PROMPT = "maxflow> ";
	
	/**
     * Utility class constructor preventing instantiation.
     */
    private Shell() {
    	throw new UnsupportedOperationException(
                "Illegal call of utility class constructor.");	
    }

	/**
     * Reads and processes input until the quit command has been entered.
     *
     * @param args command-line arguments.
	 * @throws IOException 
     */
	public synchronized static void main(String[] args) throws IOException {
		BufferedReader in 
        = new BufferedReader(new InputStreamReader(System.in));
    boolean quit = false;
    
    Net net = null;
    
    while (!quit) {
			System.out.print(PROMPT);
			String input = in.readLine();
			if (input == null)
				break;

			String[] tokens = input.trim().split("\\s+");
			if (tokens.length < 1 || tokens[0].isEmpty()) {
				printError("Empty command.");
			} else {
				try {
					switch (tokens[0].toUpperCase()) {

						case "NET": case "N":
							if (tokens.length<2)
								printError("Please give file name witn Net input data");
							else
								net = readNetFromFile(tokens[1]);
							break;
						case "FLOW": case "F":
							if (tokens.length<2)
								printError("Please give file name with Flow input data");
							else if (net == null)
								printError("Net not defined");
							else
								net = readFlowFromFile(tokens[1], net);
							break;
						case "MAXFLOW": case "M":
							performDinic(net);
							break;

						case "PRINTFLOW": case "P":
							performDinic(net);
							System.out.print(net.getFlow().toString());
							break;

						case "CURRENTFLOW": case "C":
							System.out.print(net.getFlow().toString());
							break;

						case "DEBUG": case "D":
							System.out.print(net.toString());
							break;
						case "RESIDUAL": case "R":
							System.out.print("Residual net is:\n"+net.createResidualNet().toString());
							break;

						case "STRICT": case "S":
							System.out.print(net.createNiveauGraph(net.createResidualNet()).toString());
							break;

						case "HELP": case "H":
							printHelp();
							break;

						case "QUIT": case "Q":
							quit = true;
							break;
						default:
							printError(String.format("Unknown command %s.\n Please use HELP to refer to the list of commands" , tokens[0]));
							break;
					}
				} catch (NullPointerException e) {
					printError("Net not defined");
				}
			}
    }
	}
	
	/**
	 * Performs dinic algorithm for given net.
	 * @param net to which algorithm applied
	 */
	
	private static void performDinic(Net net) {
		DinicMaxFlow dinic = new DinicMaxFlow();		
		dinic.computeMaxFlow(net);
		//dinic.step(net);
		if (!net.getFlow().isValidFlow())
			printError("Calculation failed");
		
		int flowValue = net.getFlow().getTotalFlow();
		System.out.print(String.format("Flow is: %d\n", flowValue));
		if (flowValue == 0)
			System.out.print("Sink is unreachable\n");
	}
	

	
	
	
	/**
	 * 	Reads input data (Net) from file and creates net. 
	 * @param fileName name of the file with net data
	 * @return Net net that was built
	 */
	private static Net readNetFromFile(String fileName) {
		Net net = null;
		
		try {
			File file = openFile(fileName);
			Scanner reader = new Scanner(file);
			int nodesNum = reader.nextInt(); //first line must contain nodes number
			net = new GraphWithFlow(nodesNum); 
			while (reader.hasNextInt()) {
				//reads a triplet <source target capacity> from file
				int source = reader.nextInt();
				int target = reader.nextInt();
				int cap = reader.nextInt();
				net.setEdgeCapacity(source-maxflowConstants.INDEX_OFFSET, 
						target-maxflowConstants.INDEX_OFFSET, cap); //add edge to net				
			}
			reader.close();	
			return net;	
		
		} catch (InputMismatchException e) {
			printError("Input Data must be Integers");
		} catch (NoSuchElementException e){
			printError("Input Data incomplete");
		} catch (FileNotFoundException e) {
				printError("File Not Found");
		} catch (ArithmeticException e) {
			printError(e.getMessage());			
		}
		return null;	
			
	}
	
	private static File openFile(String fileName) throws FileNotFoundException{
		
		if (!fileName.endsWith(".txt"))
			fileName+=".txt";
		File file = new File(fileName);
		
		return file;
		
	}
	
	
	/**
	 * Reads input data (Flow) from file and adds it to the Net 
	 * @param filename name of the file with Flow data
	 * @param net the net to which flow belongs
	 * @return Net new net with flow
	 */
	private static Net readFlowFromFile(String fileName, Net net) {
		Net newFlowNet = net;
		try {
			File file = openFile(fileName);
			Scanner reader = new Scanner(file);
			
			int nodesNum = reader.nextInt(); //first read numbet of nodes		
			if (nodesNum!=net.getNumberOfNodes())
				printError("Number of nodes mismatch");
			
			Flow netFlow = newFlowNet.getFlow();
			netFlow.clear();
			
			while (reader.hasNextInt()) {
				//reads a triplet <source target capacity> from file
				int source = reader.nextInt();
				int target = reader.nextInt();
				int cap = reader.nextInt();
				netFlow.setEdgeFlow(source-maxflowConstants.INDEX_OFFSET, 
						target-maxflowConstants.INDEX_OFFSET, cap);			
			}
			reader.close();
			if (!net.getFlow().isValidFlow()) {
				printError("This flow is not valid!");
				
			} else 
				return newFlowNet;
			
		} catch (FileNotFoundException e) {
			printError("File Not Found");
		} catch (InputMismatchException e) {
			printError("Input Data must be Integers");
		} catch (NoSuchElementException e){
			printError("Input Data incomplete");
		
		} catch (ArithmeticException e) {
			printError(e.getMessage());			
		}
		return net;
		
		
	}
	
	/**
	 * prints help information into console
	 */
	private static void printHelp() {
		String helpMsg = 
				"NET: NET <filename> reads data from file <filename> and constructs new net\n"				
				+"FLOW: FLOW <filename> reads data from file <filename> and adds flow to the net\n"
				+"MAXFLOW: calculates maxflow in given net and outputs maxflow capacity\n"
				+"PRINTFLOW: calculates maxflow in given net and it prints out\n"
				+"DEBUG: prints the adjacency matrix of given net\n" 
				+"CURRENTFLOW: prints current flow in given net\n"
				+"RESIDUAL: prints adjacency matrix of residual net constructed form current net and its flow\n"
				+"STRICT: prints the level graph based on current net\n"
				+"HELP: provides description of commands that can be used in this program\n" 
				+"QUIT: quit the program\n";
		System.out.print(helpMsg);
	}
	
	/**
	 * prints error message into console
	 * @param msg message string
	 */
	
	private synchronized static void printError(String msg) {
		
        System.err.println("Error! "+ msg);
	}
}
