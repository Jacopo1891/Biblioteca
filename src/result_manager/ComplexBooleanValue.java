package result_manager;

public class ComplexBooleanValue implements IValidationResult {

	private String message = "";
	private boolean value;
	
	public ComplexBooleanValue( boolean value ) {
		this.value = value;
		this.message = null;
	}
	
	public ComplexBooleanValue( String message ) {
		this.value = false;
		this.message = message;
	}
	
	public ComplexBooleanValue( boolean value, String message ) {
		this.value = value;
		this.message = message;
	}	
	
	public boolean isSuccess() {
		return this.value;
	}

	public String getMessage() {
		return this.message;
	}

}
