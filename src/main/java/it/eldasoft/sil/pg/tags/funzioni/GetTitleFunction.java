/*
 * Created on 16-lug-2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.tags.functions.AbstractGetTitleFunction;
import it.eldasoft.gene.tags.functions.GeneralTagsFunction;
import it.eldasoft.gene.tags.utils.UtilityTags;

import javax.servlet.jsp.PageContext;

/**
 * Funzione che ricava il titolo da mettere in una scheda
 *
 * @author Francesco.DeFilippis
 */
public class GetTitleFunction extends AbstractGetTitleFunction {

    @Override
    public String[] initFunction() {
	return new String[] {

		// Gare a lotto unico
		"GARE|Nuova gara a lotto unico|Gara a lotto unico {0}" + "|| select ngara from GARE where ngara = #GARE.NGARA#",
		// Lotti di gara
		"LOTTI|Nuovo lotto di gara|Lotto di gara {0}" + "|| select ngara from GARE where ngara = #GARE.NGARA#",
		// Gare divise in lotti con plichi distinti
		"TORN|Nuova gara divisa in lotti con plichi distinti|Gara divisa in lotti con plichi distinti {0}"
			+ "|| select codgar from TORN where codgar = #TORN.CODGAR#",
		// Gare divise in lotti con plico unico
		"TORN_GARE|Nuova gara divisa in lotti con plico unico|Gara divisa in lotti con plico unico {0}"
			+ "|| select codgar from TORN where codgar = #TORN.CODGAR#",
		// Elenco operatori economici
		"GAREALBO|Nuovo elenco operatori economici|Elenco operatori economici {0}"
			+ "|| select ngara from GAREALBO where ngara = #GARE.NGARA#",
		// Catalogo mercato elettronico
		"MECATALOGO|Nuovo catalogo elettronico|Catalogo elettronico {0}" + "|| select ngara from MECATALOGO where ngara = #GARE.NGARA#",
		// Avvisi
		"GAREAVVISI|Nuovo avviso|Avviso {0}" + "|| select ngara from GAREAVVISI where ngara = #GAREAVVISI.NGARA#",
		// Ditte di una gara
		"DITG||{0} - {1}" + "|| select NPROGG, NOMIMO from DITG " + "where CODGAR5 = #DITG.CODGAR5# " + "and DITTAO = #DITG.DITTAO# "
			+ "and NGARA5 = #DITG.NGARA5# ",
		// Ditte dalle fasi gi gara
		"DITG_FASIGARA||{0} - {1}" + "|| select NUMORDPL, NOMIMO from DITG " + "where CODGAR5 = #DITG.CODGAR5# "
			+ "and DITTAO = #DITG.DITTAO# " + "and NGARA5 = #DITG.NGARA5# ",
		// Ditte di una gara (personalizzazione per ASPI)
		"DITG_PROT|Nuova ditta della gara {0}|Ditta {0} della gara {1}" + "|select ngara from GARE where ngara = #NGARA# "
			+ "|select NPROGG, NGARA5 from DITG " + "where CODGAR5 = #DITG.CODGAR5# " + "and DITTAO = #DITG.DITTAO# "
			+ "and NGARA5 = #DITG.NGARA5# ",
		// Sedute di gara
		"GARSED|Nuova seduta della gara {0}|Seduta della gara {0}" + "|select ngara from GARE where ngara = #NGARA# "
			+ "|select ngara from GARE where ngara = #GARSED.NGARA#",
		// Sedute di gara richiamate da Gare divise in lotti con offerta unica
		"TORN_GARSED||Seduta della gara {0}" + "||select ngara from GARE where ngara = #GARSED.NGARA#",
		// Criteri di valutazione
		"GOEV|Titolo inserimento|Titolo modifica/visualizzazione||",
		// Lavorazione o fornitura
		"GCAP|Nuova lavorazione o fornitura della gara {0}|Lavorazione o fornitura della gara {0}"
			+ "|select ngara from GARE where ngara = #NGARA# " + "|select ngara from GARE where ngara = #GCAP.NGARA#",
		// Lavorazione o fornitura richiamate da Gare divise in lotti con
		// offerta unica
		"TORN_GCAP||Lavorazione o fornitura della gara {0}"
			+ "||select codgar1 from GARE where ngara = #GCAP.NGARA#",
		// Aggiudicazione provvisoria e definitiva richiamata da Gare divise in
		// lotti con offerta unica
		"AGGIUD_LOTTI||Calcolo aggiudicazione del lotto {0}" + "||select ngara from GARE where ngara = #GARE.NGARA#",
		// Apertura offerte e aggiudicazione provvisoria richiamata da Gare divise in
        // lotti con offerta unica
        "APERTURAOFFAGGIUD_LOTTI||Apertura offerte e calcolo aggiudicazione del lotto {0}" + "||select ngara from GARE where ngara = #GARE.NGARA#",
		// Offerte tecniche per le da Gare divise in lotti con offerta unica
		"OFFERTE_TECNICHE_DITTA||Valutazione tecnica della ditta {0}"
			+ "||select NOMIMO from DITG where CODGAR5 = #DITG.CODGAR5# and NGARA5 = #DITG.NGARA5# and DITTAO = #DITG.DITTAO#",
		// Verifica conformità per le ditte in Gare divise in lotti con offerta
		// unica
		"VERIFICA_CONFORMITA_DITTA||Verifica conformità della ditta {0}"
			+ "||select NOMIMO from DITG where CODGAR5 = #DITG.CODGAR5# and NGARA5 = #DITG.NGARA5# and DITTAO = #DITG.DITTAO#",
		// Offerte economiche per le da Gare divise in lotti con offerta unica
		"OFFERTE_ECONOMICHE_DITTA||Offerta economica della ditta {0}"
			+ "||select NOMIMO from DITG where CODGAR5 = #DITG.CODGAR5# and NGARA5 = #DITG.NGARA5# and DITTAO = #DITG.DITTAO#",
		// Offerte per la ditta esclusa per le da Gare divise in lotti con
		// offerta unica
		"OFFERTE_DITTA_ESCLUSA||Offerta della ditta esclusa {0}"
			+ "||select NOMIMO from DITG where CODGAR5 = #DITG.CODGAR5# and NGARA5 = #DITG.NGARA5# and DITTAO = #DITG.DITTAO#",
		// Adempimenti Anticorruzione
		"ANTICOR|Nuovo adempimento|Adempimento anno riferimento {0}" + "||select ANNORIF from ANTICOR where ID = #ANTICOR.ID#",
		// Lotti dell'Adempimento
		"ANTICORLOTTI|Nuovo Lotto|Lotto {0}" + "||select CIG from ANTICORLOTTI where ID = #ANTICORLOTTI.ID#",
		// Articoli
		"MEARTCAT|Nuovo articolo|{0} - {1} - Art. {2}" + "||select cais.caisim, cais.descat, meartcat.cod " + " from meartcat, opes, cais "
			+ " where opes.ngara3 = meartcat.ngara " + " and opes.nopega = meartcat.nopega " + " and cais.caisim = opes.catoff "
			+ " and meartcat.id = #MEARTCAT.ID#",
		//Prodotto caricati di un operatore economico
		"MEISCRIZPROD||Prodotto {0}" + "||select CODOE,NOMIMO from MEISCRIZPROD,DITG where ID = #MEISCRIZPROD.ID#"
			+ " and CODGAR = DITG.CODGAR5 and NGARA = DITG.NGARA5 and CODIMP = DITG.DITTAO",
		//Ricerca di mercato
		"MERIC|Nuova ricerca di mercato|Ricerca di mercato {0}" + "|| select codric from meric where id = #MERIC.ID#",
		//Ordine della ricerca di mercato
		"GARECONT_ORDINE||Ordine {0}" + "||select ngara from garecont where ngara = #GARECONT.NGARA#" + " and ncont = #GARECONT.NCONT#",
		// Nominativo commissione
		"COMMNOMIN|Nuovo nominativo|Nominativo {0}" + "|| select codtec from COMMNOMIN where ID = #COMMNOMIN.ID#",
        //Gestione Ordini
        "NSO_ORDINI|Nuovo ordine|Ordine {0}" + "|| select codord from nso_ordini where id = #NSO_ORDINI.ID#"};

    }

    @Override
    protected String getTitleInserimento(PageContext pageContext, String table) {
      String ret=this.getTitoloGOEV(pageContext, table, "NUOVO");
      return ret;
    }

    @Override
    protected String getTitleModifica(PageContext pageContext, String table, String keys) {
      String ret=this.getTitoloGOEV(pageContext, table, "MODIFICA");
      return ret;
    }

    /**
     * Viene creato il titolo per la pagina dei criteri di valutazione
     * @param pageContext
     * @param table
     * @param modo
     * @return
     */
    private String getTitoloGOEV(PageContext pageContext, String table, String modo){
      String ret=null;
      if("GOEV".equals(table)){
        if("NUOVO".equals(modo))
          ret = "Nuovo criterio";
        else
          ret = "Criterio";
        String key = UtilityTags.getParametro(pageContext, UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA);
        // Se la chiave non è settata allora leggo la chiave del padre
        if (key == null) {
          key = UtilityTags.getParametro(pageContext, UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA_PARENT);
        }
        String ngara=GeneralTagsFunction.getValCampo(key, "GOEV.NGARA");
        if(ngara==null || "".equals(ngara))
          ngara=GeneralTagsFunction.getValCampo(key, "GARE.NGARA");
        String tipoCriterio=pageContext.getRequest().getParameter("tipoCriterio");
        if("1".equals(tipoCriterio))
          ret+=" di valutazione busta tecnica della gara";
        else if("2".equals(tipoCriterio))
          ret+=" di valutazione busta economica della gara";
        ret+= " " + ngara;
      }
      return ret;
    }
}
