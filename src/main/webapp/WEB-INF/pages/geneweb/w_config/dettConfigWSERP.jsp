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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wserpsupporto.js"></script>
<script type="text/javascript">
<!--

	arrayProprieta = [["PG","wserp.erp.url" ],["PG","wserpconfigurazione.erp.url"]];
	                                                                                                                                                                    											
	tipoProprieta = [ "urlf","urlfc"];	
	
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
								$( ("#prop"  + (indice+1)) ).html(item.valore);
								//Se il campo è un url si testa se è attivo
								if("urlf"== tipoProprieta[indice] || "urlfc"== tipoProprieta[indice]){
									var url = item.valore;
									var tns="";
									if("urlf"== tipoProprieta[indice])
										tns = "erp.ws.eldasoft.maggioli.it";
									else if("urlfc"== tipoProprieta[indice])
										tns = "conf.ws.eldasoft.maggioli.it";
									loadURL(url,tns, indice);
								}	
							
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
	
-->
</script>

			<tr>
				<td colspan="2">
					<b><br>Erp</b>
				</td>
			</tr>
			<tr id="">
				<td class="etichetta-dato">
					<span id="titleProp1" >Indirizzo URL servizio WSERP</span>
				</td>
				<td class="valore-dato">
					<span id="prop1"></span>
				</td>
			</tr>
			<tr id="">
				<td class="etichetta-dato">
					<span id="titleProp2" >Indirizzo URL servizio configurazione WSERP</span>
				</td>
				<td class="valore-dato">
					<span id="prop2"></span>
				</td>
			</tr>

			<tr>
				<td colspan="2">
					<b><br>Parametri di connessione per integrazione RdA(Richiesta di Acquisto)</b>
				</td>
			</tr>
			<tr id="">
				<td class="etichetta-dato" >
					<span id="" >Utente</span>
				</td>
				<td class="valore-dato">
					<span id="ga_username" ></span>
				</td>
			</tr>
			<tr id="">
				<td class="etichetta-dato" >
					<span id="" >Password</span>
				</td>
				<td class="valore-dato">
					<span id="ga_password" ></span>
				</td>
			</tr>

			<tr>
				<td colspan="2">
					<b><br>Parametri di connessione per integrazione L190</b>
				</td>
			</tr>
			<tr id="">
				<td class="etichetta-dato" >
					<span id="" >Utente</span>
				</td>
				<td class="valore-dato">
					<span id="L190_username" ></span>
				</td>
			</tr>
			
			<tr id="">
				<td class="etichetta-dato" >
					<span id="" >Password</span>
				</td>
				<td class="valore-dato">
					<span id="L190_password" ></span>
				</td>
			</tr>
			