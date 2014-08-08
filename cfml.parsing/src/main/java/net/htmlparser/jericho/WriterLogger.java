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

import java.io.*;

/**
 * Provides an implementation of the {@link Logger} interface that sends output to the specified <code>java.io.Writer</code>.
 * <p>
 * Each log entry is formatted using the {@link BasicLogFormatter#format(String level, String message, String loggerName)} method.
 * <p>
 * Note that each <a href="Logger.html#LoggingLevel">logging level</a> can be enabled independently in this implementation.
 * All levels are enabled by default.
 */
public class WriterLogger implements Logger {
	private final Writer writer;
	private final String name;

	private boolean errorEnabled=true;
	private boolean warnEnabled=true;
	private boolean infoEnabled=true;
	private boolean debugEnabled=true;

	/**
	 * Constructs a new <code>WriterLogger</code> with the specified <code>Writer</code> and the default name.
	 * <p>
	 * The default logger name is "<code>net.htmlparser.jericho</code>".
	 *
	 * @param writer  the <code>Writer</code> to which all output is sent.
	 */
	public WriterLogger(final Writer writer) {
		this(writer,Source.PACKAGE_NAME);
	}

	/**
	 * Constructs a new <code>WriterLogger</code> with the specified <code>Writer</code> and name.
	 * <p>
	 * The value of the <code>name</code> argument is only relevant if the {@link BasicLogFormatter#OutputName} static property is set to <code>true</code>,
	 * otherwise the name is not included in the output at all.
	 *
	 * @param writer  the <code>Writer</code> to which all output is sent.
	 * @param name  the logger name, may be <code>null</code>.
	 */
	public WriterLogger(final Writer writer, final String name) {
		this.writer=writer;
		this.name=name;
	}

	/**
	 * Returns the <code>Writer</code> to which all output is sent.
	 * @return the <code>Writer</code> to which all output is sent.
	 */
	public Writer getWriter() {
		return writer;
	}

	/**
	 * Returns the name of this logger.
	 * @return the name of this logger, may be <code>null</code>.
	 */
	public String getName() {
		return name;
	}

	// Documentation inherited from Logger
	public void error(final String message) {
		if (isErrorEnabled()) log("ERROR",message);
	}

	// Documentation inherited from Logger
	public void warn(final String message) {
		if (isWarnEnabled()) log("WARN",message);
	}

	// Documentation inherited from Logger
	public void info(final String message) {
		if (isInfoEnabled()) log("INFO",message);
	}

	// Documentation inherited from Logger
	public void debug(final String message) {
		if (isDebugEnabled()) log("DEBUG",message);
	}

	// Documentation inherited from Logger
	public boolean isErrorEnabled() {
		return errorEnabled;
	}

	/**
	 * Sets whether logging is enabled at the ERROR level.
	 * @param errorEnabled  determines whether logging is enabled at the ERROR level.
	 */
	public void setErrorEnabled(final boolean errorEnabled) {
		this.errorEnabled=errorEnabled;
	}

	// Documentation inherited from Logger
	public boolean isWarnEnabled() {
		return warnEnabled;
	}

	/**
	 * Sets whether logging is enabled at the WARN level.
	 * @param warnEnabled  determines whether logging is enabled at the WARN level.
	 */
	public void setWarnEnabled(final boolean warnEnabled) {
		this.warnEnabled=warnEnabled;
	}

	// Documentation inherited from Logger
	public boolean isInfoEnabled() {
		return infoEnabled;
	}

	/**
	 * Sets whether logging is enabled at the INFO level.
	 * @param infoEnabled  determines whether logging is enabled at the INFO level.
	 */
	public void setInfoEnabled(final boolean infoEnabled) {
		this.infoEnabled=infoEnabled;
	}

	// Documentation inherited from Logger
	public boolean isDebugEnabled() {
		return debugEnabled;
	}

	/**
	 * Sets whether logging is enabled at the DEBUG level.
	 * @param debugEnabled  determines whether logging is enabled at the DEBUG level.
	 */
	public void setDebugEnabled(final boolean debugEnabled) {
		this.debugEnabled=debugEnabled;
	}

	/**
	 * Logs the specified message at the specified level.
	 * <p>
	 * This method is called internally by the {@link #error(String)}, {@link #warn(String)}, {@link #info(String)} and {@link #debug(String)} methods,
	 * with the <code>level</code> argument set to the text "<code>ERROR</code>", "<code>WARN</code>", "<code>INFO</code>", or "<code>DEBUG</code>" respectively.
	 * <p>
	 * The default implementation of this method sends the the output of
	 * {@link BasicLogFormatter#format(String,String,String) BasicLogFormatter.format}<code>(level,message,</code>{@link #getName()}<code>)</code>
	 * to the {@link #getWriter() Writer} specified in the class constructor, and then flushes it.
	 * <p>
	 * Overriding this method in a subclass provides a convenient means of logging to a <code>Writer</code> using a different format.
	 *
	 * @param level  a string representing the level of the log message.
	 * @param message  the message to log.
	 */
	protected void log(final String level, final String message) {
		try {
			writer.write(BasicLogFormatter.format(level,message,name));
			writer.flush();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
}
