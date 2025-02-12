/*
 * SPDX-License-Identifier: CDDL-1.0
 *
 * CDDL HEADER START
 *
 * This file and its contents are supplied under the terms of the
 * Common Development and Distribution License ("CDDL"), version 1.0.
 * You may only use this file in accordance with the terms of version
 * 1.0 of the CDDL.
 *
 * A full copy of the text of the CDDL should have accompanied this
 * source. A copy of the CDDL is also available via the Internet at
 * http://www.illumos.org/license/CDDL.
 *
 * CDDL HEADER END
 *
 * Copyright 2025 Peter Tribble
 *
 */

package uk.co.petertribble.jproc.util;

import java.text.DecimalFormat;
import java.text.DateFormat;
import java.util.Date;

/**
 * Utility methods to convert raw numbers into more aesthetically pleasing
 * human readable output.
 *
 * @author Peter Tribble
 */
public final class PrettyFormat {

    private static final double KSCALE = 1024.0;
    private static final double KMAX = 10000.0;
    private static final char[] NAMES = {'K', 'M', 'G', 'T', 'P', 'E'};
    private static final DecimalFormat DF = new DecimalFormat("##0");
    private static final DecimalFormat DFS = new DecimalFormat("##0.0");
    private static final DecimalFormat DFT = new DecimalFormat("00");
    private static final DateFormat DT = DateFormat.getDateTimeInstance();
    private static final DateFormat DTT = DateFormat.getTimeInstance();

    /**
     * Hide the constructor.
     */
    private PrettyFormat() {
    }

    /**
     * Return a human readable version of the input number, with
     * an extra letter to denote K/M/G/T/P/E. The number is scaled
     * by 1024 as many times as necessary.
     *
     * @param l The input number to be scaled
     *
     * @return A scaled textual representation of the input value
     */
    public static String memscale(Long l) {
	return (l == null) ? memscale(0.0) : memscale(l.longValue());
    }

    /**
     * Return a human readable version of the input number, with
     * an extra letter to denote K/M/G/T/P/E. The number is scaled
     * by 1024 as many times as necessary.
     *
     * @param l The input number to be scaled
     *
     * @return A scaled textual representation of the input value
     */
    public static String memscale(long l) {
	return memscale((double) l);
    }

    /**
     * Return a human readable version of the input number, with
     * an extra letter to denote K/M/G/T/P/E. The number is scaled
     * by 1024 as many times as necessary.
     *
     * @param l The input number to be scaled
     *
     * @return A scaled textual representation of the input value
     */
    public static String memscale(double l) {
	double lvalue = l;
	/*
	 * The input is in K, so start from there
	 */
	int i = 0;
	while (lvalue > KMAX && i < 5) {
	    lvalue = lvalue / KSCALE;
	    i++;
	}
	return DF.format(lvalue) + NAMES[i];
    }

    /**
     * Return a human readable version of the elapsed time.
     *
     * @param d The input time to be scaled
     *
     * @return A scaled textual representation of the input time
     */
    public static String timescale(Double d) {
	return (d == null) ? timescale(0.0) : timescale(d.doubleValue());
    }

    /**
     * Return a human readable version of the elapsed time.
     *
     * @param d The input time to be scaled
     *
     * @return A scaled textual representation of the input time
     */
    public static String timescale(double d) {
	if (d < 10.0) {
	    return DFS.format(d) + "s";
	}
	if (d < 60.0) {
	    return DF.format(d) + "s";
	}
	long secs = (long) d;
	long ssecs = secs % 60;
	long mins = (secs - ssecs) / 60;
	return DF.format(mins) + "m" + DFT.format(ssecs) + "s";
    }

    /**
     * Return a human readable version of the date. We pass the time in
     * seconds.
     *
     * @param l The date or time in seconds to be converted
     *
     * @return A scaled textual representation of the input date
     */
    public static String date(Long l) {
	return (l == null) ? "-" : date(l.longValue());
    }

    /**
     * Return a human readable version of the date. We pass the time in
     * seconds.
     *
     * @param l The date or time in seconds to be converted
     *
     * @return A scaled textual representation of the input date
     */
    public static String date(long l) {
	long then = l * 1000;
	return System.currentTimeMillis() - then < 86400000
	    ? DTT.format(new Date(then))
	    : DT.format(new Date(then));
    }
}
