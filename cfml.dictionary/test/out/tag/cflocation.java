
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cflocation extends GenericStartTagTypeCf {
	protected static final cflocation INSTANCE = new cflocation();

	private cflocation() {
		super("CFML if tag", "<cflocation", ">", EndTagType.NORMAL, true, true, false);
	}

}
					