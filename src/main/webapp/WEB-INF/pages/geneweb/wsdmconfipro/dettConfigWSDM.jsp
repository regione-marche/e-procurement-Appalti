
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
	//1		urlf	wsdm.fascicoloprotocollo.url
	//2		urlfc	wsdmconfigurazione.fascicoloprotocollo.url
	//3		-b		pg.wsdm.invioMailPec
	//4		-b		wsdm.protocolloSingoloInvito
	//5		urld	wsdm.documentale.ur
	//6		urldc	wsdmconfigurazione.documentale.url
	//7		-b		wsdm.loginComune
	//8		b		pg.wsdm.applicaFascicolazione
	//9 	-b		wsdm.accediFascicoloDocumentale
	//10	-b		wsdm.gestioneStrutturaCompetente
	//11	b		wsdm.bloccoIndirizzoMittente
	//12	-b		wsdm.applicaRiservatezza
	//13	assAll	wsdm.associaDocumentiProtocollo
	//14	other	wsdm.invioMailPec.delay
	//15	-b		wsdm.documentiDaProtocollo
	//16	-b		wsdm.gestioneERP
	//17	-b		wsdm.accediCreaFascicolo
	//18	-b		wsdm.obbligoClassificaFascicolo
	//19	-b		wsdm.obbligoUfficioFascicolo
	//20	-b		wsdm.accediFascicoloDocumentaleCommessa
	//21	-b		wsdm.tabellatiJiride.letdir
	//22    -b		wsdm.tabellatiJiride.pecUffint
	//23    -bPos	wsdm.posizioneAllegatoComunicazione, ho definito un tipo proprietà specifica per il campo, in quanto non è un campo si/no, ma assume i valori "prima degli altri allegati"/"in coda agli altri allegati" 
	
	
	arrayProprieta = [[idconfi,"wsdm.fascicoloprotocollo.url" ],[idconfi,"wsdmconfigurazione.fascicoloprotocollo.url"],[idconfi,"pg.wsdm.invioMailPec"],
	                  [idconfi,"wsdm.protocolloSingoloInvito"],[idconfi,"wsdm.documentale.url"],[idconfi,"wsdmconfigurazione.documentale.url"],
	                  [idconfi,"wsdm.loginComune"],[idconfi,"pg.wsdm.applicaFascicolazione"],[idconfi,"wsdm.accediFascicoloDocumentale"],
	                  [idconfi,"wsdm.gestioneStrutturaCompetente"],[idconfi,"wsdm.bloccoIndirizzoMittente"],[idconfi,"wsdm.applicaRiservatezza"],
	                  [idconfi,"wsdm.associaDocumentiProtocollo"],[idconfi,"wsdm.invioMailPec.delay"],[idconfi,"wsdm.documentiDaProtocollo"],
	                  [idconfi,"wsdm.gestioneERP"],[idconfi,"wsdm.accediCreaFascicolo"],[idconfi,"wsdm.obbligoClassificaFascicolo"],
	                  [idconfi,"wsdm.obbligoUfficioFascicolo"],[idconfi,"wsdm.accediFascicoloDocumentaleCommessa"],[idconfi,"wsdm.tabellatiJiride.letdir"],
	                  [idconfi,"wsdm.tabellatiJiride.pecUffint"],[idconfi,"wsdm.posizioneAllegatoComunicazione"]];
	                                                                                                                                                                    											
	tipoProprieta = [ "urlf","urlfc","-b","-b","urld","urldc","-b", "b","-b","-b","b","-b","assAll","other","-b","-b","-b","-b","-b","-b","-b","-b","-bPos"];	

	var tabAbilitati = false;
		
	function loadURL(valore, tns, indice) {
			var urlValido=false;
			if (valore != null && valore != "" && valore != "undefined") {
				var _spanURLValido = $("<span/>",{"class":"urltest"});
				if (_testURL(valore, tns) == true) {
					_spanURLValido.css("background-color","#00B512");
					_spanURLValido.css("border", "1px solid #00B512"); 
					_spanURLValido.text("ONLINE");
					urlValido=true;
					if((("urlf"== tipoProprieta[indice] && $("#urlServizioConfProtAttivo").val()==1) || ("urlfc"== tipoProprieta[indice] && $("#urlServizioProtAttivo").val()==1)) ||
					   (("urld"== tipoProprieta[indice] && $("#urlServizioConfDocAttivo").val()==1) || ("urldc"== tipoProprieta[indice] && $("#urlServizioDocAttivo").val()==1))){
						tabAbilitati = true;
						tabellatiEnable();
					}
				} else {
					_spanURLValido.css("background-color","#B70000");
					_spanURLValido.css("border", "1px solid #B70000"); 
					_spanURLValido.text("OFFLINE");
					if(("urlf"== tipoProprieta[indice] || "urlfc"== tipoProprieta[indice] && ($("#urlServizioDocAttivo").val()!=1 || $("#urlServizioConfDocAttivo").val()!=1)) || 
					(("urld"== tipoProprieta[indice] || "urldc"== tipoProprieta[indice]) && ($("#urlServizioProtAttivo").val()!=1 || $("#urlServizioConfProtAttivo").val()!=1))){
						tabellatiDisable();
					}
					
				}
				$( ("#prop"  + (indice+1)) ).append(_spanURLValido);
			}else{
				if(!tabAbilitati){
					tabellatiDisable();
				}
			}
			return urlValido;
			
	}
	
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
							$( ("#titleProp" + (indice+1)) ).attr('title', item.chiave);
							if ("p" == tipoProprieta[indice]) {
								if ("" != "" + item.valore) {
									$( ("#prop"  + (indice+1)) ).html("Password impostata");
								} else {
									$( ("#prop"  + (indice+1)) ).html("Password non impostata");
								}
							} else if ("b" == tipoProprieta[indice] || "-b" == tipoProprieta[indice] ) {
								var valore =item.valore;
                                if("b" == tipoProprieta[indice]){
									if("0" == valore)
									    valore = "No";
	                                else
	                                	valore = "Si";
                                }else{
                                	if("1" == valore)
									    valore = "Si";
	                                else
	                                	valore = "No";
                                }
                                
                                $( ("#prop"  + (indice+1)) ).html(valore);
                                
							} else if ("-bPos" == tipoProprieta[indice] ) {
								var valore =item.valore;
								if("1" == valore)
								    valore = "Prima degli altri allegati";
                                else
                                	valore = "In coda agli altri allegati";
                                $( ("#prop"  + (indice+1)) ).html(valore);
                                
							} else {
								$( ("#prop"  + (indice+1)) ).html(item.valore);
								//Se il campo è un url si testa se è attivo
								if("urlf"== tipoProprieta[indice] || "urlfc"== tipoProprieta[indice] || "urld"== tipoProprieta[indice] || "urldc"== tipoProprieta[indice]){
									var url = item.valore;
									var tns="";
									if("urlf"== tipoProprieta[indice])
										tns = "dm.ws.eldasoft.maggioli.it";
									else if("urlfc"== tipoProprieta[indice])
										tns = "conf.ws.eldasoft.maggioli.it";
									else if("urld"== tipoProprieta[indice])
										tns = "dm.ws.eldasoft.maggioli.it";
									else if("urldc"== tipoProprieta[indice])
										tns = "conf.ws.eldasoft.maggioli.it";
									var esito=loadURL(url,tns, indice);
									if(esito){
										if("urlf"== tipoProprieta[indice]){
											$("#urlServizioProtAttivo").val("1");
										}else if("urlfc"== tipoProprieta[indice]){
											$("#urlServizioConfProtAttivo").val("1");
											$("#urlServizioConfProt").val(item.valore);
										}else if("urld"== tipoProprieta[indice]){
											$("#urlServizioDocAttivo").val("1");
										}else if("urldc"== tipoProprieta[indice]){
											$("#urlServizioConfDocAttivo").val("1");
											$("#urlServizioConfDoc").val(item.valore);
										}
									}
								} else {
									if("assAll" == tipoProprieta[indice]){
										var valore =item.valore;
										if("0" == valore){
										    valore = "No";
										}
		                                else{
		                                	if("1" == valore){
		                                		valore = "Sì, mediante collegamento elemento documentale al protocollo";
		                                	}else{
		                                		if("2" == valore){
		                                			valore = "Sì, mediante aggiunta allegati nel protocollo";
		                                		}
		                                	}
		                                }
										$( ("#prop"  + (indice+1)) ).html(valore);
	                                }else{
	                                	$( ("#prop"  + (indice+1)) ).html(valore);
	                                }
								}
							}
							
							indice++;
							
						});
						
						_getTabellatiInDB();
						
						if($("#urlServizioProtAttivo").val()==1 && $("#urlServizioConfProtAttivo").val()==1){
							//Si visualizzano i campi per la connessione per il servizio protocollo, in base al tipo di protocollo attivo
							$("#servizio").val("FASCICOLOPROTOCOLLO");
							getWSTipoSistemaRemoto($("#urlServizioConfProt").val());
							if($("#prop7").html()=="Si"){
								if($("#tiposistemaremoto").val()=="PALEO" || $("#tiposistemaremoto").val()=="JIRIDE" || $("#tiposistemaremoto").val()=="IRIDE"){
									var tabellatiInDB = $("#tabellatiInDB").val();
									if(tabellatiInDB != "TRUE"){
										_popolaTabellatoByUrl("ruolo","ruoloProtSelect",$("#urlServizioConfProt").val());
										if($("#tiposistemaremoto").val()=="PALEO"){
										_popolaTabellatoByUrl("codiceuo","cuoProtSelect",$("#urlServizioConfProt").val());
										}
									}else{
										_popolaTabellatoByParam("ruolo","ruoloProtSelect", $("#tiposistemaremoto").val(),idconfi);
										if($("#tiposistemaremoto").val()=="PALEO"){
											_popolaTabellatoByParam("codiceuo","cuoProtSelect", $("#tiposistemaremoto").val(),idconfi);
										}
									}
								}
								gestioneCampiLoginConfigurazione("visualizza","Prot",$("#tiposistemaremoto").val());
								_getWSLoginConfigurazione("-1","FASCICOLOPROTOCOLLO","VIS",idconfi);
							}else{
								gestioneCampiLoginConfigurazione("nascondi","Prot","");
							}
							document.pagineForm.wsdmProtocollo.value=$("#tiposistemaremoto").val();
						}else {
							//Si nascondono i campi per la connessione per il servizio protocollo
							gestioneCampiLoginConfigurazione("nascondi","Prot","");
							document.pagineForm.wsdmProtocollo.value="";
						}
						
						if($("#urlServizioDocAttivo").val()==1 && $("#urlServizioConfDocAttivo").val()==1){
							//Si visualizzano i campi per la connessione per il servizio documentale, in base al tipo di protocollo attivo
							$("#servizio").val("DOCUMENTALE");
							getWSTipoSistemaRemoto($("#urlServizioConfDoc").val());
							if($("#prop7").html()=="Si"){
								if($("#tiposistemaremoto").val()=="PALEO" || $("#tiposistemaremoto").val()=="JIRIDE" || $("#tiposistemaremoto").val()=="IRIDE"){
									if(tabellatiInDB != "TRUE"){
										_popolaTabellatoByUrl("ruolo","ruoloDocSelect",$("#urlServizioConfDoc").val());
										if($("#tiposistemaremoto").val()=="PALEO"){
										_popolaTabellatoByUrl("codiceuo","cuoDocSelect",$("#urlServizioConfDoc").val());
										}
									}else{
										_popolaTabellatoByParam("ruolo","ruoloDocSelect", $("#tiposistemaremoto").val(),idconfi);
										if($("#tiposistemaremoto").val()=="PALEO"){
											_popolaTabellatoByParam("codiceuo","cuoDocSelect", $("#tiposistemaremoto").val(),idconfi);
										}
									}
								}
								gestioneCampiLoginConfigurazione("visualizza","Doc",$("#tiposistemaremoto").val());
								_getWSLoginConfigurazione("-1","DOCUMENTALE","VIS",idconfi);
							}else{
								gestioneCampiLoginConfigurazione("nascondi","Doc","");
							}
							document.pagineForm.wsdmDocumentale.value=$("#tiposistemaremoto").val(); 
						}else{
							//Si nascondono i campi per la connessione per il servizio documentale
							gestioneCampiLoginConfigurazione("nascondi","Doc","");
							document.pagineForm.wsdmDocumentale.value="";
						}
						
						if($("#urlServizioProtAttivo").val()==1 && $("#urlServizioConfProtAttivo").val()==1){
							getWSTipoSistemaRemoto($("#urlServizioConfProt").val());
							if(($("#prop3").html()=="No") || $("#tiposistemaremoto").val()!="EASYDOC" && $("#tiposistemaremoto").val()!="JIRIDE" &&
									$("#tiposistemaremoto").val()!="PRISMA" && $("#tiposistemaremoto").val()!="JPROTOCOL"){
								$("#delayMailDoc").hide();
							}
						}else{
							$("#delayMailDoc").hide();
						}
						
						if(document.pagineForm.wsdmDocumentale.value == "" && document.pagineForm.wsdmProtocollo.value == ""){
							//tabellatiDisable();
						}
					}
				},
				error: function() {
					alert("Errore nel caricamento della propriet&agrave");
				}
			});
			//Il campo "Applica gestione Struttura competente?" deve essere visibile solo per JIRIDE
			if(($("#urlServizioProtAttivo").val()==1 && $("#urlServizioConfProtAttivo").val()==1) || ($("#urlServizioDocAttivo").val()==1 && $("#urlServizioConfDocAttivo").val()==1)){
				if($("#tiposistemaremoto").val()==null || $("#tiposistemaremoto").val() ==''){
					var url = $("#urlServizioConfProt").val();
					if($("#urlServizioDocAttivo").val()==1 && $("#urlServizioConfDocAttivo").val()==1)
						url = $("#urlServizioConfDoc").val();
					getWSTipoSistemaRemoto(url);
				}
				if($("#tiposistemaremoto").val()=="JIRIDE"){
					$("#strutturaCompetente").show();
					$("#riservatezza").show();
					$("#fascicoloDocCommessa").show();
				}
				if($("#tiposistemaremoto").val()=="JIRIDE" || $("#tiposistemaremoto").val()=="ARCHIFLOW" || $("#tiposistemaremoto").val()=="ARCHIFLOWFA"){
					$("#bloccoModIndirizzoMit").show();
				}
				if($("#tiposistemaremoto").val()=="JIRIDE"){
					$("#associaDoc").show();
					$("#gestioneERP").show();
					$("#mittenteDaServizio").show();
					$("#indMitenteUffint").show();
				}
				if($("#tiposistemaremoto").val()=="TITULUS" ){
					$("#inserimentoDocDaProtocollo").show();
					$("#obblClassifica").show();
					$("#obblUfficio").show();
				}
				if($("#tiposistemaremoto").val()=="IRIDE" || $("#tiposistemaremoto").val()=="JIRIDE" || $("#tiposistemaremoto").val()=="ENGINEERING" ||  $("#tiposistemaremoto").val()=="ARCHIFLOW" || $("#tiposistemaremoto").val()=="INFOR"
						|| $("#tiposistemaremoto").val()=="JPROTOCOL" ||  $("#tiposistemaremoto").val()=="JDOC"){
					$("#accessoFunzCreaFascicolo").show();
				}
			}
		}
		
		
	});
	
-->
</script>

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
			<tr id="">
				<td class="etichetta-dato">
					<span id="titleProp2" >Indirizzo URL servizio configurazione WSDM</span>
				</td>
				<td class="valore-dato">
					<span id="prop2"></span>
				</td>
			</tr>
			<tr id="">
				<td class="etichetta-dato">
					<span id="titleProp3" >Invio mail protocollo in uscita in carico al documentale?</span>
				</td>
				<td class="valore-dato">
					<span id="prop3" ></span>
				</td>
			</tr>
			<tr id="delayMailDoc">
				<td class="etichetta-dato">
					<span id="titleProp14" >Delay tra le chiamate di invio mail al documentale</span>
				</td>
				<td class="valore-dato">
					<span id="prop14" ></span>
				</td>
			</tr>
			<tr id="">
				<td class="etichetta-dato">
					<span id="titleProp4" >Protocollazione invito a procedure di gara per singolo destinatario?</span>
				</td>
				<td class="valore-dato">
					<span id="prop4" ></span>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<b><br>Archiviazione documentale</b>
				</td>
			</tr>
			<tr id="">
				<td class="etichetta-dato">
					<span id="titleProp5" >Indirizzo URL servizio WSDM</span>
				</td>
				<td class="valore-dato">
					<span id="prop5" ></span>
				</td>
			</tr>
			<tr id="">
				<td class="etichetta-dato">
					<span id="titleProp6" >Indirizzo URL servizio configurazione WSDM</span>
				</td>
				<td class="valore-dato">
					<span id="prop6" ></span>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<b><br>Modalit&agrave; di connessione ai servizi</b>
				</td>
			</tr>
			<tr id="">
				<td class="etichetta-dato">
					<span id="titleProp7" >Parametri di connessione comuni a tutti gli utenti?</span>
				</td>
				<td class="valore-dato">
					<span id="prop7" ></span>
				</td>
			</tr>
			
			<tr id="parametriProtRiga">
				<td colspan="2">
					<b><br>Parametri di connessione al servizio 'Protocollo'</b>
				</td>
			</tr>
			<tr id="utenteProtRiga">
				<td class="etichetta-dato" >
					Utente
				</td>
				<td class="valore-dato">
					<span id="usernameProt" ></span>
				</td>
			</tr>
			<tr id="pwdProtRiga">
				<td class="etichetta-dato" >
					Password
				</td>
				<td class="valore-dato">
					<span id="pwdProt" ></span>
				</td>
			</tr>
			<tr id="ruoloProtRiga">
				<td class="etichetta-dato" >
					Ruolo
				</td>
				<td class="valore-dato">
					<span id="ruoloProt" ></span>
					<select id="ruoloProtSelect" name="ruoloProtSelect" style="display: none;"></select>
				</td>
			</tr>
			<tr id="nomeProtRiga">
				<td class="etichetta-dato" >
					Nome
				</td>
				<td class="valore-dato">
					<span id="nomeProt" ></span>
				</td>
			</tr>
			<tr id="cognomeProtRiga">
				<td class="etichetta-dato" >
					Cognome
				</td>
				<td class="valore-dato">
					<span id="cognomeProt" ></span>
				</td>
			</tr>
			<tr id="cuoProtRiga">
				<td class="etichetta-dato" >
					Codice unità organizzativa
				</td>
				<td class="valore-dato">
					<span id="cuoProt" ></span>
					<select id="cuoProtSelect" name="cuoProtSelect" style="display: none;"></select>
				</td>
			</tr>
			<tr id="idUtenteProtRiga">
				<td class="etichetta-dato" >
					Identificativo utente
				</td>
				<td class="valore-dato">
					<span id="idUtenteProt" ></span>
				</td>
			</tr>
			
			<tr id="iduoProtRiga">
				<td class="etichetta-dato" >
					Identificativo unit&agrave; operativa
				</td>
				<td class="valore-dato">
					<span id="iduoProt" ></span>
				</td>
			</tr>
			
			<tr id="parametriDocRiga">
				<td colspan="2">
					<b><br>Parametri di connessione al servizio 'Archiviazione documentale'</b>
				</td>
			</tr>
			<tr id="utenteDocRiga">
				<td class="etichetta-dato" >
					Utente
				</td>
				<td class="valore-dato">
					<span id="usernameDoc" ></span>
				</td>
			</tr>
			<tr id="pwdDocRiga">
				<td class="etichetta-dato" >
					Password
				</td>
				<td class="valore-dato">
					<span id="pwdDoc" ></span>
				</td>
			</tr>
			<tr id="ruoloDocRiga">
				<td class="etichetta-dato" >
					Ruolo
				</td>
				<td class="valore-dato">
					<span id="ruoloDoc" ></span>
					<select id="ruoloDocSelect" name="ruoloDocSelect" style="display: none;"></select>
				</td>
			</tr>
			<tr id="nomeDocRiga">
				<td class="etichetta-dato" >
					Nome
				</td>
				<td class="valore-dato">
					<span id="nomeDoc" ></span>
				</td>
			</tr>
			<tr id="cognomeDocRiga">
				<td class="etichetta-dato" >
					Cognome
				</td>
				<td class="valore-dato">
					<span id="cognomeDoc" ></span>
				</td>
			</tr>
			<tr id="cuoDocRiga">
				<td class="etichetta-dato" >
					Codice unit&agrave; organizzativa
				</td>
				<td class="valore-dato">
					<span id="cuoDoc" ></span>
					<select id="cuoDocSelect" name="cuoDocSelect" style="display: none;"></select>
				</td>
			</tr>
			<tr id="idUtenteDocRiga">
				<td class="etichetta-dato" >
					Identificativo utente
				</td>
				<td class="valore-dato">
					<span id="idUtenteDoc" ></span>
				</td>
			</tr>
			<tr id="iduoDocRiga">
				<td class="etichetta-dato" >
					Identificativo unità operativa
				</td>
				<td class="valore-dato">
					<span id="iduoDoc" ></span>
				</td>
			</tr>

			<tr>
				<td colspan="2">
					<b><br>Altri parametri</b>
				</td>
			</tr>
			<tr id="">
				<td class="etichetta-dato">
					<span id="titleProp8" >Applica fascicolazione?</span>
				</td>
				<td class="valore-dato">
					<span id="prop8" ></span>
				</td>
			</tr>
			
			<tr id="">
				<td class="etichetta-dato">
					<span id="titleProp9" >Accesso alla funzione 'Fascicolo documentale'?</span>
				</td>
				<td class="valore-dato">
					<span id="prop9" ></span>
				</td>
			</tr>
			<tr id="fascicoloDocCommessa" style="display:none;">
				<td class="etichetta-dato">
					<span id="titleProp20" >Accesso alla funzione 'Fascicolo documentale commessa/appalto'?</span>
				</td>
				<td class="valore-dato">
					<span id="prop20" ></span>
				</td>
			</tr>
			<tr id="accessoFunzCreaFascicolo" style="display:none;">
				<td class="etichetta-dato">
					<span id="titleProp17" >Accesso alla funzione 'Crea fascicolo'?</span>
				</td>
				<td class="valore-dato">
					<span id="prop17" ></span>
				</td>
			</tr>
			<tr id="strutturaCompetente" style="display:none;">
				<td class="etichetta-dato">
					<span id="titleProp10" >Applica gestione Struttura competente? </span>
				</td>
				<td class="valore-dato">
					<span id="prop10" ></span>
				</td>
			</tr>
			<tr id="riservatezza" style="display:none;">
				<td class="etichetta-dato">
					<span id="titleProp12" >Applica riservatezza dei dati?</span>
				</td>
				<td class="valore-dato">
					<span id="prop12" ></span>
				</td>
			</tr>
			<tr id="associaDoc" style="display:none;">
				<td class="etichetta-dato">
					<span id="titleProp13" >Associa documenti buste telematiche al relativo protocollo? </span>
				</td>
				<td class="valore-dato">
					<span id="prop13" ></span>
				</td>
			</tr>
			<tr id="obblClassifica" style="display:none;">
				<td class="etichetta-dato">
					<span id="titleProp18" >Rendere obbligatorio 'Classifica fascicolo'? </span>
				</td>
				<td class="valore-dato">
					<span id="prop18" ></span>
				</td>
			</tr>
			<tr id="obblUfficio" style="display:none;">
				<td class="etichetta-dato">
					<span id="titleProp19" >Rendere obbligatorio 'Ufficio amministrazione organizzativa'? </span>
				</td>
				<td class="valore-dato">
					<span id="prop19" ></span>
				</td>
			</tr>
			<tr id="mittenteDaServizio" style="display:none;">
				<td class="etichetta-dato">
					<span id="titleProp21" >Lettura 'Mittente interno' e 'Indirizzo mittente' da servizio? </span>
				</td>
				<td class="valore-dato">
					<span id="prop21" ></span>
				</td>
			</tr>
			<tr id="indMitenteUffint" style="display:none;">
				<td class="etichetta-dato">
					<span id="titleProp22" >Utilizzare 'Indirizzo mittente' ufficio intestatario? </span>
				</td>
				<td class="valore-dato">
					<span id="prop22" ></span>
				</td>
			</tr>
			<tr id="bloccoModIndirizzoMit" style="display:none;">
				<td class="etichetta-dato">
					<span id="titleProp11" >Blocco modifica 'Indirizzo mittente'? </span>
				</td>
				<td class="valore-dato">
					<span id="prop11" ></span>
				</td>
			</tr>
			<tr id="posizioneTestoComunicazione">
				<td class="etichetta-dato">
					<span id="titleProp23" >Posizione testo comunicazione rispetto agli altri allegati della comunicazione? </span>
				</td>
				<td class="valore-dato">
					<span id="prop23" ></span>
				</td>
			</tr>
			<tr id="inserimentoDocDaProtocollo" style="display:none;">
				<td class="etichetta-dato">
					<span id="titleProp15" >Attiva inserimento documenti di gara da protocollo? </span>
				</td>
				<td class="valore-dato">
					<span id="prop15" ></span>
				</td>
			</tr>
			<tr id="gestioneERP" style="display:none;">
				<td class="etichetta-dato">
					<span id="titleProp16" >Attiva selezione RdA (personalizzazione Porto Genova)? </span>
				</td>
				<td class="valore-dato">
					<span id="prop16" ></span>
				</td>
			</tr>
			
			
			<input id="tiposistemaremoto" type="hidden" value="" />
			<input id="servizio" type="hidden" value="" />
			<input id="idconfi" type="hidden" value="${param.idconfi}" />
			<input id="tabellatiInDB" type="hidden" value="" />
			<input id="urlServizioProtAttivo" type="hidden" value="" />
			<input id="urlServizioConfProtAttivo" type="hidden" value="" />
			<input id="urlServizioDocAttivo" type="hidden" value="" />
			<input id="urlServizioConfDocAttivo" type="hidden" value="" />
			<input id="urlServizioConfDoc" type="hidden" value="" />
			<input id="urlServizioConfProt" type="hidden" value="" />
			<input id="codapp" type="hidden" value="${param.codapp }" />

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
	