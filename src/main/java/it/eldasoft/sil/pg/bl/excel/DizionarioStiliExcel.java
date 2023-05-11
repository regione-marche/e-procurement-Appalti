/*
 * Created on 16-ott-2009
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl.excel;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormatter;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

/**
 * Dizionario di stili per creare fogli Excel
 *
 * @author Luca.Giacomazzo
 */
public class DizionarioStiliExcel {

  private static final String  FORMATO_STRINGA              = "@";
  private static final String  FORMATO_DATA_GG_MM_AAAA      = "dd/mm/yyyy";
  private static final String  FORMATO_NUMERO_INTERO        = "0";
  private static final String  FORMATO_NUMERO_CON_VIRGOLA_0 = "#,##0.###;-#,##0.###";
  private static final String  FORMATO_NUMERO_CON_VIRGOLA_1 = "#,##0.0##;-#,##0.0##";
  private static final String  FORMATO_NUMERO_CON_VIRGOLA_2 = "#,##0.00;-#,##0.00";
  private static final String  FORMATO_NUMERO_CON_VIRGOLA_3 = "#,##0.000;-#,##0.000";
  private static final String  FORMATO_NUMERO_CON_VIRGOLA_4 = "#,##0.00##;-#,##0.00##";
  private static final String  FORMATO_NUMERO_CON_VIRGOLA_5 = "#,##0.00###;-#,##0.00###";
  private static final String  FORMATO_NUMERO_CON_VIRGOLA_9 = "#,##0.00#######;-#,##0.00#######";

  private static final short DEFAULT_FONT_HEIGHT = 8;
  private static final short DEFAULT_HEADER_FONT_HEIGHT = 8;

  private static final String DEFAULT_FONT_NAME = HSSFFont.FONT_ARIAL;

	private Map<Integer,HSSFCellStyle> mappaStili = null;
	private Map<String,HSSFFont> mappaFonts  = null;

	public DizionarioStiliExcel(HSSFWorkbook wb){
		this.mappaFonts = new HashMap<String,HSSFFont>();
		this.mappaStili = new HashMap<Integer,HSSFCellStyle>();
		this.creaStili(wb);
	}

	private void creaStili(HSSFWorkbook wb){
    HSSFCellStyle style = null;

    // Mappa dei font definiti nell'intero workbook
    this.createFonts(wb);
    // Aggiunge i formati specifici da usare all'interno del workbook
    this.createFormats(wb);

    // Stile per l'intestazione
    style = wb.createCellStyle();
    style.setFont((HSSFFont) this.mappaFonts.get("headerFont"));
    style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
    style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
    style.setFillForegroundColor(HSSFColor.PALE_BLUE.index);
    style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    style.setDataFormat(wb.createDataFormat().getFormat(FORMATO_STRINGA));
    style.setWrapText(true);
    this.setBorder(style);
    this.mappaStili.put(new Integer(CELLA_INTESTAZIONE), style);

    // Stile per l'instazione (sfondo arancione)
    // Utilizzato per le esportazioni dell'allegato 6 e 7 per ARSS
    style = wb.createCellStyle();
    style.setFont((HSSFFont) this.mappaFonts.get("headerFont"));
    style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
    style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
    style.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);
    style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    style.setDataFormat(wb.createDataFormat().getFormat(FORMATO_STRINGA));
    style.setWrapText(true);
    this.setBorder(style);
    this.mappaStili.put(new Integer(CELLA_INTESTAZIONE_ARANCIONE), style);


    // Stile per le celle nascoste
    style = wb.createCellStyle();
    style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
    style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
    style.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);
    style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    style.setFont((HSSFFont) this.mappaFonts.get("defaultFont"));
    style.setDataFormat(wb.createDataFormat().getFormat(FORMATO_STRINGA));
    this.setBorder(style);
    style.setWrapText(true);
    this.mappaStili.put(new Integer(CELLA_NASCOSTA), style);

    // Stile per le celle che separano intestazione dai dati (che non conterrano
    // alcun testo)
    style = wb.createCellStyle();
    style.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
    style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    this.setBorder(style);
    this.mappaStili.put(new Integer(CELLA_SEPARATRICE), style);

    // Stile per le celle che separano intestazione dai dati (arancione) (che non conterrano
    // alcun testo)
    style = wb.createCellStyle();
    style.setFillForegroundColor(HSSFColor.ORANGE.index);
    style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    this.setBorder(style);
    this.mappaStili.put(new Integer(CELLA_SEPARATRICE_ARANCIONE), style);

		HSSFFont font = wb.createFont(); // Create a new font in the workbook
    font.setFontName("Calibri");
    font.setColor(HSSFColor.BLACK.index);
		font.setFontHeightInPoints(Short.parseShort("11"));

		//Stile INTERO_ALIGN_CENTER_FRAMED
		style = wb.createCellStyle();
		style.setFont(font);
	  style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		style.setWrapText(true);
		style.setDataFormat(wb.createDataFormat().getFormat(FORMATO_NUMERO_INTERO));
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		this.setBorder(style);
		this.mappaStili.put(new Integer(INTERO_ALIGN_CENTER_FRAMED), style);

		//Stile STRINGA_ALIGN_CENTER_FRAMED
		style = wb.createCellStyle();
		style.setFont(font);
	  style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		style.setWrapText(true);
		style.setDataFormat(wb.createDataFormat().getFormat(FORMATO_STRINGA));
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		this.setBorder(style);
		this.mappaStili.put(new Integer(STRINGA_ALIGN_CENTER_FRAMED), style);

		//Stile DECIMALE2_ALIGN_CENTER_FRAMED
		style = wb.createCellStyle();
		style.setFont(font);
	  style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		style.setDataFormat(wb.createDataFormat().getFormat(FORMATO_NUMERO_CON_VIRGOLA_2));
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		this.setBorder(style);
		this.mappaStili.put(new Integer(DECIMALE2_ALIGN_CENTER_FRAMED), style);
		
	      //Stile DECIMALE4_ALIGN_RIGHT_FRAMED
        style = wb.createCellStyle();
        style.setFont((HSSFFont) this.mappaFonts.get("defaultFont"));
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        style.setDataFormat(wb.createDataFormat().getFormat(FORMATO_NUMERO_CON_VIRGOLA_4));
        style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
        this.mappaStili.put(new Integer(DECIMALE4_ALIGN_RIGHT), style);

		//Stile DATA_ALIGN_CENTER_FRAMED
		style = wb.createCellStyle();
		style.setFont(font);
	  style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		style.setDataFormat(wb.createDataFormat().getFormat(FORMATO_DATA_GG_MM_AAAA));
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		this.setBorder(style);
		this.mappaStili.put(new Integer(DATA_ALIGN_CENTER_FRAMED), style);

		//Stile STRINGA_ALIGN_CENTER_GENERAL_FORMAT
        style = wb.createCellStyle();
        style.setFont(font);
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        style.setWrapText(true);
        //style.setDataFormat(wb.createDataFormat().getFormat(FORMATO_STRINGA));
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        this.setBorder(style);
        this.mappaStili.put(new Integer(STRINGA_ALIGN_CENTER_GENERAL_FORMAT), style);

    String[] arrayFormatoDati = new String[] { FORMATO_STRINGA,
        FORMATO_DATA_GG_MM_AAAA, FORMATO_NUMERO_INTERO,
        FORMATO_NUMERO_CON_VIRGOLA_2, FORMATO_NUMERO_CON_VIRGOLA_3,
        FORMATO_NUMERO_CON_VIRGOLA_5, FORMATO_NUMERO_CON_VIRGOLA_9,
        FORMATO_NUMERO_CON_VIRGOLA_1, FORMATO_NUMERO_CON_VIRGOLA_0};
    int[] arrayKeyCellStyle = {1, 31, 61, 91, 121, 151, 181, 211, 241};

    // Ciclo che permette di creare per ciascun formato di dato con relativo
    // allineamento, uno stile per cella bloccate in modifica e per celle non
    // bloccate in modifica (UNLOCKED)
    for(int cellaLocked = 1; cellaLocked > -2; cellaLocked -= 2){
      // Ciclo che permette di creare per ciascun formato di dato uno stile
      // con allineamento a destra, al centro e a sinistra
    	for (int indiceFormatoDati = 0; indiceFormatoDati < arrayFormatoDati.length; indiceFormatoDati++) {
	      for(int horizontalAlign = 0; horizontalAlign <= 2; horizontalAlign++) {
	        for(int boldWeight = 0; boldWeight <= 3; boldWeight++){
	        	style = wb.createCellStyle();
	          style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
	          style.setDataFormat(wb.createDataFormat().getFormat(arrayFormatoDati[indiceFormatoDati]));
	          if (indiceFormatoDati <= 2)
	            style.setWrapText(true);

	        	switch (boldWeight) {
						case 0:
							style.setFont((HSSFFont) this.mappaFonts.get("defaultFont"));
							break;
						case 1:
							style.setFont((HSSFFont) this.mappaFonts.get("defaultBoldFont"));
							break;
						case 2:
							style.setFont((HSSFFont) this.mappaFonts.get("defaultItalicFont"));
							break;
						case 3:
							style.setFont((HSSFFont) this.mappaFonts.get("defaultBoldItalicFont"));
							break;
						}

		        switch(horizontalAlign){
		        case 0:
		          style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		          break;
		        case 1:
		          style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		          break;
		        case 2:
		          style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		          break;
		        }

		        if(cellaLocked < 0)
		        	style.setLocked(false);

		        this.mappaStili.put(new Integer(cellaLocked * (arrayKeyCellStyle[indiceFormatoDati] +
		        		(10 * horizontalAlign) + boldWeight)), style);
	        }
	      }
	    }
    }
  }

  private void setBorder(HSSFCellStyle style) {
    style.setBorderTop(HSSFCellStyle.BORDER_THIN);
    style.setBorderRight(HSSFCellStyle.BORDER_THIN);
    style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
    style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
    style.setTopBorderColor(HSSFColor.BLACK.index);
    style.setRightBorderColor(HSSFColor.BLACK.index);
    style.setBottomBorderColor(HSSFColor.BLACK.index);
    style.setLeftBorderColor(HSSFColor.BLACK.index);
  }

	public HSSFCellStyle getStileExcel(int idStile){
		return (HSSFCellStyle) this.mappaStili.get(new Integer(idStile));
	}

	public static final int STRINGA_ALIGN_LEFT     									= 1;
	public static final int STRINGA_ALIGN_LEFT_BOLD     						= 2;
	public static final int STRINGA_ALIGN_LEFT_ITALIC     					= 3;
	public static final int STRINGA_ALIGN_LEFT_BOLD_ITALIC 	  			= 4;

	public static final int STRINGA_ALIGN_CENTER   					 				= 11;
	public static final int STRINGA_ALIGN_CENTER_BOLD   		 				= 12;
	public static final int STRINGA_ALIGN_CENTER_ITALIC   	 				= 13;
	public static final int STRINGA_ALIGN_CENTER_BOLD_ITALIC 				= 14;

	public static final int STRINGA_ALIGN_RIGHT    									= 21;
	public static final int STRINGA_ALIGN_RIGHT_BOLD   							= 22;
	public static final int STRINGA_ALIGN_RIGHT_ITALIC   						= 23;
	public static final int STRINGA_ALIGN_RIGHT_BOLD_ITALIC 				= 24;

	public static final int DATA_ALIGN_LEFT        									= 31;
	public static final int DATA_ALIGN_LEFT_BOLD        						= 32;
	public static final int DATA_ALIGN_LEFT_ITALIC      						= 33;
	public static final int DATA_ALIGN_LEFT_BOLD_ITALIC 						= 34;

	public static final int DATA_ALIGN_CENTER      						 			= 41;
	public static final int DATA_ALIGN_CENTER_LEFT_BOLD        			= 42;
	public static final int DATA_ALIGN_CENTER_LEFT_ITALIC      			= 43;
	public static final int DATA_ALIGN_CENTER_LEFT_BOLD_ITALIC 			= 44;

	public static final int DATA_ALIGN_RIGHT       									= 51;
	public static final int DATA_ALIGN_RIGHT_LEFT_BOLD        			= 52;
	public static final int DATA_ALIGN_RIGHT_LEFT_ITALIC      			= 53;
	public static final int DATA_ALIGN_RIGHT_LEFT_BOLD_ITALIC 			= 54;

	public static final int INTERO_ALIGN_LEFT      									= 61;
	public static final int INTERO_ALIGN_LEFT_LEFT_BOLD        			= 62;
	public static final int INTERO_ALIGN_LEFT_LEFT_ITALIC      			= 63;
	public static final int INTERO_ALIGN_LEFT_LEFT_BOLD_ITALIC 			= 64;

	public static final int INTERO_ALIGN_CENTER    									= 71;
	public static final int INTERO_ALIGN_CENTER_LEFT_BOLD        		= 72;
	public static final int INTERO_ALIGN_CENTER_LEFT_ITALIC      		= 73;
	public static final int INTERO_ALIGN_CENTER_LEFT_BOLD_ITALIC 		= 74;

	public static final int INTERO_ALIGN_RIGHT     									= 81;
	public static final int INTERO_ALIGN_RIGHT_LEFT_BOLD        		= 82;
	public static final int INTERO_ALIGN_RIGHT_LEFT_ITALIC      		= 83;
	public static final int INTERO_ALIGN_RIGHT_LEFT_BOLD_ITALIC 		= 84;

	public static final int DECIMALE2_ALIGN_LEFT   									= 91;
	public static final int DECIMALE2_ALIGN_LEFT_LEFT_BOLD        	= 92;
	public static final int DECIMALE2_ALIGN_LEFT_LEFT_ITALIC      	= 93;
	public static final int DECIMALE2_ALIGN_LEFT_LEFT_BOLD_ITALIC 	= 94;

	public static final int DECIMALE2_ALIGN_CENTER 									= 101;
	public static final int DECIMALE2_ALIGN_CENTER_LEFT_BOLD        = 102;
	public static final int DECIMALE2_ALIGN_CENTER_LEFT_ITALIC      = 103;
	public static final int DECIMALE2_ALIGN_CENTER_LEFT_BOLD_ITALIC = 104;

	public static final int DECIMALE2_ALIGN_RIGHT  									= 111;
	public static final int DECIMALE2_ALIGN_RIGHT_LEFT_BOLD        	= 112;
	public static final int DECIMALE2_ALIGN_RIGHT_LEFT_ITALIC      	= 113;
	public static final int DECIMALE2_ALIGN_RIGHT_LEFT_BOLD_ITALIC 	= 114;

	public static final int DECIMALE3_ALIGN_LEFT   									= 121;
	public static final int DECIMALE3_ALIGN_LEFT_LEFT_BOLD        	= 122;
	public static final int DECIMALE3_ALIGN_LEFT_LEFT_ITALIC      	= 123;
	public static final int DECIMALE3_ALIGN_LEFT_LEFT_BOLD_ITALIC 	= 124;

	public static final int DECIMALE3_ALIGN_CENTER									= 131;
	public static final int DECIMALE3_ALIGN_CENTER_LEFT_BOLD        = 132;
	public static final int DECIMALE3_ALIGN_CENTER_LEFT_ITALIC      = 133;
	public static final int DECIMALE3_ALIGN_CENTER_LEFT_BOLD_ITALIC = 134;
	
	public static final int DECIMALE3_ALIGN_RIGHT  									= 141;
	public static final int DECIMALE3_ALIGN_RIGHT_LEFT_BOLD        	= 142;
	public static final int DECIMALE3_ALIGN_RIGHT_LEFT_ITALIC      	= 143;
	public static final int DECIMALE3_ALIGN_RIGHT_LEFT_BOLD_ITALIC 	= 144;

	public static final int DECIMALE5_ALIGN_LEFT   									= 151;
	public static final int DECIMALE5_ALIGN_LEFT_LEFT_BOLD        	= 152;
	public static final int DECIMALE5_ALIGN_LEFT_LEFT_ITALIC      	= 153;
	public static final int DECIMALE5_ALIGN_LEFT_LEFT_BOLD_ITALIC 	= 154;

	public static final int DECIMALE5_ALIGN_CENTER 									= 161;
	public static final int DECIMALE5_ALIGN_CENTER_LEFT_BOLD        = 162;
	public static final int DECIMALE5_ALIGN_CENTER_LEFT_ITALIC      = 163;
	public static final int DECIMALE5_ALIGN_CENTER_LEFT_BOLD_ITALIC = 164;

	public static final int DECIMALE5_ALIGN_RIGHT  								 	= 171;
	public static final int DECIMALE5_ALIGN_RIGHT_LEFT_BOLD       	= 172;
	public static final int DECIMALE5_ALIGN_RIGHT_LEFT_ITALIC     	= 173;
	public static final int DECIMALE5_ALIGN_RIGHT_LEFT_BOLD_ITALIC 	= 174;

	public static final int DECIMALE9_ALIGN_LEFT   									= 181;
	public static final int DECIMALE9_ALIGN_LEFT_LEFT_BOLD        	= 182;
	public static final int DECIMALE9_ALIGN_LEFT_LEFT_ITALIC      	= 183;
	public static final int DECIMALE9_ALIGN_LEFT_LEFT_BOLD_ITALIC 	= 184;

	public static final int DECIMALE9_ALIGN_CENTER 									= 191;
	public static final int DECIMALE9_ALIGN_CENTER_LEFT_BOLD        = 192;
	public static final int DECIMALE9_ALIGN_CENTER_LEFT_ITALIC      = 193;
	public static final int DECIMALE9_ALIGN_CENTER_LEFT_BOLD_ITALIC = 194;

	public static final int DECIMALE9_ALIGN_RIGHT  								  = 201;
	public static final int DECIMALE9_ALIGN_RIGHT_LEFT_BOLD         = 202;
	public static final int DECIMALE9_ALIGN_RIGHT_LEFT_ITALIC       = 203;
	public static final int DECIMALE9_ALIGN_RIGHT_LEFT_BOLD_ITALIC  = 204;

	public static final int DECIMALE1_ALIGN_LEFT   									= 211;
	public static final int DECIMALE1_ALIGN_LEFT_LEFT_BOLD        	= 212;
	public static final int DECIMALE1_ALIGN_LEFT_LEFT_ITALIC      	= 213;
	public static final int DECIMALE1_ALIGN_LEFT_LEFT_BOLD_ITALIC 	= 214;

	public static final int DECIMALE1_ALIGN_CENTER 									= 221;
	public static final int DECIMALE1_ALIGN_CENTER_LEFT_BOLD        = 222;
	public static final int DECIMALE1_ALIGN_CENTER_LEFT_ITALIC      = 223;
	public static final int DECIMALE1_ALIGN_CENTER_LEFT_BOLD_ITALIC = 224;

	public static final int DECIMALE1_ALIGN_RIGHT  								  = 231;
	public static final int DECIMALE1_ALIGN_RIGHT_LEFT_BOLD         = 232;
	public static final int DECIMALE1_ALIGN_RIGHT_LEFT_ITALIC       = 233;
	public static final int DECIMALE1_ALIGN_RIGHT_LEFT_BOLD_ITALIC  = 234;

	public static final int DECIMALE0_ALIGN_LEFT   									= 241;
	public static final int DECIMALE0_ALIGN_LEFT_LEFT_BOLD        	= 242;
	public static final int DECIMALE0_ALIGN_LEFT_LEFT_ITALIC      	= 243;
	public static final int DECIMALE0_ALIGN_LEFT_LEFT_BOLD_ITALIC 	= 244;

	public static final int DECIMALE0_ALIGN_CENTER 									= 251;
	public static final int DECIMALE0_ALIGN_CENTER_LEFT_BOLD        = 252;
	public static final int DECIMALE0_ALIGN_CENTER_LEFT_ITALIC      = 253;
	public static final int DECIMALE0_ALIGN_CENTER_LEFT_BOLD_ITALIC = 254;

	public static final int DECIMALE0_ALIGN_RIGHT  								  = 261;
	public static final int DECIMALE0_ALIGN_RIGHT_LEFT_BOLD         = 262;
	public static final int DECIMALE0_ALIGN_RIGHT_LEFT_ITALIC       = 263;
	public static final int DECIMALE0_ALIGN_RIGHT_LEFT_BOLD_ITALIC  = 264;

	public static final int STRINGA_ALIGN_LEFT_UNLOCKED												= -STRINGA_ALIGN_LEFT;
	public static final int STRINGA_ALIGN_LEFT_BOLD_UNLOCKED									= -STRINGA_ALIGN_LEFT_BOLD;
	public static final int STRINGA_ALIGN_LEFT_ITALIC_UNLOCKED     						= -STRINGA_ALIGN_LEFT_ITALIC;
	public static final int STRINGA_ALIGN_LEFT_BOLD_ITALIC_UNLOCKED 					= -STRINGA_ALIGN_LEFT_BOLD_ITALIC;

	public static final int STRINGA_ALIGN_CENTER_UNLOCKED   									= -STRINGA_ALIGN_CENTER;
	public static final int STRINGA_ALIGN_CENTER_BOLD_UNLOCKED  	 				 		= -STRINGA_ALIGN_CENTER_BOLD;
	public static final int STRINGA_ALIGN_CENTER_ITALIC_UNLOCKED 							= -STRINGA_ALIGN_CENTER_ITALIC;
	public static final int STRINGA_ALIGN_CENTER_BOLD_ITALIC_UNLOCKED					= -STRINGA_ALIGN_CENTER_BOLD_ITALIC;

	public static final int STRINGA_ALIGN_RIGHT_UNLOCKED    									= -STRINGA_ALIGN_RIGHT;
	public static final int STRINGA_ALIGN_RIGHT_BOLD_UNLOCKED   							= -STRINGA_ALIGN_RIGHT_BOLD;
	public static final int STRINGA_ALIGN_RIGHT_ITALIC_UNLOCKED   						= -STRINGA_ALIGN_RIGHT_ITALIC;
	public static final int STRINGA_ALIGN_RIGHT_BOLD_ITALIC_UNLOCKED				 	= -STRINGA_ALIGN_RIGHT_BOLD_ITALIC;

	public static final int DATA_ALIGN_LEFT_UNLOCKED        									= -DATA_ALIGN_LEFT;
	public static final int DATA_ALIGN_LEFT_BOLD_UNLOCKED      		  					= -DATA_ALIGN_LEFT_BOLD;
	public static final int DATA_ALIGN_LEFT_ITALIC_UNLOCKED      							= -DATA_ALIGN_LEFT_ITALIC;
	public static final int DATA_ALIGN_LEFT_BOLD_ITALIC_UNLOCKED							= -DATA_ALIGN_LEFT_BOLD_ITALIC;

	public static final int DATA_ALIGN_CENTER_UNLOCKED      									= -DATA_ALIGN_CENTER;
	public static final int DATA_ALIGN_CENTER_LEFT_BOLD_UNLOCKED    	   			= -DATA_ALIGN_CENTER_LEFT_BOLD;
	public static final int DATA_ALIGN_CENTER_LEFT_ITALIC_UNLOCKED   			 	 	= -DATA_ALIGN_CENTER_LEFT_ITALIC;
	public static final int DATA_ALIGN_CENTER_LEFT_BOLD_ITALIC_UNLOCKED				= -DATA_ALIGN_CENTER_LEFT_BOLD_ITALIC;

	public static final int DATA_ALIGN_RIGHT_UNLOCKED       									= -DATA_ALIGN_RIGHT;
	public static final int DATA_ALIGN_RIGHT_LEFT_BOLD_UNLOCKED     	 			 	= -DATA_ALIGN_RIGHT_LEFT_BOLD;
	public static final int DATA_ALIGN_RIGHT_LEFT_ITALIC_UNLOCKED 	    			= -DATA_ALIGN_RIGHT_LEFT_ITALIC;
	public static final int DATA_ALIGN_RIGHT_LEFT_BOLD_ITALIC_UNLOCKED				= -DATA_ALIGN_RIGHT_LEFT_BOLD_ITALIC;

	public static final int INTERO_ALIGN_LEFT_UNLOCKED      									= -INTERO_ALIGN_LEFT;
	public static final int INTERO_ALIGN_LEFT_LEFT_BOLD_UNLOCKED      	 			= -INTERO_ALIGN_LEFT_LEFT_BOLD;
	public static final int INTERO_ALIGN_LEFT_LEFT_ITALIC_UNLOCKED  	   			= -INTERO_ALIGN_LEFT_LEFT_ITALIC;
	public static final int INTERO_ALIGN_LEFT_LEFT_BOLD_ITALIC_UNLOCKED				= -INTERO_ALIGN_LEFT_LEFT_BOLD_ITALIC;

	public static final int INTERO_ALIGN_CENTER_UNLOCKED    									= -INTERO_ALIGN_CENTER;
	public static final int INTERO_ALIGN_CENTER_LEFT_BOLD_UNLOCKED        		= -INTERO_ALIGN_CENTER_LEFT_BOLD;
	public static final int INTERO_ALIGN_CENTER_LEFT_ITALIC_UNLOCKED      		= -INTERO_ALIGN_CENTER_LEFT_ITALIC;
	public static final int INTERO_ALIGN_CENTER_LEFT_BOLD_ITALIC_UNLOCKED 		= -INTERO_ALIGN_CENTER_LEFT_BOLD_ITALIC;

	public static final int INTERO_ALIGN_RIGHT_UNLOCKED     									= -INTERO_ALIGN_RIGHT;
	public static final int INTERO_ALIGN_RIGHT_LEFT_BOLD_UNLOCKED       			= -INTERO_ALIGN_RIGHT_LEFT_BOLD;
	public static final int INTERO_ALIGN_RIGHT_LEFT_ITALIC_UNLOCKED     			= -INTERO_ALIGN_RIGHT_LEFT_ITALIC;
	public static final int INTERO_ALIGN_RIGHT_LEFT_BOLD_ITALIC_UNLOCKED			= -INTERO_ALIGN_RIGHT_LEFT_BOLD_ITALIC;

	public static final int DECIMALE2_ALIGN_LEFT_UNLOCKED   									= -DECIMALE2_ALIGN_LEFT;
	public static final int DECIMALE2_ALIGN_LEFT_LEFT_BOLD_UNLOCKED       		= -DECIMALE2_ALIGN_LEFT_LEFT_BOLD;
	public static final int DECIMALE2_ALIGN_LEFT_LEFT_ITALIC_UNLOCKED     		= -DECIMALE2_ALIGN_LEFT_LEFT_ITALIC;
	public static final int DECIMALE2_ALIGN_LEFT_LEFT_BOLD_ITALIC_UNLOCKED		= -DECIMALE2_ALIGN_LEFT_LEFT_BOLD_ITALIC;

	public static final int DECIMALE2_ALIGN_CENTER_UNLOCKED 									= -DECIMALE2_ALIGN_CENTER;
	public static final int DECIMALE2_ALIGN_CENTER_LEFT_BOLD_UNLOCKED       	= -DECIMALE2_ALIGN_CENTER_LEFT_BOLD;
	public static final int DECIMALE2_ALIGN_CENTER_LEFT_ITALIC_UNLOCKED     	= -DECIMALE2_ALIGN_CENTER_LEFT_ITALIC;
	public static final int DECIMALE2_ALIGN_CENTER_LEFT_BOLD_ITALIC_UNLOCKED	= -DECIMALE2_ALIGN_CENTER_LEFT_BOLD_ITALIC;

	public static final int DECIMALE2_ALIGN_RIGHT_UNLOCKED 										= -DECIMALE2_ALIGN_RIGHT;
	public static final int DECIMALE2_ALIGN_RIGHT_LEFT_BOLD_UNLOCKED 					= -DECIMALE2_ALIGN_RIGHT_LEFT_BOLD;
	public static final int DECIMALE2_ALIGN_RIGHT_LEFT_ITALIC_UNLOCKED 				= -DECIMALE2_ALIGN_RIGHT_LEFT_ITALIC;
	public static final int DECIMALE2_ALIGN_RIGHT_LEFT_BOLD_ITALIC_UNLOCKED 	= -DECIMALE2_ALIGN_RIGHT_LEFT_BOLD_ITALIC;

	public static final int DECIMALE3_ALIGN_LEFT_UNLOCKED 						 				= -DECIMALE3_ALIGN_LEFT;
	public static final int DECIMALE3_ALIGN_LEFT_LEFT_BOLD_UNLOCKED         	= -DECIMALE3_ALIGN_LEFT_LEFT_BOLD;
	public static final int DECIMALE3_ALIGN_LEFT_LEFT_ITALIC_UNLOCKED 				= -DECIMALE3_ALIGN_LEFT_LEFT_ITALIC;
	public static final int DECIMALE3_ALIGN_LEFT_LEFT_BOLD_ITALIC_UNLOCKED 	 	= -DECIMALE3_ALIGN_LEFT_LEFT_BOLD_ITALIC;

	public static final int DECIMALE3_ALIGN_CENTER_UNLOCKED										= -DECIMALE3_ALIGN_CENTER;
	public static final int DECIMALE3_ALIGN_CENTER_LEFT_BOLD_UNLOCKED					= -DECIMALE3_ALIGN_CENTER_LEFT_BOLD;
	public static final int DECIMALE3_ALIGN_CENTER_LEFT_ITALIC_UNLOCKED      	= -DECIMALE3_ALIGN_CENTER_LEFT_ITALIC;
	public static final int DECIMALE3_ALIGN_CENTER_LEFT_BOLD_ITALIC_UNLOCKED	= -DECIMALE3_ALIGN_CENTER_LEFT_BOLD_ITALIC;

	public static final int DECIMALE3_ALIGN_RIGHT_UNLOCKED	  								= -DECIMALE3_ALIGN_RIGHT;
	public static final int DECIMALE3_ALIGN_RIGHT_LEFT_BOLD_UNLOCKED	       	= -DECIMALE3_ALIGN_RIGHT_LEFT_BOLD;
	public static final int DECIMALE3_ALIGN_RIGHT_LEFT_ITALIC_UNLOCKED	     	= -DECIMALE3_ALIGN_RIGHT_LEFT_ITALIC;
	public static final int DECIMALE3_ALIGN_RIGHT_LEFT_BOLD_ITALIC_UNLOCKED	 	= -DECIMALE3_ALIGN_RIGHT_LEFT_BOLD_ITALIC;

	public static final int DECIMALE5_ALIGN_LEFT_UNLOCKED	   									= -DECIMALE5_ALIGN_LEFT;
	public static final int DECIMALE5_ALIGN_LEFT_LEFT_BOLD_UNLOCKED	        	= -DECIMALE5_ALIGN_LEFT_LEFT_BOLD;
	public static final int DECIMALE5_ALIGN_LEFT_LEFT_ITALIC_UNLOCKED	      	= -DECIMALE5_ALIGN_LEFT_LEFT_ITALIC;
	public static final int DECIMALE5_ALIGN_LEFT_LEFT_BOLD_ITALIC_UNLOCKED	 	= -DECIMALE5_ALIGN_LEFT_LEFT_BOLD_ITALIC;

	public static final int DECIMALE5_ALIGN_CENTER_UNLOCKED										= -DECIMALE5_ALIGN_CENTER;
	public static final int DECIMALE5_ALIGN_CENTER_LEFT_BOLD_UNLOCKED	        = -DECIMALE5_ALIGN_CENTER_LEFT_BOLD;
	public static final int DECIMALE5_ALIGN_CENTER_LEFT_ITALIC_UNLOCKED	      = -DECIMALE5_ALIGN_CENTER_LEFT_ITALIC;
	public static final int DECIMALE5_ALIGN_CENTER_LEFT_BOLD_ITALIC_UNLOCKED	= -DECIMALE5_ALIGN_CENTER_LEFT_BOLD_ITALIC;

	public static final int DECIMALE5_ALIGN_RIGHT_UNLOCKED	 								 	= -DECIMALE5_ALIGN_RIGHT;
	public static final int DECIMALE5_ALIGN_RIGHT_LEFT_BOLD_UNLOCKED	       	= -DECIMALE5_ALIGN_RIGHT_LEFT_BOLD;
	public static final int DECIMALE5_ALIGN_RIGHT_LEFT_ITALIC_UNLOCKED	     	= -DECIMALE5_ALIGN_RIGHT_LEFT_ITALIC;
	public static final int DECIMALE5_ALIGN_RIGHT_LEFT_BOLD_ITALIC_UNLOCKED	 	= -DECIMALE5_ALIGN_RIGHT_LEFT_BOLD_ITALIC;

	public static final int DECIMALE9_ALIGN_LEFT_UNLOCKED	   									= -DECIMALE9_ALIGN_LEFT;
	public static final int DECIMALE9_ALIGN_LEFT_LEFT_BOLD_UNLOCKED	        	= -DECIMALE9_ALIGN_LEFT_LEFT_BOLD;
	public static final int DECIMALE9_ALIGN_LEFT_LEFT_ITALIC_UNLOCKED	      	= -DECIMALE9_ALIGN_LEFT_LEFT_ITALIC;
	public static final int DECIMALE9_ALIGN_LEFT_LEFT_BOLD_ITALIC_UNLOCKED	 	= -DECIMALE9_ALIGN_LEFT_LEFT_BOLD_ITALIC;

	public static final int DECIMALE9_ALIGN_CENTER_UNLOCKED	 									= -DECIMALE9_ALIGN_CENTER;
	public static final int DECIMALE9_ALIGN_CENTER_LEFT_BOLD_UNLOCKED	        = -DECIMALE9_ALIGN_CENTER_LEFT_BOLD;
	public static final int DECIMALE9_ALIGN_CENTER_LEFT_ITALIC_UNLOCKED	      = -DECIMALE9_ALIGN_CENTER_LEFT_ITALIC;
	public static final int DECIMALE9_ALIGN_CENTER_LEFT_BOLD_ITALIC_UNLOCKED	= -DECIMALE9_ALIGN_CENTER_LEFT_BOLD_ITALIC;

	public static final int DECIMALE9_ALIGN_RIGHT_UNLOCKED  								  = -DECIMALE9_ALIGN_RIGHT;
	public static final int DECIMALE9_ALIGN_RIGHT_LEFT_BOLD_UNLOCKED       	  = -DECIMALE9_ALIGN_RIGHT_LEFT_BOLD;
	public static final int DECIMALE9_ALIGN_RIGHT_LEFT_ITALIC_UNLOCKED      	= -DECIMALE9_ALIGN_RIGHT_LEFT_ITALIC;
	public static final int DECIMALE9_ALIGN_RIGHT_LEFT_BOLD_ITALIC_UNLOCKED		= -DECIMALE9_ALIGN_RIGHT_LEFT_BOLD_ITALIC;

	public static final int DECIMALE1_ALIGN_LEFT_UNLOCKED   									= -DECIMALE1_ALIGN_LEFT;
	public static final int DECIMALE1_ALIGN_LEFT_LEFT_BOLD_UNLOCKED						= -DECIMALE1_ALIGN_LEFT_LEFT_BOLD;
	public static final int DECIMALE1_ALIGN_LEFT_LEFT_ITALIC_UNLOCKED					= -DECIMALE1_ALIGN_LEFT_LEFT_ITALIC;
	public static final int DECIMALE1_ALIGN_LEFT_LEFT_BOLD_ITALIC_UNLOCKED		= -DECIMALE1_ALIGN_LEFT_LEFT_BOLD_ITALIC;

	public static final int DECIMALE1_ALIGN_CENTER_UNLOCKED 									= -DECIMALE1_ALIGN_CENTER;
	public static final int DECIMALE1_ALIGN_CENTER_LEFT_BOLD_UNLOCKED        	= -DECIMALE1_ALIGN_CENTER_LEFT_BOLD;
	public static final int DECIMALE1_ALIGN_CENTER_LEFT_ITALIC_UNLOCKED      	= -DECIMALE1_ALIGN_CENTER_LEFT_ITALIC;
	public static final int DECIMALE1_ALIGN_CENTER_LEFT_BOLD_ITALIC_UNLOCKED	= -DECIMALE1_ALIGN_CENTER_LEFT_BOLD_ITALIC;

	public static final int DECIMALE1_ALIGN_RIGHT_UNLOCKED  								  = -DECIMALE1_ALIGN_RIGHT;
	public static final int DECIMALE1_ALIGN_RIGHT_LEFT_BOLD_UNLOCKED       	  = -DECIMALE1_ALIGN_RIGHT_LEFT_BOLD;
	public static final int DECIMALE1_ALIGN_RIGHT_LEFT_ITALIC_UNLOCKED      	= -DECIMALE1_ALIGN_RIGHT_LEFT_ITALIC;
	public static final int DECIMALE1_ALIGN_RIGHT_LEFT_BOLD_ITALIC_UNLOCKED 	= -DECIMALE1_ALIGN_RIGHT_LEFT_BOLD_ITALIC;

	public static final int DECIMALE0_ALIGN_LEFT_UNLOCKED   									= -DECIMALE0_ALIGN_LEFT;
	public static final int DECIMALE0_ALIGN_LEFT_LEFT_BOLD_UNLOCKED        		= -DECIMALE0_ALIGN_LEFT_LEFT_BOLD;
	public static final int DECIMALE0_ALIGN_LEFT_LEFT_ITALIC_UNLOCKED      		= -DECIMALE0_ALIGN_LEFT_LEFT_ITALIC;
	public static final int DECIMALE0_ALIGN_LEFT_LEFT_BOLD_ITALIC_UNLOCKED	 	= -DECIMALE0_ALIGN_LEFT_LEFT_BOLD_ITALIC;

	public static final int DECIMALE0_ALIGN_CENTER_UNLOCKED 									= -DECIMALE0_ALIGN_CENTER;
	public static final int DECIMALE0_ALIGN_CENTER_LEFT_BOLD_UNLOCKED       	= -DECIMALE0_ALIGN_CENTER_LEFT_BOLD;
	public static final int DECIMALE0_ALIGN_CENTER_LEFT_ITALIC_UNLOCKED				= -DECIMALE0_ALIGN_CENTER_LEFT_ITALIC;
	public static final int DECIMALE0_ALIGN_CENTER_LEFT_BOLD_ITALIC_UNLOCKED	= -DECIMALE0_ALIGN_CENTER_LEFT_BOLD_ITALIC;

	public static final int DECIMALE0_ALIGN_RIGHT_UNLOCKED  								  = -DECIMALE0_ALIGN_RIGHT;
	public static final int DECIMALE0_ALIGN_RIGHT_LEFT_BOLD_UNLOCKED					= -DECIMALE0_ALIGN_RIGHT_LEFT_BOLD;
	public static final int DECIMALE0_ALIGN_RIGHT_LEFT_ITALIC_UNLOCKED				= -DECIMALE0_ALIGN_RIGHT_LEFT_ITALIC;
	public static final int DECIMALE0_ALIGN_RIGHT_LEFT_BOLD_ITALIC_UNLOCKED		= -DECIMALE0_ALIGN_RIGHT_LEFT_BOLD_ITALIC;


	public static final int CELLA_INTESTAZIONE																= 500;
	public static final int CELLA_NASCOSTA																		= 501;
	public static final int CELLA_SEPARATRICE																	= 502;
	public static final int CELLA_INTESTAZIONE_ARANCIONE											= 503;
	public static final int CELLA_SEPARATRICE_ARANCIONE												= 504;
	public static final int INTERO_ALIGN_CENTER_FRAMED												= 505;
	public static final int STRINGA_ALIGN_CENTER_FRAMED												= 506;
	public static final int DECIMALE2_ALIGN_CENTER_FRAMED											= 507;
	public static final int DATA_ALIGN_CENTER_FRAMED												= 508;
	public static final int STRINGA_ALIGN_CENTER_GENERAL_FORMAT                                     = 509;
	public static final int DECIMALE4_ALIGN_RIGHT                                                   = 510;
	
  /**
   * Creazione di una libreria di font
   * @param wb
   * @return Ritorna una mappa
   */
  private void createFonts(HSSFWorkbook wb) {
    HSSFFont font = null;

    // Font per l'intestazione delle colonne:
    font = wb.createFont();
    font.setFontHeightInPoints(DEFAULT_HEADER_FONT_HEIGHT);
    font.setFontName(DEFAULT_FONT_NAME);
    font.setColor(HSSFColor.DARK_BLUE.index);
    font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
    this.mappaFonts.put("headerFont", font);

    // Font di default:
    font = wb.createFont();
    font.setFontHeightInPoints(DEFAULT_FONT_HEIGHT);
    font.setFontName(DEFAULT_FONT_NAME);
    font.setColor(HSSFColor.BLACK.index);
    font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
    this.mappaFonts.put("defaultFont", font);

    // Font default + italic:
    font = wb.createFont();
    font.setFontHeightInPoints(DEFAULT_FONT_HEIGHT);
    font.setFontName(DEFAULT_FONT_NAME);
    font.setColor(HSSFColor.BLACK.index);
    font.setItalic(true);
    font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
    this.mappaFonts.put("defaultItalicFont", font);

    // Font default + bold: Arial 8 + bold
    font = wb.createFont();
    font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
    font.setColor(HSSFColor.BLACK.index);
    font.setFontHeightInPoints(DEFAULT_FONT_HEIGHT);
    font.setFontName(DEFAULT_FONT_NAME);
    this.mappaFonts.put("defaultBoldFont", font);

    // Font default + bold: Arial 10 + bold + italic
    font = wb.createFont();
    font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
    font.setColor(HSSFColor.BLACK.index);
    font.setFontHeightInPoints(DEFAULT_FONT_HEIGHT);
    font.setFontName(DEFAULT_FONT_NAME);
    font.setItalic(true);
    this.mappaFonts.put("defaultBoldItalicFont", font);
  }

  /**
   * Creazione dei formati numerici specifici per il foglio Excel: tali formati
   * vengono riusati richiamandoli attraverso l'istruzione
   *
   * HSSFWorkbook.createDataFormat().getFormat(String nome del formato);
   *
   * @param wb
   */
  private void createFormats(HSSFWorkbook wb){
  	HSSFDataFormatter formatter = new HSSFDataFormatter();

  	NumberFormat formato = NumberFormat.getNumberInstance();
  	DecimalFormat dFormat = (DecimalFormat) formato;

  	dFormat.applyPattern(FORMATO_NUMERO_CON_VIRGOLA_0);
  	formatter.addFormat(FORMATO_NUMERO_CON_VIRGOLA_0, dFormat);

  	dFormat.applyPattern(FORMATO_NUMERO_CON_VIRGOLA_1);
  	formatter.addFormat(FORMATO_NUMERO_CON_VIRGOLA_1, dFormat);

  	dFormat.applyPattern(FORMATO_NUMERO_CON_VIRGOLA_2);
  	formatter.addFormat(FORMATO_NUMERO_CON_VIRGOLA_2, dFormat);

  	dFormat.applyPattern(FORMATO_NUMERO_CON_VIRGOLA_3);
  	formatter.addFormat(FORMATO_NUMERO_CON_VIRGOLA_3, dFormat);

  	dFormat.applyPattern(FORMATO_NUMERO_CON_VIRGOLA_5);
  	formatter.addFormat(FORMATO_NUMERO_CON_VIRGOLA_5, dFormat);

  	dFormat.applyPattern(FORMATO_NUMERO_CON_VIRGOLA_9);
  	formatter.addFormat(FORMATO_NUMERO_CON_VIRGOLA_9, dFormat);

  }
}
