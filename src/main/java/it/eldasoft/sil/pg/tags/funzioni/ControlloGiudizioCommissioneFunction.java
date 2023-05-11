/*
 * Created on 10/07/17
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

public class ControlloGiudizioCommissioneFunction extends AbstractFunzioneTag {

  public ControlloGiudizioCommissioneFunction() {
    super(5, new Class[] { PageContext.class, String.class, String.class, String.class, String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    String ngara = (String) params[1];
    String tipo = (String) params[2];
    String codgar = (String) params[3];
    String sezTec = (String) params[4];
    String esitoControllo = "ok";
    String ditta = null;

    if (ngara != null && tipo != null && !"".equals(tipo)) {
      Long tipoCriterio = new Long(tipo);
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
      Long fasgar = null;
      if ("1".equals(tipo)) {
        fasgar = new Long(5);
      } else {
        fasgar = new Long(6);
      }

      String selectDitte = "select d.nomimo from ditg d, g1cridef c, goev g where d.ngara5=? and c.ngara=d.ngara5 and g.ngara = c.ngara and g.tippar = ? "
          + "and g.necvan = c.necvan and c.maxpun >0 and (modpunti = 1 or modpunti = 3) and (d.fasgar is null or d.fasgar > ?)";
      if (!"".equals(sezTec))
        selectDitte += " and g.seztec = 1 ";
      selectDitte += " and not exists (select v.id from g1crival v where v.idcridef=c.id and v.ngara=d.ngara5 and v.dittao=d.dittao)";

      String select = "select l.dittao from gfof f, g1crival l,g1cridef c, goev g, ditg  where f.espgiu = '1' and l.ngara = ? and f.ngara2 = ? "
          + " and l.coeffi is null and c.id=l.idcridef and g.ngara = c.ngara and g.necvan = c.necvan and g.tippar = ? and (modpunti = 1 or modpunti = 3) and c.maxpun>0 "
          + " and ditg.dittao=l.dittao and l.ngara=ditg.ngara5 and (ditg.fasgar is null or ditg.fasgar > ?) ";
      if (!"".equals(sezTec))
        select += " and g.seztec = 1 ";
      select += "and not exists (select * from g1crivalcom g1 where g1.idgfof  = f.id and g1.idcrival = l.id and g1.idcridef = c.id and coeffi is not null )";

      String selectValidita = "select l.dittao from gfof f, g1crival l,g1cridef c, goev g, ditg  where f.espgiu = '1' and l.ngara = ? and f.ngara2 = ? "
          + " and l.coeffi is null and c.id=l.idcridef and g.ngara = c.ngara and g.necvan = c.necvan and g.tippar = ? and (modpunti = 1 or modpunti = 3) and c.maxpun>0 "
          + " and ditg.dittao=l.dittao and l.ngara=ditg.ngara5 and (ditg.fasgar is null or ditg.fasgar > ?) ";
      if (!"".equals(sezTec))
        selectValidita += " and g.seztec = 1 ";
      selectValidita += "and exists (select * from g1crivalcom g1 where g1.idgfof  = f.id and g1.idcrival = l.id and g1.idcridef = c.id and (coeffi > 1 or coeffi < 0) )";


      try {
        String dittaGiudizioNonEspresso =  (String) sqlManager.getObject(selectDitte, new Object[] {ngara, tipoCriterio, fasgar});
        if (dittaGiudizioNonEspresso != null && !"".equals(dittaGiudizioNonEspresso)) {
          esitoControllo = "ko";
          ditta = dittaGiudizioNonEspresso;
        } else {
          String codice = ngara;
          Long genere = (Long)sqlManager.getObject("select genere from v_gare_genere where codgar=?", new Object[]{codgar});
          if (genere != null && genere.intValue() == 3) {
            codice = codgar;
          }
          //Si controlla che nel caso di dettaglio manuale devono essere valorizzati coefficente e punteggio(basta controllare il punteggio)
          dittaGiudizioNonEspresso = (String) sqlManager.getObject(select, new Object[] {ngara, codice, tipoCriterio, fasgar});
          if (dittaGiudizioNonEspresso != null && !"".equals(dittaGiudizioNonEspresso)) {
            esitoControllo = "ko";
            String dittao = dittaGiudizioNonEspresso;
            ditta = getNomeDitta(sqlManager, dittao);
          } else {
            String dittaGiudizioNonValido = (String) sqlManager.getObject(selectValidita, new Object[] {ngara, codice, tipoCriterio, fasgar});
            if(dittaGiudizioNonValido != null && !"".equals(dittaGiudizioNonValido)){
              esitoControllo = "ko-nonvalido";
              String dittao = dittaGiudizioNonValido;
              ditta = getNomeDitta(sqlManager, dittao);
            }
          }
        }
      } catch (SQLException e) {
        throw new JspException("Errore durante il controllo dei coefficienti in G1CRIVALCOM associate a GFOF e G1CRIVAL", e);
      }
    }
    pageContext.setAttribute("dittaGiudizioCommissione", ditta);
    return esitoControllo;
  }

  private String getNomeDitta(SqlManager sqlManager,String dittao) throws SQLException{
    String nome = (String) sqlManager.getObject("select nomimp from impr where codimp = ?", new Object[] {dittao});
    return nome;
  }
}