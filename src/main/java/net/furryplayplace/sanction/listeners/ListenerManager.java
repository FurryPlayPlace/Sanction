/*
---------------------------------------------------------------------------------
File Name : ListenerManager

Developer : vakea 
Email     : vakea@fluffici.eu
Real Name : Alex Guy Yann Le Roy

Date Created  : 29.12.2024
Last Modified : 29.12.2024

---------------------------------------------------------------------------------
*/

package net.furryplayplace.sanction.listeners;

import net.furryplayplace.cottonframework.api.CottonAPI;
import net.furryplayplace.sanction.Sanction;
import net.furryplayplace.sanction.listeners.player.PlayerListener;

public class ListenerManager {
    public void register(Sanction plugin) {
        CottonAPI.get().pluginManager().getEventBus().register(new PlayerListener(plugin));
    }
}