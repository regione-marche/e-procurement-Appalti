<%/*
   * Created on 22-05-2012
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

<c:set var="filtroLivelloUtente" value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "NSO_ORDINI")}' />

<c:set var="filtroUffint" value=""/> 
<c:if test="${! empty sessionScope.uffint}">
	<c:set var="filtroUffint" value=" AND CODEIN = '${sessionScope.uffint}'"/>
</c:if>

<c:choose>
	<c:when test="${sessionScope.profiloUtente.ruoloUtenteMercatoElettronico eq 1}">
		<c:set var="isPuntoOrdinante" value="true" />
	</c:when>
	<c:otherwise>
		<c:set var="isPuntoOrdinante" value="false" />
	</c:otherwise>
</c:choose>


<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="NSO_ORDINI-lista" >

	<gene:setString name="titoloMaschera" value="Lista ordini"/>
	<c:set var="visualizzaLink" value='${gene:checkProt(pageContext, "MASC.VIS.GARE.NSO_ORDINI-scheda")}'/>
	
	<% // Ridefinisco il corpo della ricerca %>
	<gene:redefineInsert name="corpo">
	
	<gene:set name="titoloMenu">
		<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
	</gene:set>
	
		
  	<%// Creo la lista per gare e torn mediante la vista %>
		<table class="lista">
			<tr>
				<td>
  	<gene:formLista entita="NSO_ORDINI" gestisciProtezioni="true" pagesize="20" tableclass="datilista" sortColumn="-3" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreOrdiniNso"
  		where="VERSIONE=0 ${filtroUffint}">
			<gene:campoLista campo="ID" visibile="false" />
			<c:set var="tmp" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetNsoDatiListaOrdiniFunction", pageContext, datiRiga.NSO_ORDINI_ID)}'/>

			<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50">
				<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
						<gene:PopUpItem title="Visualizza ordine" href="javascript:listaVisualizza()" />
					<c:if test='${gene:checkProtFunz(pageContext, "MOD","MOD") && (datiRiga.NSO_ORDINI_STATO_ORDINE eq 1 || datiRiga.NSO_ORDINI_STATO_ORDINE eq 2)}' >
						<gene:PopUpItem title="Modifica ordine" href="javascript:listaModifica()" />
					</c:if>
					<c:if test='${gene:checkProtFunz(pageContext, "DEL","DEL") && (datiRiga.NSO_ORDINI_STATO_ORDINE eq 1 || datiRiga.NSO_ORDINI_STATO_ORDINE eq 2)}' >
						<gene:PopUpItem title="Elimina ordine" href="javascript:listaElimina()" />
					</c:if>
					<c:if test='${isPuntoOrdinante}'>
						<c:if test='${datiRiga.NSO_ORDINI_STATO_ORDINE eq 2}' >
							<gene:PopUpItem title="Invia ordine NSO" href="javascript:inviaOrdineNso('${chiaveRigaJava}')" />
						</c:if>
					</c:if>	
					<c:if test='${datiRiga.NSO_ORDINI_STATO_ORDINE eq 4 && requestScope.isPeriodoVariazione eq 1 }' >
						<gene:PopUpItem title="Variazione ordine NSO" href="javascript:variazioneOrdineNso('${chiaveRigaJava}')" />
					</c:if>
				</gene:PopUp>
				<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL") && (datiRiga.NSO_ORDINI_STATO_ORDINE eq 1 || datiRiga.NSO_ORDINI_STATO_ORDINE eq 2)}'>
						<input type="checkbox" name="keys" value="${chiaveRiga}"  />
				</c:if>	
		</gene:campoLista>
			<% // Campi veri e propri %>
			<c:set var="link" value="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
			<gene:campoLista campo="CODORD" title="Codice ordine" href="${gene:if(visualizzaLink, link, '')}" />
			<gene:campoLista campo="NGARA" title="Gara/Lotto di riferimento" />
			<gene:campoLista campo="OGGETTO" title="Oggetto" />
			<gene:campoLista campo="CUP" title="Codice CUP" />
			<gene:campoLista campo="CIG" title="Codice CIG" />
			<gene:campoLista title="Codice ordine collegato" campo="CODORD_COLLEGATO" entita="NSO_ORDINI" campoFittizio="true" definizione="T10" value="${requestScope.codiceOrdineCollegato}" visibile="true"/>
			<gene:campoLista title="Codice ordine originario" campo="CODORD_ORIGINARIO" entita="NSO_ORDINI" campoFittizio="true" definizione="T10" value="${requestScope.codiceOrdineOriginario}" visibile="true"/>
			<gene:campoLista campo="DATA_ORDINE" title="Data ordine" />
			<gene:campoLista campo="DATA_SCADENZA" title="Data scadenza" />
			<gene:campoLista campo="STATO_ORDINE" title="Stato ordine" />
			<gene:campoLista campo="ID_PADRE" visibile="false" />
			<gene:campoLista campo="ID_ORIGINARIO" visibile="false" />
			<gene:campoLista campo="CODEIN" visibile="false" />
		</gene:formLista>
				</td>
			</tr>
			<tr>
				<jsp:include page="/WEB-INF/pages/commons/pulsantiLista.jsp" />
			</tr>
		</table>
		
		
  </gene:redefineInsert>

	<gene:javaScript>

	
	function listaNuovo(){
		document.location.href = "${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/nso_ordini/nso_associaCig.jsp&tipoGara=garaLottoUnico&modo=NUOVO";
	}
	
	// Funzione che esegue l'invio a NSO'
	function inviaOrdineNso(key){
		alert('Funzione in sviluppo');
	}

	// Funzione che esegue la variazione di un ordine
	function variazioneOrdineNso(key){
		openPopUpCustom("href=gare/nso_ordini/nso_variazione-ordine.jsp&key="+key, "variazioneOrdine", 480, 320, "yes", "yes");
	}
	
	</gene:javaScript>
</gene:template>