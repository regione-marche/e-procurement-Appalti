/*
 * Created on 03-01-2022
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
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Viene verificato se esistono delle categorie per che contengono il carattere / nel codice della categoria,
 * oppure le sequenze " ." o ". "
 *
 *
 */
public class EsistonoCategorieDaScartareFunction extends AbstractFunzioneTag {

  public EsistonoCategorieDaScartareFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

    String categoriePresenti ="FALSE";
    try {
      String tipologie = (String) params[1];

      if(tipologie!=null) {
        tipologie = StringUtils.leftPad(tipologie, 3, "0");


        String selectCategorie = "select tab1tip from tab1 where tab1cod = ? order by tab1nord, tab1tip";
        List<?> datiCategorie = sqlManager.getListVector(selectCategorie, new Object[] { "G_038" });
        if (datiCategorie != null && datiCategorie.size() > 0) {
          for (int i = 0; i < datiCategorie.size(); i++) {
            Long tiplavg = (Long) SqlManager.getValueFromVectorParam(datiCategorie.get(i), 0).getValue();

            // Per ogni CAIS.TIPLAVG controllo in funzione della tipologia
            // di elenco (TIPOLOGIE) se deve essere caricato il nodo ROOT
            boolean tipologia_abilitata = false;
            switch (tiplavg.intValue()) {
            case 1:
              if ("1".equals(tipologie.substring(0, 1))) tipologia_abilitata = true;
              break;

            case 2:
              if ("1".equals(tipologie.substring(1, 2))) tipologia_abilitata = true;
              break;

            case 3:
              if ("1".equals(tipologie.substring(2))) tipologia_abilitata = true;
              break;

            case 4:
              if ("1".equals(tipologie.substring(0, 1))) tipologia_abilitata = true;
              break;

            case 5:
              if ("1".equals(tipologie.substring(2))) tipologia_abilitata = true;
              break;
            }

            if (tipologia_abilitata) {

              Long numero = null;
              String selectCAIS = "select count(*) from cais where cais.tiplavg = ? and (caisim like '%/%' "
                  + "or caisim like '%. %' or caisim like '% .%')";
              numero = (Long) sqlManager.getObject(selectCAIS, new Object[] { tiplavg});
              if(numero!=null && numero.longValue()>0) {
                categoriePresenti = "TRUE";
                break;
              }
            }
          }
        }
      }
    } catch (SQLException s) {
      throw new JspException("Errore durante la lettura delle categorie dalla CAIS ", s);
    }

    return categoriePresenti;
  }

}