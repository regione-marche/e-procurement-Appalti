/*
 * Created on 01/07/24
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
import it.eldasoft.utils.spring.UtilitySpring;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.util.*;

/**
 * Gestore del campo PERI_TIPLAV associato al tabellato A20s2.
 * Vengono rimossi dai possibili valori quelli a cui non è associato un profilo su LFS (valore tab2d2 nel tab. A9z99)
 * @author Manuel Bridda
 */
public class GestoreCampoTIPLAV extends AbstractGestoreCampoTabellatoArc {

  public GestoreCampoTIPLAV() {
    super(false, "N7");
  }

  /**
   * @see it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampoTabellato#getSql()
   */
  @Override
  public SqlSelect getSql() {

    String select="select tab1tip,tab1desc,tab1arc from tab1 where tab1cod='A20s2' order by tab1nord,tab1tip";

    return new SqlSelect(select, null);
  }

  @Override
  public String preHTML(boolean visualizzazione, boolean abilitato) {
    if(!visualizzazione){
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
              this.getPageContext(), SqlManager.class);

      Set<String> valoriMappati = new HashSet<>();

      try {
          List datiTab2 = sqlManager.getListVector("select tab2d2 from tab2 where tab2cod=? ", new Object[]{"A9z99"});
          if(datiTab2!=null && datiTab2.size()>0){
            for (int i = 0; i < datiTab2.size(); i++) {
              String desc = SqlManager.getValueFromVectorParam(datiTab2.get(i), 0).getStringValue();
              if(desc!=null && !"".equals(desc)){
                if(desc.indexOf(",")>0) {
                  String vettValori[] = desc.split(",");
                  valoriMappati.addAll(Arrays.asList(vettValori));
                }else {
                  valoriMappati.add(desc);
                }
              }
            }
          }
          int index=0;
          while(index<this.getCampo().getValori().size()){
           String valore = ((ValoreTabellato) this.getCampo().getValori().get(index)).getValore();
            if(valoriMappati.contains(valore) || StringUtils.isBlank(valore)){
              index++;
            }else{
              this.getCampo().getValori().remove(index);
            }
          }
      } catch (SQLException e) {

      }
    }
    return null;
  }
}
