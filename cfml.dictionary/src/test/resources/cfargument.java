
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfargument extends GenericStartTagTypeCf {
	protected static final cfargument INSTANCE = new cfargument();

	private cfargument() {
		super("CFML if tag", "<cfargument", ">", EndTagType.NORMAL, true, true, false);
	}

}
					