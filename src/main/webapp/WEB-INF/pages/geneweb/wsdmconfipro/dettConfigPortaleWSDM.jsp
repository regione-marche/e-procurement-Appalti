
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

	<c:set var="idconfi" value="${param.idconfi}"/>
	<c:set var="wsdmconfi_descri" value="${param.descri}"/>
	<c:set var="wsdmProtocollo" value="${param.wsdmProtocollo}"/>
	<c:set var="key" value='WSDMCONFI.ID=N:${idconfi}' scope="request" />
	<c:set var="keyParent" value='WSDMCONFI.ID=N:${idconfi}' scope="request" />

<style type="text/css">
	span.urltest {
		font-size: 10px;
		float: right;
		color: white;
		padding-left: 10px;
		padding-right: 10px;
		padding-top: 1px;
		padding-bottom: 1px;
		margin-right: 5px;
		vertical-align: middle;
		-moz-border-radius-topleft: 4px; 
		-webkit-border-top-left-radius: 4px; 
		-khtml-border-top-left-radius: 4px; 
		border-top-left-radius: 4px; 
		-moz-border-radius-topright: 4px;
		-webkit-border-top-right-radius: 4px;
		-khtml-border-top-right-radius: 4px;
		border-top-right-radius: 4px;
		-moz-border-radius-bottomleft: 4px; 
		-webkit-border-bottom-left-radius: 4px; 
		-khtml-border-bottom-left-radius: 4px; 
		border-bottom-left-radius: 4px; 
		-moz-border-radius-bottomright: 4px;
		-webkit-border-bottom-right-radius: 4px;
		-khtml-border-bottom-right-radius: 4px;
		border-bottom-right-radius: 4px;
	}

	span.esempio {
		color: #727272;
		border-top: 2px solid white;
		float: right;
		margin-right: 5px;
	}
	
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wsdmsupporto.js?v=${sessionScope.versioneModuloAttivo}"></script>
<script type="text/javascript">
<!--
	
	function modifica(){
		document.pagineForm.modalita.value = "modifica";
		document.pagineForm.submit();
	}
	
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
	//44		-"bPos"	protocollazione.wsdm.posizioneAllegatoComunicazione
	//45		""		protocollazione.wsdm.descUnitaOperativaDestinataria
	//46		""		protocollazione.wsdm.tipoFirma
	//47		""		protocollazione.wsdm.stipule.classifica
	//48		""		protocollazione.wsdm.stipule.tipoDocumento
	//49		""		protocollazione.wsdm.stipule.registro
	//50		""		protocollazione.wsdm.stipule.indice
	//51		""		protocollazione.wsdm.stipule.titolazione
	
	arrayProprieta = [[idconfi,"protocollazione.wsdm.url"],[idconfi,"protocollazione.wsdm.username"],[idconfi,"protocollazione.wsdm.password"],[idconfi,"protocollazione.wsdm.nome"],
		[idconfi,"protocollazione.wsdm.cognome"],[idconfi,"protocollazione.wsdm.ruolo"],[idconfi,"protocollazione.wsdm.codiceUO"],[idconfi,"protocollazione.wsdm.idUtente"],
		[idconfi,"protocollazione.wsdm.gare.classifica"],[idconfi,"protocollazione.wsdm.gare.tipoDocumento"],[idconfi,"protocollazione.wsdm.gare.tipoDocumento.prequalifica"],
		[idconfi,"protocollazione.wsdm.gare.registro"],[idconfi,"protocollazione.wsdm.gare.struttura"],[idconfi,"protocollazione.wsdm.gare.indice"],
		[idconfi,"protocollazione.wsdm.gare.titolazione"],[idconfi,"protocollazione.wsdm.gare.livelloRiservatezza"],[idconfi,"protocollazione.wsdm.iscrizione.classifica"],
		[idconfi,"protocollazione.wsdm.iscrizione.tipoDocumento"],[idconfi,"protocollazione.wsdm.iscrizione.registro"],[idconfi,"protocollazione.wsdm.iscrizione.indice"],
		[idconfi,"protocollazione.wsdm.iscrizione.titolazione"],[idconfi,"protocollazione.wsdm.mepa.classifica"],[idconfi,"protocollazione.wsdm.mepa.tipoDocumento"],
		[idconfi,"protocollazione.wsdm.mepa.registro"],[idconfi,"protocollazione.wsdm.mepa.indice"],[idconfi,"protocollazione.wsdm.mepa.titolazione"],
		[idconfi,"protocollazione.wsdm.avvisi.classifica"],[idconfi,"protocollazione.wsdm.avvisi.tipoDocumento"],[idconfi,"protocollazione.wsdm.avvisi.registro"],
		[idconfi,"protocollazione.wsdm.avvisi.indice"],[idconfi,"protocollazione.wsdm.avvisi.titolazione"],[idconfi,"protocollazione.wsdm.struttura"],
		[idconfi,"protocollazione.wsdm.mittenteInterno"],[idconfi,"protocollazione.wsdm.cfMittente"],[idconfi,"protocollazione.wsdm.mezzo"],[idconfi,"protocollazione.wsdm.channelCode"],
		[idconfi,"protocollazione.wsdm.idUnitaOperativa"],[idconfi,"protocollazione.wsdm.idUnitaOperativaDestinataria"],[idconfi,"protocollazione.wsdm.tipoDocumento.inviaComunicazione"],
		[idconfi,"protocollazione.wsdm.supporto"],[idconfi,"protocollazione.wsdm.tipoAssegnazione"],[idconfi,"protocollazione.wsdm.sottotipoGara"],[idconfi,"protocollazione.wsdm.sottotipoComunicazione"],
		[idconfi,"protocollazione.wsdm.posizioneAllegatoComunicazione"],[idconfi,"protocollazione.wsdm.descUnitaOperativaDestinataria"],[idconfi,"protocollazione.wsdm.tipoFirma"],
		[idconfi,"protocollazione.wsdm.stipule.classifica"],[idconfi,"protocollazione.wsdm.stipule.tipoDocumento"],[idconfi,"protocollazione.wsdm.stipule.registro"],[idconfi,"protocollazione.wsdm.stipule.indice"],
		[idconfi,"protocollazione.wsdm.stipule.titolazione"]];
	                                                                                                                                                                    											
	tipoProprieta = [ "url","","psw","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","-b","","","","","","","","","","-bPos","","","","","","",""];	

	var tabAbilitati = false;
	
	$(document).ready(function() {
		if (arrayProprieta != null && arrayProprieta.length > 0 && 
				tipoProprieta != null && tipoProprieta.length > 0 &&
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
							if($("#prop"  + (indice+1)).length ){
								$( ("#titleProp" + (indice+1)) ).attr('title', item.chiave);
								if ("psw" == tipoProprieta[indice]) {
									if (item.valore) {
										$( ("#prop"  + (indice+1)) ).html("********");
									} else {
										$( ("#prop"  + (indice+1)) ).html("");
									}
								} else if ("b" == tipoProprieta[indice] || "-b" == tipoProprieta[indice] ) {
									var valore =item.valore;
									if("b" == tipoProprieta[indice]){
										if("0" == valore)
											valore = "No";
										else if("1" == valore)
											valore = "Si";
									}else{
										if("1" == valore)
											valore = "Si";
										else if("0" == valore)
											valore = "No";
									}
									
									$( ("#prop"  + (indice+1)) ).html(valore);
									
								}else if ("-bPos" == tipoProprieta[indice] ) {
									var valore =item.valore;
									if("1" == valore)
									    valore = "Prima degli altri allegati";
	                                else
	                                	valore = "In coda agli altri allegati";
	                                $( ("#prop"  + (indice+1)) ).html(valore);
	                                
								}else {
									$( ("#prop"  + (indice+1)) ).html(item.valore);
									if("url" == tipoProprieta[indice]){
										loadURL(item.valore,indice);
									}
								}
							}
						indice++;
						});
					}
				},
				error: function() {
					alert("Errore nel caricamento della propriet&agrave");
				}
			});
			//Il campo "Applica gestione Struttura competente?" deve essere visibile solo per JIRIDE
		}
		
		
		//nascondo i titoli delle sezioni non visualizzate
		var sezioneAttiva = false;
		var i = 2;
		for(i=2;i<=8 && !sezioneAttiva;i++){
			console.log($("#titleProp"  + (i)).length);
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
		for(i=32;i<=46 && !sezioneAttiva;i++){
			if($("#titleProp"  + (i)).length ){
				sezioneAttiva = true;
			}
		}
		if(!sezioneAttiva){
			$("#parametriAltroTitle").hide();
		}
		
		sezioneAttiva = false;
		for(i=47;i<=51 && !sezioneAttiva;i++){
			if($("#titleProp"  + (i)).length ){
				sezioneAttiva = true;
			}
		}
		if(!sezioneAttiva){
			$("#parametriStipuleTitle").hide();
		}
	});
	
	function loadURL(valore, indice) {
		var tns = "dm.ws.eldasoft.maggioli.it";
		var urlValido=false;
		if (valore != null && valore != "" && valore != "undefined") {
			var _spanURLValido = $("<span/>",{"class":"urltest"});
			if (_testURL(valore, tns) == true) {
				_spanURLValido.css("background-color","#00B512");
				_spanURLValido.css("border", "1px solid #00B512"); 
				_spanURLValido.text("ONLINE");
				urlValido=true;
			} else {
				_spanURLValido.css("background-color","#B70000");
				_spanURLValido.css("border", "1px solid #B70000"); 
				_spanURLValido.text("OFFLINE");
			}
			$( ("#prop"  + (indice+1)) ).append(_spanURLValido);
		}
		return urlValido;
		
}
	
-->
</script>

<c:set var="temp" value="${gene:callFunction2('it.eldasoft.sil.pg.tags.funzioni.IsWsdmPortaleConfigPresentiFunction',  pageContext, idconfi)}" scope="request"/>		

<tr>
	<td colspan="2">
		<b><br>Protocollo</b>
	</td>
</tr>
<tr id="">
	<td class="etichetta-dato">
		<span id="titleProp1" >Indirizzo URL servizio WSDM</span>
	</td>
	<td class="valore-dato">
		<span id="prop1"></span>
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
		<span id="prop2"></span>
	</td>
</tr>
<c:if test="${wsdmProtocollo ne 'IRIDE' and wsdmProtocollo ne 'JIRIDE' and wsdmProtocollo ne 'INFOR' and wsdmProtocollo ne 'PROTSERVICE' and wsdmProtocollo ne 'JPROTOCOL'}">
<tr id="sez3">
	<td class="etichetta-dato">
		<span id="titleProp3" >Password</span>
	</td>
		<td class="valore-dato">
		<span id="prop3"></span>
	</td>
</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'IRIDE' or wsdmProtocollo eq 'JIRIDE' or wsdmProtocollo eq 'PALEO'}">
<tr id="sez6">
	<td class="etichetta-dato">
		<span id="titleProp6" >Ruolo</span>
	</td>
		<td class="valore-dato">
		<span id="prop6"></span>
	</td>
</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'PALEO'}">
<tr id="sez4">
	<td class="etichetta-dato">
		<span id="titleProp4" >Nome</span>
	</td>
		<td class="valore-dato">
		<span id="prop4"></span>
	</td>
</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'PALEO' || wsdmProtocollo eq 'LAPISOPERA'}">
<tr id="sez5">
	<td class="etichetta-dato">
		<span id="titleProp5" >Cognome</span>
	</td>
		<td class="valore-dato">
		<span id="prop5"></span>
	</td>
</tr>
</c:if>

<c:if test="${wsdmProtocollo eq 'PALEO'}">
<tr id="sez7">
	<td class="etichetta-dato">
		<span id="titleProp7" >Codice unità organizzativa</span>
	</td>
		<td class="valore-dato">
		<span id="prop7"></span>
	</td>
</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'ENGINEERING'}">
<tr id="sez8">
	<td class="etichetta-dato">
		<span id="titleProp8" >Id Utente</span>
	</td>
		<td class="valore-dato">
		<span id="prop8"></span>
	</td>
</tr>
</c:if>			
			
<tr id="parametriProcedureGaraTitle">
	<td colspan="2">
		<b><br>Parametri specifici per procedure di Gara</b>
	</td>
</tr>
<c:if test="${wsdmProtocollo eq 'JIRIDE' or wsdmProtocollo eq 'EASYDOC' or wsdmProtocollo eq 'NUMIX' or wsdmProtocollo eq 'ITALPROT'}">
<tr id="sez9">
	<td class="etichetta-dato">
		<span id="titleProp9" >Classifica</span>
	</td>
		<td class="valore-dato">
		<span id="prop9"></span>
	</td>
</tr>
</c:if>	
<c:if test="${wsdmProtocollo eq 'JIRIDE' or wsdmProtocollo eq 'TITULUS' or wsdmProtocollo eq 'ARCHIFLOW' or wsdmProtocollo eq 'ARCHIFLOWFA' or wsdmProtocollo eq 'URBI' or wsdmProtocollo eq 'JDOC' or wsdmProtocollo eq 'INFOR' or wsdmProtocollo eq 'ITALPROT'}">
<tr id="sez10">
	<td class="etichetta-dato">
		<span id="titleProp10" >Tipo documento</span>
	</td>
		<td class="valore-dato">
		<span id="prop10"></span>
	</td>
</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'ARCHIFLOWFA' or wsdmProtocollo eq 'URBI'}">
<tr id="sez11">
	<td class="etichetta-dato">
		<span id="titleProp11" >Tipo documento per busta prequalifica</span>
	</td>
		<td class="valore-dato">
		<span id="prop11"></span>
	</td>
</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'PALEO' or wsdmProtocollo eq 'INFOR'}">
<tr id="sez12">
	<td class="etichetta-dato">
		<span id="titleProp12" >Registro</span>
	</td>
		<td class="valore-dato">
		<span id="prop12"></span>
	</td>
</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'JPROTOCOL'}">
<tr id="sez13">
	<td class="etichetta-dato">
		<span id="titleProp13" >Struttura</span>
	</td>
		<td class="valore-dato">
		<span id="prop13"></span>
	</td>
</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'ENGINEERING'}">
<tr id="sez14">
	<td class="etichetta-dato">
		<span id="titleProp14" >Indice</span>
	</td>
		<td class="valore-dato">
		<span id="prop14"></span>
	</td>
</tr>
<tr id="sez15">
	<td class="etichetta-dato">
		<span id="titleProp15" >Titolazione</span>
	</td>
		<td class="valore-dato">
		<span id="prop15"></span>
	</td>
</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'JIRIDE'}">
<tr id="sez16">
	<td class="etichetta-dato">
		<span id="titleProp16" >Livello riservatezza</span>
	</td>
		<td class="valore-dato">
		<span id="prop16"></span>
	</td>
</tr>
</c:if>	
<c:if test="${wsdmProtocollo eq 'JDOC'}">
<tr id="sez42">
	<td class="etichetta-dato">
		<span id="titleProp42" >Sottotipo per buste prequalifica e offerta</span>
	</td>
		<td class="valore-dato">
		<span id="prop42"></span>
	</td>
</tr>
</c:if>			
			
<tr id="parametriElencoTitle">
	<td colspan="2">
		<b><br>Parametri specifici per Elenco operatori</b>
	</td>
</tr>
<c:if test="${wsdmProtocollo eq 'JIRIDE' or wsdmProtocollo eq 'EASYDOC' or wsdmProtocollo eq 'NUMIX' or wsdmProtocollo eq 'ITALPROT'}">
<tr id="sez17">
	<td class="etichetta-dato">
		<span id="titleProp17" >Classifica</span>
	</td>
		<td class="valore-dato">
		<span id="prop17"></span>
	</td>
</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'JIRIDE' or wsdmProtocollo eq 'TITULUS' or wsdmProtocollo eq 'ARCHIFLOW' or wsdmProtocollo eq 'URBI' or wsdmProtocollo eq 'DOCER' or wsdmProtocollo eq 'JDOC' or wsdmProtocollo eq 'INFOR' or wsdmProtocollo eq 'ITALPROT'}">
<tr id="sez18">
	<td class="etichetta-dato">
		<span id="titleProp18" >Tipo documento</span>
	</td>
		<td class="valore-dato">
		<span id="prop18"></span>
	</td>
</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'PALEO' or wsdmProtocollo eq 'INFOR'}">
<tr id="sez19">
	<td class="etichetta-dato">
		<span id="titleProp19" >Registro</span>
	</td>
		<td class="valore-dato">
		<span id="prop19"></span>
	</td>
</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'ENGINEERING'}">
<tr id="sez20">
	<td class="etichetta-dato">
		<span id="titleProp20" >Indice</span>
	</td>
		<td class="valore-dato">
		<span id="prop20"></span>
	</td>
</tr>
<tr id="sez21">
	<td class="etichetta-dato">
		<span id="titleProp21" >Titolazione</span>
	</td>
		<td class="valore-dato">
		<span id="prop21"></span>
	</td>
</tr>
</c:if>
			
			
<tr id="parametriCatalogoTitle">
	<td colspan="2">
		<b><br>Parametri specifici per Catalogo elettronico</b>
	</td>
</tr>
<c:if test="${wsdmProtocollo eq 'JIRIDE' or wsdmProtocollo eq 'EASYDOC' or wsdmProtocollo eq 'NUMIX' or wsdmProtocollo eq 'ITALPROT'}">
<tr id="sez22">
	<td class="etichetta-dato">
		<span id="titleProp22" >Classifica</span>
	</td>
		<td class="valore-dato">
		<span id="prop22"></span>
	</td>
</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'JIRIDE' or wsdmProtocollo eq 'TITULUS' or wsdmProtocollo eq 'ARCHIFLOW' or wsdmProtocollo eq 'URBI' or wsdmProtocollo eq 'DOCER' or wsdmProtocollo eq 'JDOC' or wsdmProtocollo eq 'INFOR' or wsdmProtocollo eq 'ITALPROT'}">
<tr id="sez23">
	<td class="etichetta-dato">
		<span id="titleProp23" >Tipo documento</span>
	</td>
		<td class="valore-dato">
		<span id="prop23"></span>
	</td>
</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'PALEO' or wsdmProtocollo eq 'INFOR'}">
<tr id="sez24">
	<td class="etichetta-dato">
		<span id="titleProp24" >Registro</span>
	</td>
		<td class="valore-dato">
		<span id="prop24"></span>
	</td>
</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'ENGINEERING'}">
<tr id="sez25">
	<td class="etichetta-dato">
		<span id="titleProp25" >Indice</span>
	</td>
		<td class="valore-dato">
		<span id="prop25"></span>
	</td>
</tr>
<tr id="sez26">
	<td class="etichetta-dato">
		<span id="titleProp26" >Titolazione</span>
	</td>
		<td class="valore-dato">
		<span id="prop26"></span>
	</td>
</tr>
</c:if>
			
<tr id="parametriAvvisiTitle">
	<td colspan="2">
		<b><br>Parametri specifici per Avvisi</b>
	</td>
</tr>
<c:if test="${wsdmProtocollo eq 'JIRIDE' or wsdmProtocollo eq 'EASYDOC' or wsdmProtocollo eq 'NUMIX' or wsdmProtocollo eq 'ITALPROT'}">
<tr id="sez27">
	<td class="etichetta-dato">
		<span id="titleProp27" >Classifica</span>
	</td>
		<td class="valore-dato">
		<span id="prop27"></span>
	</td>
</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'JIRIDE' or wsdmProtocollo eq 'TITULUS' or wsdmProtocollo eq 'ARCHIFLOW' or wsdmProtocollo eq 'URBI' or wsdmProtocollo eq 'JDOC' or wsdmProtocollo eq 'INFOR' or wsdmProtocollo eq 'ITALPROT'}">
<tr id="sez28">
	<td class="etichetta-dato">
		<span id="titleProp28" >Tipo documento</span>
	</td>
		<td class="valore-dato">
		<span id="prop28"></span>
	</td>
</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'PALEO' or wsdmProtocollo eq 'INFOR'}">
<tr id="sez29">
	<td class="etichetta-dato">
		<span id="titleProp29" >Registro</span>
	</td>
		<td class="valore-dato">
		<span id="prop29"></span>
	</td>
</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'ENGINEERING'}">
<tr id="sez30">
	<td class="etichetta-dato">
		<span id="titleProp30" >Indice</span>
	</td>
		<td class="valore-dato">
		<span id="prop30"></span>
	</td>
</tr>
<tr id="sez31">
	<td class="etichetta-dato">
		<span id="titleProp31" >Titolazione</span>
	</td>
		<td class="valore-dato">
		<span id="prop31"></span>
	</td>
</tr>
</c:if>


<tr id="parametriStipuleTitle">
	<td colspan="2">
		<b><br>Parametri specifici per Stipule (modulo e-Contract)</b>
	</td>
</tr>
<c:if test="${wsdmProtocollo eq 'JIRIDE'}">
<tr id="sez47">
	<td class="etichetta-dato">
		<span id="titleProp47" >Classifica</span>
	</td>
		<td class="valore-dato">
		<span id="prop47"></span>
	</td>
</tr>
<tr id="sez48">
	<td class="etichetta-dato">
		<span id="titleProp48" >Tipo documento</span>
	</td>
		<td class="valore-dato">
		<span id="prop48"></span>
	</td>
</tr>
<tr id="sez49" style="display: none;">
	<td class="etichetta-dato">
		<span id="titleProp49" >Registro</span>
	</td>
		<td class="valore-dato">
		<span id="prop49"></span>
	</td>
</tr>
<tr id="sez50" style="display: none;">
	<td class="etichetta-dato">
		<span id="titleProp50" >Indice</span>
	</td>
		<td class="valore-dato">
		<span id="prop50"></span>
	</td>
</tr>
<tr id="sez51" style="display: none;">
	<td class="etichetta-dato">
		<span id="titleProp51" >Titolazione</span>
	</td>
		<td class="valore-dato">
		<span id="prop51"></span>
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
		<span id="prop32"></span>
	</td>
</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'JIRIDE' or wsdmProtocollo eq 'ARCHIFLOWFA'}">
<tr id="sez33">
	<td class="etichetta-dato">
		<span id="titleProp33" >Mittente interno</span>
	</td>
		<td class="valore-dato">
		<span id="prop33"></span>
	</td>
</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'FOLIUM'}">
<tr id="sez34">
	<td class="etichetta-dato">
		<span id="titleProp34" >Inserire il codice fiscale del mittente?</span>
	</td>
		<td class="valore-dato">
		<span id="prop34"></span>
	</td>
</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'JIRIDE' or wsdmProtocollo eq 'ARCHIFLOW' or wsdmProtocollo eq 'ARCHIFLOWFA' or wsdmProtocollo eq 'JDOC' or wsdmProtocollo eq 'NUMIX'}">
<tr id="sez35">
	<td class="etichetta-dato">
		<span id="titleProp35" >Mezzo</span>
	</td>
		<td class="valore-dato">
		<span id="prop35"></span>
	</td>
</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'EASYDOC'}">
<tr id="sez36">
	<td class="etichetta-dato">
		<span id="titleProp36" >Channel code</span>
	</td>
		<td class="valore-dato">
		<span id="prop36"></span>
	</td>
</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'ENGINEERING' or wsdmProtocollo eq 'PRISMA'}">
<tr id="sez37">
	<td class="etichetta-dato">
		<span id="titleProp37" >Id unità operativa</span>
	</td>
		<td class="valore-dato">
		<span id="prop37"></span>
	</td>
</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'ENGINEERING' or wsdmProtocollo eq 'ENGINEERINGDOC' or wsdmProtocollo eq 'PRISMA' or wsdmProtocollo eq 'INFOR' or wsdmProtocollo eq 'DOCER'}">
<tr id="sez38">
	<td class="etichetta-dato">
		<span id="titleProp38" >Id unità operativa destinataria</span>
	</td>
		<td class="valore-dato">
		<span id="prop38"></span>
	</td>
</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'DOCER'}">
<tr id="sez45">
	<td class="etichetta-dato">
		<span id="titleProp45" >Descrizione unità operativa destinataria</span>
	</td>
		<td class="valore-dato">
		<span id="prop45"></span>
	</td>
</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'DOCER'}">
<tr id="sez46">
	<td class="etichetta-dato">
		<span id="titleProp46" >Tipo di firma</span>
	</td>
		<td class="valore-dato">
		<span id="prop46"></span>
	</td>
</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'ARCHIFLOWFA' or wsdmProtocollo eq 'URBI'}">
<tr id="sez39">
	<td class="etichetta-dato">
		<span id="titleProp39" >Tipo documento per invio comunicazione</span>
	</td>
		<td class="valore-dato">
		<span id="prop39"></span>
	</td>
</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'ARCHIFLOWFA'}">
<tr id="sez40">
	<td class="etichetta-dato">
		<span id="titleProp40" >Supporto</span>
	</td>
		<td class="valore-dato">
		<span id="prop40"></span>
	</td>
</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'JPROTOCOL'}">
<tr id="sez41">
	<td class="etichetta-dato">
		<span id="titleProp41" >Tipo assegnazione</span>
	</td>
		<td class="valore-dato">
		<span id="prop41"></span>
	</td>
</tr>
</c:if>
<c:if test="${wsdmProtocollo eq 'JDOC'}">
<tr id="sez43">
	<td class="etichetta-dato">
		<span id="titleProp43" >Sottotipo</span>
	</td>
		<td class="valore-dato">
		<span id="prop43"></span>
	</td>
</tr>
</c:if>
<tr id="sez44">
	<td class="etichetta-dato">
		<span id="titleProp44" >Posizione testo comunicazione rispetto agli altri allegati della comunicazione?</span>
	</td>
		<td class="valore-dato">
		<span id="prop44"></span>
	</td>
</tr>

			
			
			<input id="servizio" type="hidden" value="" />
			<input id="idconfi" type="hidden" value="${param.idconfi}" />

			<tr>
				<td class="comandi-dettaglio" colspan="2" >
					<c:if test='${gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD")}'>
						<INPUT type="button" id="btnModificaConfig" class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:modifica()" >
					</c:if>
					&nbsp;
				</td>
			</tr>
			<gene:redefineInsert name="addToAzioni" >
			 <td class="vocemenulaterale">
				<c:if test='${gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD")}'>
	     				<a href="javascript:modifica();" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' >
						${gene:resource("label.tags.template.dettaglio.schedaModifica")}
						</a>
				</c:if>	
			</td>
			</gene:redefineInsert>
	