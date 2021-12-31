/*
 * Created on 10/12/2019
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
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;

/**
 * La funzione calcola il massimo fra il numero di cifre decimali del campo DITG.RIBAUO di una gara/lotto
 *
 * @author Marcello Caminiti
 */
public class GetMaxNumeroCifreDecimaliFunction extends AbstractFunzioneTag {

  static Logger            logger           = Logger.getLogger(GetMaxNumeroCifreDecimaliFunction.class);

  public GetMaxNumeroCifreDecimaliFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String result = "";
    String numeroGara = (String) params[1];
    String db = ConfigManager.getValore(CostantiGenerali.PROP_DATABASE);

    if (numeroGara != null && numeroGara.length() > 0) {
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

      String select="";
      //I passi per estrarre il numero di cifre decimali indicati con un esempio (-5,23213) sono:
      // -estrazione della sola parte decimale in valore assoluto (es: 0,23213)
      // -conversione in stringa del valore
      // -prendere la sottostringa del 2 carattere in poi per Oracle, dal terzo per gli altri db (es: 23213)
      // -rimozione degli eventuali caratteri '0' posti alla fine come riempimento (per sql server non esiste la possibilità di specificare nel trim il carattere da sostituire, quindi devo prima trasformare '0' in ' ', poi dopo l'applicazione di
      //  rtrim, riporto ' ' in '0'
      // -prendere la lunghezza della stringa così ottenuta
      if("ORA".equals(db))
        select="select max(LENGTH(substr(to_char(abs(ribauo - trunc(ribauo))),2)))  from ditg where ngara5=? and ribauo is not null and ribauo - trunc(ribauo) !=0";
      else if("MSQ".equals(db))
          select="select max(len(replace(rtrim(replace(SUBSTRING(CONVERT( varchar, ABS(ribauo-CAST(ribauo AS INT)) ),3,9),'0', ' ')),' ','0'))) FROM ditg where ngara5=? and ribauo is not null and ABS(ribauo-CAST(ribauo AS INT))!=0 ";
      else if("POS".equals(db))
        select="select max(length(trim( trailing '0' from substring(cast(abs(ribauo - trunc(ribauo)) as text),3)))) from ditg where ngara5=?  and ribauo is not null and ribauo - trunc(ribauo) !=0 ";

      Long maxCifre = null;
      try {
        maxCifre = (Long) sqlManager.getObject(select, new Object[] { numeroGara });
        if (maxCifre != null)
          result = String.valueOf(maxCifre.intValue());

      } catch (SQLException e) {
        logger.error("Errore durante la lettura del campo DITG.RIBAUO per determinare il massimo numero di cifre decimali per la gara " + numeroGara, e);
      }


    }
    return result;
  }
}