package cfml.parsing.reporting;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

public class ArrayErrorListener implements ANTLRErrorListener {
	
	List<String> errors = new ArrayList<String>();
	
	public ArrayErrorListener(List<String> errors) {
		this.errors = errors;
	}
	
	@Override
	public void syntaxError(Recognizer<?, ?> recognizer, Object token, int line, int offset, String message,
			RecognitionException re) {
		errors.add("SyntaxError: Line" + line + ":" + offset + " " + message);
	}
	
	@Override
	public void reportContextSensitivity(Parser arg0, DFA arg1, int arg2, int arg3, int arg4, ATNConfigSet arg5) {
		errors.add("contextSensitivity");
	}
	
	@Override
	public void reportAttemptingFullContext(Parser arg0, DFA arg1, int arg2, int arg3, BitSet arg4, ATNConfigSet arg5) {
		errors.add("attemptingFullContext");
	}
	
	@Override
	public void reportAmbiguity(Parser arg0, DFA arg1, int arg2, int arg3, boolean arg4, BitSet arg5, ATNConfigSet arg6) {
		errors.add("reportAmbiguity");
	}
}
