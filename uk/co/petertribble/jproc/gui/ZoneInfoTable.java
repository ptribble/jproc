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
import java.awt.event.*;
import uk.co.petertribble.jproc.api.JProc;
import uk.co.petertribble.jproc.api.JProcessFilter;
import uk.co.petertribble.jingle.TableSorter;
import uk.co.petertribble.jproc.util.PrettyFormat;
import javax.swing.table.*;

/**
 * A panel showing process information rather like top, aggregated by zone.
 *
 * @author Peter Tribble
 */
public class ZoneInfoTable extends JTable implements ActionListener {

    JProc jproc;
    ZoneInfoTableModel ftm;
    TableSorter sortedModel;
    private DefaultTableCellRenderer sizeColumnRenderer;
    private DefaultTableCellRenderer timeColumnRenderer;

    private Timer timer;
    int interval;

    /**
     * Create a new ZoneInfoTable.
     *
     * @param jproc a JProc object to query for process information
     * @param jpf a filter defining the processes to be shown
     * @param interval the initial update interval, in seconds
     */
    public ZoneInfoTable(JProc jproc, JProcessFilter jpf, int interval) {
	this.jproc = jproc;
	this.interval = interval;
	setLayout(new BorderLayout());

	ftm = new ZoneInfoTableModel(jproc, jpf);
	sortedModel = new TableSorter(ftm);
	setModel(sortedModel);

	/*
	 * Create modified cell renderers for the memory, time, and start
	 * columns
	 */
	sizeColumnRenderer =
	    new DefaultTableCellRenderer() {
		public void setValue(Object value) {
		    // We know it's a Long, we wrote the model, but to be safe
		    setText(value instanceof Long ?
			    PrettyFormat.memscale((Long) value) : "");
	    }
	};
	sizeColumnRenderer.setHorizontalAlignment(JLabel.RIGHT);
	timeColumnRenderer =
	    new DefaultTableCellRenderer() {
		public void setValue(Object value) {
		    // We know it's a Double, we wrote the model
		    setText(value instanceof Double ?
			    PrettyFormat.timescale((Double) value) : "");
	    }
	};
	timeColumnRenderer.setHorizontalAlignment(JLabel.RIGHT);

	setRenderers();

	sortedModel.setTableHeader(getTableHeader());

	addMouseListener((MouseListener) new PopupListener());

	// set up for regular updates
	ftm.updateJprocess();
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
	    getColumn("TIME").setCellRenderer(timeColumnRenderer);
	} catch (IllegalArgumentException iae) {}
	try {
	    getColumn("SZ").setCellRenderer(sizeColumnRenderer);
	} catch (IllegalArgumentException iae) {}
	try {
	    getColumn("RSS").setCellRenderer(sizeColumnRenderer);
	} catch (IllegalArgumentException iae) {}
    }

    /**
     * Only show processes belonging to this user.
     *
     * @param uid The user id to show.
     */
    public void setUser(int uid) {
	ftm.setUser(uid);
    }

    /**
     * Unset user filtering, so that all users are shown.
     */
    public void unSetUser() {
	ftm.unSetUser();
    }

    /**
     * Start the loop that updates the Jprocesses regularly.
     */
    public void startLoop() {
	if (interval > 0) {
	    if (timer == null) {
		timer = new Timer(interval*1000, this);
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
	    this.interval = interval;
	    if (timer != null) {
		timer.setDelay(interval*1000);
	    }
	}
    }

    public void actionPerformed(ActionEvent e) {
	ftm.updateJprocess();
    }

    /**
     * Inner class to handle mouse popups.
     */
    class PopupListener extends MouseAdapter {

	public void mousePressed(MouseEvent e) {
	    showPopup(e);
	}

	public void mouseReleased(MouseEvent e) {
	    showPopup(e);
	}

	// show processes for zone in this row
	private void showPopup(MouseEvent e) {
	    if (e.isPopupTrigger()) {
		int uid = ftm.getZone(sortedModel.modelIndex(
				rowAtPoint(e.getPoint())));
		createPopupMenu(uid).show(e.getComponent(), e.getX(), e.getY());
	    }
	}
    }

    /**
     * Create a popup menu that can launch a window showing processes belonging
     * to the given zone.
     *
     * @param zoneid the zone id
     *
     * @return a JPopupMenu
     */
    public JPopupMenu createPopupMenu(final int zoneid) {
	JPopupMenu jpm = new JPopupMenu();
	JMenuItem showChartItem = new JMenuItem(
		JProcResources.getString("ZONE.SHOW") + " "
				+ jproc.getZoneName(zoneid));
	showChartItem.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		new ZoneProcessInfoFrame(jproc, zoneid, interval);
	    }
	});
	jpm.add(showChartItem);
	return jpm;
    }
}
