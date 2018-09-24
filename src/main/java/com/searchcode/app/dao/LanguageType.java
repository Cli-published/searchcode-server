package com.searchcode.app.dao;

import com.searchcode.app.config.IDatabaseConfig;
import com.searchcode.app.config.MySQLDatabaseConfig;
import com.searchcode.app.dto.LanguageTypeDTO;
import com.searchcode.app.service.Singleton;
import com.searchcode.app.util.Helpers;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LanguageType {
    private final Helpers helpers;
    private final IDatabaseConfig dbConfig;

    public LanguageType() {
        this(new MySQLDatabaseConfig(), Singleton.getHelpers());
    }

    public LanguageType(IDatabaseConfig dbConfig, Helpers helpers) {
        this.dbConfig = dbConfig;
        this.helpers = helpers;
    }

    public synchronized List<LanguageTypeDTO> getLanguageNamesByIds(List<String> ids) {
        List<LanguageTypeDTO> languageTypeList = new ArrayList<>();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = this.dbConfig.getConnection();
            stmt = conn.prepareStatement(String.format("SELECT id, type FROM languagetype WHERE id IN (%s);", String.join(",", ids)));

            rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String type = rs.getString("type");
                languageTypeList.add(new LanguageTypeDTO(id, type));
            }
        }
        catch (SQLException ex) {
            Singleton.getLogger().severe(" caught a " + ex.getClass() + "\n with message: " + ex.getMessage());
        }
        finally {
            this.helpers.closeQuietly(rs);
            this.helpers.closeQuietly(stmt);
            this.helpers.closeQuietly(conn);
        }

        return languageTypeList;
    }

    public synchronized Optional<LanguageTypeDTO> getByType(String type) {
        Optional<LanguageTypeDTO> languageTypeDTO = Optional.empty();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = this.dbConfig.getConnection();
            stmt = conn.prepareStatement("SELECT id, type FROM languagetype WHERE type = ? LIMIT 1;");
            stmt.setString(1, type);

            rs = stmt.executeQuery();


            while (rs.next()) {
                int id = rs.getInt("id");
                type = rs.getString("type");
                languageTypeDTO = Optional.of(new LanguageTypeDTO(id, type));
            }
        }
        catch (SQLException ex) {
            Singleton.getLogger().severe(" caught a " + ex.getClass() + "\n with message: " + ex.getMessage());
        }
        finally {
            this.helpers.closeQuietly(rs);
            this.helpers.closeQuietly(stmt);
            this.helpers.closeQuietly(conn);
        }

        return languageTypeDTO;
    }

    public synchronized Optional<LanguageTypeDTO> createLanguageType(String type) {

        Optional<LanguageTypeDTO> byType = this.getByType(type);
        if (byType.isPresent()) {
            return byType;
        }

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = this.dbConfig.getConnection();
            stmt = conn.prepareStatement("INSERT INTO `languagetype` (`type`) VALUES (?);", Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, type);
            stmt.execute();

            ResultSet tableKeys = stmt.getGeneratedKeys();
            tableKeys.next();
            int autoGeneratedID = tableKeys.getInt(1);
            byType = Optional.of(new LanguageTypeDTO(autoGeneratedID, type));
        }
        catch (SQLException ex) {
            Singleton.getLogger().severe(" caught a " + ex.getClass() + "\n with message: " + ex.getMessage());
        }
        finally {
            this.helpers.closeQuietly(stmt);
            this.helpers.closeQuietly(conn);
        }

        return byType;
    }
}
