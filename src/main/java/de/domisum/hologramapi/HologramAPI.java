package de.domisum.hologramapi;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import de.domisum.auxiliumapi.AuxiliumAPI;
import de.domisum.hologramapi.hologram.HologramManager;

public class HologramAPI
{

	// REFERENCES
	private static HologramAPI instance;
	private JavaPlugin plugin;

	private HologramManager hologramManager;


	// -------
	// CONSTRUCTOR
	// -------
	protected HologramAPI(JavaPlugin plugin)
	{
		instance = this;
		this.plugin = plugin;

		onEnable();
	}

	public static void initialize(JavaPlugin plugin)
	{
		new HologramAPI(plugin);
	}

	protected void onEnable()
	{
		AuxiliumAPI.initialize(this.plugin);

		getLogger().info(this.getClass().getSimpleName() + " has been enabled");
	}

	public void onDisable()
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

	public static HologramManager getHologramManager()
	{
		return getInstance().hologramManager;
	}

	public Logger getLogger()
	{
		return getInstance().plugin.getLogger();
	}

}
