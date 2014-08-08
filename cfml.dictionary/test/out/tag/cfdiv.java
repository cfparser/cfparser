
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfdiv extends GenericStartTagTypeCf {
	protected static final cfdiv INSTANCE = new cfdiv();

	private cfdiv() {
		super("CFML if tag", "<cfdiv", ">", EndTagType.NORMAL, true, true, false);
	}

}
					