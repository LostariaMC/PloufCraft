package fr.lumin0u.plouf.events;

import fr.lumin0u.plouf.GameManager;
import fr.lumin0u.plouf.Plouf;
import fr.lumin0u.plouf.PloufPlayer;
import fr.lumin0u.plouf.util.ItemBuilder;
import fr.lumin0u.plouf.util.Items;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class UniqueCraftMenuListener implements Listener
{
	private Inventory menu;
	
	private void createMenu()
	{
		menu = Bukkit.createInventory(null, 6*9, "Crafts uniques des joueurs");
		
		new BukkitRunnable() {
			int t = 0;
			
			@Override
			public void run() {
				updateMenu(t++);
			}
		}.runTaskTimer(Plouf.getInstance(), 0, 10);
	}
	
	private void updateMenu(int t)
	{
		GameManager gm = Plouf.getInstance().getGameManager();
		
		int i = 0;
		for(PloufPlayer player : gm.getNonSpecPlayers().stream().sorted((p1, p2) -> Integer.compare(p2.getUniqueCrafts().size(), p1.getUniqueCrafts().size())).toList())
		{
			if(i == 9)
				break;
			
			menu.setItem(i, new ItemBuilder(Material.PLAYER_HEAD)
					.setHead(player.getName())
					.setDisplayName(player.getName()).setLore("§7Tous les items listés ci-dessous", "§7n'ont été craftés que par " + player.getName())
					.build());
			
			for(int j = 0; j < Math.min(5, player.getUniqueCrafts().size()); j++)
			{
				Material craft = player.getUniqueCrafts().get((j + t) % player.getUniqueCrafts().size());
				
				menu.setItem(i + 9*(j + 1), new ItemStack(craft));
			}
			
			i++;
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event)
	{
		if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
		{
			if(Items.UNIQUE_CRAFTS_HEAD.isSimilar(event.getItem()))
			{
				if(menu == null)
					createMenu();
				
				PloufPlayer player = PloufPlayer.of(event.getPlayer());
				player.toBukkit().openInventory(menu);
			}
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event)
	{
		if(event.getInventory().equals(menu))
			event.setCancelled(true);
	}
}
