package cfml.dictionary;

public class Return {
	String parameterName;
	String type;
	
	public Return(String parameterName, String type) {
		super();
		this.parameterName = parameterName;
		this.type = type;
	}
	
	public String getParameterName() {
		return parameterName;
	}
	
	public String getType() {
		return type;
	}
}
