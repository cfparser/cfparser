package cfml.parsing.cfscript;

public abstract class CFExpression extends CFParsedStatement implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	public static byte FUNCTION = 0, ASSIGNMENT = 1, BINARY = 2, LITERAL = 3, IDENTIFIER = 4, VARIABLE = 5, UNARY = 6,
			ARRAYMEMBER = 7, NESTED = 8;
	
	public byte getType() {
		return -1;
	}
	
	public boolean isEscapeSingleQuotes() {
		return false;
	}
	
	public CFExpression(org.antlr.v4.runtime.Token t) {
		super(t);
	}
	
}
