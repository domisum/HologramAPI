package de.domisum.lib.hologram.hologram;

import de.domisum.lib.auxilium.data.container.math.Vector3D;
import de.domisum.lib.auxiliumspigot.data.structure.pds.PlayerList;
import de.domisum.lib.auxiliumspigot.util.PacketUtil;
import lombok.Setter;
import net.minecraft.server.v1_9_R1.EntityArmorStand;
import net.minecraft.server.v1_9_R1.PacketPlayOutEntity.PacketPlayOutEntityLook;
import net.minecraft.server.v1_9_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_9_R1.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_9_R1.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public abstract class Hologram
{

	// REFERENCES
	protected EntityArmorStand armorStand;
	private List<Player> visibleTo = new PlayerList();

	// PROPERTIES
	@Setter protected World world;
	protected Vector3D location;
	@Setter protected Vector3D viewLocation = new Vector3D(0, 0, 0);


	// INIT
	public Hologram(World world, Vector3D location)
	{
		this.world = world;
		this.location = location;
	}


	// GETTERS
	protected Location getArmorStandLocation()
	{
		return new Location(this.world, this.location.x, this.location.y, this.location.z);
	}

	protected Player[] getVisibleToArray()
	{
		return this.visibleTo.toArray(new Player[this.visibleTo.size()]);
	}


	// SETTERS
	public void setLocation(Vector3D location)
	{
		this.location = location;

		teleport();
	}


	// ARMORSTAND
	protected void createArmorStand()
	{
		Location asLocation = getArmorStandLocation();
		if(asLocation == null)
			throw new IllegalArgumentException("The hologram's armorstand can't be created when the location is null");
		if(asLocation.getWorld() == null)
			throw new IllegalArgumentException("The hologram's armorstand can't be created when the world is null");

		this.armorStand = new EntityArmorStand(((CraftWorld) asLocation.getWorld()).getHandle(), asLocation.getX(),
				asLocation.getY(), asLocation.getZ());
		this.armorStand.yaw = asLocation.getYaw();

		this.armorStand.setInvisible(true);
		this.armorStand.setGravity(false);
	}

	protected void teleport()
	{
		if(this.armorStand == null)
			return;

		Location asLoc = getArmorStandLocation();
		this.armorStand.setLocation(asLoc.getX(), asLoc.getY(), asLoc.getZ(), asLoc.getYaw(), asLoc.getPitch());

		sendTeleportPackets(getVisibleToArray());
		sendLook(getVisibleToArray());
	}


	// VISIBILITY
	public void showTo(Player... players)
	{
		if(this.armorStand == null)
			createArmorStand();

		for(Player p : players)
			if(!this.visibleTo.contains(p))
				this.visibleTo.add(p);

		showToSendPackets(players);
	}

	public void hideFrom(Player... players)
	{
		this.visibleTo.removeAll(Arrays.asList(players));

		hideFromSendPackets(players);
	}


	// PACKETS
	protected void showToSendPackets(Player... players)
	{
		// System.out.println(this.armorStand.getBukkitEntity().getLocation());
		PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(this.armorStand);
		PacketUtil.sendPacket(packet, players);
	}

	protected void hideFromSendPackets(Player... players)
	{
		if(this.armorStand == null)
			return;

		PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(this.armorStand.getId());
		PacketUtil.sendPacket(packet, players);
	}


	private void sendTeleportPackets(Player... players)
	{
		// System.out.println(this.armorStand.getBukkitEntity().getLocation());
		PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(this.armorStand);
		PacketUtil.sendPacket(packet, players);
	}

	private void sendLook(Player... players)
	{
		PacketPlayOutEntityLook packet = new PacketPlayOutEntityLook(this.armorStand.getId(),
				PacketUtil.toPacketAngle(this.armorStand.yaw), PacketUtil.toPacketAngle(this.armorStand.pitch), true);

		PacketUtil.sendPacket(packet, players);
	}

}
