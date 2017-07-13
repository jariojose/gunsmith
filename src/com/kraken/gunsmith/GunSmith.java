// =========================================================================
// |GUNSMITH v0.9.3.1 (WarZone) | for Minecraft v1.12
// | by Kraken | https://www.spigotmc.org/members/kraken_.287802/
// | code inspired by various Bukkit & Spigot devs -- thank you.
// | Special mention: codename_B (FireworkEffectPlayer)
// |
// | Always free & open-source! If the main plugin is being sold/re-branded,
// | please let me know on the SpigotMC site, or wherever you can. Thanks!
// | Source code: https://github.com/randallarms/gunsmith
// =========================================================================

package com.kraken.gunsmith;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

import org.bukkit.ChatColor;

public class GunSmith extends JavaPlugin implements Listener {
	
	public String VERSION = "0.9.3.1";
	
	GSListener listener;
	
	String language;
	ArrayList<String> languages = new ArrayList<String>();
	Messages messenger;
	
	GunSmithGUI gui = new GunSmithGUI(language);
	
	Player player;
	
	boolean isPlayer = false;
	boolean glassBreak = false;
	boolean silentMode = false;
	boolean guiEnabled = true;
	boolean opRequired = false;
	
    @Override
    public void onEnable() {
    	
    	getLogger().info("[GUNSMITH] Loading...");
    	
    	PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this, this);
		
		//Copies the default config.yml from within the .jar if "plugins/config.yml" does not exist
		this.getConfig().options().copyDefaults(true);
		
		this.language = getConfig().getString("language");
		getLogger().info( ChatColor.RED + "[GUNSMITH] Language: " + language.toUpperCase() );
		
		this.messenger = new Messages(language);
		
		languages.add("english");
		languages.add("spanish");
		
		listener = new GSListener(this, language);
		
		RecipeSmith recipes = new RecipeSmith(language);
		
		for (int n = 0; n < recipes.getTotal(); n++) {
			getServer().addRecipe( recipes.getRecipe(n) );
		}
	    	
    	this.silentMode = getConfig().getBoolean("silentMode");
    	silencer(silentMode);
    	getLogger().info("[GUNSMITH] Silent mode: " + silentMode );
    	
    	this.guiEnabled = getConfig().getBoolean("guiEnabled");
    	getLogger().info("[GUNSMITH] GUI enabled: " + guiEnabled );
    	
    	this.opRequired = getConfig().getBoolean("opRequired");
    	getLogger().info("[GUNSMITH] OP requirement enabled: " + opRequired );
    	
    	getLogger().info("[GUNSMITH] Finished loading.");
			
    }
    
    @Override
    public void onDisable() {
        getLogger().info("GunSmith has been disabled.");
    }
    
    public void msg(Player player, String cmd) {
    	messenger.makeMsg(player, cmd);
    }
    
    public void setLanguage() {
    	listener.loadLanguage(language);
    }
    
    public void silencer(boolean setting) {
    	getConfig().set("silentMode", setting);
    	saveConfig();
    	messenger.silence(setting);
    }
    
    public void setGlassBreak() {
    	listener.loadGlassBreak(glassBreak);
    }
    
    //GunSmith commands
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (sender instanceof Player) {
			this.player = (Player) sender;
			this.isPlayer = true;
		}
		
		String command = cmd.getName();
		
		switch (command) {
		
			//Command: guns
			case "guns":
				
				switch (args.length) {
				
					case 0:
						if ( isPlayer ) {
							
							if ( opRequired && !( player.isOp() ) ) {
								msg(player, "errorIllegalCommand");
							} else if ( guiEnabled ) {
								GunSmithGUI.openGSGUI(player);
							} else {
								msg(player, "errorGUINotEnabled");
							}
							
						} else {
							System.out.println("[GUNSMITH] This is a player-only command.");
						}
						
						return true;
						
					case 2:
						
						//Check if sender is a player and if that player has OP perms
						if (isPlayer) {
							
							if ( !player.isOp() ) {
								msg(player, "errorIllegalCommand");
								return true;
							}
							
						}
						
						//Command handling switch
						switch ( args[0].toLowerCase() ) {
						
							case "language":
								
								//Language command handling
								if ( languages.contains( args[1].toLowerCase() ) ) {
									
									this.language = args[1].toLowerCase();
									setLanguage();
									getConfig().set("language", args[1].toLowerCase());
									saveConfig();
									
									if ( !isPlayer ) {
										System.out.println("[GUNSMITH] Language set.");
									} else {
										msg(player, "cmdLang");
									}
									
									return true;
								
								//Language command error handling
								} else {
									
									if ( !isPlayer ) {
										System.out.println("[GUNSMITH] Language not found.");
									} else {
										msg(player, "errorLangNotFound");
									}
									
									return true;
								}
								
							case "glassbreak":
							case "glassBreak":
									
								//Glassbreak command handling
								switch ( args[1].toLowerCase() ) {
								
									case "true":
									case "enable":
									case "enabled":
									case "on":
										this.glassBreak = true;
										setGlassBreak();
										getConfig().set("glassBreak", true);
										saveConfig();
										
										if ( !isPlayer ) {
											System.out.println("[GUNSMITH] Glass-break on gunshot is now enabled.");
										} else {
											msg(player, "cmdGlassBreakOn");
										}
										
										return true;
										
									case "false":
									case "disable":
									case "disabled":
									case "off":
										this.glassBreak = false;
										setGlassBreak();
										getConfig().set("glassBreak", false);
										saveConfig();
										
										if ( !isPlayer ) {
											System.out.println("[GUNSMITH] Glass-break on gunshot is now disabled.");
										} else {
											msg(player, "cmdGlassBreakOff");
										}
										
										return true;
									
									//Glassbreak command error handling
									default: 
										
										if ( !isPlayer ) {
											System.out.println("[GUNSMITH] Unrecognized command format.");
										} else {
											msg(player, "errorGlassBreakFormat");
										}
										
										return true;
								
								}
								
							case "silentmode":
							case "silentMode":
								
								//Silentmode command handling
								switch ( args[1].toLowerCase() ) {
								
								case "true":
								case "enable":
								case "enabled":
								case "on":
									this.silentMode = true;
									silencer(true);
									getConfig().set("silentMode", true);
									saveConfig();
									
									if ( !isPlayer ) {
										System.out.println("[GUNSMITH] Silent mode is now enabled.");
									} else {
										msg(player, "cmdSilentOn");
									}
									
									return true;
									
								case "false":
								case "disable":
								case "disabled":
								case "off":
									this.silentMode = false;
									silencer(false);
									getConfig().set("silentMode", false);
									saveConfig();
									
									if ( !isPlayer ) {
										System.out.println("[GUNSMITH] Silent mode is now disabled.");
									} else {
										msg(player, "cmdSilentOff");
									}
									
									return true;
								
								//Silentmode command error handling
								default: 
									
									if ( !isPlayer ) {
										System.out.println("[GUNSMITH] Unrecognized command format.");
									} else {
										msg(player, "errorSilentModeFormat");
									}
									
									return true;
								
								}
								
							case "gui":
							case "GUI": 
								
								//GUI command handling	
								switch ( args[1].toLowerCase() ) {
								
								case "true":
								case "enable":
								case "enabled":
								case "on":
									this.guiEnabled = true;
									getConfig().set("guiEnabled", true);
									saveConfig();
									
									if ( !isPlayer ) {
										System.out.println("[GUNSMITH] GUI is now enabled.");
									} else {
										msg(player, "cmdGUIEnabled");
									}
									
									return true;
									
								case "false":
								case "disable":
								case "disabled":
								case "off":
									this.guiEnabled = false;
									getConfig().set("guiEnabled", false);
									saveConfig();

									if ( !isPlayer ) {
										System.out.println("[GUNSMITH] GUI is now disabled.");
									} else {
										msg(player, "cmdGUIDisabled");
									}
									
									return true;
								
								//GUI command error handling
								default: 
									
									if ( !isPlayer ) {
										System.out.println("[GUNSMITH] Unrecognized command format.");
									} else {
										msg(player, "errorGUIToggleFormat");
									}
									
									return true;
								
								}
								
						  //Command: opReqGS
			        	    case "opRequiredGS":
			        	    case "oprequiredgs":
			        	    case "opReqGS":
			        	    case "opreqgs":
			        			  
			        	    	if ( args.length == 2 ) {
			        	    		switch ( args[1].toLowerCase() ) {
			        	    			case "on":
			        	    			case "enable":
			        	    			case "enabled":
			        	    			case "true":
			        	    				this.opRequired = true;
											getConfig().set("opRequired", true);
											saveConfig();
											
											if ( !isPlayer ) {
												System.out.println("[GUNSMITH] OP requirement is now enabled.");
											} else {
												msg(player, "cmdOpReqEnabled");
											}
											
			        	    				return true;
			        	    				
			        	    			case "off":
			        	    			case "disable":
			        	    			case "disabled":
			        	    			case "false":
			        	    				this.opRequired = false;
											getConfig().set("opRequired", false);
											saveConfig();
											
											if ( !isPlayer ) {
												System.out.println("[GUNSMITH] OP requirement is now disabled.");
											} else {
												msg(player, "cmdOpReqDisabled");
											}
											
			        	    				return true;
			        	    				
			        	    			default:
			        	    				if ( !isPlayer ) {
			        	    					System.out.println("[GUNSMITH] Unrecognized arguments.");
			        	    				} else {
			        	    					player.sendMessage(ChatColor.RED + "[GS]" + ChatColor.GRAY + " | " + "Try entering \"/guns opReqGS <on/off>\".");
			        	    				}
			        	    				
			        	        	    	return true;
			        	        	    	
			        	    		}
			        	    		
			        	    	} else {
			        	    		
			        	    		if ( !isPlayer ) {
	        	    					System.out.println("[GUNSMITH] Unrecognized arguments.");
	        	    				} else {
	        	    					player.sendMessage(ChatColor.RED + "[GS]" + ChatColor.GRAY + " | " + "Try entering \"/guns opReqGS <on/off>\".");
	        	    				}
			        	    		
	        	        	    	return true;
	        	        	    	
			        	    	}
							
							}
							
					default:
						if (isPlayer) {
							msg(player, "errorIllegalCommand");
							return true;
						} else {
							System.out.println("[GUNSMITH] Unrecognized command format.");
						}
						
				}
			
				
	        //Command: versionGS     
	    		case "versionGS":
	    			
					if ( !isPlayer ) {
						System.out.println("[GUNSMITH] v" + VERSION + " (WarZone / beta)");
					} else {
						player.sendMessage(ChatColor.GRAY + "CURRENT: GunSmith v" + VERSION + " (beta)");
					}
					
	                return true;
	        
	                
	        //Command: giveGun <gunName>
			case "giveGun":
			case "givegun":
				
				switch (args.length) {
				
					case 1:
						switch (args[0]) {
						
							default:
								if (isPlayer ) {
									
									if ( player.isOp() ) {
										return new ItemSmith(language).giveGun(args, player);
									} else {
										msg(player, "errorIllegalCommand");
									}
									
								} else {
									System.out.println("[GUNSMITH] This is a player-only command.");
									return true;
								}
							
						}
						
					case 2:
						switch (args[0]) {
						
							default:
								try {
									
									if ( player.isOp() ) {
										return new ItemSmith(language).giveGun( args, getServer().getPlayer(args[1]) );
									} else {
										msg(player, "errorIllegalCommand");
									}
									
								} catch (NullPointerException npe) {
									
									if (isPlayer) {
										
										msg(player, "errorPlayerNotFound");
										
									} else {
										System.out.println("[GUNSMITH] Player not found.");
									}
									
									return true;
									
								}
								
						}
						
					default:
						
						if (isPlayer) {
							msg(player, "errorGunFormat");
						} else {
							System.out.println("[GUNSMITH] Command not recognized.");
						}
						
						return true;
				
				}
	        
	        //Command: giveAmmo <ammoName>
			case "giveAmmo":
			case "giveammo":
				
				switch (args.length) {
				
					case 1:
						switch (args[0]) {
						
							default:
								if (isPlayer) {
									
									if ( player.isOp() ) {
										new ItemSmith(language).giveAmmo(args[0], player);
									} else {
										msg(player, "errorIllegalCommand");
									}
									
								} else {
									System.out.println("[GUNSMITH] This is a player-only command.");
									return true;
								}
							
						}
						
					case 2:
						switch (args[0]) {
						
							default:
								try {
									
									if ( player.isOp() ) {
										return new ItemSmith(language).giveAmmo( args[0], getServer().getPlayer(args[1]) );
									} else {
										msg(player, "errorIllegalCommand");
									}
									
								} catch (NullPointerException npe) {
									
									if (isPlayer) {
										
										msg(player, "errorPlayerNotFound");
										
									} else {
										System.out.println("[GUNSMITH] Player not found.");
									}
									
									return true;
									
								} catch (ArrayIndexOutOfBoundsException aie) {
									
									if (isPlayer) {
										
										msg(player, "errorIllegalCommand");
										
									} else {
										System.out.println("[GUNSMITH] Unrecognized arguments.");
									}
									
									return true;
									
								}
						
						}
						
					default:
						
						if (isPlayer) {
							
							msg(player, "errorAmmoFormat");
							
						} else {
							System.out.println("[GUNSMITH] Command not recognized.");
						}
						
						return true;
			
				}
				
  			//Command: giveGrenade
  			case "giveGrenade":
  			case "givegrenade":
  				
  				switch (args.length) {
				
				case 1:
					
					switch ( args[0].toLowerCase() ) {
					
						case "frag":
							
							if (isPlayer) {
								
								if ( player.isOp() ) {
									player.getInventory().addItem( new ItemSmith(language).makeGrenade( args[0] ) );
								} else {
									msg(player, "errorIllegalCommand");
								}
								
							} else {
								System.out.println("[GUNSMITH] This is a player only command.");
							}
							
							return true;
							
					}	
					
				case 2:
					
					switch ( args[0].toLowerCase() ) {
					
					case "frag":
							
							try {
								
								if ( isPlayer ) {
									if ( player.isOp() ) {
										Player receiver = (Player) getServer().getPlayer(args[1]);
										receiver.getInventory().addItem( new ItemSmith(language).makeGrenade( args[0] ) );
									} else {
										msg(player, "errorIllegalCommand");
									}
								} else {
									System.out.println("[GUNSMITH] This is a player only command.");
								}
								
							} catch (NullPointerException npe) {
								
								if (isPlayer) {
									player.sendMessage(ChatColor.RED + "[GS]" + ChatColor.GRAY + " | Player not found!");
								} else {
									System.out.println("[GUNSMITH] Player not found.");
								}
								
							}
							
							return true;
							
					}
					
				default:
					if (isPlayer) {
						player.sendMessage(ChatColor.RED + "[GS]" + ChatColor.GRAY + " | Unrecognized format. Use \"/giveGrenade <grenadeName> {player}\"");
					} else {
						System.out.println("[GUNSMITH] Command format not recognized.");
					}
					
					return true;
  				
  				}
	  			
  			//Command: giveArmor
  			case "giveArmor":
  			case "givearmor":
  				
	  				switch (args.length) {
					
					case 1:
						
						switch ( args[0].toLowerCase() ) {
						
							case "pvthelm":
							case "pvtchest":
							case "pvtlegs":
							case "pvtboots":
								
								if (isPlayer) {
									
									if ( player.isOp() ) {
										player.getInventory().addItem( new ItemSmith(language).makeArmor( args[0] ) );
									} else {
										msg(player, "errorIllegalCommand");
									}
									
								} else {
									System.out.println("[GUNSMITH] This is a player only command.");
								}
								
								return true;
								
						}	
						
					case 2:
						
						switch ( args[0].toLowerCase() ) {
						
						case "pvthelm":
						case "pvtchest":
						case "pvtlegs":
						case "pvtboots":
								
								try {
									
									if ( isPlayer ) {
										if ( player.isOp() ) {
											Player receiver = (Player) getServer().getPlayer(args[1]);
											receiver.getInventory().addItem( new ItemSmith(language).makeArmor( args[0] ) );
										} else {
											msg(player, "errorIllegalCommand");
										}
									} else {
										System.out.println("[GUNSMITH] This is a player only command.");
									}
									
								} catch (NullPointerException npe) {
									if (isPlayer) {
										player.sendMessage(ChatColor.RED + "[GS]" + ChatColor.GRAY + " | Player not found!");
									} else {
										System.out.println("[GUNSMITH] Player not found.");
									}
								}
								
								return true;
								
						}
						
					default:
						if (isPlayer) {
							player.sendMessage(ChatColor.RED + "[GS]" + ChatColor.GRAY + " | Unrecognized format. Use \"/giveArmor <armorName> {player}\"");
						} else {
							System.out.println("[GUNSMITH] Command format not recognized.");
						}
						
						return true;
				
				}
	        
			default:
				
				if (isPlayer) {
					msg(player, "errorIllegalCommand");
				} else {
					System.out.println("[GUNSMITH] Command not recognized.");
				}
	        	
	        	return true;
		
		}
		
	}
	
		
}
