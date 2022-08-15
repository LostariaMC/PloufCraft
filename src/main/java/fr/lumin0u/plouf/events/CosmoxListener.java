package fr.lumin0u.plouf.events;

import fr.lumin0u.plouf.Plouf;
import fr.worsewarn.cosmox.game.ievents.GameStartEvent;
import fr.worsewarn.cosmox.game.ievents.GameStopEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class CosmoxListener implements Listener
{
	private boolean gameStarted;
	
	@EventHandler
	public void onGameStart(GameStartEvent event)
	{
		if(event.getGame().equals(Plouf.getGame()))
		{
			gameStarted = true;
			
			Plouf.getInstance().getGameManager().onCosmoxStart(event.getMap());
			
			Plouf.getInstance().getServer().getPluginManager().registerEvents(new CraftListener(), Plouf.getInstance());
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
