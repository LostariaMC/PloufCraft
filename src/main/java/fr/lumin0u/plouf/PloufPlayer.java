package fr.lumin0u.plouf;

import fr.worsewarn.cosmox.game.Team;
import fr.worsewarn.cosmox.game.WrappedPlayer;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.*;

public class PloufPlayer extends WrappedPlayer
{
	private final Set<Material> craftedItems = new HashSet<>();
	private Set<Material> uniqueCrafts = new HashSet<>();
	private Set<Block> placedBlocks = new HashSet<>();
	
	public PloufPlayer(UUID uid) {
		super(uid);
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
