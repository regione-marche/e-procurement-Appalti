<%/*
   * Created on 25-08-2013
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

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>
<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.PulisciSessioneFiltriFunction" parametro=""/>

<c:choose>
	<c:when test="${! empty sessionScope.uffint}">
		<c:set var="filtroUffint" value=" ANTICOR.CODEIN = '${sessionScope.uffint}'"/>
	</c:when>
	<c:otherwise>
		<c:set var="filtroUffint" value=" ANTICOR.CODEIN = '*'"/>
	</c:otherwise>	
</c:choose>

<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="ANTICOR-lista"  >
	<gene:setString name="titoloMaschera" value="Lista gare e contratti - adempimenti Legge 190/2012"/>
	<c:set var="visualizzaLink" value='${gene:checkProt(pageContext, "MASC.VIS.GARE.GAREAVVISI-scheda")}'/>
	
	<% // Ridefinisco il corpo della ricerca %>
	<gene:redefineInsert name="corpo">
		
  	<%// Creo la lista per gare e torn mediante la vista %>
		<table class="lista">
			<tr>
				<td>
  	<gene:formLista entita="ANTICOR" gestisciProtezioni="true" pagesize="20" tableclass="datilista" sortColumn="-3" where="${filtroUffint}" >

  			<gene:campoLista title="Opzioni" width="50">
				<c:if test="${currentRow >= 0}">
					<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
						<c:if test='${gene:checkProt(pageContext, "MASC.VIS.GARE.GARE-scheda")}' >
							<gene:PopUpItem title="Visualizza adempimento" href="javascript:listaVisualizza()" />
						</c:if>
						<c:if test='${gene:checkProtFunz(pageContext, "MOD","MOD") && datiRiga.ANTICOR_COMPLETATO ne "1" && sessionScope.profiloUtente.abilitazioneGare eq "A"}' >
							<gene:PopUpItem title="Modifica adempimento" href="javascript:listaModifica()" />
						</c:if>
						<c:if test='${gene:checkProtFunz(pageContext, "DEL","DEL") && empty datiRiga.ANTICOR_DATAPUBBL && sessionScope.profiloUtente.abilitazioneGare eq "A" }' >
							<gene:PopUpItem title="Elimina adempimento" href="javascript:listaElimina()" />
						</c:if>
					</gene:PopUp>
					<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL") && empty datiRiga.ANTICOR_DATAPUBBL && sessionScope.profiloUtente.abilitazioneGare eq "A"}'>
						<input type="checkbox" name="keys" value="${chiaveRiga}"  />
					</c:if>	
				</c:if>
			</gene:campoLista>
  			
  			<c:set var="link" value="javascript:chiaveRiga='${chiaveRigaJava}';visualizzaGara('${chiaveRigaJava}');" />
  			
			<% // Campi veri e propri %>
			<gene:campoLista campo="ID" headerClass="sortable" visibile="false"/>
			<gene:campoLista campo="ANNORIF" headerClass="sortable" href="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();"/>
			<gene:campoLista campo="TITOLO" headerClass="sortable" />
			<gene:campoLista campo="DATAPUBBL"  headerClass="sortable"  />
			<gene:campoLista campo="DATAAGG"  headerClass="sortable"  />
			<gene:campoLista campo="COMPLETATO" headerClass="sortable" />
			<gene:campoLista campo="ESPORTATO" title="Generato XML per ANAC?" headerClass="sortable" />
			<gene:campoLista campo="PUBBLICATO" title="Pubblicato XML su portale Appalti?" headerClass="sortable" visibile='${fn:contains(listaOpzioniDisponibili, "OP114#")}'/>
	</gene:formLista>
				</td>
			</tr>
			<tr>
				<c:if test="${sessionScope.profiloUtente.abilitazioneGare ne 'A'}" >
					<gene:redefineInsert name="listaNuovo" />
					<gene:redefineInsert name="listaEliminaSelezione" />
					<gene:redefineInsert name="pulsanteListaInserisci" />
					<gene:redefineInsert name="pulsanteListaEliminaSelezione" />
				</c:if>	
				<jsp:include page="/WEB-INF/pages/commons/pulsantiLista.jsp" />
			</tr>
		</table>
  </gene:redefineInsert>
  	<gene:javaScript>
		
	</gene:javaScript>
</gene:template>