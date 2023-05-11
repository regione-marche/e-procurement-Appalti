package it.eldasoft.sil.pg.tags.funzioni;
/*
 * Created on 28/10/10
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */


import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.tags.functions.GeneralTagsFunction;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
/**
 * Funzione per caricare il numero di richieste di iscrizione
 * ad elenco operatori economici
 *
 * @author Marcello Caminiti
 */
public class GetNumeroComunicazioniFunction extends AbstractFunzioneTag{
	public GetNumeroComunicazioniFunction() {
	    super(3, new Class[]{PageContext.class, String.class, String.class});
	  }

	@Override
  public String function(PageContext pageContext, Object[] params)
    throws JspException {


		  SqlManager sqlManager = (SqlManager) UtilitySpring.getBean(
		      "sqlManager", pageContext, SqlManager.class);

		  String msg1="Non ci sono richieste di iscrizione ad elenco operatori economici nè richieste di aggiornamento";
          String msg2="Non ci sono richieste di registrazione da portale";
          String msg3="Non ci sono richieste di aggiornamento anagrafica da portale";

          ProfiloUtente Utente = (ProfiloUtente)pageContext.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
          String abilitazioneGare = Utente.getAbilitazioneGare();
          int id = Utente.getId();

          String dbFunctionSubStringCodgar = sqlManager.getDBFunction("substr",
              new String[] { "codgar", "2", "21" });

          String entita= "V_GARE_ELEDITTE";
          if (UtilityTags.checkProtection(pageContext, "FUNZ.VIS.ALT.GARE.V_GARE_CATALDITTE-lista.ApriGare",true)){
            entita="V_GARE_CATALDITTE";
            msg1="Non ci sono richieste di iscrizione a catalogo elettronico nè richieste di aggiornamento";
          }
          
          String codUffint = (String)GeneralTagsFunction.cast("string", params[2]);
          
          try {
            //Conteggio numero richieste di iscrizione ad elenco o a catalogo e di aggiornamento elenco o anagrafica
            String select=null;
            select="select count(idprg) from W_INVCOM," + entita + " where idprg = ? and comstato = ? and comtipo = ? and codice=comkey2";

			if(!"A".equals(abilitazioneGare)){
			  select="select count(idprg) from W_INVCOM," + entita + " where idprg = ? and comstato = ? and comtipo = ? and codice=comkey2 and exists (select codgar from g_permessi where syscon=" + id +" and autori=1 and comkey2 = " + dbFunctionSubStringCodgar + ")";
            }
	        if(codUffint!=null && !"".equals(codUffint))
	           select+= " and exists (select codgar from torn where codgar = " + entita + ".codgar and cenint = '" + codUffint + "')";
	          
			Long numRichiesteIscrizione = (Long)sqlManager.getObject(select,
                new Object[]{"PA","5","FS2"});

			
            String selectSet="select count(idprg) from W_INVCOM where idprg = ? and comstato = ? and (comtipo in (?, ?) or (comtipo = ? and exists (select codice from " + entita + " where codice=comkey2)))";

            if(!"A".equals(abilitazioneGare)){
              selectSet="select count(idprg) from W_INVCOM where idprg = ? and comstato = ? and (comtipo in (?, ?) or " +
                "(comtipo = ? and exists (select codice from " + entita + " where codice=comkey2) and exists (select codgar from g_permessi where syscon=" + id +" and autori=1 and comkey2 = " + dbFunctionSubStringCodgar + ")))";
            }
            if(codUffint!=null && !"".equals(codUffint))
             selectSet+= " and (comtipo in ('FS5', 'FS6') or (comtipo = 'FS4' and exists (select torn.codgar from torn," + entita +" where torn.codgar = " + entita + ".codgar and " + entita + ".codice = W_INVCOM.comkey2 and torn.cenint = '" + codUffint + "')))";
           
            Long numRichiesteAggiornamento = (Long)sqlManager.getObject(selectSet,
                new Object[]{"PA","5", "FS5", "FS6", "FS4"});


            if(numRichiesteIscrizione!=null && numRichiesteIscrizione.longValue()>0)
              if(numRichiesteAggiornamento!=null && numRichiesteAggiornamento.longValue()>0) {
                //msg1="Ci sono " + numRichiesteIscrizione.toString() + " richieste di iscrizione ad elenco operatori economici e " +
                msg1="Ci sono " + numRichiesteIscrizione.toString() + " richieste di iscrizione ";
                if (UtilityTags.checkProtection(pageContext, "FUNZ.VIS.ALT.GARE.V_GARE_CATALDITTE-lista.ApriGare",true))
                  msg1+="a catalogo elettronico e ";
                else
                  msg1+="ad elenco operatori economici e ";

                msg1+=numRichiesteAggiornamento.toString() + " richieste di aggiornamento";
              } else {
                //msg1="Ci sono " + numRichiesteIscrizione.toString() + " richieste di iscrizione ad elenco operatori economici";
                msg1="Ci sono " + numRichiesteIscrizione.toString() + " richieste di iscrizione ";
                if (UtilityTags.checkProtection(pageContext, "FUNZ.VIS.ALT.GARE.V_GARE_CATALDITTE-lista.ApriGare",true))
                  msg1+="a catalogo elettronico";
                else
                  msg1+="ad elenco operatori economici";

  		      }
		    else if (numRichiesteIscrizione == null || numRichiesteIscrizione.longValue()==0)
		      if (numRichiesteAggiornamento != null && numRichiesteAggiornamento.longValue()>0) {
    		      msg1="Ci sono " + numRichiesteAggiornamento.toString() + " richieste di aggiornamento ";
    		      if (UtilityTags.checkProtection(pageContext, "FUNZ.VIS.ALT.GARE.V_GARE_CATALDITTE-lista.ApriGare",true))
                    msg1+="a catalogo elettronico";
                  else
                    msg1+="ad elenco operatori economici";
    	      }
		  } catch (SQLException e) {
		    throw new JspException("Errore nella determinazione delle richieste di iscrizione a elenco e di aggiornamento", e);
		  }

		  //Conteggio numero richieste di registrazione anagrafica
          try {
            String select="select count(idprg) from W_INVCOM where idprg = ? and comstato = ? and comtipo = ?";
            Long numRichiesteRegistrazione = (Long)sqlManager.getObject(select,
                new Object[]{"PA","5","FS1"});


            if(numRichiesteRegistrazione!=null && numRichiesteRegistrazione.longValue()>0){
              msg2="Ci sono " + numRichiesteRegistrazione.toString() + " richieste di registrazione da portale";
            }
          } catch (SQLException e) {
            throw new JspException("Errore nella determinazione delle richieste di registrazione ", e);
          }

          //Conteggio numero richieste di aggiornamento anagrafica
          try {
            String select="select count(idprg) from W_INVCOM where idprg = ? and comstato = ? and (comtipo = ? or comtipo = ?)";
            Long numRichiesteAggiornamento = (Long)sqlManager.getObject(select,
                new Object[]{"PA","5","FS5","FS6"});

            if(numRichiesteAggiornamento!=null && numRichiesteAggiornamento.longValue()>0){
              msg3="Ci sono " + numRichiesteAggiornamento.toString() + " richieste di aggiornamento anagrafica da portale";
            }
          } catch (SQLException e) {
            throw new JspException("Errore nella determinazione delle richieste di aggiornamento anagrafica da portale ", e);
          }

          return msg1 + "#" + msg2 + "#" + msg3;
		}
}
