package de.domisum.lib.hologram.hologram.item;

import de.domisum.lib.auxilium.data.container.math.Vector3D;
import de.domisum.lib.auxilium.util.java.annotations.API;
import de.domisum.lib.auxiliumspigot.data.container.VectorConverter;
import de.domisum.lib.auxiliumspigot.util.PacketUtil;
import de.domisum.lib.hologram.hologram.Hologram;
import net.minecraft.server.v1_13_R2.EntityItem;
import net.minecraft.server.v1_13_R2.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_13_R2.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_13_R2.PacketPlayOutMount;
import net.minecraft.server.v1_13_R2.PacketPlayOutSpawnEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@API
public class ItemDropHologram extends Hologram
{

	// CONSTANTS
	private static final double OFFSET_Y = -1.8;

	// REFERENCES
	private EntityItem item;

	// PROPERTIES
	private ItemStack itemStack;


	// INIT
	public ItemDropHologram(Location location, ItemStack itemStack)
	{
		this(location.getWorld(), VectorConverter.toVector3D(location), itemStack);
	}

	@API
	public ItemDropHologram(World world, Vector3D location, ItemStack itemStack)
	{
		super(world, location);
		this.itemStack = itemStack;

		createArmorStand();
		createItem();
	}


	// GETTERS
	@Override
	protected Location getArmorStandLocation()
	{
		return super.getArmorStandLocation().add(0, OFFSET_Y, 0);
	}


	// SETTERS
	@API
	public void setItemStack(ItemStack itemStack)
	{
		this.itemStack = itemStack;
		item.setItemStack(CraftItemStack.asNMSCopy(itemStack));

		sendItemMetadataPacket(getVisibleToArray());
	}


	// ARMORSTAND
	private void createItem()
	{
		item = new EntityItem(((CraftWorld) world).getHandle(),
				location.x,
				location.y,
				location.z,
				CraftItemStack.asNMSCopy(itemStack)
		);
		armorStand.passengers.add(item);
	}


	// PACKETS
	@Override
	protected void showToSendPackets(Player... players)
	{
		super.showToSendPackets(players);

		sendItemEntity(players);
	}

	@Override
	protected void hideFromSendPackets(Player... players)
	{
		super.hideFromSendPackets(players);
		despawnItemEntity(players);
	}


	private void despawnItemEntity(Player... players)
	{
		PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(item.getId());
		PacketUtil.sendPacket(packet, players);
	}

	private void sendItemEntity(Player... players)
	{
		PacketPlayOutSpawnEntity packetSpawn = new PacketPlayOutSpawnEntity(item, 2, 1);
		PacketPlayOutMount packetMount = new PacketPlayOutMount(armorStand);

		for(Player p : players)
		{
			PacketUtil.sendPacket(packetSpawn, p);
			PacketUtil.sendPacket(packetMount, p);
		}

		sendItemMetadataPacket(players);
	}

	private void sendItemMetadataPacket(Player... players)
	{
		PacketPlayOutEntityMetadata packetMetadata = new PacketPlayOutEntityMetadata(item.getId(), item.getDataWatcher(), true);

		PacketUtil.sendPacket(packetMetadata, players);
	}

}
