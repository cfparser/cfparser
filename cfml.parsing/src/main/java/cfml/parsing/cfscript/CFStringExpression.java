package cfml.parsing.cfscript;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Token;

public class CFStringExpression extends CFExpression {
	
	List<CFExpression> subExpressions = new ArrayList<CFExpression>();
	
	public CFStringExpression(Token t) {
		super(t);
		
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 233826913909000678L;
	
	@Override
	public String Decompile(int indent) {
		StringBuilder sb = new StringBuilder();
		sb.append("'");
		for (CFExpression expression : subExpressions) {
			if (expression instanceof CFLiteral) {
				final String txt = expression.Decompile(0);
				if (txt != null) {
					sb.append(txt.substring(1, txt.length() - 1));
				}
			} else {
				sb.append("#");
				sb.append(expression.Decompile(0));
				sb.append("#");
			}
		}
		sb.append("'");
		return sb.toString();
	}
	
	public List<CFExpression> getSubExpressions() {
		return subExpressions;
	}
	
}
