
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfimap extends GenericStartTagTypeCf {
	protected static final cfimap INSTANCE = new cfimap();

	private cfimap() {
		super("CFML if tag", "<cfimap", ">", EndTagType.NORMAL, true, true, false);
	}

}
					