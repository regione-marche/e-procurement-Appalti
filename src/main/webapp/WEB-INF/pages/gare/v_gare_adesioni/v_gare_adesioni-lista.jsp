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


<c:set var="ngaraaq" value='${gene:getValCampo(key,"NGARAAQ") }'/>
<c:set var="cenint" value='${gene:getValCampo(key,"CENINT") }'/>
<c:set var="ditta" value='${gene:getValCampo(key,"DITTA") }'/>

<c:choose>
	<c:when test="${empty ditta or ditta eq '' }">
		<c:set var="wherecodcig" value="NGARA = '${ngaraaq}'"/>
		<c:set var="codcig" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreCampoDBFunction", pageContext, "CODCIG","GARE",wherecodcig)}'/>
		<c:choose>
			<c:when test="${not empty  codcig and codcig ne '' and not fn:startsWith(codcig, '#')}">
				<c:set var="where" value="(V_GARE_ADESIONI.NGARAAQ = '${ngaraaq }' or V_GARE_ADESIONI.CODCIGAQ = '${codcig }')"/>
			</c:when>
			<c:otherwise>
				<c:set var="where" value="V_GARE_ADESIONI.NGARAAQ = '${ngaraaq }'"/>
			</c:otherwise>
		</c:choose>
		
	</c:when>
	<c:otherwise>
		<c:set var="elencoLotti" value="${gene:callFunction3('it.eldasoft.sil.pg.tags.funzioni.GetFiltroElencoLottiAggiudicatiDittaFunction', pageContext,ngaraaq, ditta)}"/>
		<c:choose >
			<c:when test="${not empty elencoCig }">
				<c:set var="where" value="(V_GARE_ADESIONI.NGARAAQ in (${elencoLotti }) or V_GARE_ADESIONI.CODCIGAQ in (${elencoCig }))"/>
			</c:when>
			<c:otherwise>
				<c:set var="where" value="V_GARE_ADESIONI.NGARAAQ in (${elencoLotti })"/>
			</c:otherwise>
		</c:choose>
	</c:otherwise>
</c:choose>
<c:set var="where" value="${where}  and V_GARE_ADESIONI.CENINT = '${cenint }'"/>

<c:set var="entitaPrincipaleModificabile" value="" scope="session" />

<c:set var="nomein" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetNOMEINFunction", pageContext, cenint)}'/>

<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="V_GARE_ADESIONI-lista" >
	<gene:setString name="titoloMaschera" value="Dettaglio impegnato in adesioni e confronti competitivi della stazione appaltante ${nomein }"/>

	<gene:redefineInsert name="corpo">
		<table class="lista">
		<tr><td >
			<gene:formLista entita="V_GARE_ADESIONI" where="${where }" sortColumn="2;3" pagesize="20" tableclass="datilista" gestisciProtezioni="true" >
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
