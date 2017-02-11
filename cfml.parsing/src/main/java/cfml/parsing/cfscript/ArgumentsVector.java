package cfml.parsing.cfscript;

import java.util.ArrayList;

public class ArgumentsVector extends ArrayList<CFExpression> {
	
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
	
}
