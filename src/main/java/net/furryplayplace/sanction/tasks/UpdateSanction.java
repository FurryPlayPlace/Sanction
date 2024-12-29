/*
---------------------------------------------------------------------------------
File Name : UpdateSanction

Developer : vakea 
Email     : vakea@fluffici.eu
Real Name : Alex Guy Yann Le Roy

Date Created  : 29.12.2024
Last Modified : 29.12.2024

---------------------------------------------------------------------------------
*/

package net.furryplayplace.sanction.tasks;

import net.furryplayplace.sanction.Sanction;

public class UpdateSanction implements Runnable {

    private final Sanction plugin;

    public UpdateSanction(Sanction plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        this.plugin.moderationManager().queryPerformer("UPDATE `sanctions` SET is_deleted = 0, update_date = CURRENT_TIMESTAMP() WHERE is_deleted = 1 AND (is_deleted != 0 AND expiration_date < NOW())");
    }
}