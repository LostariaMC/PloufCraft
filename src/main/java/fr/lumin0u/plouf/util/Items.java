package fr.lumin0u.plouf.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Items
{
	private static ImmutableSet<Material> giveableItems;
	private static ImmutableSet<Material> noWoodGiveableItems;
	
	public static final ItemStack UNIQUE_CRAFTS_HEAD = new ItemBuilder(Material.PLAYER_HEAD).setDisplayName("§eCrafts uniques des joueurs").setLore(" §e§l> §fClique §7pour voir les crafts uniques", "§7des autres joueurs").buildImmutable();
	
	public static void buildGiveableItems() {
		
		giveableItems = Arrays.stream(Material.values())
				.filter(mat -> !mat.name().matches("\\w+_SMITHING_TEMPLATE"))
				.filter(Items::isIngredient).collect(ImmutableSet.toImmutableSet());
		
		noWoodGiveableItems = giveableItems.stream().filter(material -> !material.name().matches("(\\w+_PLANKS|\\w+_WOOD|\\w+_LOG|.*CRIMSON_STEM|.*WARPED_STEM)")).collect(ImmutableSet.toImmutableSet());
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
	
	public static boolean isIngredient(Material material) {
		for(@NotNull Iterator<Recipe> it = Bukkit.getServer().recipeIterator(); it.hasNext(); )
		{
			Recipe recipe = it.next();
			if(recipe instanceof ShapedRecipe shapedRecipe && shapedRecipe.getChoiceMap().values().stream().anyMatch(rc -> rc.test(new ItemStack(material))))
			{
				return true;
			}
			if(recipe instanceof ShapelessRecipe shapelessRecipe && shapelessRecipe.getChoiceList().stream().anyMatch(rc -> rc.test(new ItemStack(material))))
			{
				return true;
			}
		}
		return false;
	}
}
