
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfchartseries extends GenericStartTagTypeCf {
	protected static final cfchartseries INSTANCE = new cfchartseries();

	private cfchartseries() {
		super("CFML if tag", "<cfchartseries", ">", EndTagType.NORMAL, true, true, false);
	}

}
					