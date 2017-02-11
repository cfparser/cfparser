package cfml.parsing.cfscript;

import java.util.Vector;

import org.antlr.v4.runtime.Token;

import cfml.parsing.cfscript.script.CFScriptStatement;
import cfml.parsing.reporting.ParseException;

public class CFFunctionExpression extends CFMember {
	private static final long serialVersionUID = 1L;
	
	private CFIdentifier nameId;
	private Vector<CFExpression> args; // Vector of CFExpression's
	private boolean isUDF = true;
	private CFScriptStatement body;
	
	// private boolean isParamExists;
	public CFFunctionExpression(CFIdentifier _name, Vector<CFExpression> _args) throws ParseException {
		this(_name.getToken(), _name, _args);
	}
	
	public CFFunctionExpression(Token t, CFIdentifier _name, Vector<CFExpression> _args) throws ParseException {
		super(t, null);
		nameId = _name;
		args = _args;
		isUDF = false;
	}
	
	@Override
	public byte getType() {
		return CFExpression.FUNCTION;
	}
	
	public String getFunctionName() {
		if (nameId instanceof CFFullVarExpression) {
			return ((CFFullVarExpression) nameId).getLastIdentifier().Decompile(0).toLowerCase();
		}
		return nameId == null ? "" : nameId.Decompile(0).toLowerCase();
	}
	
	public boolean isUDF() {
		return isUDF;
	}
	
	@Override
	public String Decompile(int indent) {
		String s = nameId == null ? "" : nameId.Decompile(indent);
		s += "(";
		
		for (int i = 0; i < args.size(); i++) {
			s += args.elementAt(i).Decompile(indent);
			if (i < args.size() - 1) {
				s += ", ";
			}
		}
		
		s += ")";
		if (body != null) {
			s += body.Decompile(indent + 2);
		}
		return s;
	}
	
	public Vector<CFExpression> getArgs() {
		return args;
	}
	
	public String getName() {
		return nameId == null ? "" : nameId.Decompile(0);
	}
	
	public CFIdentifier getIdentifier() {
		return nameId;
	}
	
	public CFIdentifier getNameId() {
		return nameId;
	}
	
	public CFScriptStatement getBody() {
		return body;
	}
	
	public void setBody(CFScriptStatement body) {
		this.body = body;
	}
	
}
