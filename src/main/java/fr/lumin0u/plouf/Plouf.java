package fr.lumin0u.plouf;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import fr.lumin0u.plouf.events.CosmoxListener;
import fr.lumin0u.plouf.util.Achievements;
import fr.lumin0u.plouf.util.I18n;
import fr.lumin0u.plouf.util.ItemBuilder;
import fr.lumin0u.plouf.util.Items;
import fr.worsewarn.cosmox.API;
import fr.worsewarn.cosmox.api.players.WrappedPlayer;
import fr.worsewarn.cosmox.api.statistics.Statistic;
import fr.worsewarn.cosmox.game.Game;
import fr.worsewarn.cosmox.game.GameVariables;
import fr.worsewarn.cosmox.game.configuration.Parameter;
import fr.worsewarn.cosmox.tools.items.DefaultItemSlot;
import fr.worsewarn.cosmox.tools.map.MapLocation;
import fr.worsewarn.cosmox.tools.map.MapLocationType;
import fr.worsewarn.cosmox.tools.map.MapTemplate;
import fr.worsewarn.cosmox.tools.map.MapType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public final class Plouf extends JavaPlugin
{
	public static final String PLOUF_ITEM_DELAY = "plouf_item_delay";
	public static final String PLOUF_WOOD_DEACTIVATED = "plouf_wood_deactivated";
	public static final String PLOUF_AUTO_REMOVE_NONINGREDIENTS = "plouf_remove_noningredients";
	
	public static final String PLOUF_ITEMS_CRAFTED = "plouf_items_crafted";
	public static final String PLOUF_UNIQUE_ITEMS_CRAFTED = "plouf_unique_items_crafted";
	
	public static final DefaultItemSlot PICKAXE_SLOT = new DefaultItemSlot("plouf_pickaxe", Items.DEFAULT_PICKAXE);
	
	public static final String GAME_IDENTIFIER = "ploufcraft";
	
	private GameManager gameManager;
	private API api;
	private Game game;
	private static Plouf instance;
	private ProtocolManager protocolManager;
	
	private static long currentTick;
	
	@Override
	public void onEnable() {
		instance = this;
		
		getCommand("ploufstop").setExecutor(this);
		
		Bukkit.getScheduler().runTaskTimer(API.instance(), () -> currentTick++, 1, 1);
		
		Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
			getLogger().info("Création de la liste des items... (peut prendre du temps, tache asynchrone)");
			long startTime = System.currentTimeMillis();
			Items.buildGiveableItems();
			long endTime = System.currentTimeMillis();
			getLogger().info(String.format("Liste des items créée en %.3f secondes", (double) (endTime - startTime) / 1000));
		});
		
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
		
		game = new Game(GAME_IDENTIFIER, "PloufCraft", ChatColor.of("#FF5050"), Material.CRAFTING_TABLE, null, 2, false, true,
				List.of(
						new Statistic(I18n.interpretable("main", "statistics_time_played"), GameVariables.TIME_PLAYED, true),
						new Statistic(I18n.interpretable("main", "statistics_games_played"), GameVariables.GAMES_PLAYED),
						new Statistic(I18n.interpretable("main", "statistics_win"), GameVariables.WIN),
						new Statistic(I18n.interpretable("statistics_crafts"), PLOUF_ITEMS_CRAFTED, true, true),
						new Statistic(I18n.interpretable("statistics_unique_crafts"), PLOUF_UNIQUE_ITEMS_CRAFTED, true, true)
				),
				List.of(Achievements.PLOUFCRAFT,
						Achievements.CRAFT_FURNACE,
						Achievements.CRAFT_WORKBENCH,
						Achievements.PLAYER_INVENTORY_CRAFTING,
						Achievements.WIN_NO_UNIQUE,
						Achievements.TOOL_CRAFTING,
						Achievements.WIN_NO_WOOD,
						Achievements.WIN_REMONTADA),
				List.of("", I18n.interpretable("game_description")),
				List.of(new MapTemplate(MapType.NONE, List.of(
						new MapLocation("name", MapLocationType.STRING),
						new MapLocation("authors", MapLocationType.STRING),
						new MapLocation("spawnpoint", MapLocationType.LOCATION),
						new MapLocation("spawnoffset", MapLocationType.STRING)
				)))
		);
		
		game.addDefaultItem(PICKAXE_SLOT, 0);
		game.setGameAuthor("lumin0u");
		game.setPreparationTime(5);
		game.addParameter(new Parameter(PLOUF_ITEM_DELAY, "", 5, 0.25f, 20,
				new ItemBuilder(Material.CLOCK)
						.setDisplayName(I18n.interpretable("host_parameter_item_delay"))
						.addLore(List.of(
								" ",
								"§7" + I18n.interpretable("host_parameter_item_delay_description"),
								" ",
								I18n.interpretable("host_parameter_item_delay_value")))
						.build(),
				List.of(1f, 0.5f, 0.25f), false, false));
		
		game.addParameter(new Parameter(PLOUF_WOOD_DEACTIVATED, "", List.of("Désactivé", "Activé"),
						new ItemBuilder(Material.OAK_LOG)
								.setDisplayName(I18n.interpretable("host_parameter_no_wood"))
								.addLore(List.of(
										" ",
										"§7" + I18n.interpretable("host_parameter_no_wood_description"),
										" ",
										I18n.interpretable("host_parameter_no_wood_value")))
								.build(),
						false, false));
		
		game.addParameter(new Parameter(PLOUF_AUTO_REMOVE_NONINGREDIENTS, "", List.of("Désactivé", "Activé"),
				new ItemBuilder(Material.STRUCTURE_VOID)
						.setDisplayName(I18n.interpretable("host_parameter_remove_non_ingredients"))
						.addLore(List.of(
								" ",
								"§7" + I18n.interpretable("host_parameter_remove_non_ingredients_description"),
								" ",
								I18n.interpretable("host_parameter_remove_non_ingredients_value")))
						.build(),
				false, false));
		Parameter maps = new Parameter("Maps", "", 0, 0, 0, new ItemBuilder(Material.PAPER).setDisplayName("§6Cartes").setLore(List.of(" ", "§7Enlève ici les cartes que tu n'aimes pas", "§7pour pouvoir exploser les joueurs et gagner")).build(), List.of(0F), false, true);
		game.addParameter(maps);
		
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
	
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if(command.getName().equalsIgnoreCase("ploufstop") && sender.isOp()) {
			getGameManager().stop();
		}
		return super.onCommand(sender, command, label, args);
	}
}
