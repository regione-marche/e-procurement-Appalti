<%
/*
 * Created on: 08-01-2019
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 /*
  * Popup per la visualizzazione del prospetto dei rilanci offerta economica 
  *
  *
  */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />



<gene:template file="lista-template.jsp" gestisciProtezioni="false" >

	<gene:redefineInsert name="head" >
		<script type="text/javascript" src="${contextPath}/js/jquery.formatCurrency-1.4.0.js"></script>
		<script type="text/javascript" src="${contextPath}/js/jquery.plugin.min.js"></script>
		<script type="text/javascript" src="${contextPath}/js/jquery.prospetto.rilanci.offeco.js"></script>
			
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/rilanci/jquery.rilanci.css" >
				
	</gene:redefineInsert>

	
	<gene:setString name="titoloMaschera"  value='Prospetto rilanci offerta economica per la gara ${param.ngara } ' />
	
	<gene:redefineInsert name="corpo">
		<gene:redefineInsert name="listaNuovo" />
		<gene:redefineInsert name="listaEliminaSelezione" />
		<table class="lista">
			<tr>
				<td colspan="2">
				<br>
				<b>Ditte in gara</b><i>&nbsp;(selezionare una riga per consultare il dettaglio dei rilanci)</i>
				<br>
				<div id="dittecontainer"></div>
				
				<br>
				<div id="titolomessaggio" style="border-bottom: 1px solid #A0AABA; padding-bottom:3px; display: none;">Dettaglio rilanci della ditta <b><i>&nbsp;<span id="nomeDitta"></span></i></b></div>
				<div id="rilancicontainer"></div>
				</td>
			</tr>
			<input type="hidden" name="ngara" id="ngara" value="${param.ngara}" />
			<input type="hidden" name="modlicg" id="modlicg" value="${param.modlicg}" />
			<input type="hidden" name="isGaraLottiConOffertaUnica" id="isGaraLottiConOffertaUnica" value="${param.isGaraLottiConOffertaUnica}" />
			<tr>
				<td class="comandi-dettaglio" colSpan="2">
					<INPUT type="button" class="bottone-azione" value="Torna a elenco concorrenti" title="Torna a elenco concorrenti" onclick="javascript:historyVaiIndietroDi(1);">&nbsp;
				</td>
			</tr>
		</table>
	</gene:redefineInsert>
	
</gene:template>	
	

		
		