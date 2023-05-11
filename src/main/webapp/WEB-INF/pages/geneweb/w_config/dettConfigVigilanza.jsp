
<%
	/*
	 * Created on 08/05/2020
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

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

	
	
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
<script type="text/javascript">
<!--

	arrayProprieta = [["PG","it.eldasoft.sil.pg.vigilanza.nomeApplicativo" ],["PG","it.eldasoft.sil.pg.vigilanza.ws.url"],["W_","it.eldasoft.sil.pl.vigilanza.ws.login.comune"],["W_","it.eldasoft.sil.pl.vigilanza.ws.login.utente"],["W_","it.eldasoft.sil.pl.vigilanza.ws.login.password"],["W_","it.eldasoft.sil.pl.vigilanza.ossreg"],["PG","it.eldasoft.inviodaticig.ws.url"],["PG","it.eldasoft.inviodaticig.nomeApplicativo"]];
	                                                                                                                                                                    											
	tipoProprieta = [ "","url","-b","","passw","-b","url",""];	
	
	function _testURL(url, tns) {
	var _URLvalido = false;
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		timeout: 3000,
		beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetURL.do",
		data: "url=" + url + "?wsdl&tns=" + tns, 
		success: function(data){
			if (data == true) {
				_URLvalido = true;
			} 
		}
	});
	return _URLvalido;
}
	function loadURL(valore, tns, indice) {
			if (valore != null && valore != "" && valore != "undefined") {
				var _spanURLValido = $("<span/>",{"class":"urltest"});
				if (_testURL(valore, tns) == true) {
					_spanURLValido.css("background-color","#00B512");
					_spanURLValido.css("border", "1px solid #00B512"); 
					_spanURLValido.text("ONLINE");
					
				} else {
					_spanURLValido.css("background-color","#B70000");
					_spanURLValido.css("border", "1px solid #B70000"); 
					_spanURLValido.text("OFFLINE");
				}
				$( ("#prop"  + (indice+1)) ).append(_spanURLValido);
			}
			
			
	}
	
	function gestioneCampiLogin(valore){
		if(valore=="1"){
			$("#sezUtente").show();
			$("#sezPassword").show();		
		}else{
			$("#sezUtente").hide();
			$("#sezPassword").hide();	
		}
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
							if ("passw" == tipoProprieta[indice]) {
								if (item.valore) {
									$( ("#prop"  + (indice+1)) ).html("*******");
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
							}else {
								$( ("#prop"  + (indice+1)) ).html(item.valore);
							}
								//Se il campo è un url si testa se è attivo
							if("url"== tipoProprieta[indice]){
								var url = item.valore;
								var tns;
								if(arrayProprieta[indice][1].indexOf("vigilanza")>0){
									tns="ws.vigilanza.sil.eldasoft.it";
								}else if(arrayProprieta[indice][1].indexOf("inviodaticig")>0){
									tns="ws.simog.eldasoft.it";
								}
								loadURL(url,tns, indice);
							}	
							if(arrayProprieta[indice][1] == "it.eldasoft.sil.pl.vigilanza.ws.login.comune"){
								gestioneCampiLogin(item.valore);
							}
							indice++;
							
						});
					}
				},
				error: function() {
					alert("Errore nel caricamento della propriet&agrave; (codapp=" + codiceApplicazione + "-chiave=" + chiave );
				}
			});	
		}
		
		
	});
	
-->
</script>


<tr>
				<td colspan="2">
					<b><br>Invio dati Vigilanza Comunicazioni</b>
				</td>
			</tr>
			<tr id="">
				<td class="etichetta-dato">
					<span id="titleProp2" >Indirizzo URL del servizio</span>
				</td>
				<td class="valore-dato">
					<span id="prop2"></span>
				</td>
			</tr>
			<tr id="">
				<td class="etichetta-dato">
					<span id="titleProp1" >Nome applicativo integrazione (da impostare se diverso da 'Vigilanza')</span>
				</td>
				<td class="valore-dato">
					<span id="prop1"></span>
				</td>
			</tr>
			<tr id="">
				<td class="etichetta-dato">
					<span id="titleProp3" >Credenziali comuni a tutti gli utenti?</span>
				</td>
				<td class="valore-dato">
					<span id="prop3"></span>
				</td>
			</tr>
			<tr id="sezUtente">
				<td class="etichetta-dato">
					<span id="titleProp4" >Utente</span>
				</td>
				<td class="valore-dato">
					<span id="prop4"></span>
				</td>
			</tr>
			<tr id="sezPassword">
				<td class="etichetta-dato">
					<span id="titleProp5" >Password</span>
				</td>
				<td class="valore-dato">
					<span id="prop5"></span>
				</td>
			</tr>
			<tr id="">
				<td class="etichetta-dato">
					<span id="titleProp6" >Integrazione relativa ad un sistema regionale?</span>
				</td>
				<td class="valore-dato">
					<span id="prop6"></span>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<b><br>Invio dati Vigilanza Richiesta CIG</b>
				</td>
			</tr>
			<tr id="">
				<td class="etichetta-dato">
					<span id="titleProp7" >Indirizzo URL del servizio</span>
				</td>
				<td class="valore-dato">
					<span id="prop7"></span>
				</td>
			</tr>
			<tr id="">
				<td class="etichetta-dato">
					<span id="titleProp8" >Nome applicativo integrazione (da impostare se diverso da 'Vigilanza')</span>
				</td>
				<td class="valore-dato">
					<span id="prop8"></span>
				</td>
			</tr>