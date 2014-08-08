
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfdbinfo extends GenericStartTagTypeCf {
	protected static final cfdbinfo INSTANCE = new cfdbinfo();

	private cfdbinfo() {
		super("CFML if tag", "<cfdbinfo", ">", EndTagType.NORMAL, true, true, false);
	}

}
					