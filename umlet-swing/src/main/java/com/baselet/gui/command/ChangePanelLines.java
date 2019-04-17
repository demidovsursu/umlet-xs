package com.baselet.gui.command;

import java.util.List;
import java.util.HashSet;

import com.baselet.control.Main;
import com.baselet.diagram.DiagramHandler;
import com.baselet.element.interfaces.GridElement;
import com.baselet.gui.CurrentGui;
import com.baselet.gui.pane.OwnSyntaxPane;

public class ChangePanelLines extends Command {
	public static class ChangeElementItem {
		public GridElement element=null;
		public int index=0;
		public String key=null;
		public String newValue=null;
		public String oldValue=null;
		public ChangeElementItem(GridElement e, int i, String k, String nv, String ov) {
			element=e;
			index=i;
			key=k;
			newValue=nv;
			oldValue=ov;
		}
	};
	private List<ChangeElementItem> changes;
	private HashSet<GridElement> updElements;

	public ChangePanelLines(List<ChangeElementItem> changes) {
		this.changes=changes;
		updElements=new HashSet<GridElement>();
		for(ChangeElementItem c: changes) {
			List<String> pa=c.element.getPanelAttributesAsList();
			if(c.index>=0 && c.index<pa.size())
				updElements.add(c.element);
		}
	}

	@Override
	public void execute(DiagramHandler handler) {
		super.execute(handler);
		for(ChangeElementItem c: changes) {
			List<String> pa=c.element.getPanelAttributesAsList();
			if(c.index>=0 && c.index<pa.size()) {
				if(c.key!=null)
					pa.set(c.index,c.key+"="+c.newValue);
				else
					pa.set(c.index,c.newValue);
			}
		}
		for(GridElement e:updElements)
			e.updateModelFromText();
		handler.getDrawPanel().repaint();
	}

	@Override
	public void undo(DiagramHandler handler) {
		super.undo(handler);
		for(ChangeElementItem c: changes) {
			List<String> pa=c.element.getPanelAttributesAsList();
			if(c.index>=0 && c.index<pa.size()) {
				if(c.key!=null)
					pa.set(c.index,c.key+"="+c.oldValue);
				else
					pa.set(c.index,c.oldValue);
			}
		}
		for(GridElement e:updElements)
			e.updateModelFromText();
		handler.getDrawPanel().repaint();
	}

}
