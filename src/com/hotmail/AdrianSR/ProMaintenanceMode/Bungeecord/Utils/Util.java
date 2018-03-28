package com.hotmail.AdrianSR.ProMaintenanceMode.Bungeecord.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.DurationFormatUtils;

import net.md_5.bungee.api.ChatColor;

public class Util {
	public static final String EMPTY_ID = "0-0-0-0-0"; // Easiest format
	public static final UUID EMPTY_UUID = UUID.fromString(EMPTY_ID);

	public static String wc2(String g) 
	{
		return g == null ? "null String" : ChatColor.translateAlternateColorCodes('&', g);
	}

	public static TimeUnit getUnit(String input) {
		TimeUnit u;
		switch (input.toLowerCase()) {
		case "error":
		default:
			return null;

		case "m":
		case "min":
		case "mins":
		case "minute":
		case "minutes":
			u = TimeUnit.MINUTES;
			break;

		case "h":
		case "hr":
		case "hrs":
		case "hour":
		case "hours":
			u = TimeUnit.HOURS;
			break;

		case "d":
		case "day":
		case "days":
			u = TimeUnit.DAYS;
			break;
		}
		return u;
	}

	public static String format(long miliseconds) 
	{
		return DurationFormatUtils.formatDuration(miliseconds, Config.TIME_FORMAT.toString());// "H:mm:ss");
	}

	public static void convertFileToUFT_8(File f, File newFile)
	{
		try {
			final FileInputStream fis = new FileInputStream(f);
		    final byte[] contents = new byte[fis.available()];
		    fis.read(contents, 0, contents.length);
		    final  String asString = new String(contents, "ISO8859_1");
		    final byte[] newBytes = asString.getBytes("UTF8");
		    final FileOutputStream fos = new FileOutputStream(newFile);
		    if (f.getPath().equalsIgnoreCase(newFile.getPath())) {
		    	if (f.getName().equalsIgnoreCase(newFile.getName())) {
		    		f.delete();
		    	}
		    }
		    fos.write(newBytes);
		    fos.close();
		    fis.close();
		} catch(Exception e) {
		    e.printStackTrace();
		}
	}
	
	public static File createNewUTF8File(File folder, String name) 
	{
		Validate.notNull(folder, "The folder cant be null");
		Validate.isTrue(folder.isDirectory(), "The folder must be a Directory");
		
		try {
			final File f = new File(folder, name);
			if (!f.exists()) {
				f.createNewFile();
			}
			final FileInputStream fis = new FileInputStream(f);
			final  byte[] contents = new byte[fis.available()];
		    fis.read(contents, 0, contents.length);
		    final String asString = new String(contents, "ISO8859_1");
		    final byte[] newBytes = asString.getBytes("UTF8");
		    final FileOutputStream fos = new FileOutputStream(new File(folder, name));
		    f.delete();
		    fos.write(newBytes);
		    fos.close();
		    fis.close();
		    return new File(folder, name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
