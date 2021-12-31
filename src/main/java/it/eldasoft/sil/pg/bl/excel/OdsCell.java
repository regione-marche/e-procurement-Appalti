/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eldasoft.sil.pg.bl.excel;

import java.util.Date;
import org.odftoolkit.odfdom.doc.table.OdfTableCell;
import org.odftoolkit.odfdom.type.ValueType;

/**
 *
 * @author marco.perazzetta
 */
public class OdsCell extends AbstractCell {

	private OdfTableCell cell;

	public OdsCell() {
	}

	public OdsCell(OdfTableCell cell) {
		this.cell = cell;
	}

	public OdfTableCell getCell() {
		return cell;
	}

	public void setCell(OdfTableCell cell) {
		this.cell = cell;
	}

	@Override
	protected String getStringValue() {
		return this.cell.getStringValue();
	}

	@Override
	protected Number getNumberValue() {
		return this.cell.getDoubleValue();
	}

	@Override
	protected Date getDateValue() {
		return this.cell.getDateValue().getTime();
	}

	@Override
	protected int getCellType() {

		int type = STRINGA;
		String odsType = this.cell.getValueType();
		if (odsType != null) {
			if (odsType.equals(ValueType.FLOAT.toString())) {
				type = AbstractCell.NUMERO;
			} else if (odsType.equals(ValueType.STRING.toString())) {
				type = AbstractCell.STRINGA;
			} else if (odsType.equals(ValueType.CURRENCY.toString())) {
				type = AbstractCell.STRINGA;
			} else if (odsType.equals(ValueType.BOOLEAN.toString())) {
				type = AbstractCell.STRINGA;
			} else if (odsType.equals(ValueType.DATE.toString())) {
				type = AbstractCell.STRINGA;
			} else if (odsType.equals(ValueType.PERCENTAGE.toString())) {
				type = AbstractCell.STRINGA;
			} else if (odsType.equals(ValueType.TIME.toString())) {
				type = AbstractCell.STRINGA;
			} else {
				type = AbstractCell.STRINGA;;
			}
		}
		return type;
	}
}
