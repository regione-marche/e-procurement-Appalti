package it.eldasoft.sil.w3.tags.gestori.plugin;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;

public class GestoreW3LOTT extends AbstractGestorePreload {

	/** Logger */
	static Logger logger = Logger.getLogger(GestoreW3LOTT.class);

	private static final String SQL_ESTRAZIONE_DATI_W3LOTT             = "select CPV from W3LOTT where NUMGARA = ? AND NUMLOTT = ?";
	private static final String SQL_ESTRAZIONE_DATI_TABCPV_CPVDESC     = "select CPVDESC from TABCPV where CPVCOD4 = ?";
	
  /**
   * 
   * @param tag
   */
  public GestoreW3LOTT(BodyTagSupportGene tag) {
    super(tag);
  }

  @Override
  public void doAfterFetch(PageContext arg0, String arg1) throws JspException {

  }

  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda) throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", page, SqlManager.class);

    String codice = UtilityTags.getParametro(page, UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA_PARENT);
    Long numgara = null;
    DataColumnContainer key = new DataColumnContainer(codice);
    try {
    	numgara = new Long((key.getColumnsBySuffix("NUMGARA", true))[0].getValue().getStringValue());
    } catch (Exception e) {
    	codice = UtilityTags.getParametro(page, UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA);
    	key = new DataColumnContainer(codice);
    	numgara = new Long((key.getColumnsBySuffix("NUMGARA", true))[0].getValue().getStringValue());
    }

    try {
		  //Ricavo Versione Simog dalla gara
		  Long ver_simog = (Long)sqlManager.getObject("select ver_simog from w3gara where NUMGARA = ?", new Object[] {numgara});
		  page.setAttribute("ver_simog", ver_simog, PageContext.REQUEST_SCOPE);
    } catch (SQLException e) {
    	throw new JspException(
    			"Errore nel recupero della versione simog dalla gara", e);
    }
  
    if (!UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(modoAperturaScheda)) {

      try {
    	  codice = UtilityTags.getParametro(page, UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA);
    	  key = new DataColumnContainer(codice);
    	  Long numlott = new Long((key.getColumnsBySuffix("NUMLOTT", true))[0].getValue().getStringValue());
        // Gestione dei tabellati per la tipologia lavoro
        gestioneTipologiaLavoro(page, sqlManager, numgara, numlott, modoAperturaScheda);

        // Gestione dei tabellati per la tipologia fornitura
        gestioneTipologiaFornitura(page, sqlManager, numgara, numlott, modoAperturaScheda);

        String luogo_istat = (String) sqlManager.getObject("select luogo_istat from w3lott where numgara = ? and numlott = ?",
            new Object[] { numgara, numlott });

        if (luogo_istat != null) {

          String selectTABSCHE = "select tb1.tabcod3, tabsche.tabdesc "
              + " from tabsche, tabsche tb1 where tabsche.tabcod='S2003' "
              + " and tabsche.tabcod1='09' and tb1.tabcod='S2003' and tb1.tabcod1='07' "
              + " and "
              + sqlManager.getDBFunction("SUBSTR", new String[] { "tabsche.tabcod3", "4", "3" })
              + " = "
              + sqlManager.getDBFunction("SUBSTR", new String[] { "tb1.tabcod2", "2", "3" })
              + " and "
              + " tabsche.tabcod3=?";

          List<?> datiTABSCHE = sqlManager.getVector(selectTABSCHE, new Object[] { luogo_istat });

          if (datiTABSCHE != null && datiTABSCHE.size() > 0) {
            String descrizioneProvincia = (String) SqlManager.getValueFromVectorParam(datiTABSCHE, 0).getValue();
            String descrizioneComune = (String) SqlManager.getValueFromVectorParam(datiTABSCHE, 1).getValue();
            page.setAttribute("descrizioneProvincia", descrizioneProvincia, PageContext.REQUEST_SCOPE);
            page.setAttribute("descrizioneComune", descrizioneComune, PageContext.REQUEST_SCOPE);
          }
        }
        
        String cpv = "";
        String cpvdescr = "";
        List< ? > datiW9LottList = sqlManager.getListVector(SQL_ESTRAZIONE_DATI_W3LOTT,
                new Object[] { numgara, numlott });
        
        if (datiW9LottList.size() > 0) {
            cpv = ((List< ? >) datiW9LottList.get(0)).get(0).toString();
            if (cpv != null && !cpv.equals("")) {
              List< ? > cpvdescrList = sqlManager.getListVector(SQL_ESTRAZIONE_DATI_TABCPV_CPVDESC,
                  new Object[] { cpv });
              if (cpvdescrList.size() > 0) {
                cpvdescr = ((List< ? >) cpvdescrList.get(0)).get(0).toString();
              }
            }
          }
          page.setAttribute("cpvdescr", cpvdescr, PageContext.REQUEST_SCOPE);

      } catch (SQLException e) {
        throw new JspException("Errore nella lettura del codice ISTAT", e);
      }

    } else {
      // Gestione dei tabellati per la tipologia lavoro
      gestioneTipologiaLavoro(page, sqlManager, null, null, modoAperturaScheda);

      // Gestione dei tabellati per la tipologia fornitura
      gestioneTipologiaFornitura(page, sqlManager, null, null, modoAperturaScheda);

    }
  }

  /**
   * Gestione dei campi relativi alle tipologie lavoro per il tipo contratto
   * lavori. Si tratta di campi multipli disegnati sempre nella scheda
   * principale ma basati su una tabella figlia (W3LOTTTIPI) dell'entità
   * princiapale (W3LOTT)
   * 
   * @param page
   * @param sqlManager
   * @param schedacompleta_id
   * @throws JspException
   */
  private void gestioneTipologiaLavoro(PageContext page, SqlManager sqlManager, Long numgara, Long numlott, String modoAperturaScheda)
      throws JspException {
    String selectTipologiaLavoro = "select tab1desc, tab1tip, cast('2' as varchar(1)), TAB1ARC from tab1 where tab1cod = ?";
    String selectW3LOTTTIPI = "select count(*) from w3lotttipi where numgara = ? and numlott = ? and idappalto = ?";

    try {
      List tabellatoTipologiaLavoro = sqlManager.getListVector(selectTipologiaLavoro, new Object[] { "W3002" });

      if (tabellatoTipologiaLavoro != null && tabellatoTipologiaLavoro.size() > 0) {
        for (int i = 0; i < tabellatoTipologiaLavoro.size(); i++) {
          String tab1desc = SqlManager.getValueFromVectorParam(tabellatoTipologiaLavoro.get(i), 0).stringValue();
          Long tab1tip = SqlManager.getValueFromVectorParam(tabellatoTipologiaLavoro.get(i), 1).longValue();
          String tab1arc = SqlManager.getValueFromVectorParam(tabellatoTipologiaLavoro.get(i), 3).stringValue();
          
          if (!UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(modoAperturaScheda)) {
            Long conteggio = (Long) sqlManager.getObject(selectW3LOTTTIPI, new Object[] { numgara, numlott, tab1tip });
            if (conteggio != null && conteggio.longValue() > 0) {
              tabellatoTipologiaLavoro.set(i, new Object[] { tab1desc, tab1tip, "1", tab1arc });
            }
          }
        }
      }
      page.setAttribute("tabellatoTipologiaLavoro", tabellatoTipologiaLavoro, PageContext.REQUEST_SCOPE);

    } catch (SQLException e) {
      throw new JspException("Errore nella gestione della Tipologia Lavoro", e);
    } catch (GestoreException e) {
      throw new JspException("Errore nella gestione della Tipologia Lavoro", e);
    }
  }

  /**
   * Gestione dei campi relativi alle modalità di fornitura per tipo contratto
   * forniture Si tratta di campi multipli disegnati sempre nella scheda
   * principale ma basati su una tabella figlia (W3LOTTTIPI) dell'entità
   * principale (W3LOTT)
   * 
   * @param page
   * @param sqlManager
   * @param schedacompleta_id
   * @throws JspException
   */
  private void gestioneTipologiaFornitura(PageContext page, SqlManager sqlManager, Long numgara, Long numlott, String modoAperturaScheda)
      throws JspException {
    String selectTipologiaFornitura = "select tab1desc, tab1tip, cast('2' as varchar(1)), TAB1ARC from tab1 where tab1cod = ?";
    String selectW3LOTTTIPI = "select count(*) from w3lotttipi where numgara = ? and numlott = ? and idappalto = ?";

    try {
      List tabellatoTipologiaFornitura = sqlManager.getListVector(selectTipologiaFornitura, new Object[] { "W3019" });

      if (tabellatoTipologiaFornitura != null && tabellatoTipologiaFornitura.size() > 0) {
        for (int i = 0; i < tabellatoTipologiaFornitura.size(); i++) {
          String tab1desc = SqlManager.getValueFromVectorParam(tabellatoTipologiaFornitura.get(i), 0).stringValue();
          Long tab1tip = SqlManager.getValueFromVectorParam(tabellatoTipologiaFornitura.get(i), 1).longValue();
          String tab1arc = SqlManager.getValueFromVectorParam(tabellatoTipologiaFornitura.get(i), 3).stringValue();
          
          if (!UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(modoAperturaScheda)) {
            Long conteggio = (Long) sqlManager.getObject(selectW3LOTTTIPI, new Object[] { numgara, numlott, tab1tip });
            if (conteggio != null && conteggio.longValue() > 0) {
              tabellatoTipologiaFornitura.set(i, new Object[] { tab1desc, tab1tip, "1", tab1arc });
            }
          } 
        }
      }
      page.setAttribute("tabellatoTipologiaFornitura", tabellatoTipologiaFornitura, PageContext.REQUEST_SCOPE);

    } catch (SQLException e) {
      throw new JspException("Errore nella gestione della Tipologia Fornitura", e);
    } catch (GestoreException e) {
      throw new JspException("Errore nella gestione della Tipologia Fornitura", e);
    }
  }

}
