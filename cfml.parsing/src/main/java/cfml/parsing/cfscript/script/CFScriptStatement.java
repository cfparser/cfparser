package cfml.parsing.cfscript.script;

public interface CFScriptStatement {
	
	public String Decompile(int indent);
	
	public void checkIndirectAssignments(String[] scriptSource);
	
}
