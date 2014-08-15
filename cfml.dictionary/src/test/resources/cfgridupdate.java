
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfgridupdate extends GenericStartTagTypeCf {
	protected static final cfgridupdate INSTANCE = new cfgridupdate();

	private cfgridupdate() {
		super("CFML if tag", "<cfgridupdate", ">", EndTagType.NORMAL, true, true, false);
	}

}
					