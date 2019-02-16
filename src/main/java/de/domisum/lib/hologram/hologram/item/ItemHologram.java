package de.domisum.lib.hologram.hologram.item;

import de.domisum.lib.auxilium.data.container.math.Vector3D;
import de.domisum.lib.auxilium.util.java.annotations.API;
import de.domisum.lib.auxilium.util.math.VectorUtil;
import de.domisum.lib.auxiliumspigot.util.PacketUtil;
import de.domisum.lib.hologram.hologram.Hologram;
import lombok.Setter;
import net.minecraft.server.v1_13_R2.EnumItemSlot;
import net.minecraft.server.v1_13_R2.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_13_R2.Vector3f;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemHologram extends Hologram
{

	// PROPERTIES
	private ItemStack itemStack;
	@Setter
	private double rotation;


	// INIT
	public ItemHologram(ItemStack itemStack)
	{
		this(null, null, itemStack);
	}

	@API
	public ItemHologram(World world, Vector3D location, ItemStack itemStack)
	{
		super(world, location);
		this.itemStack = itemStack;
		// TODO implement small
	}


	// GETTERS
	private Vector3D getRotatableOffset()
	{
		if(itemStack.getType() == Material.SKELETON_SKULL)
			return new Vector3D(-0.21, -1.03, 0.43);

		if(shouldDisplayAsFullBlock(itemStack.getType()))
			return new Vector3D(-0.05, -0.85, 0.25);

		if(shouldDisplayAsTool(itemStack.getType()))
			return new Vector3D(0.3, -2.05, 0.06);

		if(itemStack.getType() == Material.BOW)
			return new Vector3D(0.05, -1.7, 0);

		if(itemStack.getType() == Material.SHIELD)
			return new Vector3D(0.69, -1.38, 0.10);

		if(itemStack.getType() == Material.BLACK_BANNER)
			return new Vector3D(0.9, -1.8, 0.07);

		if(shouldDisplayAsRod(itemStack.getType()))
			return new Vector3D(0.93, -1.2, 0.07);

		return new Vector3D(0.37, -1.7, -0.55);
	}

	@Override
	protected Location getArmorStandLocation()
	{
		Vector3D direction = location.subtract(viewLocation);
		if(direction.length() == 0)
			direction = new Vector3D(0, 0, 0.001);
		else
			direction = direction.normalize();

		double angle = VectorUtil.getYawFromDirection(direction)+rotation;
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

	@Override
	public void setViewLocation(Vector3D viewLocation)
	{
		super.setViewLocation(viewLocation);

		teleport();
	}


	// ARMORSTAND
	@Override
	public void createArmorStand()
	{
		super.createArmorStand();

		if(itemStack.getType().name().endsWith("_SKULL"))
			armorStand.setRightArmPose(new Vector3f(-45f, -135f, 0f));
		else if(shouldDisplayAsFullBlock(itemStack.getType()))
			armorStand.setRightArmPose(new Vector3f(-15f, -135f, 0f));
		else if(shouldDisplayAsTool(itemStack.getType()))
			armorStand.setRightArmPose(new Vector3f(-145f, -90f, 0f));
		else if(shouldDisplayAsBannerOrShield(itemStack.getType()))
			armorStand.setRightArmPose(new Vector3f(-90f, 90f, 0f));
		else if(itemStack.getType() == Material.BOW)
			armorStand.setRightArmPose(new Vector3f(0f, 100f, 220f));
		else if(shouldDisplayAsRod(itemStack.getType()))
			armorStand.setRightArmPose(new Vector3f(-35f, 90f, 0f));
		else
			armorStand.setRightArmPose(new Vector3f(-90f, 0f, 0f));
	}


	// PACKETS
	@Override
	protected void showToSendPackets(Player... players)
	{
		super.showToSendPackets(players);
		sendItemInHandPacket(players);
	}

	private void sendItemInHandPacket(Player... players)
	{
		PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(armorStand.getId(),
				EnumItemSlot.MAINHAND,
				CraftItemStack.asNMSCopy(itemStack)
		);

		PacketUtil.sendPacket(packet, players);
	}


	// UTIL
	private static boolean shouldDisplayAsFullBlock(Material material)
	{
		if(material.name().endsWith("_BUTTON"))
			return true;
		if((material == Material.SNOW) || material.name().endsWith("_CARPET"))
			return true;
		if((material == Material.CHORUS_PLANT) || (material == Material.CHORUS_FLOWER))
			return true;
		if(material == Material.SKELETON_SKULL)
			return true;

		if((material == Material.GLASS_PANE) || material.name().contains("STAINED_GLASS_PANE") || (material
				== Material.IRON_BARS))
			return false;
		if((material == Material.HOPPER) || (material == Material.BARRIER))
			return false;

		return material.isSolid();
	}

	private static boolean shouldDisplayAsTool(Material material)
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

	private static boolean shouldDisplayAsRod(Material material)
	{
		if((material == Material.FISHING_ROD) || (material == Material.CARROT_ON_A_STICK))
			return true;

		return false;
	}

	private static boolean shouldDisplayAsBannerOrShield(Material material)
	{
		if(material.name().contains("BANNER") || (material == Material.SHIELD))
			return true;

		return false;
	}

}
