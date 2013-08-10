import java.io.IOException;
import java.util.Vector;
//import java.util.logging.Logger;

public class GasStation extends Thread {

	public enum State{
		OPENED,
		CLOSED,
		PAUSE
	}
	
	//private static Logger allLogger = Logger.getLogger("allLogger");
	private FuelRepository fuelRepository;
	private CoffeeHouse coffeHouse;
	private Vector<Client> clients;
	private Vector<Pump> pumps;
	private double pricePerLiter;
	private State state;
	
	
	public GasStation(int numOfPumps, double coffeePrice, int numOfCashiers, double pricePerLiter) throws SecurityException, IOException{
		this.pumps = new Vector<Pump>();
		this.fuelRepository = new FuelRepository(1, 100, 100, this);
		this.coffeHouse = new CoffeeHouse(1, coffeePrice, numOfCashiers, this);
		this.clients = new Vector<Client>();
		this.pricePerLiter = pricePerLiter;
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
		this.state = State.OPENED;
	}
	
	public void closeStation() {
		this.state = State.CLOSED;
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
	
	@Override
	public String toString(){
		double  pumpsIncome = getPumpsTotalIncome();
		double coffeeHouseIncome = coffeHouse.getTotalIncome();
		return String.format("Daily report:\n " +
							"Total income from fuel pumps: %d\n" +
							"Total income from coffee house: %d\n" +
							"Total income: %d", (int)pumpsIncome, (int)coffeeHouseIncome, (int)(pumpsIncome+coffeeHouseIncome));
	}
}