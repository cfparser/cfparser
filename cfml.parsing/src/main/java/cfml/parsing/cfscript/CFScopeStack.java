/*
 A stack of CFScope's is used to maintain local scopes, comprising the
 local vars and params, and nesting with object, as well as the global scope,
 comprising special global scope's (e.g. document/window in browsers), and
 the true global scope.
 */

package cfml.parsing.cfscript;

import java.util.ArrayList;

public class CFScopeStack extends ArrayList<CFScope> {
	
	private static final long serialVersionUID = 1L;
	
	public void push(CFScope scope) {
		super.add(scope);
	}
	
	public void pop() {
		super.remove(size() - 1);
	}
	
	public CFScope peek() {
		return super.get(size() - 1);
	}
	
	public CFScope peekFirst() {
		return super.get(0);
	}
	
}
