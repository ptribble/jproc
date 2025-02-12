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

    private static final long serialVersionUID = 1L;

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
		private static final long serialVersionUID = 1L;
		@Override
		public void setValue(Object value) {
		    // We know it's a Long, we wrote the model, but to be safe
		    setText(value instanceof Long
			    ? PrettyFormat.memscale((Long) value) : "");
	    }
	};
	sizeColumnRenderer.setHorizontalAlignment(JLabel.RIGHT);
	timeColumnRenderer =
	    new DefaultTableCellRenderer() {
		private static final long serialVersionUID = 1L;
		@Override
		public void setValue(Object value) {
		    // We know it's a Double, we wrote the model
		    setText(value instanceof Double
			    ? PrettyFormat.timescale((Double) value) : "");
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
     * messing with the columns destroys the custom renderers, so we
     * need to add them back.
     *
     * If a column is removed then it isn't shown and getColumn throws an
     * IllegalArgumentException. That doesn't matter as we don't need the
     * setCellRenderer call to succeed if the column isn't visible.
     */
    private void setRenderers() {
	try {
	    getColumn("TIME").setCellRenderer(timeColumnRenderer);
	} catch (IllegalArgumentException iae) { }
	try {
	    getColumn("SZ").setCellRenderer(sizeColumnRenderer);
	} catch (IllegalArgumentException iae) { }
	try {
	    getColumn("RSS").setCellRenderer(sizeColumnRenderer);
	} catch (IllegalArgumentException iae) { }
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
		timer = new Timer(interval * 1000, this);
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
		timer.setDelay(interval * 1000);
	    }
	}
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	ftm.updateJprocess();
    }

    /**
     * Inner class to handle mouse popups.
     */
    class PopupListener extends MouseAdapter {

	@Override
	public void mousePressed(MouseEvent e) {
	    showPopup(e);
	}

	@Override
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
	    @Override
	    public void actionPerformed(ActionEvent e) {
		new ZoneProcessInfoFrame(jproc, zoneid, interval);
	    }
	});
	jpm.add(showChartItem);
	return jpm;
    }
}
