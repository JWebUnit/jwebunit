/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.html;

import java.util.ArrayList;

import net.sourceforge.jwebunit.exception.AssertEqualsException;
import net.sourceforge.jwebunit.exception.AssertMatchException;


/**
 * Represents a row of an html table.
 *
 * @author Jim Weaver
 * @author Julien Henry
 */
public class Row {

    private ArrayList<Cell> cells = new ArrayList<Cell>();;
    
    public Row() {
    }

    /**
     * Construct a row from an array of objects which specify the
     * cells of the row.  If an object in the array is an
     * {@link net.sourceforge.jwebunit.html.Cell}, it is directly added to
     * the cells of the row, otherwise an {@link net.sourceforge.jwebunit.html.Cell}
     * is created from the toString() value of the object and an assumed colspan of 1.
     *
     * @param rowCells objects representing the row's cells.
     */
    public Row(Object[] rowCells) {
        appendCells(rowCells);
    }
    
    public void appendCells(Object[] rowCells) {
        for (int i = 0; i < rowCells.length; i++) {
            Object column = rowCells[i];
            if (column instanceof Cell) {
                this.cells.add((Cell)column);
            } else {
                this.cells.add(new Cell(column.toString()));
            }
        }
    }

    public void appendCell(Cell cell) {
        cells.add(cell);
    }

    public void appendCell(String cellText) {
        cells.add(new Cell(cellText));
    }

    public ArrayList<Cell> getCells() {
        return cells;
    }
    
    public int getCellCount() {
        return cells.size();
    }
    
    public boolean hasText(String text) {
        for (int i = 0; i < getCellCount(); i++) {
            Cell c = (Cell) getCells().get(i);
            if (c.equals(text))
                return true;
        }
        return false;
    }

    public boolean hasMatch(String regexp) {
        for (int i = 0; i < getCellCount(); i++) {
            Cell c = (Cell) getCells().get(i);
            if (c.match(regexp))
                return true;
        }
        return false;
    }


    public void assertEquals(Row r) throws AssertEqualsException {
        if (this.getCells().size()!=r.getCells().size()) {
           throw new AssertEqualsException(r.toString(), this.toString()); 
        }
        for (int i=0; i<this.getCells().size(); i++) {
            ((Cell) this.getCells().get(i)).assertEquals((Cell)r.getCells().get(i));
        }
    }

    public void assertMatch(Row r) throws AssertMatchException {
        if (this.getCells().size()!=r.getCells().size()) {
            throw new AssertMatchException(r.toString(), this.toString()); 
         }
        for (int i=0; i<this.getCells().size(); i++) {
            ((Cell) this.getCells().get(i)).assertMatch((Cell)r.getCells().get(i));
        }
    }
}
