package cfml.parsing.cfscript.script;

import java.util.List;

import org.antlr.v4.runtime.Token;

import cfml.parsing.cfscript.CFExpression;
import cfml.parsing.util.ArrayBuilder;

public class CFForInStatement extends CFParsedStatement implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	private CFExpression variable;
	private CFExpression structure;
	private CFScriptStatement body;
	
	public CFForInStatement(Token _t, CFExpression _key, CFExpression _structure, CFScriptStatement _body) {
		super(_t);
		variable = _key;
		structure = _structure; // should be a cfstruct
		body = _body;
	}
	
	@Override
	public void checkIndirectAssignments(String[] scriptSource) {
		body.checkIndirectAssignments(scriptSource);
	}
	
	@Override
	public String Decompile(int indent) {
		StringBuilder sb = new StringBuilder();
		sb.append(Indent(indent));
		sb.append("for( ");
		sb.append(variable.Decompile(0));
		sb.append(" in ");
		sb.append(structure.Decompile(indent));
		sb.append(" ) ");
		sb.append(body.Decompile(indent + 2));
		return sb.toString();
	}
	
	public CFExpression getVariable() {
		return variable;
	}
	
	public CFExpression getStructure() {
		return structure;
	}
	
	public CFScriptStatement getBody() {
		return body;
	}
	
	@Override
	public List<CFExpression> decomposeExpression() {
		return ArrayBuilder.createCFExpression(variable, structure);
	}
	
	@Override
	public List<CFScriptStatement> decomposeScript() {
		return ArrayBuilder.createCFScriptStatement(body);
	}
}
