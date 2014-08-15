
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfcalendar extends GenericStartTagTypeCf {
	protected static final cfcalendar INSTANCE = new cfcalendar();

	private cfcalendar() {
		super("CFML if tag", "<cfcalendar", ">", EndTagType.NORMAL, true, true, false);
	}

}
					