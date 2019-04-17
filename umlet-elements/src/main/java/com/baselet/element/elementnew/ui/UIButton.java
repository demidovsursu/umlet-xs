package com.baselet.element.elementnew.ui;

import java.util.List;
import java.util.Arrays;

import com.baselet.control.basics.geom.PointDouble;
import com.baselet.control.enums.ElementId;
import com.baselet.element.NewGridElement;
import com.baselet.element.facet.Facet;
import com.baselet.element.facet.PropertiesParserState;
import com.baselet.element.facet.Settings;
import com.baselet.element.facet.common.SeparatorLineFacet;
import com.baselet.element.facet.specific.ButtonTypeFacet;
import com.baselet.element.facet.specific.GoToFacet;
import com.baselet.element.facet.specific.SignalFacet;
import com.baselet.diagram.draw.DrawHandler;
import com.baselet.element.draw.DrawHelper;
import com.baselet.element.settings.SettingsManualresizeCenter;
import com.baselet.element.sticking.StickingPolygon;
import com.baselet.element.sticking.polygon.StickingPolygonGenerator;
import com.baselet.element.sticking.polygon.PointDoubleStickingPolygonGenerator;
import com.baselet.element.sticking.polygon.SimpleStickingPolygonGenerator;

public class UIButton extends NewGridElement {

	@Override
	protected Settings createSettings() {
		return new SettingsManualresizeCenter() {
			@Override
			protected List<Facet> createFacets() {
				return listOf(super.createFacets(), ButtonTypeFacet.INSTANCE, GoToFacet.INSTANCE, SignalFacet.INSTANCE);
			}
		};
	}

	@Override
	public ElementId getId() {
		return ElementId.UIButton;
	}
	private PointDouble p(double x, double y) {
		return new PointDouble(x, y);
	}

	@Override
	protected void drawCommonContent(PropertiesParserState state) {
		String type = state.getFacetResponse(ButtonTypeFacet.class, null);
		DrawHandler drawer = state.getDrawer();
		double w = getRealSize().width;
		double h = getRealSize().height;
		int radius = (int)Math.min(20, Math.min(w, h) / 5);
		if (type != null) {
			if (type.equals("()")) {
				drawer.drawRectangleRound(0, 0, w, h, Math.min(w,h)/2);
			}
			else if (type.equals("[]")) {
				drawer.drawRectangle(0, 0, w, h);
			}
                        else if (type.equals("{}")) {
				drawer.drawRectangleRound(0, 0, w, h, radius);
			}
		}
                else {
			drawer.drawRectangleRound(0, 0, w, h, radius);
		}
	}
	public String gotoName() {
		String t=state.getFacetResponse(GoToFacet.class, null);
		if(t!=null && t.length()>0) return t;
		return null;
	}
	public String getSignal() {
		String t=state.getFacetResponse(SignalFacet.class, null);
		if(t!=null) return t;
		return "";
	}
}
