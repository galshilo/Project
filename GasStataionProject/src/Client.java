import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class Client extends Thread {

	public enum Desires{
		DONE,
		FUEL,
		COFFEE,
		FUELANDCOFFEE;
		
	    public static Desires getRandom() {
	    	
	        return values()[(int) Math.ceil((Math.random() * (Desires.values().length-1)))];
	    }
	}
	
	public enum CoffeeHouseState {
		HANGINCOFFEEHOUSE,
		WAITINGFORCASHIER,
		PAYING,
		DRINKING,
		FREE,
		DONE;
	
	}
	
	public enum PumpState {
		FUELING,
		WAITINGFORPUMP,
		FREE,
		DONE;
	}
	
	private static Logger gasStationLogger = Logger.getLogger("gasStationLogger");
	
	private int id;
	private String firstName;
	private String lastName;
	private Desires desire;
	private double numberOfLiters;
	private Pump pump;
	private CoffeeHouseState coffeeState;
	private PumpState pumpState;
	private Cashier cashier;
	private CoffeeHouse coffeeHouse;
	private GasStation gasStation;
	private Handler handler;
	
	
	public Client(int id, String firstName, String lastName, double numberOfLiters, GasStation gasStation) throws SecurityException, IOException{
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.gasStation = gasStation;
		this.coffeeHouse = gasStation.getCoffeeHouse();
		this.numberOfLiters = numberOfLiters;
		this.desire = Desires.getRandom();
		setCoffeeState(CoffeeHouseState.FREE);
		setPumpState(PumpState.FREE);
		
		this.handler = new FileHandler("Logs\\Clients\\client_" + this.id+".xml");
		this.handler.setFormatter(new SimpleFormatter());
		this.handler.setFilter(new ClientFilter(this.id));
		gasStationLogger.addHandler(this.handler);
	}

	@Override
	public void run() {
		gasStationLogger.log(Level.INFO, String.format("%s: arrived to station",this.toString()));
		gasStationLogger.log(Level.INFO, String.format("%s: desires %s",this.toString(), this.desire.toString()));
		try{
		switch (this.desire){
		case FUEL:
			this.setCoffeeState(CoffeeHouseState.DONE);
			getFuel();
			break;
		case COFFEE:
			this.setPumpState(PumpState.DONE);
			enterCoffeeHouse();
			break;
		case FUELANDCOFFEE:
			getFuel();
			enterCoffeeHouse();
			break;
		default:
			break;
		}
	}catch (InterruptedException e) { }
	while (this.coffeeState != CoffeeHouseState.DONE || this.pumpState != PumpState.DONE){
		synchronized (this) {
			try {
				this.wait();
			} catch (InterruptedException e) { }
		}
	}
	this.desire = Desires.DONE;
	gasStationLogger.log(Level.INFO, String.format("%s: leavs the station, Goodbye!", this.toString()));
}

	
	
	public CoffeeHouseState getCoffeeState(){
		return this.coffeeState;
	}
	
	public PumpState getPumpState(){
		return this.pumpState;
	}
	
	public void setCoffeeState(CoffeeHouseState state){
		this.coffeeState = state;
	}
	
	public void setPumpState(PumpState state){
		this.pumpState = state;
	}
	
	
	public String getFirstName(){
		return this.firstName;
	}
	
	public int getClientId(){
		return this.id;
	}
	
	public String getLastName(){
		return this.lastName;
	}
	
	public double getNumberOfLiters(){
		return this.numberOfLiters;
	}
	
	public void getFuel(){
		setPump();
		pump.addClient(this);
	}
	
	public void  getCoffee() throws InterruptedException{
		this.cashier = coffeeHouse.assignCashierToClient();
		gasStationLogger.log(Level.INFO, String.format("%s: assigned to %s", this.toString(), this.cashier.toString()));
		this.cashier.addClient(this);
		this.coffeeState = CoffeeHouseState.WAITINGFORCASHIER;
	}
	
	public void setPump(){
		this.pump = gasStation.assignPumpToClient();
		gasStationLogger.log(Level.INFO, String.format("%s: assigned to %s", this.toString(), this.pump.toString()));
	}
	
	private void hangInCoffeeHouse() throws InterruptedException{
			synchronized (this) {
				gasStationLogger.log(Level.INFO, String.format("%s: hanging in the coffee house", this.toString()));
				this.coffeeState = CoffeeHouseState.HANGINCOFFEEHOUSE;
				Thread.sleep( (long) Math.random() * 5000);
				gasStationLogger.log(Level.INFO, String.format("%s: done hanging in the coffee house", this.toString()));
		}
	}
	
	public void removeFromCashierQueue(){
		gasStationLogger.log(Level.INFO, String.format("%s: has been removed from %s", this.toString(), this.cashier.toString()));
		this.cashier.removeClient(this);
		this.setCoffeeState(CoffeeHouseState.FREE);
	}
	
	private void drinkCoffee() throws InterruptedException{
		synchronized (this) {
		gasStationLogger.log(Level.INFO, String.format("%s: drinking his coffee... ahhh", this.toString()));
		this.coffeeState = CoffeeHouseState.DRINKING;
		Thread.sleep( (long) Math.random() * 5000);
		this.coffeeState = CoffeeHouseState.DONE;
		gasStationLogger.log(Level.INFO, String.format("%s: done drinking his coffee... ahhh", this.toString()));
		this.notifyAll();
		}
	}
	
	public void enterCoffeeHouse() throws InterruptedException{
		hangInCoffeeHouse();
		getCoffee();
		synchronized (this) {
			this.wait();//pay first bitch
		}
		drinkCoffee();	
	}
	
	
	@Override
	public String toString(){
		return String.format("Client_%d %s %s", this.getClientId(), this.getFirstName(), this.getLastName());
	}
	
}