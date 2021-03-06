package fr.mipiker.game;

import java.io.*;
import java.util.Properties;

public class Settings {

	private static final File file = new File("general.settings");

	private static Properties prop = new Properties();

	public static int RENDER_DISTANCE = 5;
	public static int DEFAULT_CHUNK_SIZE = 20;
	public static String LAST_PLAYED_MAP_NAME = "";
	public static int AUTO_SAVE_TIME = 10; // In seconds
	public static boolean SOUNDS = true;

	public static void save() {
		prop.setProperty("RENDER_DISTANCE", Integer.toString(RENDER_DISTANCE));
		prop.setProperty("DEFAULT_CHUNK_SIZE", Integer.toString(DEFAULT_CHUNK_SIZE));
		prop.setProperty("LAST_PLAYED_MAP_NAME", LAST_PLAYED_MAP_NAME);
		prop.setProperty("AUTO_SAVE_TIME", Integer.toString(AUTO_SAVE_TIME));
		prop.setProperty("SOUNDS", Boolean.toString(SOUNDS));
		try (OutputStream os = new FileOutputStream(file)) {
			prop.store(os, "Loxy general settings");
			System.out.println("[Info] Settings saved");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("[Error] Couldn't save settings");
		}
	}

	public static void load() {
		if (!file.exists())
			save();
		try (InputStream is = new FileInputStream(file)) {
			prop.load(is);
			RENDER_DISTANCE = Integer.parseInt((String) prop.get("RENDER_DISTANCE"));
			DEFAULT_CHUNK_SIZE = Integer.parseInt((String) prop.get("DEFAULT_CHUNK_SIZE"));
			LAST_PLAYED_MAP_NAME = (String) prop.get("LAST_PLAYED_MAP_NAME");
			AUTO_SAVE_TIME = Integer.parseInt((String) prop.get("AUTO_SAVE_TIME"));
			SOUNDS = Boolean.parseBoolean((String) prop.get("SOUNDS"));
			System.out.println("[Info] Settings loaded");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("[Error] Couldn't load settings");
		}
	}
}
