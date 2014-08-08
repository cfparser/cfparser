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

final class LoggerProviderLog4J implements LoggerProvider {
	public static final LoggerProvider INSTANCE=new LoggerProviderLog4J();
	
	private LoggerProviderLog4J() {}

	public Logger getLogger(final String name) {
		return new Log4JLogger(org.apache.log4j.Logger.getLogger(name));
	}
	
	private static class Log4JLogger implements Logger {
		private final org.apache.log4j.Logger log4JLogger;
		
		public Log4JLogger(final org.apache.log4j.Logger log4JLogger) {
			this.log4JLogger=log4JLogger;
		}

		public void error(final String message) {
			log4JLogger.error(message);
		}
	
		public void warn(final String message) {
			log4JLogger.warn(message);
		}
	
		public void info(final String message) {
			log4JLogger.info(message);
		}
	
		public void debug(final String message) {
			log4JLogger.debug(message);
		}
	
		public boolean isErrorEnabled() {
			return log4JLogger.isEnabledFor(org.apache.log4j.Level.ERROR);
		}
	
		public boolean isWarnEnabled() {
			return log4JLogger.isEnabledFor(org.apache.log4j.Level.WARN);
		}
	
		public boolean isInfoEnabled() {
			return log4JLogger.isEnabledFor(org.apache.log4j.Level.INFO);
		}
	
		public boolean isDebugEnabled() {
			return log4JLogger.isEnabledFor(org.apache.log4j.Level.DEBUG);
		}
	}
}
