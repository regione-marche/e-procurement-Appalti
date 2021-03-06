/*
 * Created on 02/10/20
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
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class ControlliCreaFascicoloFunction extends AbstractFunzioneTag {

	public ControlliCreaFascicoloFunction() {
		super(4, new Class[] { PageContext.class, String.class, String.class , String.class  });
	}

	@Override
  public String function(PageContext pageContext, Object[] params)
			throws JspException {

	    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
	            pageContext, SqlManager.class);

		String controlloSuperato = "SI";
		String msgErrori="<b>Non ? possibile procedere con la creazione del fascicolo documentale.</b><br>";
	    String ngara = new String((String) params[1]);
		String codgar = new String((String) params[2]);
		String genere = new String((String) params[3]);
		String select="select cenint, nomein, codrup, cogtei, nometei,destor from torn left join uffint on  cenint=codein  left join tecni on  codrup = codtec where codgar=?";
		String cenint = null;
		String nomein = null;
		String codrup = null;
		String cogtei = null;
		String nometei = null;
		String oggetto = null;
		try {
		  Vector datiTorn = sqlManager.getVector(select, new Object[]{codgar});


		  if(datiTorn!=null && datiTorn.size()>0){
		    cenint = SqlManager.getValueFromVectorParam(datiTorn, 0).getStringValue();
		    nomein = SqlManager.getValueFromVectorParam(datiTorn, 1).getStringValue();
		    codrup = SqlManager.getValueFromVectorParam(datiTorn, 2).getStringValue();
		    cogtei = SqlManager.getValueFromVectorParam(datiTorn, 3).getStringValue();
		    nometei = SqlManager.getValueFromVectorParam(datiTorn, 4).getStringValue();
		    oggetto = SqlManager.getValueFromVectorParam(datiTorn, 5).getStringValue();

		  }

		  String messaggioDet = " della gara.";
		  if( "2".equals(genere)){
		    select ="select not_gar from gare where ngara=?";
		  }else if( "10".equals(genere) || "20".equals(genere)){
		    select ="select oggetto from garealbo where ngara=?";
		    messaggioDet = " dell'elenco.";
	          if("20".equals(genere))
	            messaggioDet = " del catalogo.";
		  }else if( "11".equals(genere)){
            select ="select oggetto from gareavvisi where ngara=?";
            messaggioDet = " dell'avviso.";
          }
		  if( !"1".equals(genere) && !"3".equals(genere))
		    oggetto = (String)sqlManager.getObject(select, new Object[]{ngara});


		  if(cenint == null || "".equals(cenint)){
		    controlloSuperato = "NO";
		    msgErrori+="<br>Non ? stata inserita la stazione appaltante.";
		  } else {
    		  if(nomein == null || "".equals(nomein)){
    		    controlloSuperato = "NO";
                msgErrori+="<br>La denominazione della stazione appaltante non e' valorizzata.";
              }
		  }
		  if (codrup == null || "".equals(codrup)) {
		    controlloSuperato = "NO";
            if ( "10".equals(genere))
              msgErrori += "<br>Non ? stato inserito il responsabile dell'elenco.";
            else if ("20".equals(genere))
              msgErrori += "<br>Non ? stato inserito il responsabile del catalogo.";
            else
              msgErrori += "<br>Non ? stato inserito il responsabile unico procedimento.";
          }else{
            if(cogtei == null || "".equals(cogtei)){
  		    controlloSuperato = "NO";
              msgErrori+="<br>Il cognome del responsabile unico procedimento non e' valorizzato.";
            }
  		  if(nometei == null || "".equals(nometei)){
  		    controlloSuperato = "NO";
              msgErrori+="<br>Il nome del responsabile unico procedimento non e' valorizzato.";
            }
          }
		  if(oggetto == null || "".equals(oggetto)){
		    controlloSuperato = "NO";
            msgErrori+="<br>Non ? stata inserito l'oggetto " + messaggioDet;
          }

		  if("NO".equals(controlloSuperato))
		    pageContext.setAttribute("msg", msgErrori, PageContext.REQUEST_SCOPE);

		} catch (SQLException e) {
			throw new JspException("Errore nei controlli preliminari della funzione Crea fascicolo",e);
		}

	    return controlloSuperato ;

	}

}
