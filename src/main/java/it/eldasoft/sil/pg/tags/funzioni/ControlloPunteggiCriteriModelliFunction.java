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

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityNumeri;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Calcolo del punteggio tecnico, del punteggio economico e del punteggio totale
 * per la pagina "Criteri di valutazione". Inoltre se il punteggio totale è >
 * 100 viene dato un warning. Il warning deve essere dato solo se si è
 * modificato il dettaglio. Quindi valorizzo una variabile si sessione alla
 * modifica del dettaglio in GestoreGOEV.java
 *
 * @author Diego Pavan
 */
public class ControlloPunteggiCriteriModelliFunction extends AbstractFunzioneTag {

  public ControlloPunteggiCriteriModelliFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }
  
  SqlManager sqlManager;

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String idcrimodStringa = (String) params[1];
    Long idcrimod = Long.parseLong(idcrimodStringa);
    this.sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);
    

    try {
      Double maxPunTecnico = this.getSommaPunteggioTecnico(idcrimod);

      if (maxPunTecnico != null)
        pageContext.setAttribute("punteggioTecnico",
            UtilityNumeri.convertiDouble(maxPunTecnico,
                UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE, 3),
            PageContext.REQUEST_SCOPE);

      Double maxPunEconomico = this.getSommaPunteggioEconomico(idcrimod);

      if (maxPunEconomico != null)
        pageContext.setAttribute("punteggioEconomico",
            UtilityNumeri.convertiDouble(maxPunEconomico,
                UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE, 3),
                    PageContext.REQUEST_SCOPE);


      //Double punteggioTotale = new Double(maxPunTecnico.doubleValue() + maxPunEconomico.doubleValue());
      Double punteggioTotale = null;

      if(maxPunTecnico != null)
        punteggioTotale = maxPunTecnico;

      if(maxPunEconomico != null){
        if(punteggioTotale!= null)
          punteggioTotale = new Double(punteggioTotale.doubleValue() + maxPunEconomico.doubleValue());
        else
          punteggioTotale = maxPunEconomico;
      }

      if(punteggioTotale!=null)
        pageContext.setAttribute("punteggioTotale",
          UtilityNumeri.convertiDouble(punteggioTotale,
              UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE, 3),
                  PageContext.REQUEST_SCOPE);

    } catch (SQLException e) {
      throw new JspException("Errore durante il calcolo della somma dei " +
            "punteggi economici, della somma dei punteggi tecnici e del " +
            "punteggio totale ", e);
    }
    return null;
  }

  public Double getSommaPunteggioEconomico(Long idcrimod) throws SQLException {
    Double result = null;
    double sommaPunteggi = 0;
    boolean punteggioImpostato = false;

    List<?> listaMaxpunEconomico = this.sqlManager.getListHashMap(
        "select MAXPUN from GOEVMOD,G1CRIMOD where GOEVMOD.IDCRIMOD = G1CRIMOD.ID "
            + "and G1CRIMOD.ID = ? and TIPPAR = ? and (livpar = 1 or livpar = 3)", new Object[] {
                idcrimod, new Integer(2) });
    if (listaMaxpunEconomico != null && listaMaxpunEconomico.size() > 0) {
      for (int i = 0; i < listaMaxpunEconomico.size(); i++) {
        Double tmp = (Double) ((JdbcParametro) ((HashMap<?,?>) listaMaxpunEconomico.get(i)).get("MAXPUN")).getValue();
        if (tmp != null) {
          sommaPunteggi += tmp.doubleValue();
          punteggioImpostato = true;
        }
      }
      if (punteggioImpostato) result = new Double(sommaPunteggi);
    }
    return result;
  }
  
  public Double getSommaPunteggioTecnico(Long idcrimod) throws SQLException {
    Double result = null;
    double sommaPunteggi = 0;
    boolean punteggioImpostato = false;

    List<?> listaMaxpunTecnico = this.sqlManager.getListHashMap(
        "select MAXPUN from GOEVMOD,G1CRIMOD where GOEVMOD.IDCRIMOD = G1CRIMOD.ID "
            + "and G1CRIMOD.ID = ? and TIPPAR = ? and (livpar = 1 or livpar = 3)", new Object[] {
                idcrimod, new Integer(1) });
    if (listaMaxpunTecnico != null && listaMaxpunTecnico.size() > 0) {
      for (int i = 0; i < listaMaxpunTecnico.size(); i++) {
        Double tmp = (Double) ((JdbcParametro) ((HashMap<?,?>) listaMaxpunTecnico.get(i)).get("MAXPUN")).getValue();
        if (tmp != null) {
          sommaPunteggi += tmp.doubleValue();
          punteggioImpostato = true;
        }
      }
      if (punteggioImpostato) result = new Double(sommaPunteggi);
    }
    return result;
  }
  
}