/*
 * Created on 23-set-2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.plugin;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Gestore preload per la pagina popup delle fasi di gara
 *
 * @author Luca.Giacomazzo
 */
public class GestorePopupFasiGara extends AbstractGestorePreload {

	public static final String CHIAVE_ULTERIORI_CAMPI_POPUP_FASI_RICEZOFF_GARA = "ULTERIORI_CAMPI_POPUP_FASI_RICEZOFF_GARA";

	public GestorePopupFasiGara(BodyTagSupportGene tag) {
		super(tag);
	}

	@Override
  public void doBeforeBodyProcessing(PageContext pageContext,
			String modoAperturaScheda) throws JspException {
		TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean(
				"tabellatiManager", pageContext, TabellatiManager.class);

		SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
				pageContext, SqlManager.class);

		// Caricamento del tabellato relativo ai motivi di esclusione
		// di una ditta da una gara
		List tabellatoFasiGara = tabellatiManager.getTabellato("A1011");

		if (tabellatoFasiGara != null && tabellatoFasiGara.size() > 0)
			pageContext.setAttribute("tabellatoFasiGara", tabellatoFasiGara,
					PageContext.REQUEST_SCOPE);

		// Caricamento del tabellato relativo allo stato di ammissione di una ditta
		List tabellatoAmmgar = tabellatiManager.getTabellato("A1054");
		if (tabellatoAmmgar != null && tabellatoAmmgar.size() > 0)
			pageContext.setAttribute("tabellatoAmmgar", tabellatoAmmgar,
					PageContext.REQUEST_SCOPE);

		List tabellatoStrin = tabellatiManager.getTabellato("A1103");
		if(tabellatoStrin!=null && tabellatoStrin.size()>0)
		  pageContext.setAttribute("tabellatoStrin", tabellatoStrin,
              PageContext.REQUEST_SCOPE);

		HashMap key = UtilityTags.stringParamsToHashMap(
            (String) pageContext.getAttribute(UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA,
                PageContext.REQUEST_SCOPE), null);
        String ngara = ((JdbcParametro) key.get("DITG.NGARA5")).getStringValue();


		// Caricamento del contenuto della tabella DETMOT
		try {
		  if(ngara!=null){
		    Long ultdetlic = (Long)sqlManager.getObject("select ultdetlic from gare1 where ngara=?", new Object[]{ngara});
		    pageContext.setAttribute("ultdetlic",
		        ultdetlic, PageContext.REQUEST_SCOPE);
	      }


		  List lista = sqlManager.getListVector(
					"select moties,annoff from detmot order by moties", null);

			if (lista != null && lista.size() > 0) {
				String moties = null;
				String annoff = null;
				List listaMotiviEsclusione = new ArrayList(lista.size());
				for (int i = 0; i < lista.size(); i++) {
					Vector esclusione = (Vector) lista.get(i);
					moties = ((JdbcParametro) esclusione.get(0)).getStringValue();
					annoff = ((JdbcParametro) esclusione.get(1)).getStringValue();

					Tabellato singolaMotivazione = new Tabellato();
					singolaMotivazione.setTipoTabellato(moties);
					singolaMotivazione.setDescTabellato(annoff);
					listaMotiviEsclusione.add(singolaMotivazione);
				}
				pageContext.setAttribute("listaMotiviEsclusione",
						listaMotiviEsclusione, PageContext.REQUEST_SCOPE);
			}
		} catch (SQLException e) {
			throw new JspException(
					"Errore in fase di esecuzione delle select di inizializzazione", e);
		}

		if(UtilityTags.SCHEDA_MODO_MODIFICA.equalsIgnoreCase(modoAperturaScheda)){
			if(pageContext.getSession().getAttribute(
					CHIAVE_ULTERIORI_CAMPI_POPUP_FASI_RICEZOFF_GARA) != null){
				HashMap mappaUlterioriCampi = (HashMap) pageContext.getSession().getAttribute(
						CHIAVE_ULTERIORI_CAMPI_POPUP_FASI_RICEZOFF_GARA);

				// Valore del campo chiave del record di DITG da visualizzare nella forma
				// DITG.CODGAR5=T:<codiceGara>;DITG.DITTAO=T:<codiceDitta>;DITG.NGARA5=T:<codiceLotto>
				String[] campiChiave = pageContext.getRequest().getParameter(
						UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA).split(";");

				// Passo del wizard in visualizzazione

				String paginaAttiva = pageContext.getRequest().getParameter("paginaAttivaWizard"); //String stepWizard = pageContext.getRequest().getParameter("stepWizard");
				String keyDITG = new String("");

				// Costruzione della chiave con cui cercare in sessione l'oggetto con i
				// valori modificati della ditta in oggetto. La chiave per DITG e'
				// nella forma: <codiceTornata>;<codiceDitta>;<codiceLotto>.
				// Mentre la chiave per DITGSTATI e' nella forma:
				// <codiceTornata>;<codiceDitta>;<codiceLotto>;<fasgar>
				for(int i=0; i < campiChiave.length; i++)
					keyDITG = keyDITG.concat(campiChiave[i].split(":")[1].concat(";"));
				keyDITG = keyDITG.substring(0, keyDITG.length() -1);

				// Aggiungo fasgar alla chiave per cercare gli oggetti nella HashMap
				String keyDITGSTATI = keyDITG.concat(";" + paginaAttiva);

				DataColumnContainer dataColumnContainerUlterioriCampi = null;
				// Se la HashMap contiene un oggetto con chiave = keyDITG, allora si
				// estraggono i valori, per poi metterli nel request
				if(mappaUlterioriCampi.containsKey(keyDITG))
					dataColumnContainerUlterioriCampi =
						(DataColumnContainer)	mappaUlterioriCampi.get(keyDITG);

				// Analogo discorso per keyDITGSTATISe
				if(mappaUlterioriCampi.containsKey(keyDITGSTATI)){
					if(dataColumnContainerUlterioriCampi == null)
						dataColumnContainerUlterioriCampi =
							(DataColumnContainer)	mappaUlterioriCampi.get(keyDITGSTATI);
					else
						dataColumnContainerUlterioriCampi.addColumns(((DataColumnContainer)
								mappaUlterioriCampi.get(keyDITGSTATI)).getColumns("DITGSTATI",
										2), false);
				}

				if(dataColumnContainerUlterioriCampi != null){
					// Set nel request di una variabile per indicare che bisogna valorizzare
					// i campi con i valori precedentemente modificati e non ancora salvati
					pageContext.setAttribute("ulterioriCampiDITG", "1", PageContext.REQUEST_SCOPE);

					HashMap dataColumnFromMappa = dataColumnContainerUlterioriCampi.getColonne();
					Iterator iter = dataColumnFromMappa.keySet().iterator();
					while(iter.hasNext()){
						String campo = (String) iter.next();
			      DataColumn val = (DataColumn) dataColumnFromMappa.get(campo);
			      pageContext.setAttribute("valore".concat(campo.replace('.', '_')),
			      		val.getValue().getStringValue(), PageContext.REQUEST_SCOPE);
					}
				}
			}
		}
	}

	@Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda)
			throws JspException {
	}

}