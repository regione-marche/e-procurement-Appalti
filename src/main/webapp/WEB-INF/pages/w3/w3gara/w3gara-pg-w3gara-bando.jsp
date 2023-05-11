
<%
	/*
	 * Created on 20-Ott-2008
	 *
	 * Copyright (c) EldaSoft S.p.A.
	 * Tutti i diritti sono riservati.
	 *
	 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
	 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
	 * aver prima formalizzato un accordo specifico con EldaSoft.
	 */
%>

<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="v3_04_8_Attiva" value='${gene:callFunction2("it.eldasoft.sil.w3.tags.funzioni.IsAttivaVx_xx_xFunction",pageContext,"5")}' />

<gene:formScheda entita="W3GARA" gestisciProtezioni="true"
	gestore="it.eldasoft.sil.w3.tags.gestori.submit.GestoreW3GARABANDO">

	<gene:campoScheda>
		<td colspan="2"><b><br>SEZIONE III: PUBBLICAZIONE<br><br></b></td>
	</gene:campoScheda>
	<gene:campoScheda>
		<td colspan="2"><b>III.1) Pubblicazione</b></td>
	</gene:campoScheda>
	<gene:campoScheda campo="NUMGARA" visibile="false" />
	<gene:campoScheda campo="STATO_SIMOG" visibile="false" />
	<gene:campoScheda campo="MODO_REALIZZAZIONE" visibile="false" />
	<gene:campoScheda campo="DATA_CONFERMA_GARA" modificabile="false" visibile="${!datiRiga.W3GARA_STATO_SIMOG eq '1'}"/>
	<c:set var="titoloData" value="Data pubblicazione (1)" />
	<c:if test="${datiRiga.W3GARA_MODO_REALIZZAZIONE eq 11}">
		<c:set var="titoloData" value="Data di adesione all'accordo quadro/convenzione (1)" />
	</c:if>
	<gene:campoScheda title="${titoloData}" campo="DATA_PERFEZIONAMENTO_BANDO" obbligatorio="true"/>
	<gene:campoScheda title="Data scadenza per la presentazione delle offerte (2)" campo="DATA_TERMINE_PAGAMENTO"/>
	<gene:campoScheda title="Ora scadenza per la presentazione delle offerte" campo="ORA_SCADENZA"/>
	<gene:campoScheda campo="TIPO_OPERAZIONE" obbligatorio="true"/>
	<gene:campoScheda title="Data scadenza presentazione richieste invito (3)" campo="DSCAD_RICHIESTA_INVITO"/>
	<gene:campoScheda title="Data della lettera di invito (4)" campo="DATA_LETTERA_INVITO"/>	
	
	<gene:campoScheda>
		<td colspan="2"><b><br>III.1.1) Date di pubblicazione</b></td>
	</gene:campoScheda>
	<gene:campoScheda campo="DATA_GUCE" />
	<gene:campoScheda campo="NUMERO_GUCE" />
	<gene:campoScheda campo="DATA_GURI" />
	<gene:campoScheda campo="NUMERO_GURI" />
	<gene:campoScheda campo="DATA_ALBO" />
	<gene:campoScheda campo="DATA_BORE" />
	<gene:campoScheda campo="NUMERO_BORE" />
	
	<gene:campoScheda>
		<td colspan="2"><b><br>III.1.2) Numero inserzioni</b></td>
	</gene:campoScheda>
	<gene:campoScheda campo="QUOTIDIANI_NAZ" />
	<gene:campoScheda campo="QUOTIDIANI_REG" />
	<gene:campoScheda campo="PERIODICI" />
	<gene:campoScheda>
		<td colspan="2"><b><br>III.1.3) Altri dati</b></td>
	</gene:campoScheda>
	<gene:campoScheda campo="PROFILO_COMMITTENTE" visibile="false"/>
	<gene:campoScheda campo="SITO_MINISTERO_INF_TRASF" />
	<gene:campoScheda campo="LINK_SITO" />
	<gene:campoScheda campo="ESTREMA_URGENZA" visibile="false"/>
	<gene:campoScheda campo="LINK_AFFIDAMENTO_DIRETTO" visibile="${datiRiga.W3GARA_ESTREMA_URGENZA eq 2  && v3_04_8_Attiva}">
		<gene:checkCampoScheda funzione='validURL("##")' obbligatorio="true" messaggio="Il valore dell'indirizzo URL specificato non è valido: è possibile inserire solo un indirizzo URL che inizi con 'http://' o 'https://' e non contenga spazi o virgole" onsubmit="false"/>
	</gene:campoScheda>
	<gene:campoScheda campo="SITO_OSSERVATORIO" visibile="false"/>
	<gene:campoScheda campo="FLAG_SOSPESO" visibile="false"/>


	<gene:campoScheda>
		<td colspan="2"><b><br>SEZIONE IV: DOCUMENTI ALLEGATI<br><br></b></td>
	</gene:campoScheda>
	<gene:callFunction obj="it.eldasoft.sil.w3.tags.funzioni.GestioneW3GARADOCFunction" parametro="${gene:getValCampo(key, 'NUMGARA')}" />
	<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
		<jsp:param name="entita" value='W3GARADOC'/>
		<jsp:param name="chiave" value='${key}'/>
		<jsp:param name="nomeAttributoLista" value='datiW3GARADOC' />
		<jsp:param name="idProtezioni" value="W3GARADOC" />
		<jsp:param name="sezioneListaVuota" value="true" />
		<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/w3/w3garadoc/w3garadoc-interno-scheda.jsp"/>
		<jsp:param name="arrayCampi" value="'W3GARADOC_NUMGARA_', 'W3GARADOC_NUMDOC_', 'W3GARADOC_TIPO_DOCUMENTO_', 'W3GARADOC_NOME_DOCUMENTO_', 'VISUALIZZA_FILE_', 'selezioneFile_', 'W3GARADOC_NOTE_DOCUMENTO_' "/>
		<jsp:param name="titoloSezione" value="IV.1) Allegato n. " />
		<jsp:param name="titoloNuovaSezione" value="Documento allegato" />
		<jsp:param name="descEntitaVociLink" value="documento allegato" />
		<jsp:param name="msgRaggiuntoMax" value="i documenti allegati"/>
		<jsp:param name="usaContatoreLista" value="true"/>
		<jsp:param name="numMaxDettagliInseribili" value="5"/>
	</jsp:include>


	<gene:redefineInsert name="pulsanteNuovo" />
	<gene:redefineInsert name="schedaNuovo" />

	<c:if test="${datiRiga.W3GARA_STATO_SIMOG eq '5' or datiRiga.W3GARA_STATO_SIMOG eq '6' or datiRiga.W3GARA_STATO_SIMOG eq '7'}">
		<gene:redefineInsert name="pulsanteModifica" />
		<gene:redefineInsert name="schedaModifica" />
	</c:if>
	
	<gene:campoScheda>
		<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
	</gene:campoScheda>
	
	<gene:fnJavaScriptScheda funzione="gestioneDATA_GUCE('#W3GARA_DATA_GUCE#')" elencocampi="W3GARA_DATA_GUCE" esegui="true" />
	<gene:fnJavaScriptScheda funzione="gestioneDATA_GURI('#W3GARA_DATA_GURI#')" elencocampi="W3GARA_DATA_GURI" esegui="true" />
	<gene:fnJavaScriptScheda funzione="gestioneDATA_BORE('#W3GARA_DATA_BORE#')" elencocampi="W3GARA_DATA_BORE" esegui="true" />
	
	
</gene:formScheda>

<gene:redefineInsert name="addToAzioni" >
	<jsp:include page="/WEB-INF/pages/w3/commons/addtodocumenti-pubblica-gara-lotto.jsp" />
</gene:redefineInsert>


<gene:javaScript>
<c:if test="${modo eq 'MODIFICA'}">
	document.forms[0].encoding="multipart/form-data";
	
	var tmpSchedaConferma = schedaConferma;
	var tmpSchedaAnnulla = schedaAnnulla;
	
	var schedaConferma = function(){
		document.forms[0].action = "${pageContext.request.contextPath}/w3/Scheda.do?" + csrfToken;
		tmpSchedaConferma();
	}
	
	var schedaAnnulla = function(){
		document.forms[0].action = "${pageContext.request.contextPath}/w3/Scheda.do?" + csrfToken;
		tmpSchedaAnnulla();
	}
	
</c:if>

	function gestioneDATA_GUCE(value){
		document.getElementById("rowW3GARA_NUMERO_GUCE").style.display = (value != '' ? '':'none');
		if(value==''){
			document.forms[0].W3GARA_NUMERO_GUCE.value='';
		}
	}

	function gestioneDATA_GURI(value){
		document.getElementById("rowW3GARA_NUMERO_GURI").style.display = (value != '' ? '':'none');
		if(value==''){
			document.forms[0].W3GARA_NUMERO_GURI.value='';
		}
	}
	
	function gestioneDATA_BORE(value){
		document.getElementById("rowW3GARA_NUMERO_BORE").style.display = (value != '' ? '':'none');
		if(value==''){
			document.forms[0].W3GARA_NUMERO_BORE.value='';
		}
	}
	
	function scegliFile(indice) {
		var selezioneFile = document.getElementById("selFile[" + indice + "]").value;
		var lunghezza_stringa = selezioneFile.length;
		var posizione_barra = selezioneFile.lastIndexOf("\\");
		var posizione_punto = selezioneFile.lastIndexOf(".");
		var estensione = selezioneFile.substring(posizione_punto+1, lunghezza_stringa).toUpperCase();
		var nome = selezioneFile.substring(posizione_barra+1,lunghezza_stringa).toUpperCase();
		setValue("W3GARADOC_NOME_DOCUMENTO_" + indice, nome);
	}

	function visualizzaFileAllegato(campo_numgara, campo_numdoc, campo_nome_documento) {
		var href = "${pageContext.request.contextPath}/w3/VisualizzaDocumentoAllegato.do";
		document.location.href = href+ "?" + csrfToken + "&numgara=" + getValue(campo_numgara) + "&numdoc=" + getValue(campo_numdoc) + "&nome_documento=" + getValue(campo_nome_documento);
	}
	
	function validURL(str) {
		if(str==""){
			return true;
		}else{
			var res = /^(((http|HTTP|https|HTTPS):\/\/)|((w|W){3}(\d)?\.))[\w\?!\./:;\-_=#+*%@&quot;\(\)&amp;]+/.test(str);
			return res;
		}
	}

</gene:javaScript>
