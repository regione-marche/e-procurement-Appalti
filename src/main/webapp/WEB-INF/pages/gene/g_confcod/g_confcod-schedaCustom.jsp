<%
/*
 * Created on: 04-apr-2017
 *
 *
 * Copyright (c) Maggioli S.p.A. - Divisione ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A. - Divisione ELDASOFT
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 /* Dettaglio configurazione codifica automatica */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!--N.B.: RIDEFINITI CONTROLLI CUSTOMIZZATI PER g_confcod_scheda DI GENE-->
<gene:javaScript>
	//Cambio il riferimento al gestore di Gene (GestoreG_CONFCOD) specificando un gestore per Gare (GestoreG_CONFCODGare) 
	document.forms[0].gestore.value="it.eldasoft.sil.pg.tags.gestori.submit.GestoreG_CONFCODGare";	
	
	nascondiCONTAT();
 	
 	//'Ridefinisco' la funz. gestioneCampoCHKCODIFICA di g_confcod_scheda di gene
 	//aggiungendo il richiamo alla nascondiCONTAT();
 	var gestioneCampoCHKCODIFICADefault = gestioneCampoCHKCODIFICA;
	function gestioneCampoCHKCODIFICACustom(valore){
		 	gestioneCampoCHKCODIFICADefault(valore);
		 	nascondiCONTAT();
	}
	var gestioneCampoCHKCODIFICA = gestioneCampoCHKCODIFICACustom
	
	//Non visualizzo il campo 'Contatore' nel caso della configurazione per il campo NGARA.GARE
	function nascondiCONTAT(){
		var noment = getValue("G_CONFCOD_NOMENT");
		if(noment == 'GARE'){
		 	showObj("rowG_CONFCOD_CONTAT",false);
	 	}
	}
 	
</gene:javaScript>