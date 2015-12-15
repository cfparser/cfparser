package cfml.parsing.cfscript.script;

public class CFBreakStatement extends CFParsedStatement implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	public CFBreakStatement(org.antlr.v4.runtime.Token t) {
		super(t);
	}
	
	@Override
	public String Decompile(int indent) {
		return Indent(indent) + "break";
	}
	
}
