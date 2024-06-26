package fr.lumin0u.plouf.events;

import fr.lumin0u.plouf.GameManager;
import fr.lumin0u.plouf.Plouf;
import fr.lumin0u.plouf.PloufPlayer;
import fr.lumin0u.plouf.util.Achievements;
import fr.lumin0u.plouf.util.Items;
import fr.worsewarn.cosmox.tools.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.player.PlayerRecipeDiscoverEvent;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class CraftListener implements Listener
{
	private static final List<Material> WOODEN_TOOLS = List.of(Material.WOODEN_AXE, Material.WOODEN_HOE, Material.WOODEN_PICKAXE, Material.WOODEN_SHOVEL);
	
	private boolean isDropAction(InventoryAction action) {
		return switch(action) {
			case DROP_ONE_SLOT, DROP_ALL_SLOT ->
					true;
			default ->
					false;
		};
	}
	
	private static final List<Color> fireworkColors = List.of(Color.BLUE, Color.LIME, Color.OLIVE, Color.ORANGE, Color.PURPLE, Color.WHITE, Color.AQUA, Color.FUCHSIA, Color.AQUA, Color.GREEN, Color.NAVY, Color.RED, Color.RED, Color.YELLOW, Color.TEAL);
	
	@EventHandler
	public void itemCraftEvent(CraftItemEvent event) {
		GameManager gm = Plouf.getInstance().getGameManager();
		
		if((isDropAction(event.getAction()) && event.getWhoClicked().getItemOnCursor().getType() != Material.AIR) || event.getAction() == InventoryAction.NOTHING) {
			event.setCancelled(true);
			return;
		}
		
		if(gm.isStarted() && (event.getClick() != ClickType.NUMBER_KEY || event.getWhoClicked().getInventory().getItem(event.getHotbarButton()) == null)) {
			PloufPlayer player = gm.getPlayer(event.getWhoClicked().getUniqueId());
			
			Material crafted = event.getInventory().getResult().getType();
			
			if(!player.getCraftedItems().contains(crafted)) {
				player.addCraftedItem(crafted,
						Arrays.stream(event.getInventory().getContents())
						.map(item -> item == null ? null : item.clone())
						.toArray(ItemStack[]::new));
				
				if(crafted == Material.FURNACE) {
					player.toCosmox().grantAdvancement(Achievements.CRAFT_FURNACE.getId());
				}
				if(crafted == Material.CRAFTING_TABLE) {
					player.toCosmox().grantAdvancement(Achievements.CRAFT_WORKBENCH.getId());
				}
				if(player.getCraftedItems().containsAll(WOODEN_TOOLS)) {
					player.toCosmox().grantAdvancement(Achievements.TOOL_CRAFTING.getId());
				}
				if(event.getInventory().getMatrix().length == 4) {
					player.toCosmox().grantAdvancement(Achievements.PLAYER_INVENTORY_CRAFTING.getId());
				}
				
				if(!player.didUseWood() && Arrays.stream(event.getInventory().getMatrix()).filter(Objects::nonNull).map(ItemStack::getType).anyMatch(Items::isWood)) {
					player.setUsedWood(true);
				}
				
				Firework fw = (Firework) player.toBukkit().getWorld().spawnEntity(player.toBukkit().getEyeLocation(), EntityType.FIREWORK);
				Bukkit.getScheduler().runTaskLater(Plouf.getInstance(), fw::detonate, 1);
				
				FireworkMeta meta = fw.getFireworkMeta();
				meta.addEffect(FireworkEffect.builder().withColor(fireworkColors.get(new Random().nextInt(fireworkColors.size()))).build());
				fw.setFireworkMeta(meta);
				
				gm.updateScoreboardScores();
			}
		}
	}
	
	@EventHandler
	public void onRecipeDiscovery(PlayerRecipeDiscoverEvent event) {
		event.setCancelled(true);
	}
}
