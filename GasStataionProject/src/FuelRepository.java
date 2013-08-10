import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import java.util.logging.Handler;

import javax.sql.rowset.spi.SyncResolver;


public class FuelRepository extends Thread  {

	public enum State {
		BUSSY,
		READY
	}
	
		private static Logger gasStationLogger = Logger.getLogger("gasStationLogger");
		
		private int id;
		private double maxCapacity;
	    private double currentCapacity;
	    private State state;
	    private GasStation gasStation;
	    private final double MINIMUMLIMIT = 0.20;
		private Handler handler;
	    
	    
		public FuelRepository(int id, double maxCapacity, double currentCapacity, GasStation gasStation) throws SecurityException, IOException{
			this.id = id;
			this.maxCapacity = maxCapacity;
			this.currentCapacity = currentCapacity;
			this.state = State.READY;
			this.gasStation = gasStation;
			
			this.handler = new FileHandler("Logs\\FuelPools\\fuelPool_" + this.id+".xml");
			this.handler.setFormatter(new SimpleFormatter());
			this.handler.setFilter(new PoolFilter(this.id));
			gasStationLogger.addHandler(this.handler);
		}
		
		@Override
		public void run() {
			while (gasStation.getCurrentState() == GasStation.State.OPENED){
				if (hasReachedLimit()){
					setAlert();
				}
				synchronized (this){
					try {					
						wait();
					} catch (InterruptedException e) { }	
				}
			}
		}
		
		public State getCurrentState(){
			return this.state;
		}
		
		public double getCurrentFuelAmmount() {
			return currentCapacity;
		}
				
		public void setCurrentFuelAmmount(double ammount) {
			this.currentCapacity -= ammount;
			//exception if no enough fuel
		}
		
		public boolean isEmpty(){
			return this.currentCapacity == 0;
		}
		
		public boolean hasReachedLimit(){
			return  (currentCapacity/maxCapacity) < MINIMUMLIMIT;
		}
		
		
		public void addFuel() throws InterruptedException{
			synchronized (this) {
				gasStationLogger.log(Level.WARNING, String.format("%s: beeing filled", this.toString()));
				this.state = State.BUSSY;
				Thread.sleep((long) (Math.random() * 10000));
				this.currentCapacity = maxCapacity;
				this.state = State.READY;
				gasStationLogger.log(Level.WARNING, String.format("%s: done filling", this.toString()));
			}		
		}

		private void setAlert(){
			gasStationLogger.log(Level.WARNING, String.format("%s: reached minimum limit of %f liters prapering to refill pool", this.toString(), this.currentCapacity));

			try {
				addFuel();
			} catch (InterruptedException e1) { }
		}
		
		@Override 
		public String toString(){
			return String.format("Pool_%d", this.id);
		}
}
		