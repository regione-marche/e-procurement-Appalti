/*
 * Created on 29/mag/2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl.scadenz;

import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.bl.scadenz.AbstractGestorePromemoriaScadenzario;
import org.apache.velocity.VelocityContext;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;


public class GestorePromemoriaScadenzarioBando extends AbstractGestorePromemoriaScadenzario{

  @Override
  public String getModello() {
    return "promemoria-scadenzario-bando.txt";
  }

  @SuppressWarnings("unchecked")
  @Override
  public void popolaContesto(String codapp, Long idAttivita, String ent, Object[] chiavi, VelocityContext velocityContext)
      throws SQLException {
    Vector<JdbcParametro> datiAttivita = this.sqlManager.getVector("select datascad,tit from g_scadenz where id=?", new Object[]{idAttivita});
    String dataScadenza = "";
    String tit="";
    if(datiAttivita!=null && datiAttivita.size()>0){
      Date datascad = (Date) (datiAttivita.get(0)).getValue();
      if(datascad!=null)
        dataScadenza = UtilityDate.convertiData(datascad, UtilityDate.FORMATO_GG_MM_AAAA);
      tit = (String) (datiAttivita.get(1)).getValue();
    }

    //Nel campo chiave[0] è presente sempre il valore di CODGAR
    //Tipologia della gara(lotto unico, offerte distinte, offerta unica)
    String tipologia="";
    Long genere=null;
    String codice=null;
    Long tipgen=null;
    String oggetto=null;
    String tipologiaAppalto="";
    Vector<JdbcParametro> datiGara = this.sqlManager.getVector(
        "select genere,codice,tipgen,oggetto from v_gare_torn where codgar=?", new Object[]{chiavi[0]});
    if(datiGara!=null && datiGara.size()>0){
      genere = (Long) (datiGara.get(0)).getValue();
      codice = (String) (datiGara.get(1)).getValue();
      tipgen = (Long) (datiGara.get(2)).getValue();
      oggetto = (String) (datiGara.get(3)).getValue();
    }

    if(genere!=null){
      switch (genere.intValue()){
        case 1:
          tipologia="Gara divisa in lotti";
          break;
        case 2:
          tipologia="Gara a lotto unico";
          break;
        case 3:
          tipologia="Gara divisa in lotti con offerta in busta unica";
          break;
      }
    }

    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager",
        this.getServletContext(), TabellatiManager.class);

    if(tipgen!=null)
      tipologiaAppalto = tabellatiManager.getDescrTabellato("A1007", tipgen.toString());

    //Caricamento dei dati nel modello velocity
    velocityContext.put("DATASCAD", dataScadenza);
    velocityContext.put("TIT", tit);
    velocityContext.put("TIPOLOGIA", tipologia);
    velocityContext.put("CODICE", codice);
    velocityContext.put("OGGETTO", oggetto);
    velocityContext.put("TIPOLOGIA_APPALTO", tipologiaAppalto);

  }

}
