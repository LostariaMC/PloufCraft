package fr.lumin0u.plouf;

import fr.lumin0u.plouf.util.Achievements;
import fr.lumin0u.plouf.util.I18n;
import fr.lumin0u.plouf.util.Items;
import fr.worsewarn.cosmox.API;
import fr.worsewarn.cosmox.api.players.WrappedPlayer;
import fr.worsewarn.cosmox.api.scoreboard.CosmoxScoreboard;
import fr.worsewarn.cosmox.game.GameVariables;
import fr.worsewarn.cosmox.game.Phase;
import fr.worsewarn.cosmox.tools.chat.MessageBuilder;
import fr.worsewarn.cosmox.tools.chat.Messages;
import fr.worsewarn.cosmox.tools.map.GameMap;
import fr.worsewarn.cosmox.tools.utils.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import net.kyori.adventure.util.Ticks;
import org.bukkit.*;
import org.bukkit.Note.Tone;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.IntStream;

import static fr.lumin0u.plouf.Plouf.PLOUF_WOOD_DEACTIVATED;
import static java.util.function.Predicate.not;

public class GameManager
{
	private final Plouf plouf;
	private Map<UUID, PloufPlayer> players = new HashMap<>();
	private boolean started;
	private int time;
	private final Random itemRandom;
	private GameMap map;
	private boolean gaveWood;
	
	final int gameDuration = 2 * 60 * 20;
	
	public GameManager(Plouf main) {
		this.plouf = main;
		itemRandom = new Random();
	}
	
	public PloufPlayer getPlayer(UUID uid) {
		players.putIfAbsent(uid, new PloufPlayer(uid));
		return players.get(uid);
	}
	
	/*public static void copyChunk(Chunk chunk1, Chunk chunk2) {
		net.minecraft.world.level.chunk.Chunk nmsChunk1 = NMSUtils.getHandle(chunk1);
		net.minecraft.world.level.chunk.Chunk nmsChunk2 = NMSUtils.getHandle(chunk2);
		
		ChunkSection[] sections1 = NMSUtils.Chunk_getChunkSections(nmsChunk1);
		ChunkSection[] sections2 = NMSUtils.Chunk_getChunkSections(nmsChunk2);
		
		System.arraycopy(Arrays.stream(sections1).map(cs -> cs).toArray(ChunkSection[]::new), 0, sections2, 0, sections1.length);
	}*/
	public List<PloufPlayer> getNonSpecPlayers() {
		return players.values().stream().filter(not(PloufPlayer::isSpectator)).toList();
	}

	public List<PloufPlayer> getOnlinePlayers() {
		return players.values().stream().filter(WrappedPlayer::isOnline).toList();
	}
	
	public Location getSpawnpoint(int i) {
		return map.getLocation("spawnpoint").clone().add(Integer.parseInt(map.getStr("spawnoffset")) * i, 0, 0);
	}
	
	public void onCosmoxStart(GameMap map) {
		this.map = map;
		
		for(Player player : Bukkit.getOnlinePlayers())
			getPlayer(player.getUniqueId());
		
		int playerCount = (int) players.values().stream().filter(not(PloufPlayer::isSpectator)).filter(WrappedPlayer::isOnline).count();
		
		Chunk spawnChunk = map.getLocation("spawnpoint").getChunk();
		
		int i = 1;
		for(PloufPlayer ploufPlayer : getOnlinePlayers())
		{
			if(!ploufPlayer.isSpectator())
			{
				//copyChunk(spawnChunk, spawnChunk.getWorld().getChunkAt(spawnChunk.getX() + i, spawnChunk.getZ()));
				ploufPlayer.toBukkit().teleport(getSpawnpoint(i));
				i++;

				ploufPlayer.toBukkit().getInventory().clear();
				ploufPlayer.toBukkit().addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, PotionEffect.INFINITE_DURATION, 50, false, false));
				ploufPlayer.toBukkit().setGameMode(GameMode.SURVIVAL);

			}
			else {
				ploufPlayer.toBukkit().getInventory().clear();
				ploufPlayer.toBukkit().teleport(getSpawnpoint(i));
				ploufPlayer.toBukkit().setGameMode(GameMode.SPECTATOR);
			}
		}
		
		new BukkitRunnable()
		{
			@Override
			public void run() {
				start();
			}
		}.runTaskLater(plouf, 120);
	}
	
	public void start() {
		resetScoreboard();
		
		API.instance().getManager().setPhase(Phase.GAME);
		
		started = true;
		
		int itemDelay = getItemDelay();
		
		Bukkit.getOnlinePlayers().forEach(p -> p.undiscoverRecipes(p.getDiscoveredRecipes()));
		
		new BukkitRunnable()
		{
			int i = 0;
			boolean waitingForItems = false;
			
			@Override
			public void run() {
				
				if(!Items.isFinishedLoading()) {
					if(!waitingForItems) {
						Bukkit.broadcastMessage("§cLa liste des items n'a pas encore été complètement construite, veuillez patientez pendant sa création. Si cela prend trop de temps, merci de contacter un responsable");
						waitingForItems = true;
					}
				}
				else {
					if(waitingForItems) {
						Bukkit.broadcastMessage("§aLa liste a été créée, début de la partie !");
						waitingForItems = false;
					}
					
					if(itemDelay * ++i > gameDuration - 200)
						cancel();
					
					Material material = API.instance().getGameParameterBoolean(PLOUF_WOOD_DEACTIVATED) ? Items.getRandomNoWoodGiveableItem(itemRandom) : Items.getRandomGiveableItem(itemRandom);
					if(!gaveWood && Items.isWood(material))
						gaveWood = true;
					ItemStack item = new ItemStack(material, 1 + itemRandom.nextInt(Items.isLog(material) ? 6 : Math.min(16, material.getMaxStackSize())));
					getNonSpecPlayers().stream().filter(WrappedPlayer::isOnline).forEach(player ->
					{
						if(player.toBukkit().getInventory().firstEmpty() != -1)
							player.toBukkit().getInventory().addItem(item);
						else
							player.toBukkit().getWorld().dropItem(player.toBukkit().getLocation(), item);
					});
				}
			}
		}.runTaskTimer(plouf, 0, itemDelay);
		
		new BukkitRunnable()
		{
			@Override
			public void run() {
				if(time == gameDuration)
				{
					endGame();
					cancel();
				}
				else
				{
					if(time % 20 == 0)
						updateScoreboardTime();
					
					if(time >= gameDuration - 20 * 10 && time%20 == 0)
					{
						for(WrappedPlayer watcher : WrappedPlayer.of(Bukkit.getOnlinePlayers()))
						{
							if(time == gameDuration - 20 * 10)
							{
								watcher.sendMessage(Plouf.getGame().getPrefix() + I18n.interpretable("ten_seconds_remaining"));
								watcher.toBukkit().playSound(watcher.toBukkit().getLocation(), Sound.ITEM_GOAT_HORN_SOUND_2, 1, 2);
							}
							String title = new MessageBuilder(I18n.interpretable("title_time_remaining")).formatted((gameDuration - time)/20).toString(watcher);
							watcher.toBukkit().showTitle(Title.title(Component.text(title), Component.empty(), Times.times(Ticks.duration(2), Ticks.duration(11), Ticks.duration(7))));
							if((gameDuration - time)/20 < 7)
								watcher.toBukkit().playNote(watcher.toBukkit().getLocation(), Instrument.BASS_GUITAR, Note.natural(1, Tone.values()[((gameDuration - time)/20) % 7]));
						}
					}
				}
				
				if(Items.isFinishedLoading())
					time++;
			}
		}.runTaskTimer(plouf, 1, 1);
	}
	
	public void updateScoreboardTime() {
		
		for(WrappedPlayer watcher : WrappedPlayer.of(Bukkit.getOnlinePlayers()))
		{
			String s = new MessageBuilder(I18n.interpretable("scoreboard_time_remaining")).formatted((gameDuration - time) / 20 / 60 + ":" + String.format("%02d", ((gameDuration - time) / 20) % 60)).toString(watcher);
			watcher.toCosmox().getScoreboard().updateLine(1, s);
		}
	}
	
	public void updateScoreboardScores()
	{
		updateScoreboardScores(false, 0);
	}
	
	public void updateScoreboardScores(boolean sorted, int maxUnique)
	{
		List<Pair<PloufPlayer, String>> lines = new ArrayList<>();
		
		List<PloufPlayer> players = getNonSpecPlayers();
		
		Comparator<PloufPlayer> comparator = (p1, p2) -> Integer.compare(p2.getPoints(maxUnique), p1.getPoints(maxUnique));
		
		(sorted ? players.stream().sorted(comparator) : players.stream()).forEach(player -> lines.add(new Pair<>(player, "§7" + player.getName() + ": §f" + player.getPoints(maxUnique))));
		
		for(WrappedPlayer watcher : WrappedPlayer.of(Bukkit.getOnlinePlayers()))
		{
			for(int i = 0; i < lines.size(); i++)
			{
				if(watcher.toCosmox().getScoreboard().size() <= i + 3 || !watcher.toCosmox().getScoreboard().getLine(i + 3).equals(lines.get(i).getRight()))
					if(i + 3 < 17)
						watcher.toCosmox().getScoreboard().updateLine(i + 3, lines.get(i).getRight());
					else if(lines.get(i).getLeft().equals(watcher))
						watcher.toCosmox().getScoreboard().updateLine(18, lines.get(i).getRight());
			}
		}
	}
	
	public void resetScoreboard()
	{
		for(WrappedPlayer watcher : WrappedPlayer.of(Bukkit.getOnlinePlayers()))
		{
			resetScoreboard(watcher);
		}
		
		updateScoreboardTime();
		updateScoreboardScores();
	}

	public void resetScoreboard(WrappedPlayer watcher) {

		CosmoxScoreboard scoreboard = new CosmoxScoreboard(watcher.toBukkit());

		scoreboard.updateTitle("§f§lPLOUFCRAFT");
		scoreboard.updateLine(0, "§0");
		scoreboard.updateLine(1, "§7§l???");
		scoreboard.updateLine(2, "§1");

		for(int i = 0; i < getNonSpecPlayers().size(); i++)
		{
			scoreboard.updateLine(Math.min(18, i + 3), "§7§l???");
		}
		scoreboard.updateLine(Math.min(19, getNonSpecPlayers().size() + 3), "§2");
		scoreboard.updateLine(Math.min(20, getNonSpecPlayers().size() + 4), "§3");

		watcher.toCosmox().setScoreboard(scoreboard);
	}
	
	public void endGame() {
		started = false;
		
		updateScoreboardTime();
		updateScoreboardScores(true, 0);
		
		((World)map.getWorld()).getEntitiesByClass(Item.class).forEach(Entity::remove);
		
		int maxPoints = getNonSpecPlayers().stream().mapToInt(player -> player.getPoints(0)).max().orElse(0);
		
		for(PloufPlayer player : getNonSpecPlayers())
		{
			if(player.isOnline()) {
				player.toBukkit().setGameMode(GameMode.ADVENTURE);
				player.toBukkit().setAllowFlight(true);
				player.toBukkit().getInventory().clear();
				player.toBukkit().setItemOnCursor(null);
				
				player.toBukkit().playSound(player.toBukkit().getLocation(), Sound.ITEM_GOAT_HORN_SOUND_4, 1, 1.6f);
				
				player.toBukkit().removePotionEffect(PotionEffectType.FAST_DIGGING);
				
				if(player.getPoints(0) <= maxPoints - 10) {
					player.setPotentialRemontada(true);
				}
			}
			
			player.calculateUniqueCrafts(getNonSpecPlayers());
		}
		
		new MessageBuilder(Plouf.getGame().getPrefix() + I18n.interpretable("game_ended")).broadcast();
		
		new BukkitRunnable()
		{
			int i = 0;
			
			public void prePhaseEnd() {
				int maxPoints = getNonSpecPlayers().stream().mapToInt(player -> player.getPoints(-1)).max().getAsInt();
				
				getNonSpecPlayers().stream().filter(player -> player.getPoints(-1) == maxPoints).forEach(player ->
				{
					player.toCosmox().addMolecules(5, new MessageBuilder(I18n.interpretable("molecules_victory")).toString(player));
					
					player.toCosmox().addStatistic(GameVariables.WIN, 1);
					
					plouf.getAPI().getManager().getGame().addToResume(Messages.SUMMARY_WIN.formatted(player.getName()));
					
					if(player.getUniqueCrafts().isEmpty()) {
						player.toCosmox().grantAdvancement(Achievements.WIN_NO_UNIQUE.getId());
					}
					if(!player.didUseWood() && gaveWood) {
						player.toCosmox().grantAdvancement(Achievements.WIN_NO_WOOD.getId());
					}
					if(player.isPotentialRemontada()) {
						player.toCosmox().grantAdvancement(Achievements.WIN_REMONTADA.getId());
					}
				});
				plouf.getAPI().getManager().getGame().addToResume(Messages.SUMMARY_TIME.formatted(new SimpleDateFormat("mm':'ss").format(new Date(gameDuration * 50))));
				getNonSpecPlayers().stream().filter(player -> player.getPoints(-1) != maxPoints).forEach(player -> player.toCosmox().addMolecules(2, new MessageBuilder(I18n.interpretable("molecules_consolation_prize")).toString(player)));
				
				getNonSpecPlayers().forEach(player -> {
					player.toCosmox().addMolecules(getNonSpecPlayers().size() / 2, new MessageBuilder(I18n.interpretable("molecules_nb_players")).toString(player));
					
					player.toCosmox().addStatistic(GameVariables.GAMES_PLAYED, 1);
					
					player.toCosmox().addStatistic(Plouf.PLOUF_ITEMS_CRAFTED, player.getCraftedItems().size());
					player.toCosmox().addStatistic(Plouf.PLOUF_UNIQUE_ITEMS_CRAFTED, player.getUniqueCrafts().size());
					player.toCosmox().addStatistic(GameVariables.TIME_PLAYED, gameDuration / 20);
				});
				
				updateScoreboardScores(true, -1);
			}
			
			public void postPhaseEnd() {
				getNonSpecPlayers().forEach(player -> player.ifOnline(pl -> pl.toBukkit().getInventory().setItem(0, Items.UNIQUE_CRAFTS_HEAD.get(player))));
			}
			
			@Override
			public void run() {
				
				boolean any = false;
				
				for(PloufPlayer player : getNonSpecPlayers())
				{
					if(i >= player.getUniqueCrafts().size() || !player.isOnline())
						continue;
					any = true;
					
					updateScoreboardScores(true, i);
					
					IntStream.range(0, 9).forEach(j -> player.toBukkit().getInventory().setItem(j, new ItemStack(player.getUniqueCrafts().get(i))));
					player.toBukkit().getInventory().setItem(i+9, new ItemStack(player.getUniqueCrafts().get(i)));
					player.toBukkit().updateInventory();
					player.toBukkit().getWorld().spawnParticle(Particle.TOTEM, player.toBukkit().getLocation(), 30);
					
					player.toCosmox().addMolecules(0.5, new MessageBuilder(I18n.interpretable("molecules_unique_craft")).toString(player));
					
					player.toBukkit().playSound(player.toBukkit().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
				}
				
				if(!any)
				{
					prePhaseEnd();
					cancel();
					API.instance().getManager().setPhase(Phase.END);
					
					Bukkit.getScheduler().runTaskLater(plouf, this::postPhaseEnd, 20);
				}
				i++;
			}
		}.runTaskTimer(plouf, 60, 17);
	}
	
	public boolean isStarted() {
		return started;
	}
	
	public int getItemDelay() {
		return (int) (API.instance().getGameParameterFloat(Plouf.PLOUF_ITEM_DELAY) * 20);
	}
}
