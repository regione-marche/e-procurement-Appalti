/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eldasoft.sil.pg.bl.excel;

import java.util.Date;
import org.apache.poi.ss.usermodel.Cell;

/**
 *
 * @author marco.perazzetta
 */
public class ExcelCell extends AbstractCell {

	private Cell cell;

	public ExcelCell(Cell cell) {
		this.cell = cell;
	}

	public ExcelCell() {
	}

	public Cell getCell() {
		return cell;
	}

	public void setCell(Cell cell) {
		this.cell = cell;
	}

	@Override
	protected String getStringValue() {
		return this.cell.getStringCellValue();
	}

	@Override
	protected Number getNumberValue() {
		return this.cell.getNumericCellValue();
	}

	@Override
	protected Date getDateValue() {
		return this.cell.getDateCellValue();
	}

	@Override
	protected int getCellType() {

		int type;
		switch (this.cell.getCellType()) {
			case Cell.CELL_TYPE_NUMERIC:
				type = AbstractCell.NUMERO;
				break;
			case Cell.CELL_TYPE_STRING:
				type = AbstractCell.STRINGA;
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				type = AbstractCell.STRINGA;
				break;
			case Cell.CELL_TYPE_BLANK:
				type = AbstractCell.STRINGA;
				break;
			case Cell.CELL_TYPE_FORMULA:
				type = AbstractCell.NUMERO;
				break;
			default:
				type = AbstractCell.STRINGA;
				break;
		}
		return type;
	}
}
