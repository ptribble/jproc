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
import uk.co.petertribble.jproc.api.JProcess;
import uk.co.petertribble.jproc.api.JProcessFilter;
import uk.co.petertribble.jingle.TableSorter;
import uk.co.petertribble.jproc.util.PrettyFormat;
import javax.swing.table.*;
import java.util.List;

/**
 * A panel showing process information rather like top.
 *
 * @author Peter Tribble
 */
public class JPusageTable extends JTable implements ActionListener {

    JProc jproc;
    PSusageTableModel ftm;
    TableSorter sortedModel;
    private DefaultTableCellRenderer timeColumnRenderer;

    private Timer timer;
    int interval;

    /**
     * Create a new JPusageTable.
     *
     * @param jproc a JProc object to query for process information
     * @param jpf a filter defining the processes to be shown
     * @param interval the initial update interval, in seconds
     */
    public JPusageTable(JProc jproc, JProcessFilter jpf, int interval) {
	this.jproc = jproc;
	this.interval = interval;
	setLayout(new BorderLayout());

	ftm = new PSusageTableModel(jproc, jpf);
	sortedModel = new TableSorter(ftm);

	setModel(sortedModel);

	/*
	 * Create modified cell renderers for the time columns
	 */
	timeColumnRenderer =
	    new DefaultTableCellRenderer() {
		@Override
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
     * messing with the columns destroys the custom renderers, so we
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
     * Only show processes belonging to this zone.
     *
     * @param uid The zone id to show.
     */
    public void setZone(int uid) {
	ftm.setZone(uid);
    }

    /**
     * Unset zone filtering, so that all zones are shown.
     */
    public void unSetZone() {
	ftm.unSetZone();
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

	private void showPopup(MouseEvent e) {
	    if (e.isPopupTrigger()) {
		JProcess jp = ftm.getProcess(sortedModel.modelIndex(
				rowAtPoint(e.getPoint())));
		createPopupMenu(jp).show(e.getComponent(), e.getX(), e.getY());
	    }
	}
    }

    /**
     * Create a popup menu allowing the user to show a thread table.
     *
     * @param jp the JProcess to display
     *
     * @return a JPopupMenu
     */
    public JPopupMenu createPopupMenu(final JProcess jp) {
	JPopupMenu jpm = new JPopupMenu();
	JMenuItem showChartItem = new JMenuItem(
		JProcResources.getString("THREAD.MENU") + " " + jp.getPid());
	showChartItem.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		new LWPusageFrame(jproc, jp, interval);
	    }
	});
	jpm.add(showChartItem);
	return jpm;
    }
}
