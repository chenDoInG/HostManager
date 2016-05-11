package hostmanager.ui.button;

import rx.Observable;
import rx.Subscriber;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.TimeUnit;

public class RxButton {

    public static Observable<Void> click(JButton button) {
        return Observable.create(new ButtonClickOnSubscribe(button))
                .throttleFirst(2, TimeUnit.SECONDS);
    }

    private static final class ButtonClickOnSubscribe implements Observable.OnSubscribe<Void> {

        final JButton button;

        private ButtonClickOnSubscribe(JButton button) {
            this.button = button;
        }

        @Override
        public void call(final Subscriber<? super Void> subscriber) {
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    subscriber.onNext(null);
                }
            });
        }
    }
}
