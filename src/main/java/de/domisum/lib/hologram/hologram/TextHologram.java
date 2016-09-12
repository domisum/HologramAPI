package de.domisum.lib.hologram.hologram;

import de.domisum.auxiliumapi.util.java.annotations.APIUsage;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import de.domisum.auxiliumapi.data.container.math.Vector3D;
import de.domisum.auxiliumapi.util.bukkit.PacketUtil;
import net.minecraft.server.v1_9_R1.PacketPlayOutEntityMetadata;

@APIUsage
public class TextHologram extends Hologram
{

	// CONSTANTS
	protected static final double OFFSET_Y = -2.4;

	// PROPERTIES
	protected String text;


	// -------
	// CONSTRUCTOR
	// -------
	public TextHologram(Location location, String text)
	{
		this(location.getWorld(), new Vector3D(location), text);
	}

	public TextHologram(World world, String text)
	{
		this(world, new Vector3D(), text);
	}

	public TextHologram(String text)
	{
		this(null, new Vector3D(), text);
	}

	public TextHologram(World world, Vector3D location, String text)
	{
		super(world, location);
		this.text = text;
	}


	// -------
	// GETTERS
	// -------
	public String getText()
	{
		return this.text;
	}

	public double getWidth()
	{
		return getWidth(this.text);
	}


	@Override
	protected Location getArmorStandLocation()
	{
		return super.getArmorStandLocation().add(0, OFFSET_Y, 0);
	}


	// -------
	// SETTERS
	// -------
	public void setText(String text)
	{
		this.text = text;

		if(this.armorStand != null)
		{
			this.armorStand.setCustomName(text);
			sendMetadataPacket(getVisibleToArray());
		}
	}


	// -------
	// ARMORSTAND
	// -------
	@Override
	public void createArmorStand()
	{
		super.createArmorStand();

		this.armorStand.setCustomName(this.text);
		this.armorStand.setCustomNameVisible(true);
		this.armorStand.setSmall(true);
	}


	// -------
	// PACKETS
	// -------
	protected void sendMetadataPacket(Player... players)
	{
		PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(this.armorStand.getId(),
				this.armorStand.getDataWatcher(), true);
		PacketUtil.sendPacket(packet, players);
	}


	// -------
	// UTIL
	// -------
	public static double getWidth(String text)
	{
		text = ChatColor.stripColor(text);

		return text.length()*0.13;
	}

}
