package fr.lumin0u.plouf.util;

import fr.lumin0u.plouf.Plouf;
import fr.worsewarn.cosmox.api.apis.Gallery;
import fr.worsewarn.cosmox.tools.Language;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

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

	public static Component translate(Language language, String simpleKey, TagResolver resolver) {
		return Gallery.translate(Plouf.GAME_IDENTIFIER, language, simpleKey, resolver);
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
