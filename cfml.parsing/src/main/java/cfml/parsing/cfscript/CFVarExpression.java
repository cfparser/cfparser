package cfml.parsing.cfscript;

import org.antlr.v4.runtime.Token;

abstract class CFVarExpression extends CFExpression implements java.io.Serializable {
	private static final long serialVersionUID = 1;
	
	protected boolean indirect;
	
	protected CFVarExpression(Token t) {
		super(t);
	}
	
	public void setIndirect(boolean _indirect) {
		indirect = _indirect;
	}
	
	@Override
	public byte getType() {
		return CFExpression.VARIABLE;
	}
	
	public boolean isIndirect() {
		return indirect;
	}
	
}
