package com.baselet.element.elementnew;

import java.util.List;

import com.baselet.control.enums.ElementId;
import com.baselet.element.NewGridElement;
import com.baselet.element.facet.Facet;
import com.baselet.element.facet.PropertiesParserState;
import com.baselet.element.facet.Settings;
import com.baselet.element.facet.common.SeparatorLineWithHalignChangeFacet;
import com.baselet.element.facet.specific.TabFacet;
import com.baselet.element.facet.specific.TabFacet.TabFacetResponse;
import com.baselet.diagram.draw.DrawHandler;
import com.baselet.element.draw.DrawHelper;
import com.baselet.element.settings.SettingsManualResizeTop;

public class Entity extends NewGridElement {

	@Override
	protected Settings createSettings() {
		return new SettingsManualResizeTop() {
			@Override
			protected List<Facet> createFacets() {
				return listOf(super.createFacets(), SeparatorLineWithHalignChangeFacet.INSTANCE, TabFacet.INSTANCE);
			}
		};
	}

	@Override
	public ElementId getId() {
		return ElementId.Entity;
	}

	@Override
	protected void drawCommonContent(PropertiesParserState state) {
		DrawHandler drawer = state.getDrawer();
		drawer.drawRectangle(0, 0, getRealSize().getWidth(), getRealSize().getHeight());
	}

}
