package it.eldasoft.sil.w3.tags.gestori.decoratori;

import it.eldasoft.gene.web.struts.tags.gestori.decoratori.GestoreCampoTabellatoGen;

/**
 * Filtro per il tabellato W3031 delle categorie merceologiche diversificato tre richiesta CIG e smartCIG
 * per smartCIG si sono 6 valori in più
 * 
 * @author Mirco.Franzoni
 */
public class GestoreCategorieMerceologiche extends GestoreCampoTabellatoGen {

  /** Query per GategoriaMerceologica. */
  private final String queryTabellatoGategoriaMerceologica = "Select TAB1TIP, TAB1DESC from TAB1 Where TAB1COD = 'W3031' and TAB1NORD IS NULL Order by TAB1TIP";
  
  /**
   * Costruttore.
   */
  public GestoreCategorieMerceologiche() {
    super(false, "N7");
  }

  @Override
  public SqlSelect getSql() {
    try {
      
      return new SqlSelect(queryTabellatoGategoriaMerceologica, new Object[] {});
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

}
