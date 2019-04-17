package com.baselet.element.facet.common;

import com.baselet.control.constants.FacetConstants;
import com.baselet.element.facet.FirstRunKeyValueFacet;
import com.baselet.element.facet.PropertiesParserState;
import com.baselet.diagram.draw.helper.ColorOwn;

public class ActiveFacet extends FirstRunKeyValueFacet {

	public static final ActiveFacet INSTANCE = new ActiveFacet();

	private ActiveFacet() {}

	public static final String KEY = "active";

	@Override
	public KeyValue getKeyValue() {
		return new KeyValue(KEY, false, "1", "to mark active element");
	}

	@Override
	public void handleValue(String value, PropertiesParserState state) {
		state.setFacetResponse(ActiveFacet.class, value);
		if(value.length()>0 && !value.equals("0")) {
			state.getDrawer().setForegroundColor(ColorOwn.ACTIVE_FG);
		}
	}
}
