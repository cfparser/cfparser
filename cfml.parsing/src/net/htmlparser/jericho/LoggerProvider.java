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
 * Defines the interface for a factory class to provide {@link Logger} instances for each {@link Source} object.
 * <p>
 * It is not usually necessary for users to create implementations of this interface, as
 * several predefined instances are defined which provide the most commonly required {@link Logger} implementations.
 * <p>
 * By default, a <code>LoggerProvider</code> is chosen automatically according to the algorithm described in the static {@link Config#LoggerProvider} property.
 * This automatic choice can be overridden by setting the {@link Config#LoggerProvider} property manually with an instance of this interface,
 * but this is also usually not necessary.
 */
public interface LoggerProvider {
	/**
	 * A {@link LoggerProvider} implementation that disables all log messages.
	 */
	public static final LoggerProvider DISABLED=LoggerProviderDisabled.INSTANCE;

	/**
	 * A {@link LoggerProvider} implementation that sends all log messages to the standard error output stream (<code>System.err</code>).
	 * <p>
	 * The implementation uses the following code to create each logger:<br />
	 * <code>new </code>{@link WriterLogger}<code>(new OutputStreamWriter(System.err),name)</code>
	 */
	public static final LoggerProvider STDERR=LoggerProviderSTDERR.INSTANCE;

	/**
	 * A {@link LoggerProvider} implementation that wraps the standard <code>java.util.logging</code> system included in the Java SDK version 1.4 and above.
	 * <p>
	 * This is the default used if no other logging framework is detected.  See the description of the static {@link Config#LoggerProvider} property for more details.
	 * <p>
	 * The following mapping of <a href="Logger.html#LoggingLevel">logging levels</a> is used:
	 * <table class="bordered" style="margin: 15px" cellspacing="0">
	 *  <tr><th>{@link Logger} level<th><code>java.util.logging.Level</code>
	 *  <tr><td>{@link Logger#error(String) ERROR}<td><code>SEVERE</code>
	 *  <tr><td>{@link Logger#warn(String) WARN}<td><code>WARNING</code>
	 *  <tr><td>{@link Logger#info(String) INFO}<td><code>INFO</code>
	 *  <tr><td>{@link Logger#debug(String) DEBUG}<td><code>FINE</code>
	 * </table>
	 */
	public static final LoggerProvider JAVA=LoggerProviderJava.INSTANCE;

	/**
	 * A {@link LoggerProvider} implementation that wraps the <a target="_blank" href="http://jakarta.apache.org/commons/logging/">Jakarta Commons Logging</a> (JCL) framework.
	 * <p>
	 * See the description of the static {@link Config#LoggerProvider} property for details on when this implementation is used as the default.
	 * <p>
	 * The following mapping of <a href="Logger.html#LoggingLevel">logging levels</a> is used:
	 * <table class="bordered" style="margin: 15px" cellspacing="0">
	 *  <tr><th>{@link Logger} level<th><code>org.apache.commons.logging</code> level
	 *  <tr><td>{@link Logger#error(String) ERROR}<td><code>error</code>
	 *  <tr><td>{@link Logger#warn(String) WARN}<td><code>warn</code>
	 *  <tr><td>{@link Logger#info(String) INFO}<td><code>info</code>
	 *  <tr><td>{@link Logger#debug(String) DEBUG}<td><code>debug</code>
	 * </table>
	 */
	public static final LoggerProvider JCL=LoggerProviderJCL.INSTANCE;

	/**
	 * A {@link LoggerProvider} implementation that wraps the <a target="_blank" href="http://logging.apache.org/log4j/">Apache Log4J</a> framework.
	 * <p>
	 * See the description of the static {@link Config#LoggerProvider} property for details on when this implementation is used as the default.
	 * <p>
	 * The following mapping of <a href="Logger.html#LoggingLevel">logging levels</a> is used:
	 * <table class="bordered" style="margin: 15px" cellspacing="0">
	 *  <tr><th>{@link Logger} level<th><code>org.apache.log4j.Level</code>
	 *  <tr><td>{@link Logger#error(String) ERROR}<td><code>ERROR</code>
	 *  <tr><td>{@link Logger#warn(String) WARN}<td><code>WARN</code>
	 *  <tr><td>{@link Logger#info(String) INFO}<td><code>INFO</code>
	 *  <tr><td>{@link Logger#debug(String) DEBUG}<td><code>DEBUG</code>
	 * </table>
	 */
	public static final LoggerProvider LOG4J=LoggerProviderLog4J.INSTANCE;

	/**
	 * A {@link LoggerProvider} implementation that wraps the <a target="_blank" href="http://www.slf4j.org/">SLF4J</a> framework.
	 * <p>
	 * See the description of the static {@link Config#LoggerProvider} property for details on when this implementation is used as the default.
	 * <p>
	 * The following mapping of <a href="Logger.html#LoggingLevel">logging levels</a> is used:
	 * <table class="bordered" style="margin: 15px" cellspacing="0">
	 *  <tr><th>{@link Logger} level<th><code>org.slf4j.Logger</code> level
	 *  <tr><td>{@link Logger#error(String) ERROR}<td><code>error</code>
	 *  <tr><td>{@link Logger#warn(String) WARN}<td><code>warn</code>
	 *  <tr><td>{@link Logger#info(String) INFO}<td><code>info</code>
	 *  <tr><td>{@link Logger#debug(String) DEBUG}<td><code>debug</code>
	 * </table>
	 */
	public static final LoggerProvider SLF4J=LoggerProviderSLF4J.INSTANCE;
	
	/**
	 * Creates a new {@link Logger} instance with the specified name.
	 * <p>
	 * The <code>name</code> argument is used by the underlying logging implementation, and is normally a dot-separated name based on
	 * the package name or class name of the subsystem.
	 * <p>
	 * The name used for all automatically created {@link Logger} instances is "<code>net.htmlparser.jericho</code>".
	 *
	 * @param name  the name of the logger, the use of which is determined by the underlying logging implementation, may be <code>null</code>.
	 * @return a new {@link Logger} instance with the specified name.
	 */
	public Logger getLogger(String name);
}
