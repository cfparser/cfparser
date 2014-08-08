
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfajaximport extends GenericStartTagTypeCf {
	protected static final cfajaximport INSTANCE = new cfajaximport();

	private cfajaximport() {
		super("CFML if tag", "<cfajaximport", ">", EndTagType.NORMAL, true, true, false);
	}

}
					