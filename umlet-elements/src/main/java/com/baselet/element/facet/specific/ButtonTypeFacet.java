package com.baselet.element.facet.specific;

import java.util.Locale;

import com.baselet.control.enums.ElementStyle;
import com.baselet.element.facet.FirstRunKeyValueFacet;
import com.baselet.element.facet.PropertiesParserState;

public class ButtonTypeFacet extends FirstRunKeyValueFacet {

	public static final ButtonTypeFacet INSTANCE = new ButtonTypeFacet();

	private ButtonTypeFacet() {}

	public static final String KEY = "type";

	@Override
	public KeyValue getKeyValue() {
		return new KeyValue(KEY,
				new ValueInfo("[]", "rectangle"),
				new ValueInfo("{}", "round rectangle"),
				new ValueInfo("()", "round"),
				new ValueInfo("-", "without frame"));
	}

	@Override
	public void handleValue(String value, PropertiesParserState state) {
		state.setFacetResponse(ButtonTypeFacet.class, value);
	}

}
