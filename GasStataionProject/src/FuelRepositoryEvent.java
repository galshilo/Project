
public class FuelRepositoryEvent {

	private FuelRepository repository;
	private String msg;
	
	public FuelRepositoryEvent(FuelRepository repository, String msg){
		this.repository = repository;
		this.msg = msg;
	}
	
	public FuelRepository getFuelRepository(){
		return this.repository;
	}
	
	public String getMessage(){
		return this.msg;
	}
}
