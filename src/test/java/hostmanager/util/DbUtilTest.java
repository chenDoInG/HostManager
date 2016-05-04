package hostmanager.util;

import hostmanager.model.Host;
import org.junit.Test;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;


public class DbUtilTest {

    @Test
    public void save() throws SQLException {
        DbUtil util = new DbUtil();
        util.execute("insert into host(name,content,style) values (\"公共\",\"测试\",\"common\")");
    }

    @Test
    public void get() throws SQLException {
        DbUtil util = new DbUtil();
        util.execute(Host.class, "select * from host")
                .subscribeOn(Schedulers.io())
                .defaultIfEmpty(new Host("测试"))
                .subscribe(new Action1<Host>() {
                    @Override
                    public void call(Host host) {
                        System.out.println(host.getName());
                        assertThat(host.getName()).isEqualTo("123");
                    }
                });
    }

    @Test
    public void retry() {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("subscribing");
                throw new RuntimeException("always fails");
            }
        }).retryWhen(new Func1<Observable<? extends Throwable>, Observable<?>>() {
            @Override
            public Observable<?> call(Observable<? extends Throwable> observable) {
                return observable.zipWith(Observable.range(1, 3), new Func2<Throwable, Integer, Integer>() {
                    @Override
                    public Integer call(Throwable throwable, Integer integer) {
                        return integer;
                    }
                }).flatMap(new Func1<Integer, Observable<?>>() {
                    @Override
                    public Observable<?> call(Integer integer) {
                        System.out.println("delay retry by " + integer + " second(s)");
                        return Observable.timer(integer, TimeUnit.SECONDS);
                    }
                });
            }
        }).subscribeOn(Schedulers.io())
                .toBlocking().forEach(new Action1<String>() {
            @Override
            public void call(String s) {
                System.out.println(s);
            }
        });
    }

}