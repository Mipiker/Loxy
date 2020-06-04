package fr.mipiker.game;

import java.io.*;
import java.util.Properties;

public class Settings {

	private static final File file = new File("general.settings");

	private static Properties prop = new Properties();

	public static int RENDER_DISTANCE = 5;
	public static int DEFAULT_CHUNK_SIZE = 20;

	public static void save() {
		prop.setProperty("RENDER_DISTANCE", Integer.toString(RENDER_DISTANCE));
		prop.setProperty("DEFAULT_CHUNK_SIZE", Integer.toString(DEFAULT_CHUNK_SIZE));
		try (OutputStream os = new FileOutputStream(file)) {
			prop.store(os, "Loxy setting");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("[Error] Couldn't save settings");
		}
	}

	public static void load() {
		try (InputStream is = new FileInputStream(file)) {
			prop.load(is);
			RENDER_DISTANCE = Integer.parseInt((String) prop.get("RENDER_DISTANCE"));
			DEFAULT_CHUNK_SIZE = Integer.parseInt((String) prop.get("DEFAULT_CHUNK_SIZE"));
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("[Error] Couldn't load settings");
		}
	}
}
