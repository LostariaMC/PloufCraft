package fr.lumin0u.plouf.util;

import fr.worsewarn.cosmox.api.achievements.Achievement;
import fr.worsewarn.cosmox.api.achievements.AchievementVisibility;
import org.bukkit.Material;

public class Achievements
{
	//public static final Achievement PLOUFCRAFT = new Achievement("PloufCraft", Material.CRAFTING_TABLE, "Terminer tous les succès en PloufCraft", 0);
	public static final Achievement WIN_NO_WOOD = new Achievement("win_no_wood", "Du bois ?", Material.BIRCH_WOOD, "Gagner sans utiliser de bois (si vous en avez recu)");
	public static final Achievement CRAFT_WORKBENCH = new Achievement("craft_workbench", "Deux fois plus rapide", Material.CRAFTING_TABLE, "Crafter une table de craft").setDescriptionVisibility(AchievementVisibility.ON_OBTAIN);
	public static final Achievement CRAFT_FURNACE = new Achievement("craft_furnace", "Aventure minecraft", Material.FURNACE, "Crafter un four");
	public static final Achievement WIN_NO_UNIQUE = new Achievement("win_no_unique", "Bourrin", Material.OAK_SLAB, "Gagner sans avoir de crafts uniques");
	public static final Achievement PLAYER_INVENTORY_CRAFTING = new Achievement("player_inventory_crafting", "Comment ?", Material.GRASS_BLOCK, "Réaliser un craft sans ouvrir la table de craft").setDescriptionVisibility(AchievementVisibility.ON_OBTAIN);
	public static final Achievement TOOL_CRAFTING = new Achievement("tool_crafting", "Ca, je sais faire", Material.WOODEN_PICKAXE, "Crafter tous les outils en bois");
	public static final Achievement WIN_REMONTADA = new Achievement("win_remontada", "Remontada", Material.RAW_GOLD, "Gagner après avoir eu 10 points de retard grâce aux crafts uniques");
}
