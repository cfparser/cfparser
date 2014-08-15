
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfsprydataset extends GenericStartTagTypeCf {
	protected static final cfsprydataset INSTANCE = new cfsprydataset();

	private cfsprydataset() {
		super("CFML if tag", "<cfsprydataset", ">", EndTagType.NORMAL, true, true, false);
	}

}
					