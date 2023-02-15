package fr.lumin0u.plouf;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import fr.lumin0u.plouf.events.CosmoxListener;
import fr.lumin0u.plouf.events.CraftListener;
import fr.lumin0u.plouf.util.ItemBuilder;
import fr.lumin0u.plouf.util.Items;
import fr.worsewarn.cosmox.API;
import fr.worsewarn.cosmox.api.players.WrappedPlayer;
import fr.worsewarn.cosmox.api.statistics.Statistic;
import fr.worsewarn.cosmox.game.*;
import fr.worsewarn.cosmox.game.configuration.Parameter;
import fr.worsewarn.cosmox.tools.items.DefaultItemSlot;
import fr.worsewarn.cosmox.tools.map.MapLocation;
import fr.worsewarn.cosmox.tools.map.MapLocationType;
import fr.worsewarn.cosmox.tools.map.MapTemplate;
import fr.worsewarn.cosmox.tools.map.MapType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.UUID;

public final class Plouf extends JavaPlugin
{
	public static final String PLOUF_ITEM_DELAY = "plouf_item_delay";
	public static final String PLOUF_WOOD_DEACTIVATED = "plouf_wood_deactivated";
	
	public static final String PLOUF_ITEMS_CRAFTED = "plouf_items_crafted";
	public static final String PLOUF_UNIQUE_ITEMS_CRAFTED = "plouf_unique_items_crafted";
	
	public static final DefaultItemSlot PICKAXE_SLOT = new DefaultItemSlot("plouf_pickaxe", Items.DEFAULT_PICKAXE);
	
	private GameManager gameManager;
	private API api;
	private Game game;
	private static Plouf instance;
	private ProtocolManager protocolManager;
	
	private static long currentTick;
	
	@Override
	public void onEnable() {
		instance = this;
		
		Bukkit.getScheduler().runTaskTimer(API.instance(), () -> currentTick++, 1, 1);
		
		getLogger().info("Création de la liste des items... (peut prendre du temps)");
		Items.buildGiveableItems();
		getLogger().info("Liste des items crée");
		
		protocolManager = ProtocolLibrary.getProtocolManager();
		
		getServer().getPluginManager().registerEvents(new CosmoxListener(), this);
		
		api = API.instance();
		
		WrappedPlayer.registerType(new WrappedPlayer.PlayerWrapper<PloufPlayer>(PloufPlayer.class)
		{
			@Override
			public PloufPlayer unWrap(java.util.UUID uuid) {
				return gameManager.getPlayer(uuid);
			}
			
			@Override
			public UUID wrap(PloufPlayer player) {
				return player.getUniqueId();
			}
		});
		
		game = new Game("ploufcraft", "PloufCraft", "§x§F§F§5§0§5§0PloufCraft", Material.CRAFTING_TABLE, null, 2, false, true,
				List.of(
						new Statistic("Temps de jeu", GameVariables.TIME_PLAYED, true),
						new Statistic("Parties jouées", GameVariables.GAMES_PLAYED),
						new Statistic("Victoires", GameVariables.WIN),
						new Statistic("Items craftés", PLOUF_ITEMS_CRAFTED, true, true),
						new Statistic("Items uniques craftés", PLOUF_UNIQUE_ITEMS_CRAFTED, true, true)
				),
				List.of(),
				List.of("", "§7Craftez des trucs ! Vite !"),
				List.of(new MapTemplate(MapType.NONE, List.of(
						new MapLocation("name", MapLocationType.STRING),
						new MapLocation("authors", MapLocationType.STRING),
						new MapLocation("spawnpoint", MapLocationType.LOCATION)
				)))
		);
		
		game.addDefaultItem(PICKAXE_SLOT, 0);
		game.setGameAuthor("lumin0u");
		game.setPreparationTime(5);
		game.addParameter(new Parameter(PLOUF_ITEM_DELAY, "", 5, 0.25f, 20,
				new ItemBuilder(Material.CLOCK).setDisplayName("§bDélai entre 2 items").addLore(List.of(" ", "§7Définir le délai entre", " ", "§e Valeur actuelle : §6%f §esecondes")).build(),
				List.of(1f, 0.5f, 0.25f), false, false));
		
		game.addParameter(new Parameter(PLOUF_WOOD_DEACTIVATED, "", List.of("Désactivé", "Activé"),
						new ItemBuilder(Material.OAK_LOG).setDisplayName("§bDésactivation du bois").addLore(List.of(" ", "§7Permet de désactiver le", "§7drop de bois (n'exclut que", "§7les planches et les buches, les", "§7crafts utilisant du bois sont", "§7toujours possible)", " ", "§e Actuellement : le bois n'est pas §6%b")).build(),
						false, false));
		
		API.instance().registerNewGame(game);
		
		reset();
	}
	
	public void reset()
	{
		gameManager = new GameManager(this);
	}
	
	public API getAPI() {
		return api;
	}
	
	public static Game getGame() {
		return instance.game;
	}
	
	public static Plouf getInstance() {
		return instance;
	}
	
	public GameManager getGameManager() {
		return gameManager;
	}
	
	public static ProtocolManager getProtocolManager() {
		return instance.protocolManager;
	}
}
