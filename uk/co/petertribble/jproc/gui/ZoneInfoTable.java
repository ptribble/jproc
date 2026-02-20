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
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import uk.co.petertribble.jproc.api.JProc;
import uk.co.petertribble.jproc.api.JProcessFilter;
import uk.co.petertribble.jproc.util.PrettyFormat;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * A panel showing process information rather like top, aggregated by zone.
 *
 * @author Peter Tribble
 */
public final class ZoneInfoTable extends JTable implements ActionListener {

    private static final long serialVersionUID = 1L;

    /**
     * The JProc to query for data.
     */
    JProc jproc;
    /**
     * The underlying data model.
     */
    ZoneInfoTableModel ftm;
    /**
     * A custom renderer for sizes.
     */
    private final DefaultTableCellRenderer sizeColRenderer;
    /**
     * A custom renderer for times.
     */
    private final DefaultTableCellRenderer timeColRenderer;

    /**
     * A Timer to update the display in a loop.
     */
    private Timer timer;
    /**
     * The update interval for the table.
     */
    int interval;

    /**
     * Create a new ZoneInfoTable.
     *
     * @param njproc a JProc object to query for process information
     * @param jpf a filter defining the processes to be shown
     * @param ninterval the initial update interval, in seconds
     */
    public ZoneInfoTable(final JProc njproc, final JProcessFilter jpf,
			 final int ninterval) {
	jproc = njproc;
	interval = ninterval;
	setLayout(new BorderLayout());

	ftm = new ZoneInfoTableModel(jproc, jpf);
	setModel(ftm);
	setAutoCreateRowSorter(true);

	/*
	 * Create modified cell renderers for the memory, time, and start
	 * columns
	 */
	sizeColRenderer =
	    new DefaultTableCellRenderer() {
		private static final long serialVersionUID = 1L;
		@Override
		public void setValue(final Object value) {
		    // We know it's a Long, we wrote the model, but to be safe
		    setText(value instanceof Long
			    ? PrettyFormat.memscale((Long) value) : "");
	    }
	};
	sizeColRenderer.setHorizontalAlignment(JLabel.RIGHT);
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
	    getColumn("TIME").setCellRenderer(timeColRenderer);
	} catch (IllegalArgumentException iae) { }
	try {
	    getColumn("SZ").setCellRenderer(sizeColRenderer);
	} catch (IllegalArgumentException iae) { }
	try {
	    getColumn("RSS").setCellRenderer(sizeColRenderer);
	} catch (IllegalArgumentException iae) { }
    }

    /**
     * Only show processes belonging to this user.
     *
     * @param uid The user id to show.
     */
    public void setUser(final int uid) {
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
     * @param ninterval the desired delay, in seconds
     */
    public void setDelay(final int ninterval) {
	if (interval <= 0) {
	    stopLoop();
	} else {
	    interval = ninterval;
	    if (timer != null) {
		timer.setDelay(interval * 1000);
	    }
	}
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
	ftm.updateJprocess();
    }

    /**
     * Inner class to handle mouse popups.
     */
    class PopupListener extends MouseAdapter {

	@Override
	public void mousePressed(final MouseEvent e) {
	    showPopup(e);
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
	    showPopup(e);
	}

	// show processes for zone in this row
	private void showPopup(final MouseEvent e) {
	    if (e.isPopupTrigger()) {
		int uid = ftm.getZone(convertRowIndexToModel(
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
	    public void actionPerformed(final ActionEvent e) {
		new ZoneProcessInfoFrame(jproc, zoneid, interval);
	    }
	});
	jpm.add(showChartItem);
	return jpm;
    }
}
