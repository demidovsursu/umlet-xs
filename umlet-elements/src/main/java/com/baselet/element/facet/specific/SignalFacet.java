package com.baselet.element.facet.specific;

import java.util.Locale;

import com.baselet.control.enums.ElementStyle;
import com.baselet.element.facet.FirstRunKeyValueFacet;
import com.baselet.element.facet.PropertiesParserState;

public class SignalFacet extends FirstRunKeyValueFacet {

	public static final SignalFacet INSTANCE = new SignalFacet();

	private SignalFacet() {}

	@Override
	public KeyValue getKeyValue() {
		return new KeyValue("signal", false, "", "name of signal");
	}

	@Override
	public void handleValue(String value, PropertiesParserState state) {
		state.setFacetResponse(SignalFacet.class, value);
	}

}
