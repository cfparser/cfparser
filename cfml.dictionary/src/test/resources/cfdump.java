
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfdump extends GenericStartTagTypeCf {
	protected static final cfdump INSTANCE = new cfdump();

	private cfdump() {
		super("CFML if tag", "<cfdump", ">", EndTagType.NORMAL, true, true, false);
	}

}
					