/*
 * Created on 26/nov/08
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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione per inizializzare le sezioni delle pubblicazioni esito di una gara a
 * lotto unico o di un lotto di gara in fase di modifica
 *
 * @author Stefano.Sabbadin
 */
public class GestionePubblicazioniEsitoFunction extends AbstractFunzioneTag {

  public GestionePubblicazioniEsitoFunction() {
    super(3, new Class[] { PageContext.class,String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    
    String querySelect = "select NGARA, NPUBG, TIPPUBG, DINPUBG, DFIPUBG, TESPUBG, IMPPUB, DINVPUBG, NPRPUB "
      + "from PUBG "
      + "where NGARA = ? "
      + "order by NGARA, NPUBG asc";
    String nGara = (String) params[1];
    String tipologiaGara = (String) params[2];
    
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);
    List<Object> listaPubblicazioniEsito = new ArrayList();
    List<Object> listaPubblicazioniEsitoLotto = new ArrayList();;
    
    try {
      List listaLotti;
      String nLotto;
      if("1".equals(tipologiaGara)){
        listaLotti = sqlManager.getListVector("select ngara from gare where codgar1 = ? order by NGARA asc", new Object[] { nGara });
        if(listaLotti != null && listaLotti.size() > 0){
          for(int i=0;i<listaLotti.size();i++){
            nLotto = SqlManager.getValueFromVectorParam(listaLotti.get(i), 0).stringValue();
            listaPubblicazioniEsitoLotto = sqlManager.getListVector(querySelect, new Object[] { nLotto });
            listaPubblicazioniEsito.addAll(listaPubblicazioniEsitoLotto);
          }
        }
      }else{
        listaPubblicazioniEsito = sqlManager.getListVector(querySelect, new Object[] { nGara });
      }
      
      if (listaPubblicazioniEsito != null && listaPubblicazioniEsito.size() > 0)
        pageContext.setAttribute("pubblicazioniEsito", listaPubblicazioniEsito,
            PageContext.REQUEST_SCOPE);
    } catch (SQLException e) {
      throw new JspException("Errore nell'estrarre le pubblicazioni esito "
          + "della gara "
          + nGara, e);
    } catch (GestoreException e) {
      throw new JspException("Errore nell'estrarre le pubblicazioni esito "
          + "della gara "
          + nGara, e);
    }

    return null;
  }

}
