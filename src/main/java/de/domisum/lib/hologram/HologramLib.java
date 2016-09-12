package de.domisum.lib.hologram;

import de.domisum.lib.auxilium.AuxiliumLib;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class HologramLib
{

	// REFERENCES
	private static HologramLib instance;
	private JavaPlugin plugin;


	// -------
	// CONSTRUCTOR
	// -------
	protected HologramLib(JavaPlugin plugin)
	{
		instance = this;
		this.plugin = plugin;

		onEnable();
	}

	public static void enable(JavaPlugin plugin)
	{
		if(instance != null)
			return;

		new HologramLib(plugin);
	}

	public static void disable()
	{
		if(instance == null)
			return;

		getInstance().onDisable();
		instance = null;
	}

	protected void onEnable()
	{
		AuxiliumLib.enable(this.plugin);

		getLogger().info(this.getClass().getSimpleName()+" has been enabled");
	}

	protected void onDisable()
	{
		AuxiliumLib.disable();

		getLogger().info(this.getClass().getSimpleName()+" has been disabled");
	}


	// -------
	// GETTERS
	// -------
	public static HologramLib getInstance()
	{
		return instance;
	}

	public Logger getLogger()
	{
		return getInstance().plugin.getLogger();
	}

}
