package cfml.parsing.cfscript.script;

import org.antlr.v4.runtime.CommonTokenStream;

import cfml.parsing.cfscript.CFIdentifier;

public class CFCatchStatement extends CFCatchClause implements CFScriptStatement {
	
	private CFIdentifier var;
	private CFScriptStatement body;
	CommonTokenStream tokens;
	
	public CFCatchStatement(String _type, CFIdentifier _var, CFScriptStatement _body) {
		type = _type;
		var = _var;
		body = _body;
	}
	
	public CFCatchStatement(CFIdentifier _type, CFIdentifier _var, CFScriptStatement _body) {
		type = _type.Decompile(0);
		var = _var;
		body = _body;
	}
	
	public CFIdentifier getVariable() {
		return var;
	}
	
	public CFScriptStatement getCatchBody() {
		return body;
	}
	
	@Override
	public void checkIndirectAssignments(String[] scriptSource) {
		body.checkIndirectAssignments(scriptSource);
	}
	
	@Override
	public String Decompile(int indent) {
		StringBuilder sb = new StringBuilder();
		sb.append("catch( ");
		sb.append(type);
		sb.append(' ');
		sb.append(var.Decompile(0));
		sb.append(")\n");
		sb.append(body.Decompile(0));
		return sb.toString();
	}
	
	public CommonTokenStream getTokens() {
		return tokens;
	}
	
	public void setTokens(CommonTokenStream tokens) {
		this.tokens = tokens;
	}
}
