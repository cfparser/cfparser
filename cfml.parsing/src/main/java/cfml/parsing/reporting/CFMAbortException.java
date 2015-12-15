package cfml.parsing.reporting;

public class CFMAbortException extends RuntimeException {
	private static final long serialVersionUID = 1;
	
	private String output;
	private boolean bFlushOutput;
	
	public CFMAbortException() {
		this(false);
	}
	
	public CFMAbortException(boolean _flushOutput) {
		bFlushOutput = _flushOutput;
	}
	
	public String getOutput() {
		return (output == null ? "" : output);
	}
	
	public boolean flushOutput() {
		return bFlushOutput;
	}
	
	// only save the output from the first caller to this method
	public void setOutput(String _output) {
		if (output == null) {
			output = _output;
		}
	}
	
	/**
	 * Overrides java.lang.Throwable. We don't care about Java stack traces for CFML runtime exceptions, so save the
	 * effort of filling it in.
	 */
	@Override
	public Throwable fillInStackTrace() {
		return this;
	}
}
