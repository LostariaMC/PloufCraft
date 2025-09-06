package fr.lumin0u.plouf.util;

import fr.worsewarn.cosmox.api.achievements.AchievementDTO;
import fr.worsewarn.cosmox.api.achievements.AchievementVisibility;
import fr.worsewarn.cosmox.api.achievements.IAchievement;
import org.bukkit.Material;

public enum PloufAchievement implements IAchievement
{
	//public static final Achievement PLOUFCRAFT = new Achievement("PloufCraft", Material.CRAFTING_TABLE, "Terminer tous les succès en PloufCraft", 0);
	WIN_NO_WOOD("win_no_wood", "Du bois ?", Material.BIRCH_WOOD, "Gagner sans utiliser de bois (si vous en avez recu)"),
	CRAFT_WORKBENCH("craft_workbench", "Deux fois plus rapide", Material.CRAFTING_TABLE, "Crafter une table de craft", AchievementVisibility.ON_OBTAIN, AchievementVisibility.ALWAYS),
	CRAFT_FURNACE("craft_furnace", "Aventure minecraft", Material.FURNACE, "Crafter un four"),
	WIN_NO_UNIQUE("win_no_unique", "Bourrin", Material.OAK_SLAB, "Gagner sans avoir de crafts uniques"),
	PLAYER_INVENTORY_CRAFTING("player_inventory_crafting", "Comment ?", Material.GRASS_BLOCK, "Réaliser un craft sans ouvrir la table de craft", AchievementVisibility.ON_OBTAIN, AchievementVisibility.ALWAYS),
	TOOL_CRAFTING("tool_crafting", "Ca, je sais faire", Material.WOODEN_PICKAXE, "Crafter tous les outils en bois"),
	WIN_REMONTADA("win_remontada", "Remontada", Material.RAW_GOLD, "Gagner après avoir eu 10 points de retard grâce aux crafts uniques");


    private String identifier;
    private String name;
    private Material material;
    private String description;
    private AchievementVisibility descriptionVisibility;
    private AchievementVisibility titleVisibility;

    private PloufAchievement(String identifier, String name, Material material, String description, AchievementVisibility descriptionVisibility, AchievementVisibility titleVisibility) {
        this.identifier = identifier;
        this.name = name;
        this.material = material;
        this.description = description;
        this.descriptionVisibility = descriptionVisibility;
        this.titleVisibility = titleVisibility;
    }

    private PloufAchievement(String identifier, String name, Material material, String description) {
        this(identifier, name, material, description, AchievementVisibility.ALWAYS, AchievementVisibility.ALWAYS);
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public Material getMaterial() {
        return this.material;
    }

    public AchievementVisibility getDescriptionVisibility() {
        return this.descriptionVisibility;
    }

    public AchievementVisibility getTitleVisibility() {
        return this.titleVisibility;
    }
}
