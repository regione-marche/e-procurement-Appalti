/*
 * Created on 10/10/11
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
import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampoTabellatoArc;
import it.eldasoft.gene.tags.decorators.campi.ValoreTabellato;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;

/**
 * Gestore del campo tipo TIPGAR. Questo gestore elimina dai valori del tabellato
 * "A2044" associato al campo, i valori che non sono presenti nel tabellato "A1z03"
 *
 * @author Marcello Caminiti
 */
public class GestoreCampoTIPGAR extends AbstractGestoreCampoTabellatoArc {

  public GestoreCampoTIPGAR() {
    super(false, "N7");
  }


  @Override
  public SqlSelect getSql() {
    String profiloAttivo = (String) this.getPageContext().getSession().getAttribute("profiloAttivo");
    PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        this.getPageContext(), PgManager.class);

    String select = "select tab1tip,tab1desc,tab1arc from tab1 where tab1cod='A2044' order by tab1nord,tab1tip";

    try {
      String descTab = pgManager.getFiltroTipoGara(profiloAttivo);
      if (descTab!=null){
        select = "select tab1tip,tab1desc,tab1arc from tab1 where tab1cod='A2044' and tab1tip in (" + descTab + ") order by tab1nord,tab1tip";

      }
    } catch (GestoreException e) {

    }

    return new SqlSelect(select, null);
  }

  @Override
  public String preHTML(boolean visualizzazione, boolean abilitato) {
    if(!visualizzazione){
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
          this.getPageContext(), SqlManager.class);

      String garaLottoUnico = null;
      if(this.getPageContext().getAttribute("garaLottoUnico")!=null){
        garaLottoUnico = (String)this.getPageContext().getAttribute("garaLottoUnico");
      }
      try {
        if(!"true".equals(garaLottoUnico)){
          List datiTab2 = sqlManager.getListVector("select tab2d2 from tab2 where tab2cod=? and (tab2tip=? or tab2tip=?) ", new Object[]{"A1z05",new Long(76),new Long(77)});
          if(datiTab2!=null && datiTab2.size()>0){
            for (int i = 0; i < datiTab2.size(); i++) {
              String desc = SqlManager.getValueFromVectorParam(datiTab2.get(i), 0).getStringValue();
              if(desc!=null && !"".equals(desc)){
                if(desc.indexOf(",")>0){
                  String vettValori[] = desc.split(",");
                  for(int j=0;j<vettValori.length;j++){
                    String desc1 = vettValori[j];
                    rimozioneValore(desc1);
                  }
                }else{
                  rimozioneValore(desc);
                }
              }
            }
          }
        }

      } catch (SQLException e) {

      }
    }
    return null;
  }

  private void rimozioneValore(String valore){
    ValoreTabellato opzionePortale = new ValoreTabellato(valore, "");
    int posizionePortale = this.getCampo().getValori().indexOf(opzionePortale);
    if (posizionePortale >= 0)
      this.getCampo().getValori().remove(posizionePortale);
  }

}
