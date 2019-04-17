package com.baselet.control.enums;

public enum FormatLabels {
	UNDERLINE("_"),
	BOLD("*"),
	ITALIC("/"),
	BOXED("#"),
	LEFT("<"),
	RIGHT(">"),
	CENTER("|");

	private String value;

	private FormatLabels(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
