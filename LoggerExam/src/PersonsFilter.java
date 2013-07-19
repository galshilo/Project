import java.util.logging.Filter;
import java.util.logging.LogRecord;



public class PersonsFilter implements Filter{

	private String filter;
	
	public PersonsFilter(String filter){
		this.filter = filter;
	}
	@Override
	public boolean isLoggable(LogRecord rec) {
		return (rec.getMessage().contains(filter));
	}
	
}
