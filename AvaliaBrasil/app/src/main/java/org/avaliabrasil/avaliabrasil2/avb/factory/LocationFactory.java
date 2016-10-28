package org.avaliabrasil.avaliabrasil2.avb.factory;

import android.support.annotation.NonNull;

import org.avaliabrasil.avaliabrasil2.avb.javabeans.ranking.Location;

/**
 * @author <a href="https://github.com/Klauswk">Klaus Klein</a>
 * @version 1.0
 * @since 1.0
 */

public interface LocationFactory<T> {

    public Location getLocationByType(T base);
    public org.avaliabrasil.avaliabrasil2.avb.javabeans.ranking.Location getRegionByState(@NonNull String state);

}
