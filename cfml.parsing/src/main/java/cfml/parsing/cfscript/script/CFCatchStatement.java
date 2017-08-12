package cfml.parsing.cfscript.script;

import java.util.List;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;

import cfml.parsing.cfscript.CFExpression;
import cfml.parsing.cfscript.CFIdentifier;
import cfml.parsing.util.ArrayBuilder;

public class CFCatchStatement implements CFScriptStatement {
	
	private CFIdentifier var;
	private CFScriptStatement body;
	CommonTokenStream tokens;
	final Token token;
	Object parent;
	
	public String getType() {
		return type;
	}
	
	protected String type;
	
	public CFCatchStatement(String _type, CFIdentifier _var, CFScriptStatement _body) {
		type = _type;
		var = _var;
		body = _body;
		if (_var != null) {
			token = var.getToken();
			var.setParent(this);
		} else if (body != null) {
			token = _body.getToken();
			body.setParent(this);
		} else
			token = null;
	}
	
	public CFCatchStatement(CFIdentifier _type, CFIdentifier _var, CFScriptStatement _body) {
		type = _type.Decompile(0);
		var = _var;
		body = _body;
		if (var != null)
			token = var.getToken();
		else if (body != null)
			token = body.getToken();
		else
			token = null;
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
	
	@Override
	public Token getToken() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<CFExpression> decomposeExpression() {
		return ArrayBuilder.createCFExpression(var);
	}
	
	@Override
	public List<CFScriptStatement> decomposeScript() {
		return ArrayBuilder.createCFScriptStatement(body);
	}
	
	public Object getParent() {
		return parent;
	}
	
	public void setParent(Object parent) {
		this.parent = parent;
	}
}
