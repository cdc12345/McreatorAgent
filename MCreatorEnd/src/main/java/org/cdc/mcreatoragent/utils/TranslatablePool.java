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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mcreator.io.FileIO;
import org.cdc.mcreatoragent.AgentClass;

import java.io.*;
import java.net.URL;
import java.util.Locale;
import java.util.Objects;


/**
 * e-mail: 3154934427@qq.com
 *	翻译池,便于贡献者翻译
 *	不要出bug啊..
 * @author cdc123
 * @classname TranslatablePool
 * @date 2022/8/17 9:30
 */
public class TranslatablePool {

	private final JsonObject json;
	private static TranslatablePool instance;
	public static TranslatablePool getPool(){
		if (instance == null) {
				instance = new TranslatablePool();
		}
		return instance;
	}

	private TranslatablePool() {
		InputStream defaultPoolInput = null;
		File pool = new File("tra","pools.tra");
		if (!AgentClass.getInstance().isEnable("CNPool.forceInnerPool")) {
			try {
				URL url = new URL(
						"https://ghproxy.com/https://github.com/cdc12345/MCreator-CNPool/raw/main/pools.tra");
				defaultPoolInput = url.openStream();
			} catch (IOException ignore) {
				if (pool.exists()) {
					try {
						defaultPoolInput = new FileInputStream(pool);
					} catch (FileNotFoundException ignore1) {
					}
				}
			}
		}
		json = new Gson().fromJson(new InputStreamReader(
						Objects.requireNonNullElse(defaultPoolInput,this.getClass().getResourceAsStream("/pools.tra"))),
				JsonObject.class);
		FileIO.writeStringToFile(json.toString(),pool);
	}
	public boolean containValue(String key){
		return containValue("",key);
	}

	public boolean containValue(String nameSpace,String key){
		if (key == null){
			return false;
		}
		String lowerKey = key.toLowerCase(Locale.ENGLISH).replace('_',' ');
		if (nameSpace != null&&!"".equals(nameSpace)){
			lowerKey = nameSpace+":"+lowerKey;
		}
		return json.has(lowerKey);
	}

	public String getValue(String key){
		return getValue("",key);
	}

	public String getValue(String nameSpace,final String key){
		if (key == null){
			return null;
		}
		String lowerKey = key.toLowerCase(Locale.ENGLISH).replace('_',' ');
		String oLowerKey = lowerKey;
		if (nameSpace != null&&!"".equals(nameSpace)){
			oLowerKey = nameSpace+":"+lowerKey;
		}
		try {
			JsonElement element = json.get(oLowerKey);
			return element.getAsString();
		} catch (Exception e){
			try {
				return json.get(lowerKey).getAsString();
			} catch (NullPointerException en) {
				return key;
			}
		}
	}



}
