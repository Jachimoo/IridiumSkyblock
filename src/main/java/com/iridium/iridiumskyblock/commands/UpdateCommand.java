package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class UpdateCommand extends Command {

    public UpdateCommand() {
        super(Collections.singletonList("update"), "Updates an islands levels", "update", false);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 2 && args.length != 3) {
            sender.sendMessage(Utils.color(IridiumSkyblock.getConfiguration().prefix) + "/is update <player/all> <blocksPerTick>");
            return;
        }
        int blocksPerTick = 1000;
        if (args.length == 3) {
            if (StringUtils.isNumeric(args[2])) {
                blocksPerTick = Integer.parseInt(args[2]);
            } else {
                //TODO: Make this message configurable
                sender.sendMessage(args[2] + " is not a number");
            }
        }

        if (args[1].equalsIgnoreCase("all")) {
            boolean out = true;
            for (Island island : IridiumSkyblock.getIslandManager().islands.values()) {
                island.valuableBlocks.clear();
                if (!island.updating) {
                    island.forceInitBlocks(out ? sender : null, blocksPerTick, "Everyone");
                    out = false;
                } else {
                    //change blockspertick to blocks temp
                    IridiumSkyblock.blockspertick = IridiumSkyblock.getConfiguration().blocksPerTick;
                    IridiumSkyblock.getConfiguration().blocksPerTick = blocksPerTick;
                }
            }
            return;
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);

        User u = User.getUser(player);
        if (u.getIsland() != null) {
            if (u.getIsland().updating) {
                sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().alreadyRecalculating.replace("%player%", User.getUser(u.getIsland().getOwner()).name).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                return;
            }
            u.getIsland().forceInitBlocks(sender, blocksPerTick, User.getUser(u.getIsland().getOwner()).name);
        } else {
            sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().noIsland.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
        }
    }

    @Override
    public void admin(CommandSender sender, String[] args, Island island) {
        execute(sender, args);
    }

    @Override
    public List<String> TabComplete(CommandSender cs, org.bukkit.command.Command cmd, String s, String[] args) {
        return null;
    }
}
