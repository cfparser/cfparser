
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cffeed extends GenericStartTagTypeCf {
	protected static final cffeed INSTANCE = new cffeed();

	private cffeed() {
		super("CFML if tag", "<cffeed", ">", EndTagType.NORMAL, true, true, false);
	}

}
					