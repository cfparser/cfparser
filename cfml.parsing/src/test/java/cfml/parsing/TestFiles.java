package cfml.parsing;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import cfml.CFSCRIPTLexer;
import cfml.CFSCRIPTParser;
import cfml.parsing.util.TreeUtils;
import cfml.parsing.utils.TestUtils;

/**
 * Run a test over each *.rpgle file in src/test/resources/org/rpgleparser/tests
 * 
 * @author ryaneberly
 * 		
 */
@RunWith(Parameterized.class)
public class TestFiles {
	
	File sourceFile;
	boolean autoReplaceFailed = false;
	static String singleTestName = null;
	
	static {
		try {
			singleTestName = ResourceBundle.getBundle("cfml.test").getString("RunSingleTest");
		} catch (Exception e) {
		}
	}
	
	public TestFiles(File sourceFile) {
		super();
		this.sourceFile = sourceFile;
		try {
			autoReplaceFailed = "Y"
					.equalsIgnoreCase(ResourceBundle.getBundle("cfml.test").getString("AutoReplaceFailedTestResults"));
		} catch (Exception e) {
		}
	}
	
	@Test
	public void test() throws IOException, URISyntaxException {
		final String inputString = TestUtils.loadFile(sourceFile);
		final File expectedFile = new File(
				sourceFile.getPath().replaceAll("\\.cfc", ".expected.txt").replaceAll("\\.cfm", ".expected.txt"));
		final String expectedFileText = expectedFile.exists() ? TestUtils.loadFile(expectedFile) : null;
		final String expectedTokens = getTokens(expectedFileText);
		String expectedTree = getTree(expectedFileText);
		final List<String> errors = new ArrayList<String>();
		final ANTLRInputStream input = new ANTLRInputStream(inputString);
		final CFSCRIPTLexer lexer = new CFSCRIPTLexer(input);
		
		final String actualTokens = printTokens(lexer);
		
		if (expectedTokens != null && expectedTokens.trim().length() > 0) {
			if (autoReplaceFailed && !expectedTokens.equals(actualTokens)) {
				expectedTree = "";
			} else {
				assertEquals("Token lists do not match", expectedTokens, actualTokens);
			}
		}
		lexer.reset();
		
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		final CFSCRIPTParser parser = new CFSCRIPTParser(tokens);
		
		CFSCRIPTParser.ScriptBlockContext parseTree = null;
		parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
		try {
			parseTree = parser.scriptBlock();
		} catch (Exception e) {
			tokens.reset(); // rewind input stream
			parser.reset();
			parser.getInterpreter().setPredictionMode(PredictionMode.LL);
			parseTree = parser.scriptBlock(); // STAGE 2
		}
		
		final String actualTree = TreeUtils.printTree(parseTree, parser);
		if (!errors.isEmpty()) {
			System.out.println("/*===TOKENS===*/\r\n" + actualTokens + "\r\n");
			System.out.println("/*===TOKENS===*/\r\n" + actualTree + "\r\n/*======*/");
		}
		// assertThat(errors, is(empty()));
		
		if (expectedTree == null || expectedTree.trim().length() == 0) {
			writeExpectFile(expectedFile, actualTokens, actualTree);
			System.out.println("Tree written to " + expectedFile);
		} else {
			if (autoReplaceFailed && !actualTree.equals(expectedTree)) {
				System.out.println("Replaced content of " + expectedFile);
				expectedTree = actualTree;
				writeExpectFile(expectedFile, actualTokens, actualTree);
			}
			assertEquals("Parse trees do not match", expectedTree, actualTree);
		}
	}
	
	private void writeExpectFile(File expectedFile, String actualTokens, String actualTree) throws IOException {
		final FileOutputStream fos = new FileOutputStream(expectedFile, false);
		fos.write("/*===TOKENS===*/\r\n".getBytes());
		fos.write(actualTokens.getBytes());
		fos.write("\r\n/*===TREE===*/\r\n".getBytes());
		fos.write(actualTree.getBytes());
		fos.write("\r\n/*======*/".getBytes());
		fos.close();
		
	}
	
	public static String printTokens(CFSCRIPTLexer lexer) {
		return TestUtils.getTokenString(lexer.getAllTokens(), lexer.getVocabulary());
	}
	
	private String getTokens(String expectedFileText) {
		if (expectedFileText != null && expectedFileText.contains("/*===TOKENS===*/")) {
			int startIdx = expectedFileText.indexOf("/*===TOKENS===*/") + 16;
			while (expectedFileText.charAt(startIdx) == '\r' || expectedFileText.charAt(startIdx) == '\n') {
				startIdx++;
			}
			int endIndex = expectedFileText.indexOf("/*===", startIdx);
			if (endIndex > startIdx) {
				while (expectedFileText.charAt(endIndex - 1) == '\r' || expectedFileText.charAt(endIndex - 1) == '\n') {
					endIndex--;
				}
				return expectedFileText.substring(startIdx, endIndex);
			}
		}
		return null;
	}
	
	private String getTree(String expectedFileText) {
		if (expectedFileText != null && expectedFileText.contains("/*===TREE===*/")) {
			int startIdx = expectedFileText.indexOf("/*===TREE===*/") + 14;
			while (expectedFileText.charAt(startIdx) == '\r' || expectedFileText.charAt(startIdx) == '\n') {
				startIdx++;
			}
			int endIndex = expectedFileText.indexOf("/*======*/", startIdx);
			if (endIndex > startIdx) {
				while (expectedFileText.charAt(endIndex - 1) == '\r' || expectedFileText.charAt(endIndex - 1) == '\n') {
					endIndex--;
				}
				return expectedFileText.substring(startIdx, endIndex);
			}
		}
		if (expectedFileText != null && expectedFileText.contains("/*===")) {
			return null;
		}
		return expectedFileText;
	}
	
	@Parameterized.Parameters(name = "{index}{0}")
	public static Collection<Object[]> primeNumbers() throws URISyntaxException, IOException {
		final ArrayList<Object[]> retval = new ArrayList<Object[]>();
		final List<File> listing = new ArrayList<File>();
		fillResourceListing(new File("src/test/resources/cfml/tests"), listing);
		for (File s : listing) {
			retval.add(new Object[] { s });
		}
		return retval;
	}
	
	private static void fillResourceListing(File file, List<File> retval) {
		if (file != null) {
			if (file.isDirectory()) {
				for (File subfile : file.listFiles()) {
					fillResourceListing(subfile, retval);
				}
			} else if (file.getName().toLowerCase().endsWith(".cfc") || file.getName().toLowerCase().endsWith(".cfm")) {
				if (singleTestName == null || singleTestName.equals(file.getName())) {
					retval.add(file);
				} else if (singleTestName.equals("*LAST")) {
					if (retval.size() == 0 || file.lastModified() > retval.get(0).lastModified()) {
						retval.clear();
						retval.add(file);
					}
				}
			}
		}
	}
}
