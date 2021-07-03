package org.nrnb.gsoc.enrichment.ui;

import org.cytoscape.application.swing.CytoPanelComponent2;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.events.RowsSetEvent;
import org.cytoscape.model.events.RowsSetListener;
import org.cytoscape.model.events.SelectedNodesAndEdgesEvent;
import org.cytoscape.model.events.SelectedNodesAndEdgesListener;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.util.swing.CyColorPaletteChooserFactory;
import org.cytoscape.util.swing.IconManager;

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
    final CyServiceRegistrar registrar;

    public EnrichmentCytoPanel(CyServiceRegistrar registrar) {
        this.registrar = registrar;
        this.setLayout(new BorderLayout());
        this.colorChooserFactory = registrar.getService(CyColorPaletteChooserFactory.class);
        IconManager iconManager = registrar.getService(IconManager.class);
        this.iconFont = iconManager.getIconFont(22.0f);
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
