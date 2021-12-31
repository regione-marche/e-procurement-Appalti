package it.eldasoft.sil.pg.tags.gestori.decoratori;

import java.util.Vector;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampo;


public class GestoreCampoSiNoInvertito extends AbstractGestoreCampo {

  public String gestisciDaTrova(Vector params, DataColumn col, String conf,
      SqlManager manager) {
    
    return null;
  }

  public String getClasseEdit() {
    
    return null;
  }

  public String getClasseVisua() {
   
    return null;
  }

  public String getHTML(boolean visualizzazione, boolean abilitato) {
    
    return null;
  }

  public String getValore(String valore) {
    
    return null;
  }

  public String getValorePerVisualizzazione(String valore) {
    
    return null;
  }

  public String getValorePreUpdateDB(String valore) {
    
    return null;
  }

  protected void initGestore() {
    this.getCampo().setTipo("ET2");
    this.getCampo().getValori().clear();
    this.getCampo().addValore("", "");
    this.getCampo().addValore("1", "No");
    this.getCampo().addValore("2", "Si");
  }

  public String postHTML(boolean visualizzazione, boolean abilitato) {
   
    return null;
  }

  public String preHTML(boolean visualizzazione, boolean abilitato) {
    
    return null;
  }

}
