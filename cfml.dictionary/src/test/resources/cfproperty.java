
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfproperty extends GenericStartTagTypeCf {
	protected static final cfproperty INSTANCE = new cfproperty();

	private cfproperty() {
		super("CFML if tag", "<cfproperty", ">", EndTagType.NORMAL, true, true, false);
	}

}
					