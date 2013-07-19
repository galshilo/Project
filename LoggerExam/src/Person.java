import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class Person {

	private String name;
	private final FileHandler theHandler;
	private PersonsFilter filter;
	private static Logger allLogger = Logger.getLogger("allLogger");
	
	public Person(String name) throws SecurityException, IOException{
		this.name = name;
		theHandler = new FileHandler(name+".xml");
		filter = new PersonsFilter(this.name);
		theHandler.setFormatter(new SimpleFormatter());
		theHandler.setFilter(filter);
		allLogger.addHandler(this.theHandler);
	}
	
	public void speak(){
		String str = String.format(" %s: \t speaks", this.name);
		allLogger.log(Level.INFO, str);
	}
	
	public void shout(){
		String str = String.format(" %s: \t shouts", this.name);
		allLogger.log(Level.WARNING, str);
	}
}


