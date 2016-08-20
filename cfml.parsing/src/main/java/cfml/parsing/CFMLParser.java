package cfml.parsing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.IntStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.dfa.DFA;

import cfml.CFSCRIPTLexer;
import cfml.CFSCRIPTParser;
import cfml.CFSCRIPTParser.ExpressionContext;
import cfml.CFSCRIPTParser.ScriptBlockContext;
import cfml.dictionary.DictionaryManager;
import cfml.dictionary.SyntaxDictionary;
import cfml.dictionary.preferences.DictionaryPreferences;
import cfml.parsing.cfscript.CFExpression;
import cfml.parsing.cfscript.script.CFScriptStatement;
import cfml.parsing.cfscript.walker.CFExpressionVisitor;
import cfml.parsing.cfscript.walker.CFScriptStatementVisitor;
import cfml.parsing.reporting.IErrorReporter;
import cfml.parsing.reporting.ParseException;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.StartTag;

public class CFMLParser {
	
	private Map fCfmlSources = new HashMap();
	protected ArrayList<ParseMessage> messages = new ArrayList<ParseMessage>();
	private boolean hadFatal;
	private int errCount = 0;
	/** Syntax dictionary for working out important things for the parser. */
	private SyntaxDictionary cfdic;
	private DictionaryPreferences fDictPrefs = new DictionaryPreferences();
	IErrorReporter errorReporter = new StdErrReporter();
	CFExpressionVisitor expressionVisitor = new CFExpressionVisitor();
	CFScriptStatementVisitor scriptVisitor = new CFScriptStatementVisitor();
	CFSCRIPTLexer lexer = null;
	CFSCRIPTParser parser = null;
	
	public void clearDFA() {
		if (parser != null)
			parser.getInterpreter().clearDFA();
		if (lexer != null)
			lexer.getInterpreter().clearDFA();
	}
	
	public CFExpression parseCFExpression(String _infix, ANTLRErrorListener errorReporter) throws Exception {
		if (errorReporter == null) {
			errorReporter = this.errorReporter;
		}
		
		final ANTLRInputStream input = new ANTLRInputStream(_infix);
		if (lexer == null) {
			lexer = new CFSCRIPTLexer(input);
			// lexer.setInterpreter(new LexerATNSimulator(lexer, lexer.getATN(), lexer.getInterpreter().decisionToDFA,
			// new PredictionContextCache()));
		} else {
			lexer.setInputStream(input);
		}
		
		final CommonTokenStream tokens = new CommonTokenStream(lexer);
		
		// ScriptBlockContext scriptStatement = null;
		if (parser == null) {
			parser = new CFSCRIPTParser(tokens);
			// parser.setInterpreter(new ParserATNSimulator(parser, parser.getATN(),
			// parser.getInterpreter().decisionToDFA, new PredictionContextCache()));
			// parser.getInterpreter().clearDFA();
			if (errorReporter == null) {
				lexer.addErrorListener(this.errorReporter);
				parser.addErrorListener(this.errorReporter);
			}
		} else {
			parser.setTokenStream(tokens);
		}
		
		if (errorReporter != null) {
			lexer.addErrorListener(errorReporter);
			parser.addErrorListener(errorReporter);
		}
		// p2.scriptMode = false;
		parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
		parser.reset();
		ExpressionContext expressionContext = null;
		try {
			expressionContext = parser.expression(); // Stage 1
			// TestUtils.showGUI(expressionContext, CFSCRIPTParser.ruleNames);
			
		} catch (Exception e) {
			tokens.reset(); // rewind input stream
			parser.reset();
			parser.getInterpreter().setPredictionMode(PredictionMode.LL);
			expressionContext = parser.expression(); // STAGE 2
		} finally {
			if (errorReporter != null) {
				lexer.removeErrorListener(errorReporter);
				parser.removeErrorListener(errorReporter);
			}
		}
		if (expressionContext != null) {
			return expressionVisitor.visit(expressionContext);
		} else
			return null;
			
	}
	
	private static String readFileAsString(String filePath) throws java.io.IOException {
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}
	
	public CFMLParser() {
		DictionaryManager.initDictionaries();
		System.out.println(fDictPrefs.getDictionaryDir());
		cfdic = DictionaryManager.getDictionaryByVersion(fDictPrefs.getCFDictionary());
		if (cfdic == null) {
			throw new IllegalArgumentException("The syntax dictionary could not be loaded!");
		}
	}
	
	public CFMLParser(String dictionariesPath, String dictionary) {
		fDictPrefs.setDictionaryDir(dictionariesPath);
		fDictPrefs.setCFDictionary(dictionary);
		DictionaryManager.initDictionaries(fDictPrefs);
		cfdic = DictionaryManager.getDictionaryByVersion(dictionary);
		if (cfdic == null) {
			throw new IllegalArgumentException("The syntax dictionary could not be loaded!");
		}
	}
	
	/**
	 * Initialises the dictionary
	 * 
	 * @param newDict
	 *            the dictionary to init with
	 */
	public void setDictionary(SyntaxDictionary newDict) {
		cfdic = newDict;
	}
	
	public void addDictionary(String dictionary) {
	}
	
	public CFMLSource addCFMLSource(String path, String cfmlsource) {
		CFMLSource source = new CFMLSource(cfmlsource);
		fCfmlSources.put(path, source);
		return source;
	}
	
	public CFMLSource addCFMLSource(File cfmlsource) throws IOException {
		return addCFMLSource(cfmlsource.getPath(), readFileAsString(cfmlsource.getPath()));
	}
	
	public CFMLSource addCFMLSource(URL url) throws IOException {
		CFMLSource source = new CFMLSource(url);
		fCfmlSources.put(url.getPath(), source);
		return source;
	}
	
	public ArrayList<StartTag> getCFMLTags() {
		ArrayList<StartTag> cfmlTags = new ArrayList<StartTag>();
		Iterator sources = fCfmlSources.keySet().iterator();
		while (sources.hasNext()) {
			cfmlTags.addAll(((CFMLSource) fCfmlSources.get(sources.next())).getAllCFMLTags());
		}
		return cfmlTags;
	}
	
	public ArrayList getAllTags() {
		ArrayList<Element> allTags = new ArrayList<Element>();
		Iterator sources = fCfmlSources.keySet().iterator();
		while (sources.hasNext()) {
			allTags.addAll(((CFMLSource) fCfmlSources.get(sources.next())).getAllElements());
		}
		return allTags;
	}
	
	public String getCacheDebugInfo() {
		String info = "";
		Iterator sources = fCfmlSources.keySet().iterator();
		while (sources.hasNext()) {
			info = info.concat((((CFMLSource) fCfmlSources.get(sources.next())).getCacheDebugInfo()));
		}
		return info;
	}
	
	public String getDebuggingInfo() {
		String info = "";
		Iterator sources = fCfmlSources.keySet().iterator();
		while (sources.hasNext()) {
			info = info.concat((((CFMLSource) fCfmlSources.get(sources.next())).getDebuggingInfo()));
		}
		return info;
	}
	
	/**
	 * Returns whether this parse has experienced a fatal error or not.
	 * 
	 * @return True - a fatal error has occured, false - lets go onwards!
	 */
	public boolean hadFatal() {
		return hadFatal;
	}
	
	/**
	 * Adds a message to the parser state.
	 * 
	 * @param newMsg
	 *            The message to report to the user post-parse.
	 */
	public void addMessage(ParseMessage newMsg) {
		if (newMsg instanceof ParseError) {
			if (((ParseError) newMsg).isFatal())
				hadFatal = true;
				
			errCount++;
		}
		
		messages.add(newMsg);
	}
	
	/**
	 * Adds a whole bunch of messages to the message list.
	 * 
	 * @param newMessages
	 *            ArrayList of ParseMessage's
	 */
	public void addMessages(ArrayList newMessages) {
		Iterator msgIter = newMessages.iterator();
		ParseMessage currMsg = null;
		while (msgIter.hasNext()) {
			currMsg = (ParseMessage) msgIter.next();
			if (currMsg instanceof ParseError) {
				if (((ParseError) currMsg).isFatal())
					hadFatal = true;
					
				errCount++;
			}
			messages.add(currMsg);
		}
	}
	
	public ArrayList getMessages() {
		return messages;
	}
	
	public String printMessages() {
		String messagesText = "";
		for (ParseMessage message : messages) {
			messagesText = messagesText + message.toString() + "\n";
		}
		return messagesText;
	}
	
	public void parseElements(CFMLSource cfmlSource) {
		for (Element element : cfmlSource.getAllElements()) {
			HashMap suggestedAttributes = new HashMap();
			String attributesFound = "";
			
			Set dictAttributes = cfdic.getElementAttributes(element.getName());
			
			if (dictAttributes == null) {
				continue;
			}
			
			Object[] params = dictAttributes.toArray();
			
			Map itemAttributes = new HashMap();
			element.getAttributes().populateMap(itemAttributes, true);
			
			if (itemAttributes.size() > 0) {
				attributesFound = " (Found: " + itemAttributes.toString() + ")";
			}
			int lineNumber = cfmlSource.getRow(element.getBegin());
			int startPosition = element.getBegin();
			int endPosition = element.getEnd();
			String name = element.getName();
			String itemData = element.getTextExtractor().toString();
			
			// TODO
			// for (int i = 0; i < params.length; i++) {
			// Parameter currParam = (Parameter) params[i];
			// if (currParam.isRequired() && !(itemAttributes.containsKey(currParam.getName().toLowerCase()))) {
			// addMessage(new ParseError(lineNumber, startPosition, endPosition, itemData, "The attribute \'"
			// + currParam.getName() + "\' is compulsory for the <" + name + "> tag." + attributesFound));
			// }
			//
			// if (!currParam.getTriggers().isEmpty() && currParam.isRequired(suggestedAttributes) == 3
			// && !itemAttributes.containsKey(currParam.getName())) {
			// addMessage(new ParseError(lineNumber, startPosition, endPosition, itemData, "The attribute \'"
			// + currParam.getName() + "\' is required for the <" + name + "> tag." + attributesFound));
			// } else if (!currParam.getTriggers().isEmpty() && currParam.isTriggered(suggestedAttributes) == 0
			// && itemAttributes.containsKey(currParam.getName())) {
			// addMessage(new ParseError(lineNumber, startPosition, endPosition, itemData, "The attribute \'"
			// + currParam.getName() + "\' is not valid for the <" + name + "> tag." + attributesFound));
			// }
			// }
		}
		
	}
	
	public class StdErrReporter implements IErrorReporter {
		@Override
		public void reportError(String error) {
			// System.err.println(error);
			addMessage(new ParseError(0, 0, 0, error, error));
		}
		
		@Override
		public void reportError(RecognitionException re) {
			if (re.getOffendingToken() != null) {
				// System.out.println("Token line:" + re.token.getLine());
				// System.out.println("Token text:" + re.token.getText());
			}
			// re.printStackTrace();
			// System.err.println(re.getMessage());
			addMessage(new ParseError(re.getOffendingToken().getLine(), re.getOffendingToken().getCharPositionInLine(),
					re.getOffendingToken().getCharPositionInLine(), re.getMessage(), re.getMessage()));
		}
		
		@Override
		public void reportError(String[] tokenNames, RecognitionException re) {
			// System.out.println("Token line:" + re.token.getLine());
			// System.out.println("Token text:" + re.token.getText());
			// System.err.println(tokenNames);
			// System.err.println(re.getMessage());
			addMessage(new ParseError(re.getOffendingToken().getLine(), re.getOffendingToken().getCharPositionInLine(),
					re.getOffendingToken().getCharPositionInLine(), tokenNames.toString(), re.getMessage()));
			// re.printStackTrace();
		}
		
		@Override
		public void reportError(IntStream input, RecognitionException re, org.antlr.runtime.BitSet follow) {
			// System.out.println("Token line:" + re.token.getLine());
			// System.out.println("Token text:" + re.token.getText());
			addMessage(new ParseError(re.getOffendingToken().getLine(), re.getOffendingToken().getCharPositionInLine(),
					re.getOffendingToken().getCharPositionInLine(), re.getMessage(), re.getMessage()));
			// System.err.println(re.getMessage());
		}
		
		@Override
		public void reportAmbiguity(Parser arg0, DFA arg1, int arg2, int arg3, boolean arg4, BitSet arg5, ATNConfigSet arg6) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void reportAttemptingFullContext(Parser arg0, DFA arg1, int arg2, int arg3, BitSet arg4, ATNConfigSet arg5) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void reportContextSensitivity(Parser arg0, DFA arg1, int arg2, int arg3, int arg4, ATNConfigSet arg5) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg,
				RecognitionException re) {
			addMessage(new ParseError(line, charPositionInLine, charPositionInLine, msg, re == null ? null : re.getMessage()));
			
		}
		
	}
	
	public CFScriptStatement parseScriptFile(String file) throws ParseException, IOException {
		return parseScript(readFileAsString(file));
	}
	
	public CFScriptStatement parseScript(String cfscript) throws ParseException, IOException {
		ScriptBlockContext scriptBlockContext = parseScriptBlockContext(cfscript);
		
		CFScriptStatement result = scriptVisitor.visit(scriptBlockContext);
		return result;
	}
	
	public ScriptBlockContext parseScriptBlockContext(String cfscript) throws ParseException, IOException {
		
		ANTLRInputStream input = new ANTLRInputStream(cfscript);
		CFSCRIPTLexer lexer = new CFSCRIPTLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		// int i = 0;
		// tokens.consume();
		// while (true) {
		// System.out.println("-> " + CFSCRIPTLexer.tokenNames[tokens.get(i).getType()] + " "
		// + tokens.get(i).getType());
		// i++;
		// tokens.consume();
		// if (System.currentTimeMillis() < 1000)
		// break;
		// }
		ScriptBlockContext scriptStatement = null;
		CFSCRIPTParser parser = new CFSCRIPTParser(tokens);
		
		parser.getErrorListeners().clear();
		lexer.addErrorListener(errorReporter);
		parser.reset();
		// parser.addErrorListener(errorReporter);
		parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
		parser.setErrorHandler(new BailErrorStrategy());
		try {
			scriptStatement = parser.scriptBlock();
			// } catch (CFParseException e) {
			// addMessage(new ParseError(e.line, e.charPositionInLine, e.charPositionInLine,
			// e.getSourceException().input.toString(), e.getMessage()));
			
		} catch (Exception e) {
			/*
			 * throw new ParseException(e.getOffendingToken(), "Unexpected \'" +
			 * parser.getTokenErrorDisplay(e.getOffendingToken()) + "\' (" + e.getOffendingToken().getText() + ")");
			 */
			tokens.reset(); // rewind input stream
			parser.reset();
			parser.addErrorListener(errorReporter);
			
			parser.setErrorHandler(new DefaultErrorStrategy());
			parser.getInterpreter().setPredictionMode(PredictionMode.LL);
			scriptStatement = parser.scriptBlock(); // STAGE 2
		}
		// TestUtils.showGUI(scriptStatement, CFSCRIPTParser.ruleNames);
		return scriptStatement;
	}
	
	public void parse() {
		Iterator sources = fCfmlSources.keySet().iterator();
		while (sources.hasNext()) {
			parseElements(((CFMLSource) fCfmlSources.get(sources.next())));
		}
	}
	
	public CFMLSource getCFMLSource(String path) {
		CFMLSource cfmlSource = (CFMLSource) fCfmlSources.get(path);
		return cfmlSource;
	}
	
	public void setErrorReporter(IErrorReporter errorReporter) {
		this.errorReporter = errorReporter;
	}
	
	public void reset() {
		expressionVisitor.clear();
		scriptVisitor.clear();
	}
}
