
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cftreeitem extends GenericStartTagTypeCf {
	protected static final cftreeitem INSTANCE = new cftreeitem();

	private cftreeitem() {
		super("CFML if tag", "<cftreeitem", ">", EndTagType.NORMAL, true, true, false);
	}

}
					