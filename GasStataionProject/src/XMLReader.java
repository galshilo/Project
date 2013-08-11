import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLReader {
	

	public static GasStation initStationFromXMLFile() throws ParserConfigurationException, IOException, SAXException {
		File fXmlFile = new File("stationConfig.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		 DocumentBuilder dBuilder  = dbFactory.newDocumentBuilder();
		 Document doc = dBuilder.parse(fXmlFile);
	     doc.getDocumentElement().normalize();

	    
		Element station = doc.getDocumentElement();//GasStation element
		
		int stationId = Integer.parseInt(station.getAttribute("id"));
		int numberOfPumps = Integer.parseInt(station.getAttribute("numOfPumps"));
		double pricePerLitter = Double.parseDouble(station.getAttribute("pricePerLiter"));
		
		//get FuelRepository		
		Element fuelRepository = (Element)(station.getElementsByTagName("FuelRepository").item(0));  
		double maxCapacity = Double.parseDouble(fuelRepository.getAttribute("maxCapacity"));			
		double currentCapacity = Double.parseDouble(fuelRepository.getAttribute("currentCapacity"));

		//get CoffeeHouse
		Element coffeeHouseElement = (Element)(station.getElementsByTagName("CoffeeHouse").item(0)); 
		int numOfCasheirs = Integer.parseInt(coffeeHouseElement.getAttribute("numOfCashier"));	
		double coffeePrice = Double.parseDouble(coffeeHouseElement.getAttribute("coffeePrice"));	
		
		//get Clients
		Element clientsElement = (Element)(station.getElementsByTagName("Clients").item(0)); 
		NodeList carsNodeList = clientsElement.getElementsByTagName("Client");
		
		GasStation gasStation = new GasStation(stationId, numberOfPumps, coffeePrice, numOfCasheirs, pricePerLitter);
		 
		for(int i=0;i< carsNodeList.getLength(); i++){
			Element carElement = (Element)(carsNodeList.item(i));
			
			int id = Integer.parseInt(carElement.getAttribute("id"));
			String firstName = (String)(carElement.getAttribute("firstName"));
			String lastName = (String)(carElement.getAttribute("lastName"));
			double numOfLiters = Double.parseDouble(carElement.getAttribute("numberOfLiters"));
			
			Client newClient = new Client(id, firstName, lastName, numOfLiters, gasStation);
			
			gasStation.addClient(newClient);
		}
		
		return gasStation;

			
	}
}

