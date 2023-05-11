<%/*
   * Created on 12-04-2017
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${!empty ngara}'>
		<c:set var="ngara" value='${ngara}' />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${param.ngara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty codgar}'>
		<c:set var="codgar" value='${codgar}' />
	</c:when>
	<c:otherwise>
		<c:set var="codgar" value="${param.codgar}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty titolo}'>
		<c:set var="titolo" value='${titolo}' />
	</c:when>
	<c:otherwise>
		<c:set var="titolo" value="${param.titolo}" />
	</c:otherwise>
</c:choose>

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.InizializzaUpdateListaFunction" parametro="${key}" />


<c:choose>
	<c:when test='${updateLista ne 1}'>
		<c:set var="where" value="UFFINT.CODEIN IN (select cenint from garaltsog where ngara='${ngara}' )" />
	</c:when>
	<c:otherwise>
		<c:set var="where" value="UFFINT.DATFIN is null and (UFFINT.ISCUC is null or UFFINT.ISCUC <>'1')" />
	</c:otherwise>
</c:choose>

<gene:template file="popup-template.jsp" gestisciProtezioni="false">
	<gene:setString name="titoloMaschera" value="${titolo}"/>
	
	<% // Ridefinisco il corpo della ricerca %>
	<gene:redefineInsert name="corpo">
	
		
	<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-pg-lista-scheda.jsp">
		<jsp:param name="entita" value="V_GARE_TORN"/>
		<jsp:param name="inputFiltro" value="CODGAR=T:${codgar}"/>
		<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
	</jsp:include>
	
	<c:if test="${updateLista eq 1}">
	<c:set var="sottotitolo" value='${fn:substringAfter(titolo,"Elenco soggetti")}'/>
	
	<br>
	Nella lista sotto sono riportati gli uffici intestatari attivi in archivio.	Selezionare quelli ${sottotitolo}.
	</c:if>
		
  	<%// Creo la lista per gcap e dpre mediante la vista v_gcap_dpre%>
		<table class="lista">
			
			<tr>
				<td ${stileDati}>
  				<gene:formLista entita="UFFINT" where='${where}' tableclass="datilista" sortColumn="5" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupDettaglioSoggettiAggregati">
 					<gene:redefineInsert name="listaNuovo" />
					<gene:redefineInsert name="listaEliminaSelezione" />
					<gene:redefineInsert name="documentiAzioni" />
									
										
					<c:choose>
						<c:when test='${(updateLista eq 1)}' >
							<gene:set name="titoloMenu">
								<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
							</gene:set>
							<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50" >
								<c:if test='${currentRow >= 0 }'>
									<input type="checkbox" name="keys" value="${datiRiga.UFFINT_CODEIN}"  <c:if test="${datiRiga.UFFINT_CODEIN eq datiRiga.GARALTSOG_CENINT}">checked="checked"</c:if> onclick="javascript:aggiornaRiga(this,${currentRow + 1});"/>
								</c:if>
							</gene:campoLista>
						</c:when>
						<c:otherwise>
									<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" visibile="false" />
						</c:otherwise>
					</c:choose>
					
					<gene:campoLista campo="ID" entita="GARALTSOG"  where = "UFFINT.CODEIN=GARALTSOG.CENINT and GARALTSOG.NGARA='${ngara }'" edit="${updateLista eq 1}" visibile="false"/>
					<gene:campoLista campo="CODEIN" width="80" edit="false"  />	
					<gene:campoLista campo="CODEIN_FIT" campoFittizio="true" definizione="T16" edit="true"  visibile="false" value="${datiRiga.UFFINT_CODEIN }" />	
					<gene:campoLista campo="NOMEIN"  edit="false"/>
					<gene:campoLista campo="CFEIN" width="100" edit="false"/>
					<gene:campoLista campo="CENINT" entita="GARALTSOG"  where = "UFFINT.CODEIN=GARALTSOG.CENINT and GARALTSOG.NGARA='${ngara }'" edit="${updateLista eq 1}" visibile="false"/>
					
						
					<input type="hidden" name="numeroSoggetti" id="numeroSoggetti" value="" />
					<input type="hidden" name="ngara" id="ngara" value="${ngara}" />
					<input type="hidden" name="codgar" id="codgar" value="${codgar}" />
					<input type="hidden" name="titolo" id="titolo" value="${titolo}" />
				</gene:formLista>
				</td>
			</tr>
						
			<tr>
				<td class="comandi-dettaglio"  colSpan="2">
					<c:choose>
						<c:when test='${updateLista eq 1}'>
							<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:listaConferma();">
							<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:listaAnnullaModifica();">
						</c:when>
						<c:otherwise>
							<c:if test='${autorizzatoModifiche ne 2}'>
								<INPUT type="button"  class="bottone-azione" value='Modifica elenco soggetti' title='Modifica elenco soggetti' onclick="javascript:listaApriInModifica();">&nbsp;&nbsp;
							</c:if>						
							<INPUT type="button"  class="bottone-azione" value='Chiudi' title='Chiudi' onclick="chiudi();">
							&nbsp;
							
						</c:otherwise>
					</c:choose>
					&nbsp;
				</td>
			</tr>
			
					
		</table>
  </gene:redefineInsert>
	<gene:javaScript>
		document.getElementById("numeroSoggetti").value = ${currentRow}+1;
		
		function chiudi(){
			window.close();
		}
		
	</gene:javaScript>
		
</gene:template>