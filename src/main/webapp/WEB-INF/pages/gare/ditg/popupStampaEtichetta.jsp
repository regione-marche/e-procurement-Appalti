<%
	/*
	 * Created on 08-mag-2009
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<gene:template file="popup-template.jsp">
	<gene:setString name="titoloMaschera" value="Composizione etichetta protocollo"/>
	<gene:redefineInsert name="corpo">
		<table class="dettaglio-notab">

	<c:choose>
		<c:when test='${RISULTATO eq "OK-stampa"}'>
			<tr>
				<td>
					<p>
						<br>Composizione completata.
						<br><br>
					</p>
				</td>
			</tr>
			<tr class="comandi-dettaglio" >
	     	<td colSpan="2" align="center">
  	   		<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi();">&nbsp;
     	 	</td>
  	  </tr>			
		</c:when>
		<c:when test='${RISULTATO eq "KO-stampa"}'>
			<tr>
				<td>
					<br>Composizione non completata.
					<br>
					<br>Riprovare o contattare l'amministratore.
					<br><br>
				</td>
			</tr>
			<tr class="comandi-dettaglio">
	     	<td colSpan="2" align="center">
  	   		<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi();">&nbsp;
	   	 	</td>
  	  </tr>
		</c:when>
	</c:choose>
		</table>
	
	<form action="${contextPath}/pg/StampaEtichetta.do" method="post" name="stampaEtichettaForm" id="stampaEtichettaForm" >
		<input type="hidden" name="nomeFile" value="${nomeFile}" />
		<input type="hidden" name="subDir" value="${subDir}" />
		<input type="hidden" name="metodo" value="" />
		<input type="hidden" name="isIE" value="0" />
	<form>

  </gene:redefineInsert>
  <gene:redefineInsert name="gestioneHistory" />
  <gene:redefineInsert name="addHistory" />
  <gene:javaScript>

	  function downloadFile(){
		  if (navigator.appName == "Microsoft Internet Explorer"){
		  	window.onblur = null;
		  	apriDocumento("${pathFile}${nomeFile}");
			} else {
	  		document.stampaEtichettaForm.metodo.value = "downloadEtichetta";
  	  	document.stampaEtichettaForm.submit();
  	  }
		}

		function chiudi(){
		  if (navigator.appName == "Microsoft Internet Explorer")
		  	document.stampaEtichettaForm.isIE.value = "1";

	  	document.stampaEtichettaForm.metodo.value = "cancellaEtichetta";
	    document.stampaEtichettaForm.submit();
			window.close();
		}

	<c:if test='${RISULTATO eq "OK-stampa"}'>
		window.setTimeout('downloadFile();', 200);
	</c:if>

  </gene:javaScript>
</gene:template>