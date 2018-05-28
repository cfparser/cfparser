package cfml.parsing.cfscript.script;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.Token;

import cfml.parsing.cfscript.CFExpression;
import cfml.parsing.cfscript.CFIdentifier;
import cfml.parsing.cfscript.HasToken;

public class UserDefinedFunction implements java.io.Serializable, HasToken {
	private static final long serialVersionUID = 1;
	
	public static final int ACCESS_PRIVATE = 0;
	public static final int ACCESS_PACKAGE = 1;
	public static final int ACCESS_PUBLIC = 2;
	public static final int ACCESS_REMOTE = 3;
	
	protected CFIdentifier name;
	protected int access = -1;
	private String returnType;
	
	private Map<CFExpression, CFExpression> attributes; // the function attributes
	
	// the following attribute is only used for CFFUNCTION-based UDFs
	
	// the following two attributes are only used for CFSCRIPT-based UDFs
	private CFScriptStatement body;
	protected List<CFFunctionParameter> formals; // a list of argument names in Strings
	
	// for UDFs within a CFC; needs to be per-CFC instance
	
	// for cfScript:Java
	protected JavaBlock javaBlock;
	protected Method javaMethod;
	
	// for creating CFSCRIPT-based UDFs
	public UserDefinedFunction(CFIdentifier _name, byte _access, String _returnType, List<CFFunctionParameter> _formals,
			Map<CFExpression, CFExpression> attributes2, CFScriptStatement _body) {
		name = _name;
		access = _access;
		formals = _formals;
		attributes = attributes2;
		body = _body;
		returnType = _returnType;
	}
	
	public UserDefinedFunction(Method method, JavaBlock javaBlock) {
		name = new CFIdentifier(null, method.getName());
		this.javaBlock = javaBlock;
		this.javaMethod = method;
		access = ACCESS_PUBLIC;
		formals = new ArrayList<CFFunctionParameter>();
	}
	
	public String getTypeString() {
		return "UDF";
	}
	
	public List<CFFunctionParameter> getUDFFormals() {
		return formals;
	}
	
	public CFIdentifier getName() {
		return name;
	}
	
	public String getString() {
		return name.Decompile(0);
	}
	
	public boolean isJavaBlock() {
		return (javaBlock != null);
	}
	
	public JavaBlock getJavaBlock() {
		return javaBlock;
	}
	
	public void setJavaBlock(JavaBlock jb) {
		javaBlock = jb;
	}
	
	public String getReturnType() {
		return returnType;
	}
	
	public void dump(PrintWriter out) {
	}
	
	public int getAccess() {
		return access;
	}
	
	public Map<CFExpression, CFExpression> getAttributes() {
		return attributes;
	}
	
	public CFScriptStatement getBody() {
		return body;
	}
	
	public List<CFFunctionParameter> getFormals() {
		return formals;
	}
	
	public Method getJavaMethod() {
		return javaMethod;
	}
	
	@Override
	public String toString() {
		StringWriter w = new StringWriter();
		this.dump(new PrintWriter(w));
		return w.toString();
	}
	
	@Override
	public Token getToken() {
		return name == null ? null : name.getToken();
	}
	
}
