package cfml.parsing.util;

import java.util.ArrayList;
import java.util.List;

import cfml.parsing.cfscript.CFExpression;
import cfml.parsing.cfscript.script.CFScriptStatement;

public class ArrayBuilder {
	
	public static <E extends CFExpression> List<CFExpression> createCFExpression(@SuppressWarnings("unchecked") final E... args) {
		final ArrayList<CFExpression> retval = new ArrayList<CFExpression>();
		for (final E e : args) {
			if (e != null) {
				retval.add(e);
			}
		}
		return retval;
	}
	
	public static <E extends CFScriptStatement> List<CFScriptStatement> createCFScriptStatement(
			@SuppressWarnings("unchecked") final E... args) {
		final ArrayList<CFScriptStatement> retval = new ArrayList<CFScriptStatement>();
		for (final E e : args) {
			if (e != null) {
				retval.add(e);
			}
		}
		return retval;
	}
	
}
