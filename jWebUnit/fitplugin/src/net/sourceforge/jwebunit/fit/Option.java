package net.sourceforge.jwebunit.fit;

/**
 * Represents an html option element - for use in fit row/column fixtures.
 *
 * @author Jim Weaver
 */
public class Option {

    public String label;
    public String value;

    public Option(String label, String value) {
        this.label = label;
        this.value = value;
    }

    /**
     * Factory method.
     *
     * @param labels
     * @param values
     * @return Option array.
     */
    public static Option[] buildOptions(String[] labels, String[] values) {
        Option[] options = new Option[labels.length];
        for (int i = 0; i < options.length; i++) {
           options[i] = new Option(labels[i], values[i]);
        }
        return options;
    }

}
