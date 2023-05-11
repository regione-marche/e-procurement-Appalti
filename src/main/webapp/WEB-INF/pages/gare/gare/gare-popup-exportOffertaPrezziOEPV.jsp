<%/*
   * Created on 18-06-2015
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
	<c:when test='${not empty param.ngara}'>
		<c:set var="ngara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.isGaraLottiConOffertaUnica}'>
		<c:set var="isGaraLottiConOffertaUnica" value="${param.isGaraLottiConOffertaUnica}" />
	</c:when>
	<c:otherwise>
		<c:set var="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.ribcal}'>
		<c:set var="ribcal" value="${param.ribcal}" />
	</c:when>
	<c:otherwise>
		<c:set var="ribcal" value="${ribcal}" />
	</c:otherwise>
</c:choose>

<table style="width:100%;">
	<tr>
		<td>
			<div class="contenitore-popup">
	
		<table class="dettaglio-notab">
	    <tr>
	    	<td>&nbsp;</td>
	    	<td>
	    		<p>
	    		<br>
	    		<c:choose>
					<c:when test='${RISULTATO eq "OK"}'>
	    				Esportazione completata.
					
					</c:when>
					<c:when test='${RISULTATO eq "KO"}'>
						Impossibile procedere con l'esportazione.
					</c:when>
					<c:otherwise>
						Mediante questa funzione è possibile <b>esportare</b> in formato excel il dettaglio dell'offerta prezzi di tutte le ditte in gara.
						<br/>
					</c:otherwise>
				</c:choose>
				<br><br>
				</p>
	      </td>
	    </tr>
	    <tr class="comandi-dettaglio">
	      <td colspan="2">
					<c:if test="${empty RISULTATO }">
						<input type="button" class="bottone-azione" value="Esporta" title="Esporta" onclick="javascript:esporta();">&nbsp;
						<input type="hidden" name="ngara" id="ngara" value="${ngara}"/>
						<input type="hidden" name="isGaraLottiConOffertaUnica" id="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}"/>
					</c:if>
					
					<input type="button" class="bottone-azione" id="botChiudi" value="Annulla" title="Annula" onclick="javascript:annulla();">&nbsp;
	      </td>
	    </tr>
		</table>
		</div>
		</td>
	</tr>
</table>
		<c:choose>
			<c:when test="${empty RISULTATO }">
				<form action="${contextPath}/pg/ExportOffertaPrezziExcel.do" method="post" name="exportOffertaPrezzi" >
					<input type="hidden" name="ngara" id="ngara" value="${ngara}"/>
					<input type="hidden" name="isGaraLottiConOffertaUnica" id="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}"/>
					<input type="hidden" name="ribcal" id="ribcal" value="${ribcal}"/>
				</form>
			</c:when>
			<c:otherwise>
				<form action="${contextPath}/DownloadTempFile.do" method="post" name="downloadExportForm" >
					<input type="hidden" name="nomeTempFile" value="${nomeFileExcel}" />
				</form>
			</c:otherwise>
		</c:choose>
		
	



<script type="text/javascript">


	function annulla(){
		window.close();
	}
	
	function esporta(){
		bloccaRichiesteServer();
		document.exportOffertaPrezzi.submit();
	}
	
	function initPagina(){
		var ngara="${ngara}";
		var titolo = $("#titolomaschera").text();
		titolo += " " + ngara;
        $("#titolomaschera").text(titolo);
		<c:if test='${RISULTATO eq "OK"}'>
		$("#botChiudi").prop('value', 'Chiudi');
		$("#botChiudi").prop('title', 'Chiudi');
		window.onfocus=fnFocus;
		window.setTimeout("document.downloadExportForm.submit();", 250);
		</c:if>
	}
	
</script>