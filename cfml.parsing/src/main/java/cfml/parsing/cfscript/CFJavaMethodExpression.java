package cfml.parsing.cfscript;

/**
 * This cannot be invoked/evaluated as a normal CFExpression is.
 * It simply acts as a holder of the Java method attributes 
 * (i.e. method name, and arguments being passed to it)
 */

import java.util.Vector;

import org.antlr.v4.runtime.Token;

public class CFJavaMethodExpression extends CFExpression {
	
	private static final long serialVersionUID = 1L;
	
	private CFExpression name;
	private Vector<CFExpression> args; // Vector of CFExpression's
	private boolean _onMissingMethod = false;
	
	public CFJavaMethodExpression(Token _t, CFExpression _name, Vector<CFExpression> _args) {
		super(_t);
		name = _name;
		args = _args;
	}
	
	@Override
	public byte getType() {
		return CFExpression.FUNCTION;
	}
	
	public String getFunctionName() {
		return ((CFIdentifier) name).getName();
	}
	
	public Vector<CFExpression> getArguments() {
		return args;
	}
	
	public boolean isOnMethodMissing() {
		return _onMissingMethod;
	}
	
	public void setOnMethodMissing() {
		_onMissingMethod = true;
	}
	
	@Override
	public String Decompile(int indent) {
		StringBuilder sb = new StringBuilder();
		sb.append(name.Decompile(indent));
		sb.append("(");
		
		for (int i = 0; i < args.size(); i++) {
			sb.append((args.elementAt(i)).toString());
			if (i < args.size() - 1) {
				sb.append(", ");
			}
		}
		
		sb.append(")");
		
		return sb.toString();
	}
	
	public CFExpression getName() {
		return name;
	}
	
	public Vector<CFExpression> getArgs() {
		return args;
	}
	
	public boolean isOnMissingMethod() {
		return _onMissingMethod;
	}
	
}
