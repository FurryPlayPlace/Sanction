/*
---------------------------------------------------------------------------------
File Name : PerformAction

Developer : vakea 
Email     : vakea@fluffici.eu
Real Name : Alex Guy Yann Le Roy

Date Created  : 29.12.2024
Last Modified : 29.12.2024

---------------------------------------------------------------------------------
*/

package net.furryplayplace.sanction.api;

import java.sql.SQLException;

/**
 * Functional interface representing a database action to be performed.
 *
 * <p>Implementations of this interface are expected to define logic that interacts with a
 * {@link java.sql.Connection}. This is used in conjunction with {@code actionPerformer} to abstract
 * and encapsulate database operations.</p>
 *
 * @param <T> The type of resource passed to the {@code run} method (e.g., {@link java.sql.Connection}).
 */
@FunctionalInterface
public interface PerformAction<T> {
    void run(T action) throws SQLException;
}
