package de.domisum.lib.hologram;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class HologramLib
{

	// REFERENCES
	private static HologramLib instance;
	private JavaPlugin plugin;


	// INIT
	protected HologramLib(JavaPlugin plugin)
	{
		this.plugin = plugin;

		onEnable();
	}

	public static void enable(JavaPlugin plugin)
	{
		if(instance != null)
			return;

		instance = new HologramLib(plugin);
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
		getLogger().info(this.getClass().getSimpleName()+" has been enabled");
	}

	protected void onDisable()
	{
		getLogger().info(this.getClass().getSimpleName()+" has been disabled");
	}


	// GETTERS
	public static HologramLib getInstance()
	{
		return instance;
	}

	public Logger getLogger()
	{
		return getInstance().plugin.getLogger();
	}

}
