/*
 * Created on 02/03/16
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
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Viene eseguito il controllo per tutti i lotti OEPV di una gara ad oferta unica che siano valorizzati i punteggi tecnici ed economici dei lotti
 * e che le ditte presentino dei punteggi inferiori a quelli specificati per il lotto
 *
 * @author Marcello Caminiti
 */
public class ControlloPunteggiLottiOEPVFunction extends AbstractFunzioneTag {

  public ControlloPunteggiLottiOEPVFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        pageContext, PgManager.class);

    String messageKey= null;
    String message = null;

    String chiave = (String) params[1];
    try{
      List listaLottiOEPV = sqlManager.getListVector("select ngara,codiga from gare where codgar1=? and modlicg=6 and ngara<>codgar1", new Object[]{chiave});
      if(listaLottiOEPV!=null && listaLottiOEPV.size()>0){
        Double maxPunTecnico = null;
        Double maxPunEconomico = null;
        String msgPunteggiGaraOffUnica = "";
        String msgPunteggiDitteOffUnica = "";
        for(int i=0;i<listaLottiOEPV.size();i++){
          Vector lotto = (Vector) listaLottiOEPV.get(i);
          String ngaraLotto = ((JdbcParametro) lotto.get(0)).getStringValue();
          String codiga = ((JdbcParametro) lotto.get(1)).getStringValue();

          maxPunTecnico = pgManager.getSommaPunteggioTecnico(ngaraLotto);
          maxPunEconomico = pgManager.getSommaPunteggioEconomico(ngaraLotto);

          //Devono essere valorizzati il punteggio tecnico ed economico di ogni lotto
          if(maxPunTecnico== null ||maxPunEconomico==null)
            msgPunteggiGaraOffUnica += codiga + ",";

          if(!pgManager.controlloPunteggiDitte(ngaraLotto, maxPunTecnico, maxPunEconomico, true,"ATTIVAZIONE"))
            msgPunteggiDitteOffUnica += codiga + ",";
        }
        if(msgPunteggiGaraOffUnica!=""){
          msgPunteggiGaraOffUnica = msgPunteggiGaraOffUnica.substring(0, msgPunteggiGaraOffUnica.length() - 1);
          String tmp[] = msgPunteggiGaraOffUnica.split(",");
          if(tmp.length>1)
            msgPunteggiGaraOffUnica = "(verificare i lotti " + msgPunteggiGaraOffUnica + ")";
          else
            msgPunteggiGaraOffUnica = "(verificare il lotto " + msgPunteggiGaraOffUnica + ")";
          messageKey = "errors.gestoreException.*.attivazioneCalcoloAggiudicazione.ControlloPunteggiGaraOffUnica";
          message = this.resBundleGenerale.getString(messageKey);
          message = message.replace("{0}", msgPunteggiGaraOffUnica);
        }
        if(msgPunteggiDitteOffUnica!=""){
          msgPunteggiDitteOffUnica = msgPunteggiDitteOffUnica.substring(0, msgPunteggiDitteOffUnica.length() - 1);
          String tmp[] = msgPunteggiDitteOffUnica.split(",");
          if(tmp.length>1)
            msgPunteggiDitteOffUnica = "(verificare i lotti " + msgPunteggiDitteOffUnica + ")";
          else
            msgPunteggiDitteOffUnica = "(verificare il lotto " + msgPunteggiDitteOffUnica + ")";

          messageKey = "errors.gestoreException.*.attivazioneCalcoloAggiudicazione.ControlloPunteggiDitteOffUnica";
          String msgTmp = this.resBundleGenerale.getString(messageKey);
          msgTmp = msgTmp.replace("{0}", msgPunteggiDitteOffUnica);
          if(message!=null){
            message += "<br>";
            message += msgTmp;
          }else
            message = msgTmp;
        }
      }
    }catch (SQLException e){
      throw new JspException("Errore nella lettura dei dati della gara " +  chiave, e);
    }catch (GestoreException e) {
      throw new JspException("Errore nella lettura dei dati economici della gara " +  chiave, e);
    }
    return message;
  }

}