package com.baselet.element.facet.common;

import com.baselet.control.constants.FacetConstants;
import com.baselet.element.facet.FirstRunKeyValueFacet;
import com.baselet.element.facet.PropertiesParserState;

public class NameFacet extends FirstRunKeyValueFacet {

	public static final NameFacet INSTANCE = new NameFacet();

	private NameFacet() {}

	@Override
	public KeyValue getKeyValue() {
		return new KeyValue("name", false, "", "name of element");
	}

	@Override
	public void handleValue(String value, PropertiesParserState state) {
		state.setFacetResponse(NameFacet.class, value);
	}

}
