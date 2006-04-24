package net.sourceforge.jwebunit.fit;

/**
 * Class to represent a data row of the personal info (by columns) table.
 *
 * @author Jim Weaver
 */
public class PersonalInfoRow extends TableRow {

    public String name;
    public String citizenship;
    public String state;
    public String sex;

    public PersonalInfoRow(String[] rowCells) {
        super(rowCells);
        this.name = rowCells[0];
        this.citizenship = rowCells[1];
        this.state = rowCells[2];
        this.sex = rowCells[3];
    }
}
