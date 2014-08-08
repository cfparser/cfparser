
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfexchangetask extends GenericStartTagTypeCf {
	protected static final cfexchangetask INSTANCE = new cfexchangetask();

	private cfexchangetask() {
		super("CFML if tag", "<cfexchangetask", ">", EndTagType.NORMAL, true, true, false);
	}

}
					