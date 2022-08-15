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
	}
	
	public static List<Material> getGiveableItems() {
		return giveableItems;
	}
	
	public static Material getRandomGiveableItem(Random random) {
		return giveableItems.get(random.nextInt(giveableItems.size()));
	}
	
	public static final ItemStack DEFAULT_PICKAXE = new ItemBuilder(Material.IRON_PICKAXE).setUnbreakable(true).buildImmutable();
	
	/* = ImmutableList.of(
			Material.ANDESITE,
			Material.APPLE,
			Material.ACACIA_PLANKS,
			Material.ACACIA_LOG,
			Material.ALLIUM,
			Material.AMETHYST_SHARD,
			Material.ARROW,
			Material.AZURE_BLUET,
			Material.BLACKSTONE,
			Material.BONE,
			Material.BLACK_DYE,
			Material.BLUE_DYE,
			Material.BAMBOO,
			Material.BEETROOT,
			Material.BIRCH_LOG,
			Material.BIRCH_PLANKS,
			Material.BLACK_WOOL,
			Material.BLAZE_POWDER,
			Material.BLAZE_ROD,
			Material.BLUE_ORCHID,
			Material.BLUE_WOOL,
			Material.BONE_BLOCK,
			Material.BONE_MEAL,
			Material.BOOK,
			Material.BOOKSHELF,
			Material.BOW,
			Material.BOWL,
			Material.BREWING_STAND,
			Material.BRICK,
			Material.BRICKS,
			Material.BROWN_DYE,
			Material.BROWN_MUSHROOM,
			Material.BROWN_WOOL,
			Material.BUCKET,
			Material.CACTUS,
			Material.CALCITE,
			Material.CARROT,
			Material.CARVED_PUMPKIN,
			Material.CHAIN,
			Material.CHEST,
			Material.CHARCOAL,
			Material.CHISELED_DEEPSLATE,
			Material.CHISELED_NETHER_BRICKS,
			Material.CHISELED_SANDSTONE,
			Material.CHISELED_POLISHED_BLACKSTONE,
			Material.CHISELED_QUARTZ_BLOCK,
			Material.CHISELED_RED_SANDSTONE,
			Material.CHISELED_STONE_BRICKS,
			Material.CLAY,
			Material.CLAY_BALL,
			Material.COAL,
			Material.COAL_BLOCK,
			Material.COBBLED_DEEPSLATE,
			Material.COBBLESTONE,
			Material.COBBLESTONE,
			Material.COCOA_BEANS,
			Material.COMPASS,
			Material.COPPER_BLOCK,
			Material.COPPER_INGOT,
			Material.CORNFLOWER,
			Material.CRIMSON_PLANKS,
			Material.CRIMSON_HYPHAE,
			Material.CRIMSON_FUNGUS,
			Material.CUT_SANDSTONE,
			Material.CYAN_DYE,
			Material.CYAN_WOOL,
			Material.YELLOW_WOOL
	);*/
	
}
