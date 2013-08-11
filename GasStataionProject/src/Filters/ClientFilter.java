package Filters;
import java.util.logging.Filter;
import java.util.logging.LogRecord;

	public class ClientFilter implements Filter{

		private int filter;
		
		public ClientFilter(int filter){
			this.filter = filter;
		}
		@Override
		public boolean isLoggable(LogRecord rec) {
			return (rec.getMessage().contains("Client_" + filter));
		}	
	}
