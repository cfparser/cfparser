package cfml.parsing.cfscript.script;

import java.util.List;

import org.antlr.v4.runtime.Token;

import cfml.parsing.cfscript.CFExpression;
import cfml.parsing.util.ArrayBuilder;

public class CFImportStatement extends CFParsedStatement implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private CFExpression path;
	boolean all = false;
	
	public CFImportStatement(Token _t, CFExpression _path, boolean _all) {
		super(_t);
		path = _path;
		all = _all;
	}
	
	@Override
	public String Decompile(int indent) {
		return "import " + path.Decompile(0) + (all ? ".*" : "");
	}
	
	public CFExpression getPath() {
		return path;
	}
	
	public boolean isAll() {
		return all;
	}
	
	@Override
	public List<CFExpression> decomposeExpression() {
		return ArrayBuilder.createCFExpression(path);
	}
	
	@Override
	public List<CFScriptStatement> decomposeScript() {
		return ArrayBuilder.createCFScriptStatement();
	}
}
