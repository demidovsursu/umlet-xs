package com.baselet.element.elementnew.ui;

import java.util.Arrays;
import java.util.List;

import com.baselet.control.StringStyle;
import com.baselet.control.basics.geom.PointDouble;
import com.baselet.control.enums.AlignHorizontal;
import com.baselet.control.enums.ElementId;
import com.baselet.diagram.draw.DrawHandler;
import com.baselet.diagram.draw.DrawHandler.Layer;
import com.baselet.element.NewGridElement;
import com.baselet.element.draw.DrawHelper;
import com.baselet.element.facet.Facet;
import com.baselet.element.facet.PropertiesParserState;
import com.baselet.element.facet.Settings;
import com.baselet.element.facet.common.SeparatorLineFacet;
import com.baselet.element.facet.specific.ScrollFacet;
import com.baselet.element.facet.specific.TabFacet;
import com.baselet.element.facet.customdrawings.CustomStateFacet;
import com.baselet.element.facet.common.TextBeforeFirstSeparatorCollectorFacet;
import com.baselet.element.facet.common.TextBeforeFirstSeparatorCollectorFacet.TextBeforeFirstSeparatorCollectorFacetResponse;
import com.baselet.element.settings.SettingsManualResizeTop;
import com.baselet.element.sticking.polygon.PointDoubleStickingPolygonGenerator;

public class UIPanel extends NewGridElement {

	@Override
	protected Settings createSettings() {
		return new SettingsManualResizeTop() {
			@Override
			protected List<Facet> createFacets() {
				return listOf(super.createFacets(), TextBeforeFirstSeparatorCollectorFacet.INSTANCE, SeparatorLineFacet.INSTANCE, TabFacet.INSTANCE_NOGRID, ScrollFacet.INSTANCE);
			}
			@Override
			public AlignHorizontal getHAlign() {
				return AlignHorizontal.LEFT;
			}
		};
	}

	@Override
	public ElementId getId() {
		return ElementId.UIPanel;
	}

	@Override
	protected void drawCommonContent(PropertiesParserState state) {
		DrawHandler drawer = state.getDrawer();
		List<String> panelTitle = getTitleLines(state);
		String sel=state.getFacetResponse(CustomStateFacet.class, null);
		String scroll=state.getFacetResponse(ScrollFacet.class, null);
		double y = 0;
		double txtHeight = drawer.textHeightMaxWithSpace();
		double skipleft=0,skipright=0;
		double h=getRealSize().getHeight();
		double w=getRealSize().getWidth();
		int n=0;
		double b=drawer.getDistanceBorderToText();
		for (String line : panelTitle) {
			++n;
			y += txtHeight;
			double pos=0;
			String [] parts=line.split("\t");
			for(int i=0;i<parts.length;++i) {
				StringStyle style=StringStyle.analyzeFormatLabels(StringStyle.replaceNotEscaped(parts[i]));
				drawer.setLayer(Layer.Foreground); 
				drawer.print(style, pos+b, y, AlignHorizontal.LEFT);
				drawer.setLayer(Layer.Background);
				double wt=drawer.textWidth(style);
                        	DrawHelper.drawTab(drawer, pos, y-txtHeight, wt, txtHeight+b, b);
				if(i==0 || sel!=null && style.getStringWithoutMarkup().equals(sel)) {
					skipleft=pos;
					skipright=pos+wt+2*b;
				}
				pos+=wt+2*b;
			}
			y+=b;
		}
		state.getBuffer().setTopMin(y);
		DrawHelper.drawPanel(drawer,0, y, w, h-y, skipleft, skipright);
		if(scroll!=null) {
			if(scroll.equals("hv")) {
				DrawHelper.drawHScroll(drawer,0,h-ScrollFacet.SCROLL_SIZE,w-ScrollFacet.SCROLL_SIZE,ScrollFacet.SCROLL_SIZE);
				DrawHelper.drawVScroll(drawer,w-ScrollFacet.SCROLL_SIZE,y,ScrollFacet.SCROLL_SIZE,h-y-ScrollFacet.SCROLL_SIZE);
			}
			else if(scroll.equals("h")) {
				DrawHelper.drawHScroll(drawer,0,h-ScrollFacet.SCROLL_SIZE,w,ScrollFacet.SCROLL_SIZE);
			}
			else if(scroll.equals("v")) {
				DrawHelper.drawVScroll(drawer,w-ScrollFacet.SCROLL_SIZE,y,ScrollFacet.SCROLL_SIZE,h-y);
			}
		}
	}

	private static List<String> getTitleLines(PropertiesParserState state) {
		List<String> packageTitle;
		TextBeforeFirstSeparatorCollectorFacetResponse packageTitleResponse = state.getFacetResponse(TextBeforeFirstSeparatorCollectorFacet.class, null);
		if (packageTitleResponse != null) {
			packageTitle = packageTitleResponse.getLines();
		}
		else {
			packageTitle = Arrays.asList("");
		}
		return packageTitle;
	}

}
