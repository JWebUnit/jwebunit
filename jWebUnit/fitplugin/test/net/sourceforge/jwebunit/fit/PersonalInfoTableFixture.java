package net.sourceforge.jwebunit.fit;

/**
 * Subclass TableFixture to allow testing of the PersonalInfoTable - provide
 * table summary value and class of test object to be built for each data row
 * in the table.
 *
 * @author Jim Weaver
 */
public class PersonalInfoTableFixture extends TableFixture {

    protected String getSourceTableSummaryOrId() {
        return "infoTableColHeaders";
    }

    public Class getTargetClass() {
        return PersonalInfoRow.class;
    }

}
