package cfml.dictionary;

import static org.junit.Assert.assertNotNull;

import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import cfml.dictionary.preferences.DictionaryPreferenceConstants;
import cfml.dictionary.preferences.DictionaryPreferences;

public class TestDictionaryManager {
	
	DictionaryPreferences fPrefs;
	
	@Before
	public void setUp() throws Exception {
		fPrefs = new DictionaryPreferences();
	}
	
	@Test
	public void testGetConfiguredDictionaries() {
		DictionaryManager.initDictionaries();
		String[][] fun = DictionaryManager.getConfiguredDictionaries();
		assertNotNull(fun);
	}
	
	@Test
	public void testGetDictionary() {
		DictionaryManager.initDictionaries();
		SyntaxDictionary fun = DictionaryManager.getDictionary(DictionaryPreferenceConstants.CFDIC_KEY);
		System.err.println(fun.dictionaryURL);
		Set wee = fun.getAllTags();
		assertNotNull(fun);
	}
	
	@Ignore
	@Test
	public void testGetDictionaryByVersion() {
		DictionaryManager.initDictionaries();
		SyntaxDictionary fun = DictionaryManager.getDictionaryByVersion(fPrefs.getCFDictionary());
		System.err.println(fun.dictionaryURL);
		Set wee = fun.getAllTags();
		assertNotNull(fun);
	}
	
	@Test
	public void testExternalDictionaryLocation() {
		DictionaryPreferences dprefs = new DictionaryPreferences();
		dprefs.setDictionaryDir("src/test/resources/dictionary");
		dprefs.setCFDictionary("awesomedic");
		DictionaryManager.initDictionaries(dprefs);
		String[][] fun = DictionaryManager.getConfiguredDictionaries();
		assertNotNull(fun);
	}
	
	@Test
	public void testGetDicionaryByURL() {
		DictionaryPreferences dprefs = new DictionaryPreferences();
		dprefs.setDictionaryDir("src/test/resources/dictionary");
		dprefs.setCFDictionary("awesomedic");
		DictionaryManager.initDictionaries(dprefs);
		String[][] fun = DictionaryManager.getConfiguredDictionaries();
		assertNotNull(fun);
	}
	
}
