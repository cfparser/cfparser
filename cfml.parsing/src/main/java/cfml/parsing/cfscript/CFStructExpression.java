package cfml.parsing.cfscript;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Token;

import cfml.parsing.cfscript.script.CFScriptStatement;
import cfml.parsing.util.ArrayBuilder;

public class CFStructExpression extends CFExpression implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private ArrayList<CFExpression> elements;
	
	public CFStructExpression(Token t) {
		super(t);
		elements = new ArrayList<CFExpression>();
	}
	
	public void addElement(CFStructElementExpression _element) {
		elements.add(_element);
	}
	
	@Override
	public String Decompile(int indent) {
		StringBuilder sb = new StringBuilder();
		sb.append('{');
		for (int i = 0; i < elements.size(); i++) {
			sb.append(((CFStructElementExpression) elements.get(i)).toString());
			sb.append(',');
		}
		
		if (elements.size() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		
		sb.append('}');
		return sb.toString();
	}
	
	public ArrayList<CFExpression> getElements() {
		return elements;
	}
	
	@Override
	public List<CFExpression> decomposeExpression() {
		return elements;
	}
	
	@Override
	public List<CFScriptStatement> decomposeScript() {
		return ArrayBuilder.createCFScriptStatement();
	}
}
