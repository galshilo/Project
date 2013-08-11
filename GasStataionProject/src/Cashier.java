import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Cashier extends Thread {

	private static Logger gasStationLogger = Logger.getLogger("gasStationLogger");
	
	private int id;
	private Queue<Client> queue;
	private double income;
	private CoffeeHouse coffeeHouse;
	private Client currentClient;
	private GasStation gasStation;
	
	
	public Cashier(int id, CoffeeHouse coffeehouse, GasStation gasStation){
		this.id = id;
		this.queue = new LinkedList<Client>();
		this.income = 0;
		this.coffeeHouse = coffeehouse;
		this.gasStation = gasStation;
	}	
	
	@Override
	public void run() {
		while (this.gasStation.getCurrentState() == GasStation.State.OPENED){
			while (!queue.isEmpty()){
				try {
					servClient();
				} catch (InterruptedException e) { }
				clearClient();
			}
			synchronized (this) {
				try {
					wait();
				} catch (InterruptedException e) { }
			}
	    }
	}

	private void clearClient() {
		currentClient = null;
	}
	
	public int getCashierId(){
		return this.id;
	}
	
	public double getIncome(){
		return this.income;
	}
	
	public void servClient() throws InterruptedException{
		getNextClientFromQueue();
		
		if (currentClient.getPumpState() == Client.PumpState.FUELING){
			gasStationLogger.log(Level.INFO,String.format("%s: removed %s", this.coffeeHouse.toString(), this.currentClient.toString()));
			return;
		}
		
		synchronized (currentClient) {
			if (currentClient.getCoffeeState() == Client.CoffeeHouseState.DONE){
				currentClient.notifyAll();
				return;
			}
			this.currentClient.setCoffeeState(Client.CoffeeHouseState.PAYING);
			gasStationLogger.log(Level.INFO,String.format("%s: %s is paying \n", this.coffeeHouse.toString(), this.currentClient.toString()));
			Thread.sleep((long) (Math.random() * 5000));
			addMoney();
			this.currentClient.setCoffeeState(Client.CoffeeHouseState.FREE);
			gasStationLogger.log(Level.INFO,String.format("%s: %s done paying \n", this.coffeeHouse.toString(), this.currentClient.toString()));
			currentClient.notifyAll();
		}
	}
	
	
	private void addMoney() throws InterruptedException{
		this.income += coffeeHouse.getCoffeePrice();
		gasStationLogger.log(Level.INFO,String.format("%s: income increased by %f", this.coffeeHouse.toString(), coffeeHouse.getCoffeePrice()));
	}

	public void addClient(Client client){
		client.setCoffeeState(Client.CoffeeHouseState.WAITINGFORCASHIER);
		this.queue.add(client);
		
		if (queue.size() == 1){
			synchronized (this) {
				this.notifyAll();
			}
		}
	}
	
	public void removeClient(Client client){
		queue.remove(client);
	}
	
	private void getNextClientFromQueue() {
		this.currentClient = queue.poll();
		gasStationLogger.log(Level.INFO,String.format("%s: ready to serve %s", this.coffeeHouse.toString(), this.currentClient.toString()));
	}
	
	@Override
	public String toString(){
		return String.format("Cashier #%d:\n", this.getCashierId());

	}
}
