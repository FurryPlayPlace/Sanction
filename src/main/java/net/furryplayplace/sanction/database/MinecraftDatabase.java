package net.furryplayplace.sanction.database;

import net.furryplayplace.sanction.Sanction;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;

public class MinecraftDatabase {
    private final Sanction plugin;
    private DataSource dataSource = null;

    public MinecraftDatabase(Sanction plugin) {
        this.plugin = plugin;
        this.initDataSource();
    }

    private void initDataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl(this.plugin.getConfig().getString("mysql.url"));
        dataSource.setUsername(this.plugin.getConfig().getString("mysql.username"));
        dataSource.setPassword(this.plugin.getConfig().getString("mysql.password"));
        dataSource.setInitialSize(1);
        dataSource.setMaxTotal(10);

        this.dataSource = dataSource;
    }

    public DataSource dataSource() {
        return dataSource;
    }
}
