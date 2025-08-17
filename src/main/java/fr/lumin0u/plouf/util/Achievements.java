package fr.lumin0u.plouf.util;

import fr.worsewarn.cosmox.api.achievements.Achievement;
import fr.worsewarn.cosmox.api.achievements.AchievementDTO;
import fr.worsewarn.cosmox.api.achievements.AchievementVisibility;
import org.bukkit.Material;

public class Achievements
{
	//public static final Achievement PLOUFCRAFT = new Achievement("PloufCraft", Material.CRAFTING_TABLE, "Terminer tous les succès en PloufCraft", 0);
	public static final AchievementDTO WIN_NO_WOOD = new AchievementDTO("win_no_wood", "Du bois ?", Material.BIRCH_WOOD, "Gagner sans utiliser de bois (si vous en avez recu)");
	public static final AchievementDTO CRAFT_WORKBENCH = new AchievementDTO("craft_workbench", "Deux fois plus rapide", Material.CRAFTING_TABLE, "Crafter une table de craft").setDescriptionVisibility(AchievementVisibility.ON_OBTAIN);
	public static final AchievementDTO CRAFT_FURNACE = new AchievementDTO("craft_furnace", "Aventure minecraft", Material.FURNACE, "Crafter un four");
	public static final AchievementDTO WIN_NO_UNIQUE = new AchievementDTO("win_no_unique", "Bourrin", Material.OAK_SLAB, "Gagner sans avoir de crafts uniques");
	public static final AchievementDTO PLAYER_INVENTORY_CRAFTING = new AchievementDTO("player_inventory_crafting", "Comment ?", Material.GRASS_BLOCK, "Réaliser un craft sans ouvrir la table de craft").setDescriptionVisibility(AchievementVisibility.ON_OBTAIN);
	public static final AchievementDTO TOOL_CRAFTING = new AchievementDTO("tool_crafting", "Ca, je sais faire", Material.WOODEN_PICKAXE, "Crafter tous les outils en bois");
	public static final AchievementDTO WIN_REMONTADA = new AchievementDTO("win_remontada", "Remontada", Material.RAW_GOLD, "Gagner après avoir eu 10 points de retard grâce aux crafts uniques");
}
