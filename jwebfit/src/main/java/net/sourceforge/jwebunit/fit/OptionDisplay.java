package net.sourceforge.jwebunit.fit;

import fit.RowFixture;
import fit.Parse;

/**
 * fit RowFixture to validate html options for a select box.
 *
 * @author Jim Weaver
 */
public class OptionDisplay extends RowFixture {

    private String selectName;

    public void doRows(Parse rows) {
        selectName = rows.parts.text();
        super.doRows(rows.more);
    }

    public Class getTargetClass() {
        return Option.class;
    }

    public Object[] query() throws Exception  {
        String[] values = WebFixture.tester.getDialog().getSelectOptionValues(selectName);
        String[] labels = new String[values.length];
        for (int i = 0; i < values.length; i++) {
        	labels[i] = WebFixture.tester.getDialog().getSelectOptionLabelForValue(selectName, values[i]);
        }
        return Option.buildOptions(labels, values);
    }

}
