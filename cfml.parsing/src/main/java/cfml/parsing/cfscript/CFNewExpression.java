package cfml.parsing.cfscript;

import java.util.Vector;

import org.antlr.v4.runtime.Token;

public class CFNewExpression extends CFExpression implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private CFExpression componentPath;
	private Vector args;
	
	public CFNewExpression(Token _t, CFExpression _component, Vector _args) {
		super(_t);
		componentPath = _component;
		args = _args;
	}
	
	@Override
	public String Decompile(int indent) {
		StringBuilder sb = new StringBuilder();
		sb.append("new ");
		sb.append(componentPath.Decompile(0));
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
	
	public CFExpression getComponentPath() {
		return componentPath;
	}
	
	public Vector getArgs() {
		return args;
	}
}
