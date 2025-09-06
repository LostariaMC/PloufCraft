package fr.lumin0u.plouf.util;

import fr.worsewarn.cosmox.api.achievements.AchievementVisibility;
import fr.worsewarn.cosmox.api.statistics.IStatistic;
import fr.worsewarn.cosmox.api.statistics.UploadRule;
import org.bukkit.Material;

public enum PloufStatistic implements IStatistic
{
    PLOUF_ITEMS_CRAFTED(I18n.interpretable("statistics_crafts"), I18n.interpretable("statistics_crafts_desc"), Material.CRAFTING_TABLE, true, true, UploadRule.ADDITION),
    PLOUF_UNIQUE_ITEMS_CRAFTED(I18n.interpretable("statistics_unique_crafts"), I18n.interpretable("statistics_unique_crafts_desc"), Material.EVOKER_SPAWN_EGG, true, true, UploadRule.ADDITION),
    PLOUF_POINTS(I18n.interpretable("statistics_points"), I18n.interpretable("statistics_points_desc"), Material.GOLD_NUGGET, true, true, UploadRule.ADDITION);

    private final String name;
    private final String description;
    private final Material material;
    private final String customOperation;
    private final boolean leaderboardVisible;
    private final boolean statisticVisible;
    private final UploadRule uploadRule;

    private PloufStatistic(String name, String description, Material material, String customOperation, boolean leaderboardVisible, boolean statisticVisible, UploadRule uploadRule) {
        this.name = name;
        this.description = description;
        this.material = material;
        this.customOperation = customOperation;
        this.leaderboardVisible = leaderboardVisible;
        this.statisticVisible = statisticVisible;
        this.uploadRule = uploadRule;
    }

    private PloufStatistic(String name, String description, Material material, boolean leaderboardVisible, boolean statisticVisible, UploadRule uploadRule) {
        this(name, description, material, (String)null, leaderboardVisible, statisticVisible, uploadRule);
    }

    private PloufStatistic(String name, String description, Material material, boolean leaderboardVisible, boolean statisticVisible) {
        this(name, description, material, (String)null, leaderboardVisible, statisticVisible, UploadRule.ADDITION);
    }

    private PloufStatistic(String name, String description, Material material) {
        this(name, description, material, (String)null, false, false, UploadRule.ADDITION);
    }

    public String getStatisticKey() {
        return this.name().toLowerCase();
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getCustomOperation() {
        return this.customOperation;
    }

    public Material getMaterial() {
        return this.material;
    }

    public boolean isLeaderboardVisible() {
        return this.leaderboardVisible;
    }

    public boolean isStatisticVisible() {
        return this.statisticVisible;
    }

    public UploadRule getUploadRule() {
        return this.uploadRule;
    }
}
