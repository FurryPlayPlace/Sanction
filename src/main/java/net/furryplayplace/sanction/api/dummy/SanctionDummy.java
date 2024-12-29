/*
---------------------------------------------------------------------------------
File Name : BanDummy

Developer : vakea 
Email     : vakea@fluffici.eu
Real Name : Alex Guy Yann Le Roy

Date Created  : 29.12.2024
Last Modified : 29.12.2024

---------------------------------------------------------------------------------
*/

package net.furryplayplace.sanction.api.dummy;

import java.sql.Timestamp;

public class SanctionDummy {

    private final String reason;
    private final String author;
    private final Timestamp expiration;

    public SanctionDummy(String reason, String author, Timestamp expiration) {
        this.reason = reason;
        this.author = author;
        this.expiration = expiration;
    }

    public String reason() {
        return reason;
    }

    public Timestamp expiration() {
        return expiration;
    }

    public String author() {
        return author;
    }
}