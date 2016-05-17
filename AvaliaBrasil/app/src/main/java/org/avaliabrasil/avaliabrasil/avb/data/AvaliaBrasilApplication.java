package org.avaliabrasil.avaliabrasil.avb.data;

import android.app.Application;
import android.location.Location;

import org.avaliabrasil.avaliabrasil.avb.javabeans.User;

/**
 * @author <a href="https://github.com/Klauswk/">Klaus Klein</a>
 * @version 1.0
 * @since 1.0
 */
public class AvaliaBrasilApplication extends Application {

    private User user;

    private Location location;

    @Override
    public void onCreate() {
        super.onCreate();
        user = new User();
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public User getUser() {
        return user;
    }
}
