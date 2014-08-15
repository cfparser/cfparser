
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfgridcolumn extends GenericStartTagTypeCf {
	protected static final cfgridcolumn INSTANCE = new cfgridcolumn();

	private cfgridcolumn() {
		super("CFML if tag", "<cfgridcolumn", ">", EndTagType.NORMAL, true, true, false);
	}

}
					