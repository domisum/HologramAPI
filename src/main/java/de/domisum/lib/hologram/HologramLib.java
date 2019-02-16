package de.domisum.lib.hologram;

import de.domisum.lib.auxilium.util.java.annotations.API;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

@API
public class HologramLib
{

	// REFERENCES
	@Getter
	private static HologramLib instance;
	private JavaPlugin plugin;


	// INIT
	private HologramLib(JavaPlugin plugin)
	{
		this.plugin = plugin;

		onEnable();
	}

	@API
	public static void enable(JavaPlugin plugin)
	{
		if(instance != null)
			return;

		instance = new HologramLib(plugin);
	}

	@API
	public static void disable()
	{
		if(instance == null)
			return;

		getInstance().onDisable();
		instance = null;
	}

	private void onEnable()
	{
		getLogger().info(this.getClass().getSimpleName()+" has been enabled");
	}

	private void onDisable()
	{
		getLogger().info(this.getClass().getSimpleName()+" has been disabled");
	}


	// GETTERS
	private Logger getLogger()
	{
		return getInstance().plugin.getLogger();
	}

}
