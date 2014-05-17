package de.doridian.yiffbungee.config;

import de.doridian.yiffbungee.main.YiffBungee;

import java.io.*;

public class ConfigFileWriter extends FileWriter {
	public ConfigFileWriter(String file) throws IOException {
        super(new File(YiffBungee.instance.getDataFolder(), file));
	}
}
