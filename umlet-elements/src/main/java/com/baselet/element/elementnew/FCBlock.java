package com.baselet.element.elementnew;

import java.util.List;
import java.util.Arrays;

import com.baselet.control.basics.geom.PointDouble;
import com.baselet.control.enums.ElementId;
import com.baselet.element.NewGridElement;
import com.baselet.element.facet.Facet;
import com.baselet.element.facet.PropertiesParserState;
import com.baselet.element.facet.Settings;
import com.baselet.element.facet.common.SeparatorLineFacet;
import com.baselet.element.facet.specific.BlockTypeFacet;
import com.baselet.diagram.draw.DrawHandler;
import com.baselet.element.draw.DrawHelper;
import com.baselet.element.settings.SettingsManualresizeCenter;
import com.baselet.element.sticking.StickingPolygon;
import com.baselet.element.sticking.polygon.StickingPolygonGenerator;
import com.baselet.element.sticking.polygon.PointDoubleStickingPolygonGenerator;
import com.baselet.element.sticking.polygon.SimpleStickingPolygonGenerator;

public class FCBlock extends NewGridElement {

	@Override
	protected Settings createSettings() {
		return new SettingsManualresizeCenter() {
			@Override
			protected List<Facet> createFacets() {
				return listOf(super.createFacets(), SeparatorLineFacet.INSTANCE,BlockTypeFacet.INSTANCE);
			}
		};
	}

	@Override
	public ElementId getId() {
		return ElementId.FCBlock;
	}
	private PointDouble p(double x, double y) {
		return new PointDouble(x, y);
	}

	@Override
	protected void drawCommonContent(PropertiesParserState state) {
		String type = state.getFacetResponse(BlockTypeFacet.class, null);
		DrawHandler drawer = state.getDrawer();
		double w = getRealSize().width;
		double h = getRealSize().height;
		if (type != null) {
			double c = h/3.0;
			if(type.equals("//")){
				if(c>w) c=w;
				drawer.drawLines(p(c, 0), p(w, 0), p(w-c, h), p(0, h), p(c, 0));
		                List<PointDouble> pgon = Arrays.asList(p(c, 0), p(w, 0), p(w-c, h), p(0, h), p(c, 0));
				state.setStickingPolygonGenerator(new PointDoubleStickingPolygonGenerator(pgon));
                        }
			else if (type.equals("()")) {
				drawer.drawRectangleRound(0, 0, w, h, Math.min(w,h)/2);
			}
			else if (type.equals("[")) {
				c=12;
				if(c>w) c=w;
				drawer.drawLines(p(c, 0), p(0, 0), p(0, h), p(c, h));
			}
			else if (type.equals("]")) {
				c=12;
				if(c>w) c=w;
				drawer.drawLines(p(w-c, 0), p(w, 0), p(w, h), p(w-c, h));
			}
			else if (type.equals("<>")) {
				drawer.drawLines(p(w/2, 0), p(w, h/2), p(w/2, h), p(0, h/2), p(w/2,0));
		                List<PointDouble> pgon = Arrays.asList(p(w/2, 0), p(w, h/2), p(w/2, h), p(0, h/2), p(w/2,0));
				state.setStickingPolygonGenerator(new PointDoubleStickingPolygonGenerator(pgon));
			}
			else if (type.equals("[||]")) {
				c=12;
				drawer.drawRectangle(0, 0, w, h);
				drawer.drawLines(p(c, 0), p(c, h));
				drawer.drawLines(p(w-c, 0), p(w-c, h));
			}
			else if (type.equals("<=>")) {
				if(2*c>w) c=w/2;
				drawer.drawLines(p(0, h/2), p(c, 0), p(w-c, 0), p(w, h/2), p(w-c,h),p(c,h),p(0,h/2));
		                List<PointDouble> pgon = Arrays.asList(p(0, h/2), p(c, 0), p(w-c, 0), p(w, h/2), p(w-c,h),p(c,h),p(0,h/2));
				state.setStickingPolygonGenerator(new PointDoubleStickingPolygonGenerator(pgon));
			}
			else if (type.equals("/\\")) {
				if(c>40) c=40;
				if(2*c>w) c=w/2;
				drawer.drawLines(p(c, 0), p(w-c, 0), p(w, c), p(w,h),p(0,h),p(0,c),p(c,0));
			}
			else if (type.equals("\\/")) {
				if(c>40) c=40;
				if(2*c>w) c=w/2;
				drawer.drawLines(p(c, h), p(w-c, h), p(w, h-c), p(w,0),p(0,0),p(0,h-c),p(c,h));
			}
			else if (type.equals("=")) {
				if(w>=h) {
					drawer.drawLines(p(0, 0), p(w, 0));
					drawer.drawLines(p(0, h), p(w, h));
				}
				else {
					drawer.drawLines(p(0, 0), p(0, h));
					drawer.drawLines(p(w, 0), p(w, h));
				}
			}
                        else
				drawer.drawRectangle(0, 0, w, h);
		}
                else
			drawer.drawRectangle(0, 0, w, h);
	}


}
