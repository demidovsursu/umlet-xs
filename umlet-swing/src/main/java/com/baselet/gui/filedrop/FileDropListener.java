package com.baselet.gui.filedrop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baselet.control.Main;
import com.baselet.diagram.Notifier;

public class FileDropListener implements FileDrop.Listener {

	private static final Logger log = LoggerFactory.getLogger(FileDropListener.class);

	@Override
	public void filesDropped(File[] files) {
		List<String> filenames = new ArrayList<String>();
		for (File file : files) {
			try {
				String filename = file.getCanonicalPath();
				filenames.add(filename);
			} catch (IOException e) {
				log.error("Cannot open file dropped", e);
				Notifier.getInstance().showError("Cannot open file dropped");
			}
		}

		for (String filename : filenames) {
			Main.getInstance().doOpen(filename);
		}
	}

}
