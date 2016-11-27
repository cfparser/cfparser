package cfml.parsing.cfscript.script;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.antlr.v4.runtime.Token;

import cfml.parsing.cfscript.CFContext;
import cfml.parsing.cfscript.CFExpression;
import cfml.parsing.cfscript.CFIdentifier;
import cfml.parsing.reporting.ParseException;

public class CFFuncDeclStatement extends CFParsedStatement {
	
	private static final long serialVersionUID = 1L;
	
	private CFIdentifier name;
	private List<CFFunctionParameter> formals; // Vector of String's
	private Map<CFExpression, CFExpression> attributes;
	private CFScriptStatement body;
	
	private byte access;
	private CFIdentifier returnType;
	
	// TODO: prevent function declared inside function. May want to do this elsewhere
	public CFFuncDeclStatement(Token _t, CFIdentifier _name, String _access, CFIdentifier _returnType,
			List<CFFunctionParameter> _formals, Map<CFExpression, CFExpression> _attr, CFScriptStatement _body) {
		super(_t);
		name = _name;
		formals = _formals;
		body = _body;
		returnType = _returnType;
		
		access = UserDefinedFunction.ACCESS_PUBLIC;
		if (_access != null) {
			String accessStr = _access.toUpperCase();
			if (accessStr.equals("PUBLIC")) {
				access = UserDefinedFunction.ACCESS_PUBLIC;
			} else if (accessStr.equals("PRIVATE")) {
				access = UserDefinedFunction.ACCESS_PRIVATE;
			} else if (accessStr.equals("REMOTE")) {
				access = UserDefinedFunction.ACCESS_REMOTE;
			} else if (accessStr.equals("PACKAGE")) {
				access = UserDefinedFunction.ACCESS_PACKAGE;
			} else {
				throw new ParseException(token, "Invalid access type name \"" + _access + "\" is not supported.");
			}
		}
		
		// handle the function attributes
		attributes = _attr;
		
	}
	
	public CFScriptStatement getBody() {
		return body;
	}
	
	@Override
	public void checkIndirectAssignments(String[] scriptSource) {
		body.checkIndirectAssignments(scriptSource);
	}
	
	public UserDefinedFunction getUDF() {
		return new UserDefinedFunction(name, access, returnType.Decompile(0), formals, attributes, body);
	}
	
	public CFStatementResult Exec(CFContext context) {
		return null;
	}
	
	@Override
	public String Decompile(int indent) {
		StringBuilder sb = new StringBuilder();
		sb.append(Indent(indent));
		if (name != null) {
			switch (access) {
			case UserDefinedFunction.ACCESS_PUBLIC:
				sb.append("public");
				break;
			case UserDefinedFunction.ACCESS_PRIVATE:
				sb.append("private");
				break;
			case UserDefinedFunction.ACCESS_REMOTE:
				sb.append("remote");
				break;
			case UserDefinedFunction.ACCESS_PACKAGE:
				sb.append("package");
				break;
			}
		}
		
		if (returnType != null) {
			sb.append(" ");
			sb.append(returnType.Decompile(indent));
		}
		
		sb.append(" function ");
		
		if (name != null)
			sb.append(name.Decompile(0));
		sb.append("(");
		for (int i = 0; i < formals.size(); i++) {
			sb.append(formals.get(i));
			if (i != formals.size() - 1) {
				sb.append(", ");
			}
		}
		sb.append(") ");
		Iterator<Entry<CFExpression, CFExpression>> attribs = attributes.entrySet().iterator();
		while (attribs.hasNext()) {
			Entry<CFExpression, CFExpression> attrib = attribs.next();
			sb.append(attrib.getKey().Decompile(0).replace('.', ':'));
			if (attrib.getValue() != null)
				sb.append("=" + attrib.getValue().Decompile(0));
			if (attribs.hasNext()) {
				sb.append(" ");
			}
		}
		sb.append(body.Decompile(indent + 2));
		return sb.toString();
	}
	
	public List<CFFunctionParameter> getFormals() {
		return formals;
	}
	
	public CFIdentifier getName() {
		return name;
	}
	
	public Map<CFExpression, CFExpression> getAttributes() {
		return attributes;
	}
	
	public byte getAccess() {
		return access;
	}
	
	public CFIdentifier getReturnType() {
		return returnType;
	}
}
