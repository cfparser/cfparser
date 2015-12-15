package cfml.parsing.cfscript;

/**
 * Execution context for evaluating cfscript statements and expressions.
 *   The execution context comprises the global scope list, the local scope list,
 *   the current function hierarchy, and the current source _line and _col.
 */

import java.util.Stack;

public class CFContext implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private CFCallStack _callStack;
	
	private Stack<CFCallStack> callStackStack;
	
	private int _line;
	private int _col;
	
	// This is set by every expression evaluation routine - we need it for the
	// "eval()" builtin
	public CFData _lastExpr;
	
	public CFContext() {
		_callStack = new CFCallStack();
		callStackStack = new Stack<CFCallStack>();
		
		_line = 0;
		_col = 0;
		
		_lastExpr = CFUndefinedValue.UNDEFINED;
	}
	
	public CFCallStack getCallStack() {
		return _callStack;
	}
	
	public Stack<CFCallStack> getCallStackStack() {
		return callStackStack;
	}
	
	public void enterCustomTag() {
		callStackStack.push(_callStack);
		_callStack = new CFCallStack();
	}
	
	public void leaveCustomTag() {
		_callStack = callStackStack.pop();
	}
	
	/** UDF specific functions **/
	
	public boolean containsFunction(String _function) {
		return (getUDF(_function) != null);
	}
	
	private Object getUDF(String function) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void pushCall(CFCall call) {
		_callStack.push(call);
	}
	
	public void popCall() {
		_callStack.pop();
	}
	
	public boolean isCallEmpty() {
		return _callStack.isEmpty();
	}
	
	public void setLineCol(int line, int col) {
		_line = line;
		_col = col;
	}
	
	public int getLine() {
		return _line;
	}
	
	public int getCol() {
		return _col;
	}
	
	@Override
	public String toString() {
		return ""; // might want to put scope print out here
	}
	
}
