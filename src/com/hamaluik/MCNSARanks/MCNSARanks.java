package com.hamaluik.MCNSARanks;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.hamaluik.MCNSARanks.commands.*;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class MCNSARanks extends JavaPlugin {
	// the basics
	public Logger log = Logger.getLogger("Minecraft");
	public PermissionManager permissions = null;
	
	// the commands..
	public HashMap<String, Command> commands = new HashMap<String, Command>();
	
	// listeners
	MCNSARanksPlayerListener playerListener = new MCNSARanksPlayerListener(this);
	MCNSARanksCommandExecutor commandExecutor = new MCNSARanksCommandExecutor(this);
	
	// startup routine..
	public void onEnable() {		
		// set up the plugin..
		this.setupPermissions();
		
		// import the plugin manager
		PluginManager pm = this.getServer().getPluginManager();
		
		// setup listeners
		pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Event.Priority.Monitor, this);
		pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Event.Priority.Lowest, this);
		
		// register commands
		registerCommand(new CommandPlayers(this));
		registerCommand(new CommandRank(this));
		registerCommand(new CommandRanks(this));
		
		// and set everyones colours
		Player[] players = getServer().getOnlinePlayers();
		for(int i = 0; i < players.length; i++) {
			players[i].setPlayerListName(processColours(permissions.getUser(players[i]).getPrefix() + players[i].getName()));
		}
		
		log.info("[MCNSARanks] plugin enabled");
	}

	// shutdown routine
	public void onDisable() {
		log.info("[MCNSARanks] plugin disabled");
	}
	
	// register a command
	private void registerCommand(Command commandRanks) {
		// add the command to the commands list and register it with Bukkit
		this.commands.put(commandRanks.getCommand(), commandRanks);
		this.getCommand(commandRanks.getCommand()).setExecutor(this.commandExecutor);
	}

	// load the permissions plugin
	public void setupPermissions() {
		if(Bukkit.getServer().getPluginManager().isPluginEnabled("PermissionsEx")) {
			this.permissions = PermissionsEx.getPermissionManager();
			log.info("[MCNSARanks] PermissionsEx successfully loaded!");
		}
		else {
			log.warning("[MCNSARanks] ERROR: PermissionsEx not found!");
		}
	}
	
	// just an interface function for checking permissions
	// if permissions are down, default to OP status.
	public boolean hasPermission(Player player, String permission) {
		if(permissions != null) {
			return permissions.has(player, permission);
		}
		else {
			return player.isOp();
		}
	}
	
	// allow for colour tags to be used in strings..
	public String processColours(String str) {
		return str.replaceAll("(&([a-f0-9]))", "\u00A7$2");
	}
	
	// strip colour tags from strings..
	public String stripColours(String str) {
		return str.replaceAll("(&([a-f0-9]))", "");
	}
}
