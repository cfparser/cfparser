package cfml.parsing.util;

import java.io.Serializable;

import javolution.util.FastComparator;

/**
 * A subclass of javolution.util.FastMap that supports case-insensitive keys. Keys are always strings.
 * 
 * FastMap performs better than java.util.HashMap, and also gives us the ability to provide a key comparator, which is
 * how case-insensitivity is implemented.
 */
public class FastMap<K, V> extends javolution.util.FastMap<String, V>
		implements CaseSensitiveMap<String, V>, Serializable, Cloneable {
	static final long serialVersionUID = 1;
	
	public static final boolean CASE_SENSITIVE = true;
	
	public static final boolean CASE_INSENSITIVE = false;
	
	// use the FastMap default key comparator for case-sensitive comparisons
	public static FastComparator<String> stringComparatorIgnoreCase = new StringComparatorIgnoreCase();
	
	public FastMap() { // FastMaps are case-sensitive by default
	}
	
	public FastMap(FastMap<String, V> map) {
		setKeyComparator(map.getKeyComparator()); // must be done before
		// putAll()
		setShared(map.isShared());
		putAll(map);
	}
	
	public FastMap(java.util.Map<String, V> map) {
		putAll(map);
	}
	
	public FastMap(int initialCapacity) {
		super(initialCapacity);
	}
	
	// this constructor is not part of the standard java.util.Map interface
	public FastMap(boolean isCaseSensitive) {
		if (!isCaseSensitive) {
			setKeyComparator(stringComparatorIgnoreCase);
		}
	}
	
	@Override
	public Object clone() {
		return new FastMap<String, V>(this);
	}
	
	// this method is not part of the standard java.util.Map interface
	@Override
	public boolean isCaseSensitive() {
		return (getKeyComparator() != stringComparatorIgnoreCase);
	}
	
	// case-insensitive string comparator for use by FastMap
	private static class StringComparatorIgnoreCase extends FastComparator<String> {
		private static final long serialVersionUID = 1L;
		
		// the formal definition of this method says you're supposed to allow
		// nulls;
		// but we're dealing with keys here, which aren't allowed to be null, so
		// don't
		// bother checking for nulls
		@Override
		public boolean areEqual(String key1, String key2) {
			return key1.equalsIgnoreCase(key2);
		}
		
		@Override
		public int compare(String key1, String key2) {
			return key1.compareToIgnoreCase(key2);
		}
		
		@Override
		public int hashCodeOf(String key) {
			// toLowerCase() performs much better here than toUpperCase()
			return key.toLowerCase().hashCode();
		}
	}
}
