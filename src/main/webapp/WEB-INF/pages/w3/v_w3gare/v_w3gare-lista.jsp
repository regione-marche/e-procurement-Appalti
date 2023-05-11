
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

<c:set var="where1" value="" />
<c:choose>
	<c:when test='${sessionScope.profiloUtente.abilitazioneGare eq "A" or sessionScope.profiloUtente.abilitazioneGare eq "C"}'>
		<c:if test="${!empty sessionScope.uffint}">
			<c:set var="where1" value=" V_W3GARE.CODEIN = '${sessionScope.uffint}'" />	
		</c:if>
	</c:when>
	<c:otherwise>
		<c:if test="${!empty sessionScope.uffint}">
			<c:set var="filtroCodein" value=" AND CODEIN = '${sessionScope.uffint}'" />
		</c:if>
		<c:set var="where1" value="(SYSCON = ${sessionScope.profiloUtente.id} OR RUP_CODTEC in (select CODTEC from TECNI where CFTEC=(select SYSCF from USRSYS where SYSCON = ${sessionScope.profiloUtente.id} and SYSCF is not null and SYSCF <> '')
		or CFTEC in (select CFRUP from W9DELEGHE where ID_COLLABORATORE = ${sessionScope.profiloUtente.id} ${filtroCodein}) ))"/>
		<c:if test="${!empty sessionScope.uffint}">
			<c:set var="where1" value=" V_W3GARE.CODEIN = '${sessionScope.uffint}' and ${where1}" />
		</c:if>
	</c:otherwise>
</c:choose>

<gene:template file="lista-template.jsp" gestisciProtezioni="true"
	idMaschera="V_W3GARE-lista" schema="W3">
	<gene:setString name="titoloMaschera" value="Lista delle richieste CIG e smartCIG" />
	<gene:setString name="entita" value="V_W3GARE" />
	<gene:redefineInsert name="corpo">
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
		<table class="lista">
			<tr>
				<td><gene:formLista entita="V_W3GARE" pagesize="20" sortColumn="-2;-3"
					where="${where1}" tableclass="datilista" gestisciProtezioni="true" gestore="it.eldasoft.sil.w3.tags.gestori.submit.GestoreV_W3GARE">
					<c:if test="${datiRiga.V_W3GARE_TIPO_GARA eq 'G'}"> 
						<c:set var="chiaveRigaJava" value="W3GARA.NUMGARA=N:${datiRiga.V_W3GARE_NUMGARA}" />
						<c:set var="chiaveRiga" value="W3GARA.NUMGARA=N:${datiRiga.V_W3GARE_NUMGARA}" />
					</c:if>
					<c:if test="${datiRiga.V_W3GARE_TIPO_GARA eq 'S'}"> 
						<c:set var="chiaveRigaJava" value="W3SMARTCIG.CODRICH=N:${datiRiga.V_W3GARE_NUMGARA}" />
						<c:set var="chiaveRiga" value="W3SMARTCIG.CODRICH=N:${datiRiga.V_W3GARE_NUMGARA}" />
					</c:if>
					<gene:campoLista title="Opzioni <br><center>${titoloMenu}</center>"
						width="50">
						<c:if test="${currentRow >= 0}">
							<gene:PopUp variableJs="rigaPopUpMenu${currentRow}"
								onClick="chiaveRiga='${chiaveRigaJava}'">
								<gene:PopUpItemResource
									resource="popupmenu.tags.lista.visualizza"
									title="Visualizza gara" href="javascript:chiaveRiga='${chiaveRigaJava}';listaMetodoGara('${datiRiga.V_W3GARE_TIPO_GARA}','apri');" />
									
								<c:if test="${!(datiRiga.V_W3GARE_STATO_SIMOG eq '5' or datiRiga.V_W3GARE_STATO_SIMOG eq '6' or datiRiga.V_W3GARE_STATO_SIMOG eq '7')}">	
									<c:if test='${gene:checkProtFunz(pageContext, "MOD","MOD")}'>
										<gene:PopUpItemResource
											resource="popupmenu.tags.lista.modifica" title="Modifica gara" 
											href="javascript:chiaveRiga='${chiaveRigaJava}';listaMetodoGara('${datiRiga.V_W3GARE_TIPO_GARA}','modifica');"/>
									</c:if>
								</c:if>
								
								<c:if test='${gene:checkProtFunz(pageContext, "DEL","DEL")}'>
									<c:if test="${empty datiRiga.V_W3GARE_ID_GARA}">
										<gene:PopUpItemResource resource="popupmenu.tags.lista.elimina"
											title="Elimina gara" />
									</c:if>		
								</c:if>
							</gene:PopUp>
							
							<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")}'>
								<c:choose>
									<c:when test="${empty datiRiga.V_W3GARE_ID_GARA}">
										<input type="checkbox" name="keys" value="${chiaveRiga}" />
									</c:when>
								</c:choose>
							</c:if>
							
						</c:if>
						
					</gene:campoLista>
					
					<gene:campoLista campo="TIPO_GARA" visibile="false"/>
					<gene:campoLista campo="NUMGARA" visibile="false"/>
					<gene:campoLista title="" width="30">
						<c:choose>
							<c:when test="${datiRiga.V_W3GARE_TIPO_GARA eq 'S'}"> 
								<img title="SmartCig" alt="SmartCig" src="${pageContext.request.contextPath}/img/smartcig.png"/>
							</c:when>
							<c:otherwise>
								<img title="Cig" alt="Cig" src="${pageContext.request.contextPath}/img/cig.png"/>
							</c:otherwise>
						</c:choose>
					</gene:campoLista>
					<gene:campoLista campo="ID_GARA" width="120" href="javascript:chiaveRiga='${chiaveRigaJava}';listaMetodoGara('${datiRiga.V_W3GARE_TIPO_GARA}','apri');" />
					<gene:campoLista campo="STATO_SIMOG" width="120" />
					<gene:campoLista title="Data pubblicazione" campo="DATA_CONFERMA_GARA" width="100"/>
					<gene:campoLista campo="OGGETTO" headerClass="sortable"	href="javascript:chiaveRiga='${chiaveRigaJava}';listaMetodoGara('${datiRiga.V_W3GARE_TIPO_GARA}','apri');" />
					<gene:campoLista campo="IMPORTO_GARA" />
					<gene:campoLista campo="CODICE_GARA" />
				</gene:formLista></td>
				<gene:redefineInsert name="pulsanteListaInserisci">
					<INPUT type="button" class="bottone-azione"
					title='${gene:resource("label.tags.template.lista.listaPageNuovo")}'
					value="Nuovo" onclick="javascript:listaNuovaGara();">
				</gene:redefineInsert>
				<gene:redefineInsert name="listaNuovo">
					<td class="vocemenulaterale"><a
					href="javascript:listaNuovaGara();" title="Inserisci"
					tabindex="1501"> Nuovo </a></td>
				</gene:redefineInsert>
			</tr>
			<tr><jsp:include page="/WEB-INF/pages/commons/pulsantiLista.jsp" /></tr>
		</table>
	</gene:redefineInsert>
	
	<gene:javaScript>
	
		// Visualizzazione del dettaglio
		function listaMetodoGara(tipo, metodo) {
			if (tipo == 'G') 
			{
				document.forms[0].jspPathTo.value = "/WEB-INF/pages/w3/w3gara/w3gara-scheda.jsp";
				document.forms[0].entita.value = "W3GARA";
			}
			else
			{
				document.forms[0].jspPathTo.value = "/WEB-INF/pages/w3/w3smartcig/w3smartcig-scheda.jsp";
				document.forms[0].entita.value = "W3SMARTCIG";
			}
			document.forms[0].key.value=chiaveRiga;
			document.forms[0].metodo.value=metodo;
			document.forms[0].activePage.value="0";
			document.forms[0].submit();
		}
		
		function listaNuovaGara(){
			document.location.href = '${pageContext.request.contextPath}/ApriPagina.do?' + csrfToken + '&href=w3/w3gara/w3gara-nuova.jsp';
		}
		
	</gene:javaScript>
</gene:template>