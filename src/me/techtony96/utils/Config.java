package me.techtony96.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Scanner;

public class Config {
	
	private static boolean configExists = false;

	private static HashMap<String, String> cache = new HashMap<String, String>();

	public static String get(String key) {
		if (!cache.containsKey(key.toLowerCase())) {
			reloadConfig();
		}
		return cache.get(key);
	}

	public static void reloadConfig(){
		cache.clear();
		Scanner scanner;
		try {
			scanner = new Scanner(new File("config.txt"));
			while(scanner.hasNextLine()){
				String line = scanner.nextLine();
				if (line.startsWith("#"))
					continue;
				if(line.contains("=")){
					String key = line.split("=")[0].toLowerCase();
					String value;
					if (line.split("=").length > 1){
						value = line.split("=")[1];
					} else {
						value = "";
					}
					cache.put(key, value);
				}
			}
		} catch (FileNotFoundException e) {
			if (configExists == false){
				Logger.warning("Config file not found, creating one.");
				createConfig();
				reloadConfig();
			} else {
				Logger.error("Config file could not be created.");
				Logger.debug(e);
			}
			
		}
		
	}
	
	private static void createConfig(){
		configExists = true;
		try {
			PrintWriter writer = new PrintWriter("config.txt", "UTF-8");
			writer.println("Token=");
			writer.close();
		} catch (FileNotFoundException e) {
			Logger.error("File not found while creating config.txt");
			Logger.debug(e);
		} catch (UnsupportedEncodingException e) {
			Logger.error("Unsupported encoding exception encountered while creating config.txt");
			Logger.debug(e);
		}
		
	}

}
