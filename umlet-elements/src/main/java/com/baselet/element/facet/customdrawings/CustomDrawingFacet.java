package com.baselet.element.facet.customdrawings;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baselet.control.enums.FormatLabels;
import com.baselet.control.enums.Priority;
import com.baselet.element.facet.Facet;
import com.baselet.element.facet.PropertiesParserState;
import com.baselet.element.facet.customdrawings.CustomStateFacet;
import com.baselet.gui.AutocompletionText;

public class CustomDrawingFacet extends Facet {

	public static final CustomDrawingFacet INSTANCE = new CustomDrawingFacet();
	public static final Logger logger = LoggerFactory.getLogger(CustomDrawingFacet.class);
	public static final String CODE_SEP_START = "customelement=";
	public static final String CODE_START_INFO = "indicates the start of custom drawing commands, has no close command.";

	public static class CustomDrawingFacetResponse {
                boolean isActive=false;
		boolean inState=true;
	}
	@Override
	public boolean checkStart(String line, PropertiesParserState state) {
		CustomDrawingFacetResponse dt = getOrInit(state);
		if (dt.isActive) {
			return true;
		}
		else if (CODE_SEP_START.equals(line)) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public void handleLine(String line, PropertiesParserState state) {
		CustomDrawingFacetResponse dt = getOrInit(state);
		if (CODE_SEP_START.equals(line)) {
			if (dt.isActive) {
				// custom commands are already turned on -> error
				throw new RuntimeException(FormatLabels.BOLD.getValue() + "Invalid value: " + FormatLabels.BOLD.getValue() + CODE_SEP_START + "\nDuplicate command. This command may only occur once.");
			}
			dt.isActive=true;
		}
		else if(line.endsWith(":")) {
			String st = state.getFacetResponse(CustomStateFacet.class, null);
			String labels=":"+line.replace(',',':');
			if(st!=null && labels.contains(":"+st+":")) {
				dt.inState=true;
			}
			else {
				dt.inState=false;
			}
		}
		else if(dt.inState) {
			CustomDrawingParserImpl.parse(line, state.getGridElementSize().getWidth(), state.getGridElementSize().getHeight(), state.getDrawer());
		}
	}

	@Override
	public List<AutocompletionText> getAutocompletionStrings() {
		List<AutocompletionText> autocompletionList = new LinkedList<AutocompletionText>();
		autocompletionList.add(new AutocompletionText(CODE_SEP_START, CODE_START_INFO));

		autocompletionList.add(new AutocompletionText("state:",
				"drawing commands for \"state\""));
		autocompletionList.add(new AutocompletionText("drawLine(20, 10, 50, 20)",
				"Draws a line from (x1, y1) to (x2, y2). Supports fg, lt and lw option after the last bracket."));

		autocompletionList.add(new AutocompletionText("drawRectangle(5,5,40,20)",
				"Draws a rectangle with the top left corner at (x, y). Supports fg, bg, lt and lw option after the last bracket."));

		autocompletionList.add(new AutocompletionText("drawRectangleRound(5,5,40,20,5)",
				"Draws a rectangle with round corners with the top left corner at (x, y). Supports fg, bg, lt and lw option after the last bracket."));

		autocompletionList.add(new AutocompletionText("drawCircle(20,20,20)",
				"Draws a circle with the center at (x, y) and the given radius. Supports fg, bg, lt and lw option after the last bracket."));

		autocompletionList.add(new AutocompletionText("drawEllipse(0,0,40,20)",
				"Draws an ellipse where the top left corner of the surrounding rectangle is at (x, y). Supports fg, bg, lt and lw option after the last bracket."));

		autocompletionList.add(new AutocompletionText("drawArc(0,0,60,30,0,270,false)",
				"Draws an elliptical arc where the top left corner of the surrounding rectangle is at (x, y). Supports fg, bg, lt and lw option after the last bracket."));

		autocompletionList.add(new AutocompletionText("drawText(\"This is text!\", width/2, height/2, center)",
				"Draws the text at (x, y) with the given horizontal alignment. Supports fg option after the last bracket."));

		autocompletionList.add(new AutocompletionText("drawImage(\"image.png\",0,0,20,20)",
				"Draws the image in (x, y, w, h)"));

		return autocompletionList;
	}

	@Override
	public Priority getPriority() {
		return Priority.HIGHEST;
	}
	private CustomDrawingFacetResponse getOrInit(PropertiesParserState state) {
		return state.getOrInitFacetResponse(CustomDrawingFacet.class, new CustomDrawingFacetResponse());
	}
}
