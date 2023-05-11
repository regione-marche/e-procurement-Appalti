<%/*
   * Created on 15-10-2021
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE LA POPUP PER EXPORT OPERATORI ISCRITTI

%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />


<c:choose>
	<c:when test='${not empty param.ngara}'>
		<c:set var="locNgara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="locNgara" value="${ngara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.categoria}'>
		<c:set var="locCategoria" value="${param.categoria}" />
	</c:when>
	<c:otherwise>
		<c:set var="locCategoria" value="${categoria}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${param.genere eq "20"}'>
		<c:set var="genereTitolo" value="del catalogo"/>
	</c:when>
	<c:otherwise>
		<c:set var="genereTitolo" value="dell' elenco"/>
	</c:otherwise>
</c:choose>

<table style="width:100%;">
	<tr>
		<td>
<div class="contenitore-popup">

<c:choose>
	<c:when test='${empty RISULTATO or RISULTATO eq "KO"}'>
		<form action="${contextPath}/pg/EseguiExportOperatoriIscritti.do" method="post" name="exportOperatoriIscritti" >
			<table class="dettaglio-notab">
		    <tr>
		    	<td colspan="2">
		    	  <br>
		    	  <p>
		    	 	Mediante questa funzione è possibile <b>esportare</b> in formato excel la lista degli operatori ${genereTitolo} iscritti per la categoria <b>${locCategoria}</b>.
			      </p>
				  <br>&nbsp;
				  <br>&nbsp;
		      	</td>
			</tr>
		  	<tr>
			    <td class="comandi-dettaglio" colspan="2">
						<input type="button" class="bottone-azione" value="Esporta" title="Esporta" onclick="javascript:esporta();">&nbsp;
						<input type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
						<input type="hidden" name="categoria" id="categoria" value="${locCategoria}"/>
						<input type="hidden" name="ngara" id="ngara" value="${locNgara}"/>
						<input type="hidden" name="genere" id="genere" value="${param.genere}"/>
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
	    		<br>
	    		<p>
	    		  Esportazione completata.
				</p>
			    <br>&nbsp;
			    <br>&nbsp;
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
		document.exportOperatoriIscritti.submit();
	}

	function annulla(){
		window.close();
	}

	function initPagina(){
		checkAttivaBloccoPaginaPopup();
		document.getElementById("titolomaschera").innerHTML = "Esportazione degli operatori ${genereTitolo} ${locNgara} iscritti per categoria";
			
	
	<c:if test='${RISULTATO eq "OK"}'>
		window.onfocus=fnFocus;
		window.setTimeout("document.downloadExportForm.submit();", 250);
	</c:if>
	}

-->
</script>