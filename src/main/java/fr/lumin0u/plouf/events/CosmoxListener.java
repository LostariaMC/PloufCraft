package fr.lumin0u.plouf.events;

import fr.lumin0u.plouf.Plouf;
import fr.worsewarn.cosmox.API;
import fr.worsewarn.cosmox.api.players.WrappedPlayer;
import fr.worsewarn.cosmox.game.events.GameStartEvent;
import fr.worsewarn.cosmox.game.events.GameStopEvent;
import fr.worsewarn.cosmox.game.events.PlayerJoinGameEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class CosmoxListener implements Listener
{
	private boolean gameStarted;

	@EventHandler
	public void onPlayerJoin(PlayerJoinGameEvent event) {

		if(API.instance().getManager().getPhase().getState() == 0) return;

		Player player = event.getPlayer();
		player.setGameMode(GameMode.SPECTATOR);
		if(Bukkit.getOnlinePlayers().size()>1) player.teleport(Bukkit.getOnlinePlayers().stream().filter(all -> all != player).toList().get(0));

		Plouf.getInstance().getGameManager().resetScoreboard(WrappedPlayer.of(player));
	}
	
	@EventHandler
	public void onGameStart(GameStartEvent event)
	{
		if(event.getGame().equals(Plouf.getGame()))
		{
			gameStarted = true;
			
			Plouf.getInstance().getGameManager().onCosmoxStart(event.getMap());
			
			Plouf.getInstance().getServer().getPluginManager().registerEvents(new CraftListener(), Plouf.getInstance());
			Plouf.getInstance().getServer().getPluginManager().registerEvents(new UniqueCraftMenuListener(), Plouf.getInstance());
			Plouf.getInstance().getServer().getPluginManager().registerEvents(new SpectatorListener(), Plouf.getInstance());
			Plouf.getInstance().getServer().getPluginManager().registerEvents(new BlockListener(), Plouf.getInstance());
		}
	}
	
	@EventHandler
	public void onGameStop(GameStopEvent event) {
		
		if(gameStarted)
		{
			gameStarted = false;
			
			HandlerList.unregisterAll(Plouf.getInstance()); //Tous les évènements ne sont plus écoutés
			
			Bukkit.getScheduler().cancelTasks(Plouf.getInstance()); // arret de toutes les taches programmées du plugin
			
			Plouf.getInstance().reset();
			
			Bukkit.getPluginManager().registerEvents(this, Plouf.getInstance());
		}
	}
}
