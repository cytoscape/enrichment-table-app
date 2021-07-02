package org.nrnb.gsoc.enrichment.ui;

import org.cytoscape.application.swing.CytoPanelComponent2;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.events.RowsSetEvent;
import org.cytoscape.model.events.RowsSetListener;
import org.cytoscape.model.events.SelectedNodesAndEdgesEvent;
import org.cytoscape.model.events.SelectedNodesAndEdgesListener;
import org.cytoscape.util.swing.CyColorPaletteChooserFactory;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EnrichmentCytoPanel extends JPanel
        implements CytoPanelComponent2, ListSelectionListener, ActionListener, RowsSetListener, TableModelListener, SelectedNodesAndEdgesListener {
    EnrichmentTableModel tableModel;
    final CyColorPaletteChooserFactory colorChooserFactory;
    private static final Icon chartIcon = new ImageIcon(
            EnrichmentCytoPanel.class.getResource("/images/chart20.png"));
    final Font iconFont;

    public EnrichmentCytoPanel(LayoutManager layout, boolean isDoubleBuffered, CyColorPaletteChooserFactory colorChooserFactory, Font iconFont) {
        super(layout, isDoubleBuffered);
        this.colorChooserFactory = colorChooserFactory;
        this.iconFont = iconFont;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void valueChanged(ListSelectionEvent e) {

    }

    @Override
    public void tableChanged(TableModelEvent e) {

    }

    @Override
    public String getIdentifier() {
        return null;
    }

    @Override
    public Component getComponent() {
        return null;
    }

    @Override
    public CytoPanelName getCytoPanelName() {
        return null;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public void handleEvent(RowsSetEvent e) {

    }

    @Override
    public void handleEvent(SelectedNodesAndEdgesEvent event) {

    }
}
