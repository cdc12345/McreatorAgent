package org.cdc.mcreatoragent.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;


public class ZHConverter {


	private final Properties charMap = new Properties();
	private final Set<String> conflictingSets  = new HashSet<>();

	public static final int TRADITIONAL = 0;
	public static final int SIMPLIFIED = 1;
	private static final int NUM_OF_CONVERTERS = 2;
	private static final ZHConverter[] converters = new ZHConverter[NUM_OF_CONVERTERS];
	private static final String[]  propertyFiles = new String[2];

	static {
		propertyFiles[TRADITIONAL] = "/Hant.properties";
		propertyFiles[SIMPLIFIED] = "/Hans.properties";
	}



	/**
	 *
	 * @param targetType 0 for traditional and 1 for simplified
	 * @return
	 */
	public static ZHConverter getInstance(int targetType) {

		if (targetType >= 0 && targetType < NUM_OF_CONVERTERS) {

			if (converters[targetType] == null) {
				synchronized(ZHConverter.class) {
					if (converters[targetType] == null) {
						converters[targetType] = new ZHConverter(propertyFiles[targetType]);
					}
				}
			}
			return converters[targetType];

		} else {
			return null;
		}
	}

	public static String convert(String text, int converterType) {
		ZHConverter instance = getInstance(converterType);
		return instance.convert(text);
	}


	private ZHConverter(String propertyFile) {

	    InputStream is;


	    is = getClass().getResourceAsStream(propertyFile);

		if (is != null) {
			InputStreamReader reader = null;
			try {
				reader = new InputStreamReader(is);
				charMap.load(reader);
			} catch (FileNotFoundException ignore) {
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (reader != null)
						reader.close();
					is.close();
				} catch (IOException ignore) {
				}
			}
		}
		initializeHelper();
	}

	private void initializeHelper() {
		Map<Object,Integer> stringPossibilities = new HashMap<>();
		Iterator<Object> iter = charMap.keySet().iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			if (key.length() >= 1) {

				for (int i = 0; i < (key.length()); i++) {
					String keySubstring = key.substring(0, i + 1);
					if (stringPossibilities.containsKey(keySubstring)) {
						Integer integer = stringPossibilities.get(keySubstring);
						stringPossibilities.put(keySubstring,
								integer + 1);

					} else {
						stringPossibilities.put(keySubstring, 1);
					}

				}
			}
		}

		iter = stringPossibilities.keySet().iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			if (stringPossibilities.get(key) > 1) {
				conflictingSets.add(key);
			}
		}
	}

	public String convert(String in) {
		StringBuilder outString = new StringBuilder();
		StringBuilder stackString = new StringBuilder();

		for (int i = 0; i < in.length(); i++) {

			char c = in.charAt(i);
			String key = "" + c;
			stackString.append(key);

			if (conflictingSets.contains(stackString.toString())) {
			} else if (charMap.containsKey(stackString.toString())) {
				outString.append(charMap.get(stackString.toString()));
				stackString.setLength(0);
			} else {
				CharSequence sequence = stackString.subSequence(0, stackString.length()-1);
				stackString.delete(0, stackString.length()-1);
				flushStack(outString, new StringBuilder(sequence));
			}
		}

		flushStack(outString, stackString);

		return outString.toString();
	}


	private void flushStack(StringBuilder outString, StringBuilder stackString) {
		while (stackString.length() > 0){
			if (charMap.containsKey(stackString.toString())) {
					outString.append(charMap.get(stackString.toString()));
					stackString.setLength(0);

				} else {
					outString.append(stackString.charAt(0));
					stackString.delete(0, 1);
			}

		}
	}


	String parseOneChar(String c) {

		if (charMap.containsKey(c)) {
			return (String) charMap.get(c);

		}
		return c;
	}


}
