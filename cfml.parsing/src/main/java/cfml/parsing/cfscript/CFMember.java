package cfml.parsing.cfscript;

import org.antlr.v4.runtime.Token;

public class CFMember extends CFExpression implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	protected Token token;
	
	private CFExpression expression;
	
	public CFMember(Token _t, CFExpression expr) {
		super(_t);
		token = _t;
		expression = expr;
	}
	
	@Override
	public byte getType() {
		return CFExpression.ARRAYMEMBER;
	}
	
	@Override
	public Token getToken() {
		return token;
	}
	
	@Override
	public String Decompile(int indent) {
		return "[" + expression.Decompile(0) + "]";
	}
	
	@Override
	public String toString() {
		return Decompile(0);
	}
	
	public CFExpression getExpression() {
		return expression;
	}
	
}
