<%/*
   * Created on 02-10-2018
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>

<gene:template file="scheda-template.jsp">
<gene:setString name="titoloMaschera" value="Crea nuovo ordine"/>

	<c:set var="modo" value="NUOVO" />
	<gene:redefineInsert name="documentiAzioni" />
	<gene:redefineInsert name="schedaConferma" >
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:creaNuovaGara();" title="Avanti" tabindex="1502">
					Avanti &gt;
				</a>
			</td>
		</tr>
	<c:if test='${not empty param.tipoGara}' >
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:indietro();" title="Indietro" tabindex="1503">
					&lt; Indietro
				</a>
			</td>
		</tr>
	</c:if>
	</gene:redefineInsert>
	<gene:redefineInsert name="schedaAnnulla" >
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:annullaCreazione();" title="Annulla" tabindex="1504">
					Annulla
				</a>
			</td>
		</tr>	
	</gene:redefineInsert>
	<gene:redefineInsert name="corpo">
		<gene:formScheda entita="NSO_ORDINI" gestisciProtezioni="true" >
			<gene:campoScheda>
				<td colspan="2">
					<br><b>Impostare la gara/lotto oggetto dell'ordine:</b>
					<br><br>
				</td>
			</gene:campoScheda>
			<gene:archivio titolo="cig oggetto dell'ordine"
				obbligatorio="true" 
				scollegabile="false"
				inseribile="false"
				lista='gare/nso_ordini/nso_ordini-popup-selezionaCig.jsp' 
				scheda="" 
				schedaPopUp="" 
				campi="GARE.CODCIG;GARE.NOT_GAR;GARE.CODGAR1;GARE.NGARA;GARE.DITTA" 
				chiave=""
				where=""
				formName="formArchivioCigOggettoOrdine">	
				<gene:campoScheda campo="CODCIG" entita="GARE" where="NSO_ORDINI.NGARA=GARE.NGARA" />
				<gene:campoScheda campo="NOT_GAR" entita="GARE" where="NSO_ORDINI.NGARA=GARE.NGARA" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoNote" />
				<gene:campoScheda campo="CODGAR1" entita="GARE" where="NSO_ORDINI.NGARA=GARE.NGARA" visibile="false"/>
				<gene:campoScheda campo="NGARA" title="Codice gara o lotto di gara"/>
				<gene:campoScheda campo="DITTA" entita="GARE" where="NSO_ORDINI.NGARA=GARE.NGARA" visibile="false"/>
				
			</gene:archivio>	
			
			<gene:campoScheda>
				<td class="comandi-dettaglio" colSpan="2">
		      <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annullaCreazione();">&nbsp;&nbsp;&nbsp;&nbsp;
		   	<c:if test='${not empty param.tipoGara}' >
		     	<INPUT type="button" class="bottone-azione" value="&lt; Indietro" title="Indietro" onclick="javascript:indietro();">&nbsp;
		    </c:if>
			<c:choose>
		   	<c:when test='${not empty param.modScheda}' >
		      <INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:associaAppalto();">&nbsp;
				</c:when>
				<c:otherwise>
		      <INPUT type="button" class="bottone-azione" value="Avanti &gt;" title="Avanti" onclick="javascript:creaNuovoOrdineNso();">&nbsp;
				</c:otherwise>
			</c:choose>
				</td>
			</gene:campoScheda>
		</gene:formScheda>
	</gene:redefineInsert>

<gene:javaScript>
	var tipoAppalto="${param.tipoAppalto }";
	var tipoAppalto="1";
	$('#formArchivioCigOggettoOrdine').append('<input type="hidden" name="tipgen" id="tipgen" value="' + tipoAppalto + '" />');
	
	$('#NSO_ORDINI_NGARA').keypress(function(event) {
	    if (event.keyCode == 13) {
	        event.preventDefault();
	    }
	});
	
		function annullaCreazione(){
			bloccaRichiesteServer();
			historyBack();
		}

		function indietro(){
			bloccaRichiesteServer();
			historyBack();
		}

		function creaNuovoOrdineNso(){
			clearMsg();
			var ngara = getValue("NSO_ORDINI_NGARA");
			var modlic = getValue("GARE_MODLICG");
			var bustalotti = getValue("GARE_BUSTALOTTI");
			var listaLavForn ="false";
			if(ngara==null || ngara==""){
				outMsg('Il campo "Codice gara oggetto di ordine" è obbligatorio ', "ERR");
				onOffMsg();
				return;
			}else{
				
				if (verificaPresLavForn() == true) {
					visListaLavorazioni();
				}else{
					document.forms[0].activePage.value = 0;
					document.forms[0].jspPath.value="/WEB-INF/pages/gare/nso_ordini/nso_ordini-scheda.jsp";
					document.forms[0].jspPathTo.value="/WEB-INF/pages/gare/nso_ordini/nso_ordini-scheda.jsp";
					var ngara = getValue("NSO_ORDINI_NGARA");
					document.forms[0].action = document.forms[0].action + "&tipoAppalto=${param.tipoAppalto}&ngara=" + ngara;
					bloccaRichiesteServer();
					document.forms[0].submit();
				}
			}
		}
		
		
	function visListaLavorazioni(){
		var codiceGara = getValue("GARE_CODGAR1");
		var numeroGara = getValue("NSO_ORDINI_NGARA");
		var codiceDitta = getValue("GARE_DITTA");
		var isGaraLottiConOffertaUnica="";
		var bustalotti ="";
		
		var chiave = "DITG.CODGAR5=T:" + codiceGara + ";";
		chiave += "DITG.DITTAO=T:" + codiceDitta + ";";
		chiave += "DITG.NGARA5=T:" + numeroGara;
		//alert(chiave);
		var href = contextPath + "/ApriPagina.do?href=gare/nso_ordini/nso_lavorazioni-lista.jsp";
		href += "&codiceGara="+codiceGara;
		href += "&numeroGara="+numeroGara;
		href += "&codiceDitta="+codiceDitta;
		href += "&key="+chiave;
		var offtel = "";
		href += "&offtel="+offtel;
		href += "&modlicg=" + "";
		href += "&isGaraLottiConOffertaUnica=" + isGaraLottiConOffertaUnica;
		href += "&" + csrfToken;
		document.location.href = href;
	}
	
	function verificaPresLavForn() {
		var codiceGara = getValue("GARE_CODGAR1");
		var numeroGara = getValue("NSO_ORDINI_NGARA");
		var codiceDitta = getValue("GARE_DITTA");
		var _pres = false;
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
			url: "pg/GetNsoPresenzaLavForn.do",
			"data": {
				"codiceGara"  : codiceGara,
				"numeroGara"  : numeroGara,
				"codiceDitta" : codiceDitta
			},
				 
			success: function(data){
				if (data == true) {
					_pres = true;
				} 
			}
		});
		return _pres;
	}
	
		
		

	<c:if test='${not empty param.chiavePadre}'>
		document.forms[0].keyParent.value = ${param.chiavePadre};
	</c:if>

	</gene:javaScript>
</gene:template>