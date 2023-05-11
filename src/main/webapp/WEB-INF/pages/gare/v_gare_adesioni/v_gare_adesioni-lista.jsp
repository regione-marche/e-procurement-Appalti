<%
/*
 * Created on: 29-mar-2016
 *
 *
 * Copyright (c) Maggioli S.p.A. - Divisione ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A. - Divisione ELDASOFT
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 /* Lista Modelli di Comunicazioni */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${!empty param.ngaraaq}'>
		<c:set var="ngaraaq" value='${param.ngaraaq}' />
	</c:when>
	<c:otherwise>
		<c:set var="ngaraaq" value="${ngaraaq}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty param.cenint}'>
		<c:set var="cenint" value='${param.cenint}' />
	</c:when>
	<c:otherwise>
		<c:set var="cenint" value="${cenint}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty param.ditta}'>
		<c:set var="ditta" value='${param.ditta}' />
	</c:when>
	<c:otherwise>
		<c:set var="ditta" value="${ditta}" />
	</c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/pages/commons/defCostantiAppalti.jsp" />
<c:if test='${not empty ngaraaq and gene:matches(ngaraaq, regExpresValidazStringhe, true)}' />
<c:if test='${not empty cenint and gene:matches(cenint, regExpresValidazStringhe, true)}' />
<c:if test='${not empty ditta and gene:matches(ditta, regExpresValidazStringhe, true)}' />

${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.ValidazioneParametroFunction", pageContext, ngaraaq, "SC", "20")}
${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.ValidazioneParametroFunction", pageContext, cenint, "SC", "16")}
${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.ValidazioneParametroFunction", pageContext, ditta, "SC", "10")}

<c:set var="entitaPrincipaleModificabile" value="" scope="session" />

<c:set var="nomein" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetNOMEINFunction", pageContext, cenint)}'/>

<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="V_GARE_ADESIONI-lista" >
	<gene:setString name="titoloMaschera" value="Dettaglio impegnato in adesioni e confronti competitivi della stazione appaltante ${nomein }"/>

	<gene:redefineInsert name="corpo">
		<table class="lista">
		<tr><td >
			<gene:formLista entita="V_GARE_ADESIONI"  sortColumn="2;3" pagesize="20" tableclass="datilista" gestisciProtezioni="true" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreV_GARE_ADESIONILista">
			  	<gene:redefineInsert name="addHistory">
					<gene:historyAdd titolo='${gene:getString(pageContext,"titoloMaschera",gene:resource("label.tags.template.lista.titolo"))}' id="V_GARE_ADESIONI-lista" />
				</gene:redefineInsert>
				<gene:redefineInsert name="listaNuovo"></gene:redefineInsert>
				<gene:redefineInsert name="listaEliminaSelezione"></gene:redefineInsert>
				<c:if test="${gene:checkProtFunz(pageContext, 'ALT','apriDocAssociati')}">
					<gene:campoLista title="Opzioni" width="30">
					<c:if test="${not empty datiRiga.V_GARE_ADESIONI_NGARAAQ}">
						<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
							<gene:PopUpItem title="Visualizza documenti associati" href="documentiAssociati('${datiRiga.V_GARE_ADESIONI_NGARA}')" />
						</gene:PopUp>
					</c:if>
					</gene:campoLista>
				</c:if>
				<gene:campoLista campo="NGARA" />
				<gene:campoLista campo="CODCIG" />
				<gene:campoLista campo="OGGETTO" />
				<gene:campoLista campo="IMPAPP" headerClass="sortable" />
				<gene:campoLista campo="NOMEST" />
				<gene:campoLista campo="IAGGIU" />
				<gene:campoLista campo="DATTOA" />
				<gene:campoLista campo="NGARAAQ" visibile="false"/>
				<input type="hidden" name="ngaraaq" id="ngaraaq" value="${ngaraaq}"/>
				<input type="hidden" name="cenint" id="cenint" value="${cenint}"/>
				<input type="hidden" name="ditta" id="ditta" value="${ditta}"/>
			</gene:formLista>
		</td></tr>
		</table>
  <gene:javaScript>
	
	function documentiAssociati(chiave){
		var entita,valori;
		valori = "GARE.NGARA=T:"+chiave;
		entita = "GARE";
	  try {
		var href = contextPath+'/ListaDocumentiAssociati.do?'+csrfToken+'&metodo=visualizza&entita='+entita+'&valori='+valori;
		document.location.href = href;
    } catch(e) {
	  }
	}
	
	</gene:javaScript>
  </gene:redefineInsert>
</gene:template>
