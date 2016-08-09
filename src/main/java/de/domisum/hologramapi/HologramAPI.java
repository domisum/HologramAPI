package de.domisum.hologramapi;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import de.domisum.auxiliumapi.AuxiliumAPI;

public class HologramAPI
{

	// REFERENCES
	private static HologramAPI instance;
	private JavaPlugin plugin;


	// -------
	// CONSTRUCTOR
	// -------
	protected HologramAPI(JavaPlugin plugin)
	{
		instance = this;
		this.plugin = plugin;

		onEnable();
	}

	public static void enable(JavaPlugin plugin)
	{
		if(instance != null)
			return;

		new HologramAPI(plugin);
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
		AuxiliumAPI.enable(this.plugin);

		getLogger().info(this.getClass().getSimpleName() + " has been enabled");
	}

	protected void onDisable()
	{
		getLogger().info(this.getClass().getSimpleName() + " has been disabled");
	}


	// -------
	// GETTERS
	// -------
	public static HologramAPI getInstance()
	{
		return instance;
	}

	public Logger getLogger()
	{
		return getInstance().plugin.getLogger();
	}

}
