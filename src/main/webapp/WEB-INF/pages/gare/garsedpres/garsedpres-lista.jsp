
<%
	/*
	 * Created on 04-06-2013
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="numeroGara" value='${gene:getValCampo(key, "GARSED.NGARA")}' />
<c:set var="numeroSeduta" value='${gene:getValCampo(key, "GARSED.NUMSED")}' />

<c:choose>
	<c:when test='${not empty param.dataSeduta}'>
		<c:set var="dataSeduta" value="${param.dataSeduta}" />
	</c:when>
	<c:otherwise>
		<c:set var="dataSeduta" value="${dataSeduta}" />
	</c:otherwise>
</c:choose>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />


<c:set var="where" value='GARSEDPRES.NGARA = #GARSED.NGARA# AND GARSEDPRES.NUMSED = #GARSED.NUMSED#' />

<gene:template file="lista-template.jsp" gestisciProtezioni="true" idMaschera="GARSEDPRES-lista" schema="GARE">
	<gene:setString name="titoloMaschera" value="Lista componenti della commissione di gara presenti nella seduta del ${dataSeduta }" />
	<gene:setString name="entita" value="GARSEDPRES" />
	<gene:redefineInsert name="corpo">
	
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
		
			
		<table class="lista">
			<tr>
				<td><gene:formLista entita="GARSEDPRES" pagesize="20" tableclass="datilista" gestisciProtezioni="true" sortColumn="4" where="${where }" >
					<gene:campoLista title="Opzioni <br><center>${titoloMenu}</center>"	width="50">
						<c:if test="${currentRow >= 0}">
							<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
							<c:if test='${gene:checkProtFunz(pageContext, "DEL","DEL")}'>
								<gene:PopUpItemResource resource="popupmenu.tags.lista.elimina"
									title="Elimina" />
							</c:if>
						</gene:PopUp>
						<c:if test='${gene:checkProtFunz(pageContext, "DEL","LISTADELSEL")}'>
							<input type="checkbox" name="keys" value="${chiaveRiga}" />
						</c:if>
						</c:if>
					</gene:campoLista>
					
					<gene:campoLista campo="NGARA"  visibile="false"/>
					<gene:campoLista campo="NUMSED"  visibile="false"/>
					<gene:campoLista campo="CODFOF" title="Codice componente" width="140"/>
					<gene:campoLista campo="NOMFOF"  title="Nome componente" entita="GFOF" where="GFOF.CODFOF=GARSEDPRES.CODFOF and GFOF.NGARA2=GARSEDPRES.NGARA and GFOF.NUMCOMM=1"/>
					<input type="hidden" name="dataSeduta" id="dataSeduta" value="${dataSeduta}" />
				</gene:formLista></td>
			</tr>
			
			<gene:redefineInsert name="listaNuovo">
				<c:if test='${gene:checkProtFunz(pageContext,"INS","LISTANUOVO")}'>
					<tr>
						<td class="vocemenulaterale">
							<a href="javascript:aggiungiComponente('${numeroGara}','${numeroSeduta}');" title="Aggiungi componente alla seduta" tabindex="1501">
								Aggiungi componente alla seduta</a></td>
					</tr>
				</c:if>
			</gene:redefineInsert>
			
			<tr>
				<td class="comandi-dettaglio" colSpan="2">
					<c:if test='${gene:checkProtFunz(pageContext,"INS","LISTANUOVO")}'>
						<INPUT type="button"  class="bottone-azione" value='Aggiungi componente alla seduta' title='Aggiungi componente alla seduta' onclick="aggiungiComponente('${numeroGara}','${numeroSeduta}');">&nbsp;&nbsp;&nbsp;
					</c:if>
					<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")}'>
						<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' title='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' onclick="javascript:listaEliminaSelezione();">
					</c:if>
				</td>
			</tr>
			
		</table>
	</gene:redefineInsert>
	<gene:javaScript>
		function aggiungiComponente(numeroGara,numeroSeduta){
			openPopUpCustom("href=gare/garsedpres/gfof-garsedpres-lista-componenti-popup.jsp&numeroSeduta=" + numeroSeduta + "&numeroGara=" + numeroGara, "aggiungiComponenteSeduta", 650, 500, "yes", "yes");
		}
</gene:javaScript>
</gene:template>