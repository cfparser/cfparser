
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfobjectcache extends GenericStartTagTypeCf {
	protected static final cfobjectcache INSTANCE = new cfobjectcache();

	private cfobjectcache() {
		super("CFML if tag", "<cfobjectcache", ">", EndTagType.NORMAL, true, true, false);
	}

}
					