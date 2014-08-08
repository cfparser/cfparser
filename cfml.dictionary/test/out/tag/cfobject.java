
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfobject extends GenericStartTagTypeCf {
	protected static final cfobject INSTANCE = new cfobject();

	private cfobject() {
		super("CFML if tag", "<cfobject", ">", EndTagType.NORMAL, true, true, false);
	}

}
					