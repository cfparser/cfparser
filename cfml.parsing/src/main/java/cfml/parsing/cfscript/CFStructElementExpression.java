package cfml.parsing.cfscript;

import java.util.List;

import org.antlr.v4.runtime.Token;

import cfml.parsing.cfscript.script.CFScriptStatement;
import cfml.parsing.util.ArrayBuilder;

public class CFStructElementExpression extends CFExpression implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private CFIdentifier key;
	private CFExpression value;
	
	public CFStructElementExpression(Token t, CFIdentifier _key, CFExpression _value) {
		super(t);
		key = _key;
		value = _value;
	}
	
	@Override
	public String toString() {
		return Decompile(0);
	}
	
	@Override
	public String Decompile(int indent) {
		StringBuilder sb = new StringBuilder();
		sb.append(key.Decompile(0));
		sb.append(':');
		sb.append(value.Decompile(0));
		return sb.toString();
	}
	
	public CFIdentifier getKey() {
		return key;
	}
	
	public CFExpression getValue() {
		return value;
	}
	
	@Override
	public List<CFExpression> decomposeExpression() {
		return ArrayBuilder.createCFExpression(key, value);
	}
	
	@Override
	public List<CFScriptStatement> decomposeScript() {
		return ArrayBuilder.createCFScriptStatement();
	}
}
