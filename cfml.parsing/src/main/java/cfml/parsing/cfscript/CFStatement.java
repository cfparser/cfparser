/*
 * Expression tree's for javascript statements.
 */

package cfml.parsing.cfscript;

public interface CFStatement {
	
	public String Decompile(int indent);
	
	public void checkIndirectAssignments(String[] scriptSource);
	
}
