/*
 * Created on 19/11/2012
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.decoratori;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampo;
import it.eldasoft.gene.tags.decorators.campi.ValoreTabellato;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.jsp.PageContext;

/**
 * Gestore per il campo TIPLAVG di v_cais_tit. In base al valore del campo TIPOELE di GARELABO
 * si modificano i valori da visualizzare
 *
 * @author Marcello Caminiti
 */
public class GestoreCampoTipoCategoria extends AbstractGestoreCampo {

  @Override
  public String getValore(String valore) {

    return null;
  }

  @Override
  public String getValorePerVisualizzazione(String valore) {

    return null;
  }

  @Override
  public String getValorePreUpdateDB(String valore) {

    return null;
  }

  @Override
  public String preHTML(boolean visualizzazione, boolean abilitato) {

    String tipoele = (String) this.getPageContext().getAttribute("tipoele",
        PageContext.PAGE_SCOPE);


    SqlManager sql = (SqlManager) UtilitySpring.getBean("sqlManager",
        this.getPageContext(), SqlManager.class);

    String carattere = Character.toString(tipoele.charAt(0));
    if(!"1".equals(carattere)){
      //Tipo elenco non per lavori, si tolgono le voci dal tabellato
      ValoreTabellato opzionePortale = new ValoreTabellato("1", "Lavori");
      int posizionePortale = this.getCampo().getValori().indexOf(opzionePortale);
      if (posizionePortale >= 0)
        this.getCampo().getValori().remove(posizionePortale);

      //Viene eliminata anche la voce Lavori sotto 150.000 euro
      opzionePortale = new ValoreTabellato("4", "Lavori sotto 150.000 euro");
      posizionePortale = this.getCampo().getValori().indexOf(opzionePortale);
      if (posizionePortale >= 0)
        this.getCampo().getValori().remove(posizionePortale);
    }else{
      //Anche se la voce lavori è visibile si deve controllare se visualizzare la voce Lavori sotto 150.000 euro
      try {
        Long numeroCategorie = (Long) sql.getObject("select count(*) from cais where tiplavg=?",
                      new Object[] {new Long(4)});

        if (numeroCategorie == null || numeroCategorie.longValue()== 0){
          ValoreTabellato opzionePortale = new ValoreTabellato("4", "Lavori sotto 150.000 euro");
          int posizionePortale = this.getCampo().getValori().indexOf(opzionePortale);
          if (posizionePortale >= 0)
            this.getCampo().getValori().remove(posizionePortale);
        }


      } catch (SQLException e) {

      }
    }


    carattere = Character.toString(tipoele.charAt(1));
    if(!"1".equals(carattere)){
      //Tipo elenco non per forniture, si tolgono le voci dal tabellato
      ValoreTabellato opzionePortale = new ValoreTabellato("2", "Forniture");
      int posizionePortale = this.getCampo().getValori().indexOf(opzionePortale);
      if (posizionePortale >= 0)
        this.getCampo().getValori().remove(posizionePortale);
    }

    carattere = Character.toString(tipoele.charAt(2));
    if(!"1".equals(carattere)){
      //Tipo elenco non per servizi, si tolgono le voci dal tabellato
      ValoreTabellato opzionePortale = new ValoreTabellato("3", "Servizi");
      int posizionePortale = this.getCampo().getValori().indexOf(opzionePortale);
      if (posizionePortale >= 0)
        this.getCampo().getValori().remove(posizionePortale);

      opzionePortale = new ValoreTabellato("5", "Servizi professionali");
      posizionePortale = this.getCampo().getValori().indexOf(opzionePortale);
      if (posizionePortale >= 0)
        this.getCampo().getValori().remove(posizionePortale);


    }else{
      try {
        Long numeroCategorie = (Long) sql.getObject("select count(*) from cais where tiplavg=?",
                      new Object[] {new Long(5)});

        if (numeroCategorie == null || numeroCategorie.longValue()==0){
          ValoreTabellato opzionePortale = new ValoreTabellato("5", "Servizi professionali");
          int posizionePortale = this.getCampo().getValori().indexOf(opzionePortale);
          if (posizionePortale >= 0)
            this.getCampo().getValori().remove(posizionePortale);
        }


      } catch (SQLException e) {

      }
    }

    return null;
  }

  @Override
  public String getHTML(boolean visualizzazione, boolean abilitato) {

    return null;
  }

  @Override
  public String postHTML(boolean visualizzazione, boolean abilitato) {

    return null;
  }

  @Override
  public String getClasseEdit() {

    return null;
  }

  @Override
  public String getClasseVisua() {

    return null;
  }

  @Override
  protected void initGestore() {


  }

  @Override
  public String gestisciDaTrova(Vector params, DataColumn col, String conf, SqlManager manager) {

    return null;
  }


}
