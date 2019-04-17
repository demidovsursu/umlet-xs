package com.baselet.element.facet.customdrawings;

import com.baselet.control.constants.FacetConstants;
import com.baselet.element.facet.FirstRunKeyValueFacet;
import com.baselet.element.facet.PropertiesParserState;

public class CustomStateFacet extends FirstRunKeyValueFacet {

	public static final CustomStateFacet INSTANCE = new CustomStateFacet();

	private CustomStateFacet() {}

	@Override
	public KeyValue getKeyValue() {
		return new KeyValue("state", false, "", "state of element");
	}

	@Override
	public void handleValue(String value, PropertiesParserState state) {
		state.setFacetResponse(CustomStateFacet.class, value);
	}

}
