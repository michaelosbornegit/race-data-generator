package view.util;

import java.util.function.Consumer;
import java.util.regex.Pattern;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * A ChangeListener for Integers that only consumes integers if they are less
 * than a given max.
 *
 * @author Peter Bae
 * @author Michael Osborne
 */
public class IntMaxListener implements ChangeListener<String> {

    /**
     * The regular expression to use for validation.
     */
    private static final Pattern INTEGER_PATTERN = Pattern.compile("\\d*");

    /**
     * The consumer that consumes valid Integer values.
     */
    private Consumer<Integer> myConsumer;

    /**
     * The max value.
     */
    private int myMax;

    /**
     * Constructs a new {@link IntMaxListener}.
     *
     * @param theConsumer The consumer to consume valid integer values.
     * @param theMax      The max integer value that can be accepted.
     */
    public IntMaxListener(final Consumer<Integer> theConsumer,
            final int theMax) {
        myConsumer = theConsumer;
        myMax = theMax;
    }

    /**
     * Checks to see if the new value is valid, and consumes it if it is.
     */
    @Override
    public void changed(final ObservableValue<? extends String> c,
            final String o, final String n) {
        if (n.length() > 0 && INTEGER_PATTERN.matcher(n).matches()
                && Integer.parseInt(n) <= myMax && Integer.parseInt(n) >= 1) {
            myConsumer.accept(Integer.parseInt(n));
        }
    }
}
