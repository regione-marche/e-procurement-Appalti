<%
/*
 * Created on: 24/11/2021
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
 
<c:choose>
	<c:when test='${not empty param.codgar}'>
		<c:set var="codgar" value="${param.codgar}" />
	</c:when>
	<c:otherwise>
		<c:set var="codgar" value="${codgar}" />
	</c:otherwise>
</c:choose>
 
 <c:choose>
	<c:when test='${not empty param.ngara}'>
		<c:set var="ngara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${not empty param.dittao}'>
		<c:set var="dittao" value="${param.dittao}" />
	</c:when>
	<c:otherwise>
		<c:set var="dittao" value="${dittao}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.fase}'>
		<c:set var="fase" value="${param.fase}" />
	</c:when>
	<c:otherwise>
		<c:set var="fase" value="${fase}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.statoDGUE}'>
		<c:set var="statoDGUE" value="${param.statoDGUE}" />
	</c:when>
	<c:otherwise>
		<c:set var="statoDGUE" value="${statoDGUE}" />
	</c:otherwise>
</c:choose>

<c:set var="where" value="codgar='${codgar}' and codimp='${dittao}' and busta=${fase}"/>

<c:set var="nomimo" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetNOMIMOFunction", pageContext, ngara, codgar, dittao)}' />


<gene:template file="lista-template.jsp" gestisciProtezioni="true"
	schema="GARE" idMaschera="DGUE_ELABORAZIONI-lista">
	<gene:setString name="titoloMaschera"
		value="Analisi documenti DGUE della ditta ${nomimo}" />

	<gene:redefineInsert name="corpo">
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
		<table class="lista">
			<tr>
				<td><gene:formLista entita="V_DGUEELABSUB" sortColumn="1;5"
						pagesize="20" tableclass="datilista" gestisciProtezioni="true"
						where='${where}'>

						<gene:redefineInsert name="listaNuovo"></gene:redefineInsert>
						<gene:redefineInsert name="listaEliminaSelezione"></gene:redefineInsert>
						
						<gene:campoLista campo="TAB5NORD" visibile="false"/>
						<gene:campoLista campo="IDELABORAZIONE" visibile="false"/>
						<gene:campoLista campo="DIGNOMDOC" />
						<% //<gene:campoLista campo="DESCRIZIONE" entita="V_GARE_DOCDITTA" where="V_GARE_DOCDITTA.IDDOCDG=V_DGUEELABSUB.IDDOCDIG" /> %>
						<gene:campoLista campo="ESCLUSIONE" title="Criteri esclus.?"  />
						<gene:campoLista campo="DITTA" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampiDgue"/>
						<gene:campoLista campo="RUOLO" />
						<gene:campoLista campo="ISGRUPPO" />
						<gene:campoLista campo="NOMEGRUPPO" />
						<gene:campoLista campo="ISCONSORZIO" />
						<gene:campoLista campo="CONSORZIATE" />
						
						<gene:campoLista campo="SUB" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampiDgue" />
						<gene:campoLista campo="AUX" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampiDgue" />
						
						<gene:campoLista campo="STATO" visibile="false" entita="DGUE_ELABORAZIONI" where="DGUE_ELABORAZIONI.ID=V_DGUEELABSUB.IDELABORAZIONE"/>
						<gene:campoLista campo="ERRMSG" visibile="false" entita="DGUE_ELABORAZIONI" where="DGUE_ELABORAZIONI.ID=V_DGUEELABSUB.IDELABORAZIONE"/>
						<gene:campoLista title="&nbsp;" width="20">
						<c:choose>
							<c:when test="${datiRiga.DGUE_ELABORAZIONI_STATO.contains('ERROR')}">
								<c:set var="mdgue_img" value="4"/>
								<c:set var="mdgue_tooltip" value=""/>
							</c:when>
							<c:when test="${datiRiga.DGUE_ELABORAZIONI_STATO.contains('WARNING')}">
								<c:set var="mdgue_img" value="5"/>
								<c:set var="mdgue_tooltip" value="ATTENZIONE: dichiarati criteri di esclusione"/>
							</c:when>
							<c:otherwise>
								<c:set var="mdgue_img" value="3"/>
								<c:set var="mdgue_tooltip" value="Nessun criterio di esclusione dichiarato"/>
							</c:otherwise>
						</c:choose>
							<img width="16" height="16" title="${mdgue_tooltip}${datiRiga.DGUE_ELABORAZIONI_ERRMSG}" alt="${datiRiga.DGUE_ELABORAZIONI_ERRMSG}" src="${pageContext.request.contextPath}/img/statoMDGUE_${mdgue_img}.png"/>
						</gene:campoLista>
						<input type="hidden" name="codgar" id="codgar" value="${codgar}" />
						<input type="hidden" name="dittao" id="dittao" value="${dittao}" />
						<input type="hidden" name="fase" id="fase" value="${fase}" />
						<input type="hidden" name="statoDGUE" id="statoDGUE" value="${statoDGUE}" />
				</gene:formLista>
				</td>
			</tr>
		</table>
</gene:redefineInsert>
<gene:redefineInsert name="addToAzioni" >						
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:exportExcelDGUE('${dittao}');" title='Esporta in Excel' tabindex="1506">
					Esporta in Excel
				</a>
			</td>
		</tr>
</gene:redefineInsert>
<gene:javaScript>
	$(function() {
	  $('.rowValue').tooltip({
	    content: function(){
	      var element = $( this );
	      return element.attr('title')
	    }
	  });
	});
	
	function exportExcelDGUE(codimp) {
			var chiave="${key}";
			var ngara="${ngara}";
			var fase = "${fase}";
			var faseCall = 'Apertura doc. amministrativa';
			if(fase=='4')
				faseCall = 'Ricezione domande e offerte';
			var codgar="${codgar}";
			href = "href=gare/gare/gare-popup-esportaDocumentoDGUE.jsp&codgar=" + codgar + "&ngara="+ngara+"&codimp=" + codimp + "&faseCall=" + faseCall;
			openPopUpCustom(href, "analisiDocumentiDGUE", 450, 350, "yes", "yes");
		}
		
</gene:javaScript>
</gene:template>