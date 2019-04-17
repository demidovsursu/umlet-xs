package com.baselet.diagram;

import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.JMenuItem;

import com.baselet.gui.menu.MenuFactorySwing;
import static com.baselet.control.constants.MenuConstants.START_EMU;
import static com.baselet.control.constants.MenuConstants.STOP_EMU;
import com.baselet.control.config.Config;
import com.baselet.diagram.CurrentDiagram;
import com.baselet.diagram.DiagramHandler;

@SuppressWarnings("serial")
public class DiagramPopupMenu extends JPopupMenu {

	public DiagramPopupMenu(boolean extendedForStandaloneGUI) {
		final MenuFactorySwing menuFactory = MenuFactorySwing.getInstance();
		final JMenuItem emuItem=menuFactory.createStartStop();
		add(menuFactory.createPaste());
		add(emuItem);
		if (extendedForStandaloneGUI) { // Extended is true for StandaloneGUI
			add(menuFactory.createNew());
			add(menuFactory.createOpen());
			add(menuFactory.createClose());
			add(menuFactory.createRecentFiles());
			add(menuFactory.createSave());
			add(menuFactory.createSaveAs());
		}
		add(menuFactory.createExportAs());
		add(menuFactory.createPrint());

		addPopupMenuListener(new PopupMenuListener() {
			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				menuFactory.updateDiagramDependendComponents();
				DiagramHandler actualHandler = CurrentDiagram.getInstance().getDiagramHandler();
				if(actualHandler!=null)
					emuItem.setText(actualHandler.isEmulation()?STOP_EMU:START_EMU);
			}
		});
	}
}
