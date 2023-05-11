package it.eldasoft.sil.w3.tags.gestori.plugin;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;

public class GestoreW3GARA extends AbstractGestorePreload {

  /** Logger */
  static Logger logger = Logger.getLogger(GestoreW3GARA.class);

  /**
   * 
   * @param tag
   */
  public GestoreW3GARA(BodyTagSupportGene tag) {
    super(tag);
  }

  public void doAfterFetch(PageContext arg0, String arg1) throws JspException {

  }

  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda)
      throws JspException {

	  SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", page, SqlManager.class);
	  String archiviFiltrati = ConfigManager.getValore("it.eldasoft.associazioneUffintAbilitata.archiviFiltrati");
  	  boolean tecniFiltrato = (archiviFiltrati.indexOf("TECNI") != -1);
	  String codein = null;
	  String cfein = null;
	  String cfanac = null;
	  String iscuc = null;
	  HttpSession session = page.getSession();
	  String uffint = (String) session.getAttribute("uffint");
	  uffint = StringUtils.stripToEmpty(uffint);
	  if(!"".equals(uffint)){
		  try {
			  codein = page.getSession().getAttribute("uffint").toString();
			  //ricavo il CF della SA
			  Vector<?> datiSA = sqlManager.getVector(
		          "select cfein, cfanac, iscuc from uffint where codein = ?", new Object[] { codein });
			  if(datiSA!= null){
				  cfein = datiSA.get(0).toString();
				  cfanac = datiSA.get(1).toString();
				  iscuc = datiSA.get(2).toString();
		      }
			  if (iscuc != null && iscuc.equals("1") && cfanac != null && !cfanac.trim().equals("")) {
				  cfein = cfanac;
			  }
			  page.setAttribute("cfein", cfein, PageContext.REQUEST_SCOPE);
	    } catch (SQLException e) {
	      throw new JspException(
	          "Errore nel recupero del codice fiscale della Stazione appaltante", e);
	    }
	  }
	  
      
    if (UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(modoAperturaScheda)) {


      try { 
      List<?> datiUFFINT = null;
      Object[] parametri = null;
      
      if(!"".equals(uffint)){
        String query = "select nomein, cfein from uffint where codein = ? ";
        parametri = new Object[] {codein};
        datiUFFINT = sqlManager.getListVector(query, parametri);
        
        if (datiUFFINT != null && datiUFFINT.size() == 1) {
          String azienda_denom = (String) SqlManager.getValueFromVectorParam(datiUFFINT.get(0), 0).getValue();
          page.setAttribute("azienda_denom", azienda_denom, PageContext.REQUEST_SCOPE);
          
          String azienda_cf = (String) SqlManager.getValueFromVectorParam(datiUFFINT.get(0), 1).getValue();
          page.setAttribute("azienda_cf", azienda_cf, PageContext.REQUEST_SCOPE);
        }
      }
 /*
        ProfiloUtente profilo = (ProfiloUtente) page.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
        
        List<?> datiTECNI = null;
        Object[] parametri = null;
        String query = "select tecni.codtec, tecni.nomtec, tecni.cftec from tecni, w3usrsys "
                        + "where tecni.codtec = w3usrsys.rup_codtec and w3usrsys.syscon = ? ";
        if(!"".equals(uffint) && tecniFiltrato){
        	query += " and tecni.cgentei = ?";
        	parametri = new Object[] { new Long(profilo.getId()) , codein };
        } else {
        	parametri = new Object[] { new Long(profilo.getId())};
        }
        datiTECNI = sqlManager.getListVector(query, parametri);
        if (datiTECNI != null && datiTECNI.size() == 1) {
          String rup_codtec = (String) SqlManager.getValueFromVectorParam(datiTECNI.get(0), 0).getValue();
          page.setAttribute("rup_codtec", rup_codtec, PageContext.REQUEST_SCOPE);

          String nomtec = (String) SqlManager.getValueFromVectorParam(datiTECNI.get(0), 1).getValue();
          page.setAttribute("nomtec", nomtec, PageContext.REQUEST_SCOPE);

          String cftec = (String) SqlManager.getValueFromVectorParam(datiTECNI.get(0), 2).getValue();
          page.setAttribute("cftec", cftec, PageContext.REQUEST_SCOPE);

          List<?> datiW3USRSYSCOLL = null;
          parametri = null;
          query = "select w3aziendaufficio.id, w3aziendaufficio.ufficio_denom, "
              + "w3aziendaufficio.azienda_cf, w3aziendaufficio.azienda_denom, w3aziendaufficio.indexcoll "
              + "from w3aziendaufficio, w3usrsyscoll "
              + "where w3aziendaufficio.id = w3usrsyscoll.w3aziendaufficio_id "
              + "and w3usrsyscoll.syscon = ? and w3usrsyscoll.rup_codtec = ? ";
          if(!"".equals(uffint)){ 
        	  query += " and w3aziendaufficio.azienda_cf = ?";
        	  parametri = new Object[] { new Long(profilo.getId()), rup_codtec, cfein };
          } else {
        	  parametri = new Object[] { new Long(profilo.getId()), rup_codtec };
          }
          datiW3USRSYSCOLL = sqlManager.getListVector(query, parametri);
          
          if (datiW3USRSYSCOLL != null && datiW3USRSYSCOLL.size() == 1) {

            Long collaborazione = (Long) SqlManager.getValueFromVectorParam(datiW3USRSYSCOLL.get(0), 0).getValue();
            page.setAttribute("collaborazione", collaborazione, PageContext.REQUEST_SCOPE);

            String ufficio_denom = (String) SqlManager.getValueFromVectorParam(datiW3USRSYSCOLL.get(0), 1).getValue();
            page.setAttribute("ufficio_denom", ufficio_denom, PageContext.REQUEST_SCOPE);

            String azienda_cf = (String) SqlManager.getValueFromVectorParam(datiW3USRSYSCOLL.get(0), 2).getValue();
            page.setAttribute("azienda_cf", azienda_cf, PageContext.REQUEST_SCOPE);

            String azienda_denom = (String) SqlManager.getValueFromVectorParam(datiW3USRSYSCOLL.get(0), 3).getValue();
            page.setAttribute("azienda_denom", azienda_denom, PageContext.REQUEST_SCOPE);

            String indexcoll = (String) SqlManager.getValueFromVectorParam(datiW3USRSYSCOLL.get(0), 4).getValue();
            page.setAttribute("indexcoll", indexcoll, PageContext.REQUEST_SCOPE);

          }
        } */
      } catch (SQLException e) {
        throw new JspException(
            "Errore nella lettura del RUP e delle collaborazioni associate", e);
      }

    } else {
    	//calcolo il totale importo gara
      try {
      	String codice = UtilityTags.getParametro(page, UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA);

        DataColumnContainer key = new DataColumnContainer(codice);
        Long numgara = new Long((key.getColumnsBySuffix("NUMGARA", true))[0].getValue().getStringValue());

        Object importoObj = sqlManager.getObject("select SUM(" + sqlManager.getDBFunction("isnull", new String[] {"IMPORTO_LOTTO", "0.00" }) + ") from W3LOTT where NUMGARA=? and STATO_SIMOG in (1,2,3,4,7,99)", 
            new Object[] { numgara });

        Double importo = new Double(0);
        if (importoObj != null) {
          if (!(importoObj instanceof Double)) {
            importo = new Double(importoObj.toString());
          } else {
            importo = (Double) importoObj;
          }
        }
      	page.setAttribute("importo", importo, PageContext.REQUEST_SCOPE);
      	//calcolo numero lotti
      	Long numero_lotti = (Long) sqlManager.getObject("select count(*) from w3lott where numgara = ? and stato_simog in (1,2,3,4,7,99)", new Object[] { numgara });
      	page.setAttribute("numero_lotti", numero_lotti, PageContext.REQUEST_SCOPE);
        
      } catch (SQLException e) {
          throw new JspException("Errore nel calcolo dell'importo gara", e);
      }
    }
  }
}
