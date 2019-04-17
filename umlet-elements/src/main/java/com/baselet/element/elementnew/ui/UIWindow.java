package com.baselet.element.elementnew.ui;

import java.util.List;

import com.baselet.control.enums.ElementId;
import com.baselet.control.enums.AlignHorizontal;
import com.baselet.element.NewGridElement;
import com.baselet.element.facet.Facet;
import com.baselet.element.facet.PropertiesParserState;
import com.baselet.element.facet.Settings;
import com.baselet.element.facet.common.SeparatorLineFacet;
import com.baselet.element.facet.specific.TabFacet;
import com.baselet.element.settings.SettingsManualResizeTop;
import com.baselet.diagram.draw.DrawHandler;
import com.baselet.element.draw.DrawHelper;

public class UIWindow extends NewGridElement {

	@Override
	protected Settings createSettings() {
		return new SettingsManualResizeTop() {
			@Override
			protected List<Facet> createFacets() {
				return listOf(super.createFacets(), TabFacet.INSTANCE_NOGRID, SeparatorLineFacet.INSTANCE);
			}
			@Override
			public AlignHorizontal getHAlign() {
				return AlignHorizontal.LEFT;
			}
		};
	}

	@Override
	public ElementId getId() {
		return ElementId.UIWindow;
	}

	@Override
	protected void drawCommonContent(PropertiesParserState state) {
		DrawHandler drawer = state.getDrawer();
		drawer.drawRectangle(0, 0, getRealSize().getWidth(), getRealSize().getHeight());
	}
}
