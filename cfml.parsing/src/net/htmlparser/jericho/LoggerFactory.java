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

final class LoggerFactory {
	private static LoggerProvider defaultLoggerProvider=null;

	public static Logger getLogger(final String name) {
		return getLoggerProvider().getLogger(name);
	}

	public static Logger getLogger(final Class loggedClass) {
		return getLogger(loggedClass.getName());
	}
	
	public static LoggerProvider getLoggerProvider() {
		return (Config.LoggerProvider!=null) ? Config.LoggerProvider : getDefaultLoggerProvider();
	}

	private static LoggerProvider getDefaultLoggerProvider() {
		if (defaultLoggerProvider==null) defaultLoggerProvider=determineDefaultLoggerProvider();
		return defaultLoggerProvider;
	}

	private static LoggerProvider determineDefaultLoggerProvider() {
		if (isClassAvailable("org.slf4j.impl.StaticLoggerBinder")) {
			if (isClassAvailable("org.slf4j.impl.JDK14LoggerFactory")) return LoggerProvider.JAVA;
			if (isClassAvailable("org.slf4j.impl.Log4jLoggerFactory")) return LoggerProvider.LOG4J;
			if (!isClassAvailable("org.slf4j.impl.JCLLoggerFactory")) return LoggerProvider.SLF4J;
			// fall through to next check if SLF4J is configured to use JCL
		}
		if (isClassAvailable("org.apache.commons.logging.Log")) {
			final String logClassName=org.apache.commons.logging.LogFactory.getLog("test").getClass().getName();
			if (logClassName.equals("org.apache.commons.logging.impl.Jdk14Logger")) return LoggerProvider.JAVA;
			if (logClassName.equals("org.apache.commons.logging.impl.Log4JLogger")) return LoggerProvider.LOG4J;
			return LoggerProvider.JCL;
		}
		if (isClassAvailable("org.apache.log4j.Logger")) return LoggerProvider.LOG4J;
		return LoggerProvider.JAVA;
	}

	private static boolean isClassAvailable(final String className) {
		try {
			Class.forName(className);
			return true;
		} catch (Throwable t) {
			return false;
		}
	}
}
