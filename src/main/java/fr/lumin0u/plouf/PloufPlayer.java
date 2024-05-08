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
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static fr.lumin0u.plouf.Plouf.PLOUF_AUTO_REMOVE_NONINGREDIENTS;

public class PloufPlayer extends WrappedPlayer
{
	private final Map<Material, ItemStack[]> craftedItems = new HashMap<>();
	private Set<Material> uniqueCrafts = new HashSet<>();
	private final Set<Block> placedBlocks = new HashSet<>();
	
	private boolean usedWood = false;
	private boolean potentialRemontada = false;
	
	@NotNull
	private WrappedPlayer spectatorTarget = WrappedPlayer.NULL;
	
	public PloufPlayer(UUID uid) {
		super(uid);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				if(Plouf.getInstance().getGameManager().isStarted() && isOnline() && API.instance().getGameParameterBoolean(PLOUF_AUTO_REMOVE_NONINGREDIENTS)) {
					for(ItemStack item : toBukkit().getInventory()) {
						if(item != null && !Items.memIsIngredient(item.getType())) {
							Bukkit.getScheduler().runTask(Plouf.getInstance(), () -> toBukkit().getInventory().remove(item.getType()));
						}
					}
				}
				
				if(isSpectator() && spectatorTarget.isNot(null) && spectatorTarget.isOnline()) {
					Bukkit.getScheduler().runTask(Plouf.getInstance(), () -> {
						toBukkit().getOpenInventory().getTopInventory().setContents(spectatorTarget.toBukkit().getOpenInventory().getTopInventory().getContents());
						toBukkit().getOpenInventory().getBottomInventory().setContents(spectatorTarget.toBukkit().getInventory().getContents());
					});
				}
			}
		}.runTaskTimerAsynchronously(Plouf.getInstance(), 2, 2);
	}
	
	public boolean isSpectator() {
		return toCosmox().getTeam().equals(Team.SPEC);
	}
	
	public Set<Material> getCraftedItems() {
		return craftedItems.keySet();
	}
	
	public List<Material> getUniqueCrafts() {
		return new ArrayList<>(uniqueCrafts);
	}
	
	public void addCraftedItem(Material mat, ItemStack[] recipe) {
		craftedItems.put(mat, recipe);
	}
	
	public ItemStack[] getRecipe(Material mat) {
		return craftedItems.get(mat);
	}
	
	public int getPoints(int maxUnique) {
		int n = (maxUnique == -1 ? uniqueCrafts.size() : Math.min(maxUnique, uniqueCrafts.size()));
		return craftedItems.size()
				+ (n * (n + 1)) / 4 + n + (n % 2 == 1 ? 0 : 2); // calcul de 2 + 2 + 3 + 3 + 4 + 4 + ...
	}
	
	public void calculateUniqueCrafts(List<PloufPlayer> others) {
		uniqueCrafts = new HashSet<>(craftedItems.keySet());
		
		for(PloufPlayer player : others)
		{
			if(player.is(this))
				continue;
			
			uniqueCrafts.removeAll(player.craftedItems.keySet());
		}
	}
	
	public Set<Block> getPlacedBlocks()
	{
		return placedBlocks;
	}
	
	public static PloufPlayer of(Object player) {
		return WrappedPlayer.of(player).to(PloufPlayer.class);
	}
	
	public boolean didUseWood() {
		return usedWood;
	}
	
	public void setUsedWood(boolean usedWood) {
		this.usedWood = usedWood;
	}
	
	public boolean isPotentialRemontada() {
		return potentialRemontada;
	}
	
	public void setPotentialRemontada(boolean potentialRemontada) {
		this.potentialRemontada = potentialRemontada;
	}
	
	public void setSpectatorTarget(@NotNull WrappedPlayer spectatorTarget) {
		this.spectatorTarget = spectatorTarget;
	}
	
	@NotNull
	public WrappedPlayer getSpectatorTarget() {
		return spectatorTarget;
	}
}
