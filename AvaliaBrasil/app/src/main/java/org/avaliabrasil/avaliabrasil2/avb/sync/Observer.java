package org.avaliabrasil.avaliabrasil2.avb.sync;

import android.database.Cursor;

/**
 * @author <a href="https://github.com/Klauswk/">Klaus Klein</a>
 *         <p/>
 *         Interface to couple activies and inform, using the <a href="http://www.tutorialspoint.com/design_pattern/observer_pattern.htm">Observer Design Pattern</a>
 *         that a data has change.
 * @version 1.0
 * @see <a href="http://www.tutorialspoint.com/design_pattern/observer_pattern.htm">Observer Design Pattern</a>
 * @since 1.0
 */
public interface Observer {

    public void update(Cursor cursor);
}
