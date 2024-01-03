package fr.lumin0u.plouf.util;

import fr.lumin0u.plouf.Plouf;
import fr.worsewarn.cosmox.api.languages.Language;
import fr.worsewarn.cosmox.api.languages.LanguageManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class I18n
{
	public static String key(String simpleKey) {
		return key(Plouf.GAME_IDENTIFIER, simpleKey);
	}
	
	public static String key(String file, String simpleKey) {
		return file + "." + simpleKey;
	}
	
	public static String interpretable(String simpleKey) {
		return interpretable(Plouf.GAME_IDENTIFIER, simpleKey);
	}
	
	public static String interpretable(String file, String simpleKey) {
		return "@lang/" + key(file, simpleKey) + "/";
	}
	
	public static String translate(Language language, String simpleKey, Object... args) {
		return LanguageManager.getInstance().translate(key(simpleKey), language).formatted(args);
	}
	
	/*
	public static List<String> concatStrings(Object... things) {
		List<String> list = new ArrayList<>();
		for(Object thing : things) {
			if(thing instanceof Collection) {
				list.addAll((Collection<? extends String>) thing);
			}
			else if(thing != null) {
				list.add(thing.toString());
			}
		}
		
		return list;
	}*/
}
