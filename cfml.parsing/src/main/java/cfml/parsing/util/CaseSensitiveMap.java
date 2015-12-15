package cfml.parsing.util;

public interface CaseSensitiveMap<K, V> extends java.util.Map<String, V> {
	
	public boolean isCaseSensitive();
	
}
