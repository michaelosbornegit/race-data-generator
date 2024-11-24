package view.util;

import java.util.function.Consumer;
import java.util.regex.Pattern;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * A ChangeListener for Integers.
 *
 * @author Myles Haynes
 * @author Peter Bae
 * @author Michael Osborne
 */
public class IntListener implements ChangeListener<String> {

    /**
     * The regular expression to use for validation.
     */
    private static final Pattern INTEGER_PATTERN = Pattern.compile("\\d*");

    /**
     * The Integer Consumer.
     */
    private Consumer<Integer> myConsumer;

    /**
     * Constructs an {@link IntListener} with the given consumer.
     *
     * @param theConsumer The Integer Consumer.
     */
    public IntListener(final Consumer<Integer> theConsumer) {
        myConsumer = theConsumer;
    }

    /**
     * Validates the changes value, and calls the consumer if it is valid.
     */
    @Override
    public void changed(final ObservableValue<? extends String> c,
            final String o, final String n) {
        if (n.length() > 0 && INTEGER_PATTERN.matcher(n).matches()) {
            myConsumer.accept(Integer.parseInt(n));
        }
    }
}
