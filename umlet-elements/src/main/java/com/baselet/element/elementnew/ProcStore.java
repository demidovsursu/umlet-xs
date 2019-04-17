package com.baselet.element.elementnew;

import java.util.List;

import com.baselet.control.enums.ElementId;
import com.baselet.element.NewGridElement;
import com.baselet.element.facet.Facet;
import com.baselet.element.facet.PropertiesParserState;
import com.baselet.element.facet.Settings;
import com.baselet.element.settings.SettingsBase;
import com.baselet.diagram.draw.DrawHandler;
import com.baselet.element.draw.DrawHelper;
import com.baselet.element.facet.common.GroupFacet;
import com.baselet.element.facet.common.LayerFacet;
import com.baselet.element.facet.common.NameFacet;
import com.baselet.element.facet.common.ActiveFacet;
import com.baselet.element.facet.common.SeparatorLineFacet;
import com.baselet.element.facet.common.TextPrintFacet;

public class ProcStore extends NewGridElement {

	@Override
	protected Settings createSettings() {
		return new SettingsBase() {
			@Override
			protected List<Facet> createFacets() {
				return listOf(LayerFacet.INSTANCE, GroupFacet.INSTANCE, TextPrintFacet.INSTANCE);
			}
		};
	}

	@Override
	public ElementId getId() {
		return ElementId.ProcStore;
	}

	@Override
	protected void drawCommonContent(PropertiesParserState state) {
		DrawHandler drawer = state.getDrawer();
		drawer.drawRectangle(0, 0, getRealSize().getWidth(), getRealSize().getHeight());
	}

}
