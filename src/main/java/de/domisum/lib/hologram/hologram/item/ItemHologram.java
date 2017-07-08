package de.domisum.lib.hologram.hologram.item;

import de.domisum.lib.auxilium.data.container.math.Vector3D;
import de.domisum.lib.auxilium.util.math.VectorUtil;
import de.domisum.lib.auxiliumspigot.data.container.VectorConverter;
import de.domisum.lib.auxiliumspigot.util.PacketUtil;
import de.domisum.lib.hologram.hologram.Hologram;
import net.minecraft.server.v1_9_R1.EnumItemSlot;
import net.minecraft.server.v1_9_R1.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_9_R1.Vector3f;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemHologram extends Hologram
{

	// PROPERTIES
	protected ItemStack itemStack;
	protected double rotation;


	// INIT
	public ItemHologram(Location location, ItemStack itemStack)
	{
		this(location.getWorld(), VectorConverter.toVector3D(location), itemStack);
	}

	public ItemHologram(Vector3D location, ItemStack itemStack)
	{
		this(null, location, itemStack);
	}

	public ItemHologram(ItemStack itemStack)
	{
		this(null, null, itemStack);
	}

	public ItemHologram(World world, Vector3D location, ItemStack itemStack)
	{
		super(world, location);
		this.itemStack = itemStack;
		// TODO implement small
	}


	// GETTERS
	protected Vector3D getRotatableOffset()
	{
		if(this.itemStack.getType() == Material.SKULL_ITEM)
			return new Vector3D(-0.21, -1.03, 0.43);

		if(displayAsFullBlock(this.itemStack.getType()))
			return new Vector3D(-0.05, -0.85, 0.25);

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

		return new Vector3D(0.37, -1.7, -0.55);
	}

	@Override protected Location getArmorStandLocation()
	{
		Vector3D direction = this.location.subtract(this.viewLocation);
		if(direction.length() == 0)
			direction = new Vector3D(0, 0, 0.001);
		else
			direction = direction.normalize();

		double angle = VectorUtil.getYawFromDirection(direction)+this.rotation;
		Vector3D rotatableOffset = getRotatableOffset();
		Vector3D rotatedOffset = VectorUtil.rotateOnXZPlane(rotatableOffset, -angle);

		Location offsetLocation = super.getArmorStandLocation().add(rotatedOffset.x, rotatedOffset.y, rotatedOffset.z);
		offsetLocation.setYaw((float) angle);

		return offsetLocation;
	}


	// SETTERS
	public void setItemStack(ItemStack itemStack)
	{
		this.itemStack = itemStack;

		sendItemInHandPacket(getVisibleToArray());
	}

	@Override public void setViewLocation(Vector3D viewLocation)
	{
		super.setViewLocation(viewLocation);

		teleport();
	}

	public void setRotation(double rotation)
	{
		this.rotation = rotation;
	}


	// ARMORSTAND
	@Override public void createArmorStand()
	{
		super.createArmorStand();

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


	// PACKETS
	@Override protected void showToSendPackets(Player... players)
	{
		super.showToSendPackets(players);
		sendItemInHandPacket(players);
	}

	protected void sendItemInHandPacket(Player... players)
	{
		PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(this.armorStand.getId(), EnumItemSlot.MAINHAND,
				CraftItemStack.asNMSCopy(this.itemStack));

		PacketUtil.sendPacket(packet, players);
	}


	// UTIL
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
