/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eldasoft.sil.pg.bl.excel;

import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.utility.UtilityDate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.odftoolkit.odfdom.doc.table.OdfTable;
import org.odftoolkit.odfdom.doc.table.OdfTableCell;
import org.odftoolkit.odfdom.doc.table.OdfTableRow;

/**
 *
 * @author marco.perazzetta
 */
public abstract class AbstractCell {

	protected static final int STRINGA = 0;
	protected static final int NUMERO = 1;

	private static final DecimalFormat dfOriginal;

	static {
		DecimalFormatSymbols originalFormat = new DecimalFormatSymbols(Locale.ITALIAN);
		originalFormat.setGroupingSeparator('.');
		originalFormat.setDecimalSeparator(',');
		dfOriginal = new DecimalFormat("#,##0.00000", originalFormat);
	}

	protected abstract String getStringValue();

	protected abstract Number getNumberValue();

	protected abstract Date getDateValue();

	protected abstract int getCellType();

	protected static AbstractCell getInstance(Object sheet, int rowIndex, int columnIndex) throws GestoreException {

		Object row;
		Object cell;
		AbstractCell absCell = null;
		if (sheet instanceof Sheet) {
			row = ((Sheet) sheet).getRow(rowIndex);
		} else if (sheet instanceof OdfTable) {
			row = ((OdfTable) sheet).getRowByIndex(rowIndex);
		} else {
			throw new GestoreException("Impossibile ricavare il foglio di lavoro, tipo di oggetto sconosciuto", "legge190.cannotdecodesheetype");
		}
		if (row != null) {
			if (sheet instanceof Sheet) {
				cell = ((Row) row).getCell(columnIndex);
			} else {
				cell = ((OdfTableRow) row).getCellByIndex(columnIndex);
			}
			if (cell instanceof Cell) {
				absCell = new ExcelCell((Cell) cell);
			} else if (cell instanceof OdfTableCell) {
				absCell = new OdsCell((OdfTableCell) cell);
			}
		}
		return absCell;
	}

	protected String getStringFromCell() throws GestoreException {

		String str = null;
		if (this != null) {
			try {
				str = this.getStringValue();
			} catch (Exception ex) {
				try {
					str = this.getNumberFromCell(null).toString();
				} catch (GestoreException ex1) {
					try {
						str = this.getDateFromCell().toString();
					} catch (GestoreException ex2) {
						throw new GestoreException("Impossibile convertire in stringa il valore della cella", "legge190.cannotgetstringfromcell");
					}
				}
			}
		}
		return str;
	}

	protected Number getNumberFromCell(Integer numDecimals) throws GestoreException {

		Number num = null;
		if (this != null) {
			switch (this.getCellType()) {
				case AbstractCell.NUMERO:
					num = this.getNumberValue();
					break;
				case AbstractCell.STRINGA:
					try {
						String cellvalue = ExcelUtils.myTrim(this.getStringValue());
						if (StringUtils.isNotBlank(cellvalue)) {
							try {
								cellvalue = cellvalue.replace(".", "").replace(",", ".");
								num = Double.parseDouble(cellvalue);
							} catch (NumberFormatException ex1) {
								throw new GestoreException("La cella non contiene un valore numerico", "legge190.cannotgetnumberfromcell");
							}
						}
					} catch (Exception ex2) {
						throw new GestoreException("La cella non contiene un valore numerico", "legge190.cannotgetnumberfromcell");
					}
					break;
				default:
					throw new GestoreException("La cella non contiene un valore numerico", "legge190.cannotgetnumberfromcell");
			}
			num = getCorrectFormatNumber(num, numDecimals);
		}
		return num;
	}

	protected Date getDateFromCell() throws GestoreException {

		Date date = null;
		String dateStr = null;
		if (this != null) {
			try {
				date = this.getDateValue();
			} catch (Exception ex1) {
				try {
					dateStr = ExcelUtils.myTrim(this.getStringValue());
				} catch (Exception ex2) {
					throw new GestoreException("La cella non contiene una data", "legge190.cannotgetdatefromcell");
				}
			}
		}
		if (date == null && StringUtils.isNotBlank(dateStr)) {

				date = UtilityDate.convertiData(dateStr, UtilityDate.FORMATO_GG_MM_AAAA);
				if(date == null)
				  throw new GestoreException("La cella non contiene una data", "legge190.cannotgetdatefromcell");
		}
		return date;
	}

	private static Number getCorrectFormatNumber(Number num, Integer numDecimals) {

		Number ret = null;
		if (num != null) {
			String numFormatted = dfOriginal.format(num);
			int i = numFormatted.indexOf(',') + 1;
			Integer decimals = new Integer(numFormatted.substring(i));
			if (decimals == 0) {
				ret = num.longValue();
			} else {
				BigDecimal bd = new BigDecimal(num.doubleValue());
				if (numDecimals != null && numDecimals > 0) {
					bd = bd.setScale(numDecimals, RoundingMode.HALF_UP);
					ret = bd.doubleValue();
				} else if (numDecimals == 0) {
					bd = bd.setScale(0, RoundingMode.HALF_UP);
					ret = bd.longValue();
				} else {
					ret = num.doubleValue();
				}
			}
		}
		return ret;
	}
}
