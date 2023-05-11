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
<gene:setString name="titoloMaschera" value="Nuova gara"/>

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
		<gene:formScheda entita="GARE" gestisciProtezioni="true" >
			<gene:campoScheda>
				<td colspan="2">
					<br><b>Impostare la gara o lotto di gara oggetto di rilancio dell'offerta economica:</b>
					<br><br>
				</td>
			</gene:campoScheda>
			<gene:archivio titolo="gare oggetto di rilancio"
				obbligatorio="true" 
				scollegabile="false"
				inseribile="false"
				lista='gare/gare/trovaGaraRilancio/gare-popup-selezionaGaraRilancio.jsp' 
				scheda="" 
				schedaPopUp="" 
				campi="GARE.NGARA" 
				functionId="skip"
				chiave=""
				formName="formArchivioGareOggettoRilancio">	
				<gene:campoScheda campo="PRECED" title="Codice gara o lotto di gara"/>
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
		      <INPUT type="button" class="bottone-azione" value="Avanti &gt;" title="Avanti" onclick="javascript:creaNuovaGara();">&nbsp;
				</c:otherwise>
			</c:choose>
				</td>
			</gene:campoScheda>
		</gene:formScheda>
	</gene:redefineInsert>

<gene:javaScript>
	var tipoAppalto="${param.tipoAppalto }";
	$('#formArchivioGareOggettoRilancio').append('<input type="hidden" name="tipgen" id="tipgen" value="' + tipoAppalto + '" />');
	
	$('#GARE_PRECED').keypress(function(event) {
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
			document.location.href = "${pageContext.request.contextPath}/pg/InitNuovaGara.do?" + csrfToken;
		}

		function creaNuovaGara(){
			clearMsg();
			var preced = getValue("GARE_PRECED");
			if(preced==null || preced==""){
				outMsg('Il campo "Codice gara oggetto di rilancio off.economica" è obbligatorio ', "ERR");
				onOffMsg();
				return;
			}else{
				document.forms[0].activePage.value = 0;
				document.forms[0].jspPath.value="/WEB-INF/pages/gare/gare/gare-scheda.jsp";
				document.forms[0].jspPathTo.value="/WEB-INF/pages/gare/gare/gare-scheda.jsp";
				var preced = getValue("GARE_PRECED");
				document.forms[0].action = document.forms[0].action + "&tipoAppalto=${param.tipoAppalto}&tipoGara=${param.tipoGara}&proceduraTelematica=${param.proceduraTelematica}&modalitaPresentazione=${param.modalitaPresentazione}&preced=" + preced;
				bloccaRichiesteServer();
				document.forms[0].submit();
			}
		}

	<c:if test='${not empty param.chiavePadre}'>
		document.forms[0].keyParent.value = ${param.chiavePadre};
	</c:if>

	</gene:javaScript>
</gene:template>