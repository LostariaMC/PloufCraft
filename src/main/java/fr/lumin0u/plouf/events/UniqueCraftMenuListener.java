package fr.lumin0u.plouf.events;

import fr.lumin0u.plouf.GameManager;
import fr.lumin0u.plouf.Plouf;
import fr.lumin0u.plouf.PloufPlayer;
import fr.lumin0u.plouf.util.I18n;
import fr.lumin0u.plouf.util.ItemBuilder;
import fr.lumin0u.plouf.util.Items;
import fr.worsewarn.cosmox.api.languages.Language;
import fr.worsewarn.cosmox.tools.Utils;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UniqueCraftMenuListener implements Listener
{
	private Map<Language, Inventory> menus;
	private List<PloufPlayer> sortedPlayers;
	
	private void createMenus()
	{
		GameManager gm = Plouf.getInstance().getGameManager();
		
		sortedPlayers = gm.getNonSpecPlayers().stream().sorted((p1, p2) -> Integer.compare(p2.getUniqueCrafts().size(), p1.getUniqueCrafts().size())).toList();
		
		menus = new HashMap<>(Language.values().length);
		
		for(Language language : Language.values()) {
			Inventory menu = Bukkit.createInventory(null, 6*9, Component.text(ChatColor.stripColor(I18n.translate(language, "menu_unique_crafts_title"))));
			
			int i = 0;
			for(PloufPlayer player : sortedPlayers)
			{
				if(i == 9)
					break;
				
				int count = player.getUniqueCrafts().size();
				menu.setItem(i, new ItemBuilder(Material.PLAYER_HEAD)
						.setHead(player.getName())
						.setDisplayName(player.getName())
						.addLore(I18n.translate(language, "item_unique_crafts_description1", count))
						.addLore(Utils.cutList(I18n.translate(language, "item_unique_crafts_description2", player.getName()), 30, ChatColor.GRAY))
						.build());
				
				i++;
			}
			
			new BukkitRunnable() {
				int t = 0;
				
				@Override
				public void run() {
					updateMenu(t++);
				}
			}.runTaskTimer(Plouf.getInstance(), 0, 20);
			
			menus.put(language, menu);
		}
	}
	
	private void updateMenu(int t)
	{
		GameManager gm = Plouf.getInstance().getGameManager();
		
		int i = 0;
		for(PloufPlayer player : sortedPlayers)
		{
			if(i == 9)
				break;
			
			int count = player.getUniqueCrafts().size();
			
			for(int j = 0; j < Math.min(5, count); j++)
			{
				Material craft = player.getUniqueCrafts().get((count > 5 ? j + t : j) % count);
				ItemStack item = new ItemBuilder(craft).addLore("§7§l> §7Voir le craft").build();
				
				for(Inventory inv : menus.values()) {
					inv.setItem(i + 9 * (j + 1), item);
				}
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
				if(menus == null)
					createMenus();
				
				PloufPlayer player = PloufPlayer.of(event.getPlayer());
				player.toBukkit().openInventory(menus.get(player.toCosmox().getRedisPlayer().getLanguage()));
			}
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event)
	{
		if(menus != null && menus.containsValue(event.getInventory())) {
			event.setCancelled(true);
			
			if(event.getCurrentItem() != null && event.getSlot() >= 9) {
				Inventory inv = Bukkit.createInventory(null, InventoryType.WORKBENCH);
				
				inv.setContents(sortedPlayers.get(event.getSlot() % 9).getRecipe(event.getCurrentItem().getType()));
				
				event.getWhoClicked().closeInventory();
				event.getWhoClicked().openInventory(inv);
			}
		}
	}
}
