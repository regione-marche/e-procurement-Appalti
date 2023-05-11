<%/*
   * Created on 20-nov-2014
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI EDIT
  // DEL DETTAGLIO DI UN DOCUMENTO ASSOCIATO RELATIVA AI DATI EFFETTIVI
%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

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

	arrayProprieta = [["PG","wsdm.fascicoloprotocollo.url" ],["PG","wsdmconfigurazione.fascicoloprotocollo.url"],["PG","pg.wsdm.invioMailPec"],["PG","wsdm.protocolloSingoloInvito"],["PG","wsdm.documentale.url"],["PG","wsdmconfigurazione.documentale.url"],["PG","wsdm.loginComune"],["PG","pg.wsdm.applicaFascicolazione"],["PG","wsdm.accediFascicoloDocumentale"],["PG","wsdm.gestioneStrutturaCompetente"],["PG","wsdm.bloccoIndirizzoMittente"],["PG","wsdm.stazioneAppaltante"],["PG","wsdm.applicaRiservatezza"],["PG","wsdm.associaDocumentiProtocollo"],["PG","wsdm.invioMailPec.delay"]];
	                                                                                                                                                                    											
	tipoProprieta = [ "urlf","urlfc","-b","-b","urld","urldc","-b", "b","-b","-b","b","tab","-b","assAll","other"];	
	
	function getDescrizioneEnte(indice,codein) {
		var result = false;
        if (codein != "") {
			$.ajax({
				type: "GET",
				dataType: "json",
				async: false,
				beforeSend: function(x) {
				if(x && x.overrideMimeType) {
					x.overrideMimeType("application/json;charset=UTF-8");
				   }
				},
				url: "${pageContext.request.contextPath}/pg/GetWSDMDescrizioneEnte.do",
				data: "codein=" + codein,
				success: function(data){
					if (data.enteEsistente == true) {
						$("#prop" + (indice + 1)).html(data.CODEIN.value + " - " + data.NOMEIN.value);
						result = true;
					}
				},
				error: function(e){
					alert("Ente: errore durante la lettura delle informazioni");
				}
			});
		}
		return result;
	}

	
	
	function loadURL(valore, tns, indice) {
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
	
	$(document).ready(function() {
		if (arrayProprieta != null && arrayProprieta.length > 0 && 
				tipoProprieta != null && tipoProprieta.length > 0 &&
				arrayProprieta.length <= tipoProprieta.length) {
			
			$.ajax({
				url: '${pageContext.request.contextPath}/GetProprieta.do',
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
                                
							} else if ("tab" == tipoProprieta[indice]){
								getDescrizioneEnte(indice,item.valore);
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
						
						if($("#prop7").html()=="Si" && $("#urlServizioProtAttivo").val()==1 && $("#urlServizioConfProtAttivo").val()==1){
							//Si visualizzano i campi per la connessione per il servizio protocollo, in base al tipo di protocollo attivo
							//$("#servizio").val("FASCICOLOPROTOCOLLO");
							getWSTipoSistemaRemoto($("#urlServizioConfProt").val());
							if($("#tiposistemaremoto").val()=="PALEO" || $("#tiposistemaremoto").val()=="JIRIDE" || $("#tiposistemaremoto").val()=="IRIDE"){
								_popolaTabellatoByUrl("ruolo","ruoloProtSelect",$("#urlServizioConfProt").val());
								if($("#tiposistemaremoto").val()=="PALEO")
									_popolaTabellatoByUrl("codiceuo","cuoProtSelect",$("#urlServizioConfProt").val());
							}
							gestioneCampiLoginConfigurazione("visualizza","Prot",$("#tiposistemaremoto").val());
							_getWSLoginConfigurazione("-1","FASCICOLOPROTOCOLLO","VIS");
						}else {
							//Si nascondono i campi per la connessione per il servizio protocollo
							gestioneCampiLoginConfigurazione("nascondi","Prot","");
						}
						
						if($("#prop7").html()=="Si" && $("#urlServizioDocAttivo").val()==1 && $("#urlServizioConfDocAttivo").val()==1){
							//Si visualizzano i campi per la connessione per il servizio documentale, in base al tipo di protocollo attivo
							$("#servizio").val("DOCUMENTALE");
							getWSTipoSistemaRemoto($("#urlServizioConfDoc").val());
							if($("#tiposistemaremoto").val()=="PALEO" || $("#tiposistemaremoto").val()=="JIRIDE" || $("#tiposistemaremoto").val()=="IRIDE"){
								_popolaTabellatoByUrl("ruolo","ruoloDocSelect",$("#urlServizioConfDoc").val());
								if($("#tiposistemaremoto").val()=="PALEO")
									_popolaTabellatoByUrl("codiceuo","cuoDocSelect",$("#urlServizioConfDoc").val());
							}
							gestioneCampiLoginConfigurazione("visualizza","Doc",$("#tiposistemaremoto").val());
							_getWSLoginConfigurazione("-1","DOCUMENTALE","VIS");
						}else{
							//Si nascondono i campi per la connessione per il servizio documentale
							gestioneCampiLoginConfigurazione("nascondi","Doc","");
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
					}
				},
				error: function() {
					alert("Errore nel caricamento della propriet&agrave; (codapp=" + codiceApplicazione + "-chiave=" + chiave );
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
				}
				if($("#tiposistemaremoto").val()=="JIRIDE" || $("#tiposistemaremoto").val()=="ARCHIFLOW" || $("#tiposistemaremoto").val()=="ARCHIFLOWFA"){
					$("#bloccoModIndirizzoMit").show();
				}
				if($("#tiposistemaremoto").val()=="JIRIDE"){
					$("#associaDoc").show();
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
					<span id="titleProp15" >Delay tra richieste ripetute di invio mail (in millisecondi)</span>
				</td>
				<td class="valore-dato">
					<span id="prop15" ></span>
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
					<span id="titleProp13" >Applica riservatezza dei dati?</span>
				</td>
				<td class="valore-dato">
					<span id="prop13" ></span>
				</td>
			</tr>
			<tr id="associaDoc" style="display:none;">
				<td class="etichetta-dato">
					<span id="titleProp14" >Associa documenti buste telematiche al relativo protocollo? </span>
				</td>
				<td class="valore-dato">
					<span id="prop14" ></span>
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
			<tr id="">
				<td class="etichetta-dato">
					<span id="titleProp12" >Stazione appaltante per cui viene gestita l'integrazione</span>
				</td>
				<td class="valore-dato">
					<span id="prop12" ></span>
				</td>
			</tr>
			
			<input id="tiposistemaremoto" type="hidden" value="" />
			<input id="servizio" type="hidden" value="" />
			<input id="urlServizioProtAttivo" type="hidden" value="" />
			<input id="urlServizioConfProtAttivo" type="hidden" value="" />
			<input id="urlServizioDocAttivo" type="hidden" value="" />
			<input id="urlServizioConfDocAttivo" type="hidden" value="" />
			<input id="urlServizioConfDoc" type="hidden" value="" />
			<input id="urlServizioConfProt" type="hidden" value="" />