package cfml.parsing.cfscript;

public class CFCall implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private CFScopeStack _scopeStack;
	
	public CFCall() {
		_scopeStack = new CFScopeStack();
		// The scope for local variables and parameters
		_scopeStack.push(new CFCallScope());
	}
	
	// Push a new ("with") scope
	public void push(CFScope scope) {
		_scopeStack.push(scope);
	}
	
	// Pop the innermost ("with") scope
	public void pop() {
		_scopeStack.pop();
	}
	
	public CFScopeStack scopeStack() {
		return _scopeStack;
	}
}
