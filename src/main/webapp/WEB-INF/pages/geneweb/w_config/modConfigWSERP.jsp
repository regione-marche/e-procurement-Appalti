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
	
	arrayProprieta = [["PG","wserp.erp.url" ],["PG","wserpconfigurazione.erp.url"]];
	
	tipoProprieta = [ "",""];											
											
	
	function gestisciSubmit(){
		var mod_ga_pwd = document.getElementById("modifica_ga_password").value;
		if(mod_ga_pwd == '1'){
			_setWSERP_Login()
		}
		var mod_L190_pwd = document.getElementById("modifica_L190_password").value;
		if(mod_L190_pwd == '1'){
			_setWSERP_L190_Login()
		}
		document.formProprieta.submit();
	}
	
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
								$( ("#titoloProp" + (indice+1)) ).attr('title', item.chiave);
								$( ("#codapp" + (indice+1)) ).val(item.codapp);
								$( ("#chiave"+ (indice+1)) ).val(item.chiave);
								$( ("#prop"  + (indice+1)) ).val(item.valore);
							indice++;
						});
					}
				},
				error: function() {
					alert("Errore nel caricamento della propriet&agrave; (codapp=" + codiceApplicazione + "-chiave=" + chiave );
				}
			});
			
			_getWSERP_Login();
			_getWSERP_L190_Login();
		
		}
	});
			
	function reset_ga_Password() {
		document.getElementById("ga_password").value = "";
	}
	
	function resetModifica_ga_Password() {
		document.getElementById("modifica_ga_password").value = "1";
	}

	function reset_L190_Password() {
		document.getElementById("L190_password").value = "";
	}
	
	function resetModifica_L190_Password() {
		document.getElementById("modifica_L190_password").value = "1";
	}
			
-->
</script>

			<% // Valorizzare con il nome del gestore predisposto per il salvataggio delle proprieta' nella W_CONFIG. %>
			<% // (indicare package e classe). Il gestore deve estendere la classe %>
			<% // it.eldasoft.gene.web.struts.w_config.AbstractGestoreProprieta    %>
			<input type="hidden" name="gestoreProprieta" value="" />

			<tr>
				<td colspan="2">
					<b><br>Erp</b>
				</td>
			</tr>
			
			<tr id="">
				<td class="etichetta-dato" >
					<span id="titoloProp1" >Indirizzo URL servizio WSERP</span>
				</td>
				<td class="valore-dato">
					<input type="hidden" id="codapp1" name="codapp" value="PG" />
					<input type="hidden" id="chiave1" name="chiave" value="wserp.erp.url" maxlength="60" />
					<input type="text" id="prop1" name="valore" size="80" maxlength="500" />
					<br>&nbsp;&nbsp;&nbsp;(Esempio: <i>http://&lt;nomeserver&gt;:8080/EldasoftWSERP/services/WSERP</i>)
				</td>
			</tr>
			
			<tr id="">
				<td class="etichetta-dato" >
					<span id="titoloProp2" >Indirizzo URL servizio configurazione WSERP</span>
				</td>
				<td class="valore-dato">
					<input type="hidden" id="codapp2" name="codapp" value="PG" />
					<input type="hidden" id="chiave2" name="chiave" value="wsdmconfigurazione.erp.url" maxlength="60" />
					<input type="text" id="prop2" name="valore" size="80" maxlength="500" />
					<br>&nbsp;&nbsp;&nbsp;(Esempio: <i>http://&lt;nomeserver&gt;:8080/EldasoftWSERP/services/WSERPConfigurazione</i>)
				</td>
			</tr>
			
			<tr>
				<td colspan="2">
					<b><br>Parametri di connessione per integrazione RdA(Richiesta di Acquisto)</b>
				</td>
			</tr>
			<tr id="">
				<td class="etichetta-dato">Utente</td>
				<td class="valore-dato"><input id="ga_username" name="username" title="Utente" class="testo" type="text" size="24" value="" maxlength="100"
				 onfocus="javascript:reset_ga_Password();" onclick="javascript:reset_ga_Password();"/></td>
			</tr>
			<tr id="">
				<td class="etichetta-dato">Password</td>
				<td class="valore-dato"><input id="ga_password" name="password" title="Password" class="testo" type="password" size="24" value="" maxlength="100"
				 onfocus="javascript:reset_ga_Password();" onclick="javascript:reset_ga_Password();" onchange="javascript:resetModifica_ga_Password();"/></td>
			</tr>
			
			<tr>
				<td colspan="2">
					<b><br>Parametri di connessione per integrazione L190</b>
				</td>
			</tr>
			<tr id="">
				<td class="etichetta-dato">Utente</td>
				<td class="valore-dato"><input id="L190_username" name="username" title="Utente" class="testo" type="text" size="24" value="" maxlength="100"
				 onfocus="javascript:reset_L190_Password();" onclick="javascript:reset_L190_Password();"/></td>
			</tr>
			<tr id="">
				<td class="etichetta-dato">Password</td>
				<td class="valore-dato"><input id="L190_password" name="password" title="Password" class="testo" type="password" size="24" value="" maxlength="100"
				 onfocus="javascript:reset_L190_Password();" onclick="javascript:reset_L190_Password();" onchange="javascript:resetModifica_L190_Password();"/></td>
			</tr>
			
			 <input type="hidden" name="modifica_ga_password" id="modifica_ga_password" value="0" />
			 <input type="hidden" name="modifica_L190_password" id="modifica_L190_password" value="0" />
			 


			
