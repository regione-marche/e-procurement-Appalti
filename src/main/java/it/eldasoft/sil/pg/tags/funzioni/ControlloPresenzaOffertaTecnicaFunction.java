/*
 * Created on 23-12-2015
 *
  * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che determina la applicabilità o meno del campo discriminante la
 * applicazione della legge regionale per la Sicilia
 *
 * @author Cristian Febas
 */
public class ControlloPresenzaOffertaTecnicaFunction extends AbstractFunzioneTag {

  public ControlloPresenzaOffertaTecnicaFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String result = "false";

    //Se gara a Lotto unico o Offerte distinte si considera GARE1.VALTEC, altrimenti
    //si deve controllare se fra i lotti ve n'è uno con Valtec =1
    String valtec=null;
    Long modalitaAggiudicazioneGara = null;
    String codice = (String) params[1];
    String isGaraLottiConOffertaUnica = (String) params[2];

    try {
      if("false".equals(isGaraLottiConOffertaUnica)){
        valtec = (String)sqlManager.getObject("select valtec from gare1 where ngara=?", new Object[]{codice});
        modalitaAggiudicazioneGara = (Long)sqlManager.getObject("select modlicg from gare where ngara=?", new Object[]{codice});
      }else{
        // Conteggio del numero di lotti, di una gara divisa in lotti con
        // offerta unica con GARE1.VALTEC=1.
        Long numeroLottiVALTEC = (Long) sqlManager.getObject(
                "select count(*) from gare1 " +
                 "where codgar1 = ? " +
                   "and ngara!=codgar1 " +
                   "and valtec = '1'", new Object[]{codice});

        // Se almeno un lotto di tale gara ha VALTEC=1, allora le fasi
        // FASE_APERTURA_OFFERTE_TECNICHE e FASE_CHIUSURA_VALUTAZIONE_TECNICA
        // devono essere visibile per tutti i lotti
        if(numeroLottiVALTEC != null && numeroLottiVALTEC.longValue() > 0)
          valtec="1";

        // Conteggio del numero di lotti, di una gara divisa in lotti con
        // offerta unica, di tipo 'OEPV' (GARE.MODLICG = 6).
        Long numeroLottiOEPV = (Long) sqlManager.getObject(
            "select count(*) from gare " +
             "where codgar1 = ? " +
               "and genere is null " +
               "and modlicg = 6", new Object[]{codice});

        // Se almeno un lotto di tale gara e' di tipo OEPV, allora la fase
        // FASE_APERTURA_OFFERTE_TECNICHE deve essere visibile per tutti i
        // lotti e quindi l'oggetto modalitaAggiudicazioneGara viene posto
        // comunque pari a new Long(6)
        if(numeroLottiOEPV != null && numeroLottiOEPV.longValue() > 0)
          modalitaAggiudicazioneGara = new Long(6);
      }
    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura del campo GARE1.VALTEC", e);
    }

    if("1".equals(valtec) || (new Long(6)).equals(modalitaAggiudicazioneGara))
      result = "true";
    return result;
  }

}
