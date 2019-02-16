package de.domisum.lib.hologram.hologram;

import de.domisum.lib.auxilium.data.container.math.Vector3D;
import de.domisum.lib.auxiliumspigot.data.structure.pds.PlayerList;
import de.domisum.lib.auxiliumspigot.util.PacketUtil;
import lombok.Setter;
import net.minecraft.server.v1_13_R2.EntityArmorStand;
import net.minecraft.server.v1_13_R2.PacketPlayOutEntity.PacketPlayOutEntityLook;
import net.minecraft.server.v1_13_R2.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_13_R2.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_13_R2.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public abstract class Hologram
{

	// REFERENCES
	protected EntityArmorStand armorStand;
	private final List<Player> visibleTo = new PlayerList();

	// PROPERTIES
	@Setter
	protected World world;
	protected Vector3D location;
	@Setter
	protected Vector3D viewLocation = new Vector3D(0, 0, 0);


	// INIT
	public Hologram(World world, Vector3D location)
	{
		this.world = world;
		this.location = location;
	}


	// GETTERS
	protected Location getArmorStandLocation()
	{
		return new Location(world, location.x, location.y, location.z);
	}

	protected Player[] getVisibleToArray()
	{
		return visibleTo.toArray(new Player[visibleTo.size()]);
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

		armorStand = new EntityArmorStand(((CraftWorld) asLocation.getWorld()).getHandle(),
				asLocation.getX(),
				asLocation.getY(),
				asLocation.getZ()
		);
		armorStand.yaw = asLocation.getYaw();

		armorStand.setInvisible(true);
		armorStand.setNoGravity(true);
	}

	protected void teleport()
	{
		if(armorStand == null)
			return;

		Location asLoc = getArmorStandLocation();
		armorStand.setLocation(asLoc.getX(), asLoc.getY(), asLoc.getZ(), asLoc.getYaw(), asLoc.getPitch());

		sendTeleportPackets(getVisibleToArray());
		sendLook(getVisibleToArray());
	}


	// VISIBILITY
	public void showTo(Player... players)
	{
		if(armorStand == null)
			createArmorStand();

		for(Player p : players)
			if(!visibleTo.contains(p))
				visibleTo.add(p);

		showToSendPackets(players);
	}

	public void hideFrom(Player... players)
	{
		visibleTo.removeAll(Arrays.asList(players));

		hideFromSendPackets(players);
	}


	// PACKETS
	protected void showToSendPackets(Player... players)
	{
		// System.out.println(this.armorStand.getBukkitEntity().getLocation());
		PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(armorStand);
		PacketUtil.sendPacket(packet, players);
	}

	protected void hideFromSendPackets(Player... players)
	{
		if(armorStand == null)
			return;

		PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(armorStand.getId());
		PacketUtil.sendPacket(packet, players);
	}


	private void sendTeleportPackets(Player... players)
	{
		// System.out.println(this.armorStand.getBukkitEntity().getLocation());
		PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(armorStand);
		PacketUtil.sendPacket(packet, players);
	}

	private void sendLook(Player... players)
	{
		PacketPlayOutEntityLook packet = new PacketPlayOutEntityLook(armorStand.getId(),
				PacketUtil.toPacketAngle(armorStand.yaw),
				PacketUtil.toPacketAngle(armorStand.pitch),
				true
		);

		PacketUtil.sendPacket(packet, players);
	}

}
