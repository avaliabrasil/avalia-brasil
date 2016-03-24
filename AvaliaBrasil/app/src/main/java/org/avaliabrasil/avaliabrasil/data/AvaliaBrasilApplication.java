package org.avaliabrasil.avaliabrasil.data;

import android.app.Application;

import org.avaliabrasil.avaliabrasil.rest.javabeans.User;

/**
 * @author <a href="https://github.com/Klauswk/">Klaus Klein</a>
 * @version 1.0
 * @since 1.0
 */
public class AvaliaBrasilApplication extends Application {

    private User user;

    @Override
    public void onCreate() {
        super.onCreate();
        user = new User();
    }


    public User getUser() {
        return user;
    }
}
