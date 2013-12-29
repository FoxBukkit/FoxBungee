package de.doridian.yiffbungee.database;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.StackKeyedObjectPoolFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnectionPool {
	private static PoolingDataSource dataSource;

	static void initMe() {
		instance = new DatabaseConnectionPool();
	}

    public static DatabaseConnectionPool instance;

    private DatabaseConnectionPool() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch(Exception e) {
			System.err.println("Error loading JBBC MySQL: " + e.toString());
		}

		GenericObjectPool connectionPool = new GenericObjectPool(null);
		connectionPool.setMaxActive(10);
		connectionPool.setMaxIdle(5);
		connectionPool.setTestOnBorrow(true);
		connectionPool.setTestOnReturn(true);
		connectionPool.setTestWhileIdle(true);

		ConnectionFactory connectionFactory = new DriverManagerConnectionFactory("jdbc:mysql://" + DatabaseConfiguration.HOST + "/" + DatabaseConfiguration.NAME, DatabaseConfiguration.USER, DatabaseConfiguration.PASSWORD);
		PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(
				connectionFactory,
				connectionPool,
				new StackKeyedObjectPoolFactory(),
				"SELECT 1",
				false,
				true
		);
		poolableConnectionFactory.setValidationQueryTimeout(3);

		dataSource = new PoolingDataSource(connectionPool);
    }

    public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
    }
}
