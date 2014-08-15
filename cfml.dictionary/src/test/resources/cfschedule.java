
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfschedule extends GenericStartTagTypeCf {
	protected static final cfschedule INSTANCE = new cfschedule();

	private cfschedule() {
		super("CFML if tag", "<cfschedule", ">", EndTagType.NORMAL, true, true, false);
	}

}
					