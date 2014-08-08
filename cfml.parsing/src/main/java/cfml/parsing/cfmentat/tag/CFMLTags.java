package cfml.parsing.cfmentat.tag;

import java.util.Set;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.StartTagType;
import net.htmlparser.jericho.TagType;
import cfml.dictionary.DictionaryManager;
import cfml.dictionary.SyntaxDictionary;
import cfml.dictionary.Tag;
import cfml.dictionary.preferences.DictionaryPreferences;
import cfml.parsing.preferences.ParserPreferences;

public class CFMLTags {
	
	public static final StartTagType CFML_STANDARD = StartTagTypeCFMLStandard.INSTANCE;
	public static final StartTagType CFML_SCRIPT = StartTagTypeCFMLScript.INSTANCE;
	public static final StartTagType HTML_SCRIPT = StartTagTypeHTMLScript.INSTANCE;
	public static final StartTagType CFML_COMMENT = StartTagTypeCFMLComment.INSTANCE;
	public static final StartTagType CFML_SET = StartTagTypeCfSet.INSTANCE;
	public static final StartTagType CFML_IF = StartTagTypeCfIf.INSTANCE;
	public static final StartTagType CFML_ELSE = StartTagTypeCfElse.INSTANCE;
	public static final StartTagType CFML_ELSEIF = StartTagTypeCfElseIf.INSTANCE;
	public static final StartTagType CFML_ARGUMENT = StartTagTypeCfArgument.INSTANCE;
	public static final StartTagType CFML_FUNCTION = StartTagTypeCfFunction.INSTANCE;
	public static final StartTagType CFML_CONTENT = StartTagTypeCfContent.INSTANCE;
	public static final StartTagType CFML_RETURN = StartTagTypeCfReturn.INSTANCE;
	public static final StartTagType CFML_MAIL = StartTagTypeCFMail.INSTANCE;
	public static final StartTagType CFML_QUERY = StartTagTypeCFQuery.INSTANCE;
	
	private static SyntaxDictionary cfdic;
	
	private CFMLTags() {
	}
	
	private static final TagType[] TAG_TYPES = { CFML_STANDARD, CFML_COMMENT, CFML_SET, CFML_IF, CFML_ELSE,
			CFML_ELSEIF, CFML_FUNCTION, CFML_ARGUMENT, HTML_SCRIPT, CFML_SCRIPT, CFML_CONTENT, CFML_RETURN, CFML_MAIL,
			CFML_QUERY };
	
	static boolean registered = false;
	
	public static void register() {
		if (registered)
			return;
		DictionaryManager.initDictionaries();
		cfdic = DictionaryManager.getDictionary("CF_DICTIONARY");
		Set<Tag> cfTags = cfdic.getAllTags();
		CFMLStartTag cftag;
		for (Tag tag : cfTags) {
			if (!tag.getName().equals("cfif") && tag.getName().equals("cfcomment")) {
				if (tag.isSingle()) {
					cftag = new CFMLStartTag(tag.getHelp(), "<" + tag.getName(), ">", null, false, tag.hasParameters(),
							tag.isXMLStyle());
				} else {
					cftag = new CFMLStartTag(tag.getHelp(), "<" + tag.getName(), ">", EndTagType.NORMAL, false,
							tag.hasParameters(), tag.isXMLStyle());
				}
				cftag.register();
			}
		}
		// we register these last so they override anything in the syntax dictionary
		for (TagType tagType : TAG_TYPES)
			tagType.register();
		registered = true;
	}
	
	public static void register(ParserPreferences prefs) {
		if (registered)
			return;
		DictionaryPreferences dictPrefs = new DictionaryPreferences();
		dictPrefs.setDictionaryDir(prefs.getDictionaryDir());
		dictPrefs.setCFDictionary(prefs.getCFDictionary());
		DictionaryManager.initDictionaries(dictPrefs);
		cfdic = DictionaryManager.getDictionary("CF_DICTIONARY");
		Set<Tag> cfTags = cfdic.getAllTags();
		CFMLStartTag cftag;
		for (Tag tag : cfTags) {
			if (!tag.getName().equals("cfif") && tag.getName().equals("cfcomment")) {
				if (tag.isSingle()) {
					cftag = new CFMLStartTag(tag.getHelp(), "<" + tag.getName(), ">", null, false, tag.hasParameters(),
							tag.isXMLStyle());
				} else {
					cftag = new CFMLStartTag(tag.getHelp(), "<" + tag.getName(), ">", EndTagType.NORMAL, false,
							tag.hasParameters(), tag.isXMLStyle());
				}
				cftag.register();
			}
		}
		// we register these last so they override anything in the syntax dictionary
		for (TagType tagType : TAG_TYPES)
			tagType.register();
		registered = true;
	}
	
}
