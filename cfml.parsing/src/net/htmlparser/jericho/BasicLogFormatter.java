// Jericho HTML Parser - Java based library for analysing and manipulating HTML
// Version 3.1
// Copyright (C) 2004-2009 Martin Jericho
// http://jericho.htmlparser.net/
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of either one of the following licences:
//
// 1. The Eclipse Public License (EPL) version 1.0,
// included in this distribution in the file licence-epl-1.0.html
// or available at http://www.eclipse.org/legal/epl-v10.html
//
// 2. The GNU Lesser General Public License (LGPL) version 2.1 or later,
// included in this distribution in the file licence-lgpl-2.1.txt
// or available at http://www.gnu.org/licenses/lgpl.txt
//
// This library is distributed on an "AS IS" basis,
// WITHOUT WARRANTY OF ANY KIND, either express or implied.
// See the individual licence texts for more details.

package net.htmlparser.jericho;

import java.util.logging.*;

/**
 * Provides basic formatting for log messages.
 * <p>
 * This class extends the <code>java.util.logging.Formatter</code> class, allowing it to be specified as a formatter for the <code>java.util.logging</code> system.
 * <p>
 * The static {@link #format(String level, String message, String loggerName)} method provides a means of using the same formatting
 * outside of the <code>java.util.logging</code> framework.  See the documentation of this method for more details.
 */
public class BasicLogFormatter extends Formatter {
	/**
	 * Determines whether the <a href="Logger.html#LoggingLevel">logging level</a> is included in the output.
	 * <p>
	 * The default value is <code>true</code>.
	 * <p>
	 * As this is a static property, changing the value will affect all <code>BasicLogFormatter</code> instances, as well as the behaviour of the
	 * static {@link #format(String level, String message, String loggerName)} method.
	 */
	public static boolean OutputLevel=true;

	/**
	 * Determines whether the logger name is included in the output.
	 * <p>
	 * The default value is <code>false</code>.
	 * <p>
	 * The logger name used for all automatically created {@link Logger} instances is "<code>net.htmlparser.jericho</code>".
	 * <p>
	 * As this is a static property, changing the value will affect all <code>BasicLogFormatter</code> instances, as well as the behaviour of the
	 * static {@link #format(String level, String message, String loggerName)} method.
	 */
	public static boolean OutputName=false;
	
	static final Formatter INSTANCE=new BasicLogFormatter();
	
	/**
	 * Returns a formatted string representing the log entry information contained in the specified <code>java.util.logging.LogRecord</code>.
	 * <p>
	 * This method is not called directly, but is used by the <code>java.util.logging</code> framework when this class is specified
	 * as a formatter in the <code>logging.properties</code> file.
	 * <p>
	 * See the documentation of the parent <code>java.util.logging.Formatter</code> class in the Java SDK for more details.
	 *
	 * @param logRecord  a <code>java.util.logging.LogRecord</code> object containing all of the log entry information.
	 * @return a formatted string representing the log entry information contained in the specified <code>java.util.logging.LogRecord</code>.
	 */
	public String format(final LogRecord logRecord) {
		return format(logRecord.getLevel().getName(),logRecord.getMessage(),logRecord.getLoggerName());
	}

	/**
	 * Returns a formatted string representing the specified log entry information.
	 * <p>
	 * This method is used by the default implementation of the {@link WriterLogger#log(String level, String message)} method.
	 * <p>
	 * The static properties {@link #OutputLevel} and {@link #OutputName} affect what information is included in the output.
	 * <p>
	 * The static {@link Config#NewLine} property determines the character sequence used for line breaks.
	 * <p>
	 * A line of output typically looks like this:
	 * <blockquote class="SmallVerticalMargin"><code>INFO: this is the log message</code></blockquote>
	 * or if the {@link #OutputName} property is set to <code>true</code>, the output would look similar to this:
	 * <blockquote class="SmallVerticalMargin"><code>INFO: [net.htmlparser.jericho] this is the log message</code></blockquote>
	 *
	 * @param level  a string representing the <a href="Logger.html#LoggingLevel">logging level</a> of the log entry.
	 * @param message  the log message.
	 * @param loggerName  the name of the logger.
	 * @return a formatted string representing the specified log entry information.
	 */
	public static String format(final String level, final String message, final String loggerName) {
		final StringBuilder sb=new StringBuilder(message.length()+40);
		if (OutputLevel) sb.append(level).append(": ");
		if (OutputName && loggerName!=null) sb.append('[').append(loggerName).append("] ");
		sb.append(message);
		sb.append(Config.NewLine);
		return sb.toString();
	}
}
