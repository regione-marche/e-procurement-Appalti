/*
 * Created on 15/t/09
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
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * La funzione verifica se i lotti della gara hanno gia' il carrello associato
 * ed eventualmente blocca la lettura rda (nasconde il menu)
 *
 * @author Cristian Febas
 */
public class GetWSERPBloccoLetturaRdaFunction extends AbstractFunzioneTag {

  public GetWSERPBloccoLetturaRdaFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String ngara = (String) params[1];
    String codiceGara = (String) params[2];

    String visLetturaRda = "false";
    String bloccoOperazioniSmeUp= "false";
    try {
      Vector<?> datiGara = sqlManager.getVector(
          "select v.genere, g.bustalotti, g.esineg, g.modlicg from v_gare_torn v,gare g" +
          " where g.ngara = ? and g.codgar1 = v.codgar", new Object[]{ngara});
      if(datiGara!=null && datiGara.size()>0){
        Long genere = (Long) SqlManager.getValueFromVectorParam(datiGara, 0).getValue();
        Long bustalotti = (Long) SqlManager.getValueFromVectorParam(datiGara, 1).getValue();
        Long esineg = (Long) SqlManager.getValueFromVectorParam(datiGara, 2).getValue();
        Long modlicg = (Long) SqlManager.getValueFromVectorParam(datiGara, 3).getValue();

        if(esineg != null){
          visLetturaRda = "false";
        }else{
          if(new Long(3).equals(genere)){
            bustalotti = (Long) sqlManager.getObject("select bustalotti from gare where codgar1 = ? and ngara = ?"
            ,new Object[] { codiceGara,codiceGara });
          }
          if(new Long(3).equals(genere) && new Long(2).equals(bustalotti)){
            Long countLottiDisp = (Long) sqlManager.getObject("select count(*) from gare ga" +
            		" where ga.codgar1 = ? and ga.ngara<> ga.codgar1 and ga.modlicg in (5, 6, 14, 16)" +
            		" and not exists (select gc.ngara from gcap gc where gc.ngara = ga.ngara )"
                ,new Object[] { codiceGara });
            if(countLottiDisp > 0){
              visLetturaRda= "true";
            }
            Long counterOpSmeUp = (Long) sqlManager.getObject("select count(*) from gcap where codcarr is not null " +
            		" and ngara in (select ngara from gare where codgar1= ? ) ", new Object[] { codiceGara });
            if(counterOpSmeUp > 0){
              bloccoOperazioniSmeUp= "true";
            }

          }else{
            if(new Long(5).equals(modlicg) || new Long(6).equals(modlicg) || new Long(14).equals(modlicg) ||new Long(16).equals(modlicg)){
              Long counter = (Long) sqlManager.getObject("select count(*) from gcap where ngara = ? ", new Object[] { ngara });
              if(counter == 0){
                if(new Long(1).equals(genere)){
                  counter = (Long) sqlManager.getObject("select count(*) from gcap" +
                  		" where codcarr is  null and codrda is null and posrda is null" +
                  		" and ngara in (select ngara from gare where codgar1= ? ) ", new Object[] { codiceGara });
                  if(counter > 0){
                    visLetturaRda= "false";
                  }else{
                    visLetturaRda= "true";
                  }
                }else{
                  visLetturaRda= "true";
                }

              }
              Long counterOpSmeUp = (Long) sqlManager.getObject("select count(*) from gcap where ngara = ? and codcarr is not null", new Object[] { ngara });
              if(counterOpSmeUp > 0){
                bloccoOperazioniSmeUp= "true";
              }
            }
          }

        }

      }

      pageContext.setAttribute("bloccoOperazioniSmeUp", bloccoOperazioniSmeUp, PageContext.REQUEST_SCOPE);

    }catch (SQLException e) {
        throw new JspException(
                "Errore durante la verifica delle associazioni del carrello alla gara ", e);
    }

    return visLetturaRda;
  }

}