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

/**
 * Defines the interface for handling log messages.
 * <p>
 * It is not usually necessary for users to create implementations of this interface, as
 * the {@link LoggerProvider} interface contains several predefined instances which provide the most commonly required <code>Logger</code> implementations.
 * <p>
 * By default, logging is configured automatically according to the algorithm described in the static {@link Config#LoggerProvider} property.
 * <p>
 * An instance of a class that implements this interface is used by calling the {@link Source#setLogger(Logger)} method on the relevant {@link Source} object.
 * <p>
 * Four <i><a name="LoggingLevel">logging levels</a></i> are defined in this interface.
 * The logging level is specified only by the use of different method names, there is no class or type defining the levels.
 * This makes the code required to wrap other logging frameworks much simpler and more efficient.
 * <p>
 * The four logging levels are:
 * <ul class="SmallVerticalMargin">
 *  <li>{@link #error(String) ERROR}
 *  <li>{@link #warn(String) WARN}
 *  <li>{@link #info(String) INFO}
 *  <li>{@link #debug(String) DEBUG}
 * </ul>
 * <p>
 * IMPLEMENTATION NOTE: Ideally the <code>java.util.logging.Logger</code> class could have been used as a basis for logging, even if used to define a wrapper
 * around other logging frameworks.
 * This would have avoided the need to define yet another logging interface, but because <code>java.util.logging.Logger</code> is implemented very poorly,
 * it is quite tricky to extend it as a wrapper.
 * Other logging wrapper frameworks such as <a target="_blank" href="http://www.slf4j.org/">SLF4J</a> or
 * <a target="_blank" href="http://jakarta.apache.org/commons/logging/">Jakarta Commons Logging</a> provide good logging interfaces, but to avoid
 * introducing dependencies it was decided to create this new interface.
 *
 * @see Config#LoggerProvider
 */
public interface Logger {
	/**
	 * Logs a message at the ERROR level.
	 * @param message  the message to log.
	 */
	void error(String message);

	/**
	 * Logs a message at the WARN level.
	 * @param message  the message to log.
	 */
	void warn(String message);

	/**
	 * Logs a message at the INFO level.
	 * @param message  the message to log.
	 */
	void info(String message);

	/**
	 * Logs a message at the DEBUG level.
	 * @param message  the message to log.
	 */
	void debug(String message);

	/**
	 * Indicates whether logging is enabled at the ERROR level.
	 * @return <code>true</code> if logging is enabled at the ERROR level, otherwise <code>false</code>.
	 */
	boolean isErrorEnabled();

	/**
	 * Indicates whether logging is enabled at the WARN level.
	 * @return <code>true</code> if logging is enabled at the WARN level, otherwise <code>false</code>.
	 */
	boolean isWarnEnabled();

	/**
	 * Indicates whether logging is enabled at the INFO level.
	 * @return <code>true</code> if logging is enabled at the INFO level, otherwise <code>false</code>.
	 */
	boolean isInfoEnabled();

	/**
	 * Indicates whether logging is enabled at the DEBUG level.
	 * @return <code>true</code> if logging is enabled at the DEBUG level, otherwise <code>false</code>.
	 */
	boolean isDebugEnabled();
}