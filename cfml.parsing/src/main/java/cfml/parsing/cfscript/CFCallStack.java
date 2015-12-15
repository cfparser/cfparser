package cfml.parsing.cfscript;

import java.util.Stack;

public class CFCallStack extends Stack<CFCall> implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public CFScopeStack localScope() {
		return peek().scopeStack();
	}
	
}
