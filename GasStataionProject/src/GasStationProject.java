import java.io.IOException;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class GasStationProject {
	
	private static Logger theLogger = Logger.getLogger("gasStationLogger");
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		FileHandler allHandler = new FileHandler("Logs\\gasStationLogger.xml", false);
		allHandler.setFormatter(new SimpleFormatter());
		theLogger.addHandler(allHandler);
		
		GasStation station = new GasStation(3, 5.25, 3, 7.82);
		Client c1 = new Client(1, "A", "", 90, station);
		station.addClient(c1);
		
		theLogger.log(Level.INFO, "Station opened");
		System.out.println("Hello,\n" +
				"Press any key to open the GasStation\n" +
				"Press 'c' to add a new client\n" +
				"Press 'e' to close the station and view a financial report");
		
		System.in.read();
		
		station.start();	
		
		char c =  (char) System.in.read();
		while (c != 'e'){
			switch (c){
			case 'c':
				station.addClient(new Client(1,"Dummy","", 10, station));
				break;
			case 'q':
				station.closeStation();
				theLogger.log(Level.INFO, "Station closed");
				break;
			}
			
			c =  (char) System.in.read();
		}
	}
}
