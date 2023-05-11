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
<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GestioneListaLavorazioniFornitureFunction" parametro="${funcParam}" />
<c:set var="locArticoli" value="delle lavorazioni e forniture" />

<c:set var="documentiAssociatiDB" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.documentiAssociatiDB")}'/>

<c:choose>
	<c:when test='${not empty param.ngara}'>
		<c:set var="locNGara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="locNGara" value="${ngara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.codiceGara}'>
		<c:set var="codiceGara" value="${param.codiceGara}" />
	</c:when>
	<c:otherwise>
		<c:set var="codiceGara" value="${codiceGara}" />
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

<c:set var="tipoFornitura" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipoFornitureFunction",  pageContext,codiceGara)}' />
<c:if test="${tipoFornitura == 98 }">
	<c:set var="locArticoli" value="dei prodotti" />
</c:if>

<table style="width:100%;">
	<tr>
		<td>
<div class="contenitore-popup">

<c:choose>
	<c:when test='${empty RISULTATO or RISULTATO eq "KO"}'>
		<form action="${contextPath}/pg/EseguiExportLavorazioniForniture.do" method="post" name="exportOffertaPrezzi" >
			<table class="dettaglio-notab">
		    <tr>
		    	<td colspan="2">
		    		<p>
		    			Mediante questa funzione è possibile <b>esportare</b> in formato excel la lista ${locArticoli} della gara.
						  <br>&nbsp;
						  <br>&nbsp;
						</p>
					</td>
				</tr>
		<c:choose>
			<c:when test='${gene:checkProt(pageContext, "COLS.VIS.GARE.GCAP.PREZUN") and tipoFornitura ne "98"}'>
				<tr>
		    	<td class="etichetta-dato" >
					  Esportare i prezzi unitari a base di gara ?
					</td>
					<td class="valore-dato">
						<select name="exportPrezziUnitari" id="exportPrezziUnitari" >
							<option value="1" <c:if test='${exportPrezziUnitari eq "1"}'>selected="selected"</c:if> >Si</option>
							<option value="2" <c:if test='${exportPrezziUnitari ne "1"}'>selected="selected"</c:if> >No</option>
						</select>
					</td>
				</tr>
			</c:when>
			<c:otherwise>
				<input type="hidden" name="exportPrezziUnitari" id="exportPrezziUnitari" value="2" />
			</c:otherwise>
		</c:choose>

		<c:choose>
			<c:when test='${(empty bloccoOfferteDitte or bloccoOfferteDitte eq "2") and documentiAssociatiDB ne "1"}'>
				<tr>
					<td class="etichetta-dato">
						Archiviare il file prodotto nei documenti associati della gara ?
					</td>
					<td class="valore-dato">
						<select name="archiviaXLSDocAss" id="archiviaXLSDocAss" >
							<option value="1" <c:if test='${archiviaXLSDocAss eq "1"}'>selected="selected"</c:if> >Si</option>
							<option value="2" <c:if test='${archiviaXLSDocAss ne "1"}'>selected="selected"</c:if> >No</option>
						</select>
					</td>
				</tr>
			</c:when>
			<c:otherwise>
				<input type="hidden" name="archiviaXLSDocAss" id="archiviaXLSDocAss" value="2" />
			</c:otherwise>
		</c:choose>
				<tr>
	    		<td colspan="2">
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
						<input type="hidden" name="ngara" id="ngara" value="${locNGara}"/>
						<input type="hidden" name="codiceGara" id="codiceGara" value="${codiceGara}"/>
						<input type="hidden" name="bloccoOfferteDitte" id="bloccoOfferteDitte" value="${bloccoOfferteDitte}" />
						<input type="hidden" name="ribcal" id="ribcal" value="${ribcal}" />
		  	  </td>
			  </tr>
			</table>
		<c:choose>
			<c:when test='${not empty param.garaLottiConOffertaUnica}'>
				<input type="hidden" name="garaLottiConOffertaUnica" id="garaLottiConOffertaUnica" value="${param.garaLottiConOffertaUnica}" />
			</c:when>
			<c:when test='${not empty requestScope.garaLottiConOffertaUnica}'>
				<input type="hidden" name="garaLottiConOffertaUnica" id="garaLottiConOffertaUnica" value="${requestScope.garaLottiConOffertaUnica}" />
			</c:when>
		</c:choose>
		<c:choose>
			<c:when test='${not empty param.tipoFornitura}'>
				<input type="hidden" name="tipoFornitura" id="tipoFornitura" value="${param.tipoFornitura}" />
			</c:when>
			<c:when test='${not empty requestScope.tipoFornitura}'>
				<input type="hidden" name="tipoFornitura" id="tipoFornitura" value="${requestScope.tipoFornitura}" />
			</c:when>
		</c:choose>
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
	var tipofornitura="${tipoFornitura}"

	function esporta(){
		bloccaRichiesteServer();
		document.exportOffertaPrezzi.submit();
	}

	function annulla(){
		window.close();
	}

	function initPagina(){
		checkAttivaBloccoPaginaPopup();
	if (tipofornitura == "98"){
		document.getElementById("titolomaschera").innerHTML = "Esportazione della lista dei prodotti della gara ${locNGara}";
	}else{
		document.getElementById("titolomaschera").innerHTML = "Esportazione della lista delle lavorazioni e forniture della gara ${locNGara}";
	}
			
	
	<c:if test='${RISULTATO eq "OK"}'>
		window.onfocus=fnFocus;
		window.setTimeout("document.downloadExportForm.submit();", 250);
	</c:if>
	}

-->
</script>