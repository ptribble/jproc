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

package uk.co.petertribble.jproc.gui;

import javax.swing.*;
import java.awt.BorderLayout;
import uk.co.petertribble.jproc.api.*;
import uk.co.petertribble.jingle.TableSorter;
import uk.co.petertribble.jproc.util.PrettyFormat;
import javax.swing.table.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * A panel showing process information rather like top.
 *
 * @author Peter Tribble
 */
public class LWPusageTable extends JTable implements ActionListener {

    private LWPusageTableModel ftm;
    private DefaultTableCellRenderer timeColumnRenderer;
    private Timer timer;
    private int delay = 1000;

    /**
     * Create a new LWPusageTable.
     *
     * @param jproc a JProc object to query for process information
     * @param jp the process to be shown
     * @param interval the initial update interval, in seconds
     */
    public LWPusageTable(JProc jproc, JProcess jp, int interval) {
	setLayout(new BorderLayout());

	ftm = new LWPusageTableModel(jproc, jp);
	TableSorter sortedModel = new TableSorter(ftm);
	setModel(sortedModel);

	/*
	 * Create modified cell renderers for the time columns
	 */
	timeColumnRenderer =
	    new DefaultTableCellRenderer() {
		public void setValue(Object value) {
		    // We know it's a Double, we wrote the model
		    setText((value instanceof Double) ?
			    PrettyFormat.timescale((Double) value) : "");
	    }
	};
	timeColumnRenderer.setHorizontalAlignment(JLabel.RIGHT);
	setRenderers();

	sortedModel.setTableHeader(getTableHeader());

	// set up for regular updates
	delay = interval*1000;
	update();
	startLoop();
    }

    /*
     * This is also called from addColumn and removeColumn as the act of
     * messing with the columns destroys the custom rendererers, so we
     * need to add them back.
     *
     * If a column is removed then it isn't shown and getColumn throws an
     * IllegalArgumentException. That doesn't matter as we don't need the
     * setCellRenderer call to succeed if the column isn't visible.
     */
    private void setRenderers() {
	try {
	    getColumn("STIME").setCellRenderer(timeColumnRenderer);
	} catch (IllegalArgumentException iae) {}
	try {
	    getColumn("UTIME").setCellRenderer(timeColumnRenderer);
	} catch (IllegalArgumentException iae) {}
	try {
	    getColumn("RTIME").setCellRenderer(timeColumnRenderer);
	} catch (IllegalArgumentException iae) {}
    }

    /**
     * Return the list of column names.
     *
     * @return a List of column names
     */
    public List <String> columns() {
	return ftm.columns();
    }

    /**
     * Remove a column from the list of items to be displayed.
     *
     * @param s the name of the column to be removed
     */
    public void removeColumn(String s) {
	ftm.removeColumn(s);
	setRenderers();
    }

    /**
     * Add a column to the list of items to be displayed.
     *
     * @param s the name of the column to be added
     */
    public void addColumn(String s) {
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
    public void setDelay(int interval) {
	if (interval <= 0) {
	    stopLoop();
	} else {
	    delay = interval*1000;
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

    public void actionPerformed(ActionEvent e) {
	update();
    }
}
