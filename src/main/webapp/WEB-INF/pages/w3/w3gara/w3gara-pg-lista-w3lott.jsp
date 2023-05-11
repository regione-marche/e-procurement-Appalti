
<%
	/*
	 * Created on 23-Apr-2008
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

<c:set var="stato_simog_gara" value='${gene:callFunction2("it.eldasoft.sil.w3.tags.funzioni.GetStatoSimogW3GaraFunction",pageContext,numgara)}' />

<gene:set name="titoloMenu">
	<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
</gene:set>
<table class="dettaglio-tab-lista">

	<tr>
		<td>
			<br>
			Filtro di visualizzazione sullo stato dei lotti: 
			<select name="filtroStatoSimog" id="filtroStatoSimog" onChange="javascript:aggiornaLista();">
				<option value="TUTTI">Visualizza tutti i lotti indipendentemente dallo stato</option>
				<option value="SIMOG">Visualizza solo i lotti da inviare ad ANAC</option>
			</select>
		</td>
	</tr>
	
	<tr>
		<td>
		<gene:formLista entita="W3LOTT" pagesize="20" sortColumn="3"
			where="W3LOTT.NUMGARA = #W3GARA.NUMGARA#" tableclass="datilista"
			gestisciProtezioni="true" gestore="it.eldasoft.sil.w3.tags.gestori.submit.GestoreW3LOTT"
			plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreW3GARELotti">
			
			<input type="hidden" name="filtroStatoSimog" />	
					 
			<gene:campoLista title="Opzioni <br><center>${titoloMenu}</center>"
				width="50">
				<c:if test="${currentRow >= 0}">
				
					<gene:PopUp variableJs="rigaPopUpMenu${currentRow}"
						onClick="chiaveRiga='${chiaveRigaJava}'">
						
						<gene:PopUpItemResource resource="popupmenu.tags.lista.visualizza"
							title="Visualizza lotto" />
						
						<c:if test='${gene:checkProtFunz(pageContext, "MOD","MOD")}'>
							<c:if test="${!(stato_simog_gara eq '5' or stato_simog_gara eq '6' or stato_simog_gara eq '7')}">
								<c:if test="${!(datiRiga.W3LOTT_STATO_SIMOG eq '5' or datiRiga.W3LOTT_STATO_SIMOG eq '6' or datiRiga.W3LOTT_STATO_SIMOG eq '7')}">
									<gene:PopUpItemResource resource="popupmenu.tags.lista.modifica"
										title="Modifica lotto" />
								</c:if>
							</c:if>
						</c:if>
						
						<c:if test='${gene:checkProtFunz(pageContext, "DEL","DEL")}'>
							<c:if test="${!(stato_simog_gara eq '5' or stato_simog_gara eq '6' or stato_simog_gara eq '7')}">
								<c:if test="${empty datiRiga.W3LOTT_CIG}">
									<gene:PopUpItemResource resource="popupmenu.tags.lista.elimina"
										title="Elimina lotto" />
								</c:if>
							</c:if>
						</c:if>
					</gene:PopUp>
					
					<c:if test='${gene:checkProtFunz(pageContext, "DEL","LISTADELSEL")}'>
						<c:choose>
							<c:when test="${empty datiRiga.W3LOTT_CIG and !(stato_simog_gara eq '5' or stato_simog_gara eq '6' or stato_simog_gara eq '7')}">
								<input type="checkbox" name="keys" value="${chiaveRiga}" />
							</c:when>
							<c:otherwise>
								<input type="checkbox" name="keys" value="${chiaveRiga}" disabled="disabled"/>
							</c:otherwise>
						</c:choose>
					</c:if>
				</c:if>
			
			</gene:campoLista>
			
			<gene:campoLista campo="NUMGARA" visibile="false"/>
			<gene:campoLista title="N." campo="NUMLOTT" width="30" href="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
			<gene:campoLista campo="CIG" width="80" href="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
			<gene:campoLista campo="STATO_SIMOG" width="120"/>
			<gene:campoLista campo="DATA_PUBBLICAZIONE" width="100"/>
			<gene:campoLista campo="OGGETTO" href="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
			<gene:campoLista campo="TIPO_CONTRATTO"/>
			<gene:campoLista campo="IMPORTO_LOTTO"/>
			<gene:campoLista title="&nbsp;" width="24">
				<c:choose>
					<c:when test="${datiRiga.W3LOTT_STATO_SIMOG eq '1' or datiRiga.W3LOTT_STATO_SIMOG eq '3' or datiRiga.W3LOTT_STATO_SIMOG eq '5'}">
						<img width="24" height="24" title="" alt="" src="${pageContext.request.contextPath}/img/invioFlusso.png"/>	
					</c:when>
				</c:choose>
			</gene:campoLista>
			
	
		</gene:formLista></td>
	</tr>
	<c:if test="${stato_simog_gara eq '5' or stato_simog_gara eq '6' or stato_simog_gara eq '7'}">
		<gene:redefineInsert name="pulsanteListaInserisci"/>
		<gene:redefineInsert name="pulsanteListaEliminaSelezione"/>
		<gene:redefineInsert name="listaNuovo"/>
		<gene:redefineInsert name="listaEliminaSelezione"/>
	</c:if>
	<tr>
		<jsp:include page="/WEB-INF/pages/commons/pulsantiListaPage.jsp" />
	</tr>

	<gene:redefineInsert name="addToAzioni" >
		<jsp:include page="/WEB-INF/pages/w3/commons/addtodocumenti-richieste-lotti.jsp" />
	</gene:redefineInsert>

</table>

<gene:javaScript>
	
	aggiornaFiltroStatoSimog();	
		
	function aggiornaFiltroStatoSimog() {
		document.getElementById("filtroStatoSimog").value = "${param.filtroStatoSimog}";
	
		if (document.getElementById("filtroStatoSimog").value == "") {
			document.getElementById("filtroStatoSimog").value = "TUTTI";
		}
		
		document.forms[0].value = document.getElementById("filtroStatoSimog").value;
	}
		
		
	function aggiornaLista(){
		document.forms[0].filtroStatoSimog.value = document.getElementById("filtroStatoSimog").value;
		listaVaiAPagina(0);
	}

</gene:javaScript>	




