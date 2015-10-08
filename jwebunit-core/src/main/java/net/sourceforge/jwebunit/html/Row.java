/**
 * Copyright (c) 2002-2015, JWebUnit team.
 *
 * This file is part of JWebUnit.
 *
 * JWebUnit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JWebUnit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JWebUnit.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.jwebunit.html;

import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a row of an html table.
 *
 * @author Jim Weaver
 * @author Julien Henry
 */
public class Row {

  private List<Cell> cells = new ArrayList<Cell>();

  public Row() {
  }

  /**
   * Construct a row from an array of objects which specify the cells of the row. If an object in the array is an
   * {@link net.sourceforge.jwebunit.html.Cell}, it is directly added to the cells of the row, otherwise an
   * {@link net.sourceforge.jwebunit.html.Cell} is created from the toString() value of the object and an assumed
   * colspan of 1.
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
        this.cells.add((Cell) column);
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

  public List<Cell> getCells() {
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

  public void assertEquals(Row r) {
    Assert.assertTrue("Cell count are not equal",
      this.getCells().size() == r.getCells().size());
    for (int i = 0; i < this.getCells().size(); i++) {
      ((Cell) this.getCells().get(i)).assertEquals((Cell) r.getCells()
        .get(i));
    }
  }

  public void assertMatch(Row r) {
    Assert.assertTrue("Cell count are not equal",
      this.getCells().size() == r.getCells().size());
    for (int i = 0; i < this.getCells().size(); i++) {
      ((Cell) this.getCells().get(i)).assertMatch((Cell) r.getCells()
        .get(i));
    }
  }
}
