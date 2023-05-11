<%
/*
 * Created on: 20/11/2008
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

<gene:set name="titoloMenu">
	<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
</gene:set>

<%/*La pagina può essere richiamata anche da TORN per le gare divise a lotti con offerta unica*/ %>
<c:choose>
	<c:when test='${tipologiaGara eq "3"}'>
		<%/*gara divisa a lotti con offerta unica*/ %>
		<c:set var="where" value='GARSED.NGARA = #TORN.CODGAR#'/>
		<c:set var="codiceGara" value='${gene:getValCampo(key,"CODGAR")}' />
	</c:when>
	<c:otherwise>
		<c:set var="where" value='GARSED.NGARA = #GARE.NGARA#'/>
		
		<c:set var="codiceGara" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetCodiceGaraFunction", pageContext)}' />
	</c:otherwise>
</c:choose>



<table class="dettaglio-tab-lista">
	<tr>
		<td>
			<gene:formLista entita="GARSED" where='${where}' tableclass="datilista" sortColumn="3;2"
						gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreGARSED" pagesize="25" >
				<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-pg-lista-scheda.jsp">
					<jsp:param name="entita" value="V_GARE_TORN"/>
					<jsp:param name="inputFiltro" value="CODGAR=T:${codiceGara}"/>
					<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
				</jsp:include>
				<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50">
					<c:if test="${currentRow >= 0}">
						<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
							<c:if test='${gene:checkProt(pageContext, "MASC.VIS.GARE.GARSED-scheda")}' >
								<gene:PopUpItemResource resource="popupmenu.tags.lista.visualizza" title="Visualizza seduta di gara"/>
							</c:if>
							<c:if test='${(autorizzatoModifiche ne "2") && gene:checkProt(pageContext, "MASC.VIS.GARE.GARSED-scheda") && gene:checkProtFunz(pageContext, "MOD","MOD")}' >
								<gene:PopUpItemResource resource="popupmenu.tags.lista.modifica" title="Modifica seduta di gara" />
							</c:if>
							<c:if test='${(autorizzatoModifiche ne "2") && gene:checkProtFunz(pageContext, "DEL","DEL")}' >
								<gene:PopUpItemResource resource="popupmenu.tags.lista.elimina" title="Elimina seduta di gara" />
							</c:if>
						</gene:PopUp>
					<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")}'>
						<input type="checkbox" name="keys" value="${chiaveRiga}"  />
					</c:if>					
					</c:if>
				</gene:campoLista>
				<gene:campoLista campo="NUMSED" visibile="false" />
				
				<c:set var="visualizzaLink" value='${gene:checkProt(pageContext, "MASC.VIS.GARE.GARSED-scheda")}'/>				
				<c:set var="link" value="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
				<gene:campoLista campo="DATINI" headerClass="sortable" href="${gene:if(visualizzaLink, link, '')}" />
				<gene:campoLista campo="FASE" headerClass="sortable" />
				<gene:campoLista campo="DATPRE" headerClass="sortable" />
				<gene:campoLista campo="NGARA" visibile="false" />
			</gene:formLista >
		</td>
	</tr>
	<tr><jsp:include page="/WEB-INF/pages/commons/pulsantiListaPage.jsp" /></tr>
</table>