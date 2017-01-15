package cfml.parsing.cfscript;

import org.antlr.v4.runtime.Token;

public class CFElvisExpression extends CFExpression implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// instance vars
	private CFExpression _left;
	private CFExpression _right;
	private String operatorImage = "?:";
	
	public CFElvisExpression(Token t, CFExpression left, CFExpression right) {
		super(t);
		_left = left;
		_right = right;
	}
	
	@Override
	public byte getType() {
		return CFExpression.BINARY;
	}
	
	@Override
	public String Decompile(int indent) {
		return "" + _left.Decompile(indent) + " " + operatorImage + " " + _right.Decompile(indent);
	}
	
	public CFExpression getLeft() {
		return _left;
	}
	
	public CFExpression getRight() {
		return _right;
	}
	
	public String getOperatorImage() {
		return operatorImage;
	}
	
}
