package cfml.parsing.cfscript;

import org.antlr.runtime.BitSet;
import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.IntStream;
import org.antlr.v4.runtime.RecognitionException;

public interface IErrorReporter extends ANTLRErrorListener {
	void reportError(String error);
	
	void reportError(RecognitionException re);
	
	void reportError(String[] tokenNames, RecognitionException e);
	
	void reportError(IntStream input, RecognitionException re, BitSet follow);
}
