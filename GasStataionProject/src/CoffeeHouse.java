import java.io.IOException;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class CoffeeHouse extends Thread {
	
	
	private static Logger gasStationLogger = Logger.getLogger("gasStationLogger");
	
	private int id;
	private Vector<Cashier> cashiers;
	private GasStation gasStation;
	private final double coffeePrice;
	private Handler handler;
	
	public CoffeeHouse(int id, double coffeePrice, int numOfCashiers, GasStation gasStation) throws SecurityException, IOException{
		this.id = id;
		this.cashiers = new Vector<Cashier>();
		this.coffeePrice = coffeePrice;
		this.gasStation = gasStation;
		initCashiers(numOfCashiers);

		this.handler = new FileHandler("Logs\\CoffeeHouses\\CoffeeHouse_" + this.id+".xml");
		this.handler.setFormatter(new SimpleFormatter());
		this.handler.setFilter(new CoffeeHouseFilter(this.id));
		gasStationLogger.addHandler(this.handler);
	}
	
	@Override
	public void run() {
		startCashiers();
	}

	private void startCashiers() {
		for (Cashier c : cashiers){
			c.start();
		}
	}
	
	public int getCoffeeHouseId(){
		return this.id;
	}
	
	private void initCashiers(int numOfCashiers){
		for (int i = 1 ;i<= numOfCashiers; i++){
			addCashier(i);
		}
	}
	
	private void addCashier(int id){
		Cashier cashier = new Cashier(id, this, this.gasStation);
		cashiers.add(cashier);
		gasStationLogger.log(Level.INFO, String.format("%s: new cashier added %s", this.toString(), cashier.toString()));
	}
	
	public double getCoffeePrice(){
		return this.coffeePrice;
	}
	
	public double getTotalIncome(){
		double total = 0;
		for (Cashier cashier : cashiers){
			total += cashier.getIncome();
		}
		return total;
	}
	
	public Cashier assignCashierToClient(){
		return cashiers.elementAt((int) (Math.random() * cashiers.size() - 1 ));
	}
	
	@Override
	public String toString(){
		return String.format("CoffeeHouse_%d", this.id);
	}
}
