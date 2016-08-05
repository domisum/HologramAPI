package de.domisum.hologramapi.hologram;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.entity.Player;

import de.domisum.auxiliumapi.data.structure.pds.PlayerList;
import de.domisum.auxiliumapi.util.bukkit.PacketUtil;
import net.minecraft.server.v1_9_R1.EntityArmorStand;
import net.minecraft.server.v1_9_R1.PacketPlayOutEntity.PacketPlayOutEntityLook;
import net.minecraft.server.v1_9_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_9_R1.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_9_R1.PacketPlayOutSpawnEntityLiving;

public abstract class Hologram
{

	// REFERENCES
	protected EntityArmorStand armorStand;
	protected List<Player> visibleTo = new PlayerList();

	// PROPERTIES
	protected Location location;


	// -------
	// CONSTRUCTOR
	// -------
	public Hologram(Location location)
	{
		this.location = location.clone();
	}


	// -------
	// GETTERS
	// -------
	public Location getLocation()
	{
		return this.location.clone();
	}

	protected Location getArmorStandLocation()
	{
		return this.location;
	}

	protected Player[] getVisibleToArray()
	{
		return this.visibleTo.toArray(new Player[this.visibleTo.size()]);
	}


	// -------
	// SETTERS
	// -------
	public void setLocation(Location location)
	{
		this.location = location;

		teleport();
	}


	// -------
	// ARMORSTAND
	// -------
	protected void createArmorStand()
	{
		Location asLocation = getArmorStandLocation();
		this.armorStand = new EntityArmorStand(((CraftWorld) asLocation.getWorld()).getHandle(), asLocation.getX(),
				asLocation.getY(), asLocation.getZ());
		this.armorStand.yaw = asLocation.getYaw();

		this.armorStand.setInvisible(true);
		this.armorStand.setGravity(false);
	}

	protected void teleport()
	{
		Location asLoc = getArmorStandLocation();
		this.armorStand.setLocation(asLoc.getX(), asLoc.getY(), asLoc.getZ(), asLoc.getYaw(), asLoc.getPitch());

		sendTeleportPackets(getVisibleToArray());
		sendLook(getVisibleToArray());
	}


	// -------
	// VISIBILITY
	// -------
	public void showTo(Player... players)
	{
		for(Player p : players)
			this.visibleTo.add(p);

		showToSendPackets(players);
	}

	public void hideFrom(Player... players)
	{
		for(Player p : players)
			this.visibleTo.remove(p);

		hideFromSendPackets(players);
	}


	// -------
	// PACKETS
	// -------
	protected void showToSendPackets(Player... players)
	{
		PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(this.armorStand);
		PacketUtil.sendPacket(packet, players);
	}

	protected void hideFromSendPackets(Player... players)
	{
		PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(this.armorStand.getId());
		PacketUtil.sendPacket(packet, players);
	}


	protected void sendTeleportPackets(Player... players)
	{
		PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(this.armorStand);
		PacketUtil.sendPacket(packet, players);
	}

	protected void sendLook(Player... players)
	{
		PacketPlayOutEntityLook packet = new PacketPlayOutEntityLook(this.armorStand.getId(),
				PacketUtil.toPacketAngle(this.armorStand.yaw), PacketUtil.toPacketAngle(this.armorStand.pitch), true);

		PacketUtil.sendPacket(packet, players);
	}

}
