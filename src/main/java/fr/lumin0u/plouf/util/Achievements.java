package fr.lumin0u.plouf.util;

import fr.worsewarn.cosmox.api.achievements.Achievement;
import org.bukkit.Material;

public class Achievements
{
	public static final Achievement PLOUFCRAFT = new Achievement(3500, "PloufCraft", Material.CRAFTING_TABLE, "Terminer tous les succès en PloufCraft", 0);
	public static final Achievement WIN_NO_WOOD = new Achievement(3501, "Du bois ?", Material.BIRCH_WOOD, "Gagner sans utiliser de bois (si vous en avez recu)", 3500);
	public static final Achievement CRAFT_WORKBENCH = new Achievement(3502, "Deux fois plus rapide", Material.CRAFTING_TABLE, "Crafter une table de craft", 3500).setDescriptionVisibility(false, true);
	public static final Achievement CRAFT_FURNACE = new Achievement(3503, "Aventure minecraft", Material.FURNACE, "Crafter un four", 3500);
	public static final Achievement WIN_NO_UNIQUE = new Achievement(3504, "Bourrin", Material.OAK_SLAB, "Gagner sans avoir de crafts uniques", 3500);
	public static final Achievement PLAYER_INVENTORY_CRAFTING = new Achievement(3505, "Comment ?", Material.GRASS_BLOCK, "Réaliser un craft sans ouvrir la table de craft", 3500).setDescriptionVisibility(false, true);
	public static final Achievement TOOL_CRAFTING = new Achievement(3506, "Ca, je sais faire", Material.WOODEN_PICKAXE, "Crafter tous les outils en bois", 3500);
	public static final Achievement WIN_REMONTADA = new Achievement(3507, "Remontada", Material.RAW_GOLD, "Gagner après avoir eu 10 points de retard grâce aux crafts uniques", 3500);
}
