/*
 * Created on 24/07/17
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
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * La funzione verifica se nel caso sia prevista la parametrazione, essa sia stata
 * effettivamente applicata
 *
 * @author Marcello Caminiti
 */
public class ControlloPresenzaRiparametrazioneApplicataFunction extends AbstractFunzioneTag {

  public ControlloPresenzaRiparametrazioneApplicataFunction() {
    super(4, new Class[] { PageContext.class, String.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String ngara = (String) params[1];
    String tipo = (String) params[2]; //"1" si controlla la riparametrazione Tecnica, "2" quella economica
    String lotti = (String) params[3];//"singolo" si controlla un singolo lotto, "tutti" si controllano tutti i lotti OEPV della gara

    String esito= "OK";
    Long fase = null;
    String selectLotti="select g.ngara, g1.###, g.codiga from gare g, gare1 g1 where ";
    String selectConteggioPunteggi = "select count(ngara5) from ditg where ngara5=? and (fasgar is null or fasgar > ?) and ### is null ";

    if("singolo".equals(lotti))
      selectLotti+="g.ngara =? and g.ngara=g1.ngara";
    else
      selectLotti+="g.codgar1=? and g.codgar1 != g.ngara and g.modlicg=6 and g1.ngara=g.ngara";

    selectLotti+=" order by g.ngara";

    if("1".equals(tipo)){
      fase=new Long(5);
      selectLotti = selectLotti.replace("###", "riptec");
      selectConteggioPunteggi = selectConteggioPunteggi.replace("###", "puntecrip");
    }else{
      fase=new Long(6);
      selectLotti = selectLotti.replace("###", "ripeco");
      selectConteggioPunteggi = selectConteggioPunteggi.replace("###", "punecorip");
    }

    String elencoLotti="";
    try {
      List listaLotti = sqlManager.getListVector(selectLotti, new Object[]{ngara});
      if(listaLotti!=null && listaLotti.size()>0){
        String ngaraLotto = null;
        String codiga=null;
        Long riparametrazione=null;
        Long numOccorrenze = null;

        for(int i=0;i<listaLotti.size();i++){
          ngaraLotto = SqlManager.getValueFromVectorParam(listaLotti.get(i), 0).getStringValue();
          riparametrazione = SqlManager.getValueFromVectorParam(listaLotti.get(i), 1).longValue();
          codiga = SqlManager.getValueFromVectorParam(listaLotti.get(i), 2).getStringValue();
          if(new Long(1).equals(riparametrazione) || new Long(2).equals(riparametrazione)){
            numOccorrenze = (Long) sqlManager.getObject(selectConteggioPunteggi, new Object[] { ngaraLotto,fase});
            if (numOccorrenze.longValue() >0){
              esito = "NOK";
              elencoLotti+=codiga + ",";
            }
          }
        }
      }

    }catch (SQLException e) {
      throw new JspException("Errore nel controllo sulla presenza della riparametrazione ", e);
    } catch (GestoreException e) {
      throw new JspException("Errore nel controllo sulla presenza della riparametrazione ", e);
    }
    if("NOK".equals(esito) && !"singolo".equals(lotti)){
      elencoLotti= elencoLotti.substring(0, elencoLotti.length()-1);
      pageContext.setAttribute("elencoLottiNonRiparam",elencoLotti);
    }

    return esito;
  }

}