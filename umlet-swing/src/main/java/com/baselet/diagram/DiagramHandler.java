package com.baselet.diagram;

import java.awt.Component;
import java.awt.MouseInfo;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayDeque;
import java.util.Queue;
 
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baselet.control.enums.ElementId;
import com.baselet.control.ErrorMessages;
import com.baselet.control.HandlerElementMap;
import com.baselet.control.Main;
import com.baselet.control.config.Config;
import com.baselet.control.SharedUtils;
import com.baselet.control.basics.Converter;
import com.baselet.control.basics.geom.Point;
import com.baselet.control.constants.Constants;
import com.baselet.control.enums.Program;
import com.baselet.diagram.io.DiagramFileHandler;
import com.baselet.element.ComponentSwing;
import com.baselet.element.NewGridElement;
import com.baselet.element.interfaces.GridElement;
import com.baselet.element.elementnew.ui.UIButton;
import com.baselet.element.facet.KeyValueFacet;
import com.baselet.element.facet.common.ActiveFacet;
import com.baselet.element.sticking.Stickable;
import com.baselet.element.sticking.StickableMap;
import com.baselet.gui.BaseGUI;
import com.baselet.gui.CurrentGui;
import com.baselet.gui.command.Controller;
import com.baselet.gui.listener.DiagramListener;
import com.baselet.gui.listener.GridElementListener;
import com.baselet.gui.menu.MenuFactorySwing;
import com.baselet.element.relation.Relation;

import com.baselet.gui.command.Command;
import com.baselet.gui.command.GoTo;
import com.baselet.gui.command.ChangeElementSetting;
import com.baselet.gui.command.Macro;
import com.baselet.gui.command.ChangePanelLines;

public class DiagramHandler {

	private static final Logger log = LoggerFactory.getLogger(DiagramHandler.class);

        private Timer timer = null;
        private TimerTask timerTask = null;
	private long startTime;
	private boolean emulationMode=false;
	private Queue<String> signalQueue=new ArrayDeque<String>();
	private final static String signalRegex="[^-+()\\[\\]<>{}=$]+(\\([^()]*\\))?";
	private Map<String,ChangePanelLines.ChangeElementItem> cacheData=null;
	private Map<String,List<String>> procMap=null;
	private Map<String,GridElement> elementMap=null;
	private List<String> args=null;
	private int nCalls=0;

	private boolean isChanged;
	private final DiagramFileHandler fileHandler;
	private FontHandler fontHandler;

	protected DrawPanel drawpanel;
	private final Controller controller;
	protected DiagramListener listener;
	private String helptext;
	private boolean enabled;
	private int gridSize;
//	private StringBuilder mylog=new StringBuilder();

//	private OldRelationListener relationListener;
	private GridElementListener gridElementListener;

	public static DiagramHandler forExport(FontHandler fontHandler) {
		DiagramHandler returnHandler = new DiagramHandler(null, false);
		if (fontHandler != null) {
			returnHandler.fontHandler = fontHandler;
		}
		return returnHandler;
	}

	public DiagramHandler(File diagram) {
		this(diagram, false);
	}

	protected DiagramHandler(File diagram, boolean nolistener) {
		gridSize = Constants.DEFAULTGRIDSIZE;
		isChanged = false;
		enabled = true;
		drawpanel = createDrawPanel();
		controller = new Controller(this);
		fontHandler = new FontHandler(this);
		fileHandler = DiagramFileHandler.createInstance(this, diagram);
		if (!nolistener) {
			setListener(new DiagramListener(this));
		}
		if (diagram != null) {
			fileHandler.doOpen();
		}

		boolean extendedPopupMenu = false;
		BaseGUI gui = CurrentGui.getInstance().getGui();
		if (gui != null) {
			gui.setValueOfZoomDisplay(getGridSize());
//			extendedPopupMenu = gui.hasExtendedContextMenu();
		}

		initDiagramPopupMenu(extendedPopupMenu);
	}

	protected DrawPanel createDrawPanel() {
		return new DrawPanel(this, true);
	}

	protected void initDiagramPopupMenu(boolean extendedPopupMenu) {
		drawpanel.setComponentPopupMenu(new DiagramPopupMenu(extendedPopupMenu));
	}

	public void setEnabled(boolean en) {
		if (!en && enabled) {
			drawpanel.removeMouseListener(listener);
			drawpanel.removeMouseMotionListener(listener);
			enabled = false;
		}
		else if (en && !enabled) {
			drawpanel.addMouseListener(listener);
			drawpanel.addMouseMotionListener(listener);
			enabled = true;
		}
	}

	protected void setListener(DiagramListener listener) {
		this.listener = listener;
		drawpanel.addMouseListener(this.listener);
		drawpanel.addMouseMotionListener(this.listener);
		drawpanel.addMouseWheelListener(this.listener);
	}

	public DiagramListener getListener() {
		return listener;
	}

	public void setChanged(boolean changed) {
		if (isChanged != changed) {
			isChanged = changed;
			BaseGUI gui = CurrentGui.getInstance().getGui();
			if (gui != null) {
				gui.setDiagramChanged(this, changed);
			}
		}
	}

	public DrawPanel getDrawPanel() {
		return drawpanel;
	}

	public DiagramFileHandler getFileHandler() {
		return fileHandler;
	}

	public FontHandler getFontHandler() {
		return fontHandler;
	}

	public Controller getController() {
		return controller;
	}

	// returnvalue needed for eclipse plugin
	// returns true if the file is saved, else returns false
	public boolean doSave() {
		try {
			fileHandler.doSave();
			reloadPalettes();
			CurrentGui.getInstance().getGui().afterSaving();
			return true;
		} catch (IOException e) {
			log.error(ErrorMessages.ERROR_SAVING_FILE, e);
			displayError(ErrorMessages.ERROR_SAVING_FILE + e.getMessage());
			return false;
		}
	}

	public void doSaveAs(String extension) {
		if (drawpanel.getGridElements().isEmpty()) {
			displayError(ErrorMessages.ERROR_SAVING_EMPTY_DIAGRAM);
		}
		else {
			try {
				fileHandler.doSaveAs(extension);
				reloadPalettes();
				CurrentGui.getInstance().getGui().afterSaving();
			} catch (IOException e) {
				log.error(ErrorMessages.ERROR_SAVING_FILE, e);
				displayError(ErrorMessages.ERROR_SAVING_FILE + e.getMessage());
			}
		}
	}

	public void doPrint() {
		PrinterJob printJob = PrinterJob.getPrinterJob();
		printJob.setPrintable(getDrawPanel());
		if (printJob.printDialog()) {
			try {
				printJob.print();
			} catch (PrinterException pe) {
				displayError(ErrorMessages.ERROR_PRINTING);
			}
		}
	}

	// reloads the diagram from file + updates gui
	public void reload() {
		drawpanel.removeAll();
		fileHandler.doOpen();
		drawpanel.updatePanelAndScrollbars();
	}

	// reloads palettes if the palette has been changed.
	private void reloadPalettes() {
		for (DiagramHandler d : Main.getInstance().getPalettes().values()) {
			if (d.getFileHandler().equals(getFileHandler()) && !d.equals(this)) {
				d.reload();
			}
		}
		getDrawPanel().getSelector().updateSelectorInformation(); // Must be updated to remain in the current Property Panel
	}

	public void doClose() {
		if (askSaveIfDirty()) {
			Main.getInstance().getDiagrams().remove(this); // remove this DiagramHandler from the list of managed diagrams
			drawpanel.getSelector().deselectAll(); // deselect all elements of the drawpanel (must be done BEFORE closing the tab, because otherwise it resets this DrawHandler again as the current DrawHandler
			CurrentGui.getInstance().getGui().close(this); // close the GUI (tab, ...) and set the next active tab as the CurrentDiagram

			// update property panel to now selected diagram (or to empty if no diagram exists)
			DiagramHandler newhandler = CurrentDiagram.getInstance().getDiagramHandler();
			if (newhandler != null) {
				newhandler.getDrawPanel().getSelector().updateSelectorInformation();
			}
			else {
				Main.getInstance().setPropertyPanelToGridElement(null);
			}
		}
	}

	/**
	 * closes this diagram handler and adds a new empty diagram if this was the last diagram of UMLet
	 */
	public void doCloseAndAddNewIfNoLeft() {
		doClose();
		if (Main.getInstance().getDiagrams().size() == 0) {
			Main.getInstance().doNew();
		}
	}

	public String getName() {
		String name = fileHandler.getFileName();
		if (name.contains(".")) {
			name = name.substring(0, name.lastIndexOf("."));
		}
		return name;
	}

	public String getFullPathName() {
		return fileHandler.getFullPathName();
	}

	public GridElementListener getEntityListener(GridElement e) {
		if (gridElementListener == null) {
			gridElementListener = new GridElementListener(this);
		}
		return gridElementListener;
	}

	public boolean askSaveIfDirty() {
		if (isChanged) {
			int ch = JOptionPane.showOptionDialog(CurrentGui.getInstance().getGui().getMainFrame(), "Save changes?", Program.getInstance().getProgramName() + " - " + getName(), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
			if (ch == JOptionPane.YES_OPTION) {
				doSave();
				return true;
			}
			else if (ch == JOptionPane.NO_OPTION) {
				return true;
			}
			return false; // JOptionPane.CANCEL_OPTION
		}
		return true;
	}

	public void setHelpText(String helptext) {
		this.helptext = helptext;
	}

	public String getHelpText() {
		if (helptext == null) {
			return Constants.getDefaultHelptext();
		}
		else {
			return helptext;
		}
	}

	public boolean isChanged() {
		return isChanged;
	}

	public int getGridSize() {
		return gridSize;
	}

	public float getZoomFactor() {
		return (float) getGridSize() / (float) Constants.DEFAULTGRIDSIZE;
	}

	public void setGridSize(int gridSize) {
		this.gridSize = gridSize;
	}

	public int realignToGrid(double val) {
		return realignToGrid(true, val, false);
	}

	public int realignToGrid(boolean logRealign, double val) {
		return realignToGrid(logRealign, val, false);
	}

	public int realignToGrid(boolean logRealign, double val, boolean roundUp) {
		return SharedUtils.realignTo(logRealign, val, roundUp, gridSize);
	}

	public static int realignTo(int val, int toVal) {
		return SharedUtils.realignTo(false, val, false, toVal);
	}

	public static void zoomEntity(int fromFactor, int toFactor, GridElement e) {
		Vector<GridElement> vec = new Vector<GridElement>();
		vec.add(e);
		zoomEntities(fromFactor, toFactor, vec);
	}

	public static void zoomEntities(int fromFactor, int toFactor, List<GridElement> selectedEntities) {

		/**
		 * The entities must be resized to the new factor
		 */

		for (GridElement entity : selectedEntities) {
			int newX = entity.getRectangle().x * toFactor / fromFactor;
			int newY = entity.getRectangle().y * toFactor / fromFactor;
			int newW = entity.getRectangle().width * toFactor / fromFactor;
			int newH = entity.getRectangle().height * toFactor / fromFactor;
			entity.setLocation(realignTo(newX, toFactor), realignTo(newY, toFactor));
			// Normally there should be no realign here but relations and custom elements sometimes must be realigned therefore we don't log it as an error
			entity.setSize(realignTo(newW, toFactor), realignTo(newH, toFactor));

		}
	}

	public void setGridAndZoom(int factor) {
		setGridAndZoom(factor, true);
	}

	public void setGridAndZoom(int factor, boolean manualZoom) {

		/**
		 * Store the old gridsize and the new one. Furthermore check if the zoom process must be made
		 */

		int oldGridSize = getGridSize();

		if (factor < 1 || factor > 20) {
			return; // Only zoom between 10% and 200% is allowed
		}
		if (factor == oldGridSize) {
			return; // Only zoom if gridsize has changed
		}

		setGridSize(factor);

		/**
		 * Zoom entities to the new gridsize
		 */

		zoomEntities(oldGridSize, gridSize, getDrawPanel().getGridElements());

		// AB: Zoom origin
		getDrawPanel().zoomOrigin(oldGridSize, gridSize);

		/**
		 * The zoomed diagram will shrink to the upper left corner and grow to the lower right
		 * corner but we want to have the zoom center in the middle of the actual visible drawpanel
		 * so we have to change the coordinates of the entities again
		 */

		if (manualZoom) {
			// calculate mouse position relative to UMLet scrollpane
			Point mouseLocation = Converter.convert(MouseInfo.getPointerInfo().getLocation());
			Point viewportLocation = Converter.convert(getDrawPanel().getScrollPane().getViewport().getLocationOnScreen());
			float x = mouseLocation.x - viewportLocation.x;
			float y = mouseLocation.y - viewportLocation.y;

			// And add any space on the upper left corner which is not visible but reachable by scrollbar
			x += getDrawPanel().getScrollPane().getViewport().getViewPosition().getX();
			y += getDrawPanel().getScrollPane().getViewport().getViewPosition().getY();

			// The result is the point where we want to center the zoom of the diagram
			float diffx, diffy;
			diffx = x - x * gridSize / oldGridSize;
			diffy = y - y * gridSize / oldGridSize;

			// AB: Move origin in opposite direction
			log.debug("diffX/diffY: " + diffx + "/" + diffy);
			log.debug("Manual Zoom Delta: " + realignToGrid(false, diffx) + "/" + realignToGrid(false, diffy));
			getDrawPanel().moveOrigin(realignToGrid(false, -diffx), realignToGrid(false, -diffy));

			for (GridElement e : getDrawPanel().getGridElements()) {
				e.setLocationDifference(realignToGrid(false, diffx), realignToGrid(false, diffy));
			}

			/**
			 * Now we have to do some additional "clean up" stuff which is related to the zoom
			 */

			getDrawPanel().updatePanelAndScrollbars();

			// Set changed only if diagram is not empty (otherwise no element has been changed)
			if (!drawpanel.getGridElements().isEmpty()) {
				setChanged(true);
			}

			BaseGUI gui = CurrentGui.getInstance().getGui();
			if (gui != null) {
				gui.setValueOfZoomDisplay(factor);
			}

			float zoomFactor = CurrentDiagram.getInstance().getDiagramHandler().getZoomFactor() * 100;
			String zoomtext;
			if (CurrentDiagram.getInstance().getDiagramHandler() instanceof PaletteHandler) {
				zoomtext = "Palette zoomed to " + Integer.toString((int) zoomFactor) + "%";
			}
			else {
				zoomtext = "Diagram zoomed to " + Integer.toString((int) zoomFactor) + "%";
			}
			Notifier.getInstance().showInfo(zoomtext);
		}
	}

	private void displayError(String error) {
		JOptionPane.showMessageDialog(CurrentGui.getInstance().getGui().getMainFrame(), error, "ERROR", JOptionPane.ERROR_MESSAGE);
	}

	public void setHandlerAndInitListeners(GridElement element) {
		if (HandlerElementMap.getHandlerForElement(element) != null) {
			((Component) element.getComponent()).removeMouseListener(HandlerElementMap.getHandlerForElement(element).getEntityListener(element));
			((Component) element.getComponent()).removeMouseMotionListener(HandlerElementMap.getHandlerForElement(element).getEntityListener(element));
		}
		HandlerElementMap.setHandlerForElement(element, this);
		((Component) element.getComponent()).addMouseListener(HandlerElementMap.getHandlerForElement(element).getEntityListener(element));
		((Component) element.getComponent()).addMouseMotionListener(HandlerElementMap.getHandlerForElement(element).getEntityListener(element));
		if (element instanceof NewGridElement) {
			((ComponentSwing) element.getComponent()).setHandler(this);
		}
		element.updateModelFromText(); // must be updated here because the new handler could have a different zoom level
	}

	public void setEmulation(boolean mode) {
		if(emulationMode==mode) return;
		emulationMode=mode;
		if(mode) {
			drawpanel.getSelector().deselectAll();
			timer=new Timer();
			final Object param=this;
			timerTask=new TimerTask() {
				@Override 
				public void run() {
					MenuFactorySwing menuFactory = MenuFactorySwing.getInstance();
					menuFactory.doAction("Timer", param);
				}
		    		};
			int ts=Config.getInstance().getDefaultSimStep();
			timer.schedule(timerTask,ts,ts);
			startTime=System.currentTimeMillis();
			Notifier.getInstance().showInfo("Start simulation");
		}
		else {
			if(timerTask != null) {
				timerTask.cancel();
				timerTask = null;
			}
			timer.cancel();
			timer=null;
			Notifier.getInstance().showInfo("Stop simulation");
		}

	}
	private String setArgs(String s, boolean subst) {
		int pos=s.indexOf('(');
		if(pos>0 && s.endsWith(")")) {
			ArrayList<String> newArgs=new ArrayList<String>();
			String[] parts=s.substring(pos+1,s.length()-1).split(",",-1);
			for(int i=0;i<parts.length;++i)
				newArgs.add(subst?replaceSubst(parts[i]):parts[i]);
			args=newArgs;
			return s.substring(0,pos);
		}
		args=null;
		return s;
	}
	public Command emulationKey(String s) {
		Macro c=new Macro(new ArrayList<Command>());
		initCacheData();
		args=new ArrayList<String>();
		args.add(s);
		execAction(findProc("keypress"));
		createChangePanelLines(c);
		Notifier.getInstance().showInfo("Press key "+s+" at "+String.format("%.3f",(System.currentTimeMillis()-startTime)/1000.0));
		if(c.getCommands().size()==0) return null;
		return c;
	}
	public Command emulationClick(GridElement e) {
		Macro c=new Macro(new ArrayList<Command>());
		String inf="Click";
		if(e instanceof UIButton) {
			String to=((UIButton)e).gotoName();
			String s=((UIButton)e).getSignal().trim();
			initCacheData();
			execAction(s);
			createChangePanelLines(c);
			if(s.matches(signalRegex)) {
				signalQueue.offer(s);
				inf="Signal "+s;
			}
			if(to!=null) {
				GridElement x=elementMap.get(to);
				if(x!=null) {
					List<GridElement> o=drawpanel.getActiveElements();
					if(o.size()==1 && o.get(0)==x) ;
					else {
						if(o.size()>0) 
							c.getCommands().add(new ChangeElementSetting(ActiveFacet.KEY, null, o));
						ArrayList<GridElement> a=new ArrayList<GridElement>();
						a.add(x);
						c.getCommands().add(new ChangeElementSetting(ActiveFacet.KEY, '+', a));
					}
					c.getCommands().add(new GoTo(x));
				}
			}
		}
		Notifier.getInstance().showInfo(inf+" at "+String.format("%.3f",(System.currentTimeMillis()-startTime)/1000.0));
		if(c.getCommands().size()==0) return null;
		return c;
	}
	public Command emulationStart() {
		List<GridElement> el=new ArrayList(drawpanel.getSelector().getSelectedElements());
		GridElement x = null;
		if(!emulationMode) {
			setEmulation(true);
		}
		Macro c=null;
		ArrayList<GridElement> a=new ArrayList<GridElement>();
		for(GridElement e: el) {
			if(e instanceof UIButton) {
				Command cc=emulationClick(e);
				if(cc!=null) return cc;
			}
			else {
				a.add(e);
			}
		}
		if(a.size()>0) {
			c=new Macro(new ArrayList<Command>());
			List<GridElement> o=drawpanel.getActiveElements();
			if(o.size()>0) c.getCommands().add(new ChangeElementSetting(ActiveFacet.KEY, null, o));
			c.getCommands().add(new ChangeElementSetting(ActiveFacet.KEY, '+', a));
			signalQueue.offer("START");

		}
		return c;
	}
	private String findAction(GridElement e, String a) {
		List<String> pa=e.getPanelAttributesAsList();
 		Pattern ptn = Pattern.compile("^"+a+"\\s*/\\s*(.*)");
		for(String line:pa) {
			Matcher matcher = ptn.matcher(line);
			if(matcher.find()) {
				return matcher.group(1).trim();
			}
		}
		return null;
	}
	private boolean testTrigger(String signal, String trigger) {
		if(signal.length()==0 && trigger.length()==0) return true;
		if(trigger.length()!=0 && signal.length()==0) return false;
		String name=signal;
		int pos=signal.indexOf('(');
		int zpt;
		if(pos>0) name=signal.substring(0,pos);
		int i=0;
		while(i<trigger.length()) {
			pos=trigger.indexOf('(',i);
			zpt=trigger.indexOf(',',i);
			if(zpt<0) {
				zpt=trigger.length();
			}
			if(pos>=zpt) pos=-1;
			if(pos>0) {
				pos=trigger.indexOf(')',i);
				if(pos<0) return false;
				++pos;
				if(pos>=trigger.length()) zpt=pos;
				else if(trigger.charAt(pos)!=',') return false;
				else zpt=pos;
				if(signal.equals(trigger.substring(i,zpt).trim())) return true;
				i=pos+1;
			}
			else {
				if(name.equals(trigger.substring(i,zpt).trim())) return true;
				i=zpt+1;
			}
		}
		return false;
	}
	private boolean testCond(String op, String arg1, String arg2) {
//		mylog.append("("+arg1+op+arg2+") ");
		if(arg1.length()==0) return false;
		if(arg1.indexOf('$')>=0) arg1=replaceSubst(arg1);
		else arg1=getData(arg1);
		if(arg1==null) return false;
		if(op.equals("==") || op.equals("=")) return arg1.equals(arg2);
		if(op.equals("~=")) {
			try {
				return arg1.matches(arg2);
			} catch(Throwable t) {
				return false;
			}
		}
		if(op.equals("<>") || op.equals("!=")) return !arg1.equals(arg2);
		if(op.equals("<")) return arg1.length()<arg2.length() || arg1.length()==arg2.length() && arg1.compareTo(arg2)<0;
		if(op.equals("<=")) return arg1.length()<arg2.length() || arg1.length()==arg2.length() && arg1.compareTo(arg2)<=0;
		if(op.equals(">")) return arg1.length()>arg2.length() || arg1.length()==arg2.length() && arg1.compareTo(arg2)>0;
		if(op.equals("<=")) return arg1.length()>arg2.length() || arg1.length()==arg2.length() && arg1.compareTo(arg2)>=0;
		return false;
	}
	private boolean testCond(String cond) {
		if(cond.length()==0) return false;
		String[] parts=cond.split("(==|=|>=|<=|<>|>|<|!=|~=)",2);
		if(parts.length>1) {
			String op=cond.substring(parts[0].length(),cond.length()-parts[1].length());
			return testCond(op,parts[0].trim(),replaceSubst(parts[1].trim()));
		}
//		mylog.append("("+parts[0]+")? ");

		String val=null;
		if(parts[0].length()>0 && parts[0].charAt(0)=='!') {
			val=getData(parts[0].substring(1));
			if(val==null || val.length()==0) return true;
			return false;
		}
		val=getData(parts[0]);
		if(val==null || val.length()==0) return false;
		return true;
	}
	private boolean testCondAnd(String cond) {
		if(cond.length()==0) return false;
		String [] parts=cond.split("&&");
		for(int i=0;i<parts.length;++i) 
			if(!testCond(parts[i].trim())) return false;
		return true;
	}
	private boolean testGuard(String guard) {
//		mylog.append("test("+guard+") ");

		if(guard.length()==0 || guard.equals("[else]")) return true;
		if(guard.length()<2 || !(guard.charAt(0)=='[' && guard.charAt(guard.length()-1)==']')) return false;
		String [] parts=guard.substring(1,guard.length()-1).split("\\|\\|");
		for(int i=0;i<parts.length;++i) 
			if(testCondAnd(parts[i].trim())) return true;
		return false;
	}
	private boolean findInternalAction(GridElement e, String signal) {
		List<String> pa=e.getPanelAttributesAsList();
		for(String line:pa) {
			int pos=line.indexOf('/');
			if(pos>0) {
				String trigger=line.substring(0,pos).trim();
				String guard="";
				String action=line.substring(pos+1).trim();
				int gpos=trigger.indexOf('[');
				if(gpos>=0) {
					guard=trigger.substring(gpos);
					trigger=trigger.substring(0,gpos).trim();
				}
				if(testTrigger(signal,trigger)&&testGuard(guard)) {
					execAction(action);
					return true;
				}
			}
		}
		return false;
	}
	private ChangePanelLines.ChangeElementItem findData(String name) {
		// element.prop[.#] [data.]prop[.#]
		// element.#
		if(name.length()==0) return null;
		ChangePanelLines.ChangeElementItem c=null;
		if(cacheData.containsKey(name)) {
			c=cacheData.get(name);
			if(c==null) return null;
			if(c.oldValue==null && c.newValue!=null) return cacheData.get(c.newValue);
			return c;
		}
		String[] parts=name.split("\\.");
		String prefix="";
		int i=0;
		GridElement e=null;
		if(parts.length==1) {
			e=elementMap.get("");
			prefix="";
		}
		else if(parts.length>1) {
			prefix=parts[0];
			e=elementMap.get(parts[0]);
			i=1;
		}
		if(e!=null) {
			int ofs=0;
			int index=-1;
			int k=0;
			if(i+1<parts.length && parts[i+1].matches("\\d+"))
				ofs=Integer.parseInt(parts[i+1]);
			List<String> pa=e.getPanelAttributesAsList();
			if(parts[i].matches("\\d+")) {
				index=Integer.parseInt(parts[i]);
			}
			else {
				for (String line : pa) {
					if (line.startsWith(parts[i] + KeyValueFacet.SEP)) {
						index=k;
						break;
					}
					++k;
				}
			}
			if(index<0 || index+ofs>=pa.size()) {
				cacheData.put(name,null);
				return null;
			}
			else {
				index+=ofs;
				String newname=prefix+"."+String.valueOf(index);
				if(!name.equals(newname)) {
					c=new ChangePanelLines.ChangeElementItem(e,0,null,newname,null);
					cacheData.put(name,c);
				}
				String line=pa.get(index);
				int pos=line.indexOf('=');
				if(pos>0)
					c=new ChangePanelLines.ChangeElementItem(e,index,line.substring(0,pos),null,line.substring(pos+1));
				else
					c=new ChangePanelLines.ChangeElementItem(e,index,null,null,line);
				cacheData.put(newname,c);
				return c;
			}
		}
		else {
			cacheData.put(name,null);
			return null;
		}
	}
	private void setData(String name, String value) {
		ChangePanelLines.ChangeElementItem c=findData(name);
		if(c==null) return;
		c.newValue=value;
	}
	private String getData(String name) {
		ChangePanelLines.ChangeElementItem c=findData(name);
		if(c==null) return null;
		if(c.newValue!=null) return c.newValue;
		return c.oldValue;
	}
	private List<String> findProc(String a) {
//		mylog.append("find("+a+") ");
		return procMap.get(a);
	}
	private boolean selectNextRelation(String signal, NewGridElement ne, ArrayList<GridElement> na, boolean all, String exitAction) {
		List<Stickable> ls=ne.getStartStickables();
		GridElement elseLink=null;
		String elseAction=null;
		int n=0;
		for(Stickable s: ls) {
			if(s instanceof Relation) {
			Relation r=(Relation)s;
			String h=r.getHeader();
			String trigger="";
			if(h!=null) {
				trigger=h;
			}
			String guard="";
			String action="";
			int pos=trigger.indexOf('/');
			if(pos>=0) {
				action=trigger.substring(pos+1).trim();
				trigger=trigger.substring(0,pos).trim();
			}
			int gpos=trigger.indexOf('[');
			if(gpos>=0) {
				guard=trigger.substring(gpos);
				trigger=trigger.substring(0,gpos).trim();
			}
			if(testTrigger(signal,trigger)) {
				if(guard.equals("[else]")) {
					elseLink=r;
					elseAction=action;
				}
				else if(testGuard(guard)) {
					na.add(r);
					if(n==0) execAction(exitAction);
					execAction(action);
					++n;
					if(!all) break;
				}
			}
			else if(trigger.length()==0 && guard.equals("[else]") && elseLink==null) {
				elseLink=r;
				elseAction=action;
			}
			}
		}
		if(n==0 && elseLink!=null) {
			na.add(elseLink);
			execAction(exitAction);
			execAction(elseAction);
			++n;
		}
		return n>0;
	}
	private void initCacheData() {
		procMap=new HashMap<String,List<String>>(1);
		elementMap=new HashMap<String,GridElement>(1);
		for (GridElement e : drawpanel.getGridElements()) {
			if (e instanceof NewGridElement) {
				NewGridElement ne=(NewGridElement)e;
				if(ne.getId()==ElementId.ProcStore) {
					List<String> pa=ne.getPanelAttributesAsList();
					ArrayList<String> res=new ArrayList<String>();
					String name="";
					for(String line : pa) {
						if(line.startsWith("//")) ;
						else if(line.endsWith(":")) {
							if(name.length()>0) {
								if(res.size()>0) procMap.put(name,res);
							}
							res=new ArrayList<String>();
							name=line.substring(0,line.length()-1);
						}
						else {
							res.add(line);
						}
					}
					if(res.size()>0 && name.length()>0) procMap.put(name,res);
				}
				else if(ne.getId()==ElementId.DataStore) {
					String name=ne.getName();
					elementMap.put(name,ne);
				}
				else {
					String name=ne.getName();
					if(name.length()>0)
						elementMap.put(name,ne);
				}
			}
		}
		cacheData=new HashMap<String,ChangePanelLines.ChangeElementItem>(1);
		nCalls=0;
		args=null;
	}
	private void createChangePanelLines(Macro c) {
		ArrayList<ChangePanelLines.ChangeElementItem> changes=new ArrayList<ChangePanelLines.ChangeElementItem>();
		for(ChangePanelLines.ChangeElementItem ch: cacheData.values()) {
			if(ch!=null && ch.oldValue!=null && ch.newValue!=null && !ch.oldValue.equals(ch.newValue)) {
				changes.add(ch);
			}
		}
		if(changes.size()>0) c.getCommands().add(new ChangePanelLines(changes));
	}
	private String getDataSubstr(String s) {
		if(s.length()>0 && s.charAt(0)=='#') {
//			mylog.append("len("+s.substring(1)+") ");
			String v=getData(s.substring(1));
			if(v==null) return null;
			return String.valueOf(v.length());
		}
 		Pattern ptn = Pattern.compile("([<>])(-?\\d+)$");
		Matcher matcher = ptn.matcher(s);
		if(matcher.find()) {
//		mylog.append("sub("+s.substring(0,matcher.start())+","+matcher.group(1)+","+matcher.group(2)+") ");
			String op=matcher.group(1);
			int len=Integer.parseInt(matcher.group(2));
			String v=getDataSubstr(s.substring(0,matcher.start()));
			if(v==null) return null;
			if(op.equals(">")) {
				if(len>0) {
					if(v.length()>=len)
						return v.substring(v.length()-len,v.length());
					else
						return v;
				}
				else {
					if(v.length()>=-len)
						return v.substring(0,v.length()+len);
					else
						return "";
				}
			}
			else {
				if(len>0) {
					if(v.length()>=len)
						return v.substring(0,len);
					else
						return v;
				}
				else {
					if(v.length()>=-len)
						return v.substring(-len);
					else
						return "";
				}
			}
		}
		else {
//		mylog.append("getData("+s+") ");
			return getData(s);
		}
	}
	private String replaceSubst(String s) {
		StringBuilder a=new StringBuilder();
		String z;
		int i=0,p;
		while(i<s.length()) {
			p=s.indexOf('$',i);
			if(p<0 || p>=s.length()-1) {
				a.append(s,i,s.length());
				break;
			}
			else {
				a.append(s,i,p);
				char n=s.charAt(p+1);
				if(n=='(') {
					i=s.indexOf(')',p);
					if(i<0) {
						a.append(s,p,s.length());
						break;
					}
					else {
						z=getDataSubstr(s.substring(p+2,i));
						if(z!=null) a.append(z);
					}
					++i;
				}
				else if(n>='1' && n<='9') {
					int na=n-'1';
					if(args!=null && na<args.size()) a.append(args.get(na));
					i=p+2;
				}
				else if(n=='$') {
					a.append(n);
					i=p+2;
				}
				else {
					a.append('$');
					i=p+1;
				}
			}
		}
		return a.toString();
	}
	private void execAction(String a) {
		if(a==null || a.length()==0) return;
//		mylog.append("action("+a+") ");
		if(a.startsWith("++") || a.startsWith("--")) {
			String name=replaceSubst(a.substring(2).trim());
			String v=getData(name);
			if(v!=null) {
				try {
					int val=Integer.parseInt(v);
					val+=a.charAt(0)=='+'?1:-1;
					setData(name,String.valueOf(val));
				} catch(Throwable t) {}
			}
		}
		else if(a.startsWith("<")) {
			String signal=replaceSubst(a.substring(1).trim());
			if(signal.matches(signalRegex))
				signalQueue.offer(signal);
		}
		else if(a.indexOf('=')>0) {
			String name=a.substring(0,a.indexOf('=')).trim();
			String v=replaceSubst(a.substring(a.indexOf('=')+1));
			setData(name,v);
		}
		else if(nCalls<100) {
			++nCalls;
			List<String> saveArgs=args;
			execAction(findProc(setArgs(a,true)));
			args=saveArgs;
		}
	}
	private void execAction(List<String> a) {
		if(a==null || a.size()==0) return;
		boolean prevtest=false;
		for(String s:a) {
			if(s.length()>0) {
				if(s.charAt(0)=='[') {
					int pos=s.indexOf(']');
					if(pos>0) {
						String guard=s.substring(0,pos+1);
						if(guard.equals("[else]")) {
							if(!prevtest)
								execAction(s.substring(pos+1).trim());
							prevtest=false;
						}
						else {
							if(testGuard(guard)) {
								prevtest=true;
								execAction(s.substring(pos+1).trim());
							}
						}
					}
				}
				else {
					prevtest=false;
					execAction(s.trim());
				}
			}
			else {
				prevtest=false;
			}
		}
	}
	public Command emulationStep() {
		String signal="";
		if(!signalQueue.isEmpty())
			signal=signalQueue.poll();
		List<GridElement> ae=drawpanel.getActiveElements();
		if(ae==null || ae.size()==0) return null;
		initCacheData();
		ArrayList<GridElement> na=new ArrayList<GridElement>();
		ArrayList<GridElement> oa=new ArrayList<GridElement>();
		Macro c=new Macro(new ArrayList<Command>());
//		mylog=new StringBuilder();
		try {
		if(signal.length()==0) {
			execAction(findProc("timer"));
		}
		else {
			setArgs(signal,false);
		}
		for(GridElement e:ae) {
			if(e instanceof NewGridElement) {
			NewGridElement ne=(NewGridElement)e;
			String h=ne.getHeader();
			String t=ne.getSetting("type");
			if(ne instanceof Relation) {
				oa.add(ne);
				GridElement next=drawpanel.getElementOnEnd(ne);
				if(next!=null) na.add(next);
				if(next!=null && next instanceof com.baselet.element.elementnew.uml.State) {
					execAction(findAction(next,"entry"));
				}
			} 
			else if(ne instanceof com.baselet.element.elementnew.uml.State) {
				if(t!=null && t.equals("receiver")) {
					if(h!=null && testTrigger(signal,h)) {
						oa.add(ne);
						selectNextRelation("",ne,na,false,"");
					}
				}
				else if(t!=null && t.equals("sender")) {
					if(signalQueue.isEmpty()) {
						oa.add(ne);
						if(h!=null && h.matches(signalRegex))
							signalQueue.offer(h);
						selectNextRelation("",ne,na,false,"");
					}
				}
				else if(signal.length()>0){
					if(findInternalAction(ne,signal)) ;
					else if(selectNextRelation(signal,ne,na,false,findAction(ne,"exit"))) {
						oa.add(ne);
					}
					else
						execAction(findAction(ne,"do"));
				}
				else {
					execAction(findAction(ne,"do"));
				}
			}
			else if(ne instanceof com.baselet.element.elementnew.uml.SpecialState) {
				if(t!=null && t.equals("termination")) {
					c.getCommands().add(new ChangeElementSetting(ActiveFacet.KEY, null, ae));
					signal="";
				        return c;
				}
				if(selectNextRelation("",ne,na,false,""))
					oa.add(ne);
			}
			else if(ne instanceof com.baselet.element.elementnew.uml.SyncBarHorizontal || e instanceof com.baselet.element.elementnew.uml.SyncBarVertical) {
				int n=ne.getEndStickables().size();
				String a=ne.getActive();
				if(a.equals(String.valueOf(n))) {
					oa.add(ne);
					selectNextRelation("",ne,na,true,"");
				}
			}
			}
		}
		createChangePanelLines(c);
//		Notifier.getInstance().showInfo(String.valueOf(ae.size())+" - "+String.valueOf(oa.size())+" + "+String.valueOf(na.size()));
		if(oa.size()>0) c.getCommands().add(new ChangeElementSetting(ActiveFacet.KEY, null, oa));
		if(na.size()>0) c.getCommands().add(new ChangeElementSetting(ActiveFacet.KEY, '+', na));
//		Notifier.getInstance().showInfo("Ok: "+mylog.toString());
		if(c.getCommands().size()==0) return null;
		}
		catch(Throwable t) {
			Notifier.getInstance().showInfo("Error is simulation");
		}
		return c;
	}
	public boolean isEmulation() {
		return emulationMode;
	}
}
