package com.baselet.standalone.gui;

import java.awt.event.KeyEvent;
import java.util.Collection;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import com.baselet.control.constants.MenuConstants;
import com.baselet.diagram.DiagramHandler;
import com.baselet.diagram.PaletteHandler;
import com.baselet.element.interfaces.GridElement;
import com.baselet.gui.menu.MenuFactorySwing;

public class MenuBuilder {

	private MenuFactorySwing menuFactory;
	private JMenu editMenu;
	private JMenuItem editUndo;
	private JMenuItem editRedo;
	private JMenuItem editDelete;
	private JMenuItem editSelectAll;
	private JMenuItem editGroup;
	private JMenuItem editUngroup;
	private JMenuItem editCut;
	private JMenuItem editCopy;
	private JMenuItem editPaste;

	public JMenuBar createMenu(JPanel searchPanel, JPanel zoomPanel /*, JToggleButton mailButton*/) {
		/*********** CREATE MENU *****************/
		JMenuBar menu = new JMenuBar();
		menuFactory = MenuFactorySwing.getInstance();

		JMenu fileMenu = new JMenu(MenuConstants.FILE);
		fileMenu.setMnemonic(KeyEvent.VK_F);
		fileMenu.add(menuFactory.createNew());
		fileMenu.add(menuFactory.createOpen());
		fileMenu.add(menuFactory.createClose());
		fileMenu.add(menuFactory.createRecentFiles());
		fileMenu.addSeparator();
		fileMenu.add(menuFactory.createSave());
		fileMenu.add(menuFactory.createSaveAs());
		fileMenu.add(menuFactory.createExportAs());
		fileMenu.addSeparator();
		fileMenu.add(menuFactory.createEditCurrentPalette());
		fileMenu.addSeparator();
		fileMenu.add(menuFactory.createOptions());
		fileMenu.addSeparator();
		fileMenu.add(menuFactory.createPrint());
		fileMenu.addSeparator();
		fileMenu.add(menuFactory.createExit());
		menu.add(fileMenu);

		editMenu = new JMenu(MenuConstants.EDIT);
		editMenu.setMnemonic(KeyEvent.VK_E);
		editMenu.add(editUndo = menuFactory.createUndo());
		editMenu.add(editRedo = menuFactory.createRedo());
		editMenu.add(editDelete = menuFactory.createDelete());
		editMenu.addSeparator();
		editMenu.add(editSelectAll = menuFactory.createSelectAll());
		editMenu.add(editGroup = menuFactory.createGroup());
		editMenu.add(editUngroup = menuFactory.createUngroup());
		editMenu.addSeparator();
		editMenu.add(editCopy = menuFactory.createCopy());
		editMenu.add(editCut = menuFactory.createCut());
		editMenu.add(editPaste = menuFactory.createPaste());
		menu.add(editMenu);
		editDelete.setEnabled(false);
		editGroup.setEnabled(false);
		editCut.setEnabled(false);
		editPaste.setEnabled(false);
		editUngroup.setEnabled(false);

		// Custom Element Menu
		// Help Menu
		JMenu helpMenu = new JMenu(MenuConstants.HELP);
		helpMenu.setMnemonic(KeyEvent.VK_H);
		helpMenu.add(menuFactory.createOnlineHelp());
		helpMenu.add(menuFactory.createOnlineSampleDiagrams());
		helpMenu.add(menuFactory.createVideoTutorials());
		helpMenu.addSeparator();
		helpMenu.add(menuFactory.createProgramHomepage());
		helpMenu.add(menuFactory.createRateProgram());
		helpMenu.addSeparator();
		helpMenu.add(menuFactory.createAboutProgram());
		menu.add(helpMenu);

		menu.add(searchPanel);
		menu.add(zoomPanel);
		return menu;
	}

	public void elementsSelected(Collection<GridElement> selectedElements) {
		if (selectedElements.isEmpty()) {
			editDelete.setEnabled(false);
			editGroup.setEnabled(false);
			editCut.setEnabled(false);
			// menu_edit_copy must remain enabled even if no entity is selected to allow the export of the full diagram to the system clipboard.
		}
		else {
			editDelete.setEnabled(true);
			editCut.setEnabled(true);

			boolean allElementsInGroup = true;
			for (GridElement e : selectedElements) {
				if (e.getGroup() == null) {
					allElementsInGroup = false;
				}
			}
			editUngroup.setEnabled(allElementsInGroup);
			editGroup.setEnabled(!allElementsInGroup && selectedElements.size() > 1);
		}
	}

	public void enablePasteMenuEntry() {
		editPaste.setEnabled(true);
	}

	public void updateGrayedOutMenuItems(DiagramHandler handler) {
		// These menuitems only get changed if this is not the palette or custompreview
		if (!(handler instanceof PaletteHandler)) {
			menuFactory.updateDiagramDependendComponents();

		}

		// The menu_edit menuitems always work with the actual selected diagram (diagram, palette or custompreview), therefore we change it everytime
		if (handler == null || handler.getDrawPanel().getGridElements().isEmpty()) {
			editCopy.setEnabled(false);
			editSelectAll.setEnabled(false);
		}
		else {
			editMenu.setEnabled(true); // must be set to enabled explicitely because it could be deactivated from CustomPreview
		}

		if (handler == null || !handler.getController().isUndoable()) {
			editUndo.setEnabled(false);
		}
		else {
			editUndo.setEnabled(true);
		}
		if (handler == null || !handler.getController().isRedoable()) {
			editRedo.setEnabled(false);
		}
		else {
			editRedo.setEnabled(true);
		}
	}
}
