
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class LoggerExam {

	
	public static void main(String[] args) throws SecurityException, IOException {
		
		List<Person> ppl;
		Logger allLogger = Logger.getLogger("allLogger");
		

			ppl = new ArrayList<Person>();
			FileHandler allHandler = new FileHandler("allLogger.xml");
			allHandler.setFormatter(new SimpleFormatter());
			allLogger.addHandler(allHandler);
			
			Person p1 = new Person("Gal");
			Person p2 = new Person("Dani");
			Person p3 = new Person("Ron");
			
			ppl.add(p1);
			ppl.add(p2);
			ppl.add(p3);
			
			for (Person p:ppl){
				p.speak();
			}
	}
}
