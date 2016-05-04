package hostmanager.enums;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public enum FileSystem {
    Win {
        @Override
        public Observable<File> getHostFile() {
            return Observable.just(File.listRoots())
                    .subscribeOn(Schedulers.io())
                    .flatMap(new Func1<File[], Observable<File>>() {
                        @Override
                        public Observable<File> call(final File[] files) {
                            return Observable.create(new Observable.OnSubscribe<File>() {
                                @Override
                                public void call(Subscriber<? super File> subscriber) {
                                    for (File file : files) {
                                        subscriber.onNext(file);
                                    }
                                    subscriber.onCompleted();
                                }
                            });
                        }
                    })
                    .filter(new Func1<File, Boolean>() {
                        @Override
                        public Boolean call(File file) {
                            File check = new File(file + "\\Windows");
                            return check.exists();
                        }
                    })
                    .first()
                    .flatMap(new Func1<File, Observable<File>>() {
                        @Override
                        public Observable<File> call(final File file) {
                            return Observable.create(new Observable.OnSubscribe<File>() {
                                @Override
                                public void call(Subscriber<? super File> subscriber) {
                                    File host = new File(file + WIN_HOST);
                                    subscriber.onNext(host);
                                    subscriber.onCompleted();
                                }
                            });
                        }
                    });
        }

        @Override
        public Observable<String> getHost() {
            return getHostFile().flatMap(transform());
        }

        @Override
        void initPassword() {
            //do nothing in windows system;
        }

        @Override
        boolean executeCommand(String... cmd) {
            //do nothing in windows system;
            return true;
        }

    },
    Mac {
        @Override
        public Observable<File> getHostFile() {
            return Observable.just(new File(MAC_HOST))
                    .subscribeOn(Schedulers.io());
        }

        @Override
        public Observable<String> getHost() {
            return getHostFile().flatMap(transform());
        }

        @Override
        void initPassword() {
            if (StringUtils.isEmpty(pwd)) {
                JPasswordField passwordField = new JPasswordField();
                JOptionPane.showMessageDialog(null, passwordField, "请输入密码", JOptionPane.PLAIN_MESSAGE);
                pwd = String.valueOf(passwordField.getPassword());
            }
        }

        @Override
        boolean executeCommand(String... cmd) {
            try {
                Process p = Runtime.getRuntime().exec(cmd);
                p.waitFor();

                return p.exitValue() == 0;
            } catch (IOException | InterruptedException ignore) {
            }
            return false;
        }

    };

    private static Func1<File, Observable<String>> transform() {
        return new Func1<File, Observable<String>>() {
            @Override
            public Observable<String> call(final File file) {
                return Observable.create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        try {
                            subscriber.onNext(FileUtils.readFileToString(file, Charsets.toCharset("UTF-8")));
                        } catch (IOException e) {
                            subscriber.onError(e);
                        }
                        subscriber.onCompleted();
                    }
                });
            }
        };
    }

    private final static String WIN_HOST = "\\Windows\\System32\\drivers\\etc\\hosts";
    private final static String MAC_HOST = "/etc/hosts";

    public abstract Observable<File> getHostFile();

    public abstract Observable<String> getHost();

    protected String pwd;

    public void changeHost(final File file, final String content) throws IOException {
        initPassword();
        if (executeCommand("/bin/bash", "-c", "echo \"" + pwd + "\"| sudo -S chmod 777 " + MAC_HOST)) {
            FileUtils.writeStringToFile(file, content, Charsets.toCharset("UTF-8"));
        } else {
            JOptionPane.showMessageDialog(null, "您输入的密码错误,请重新尝试", "密码提示", JOptionPane.ERROR_MESSAGE);
            pwd = "";
        }
    }

    abstract void initPassword();

    abstract boolean executeCommand(String... cmd);

    public static FileSystem os() {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            return Win;
        }
        return Mac;
    }
}
