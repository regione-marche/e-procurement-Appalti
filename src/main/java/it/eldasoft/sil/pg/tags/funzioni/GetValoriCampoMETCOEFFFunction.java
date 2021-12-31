/*
 * Created on 26/04/2016
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
import it.eldasoft.utils.utility.UtilityNumeri;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che preleva la descrizione dal tabellato A1127 o A1140, che contiene un valore
 * che rappresenta un numero decimale con la virgola come separatore delle cifre decimali.
 * Tale valore viene convertito in un double e viene creata la lista delle coppie di valori
 * (Double, String)
 *
 * @author M.C.
 */
public class GetValoriCampoMETCOEFFFunction extends AbstractFunzioneTag {

  public GetValoriCampoMETCOEFFFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
    String modalita= (String)params[1];

    String tabellato="A1127";
    if("DLGS2017".equals(modalita))
      tabellato="A1140";

    List<?> datiTabellato = null;
    List listaDatiVisualizzazione = null;
    try {
      String select = "select tab1desc from tab1 where tab1cod=? order by tab1tip";
      datiTabellato = sqlManager.getListVector(select, new Object[]{tabellato});
      if(datiTabellato!=null && datiTabellato.size()>0){
        listaDatiVisualizzazione = new Vector();
        String desc = null;
        Double valore = null;
        for(int i=0; i<datiTabellato.size();i++){
          desc = SqlManager.getValueFromVectorParam(datiTabellato.get(i), 0).stringValue();
          valore = UtilityNumeri.convertiDouble(desc, UtilityNumeri.FORMATO_DOUBLE_CON_VIRGOLA_DECIMALE);
          listaDatiVisualizzazione.add(((new Object[] {valore,desc})));
        }

      }

      pageContext.setAttribute("listaValoriTabellatoDLGS", listaDatiVisualizzazione, PageContext.REQUEST_SCOPE);
    } catch (SQLException e) {
      throw new JspException("Errore nella lettura del tabellato " + tabellato, e);
    } catch (GestoreException e) {
      throw new JspException("Errore nella lettura del tabellato" + tabellato, e);
    }

    return null;
  }

}
