<%
/*
 * Created on 18-ott-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI LISTA GRUPPI 
 // CONTENENTE LA EFFETTIVA LISTA DEI GRUPPI E LE RELATIVE FUNZIONALITA' 
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ page
	import="it.eldasoft.sil.pg.web.struts.sigla.*,
            it.eldasoft.gene.db.domain.genric.*,
			java.util.List"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="idAccountCorrente" value="${profiloUtente.id}" scope="request"/>
<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />

<gene:template file="menuAzioni-template.jsp">
<c:choose>
	<c:when test='${empty requestScope.cambioPaginaRicerca || requestScope.cambioPaginaRicerca ne "true"}' >
		<gene:insert name="addHistory">
			<gene:historyAdd titolo='Lista Articoli' id="Lista Articoli" />
		</gene:insert>
	</c:when>
	<c:otherwise>
		
	</c:otherwise>
</c:choose>
</gene:template>

<gene:template file="scheda-template.jsp">

	<gene:redefineInsert name="head" >
		<script type="text/javascript" src="${contextPath}/js/jquery.cookie.js"></script></script>
		<script type="text/javascript" src="${contextPath}/js/general.js"></script></script>
			
	</gene:redefineInsert>
	
	<gene:redefineInsert name="corpo">
		<gene:setString name="titoloMaschera" value="Lista Articoli" />
<c:choose>
	<c:when test='${not empty requestScope.reload and requestScope.reload eq "1"}' >
<script type="text/javascript">
		historyVaiIndietroDi(3);
</script>
	</c:when>
	<c:otherwise>
	
<form name="listaArticoli" action="FormRicercaInserimentoArticoli.do" method="post">			
	<table class="lista">
		<tr>
			<td>
				<display:table name="listaArticoli.elements" partialList="true" defaultsort="-1" id="articolo" class="datilista" sort="list" 
				 size="${listaArticoli.totalNumItems}" pagesize="${listaArticoli.maxItemsPerPage}" requestURI="FormRicercaInserimentoArticoli.do">
					<display:column class="associadati" title="Opzioni<br><center><a href='javascript:selezionaTutti(document.listaArticoli.elements);' Title='Seleziona Tutti'> <img src='${pageContext.request.contextPath}/img/ico_check.gif' height='15' width='15' alt='Seleziona Tutti'></a>&nbsp;<a href='javascript:deselezionaTutti(document.listaArticoli.elements);' Title='Deseleziona Tutti'><img src='${pageContext.request.contextPath}/img/ico_uncheck.gif' height='15' width='15' alt='Deseleziona Tutti'></a></center>" style="width:50px">
					<c:if test='${(gene:checkProt(pageContext,"FUNZ.VIS.DEL.GENE.USRSYS-Lista.LISTADELSEL")) && !((!fn:contains(listaOpzioniUtenteAbilitate, "ou89#")) && (fn:contains(account.opzioniUtente, "ou89|")))}'>
						<input type="checkbox" name="keys" value="${articolo.id}_#_${articolo.cd_bene_servizio}_#_${articolo.ds_bene_servizio}_#_${articolo.unita_misura}_#_${articolo.fl_gestione_inventario}_#_${articolo.fl_gestione_magazzino}_#_${articolo.cd_voce_iva}_#_${articolo.tipoGestione}"/>
					</c:if>
					</display:column>
					<display:column property="cd_bene_servizio" title="Codice articolo" sortable="false"	headerClass="sortable" />
					<display:column property="ds_bene_servizio" title="Descrizione" sortable="false" headerClass="sortable" />
					<display:column property="unita_misura" title="Unita di misura" sortable="false" headerClass="sortable" />
					<display:column property="fl_gestione_inventario" title="Gestione inventario" sortable="false" headerClass="sortable"/>
					<display:column property="fl_gestione_magazzino" title="Gestione magazzino" sortable="false" headerClass="sortable"/>
					<display:column property="cd_voce_iva" title="Iva" sortable="false" headerClass="sortable" />
					<display:column property="tipoGestione" title="Tipo gestione" sortable="false" headerClass="sortable" />
				</display:table>
			</td>
		</tr>
	<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.DEL.GENE.USRSYS-Lista.LISTADELSEL")}'>
		<tr>
			<td class="comandi-dettaglio" colSpan="2">
				<INPUT type="button"  class="bottone-azione" value='Inserisci' title='Inserisci' onclick="javascript:inserisci()">&nbsp;
			</td>
		</tr>
	</c:if>
	</table>
	<input type="hidden" name="metodo" id="metodo" value="inserisci">
	<input type="hidden" name="keys" id="keys" value="">
	<input type="hidden" name="ngara" id="ngara" value="${param.ngara}" />
</form>


</c:otherwise>
</c:choose>


	</gene:redefineInsert>
	<gene:javaScript>

		function inserisci(){
			var numeroOggetti = contaCheckSelezionati(document.forms[0].keys);
	  		if (numeroOggetti == 0) {
	      		alert("Selezionare almeno un articolo dalla lista");
	      	} else {
	      		document.listaArticoli.submit();
 			}
		}
		
	</gene:javaScript>
</gene:template>

