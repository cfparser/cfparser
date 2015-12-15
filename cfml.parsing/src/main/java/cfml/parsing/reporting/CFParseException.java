package cfml.parsing.reporting;

import org.antlr.runtime.RecognitionException;

public class CFParseException extends RecognitionException {
	
	private RecognitionException rootException;
	private String message;
	
	public CFParseException(String _msg, RecognitionException _rootError) {
		message = _msg;
		rootException = _rootError;
		this.line = _rootError.line;
		this.charPositionInLine = _rootError.charPositionInLine;
	}
	
	@Override
	public String getMessage() {
		return message;
	}
	
	public RecognitionException getSourceException() {
		return rootException;
	}
}
