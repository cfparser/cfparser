package cfml.parsing.cfscript;

import java.util.ArrayList;

import org.antlr.v4.runtime.Token;

public class CFStructExpression extends CFExpression implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private ArrayList elements;
	
	public CFStructExpression(Token t) {
		super(t);
		elements = new ArrayList();
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
	
	public ArrayList getElements() {
		return elements;
	}
	
}
