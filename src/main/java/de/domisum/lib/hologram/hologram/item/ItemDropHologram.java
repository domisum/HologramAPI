package de.domisum.lib.hologram.hologram.item;

import de.domisum.lib.auxilium.data.container.math.Vector3D;
import de.domisum.lib.auxiliumspigot.util.PacketUtil;
import de.domisum.lib.hologram.hologram.Hologram;
import net.minecraft.server.v1_9_R1.EntityItem;
import net.minecraft.server.v1_9_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_9_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_9_R1.PacketPlayOutMount;
import net.minecraft.server.v1_9_R1.PacketPlayOutSpawnEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemDropHologram extends Hologram
{

	// CONSTANTS
	protected static final double OFFSET_Y = -1.8;

	// REFERENCES
	protected EntityItem item;

	// PROPERTIES
	protected ItemStack itemStack;


	// -------
	// CONSTRUCTOR
	// -------
	public ItemDropHologram(World world, ItemStack itemStack)
	{
		this(world, new Vector3D(), itemStack);
	}

	public ItemDropHologram(World world, Vector3D location, ItemStack itemStack)
	{
		super(world, location);
		this.itemStack = itemStack;

		createArmorStand();
		createItem();
	}


	// -------
	// GETTERS
	// -------
	@Override protected Location getArmorStandLocation()
	{
		return super.getArmorStandLocation().add(0, OFFSET_Y, 0);
	}


	// -------
	// SETTERS
	// -------
	public void setItemStack(ItemStack itemStack)
	{
		this.itemStack = itemStack;
		this.item.setItemStack(CraftItemStack.asNMSCopy(itemStack));

		sendItemMetadataPacket(getVisibleToArray());
	}


	// -------
	// ARMORSTAND
	// -------
	protected void createItem()
	{
		this.item = new EntityItem(((CraftWorld) this.world).getHandle(), this.location.x, this.location.y, this.location.z,
				CraftItemStack.asNMSCopy(this.itemStack));
		this.armorStand.passengers.add(this.item);
	}


	// -------
	// PACKETS
	// -------
	@Override protected void showToSendPackets(Player... players)
	{
		super.showToSendPackets(players);

		sendItemEntity(players);
	}

	@Override protected void hideFromSendPackets(Player... players)
	{
		super.hideFromSendPackets(players);
		despawnItemEntity(players);
	}


	protected void despawnItemEntity(Player... players)
	{
		PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(this.item.getId());
		PacketUtil.sendPacket(packet, players);
	}

	protected void sendItemEntity(Player... players)
	{
		PacketPlayOutSpawnEntity packetSpawn = new PacketPlayOutSpawnEntity(this.item, 2, 1);
		PacketPlayOutMount packetMount = new PacketPlayOutMount(this.armorStand);

		for(Player p : players)
		{
			PacketUtil.sendPacket(packetSpawn, p);
			PacketUtil.sendPacket(packetMount, p);
		}

		sendItemMetadataPacket(players);
	}

	protected void sendItemMetadataPacket(Player... players)
	{
		PacketPlayOutEntityMetadata packetMetadata = new PacketPlayOutEntityMetadata(this.item.getId(),
				this.item.getDataWatcher(), true);

		PacketUtil.sendPacket(packetMetadata, players);
	}

}
