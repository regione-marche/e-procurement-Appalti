/*
 * Created on 22/08/2014
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.decoratori;

import java.util.HashMap;
import java.util.Vector;

import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampo;
import it.eldasoft.gene.tags.decorators.campi.ValoreTabellato;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Gestore per il campo DOCUMGARA.BUSTA
 *
 * @author Marcello Caminiti
 */
public class GestoreCampoBusta extends AbstractGestoreCampo {

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

    SqlManager sql = (SqlManager) UtilitySpring.getBean("sqlManager",
        this.getPageContext(), SqlManager.class);

    HashMap datiRiga = (HashMap) this.getPageContext().getAttribute("datiRiga",
        PageContext.REQUEST_SCOPE);

    //String iterga = datiRiga.get("TORN_ITERGA").toString();
    String iterga = null;
    boolean fasiRicezione = false;
    if(datiRiga.get("TORN_ITERGA")!=null)
      iterga = datiRiga.get("TORN_ITERGA").toString();
    if(iterga == null || "".equals(iterga))
      iterga = (String) this.getPageContext().getAttribute("iterga",
          PageContext.PAGE_SCOPE);

    String fase = (String) this.getPageContext().getAttribute("step",
        PageContext.PAGE_SCOPE);
    if(datiRiga.get("WIZARD_PAGINA_ATTIVA")!=null || "FaseRicezione".equals(fase))
      fasiRicezione=true;


    if(fasiRicezione || (!"".equals(iterga) && !"2".equals(iterga) && !"4".equals(iterga) && !"7".equals(iterga))){
      ValoreTabellato opzione = new ValoreTabellato("4", "Prequalifica");
      int posizioneOpzione = this.getCampo().getValori().indexOf(opzione);
      if (posizioneOpzione >= 0)
        this.getCampo().getValori().remove(posizioneOpzione);
    }

    String modlic= null;
    if(datiRiga.get("GARE_MODLICG")!=null)
      modlic = datiRiga.get("GARE_MODLICG").toString();
    else  if(datiRiga.get("TORN_MODLIC")!=null)
      modlic = datiRiga.get("TORN_MODLIC").toString();

    String valtec = null;
    if(datiRiga.get("GARE1_VALTEC")!=null)
      valtec = datiRiga.get("GARE1_VALTEC").toString();
    else  if(datiRiga.get("TORN_VALTEC")!=null)
      valtec = datiRiga.get("TORN_VALTEC").toString();
    else
      valtec = (String) this.getPageContext().getAttribute("valtec",
          PageContext.PAGE_SCOPE);


    if(modlic == null || "".equals(modlic))
      modlic = (String) this.getPageContext().getAttribute("modlic",
          PageContext.PAGE_SCOPE);

    if((modlic != null && !"".equals(modlic) && !"6".equals(modlic))
         && !"1".equals(valtec)){
      ValoreTabellato opzione = new ValoreTabellato("2", "Offerta tecnica");
      int posizioneOpzione = this.getCampo().getValori().indexOf(opzione);
      if (posizioneOpzione >= 0)
        this.getCampo().getValori().remove(posizioneOpzione);
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
