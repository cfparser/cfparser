package cfml.parsing.cfscript;

import java.util.List;

public class CFUndefinedValue extends CFData implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static final CFUndefinedValue UNDEFINED = new CFUndefinedValue();
	
	private CFUndefinedValue() {
	}
	
	public String getString() {
		return "";
	}
	
	@Override
	public String toString() {
		return "[undefined]";
	}
	
	public CFData duplicate() {
		return this;
	}
	
	/**
	 * The following methods are not allowed to be invoked for static instances.
	 */
	public void setQueryTableData(List queryTableData, int queryColumn) {
		throw new UnsupportedOperationException("static instance");
	}
	
	public void setExpression(boolean exp) {
		throw new UnsupportedOperationException("static instance");
	}
	
	public void setJavaCast(byte cast) {
		throw new UnsupportedOperationException("static instance");
	}
	
	public void setReference(boolean b) {
		throw new UnsupportedOperationException("static instance");
	}
	
	protected void setImplicit(boolean implicit) {
		throw new UnsupportedOperationException("static instance");
	}
}
