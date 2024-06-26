package fr.lumin0u.plouf.util;

import com.google.common.collect.ImmutableSet;
import fr.worsewarn.cosmox.tools.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static java.util.function.Predicate.not;

public class Items
{
	private static ImmutableSet<Material> giveableItems;
	private static ImmutableSet<Material> ingredients;
	private static ImmutableSet<Material> noWoodGiveableItems;
	private static boolean finishedLoading = false;
	
	public static final I18nItemStack UNIQUE_CRAFTS_HEAD = new I18nItemStack(language -> new ItemBuilder(Material.PLAYER_HEAD)
			.setDisplayName(I18n.translate(language, "menu_unique_crafts_title"))
			.setLore(Utils.cutList(I18n.translate(language, "menu_unique_crafts_description"), 30, ChatColor.GRAY))
			.buildImmutable());
	
	public static void buildGiveableItems() {
		
		ingredients = Arrays.stream(Material.values())
				.filter(Items::calculateIsIngredient)
				.collect(ImmutableSet.toImmutableSet());
		
		giveableItems = ingredients.stream()
				.filter(mat -> !mat.name().matches("\\w+_SMITHING_TEMPLATE"))
				.collect(ImmutableSet.toImmutableSet());
		
		noWoodGiveableItems = giveableItems.stream().filter(not(Items::isWood)).collect(ImmutableSet.toImmutableSet());
		
		finishedLoading = true;
	}
	
	public static boolean isFinishedLoading() {
		return finishedLoading;
	}
	
	public static Set<Material> getIngredients() {
		return ingredients;
	}
	
	public static Set<Material> getGiveableItems() {
		return giveableItems;
	}
	
	public static Set<Material> getNoWoodGiveableItems() {
		return noWoodGiveableItems;
	}
	
	public static Material getRandomGiveableItem(Random random) {
		return giveableItems.stream().skip(random.nextInt(giveableItems.size())).findFirst().orElse(null);
	}
	public static Material getRandomNoWoodGiveableItem(Random random) {
		return noWoodGiveableItems.stream().skip(random.nextInt(noWoodGiveableItems.size())).findFirst().orElse(null);
	}
	
	public static final ItemStack DEFAULT_PICKAXE = new ItemBuilder(Material.IRON_PICKAXE).setUnbreakable(true).buildImmutable();
	
	private static boolean calculateIsIngredient(Material material) {
		for(@NotNull Iterator<Recipe> it = Bukkit.getServer().recipeIterator(); it.hasNext(); )
		{
			Recipe recipe = it.next();
			if(recipe instanceof ShapedRecipe shapedRecipe && shapedRecipe.getChoiceMap().values().stream().filter(Objects::nonNull).anyMatch(rc -> rc.test(new ItemStack(material))))
			{
				return true;
			}
			if(recipe instanceof ShapelessRecipe shapelessRecipe && shapelessRecipe.getChoiceList().stream().filter(Objects::nonNull).anyMatch(rc -> rc.test(new ItemStack(material))))
			{
				return true;
			}
		}
		return false;
	}
	
	public static boolean memIsIngredient(Material material) {
		return ingredients.contains(material);
	}
	
	public static boolean isWood(Material material) {
		return material.name().matches("(\\w+_PLANKS|\\w+_WOOD|\\w+_LOG|.*CRIMSON_STEM|.*WARPED_STEM|.*HYPHAE)") || material == Material.BAMBOO_BLOCK || material == Material.STRIPPED_BAMBOO_BLOCK;
	}
	
	public static boolean isLog(Material material) {
		return material.name().matches("(\\w+_WOOD|\\w+_LOG|.*CRIMSON_STEM|.*WARPED_STEM|.*HYPHAE)") || material == Material.BAMBOO_BLOCK || material == Material.STRIPPED_BAMBOO_BLOCK;
	}
}
