package cfml.parsing.cfscript;

import java.util.ArrayList;

import org.antlr.v4.runtime.Token;

public class ArgumentsVector extends ArrayList<CFExpression> implements HasToken {
	
	private static final long serialVersionUID = 1L;
	
	public ArgumentsVector() {
		super();
	}
	
	public void addNamedArg(CFExpression name, CFExpression _val) {
		if (name == null) {
			add(_val);
		} else {
			add(new CFAssignmentExpression(name.getToken(), name, _val));
		}
	}
	
	@Override
	public Token getToken() {
		return size() > 0 ? get(0).getToken() : null;
	}
	
}
