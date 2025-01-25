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
public class ProcessTable extends JTable implements ActionListener {

    private static final long serialVersionUID = 1L;

    private ProcessTableModel ptm;
    private DefaultTableCellRenderer sizeRenderer;
    private DefaultTableCellRenderer timeRenderer;

    private Timer timer;
    private int delay;

    /**
     * Create a new ProcessTable.
     *
     * @param jproc a JProc object to query for process information
     * @param process the JProcess to display
     * @param interval the initial update interval in seconds
     */
    public ProcessTable(JProc jproc, JProcess process, int interval) {
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
    public void setDelay(int interval) {
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
    public void actionPerformed(ActionEvent e) {
	update();
    }

    /**
     * A custom renderer that displays size information in human readable
     * format.
     */
    public class SizeRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;
	@Override
	public void setValue(Object value) {
	    // We know it's a Long, we wrote the model, but to be safe
	    setText(value instanceof Long
		    ? PrettyFormat.memscale((Long) value) : "");
	}
    }

    /**
     * A custom renderer that displays time information in human readable
     * format.
     */
    public class TimeRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;
	@Override
	public void setValue(Object value) {
	    // We know it's a Long, we wrote the model, but to be safe
	    setText(value instanceof Double
		    ? PrettyFormat.timescale((Double) value) : "");
	}
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int col) {
	if ((row == 1 || row == 2) && (col == 1 || col == 2)) {
	    return sizeRenderer;
	}
	if ((row == 3 || row == 4) && (col == 1 || col == 2)) {
	    return timeRenderer;
	}
	return super.getCellRenderer(row, col);
    }
}
