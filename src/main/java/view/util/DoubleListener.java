package view.util;

import java.util.function.Consumer;
import java.util.regex.Pattern;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * A ChangeListener for Doubles.
 *
 * @author Myles Haynes
 * @author Peter Bae
 * @author Michael Osborne
 */
public class DoubleListener implements ChangeListener<String> {

    /**
     * The regular expression to use for validation.
     */
    private static final Pattern INTEGER_PATTERN = Pattern
            .compile("\\d*\\.?\\d*");

    /**
     * The consumer that consumes valid Double values.
     */
    private Consumer<Double> myConsumer;

    /**
     * Constructs a new DoubleListener with the given Consumer.
     *
     * @param theConsumer The Double consumer.
     */
    public DoubleListener(final Consumer<Double> theConsumer) {
        myConsumer = theConsumer;
    }

    /**
     * Checks to see if the new value is valid, and consumes it if it is.
     */
    @Override
    public void changed(final ObservableValue<? extends String> c,
            final String o, final String n) {
        if (n.length() > 0 && INTEGER_PATTERN.matcher(n).matches()) {
            myConsumer.accept(Double.parseDouble(n));
        }
    }
}
