package com.baselet.element.facet.specific;

import java.util.Collections;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.baselet.control.StringStyle;
import com.baselet.control.enums.Priority;
import com.baselet.element.facet.FirstRunFacet;
import com.baselet.element.facet.PropertiesParserState;
import com.baselet.element.facet.KeyValueFacet;
import com.baselet.diagram.draw.DrawHandler;
import com.baselet.gui.AutocompletionText;
import com.baselet.element.facet.common.TextBeforeFirstSeparatorCollectorFacet;
import com.baselet.element.facet.common.TextBeforeFirstSeparatorCollectorFacet.TextBeforeFirstSeparatorCollectorFacetResponse;

public class TabFacet extends FirstRunFacet {

	private static final String TAB_KEY = "\t";
	private static final String HEADER_SPLIT = "--";
	private static final String KEY = "grid";

	public static final TabFacet INSTANCE = new TabFacet(true);
	public static final TabFacet INSTANCE_NOGRID = new TabFacet(false);

	private boolean gflg=true;
	protected TabFacet(boolean gflg) {
		this.gflg=gflg;
	}

	public static class TabFacetResponse {
		private final ArrayList<ArrayList<Double> > tabSizes = new ArrayList<ArrayList<Double> >();
		private final ArrayList<Boolean> gridFlags = new ArrayList<Boolean>();
		double sectionStart = 0;
                boolean gridFlag=true;
		int section=0;
		int sectionToDraw=0;
		TabFacetResponse(boolean gflg) {
			gridFlag=gflg;
		}
		public List<Double> getTabs(int section) {
			while(tabSizes.size()<section+1) {
				tabSizes.add(new ArrayList<Double>());
			}
			return tabSizes.get(section);
		}
		public int getSection() {
			return sectionToDraw;
		}
		public double getSectionStart() {
			return sectionStart;
		}
		public boolean needGrid() {
			if(sectionToDraw<gridFlags.size())
				return gridFlags.get(sectionToDraw);
			else
				return gridFlag;
		}
		public void setSectionStart(double p) {
			sectionStart=p;
		}
		public void newSection(double p) {
			++sectionToDraw;
			sectionStart=p;
		}
	}

	@Override
	public boolean checkStart(String line, PropertiesParserState state) {
		TabFacetResponse tf=getOrInit(state);
		DrawHandler drawer = state.getDrawer();
		
		if(line.equals(HEADER_SPLIT)) {
			++tf.section;
			tf.gridFlags.add(tf.gridFlag);
		}
		if(line.startsWith(KEY + KeyValueFacet.SEP)) {
			tf.gridFlag=line.substring((KEY + KeyValueFacet.SEP).length()).equals("yes");
			return true;
		}
		String[] parts=line.split(TAB_KEY,-1);
		if(parts.length<=1) return false;
		double wx=drawer.textWidth("x");
		List<Double> tabs=tf.getTabs(tf.section);
		for(int i=0; i<parts.length; ++i) {
			StringStyle s=StringStyle.analyzeFormatLabels(parts[i]);
			String sm=s.getStringWithoutMarkup();
			int len=sm.length();
			int n=0;
			while(n+1<=len && sm.charAt(len-n-1)==' ') {
				++n;
			}
			double w=drawer.textWidth(s)+n*wx;
			if(tabs.size()<i+1) tabs.add(w);
			else if(tabs.get(i)<w) tabs.set(i,w);
		}
		return false;
	}

	@Override
	public void handleLine(String line, PropertiesParserState state) {
	}

	@Override
	public List<AutocompletionText> getAutocompletionStrings() {
		return Arrays.asList(new AutocompletionText(KEY + KeyValueFacet.SEP + "yes", "draw grid lines"),
				new AutocompletionText(KEY + KeyValueFacet.SEP + "no", "no line"));
	}

	@Override
	public Priority getPriority() {
		return Priority.LOWEST; // only collect after TextBeforeFirstSeparatorCollectorFacet
	}

	private TabFacetResponse getOrInit(PropertiesParserState state) {
		TabFacetResponse tr=state.getFacetResponse(TabFacet.class,null);
		if(tr==null) {
			tr=new TabFacetResponse(gflg);
                        state.setFacetResponse(TabFacet.class,tr);
		}
		return tr;
	}

}
