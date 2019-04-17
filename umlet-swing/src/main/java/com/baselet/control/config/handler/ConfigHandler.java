package com.baselet.control.config.handler;

import java.awt.Frame;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import com.baselet.control.basics.geom.Dimension;
import com.baselet.control.config.Config;
import com.baselet.control.config.SharedConfig;
import com.baselet.control.enums.Program;
import com.baselet.control.util.Path;
import com.baselet.control.util.RecentlyUsedFilesList;
import com.baselet.gui.BaseGUI;

public class ConfigHandler {

	private static final String PROGRAM_VERSION = "program_version";
	private static final String PROPERTIES_PANEL_FONTSIZE = "properties_panel_fontsize";
	private static final String DEFAULT_FONTSIZE = "default_fontsize";
	private static final String DEFAULT_FONTFAMILY = "default_fontfamily";
	private static final String SHOW_STICKINGPOLYGON = "show_stickingpolygon";
	private static final String SHOW_GRID = "show_grid";
	private static final String UI_MANAGER = "ui_manager";
	private static final String PRINT_PADDING = "print_padding";
	private static final String OPEN_FILE_HOME = "open_file_home";
	private static final String SAVE_FILE_HOME = "save_file_home";
	private static final String DEV_MODE = "dev_mode";
	private static final String LAST_USED_PALETTE = "last_used_palette";
	private static final String MAIN_SPLIT_POSITION = "main_split_position";
	private static final String RIGHT_SPLIT_POSITION = "right_split_position";
	private static final String START_MAXIMIZED = "start_maximized";
	private static final String MAIL_SPLIT_POSITION = "mail_split_position";
	private static final String PROGRAM_SIZE = "program_size";
	private static final String PROGRAM_LOCATION = "program_location";
	private static final String RECENT_FILES = "recent_files";

	public static void loadConfig() {

		Properties props = loadProperties();
		if (props.isEmpty()) {
			return;
		}

		Config cfg = Config.getInstance();

		cfg.setProgramVersion(getStringProperty(props, PROGRAM_VERSION, Program.getInstance().getVersion()));
		cfg.setDefaultFontsize(getIntProperty(props, DEFAULT_FONTSIZE, cfg.getDefaultFontsize()));
		cfg.setPropertiesPanelFontsize(getIntProperty(props, PROPERTIES_PANEL_FONTSIZE, cfg.getPropertiesPanelFontsize()));
		cfg.setDefaultFontFamily(getStringProperty(props, DEFAULT_FONTFAMILY, cfg.getDefaultFontFamily()));
		SharedConfig.getInstance().setShow_stickingpolygon(getBoolProperty(props, SHOW_STICKINGPOLYGON, SharedConfig.getInstance().isShow_stickingpolygon()));
		cfg.setShow_grid(getBoolProperty(props, SHOW_GRID, cfg.isShow_grid()));
		cfg.setUiManager(getStringProperty(props, UI_MANAGER, cfg.getUiManager()));
		cfg.setPrintPadding(getIntProperty(props, PRINT_PADDING, cfg.getPrintPadding()));
		cfg.setOpenFileHome(getStringProperty(props, OPEN_FILE_HOME, cfg.getOpenFileHome()));
		cfg.setSaveFileHome(getStringProperty(props, SAVE_FILE_HOME, cfg.getSaveFileHome()));
		SharedConfig.getInstance().setDev_mode(getBoolProperty(props, DEV_MODE, SharedConfig.getInstance().isDev_mode()));
		cfg.setLastUsedPalette(getStringProperty(props, LAST_USED_PALETTE, cfg.getLastUsedPalette()));
		cfg.setMain_split_position(getIntProperty(props, MAIN_SPLIT_POSITION, cfg.getMain_split_position()));
		cfg.setRight_split_position(getIntProperty(props, RIGHT_SPLIT_POSITION, cfg.getRight_split_position()));
		cfg.setStart_maximized(getBoolProperty(props, START_MAXIMIZED, cfg.isStart_maximized()));

		// In case of start_maximized=true we don't store any size or location information
		if (!cfg.isStart_maximized()) {
			cfg.setProgram_size(getDimensionProperty(props, PROGRAM_SIZE, cfg.getProgram_size()));
			cfg.setProgram_location(getPointProperty(props, PROGRAM_LOCATION, cfg.getProgram_location()));
		}

		String recentFiles = props.getProperty(RECENT_FILES);
		if (recentFiles != null) {
			RecentlyUsedFilesList.getInstance().addAll(Arrays.asList(props.getProperty(RECENT_FILES).split("\\|")));
		}


	}

	public static void saveConfig(BaseGUI gui) {
		try {
			Path.safeDeleteFile(new File(Path.legacyConfig()), false); // delete legacy cfg file if it exists and replace it with osConform one
			File configfile = new File(Path.osConformConfig());
			Path.safeDeleteFile(configfile, false);
			Path.safeCreateFile(configfile, false);

			Config cfg = Config.getInstance();
			Properties props = new Properties();

			props.setProperty(PROGRAM_VERSION, Program.getInstance().getVersion());
			props.setProperty(DEFAULT_FONTSIZE, Integer.toString(cfg.getDefaultFontsize()));
			props.setProperty(PROPERTIES_PANEL_FONTSIZE, Integer.toString(cfg.getPropertiesPanelFontsize()));
			props.setProperty(DEFAULT_FONTFAMILY, cfg.getDefaultFontFamily());
			props.setProperty(SHOW_STICKINGPOLYGON, Boolean.toString(SharedConfig.getInstance().isShow_stickingpolygon()));
			props.setProperty(SHOW_GRID, Boolean.toString(cfg.isShow_grid()));
			props.setProperty(UI_MANAGER, cfg.getUiManager());
			props.setProperty(PRINT_PADDING, Integer.toString(cfg.getPrintPadding()));
			props.setProperty(OPEN_FILE_HOME, cfg.getOpenFileHome());
			props.setProperty(SAVE_FILE_HOME, cfg.getSaveFileHome());
			props.setProperty(DEV_MODE, Boolean.toString(SharedConfig.getInstance().isDev_mode()));
			props.setProperty(LAST_USED_PALETTE, cfg.getLastUsedPalette());

			props.setProperty(MAIN_SPLIT_POSITION, Integer.toString(gui.getMainSplitPosition()));
			props.setProperty(RIGHT_SPLIT_POSITION, Integer.toString(gui.getRightSplitPosition()));
			if (gui.saveWindowSizeInConfig()) {
				// If the window is maximized in any direction this fact is written in the cfg
				Frame topContainer = gui.getMainFrame();
				if ((topContainer.getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
					props.setProperty(START_MAXIMIZED, "true");
				} // Otherwise the size and the location is written in the cfg
				else {
					props.setProperty(START_MAXIMIZED, "false");
					props.setProperty(PROGRAM_SIZE, topContainer.getSize().width + "," + topContainer.getSize().height);
					props.setProperty(PROGRAM_LOCATION, topContainer.getLocation().x + "," + topContainer.getLocation().y);
				}
			}
			if (!RecentlyUsedFilesList.getInstance().isEmpty()) {
				StringBuilder sb = new StringBuilder("");
				for (String recentFile : RecentlyUsedFilesList.getInstance()) {
					sb.append(recentFile).append("|");
				}
				sb.setLength(sb.length() - 1);
				props.setProperty(RECENT_FILES, sb.toString());
			}

			FileOutputStream outStream = new FileOutputStream(configfile);
			try {
				props.store(outStream, null);
			} finally {
				outStream.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private static Properties loadProperties() {
		Properties result = new Properties();

		if (Path.hasOsConformConfig()) {
			result = loadPropertiesFromFile(Path.osConformConfig());
		}
		else if (Path.hasLegacyConfig()) {
			result = loadPropertiesFromFile(Path.legacyConfig());
		}

		return result;
	}

	private static Properties loadPropertiesFromFile(String filePath) {
		Properties result = new Properties();

		try {
			FileInputStream inputStream = new FileInputStream(filePath);
			try {
				result.load(inputStream);
			} finally {
				inputStream.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private static int getIntProperty(Properties props, String key, int defaultValue) {
		String result = props.getProperty(key);
		if (result != null) {
			try {
				return Integer.parseInt(result);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return defaultValue;
	}

	private static boolean getBoolProperty(Properties props, String key, boolean defaultValue) {
		String result = props.getProperty(key);
		if (result != null) {
			return Boolean.parseBoolean(result);
		}
		return defaultValue;
	}

	private static String getStringProperty(Properties props, String key, String defaultValue) {
		return props.getProperty(key, defaultValue);
	}

	private static Dimension getDimensionProperty(Properties props, String key, Dimension defaultValue) {
		String result = props.getProperty(key);
		if (result != null) {
			try {
				int x = Integer.parseInt(result.substring(0, result.indexOf(",")));
				int y = Integer.parseInt(result.substring(result.indexOf(",") + 1));
				return new Dimension(x, y);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return defaultValue;
	}

	private static Point getPointProperty(Properties props, String key, Point defaultValue) {
		String result = props.getProperty(key);
		if (result != null) {
			try {
				int x = Integer.parseInt(result.substring(0, result.indexOf(",")));
				int y = Integer.parseInt(result.substring(result.indexOf(",") + 1));
				return new Point(x, y);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return defaultValue;
	}
}
