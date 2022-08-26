package fr.lumin0u.plouf.util;

import com.google.common.collect.ImmutableList;
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
	private static ImmutableList<Material> giveableItems;
	private static ImmutableList<Material> noWoodGiveableItems;
	
	public static void buildGiveableItems() {
		
		giveableItems = Arrays.stream(Material.values()).filter(mat ->
				{
					for(@NotNull Iterator<Recipe> it = Bukkit.getServer().recipeIterator(); it.hasNext(); )
					{
						Recipe recipe = it.next();
						if(recipe instanceof ShapedRecipe shapedRecipe && shapedRecipe.getIngredientMap().values().stream().filter(Objects::nonNull).anyMatch(i -> i.getType().equals(mat)))
						{
							return true;
						}
						if(recipe instanceof ShapelessRecipe shapelessRecipe && shapelessRecipe.getIngredientList().stream().filter(Objects::nonNull).anyMatch(i -> i.getType().equals(mat)))
						{
							return true;
						}
					}
					return false;
				}).collect(ImmutableList.toImmutableList());
		
		noWoodGiveableItems = giveableItems.stream().filter(material -> !material.name().matches("(\\w+_PLANKS|\\w+_WOOD|\\w+_LOG)")).collect(ImmutableList.toImmutableList());
	}
	
	public static List<Material> getGiveableItems() {
		return giveableItems;
	}
	
	public static List<Material> getNoWoodGiveableItems() {
		return noWoodGiveableItems;
	}
	
	public static Material getRandomGiveableItem(Random random) {
		return giveableItems.get(random.nextInt(giveableItems.size()));
	}
	public static Material getRandomNoWoodGiveableItem(Random random) {
		return noWoodGiveableItems.get(random.nextInt(noWoodGiveableItems.size()));
	}
	
	public static final ItemStack DEFAULT_PICKAXE = new ItemBuilder(Material.IRON_PICKAXE).setUnbreakable(true).buildImmutable();
}
