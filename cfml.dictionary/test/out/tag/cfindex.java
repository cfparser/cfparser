
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfindex extends GenericStartTagTypeCf {
	protected static final cfindex INSTANCE = new cfindex();

	private cfindex() {
		super("CFML if tag", "<cfindex", ">", EndTagType.NORMAL, true, true, false);
	}

}
					