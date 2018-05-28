/*
 * Expression tree's for javascript statements.
 */

package cfml.parsing.cfscript;

public interface CFStatement extends HasToken {
	
	public String Decompile(int indent);
	
	public void checkIndirectAssignments(String[] scriptSource);
	
	public Object getParent();
	
}
