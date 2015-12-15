package cfml.parsing.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SourceReader extends Object {
	
	private List<String> lineBuffer = new ArrayList<String>();
	private int lineStart, lineCount;
	
	public SourceReader(BufferedReader in) throws IOException {
		this(in, 0, Integer.MAX_VALUE);
	}
	
	public SourceReader(BufferedReader in, int _lineStart, int _lineCount) throws IOException {
		
		lineStart = _lineStart;
		lineCount = _lineCount;
		
		if (lineStart < 0)
			lineStart = 0;
			
		// --[ Skip a head
		int currentLine = 0;
		while ((currentLine < lineStart) && (in.readLine() != null)) {
			currentLine++;
		}
		
		if (currentLine != lineStart)
			throw new IOException("sourceReader: not enough lines in the file");
			
		String line;
		for (int i = 0; (i < lineCount) && ((line = in.readLine()) != null); i++) {
			lineBuffer.add(line);
		}
	}
	
	public String[] getLines() {
		return lineBuffer.toArray(new String[lineBuffer.size()]);
	}
	
	public int getLineStart() {
		return lineStart;
	}
}
