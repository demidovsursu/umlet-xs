package com.baselet.element.facet.specific;

import java.util.Locale;

import com.baselet.control.enums.ElementStyle;
import com.baselet.element.facet.FirstRunKeyValueFacet;
import com.baselet.element.facet.PropertiesParserState;

public class GoToFacet extends FirstRunKeyValueFacet {

	public static final GoToFacet INSTANCE = new GoToFacet();

	private GoToFacet() {}

	@Override
	public KeyValue getKeyValue() {
		return new KeyValue("goto", false, "", "name of element to go");
	}

	@Override
	public void handleValue(String value, PropertiesParserState state) {
		state.setFacetResponse(GoToFacet.class, value);
	}

}
