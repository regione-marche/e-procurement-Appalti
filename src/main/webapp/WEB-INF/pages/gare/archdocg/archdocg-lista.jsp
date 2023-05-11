<%--

/*
 * Created on: 10-apr-2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

Lista categorie d'iscrizione

--%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="visualizzaLink" value='true'/>
<c:set var="gruppoSelezionato" value="${gene:if(empty param.TIPO,1,param.TIPO)}"/>

<c:set var="opzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" scope="request"/>


<c:choose>
	<c:when test='${not empty param.ordinamento}'>
		<c:set var="ordinamento" value="${param.ordinamento}" />
	</c:when>
	<c:otherwise>
		<c:set var="ordinamento" value="${ordinamento}" />
	</c:otherwise>
</c:choose>


<c:choose>
	<c:when test="${empty ordinamento }">
		<%//All'apertura della pagina la lista viene ordinata rispetto al campo ..., quindi nella costruzione automatica della %>
		<%//from viene inserita ... %>
		<c:set var="where" value="ARCHDOCG.GRUPPO=${gruppoSelezionato}" />
	</c:when>
	<c:otherwise>
		<%//Quando si forza l'ordinamento sulle colonne della lista non viene più inserita la  ...nella from, %>
		<%//quindi non si deve gestire la join %>
		<c:set var="where" value="ARCHDOCG.GRUPPO=${gruppoSelezionato}" />
		
	</c:otherwise>
</c:choose>


<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="ARCHDOCG-lista" >
	<gene:setString name="titoloMaschera" value="Documentazione di gara"/>
	
	<gene:redefineInsert name="pulsanteListaInserisci">
		<c:if test='${gene:checkProtFunz(pageContext,"INS","LISTANUOVO")}'>
			<INPUT type="button"  class="bottone-azione" value='Nuovo' title='${gene:resource("label.tags.template.lista.listaPageNuovo")}' onclick="listaNuovo();">
		</c:if>
	</gene:redefineInsert>
	
	
	<gene:redefineInsert name="corpo">
	
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
	
		<table class="lista">
		<tr>
			<td>
					<input type="radio" name="tipologia" value="1" onclick="cambiaTipoDocumentazione('1');" <c:if test="${gruppoSelezionato == 1}">checked="checked"
					</c:if>><c:out value="Documenti del bando"></c:out></input>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<input type="radio" name="tipologia" value="6" onclick="cambiaTipoDocumentazione('6');" <c:if test="${gruppoSelezionato == 6}">checked="checked"
					</c:if>><c:out value="Documenti dell'invito"></c:out></input>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;				
			</td>
		</tr>
		<tr>
			<td>
					<input type="radio" name="tipologia" value="2" onclick="cambiaTipoDocumentazione('2');" <c:if test="${gruppoSelezionato == 2}">checked="checked"
					</c:if>><c:out value="Requisiti dei concorrenti"></c:out></input>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<input type="radio" name="tipologia" value="3" onclick="cambiaTipoDocumentazione('3');" <c:if test="${gruppoSelezionato == 3}">checked="checked"
					</c:if>><c:out value="Documenti richiesti ai concorrenti"></c:out></input>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			</td>
		</tr>
		<tr>
			<td>
					<input type="radio" name="tipologia" value="4" onclick="cambiaTipoDocumentazione('4');" <c:if test="${gruppoSelezionato == 4}">checked="checked"
					</c:if>><c:out value="Documenti dell'esito"></c:out></input>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			</td>
		</tr>
		<tr>
			<td>
			<gene:formLista entita="ARCHDOCG" sortColumn="2;4" pagesize="20" tableclass="datilista"
				gestisciProtezioni="true" where="${where}" 
				gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreARCHDOCG"> 

				<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50">
					<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
						<gene:PopUpItemResource variableJs="rigaPopUpMenu${currentRow}" resource="popupmenu.tags.lista.visualizza" title="Visualizza documentazione" />
						<c:if test='${autorizzatoModifiche ne "2" && gene:checkProtFunz(pageContext, "MOD","MOD")}' >
							<gene:PopUpItemResource variableJs="rigaPopUpMenu${currentRow}" resource="popupmenu.tags.lista.modifica" title="Modifica documentazione"/>
						</c:if>
						<c:if test='${autorizzatoModifiche ne "2" && gene:checkProtFunz(pageContext, "DEL","DEL")}' >
							<gene:PopUpItemResource resource="popupmenu.tags.lista.elimina" title="Elimina documentazione" />
						</c:if>
					</gene:PopUp>
					<c:if test='${gene:checkProtFunz(pageContext, "DEL","LISTADELSEL")}'>
							<input type="checkbox" name="keys" value="${chiaveRiga}" />
					</c:if>
				</gene:campoLista>
				<%-- Campi veri e propri --%>
				<c:set var="link" value="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />

				<gene:campoLista campo="TIPOGARA"/>
				<gene:campoLista campo="GARTEL" width="80"/>
				<gene:campoLista campo="DESCRIZIONE" width="450" href="${gene:if(visualizzaLink, link, '')}" />
				<input type="hidden" name="TIPO" value="${gruppoSelezionato}" />	
				<input type="hidden" id="ordinamento" name="ordinamento" value="${ordinamento }"/>			
			</gene:formLista>
			</td>
		</tr>
		<tr>
			<jsp:include page="/WEB-INF/pages/commons/pulsantiListaPage.jsp" />
		</tr>
		</table>
  </gene:redefineInsert>

	<gene:javaScript>
	function cambiaTipoDocumentazione(tipo) {
		document.location.href="ApriPagina.do?"+csrfToken+"&href=gare/archdocg/archdocg-lista.jsp?TIPO="+tipo;
	}
	 
	
	var listaOrdinaPerDefault = listaOrdinaPer;
	var listaOrdinaPer = listaOrdinaPerCustom;
	function listaOrdinaPerCustom(campo){
		document.getElementById("ordinamento").value=campo;
		listaOrdinaPerDefault(campo);
	}
	document.forms[0].action += "?gruppoSelezionato=${gruppoSelezionato}";
	
	
  </gene:javaScript>
</gene:template>
