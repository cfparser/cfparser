package cfml.parsing.cfscript;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Token;

import cfml.parsing.cfscript.script.CFScriptStatement;
import cfml.parsing.util.ArrayBuilder;

public class CFNewExpression extends CFExpression implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private CFExpression componentPath;
	private List<CFExpression> args;
	
	public CFNewExpression(Token _t, CFExpression _component, ArrayList<CFExpression> _args) {
		super(_t);
		componentPath = _component;
		if (componentPath != null) {
			componentPath.setParent(this);
		}
		args = _args;
		if (args != null) {
			args.forEach(arg -> arg.setParent(this));
		}
	}
	
	@Override
	public String Decompile(int indent) {
		StringBuilder sb = new StringBuilder();
		sb.append("new ");
		sb.append(componentPath.Decompile(0));
		sb.append("(");
		for (int i = 0; i < args.size(); i++) {
			sb.append((args.get(i)).toString());
			if (i < args.size() - 1) {
				sb.append(", ");
			}
		}
		sb.append(")");
		
		return sb.toString();
	}
	
	public CFExpression getComponentPath() {
		return componentPath;
	}
	
	public List getArgs() {
		return args;
	}
	
	@Override
	public List<CFExpression> decomposeExpression() {
		List<CFExpression> retval = new ArrayList<CFExpression>();
		retval.add(componentPath);
		retval.addAll(args);
		return retval;
	}
	
	@Override
	public List<CFScriptStatement> decomposeScript() {
		return ArrayBuilder.createCFScriptStatement();
	}
}
