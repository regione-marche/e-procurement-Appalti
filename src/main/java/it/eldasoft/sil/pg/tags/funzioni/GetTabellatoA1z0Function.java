/*
 * Created on 07-02-2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

/**
 * Vengono estratti da tab2 i valori per i tabellati del tipo A1z0
 *
 * @author Marcello Caminiti
 */
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class GetTabellatoA1z0Function extends AbstractFunzioneTag {

  public GetTabellatoA1z0Function() {
    super(2, new Class[]{PageContext.class, String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    pageContext = (PageContext) params[0];
    String tabellato = (String)params[1];

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    try {
      List listaValoriA1z0 = sqlManager.getListVector("select tab2tip,tab2d2 from tab2" +
      		" where tab2cod=? order by tab2tip", new Object[]{tabellato});
      if(listaValoriA1z0!=null && listaValoriA1z0.size()>0){
        List listaValoriEsplicitata = new Vector();
        for(int i=0;i<listaValoriA1z0.size();i++){
          String tab2tip = SqlManager.getValueFromVectorParam(listaValoriA1z0.get(i), 0).getStringValue();
          String desc = SqlManager.getValueFromVectorParam(listaValoriA1z0.get(i), 1).getStringValue();
          if(desc.indexOf(",")>0){
            String vettValori[] = desc.split(",");
            for(int j=0;j<vettValori.length;j++){
              Vector vet = new Vector();
              String desc1 = vettValori[j];
              vet.add(tab2tip);
              vet.add(desc1);
              listaValoriEsplicitata.add(vet);
            }
          }else{
            Vector vet = new Vector();
            vet.add(tab2tip);
            vet.add(desc);
            listaValoriEsplicitata.add(vet);
          }

        }
        pageContext.setAttribute("listaValori" + tabellato , listaValoriEsplicitata, PageContext.REQUEST_SCOPE);
      }

    } catch (SQLException s){
      throw new JspException("Errore durante la lettura del tabellato " + tabellato, s);
    }
    return "";
  }

}