package fr.lumin0u.plouf;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import fr.lumin0u.plouf.events.CosmoxListener;
import fr.lumin0u.plouf.util.*;
import fr.worsewarn.cosmox.API;
import fr.worsewarn.cosmox.api.players.WrappedPlayer;
import fr.worsewarn.cosmox.api.statistics.StatisticDTO;
import fr.worsewarn.cosmox.api.statistics.UploadRule;
import fr.worsewarn.cosmox.game.Game;
import fr.worsewarn.cosmox.game.configuration.parameters.Parameter;
import fr.worsewarn.cosmox.game.configuration.teams.TeamConfiguration;
import fr.worsewarn.cosmox.tools.map.game.GameMap;
import fr.worsewarn.cosmox.tools.map.template.MapTemplate;
import fr.worsewarn.cosmox.tools.map.template.MapTemplateElement;
import fr.worsewarn.cosmox.tools.map.template.MapTemplateElementType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
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
	public static final String PLOUF_POINTS = "plouf_points";

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
		//salam aleykoum les petits patissiers
		game = new Game("PloufCraft", "#ff5050", new fr.worsewarn.cosmox.tools.items.ItemBuilder(Material.CRAFTING_TABLE), List.of(new TeamConfiguration(GameMap.Type.A, List.of())), 2,
                PloufStatistic.class,
				PloufAchievement.class,
				I18n.interpretable("game_description"),

				List.of(new MapTemplate(GameMap.Type.A, List.of(
						new MapTemplateElement("name", MapTemplateElementType.STRING),
						new MapTemplateElement("authors", MapTemplateElementType.STRING),
						new MapTemplateElement("map", MapTemplateElementType.CUBOID),
						new MapTemplateElement("spawnpoint", MapTemplateElementType.LOCATION),
						new MapTemplateElement("spawnoffset", MapTemplateElementType.STRING)
				)))
		);

		game.setGameAuthor("lumin0u");
		game.setPreparationTime(5);
		game.addParameter(new Parameter(PLOUF_ITEM_DELAY, "", 5, 0.25f, 20,
				new ItemBuilder(Material.CLOCK)
						.setDisplayName(I18n.interpretable("host_parameter_item_delay"))
						.addLore(List.of(
								" ",
								"<gray>" + I18n.interpretable("host_parameter_item_delay_description"),
								" ",
								I18n.interpretable("host_parameter_item_delay_value")))
						.build(),
				List.of(1f, 0.5f, 0.25f), false, false));

		game.addParameter(new Parameter(PLOUF_WOOD_DEACTIVATED, "", List.of("Désactivé", "Activé"),
						new ItemBuilder(Material.OAK_LOG)
								.setDisplayName(I18n.interpretable("host_parameter_no_wood"))
								.addLore(List.of(
										" ",
										"<gray>" + I18n.interpretable("host_parameter_no_wood_description"),
										" ",
										I18n.interpretable("host_parameter_no_wood_value")))
								.build(),
						false, false));

		game.addParameter(new Parameter(PLOUF_AUTO_REMOVE_NONINGREDIENTS, "", List.of("Désactivé", "Activé"),
				new ItemBuilder(Material.STRUCTURE_VOID)
						.setDisplayName(I18n.interpretable("host_parameter_remove_non_ingredients"))
						.addLore(List.of(
								" ",
								"<gray>" + I18n.interpretable("host_parameter_remove_non_ingredients_description"),
								" ",
								I18n.interpretable("host_parameter_remove_non_ingredients_value")))
						.build(),
				false, false));
		Parameter maps = new Parameter("Maps", "", 0, 0, 0, new ItemBuilder(Material.PAPER).setDisplayName("<gold>Cartes").setLore(List.of(" ", "<gray>Enlève ici les cartes que tu n'aimes pas", "<gray>pour pouvoir exploser les joueurs et gagner")).build(), List.of(0F), false, true);
		game.addParameter(maps);

		API.instance().registerGame(game);

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

	public static World getWorld() {
		return Bukkit.getWorld("world");
	}
}
