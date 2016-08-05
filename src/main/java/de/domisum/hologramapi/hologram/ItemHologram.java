package de.domisum.hologramapi.hologram;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import de.domisum.auxiliumapi.data.container.math.Vector3D;
import de.domisum.auxiliumapi.util.math.VectorUtil;
import net.minecraft.server.v1_9_R1.EnumItemSlot;
import net.minecraft.server.v1_9_R1.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_9_R1.Vector3f;

public class ItemHologram extends Hologram
{

	// PROPERTIES
	protected ItemStack itemStack;
	protected boolean small;
	protected Location viewLocation;

	protected Vector3f armPose;


	// -------
	// CONSTRUCTOR
	// -------
	public ItemHologram(Location location, ItemStack itemStack, boolean small, Location viewLocation, Vector3f armPose)
	{
		super(location);
		this.itemStack = itemStack;
		this.small = small;
		this.viewLocation = viewLocation;

		this.armPose = armPose;

		createArmorStand();
	}


	// -------
	// GETTERS
	// -------
	protected Vector3D getRotatableOffset()
	{
		if(this.itemStack.getType() == Material.SKULL_ITEM)
			return new Vector3D(-0.21, -1.03, 0.43);

		if(displayAsFullBlock(this.itemStack.getType()))
			return new Vector3D(-0.05, -0.8, 0.25);

		if(displayAsTool(this.itemStack.getType()))
			return new Vector3D(0.3, -2.05, 0.06);

		if(this.itemStack.getType() == Material.BOW)
			return new Vector3D(0.05, -1.7, 0);

		if(this.itemStack.getType() == Material.SHIELD)
			return new Vector3D(0.69, -1.38, 0.10);

		if(this.itemStack.getType() == Material.BANNER)
			return new Vector3D(0.9, -1.8, 0.07);

		if(displayAsRod(this.itemStack.getType()))
			return new Vector3D(0.93, -1.2, 0.07);

		return new Vector3D(0.37, -1.65, -0.55);
	}

	@Override
	protected Location getArmorStandLocation()
	{
		Vector direction = this.location.toVector().subtract(this.viewLocation.toVector()).normalize();
		if(direction.length() == 0)
			direction = new Vector(0.001, 0, 0);

		Location helperLocation = this.viewLocation.clone();
		helperLocation.setDirection(direction);
		float angle = helperLocation.getYaw() - this.location.getYaw();
		Vector3D rotatedOffset = VectorUtil.rotateOnXZPlane(getRotatableOffset(), -angle);

		Location offsetLocation = this.location.clone().add(rotatedOffset.x, rotatedOffset.y, rotatedOffset.z);
		offsetLocation.setYaw(angle);

		return offsetLocation;
	}


	// -------
	// SETTERS
	// -------
	public void setItemStack(ItemStack itemStack)
	{
		this.itemStack = itemStack;

		sendItemInHandPacket(getVisibleToArray());
	}

	public void setSmall(boolean small)
	{
		// TODO set small packet
	}

	public void setViewLocation(Location viewLocation)
	{
		this.viewLocation = viewLocation;

		teleport();
	}


	// -------
	// ARMORSTAND
	// -------
	@Override
	public void createArmorStand()
	{
		super.createArmorStand();

		this.armorStand.setArms(true);
		if(this.armPose != null)
			this.armorStand.setRightArmPose(this.armPose);
		else
		{
			if(this.itemStack.getType() == Material.SKULL_ITEM)
				this.armorStand.setRightArmPose(new Vector3f(-45f, -135f, 0f));
			else if(displayAsFullBlock(this.itemStack.getType()))
				this.armorStand.setRightArmPose(new Vector3f(-15f, -135f, 0f));
			else
			{

				if(displayAsTool(this.itemStack.getType()))
					this.armorStand.setRightArmPose(new Vector3f(-145f, -90f, 0f));
				else if(displayAsBannerOrShield(this.itemStack.getType()))
					this.armorStand.setRightArmPose(new Vector3f(-90f, 90f, 0f));
				else if(this.itemStack.getType() == Material.BOW)
					this.armorStand.setRightArmPose(new Vector3f(0f, 100f, 220f));
				else if(displayAsRod(this.itemStack.getType()))
					this.armorStand.setRightArmPose(new Vector3f(-35f, 90f, 0f));
				else
					this.armorStand.setRightArmPose(new Vector3f(-90f, 0f, 0f));
			}
		}

		this.armorStand.setSmall(this.small);
	}

	@Override
	protected void teleport()
	{
		super.teleport();

		sendItemInHandPacket(getVisibleToArray());
	}


	// -------
	// PACKETS
	// -------
	@Override
	protected void showToSendPackets(Player... players)
	{
		super.showToSendPackets(players);
		sendItemInHandPacket(players);
	}

	protected void sendItemInHandPacket(Player... players)
	{
		PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(this.armorStand.getId(), EnumItemSlot.MAINHAND,
				CraftItemStack.asNMSCopy(this.itemStack));

		for(Player p : players)
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
	}


	// -------
	// UTIL
	// -------
	protected static boolean displayAsFullBlock(Material material)
	{
		if((material == Material.STONE_BUTTON) || (material == Material.WOOD_BUTTON))
			return true;
		if((material == Material.SNOW) || (material == Material.CARPET))
			return true;
		if((material == Material.CHORUS_PLANT) || (material == Material.CHORUS_FLOWER))
			return true;
		if((material == Material.SKULL_ITEM))
			return true;

		if((material == Material.THIN_GLASS) || (material == Material.STAINED_GLASS_PANE) || (material == Material.IRON_FENCE))
			return false;
		if((material == Material.HOPPER) || (material == Material.BARRIER))
			return false;

		return material.isSolid();
	}

	protected static boolean displayAsTool(Material material)
	{
		if((material == Material.STICK) || (material == Material.BONE) || (material == Material.BLAZE_ROD))
			return true;

		String name = material.name();
		if(name.endsWith("_SWORD"))
			return true;
		if(name.endsWith("_SPADE"))
			return true;
		if(name.endsWith("_PICKAXE"))
			return true;
		if(name.endsWith("_AXE"))
			return true;
		if(name.endsWith("_HOE"))
			return true;

		return false;
	}

	protected static boolean displayAsRod(Material material)
	{
		if((material == Material.FISHING_ROD) || (material == Material.CARROT_STICK))
			return true;

		return false;
	}

	protected static boolean displayAsBannerOrShield(Material material)
	{
		if((material == Material.BANNER) || (material == Material.SHIELD))
			return true;

		return false;
	}

}
