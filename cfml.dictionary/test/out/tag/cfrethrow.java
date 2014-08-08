
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfrethrow extends GenericStartTagTypeCf {
	protected static final cfrethrow INSTANCE = new cfrethrow();

	private cfrethrow() {
		super("CFML if tag", "<cfrethrow", ">", EndTagType.NORMAL, true, true, false);
	}

}
					