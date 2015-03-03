package cfml.parsing.util;

import org.antlr.v4.runtime.ParserRuleContext;

public class ExpressionUtils {
	
	// return first not null
	public static ParserRuleContext coalesce(ParserRuleContext... expressions) {
		for (int i = 0; i < expressions.length; i++) {
			if (expressions[i] != null) {
				return expressions[i];
			}
		}
		return null;
	}
}
