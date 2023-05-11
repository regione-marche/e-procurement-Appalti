/*
 * Created on 27/Mag/2022
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.sil.pg.bl.tasks;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.admin.AccountManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.ControlloDati190Manager;
import it.eldasoft.sil.pg.bl.LeggiPubblicazioniManager;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupAcquisisciDaPortale;
import it.eldasoft.sil.portgare.datatypes.AggiornamentoAnagraficaImpresaDocument;
import it.eldasoft.sil.portgare.datatypes.RecapitiType;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;
import net.sf.json.JSONArray;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;


public class ConsultazioneDellePubblicazioniSIMAPBatchManager {

  static Logger      logger = Logger.getLogger(ControlloDati190Manager.class);
  static String     nomeFileXML_Aggiornamento = "dati_agganag.xml";
  
  private SqlManager sqlManager; 
  private LeggiPubblicazioniManager  leggiPubblicazioniManager;

 /**
  *
  * @param sqlManager
  */
 public void setSqlManager(SqlManager sqlManager) {
   this.sqlManager = sqlManager;
 }

 public void setLeggiPubblicazioniManager(LeggiPubblicazioniManager leggiPubblicazioniManager) {
   this.leggiPubblicazioniManager = leggiPubblicazioniManager;
 }
  
 public void consultazioneDellePubblicazioniSIMAP() throws Exception{
    
   if (logger.isDebugEnabled())
			logger.debug("consultazioneDellePubblicazioniSIMAP: inizio metodo");
	 
   String isSimapAbilitato = "0";
   String urlSimap = ConfigManager.getValore("it.eldasoft.bandoavvisosimap.ws.url");
   if (urlSimap != null && !"".equals(urlSimap)) {
	  isSimapAbilitato = "1";
   }

	if ("1".equals(isSimapAbilitato)) {
		String codgar = "";
		Long numpub;
		int k = 0;
		String selectListaGare = "SELECT DISTINCT(codgar) FROM garuuid where eseguito is null and tipric like 'SIMAP%'";

		List listaGare = sqlManager.getListVector(selectListaGare, new Object[] {});
		if (listaGare != null && listaGare.size() > 0) {
			for (int i = 0; i < listaGare.size(); i++) {
				codgar = (String) SqlManager.getValueFromVectorParam(listaGare.get(i), 0).getValue();
				JSONArray ja = this.leggiPubblicazioniManager.leggiPubblicazioni(codgar,null);
			}
		}

	}
	if (logger.isDebugEnabled())
		logger.debug("consultazioneDellePubblicazioniSIMAP: fine metodo");
 }
}
