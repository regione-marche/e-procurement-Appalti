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
	<gene:setString name="titoloMaschera" value="Ritiro definitivo plichi"/>
	<gene:redefineInsert name="corpo">
		<table class="dettaglio-notab">

	<c:choose>
		<c:when test='${RISULTATO eq "OK-stampa"}'>
			<tr>
				<td>
					<p>
						<br>Composizione della lista dei plichi ritirati completata.
						<br>
						<br>
						<br><b>Confermi il ritiro definitivo dei plichi presenti nella lista appena prodotta?</b>
						<br>Prima di confermare, accertarsi che la lista dei plichi sia stata stampata correttamente.
						<br>Dopo la conferma, non sar&agrave; più possibile riprodurre tale lista.
						<br><br>
					</p>
				</td>
			</tr>
			<tr>
	     	<td class="comandi-dettaglio" colSpan="2">
  	   		<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:conferma();">&nbsp;
  	   		<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;
	   	 	</td>
  	  </tr>			
		</c:when>
		<c:when test='${RISULTATO eq "KO-stampa"}'>
			<tr>
				<td>
					<br>Composizione della lista dei plichi ritirati non completata.
					<br>
					<br>Riprovare o contattare l'amministratore.
				</td>
			</tr>
			<tr>
	     	<td class="comandi-dettaglio" colSpan="2">
  	   		<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi();">&nbsp;
	   	 	</td>
  	  </tr>
		</c:when>
		<c:when test='${RISULTATO eq "OK-aggiorna"}'>
			<tr>
				<td>
					<br>La stampa ritiro definitivo è stato completato con successo.
					<br>Premere il pulsante 'Chiudi' per ritornare alla lista.
					<br><br>
				</td>
			</tr>
    <tr class="comandi-dettaglio">
      <td align="center" colSpan="2">
  	   		<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi();">&nbsp;
	   	 	</td>
  	  </tr>
		</c:when>
		<c:when test='${RISULTATO eq "KO-aggiorna"}'>
			<tr>
				<td>
					<br>Errore nell'aggiornamento dello stato delle ditte: la stampa ritiro definitivo non è stata completata<br><br>
				</td>
			</tr>
	    <tr class="comandi-dettaglio">
      	<td align="center" colSpan="2">
      		<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi();">&nbsp;
	   	 	</td>
  	  </tr>
		</c:when>
		<c:when test='${RISULTATO eq "OK-annulla" or RISULTATO eq "KO-annulla"}'>
			<tr>
				<td>
					<br>La stampa ritiro definitivo è stato completato con successo.
					<br><br>
				</td>
			</tr>
	    <tr class="comandi-dettaglio">
      	<td align="center" colSpan="2">
       		<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi();">&nbsp;
	   	 	</td>
  	  </tr>
		</c:when>
	</c:choose>
		</table>

<c:choose>
	<c:when test="${empty RISULTATO}" >
		<c:set var="locTipprot" value="${param.tipprot}" />
		<c:set var="locDatap" value="${param.datap}" />
		<c:set var="locOperatoreDatap" value="${param.operatoreDatap}" />
		<c:set var="locNomeFile" value="" />
	</c:when>
	<c:otherwise>
		<c:set var="locTipprot" value="${tipprot}" />
		<c:set var="locDatap" value="${datap}" />
		<c:set var="locOperatoreDatap" value="${operatoreDatap}" />
		<c:set var="locNomeFile" value="${nomeFile}" />
	</c:otherwise>
</c:choose>
	
	<form action="${contextPath}/pg/StampaRitiroDefinitivo.do" method="post" name="stampaRitiroDefinitivoForm" id="stampaRitiroDefinitivoForm" >
		<input type="hidden" name="tipprot" value="${locTipprot}" />
		<input type="hidden" name="datap" value="${locDatap}" />
		<input type="hidden" name="operatoreDatap" value="${locOperatoreDatap}" />
		<input type="hidden" name="nomeFile" value="${locNomeFile}" />
		<input type="hidden" name="metodo" value="" />
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
		  	document.stampaRitiroDefinitivoForm.metodo.value = "downloadRitiroDefinitivo";
		    document.stampaRitiroDefinitivoForm.submit();
  	  }	  
		}

  	function conferma(){
		  if (navigator.appName == "Microsoft Internet Explorer")
		  	window.onblur = null;
		  document.stampaRitiroDefinitivoForm.metodo.value = "aggiornaRitiroDefinitivo";
	    document.stampaRitiroDefinitivoForm.submit();
  	}

		function chiudi(){
			window.opener.rileggi();
			window.close();
		}
		
		function annulla(){
		  if (navigator.appName == "Microsoft Internet Explorer")
		  	window.onblur = null;
	  	document.stampaRitiroDefinitivoForm.metodo.value="annullaRitiroDefinitivo";
	    document.stampaRitiroDefinitivoForm.submit();
		}

	<c:if test='${fn:contains(RISULTATO, "-annulla") or RISULTATO eq "OK-aggiorna"}'>
		chiudi();
	</c:if>

	<c:if test='${RISULTATO eq "OK-stampa"}'>
		downloadFile();
	</c:if>
  </gene:javaScript>
</gene:template>