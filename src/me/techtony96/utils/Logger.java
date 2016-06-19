package me.techtony96.utils;

public class Logger {

	private static boolean debug = true;

	public static void info(String s) {
		System.out.println("[Info] " + s);
	}

	public static void warning(String s) {
		System.out.println("[Warning] " + s);
	}

	public static void error(String s) {
		System.out.println("[ERROR] " + s);
	}

	public static void debug(String s) {
		if (debug)
			System.out.println(s);
	}
	
	public static void debug(Exception e){
		if (debug)
			e.printStackTrace();
	}

}
