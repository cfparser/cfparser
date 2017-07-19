package cfml.parsing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Stack;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import cfml.CFSCRIPTLexer;
import cfml.CFSCRIPTParser;
import cfml.CFSCRIPTParser.ExpressionContext;
import cfml.parsing.cfml.CFMLVisitor;
import cfml.parsing.cfscript.CFExpression;
import cfml.parsing.cfscript.script.CFScriptStatement;
import cfml.parsing.cfscript.walker.CFScriptStatementVisitor;
import cfml.parsing.reporting.ArrayErrorListener;
import cfml.parsing.util.TreeUtils;
import cfml.parsing.utils.TestUtils;
import net.htmlparser.jericho.Element;

/**
 * Run a test over each *.cfm file in /cfml.parsing/src/test/resources/tag/tests
 * 
 * @author ryaneberly
 * 
 */
@RunWith(Parameterized.class)
public class TestTagFiles {
	
	File sourceFile;
	boolean autoReplaceFailed = false;
	static String singleTestName = null;
	
	static {
		try {
			singleTestName = ResourceBundle.getBundle("cfml.test").getString("RunSingleTest");
		} catch (Exception e) {
		}
	}
	
	public TestTagFiles(File sourceFile) {
		super();
		this.sourceFile = sourceFile;
		try {
			autoReplaceFailed = "Y"
					.equalsIgnoreCase(ResourceBundle.getBundle("cfml.test").getString("AutoReplaceFailedTestResults"));
		} catch (Exception e) {
		}
	}
	
	@Test
	public void test() throws Exception {
		final String inputString = TestUtils.loadFile(sourceFile);
		final File expectedFile = new File(
				sourceFile.getPath().replaceAll("\\.cfc", ".expected.txt").replaceAll("\\.cfm", ".expected.txt"));
		final String expectedFileText = expectedFile.exists() ? TestUtils.loadFile(expectedFile) : null;
		final CFMLSource cfmlSource = new CFMLSource(inputString);
		final List<Element> elements = cfmlSource.getChildElements();
		
		StringBuilder actual = new StringBuilder();
		if (elements.isEmpty()) {
			// Did not parse
		} else {
			CFMLParser p = new CFMLParser();
			TestTagVisitor visitor = new TestTagVisitor();
			for (Element child : elements) {
				p.visit(child, 0, visitor);
			}
			actual.append(visitor.toString());
		}
		for (Element elem1 : cfmlSource.getAllElements()) {
			elem1.getAttributes();
			elem1.getAllElements();
			elem1.getAllStartTags();
		}
		if (expectedFileText == null) {
			writeExpectFile(expectedFile, actual.toString().replace("\r", ""));
		} else {
			Assert.assertEquals(expectedFileText.replace("\r", ""), actual.toString().replace("\r", ""));
		}
		// Assert.assertEquals(cfmlSource.getMessages().toString(), 0, cfmlSource.getMessages().size());
	}
	
	static final class TestTagVisitor extends CFMLVisitor {
		
		StringBuilder output = new StringBuilder();
		Stack<String> depthStack = new Stack<String>();
		
		@Override
		public void visitElementStart(Element elem) {
			output.append((depthStack.isEmpty() ? "" : depthStack.peek()) + "START:" + elem.getName() + "\n");
			if (depthStack.isEmpty()) {
				depthStack.push("  ");
			} else {
				depthStack.push(depthStack.peek() + "  ");
			}
			output.append(depthStack.peek() + "=========TAG=================\n");
			output.append(depthStack.peek() + elem + "\n");
			
		}
		
		@Override
		public void visitElementEnd(Element elem) {
			depthStack.pop();
			output.append((depthStack.isEmpty() ? "" : depthStack.peek()) + "END:" + elem.getName() + "\n");
		}
		
		@Override
		public void visitExpression(String context, CFExpression expression) {
			// output.append(depthStack.peek() + expression.Decompile(0) + "\n");
		}
		
		@Override
		public void visitScript(CFScriptStatement scriptStatement) {
			output.append(depthStack.peek() + "!!!" + scriptStatement.Decompile(0) + "\n");
		}
		
		public String toString() {
			return output.toString();
		}
		
		@Override
		public boolean visitPreParseExpression(String context, String cfscript) {
			try {
				ParseInfo info = parseExpression(cfscript);
				output.append(depthStack.peek() + context + "\n");
				if (!info.errors.isEmpty()) {
					output.append(depthStack.peek() + "=========ERRORs=================\n");
					output.append(depthStack.peek() + info.errors + "\n");
				}
				output.append(depthStack.peek() + "==========Tokens==============\n");
				output.append(depthStack.peek() + info.actualTokens.replace("\n", "\n" + depthStack.peek()) + "\n");
				output.append(depthStack.peek() + "===========Tree================\n");
				output.append(depthStack.peek() + info.actualTree.replace("\n", "\n" + depthStack.peek()) + "\n");
				output.append(depthStack.peek() + "===========Expression===============\n");
				output.append(
						depthStack.peek()
								+ (info.result == null ? "null"
										: info.result.Decompile(depthStack.size()).replace("\n", "\n" + depthStack.peek()))
								+ "\n");
				
			} catch (Exception e) {
				output.append(depthStack.peek() + "===========EXCEPTION===============\n");
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				output.append(depthStack.peek() + sw.toString().replace("\n", "\n" + depthStack.peek()) + "\n");
			}
			output.append(depthStack.peek() + "==================================\n");
			return false;
		}
		
	}
	
	static class ParseInfo {
		public final String actualTokens;
		public final String actualTree;
		public final CFScriptStatement result;
		public final List<String> errors;
		
		public ParseInfo(String actualTokens, String actualTree, CFScriptStatement result, List<String> errors) {
			super();
			this.actualTokens = actualTokens;
			this.actualTree = actualTree;
			this.result = result;
			this.errors = errors;
		}
	}
	
	public static final ParseInfo parse(final String inputString) throws IOException, URISyntaxException {
		final List<String> errors = new ArrayList<String>();
		final ANTLRInputStream input = new ANTLRInputStream(inputString);
		final CFSCRIPTLexer lexer = new CFSCRIPTLexer(input);
		
		final String actualTokens = printTokens(lexer);
		
		lexer.reset();
		
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		final CFSCRIPTParser parser = new CFSCRIPTParser(tokens);
		parser.addErrorListener(new ArrayErrorListener(errors));
		
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
		boolean iKnowThisIsAllGoodSoReplaceIt = false;
		CFScriptStatement result;
		CFScriptStatementVisitor scriptVisitor = new CFScriptStatementVisitor();
		result = scriptVisitor.visit(parseTree);
		// String blah = result == null ? "" : result.Decompile(0);
		return new ParseInfo(actualTokens, actualTree, result, errors);
	}
	
	public static final ParseInfo parseExpression(final String inputString) throws IOException, URISyntaxException {
		final List<String> errors = new ArrayList<String>();
		final ANTLRInputStream input = new ANTLRInputStream(inputString);
		final CFSCRIPTLexer lexer = new CFSCRIPTLexer(input);
		
		final String actualTokens = printTokens(lexer);
		
		lexer.reset();
		
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		final CFSCRIPTParser parser = new CFSCRIPTParser(tokens);
		parser.addErrorListener(new ArrayErrorListener(errors));
		
		ExpressionContext parseTree = null;
		parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
		try {
			parseTree = parser.expression();
		} catch (Exception e) {
			tokens.reset(); // rewind input stream
			parser.reset();
			parser.getInterpreter().setPredictionMode(PredictionMode.LL);
			parseTree = parser.expression(); // STAGE 2
		}
		final String actualTree = TreeUtils.printTree(parseTree, parser);
		boolean iKnowThisIsAllGoodSoReplaceIt = false;
		CFScriptStatement result;
		CFScriptStatementVisitor scriptVisitor = new CFScriptStatementVisitor();
		result = scriptVisitor.visit(parseTree);
		// String blah = result == null ? "" : result.Decompile(0);
		return new ParseInfo(actualTokens, actualTree, result, errors);
	}
	
	private void writeExpectFile(File expectedFile, String actualTokens) throws IOException {
		final FileOutputStream fos = new FileOutputStream(expectedFile, false);
		fos.write(actualTokens.getBytes());
		fos.close();
		
	}
	
	public static String printTokens(CFSCRIPTLexer lexer) {
		return TestUtils.getTokenString(lexer.getAllTokens(), lexer.getVocabulary());
	}
	
	@Parameterized.Parameters(name = "{index}{0}")
	public static Collection<Object[]> primeNumbers() throws URISyntaxException, IOException {
		final ArrayList<Object[]> retval = new ArrayList<Object[]>();
		final List<File> listing = new ArrayList<File>();
		fillResourceListing(new File("src/test/resources/tag/tests"), listing);
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
