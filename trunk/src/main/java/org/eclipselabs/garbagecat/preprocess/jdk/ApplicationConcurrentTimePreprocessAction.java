/******************************************************************************
 * Garbage Cat                                                                *
 *                                                                            *
 * Copyright (c) 2008-2010 Red Hat, Inc.                                      *
 * All rights reserved. This program and the accompanying materials           *
 * are made available under the terms of the Eclipse Public License v1.0      *
 * which accompanies this distribution, and is available at                   *
 * http://www.eclipse.org/legal/epl-v10.html                                  *
 *                                                                            *
 * Contributors:                                                              *
 *    Red Hat, Inc. - initial API and implementation                          *
 ******************************************************************************/
package org.eclipselabs.garbagecat.preprocess.jdk;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipselabs.garbagecat.util.jdk.JdkRegEx;
import org.eclipselabs.garbagecat.util.jdk.JdkUtil;

/**
 * <p>
 * APPLICATION_CONCURRENT_TIME
 * </p>
 * 
 * <p>
 * Combined {@link org.eclipselabs.garbagecat.domain.jdk.CmsConcurrentEvent} and
 * {@link org.eclipselabs.garbagecat.domain.jdk.ApplicationConcurrentTimeEvent} split across 2
 * lines. Split into separate events. Appears to happen when the JVM is under stress with low
 * throughput. It could be a JVM bug.
 * </p>
 * 
 * <h3>Example Logging</h3>
 * 
 * <pre>
 * 1122748.949Application time: 0.0005210 seconds
 * : [CMS-concurrent-mark-start]
 * </pre>
 * 
 * Preprocessed:
 * 
 * <pre>
 * Application time: 0.0005210 seconds
 * 1122748.949: [CMS-concurrent-mark-start]
 * </pre>
 * 
 * @author <a href="mailto:mmillson@redhat.com">Mike Millson</a>
 * 
 */
public class ApplicationConcurrentTimePreprocessAction implements PreprocessAction {

	/**
	 * Regular expressions defining the 1st logging line.
	 */
	private static final String REGEX_LINE1 = "^(" + JdkRegEx.TIMESTAMP
			+ ")(: \\[CMS-concurrent-preclean: " + JdkRegEx.DURATION_FRACTION
			+ "\\])?(Application time: \\d{1,4}\\.\\d{7} seconds)$";

	/**
	 * Regular expressions defining the 2nd logging line.
	 */
	private static final String REGEX_LINE2 = "^(: \\[CMS-concurrent-mark-start\\])?"
			+ JdkRegEx.TIMES_BLOCK + "?$";

	/**
	 * The log entry for the event. Can be used for debugging purposes.
	 */
	private String logEntry;

	/**
	 * Create event from log entry.
	 */
	public ApplicationConcurrentTimePreprocessAction(String logEntry) {
		Pattern pattern = Pattern.compile(REGEX_LINE1);
		Matcher matcher = pattern.matcher(logEntry);
		if (matcher.find()) {
			this.logEntry = logEntry;
			// Split line1 logging apart
			if (matcher.group(5) != null) {
				this.logEntry = matcher.group(5) + "\n";
				if (matcher.group(1) != null) {
					this.logEntry = this.logEntry + matcher.group(1);
				}
				if (matcher.group(3) != null) {
					this.logEntry = this.logEntry + matcher.group(3);
				}
			}
		} else {
			// line2 logging
			this.logEntry = logEntry + "\n";
		}
	}

	public String getLogEntry() {
		return logEntry;
	}

	public String getName() {
		return JdkUtil.PreprocessActionType.APPLICATION_CONCURRENT_TIME.toString();
	}

	/**
	 * Determine if the logLine matches the logging pattern(s) for this event.
	 * 
	 * @param logLine
	 *            The log line to test.
	 * @param priorLogLine
	 *            The last log entry processed.
	 * @return true if the log line matches the event pattern, false otherwise.
	 */
	public static final boolean match(String logLine, String priorLogLine) {
		return (logLine.matches(REGEX_LINE1) || (logLine.matches(REGEX_LINE2) && priorLogLine
				.matches(REGEX_LINE1)));
	}
}
