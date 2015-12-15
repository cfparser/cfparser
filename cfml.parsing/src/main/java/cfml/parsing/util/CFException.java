/**
 * Exception which can report the line number and column on which it occurred,
 * and what went wrong.
 */

package cfml.parsing.util;

import cfml.parsing.cfscript.CFContext;

public class CFException extends Throwable implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public int line;
	public int col;
	
	public CFException(String mess, CFContext context) {
		super();
		
		int line = context.getLine();
		int col = context.getCol();
		
		init(mess, line, col);
	}
	
	private void init(String mess, int lineInit, int colInit) {
		line = lineInit;
		col = colInit;
	}
	
	@Override
	public String toString() {
		return "[line " + String.valueOf(line) + ", column " + String.valueOf(col) + "] " + super.toString();
	}
	
}
