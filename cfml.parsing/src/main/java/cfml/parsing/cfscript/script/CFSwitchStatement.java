package cfml.parsing.cfscript.script;

import java.util.ArrayList;

/**
 * This class represents a switch statement. 
 */

import java.util.List;

import org.antlr.v4.runtime.Token;

import cfml.parsing.cfscript.CFExpression;
import cfml.parsing.util.ArrayBuilder;

public class CFSwitchStatement extends CFParsedStatement implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public List<CFCase> getCases() {
		return cases;
	}
	
	public CFExpression getVariable() {
		return variable;
	}
	
	private List<CFCase> cases;
	private CFExpression variable;
	
	public CFSwitchStatement(Token _token, CFExpression _variable, List<CFCase> _cases) {
		super(_token);
		cases = _cases;
		variable = _variable;
	}
	
	@Override
	public void checkIndirectAssignments(String[] scriptSource) {
		for (int i = 0; i < cases.size(); i++) {
			cases.get(i).checkIndirectAssignments(scriptSource);
		}
	}
	
	@Override
	public String Decompile(int _indent) {
		StringBuilder sb = new StringBuilder();
		sb.append("switch (");
		sb.append(variable.Decompile(0));
		sb.append("){\n");
		for (int i = 0; i < cases.size(); i++) {
			sb.append(cases.get(i).Decompile(0));
		}
		sb.append("\n}");
		return sb.toString();
	}
	
	@Override
	public List<CFExpression> decomposeExpression() {
		return ArrayBuilder.createCFExpression(variable);
	}
	
	@Override
	public List<CFScriptStatement> decomposeScript() {
		List<CFScriptStatement> retval = new ArrayList<CFScriptStatement>();
		retval.addAll(retval);
		
		return retval;
	}
}
