package cfml.parsing.cfscript.script;

import org.antlr.v4.runtime.Token;

import cfml.parsing.cfscript.CFExpression;

public class CFReturnStatement extends CFParsedStatement {
	
	private static final long serialVersionUID = 1L;
	
	private CFExpression _ret; // null if no return
	
	// "ret" is null if there is no return value
	public CFReturnStatement(Token t, CFExpression ret) {
		super(t);
		_ret = ret;
	}
	
	@Override
	public String Decompile(int indent) {
		StringBuilder s = new StringBuilder();
		s.append(Indent(indent));
		s.append("return ");
		if (_ret != null) {
			s.append(_ret.Decompile(indent));
		}
		return s.toString();
	}
	
	public CFExpression getExpression() {
		return _ret;
	}
}
