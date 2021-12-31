
<%
	/*
	 * Created on 09-mar-2016
	 *
	 * Copyright (c) EldaSoft S.p.A.
	 * Tutti i diritti sono riservati.
	 *
	 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
	 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
	 * aver prima formalizzato un accordo specifico con EldaSoft.
	 */

%>

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<c:set var="isNavigazioneDisabilitata" value="1" scope="request" />

<c:set var="idconfi" value="${param.idconfi}"/>
<c:set var="descri" value="${param.descri}"/>
<c:set var="wsdmProtocollo" value="${param.wsdmProtocollo}"/>
<c:set var="key" value='WSDMCONFI.ID=N:${idconfi}' scope="request" />
<c:set var="keyParent" value='WSDMCONFI.ID=N:${idconfi}' scope="request" />
<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="request" />
<c:set var="codapp" value="${param.codapp}"/>

<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/wsdm/jquery.wsdm.css" >
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.validate.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wsdmsupporto.js?v=${sessionScope.versioneModuloAttivo}"></script>
<script type="text/javascript">
<!--

	var arrayProprieta = null;
	
	<%-- I valori attualmente gestiti sono:  --%>
	<%-- p		password  --%>
	<%-- b		valore booleano  --%>
	var tipoProprieta = null;

	// Azioni di pagina

	function gestisciSubmit(){
		var continua = true;
		//Controllo del titolo del documento associato
		
		if(continua)
			document.formProprieta.submit();
	}

	function annulla() {
		document.pagineForm.modalita.value = "visualizza";
		document.pagineForm.submit();
	}
	
-->

-->
</script>

<form action="${contextPath}/SalvaConfigurazioneWsdm.do?activePage=3" name="formProprieta" method="post" >
	
<script type="text/javascript">
<!--
	
	//IMPOTANTE: è stata definita la funzione javascrit "disabilitaProp" che impostando la prop8 a no, interviene sulla property 'wsdm.accediFascicoloDocumentale' e 'wsdm.gestioneStrutturaCompetente'.
	//           Poichè le properties sono individuate in base alla posizione consecutiva nella pagina, se si modifica l'ordine delle properties si deve modificare il riferimento
	//			 a tale property nella funzione
	
	
	//IMPORTATNTE: Tutta la gestione le sezioni dei parametri di login sono basate su funzioni scritte tenendo conto che i nomi dei campi hanno una struttura del tipo prop1, prop2,... quindi se si modifica ll'ordine dei campi
	//             si deve intervenire nelle funzioni che intervengono nella gestione!!!!
	
	var idconfi = '${param.idconfi}';
	
	//Valori delle property
	//1			"url"	protocollazione.wsdm.url
	//2			""		protocollazione.wsdm.username
	//3			"psw"	protocollazione.wsdm.password
	//4			""		protocollazione.wsdm.nome
	//5			""		protocollazione.wsdm.cognome
	//6			""		protocollazione.wsdm.ruolo
	//7			""		protocollazione.wsdm.codiceUO
	//8			""		protocollazione.wsdm.idUtente
	//9			""		protocollazione.wsdm.gare.classifica
	//10		""		protocollazione.wsdm.gare.tipoDocumento
	//11		""		protocollazione.wsdm.gare.tipoDocumento.prequalifica
	//12		""		protocollazione.wsdm.gare.registro
	//13		""		protocollazione.wsdm.gare.struttura
	//14		""		protocollazione.wsdm.gare.indice
	//15		""		protocollazione.wsdm.gare.titolazione
	//16		""		protocollazione.wsdm.gare.livelloRiservatezza
	//17		""		protocollazione.wsdm.iscrizione.classifica
	//18		""		protocollazione.wsdm.iscrizione.tipoDocumento
	//19		""		protocollazione.wsdm.iscrizione.registro
	//20		""		protocollazione.wsdm.iscrizione.indice
	//21		""		protocollazione.wsdm.iscrizione.titolazione
	//22		""		protocollazione.wsdm.mepa.classifica
	//23		""		protocollazione.wsdm.mepa.tipoDocumento
	//24		""		protocollazione.wsdm.mepa.registro
	//25		""		protocollazione.wsdm.mepa.indice
	//26		""		protocollazione.wsdm.mepa.titolazione
	//27		""		protocollazione.wsdm.avvisi.classifica
	//28		""		protocollazione.wsdm.avvisi.tipoDocumento
	//29		""		protocollazione.wsdm.avvisi.registro
	//30		""		protocollazione.wsdm.avvisi.indice
	//31		""		protocollazione.wsdm.avvisi.titolazione
	//32		""		protocollazione.wsdm.struttura
	//33		""		protocollazione.wsdm.mittenteInterno
	//34		"-b"	protocollazione.wsdm.cfMittente
	//35		""		protocollazione.wsdm.mezzo
	//36		""		protocollazione.wsdm.channelCode
	//37		""		protocollazione.wsdm.idUnitaOperativa
	//38		""		protocollazione.wsdm.idUnitaOperativaDestinataria
	//39		""		protocollazione.wsdm.tipoDocumento.inviaComunicazione
	//40		""		protocollazione.wsdm.supporto
	//41		""		protocollazione.wsdm.tipoAssegnazione
	//42		""		protocollazione.wsdm.sottotipoGara
	//43		""		protocollazione.wsdm.sottotipoComunicazione
	//44		-"b"	protocollazione.wsdm.posizioneAllegatoComunicazione
	
	
	arrayProprieta = [[idconfi,"wsdm.fascicoloprotocollo.url"],[idconfi,"protocollazione.wsdm.username"],[idconfi,"protocollazione.wsdm.password"],[idconfi,"protocollazione.wsdm.nome"],
		[idconfi,"protocollazione.wsdm.cognome"],[idconfi,"protocollazione.wsdm.ruolo"],[idconfi,"protocollazione.wsdm.codiceUO"],[idconfi,"protocollazione.wsdm.idUtente"],
		[idconfi,"protocollazione.wsdm.gare.classifica"],[idconfi,"protocollazione.wsdm.gare.tipoDocumento"],[idconfi,"protocollazione.wsdm.gare.tipoDocumento.prequalifica"],
		[idconfi,"protocollazione.wsdm.gare.registro"],[idconfi,"protocollazione.wsdm.gare.struttura"],[idconfi,"protocollazione.wsdm.gare.indice"],
		[idconfi,"protocollazione.wsdm.gare.titolazione"],[idconfi,"protocollazione.wsdm.gare.livelloRiservatezza"],[idconfi,"protocollazione.wsdm.iscrizione.classifica"],
		[idconfi,"protocollazione.wsdm.iscrizione.tipoDocumento"],[idconfi,"protocollazione.wsdm.iscrizione.registro"],[idconfi,"protocollazione.wsdm.iscrizione.indice"],
		[idconfi,"protocollazione.wsdm.iscrizione.titolazione"],[idconfi,"protocollazione.wsdm.mepa.classifica"],[idconfi,"protocollazione.wsdm.mepa.tipoDocumento"],
		[idconfi,"protocollazione.wsdm.mepa.registro"],[idconfi,"protocollazione.wsdm.mepa.indice"],[idconfi,"protocollazione.wsdm.mepa.titolazione"],[idconfi,"protocollazione.wsdm.avvisi.classifica"],
		[idconfi,"protocollazione.wsdm.avvisi.tipoDocumento"],[idconfi,"protocollazione.wsdm.avvisi.registro"],[idconfi,"protocollazione.wsdm.avvisi.indice"],
		[idconfi,"protocollazione.wsdm.avvisi.titolazione"],[idconfi,"protocollazione.wsdm.struttura"],[idconfi,"protocollazione.wsdm.mittenteInterno"],[idconfi,"protocollazione.wsdm.cfMittente"],
		[idconfi,"protocollazione.wsdm.mezzo"],[idconfi,"protocollazione.wsdm.channelCode"],[idconfi,"protocollazione.wsdm.idUnitaOperativa"],[idconfi,"protocollazione.wsdm.idUnitaOperativaDestinataria"],
		[idconfi,"protocollazione.wsdm.tipoDocumento.inviaComunicazione"],[idconfi,"protocollazione.wsdm.supporto"],[idconfi,"protocollazione.wsdm.tipoAssegnazione"],
		[idconfi,"protocollazione.wsdm.sottotipoGara"],[idconfi,"protocollazione.wsdm.sottotipoComunicazione"],[idconfi,"protocollazione.wsdm.posizioneAllegatoComunicazione"]];
											
	tipoProprieta = [ "label","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","-b","","","","","","","","","","-b"];											
	
	var condLoginComune;
	var condUrlProtocollo;
	var condUrlDocumentale;
		
	
	$(document).ready(function() {
		if (arrayProprieta != null && arrayProprieta.length > 0 &&
				tipoProprieta != null && tipoProprieta.length > 0   &&
				arrayProprieta.length <= tipoProprieta.length) {
			
			$.ajax({
				url: '${pageContext.request.contextPath}/GetWsdmProprieta.do',
				type: 'POST',
				async: false,
				dataType: 'json',
				data: { arrayProp: arrayProprieta },
				success: function(data) {
					if (data && data.length > 0) {
						var indice = 0;
						$.map( data, function( item ) {
								$( ("#titoloProp" + (indice+1)) ).attr('title', item.chiave);
								$( ("#idconfi" + (indice+1)) ).val(item.idconfi);
								$( ("#valore"+ (indice+1)) ).val(item.valore);
							if ("label" == tipoProprieta[indice]) {
								$( ("#prop"  + (indice+1)) ).html(item.valore);
							}else if("b" == tipoProprieta[indice] || "-b" == tipoProprieta[indice]){
								var valore =item.valore;
								if((valore=="" || valore== null) && "b" == tipoProprieta[indice]){
									valore='1';
								}else if((valore=="" || valore== null) && "-b" == tipoProprieta[indice]){
									valore='0';
								}
								$( ("#prop"  + (indice+1)) ).find('option[value="' + valore + '"]').attr("selected",true);
							}else{
								$( ("#prop"  + (indice+1)) ).val(item.valore);
							}
							indice++;
						});
						
					}
				},
				error: function() {
					alert("Errore nel caricamento della propriet&agrave");
				}
			});			
		}
		
		//nascondo i titoli delle sezioni non visualizzate
		var sezioneAttiva = false;
		var i = 2;
		for(i=2;i<=8 && !sezioneAttiva;i++){
			if($("#titleProp"  + (i)).length ){
				sezioneAttiva = true;
			}
		}
		if(!sezioneAttiva){
			$("#parametriConnessioneTitle").hide();
		}
		
		sezioneAttiva = false;
		for(i=9;i<=16 && !sezioneAttiva;i++){
			if($("#titleProp"  + (i)).length ){
				sezioneAttiva = true;
			}
		}
		//per JODC la sezione va visualizzata
		if(!sezioneAttiva && $("#titleProp"  + (42)).length)
			sezioneAttiva = true;
		if(!sezioneAttiva){
			$("#parametriProcedureGaraTitle").hide();
		}
		
		sezioneAttiva = false;
		for(i=17;i<=21 && !sezioneAttiva;i++){
			if($("#titleProp"  + (i)).length ){
				sezioneAttiva = true;
			}
		}
		if(!sezioneAttiva){
			$("#parametriElencoTitle").hide();
		}
		
		sezioneAttiva = false;
		for(i=22;i<=26 && !sezioneAttiva;i++){
			if($("#titleProp"  + (i)).length ){
				sezioneAttiva = true;
			}
		}
		if(!sezioneAttiva){
			$("#parametriCatalogoTitle").hide();
		}
		
		sezioneAttiva = false;
		for(i=22;i<=26 && !sezioneAttiva;i++){
			if($("#titleProp"  + (i)).length ){
				sezioneAttiva = true;
			}
		}
		if(!sezioneAttiva){
			$("#parametriCatalogoTitle").hide();
		}
		
		sezioneAttiva = false;
		for(i=27;i<=31 && !sezioneAttiva;i++){
			if($("#titleProp"  + (i)).length ){
				sezioneAttiva = true;
			}
		}
		if(!sezioneAttiva){
			$("#parametriAvvisiTitle").hide();
		}
		
		sezioneAttiva = false;
		for(i=32;i<=41 && !sezioneAttiva;i++){
			if($("#titleProp"  + (i)).length ){
				sezioneAttiva = true;
			}
		}
		if(!sezioneAttiva){
			$("#parametriAltroTitle").hide();
		}
		
	});
-->
</script>

<tr>
	<td colspan="2">
		<b><br>Protocollo</b>
	</td>
</tr>
<tr id="sez1">
	<td class="etichetta-dato">
		<span id="titleProp1" >Indirizzo URL servizio WSDM</span>
	</td>
	<td class="valore-dato">
		<input type="hidden" id="codapp1" name="idconfi" value="${idconfi}" />
		<input type="hidden" id="valore1" name="valore" value="" />
		<input type="hidden" id="chiave1" name="chiave" value="protocollazione.wsdm.url" maxlength="100" />
		<span id="prop1" name="valore" title="Url" value="" maxlength="100"/>
	</td>
</tr>
<tr id="parametriConnessioneTitle">
	<td colspan="2">
		<b><br>Parametri di connessione</b>
	</td>
</tr>
<tr id="sez2">
	<td class="etichetta-dato">
		<span id="titleProp2" >Utente</span>
	</td>
	<td class="valore-dato">
		<input type="hidden" id="codapp2" name="idconfi" value="${idconfi}" />
		<input type="hidden" id="chiave2" name="chiave" value="protocollazione.wsdm.username" maxlength="100" />
		<input type="text" id="prop2" name="valore"title="Username" class="testo" type="text" size="50" value="" maxlength="100"/>
	</td>
</tr>
<c:if test="${wsdmProtocollo ne 'IRIDE' and wsdmProtocollo ne 'JIRIDE' and wsdmProtocollo ne 'INFOR' and wsdmProtocollo ne 'PROTSERVICE' and wsdmProtocollo ne 'JPROTOCOL'}">
	<tr id="sez3">
		<td class="etichetta-dato">
			<span id="titleProp3" >Password</span>
		</td>
		<td class="valore-dato">
			<input type="hidden" id="codapp3" name="idconfi" value="${idconfi}" />
			<input type="hidden" id="chiave3" name="chiave" value="protocollazione.wsdm.password" maxlength="100" />
			<input type="password" id="prop3" name="valore" title="Password" class="testo" size="50" value="" maxlength="100"/>
		</td>
	</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'IRIDE' or wsdmProtocollo eq 'JIRIDE' or wsdmProtocollo eq 'PALEO'}">
	<tr id="sez6">
		<td class="etichetta-dato">
			<span id="titleProp6" >Ruolo</span>
		</td>
		<td class="valore-dato">
			<input type="hidden" id="codapp6" name="idconfi" value="${idconfi}" />
			<input type="hidden" id="chiave6" name="chiave" value="protocollazione.wsdm.ruolo" maxlength="100" />
			<input type="text" id="prop6" name="valore"title="Ruolo" class="testo" type="text" size="50" value="" maxlength="100"/>
		</td>
	</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'PALEO'}">
	<tr id="sez4">
		<td class="etichetta-dato">
			<span id="titleProp4" >Nome</span>
		</td>
		<td class="valore-dato">
			<input type="hidden" id="codapp4" name="idconfi" value="${idconfi}" />
			<input type="hidden" id="chiave4" name="chiave" value="protocollazione.wsdm.nome" maxlength="100" />
			<input type="text" id="prop4" name="valore"title="Nome" class="testo" type="text" size="50" value="" maxlength="100"/>
		</td>
	</tr>
	<tr id="sez5">
		<td class="etichetta-dato">
			<span id="titleProp5" >Cognome</span>
		</td>
		<td class="valore-dato">
			<input type="hidden" id="codapp5" name="idconfi" value="${idconfi}" />
			<input type="hidden" id="chiave5" name="chiave" value="protocollazione.wsdm.cognome" maxlength="100" />
			<input type="text" id="prop5" name="valore"title="Cognome" class="testo" type="text" size="50" value="" maxlength="100"/>
		</td>
	</tr>
</c:if>

<c:if test="${wsdmProtocollo eq 'PALEO'}">
	<tr id="sez7">
		<td class="etichetta-dato">
			<span id="titleProp7" >Codice unità organizzativa</span>
		</td>
		<td class="valore-dato">
			<input type="hidden" id="codapp7" name="idconfi" value="${idconfi}" />
			<input type="hidden" id="chiave7" name="chiave" value="protocollazione.wsdm.codiceUO" maxlength="100" />
			<input type="text" id="prop7" name="valore"title="CodiceUO" class="testo" type="text" size="50" value="" maxlength="100"/>
		</td>
	</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'ENGINEERING'}">
	<tr id="sez8">
		<td class="etichetta-dato">
			<span id="titleProp8" >Id utente</span>
		</td>
		<td class="valore-dato">
			<input type="hidden" id="codapp8" name="idconfi" value="${idconfi}" />
			<input type="hidden" id="chiave8" name="chiave" value="protocollazione.wsdm.idUtente" maxlength="100" />
			<input type="text" id="prop8" name="valore"title="IdUtente" class="testo" type="text" size="50" value="" maxlength="100"/>
		</td>
	</tr>
</c:if>		
	
	<tr id="parametriProcedureGaraTitle">
		<td colspan="2">
			<b><br>Parametri specifici per procedure di Gara</b>
		</td>
	</tr>
<c:if test="${wsdmProtocollo eq 'JIRIDE' or wsdmProtocollo eq 'EASYDOC'}">
	<tr id="sez9">
		<td class="etichetta-dato">
			<span id="titleProp9" >Classifica</span>
		</td>
		<td class="valore-dato">
			<input type="hidden" id="codapp9" name="idconfi" value="${idconfi}" />
			<input type="hidden" id="chiave9" name="chiave" value="protocollazione.wsdm.gare.classifica" maxlength="60" />
			<input type="text" id="prop9" name="valore"title="Classifica" class="testo" type="text" size="50" value="" maxlength="100"/>
		</td>
	</tr>
</c:if>	
<c:if test="${wsdmProtocollo eq 'JIRIDE' or wsdmProtocollo eq 'TITULUS' or wsdmProtocollo eq 'ARCHIFLOW' or wsdmProtocollo eq 'ARCHIFLOWFA' or wsdmProtocollo eq 'URBI'}">
	<tr id="sez10">
		<td class="etichetta-dato">
			<span id="titleProp10" >Tipo documento</span>
		</td>
		<td class="valore-dato">
			<input type="hidden" id="codapp10" name="idconfi" value="${idconfi}" />
			<input type="hidden" id="chiave10" name="chiave" value="protocollazione.wsdm.gare.tipoDocumento" maxlength="60" />
			<input type="text" id="prop10" name="valore"title="TipoDocumento" class="testo" type="text" size="50" value="" maxlength="100"/>
		</td>
	</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'ARCHIFLOWFA' or wsdmProtocollo eq 'URBI'}">
	<tr id="sez11">
		<td class="etichetta-dato">
			<span id="titleProp11" >Tipo documento per busta prequalifica</span>
		</td>
		<td class="valore-dato">
			<input type="hidden" id="codapp11" name="idconfi" value="${idconfi}" />
			<input type="hidden" id="chiave11" name="chiave" value="protocollazione.wsdm.gare.tipoDocumento.prequalifica" maxlength="60" />
			<input type="text" id="prop11" name="valore"title="Prequalifica" class="testo" type="text" size="50" value="" maxlength="100"/>
		</td>
	</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'PALEO' or wsdmProtocollo eq 'INFOR'}">
	<tr id="sez12">
		<td class="etichetta-dato">
			<span id="titleProp12" >Registro</span>
		</td>
		<td class="valore-dato">
			<input type="hidden" id="codapp12" name="idconfi" value="${idconfi}" />
			<input type="hidden" id="chiave12" name="chiave" value="protocollazione.wsdm.gare.registro" maxlength="60" />
			<input type="text" id="prop12" name="valore"title="Registro" class="testo" type="text" size="50" value="" maxlength="100"/>
		</td>
	</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'JPROTOCOL'}">
	<tr id="sez13">
		<td class="etichetta-dato">
			<span id="titleProp13" >Struttura</span>
		</td>
		<td class="valore-dato">
			<input type="hidden" id="codapp13" name="idconfi" value="${idconfi}" />
			<input type="hidden" id="chiave13" name="chiave" value="protocollazione.wsdm.gare.struttura" maxlength="60" />
			<input type="text" id="prop13" name="valore"title="Struttura" class="testo" type="text" size="50" value="" maxlength="100"/>
		</td>
	</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'ENGINEERING'}">
	<tr id="sez14">
		<td class="etichetta-dato">
			<span id="titleProp14" >Indice</span>
		</td>
		<td class="valore-dato">
			<input type="hidden" id="codapp14" name="idconfi" value="${idconfi}" />
			<input type="hidden" id="chiave14" name="chiave" value="protocollazione.wsdm.gare.indice" maxlength="60" />
			<input type="text" id="prop14" name="valore"title="Indice" class="testo" type="text" size="50" value="" maxlength="100"/>
		</td>
	</tr>
	<tr id="sez15">
		<td class="etichetta-dato">
			<span id="titleProp15" >Titolazione</span>
		</td>
		<td class="valore-dato">
			<input type="hidden" id="codapp15" name="idconfi" value="${idconfi}" />
			<input type="hidden" id="chiave15" name="chiave" value="protocollazione.wsdm.gare.titolazione" maxlength="60" />
			<input type="text" id="prop15" name="valore"title="Titolazione" class="testo" type="text" size="50" value="" maxlength="100"/>
		</td>
	</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'JIRIDE'}">
	<tr id="sez16">
		<td class="etichetta-dato">
			<span id="titleProp16" >Livello riservatezza</span>
		</td>
		<td class="valore-dato">
			<input type="hidden" id="codapp16" name="idconfi" value="${idconfi}" />
			<input type="hidden" id="chiave16" name="chiave" value="protocollazione.wsdm.gare.livelloRiservatezza" maxlength="60" />
			<input type="text" id="prop16" name="valore"title="LivelloRiservatezza" class="testo" type="text" size="50" value="" maxlength="100"/>
		</td>
	</tr>
</c:if>	

<c:if test="${wsdmProtocollo eq 'JDOC'}">
	<tr id="sez42">
		<td class="etichetta-dato">
			<span id="titleProp42" >Sottotipo per buste prequalifica e offerta</span>
		</td>
		<td class="valore-dato">
			<input type="hidden" id="codapp42" name="idconfi" value="${idconfi}" />
			<input type="hidden" id="chiave42" name="chiave" value="protocollazione.wsdm.sottotipoGara" maxlength="100" />
			<input type="text" id="prop42" name="valore" title="Sottotipo per buste prequalifica e offerta" class="testo" type="text" size="50" value="" maxlength="100"/>
		</td>
	</tr>
</c:if>		
	
	<tr id="parametriElencoTitle">
		<td colspan="2">
			<b><br>Parametri specifici per Elenco operatori</b>
		</td>
	</tr>
<c:if test="${wsdmProtocollo eq 'JIRIDE'or wsdmProtocollo eq 'EASYDOC'}">
	<tr id="sez17">
		<td class="etichetta-dato">
			<span id="titleProp17" >Classifica</span>
		</td>
		<td class="valore-dato">
			<input type="hidden" id="codapp17" name="idconfi" value="${idconfi}" />
			<input type="hidden" id="chiave17" name="chiave" value="protocollazione.wsdm.iscrizione.classifica" maxlength="60" />
			<input type="text" id="prop17" name="valore"title="Classifica" class="testo" type="text" size="50" value="" maxlength="100"/>
		</td>
	</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'JIRIDE' or wsdmProtocollo eq 'TITULUS' or wsdmProtocollo eq 'ARCHIFLOW' or wsdmProtocollo eq 'URBI'}">
	<tr id="sez18">
		<td class="etichetta-dato">
			<span id="titleProp18" >Tipo documento</span>
		</td>
		<td class="valore-dato">
			<input type="hidden" id="codapp18" name="idconfi" value="${idconfi}" />
			<input type="hidden" id="chiave18" name="chiave" value="protocollazione.wsdm.iscrizione.tipoDocumento" maxlength="60" />
			<input type="text" id="prop18" name="valore"title="TipoDocumento" class="testo" type="text" size="50" value="" maxlength="100"/>
		</td>
	</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'PALEO' or wsdmProtocollo eq 'INFOR'}">
	<tr id="sez19">
		<td class="etichetta-dato">
			<span id="titleProp19" >Registro</span>
		</td>
		<td class="valore-dato">
			<input type="hidden" id="codapp19" name="idconfi" value="${idconfi}" />
			<input type="hidden" id="chiave19" name="chiave" value="protocollazione.wsdm.iscrizione.registro" maxlength="60" />
			<input type="text" id="prop19" name="valore"title="Registro" class="testo" type="text" size="50" value="" maxlength="100"/>
		</td>
	</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'ENGINEERING'}">
	<tr id="sez20">
		<td class="etichetta-dato">
			<span id="titleProp20" >Indice</span>
		</td>
		<td class="valore-dato">
			<input type="hidden" id="codapp20" name="idconfi" value="${idconfi}" />
			<input type="hidden" id="chiave20" name="chiave" value="protocollazione.wsdm.iscrizione.indice" maxlength="60" />
			<input type="text" id="prop20" name="valore"title="Indice" class="testo" type="text" size="50" value="" maxlength="100"/>
		</td>
	</tr>
	<tr id="sez21">
		<td class="etichetta-dato">
			<span id="titleProp21" >Titolazione</span>
		</td>
		<td class="valore-dato">
			<input type="hidden" id="codapp21" name="idconfi" value="${idconfi}" />
			<input type="hidden" id="chiave21" name="chiave" value="protocollazione.wsdm.iscrizione.titolazione" maxlength="60" />
			<input type="text" id="prop21" name="valore"title="Titolazione" class="testo" type="text" size="50" value="" maxlength="100"/>
		</td>
	</tr>
</c:if>

	<tr id="parametriCatalogoTitle">
		<td colspan="2">
			<b><br>Parametri specifici per Catalogo elettronico</b>
		</td>
	</tr>
<c:if test="${wsdmProtocollo eq 'JIRIDE'or wsdmProtocollo eq 'EASYDOC'}">
	<tr id="sez22">
		<td class="etichetta-dato">
			<span id="titleProp22" >Classifica</span>
		</td>
		<td class="valore-dato">
			<input type="hidden" id="codapp22" name="idconfi" value="${idconfi}" />
			<input type="hidden" id="chiave22" name="chiave" value="protocollazione.wsdm.mepa.classifica" maxlength="60" />
			<input type="text" id="prop22" name="valore"title="Classifica" class="testo" type="text" size="50" value="" maxlength="100"/>
		</td>
	</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'JIRIDE' or wsdmProtocollo eq 'TITULUS' or wsdmProtocollo eq 'ARCHIFLOW' or wsdmProtocollo eq 'URBI'}">
	<tr id="sez23">
		<td class="etichetta-dato">
			<span id="titleProp23" >Tipo documento</span>
		</td>
		<td class="valore-dato">
			<input type="hidden" id="codapp23" name="idconfi" value="${idconfi}" />
			<input type="hidden" id="chiave23" name="chiave" value="protocollazione.wsdm.mepa.tipoDocumento" maxlength="60" />
			<input type="text" id="prop23" name="valore"title="TipoDocumento" class="testo" type="text" size="50" value="" maxlength="100"/>
		</td>
	</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'PALEO' or wsdmProtocollo eq 'INFOR'}">
	<tr id="sez24">
		<td class="etichetta-dato">
			<span id="titleProp24" >Registro</span>
		</td>
		<td class="valore-dato">
			<input type="hidden" id="codapp24" name="idconfi" value="${idconfi}" />
			<input type="hidden" id="chiave24" name="chiave" value="protocollazione.wsdm.mepa.registro" maxlength="60" />
			<input type="text" id="prop24" name="valore"title="Registro" class="testo" type="text" size="50" value="" maxlength="100"/>
		</td>
	</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'ENGINEERING'}">
	<tr id="sez25">
		<td class="etichetta-dato">
			<span id="titleProp25" >Indice</span>
		</td>
		<td class="valore-dato">
			<input type="hidden" id="codapp25" name="idconfi" value="${idconfi}" />
			<input type="hidden" id="chiave25" name="chiave" value="protocollazione.wsdm.mepa.indice" maxlength="60" />
			<input type="text" id="prop25" name="valore"title="Indice" class="testo" type="text" size="50" value="" maxlength="100"/>
		</td>
	</tr>
	<tr id="sez26">
		<td class="etichetta-dato">
			<span id="titleProp26" >Titolazione</span>
		</td>
		<td class="valore-dato">
			<input type="hidden" id="codapp26" name="idconfi" value="${idconfi}" />
			<input type="hidden" id="chiave26" name="chiave" value="protocollazione.wsdm.mepa.titolazione" maxlength="60" />
			<input type="text" id="prop26" name="valore"title="Titolazione" class="testo" type="text" size="50" value="" maxlength="100"/>
		</td>
	</tr>
</c:if>

	<tr id="parametriAvvisiTitle">
		<td colspan="2">
			<b><br>Parametri specifici per Avvisi</b>
		</td>
	</tr>
<c:if test="${wsdmProtocollo eq 'JIRIDE' or wsdmProtocollo eq 'EASYDOC'}">
	<tr id="sez27">
		<td class="etichetta-dato">
			<span id="titleProp27" >Classifica</span>
		</td>
		<td class="valore-dato">
			<input type="hidden" id="codapp27" name="idconfi" value="${idconfi}" />
			<input type="hidden" id="chiave27" name="chiave" value="protocollazione.wsdm.avvisi.classifica" maxlength="60" />
			<input type="text" id="prop27" name="valore"title="Classifica" class="testo" type="text" size="50" value="" maxlength="100"/>
		</td>
	</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'JIRIDE' or wsdmProtocollo eq 'TITULUS' or wsdmProtocollo eq 'ARCHIFLOW' or wsdmProtocollo eq 'URBI'}">
	<tr id="sez28">
		<td class="etichetta-dato">
			<span id="titleProp28" >Tipo documento</span>
		</td>
		<td class="valore-dato">
			<input type="hidden" id="codapp28" name="idconfi" value="${idconfi}" />
			<input type="hidden" id="chiave28" name="chiave" value="protocollazione.wsdm.avvisi.tipoDocumento" maxlength="60" />
			<input type="text" id="prop28" name="valore"title="TipoDocumento" class="testo" type="text" size="50" value="" maxlength="100"/>
		</td>
	</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'PALEO' or wsdmProtocollo eq 'INFOR'}">
	<tr id="sez29">
		<td class="etichetta-dato">
			<span id="titleProp29" >Registro</span>
		</td>
		<td class="valore-dato">
			<input type="hidden" id="codapp29" name="idconfi" value="${idconfi}" />
			<input type="hidden" id="chiave29" name="chiave" value="protocollazione.wsdm.avvisi.registro" maxlength="60" />
			<input type="text" id="prop29" name="valore"title="Registro" class="testo" type="text" size="50" value="" maxlength="100"/>
		</td>
	</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'ENGINEERING'}">
	<tr id="sez30">
		<td class="etichetta-dato">
			<span id="titleProp30" >Indice</span>
		</td>
		<td class="valore-dato">
			<input type="hidden" id="codapp30" name="idconfi" value="${idconfi}" />
			<input type="hidden" id="chiave30" name="chiave" value="protocollazione.wsdm.avvisi.indice" maxlength="60" />
			<input type="text" id="prop30" name="valore"title="Indice" class="testo" type="text" size="50" value="" maxlength="100"/>
		</td>
	</tr>
	<tr id="sez31">
		<td class="etichetta-dato">
			<span id="titleProp31" >Titolazione</span>
		</td>
		<td class="valore-dato">
			<input type="hidden" id="codapp31" name="idconfi" value="${idconfi}" />
			<input type="hidden" id="chiave31" name="chiave" value="protocollazione.wsdm.avvisi.titolazione" maxlength="60" />
			<input type="text" id="prop31" name="valore"title="Titolazione" class="testo" type="text" size="50" value="" maxlength="100"/>
		</td>
	</tr>
</c:if>

	<tr id="parametriAltroTitle">
		<td colspan="2">
			<b><br>Altri parametri</b>
		</td>
	</tr>
<c:if test="${wsdmProtocollo eq 'PRISMA'}">
	<tr id="sez32">
		<td class="etichetta-dato">
			<span id="titleProp32" >Struttura</span>
		</td>
		<td class="valore-dato">
			<input type="hidden" id="codapp32" name="idconfi" value="${idconfi}" />
			<input type="hidden" id="chiave32" name="chiave" value="protocollazione.wsdm.struttura" maxlength="60" />
			<input type="text" id="prop32" name="valore"title="Struttura" class="testo" type="text" size="50" value="" maxlength="100"/>
		</td>
	</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'JIRIDE' or wsdmProtocollo eq 'ARCHIFLOWFA'}">
	<tr id="sez33">
		<td class="etichetta-dato">
			<span id="titleProp33" >Mittente interno</span>
		</td>
		<td class="valore-dato">
			<input type="hidden" id="codapp33" name="idconfi" value="${idconfi}" />
			<input type="hidden" id="chiave33" name="chiave" value="protocollazione.wsdm.mittenteInterno" maxlength="60" />
			<input type="text" id="prop33" name="valore"title="MittenteInterno" class="testo" type="text" size="50" value="" maxlength="100"/>
		</td>
	</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'FOLIUM'}">
	<tr id="sez34">
		<td class="etichetta-dato">
			<span id="titleProp34" >Inserire il codice fiscale del mittente?</span>
		</td>
		<td class="valore-dato">
			<input type="hidden" id="codapp34" name="idconfi" value="${idconfi}" />
			<input type="hidden" id="chiave34" name="chiave" value="protocollazione.wsdm.cfMittente" maxlength="60" />
			<select id="prop34" name="valore" onchange="visualizzaPropDelay(this);">
				<option value='1'>Si</option>
				<option value='0'>No</option>
			</select>
			
		</td>
	</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'JIRIDE' or wsdmProtocollo eq 'ARCHIFLOW' or wsdmProtocollo eq 'ARCHIFLOWFA' or wsdmProtocollo eq 'JDOC'}">
	<tr id="sez35">
		<td class="etichetta-dato">
			<span id="titleProp35" >Mezzo</span>
		</td>
		<td class="valore-dato">
			<input type="hidden" id="codapp35" name="idconfi" value="${idconfi}" />
			<input type="hidden" id="chiave35" name="chiave" value="protocollazione.wsdm.mezzo" maxlength="60" />
			<input type="text" id="prop35" name="valore"title="Mezzo" class="testo" type="text" size="50" value="" maxlength="100"/>
		</td>
	</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'EASYDOC'}">
	<tr id="sez36">
		<td class="etichetta-dato">
			<span id="titleProp36" >Channel code</span>
		</td>
		<td class="valore-dato">
			<input type="hidden" id="codapp36" name="idconfi" value="${idconfi}" />
			<input type="hidden" id="chiave36" name="chiave" value="protocollazione.wsdm.channelCode" maxlength="60" />
			<input type="text" id="prop36" name="valore"title="ChannelCode" class="testo" type="text" size="50" value="" maxlength="100"/>
		</td>
	</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'ENGINEERING' or wsdmProtocollo eq 'PRISMA'}">
	<tr id="sez37">
		<td class="etichetta-dato">
			<span id="titleProp37" >Id unità operativa</span>
		</td>
		<td class="valore-dato">
			<input type="hidden" id="codapp37" name="idconfi" value="${idconfi}" />
			<input type="hidden" id="chiave37" name="chiave" value="protocollazione.wsdm.idUnitaOperativa" maxlength="60" />
			<input type="text" id="prop37" name="valore"title="IdUnitaOperativa" class="testo" type="text" size="50" value="" maxlength="100"/>
		</td>
	</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'ENGINEERING' or wsdmProtocollo eq 'PRISMA' or wsdmProtocollo eq 'INFOR'}">
	<tr id="sez38">
		<td class="etichetta-dato">
			<span id="titleProp38" >Id unità operativa destinataria</span>
		</td>
		<td class="valore-dato">
			<input type="hidden" id="codapp38" name="idconfi" value="${idconfi}" />
			<input type="hidden" id="chiave38" name="chiave" value="protocollazione.wsdm.idUnitaOperativaDestinataria" maxlength="60" />
			<input type="text" id="prop38" name="valore"title="IdUnitaOperativaDestinataria" class="testo" type="text" size="50" value="" maxlength="100"/>
		</td>
	</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'ARCHIFLOWFA' or wsdmProtocollo eq 'URBI'}">
	<tr id="sez39">
		<td class="etichetta-dato">
			<span id="titleProp39" >Tipo documento per invio comunicazione</span>
		</td>
		<td class="valore-dato">
			<input type="hidden" id="codapp39" name="idconfi" value="${idconfi}" />
			<input type="hidden" id="chiave39" name="chiave" value="protocollazione.wsdm.tipoDocumento.inviaComunicazione" maxlength="60" />
			<input type="text" id="prop39" name="valore"title="InviaComunicazione" class="testo" type="text" size="50" value="" maxlength="100"/>
		</td>
	</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'ARCHIFLOWFA'}">
	<tr id="sez40">
		<td class="etichetta-dato">
			<span id="titleProp40" >Supporto</span>
		</td>
		<td class="valore-dato">
			<input type="hidden" id="codapp40" name="idconfi" value="${idconfi}" />
			<input type="hidden" id="chiave40" name="chiave" value="protocollazione.wsdm.supporto" maxlength="60" />
			<input type="text" id="prop40" name="valore"title="Supporto" class="testo" type="text" size="50" value="" maxlength="100"/>
		</td>
	</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'JPROTOCOL'}">
	<tr id="sez41">
		<td class="etichetta-dato">
			<span id="titleProp41" >Tipo assegnazione</span>
		</td>
		<td class="valore-dato">
			<input type="hidden" id="codapp41" name="idconfi" value="${idconfi}" />
			<input type="hidden" id="chiave41" name="chiave" value="protocollazione.wsdm.tipoAssegnazione" maxlength="60" />
			<input type="text" id="prop41" name="valore"title="tipoAssegnazione" class="testo" type="text" size="50" value="" maxlength="100"/>
		</td>
	</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'JDOC'}">
	<tr id="sez43">
		<td class="etichetta-dato">
			<span id="titleProp43" >Sottotipo</span>
		</td>
		<td class="valore-dato">
			<input type="hidden" id="codapp43" name="idconfi" value="${idconfi}" />
			<input type="hidden" id="chiave43" name="chiave" value="protocollazione.wsdm.sottotipoComunicazione" maxlength="60" />
			<input type="text" id="prop43" name="valore" title="Sottotipo" class="testo" type="text" size="50" value="" maxlength="100"/>
		</td>
	</tr>
</c:if>
<tr id="sez44">
	<td class="etichetta-dato">
		<span id="titleProp44" >Posizione testo comunicazione rispetto agli altri allegati della comunicazione?</span>
	</td>
	<td class="valore-dato">
			<input type="hidden" id="codapp44" name="idconfi" value="${idconfi}" />
			<input type="hidden" id="chiave44" name="chiave" value="protocollazione.wsdm.posizioneAllegatoComunicazione" maxlength="60" />
			<select id="prop44" name="valore" onchange="visualizzaPropDelay(this);">
				<option value='1'>Prima degli altri allegati</option>
				<option value='0'>In coda agli altri allegati</option>
			</select>
			
		</td>
</tr>

	<input id="wsdmProtocollo" name="wsdmProtocollo" type="hidden" value="${param.wsdmProtocollo}"/>
	<input id="descri" name="descri" type="hidden" value="${descri}"/>
	<input id="servizio" name="servizio" type="hidden" value="" />
	<input id="tabellatiInDB" name="tabellatiInDB" type="hidden" value="" />
	<input id="codapp" name="codapp" type="hidden" value="${codapp }" />
<tr>
	<td class="comandi-dettaglio" colspan="2" >
		<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:gestisciSubmit()">
		<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:annulla()">
		&nbsp;
	</td>
</tr>

<gene:redefineInsert name="addToAzioni" >
<tr>
	<td class="vocemenulaterale">
		<a href="javascript:gestisciSubmit();" tabindex="1502" title="Salva">Salva</a>
	</td>
</tr>		
<tr>
	<td class="vocemenulaterale">
		<a href="javascript:annulla();" tabindex="1503" title="Annulla">Annulla</a>
	</td>
</tr>
</gene:redefineInsert>

</form>
	