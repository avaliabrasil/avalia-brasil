package org.avaliabrasil.avaliabrasil.sync;

/**
 * @author <a href="https://github.com/Klauswk/">Klaus Klein</a>
 *
 * Interface to couple activies and inform, using the <a href="http://www.tutorialspoint.com/design_pattern/observer_pattern.htm">Observer Design Pattern</a>
 * that a data has change.
 *
 * @see <a href="http://www.tutorialspoint.com/design_pattern/observer_pattern.htm">Observer Design Pattern</a>
 * @since 1.0
 * @version 1.0
 */
public interface Observer {

    public void update();
}
