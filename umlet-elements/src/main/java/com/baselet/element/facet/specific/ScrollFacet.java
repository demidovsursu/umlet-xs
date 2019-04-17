package com.baselet.element.facet.specific;

import java.util.Locale;
import java.util.List;

import com.baselet.control.basics.XValues;
import com.baselet.control.enums.Priority;
import com.baselet.diagram.draw.DrawHandler;

import com.baselet.control.enums.ElementStyle;
import com.baselet.element.facet.FirstRunKeyValueFacet;
import com.baselet.element.facet.PropertiesParserState;

public class ScrollFacet extends FirstRunKeyValueFacet {

	public static final ScrollFacet INSTANCE = new ScrollFacet();

	private ScrollFacet() {}


	public static final String KEY = "scroll";
	public static final int SCROLL_SIZE = 15;

	@Override
	public KeyValue getKeyValue() {
		return new KeyValue(KEY,
				new ValueInfo("h", "horizontal"),
				new ValueInfo("v", "vertical"),
				new ValueInfo("hv", "both"));
	}

	@Override
	public void handleValue(String value, PropertiesParserState state) {
		state.setFacetResponse(ScrollFacet.class, value);
	}
	@Override
	public void parsingFinished(PropertiesParserState state, List<String> handledLines) {
		if (!handledLines.isEmpty()) {
			String s=extractValue(handledLines.get(0));
			if(s.indexOf('v')>=0) state.getBuffer().addToRight(SCROLL_SIZE);
		}
	}

	@Override
	public Priority getPriority() {
		return Priority.LOW; 
	}

}
