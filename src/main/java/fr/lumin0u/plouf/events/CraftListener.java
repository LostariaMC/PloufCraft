package fr.lumin0u.plouf.events;

import fr.lumin0u.plouf.GameManager;
import fr.lumin0u.plouf.Plouf;
import fr.lumin0u.plouf.PloufPlayer;
import fr.lumin0u.plouf.util.Items;
import fr.lumin0u.plouf.util.Utils;
import fr.worsewarn.cosmox.API;
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
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerRecipeDiscoverEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.List;

import static fr.lumin0u.plouf.Plouf.PLOUF_AUTO_REMOVE_NONINGREDIENTS;

public class CraftListener implements Listener
{
	@EventHandler
	public void itemCraftEvent(CraftItemEvent event) {
		GameManager gm = Plouf.getInstance().getGameManager();
		
		if(gm.isStarted())
		{
			PloufPlayer player = gm.getPlayer(event.getWhoClicked().getUniqueId());
			
			Material crafted = event.getRecipe().getResult().getType();
			
			if(!player.getCraftedItems().contains(crafted))
			{
				player.addCraftedItem(crafted);
				
				Firework fw = (Firework) player.toBukkit().getWorld().spawnEntity(player.toBukkit().getEyeLocation(), EntityType.FIREWORK);
				Bukkit.getScheduler().runTaskLater(Plouf.getInstance(), fw::detonate, 10);
				
				FireworkMeta meta = fw.getFireworkMeta();
				meta.addEffect(FireworkEffect.builder().withColor(Utils.choice(List.of(Color.BLUE, Color.LIME, Color.OLIVE, Color.ORANGE, Color.PURPLE, Color.WHITE, Color.AQUA, Color.FUCHSIA, Color.AQUA, Color.GREEN, Color.NAVY, Color.RED, Color.RED, Color.YELLOW, Color.TEAL))).build());
				fw.setFireworkMeta(meta);
				
				gm.updateScoreboardScores();
			}
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		GameManager gm = Plouf.getInstance().getGameManager();
		
		if(gm.isStarted())
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event)
	{
		if(!PloufPlayer.of(event.getPlayer()).getPlacedBlocks().contains(event.getBlock()))
		{
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event)
	{
		PloufPlayer.of(event.getPlayer()).getPlacedBlocks().add(event.getBlock());
	}
	
	@EventHandler
	public void onRecipeDiscovery(PlayerRecipeDiscoverEvent event) {
		event.setCancelled(true);
	}
}
