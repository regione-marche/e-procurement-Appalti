/*
 * Created on 12/giu/09
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityNumeri;

/**
 * Calcolo del punteggio tecnico, del punteggio economico e del punteggio totale
 * per la pagina "Criteri di valutazione". Inoltre se il punteggio totale è >
 * 100 viene dato un warning. Il warning deve essere dato solo se si è
 * modificato il dettaglio. Quindi valorizzo una variabile si sessione alla
 * modifica del dettaglio in GestoreGOEV.java
 *
 * @author Marcello Caminiti
 */
public class ControlloPunteggiCriteriFunction extends AbstractFunzioneTag {

  public ControlloPunteggiCriteriFunction() {
    super(4, new Class[] { PageContext.class, String.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String ngara = (String) params[1];
    String costofisso = (String) params[2];
    String sezionitec = (String) params[3];
    PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        pageContext, PgManager.class);

    try {
      Double maxPunTecnico = pgManager.getSommaPunteggioTecnico(ngara);

      if (maxPunTecnico != null)
        pageContext.setAttribute("punteggioTecnico",
            UtilityNumeri.convertiDouble(maxPunTecnico,
                UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE, 3),
            PageContext.REQUEST_SCOPE);

      Double maxPunEconomico = pgManager.getSommaPunteggioEconomico(ngara);

      if (maxPunEconomico != null)
        pageContext.setAttribute("punteggioEconomico",
            UtilityNumeri.convertiDouble(maxPunEconomico,
                UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE, 3),
                    PageContext.REQUEST_SCOPE);


      //Double punteggioTotale = new Double(maxPunTecnico.doubleValue() + maxPunEconomico.doubleValue());
      Double punteggioTotale = null;

      if(maxPunTecnico != null)
        punteggioTotale = maxPunTecnico;

      if(!"1".equals(costofisso)){
        if(maxPunEconomico != null){
          if(punteggioTotale!= null)
            punteggioTotale = new Double(punteggioTotale.doubleValue() + maxPunEconomico.doubleValue());
          else
            punteggioTotale = maxPunEconomico;
        }
      }

      if(punteggioTotale!=null)
        pageContext.setAttribute("punteggioTotale",
          UtilityNumeri.convertiDouble(punteggioTotale,
              UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE, 3),
                  PageContext.REQUEST_SCOPE);

      //Lettura dei campi MINTEC e MINECO di GARE1
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
          pageContext, SqlManager.class);
      Vector datiGare1 = sqlManager.getVector("select mintec, mineco from gare1 where ngara = ?", new Object[]{ngara});
      if(datiGare1!=null && datiGare1.size()>0){

        Double mintec = (Double)((JdbcParametro) datiGare1.get(0)).getValue();
        Double mineco = (Double)((JdbcParametro) datiGare1.get(1)).getValue();

        if(mintec!= null)
          pageContext.setAttribute("SogliaMinTec",
              UtilityNumeri.convertiDouble(mintec,
                  UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE, 3),
                      PageContext.REQUEST_SCOPE);

        if(mineco!= null)
          pageContext.setAttribute("SogliaMinEco",
              UtilityNumeri.convertiDouble(mineco,
                  UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE, 3),
                      PageContext.REQUEST_SCOPE);
      }
      if("1".equals(sezionitec)) {
        Double puntecSex[] = pgManager.getSommaPunteggiTecniciSez(ngara);
        if (puntecSex[0] != null)
          pageContext.setAttribute("punteggioTecnicoQualitativo",
              UtilityNumeri.convertiDouble(puntecSex[0],
                  UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE, 3),
                      PageContext.REQUEST_SCOPE);
        if (puntecSex[1] != null)
          pageContext.setAttribute("punteggioTecnicoQuantitativo",
              UtilityNumeri.convertiDouble(puntecSex[1],
                  UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE, 3),
                      PageContext.REQUEST_SCOPE);
      }

    } catch (SQLException e) {
      throw new JspException("Errore durante il calcolo della somma dei " +
            "punteggi economici, della somma dei punteggi tecnici e del " +
            "punteggio totale ", e);
    }
    return null;
  }

}