package cfml.parsing.cfscript;

import org.antlr.v4.runtime.Token;

public class CFElvisExpression extends CFBinaryExpression {
	
	private static final long serialVersionUID = 1L;
	
	public CFElvisExpression(Token t, CFExpression left, CFExpression right) {
		super(t, left, right);
		operatorImage = "?:";
	}
	
	@Override
	public String Decompile(int indent) {
		return "" + _left.Decompile(indent) + " " + operatorImage + " " + _right.Decompile(indent);
	}
	
}
