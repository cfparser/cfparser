
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfassociate extends GenericStartTagTypeCf {
	protected static final cfassociate INSTANCE = new cfassociate();

	private cfassociate() {
		super("CFML if tag", "<cfassociate", ">", EndTagType.NORMAL, true, true, false);
	}

}
					