
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfreturn extends GenericStartTagTypeCf {
	protected static final cfreturn INSTANCE = new cfreturn();

	private cfreturn() {
		super("CFML if tag", "<cfreturn", ">", EndTagType.NORMAL, true, true, false);
	}

}
					