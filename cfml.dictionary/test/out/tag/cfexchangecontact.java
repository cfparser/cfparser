
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfexchangecontact extends GenericStartTagTypeCf {
	protected static final cfexchangecontact INSTANCE = new cfexchangecontact();

	private cfexchangecontact() {
		super("CFML if tag", "<cfexchangecontact", ">", EndTagType.NORMAL, true, true, false);
	}

}
					