package com.baselet.element.facet.specific;

import java.util.Locale;

import com.baselet.control.enums.ElementStyle;
import com.baselet.element.facet.FirstRunKeyValueFacet;
import com.baselet.element.facet.PropertiesParserState;

public class BlockTypeFacet extends FirstRunKeyValueFacet {

	public static final BlockTypeFacet INSTANCE = new BlockTypeFacet();

	private BlockTypeFacet() {}

	public static final String KEY = "type";

	@Override
	public KeyValue getKeyValue() {
		return new KeyValue(KEY,
				new ValueInfo("[]", "process"),
				new ValueInfo("//", "input/output"),
				new ValueInfo("()", "terminal"),
				new ValueInfo("[", "comment"),
				new ValueInfo("]", "comment"),
				new ValueInfo("<>", "decision"),
				new ValueInfo("[||]", "predefined process"),
				new ValueInfo("<=>", "setup"),
				new ValueInfo("/\\", "loop begin"),
				new ValueInfo("\\/", "loop end"),
				new ValueInfo("=", "parallel"));
	}

	@Override
	public void handleValue(String value, PropertiesParserState state) {
		state.setFacetResponse(BlockTypeFacet.class, value);
	}

}
