
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfchartdata extends GenericStartTagTypeCf {
	protected static final cfchartdata INSTANCE = new cfchartdata();

	private cfchartdata() {
		super("CFML if tag", "<cfchartdata", ">", EndTagType.NORMAL, true, true, false);
	}

}
					