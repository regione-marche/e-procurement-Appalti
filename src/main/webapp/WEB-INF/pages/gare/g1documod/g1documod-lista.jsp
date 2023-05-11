
<%
/*
 * Created on: 17-mag-2021
 *
 *
 * Copyright (c) Maggioli S.p.A. - Divisione ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A. - Divisione ELDASOFT
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 /* Lista Documentazione di Contratto */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="idDocumod" value='${gene:getValCampo(key, "G1DOCUMOD.ID")}' />
<c:set var="tmp" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetDatiG1documodFunction", pageContext, idDocumod)}'/>
<c:set var="visualizzaLink" value='true' />

<c:choose>
	<c:when test='${not empty param.gruppo}'>
		<c:set var="gruppo" value="${param.gruppo}" />
	</c:when>
	<c:otherwise>
		<c:set var="gruppo" value="${gruppo}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.busta}'>
		<c:set var="busta" value="${param.busta}" />
	</c:when>
	<c:otherwise>
		<c:set var="busta" value="${busta}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${gruppo eq 3}'>
		<c:set var="titoloMaschera" value='Modelli di Documenti della busta ${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoFunction", pageContext, "A1013",busta,"false")}' />
	</c:when>
	<c:otherwise>
		<c:set var="titoloMaschera" value='Modelli di ${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoFunction", pageContext, "A1064",gruppo,"false")}' />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.busta}'>
		<c:set var="where" value="gruppo = '${gruppo}' and busta = '${busta}'" />
	</c:when>
	<c:otherwise>
		<c:set var="where" value="gruppo = '${gruppo}'" />
	</c:otherwise>
</c:choose>

<gene:template file="lista-template.jsp" gestisciProtezioni="true"
	schema="GARE" idMaschera="G1DOCUMOD-lista">
	<gene:setString name="titoloMaschera"
		value="${titoloMaschera}" />

	<gene:redefineInsert name="corpo">
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
		<table class="lista">
			<tr>
				<td><gene:formLista entita="G1DOCUMOD" sortColumn="2"
						pagesize="20" tableclass="datilista" gestisciProtezioni="true"
						where='${where}'
						gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreG1DOCUMOD">
						<gene:campoLista title="Opzioni<center>${titoloMenu}</center>"
							width="50">
							<gene:PopUp variableJs="rigaPopUpMenu${currentRow}"
								onClick="chiaveRiga='${chiaveRigaJava}'">
								<gene:PopUpItem title="Visualizza modello"
									href="javascript:listaVisualizza()" />
								<c:if test='${gene:checkProtFunz(pageContext, "MOD","MOD")}'>
									<gene:PopUpItem title="Modifica modello"
										href="javascript:listaModifica()" />
								</c:if>
								<c:if test='${gene:checkProtFunz(pageContext, "DEL","DEL")}'>
									<gene:PopUpItem title="Elimina modello"
										href="javascript:listaElimina()" />
								</c:if>
							</gene:PopUp>
							<c:if
								test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")}'>
								<input type="checkbox" name="keys" value="${chiaveRiga}" />
							</c:if>
						</gene:campoLista>
						<%
						// Campi veri e propri
						%>
						<c:set var="link"
							value="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />

						<gene:campoLista campo="ID" visibile="false" />
						<gene:campoLista campo="DESCRIZIONE" width="450"
							href="${gene:if(visualizzaLink, link, '')}" />							
						<gene:campoLista campo="TIPOGARA" visibile='${gruppo ne 20}'/>
						<gene:campoLista campo="TIPOPROC" visibile='${gruppo ne 20}'/>
						<gene:campoLista campo="CRITLIC" visibile='${gruppo ne 20}'/>
						<gene:campoLista campo="GARTEL" visibile='${gruppo ne 20}'/>
						<gene:campoLista campo="ESCLUSO" visibile='${gruppo eq 20}'/>
						<gene:campoLista campo="LIMINF" title="Da importo"/>
						<gene:campoLista campo="LIMSUP" title="A importo"/>
						<gene:campoLista title="N.doc." campo="N_DOC_MOD"
							entita="G1DOCUMOD" width="32" campoFittizio="true"
							definizione="N7;;;" value=""
							gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoNumeroDocumentiPerModello" />

						<input type="hidden" name="gruppo" id="gruppo" value="${gruppo}" />
						<input type="hidden" name="busta" id="busta" value="${busta}" />
						<input type="hidden" name="titoloMaschera" id="titoloMaschera" value="${titoloMaschera}" />
						<input type="hidden" name="where" id="where" value="${where}" />

					</gene:formLista></td>
			</tr>
			<tr>
				<td class="comandi-dettaglio" colSpan="2"><gene:insert
						name="addPulsanti" /> <gene:insert name="pulsanteListaInserisci">
						<c:if test='${gene:checkProtFunz(pageContext,"INS","LISTANUOVO")}'>
							<INPUT type="button" class="bottone-azione"
								value='${gene:resource("label.tags.template.lista.listaNuovo")}'
								title='${gene:resource("label.tags.template.lista.listaNuovo")}'
								onclick="javascript:listaNuovoCustom()">
						</c:if>
					</gene:insert> <gene:insert name="pulsanteListaEliminaSelezione">
						<c:if
							test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")}'>
							<INPUT type="button" class="bottone-azione"
								value='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}'
								title='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}'
								onclick="javascript:listaEliminaSelezione()">
						</c:if>
					</gene:insert> &nbsp;</td>
			</tr>
		</table>
	</gene:redefineInsert>
<gene:javaScript>
function listaNuovoCustom() {
	document.forms[0].action=document.forms[0].action+"&gruppo=${gruppo}"+"&busta=${busta}";
	listaNuovoDefault();
}

var listaNuovoDefault = listaNuovo;
var listaNuovo = listaNuovoCustom;

</gene:javaScript>
</gene:template>