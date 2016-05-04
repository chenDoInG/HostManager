package hostmanager.util;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

import java.lang.reflect.Field;
import java.sql.*;

public class DbUtil {

    private Connection connection;
    private Statement statement;

    private static final String CREATE_TABLE_HOST_IF_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS host(name,content,type,PRIMARY KEY (name))";

    public DbUtil() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:hostmanager.db");
            statement = connection.createStatement();
            statement.execute(CREATE_TABLE_HOST_IF_NOT_EXISTS);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void execute(String sql) throws SQLException {
        statement.execute(sql);
    }

    public <T> Observable<T> execute(final Class<T> type, final String sql) throws SQLException {
        final ResultSet resultSet = statement.executeQuery(sql);

        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    while (resultSet.next()) {
                        try {
                            T clz = type.newInstance();
                            for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                                try {
                                    Field field = type.getDeclaredField(resultSet.getMetaData().getColumnName(i));
                                    field.setAccessible(true);
                                    field.set(clz, resultSet.getString(i));
                                } catch (NoSuchFieldException ignore) {
                                }
                            }
                            subscriber.onNext(clz);
                        } catch (InstantiationException | IllegalAccessException e) {
                            subscriber.onError(e);
                        }
                    }
                } catch (SQLException e) {
                    subscriber.onError(e);
                }
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
    }

    public void close() {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException ignore) {
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ignore) {
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        close();
    }
}
