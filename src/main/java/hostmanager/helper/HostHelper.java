package hostmanager.helper;

import hostmanager.component.DaggerHostHelperComponent;
import hostmanager.model.Host;
import hostmanager.util.DbUtil;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

import javax.inject.Inject;
import java.sql.SQLException;

public class HostHelper {

    @Inject
    DbUtil db;

    public HostHelper() {
        DaggerHostHelperComponent.builder().build().inject(this);
    }

    public Observable<Host> getHost(Host host) throws SQLException {
        return db.execute(Host.class, new Condition().select().where().name(host.getName()).build());
    }

    public void deleteHost(Host host) throws SQLException {
        getHost(host)
                .subscribe(new Action1<Host>() {
                    @Override
                    public void call(Host host) {
                        try {
                            db.execute(new Condition().delete().where().name(host.getName()).build());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    public Observable<Host> getCommonHost() throws SQLException {
        return db.execute(Host.class, new Condition().select().where().common().build());
    }

    public Observable<Host> getLocalHosts() throws SQLException {
        return db.execute(Host.class, new Condition().select().where().local().build());
    }

    public void saveLocalHost(final Host host) throws SQLException {
        db.execute(Host.class, new Condition().select().where().name(host.getName()).and().local().build())
                .switchIfEmpty(Observable.create(new Observable.OnSubscribe<Host>() {
                    @Override
                    public void call(Subscriber<? super Host> subscriber) {
                        try {
                            db.execute(new Condition().insertLocal(host).build());
                            subscriber.onNext(host);
                        } catch (SQLException e) {
                            subscriber.onError(e);
                        }
                        subscriber.onCompleted();
                    }
                }))
                .subscribe(new Action1<Host>() {
                    @Override
                    public void call(Host host1) {
                        try {
                            db.execute(new Condition()
                                    .update().set()
                                    .content(StringUtils.isEmpty(host.getContent()) ? "" : host.getContent())
                                    .where()
                                    .name(host1.getName()).build());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    public void saveCommonHost(Host host) throws SQLException {
        if (host.getLastUpdateTime() == null || DateTime.now().getMillis() - host.getLastUpdateTime().getMillis() > 100) {
            String sql = new Condition().update().set().name(host.getName()).separator().content(host.getContent()).where().common().build();
            db.execute(sql);
            host.setLastUpdateTime(DateTime.now());
        }
    }

    public void initCommonHost(Host host) throws SQLException {
        db.execute(new Condition().insertCommon(host).build());
    }

    public static class Condition {
        private static final StringBuilder DELETE = new StringBuilder("DELETE FROM host ");
        private static final StringBuilder SELECT = new StringBuilder("SELECT * FROM host ");
        private static final StringBuilder UPDATE = new StringBuilder("UPDATE host ");
        private static final StringBuilder INSERT = new StringBuilder("INSERT INTO host ");
        private static final StringBuilder LOCAL = new StringBuilder(" type = \"local\" ");
        private static final StringBuilder ONLINE = new StringBuilder(" type = \"online\" ");
        private static final StringBuilder COMMON = new StringBuilder(" type = \"common\" ");
        private static final StringBuilder NAME = new StringBuilder(" name = ");
        private static final StringBuilder WHERE = new StringBuilder(" WHERE ");
        private static final StringBuilder SET = new StringBuilder(" SET ");
        private static final StringBuilder CONTENT = new StringBuilder(" content = ");
        private static final StringBuilder SEPERATOR = new StringBuilder(" , ");

        private static StringBuilder sql = new StringBuilder();

        public Condition delete() {
            sql.append(DELETE);
            return this;
        }

        public Condition separator() {
            sql.append(SEPERATOR);
            return this;
        }

        public Condition insertLocal(Host host) {
            sql.append(INSERT)
                    .append("(name,content,type) VALUES")
                    .append("(\"").append(host.getName()).append("\",\"")
                    .append(host.getContent() == null ? "" : host.getContent())
                    .append("\",\"local\")");
            return this;
        }

        public Condition insertCommon(Host host) {
            sql.append(INSERT)
                    .append("(name,content,type) VALUES")
                    .append("(\"").append(host.getName()).append("\",\"")
                    .append(host.getContent() == null ? "" : host.getContent())
                    .append("\",\"common\")");
            return this;
        }

        public Condition and() {
            sql.append(" AND ");
            return this;
        }

        public Condition content(String using) {
            sql.append(CONTENT).append("\"").append(using).append("\"");
            return this;
        }

        public Condition set() {
            sql.append(SET);
            return this;
        }

        public Condition select() {
            sql.append(SELECT);
            return this;
        }

        public Condition where() {
            sql.append(WHERE);
            return this;
        }

        public Condition update() {
            sql.append(UPDATE);
            return this;
        }

        public Condition name(String using) {
            sql.append(NAME).append("\"").append(using).append("\"");
            return this;
        }

        public Condition common() {
            sql.append(COMMON);
            return this;
        }

        public Condition online() {
            sql.append(ONLINE);
            return this;
        }

        public Condition local() {
            sql.append(LOCAL);
            return this;
        }

        public String build() {
            String result = sql.toString();
            System.out.println(result);
            clean();
            return result;
        }

        private void clean() {
            sql = new StringBuilder();
        }
    }

}
