/*
 * Created on 04/10/10
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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che determina l'ordinamento della lista di
 * selezione delle ditte da elenco operatori economici.
 *
 * @author Marcello Caminiti
 */
public class GetOrdinamentoElencoOpEconomiciFunction extends AbstractFunzioneTag {

  public GetOrdinamentoElencoOpEconomiciFunction() {
    super(4, new Class[] { PageContext.class,String.class,String.class,String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String tipoalogoString = (String) params[1];
    String classifica = (String)params[2];
    String entita = (String)params[3];
    String ordinamento ="13";

	String select = "select tab1desc from tab1 where tab1cod='A1073' and tab1tip=?";

	SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

	try {
      if(tipoalogoString!=null && !"".equals(tipoalogoString)){
	    Long tipoalgo = Long.parseLong(tipoalogoString);
	    int tipoAlgoritmo = tipoalgo.intValue();
	    if("V_DITTE_ELESUM".equals(entita))
          ordinamento ="12";   //NUMORD

        if(tipoAlgoritmo==1 || (tipoAlgoritmo==5 && (classifica==null ||"".equals(classifica)))){
          if("V_DITTE_ELESUM".equals(entita))
            ordinamento="8;15";   //NUMIR;NUMORD
          else
            ordinamento="8;29"; //NUMIR;NUMORD
        }else if(tipoAlgoritmo==2){
          if("V_DITTE_ELESUM".equals(entita))
            ordinamento="15"; //NUMORD
          else
            ordinamento="29"; //NUMORD
        }else if(tipoAlgoritmo==3 || (tipoAlgoritmo==4 && (classifica==null ||"".equals(classifica)))){
          if("V_DITTE_ELESUM".equals(entita))
            ordinamento="9;15";  //NUMIMP;NUMORD
          else
            ordinamento="9;29"; //NUMIMP;NUMORD
        }else if(tipoAlgoritmo==4 && classifica!=null && !"".equals(classifica)){
          ordinamento="16;29";  //NUMIPCLA;NUMORD
        }else if(tipoAlgoritmo==5 && classifica!=null && !"".equals(classifica)){
          ordinamento="15;29"; //NUMIRCLA;NUMORD
        }else if(tipoAlgoritmo==6){
          if("V_DITTE_ELESUM".equals(entita))
            ordinamento="9;15";  //NUMIMP;NUMORD
          else
            ordinamento="23;29"; //NUMIPTOT;NUMORD
        }else if(tipoAlgoritmo==7){
          if("V_DITTE_ELESUM".equals(entita))
            ordinamento="8;15";   //NUMIR;NUMORD
          else
            ordinamento="22;29";  //NUMIRTOT;NUMORD
        }else if(tipoAlgoritmo==8){
          ordinamento="9;14";  //NUMIPTOT; NUMORD
        }else if(tipoAlgoritmo==9){
          ordinamento="8;14";  //NUMIRTOT; NUMORD
        }else if(tipoAlgoritmo==10){
          if("V_DITTE_ELESUM".equals(entita))
            ordinamento="10;15"; //NUMAP, NUMORD
          else
            ordinamento="24;29"; //NUMAPTOT, NUMORD
        }else if(tipoAlgoritmo==11 || (tipoAlgoritmo==12 && (classifica==null ||"".equals(classifica)))){
          ordinamento="10;29"; //NUMAP, NUMORD
        }else if(tipoAlgoritmo==12 && classifica!=null && !"".equals(classifica)){
          ordinamento="17;29"; //NUMAPCLA
        }else if(tipoAlgoritmo==13){
          if("V_DITTE_ELESUM".equals(entita))
            ordinamento="10;11;15"; //NUMAP,NUMINV,NUMORD
          else
            ordinamento="24;25;29"; //NUMAPTOT,NUMINVTOT,NUMORD
        }else if(tipoAlgoritmo==14 || (tipoAlgoritmo==15 && (classifica==null ||"".equals(classifica)))){
          ordinamento="10;11";  //NUMAP, NUMINV, NUMORD
          if("V_DITTE_ELESUM".equals(entita))
            ordinamento+=";15";
          else
            ordinamento+=";29";
        }else if(tipoAlgoritmo==15 && classifica!=null && !"".equals(classifica)){
          ordinamento="17;18"; //NUMAPCLA,NUMINVCLA, NUMORD
          if("V_DITTE_ELESUM".equals(entita))
            ordinamento+=";15";
          else
            ordinamento+=";29";
        }

        String tipoalgoDesc = (String) sqlManager.getObject(select, new Object[]{tipoalgo});
        pageContext.setAttribute("criterioRotazioneDesc", tipoalgoDesc, PageContext.REQUEST_SCOPE);
	  }


    } catch (SQLException e) {
      throw new JspException(
          "Errore durante la lettura del criterio di rotazione dell'operatore economico ", e);
    }


    return ordinamento;
  }

}
