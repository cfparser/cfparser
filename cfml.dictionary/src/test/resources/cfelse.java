
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfelse extends GenericStartTagTypeCf {
	protected static final cfelse INSTANCE = new cfelse();

	private cfelse() {
		super("CFML if tag", "<cfelse", ">", EndTagType.NORMAL, true, true, false);
	}

}
					