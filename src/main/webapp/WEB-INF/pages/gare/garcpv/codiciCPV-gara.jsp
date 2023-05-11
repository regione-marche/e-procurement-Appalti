<%
/*
 * Created on: 29/10/2008
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

<%/* Verifico se si può modificare il CODICE CPV */%>
<c:choose>
	<c:when test='${gene:checkProtObj(pageContext,"COLS.MOD","GARE.GARCPV.CODCPV") && not empty modo and modo ne "VISUALIZZA" }'>
		<c:set var="modCODCPV" value="true" scope="request"/>
	</c:when>
	<c:otherwise>
		<c:set var="modCODCPV" value="false" scope="request"/>
	</c:otherwise>
</c:choose>

<%/* Verifico se la pagina è aperta da GARE o da TORN */%>
<c:if test="${modo ne 'NUOVO'}">
	<c:choose>
		<c:when test='${tipologiaGara == "3" && param.lottoOffertaUnica ne "true"}'>
			<c:set var="where" value="GARCPV.NGARA = TORN.CODGAR AND GARCPV.TIPCPV = '1'" />
			<c:set var="chiave" value='${gene:getValCampo(key, "CODGAR")}' />
		</c:when>
		<c:when test='${tipologiaGara == "11"}'>
			<c:set var="where" value="GARCPV.NGARA = GAREAVVISI.NGARA AND GARCPV.TIPCPV = '1'" />
			<c:if test='${modo ne "NUOVO"}'>
			<c:set var="chiave" value='${gene:getValCampo(key, "NGARA")}' />
			</c:if>
		</c:when>
		<c:when test="${param.lottoStipula eq 'true'}">
			<c:set var="where" value="GARCPV.NGARA = '${ngara}' AND GARCPV.TIPCPV = '1'" />
			<c:set var="chiave" value='${ngara}' />
		</c:when>
		<c:otherwise>
			<c:set var="where" value="GARCPV.NGARA = GARE.NGARA AND GARCPV.TIPCPV = '1'" />
			<c:set var="chiave" value='${gene:getValCampo(key, "NGARA")}' />
		</c:otherwise>
	</c:choose>
</c:if>

	<gene:gruppoCampi idProtezioni="CPVPR">
		<gene:campoScheda>
			<td colspan="2" id="IntestazioneCPV"><b>CPV</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="NGARA" entita="GARCPV" visibile="false" where="${where}" value='${chiave}'/>
		<gene:campoScheda campo="NUMCPV" entita="GARCPV" visibile="false" where="${where}"/>
		<c:choose>
			<c:when test="${param.datiModificabili }">
				<gene:campoScheda campo="CODCPV" entita="GARCPV" href="#" where="${where}" defaultValue ="${param.initCODCPV }" title="Codice CPV principale" speciale="true">
					<gene:popupCampo titolo="Dettaglio codice CPV" href="#" />
				</gene:campoScheda>
			</c:when>
			<c:otherwise>
				<gene:campoScheda campo="CODCPV" entita="GARCPV" where="${where}" defaultValue ="" title="Codice CPV principale" />
			</c:otherwise>
		</c:choose>
		
		<gene:campoScheda campo="TIPCPV" entita="GARCPV" visibile="false" value="1"/>
	</gene:gruppoCampi>
	
	<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GestioneCPVOggettoComplementareFunction" parametro='${chiave}' />
	<jsp:include page="/WEB-INF/pages/commons/interno-sezione-multipla-singola-riga.jsp" >
		<jsp:param name="entita" value='GARCPV'/>
		<jsp:param name="chiave" value='${chiave}'/>
		<jsp:param name="nomeAttributoLista" value='cpv' />
		<jsp:param name="idProtezioni" value="CPVCOMP" />
		<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/garcpv/cpv-ogg-compl.jsp"/>
		<jsp:param name="arrayCampi" value="'GARCPV_NGARA_', 'GARCPV_NUMCPV_', 'GARCPV_CODCPV_', 'GARCPV_TIPCPV_'"/>
		<jsp:param name="titoloSezione" value="CPV oggetto complementare" />
		<jsp:param name="titoloNuovaSezione" value="Nuovo CPV oggetto complementare" />
		<jsp:param name="descEntitaVociLink" value="CPV ogg. complementare" />
		<jsp:param name="msgRaggiuntoMax" value="i CPV oggetto complementare"/>
		<jsp:param name="sezioneListaVuota" value="false"/>
		<jsp:param name="sezioneEliminabile" value="true"/>
		<jsp:param name="sezioneInseribile" value="${param.datiModificabili }"/>
		<jsp:param name="datiModificabili" value="${param.datiModificabili }"/>
	</jsp:include>
	<gene:javaScript>
	
	function formCPV(modifica, campo){
		console.log("popup");
			
	}
	
	$(window).ready(function (){
		
		_creaFinestraAlberoCpvVP();
		_creaFinestraAlberoCpvVSUPP();
		_creaLinkAlberoCpvVP($("#GARCPV_CODCPV").parent(), "${modo}", $("#GARCPV_CODCPV"), $("#GARCPV_CODCPVview") );
		
		$("input[name*='CPV']").attr('readonly','readonly');
		$("input[name*='CPV']").attr('tabindex','-1');
		$("input[name*='CPV']").css('border-width','1px');
		$("input[name*='CPV']").css('background-color','#E0E0E0');
		
	});
	
	<c:if test='${!(modo eq "VISUALIZZA")}'>
		
		//Quando si inserisce un CPV complementare, in automatico si deve lanciare
		//in automatico la pop-up per la definizione del codice
		var showNextElementoSchedaMultipla_Default = showNextElementoSchedaMultipla;
		function showNextElementoSchedaMultipla_Custom(tipo, campi,visibilitaCampi){
			showNextElementoSchedaMultipla_Default(tipo, campi, visibilitaCampi);
			if(tipo == "CPVCOMP"){
				var indice = eval("lastId" + tipo + "Visualizzata");
				var modifica= "${requestScope.modCODCPV}";
				formCPV(modifica, 'GARCPV_CODCPV_' + indice);
			}
		}
		showNextElementoSchedaMultipla = showNextElementoSchedaMultipla_Custom;
	</c:if>
	
	</gene:javaScript>	