/*
---------------------------------------------------------------------------------
File Name : CommandManager

Developer : vakea 
Email     : vakea@fluffici.eu
Real Name : Alex Guy Yann Le Roy

Date Created  : 29.12.2024
Last Modified : 29.12.2024

---------------------------------------------------------------------------------
*/

package net.furryplayplace.sanction.commands;

import net.furryplayplace.cottonframework.api.CottonAPI;
import net.furryplayplace.sanction.Sanction;
import net.furryplayplace.sanction.commands.ban.BanCommand;
import net.furryplayplace.sanction.commands.ban.UnbanCommand;
import net.furryplayplace.sanction.commands.kick.KickCommand;
import net.furryplayplace.sanction.commands.mute.MuteCommand;
import net.furryplayplace.sanction.commands.mute.UnmuteCommand;
import net.furryplayplace.sanction.commands.warn.WarnCommand;

public class CommandManager {
    public void register(Sanction plugin) {
        CottonAPI.get().pluginManager().registerCommand(plugin, new BanCommand(plugin));
        CottonAPI.get().pluginManager().registerCommand(plugin, new UnbanCommand(plugin));

        CottonAPI.get().pluginManager().registerCommand(plugin, new MuteCommand(plugin));
        CottonAPI.get().pluginManager().registerCommand(plugin, new UnmuteCommand(plugin));

        CottonAPI.get().pluginManager().registerCommand(plugin, new KickCommand(plugin));
        CottonAPI.get().pluginManager().registerCommand(plugin, new WarnCommand(plugin));
        CottonAPI.get().pluginManager().registerCommand(plugin, new HistoryCommand(plugin));
    }
}