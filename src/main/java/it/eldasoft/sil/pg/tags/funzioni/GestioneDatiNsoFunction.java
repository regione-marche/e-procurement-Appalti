/*
 * Created on 07/04/2020
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
import it.eldasoft.utils.utility.UtilityMath;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che recupera i dati di collegamento per gli ordini NSO
 *
 * @author Cristian.Febas
 */
public class GestioneDatiNsoFunction extends AbstractFunzioneTag {

  public GestioneDatiNsoFunction() {
    super(2, new Class[] { PageContext.class,String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String idOrdine = (String) params[1];
    idOrdine = UtilityStringhe.convertiNullInStringaVuota(idOrdine);
    if(!"".equals(idOrdine)){
      Long idO = new Long(idOrdine);

      String selectOrdine = "select a.stato_ordine,b.codord,a.id_originario,a.codord,a.versione" +
      		" from nso_ordini a" +
      		" left join nso_ordini b on a.id_padre=b.id" +
      		" where a.id = ?";

      String selectLavForn = "select count(*) from v_gcap_dpre,nso_ordini" +
      " where nso_ordini.id = ? and nso_ordini.ngara = v_gcap_dpre.ngara ";


      String selectGaraLotto = "select g.codgar1,g.ngara,g.ditta" +
      		" from nso_ordini o" +
      		" left join gare g on o.ngara = g.ngara" +
      		" where o.id = ? ";



      try {

        Vector<?> datiOrdine = sqlManager.getVector(selectOrdine,new Object[] { new Long(idO) });
        if (datiOrdine != null && datiOrdine.size() > 0){
          Long statoOrdine = SqlManager.getValueFromVectorParam(datiOrdine, 0).longValue();
          pageContext.setAttribute("statoOrdine",statoOrdine,PageContext.REQUEST_SCOPE);
          String codiceOrdineCollegato = SqlManager.getValueFromVectorParam(datiOrdine, 1).stringValue();
          pageContext.setAttribute("codiceOrdineCollegato",codiceOrdineCollegato,PageContext.REQUEST_SCOPE);
          Long idOriginarioOrdine = SqlManager.getValueFromVectorParam(datiOrdine, 2).longValue();
          pageContext.setAttribute("idOriginarioOrdine",idOriginarioOrdine,PageContext.REQUEST_SCOPE);
          String codiceOrdine = SqlManager.getValueFromVectorParam(datiOrdine, 3).stringValue();
          pageContext.setAttribute("codiceOrdine",codiceOrdine,PageContext.REQUEST_SCOPE);
          Long versioneOrdine = SqlManager.getValueFromVectorParam(datiOrdine, 4).longValue();
          pageContext.setAttribute("versioneOrdine",versioneOrdine,PageContext.REQUEST_SCOPE);

        }



        Vector<?> datiLavForn = sqlManager.getVector(selectLavForn,new Object[] { new Long(idO) });
        if (datiLavForn != null && datiLavForn.size() > 0){
          Long countGcap = SqlManager.getValueFromVectorParam(datiLavForn, 0).longValue();
          String isMonoRiga = "";
          if(new Long(0).equals(countGcap)){
            isMonoRiga = "1";
          }else{
            isMonoRiga = "0";
          }
          pageContext.setAttribute("isMonoRiga",isMonoRiga,PageContext.REQUEST_SCOPE);
        }

        Vector<?> datiGaraLotto = sqlManager.getVector(selectGaraLotto,new Object[] { new Long(idO) });
        if (datiGaraLotto != null && datiGaraLotto.size() > 0){
          String codgar = SqlManager.getValueFromVectorParam(datiGaraLotto, 0).stringValue();
          String ngara = SqlManager.getValueFromVectorParam(datiGaraLotto, 1).stringValue();
          String ditta = SqlManager.getValueFromVectorParam(datiGaraLotto, 2).stringValue();
          pageContext.setAttribute("codiceGara",codgar,PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("numeroGara",ngara,PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("codiceDitta",ditta,PageContext.REQUEST_SCOPE);
        }

        //Calcolo importi di riepilogo
        //LineExtensionAmount importo ordinato
        Double impOrdinato = null;
        String selectImportoOrdinato = "select sum(coalesce(quantita,0)* coalesce(prezzo_unitario,0)) from nso_linee_ordini where nso_ordini_id = ?" ;
        Object objImportoOrdinato = sqlManager.getObject(selectImportoOrdinato,new Object[] { new Long(idO) });
        if (objImportoOrdinato instanceof Long){
          impOrdinato = ((Long) objImportoOrdinato).doubleValue();
        }else{
          if(objImportoOrdinato instanceof Double){
            impOrdinato = (Double) objImportoOrdinato;
          }
        }
        if(impOrdinato!=null){
          //totale netto
          impOrdinato = UtilityMath.round(impOrdinato,2);
          pageContext.setAttribute("impOrdinato",impOrdinato,PageContext.REQUEST_SCOPE);
        }

        // Somma imposte per iva
        Double impIva = null;
        String selectImportoIva = "select sum(coalesce(quantita,0)* coalesce(prezzo_unitario,0)*coalesce(iva/100,0))" +
                " from nso_linee_ordini where nso_ordini_id = ?" ;
        Object objImportoIva = sqlManager.getObject(selectImportoIva,new Object[] { new Long(idO) });
        if (objImportoIva instanceof Long){
          impIva = ((Long) objImportoIva).doubleValue();
        }else{
          if(objImportoIva instanceof Double){
            impIva = (Double) objImportoIva;
          }
        }
        if(impIva!=null){
          //totale imposte per iva
          impIva = UtilityMath.round(impIva,2);
          pageContext.setAttribute("impIva",impIva,PageContext.REQUEST_SCOPE);
        }

        // Somma importi lordo iva
        Double impTotale = null;
        String selectImportoTotale = "select sum(coalesce(quantita,0)* coalesce(prezzo_unitario,0)*(1+1*coalesce(iva/100,0)))" +
        		" from nso_linee_ordini where nso_ordini_id = ?" ;
        Object objImportoTotale = sqlManager.getObject(selectImportoTotale,new Object[] { new Long(idO) });
        if (objImportoTotale instanceof Long){
          impTotale = ((Long) objImportoTotale).doubleValue();
        }else{
          if(objImportoTotale instanceof Double){
            impTotale = (Double) objImportoTotale;
          }
        }
        if(impTotale!=null){
          //totale lordo iva
          impTotale = UtilityMath.round(impTotale,2);
          pageContext.setAttribute("impTotale",impTotale,PageContext.REQUEST_SCOPE);
        }



      } catch (SQLException e) {
        throw new JspException("Errore nell'estrarre i dati della gara/lotto collegata all'ordine", e);
      } catch (GestoreException ge) {
        throw new JspException("Errore nell'estrarre i dati della gara/lotto collegata all'ordine", ge);
      }

    }

    return null;
  }

}
