import java.io.IOException;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;


public class GasStationProject {
	
	private static Logger theLogger = Logger.getLogger("gasStationLogger");
	
	public static void main(String[] args) throws IOException, InterruptedException, ParserConfigurationException, SAXException {
		

		FileHandler allHandler = new FileHandler("Logs\\gasStationLogger.xml", false);
		allHandler.setFormatter(new SimpleFormatter());
		theLogger.addHandler(allHandler);
	
		GasStation station = XMLReader.initStationFromXMLFile();
		
		theLogger.log(Level.INFO, "Station opened");
		System.out.println("Hello,\n" +
				"Press any key to open the GasStation\n" +
				"Press 'c' to add a new client\n" +
				"Press 'e' to close the station and view a financial report");
		
		System.in.read();
		
		station.start();	
		
		char c =  (char) System.in.read();
		while (c != 'e'){
			if (c == 'c'){
				addNewClient(station);
			}
			c =  (char) System.in.read();
		}
		
		station.closeStation();
		theLogger.log(Level.INFO, "Station closed");
		
		for (Handler h: theLogger.getHandlers()){
			h.close();
		}
			
		}
	
	public static void addNewClient(GasStation gasStation) throws SecurityException, IOException{
		Scanner input = new Scanner(System.in);
		int id;
		double litters;
		String firstName,lastName;
		
		firstName = input.nextLine();
		System.out.println("Please enter the client's first name");
		firstName = input.nextLine();
		System.out.println("Please enter the client's last name");
		lastName = input.nextLine();
		System.out.println("Please enter the client's id");
		id = input.nextInt();
		input.nextLine();
		System.out.println("How many litters does the client want to fuel?");
		litters = input.nextDouble();
		input.nextLine();
		Client newCLient = new Client(id, firstName, lastName, litters, gasStation);
		gasStation.addClient(newCLient);
		System.out.println("Client has been added successfully");
		newCLient.start();

		
		}
	}


