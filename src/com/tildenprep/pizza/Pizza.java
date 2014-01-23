package com.tildenprep.pizza;

//Testing

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;


import java.io.File;

public class Pizza extends JavaPlugin implements Listener {

    public void onEnable(){
        getLogger().info("onEnable has been invoked!");
        getServer().getPluginManager().registerEvents(this, this);
        File file = new File(getDataFolder(), "config.yml");
        if(!file.exists()){
            try{
                saveDefaultConfig();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void onDisable(){
        getLogger().info("onDisable has been invoked!");
        saveConfig();
    }


    @EventHandler
    public void onPlayerInteractBlock(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        //checks if youa re holding
        if (player.getItemInHand().getType() == org.bukkit.Material.STICK){
            if (player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("wand")){

                org.bukkit.util.Vector v = player.getLocation().getDirection();
                Location loc = player.getLocation();
                for (int i=100;i>0;i--){
                    player.getWorld().playEffect(loc, Effect.MOBSPAWNER_FLAMES, 2004);
                    loc = loc.add(v);
                }

                int targetX = player.getTargetBlock(null, 50).getLocation().getBlockX();
                int targetY = player.getTargetBlock(null, 50).getLocation().getBlockY();
                int targetZ = player.getTargetBlock(null, 50).getLocation().getBlockZ();
                int playerX = player.getLocation().getBlockX();
                int playerY = player.getLocation().getBlockY();
                int playerZ = player.getLocation().getBlockZ();
                player.getWorld().playEffect(player.getTargetBlock(null, 50).getLocation(), Effect.MOBSPAWNER_FLAMES, 2004);
                Player[] players = Bukkit.getServer().getOnlinePlayers();
                for (int i=0;i<players.length;i++){
                    Player current = players[i];
                    int curX = current.getLocation().getBlockX();
                    int curY = current.getLocation().getBlockY();
                    int curZ = current.getLocation().getBlockZ();
                    if (curX >= targetX-1 && curX <= targetX+1 && curY >= targetY-1 && curY <= targetY+1 && curZ >= targetZ-1 && curZ <= targetZ+1){
                        //set
                        current.setFireTicks(1000);
                    }
                }
            }
            else if (player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("griefwand")){
                player.getTargetBlock(null, 50).setType(org.bukkit.Material.AIR);
            }
        }
    }

    @EventHandler
    public void onEntityDamage (EntityDamageByEntityEvent e) {
        Player player = (Player) e.getDamager();
        Location loc = new Location(player.getWorld(), 0, 0, 0);
        if (player.getItemInHand().getType() == Material.STICK){
            if (player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("supahtroll")){
                player.getWorld().playEffect(e.getEntity().getLocation(), Effect.MOBSPAWNER_FLAMES, 2004);
                loc.setX(e.getEntity().getLocation().getX() -50 + (int)(Math.random() * ((50 + 50) + 1)));
                loc.setZ(e.getEntity().getLocation().getZ() -50 + (int)(Math.random() * ((50 + 50) + 1)));
                loc.setY(75);
                e.getEntity().teleport(loc);
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event){
        String rank;
        String p = event.getPlayer().getName()+ "Rank";
        if (this.getConfig().getString(p)!=null){
            rank = this.getConfig().getString(p);
        }
        else{
            rank = "§4§l[ERROR]§f";
        }
        event.setFormat(rank + "%s: %s");
        String text = event.getMessage();
        String[] characters = text.split("");
        String output = "";
        if (text.contains("&")){
            for (int i=0;i<characters.length;i++){
                if (characters[i].equals("&")){
                    output = output + "§";
                }
                else {
                    output = output + characters[i];
                }
            }
            event.getPlayer().chat(output);
            event.setCancelled(true);
        }
        /*String[] words = text.split(" ");
        for (int i=0;i<words.length;i++){
            if (words[i].equalsIgnoreCase("swag")||words[i].equalsIgnoreCase("sweg")||words[i].toLowerCase().contains("sweg")||words[i].toLowerCase().contains("swag")){
                event.getPlayer().chat("§6§lPUNCH ME IN THE FACE IRL!!!!");
                event.setCancelled(true);
            }
            if(words[i].equalsIgnoreCase("yolo")||words[i].toLowerCase().contains("yolo")){
                event.getPlayer().chat("§4§lI IZ A IDIOT!!!!");
                event.getPlayer().sendMessage("§2§lYou believe YOLO, so you should test that theory and die.");
                PotionEffect yolonausea = new PotionEffect(PotionEffectType.CONFUSION, 999999999, 1);
                yolonausea.apply(event.getPlayer());
                event.setCancelled(true);
            }
        }*/
    }
    @EventHandler
    public void onLogin(PlayerLoginEvent event){
        if (this.getConfig().getString(event.getPlayer().getName())!=null){
            String nick = this.getConfig().getString(event.getPlayer().getName());
            event.getPlayer().setDisplayName(nick);
            event.getPlayer().setPlayerListName(nick);
        }
        String p = event.getPlayer().getName()+"Rank";
        String place = this.getConfig().getString(p);
        System.out.println("Rank: "+ place + " " + event.getPlayer().getName()+"Rank");
        if (place==null){
            this.getConfig().set(p, "§7[Guest]");
        }
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        if (this.getConfig().getString(event.getPlayer().getName())!=null){
            event.setJoinMessage("§e"+ this.getConfig().getString(event.getPlayer().getName()) + "§e joined the game");
        }
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        if (this.getConfig().getString(event.getPlayer().getName())!=null){
            event.setQuitMessage("§e"+ this.getConfig().getString(event.getPlayer().getName()) + "§e joined the game");
        }
    }




    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        Player player = (Player) sender;
        if(cmd.getName().equalsIgnoreCase("nick") || cmd.getName().equalsIgnoreCase("nickname")){ // If the player typed /nick then do the following...
            if (args[0]==null){
                return false;
            }

            String[] characters = args[0].split("");
            String output = "";
            if (args[0].contains("&")){
                for (int i=0;i<characters.length;i++){
                    if (characters[i].equals("&")){
                        output = output + "§";
                    }
                    else {
                        output = output + characters[i];
                    }
                }
            }
            else{
                output = args[0];
            }
            player.setDisplayName(output + "§f");
            player.setPlayerListName(output + "§f");
            player.sendMessage("§6§lYour nickname §f" + output + "§6§l has been assigned.");
            String p = player.getName();
            String dn = player.getDisplayName();
            this.getConfig().set(p, dn);
            return true;
        }
        else if(cmd.getName().equalsIgnoreCase("test")){ // If the player typed /nick then do the following...
            player.getWorld().playEffect(player.getLocation(), Effect.MOBSPAWNER_FLAMES, 2004);
            return true;
        }
        else if (cmd.getName().equalsIgnoreCase("nickother")){
            if (args[0]==null){
                return false;
            }

            Player target = Bukkit.getServer().getPlayer(args[0]);
            String[] characters = args[1].split("");
            String output = "";
            if (args[0].contains("&")){
                for (int i=0;i<characters.length;i++){
                    if (characters[i].equals("&")){
                        output = output + "§";
                    }
                    else {
                        output = output + characters[i];
                    }
                }
            }
            else{
                output = args[1];
            }
            target.setDisplayName(output + "§f");
            target.setPlayerListName(output + "§f");
            player.sendMessage("§6§lYou have changed §f" + args[0] + "§6§l's nickname to §f"  + output);
            String p = target.getName();
            String dn = target.getDisplayName();
            this.getConfig().set(p, dn);
            return true;
        }
        else if (cmd.getName().equalsIgnoreCase("setrank")){;
            /*if (args[0]!=null){
                return true;
            }
            else {
                return false;
            }*/
            String[] characters = (args[1]).split("");
            String output = "";
            if (args[1].contains("&")){
                for (int i=0;i<characters.length;i++){
                    if (characters[i].equals("&")){
                        output = output + "§";
                    }
                    else {
                        output = output + characters[i];
                    }
                }
            }
            else{
                output = args[1];
            }
            this.getConfig().set(args[0]+"Rank", "[" + output + "]§f");
            player.sendMessage("You have changed " + args[0] + "'s Rank to ["+ output + "]");
            return true;
        }
        else if (cmd.getName().equalsIgnoreCase("invsee")){
            if (args.length !=1){
                return false;
            }
            Player target = Bukkit.getServer().getPlayer(args[0]);
            if (target==null){
                player.sendMessage("§4This player is not online");
                return false;
            }
            player.openInventory(target.getInventory());
            return true;
        }
        else if (cmd.getName().equalsIgnoreCase("kickviewer") && this.getConfig().getString(player.getName()+"Rank").equals("[§5Streamer]§f")){
            if (args.length !=1){
                return false;
            }
            Player target = Bukkit.getServer().getPlayer(args[0]);
            target.kickPlayer("You have been kicked for a reason!");
            player.sendMessage("§3You have kicked "+target.getName());
        }
        return false;
    }
}
