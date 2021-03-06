/*
 * Copyright (c) 2016 Boyter Online Services
 *
 * Use of this software is governed by the Fair Source License included
 * in the LICENSE.TXT file, but will be eventually open under GNU General Public License Version 3
 * see the README.md for when this clause will take effect
 *
 * Version 1.3.4
 */

package com.searchcode.app.config;

import com.searchcode.app.service.Singleton;
import com.searchcode.app.util.Helpers;
import com.searchcode.app.util.LoggerWrapper;
import com.searchcode.app.util.Properties;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Optional;

public class SphinxSearchConfig {

    private final Helpers helpers;
    private final HashMap<String, Connection> connectionList = new HashMap<>();
    private final LoggerWrapper logger;

    public SphinxSearchConfig() {
        this.helpers = Singleton.getHelpers();
        this.logger = Singleton.getLogger();
    }

    public synchronized Optional<Connection> getConnection(String server) throws SQLException {
        Connection connection = null;

        try {
            connection = connectionList.getOrDefault(server, null);

            if (connection == null || connection.isClosed() || !connection.isValid(1)) {
                this.helpers.closeQuietly(connection);
                Class.forName("com.mysql.jdbc.Driver");
                String connectionString = (String) Properties.getProperties().getOrDefault("sphinx_connection_string", "jdbc:mysql://%s:9306?characterEncoding=utf8&maxAllowedPacket=10000000");

                connectionString = String.format(connectionString, server);
                connection = DriverManager.getConnection(connectionString, Values.EMPTYSTRING, Values.EMPTYSTRING);

                connectionList.put(server, connection);
            }
        } catch (ClassNotFoundException ex) {
            this.logger.severe(String.format("f9e4283d::error in class %s exception %s it appears searchcode is unable to connect sphinx using mysql connection as the driver is missing", ex.getClass(), ex.getMessage()));
        }

        return Optional.ofNullable(connection);
    }
}