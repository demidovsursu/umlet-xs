package com.baselet.element.facet.specific;

import java.util.Locale;
import java.util.Arrays;
import java.util.List;

import com.baselet.control.enums.ElementStyle;
import com.baselet.element.facet.FirstRunKeyValueFacet;
import com.baselet.element.facet.PropertiesParserState;
import com.baselet.control.basics.geom.Dimension;
import com.baselet.diagram.draw.DrawHandler;
import com.baselet.diagram.draw.helper.ColorOwn;

public class FrameFacet extends FirstRunKeyValueFacet {

	public static final FrameFacet INSTANCE = new FrameFacet();

	private FrameFacet() {}

	public static final String KEY = "frame";

	@Override
	public KeyValue getKeyValue() {
		return new KeyValue(KEY,
				new ValueInfo("no", "without frame"));
	}

	@Override
	public void handleValue(String value, PropertiesParserState state) {
	}
	@Override
	public void parsingFinished(PropertiesParserState state, List<String> handledLines) {
		DrawHandler drawer = state.getDrawer();
		Dimension d=state.getGridElementSize();
		if(handledLines.size()==0 || extractValue(handledLines.get(0)).equals("yes")) {
			drawer.drawRectangle(0, 0, d.getWidth(), d.getHeight());
		}
		else {
			ColorOwn oldFg = drawer.getForegroundColor();
			drawer.setForegroundColor(ColorOwn.TRANSPARENT);
			drawer.drawRectangle(0, 0, d.getWidth(), d.getHeight());
			drawer.setForegroundColor(oldFg);
		}
	}
}
