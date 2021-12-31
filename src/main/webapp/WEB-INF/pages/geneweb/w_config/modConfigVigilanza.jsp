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


<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wserpsupporto.js"></script>
<script type="text/javascript">

<!--
	
	//IMPORTANTE:
	//           Poichè le properties sono individuate in base alla posizione consecutiva nella pagina, se si modifica l'ordine delle properties si deve modificare il riferimento
	//			 a tale property nella funzione
	
	arrayProprieta = [["PG","it.eldasoft.sil.pg.vigilanza.nomeApplicativo" ],["PG","it.eldasoft.sil.pg.vigilanza.ws.url"],["W_","it.eldasoft.sil.pl.vigilanza.ws.login.comune"],["W_","it.eldasoft.sil.pl.vigilanza.ws.login.utente"],["W_","it.eldasoft.sil.pl.vigilanza.ws.login.password"],["W_","it.eldasoft.sil.pl.vigilanza.ossreg"],["PG","it.eldasoft.inviodaticig.ws.url"],["PG","it.eldasoft.inviodaticig.nomeApplicativo"]];
	                                                                                                                                                                    											
	tipoProprieta = [ "","","-b","","p","-b","",""];											
	
	
	$(document).ready(function() {
		if (arrayProprieta != null && arrayProprieta.length > 0 &&
				tipoProprieta != null && tipoProprieta.length > 0   &&
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
							 if ("b" == tipoProprieta[indice] || "-b" == tipoProprieta[indice]) {
								$( ("#titoloProp" + (indice+1)) ).attr('title', item.chiave);
								$( ("#codapp" + (indice+1)) ).val(item.codapp);
								$( ("#chiave"+ (indice+1)) ).val(item.chiave);
								var valore =item.valore;
								if("b" == tipoProprieta[indice]){
									if("0" == valore)
										valore = "0";
									else
										valore = "1";
								}else{
									if("1" == valore)
										valore = "1";
									else
										valore = "0";
								}
								$( ("#prop"  + (indice+1)) ).find('option[value="' + valore + '"]').attr("selected",true);
							}else if("p" == tipoProprieta[indice]){
								_getPassword(arrayProprieta[indice][1],arrayProprieta[indice][0]);
							}
							else{
								$( ("#titoloProp" + (indice+1)) ).attr('title', item.chiave);
								$( ("#codapp" + (indice+1)) ).val(item.codapp);
								$( ("#chiave"+ (indice+1)) ).val(item.chiave);
								$( ("#prop"  + (indice+1)) ).val(item.valore);
							}
							indice++;
						});
					}
				},
				error: function() {
					alert("Errore nel caricamento della propriet&agrave; (codapp=" + codiceApplicazione + "-chiave=" + chiave );
				}
			});
			gestioneCampiLogin();
		
		}
	});
	
	function _getPassword(chiave, codapp){
		$.ajax({
			type: "POST",
			dataType: "json",
			async: false,
			beforeSend: function(x) {
				if(x && x.overrideMimeType) {
					x.overrideMimeType("application/json;charset=UTF-8");
				}
			},
			url: "pg/GetW_CONFIGProperty.do",
			data: "chiave=" + chiave + "&codapp=" + codapp + "&criptato=1",
			success: function(data){
				if (data) {
					$("#password").val(data.propertyW_CONFIG);
	        	}
			},
			error: function(e){
				alert("Errore durante la lettura del campo password");
			}
		});
	}
	
	function _setPassword() {
		var mod = $("#password_mod").val();
		if(mod == "1"){
			var codapp = arrayProprieta[4][0];
			var chiave = arrayProprieta[4][1];
			var valore = $("#password").val();
			$.ajax({
				type: "GET",
				async: false,
				url: "pg/SetW_CONFIG.do",
				data : {
					codapp: codapp,
					chiave: chiave,
					valore: valore,
					criptato: 1
				}
			});
		}
	}
	
	function gestisciSubmit(){
		_setPassword();
		document.formProprieta.submit();
	}
	
	function setPasswordMod(){
		$("#password_mod").val("1");
	}
	
	function gestioneCampiLogin(){
		var valore = $("#prop3").val();
		if(valore==1){
			$("#sezUtente").show();
			$("#sezPassword").show();		
		}else{
			$("#sezUtente").hide();
			$("#prop4").val("");
			$("#sezPassword").hide();	
			$("#password").val("");	
			$("#password_mod").val("1");
		}
	}		
			
-->
</script>

			<% // Valorizzare con il nome del gestore predisposto per il salvataggio delle proprieta' nella W_CONFIG. %>
			<% // (indicare package e classe). Il gestore deve estendere la classe %>
			<% // it.eldasoft.gene.web.struts.w_config.AbstractGestoreProprieta    %>
			<input type="hidden" name="gestoreProprieta" value="" />
			<input type="hidden" name="sezione" value="Integrazione Vigilanza" />

			<tr>
				<td colspan="2">
					<b><br>Invio dati Vigilanza Comunicazioni</b>
				</td>
			</tr>
			
			<tr id="">
				<td class="etichetta-dato" >
					<span id="titoloProp2" >Indirizzo URL del servizio</span>
				</td>
				<td class="valore-dato">
					<input type="hidden" id="codapp2" name="codapp" />
					<input type="hidden" id="chiave2" name="chiave" maxlength="60" />
					<input type="text" id="prop2" name="valore" size="80" maxlength="500" />
					<br>&nbsp;&nbsp;&nbsp;<i>(Esempio URL del servizio: http://localhost:8080/WS2Vigilanza/services/SitatWS)<i>
				</td>
			</tr>
			<tr id="">
				<td class="etichetta-dato" >
					<span id="titoloProp1" >Nome applicativo integrazione (da impostare se diverso da 'Vigilanza')</span>
				</td>
				<td class="valore-dato">
					<input type="hidden" id="codapp1" name="codapp" />
					<input type="hidden" id="chiave1" name="chiave" maxlength="60" />
					<input type="text" id="prop1" name="valore" size="80" maxlength="500" />
				</td>
			</tr>
			
			
			<tr id="sez3">
				<td class="etichetta-dato">
					<span id="titleProp3" >Credenziali comuni a tutti gli utenti?</span>
				</td>
				<td class="valore-dato">
					<input type="hidden" id="codapp3" name="codapp" />
					<input type="hidden" id="chiave3" name="chiave" maxlength="60" />
					<select id="prop3" name="valore" onchange="javascript:gestioneCampiLogin(this);">>
						<option value='1'>Si</option>
						<option value='0'>No</option>
					</select>
					
				</td>
			</tr>
			
			<tr id="sezUtente">
				<td class="etichetta-dato" >
					<span id="titoloProp4" >Utente</span>
				</td>
				<td class="valore-dato">
					<input type="hidden" id="codapp4" name="codapp" />
					<input type="hidden" id="chiave4" name="chiave" maxlength="60" />
					<input type="text" id="prop4" name="valore" size="24" maxlength="500" />
				</td>
			</tr>
			<tr id="sezPassword">
				<td class="etichetta-dato">Password</td>
				<td class="valore-dato">
					<input id="password" name="vigilanza_psw" title="vigilanza_psw" class="testo" type="password" size="24" value="" maxlength="100" autocomplete=off onchange="javascript:setPasswordMod();"/>
					<input id="password_mod" type="hidden" value="0"/>
				</td>
			</tr>
			<tr id="sez6">
				<td class="etichetta-dato">
					<span id="titleProp6" >Integrazione relativa ad un sistema regionale?</span>
				</td>
				<td class="valore-dato">
					<input type="hidden" id="codapp6" name="codapp" />
					<input type="hidden" id="chiave6" name="chiave"  maxlength="60" />
					<select id="prop6" name="valore" >
						<option value='1'>Si</option>
						<option value='0'>No</option>
					</select>
					
				</td>
			</tr>
			
			
			<tr>
				<td colspan="2">
					<b><br>Invio dati Vigilanza Richiesta CIG</b>
				</td>
			</tr>
			<tr id="">
				<td class="etichetta-dato" >
					<span id="titoloProp7" >Indirizzo URL del servizio</span>
				</td>
				<td class="valore-dato">
					<input type="hidden" id="codapp7" name="codapp" />
					<input type="hidden" id="chiave7" name="chiave" maxlength="60" />
					<input type="text" id="prop7" name="valore" size="80" maxlength="500" />
					<br>&nbsp;&nbsp;&nbsp;<i>(Esempio URL del servizio: http://localhost:8080/WS2Vigilanza/services/EldasoftSimogWS)<i>
				</td>
			</tr>
			<tr id="">
				<td class="etichetta-dato" >
					<span id="titoloProp8" >Nome applicativo integrazione (da impostare se diverso da 'Vigilanza')</span>
				</td>
				<td class="valore-dato">
					<input type="hidden" id="codapp8" name="codapp" />
					<input type="hidden" id="chiave8" name="chiave" maxlength="60" />
					<input type="text" id="prop8" name="valore" size="80" maxlength="500" />
				</td>
			</tr>
