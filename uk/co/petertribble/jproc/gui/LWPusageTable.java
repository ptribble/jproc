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
 * Copyright 2026 Peter Tribble
 *
 */

package uk.co.petertribble.jproc.gui;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.Timer;
import java.awt.BorderLayout;
import uk.co.petertribble.jproc.api.JProc;
import uk.co.petertribble.jproc.api.JProcess;
import uk.co.petertribble.jproc.api.NoSuchProcessException;
import uk.co.petertribble.jproc.util.PrettyFormat;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * A panel showing process information rather like top.
 *
 * @author Peter Tribble
 */
public final class LWPusageTable extends JTable implements ActionListener {

    private static final long serialVersionUID = 1L;

    /**
     * The underlying data model.
     */
    private LWPusageTableModel ftm;
    /**
     * A custom renderer for times.
     */
    private DefaultTableCellRenderer timeColRenderer;
    /**
     * A Timer to update the display in a loop.
     */
    private Timer timer;
    /**
     * The update interval for the table.
     */
    private int delay = 1000;

    /**
     * Create a new LWPusageTable.
     *
     * @param jproc a JProc object to query for process information
     * @param jp the process to be shown
     * @param interval the initial update interval, in seconds
     */
    public LWPusageTable(final JProc jproc, final JProcess jp,
			 final int interval) {
	setLayout(new BorderLayout());

	ftm = new LWPusageTableModel(jproc, jp);
	setModel(ftm);
	setAutoCreateRowSorter(true);

	/*
	 * Create modified cell renderers for the time columns
	 */
	timeColRenderer =
	    new DefaultTableCellRenderer() {
		private static final long serialVersionUID = 1L;
		@Override
		public void setValue(final Object value) {
		    // We know it's a Double, we wrote the model
		    setText(value instanceof Double
			    ? PrettyFormat.timescale((Double) value) : "");
	    }
	};
	timeColRenderer.setHorizontalAlignment(JLabel.RIGHT);
	setRenderers();

	// set up for regular updates
	delay = interval * 1000;
	update();
	startLoop();
    }

    /*
     * This is also called from addColumn and removeColumn as the act of
     * messing with the columns destroys the custom renderers, so we
     * need to add them back.
     *
     * If a column is removed then it isn't shown and getColumn throws an
     * IllegalArgumentException. That doesn't matter as we don't need the
     * setCellRenderer call to succeed if the column isn't visible.
     */
    private void setRenderers() {
	try {
	    getColumn("STIME").setCellRenderer(timeColRenderer);
	} catch (IllegalArgumentException iae) { }
	try {
	    getColumn("UTIME").setCellRenderer(timeColRenderer);
	} catch (IllegalArgumentException iae) { }
	try {
	    getColumn("RTIME").setCellRenderer(timeColRenderer);
	} catch (IllegalArgumentException iae) { }
    }

    /**
     * Return the list of column names.
     *
     * @return a List of column names
     */
    public List<String> columns() {
	return ftm.columns();
    }

    /**
     * Remove a column from the list of items to be displayed.
     *
     * @param s the name of the column to be removed
     */
    public void removeColumn(final String s) {
	ftm.removeColumn(s);
	setRenderers();
    }

    /**
     * Add a column to the list of items to be displayed.
     *
     * @param s the name of the column to be added
     */
    public void addColumn(final String s) {
	ftm.addColumn(s);
	setRenderers();
    }

    /**
     * Start the loop that updates the display regularly.
     */
    public void startLoop() {
	if (delay > 0) {
	    if (timer == null) {
		timer = new Timer(delay, this);
	    }
	    timer.start();
	}
    }

    /**
     * Stop the loop that updates the display.
     */
    public void stopLoop() {
	if (timer != null) {
	    timer.stop();
	}
    }

    /**
     * Set the loop delay to be the specified number of seconds.
     * If a zero or negative delay is requested, stop the updates
     * and remember the previous delay.
     *
     * @param interval the desired delay, in seconds
     */
    public void setDelay(final int interval) {
	if (interval <= 0) {
	    stopLoop();
	} else {
	    delay = interval * 1000;
	    if (timer != null) {
		timer.setDelay(delay);
	    }
	}
    }

    /*
     * Update the underlying model. Upon process exit, the model will throw a
     * NoSuchProcessException. Upon receipt of such an exception, we set our
     * Model to be the special model displaying an informative message.
     */
    private void update() {
	try {
	    ftm.updateJprocess();
	} catch (NoSuchProcessException nspe) {
	    stopLoop();
	    setModel(new ProcessExitedTableModel());
	}
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
	update();
    }
}
