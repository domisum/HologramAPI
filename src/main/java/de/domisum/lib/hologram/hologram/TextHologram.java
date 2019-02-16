package de.domisum.lib.hologram.hologram;

import de.domisum.lib.auxilium.data.container.math.Vector3D;
import de.domisum.lib.auxilium.util.java.annotations.API;
import de.domisum.lib.auxiliumspigot.data.container.VectorConverter;
import de.domisum.lib.auxiliumspigot.util.PacketUtil;
import lombok.Getter;
import net.minecraft.server.v1_13_R2.ChatMessage;
import net.minecraft.server.v1_13_R2.PacketPlayOutEntityMetadata;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

@API
public class TextHologram extends Hologram
{

	// CONSTANTS
	private static final double OFFSET_Y = -2.4;

	// PROPERTIES
	@Getter
	private String text;


	// INIT
	@API
	public TextHologram(Location location, String text)
	{
		this(location.getWorld(), VectorConverter.toVector3D(location), text);
	}

	@API
	public TextHologram(World world, String text)
	{
		this(world, new Vector3D(), text);
	}

	@API
	public TextHologram(String text)
	{
		this(null, new Vector3D(), text);
	}

	@API
	public TextHologram(World world, Vector3D location, String text)
	{
		super(world, location);
		this.text = text;
	}


	// GETTERS
	public double getWidth()
	{
		return getWidth(text);
	}

	@Override
	protected Location getArmorStandLocation()
	{
		return super.getArmorStandLocation().add(0, OFFSET_Y, 0);
	}


	// SETTERS
	public void setText(String text)
	{
		this.text = text;

		if(armorStand != null)
		{
			armorStand.setCustomName(new ChatMessage(text));
			sendMetadataPacket(getVisibleToArray());
		}
	}


	// ARMORSTAND
	@Override
	public void createArmorStand()
	{
		super.createArmorStand();

		armorStand.setCustomName(new ChatMessage(text));
		armorStand.setCustomNameVisible(true);
		armorStand.setSmall(true);
	}


	// PACKETS
	private void sendMetadataPacket(Player... players)
	{
		PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(armorStand.getId(),
				armorStand.getDataWatcher(),
				true
		);
		PacketUtil.sendPacket(packet, players);
	}


	// UTIL
	@API
	public static double getWidth(String text)
	{
		String rawText = ChatColor.stripColor(text);

		return rawText.length()*0.13;
	}

}
