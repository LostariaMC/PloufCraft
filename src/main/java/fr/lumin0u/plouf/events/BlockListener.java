package fr.lumin0u.plouf.events;

import fr.lumin0u.plouf.GameManager;
import fr.lumin0u.plouf.Plouf;
import fr.lumin0u.plouf.PloufPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class BlockListener implements Listener
{
	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		GameManager gm = Plouf.getInstance().getGameManager();
		
		if(gm.isStarted())
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if(!PloufPlayer.of(event.getPlayer()).getPlacedBlocks().contains(event.getBlock())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		PloufPlayer.of(event.getPlayer()).getPlacedBlocks().add(event.getBlock());
	}
	
	@EventHandler
	public void onPlayerEditBlock(PlayerInteractEvent event) {
		if(event.hasBlock()) {
			event.setCancelled(true);
		}
	}
}
