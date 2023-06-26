/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2022, Pylo, opensource contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.cdc.mcreatoragent.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.cdc.mcreatoragent.AgentClass;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
/**
 * e-mail: 3154934427@qq.com
 * 翻译api
 *
 * @author cdc123
 * @classname TranslatorUtils
 * @date 2022/10/9 16:35
 */
public class TranslatorUtils {

	public static String translateAuto(String origin){
		if (isEnglish(origin))
			return translateENToCN(origin);
		else
			return translateCNToEN(origin);
	}

	private static final HashMap<String,String> translation = new HashMap<>();
	public static String translateENToCN(String origin){
		if (origin == null) return "";
		if (!isEnglish(origin)) return origin;
		if (translation.containsKey(origin)) return translation.get(origin);
		String result;
		String code = URLEncoder.encode(origin,StandardCharsets.UTF_8);
		try {
			switch (AgentClass.config.getProperty("Translator.engine","Han")) {
				//				case "百度" -> result = translateBaidu(code, "auto", "zh");
				case "Kate" -> result = translateKate(code);
				case "Han" -> result = translateHan(code);
				default -> {
					return origin;
				}
			}
		} catch (Exception e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,"翻译引擎异常,返回信息为"+e.getMessage(),"翻译引擎崩溃",JOptionPane.WARNING_MESSAGE);
			return origin;
		}
		translation.put(origin,result);
		translation.put(result,origin);
		return result;
	}

	public static String translateCNToEN(String origin)  {
		if (origin == null) return "";
		if (isEnglish(origin)) return origin;
		if (translation.containsKey(origin)) return translation.get(origin);
		String result = origin;
		String code = URLEncoder.encode(origin,StandardCharsets.UTF_8);
		try {
			switch (AgentClass.config.getProperty("Translator.engine","Han")) {
//				case "百度" -> result = translateBaidu(code, "auto", "en");
				case "Kate" -> result = translateKate(code);
				case "Han" -> result = translateHan(code);
			default -> {
				return result;
			}
			}
		} catch (Exception e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,"翻译引擎异常,返回信息为"+e.getMessage(),"翻译引擎崩溃",JOptionPane.WARNING_MESSAGE);
			return result;
		}
		translation.put(origin,result);
		translation.put(result,origin);
		return result;
	}



	public static String translateKate(String origin) throws IOException {
		String urlString = "https://api.66mz8.com/api/translation.php?info=%s";
		URL url = new URL(String.format(urlString,origin));
		return new Gson().fromJson(new InputStreamReader(url.openStream()),JsonObject.class).get("fanyi").getAsString();
	}

	public static String translateHan(String origin) throws IOException {
		String urlString = "https://api.vvhan.com/api/fy?text=%s";
		URL url = new URL(String.format(urlString,origin));
		return new Gson().fromJson(new InputStreamReader(url.openStream()),JsonObject.class).getAsJsonObject("data").get("fanyi").getAsString();
	}


	private static final String englishMatch = "[a-zA-Z_:\s]+";
	/**
	 * 检查是否为英文,如果不是则返回否
	 * @param text 文本
	 * @return 英文和null返回是,否则返回false
	 */
	public static boolean isEnglish(String text){
		if (text == null) return true;
		return text.matches(englishMatch);
	}
}
