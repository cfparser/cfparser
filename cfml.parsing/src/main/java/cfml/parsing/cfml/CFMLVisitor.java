package cfml.parsing.cfml;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

import cfml.parsing.cfscript.CFExpression;
import cfml.parsing.cfscript.script.CFScriptStatement;
import net.htmlparser.jericho.Element;

public abstract class CFMLVisitor implements ANTLRErrorListener {
	
	List<ANTLRErrorListener> errorListeners = new ArrayList<ANTLRErrorListener>();
	
	public void addErrorListener(ANTLRErrorListener errorListener) {
		errorListeners.add(errorListener);
	}
	
	@Override
	public void reportAmbiguity(Parser arg0, DFA arg1, int arg2, int arg3, boolean arg4, BitSet arg5, ATNConfigSet arg6) {
		for (ANTLRErrorListener err : errorListeners) {
			err.reportAmbiguity(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
		}
	}
	
	@Override
	public void reportAttemptingFullContext(Parser arg0, DFA arg1, int arg2, int arg3, BitSet arg4, ATNConfigSet arg5) {
		for (ANTLRErrorListener err : errorListeners) {
			err.reportAttemptingFullContext(arg0, arg1, arg2, arg3, arg4, arg5);
		}
	}
	
	@Override
	public void reportContextSensitivity(Parser arg0, DFA arg1, int arg2, int arg3, int arg4, ATNConfigSet arg5) {
		for (ANTLRErrorListener err : errorListeners) {
			err.reportContextSensitivity(arg0, arg1, arg2, arg3, arg4, arg5);
		}
		
	}
	
	@Override
	public void syntaxError(Recognizer<?, ?> arg0, Object arg1, int arg2, int arg3, String arg4, RecognitionException arg5) {
		for (ANTLRErrorListener err : errorListeners) {
			err.syntaxError(arg0, arg1, arg2, arg3, arg4, arg5);
		}
		
	}
	
	public abstract void visitElementStart(Element elem);
	
	public abstract void visitElementEnd(Element elem);
	
	public abstract void visitExpression(String context, CFExpression expression);
	
	public abstract void visitScript(CFScriptStatement scriptStatement);
	
	public boolean visitPreParseExpression(String context, String cfscript) {
		return true;
	}
	
}
