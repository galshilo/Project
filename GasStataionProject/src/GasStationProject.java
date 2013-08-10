import java.io.IOException;
import java.util.Scanner;


public class GasStationProject {
	public static void main(String[] args) throws IOException, InterruptedException {
		GasStation station = new GasStation(3, 5.25, 3, 7.82);
		Client c1 = new Client(1, "A", "", 90, station);
		station.addClient(c1);
		
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
				break;
			}
			
			c =  (char) System.in.read();
		}
	}
}
