package repository;

import java.sql.ResultSet;

public interface RepositoryManager {
        void connect();
        void disconnect();
        void setAutoCommit(boolean autoCommit);

        void createPrepareStatement(String sql);
        RepositoryManager prepStatementSetInt(int paramIndex, int x);
        void prepStatementAddBatch();
        int[] prepStatementExecuteBatch();
        void commit();

        boolean execute(String sql);
        ResultSet executeQuery(String sql);
}
