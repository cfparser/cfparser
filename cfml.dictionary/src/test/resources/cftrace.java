
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cftrace extends GenericStartTagTypeCf {
	protected static final cftrace INSTANCE = new cftrace();

	private cftrace() {
		super("CFML if tag", "<cftrace", ">", EndTagType.NORMAL, true, true, false);
	}

}
					