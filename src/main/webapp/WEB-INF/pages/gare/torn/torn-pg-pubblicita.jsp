<%
/*
 * Created on: 28/10/2008
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

<c:set var = "iterga"  value = "${param.iterga}" />
<c:set var = "tipoPub"  value = "${param.tipoPub}" />
<c:choose>
	<c:when test="${tipologiaGara eq '1'}">
		<c:set var = "bandoVisibileProfilo" value="${gene:checkProt(pageContext, 'SEZ.VIS.GARE.TORN-scheda.PUBBLICITA.PUBBANDO')}" />
		<c:set var = "esitoVisibileProfilo" value="${gene:checkProt(pageContext, 'SEZ.VIS.GARE.TORN-scheda.PUBBLICITA.PUBESITO')}" />
	</c:when>
	<c:otherwise>
		<c:set var = "bandoVisibileProfilo" value="${gene:checkProt(pageContext, 'SEZ.VIS.GARE.TORN-OFFUNICA-scheda.PUBBLICITA.PUBBANDO')}" />
		<c:set var = "esitoVisibileProfilo" value="${gene:checkProt(pageContext, 'SEZ.VIS.GARE.TORN-OFFUNICA-scheda.PUBBLICITA.PUBESITO')}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${empty tipoPub or tipoPub eq "1"}'>
		<c:set var = "gestore" value="it.eldasoft.sil.pg.tags.gestori.submit.GestoreTORN" />
	</c:when>
	<c:otherwise>
		<c:set var = "gestore" value="it.eldasoft.sil.pg.tags.gestori.submit.GestoreGARE" />
	</c:otherwise>
</c:choose>

<gene:formScheda entita="TORN" gestisciProtezioni="true" gestore="${gestore}" >
	<gene:campoScheda campo="CODGAR"  visibile="false" />
	<gene:campoScheda>
			<td colspan="2">
			<c:if test='${bandoVisibileProfilo}'>
				<input type="radio" value="1" name="filtroDocumentazione" id="atti" <c:if test='${empty tipoPub or tipoPub eq "1" and bandoVisibileProfilo}'>checked="checked"</c:if> <c:if test='${modo eq "MODIFICA"}'>disabled="true" </c:if> onclick="javascript:cambiaTipoPubblicazione('1');" />
				 Pubblicazioni bando, avviso o delibera a contrarre
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			</c:if>
				<c:if test='${empty param.garaPerElenco and empty garaPerCatalogo and esitoVisibileProfilo}'>
					<input type="radio" value="2" name="filtroDocumentazione" id="requisitiRichiesti" <c:if test='${tipoPub eq "2" or !bandoVisibileProfilo}'>checked="checked"</c:if> <c:if test='${modo eq "MODIFICA" }'>disabled="true" </c:if> onclick="javascript:cambiaTipoPubblicazione('2');" />
						Pubblicazioni esito
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</c:if>
			</td>
	</gene:campoScheda>	
	<input type="hidden" name="tipologiaGara" value="${tipologiaGara}"/>
	<input type="hidden" name="tipoPub" value="${tipoPub}"/>
	
	<c:choose>
	<c:when test='${bandoVisibileProfilo and (empty tipoPub or tipoPub eq "1")}'>
		<jsp:include page="torn-pg-pub-bando.jsp" >
			<jsp:param name="tipologiaGara" value="${tipologiaGara}"/>
		</jsp:include>
	</c:when>
	<c:otherwise>
		<jsp:include page="/WEB-INF/pages/gare/gare/gare-pg-pub-esito.jsp">
			<jsp:param name="tipologiaGara" value="${tipologiaGara}"/>
		</jsp:include>
	</c:otherwise>
</c:choose>
</gene:formScheda>

<gene:javaScript>
	function cambiaTipoPubblicazione(tipoPub){
		document.forms[0].metodo.value="apri";
		document.forms[0].jspPathTo.value=document.forms[0].jspPath.value;
		bloccaRichiesteServer();
		document.forms[0].action += "&tipoPub=" + tipoPub;
		document.forms[0].submit();
	}
</gene:javaScript>