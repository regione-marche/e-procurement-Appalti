
<%
	/*
	 * Created on 20-Ott-2008
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
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>

<gene:template file="lista-template.jsp" gestisciProtezioni="true"
	idMaschera="W3GARA-lista" schema="W3">
	<gene:setString name="titoloMaschera" value="Lista delle gare" />
	<gene:setString name="entita" value="W3GARA" />
	<gene:redefineInsert name="corpo">
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
		<table class="lista">
			<tr>
				<td><gene:formLista entita="W3GARA" pagesize="20" sortColumn="2"
					tableclass="datilista" gestisciProtezioni="true" gestore="it.eldasoft.sil.w3.tags.gestori.submit.GestoreW3GARA">
					<gene:campoLista title="Opzioni <br><center>${titoloMenu}</center>"
						width="50">
						<c:if test="${currentRow >= 0}">
							<gene:PopUp variableJs="rigaPopUpMenu${currentRow}"
								onClick="chiaveRiga='${chiaveRigaJava}'">
								<gene:PopUpItemResource
									resource="popupmenu.tags.lista.visualizza"
									title="Visualizza gara" />
									
								<c:if test="${!(datiRiga.W3GARA_STATO_SIMOG eq '5' or datiRiga.W3GARA_STATO_SIMOG eq '6' or datiRiga.W3GARA_STATO_SIMOG eq '7')}">	
									<c:if test='${gene:checkProtFunz(pageContext, "MOD","MOD")}'>
										<gene:PopUpItemResource
											resource="popupmenu.tags.lista.modifica" title="Modifica gara" />
									</c:if>
								</c:if>
								
								<c:if test='${gene:checkProtFunz(pageContext, "DEL","DEL")}'>
									<c:if test="${empty datiRiga.W3GARA_ID_GARA}">
										<gene:PopUpItemResource resource="popupmenu.tags.lista.elimina"
											title="Elimina gara" />
									</c:if>		
								</c:if>
							</gene:PopUp>
							
							<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")}'>
								<c:choose>
									<c:when test="${empty datiRiga.W3GARA_ID_GARA}">
										<input type="checkbox" name="keys" value="${chiaveRiga}" />
									</c:when>
									<c:otherwise>
										<input type="checkbox" name="keys" value="${chiaveRiga}" disabled="disabled"/>
									</c:otherwise>
								</c:choose>
							</c:if>
							
						</c:if>
					</gene:campoLista>
					
					<gene:campoLista campo="NUMGARA" visibile="false"/>
					<gene:campoLista campo="ID_GARA" width="120" href="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
					<gene:campoLista campo="STATO_SIMOG" width="120" />
					<gene:campoLista title="Data pubblicazione" campo="DATA_CONFERMA_GARA" width="100"/>
					<gene:campoLista campo="OGGETTO" headerClass="sortable"	href="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
					<gene:campoLista campo="IMPORTO_GARA" />
					<gene:campoLista title="&nbsp;" width="24">
						<c:choose>
							<c:when test="${datiRiga.W3GARA_STATO_SIMOG eq '1' or datiRiga.W3GARA_STATO_SIMOG eq '3' or datiRiga.W3GARA_STATO_SIMOG eq '5'}">
								<img width="24" height="24" title="" alt="" src="${pageContext.request.contextPath}/img/invioFlusso.png"/>	
							</c:when>
						</c:choose>
					</gene:campoLista>
				</gene:formLista></td>
			</tr>
			<tr><jsp:include page="/WEB-INF/pages/commons/pulsantiLista.jsp" /></tr>
		</table>
	</gene:redefineInsert>
</gene:template>