<%
/*
 * Created on: 31-08-2010
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

/*
	Descrizione:
		Finestra per l'attivazione della funzione 'Pubblica su portale Alice Gare'
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty requestScope.pubblicazioneEseguita and requestScope.pubblicazioneEseguita eq "1"}' >
<script type="text/javascript">
		opener.historyReload();
		window.close();
</script>
	</c:when>
	<c:otherwise>

<jsp:include page="/WEB-INF/pages/commons/defCostantiAppalti.jsp" />
<div style="width:97%;">
<gene:template file="popup-message-template.jsp">

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:set var="modo" value="NUOVO" scope="request" />

	<c:choose>
		<c:when test='${!empty param.isProceduraTelematica}'>
			<c:set var="isProceduraTelematica" value="${param.isProceduraTelematica}" />
		</c:when>
		<c:otherwise>
			<c:set var="isProceduraTelematica" value="${isProceduraTelematica}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.bando}'>
			<c:set var="bando" value="${param.bando}" />
		</c:when>
		<c:otherwise>
			<c:set var="bando" value="${bando}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.codgar}'>
			<c:set var="codgar" value="${param.codgar}" />
		</c:when>
		<c:otherwise>
			<c:set var="codgar" value="${codgar}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.ngara}'>
			<c:set var="ngara" value="${param.ngara}" />
		</c:when>
		<c:otherwise>
			<c:set var="ngara" value="${ngara}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.step}'>
			<c:set var="step" value="${param.step}" />
		</c:when>
		<c:otherwise>
			<c:set var="step" value="${step}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.entita}'>
			<c:set var="entita" value="${param.entita}" />
		</c:when>
		<c:otherwise>
			<c:set var="entita" value="${entita}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.garaElencoCatalogo}'>
			<c:set var="garaElencoCatalogo" value="${param.garaElencoCatalogo}" />
		</c:when>
		<c:otherwise>
			<c:set var="garaElencoCatalogo" value="${garaElencoCatalogo}" />
		</c:otherwise>
	</c:choose>
	
	
	<c:choose>
		<c:when test='${!empty param.valtec}'>
			<c:set var="valtec" value="${param.valtec}" />
		</c:when>
		<c:otherwise>
			<c:set var="valtec" value="${valtec}" />
		</c:otherwise>
	</c:choose>
	
	<c:if test="${empty entita ||  entita==''}">
		<c:set var="entita" value="TORN" />
	</c:if>

	<c:choose>
		<c:when test='${!empty ngara}'>
			<c:set var="valoreChiave" value="${ngara}" />
		</c:when>
		<c:otherwise>
			<c:set var="valoreChiave" value="${codgar}" />
		</c:otherwise>
	</c:choose>
		
	<c:choose>
		<c:when test='${!empty param.garavviso}'>
			<c:set var="garavviso" value="${param.garavviso}" />
		</c:when>
		<c:otherwise>
			<c:set var="garavviso" value="${garavviso}" />
		</c:otherwise>
	</c:choose>
		
	<c:set var="chiaveTorn" value="TORN.CODGAR=T:${codgar}"/>
	<c:set var="tipologiaGara" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipologiaGaraFunction",  pageContext,chiaveTorn)}'/>
	<c:choose>
		<c:when test="${tipologiaGara eq '3' }" >
			<c:set var="offertaUnica" value="true" />
		</c:when>
		<c:otherwise>
			<c:set var="offertaUnica" value="false" />
		</c:otherwise>
	</c:choose>
	
	<gene:setString name="titoloMaschera" value='Verifica integrazione articoli con ERP' />
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="${entita }" gestisciProtezioni="false" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreWSERPMessaggi">
			
	<gene:campoScheda nome="msgPagina">
		<td colSpan="2">
			<br>
			<c:choose>
				<c:when test='${not empty requestScope.controlloSuperato and requestScope.controlloSuperato eq "NO"}' >
					${requestScope.msg }
				</c:when>
				<c:when test='${not empty requestScope.controlloSuperato and requestScope.controlloSuperato eq "WARNING"}' >
						${requestScope.msg }
				</c:when>
				<c:otherwise>
						${requestScope.msg }
				</c:otherwise>
			</c:choose>
			
			<br>&nbsp;
			<br>&nbsp;
		</td>
	</gene:campoScheda>

		<gene:campoScheda campo="CODGAR" campoFittizio="true" defaultValue="${param.codgar}" visibile="false" definizione="T21;0"/>
		<gene:campoScheda campo="NGARA" campoFittizio="true" defaultValue="${param.ngara}" visibile="false" definizione="T20;0"/>
		<gene:campoScheda campo="LOTTODIGARA" campoFittizio="true" defaultValue="${param.lottoDiGara}" visibile="false" definizione="T1;0"/>

		<input type="hidden" name="isProceduraTelematica" id="isProceduraTelematica" value="${isProceduraTelematica}" />
		<input type="hidden" name="bando" id="bando" value="${bando}" />
		<input type="hidden" name="codgar" id="codgar" value="${codgar}" />
		<input type="hidden" name="ngara" id="ngara" value="${ngara}" />
		<input type="hidden" name="iterga" id="iterga" value="${iterga }" />

	</gene:formScheda>
  </gene:redefineInsert>

	
	<gene:redefineInsert name="buttons">
			<c:choose>
				<c:when test='${not empty requestScope.controlloSuperato and requestScope.controlloSuperato eq "NO"}'>
					<INPUT type="button" id="pulsanteAnnulla" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
				</c:when>
				<c:otherwise>
					<INPUT type="button" id="pulsanteAnnulla" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla()">&nbsp;
				</c:otherwise>
			</c:choose>
	</gene:redefineInsert>

	
	
	<gene:javaScript>
		
		
		
		function annulla(){
			window.close();
		}
		
	</gene:javaScript>
</gene:template>
</div>

	</c:otherwise>
</c:choose>