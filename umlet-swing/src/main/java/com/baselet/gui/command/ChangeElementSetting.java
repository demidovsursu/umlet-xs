package com.baselet.gui.command;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.baselet.control.HandlerElementMap;
import com.baselet.diagram.DiagramHandler;
import com.baselet.element.interfaces.GridElement;

public class ChangeElementSetting extends Command {

	private String key;
	private char op='=';
	private Map<GridElement, String> elementValueMap=null;
	private Collection<GridElement> elements=null;
	private Map<GridElement, String> oldValue;

	public ChangeElementSetting(String key, String value, Collection<GridElement> element) {
		this(key, createSingleValueMap(value, element));
	}
	public ChangeElementSetting(String key, char op, Collection<GridElement> element) {
		this.op=op;
		elements=element;
		elementValueMap=null;
		this.key = key;
	}

	public ChangeElementSetting(String key, Map<GridElement, String> elementValueMap) {
		this.key = key;
		this.elementValueMap = elementValueMap;
	}

	@Override
	public void execute(DiagramHandler handler) {
		super.execute(handler);
		if(elementValueMap==null) 
			elementValueMap=createIncValueMap(key,op,elements);
		oldValue = new HashMap<GridElement, String>();

		for (Entry<GridElement, String> entry : elementValueMap.entrySet()) {
			GridElement e = entry.getKey();
			oldValue.put(e, e.getSetting(key));
			e.setProperty(key, entry.getValue());
			if (handler.getDrawPanel().getSelector().isSelected(e)) {
				HandlerElementMap.getHandlerForElement(e).getDrawPanel().getSelector().updateSelectorInformation(); // update the property panel to display changed attributes
			}
		}
		handler.getDrawPanel().repaint();
	}

	@Override
	public void undo(DiagramHandler handler) {
		super.undo(handler);
		for (Entry<GridElement, String> entry : oldValue.entrySet()) {
			entry.getKey().setProperty(key, entry.getValue());
		}
		handler.getDrawPanel().repaint();
	}

	private static Map<GridElement, String> createSingleValueMap(String value, Collection<GridElement> elements) {
		Map<GridElement, String> singleValueMap = new HashMap<GridElement, String>(1);
		for (GridElement e : elements) {
			singleValueMap.put(e, value);
		}
		return singleValueMap;
	}
	private static Map<GridElement, String> createIncValueMap(String key, char op, Collection<GridElement> elements) {
		Map<GridElement, String> incValueMap = new HashMap<GridElement, String>(1);
		for (GridElement e : elements) {
			String old=incValueMap.get(e);
			if(old==null)
			{	old=e.getSetting(key);
				if(old==null || old.length()==0) old="0";
				int val=0;
				try {
					val=Integer.parseInt(old);
			        } catch (Throwable t) {}
				old=String.valueOf(val+(op=='+'?1:-1));
			}
			else {
				old=String.valueOf(Integer.parseInt(old)+(op=='+'?1:-1));
			}			
			incValueMap.put(e, old);
		}
		return incValueMap;
	}
}
