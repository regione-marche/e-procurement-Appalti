/*
 * Created on 23/03/120
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.plugin;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityNumeri;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Gestore richiamato dalla pagina popupAggiornaImportoOfferto.jsp per effettuare i controlli
 * sull'importo offerto
 *
 * @author Marcello Caminiti
 */
public class GestorePopupAggiornaImportoOfferto extends AbstractGestorePreload {

  public GestorePopupAggiornaImportoOfferto(BodyTagSupportGene tag) {
    super(tag);
  }


  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda)
      throws JspException {
  }


  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda)
      throws JspException {


    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        page, SqlManager.class);


    // lettura dei parametri di input
    String codiceGara = page.getRequest().getParameter("codiceGara");
    String ngara = page.getRequest().getParameter("numeroGara");
    String codiceDitta = page.getRequest().getParameter("codiceDitta");
    String  isOffertaUnica = page.getRequest().getParameter("isGaraLottiConOffertaUnica");
    String totaleOfferto = page.getRequest().getParameter("totaleOfferto");
    String  isPrequalifica = page.getRequest().getParameter("isPrequalifica");
    String sicinc = page.getRequest().getParameter("sicinc");
    String faseGara = page.getRequest().getParameter("faseGara");
    String ribcal = page.getRequest().getParameter("ribcal");

    String modo = (String) page.getAttribute(
        UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO, PageContext.REQUEST_SCOPE);

    if (UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(modo) && "false".equals( isPrequalifica)) {
      String msg="";
      String esitoControllo = "ok";
      String sbiancareAggiudicazione = "false";

      try {
        if (!"TRUE".equals(isOffertaUnica.toUpperCase())){
          String select="select impapp,offaum from gare,torn where ngara=? and codgar1=? and codgar1=codgar";
          Vector dati = sqlManager.getVector(select, new Object[]{ngara,codiceGara});
          if(dati!=null && dati.size()>0 ){
            Double impapp = ((JdbcParametro) dati.get(0)).doubleValue();
            String offaum = ((JdbcParametro) dati.get(1)).stringValue();
            if (impapp==null)
              impapp = new Double(0);

            Double importoDouble = UtilityNumeri.convertiDouble(totaleOfferto);
            if(importoDouble==null)
              importoDouble = new Double(0);

            if("2".equals(offaum) && importoDouble.doubleValue()>impapp.doubleValue()){
              esitoControllo="NOK";
              msg="<b>ATTENZIONE:</b> L'importo offerto supera l'importo a base di gara.";
              if ("2".equals(ribcal) && Long.parseLong(faseGara)<7){
                msg+=" Non si procederà pertanto all'aggiornamento del ribasso percentuale.";
                sbiancareAggiudicazione="true";
              }
              page.setAttribute("messaggioControlloImporto", msg, PageContext.REQUEST_SCOPE);
            }

          }
        }else{
          String select="select IMPAPP,OFFAUM,NGARA,IMPNRL,IMPSIC from gare,ditg,torn where codgar1 = ? and (genere <> 3 or genere is null) and codgar1=codgar " +
            "and (modlicg = 5 or modlicg = 6 or modlicg = 14 or modlicg = 16) and codgar1=codgar5 and ngara5=ngara and dittao = ? and (partgar='1' or partgar is null)";
          List datiGARE = sqlManager.getListVector(select, new Object[] { codiceGara,codiceDitta });
          if (datiGARE != null && datiGARE.size() > 0) {
            double totaleOffertoLotto;
            Double importoNoRibasso = null;
            Double importoSicurezza = null;
            for (int i = 0; i < datiGARE.size(); i++) {
              Double impapp = SqlManager.getValueFromVectorParam(datiGARE.get(i),0).doubleValue();
              if(impapp == null) impapp = new Double(0);
              String offaum = SqlManager.getValueFromVectorParam(datiGARE.get(i), 1).stringValue();
              String ngaraLotto = SqlManager.getValueFromVectorParam(datiGARE.get(i), 2).stringValue();

            //Importo non soggetto al ribasso
              importoNoRibasso = SqlManager.getValueFromVectorParam(datiGARE.get(i),3).doubleValue();
              if(importoNoRibasso == null)
                  importoNoRibasso = new Double(0);

              //Importo sicurezza
              importoSicurezza = SqlManager.getValueFromVectorParam(datiGARE.get(i),4).doubleValue();
              if(importoSicurezza == null)
                  importoSicurezza = new Double(0);

              //Importo dettaglio prezzi
              select = "select impoff,reqmin from dpre where ngara= ? and dittao = ?";

              double totImpoff=0;
              List retDPRE = sqlManager.getListVector(select,
                      new Object[] { ngaraLotto, codiceDitta});
              if (retDPRE != null && retDPRE.size() > 0){
                for (int j = 0; j < retDPRE.size(); j++) {
                      Double impoff = SqlManager.getValueFromVectorParam(retDPRE.get(j),0).doubleValue();
                      if (impoff  == null ) impoff = new Double(0);
                      totImpoff += impoff.doubleValue();
                  }
              }

              totaleOffertoLotto = importoNoRibasso.doubleValue() + totImpoff;
              if (sicinc == null || "1".equals(sicinc)) totaleOffertoLotto += importoSicurezza.doubleValue();

              if("2".equals(offaum) && totaleOffertoLotto >impapp.doubleValue()){
                esitoControllo = "NOK";
                if(msg.length()>2)
                  msg+=", ";
                msg+=ngaraLotto;
              }

            }

            if("NOK".equals(esitoControllo)){
              String messaggio="<b>ATTENZIONE:</b> L'importo offerto supera l'importo a base di gara per i seguenti lotti: " + msg + ".";
              if ("2".equals(ribcal) && Long.parseLong(faseGara)<7){
                messaggio+=" Per questi lotti non si procederà all'aggiornamento del ribasso percentuale.";
                sbiancareAggiudicazione="true";
              }
              page.setAttribute("messaggioControlloImporto", messaggio, PageContext.REQUEST_SCOPE);
            }
          }

        }


      }catch (SQLException e) {
          throw new JspException("Errore durante la valutazione dell'importo offerto rispetto all'importo a base di gara ", e);
      } catch (GestoreException e) {
        throw new JspException("Errore durante la valutazione dell'importo offerto rispetto all'importo a base di gara ", e);
      }

      page.setAttribute("sbiancareAggiudicazione", sbiancareAggiudicazione, PageContext.REQUEST_SCOPE);

    }
  }

}