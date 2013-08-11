package Filters;
import java.util.logging.Filter;
import java.util.logging.LogRecord;

	public class PoolFilter implements Filter{

		private int filter;
		
		public PoolFilter(int filter){
			this.filter = filter;
		}
		@Override
		public boolean isLoggable(LogRecord rec) {
			return (rec.getMessage().contains("Pool_" + filter));
		}	
	}
