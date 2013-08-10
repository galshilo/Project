import java.util.logging.Filter;
import java.util.logging.LogRecord;

	public class CoffeeHouseFilter implements Filter{

		private int filter;
		
		public CoffeeHouseFilter(int filter){
			this.filter = filter;
		}
		@Override
		public boolean isLoggable(LogRecord rec) {
			return (rec.getMessage().contains("CoffeeHouse_" + filter));
		}	
	}
