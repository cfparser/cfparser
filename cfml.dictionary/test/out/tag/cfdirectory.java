
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfdirectory extends GenericStartTagTypeCf {
	protected static final cfdirectory INSTANCE = new cfdirectory();

	private cfdirectory() {
		super("CFML if tag", "<cfdirectory", ">", EndTagType.NORMAL, true, true, false);
	}

}
					