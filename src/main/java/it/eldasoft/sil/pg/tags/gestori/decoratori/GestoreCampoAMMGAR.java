package it.eldasoft.sil.pg.tags.gestori.decoratori;

import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampoTabellatoArc;
import it.eldasoft.gene.tags.decorators.campi.ValoreTabellato;

public class GestoreCampoAMMGAR extends AbstractGestoreCampoTabellatoArc {

  public GestoreCampoAMMGAR() {
    super(false, "N7");
  }

  /**
   * @see it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampoTabellato#getSql()
   */
  @Override
  public SqlSelect getSql() {

    String select="select tab1tip,tab1desc,tab1arc from tab1 where tab1cod='A1054' order by tab1nord,tab1tip";

    return new SqlSelect(select, null);
  }

    @Override
    public String preHTML(boolean visualizzazione, boolean abilitato) {
        if(!visualizzazione){
            ValoreTabellato opzione = new ValoreTabellato("11", "");
            int posizione = this.getCampo().getValori().indexOf(opzione);
            if (posizione >= 0)
                this.getCampo().getValori().remove(posizione);
        }
        return null;
    }

}
