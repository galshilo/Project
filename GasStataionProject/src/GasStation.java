import java.io.IOException;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
//import java.util.logging.Logger;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class GasStation extends Thread {

	public enum State{
		OPENED,
		CLOSED,
		PAUSE
	}
	
	private static Logger gasStationLogger = Logger.getLogger("gasStationLogger");;
	
	private int id;
	private FuelRepository fuelRepository;
	private CoffeeHouse coffeHouse;
	private Vector<Client> clients;
	private Vector<Pump> pumps;
	private double pricePerLiter;
	private State state;
	private Handler handler;
	
	
	public GasStation(int id, int numOfPumps, double coffeePrice, int numOfCashiers, double pricePerLiter) throws SecurityException, IOException{
		this.id = id;
		this.pumps = new Vector<Pump>();
		this.fuelRepository = new FuelRepository(1, 100, 100, this);
		this.coffeHouse = new CoffeeHouse(1, coffeePrice, numOfCashiers, this);
		this.clients = new Vector<Client>();
		this.pricePerLiter = pricePerLiter;
		this.handler = new FileHandler("Logs\\GasStation_" + this.id+".xml");
		this.handler.setFormatter(new SimpleFormatter());
		gasStationLogger.addHandler(this.handler);
		initPumps(numOfPumps);
		openStation();


	}
	
	@Override
	public void run(){
		fuelRepository.start();
		startPumps();
		coffeHouse.start();
		startClients();
	}

	private void startClients() {
		for (Client c : clients){
			c.start();
		}
	}

	private void startPumps() {
		for (Pump p : pumps){
			p.start();
		}
	}
	
	
	public void addClient(Client client){
		this.clients.add(client);
	}
	
	public double getTotalIncome(){
		return coffeHouse.getTotalIncome() + getPumpsTotalIncome();
	}
	
	public void openStation(){
		gasStationLogger.log(Level.INFO, String.format("%s: Opened", this.toString()));
		this.state = State.OPENED;
	}
	
	public void closeStation() {
		gasStationLogger.log(Level.INFO, String.format("%s: Closed", this.toString()));
		this.state = State.CLOSED;
		gasStationLogger.log(Level.INFO, String.format("%s: ", this.getFinancialReport()));
	}
	
	public double getPricePerLiter(){
		return this.pricePerLiter;
	}
	
	public State getCurrentState(){
		return this.state;
	}
	
	private void initPumps(int numOfPumps) throws SecurityException, IOException{
		for (int i = 1;i <= numOfPumps; i++){
			addPump(i);
		}
	}
	
	private void addPump(int id) throws SecurityException, IOException{
		this.pumps.add(new Pump(id ,this.fuelRepository, this));
	}
	
	public Pump assignPumpToClient(){
		return pumps.elementAt((int) (Math.random() * pumps.size()));
	}
	
	private double getPumpsTotalIncome(){
		double total = 0;
		for (Pump p : pumps){
			total += p.getIncome();
		}
		return total;
	}

	public CoffeeHouse getCoffeeHouse(){
		return this.coffeHouse;
	}
	
	public String getFinancialReport(){
		return String.format("Daily report:\n " +
							"Total income from fuel pumps: %f\n" +
							"Total income from coffee house: %f\n" +
							"Total income: %f", getPumpsTotalIncome(), coffeHouse.getTotalIncome(), this.getTotalIncome());
		
	}
	
	@Override
	public String toString(){
		return String.format("GasStation_%d", this.id);
	}
}