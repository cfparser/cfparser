package cfml.parsing.utils;

import static org.junit.Assert.fail;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.gui.TreeViewer;
import org.junit.Assert;

import cfml.CFSCRIPTLexer;
import cfml.CFSCRIPTParser;
import cfml.CFSCRIPTParser.ScriptBlockContext;
import cfml.parsing.CFMLParser;
import cfml.parsing.cfscript.CFExpression;
import cfml.parsing.cfscript.CFFullVarExpression;
import cfml.parsing.cfscript.CFIdentifier;
import cfml.parsing.cfscript.CFMember;
import cfml.parsing.cfscript.CFUnaryExpression;
import cfml.parsing.cfscript.script.CFExpressionStatement;
import cfml.parsing.cfscript.script.CFScriptStatement;

public class TestUtils {
	public static String _pad280(String input) {
		return (input + "                                                                                                                  ")
				.substring(0, 112);
	}
	
	public static String pad280(String input) {
		BufferedReader r = new BufferedReader(new StringReader(input));
		StringBuilder output = new StringBuilder();
		String line = null;
		try {
			while ((line = r.readLine()) != null) {
				output.append(_pad280(line)).append("\r\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output.toString();
	}
	
	public static String pad280Free(String input) {
		BufferedReader r = new BufferedReader(new StringReader(input));
		StringBuilder output = new StringBuilder();
		String line = null;
		try {
			while ((line = r.readLine()) != null) {
				if (!line.startsWith("        ")) {
					line = "       " + line;
				}
				output.append(_pad280(line)).append("\r\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output.toString();
	}
	
	public static void showToks(List<CommonToken> tokenList) {
		for (CommonToken tok : tokenList) {
			System.out.println(tok);
		}
	}
	
	public static List<CommonToken> runX(String inputstr) {
		List<String> errors = new ArrayList<String>();
		List<CommonToken> retval = runX(inputstr, errors, false, false);
		if (errors.size() > 0) {
			throw new RuntimeException(errors.toString());
		}
		return retval;
	}
	
	public static List<CommonToken> runX(String inputstr, final List<String> errors) {
		return runX(inputstr, errors, false, false);
	}
	
	public static List<CommonToken> runXQuietly(String inputstr, final List<String> errors) {
		return runX(inputstr, errors, true, false);
	}
	
	public static List<CommonToken> runXGUI(String inputstr) {
		return runX(inputstr, null, true, true);
	}
	
	public static List<CommonToken> runX(String inputstr, final List<String> errors, final boolean quietly,
			final boolean gui) {
		ANTLRInputStream input = new ANTLRInputStream(inputstr);
		CFSCRIPTLexer lexer = new CFSCRIPTLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		final CFSCRIPTParser parser = new CFSCRIPTParser(tokens);
		
		if (errors != null) {
			parser.addErrorListener(new ANTLRErrorListener() {
				
				public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
						int charPositionInLine, String msg, RecognitionException e) {
					errors.add("Line:" + line + " " + msg + parser.getDFAStrings());
					
				}
				
				public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact,
						BitSet ambigAlts, ATNConfigSet configs) {
					// TODO Auto-generated method stub
					
				}
				
				public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex,
						BitSet conflictingAlts, ATNConfigSet configs) {
					// TODO Auto-generated method stub
					
				}
				
				public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex,
						int prediction, ATNConfigSet configs) {
					// TODO Auto-generated method stub
					
				}
				
			});
		}
		CFSCRIPTParser.ScriptBlockContext entry = null;
		parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
		try {
			entry = parser.scriptBlock();
		} catch (Exception e) {
			tokens.reset(); // rewind input stream
			parser.reset();
			parser.getInterpreter().setPredictionMode(PredictionMode.LL);
			entry = parser.scriptBlock(); // STAGE 2
		}
		List<CommonToken> tokenList = new ArrayList<CommonToken>();
		
		for (int i = 0; i < entry.getChildCount(); i++) {
			doTokenList(entry.getChild(i), tokenList);
		}
		
		if (gui || (errors != null && errors.size() > 0 && !quietly)) {
			
			for (CommonToken tok : tokenList) {
				System.out.println(tok + " - " + (tok.getType() > 0 ? lexer.getRuleNames()[tok.getType() - 1] : ""));
			}
			String tree = entry.toStringTree(parser).replaceAll("\\\\r\\\\n", "\\r\\n\r\n");
			System.out.println(tree);
			
			if (tree.length() < 1000)
				showGUI(entry, parser);
		}
		return tokenList;
	}
	
	public static void showGUI(ScriptBlockContext tree, CFSCRIPTParser parser) {
		showGUI(tree, parser.getRuleNames());
	}
	
	public static void showGUI(ParseTree tree, String[] ruleNames) {
		// JFrame frame = new JFrame("Antlr AST");
		JDialog frame = new JDialog();
		JPanel panel = new JPanel();
		TreeViewer viewr = new TreeViewer(Arrays.asList(ruleNames), tree);
		viewr.setAutoscrolls(true);
		viewr.setScale(1.5);// scale a little
		
		JScrollPane viewport = new JScrollPane();
		viewport.setSize(100, 100);
		viewport.setViewportView(viewr);
		
		frame.add(viewport);
		frame.setSize(1100, 800);
		frame.setLocation(new Point(200, 100));
		frame.setModal(true);
		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
		
	}
	
	static void doTokenList(ParseTree parseTree, List<CommonToken> tokenList) {
		for (int i = 0; i < parseTree.getChildCount(); i++) {
			ParseTree payload = parseTree.getChild(i);
			
			if (payload.getPayload() instanceof CommonToken) {
				tokenList.add((CommonToken) payload.getPayload());
			} else {
				doTokenList(payload, tokenList);
			}
			
		}
	}
	
	public static String loadFile(String path) throws IOException {
		InputStream is = TestUtils.class.getResourceAsStream(path);
		byte[] b = new byte[is.available()];
		is.read(b);
		is.close();
		return new String(b);
	}
	
	public static String loadFileByPath(String path) throws IOException {
		return loadFile(new File(path));
	}
	
	public static String loadFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);
		byte[] b = new byte[is.available()];
		is.read(b);
		is.close();
		return new String(b);
	}
	
	public static void assertTokens(List<CommonToken> tokenList, String... tokens) {
		Iterator<CommonToken> toks = tokenList.iterator();
		String stok = null;
		for (String tok : tokens) {
			if (!toks.hasNext())
				return;
			if (!tok.trim().equals((stok = toks.next().getText()).trim())) {
				String message = toString(tokenList);
				System.out.println(message);
				Assert.assertEquals(message, tok.trim(), stok.trim());
			}
		}
	}
	
	public static String toString(List<CommonToken> tokenList) {
		final StringBuilder b = new StringBuilder();
		final Iterator<CommonToken> toks = tokenList.iterator();
		while (toks.hasNext()) {
			b.append(",\"").append(toks.next().getText().trim()).append("\"");
		}
		return b.toString().substring(1);
	}
	
	public static String toStringNodes(List<ParseTree> tokenList) {
		final StringBuilder b = new StringBuilder();
		final Iterator<ParseTree> toks = tokenList.iterator();
		while (toks.hasNext()) {
			b.append(",\"").append(toks.next().getText().trim()).append("\"");
		}
		return b.toString().substring(1);
	}
	
	public static List<ParseTree> getLeaves(ParseTree parseTree) {
		List<ParseTree> retval = new ArrayList<ParseTree>();
		for (int i = 0; i < parseTree.getChildCount(); i++) {
			if (parseTree.getChild(i).getChildCount() > 0) {
				retval.addAll(getLeaves(parseTree.getChild(i)));
			} else {
				retval.add((ParseTree) parseTree.getChild(i));
			}
		}
		return retval;
	}
	
	public static List<String> getLeavesStrings(ParseTree parseTree) {
		List<String> retval = new ArrayList<String>();
		for (ParseTree node : getLeaves(parseTree)) {
			retval.add(node.getText());
		}
		return retval;
	}
	
	public static void assertTreeNodes(List<ParseTree> nodeList, String... tokens) {
		Iterator<ParseTree> toks = nodeList.iterator();
		String stok = null;
		for (String tok : tokens) {
			if (!toks.hasNext())
				return;
			if (!tok.trim().equals((stok = toks.next().getText()).trim())) {
				String message = toStringNodes(nodeList);
				System.out.println(message);
				Assert.assertEquals(message, tok.trim(), stok.trim());
			}
		}
	}
	
	public static CFScriptStatement parseScript(String script) {
		CFScriptStatement scriptStatement = null;
		CFMLParser fCfmlParser = new CFMLParser();
		try {
			scriptStatement = fCfmlParser.parseScript(script);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail(e.getMessage());
		}
		return scriptStatement;
	}
	
	public static CFExpression parseExpression(String script) {
		CFExpression scriptStatement = null;
		CFMLParser fCfmlParser = new CFMLParser();
		try {
			scriptStatement = fCfmlParser.parseCFExpression(script, new ANTLRErrorListener() {
				
				@Override
				public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
						int charPositionInLine, String msg, RecognitionException e) {
					System.out.println("se");
					
				}
				
				@Override
				public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex,
						int prediction, ATNConfigSet configs) {
					System.out.println("cs");
					
				}
				
				@Override
				public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex,
						BitSet conflictingAlts, ATNConfigSet configs) {
					// TODO Auto-generated method stub
					System.out.println("fc" + conflictingAlts.size());
					
				}
				
				@Override
				public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact,
						BitSet ambigAlts, ATNConfigSet configs) {
					// TODO Auto-generated method stub
					System.out.println("amb");
					
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail(e.getMessage());
		}
		return scriptStatement;
	}
	
	public static void printCFScriptTree(CFScriptStatement scriptStatement) {
		System.out.println(scriptStatement.getClass());
		if (scriptStatement instanceof CFExpressionStatement) {
			printCFExpressionTree(((CFExpressionStatement) scriptStatement).getExpression());
		}
	}
	
	public static void printCFExpressionTree(CFExpression expression, String... indent) {
		final String prefix = indent.length > 0 ? indent[0] : "";
		if (expression instanceof CFFullVarExpression) {
			System.out.println(prefix + expression.getClass().getSimpleName());
			for (CFExpression subexpression : ((CFFullVarExpression) expression).getExpressions()) {
				printCFExpressionTree(subexpression, prefix + "  ");
			}
			return;
		} else if (expression instanceof CFIdentifier || expression instanceof CFMember) {
			System.out.print(prefix + expression.Decompile(0));
		} else if (expression instanceof CFUnaryExpression) {
			CFUnaryExpression unaryExpression = (CFUnaryExpression) expression;
			System.out.println(prefix + expression.getClass().getSimpleName());
			printCFExpressionTree(unaryExpression.getSub(), prefix + "  ");
		} else {
			System.out.print(prefix + expression.getClass().getSimpleName());
		}
		System.out.println(expression.getClass().getSimpleName());
	}
}
