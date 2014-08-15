
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cferror extends GenericStartTagTypeCf {
	protected static final cferror INSTANCE = new cferror();

	private cferror() {
		super("CFML if tag", "<cferror", ">", EndTagType.NORMAL, true, true, false);
	}

}
					