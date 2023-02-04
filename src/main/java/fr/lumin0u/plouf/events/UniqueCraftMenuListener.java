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

public class UniqueCraftMenuListener implements Listener
{
	private Inventory menu;
	
	private void createMenu()
	{
		menu = Bukkit.createInventory(null, 6*9, "Crafts uniques des autres joueurs");
		GameManager gm = Plouf.getInstance().getGameManager();
		
		int i = 0;
		for(PloufPlayer player : gm.getNonSpecPlayers().stream().sorted((p1, p2) -> Integer.compare(p1.getUniqueCrafts().size(), p2.getUniqueCrafts().size())).toList())
		{
			if(i == 9)
				break;
			
			menu.setItem(i, new ItemBuilder(Material.PLAYER_HEAD)
					.setHead(player.getName())
					.setDisplayName(player.getName()).setLore("§7Tout les items listés ci-dessous", "§7n'ont été craftés que par " + player.getName())
					.build());
			
			int j = 1;
			
			for(Material craft : player.getUniqueCrafts())
			{
				if(j == 6)
					break;
				
				menu.setItem(i + 9*j, new ItemStack(craft));
				j++;
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
