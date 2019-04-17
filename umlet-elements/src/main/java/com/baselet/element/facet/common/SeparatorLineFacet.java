package com.baselet.element.facet.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.baselet.control.basics.XValues;
import com.baselet.control.enums.LineType;
import com.baselet.diagram.draw.DrawHandler;
import com.baselet.diagram.draw.DrawHandler.Layer;
import com.baselet.element.facet.Facet;
import com.baselet.element.facet.PropertiesParserState;
import com.baselet.element.facet.specific.TabFacet;
import com.baselet.element.facet.specific.ScrollFacet;
import com.baselet.element.facet.common.TextPrintFacet;
import com.baselet.gui.AutocompletionText;

public class SeparatorLineFacet extends Facet {

	public static final SeparatorLineFacet INSTANCE = new SeparatorLineFacet();

	private static final String KEY_PREFIX = "-";

	private final Map<String, LineType> variants = new HashMap<String, LineType>();

	protected SeparatorLineFacet() {
		variants.put(KEY_PREFIX, null);
		for (LineType lt : LineType.LT_LIST) {
			variants.put(KEY_PREFIX + lt.getValue(), lt);
		}
	}

	private static final double Y_SPACE = 5;

	@Override
	public void handleLine(String line, PropertiesParserState state) {
		DrawHandler drawer = state.getDrawer();
		TextPrintFacet.setupAtFirstLine("",drawer,state);
		LineType ltBefore = drawer.getLineType();
		LineType ltForLine = variants.get(line);
		if (ltForLine != null) {
			drawer.setLineType(ltForLine);
		}

		drawer.setLayer(Layer.Foreground); // should be always on top of background
		double linePos = state.getTextPrintPosition() - drawer.textHeightMax() + Y_SPACE / 2;
		XValues xPos = state.getXLimits(linePos);
		drawer.drawLine(xPos.getLeft() + 0.5, linePos, xPos.getRight() - 1, linePos);
		state.increaseTextPrintPosition(Y_SPACE);
		drawer.setLayer(Layer.Background);
		drawer.setLineType(ltBefore);
		if(line.equals("--")) {
			TabFacet.TabFacetResponse tabResponse = state.getFacetResponse(TabFacet.class, null);
                        if(tabResponse!=null) {
				if(tabResponse.needGrid()) {
					double p=tabResponse.getSectionStart();
		                	List<Double> tabs=tabResponse.getTabs(tabResponse.getSection());
					double pos=0;
					for(int i=0;i<tabs.size()-1;++i) {
						pos+=tabs.get(i)+2*drawer.getDistanceBorderToText();
						drawer.drawLine(pos, p, pos, linePos);
					}
				}
				tabResponse.newSection(linePos);
			}
		}
	}

	@Override
	public boolean checkStart(String line, PropertiesParserState state) {
		return variants.containsKey(line);
	}
	@Override
	public void parsingFinished(PropertiesParserState state, List<String> handledLines) {
		TabFacet.TabFacetResponse tabResponse = state.getFacetResponse(TabFacet.class, null);
		if(tabResponse!=null) {
			if(tabResponse.needGrid()) {
				double p=tabResponse.getSectionStart();
				DrawHandler drawer = state.getDrawer();
				int height = state.getGridElementSize().getHeight();
				String scroll=state.getFacetResponse(ScrollFacet.class, null);
				if(scroll!=null && scroll.indexOf('h')>=0) height-=ScrollFacet.SCROLL_SIZE;
		                List<Double> tabs=tabResponse.getTabs(tabResponse.getSection());
				double pos=0;
				for(int i=0;i<tabs.size()-1;++i) {
					pos+=tabs.get(i)+2*drawer.getDistanceBorderToText();
					drawer.drawLine(pos, p, pos, height);
				}
			}
		}
	}

	@Override
	public List<AutocompletionText> getAutocompletionStrings() {
		List<AutocompletionText> returnList = new ArrayList<AutocompletionText>();
		for (Entry<String, LineType> lt : variants.entrySet()) {
			if (lt.getValue() == null) {
				returnList.add(new AutocompletionText(lt.getKey(), "draw horizontal line of current linetype"));
			}
			else {
				returnList.add(new AutocompletionText(lt.getKey(), "draw " + lt.getValue().getReadableText() + " horizontal line"));
			}
		}
		return returnList;
	}

}
