package fr.lumin0u.plouf.events;

import com.destroystokyo.paper.event.player.PlayerStartSpectatingEntityEvent;
import fr.lumin0u.plouf.PloufPlayer;
import fr.worsewarn.cosmox.api.players.WrappedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class SpectatorListener implements Listener
{
	/*
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Bukkit.getOnlinePlayers().stream()
				.map(PloufPlayer::of)
				.filter(WrappedPlayer::isOnline)
				.filter(PloufPlayer::isSpectator)
				.filter(watcher -> watcher.getSpectatorTarget().is(event.getWhoClicked()))
				.forEach(watcher ->
				{
					Bukkit.getScheduler().runTaskLater(Plouf.getInstance(), () -> {
								watcher.toBukkit().getOpenInventory().getTopInventory().setContents(event.getWhoClicked().getOpenInventory().getTopInventory().getContents());
								watcher.toBukkit().getOpenInventory().getBottomInventory().setContents(event.getWhoClicked().getInventory().getContents());
							}, 1);
				});
	}
	
	@EventHandler
	public void onOpenInventory(InventoryOpenEvent event) {
		Bukkit.getOnlinePlayers().stream()
				.map(PloufPlayer::of)
				.filter(WrappedPlayer::isOnline)
				.filter(PloufPlayer::isSpectator)
				.filter(watcher -> watcher.getSpectatorTarget().is(event.getPlayer()))
				.forEach(watcher ->
				{
					watcher.toBukkit().closeInventory();
					watcher.toBukkit().openInventory(Bukkit.createInventory(null, InventoryType.WORKBENCH));
				});
	}
	*/
	@EventHandler
	public void onCloseInventory(InventoryCloseEvent event) {
		PloufPlayer.of(event.getPlayer()).setSpectatorTarget(WrappedPlayer.NULL);
	}
	
	@EventHandler
	public void onStartSpectating(PlayerStartSpectatingEntityEvent event) {
		if(event.getNewSpectatorTarget() instanceof Player target) {
			event.setCancelled(true);
			
			PloufPlayer watcher = PloufPlayer.of(event.getPlayer());
			watcher.setSpectatorTarget(WrappedPlayer.of(target.getPlayer()));
			
			Inventory inv = Bukkit.createInventory(null, InventoryType.WORKBENCH);
			inv.setContents(event.getPlayer().getOpenInventory().getTopInventory().getContents());
			watcher.toBukkit().openInventory(inv);
		}
	}
}
