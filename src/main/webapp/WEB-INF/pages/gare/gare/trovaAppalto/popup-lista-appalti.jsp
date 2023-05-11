<%
/*
 * Created on: 17-mar-2009
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

 /* Lista popup di selezione dell'appalto da associare alla gara */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.archWhereFunctions.ComponiWhereAPPAFunction" />

<% // il tipo della gara viene ricavato da trovaParameter in cui viene impostato il valore del parametro per APPA.TIPLAVG = ?%>
<% // in trovaParameter vanno prese in considerazione le seguenti casistiche:%>
<% // trovaParameter=T:1 e trovaParameter=%...%;T:1%>
<c:set var="labelTipoGara" value=''/>
<c:set var="nomeContainerFiltri" value="deftrovaAPPA-${empty param.numeroPopUp ? 0 : param.numeroPopUp}"/> 
<c:if test = "${fn:containsIgnoreCase(sessionScope[nomeContainerFiltri].trovaAddWhere, 'APPA.TIPLAVG')}">
	
	<c:choose>
		<c:when test="${fn:contains(sessionScope[nomeContainerFiltri].trovaParameter, ';')}">
			<% // il tipo della gara viene ricavato da trovaParameter in cui viene impostato il filtro (APPA.TIPLAVG = ...)%>
			<c:set var="indicePartenza" value="${fn:indexOf(sessionScope[nomeContainerFiltri].trovaParameter, ';') + 2}"/>
		</c:when>
		<c:otherwise>
			<c:set var="indicePartenza" value="${fn:indexOf(sessionScope[nomeContainerFiltri].trovaParameter, ':')}"/>
		</c:otherwise>
	</c:choose>
	<c:set var="tipoGara" value="${fn:substring(sessionScope[nomeContainerFiltri].trovaParameter, indicePartenza + 1, indicePartenza + 2)}"/>
	<c:choose>
		<c:when test="${tipoGara eq '1' }">
			<c:set var="labelTipoGara" value='per lavori'/>
		</c:when>
		<c:when test="${tipoGara eq '2' }">
			<c:set var="labelTipoGara" value='per forniture'/>
		</c:when>
		<c:when test="${tipoGara eq '3' }">
			<c:set var="labelTipoGara" value='per servizi'/>
		</c:when>
	</c:choose>
</c:if>


<gene:template file="popup-template.jsp">
	<gene:setString name="titoloMaschera" value="Selezione appalto"/>
	<gene:redefineInsert name="corpo">
		<gene:formLista pagesize="25" sortColumn="2;3" tableclass="datilista" entita="APPA" inserisciDaArchivio="false" gestisciProtezioni="true">
			<table class="dettaglio-noBorderBottom">
				<tr><td colspan="2">
					Nella lista sottostante sono elencati gli appalti ${labelTipoGara} che non sono già collegati a una gara, oppure sono collegati a una gara che è stata annullata, e che risultano ancora non aggiudicati (data atto aggiudicazione non valorizzata)
					</td></tr>
				<tr><td>&nbsp;</td></tr>
			</table>		
			
			<% // Aggiungo gli item al menu contestuale di riga %>
			<gene:campoLista title="Opzioni" width="50">
				<c:if test="${currentRow >= 0}" >
				<gene:PopUp variableJs="jvarRow${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
					<gene:PopUpItem title="Seleziona" href="javascript:archivioSeleziona(${datiArchivioArrayJs});"/>
				</gene:PopUp>
				</c:if>
			</gene:campoLista>
			<gene:campoLista campo="CODLAV" headerClass="sortable" href="javascript:archivioSeleziona(${datiArchivioArrayJs});" />
			<gene:campoLista campo="NAPPAL" headerClass="sortable" width="50" href="javascript:archivioSeleziona(${datiArchivioArrayJs});" />
			<gene:campoLista campo="CODCUA" headerClass="sortable" href="javascript:archivioSeleziona(${datiArchivioArrayJs});" />
			<gene:campoLista campo="NOTAPP" headerClass="sortable" />
			<gene:campoLista campo="CODCIG" headerClass="sortable" href="javascript:archivioSeleziona(${datiArchivioArrayJs});"/>
			<gene:campoLista campo="CUPPRG" entita="PERI" where="APPA.CODLAV = PERI.CODLAV" href="javascript:archivioSeleziona(${datiArchivioArrayJs});"/>
		</gene:formLista>
  </gene:redefineInsert>
</gene:template>
