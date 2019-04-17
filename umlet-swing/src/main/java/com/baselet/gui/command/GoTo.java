package com.baselet.gui.command;

import java.awt.Color;
import java.awt.Point;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.baselet.control.basics.Converter;
import com.baselet.control.basics.geom.Rectangle;
import com.baselet.diagram.DiagramHandler;
import com.baselet.diagram.DrawPanel;
import com.baselet.diagram.SelectorOld;
import com.baselet.element.interfaces.GridElement;

public class GoTo extends Command {

	private int dx=0;
	private int dy=0;
	private GridElement e=null;
	
	public GoTo(GridElement e) {
		this.e = e;
	}
	private void moveTo(DrawPanel d, int dx, int dy) {
		if (dx != 0 || dy != 0) {
			for (GridElement e : d.getGridElements()) {
				Rectangle r=e.getRectangle();
				e.setRectangle(new Rectangle(r.x+dx,r.y+dy,r.width,r.height));
			}
		}
	}
	
	@Override
	public void execute(DiagramHandler handler) {
		super.execute(handler);
		DrawPanel d = handler.getDrawPanel();
		if(e!=null) {
			Rectangle panelview = Converter.convert(d.getVisibleRect());
			Rectangle r=e.getRectangle();
			dx=panelview.x + handler.getGridSize() - r.x;
			dy=panelview.y + handler.getGridSize() - r.y;
			if (dx!=0 || dy!=0) {
				moveTo(d, dx, dy);
				d.updatePanelAndScrollbars();
			}
		}
	}

	@Override
	public void undo(DiagramHandler handler) {
		super.undo(handler);
		if (dx!=0 || dy!=0) {
			DrawPanel d = handler.getDrawPanel();
			moveTo(d, -dx, -dy);
			d.updatePanelAndScrollbars();
		}
	}
}
