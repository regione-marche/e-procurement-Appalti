<%/*
   * Created on 06-10-2009
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
<c:set var="locLotti" value="dei lotti" />



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
	<c:when test='${empty RISULTATO or RISULTATO eq "KO"}'>
		<form action="${contextPath}/pg/EseguiExportLottiGara.do" method="post" name="exportLottiGara" >
			<table class="dettaglio-notab">
		    <tr>
		    	<td colspan="2">
		    	  <p>
		    	 	Mediante questa funzione è possibile <b>esportare</b> in formato excel la lista ${locLotti} della gara.
			      </p>
				  <br>&nbsp;
				  <br><b>ATTENZIONE: salvare il file prodotto prima di procedere alla sua modifica.</b>
				  <br>&nbsp;
				  <br><b>ATTENZIONE: il file prodotto potr&agrave; essere usato in importazione solo dalla stessa gara da cui &egrave; stato esportato.</b>
				  <br>&nbsp;
				  <br>&nbsp;
		      	</td>
			</tr>
		  	<tr>
			    <td class="comandi-dettaglio" colspan="2">
						<input type="button" class="bottone-azione" value="Esporta" title="Esporta" onclick="javascript:esporta();">&nbsp;
						<input type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
						<input type="hidden" name="codgar" id="codgar" value="${locCodgar}"/>
						<input type="hidden" name="codiceGara" id="codiceGara" value="${codiceGara}"/>
		  	  </td>
			  </tr>
			</table>
		</form>
	</c:when>
	<c:otherwise>
		<table class="lista">
	    <tr>
	    	<td>&nbsp;</td>
	    	<td>
	    		<p>
	    			Esportazione completata.
					  <br>&nbsp;
					  <br>&nbsp;
					  <br><b>ATTENZIONE: salvare il file prodotto prima di procedere alla sua modifica.</b>
					  <br>&nbsp;
					  <br><b>ATTENZIONE: il file prodotto potr&agrave; essere usato in importazione solo dalla stessa gara da cui &egrave; stato esportato.</b>
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
			<input type="hidden" name="nomeTempFile" value="${nomeFileExcel}" />
		</form>
	</c:otherwise>
</c:choose>

</div>
		</td>
	</tr>
</table>
<!--/div-->

			<!-- PARTE NECESSARIA PER VISUALIZZARE I POPUP MENU DI OPZIONI PER CAMPO -->
			<IFRAME class="gene" id="iframepopmenu"></iframe>
			<div id="popmenu" class="popupmenuskin"
				onMouseover="highlightMenuPopup(event,'on');"
				onMouseout="highlightMenuPopup(event,'off');"></div>

<script type="text/javascript">
<!--
	function esporta(){
		bloccaRichiesteServer();
		document.exportLottiGara.submit();
	}

	function annulla(){
		window.close();
	}

	function initPagina(){
		checkAttivaBloccoPaginaPopup();
		document.getElementById("titolomaschera").innerHTML = "Esportazione della lista dei lotti della gara ${locCodgar}";
			
	
	<c:if test='${RISULTATO eq "OK"}'>
		window.onfocus=fnFocus;
		window.setTimeout("document.downloadExportForm.submit();", 250);
	</c:if>
	}

-->
</script>