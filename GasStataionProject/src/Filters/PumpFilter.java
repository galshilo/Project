package Filters;
import java.util.logging.Filter;
import java.util.logging.LogRecord;

	public class PumpFilter implements Filter{

		private int filter;
		
		public PumpFilter(int filter){
			this.filter = filter;
		}
		@Override
		public boolean isLoggable(LogRecord rec) {
			return (rec.getMessage().contains("Pump_" + filter));
		}	
	}
