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

import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.Timer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import uk.co.petertribble.jproc.api.JProc;
import uk.co.petertribble.jproc.api.JProcess;
import uk.co.petertribble.jproc.api.NoSuchProcessException;
import uk.co.petertribble.jproc.util.PrettyFormat;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * A tabular representation of a process.
 *
 * @author Peter Tribble
 */
public final class ProcessTable extends JTable implements ActionListener {

    private static final long serialVersionUID = 1L;

    /**
     * The underlying data model.
     */
    private final ProcessTableModel ptm;
    /**
     * A custom renderer for sizes.
     */
    private final DefaultTableCellRenderer sizeRenderer;
    /**
     * A custom renderer for times.
     */
    private final DefaultTableCellRenderer timeRenderer;

    /**
     * A Timer to update the display in a loop.
     */
    private Timer timer;
    /**
     * The update interval for the table.
     */
    private int delay;

    /**
     * Create a new ProcessTable.
     *
     * @param jproc a JProc object to query for process information
     * @param process the JProcess to display
     * @param interval the initial update interval in seconds
     */
    public ProcessTable(final JProc jproc, final JProcess process,
			final int interval) {
	delay = interval * 1000;
	ptm = new ProcessTableModel(jproc, process);
	setModel(ptm);
	sizeRenderer = new SizeRenderer();
	sizeRenderer.setHorizontalAlignment(JLabel.RIGHT);
	timeRenderer = new TimeRenderer();
	timeRenderer.setHorizontalAlignment(JLabel.RIGHT);
	update();
	startLoop();
    }

    /**
     * Start the loop that updates the Jprocesses regularly.
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
     * Stop the loop that updates the Jprocesses.
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
	    ptm.updateProcess();
	} catch (NoSuchProcessException nspe) {
	    stopLoop();
	    setModel(new ProcessExitedTableModel());
	}
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
	update();
    }

    /**
     * A custom renderer that displays size information in human readable
     * format.
     */
    public final class SizeRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;
	@Override
	public void setValue(final Object value) {
	    // We know it's a Long, we wrote the model, but to be safe
	    setText(value instanceof Long
		    ? PrettyFormat.memscale((Long) value) : "");
	}
    }

    /**
     * A custom renderer that displays time information in human readable
     * format.
     */
    public final class TimeRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;
	@Override
	public void setValue(final Object value) {
	    // We know it's a Long, we wrote the model, but to be safe
	    setText(value instanceof Double
		    ? PrettyFormat.timescale((Double) value) : "");
	}
    }

    @Override
    public TableCellRenderer getCellRenderer(final int row, final int col) {
	if ((row == 1 || row == 2) && (col == 1 || col == 2)) {
	    return sizeRenderer;
	}
	if ((row == 3 || row == 4) && (col == 1 || col == 2)) {
	    return timeRenderer;
	}
	return super.getCellRenderer(row, col);
    }
}
