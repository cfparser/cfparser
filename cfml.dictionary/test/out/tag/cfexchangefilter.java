
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfexchangefilter extends GenericStartTagTypeCf {
	protected static final cfexchangefilter INSTANCE = new cfexchangefilter();

	private cfexchangefilter() {
		super("CFML if tag", "<cfexchangefilter", ">", EndTagType.NORMAL, true, true, false);
	}

}
					