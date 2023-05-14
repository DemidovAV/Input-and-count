package repository;



import java.sql.*;


public class SQLiteManager implements RepositoryManager {

    private Connection connection;
    private Statement statement;
    private PreparedStatement preparedStatement;

    String dbFile;

    public SQLiteManager(String dbFile) {
        this.dbFile = dbFile;
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Can't register JDBC");
        }
    }

    public Connection createConnection() {
        try {
            return DriverManager.getConnection("jdbc:sqlite:" + dbFile);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to connect to database");
        }
    }


    @Override
    public void connect() {
        if (connection != null) throw new RuntimeException("Connection already exists");
        try {
            connection = createConnection();
            statement = connection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void disconnect() {
        if (statement != null) {
            try {
                statement.close();
                statement = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
                preparedStatement = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void setAutoCommit(boolean autoCommit) {
        if (connection == null) throw new RuntimeException("No connection to DB");
        try {
            connection.setAutoCommit(autoCommit);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void createPrepareStatement(String sql) {
        if (preparedStatement != null) {
            try {
               preparedStatement.close();
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            preparedStatement = connection.prepareStatement(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public RepositoryManager prepStatementSetInt(int paramIndex, int x) {
        if (preparedStatement == null) throw new RuntimeException("No prepared statement created");
        try {
            preparedStatement.setInt(paramIndex, x);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Override
    public void prepStatementAddBatch() {
        if (preparedStatement == null) throw new RuntimeException("No prepared statement created");
        try {
            preparedStatement.addBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int[] prepStatementExecuteBatch() {
        if (preparedStatement == null) throw new RuntimeException("No prepared statement created");
        try {
            return preparedStatement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void commit() {
        if (connection == null) throw new RuntimeException("No connection to DB");
        try {
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean execute(String sql) {
        if (statement == null) throw new RuntimeException("No connection to DB");
        try {
            return statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResultSet executeQuery(String sql) {
        if (statement == null) throw new RuntimeException("No connection to DB");
        try {
            return statement.executeQuery(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
