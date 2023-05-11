<%
/*
 * Created on: 09-07-12
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 /* Pagina per visualizzazione/inserimento dell'offerta tecnica/economica di
  * una ditta per i diversi lotti per le gare a lotti con offerta unica, accessibile
  * dalle fasi offerta economica e offerta tecnica delle fasi di gara.
  *
  * Questa pagina e' stata ispirata alle fasi di gara (gare-pg-fasiGara.jsp) della
  * quale riprende:
  * - la logica per l'inizializzazione della lista;
  * - la logica del gestore di salvataggio;
  * - molto codice JS per aggiornamento dei campi;
  */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>


<jsp:include page="../gare/fasiGara/defStepWizardFasiGara.jsp" />

<c:set var="risultatiPerPagina" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.sil.pg.fasi.paginazione")}' scope="request"/>

<% // Set di una variabile temporanea per costruire il valore delle stringhe     %>
<% // strProtVisualizzaFasiGara e strProtModificaFasiGara in funzione dello step %>
<% // del wizard attivo. Questa variabile e' stata introdotta per non modificare %>
<% // i record presenti nella tabella W_OGGETTI (e tabelle collegate W_AZIONI e  %>
<% // W_PROAZI e di tutti di i profili esistenti) in seguito all'introduzione di %>
<% // nuovi step nel wizard fasi di gara %>

<c:set var="varTmp" value="${paginaAttivaWizard/10}" />
<c:if test='${fn:endsWith(varTmp, ".0")}'>
	<c:set var="varTmp" value='${fn:substringBefore(varTmp, ".0")}' />
</c:if>

<gene:template file="scheda-template.jsp" >
	<% // Settaggio delle stringhe utilizzate nel template %>

<c:choose>
	<c:when test='${!empty paginaAttivaWizard}'>
		<c:set var="paginaAttivaWizard" value='${paginaAttivaWizard}' />
	</c:when>
	<c:otherwise>
		<c:set var="paginaAttivaWizard" value="${param.paginaAttivaWizard}" />
	</c:otherwise>
</c:choose>




<c:choose>
	<c:when test='${!empty chiave}'>
		<c:set var="chiave" value='${chiave}' />
	</c:when>
	<c:otherwise>
		<c:set var="chiave" value="${param.chiave}" />
	</c:otherwise>
</c:choose>

<c:set var="codiceGara" value='${gene:getValCampo(chiave, "TORN.CODGAR")}' />

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.InizializzaUpdateListaFunction" parametro="${chiave}" />

<jsp:include page="/WEB-INF/pages/gare/gare/bloccaModifica-fasiGara.jsp" >
	<jsp:param name="entita" value="V_GARE_TORN" />
	<jsp:param name="inputFiltro" value="CODGAR=T:${codiceGara }" />
	<jsp:param name="filtroCampoEntita" value="codgar = '${codiceGara }'" />
	<jsp:param name="updateLista" value="${updateLista}" />
</jsp:include>

${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.ValidazioneParametroFunction", pageContext, codiceGara, "SC", "21")}
<c:set var="parametri" value="T:${codiceGara}"/>
<c:choose>
	<c:when test='${paginaAttivaWizard eq step6Wizard}'>
		<gene:setString name="titoloMaschera" value='Valutazione tecnica per lotto della gara ${codiceGara }' />
		<c:set var="where" value=" GARE.CODGAR1 = ? and GARE.GENERE is null AND (GARE.MODLICG = 6 or exists (select ngara from gare1 where gare.ngara=gare1.ngara and gare1.valtec='1'))"/>
	</c:when>
	<c:when test='${paginaAttivaWizard eq step7Wizard}'>
		<gene:setString name="titoloMaschera" value='Valutazione economica per lotto della gara ${codiceGara }' />
		<c:set var="where" value=" GARE.CODGAR1 = ? and GARE.GENERE is null AND GARE.MODLICG = 6 and ngara in (select ngara from gare1 where gare.ngara=gare1.ngara and (gare1.costofisso is null or gare1.costofisso<>'1'))"/>
	</c:when>
	
</c:choose>
${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.ImpostazioneFiltroFunction", pageContext, "GARE", where, parametri)}

<gene:redefineInsert name="corpo">

<table class="lista">


	<!-- inizia pagine a lista -->

		<tr>
			<td >
				<gene:formLista entita="GARE" tableclass="datilista" sortColumn="2" pagesize="20" >
					<gene:redefineInsert name="listaNuovo" />
					<gene:redefineInsert name="listaEliminaSelezione" />
										
					<gene:campoLista campo="CODGAR1" visibile="false" />					
					<gene:campoLista campo="NGARA" headerClass="sortable" width="120" title="Codice lotto"/>
					<gene:campoLista campo="CODIGA" title="Lotto" headerClass="sortable" width="100" />
					<gene:campoLista campo="NOT_GAR" ordinabile="false" />
					<gene:campoLista campo="IMPAPP" ordinabile="false" />
					<gene:campoLista title="&nbsp;" width="20">
						<a href='javascript:dettaglioOffertaPerLotto("${datiRiga.GARE_NGARA }");' title='Dettaglio valutazione ${gene:if(paginaAttivaWizard eq step6Wizard, "tecnica", "economica") }' >
							<img width="16" height="16" title='Dettaglio valutazione ${gene:if(paginaAttivaWizard eq step6Wizard, "tecnica", "economica") }' alt='Dettaglio valutazione ${gene:if(paginaAttivaWizard eq step6Wizard, "tecnica", "economica") }' src="${pageContext.request.contextPath}/img/offertaditta.png"/>
						</a>
					</gene:campoLista>
					<input type="hidden" name="chiave" value="${chiave}" />
					<input type="hidden" name="paginaAttivaWizard" value="${paginaAttivaWizard}" />
					
				</gene:formLista>
			</td>
		</tr>
	
<!-- fine pagine a lista -->
	<tr>
		<td class="comandi-dettaglio" colSpan="2">
			<INPUT type="button" class="bottone-azione" value="Torna a elenco concorrenti" title="Torna a elenco concorrenti" onclick="javascript:historyVaiIndietroDi(1);"/>&nbsp;

				
			&nbsp;
		</td>
	</tr>
	
</table>

<gene:javaScript>
	function dettaglioOffertaPerLotto(ngara){
		var codgar="${codiceGara }";
		var chiave="DITG.CODGAR5=T:" + codgar + ";DITG.NGARA5=T:"+ngara+";DITG.DITTAO=T:AAA";
		var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=gare/ditg/dettaglioOfferteDitta-OffertaUnicaLotti.jsp";
		href += "&key=" + chiave + "&paginaAttivaWizard=${paginaAttivaWizard}&isOffertaPerLotto=true";
		document.location.href = href;
		
	}
	

</gene:javaScript>

</gene:redefineInsert>
</gene:template>