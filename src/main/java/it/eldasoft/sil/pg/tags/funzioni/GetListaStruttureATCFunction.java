package it.eldasoft.sil.pg.tags.funzioni;
/*
 * Created on 06/06/18
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */


import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneATCManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
/**
 * Funzione per caricare la lista delle strutture ATC
 *
 * @author Marcello Caminiti
 */
public class GetListaStruttureATCFunction extends AbstractFunzioneTag{
	public GetListaStruttureATCFunction() {
	    super(1, new Class[]{PageContext.class});
	  }

	@Override
  public String function(PageContext pageContext, Object[] params)
    throws JspException {

	    GestioneATCManager gestioneATCManager = (GestioneATCManager) UtilitySpring.getBean(
		      "gestioneATCManager", pageContext, GestioneATCManager.class);
	    try {
	    List ret = new Vector();
	    List<Map<String, String>> strutture = gestioneATCManager.getStrutture();
	    if(strutture!=null && strutture.size()>0){
          String id=null;
          String nome=null;
          for(int i=0;i<strutture.size();i++){
            id = strutture.get(i).get("id");
            nome = strutture.get(i).get("nome_ufficio");
            ret.add(new Object[] {id, nome});
          }
          this.getRequest().setAttribute("listaStruttureATC", ret);
        }
      } catch (GestoreException e) {
        throw new JspException("Errore nella lettura delle strutture ATC ", e);
      }


	    return null;
	}
}
