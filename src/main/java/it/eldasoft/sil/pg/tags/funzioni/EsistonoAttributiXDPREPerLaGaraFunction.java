/*
 * Created on 17-06-2015
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.spring.UtilitySpring;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che verifica l'esistenza di attributi specifici per la gara relativi a XDPRE andando a leggere la tabella
 * GARCONFDATI
 *
 * @author marcello caminiti
 */
public class EsistonoAttributiXDPREPerLaGaraFunction extends AbstractFunzioneTag {

  /**
   * Costruttore
   */
  public EsistonoAttributiXDPREPerLaGaraFunction() {
    super(2, new Class[] {PageContext.class, String.class });
  }


  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    String ret="0";


      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean(
          "sqlManager", pageContext, SqlManager.class);

      String entitaDinamica = "XDPRE";
      String gara = ((String) params[1]);
      String entitaPadre = "DPRE";

      String select="select count(id) from garconfdati where ngara=? and entita=?";


      try {
        // estrazione dell'eventuale entità dinamica
        Long conteggio = (Long)sqlManager.getObject(select, new Object[]{gara,entitaDinamica});
        if(conteggio!=null && conteggio.longValue()>0){
          ret = conteggio.toString();

          HashMap rsEntitaDinamica = sqlManager.getHashMap(
              "select DYNENT_PGNAME, DYNENT_DESC from DYNENT where DYNENT.DYNENT_NAME = ? ", new Object[] { entitaDinamica });

          if (rsEntitaDinamica != null) {

            GeneManager geneManager = (GeneManager) UtilitySpring.getBean(
                "geneManager", pageContext, GeneManager.class);

            String joinCONFDATI = "dynent_name=entita and campo= dyncam_name and ngara='" + gara + "'";

            String tabEntitaDinamica = ((JdbcParametro) rsEntitaDinamica.get("DYNENT_PGNAME")).stringValue();
            String descrizioneEntitaDinamica = ((JdbcParametro) rsEntitaDinamica.get("DYNENT_DESC")).stringValue();

            String schemaEntitaDinamica = DizionarioTabelle.getInstance().getDaNomeTabella(entitaDinamica).getNomeSchema();

            // ricerca e popolamento dei campi del generatore attributi per
            // l'entità dinamica determinata
            geneManager.setCampiGeneratoreAttributi(pageContext, entitaDinamica,
                entitaPadre, "GARCONFDATI",joinCONFDATI);
            // se esistono dei campi definiti con il generatore attributi, allora
            // inserisco tutte le informazioni per generare la pagina
            Vector elencoCampi = (Vector) pageContext.getAttribute("elencoCampi_".concat(entitaPadre),
                PageContext.REQUEST_SCOPE);
            if (elencoCampi != null && elencoCampi.size() > 0) {
              //Si deve riordinare la lista dei campi tenendo conto del valore di NUMORD di GARCONFDATI
              List<JdbcParametro> elencoCampiGARCONFDATI = sqlManager.getListVector("select campo from garconfdati where ngara=? and entita=? order by numord", new Object[]{gara,entitaDinamica});
              if(elencoCampiGARCONFDATI!=null && elencoCampiGARCONFDATI.size()>0){
                Vector<HashMap<Object,Object>> elencoCampiOrdinato =  new Vector();
                HashMap<Object,Object> campo = new HashMap();
                String codiceCampo = null;
                for(int i=0;i<elencoCampiGARCONFDATI.size();i++){
                  codiceCampo = SqlManager.getValueFromVectorParam(elencoCampiGARCONFDATI.get(i), 0).getStringValue();
                  for(int j=0;j<elencoCampi.size();j++){
                    campo = (HashMap<Object,Object>)elencoCampi.get(j);
                    if(codiceCampo.equals(campo.get("nome"))){
                      //Si deve rendere il campo sempre visibile, indipendentemente dal valore impostato nel generatore attributi
                      campo.remove("visScheda");
                      campo.put("visScheda",new Boolean(true));
                      elencoCampiOrdinato.add(campo);
                      break;
                    }
                  }
                }
                pageContext.setAttribute("elencoCampi_".concat(entitaPadre),
                    elencoCampiOrdinato, PageContext.REQUEST_SCOPE);
              }

              pageContext.setAttribute("DYNENT_SCHEMA_".concat(entitaPadre),
                  schemaEntitaDinamica, PageContext.REQUEST_SCOPE);
              pageContext.setAttribute("DYNENT_NAME_".concat(entitaPadre),
                  entitaDinamica, PageContext.REQUEST_SCOPE);
              pageContext.setAttribute("DYNENT_PGNAME_".concat(entitaPadre),
                  tabEntitaDinamica, PageContext.REQUEST_SCOPE);
              pageContext.setAttribute("DYNENT_DESC_".concat(entitaPadre),
                  descrizioneEntitaDinamica, PageContext.REQUEST_SCOPE);

             Boolean gestisciProtezioni = new Boolean(false);
              /*
              // se esiste una property nascosta valorizzata a 1 allora si
              // controllano i campi del generatore attributi mediante profilo
              if ("1".equals(ConfigManager.getValore("it.eldasoft.genAttributi.controllo.usaProfilo")))
                gestisciProtezioni = new Boolean(true);
              */
              pageContext.setAttribute("gestisciProtezioniGenAttributi",
                  gestisciProtezioni, PageContext.REQUEST_SCOPE);
            }
          }
        }

      } catch (Throwable e) {
        throw new JspException(
            "Errore durante la lettura della tabella GARCONFDATI per la gara \""
                + gara
                + "\"", e);
      }


    return ret;
  }
}