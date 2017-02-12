package cfml.parsing.cfscript;

import java.util.List;

import cfml.parsing.cfscript.script.CFScriptStatement;
import cfml.parsing.util.ArrayBuilder;

/**
 * Definition of expression tree for a unary expression.
 */

public class CFNestedExpression extends CFExpression implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	private CFExpression sub;
	
	public CFNestedExpression(org.antlr.v4.runtime.Token _t, CFExpression _sub) {
		super(_t);
		sub = _sub;
	}
	
	@Override
	public byte getType() {
		return CFExpression.NESTED;
	}
	
	@Override
	public String Decompile(int indent) {
		StringBuilder sb = new StringBuilder();
		sb.append('#');
		sb.append(sub.Decompile(0));
		sb.append('#');
		return sb.toString();
	}
	
	public CFExpression getSub() {
		return sub;
	}
	
	@Override
	public List<CFExpression> decomposeExpression() {
		return ArrayBuilder.createCFExpression(sub);
	}
	
	@Override
	public List<CFScriptStatement> decomposeScript() {
		return ArrayBuilder.createCFScriptStatement();
	}
	
}
