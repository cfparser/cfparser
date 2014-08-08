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

final class LoggerProviderJava implements LoggerProvider {
	public static final LoggerProvider INSTANCE=new LoggerProviderJava();
	
	private LoggerProviderJava() {}

	public Logger getLogger(final String name) {
		return new JavaLogger(java.util.logging.Logger.getLogger(name));
	}

	private class JavaLogger implements Logger {
		private final java.util.logging.Logger javaLogger;
		
		public JavaLogger(final java.util.logging.Logger javaLogger) {
			this.javaLogger=javaLogger;
		}

		public void error(final String message) {
			javaLogger.severe(message);
		}
	
		public void warn(final String message) {
			javaLogger.warning(message);
		}
	
		public void info(final String message) {
			javaLogger.info(message);
		}
	
		public void debug(final String message) {
			javaLogger.fine(message);
		}
	
		public boolean isErrorEnabled() {
			return javaLogger.isLoggable(java.util.logging.Level.SEVERE);
		}
	
		public boolean isWarnEnabled() {
			return javaLogger.isLoggable(java.util.logging.Level.WARNING);
		}
	
		public boolean isInfoEnabled() {
			return javaLogger.isLoggable(java.util.logging.Level.INFO);
		}
	
		public boolean isDebugEnabled() {
			return javaLogger.isLoggable(java.util.logging.Level.FINE);
		}
	}
}
