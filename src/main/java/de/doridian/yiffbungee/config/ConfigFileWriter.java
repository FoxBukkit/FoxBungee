package de.doridian.yiffbungee.config;

import java.io.*;

public class ConfigFileWriter extends FileWriter {
	public ConfigFileWriter(String file) throws IOException {
		super("plugins/YiffBungee/" + file);
	}
}
