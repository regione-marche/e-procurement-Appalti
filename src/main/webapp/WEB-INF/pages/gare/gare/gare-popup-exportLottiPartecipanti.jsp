<%/*
   * Created on 01-10-2024
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE LA POPUP PER EXPORT OFFERTA PREZZI E RISULTATO EXPORT
  // OFFERTA PREZZI

%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:choose>
	<c:when test='${not empty param.codgar}'>
		<c:set var="locCodgar" value="${param.codgar}" />
	</c:when>
	<c:otherwise>
		<c:set var="locCodgar" value="${codgar}" />
	</c:otherwise>
</c:choose>

<table style="width:100%;">
	<tr>
		<td>
<div class="contenitore-popup">

<c:choose>
	<c:when test="${param.check eq 'false' and empty esito}">
	<table class="lista">
		<tr>
			<td>
				<br>&nbsp;
				<p>
					Mediante questa funzione è possibile <b>esportare</b> in formato excel la lista dei lotti della gara con i relativi partecipanti.
					<br>
					<br>
		 			Non è possibile procedere all'export perché prima deve essere dettagliato l'invio offerta ai singoli lotti della gara per ogni ditta nella lista.
					<br>&nbsp;
					<br>&nbsp;
				</p>
			</td>
		</tr>
		<tr class="comandi-dettaglio">
			<td colspan="2">
				<input type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;
			</td>
		</tr>
	</table>
	</c:when>
	<c:when test='${param.check ne "false" && empty esito}'>
		<form action="${contextPath}/pg/EseguiExportLottiPartecipanti.do" method="post" name="exportLottiPartecipanti" >
			<table class="dettaglio-notab">
		    <tr>
		    	<td colspan="2">
		    	  <p>
		    	 	Mediante questa funzione è possibile <b>esportare</b> in formato excel la lista dei lotti della gara con i relativi partecipanti.
					  <br>
					  <br>
					  È possibile eseguire l'export ordinando i risultati per lotto o per partecipante.
			      </p>
				  <br>
					<p>Impostare il formato di export:</p>
					<input type="radio" id="lotti" name="tipologiaExcel" value="lotti" checked>
					<label for="lotti">Ordinato per lotto</label><br>
					<input type="radio" id="partecipanti" name="tipologiaExcel" value="partecipanti">
					<label for="partecipanti">Ordinato per partecipante</label><br>
				  <br>&nbsp;
				  <br>&nbsp;
		      	</td>
			</tr>
		  	<tr>
			    <td class="comandi-dettaglio" colspan="2">
						<input type="button" class="bottone-azione" value="Esporta" title="Esporta" onclick="javascript:esporta();">&nbsp;
						<input type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
						<input type="hidden" name="codgar" id="codgar" value="${locCodgar}"/>
						<input type="hidden" name="check" id="check" value="${param.check}"/>
						<input type="hidden" name="fase" id="fase" value="${param.fasgar}"/>
		  	  </td>
			  </tr>
			</table>
		</form>
	</c:when>
	<c:otherwise>
		<table class="lista">
	    <tr>
	    	<td>
				<br>&nbsp;
	    		<p>
					Esportazione completata.
				  <br>&nbsp;
				  <br>&nbsp;
				</p>
	      </td>
	    </tr>
	    <tr class="comandi-dettaglio">
	      <td colspan="2">
					<input type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;
	      </td>
	    </tr>
		</table>
		<form action="${contextPath}/DownloadTempFile.do" method="post" name="downloadExportForm" >
			<input type="hidden" name="nomeTempFile" value="${nomeFile}" />
		</form>
	</c:otherwise>
</c:choose>

</div>
		</td>
	</tr>
</table>
<script type="text/javascript">
<!--
	function esporta(){
		bloccaRichiesteServer();
		document.exportLottiPartecipanti.submit();
	}

	function annulla(){
		window.close();
	}

	function initPagina(){
		checkAttivaBloccoPaginaPopup();
		document.getElementById("titolomaschera").innerHTML = "Esporta dettaglio partecipazione in excel della gara ${locCodgar}";
			
	
	<c:if test='${esito eq "ok"}'>
		window.onfocus=fnFocus;
		window.setTimeout("document.downloadExportForm.submit();", 250);
	</c:if>
	}

-->
</script>