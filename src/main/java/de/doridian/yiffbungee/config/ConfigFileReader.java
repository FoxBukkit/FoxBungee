package de.doridian.yiffbungee.config;

import de.doridian.yiffbungee.main.YiffBungee;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class ConfigFileReader extends FileReader {
	public ConfigFileReader(String file) throws FileNotFoundException {
		super(new File(YiffBungee.instance.getDataFolder(), file));
	}
}
