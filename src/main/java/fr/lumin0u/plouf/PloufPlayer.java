package fr.lumin0u.plouf;

import fr.lumin0u.plouf.util.Items;
import fr.worsewarn.cosmox.API;
import fr.worsewarn.cosmox.api.players.WrappedPlayer;
import fr.worsewarn.cosmox.game.teams.Team;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static fr.lumin0u.plouf.Plouf.PLOUF_AUTO_REMOVE_NONINGREDIENTS;

public class PloufPlayer extends WrappedPlayer
{
	private final Set<Material> craftedItems = new HashSet<>();
	private Set<Material> uniqueCrafts = new HashSet<>();
	private Set<Block> placedBlocks = new HashSet<>();
	
	public PloufPlayer(UUID uid) {
		super(uid);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				if(Plouf.getInstance().getGameManager().isStarted() && isOnline() && API.instance().getGameParameterBoolean(PLOUF_AUTO_REMOVE_NONINGREDIENTS)) {
					for(ItemStack item : toBukkit().getInventory()) {
						if(!Items.isIngredient(item.getType())) {
							Bukkit.getScheduler().runTask(Plouf.getInstance(), () -> toBukkit().getInventory().remove(item.getType()));
						}
					}
				}
			}
		}.runTaskTimerAsynchronously(Plouf.getInstance(), 5, 5);
	}
	
	public boolean isSpectator() {
		return toCosmox().getTeam().equals(Team.SPEC);
	}
	
	public Set<Material> getCraftedItems() {
		return new HashSet<>(craftedItems);
	}
	
	public List<Material> getUniqueCrafts() {
		return new ArrayList<>(uniqueCrafts);
	}
	
	public void addCraftedItem(Material mat) {
		craftedItems.add(mat);
	}
	
	public int getPoints(int maxUnique) {
		return craftedItems.size() + 2 * (maxUnique == -1 ? uniqueCrafts.size() : Math.min(maxUnique, uniqueCrafts.size()));
	}
	
	public void calculateUniqueCrafts(List<PloufPlayer> others) {
		uniqueCrafts = new HashSet<>(craftedItems);
		
		for(PloufPlayer player : others)
		{
			if(player.is(this))
				continue;
			
			uniqueCrafts.removeIf(player.craftedItems::contains);
		}
	}
	
	public Set<Block> getPlacedBlocks()
	{
		return placedBlocks;
	}
	
	public static PloufPlayer of(Object player) {
		return WrappedPlayer.of(player).to(PloufPlayer.class);
	}
}
