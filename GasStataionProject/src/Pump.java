import java.io.IOException;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
//import java.util.logging.Handler;
import java.util.logging.Handler;

import Filters.PumpFilter;

public class Pump extends Thread {

	public enum State
	{
		BUSSY,
		READY
	}
	
	private static Logger gasStationLogger = Logger.getLogger("gasStationLogger");
	
	private int identifier;
	private FuelRepository fuelRepository;
	private State state;
	private Queue<Client> queue;
	private double income;
	private Handler handler;
	private Client currentClient;
	private GasStation gasStation;
	
	public Pump(int id, FuelRepository repository, GasStation gasStation) throws SecurityException, IOException{
		this.identifier = id;
		this.fuelRepository = repository;
		this.gasStation = gasStation;
		this.queue = new LinkedList<Client>();
		this.handler = new FileHandler("Logs\\Pumps\\pump_" + this.identifier+".xml");
		this.handler.setFormatter(new SimpleFormatter());
		this.handler.setFilter(new PumpFilter(this.getPumpId()));
		gasStationLogger.addHandler(this.handler);
	}
	
	@Override
	public void run(){
		while (gasStation.getCurrentState() == GasStation.State.OPENED){
			while (!queue.isEmpty()){
				try {
					servClient();
				} catch (InterruptedException e) { }
				clearClient();
			}
			synchronized (this){
				try {
					gasStationLogger.log(Level.INFO,String.format("%s: sleeping, waiting for new client", this.toString()));
					wait();
				} catch (InterruptedException e) { }			
			}
		}
	}

	private void clearClient() {
		currentClient = null;
	}
	
	public void giveFuel(double ammount) throws InterruptedException{
			synchronized (fuelRepository) {
				synchronized (currentClient) {
				this.setPumpState(State.BUSSY);
					gasStationLogger.log(Level.INFO,String.format("%s: fueling %s", this.toString(), this.currentClient.toString()));	
					this.currentClient.setPumpState(Client.PumpState.FUELING);		
					Thread.sleep((long) (Math.random() * 5000));
					this.currentClient.setPumpState(Client.PumpState.DONE);
					this.fuelRepository.setCurrentFuelAmmount(ammount);
					gasStationLogger.log(Level.INFO,String.format("%s: done fueling %s", this.toString(), this.currentClient.toString()));
					this.setPumpState(State.READY);
					currentClient.notifyAll();
					fuelRepository.notifyAll();
				}
			}
	}
	
	public int getPumpId(){
		return this.identifier;
	}
	
	public void addIncome(double ammount){
		gasStationLogger.log(Level.INFO,String.format("%s: income increased by %f", this.toString(), ammount));
		this.income += ammount;
	}
	
	public double getIncome(){
		return this.income;
	}
	
	public void addClient(Client client){
		client.setPumpState(Client.PumpState.WAITINGFORPUMP);
		this.queue.add(client);
		gasStationLogger.log(Level.INFO,String.format("%s: client %s added to queue", this.toString(), client.toString()));
		if (queue.size() == 1){
			synchronized (this) {
				this.notifyAll(); //wakes up the pump (only if queue was empty)
				gasStationLogger.log(Level.INFO,String.format("%s: wakes up!", this.toString()));
			}
		}
	}
	
	public void removeClient(Client client){
		this.queue.remove(client);
	}
	
	private boolean handleClient() throws InterruptedException {
		getNextClientFromQueue();
		
		Client.CoffeeHouseState currentCoffeeState = this.currentClient.getCoffeeState();
		if (currentCoffeeState == Client.CoffeeHouseState.HANGINCOFFEEHOUSE ||
			currentCoffeeState == Client.CoffeeHouseState.WAITINGFORCASHIER)
		{
			synchronized (this.currentClient) {
				gasStationLogger.log(Level.INFO,String.format("%s: need to choose between coffee and fuel", this.currentClient.toString()));
				int randomState = (int)(Math.random() * 2);
				gasStationLogger.log(Level.INFO,String.format("%s: decided to %s",this.currentClient.toString(), (randomState == 0) ? "fuel" : "drink coffee"));
				if (randomState == 0){//decided he wants to fuel, so he leaves the coffee house;
					this.currentClient.removeFromCashierQueue();
					this.currentClient.setCoffeeState(Client.CoffeeHouseState.DONE);
					return true;

			}
			else {
				this.currentClient.setPumpState(Client.PumpState.DONE);
				gasStationLogger.log(Level.INFO,String.format("%s: client %s decided to leave the queue",this.toString(), this.currentClient.toString()));
				return false;
			}
			}
		}
		if (currentCoffeeState == Client.CoffeeHouseState.PAYING ||
			currentCoffeeState == Client.CoffeeHouseState.DRINKING) {
			this.currentClient.setPumpState(Client.PumpState.DONE);
			return false;
		}
		return true;
	}

	private void getNextClientFromQueue() {
		currentClient = queue.poll();		
		gasStationLogger.log(Level.INFO,String.format("%s: ready to serve client %s", this.toString(),  this.currentClient.toString()));
	}
	
	public void servClient() throws InterruptedException{
		if (handleClient()){
			
			giveFuel(this.currentClient.getNumberOfLiters());
			addIncome(this.currentClient.getNumberOfLiters() * this.gasStation.getPricePerLiter());
		}
	}

	public State getPumpState() {
		return state;
	}

	public void setPumpState(State state) {
		this.state = state;
		gasStationLogger.log(Level.INFO,String.format("%s: state changed to %s", this.toString(), state.toString()));
	}
	
	@Override
	public String toString(){
		return String.format("Pump_%d", this.identifier);
	}
}
