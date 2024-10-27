/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at usr/src/OPENSOLARIS.LICENSE
 * or http://www.opensolaris.org/os/licensing.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at usr/src/OPENSOLARIS.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

package uk.co.petertribble.jproc.util;

import java.text.DecimalFormat;
import java.text.DateFormat;
import java.util.Date;

/**
 * A class for generating human-readable output.
 *
 * @author Peter Tribble
 */
public final class PrettyFormat {

    private static final double KSCALE = 1024.0;
    private static final double KMAX = 10000.0;
    private static final char[] NAMES = { 'K', 'M', 'G', 'T', 'P', 'E' };
    private static final DecimalFormat DF = new DecimalFormat("##0");
    private static final DecimalFormat DFS = new DecimalFormat("##0.0");
    private static final DecimalFormat DFT = new DecimalFormat("00");
    private static final DateFormat DT = DateFormat.getDateTimeInstance();
    private static final DateFormat DTT = DateFormat.getTimeInstance();

    /*
     * Hide the constructor.
     */
    private PrettyFormat() {
    }

    /**
     * Return a human readable version of the input number, with
     * an extra letter to denote k/m/g/t/p/e. The number is scaled
     * by 1024 as many times as necessary.
     */
    public static String memscale(Long l) {
	return (l == null) ? memscale(0.0) : memscale(l.longValue());
    }

    /**
     * Return a human readable version of the input number, with
     * an extra letter to denote k/m/g/t/p/e. The number is scaled
     * by 1024 as many times as necessary.
     */
    public static String memscale(long l) {
	return memscale((double) l);
    }

    /**
     * Return a human readable version of the input number, with
     * an extra letter to denote K/M/G/T/P/E. The number is scaled
     * by 1024 as many times as necessary.
     */
    public static String memscale(double l) {
	double lvalue = l;
	/*
	 * The input is in K, so start from there
	 */
	int i = 0;
	while (lvalue > KMAX && i < 5) {
	    lvalue = lvalue/KSCALE;
	    i++;
	}
	return DF.format(lvalue) + NAMES[i];
    }

    /**
     * Return a human readable version of the elapsed time.
     */
    public static String timescale(Double d) {
	return (d == null) ? timescale(0.0) : timescale(d.doubleValue());
    }

    /**
     * Return a human readable version of the elapsed time.
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
	long mins = (secs - ssecs) /60;
	return DF.format(mins) + "m" + DFT.format(ssecs) + "s";
    }

    /**
     * Return a human readable version of the date.
     */
    public static String date(Long l) {
	return (l == null) ? "-" : date(l.longValue());
    }

    /**
     * Return a human readable version of the date. We pass the time in
     * seconds.
     */
    public static String date(long l) {
	long then = l*1000;
	return System.currentTimeMillis() - then < 86400000
	    ? DTT.format(new Date(then))
	    : DT.format(new Date(then));
    }
}
