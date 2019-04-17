package com.baselet.control.config;

import java.awt.Font;
import java.awt.Point;
import java.io.File;

import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baselet.control.basics.geom.Dimension;
import com.baselet.control.constants.SystemInfo;
import com.baselet.control.enums.Os;
import com.baselet.control.enums.Program;
import com.baselet.control.enums.RuntimeType;

public class Config {

	private static Logger log = LoggerFactory.getLogger(Config.class);

	private static Config instance = new Config();

	public static Config getInstance() {
		return instance;
	}

	private final String DEFAULT_FILE_HOME = System.getProperty("user.dir");

	private String uiManager;
	private String openFileHome = DEFAULT_FILE_HOME;
	private String saveFileHome = DEFAULT_FILE_HOME;
	private String programVersion;

	private String lastUsedPalette = ""; // default is empty string not null because null cannot be stored as property
	private boolean checkForUpdates = true;
	private int printPadding = 20;
	private Point program_location = new Point(5, 5);
	private Dimension program_size = new Dimension(960, 750);
	private int mail_split_position = 250;
	private int right_split_position = 400;
	private int main_split_position = 600;
	private boolean show_grid = false;
	private boolean start_maximized = false;
	private String defaultFontFamily = Font.SANS_SERIF;
	private Integer defaultFontsize = 14;
	private Integer defaultSimStep = 1000;
	private Integer propertiesPanelFontsize = 11;

	public Config() {
		if (Program.getInstance().getRuntimeType() != RuntimeType.BATCH) { // batchmode shouldn't access UIManager.class
			initUiManager();
		}
	}

	private void initUiManager() {
		// The default MacOS theme looks ugly, therefore we set metal
		if (SystemInfo.OS == Os.MAC) {
			uiManager = "javax.swing.plaf.metal.MetalLookAndFeel";
		}
		else if (Program.getInstance().getRuntimeType() == RuntimeType.ECLIPSE_PLUGIN && UIManager.getSystemLookAndFeelClassName().equals("com.sun.java.swing.plaf.gtk.GTKLookAndFeel")) {
			uiManager = "javax.swing.plaf.metal.MetalLookAndFeel";
		}
		else {
			uiManager = UIManager.getSystemLookAndFeelClassName();
		}
	}

	public String getUiManager() {
		return uiManager;
	}

	public void setUiManager(String uiManager) {
		this.uiManager = uiManager;
	}

	public String getOpenFileHome() {
		return getFileHelper(openFileHome, DEFAULT_FILE_HOME);
	}

	public void setOpenFileHome(String openFileHome) {
		log.trace("setting openFileHome path to: " + openFileHome);
		this.openFileHome = openFileHome;
	}

	public String getSaveFileHome() {
		return getFileHelper(saveFileHome, DEFAULT_FILE_HOME);
	}

	private String getFileHelper(String fileLocToCheck, String defaultValue) {
		try {
			if (new File(fileLocToCheck).exists()) {
				return fileLocToCheck;
			}
		} catch (Exception e) {/* nothing to do */}

		// if stored location doesn't exist or there is an exception while accessing the location, return default value
		return defaultValue;
	}

	public void setSaveFileHome(String saveFileHome) {
		log.trace("setting saveFileHome path to: " + saveFileHome);
		this.saveFileHome = saveFileHome;
	}

	public void setProgramVersion(String cfgVersion) {
		programVersion = cfgVersion;
	}

	public String getProgramVersion() {
		return programVersion;
	}

	public String getLastUsedPalette() {
		return lastUsedPalette;
	}

	public void setLastUsedPalette(String lastUsedPalette) {
		this.lastUsedPalette = lastUsedPalette;
	}


	public int getPrintPadding() {
		return printPadding;
	}

	public void setPrintPadding(int printPadding) {
		this.printPadding = printPadding;
	}

	public Point getProgram_location() {
		return program_location;
	}

	public void setProgram_location(Point program_location) {
		this.program_location = program_location;
	}

	public Dimension getProgram_size() {
		return program_size;
	}

	public void setProgram_size(Dimension program_size) {
		this.program_size = program_size;
	}

	public int getMail_split_position() {
		return mail_split_position;
	}

	public void setMail_split_position(int mail_split_position) {
		this.mail_split_position = mail_split_position;
	}

	public int getRight_split_position() {
		return right_split_position;
	}

	public void setRight_split_position(int right_split_position) {
		this.right_split_position = right_split_position;
	}

	public int getMain_split_position() {
		return main_split_position;
	}

	public void setMain_split_position(int main_split_position) {
		this.main_split_position = main_split_position;
	}


	public boolean isShow_grid() {
		return show_grid;
	}

	public void setShow_grid(boolean show_grid) {
		this.show_grid = show_grid;
	}

	public boolean isStart_maximized() {
		return start_maximized;
	}

	public void setStart_maximized(boolean start_maximized) {
		this.start_maximized = start_maximized;
	}

	public String getDefaultFontFamily() {
		return defaultFontFamily;
	}

	public void setDefaultFontFamily(String defaultFontFamily) {
		this.defaultFontFamily = defaultFontFamily;
	}

	public Integer getDefaultFontsize() {
		return defaultFontsize;
	}

	public void setDefaultFontsize(Integer defaultFontsize) {
		this.defaultFontsize = defaultFontsize;
	}

	public Integer getDefaultSimStep() {
		return defaultSimStep;
	}

	public void setDefaultSimStep(Integer defaultSimStep) {
		this.defaultSimStep = defaultSimStep;
	}

	public Integer getPropertiesPanelFontsize() {
		return propertiesPanelFontsize;
	}

	public void setPropertiesPanelFontsize(Integer propertiesPanelFontsize) {
		this.propertiesPanelFontsize = propertiesPanelFontsize;
	}

}
