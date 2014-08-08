
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfset extends GenericStartTagTypeCf {
	protected static final cfset INSTANCE = new cfset();

	private cfset() {
		super("CFML if tag", "<cfset", ">", EndTagType.NORMAL, true, true, false);
	}

}
					