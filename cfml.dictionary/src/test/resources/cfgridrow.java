
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfgridrow extends GenericStartTagTypeCf {
	protected static final cfgridrow INSTANCE = new cfgridrow();

	private cfgridrow() {
		super("CFML if tag", "<cfgridrow", ">", EndTagType.NORMAL, true, true, false);
	}

}
					