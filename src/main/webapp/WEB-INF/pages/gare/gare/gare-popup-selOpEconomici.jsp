
<%
	/*
	 * Created on 06-09-2010
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
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:choose>
	<c:when test='${not empty param.ngara}'>
		<c:set var="ngara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.aggnumord}'>
		<c:set var="aggnumord" value="${param.aggnumord}" />
	</c:when>
	<c:otherwise>
		<c:set var="aggnumord" value="${aggnumord}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.garaElenco}'>
		<c:set var="garaElenco" value="${param.garaElenco}"  />
	</c:when>
	<c:otherwise>
		<c:set var="garaElenco" value="${garaElenco}" />
	</c:otherwise>
</c:choose>

<c:choose>
     <c:when test='${not empty param.categoriaPrev}'>
             <c:set var="categoriaPrev" value="${param.categoriaPrev}"  />
     </c:when>
	<c:otherwise>
		<c:set var="categoriaPrev" value="${categoriaPrev}" />
	</c:otherwise>
</c:choose>

<c:choose>
     <c:when test='${not empty param.tipoCategoria}'>
             <c:set var="tipoCategoria" value="${param.tipoCategoria}"  />
     </c:when>
	<c:otherwise>
		<c:set var="tipoCategoria" value="${tipoCategoria}" />
	</c:otherwise>
</c:choose>

<c:choose>
     <c:when test='${not empty param.classifica}'>
             <c:set var="classifica" value="${param.classifica}"  />
     </c:when>
	<c:otherwise>
		<c:set var="classifica" value="${classifica}" />
	</c:otherwise>
</c:choose>

<c:choose>
     <c:when test='${not empty param.tipoGara}'>
             <c:set var="tipoGara" value="${param.tipoGara}"  />
     </c:when>
	<c:otherwise>
		<c:set var="tipoGara" value="${tipoGara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.inserimentoDitteIterSemplificato}'>
		<c:set var="inserimentoDitteIterSemplificato" value="${param.inserimentoDitteIterSemplificato}" />
	</c:when>
	<c:otherwise>
		<c:set var="inserimentoDitteIterSemplificato" value="${inserimentoDitteIterSemplificato}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.isGaraLottiConOffertaUnica}'>
		<c:set var="isGaraLottiConOffertaUnica" value="${param.isGaraLottiConOffertaUnica}" />
	</c:when>
	<c:otherwise>
		<c:set var="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.WIZARD_PAGINA_ATTIVA}'>
		<c:set var="WIZARD_PAGINA_ATTIVA" value="${param.WIZARD_PAGINA_ATTIVA}" />
	</c:when>
	<c:otherwise>
		<c:set var="WIZARD_PAGINA_ATTIVA" value="${WIZARD_PAGINA_ATTIVA}" />
	</c:otherwise>
</c:choose>


<c:choose>
	<c:when test='${not empty param.AGGIORNAMENTO_NUMORD}'>
		<c:set var="AGGIORNAMENTO_NUMORD" value="${param.AGGIORNAMENTO_NUMORD}" />
	</c:when>
	<c:otherwise>
		<c:set var="AGGIORNAMENTO_NUMORD" value="${AGGIORNAMENTO_NUMORD}" />
	</c:otherwise>
</c:choose>


<c:choose>
	<c:when test='${not empty param.AGGIORNAMENTO_IMPORTO}'>
		<c:set var="AGGIORNAMENTO_IMPORTO" value="${param.AGGIORNAMENTO_IMPORTO}" />
	</c:when>
	<c:otherwise>
		<c:set var="AGGIORNAMENTO_IMPORTO" value="${AGGIORNAMENTO_IMPORTO}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.minOp}'>
		<fmt:parseNumber var="minOp" type="number" value="${param.minOp}"/>
	</c:when>
	<c:otherwise>
		<fmt:parseNumber var="minOp" type="number" value="${minOp}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.limSupDitteSel}'>
		<fmt:parseNumber var="limSupDitteSel" type="number" value="${param.limSupDitteSel}"/>
	</c:when>
	<c:otherwise>
		<fmt:parseNumber var="limSupDitteSel" type="number" value="${limSupDitteSel}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.tipoalgo}'>
		<c:set var="tipoalgo" value="${param.tipoalgo}" />
	</c:when>
	<c:otherwise>
		<c:set var="tipoalgo" value="${tipoalgo}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.stazioneAppaltante}'>
		<c:set var="stazioneAppaltante" value="${param.stazioneAppaltante}" />
	</c:when>
	<c:otherwise>
		<c:set var="stazioneAppaltante" value="${stazioneAppaltante}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param._csrf}'>
		<c:set var="_csrf" value="${param._csrf}" />
	</c:when>
	<c:otherwise>
		<c:set var="_csrf" value="${_csrf}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.selezioneAutomaticaDitte}'>
		<c:set var="selezioneAutomaticaDitte" value="${param.selezioneAutomaticaDitte}" />
	</c:when>
	<c:otherwise>
		<c:set var="selezioneAutomaticaDitte" value="${selezioneAutomaticaDitte}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.modalitaSelezioneMista}'>
		<c:set var="modalitaSelezioneMista" value="${param.modalitaSelezioneMista}" />
	</c:when>
	<c:otherwise>
		<c:set var="modalitaSelezioneMista" value="${modalitaSelezioneMista}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${selezioneAutomaticaDitte eq "true"}'>
		<c:set var="msgModalita" value="automatica"/>
	</c:when>
	<c:otherwise>
		<c:set var="msgModalita" value="manuale"/>
	</c:otherwise>
</c:choose>

<c:if test='${empty RISULTATO or RISULTATO ne "OK"}' >
	${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetDatiGarealboFunction", pageContext, garaElenco)}
</c:if>

<c:set var="where" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetFiltroSelOperatoriFunction", pageContext)}' scope="request"/>

<c:choose>
	<c:when test='${not empty RISULTATO and RISULTATO eq "OK"}' >
		<script type="text/javascript">
			window.opener.document.forms[0].pgSort.value = "";
			window.opener.document.forms[0].pgLastSort.value = "";
			window.opener.document.forms[0].pgLastValori.value = "";
			window.opener.bloccaRichiesteServer();
			window.opener.listaVaiAPagina(0);
			window.close();
		</script>
	</c:when>
	<c:when test='${aggnumord eq "1" and empty AGGIORNAMENTO_NUMORD and selezioneAutomaticaDitte eq "false"}' >
		<c:set var="modo" value="MODIFICA" scope="request" />
		<gene:template file="popup-template.jsp" gestisciProtezioni="true" idMaschera="${entita}-lista" schema="GARE">
		<gene:redefineInsert name="head">
			<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>		
		</gene:redefineInsert>
		
		
		<gene:setString name="titoloMaschera" value="Selezione ${msgModalita } ditte dall'elenco operatori economici ${garaElenco} mediante rotazione" />
		<gene:setString name="entita" value="${entita}" />
		<gene:redefineInsert name="corpo">
			
			<br/>
			E' in corso il riassegnamento con modalità casuale del numero d'ordine degli operatori in elenco ...
			<br/><br/>
			<gene:formLista entita="DITG" pagesize="${selezioneAutomaticaDitte eq 'false' ? 20 : 0}" tableclass="datilista" gestisciProtezioni="false" sortColumn="1" where="1<>1" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupAssociaOperatoriEconomici">
				<gene:campoLista campo="NGARA5"  visibile="false"/>
				<input type="hidden" name="where" id="where" value="${where}" />
				<input type="hidden" name="ngara" id="ngara" value="${ngara}" />
                   <input type="hidden" name="garaElenco" id="garaElenco" value="${garaElenco}" />
                   <input type="hidden" name="categoriaPrev" id="categoriaPrev" value="${categoriaPrev}" />
                   <input type="hidden" name="classifica" id="classifica" value="${classifica}" />
                   <input type="hidden" name="WIZARD_PAGINA_ATTIVA" id="WIZARD_PAGINA_ATTIVA" value="${WIZARD_PAGINA_ATTIVA}" />
				<input type="hidden" name="isGaraLottiConOffertaUnica" id="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
				<input type="hidden" name="inserimentoDitteIterSemplificato" id="inserimentoDitteIterSemplificato" value="${inserimentoDitteIterSemplificato}" />
				<input type="hidden" name="tipoGara" id="tipoGara" value="${tipoGara}" />
				<input type="hidden" name="aggnumord" id="aggnumord" value="${aggnumord}" />
				<input type="hidden" name="eseguireAggiornamentoNumOrdine" id="eseguireAggiornamentoNumOrdine" value="SI" />
				<input type="hidden" name="eseguireCalcoloImporto" id="eseguireCalcoloImporto" value="NO" />
				<input type="hidden" name="AGGIORNAMENTO_NUMORD" id="AGGIORNAMENTO_NUMORD" value="${AGGIORNAMENTO_NUMORD }" />
				<input type="hidden" name="AGGIORNAMENTO_IMPORTO" id="AGGIORNAMENTO_IMPORTO" value="${AGGIORNAMENTO_IMPORTO }" />
				<input type="hidden" name="tipoCategoria" id="tipoCategoria" value="${tipoCategoria }" />
				<input type="hidden" name="minOp" id="minOp" value="${minOp }" />
				<input type="hidden" name="limSupDitteSel" id="limSupDitteSel" value="${limSupDitteSel}" />
				<input type="hidden" name="tipoalgo" id="tipoalgo" value="${tipoalgo }" />
				<input type="hidden" name="stazioneAppaltante" id="stazioneAppaltante" value="${stazioneAppaltante }" />
				<input type="hidden" name="ctrlaggiu" id="ctrlaggiu" value="${ctrlaggiu}" />
				<input type="hidden" name="_csrf" id="_csrf" value="${_csrf}" />
				<input type="hidden" name="selezioneAutomaticaDitte" id="selezioneAutomaticaDitte" value="${selezioneAutomaticaDitte}" />
				<input type="hidden" name="modalitaSelezioneMista" id="modalitaSelezioneMista" value="${modalitaSelezioneMista}" />
			</gene:formLista>
		</gene:redefineInsert>
		<gene:javaScript>
		 $("[id^=tabformLista]").hide();
		document.forms[0].pgSort.value = "";
		document.forms[0].pgLastSort.value = "";
		document.forms[0].pgLastValori.value = "";
		//Si blocca in modifica la pagina
		document.getElementById('bloccaScreen').style.visibility='visible';
		$('#bloccaScreen').css("width",$(document).width());
		$('#bloccaScreen').css("height",$(document).height());
		document.getElementById('wait').style.visibility='visible';
		$("#wait").offset({ top: $(window).height() / 2, left: ($(window).width() / 2) - 200});
		listaConferma();
		
	</gene:javaScript>
		</gene:template>
	</c:when>
	<c:when test='${(ctrlaggiu eq "1" or ctrlaggiu eq "2") and empty AGGIORNAMENTO_IMPORTO }' >
		<c:set var="modo" value="MODIFICA" scope="request" />
		<gene:template file="popup-template.jsp" gestisciProtezioni="true" idMaschera="${entita}-lista" schema="GARE">
		<gene:setString name="titoloMaschera" value="Selezione ${msgModalita } ditte dall'elenco operatori economici ${garaElenco} mediante rotazione" />
		<gene:setString name="entita" value="${entita}" />
		<gene:redefineInsert name="corpo">
			
			<br/>
			E' in corso il calcolo dell'importo aggiudicato nel periodo degli operatori in elenco ...
			<br/><br/>
			<gene:formLista entita="DITG" pagesize="${selezioneAutomaticaDitte eq 'false' ? 20 : 0}" tableclass="datilista" gestisciProtezioni="false" sortColumn="1" where="1<>1" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupAssociaOperatoriEconomici">
				<gene:campoLista campo="NGARA5"  visibile="false"/>
				<input type="hidden" name="where" id="where" value="${where}" />
				<input type="hidden" name="ngara" id="ngara" value="${ngara}" />
                   <input type="hidden" name="garaElenco" id="garaElenco" value="${garaElenco}" />
                   <input type="hidden" name="categoriaPrev" id="categoriaPrev" value="${categoriaPrev}" />
                   <input type="hidden" name="classifica" id="classifica" value="${classifica}" />
                   <input type="hidden" name="WIZARD_PAGINA_ATTIVA" id="WIZARD_PAGINA_ATTIVA" value="${WIZARD_PAGINA_ATTIVA}" />
				<input type="hidden" name="isGaraLottiConOffertaUnica" id="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
				<input type="hidden" name="inserimentoDitteIterSemplificato" id="inserimentoDitteIterSemplificato" value="${inserimentoDitteIterSemplificato}" />
				<input type="hidden" name="tipoGara" id="tipoGara" value="${tipoGara}" />
				<input type="hidden" name="aggnumord" id="aggnumord" value="${aggnumord}" />
				<input type="hidden" name="eseguireAggiornamentoNumOrdine" id="eseguireAggiornamentoNumOrdine" value="NO" />
				<input type="hidden" name="eseguireCalcoloImporto" id="eseguireCalcoloImporto" value="SI" />
				<input type="hidden" name="AGGIORNAMENTO_NUMORD" id="AGGIORNAMENTO_NUMORD" value="${AGGIORNAMENTO_NUMORD }" />
				<input type="hidden" name="AGGIORNAMENTO_IMPORTO" id="AGGIORNAMENTO_IMPORTO" value="${AGGIORNAMENTO_IMPORTO }" />
				<input type="hidden" name="tipoCategoria" id="tipoCategoria" value="${tipoCategoria }" />
				<input type="hidden" name="minOp" id="minOp" value="${minOp }" />
				<input type="hidden" name="limSupDitteSel" id="limSupDitteSel" value="${limSupDitteSel}" />
				<input type="hidden" name="tipoalgo" id="tipoalgo" value="${tipoalgo }" />
				<input type="hidden" name="stazioneAppaltante" id="stazioneAppaltante" value="${stazioneAppaltante }" />
				<input type="hidden" name="ctrlaggiu" id="ctrlaggiu" value="${ctrlaggiu}" />
				<input type="hidden" name="_csrf" id="_csrf" value="${_csrf}" />
				<input type="hidden" name="selezioneAutomaticaDitte" id="selezioneAutomaticaDitte" value="${selezioneAutomaticaDitte}" />
				<input type="hidden" name="modalitaSelezioneMista" id="modalitaSelezioneMista" value="${modalitaSelezioneMista}" />
			</gene:formLista>
		</gene:redefineInsert>
		<gene:javaScript>
		 $("[id^=tabformLista]").hide();
		document.forms[0].pgSort.value = "";
		document.forms[0].pgLastSort.value = "";
		document.forms[0].pgLastValori.value = "";
		//Si blocca in modifica la pagina
		document.getElementById('bloccaScreen').style.visibility='visible';
		$('#bloccaScreen').css("width",$(document).width());
		$('#bloccaScreen').css("height",$(document).height());
		document.getElementById('wait').style.visibility='visible';
		$("#wait").offset({ top: $(window).height() / 2, left: ($(window).width() / 2) - 200});
		listaConferma();
		
	</gene:javaScript>
		</gene:template>
	</c:when>
	<c:otherwise>
	
	
	
	
<c:if test="${!empty elencoNumcla}">
	<c:set var="classificaCatPrev" value='${fn:substringBefore(elencoNumcla,",")}'/>
	<c:if test='${empty classificaCatPrev && fn:indexOf(elencoNumcla,",")<0}'>
		<c:set var="classificaCatPrev" value='${elencoNumcla}'/>
	</c:if>
</c:if>

<c:set var="ordinamento" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetOrdinamentoElencoOpEconomiciFunction", pageContext,tipoalgo,classificaCatPrev,entita)}' scope="request"/>

${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetImportoGaraFunction", pageContext,ngara,isGaraLottiConOffertaUnica)}

<c:set var="isPopolatatW_PUSER" value='${gene:callFunction("it.eldasoft.gene.tags.functions.isPopolatatW_PUSERFunction", pageContext)}' />
					
<c:set var="modo" value="MODIFICA" scope="request" />

<gene:template file="popup-template.jsp" gestisciProtezioni="true" idMaschera="${entita}-lista" schema="GARE">
	<gene:redefineInsert name="head">
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>	
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/controlliFormali.js"></script>	
	</gene:redefineInsert>
	
	<gene:setString name="titoloMaschera" value="Selezione ${msgModalita } ditte dall'elenco operatori economici ${garaElenco} mediante rotazione" />
	<gene:setString name="entita" value="${entita}" />
	<gene:redefineInsert name="corpo">
	
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
		
		<c:if test="${selezioneAutomaticaDitte eq 'false'}">
			<br/>
			Nella lista sottostante sono riportate le ditte abilitate all'elenco operatori economici 
			a cui è stato assegnato un numero ordine e qualificate per le seguenti categorie o prestazioni della gara corrente:<br>
			${tableCategorie }
			<c:if test="${tableFiltriAggiuntiviObbl ne '' || tableFiltriAggiuntivi ne '' || tableZoneAttivita ne ''}">
				<br/>Gli operatori sono ulteriormente filtrati in base ai seguenti criteri:<br/>
			</c:if>
			<c:if test="${tableFiltriAggiuntiviObbl ne ''}">
				${tableFiltriAggiuntiviObbl}
			</c:if>
			<c:if test="${tableFiltriAggiuntivi ne ''}">
				${tableFiltriAggiuntivi}
			</c:if>
			<c:if test="${tableZoneAttivita ne ''}">
				Zone di attivit&agrave;:
				${tableZoneAttivita}
			</c:if>
			<c:if test="${tableAffidatariEsclusi ne ''}">
				<br/>Viene escluso dalla selezione l'affidatario uscente:<br/>
				${tableAffidatariEsclusi}
			</c:if>
			<br/>
			L'ordine con cui sono presentate le ditte si basa sul criterio di rotazione '${criterioRotazioneDesc}'.
			<c:choose>
				<c:when test="${aggnumord eq '1'}">
					In particolare il numero d'ordine è stato appena riassegnato alle ditte con modalità casuale.
				</c:when>
				<c:when test="${tipoalgo eq 2}">
					Prima di procedere alla selezione, verificare che agli operatori in elenco sia stato riassegnato 
					il numero ordine con modalità casuale.
				</c:when>
			</c:choose>
			<c:if test='${ctrlaggiu eq 1 or ctrlaggiu eq 2}'>
				<br/><br/>
				E' previsto il controllo sull'importo aggiudicato complessivo degli operatori: 
				per ogni operatore vengono considerate le procedure aggiudicate negli ultimi ${ctrlimpValorePeriodo} giorni.
				<c:if test='${ctrlimpga eq 1}'>Nell'importo aggiudicato viene conteggiato anche l'importo a base di gara della procedura corrente.</c:if> 
				L'importo limite è ${ctrlimpValorePerVisualizzazione}
				<c:if test='${ctrlaggiu eq 1}'> e gli operatori che superano tale importo non possono venire selezionati</c:if>.
				<c:set var="ditteSelezionabili" value="0"/>
			</c:if>
			<br/><br/>
			<c:if test='${not empty minOp}'>
				<b>Numero minimo ditte da selezionare, ove esistenti:</b> <span id="minOp">${gene:if(minOp eq "0", "nessuna limitazione", minOp)}</span><br/>
			</c:if>
			Selezionare le ditte che si intende inserire in gara.
			<br/>
		</c:if>			
		<c:if test="${selezioneAutomaticaDitte eq 'true'}">
			<br>
			Confermi l'inserimento delle ditte in gara mediante selezione da elenco?
			<br><br>
			<font color='#0000FF'>ATTENZIONE: Si sottolinea che la selezione automatica può essere attivata una sola volta e che le ditte che vengono inserite in gara mediante questa selezione non possono essere eliminate.
			<c:choose>
				<c:when test="${modalitaSelezioneMista eq 'true' }">
					<br>Se mediante la selezione automatica non si raggiunge il numero di ditte da invitare,
					è possibile integrare l'elenco delle ditte da invitare mediante la selezione manuale da elenco o mediante la selezione da anagrafica.
				</c:when>
			</c:choose>
			</font>			
			<br/>
			<br/>
			Vengono considerate le ditte abilitate all'elenco operatori economici 
			a cui è stato assegnato un numero ordine e qualificate per le seguenti categorie o prestazioni della gara corrente:<br/>
			${tableCategorie }
			<c:if test="${tableFiltriAggiuntiviObbl ne '' || tableFiltriAggiuntivi ne '' || tableZoneAttivita ne ''}">
				<br/>Gli operatori sono ulteriormente filtrati in base ai seguenti criteri:<br/>
			</c:if>
			<c:if test="${tableFiltriAggiuntiviObbl ne ''}">
				${tableFiltriAggiuntiviObbl}
			</c:if>
			<c:if test="${tableFiltriAggiuntivi ne ''}">
				${tableFiltriAggiuntivi}
			</c:if>
			<c:if test="${tableZoneAttivita ne ''}">
				Zone di attivit&agrave;:
				${tableZoneAttivita}
			</c:if>
			<c:if test="${tableAffidatariEsclusi ne ''}">
				<br/>Viene escluso dalla selezione l'affidatario uscente:<br/>
				${tableAffidatariEsclusi}
			</c:if>
			<br/>			
			L'ordine con cui sono selezionate le ditte si basa sul criterio di rotazione '${criterioRotazioneDesc}'.
			<c:choose>
				<c:when test="${aggnumord eq '1'}">
					Prima di procedere alla selezione il numero d'ordine delle ditte viene riassegnato con modalità casuale.
				</c:when>
				<c:when test="${tipoalgo eq 2}">
					Prima di procedere, verificare che agli operatori in elenco sia stato riassegnato 
					il numero ordine con modalità casuale.
				</c:when>
			</c:choose>
			<c:if test='${ctrlaggiu eq 1}'>
				<br/><br/>
				E' previsto il controllo sull'importo aggiudicato complessivo degli operatori: 
				per ogni operatore vengono considerate le procedure aggiudicate negli ultimi ${ctrlimpValorePeriodo} giorni. 
				<c:if test='${ctrlimpga eq 1}'>Nell'importo aggiudicato viene conteggiato anche l'importo a base di gara della procedura corrente.</c:if> 
				L'importo limite è ${ctrlimpValorePerVisualizzazione} e gli operatori che superano tale limite non vengono selezionati.
				<c:set var="ditteSelezionabili" value="0"/>
			</c:if>
			<br/><br/>
			<c:if test='${not empty minOp}'>
				<b>Numero minimo ditte da selezionare, ove esistenti:</b> <span id="minOp">${gene:if(minOp eq "0", "nessuna limitazione", minOp)}</span><br/>
			</c:if>
		</c:if>
		
		<c:if test="${ctrlimpga ne 1 }">
			<c:set var="importoGara" value="0"/>
		</c:if>
		
		<table class="lista">
			<tr>
				<td><gene:formLista entita="${entita}" pagesize="${selezioneAutomaticaDitte eq 'false' ? 20 : 0}" tableclass="datilista" gestisciProtezioni="true" sortColumn="${ordinamento}" where="${where}" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupAssociaOperatoriEconomici">
					<gene:campoLista title="Opzioni <br><center>${titoloMenu}</center>"	width="50">
						<c:if test="${currentRow >= 0}">
							<c:choose>
								<c:when test='${entita == "V_DITTE_ELECAT"}'>
									<c:set var="valoreIMPAGG" value="${datiRiga.V_DITTE_ELECAT_IAGGIUELE + importoGara }"/>
								</c:when>
								<c:when test='${entita == "V_DITTE_ELECAT_SA"}'>
									<c:set var="valoreIMPAGG" value="${datiRiga.V_DITTE_ELECAT_SA_IAGGIUELE + importoGara }"/>
								</c:when>
								<c:otherwise>
									<c:set var="valoreIMPAGG" value="${datiRiga.V_DITTE_ELESUM_IAGGIUELE + importoGara }"/>
								</c:otherwise>
							</c:choose>
							<input type="hidden" name="imprese" value="${chiaveRiga}" />
							<input type="checkbox" name="keys" value="${chiaveRiga}" <c:if test="${ctrlaggiu eq 1 and valoreIMPAGG > ctrlimp}">disabled style="display: none;"</c:if> />
							<c:if test="${ctrlaggiu eq 1 and ctrlimp >= valoreIMPAGG }">
								<c:set var="ditteSelezionabili" value="${ditteSelezionabili + 1 }"/>
							</c:if>
							<input type="hidden" name="campoDisabilitatoImportoSopraLimite" value="${ctrlaggiu eq 1 and valoreIMPAGG > ctrlimp}"/>
						</c:if>
					</gene:campoLista>
					
					<gene:campoLista campo="GARA"  visibile="false"/>
					<gene:campoLista campo="CODICE" visibile="false"/>
					<gene:campoLista campo="RAGSOC" ordinabile="${selezioneAutomaticaDitte eq 'false'}"/>
					<gene:campoLista campo="LOCIMP" ordinabile="false" visibile="false"/>
					<gene:campoLista campo="PROIMP" ordinabile="false" visibile="false"/>
					
					<c:choose>
						<c:when test='${entita == "V_DITTE_ELECAT"}'>
							<gene:campoLista campo="LUOGO_DITTA" campoFittizio="true" value='${gene:concat(datiRiga.V_DITTE_ELECAT_LOCIMP, gene:if(empty datiRiga.V_DITTE_ELECAT_PROIMP, "", gene:concat(" (", gene:concat(datiRiga.V_DITTE_ELECAT_PROIMP, ")"))))}' definizione="T100;;;;LOCIMP" ordinabile="false"/>
						</c:when>
						<c:when test='${entita == "V_DITTE_ELECAT_SA"}'>
							<gene:campoLista campo="LUOGO_DITTA" campoFittizio="true" value='${gene:concat(datiRiga.V_DITTE_ELECAT_SA_LOCIMP, gene:if(empty datiRiga.V_DITTE_ELECAT_SA_PROIMP, "", gene:concat(" (", gene:concat(datiRiga.V_DITTE_ELECAT_SA_PROIMP, ")"))))}' definizione="T100;;;;LOCIMP" ordinabile="false"/>
						</c:when>
						<c:otherwise>
							<gene:campoLista campo="LUOGO_DITTA" campoFittizio="true" value='${gene:concat(datiRiga.V_DITTE_ELESUM_LOCIMP, gene:if(empty datiRiga.V_DITTE_ELESUM_PROIMP, "", gene:concat(" (", gene:concat(datiRiga.V_DITTE_ELESUM_PROIMP, ")"))))}' definizione="T100;;;;LOCIMP" ordinabile="false"/>
						</c:otherwise>
					</c:choose>
					<c:choose>
						<c:when test="${entita == 'V_DITTE_ELECAT_SA' }">
							<gene:campoLista campo="NUMIRTOT" ordinabile="${selezioneAutomaticaDitte eq 'false'}" title="N.inviti e penalità" visibile="${tipoalgo eq '9'}" width="80"/>
							<gene:campoLista campo="NUMIPTOT" ordinabile="${selezioneAutomaticaDitte eq 'false'}" title="N.inviti e penalità" visibile="${tipoalgo eq '8'}" width="80"/>
							<gene:campoLista campo="NUMINVTOT" ordinabile="${selezioneAutomaticaDitte eq 'false'}" title="N.inviti" visibile="${tipoalgo eq '8' or tipoalgo eq '9'}" width="80"/>
							<gene:campoLista campo="NUMPENTOT" ordinabile="${selezioneAutomaticaDitte eq 'false'}" title="N.inviti virtuali" visibile="${tipoalgo eq '9'}" width="80"/>
							<gene:campoLista campo="NUMAGGTOT" ordinabile="${selezioneAutomaticaDitte eq 'false'}" title="N.aggiud." visibile="${tipoalgo eq '8' or tipoalgo eq '9'}" width="80"/>
							<gene:campoLista campo="NUMALTTOT" ordinabile="${selezioneAutomaticaDitte eq 'false'}" title="N.penalità" visibile="${tipoalgo eq '8' or tipoalgo eq '9'}" width="80"/>
						</c:when>
						<c:otherwise>
							<gene:campoLista campo="NUMIR" ordinabile="${selezioneAutomaticaDitte eq 'false'}" title="N.inviti e penalità" visibile="${tipoalgo eq '1' or (tipoalgo eq '5' and empty classificaCatPrev ) or (tipoalgo eq '7' and empty categoriaPrev)}" width="80"/>
							<gene:campoLista campo="NUMIP" ordinabile="${selezioneAutomaticaDitte eq 'false'}" title="N.inviti e penalità" visibile="${tipoalgo eq '3' or (tipoalgo eq '4' and empty classificaCatPrev ) or (tipoalgo eq '6' and empty categoriaPrev)}" width="80"/>
							<gene:campoLista campo="NUMAP" ordinabile="${selezioneAutomaticaDitte eq 'false'}" title="N.aggiud.e penalità" visibile="${tipoalgo eq '11' or tipoalgo eq '14' or ((tipoalgo eq '12' or tipoalgo eq '15') and empty classificaCatPrev ) or ((tipoalgo eq '10' or tipoalgo eq '13') and empty categoriaPrev)}" width="80"/>
							<gene:campoLista campo="NUMINV" ordinabile="${selezioneAutomaticaDitte eq 'false'}" title="N.inviti" visibile="${tipoalgo eq '1' or tipoalgo eq '3' or tipoalgo eq '11' or tipoalgo eq '14' or ((tipoalgo eq '4' or tipoalgo eq '5' or tipoalgo eq '12' or tipoalgo eq '15') and empty classificaCatPrev ) or ((tipoalgo eq '6' or tipoalgo eq '7' or tipoalgo eq '10' or tipoalgo eq '13') and empty categoriaPrev)}" width="60"/>
							<gene:campoLista campo="NUMPEN" ordinabile="${selezioneAutomaticaDitte eq 'false'}" title="N.inviti virtuali" visibile="${tipoalgo eq '1' or (tipoalgo eq '5' and empty classificaCatPrev ) or (tipoalgo eq '7' and empty categoriaPrev)}" width="60"/>
							<gene:campoLista campo="NUMAGG" ordinabile="${selezioneAutomaticaDitte eq 'false'}" title="N.aggiud." visibile="${tipoalgo eq '1' or tipoalgo eq '3' or tipoalgo eq '11'  or tipoalgo eq '14' or ((tipoalgo eq '4' or tipoalgo eq '5' or tipoalgo eq '12' or tipoalgo eq '15') and empty classificaCatPrev ) or ((tipoalgo eq '6' or tipoalgo eq '7' or tipoalgo eq '10' or tipoalgo eq '13') and empty categoriaPrev)}" width="60"/>
							<gene:campoLista campo="NUMALT" ordinabile="${selezioneAutomaticaDitte eq 'false'}" title="N.penalità" visibile="${tipoalgo eq '1' or tipoalgo eq '3' or tipoalgo eq '11' or tipoalgo eq '14' or ((tipoalgo eq '4' or tipoalgo eq '5' or tipoalgo eq '12' or tipoalgo eq '15') and empty classificaCatPrev ) or ((tipoalgo eq '6' or tipoalgo eq '7'  or tipoalgo eq '10' or tipoalgo eq '13') and empty categoriaPrev)}" width="60"/>
							<c:if test='${entita == "V_DITTE_ELECAT"}'>
								<gene:campoLista campo="NUMIRCLA" title="N.inviti e penalità" ordinabile="${selezioneAutomaticaDitte eq 'false'}" visibile="${tipoalgo eq '5' and !empty classificaCatPrev }" width="80"/>
								<gene:campoLista campo="NUMIPCLA" title="N.inviti e penalità" ordinabile="${selezioneAutomaticaDitte eq 'false'}" visibile="${tipoalgo eq '4' and !empty classificaCatPrev }" width="80"/>
								<gene:campoLista campo="NUMAPCLA" title="N.aggiud.e penalità" ordinabile="${selezioneAutomaticaDitte eq 'false'}" visibile="${(tipoalgo eq '12' or tipoalgo eq '15') and !empty classificaCatPrev }" width="80"/>
								<gene:campoLista campo="NUMINVCLA" title="N.inviti" ordinabile="${selezioneAutomaticaDitte eq 'false'}" visibile="${(tipoalgo eq '4' or tipoalgo eq '5' or tipoalgo eq '12' or tipoalgo eq '15') and !empty classificaCatPrev }" width="60"/>
								<gene:campoLista campo="NUMPENCLA" title="N.inviti virtuali" ordinabile="${selezioneAutomaticaDitte eq 'false'}" visibile="${tipoalgo eq '5' and !empty classificaCatPrev }" width="60"/>
								<gene:campoLista campo="NUMAGGCLA" title="N.aggiud." ordinabile="${selezioneAutomaticaDitte eq 'false'}" visibile="${(tipoalgo eq '4' or tipoalgo eq '5' or tipoalgo eq '12' or tipoalgo eq '15') and !empty classificaCatPrev }" width="60"/>
								<gene:campoLista campo="NUMALTCLA" title="N.penalità" ordinabile="${selezioneAutomaticaDitte eq 'false'}" visibile="${(tipoalgo eq '4' or tipoalgo eq '5' or tipoalgo eq '12' or tipoalgo eq '15') and !empty classificaCatPrev }" width="60"/>
								<gene:campoLista campo="NUMIRTOT"  title="N.inviti e penalità" ordinabile="${selezioneAutomaticaDitte eq 'false'}" visibile="${tipoalgo eq '7'}" width="80"/>
								<gene:campoLista campo="NUMIPTOT" title="N.inviti e penalità" ordinabile="${selezioneAutomaticaDitte eq 'false'}" visibile="${tipoalgo eq '6'}" width="80"/>
								<gene:campoLista campo="NUMAPTOT" title="N.aggiud.e penalità" ordinabile="${selezioneAutomaticaDitte eq 'false'}" visibile="${tipoalgo eq '10' or tipoalgo eq '13'}" width="80"/>
								<gene:campoLista campo="NUMINVTOT" title="N.inviti" ordinabile="${selezioneAutomaticaDitte eq 'false'}" visibile="${tipoalgo eq '6' or tipoalgo eq '7' or tipoalgo eq '10' or tipoalgo eq '13'}" width="60"/>
								<gene:campoLista campo="NUMPENTOT" title="N.inviti virtuali" ordinabile="${selezioneAutomaticaDitte eq 'false'}" visibile="${tipoalgo eq '7'}" width="60"/>
								<gene:campoLista campo="NUMAGGTOT" title="N.aggiud." ordinabile="${selezioneAutomaticaDitte eq 'false'}" visibile="${tipoalgo eq '6' or tipoalgo eq '7' or tipoalgo eq '10' or tipoalgo eq '13'}" width="60"/>
								<gene:campoLista campo="NUMALTTOT" title="N.penalità" ordinabile="${selezioneAutomaticaDitte eq 'false'}" visibile="${tipoalgo eq '6' or tipoalgo eq '7' or tipoalgo eq '10' or tipoalgo eq '13'}" width="60"/>
							</c:if>
						</c:otherwise>
					</c:choose>
					
					<gene:campoLista campo="NUMORD" width="60" ordinabile="${selezioneAutomaticaDitte eq 'false'}" visibile="${selezioneAutomaticaDitte eq 'false'}"/>
					
					<c:if test='${ctrlaggiu eq 1 or ctrlaggiu eq 2}'>
						<gene:campoLista campo="IAGGIUELE" visibile="false" />
						<c:choose>
							<c:when test='${entita == "V_DITTE_ELECAT"}'>
								<c:set var="valoreIMPAGG" value="${datiRiga.V_DITTE_ELECAT_IAGGIUELE + importoGara }"/>
							</c:when>
							<c:when test='${entita == "V_DITTE_ELECAT_SA"}'>
								<c:set var="valoreIMPAGG" value="${datiRiga.V_DITTE_ELECAT_SA_IAGGIUELE + importoGara }"/>
							</c:when>
							<c:otherwise>
								<c:set var="valoreIMPAGG" value="${datiRiga.V_DITTE_ELESUM_IAGGIUELE + importoGara }"/>
							</c:otherwise>
						</c:choose>
						<gene:campoLista title="Imp. aggiudicato" campo="IMPAGG" campoFittizio="true" definizione="TF24.5;0;;MONEY;G1IAGGIUELE" value="${valoreIMPAGG }" />
						<gene:campoLista title="&nbsp;" width="20" >
							<c:if test="${valoreIMPAGG > ctrlimp }">
								<img width="16" height="16" title="Imp. aggiudicato nel periodo superiore all'importo limite" alt="Imp. aggiudicato nel periodo superiore all'importo limite" src="${pageContext.request.contextPath}/img/isquantimod.png"/>
							</c:if>
						</gene:campoLista>
					</c:if>
					
					<c:choose>
						<c:when test='${entita == "V_DITTE_ELECAT_SA"}'>
							<c:set var="codiceDitta" value="${datiRiga.V_DITTE_ELECAT_SA_CODICE}" />
							<c:set var="ragSociale" value="${datiRiga.V_DITTE_ELECAT_SA_RAGSOC}" />
						</c:when>
						<c:when test='${entita == "V_DITTE_ELECAT"}'>
							<c:set var="codiceDitta" value="${datiRiga.V_DITTE_ELECAT_CODICE}" />
							<c:set var="ragSociale" value="${datiRiga.V_DITTE_ELECAT_RAGSOC}" />
						</c:when>
						<c:otherwise>
							<c:set var="codiceDitta" value="${datiRiga.V_DITTE_ELESUM_CODICE}" />
							<c:set var="ragSociale" value="${datiRiga.V_DITTE_ELESUM_RAGSOC}" />
						</c:otherwise>
					</c:choose>
						
					<c:if test="${gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.VerificaDocumentiSelezioneDitteElenco')}">
						<gene:campoLista title="&nbsp;" width="20" >
							<a href="javascript:consultaDocumentiRichiesti('${codiceDitta}');" title="'Consultazione documenti iscrizione a elenco" >
								<img width="16" height="16" title="Consultazione documenti iscrizione a elenco" alt="Consultazione documenti iscrizione a elenco" src="${pageContext.request.contextPath}/img/documentazione_elenco.png"/>
							</a>
						</gene:campoLista>
					</c:if>
					
					<gene:campoLista title="&nbsp;" width="20" >
						<a href="javascript:apriElencoGareFornitore('${codiceDitta}');" title="Dettaglio inviti ricevuti" >
							<img width="16" height="16" title="Dettaglio inviti ricevuti" alt="Dettaglio gare a cui la ditta è stata invitata" src="${pageContext.request.contextPath}/img/consultaGare.png"/>
						</a>
					</gene:campoLista>
					
					<c:if test="${!empty categoriaPrev && gene:checkProt(pageContext, 'MASC.VIS.GARE.ISCRIZCAT-scheda')}">
						<gene:campoLista title="&nbsp;" width="20" >
							<a href="javascript:chiaveRiga='${chiaveRigaJava}';apriPopupUltInfo('${chiaveRigaJava}');" title="Ulteriori informazioni iscrizione a elenco per la categoria prevalente" >
									<img width="16" height="16" title="Ulteriori informazioni iscrizione a elenco per la categoria prevalente" alt="Ulteriori informazioni iscrizione a elenco per la categoria prevalente" src="${pageContext.request.contextPath}/img/opzioniUlteriori.png"/>
							</a>
						</gene:campoLista>
					</c:if>
					<gene:campoLista title="&nbsp;" width="20">
					<c:set var="key" value=";DITG.DITTAO=T:${codiceDitta}"/>
					<c:set var="tipoImpresa" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipologiaImpresaFunction",  pageContext,key)}'/>
					<c:if test="${isPopolatatW_PUSER == 'SI'}">
						<c:choose>
							<c:when test="${tipoImpresa eq '3' or tipoImpresa eq '10'}">
								<c:set var="dittaoIcona" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetMandatariaRTFunction",  pageContext, codiceDitta )}'/>
							</c:when>
							<c:otherwise>
								<c:set var="dittaoIcona" value='${codiceDitta}'/>
							</c:otherwise>
						</c:choose>
					</c:if>
					<c:set var="impresaRegistrata" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.ImpresaRegistrataSuPortaleFunction",  pageContext, dittaoIcona )}'/>
					<c:if test="${impresaRegistrata == 'SI'}">
							<img width="16" height="16" title="Ditta registrata su portale" alt="Ditta registrata su portale" src="${pageContext.request.contextPath}/img/ditta_acquisita.png"/>
					</c:if>
					</gene:campoLista >
					
					
					
					<input type="hidden" name="where" id="where" value="${where}" />
					<input type="hidden" name="entita" id="entita" value="${entita}" />
					<input type="hidden" name="ngara" id="ngara" value="${ngara}" />
                    <input type="hidden" name="garaElenco" id="garaElenco" value="${garaElenco}" />
                    <input type="hidden" name="categoriaPrev" id="categoriaPrev" value="${categoriaPrev}" />
                    <input type="hidden" name="classifica" id="classifica" value="${classifica}" />
                    <input type="hidden" name="WIZARD_PAGINA_ATTIVA" id="WIZARD_PAGINA_ATTIVA" value="${WIZARD_PAGINA_ATTIVA}" />
					<input type="hidden" name="isGaraLottiConOffertaUnica" id="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
					<input type="hidden" name="inserimentoDitteIterSemplificato" id="inserimentoDitteIterSemplificato" value="${inserimentoDitteIterSemplificato}" />
					<input type="hidden" name="tipoGara" id="tipoGara" value="${tipoGara}" />
					<input type="hidden" name="aggnumord" id="aggnumord" value="${aggnumord}" />
					<input type="hidden" name="eseguireAggiornamentoNumOrdine" id="eseguireAggiornamentoNumOrdine" value="NO" />
					<input type="hidden" name="eseguireCalcoloImporto" id="eseguireCalcoloImporto" value="NO" />
					<input type="hidden" name="AGGIORNAMENTO_NUMORD" id="AGGIORNAMENTO_NUMORD" value="${AGGIORNAMENTO_NUMORD }" />
					<input type="hidden" name="AGGIORNAMENTO_IMPORTO" id="AGGIORNAMENTO_IMPORTO" value="${AGGIORNAMENTO_IMPORTO }" />
					<input type="hidden" name="selezioneAutomaticaDitte" id="selezioneAutomaticaDitte" value="${selezioneAutomaticaDitte }" />
					<input type="hidden" name="tipoCategoria" id="tipoCategoria" value="${tipoCategoria }" />
					<input type="hidden" name="minOp" id="minOp" value="${minOp }" />
					<input type="hidden" name="limSupDitteSel" id="limSupDitteSel" value="${limSupDitteSel}" />
					<input type="hidden" name="tipoalgo" id="tipoalgo" value="${tipoalgo }" />
					<input type="hidden" name="stazioneAppaltante" id="stazioneAppaltante" value="${stazioneAppaltante }" />
					<input type="hidden" name="numeroSelezionati" id="numeroSelezionati" value="" />
					<input type="hidden" name="ctrlaggiu" id="ctrlaggiu" value="${ctrlaggiu}" />
					
					<input type="hidden" name="criterioRotazioneDesc" id="criterioRotazioneDesc" value="${criterioRotazioneDesc}" />
					<input type="hidden" name="filtriUlteriori" id="filtriUlteriori" value="${filtriUlteriori}" />
					<input type="hidden" name="filtroCategoria" id="filtroCategoria" value="${filtroCategoria}" />
					<input type="hidden" name="filtriZone" id="filtriZone" value="${filtriZone}" />
					<input type="hidden" name="filtriAffidatariEsclusi" id="filtriAffidatariEsclusi" value="${filtriAffidatariEsclusi}" />
					<input type="hidden" name="ctrlimp" id="ctrlimp" value="${ctrlimp}" />
					<input type="hidden" name="ctrlimpga" id="ctrlimpga" value="${ctrlimpga}" />
					<input type="hidden" name="ctrlimpValorePeriodo" id="ctrlimpValorePeriodo" value="${ctrlimpValorePeriodo}" />
					<input type="hidden" name="modalitaSelezioneMista" id="modalitaSelezioneMista" value="${modalitaSelezioneMista}" />
					<input type="hidden" name="elencoIdFiltriSpecificiObbl" id="elencoIdFiltriSpecificiObbl" value="${elencoIdFiltriSpecificiObbl}" />
					
				</gene:formLista></td>
			</tr>
			<tr>
				<c:if test="${selezioneAutomaticaDitte eq 'true'}">
					<c:if test="${ctrlaggiu ne 1 }">
						<c:set var="ditteSelezionabili" value="${datiRiga.rowCount}"/>
					</c:if>
	
					<c:choose>
						<c:when test='${limSupDitteSel ne 0 && ditteSelezionabili gt limSupDitteSel }'>
							<c:set var="nmsel" value="${limSupDitteSel}" />
						</c:when>
						<c:otherwise>
							<c:set var="nmsel" value="${ditteSelezionabili}" />
						</c:otherwise>
					</c:choose>
					
					<c:choose>
						<c:when test="${datiRiga.rowCount == 0  }">
							<b>Numero ditte in elenco selezionabili:</b> 0
						</c:when>
						<c:otherwise>
							<b>Numero ditte in elenco selezionabili:</b> ${nmsel}
						</c:otherwise>
					</c:choose>
						<br><br>Indicare il numero di ditte da selezionare:
						<c:choose>
							<c:when test="${nmsel gt minOp}">
								<input id="numOPDaSel" class="testo" type="text" size="10" value="" maxlength="10" onchange="javascript:validazioneCampo(this);"/>
							</c:when>
							<c:otherwise>
								<input id="numOPDaSel" class="testo" type="text" size="10" value="${nmsel}" maxlength="10" readonly="readonly" />
							</c:otherwise>
						</c:choose>
				</c:if>
			</tr>
			<tr>
				<td class="comandi-dettaglio"  colSpan="2">
					<c:if test='${showFiltroEsclAffUsc eq "1"}'>
						<INPUT type="button"  class="bottone-azione" value='Escludi affidatario uscente' title='Escludi affidatario uscente' onclick="javascript:escludiAffidatariUscenti();">			
						&nbsp;
					</c:if>
					<c:if test='${not empty categoriaPrev and categoriaPrev ne "" and showFiltroUltCat eq "1"}'>
						<INPUT type="button"  class="bottone-azione" value='Filtra su categorie e classi' title='Filtra su categorie e classi' onclick="javascript:impostaFiltroUltCategorie();">
						&nbsp;			
					</c:if>
					<c:if test='${showFiltroZoneAttivita eq "1"}'>
						<INPUT type="button"  class="bottone-azione" value='Filtra su zone attivit&agrave;' title='Filtra su zone attivit&agrave;' onclick="javascript:applicaFiltroZoneAttivita();">			
						&nbsp;
					</c:if>
					<c:if test='${showFiltroSpecifico eq "1"}'>
						<INPUT type="button"  class="bottone-azione" value='Ulteriori filtri' title='Ulteriori filtri' onclick="javascript:applicaUlterioreFiltro();">			
						&nbsp;
					</c:if>
						&nbsp;<br><br>
					<c:if test="${(datiRiga.rowCount > 0 && ctrlaggiu ne 1) || ( ctrlaggiu eq 1 && ditteSelezionabili>0) }">
						<INPUT type="button"  id="Aggiungi" class="bottone-azione" value='Aggiungi ditte selezionate' title='Aggiungi ditte selezionate' onclick="javascript:aggiungi();">&nbsp;&nbsp;&nbsp;
					</c:if>
					<INPUT type="button"  id="Chiudi" class="bottone-azione" value='Chiudi' title='Chiudi' onclick="javascript:chiudi();">&nbsp;
					
				</td>
			</tr>			
		</table>
	</gene:redefineInsert>
	<gene:javaScript>
		
		//setValue("WIZARD_PAGINA_ATTIVA", window.opener.getValue("WIZARD_PAGINA_ATTIVA"));
		function aggiungi(){
			var numeroOggetti = contaCheckSelezionati(document.forms[0].keys);
	  		if (numeroOggetti == 0) {
	      		alert("Selezionare almeno una ditta dalla lista");
	      	} else {
				<c:if test="${selezioneAutomaticaDitte eq 'true'}">
				$('input[type="checkbox"]').prop('disabled', false);
				var numOPDaSel = document.getElementById("numOPDaSel").value;
				if(numOPDaSel==null || numOPDaSel ==""  ){
					alert("Indicare il numero di ditte da selezionare");
					return;
				}else if(numOPDaSel ==0){
					alert("Il numero di ditte da selezionare deve essere maggiore di 0");
					return;
				}else{
					var elementiLista=${nmsel};
					var minOp = "${minOp}";
					if(minOp == null || minOp == ''){
						minOp == 0;
					}
					minOp = parseInt(minOp);
					if(numOPDaSel>elementiLista){
						alert("Il numero di ditte da selezionare deve essere inferiore o uguale al numero di ditte selezionabili");
						return;
					}
					if(numOPDaSel < minOp){
						if(elementiLista > minOp){
							alert('Specificare un numero di operatori non inferiore al minimo');
							return;
						}else{
							if(!confirm("Attenzione: si sta selezionando un numero di ditte inferiore al minimo previsto. Continuare?"))
								return;
						}
					}
					
					document.getElementById("numeroSelezionati").value=numOPDaSel;
				}
				</c:if>
				//Con alcuni DB nelle checkbox "keys" vengono inserite le chiavi nell'ordine
				//V_DITTE_ELECAT.CODICE=T:000001;V_DITTE_ELECAT.GARA=T:E00001
				//con altri invece V_DITTE_ELECAT.GARA=T:E00001;V_DITTE_ELECAT.CODICE=T:000001
				//e ciò causa problemi nel gestore quando si prelevano i dati dalla "keys",
				//per evitare l'errore normalizzo sempre alla forma:
				//V_DITTE_ELECAT.GARA=T:...;V_DITTE_ELECAT.CODICE=T:...
				var numeroOperatori = ${currentRow}+1;
				var ngara;
				var codice;
				//Nel caso di selezione automatiche ditte, tutti i campi check vengono selezionati in automatico,
				//però nel caso di ctrliaggiu=1 devono essere esclusi i check corrispondenti agli operatori con importo
				//aggiudicato sopra il limite. Per tenere traccia di questi sono stati introdotti i campi
				//nascosti campoDisabilitatoImportoSopraLimite
				for(var i=1; i <= numeroOperatori; i++){
					if(numeroOperatori == 1){ 
						ngara = document.forms[0].imprese.value.split(";")[0];
						codice = document.forms[0].imprese.value.split(";")[1];
						if(ngara.indexOf("CODICE")>0){
							document.forms[0].keys.value = codice + ";" + ngara;
							document.forms[0].imprese.value = codice + ";" + ngara;
						}
					} else if(numeroOperatori > 1){
						ngara = document.forms[0].imprese[i - 1].value.split(";")[0];
						codice = document.forms[0].imprese[i - 1].value.split(";")[1];
						if(document.forms[0].campoDisabilitatoImportoSopraLimite[i - 1].value=="true"){
							document.forms[0].keys[i - 1].checked = false;
							if(ngara.indexOf("CODICE")>0){
								document.forms[0].imprese[i - 1].value = codice + ";" + ngara;
							}
						}else{
							if(ngara.indexOf("CODICE")>0){
								document.forms[0].keys[i - 1].value = codice + ";" + ngara;
								document.forms[0].imprese[i - 1].value = codice + ";" + ngara;
							}
						}
					}

				}
	      		listaConferma();
 			}
		}
		
		//Viene ricaricata la pagina chiamante in modo da ripulire le 
		//variabili di sessioni
		function chiudi(){
			window.opener.document.forms[0].pgSort.value = "";
			window.opener.document.forms[0].pgLastSort.value = "";
			window.opener.document.forms[0].pgLastValori.value = "";
			window.opener.bloccaRichiesteServer();
			window.opener.listaVaiAPagina(0);
			window.close();
		}
						
		function impostaFiltroUltCategorie(){
			href = "href=gare/gare/gare-popup-filtroUlterioriCategorie.jsp";
			href += "&categoriaPrev=${categoriaPrev}";
			href += "&ngara=${ngara }";
			href += "&classifica=${classifica }";
			href += "&isGaraLottiConOffertaUnica=${isGaraLottiConOffertaUnica }"
			href += "&tipoGara=${tipoGara }";
			href += "&criterioRotazione=${tipoalgo }";
			href += "&garaElenco=${garaElenco }";
			href += "&stazioneAppaltante=${stazioneAppaltante }";
			openPopUpCustom(href, "impostaFiltroUltCategorie", 900, 550, "yes", "yes");
		}
		
		function applicaUlterioreFiltro(){
			href = "href=gare/gare/gare-popup-filtroSpecificoOp.jsp";
			href += "&ngara=${ngara }";
			href += "&isGaraLottiConOffertaUnica=${isGaraLottiConOffertaUnica }"
			href += "&tipoGara=${tipoGara }";
			href += "&criterioRotazione=${tipoalgo }";
			href += "&garaElenco=${garaElenco }";
			href += "&stazioneAppaltante=${stazioneAppaltante }";
			href += "&elencoIdFiltriSpecificiObbl=${elencoIdFiltriSpecificiObbl}";
			openPopUpCustom(href, "impostaFiltroSpecifico", 900, 550, "yes", "yes");
		}
		
		function applicaFiltroZoneAttivita(){
			href = "href=gare/gare/gare-popup-filtroZoneAttOp.jsp";
			href += "&ngara=${ngara }";
			href += "&isGaraLottiConOffertaUnica=${isGaraLottiConOffertaUnica }"
			href += "&tipoGara=${tipoGara }";
			href += "&criterioRotazione=${tipoalgo }";
			href += "&garaElenco=${garaElenco }";
			href += "&stazioneAppaltante=${stazioneAppaltante }";
			openPopUpCustom(href, "impostaFiltroZoneAtt", 600, 600, "yes", "yes");
		}

		function escludiAffUsc(){
			href = "href=gare/gare/gare-popup-esclAffUscOp.jsp";
			href += "&categoriaPrev=${categoriaPrev}";
			href += "&ngara=${ngara }";
			href += "&classifica=${classifica }";
			href += "&isGaraLottiConOffertaUnica=${isGaraLottiConOffertaUnica }"
			href += "&tipoGara=${tipoGara }";
			href += "&criterioRotazione=${tipoalgo }";
			href += "&garaElenco=${garaElenco}";
			href += "&stazioneAppaltante=${stazioneAppaltante }";
			openPopUpCustom(href, "impostaFiltroSpecifico", 900, 600, "yes", "yes");
		}
		

		function escludiAffidatariUscenti(){
			href = "href=gare/gare/gare-popup-ricerca-affidatari-uscenti.jsp";
			href += "&categoriaPrev=${categoriaPrev}";
			href += "&ngara=${ngara }";
			href += "&classifica=${classifica }";
			href += "&isGaraLottiConOffertaUnica=${isGaraLottiConOffertaUnica }"
			href += "&tipoGara=${tipoGara }";
			href += "&criterioRotazione=${tipoalgo }";
			href += "&garaElenco=${garaElenco}";
			href += "&stazioneAppaltante=${stazioneAppaltante }";
			openPopUpCustom(href, "impostaFiltroSpecifico", 900, 600, "yes", "yes");
		}

		function consultaDocumentiRichiesti(codiceDitta){
			var codiceGara= "$" + "${garaElenco }";
			var ngara= "${garaElenco}";
			chiave = "DITG.CODGAR5=T:" + codiceGara + ";DITG.DITTAO=T:" + codiceDitta + ";DITG.NGARA5=T:" + ngara;
			setContextPath("${pageContext.request.contextPath}");
			verificaDocumentiRichiesti(chiave,"CONSULTAZIONE",0,"false","${autorizzatoModifiche }");
		}
		
		function apriElencoGareFornitore(codiceDitta){
			href = "href=gare/gare/popup-elencoInvitiPregressiFornitore.jsp";
			href += "&codiceDitta="+codiceDitta;
			href += "&codiceElenco=${garaElenco}";
			href += "&tipoalgo=${tipoalgo}";
			href += "&categoriaPrev=${categoriaPrev}";
			href += "&classifica=${classifica}";
			href += "&codiceGara="+"$"+"${garaElenco}";
			href += "&stazioneAppaltante=${stazioneAppaltante}";
			href += "&isGaraLottiConOffertaUnica=${isGaraLottiConOffertaUnica}";
			openPopUpCustom(href, "elencoGareFornitore", 1100, 550, "yes", "yes");
		}
		
		<c:if test="${selezioneAutomaticaDitte eq 'true'}">
			$('input[type="checkbox"]').prop('checked', true);
			$('input[type="checkbox"]').prop('disabled', true);
			$("span.pagelinks").hide();
			$("[id^=tabformLista]").hide();
			$("span.pagebanner").hide();
			/*
			var testo = $("span.pagebanner").text();
			if(!testo.indexOf("Nessun") == 0){
				var testoSplit=testo.split(" ");
				$("span.pagebanner").html("Verranno inserite <b>" + testoSplit[1] + "</b> ditte")
			}
			*/
			$("#Aggiungi").prop('value', 'Conferma');
			$("#Aggiungi").prop('title', 'Conferma');
			$("#Chiudi").prop('value', 'Annulla');
			$("#Chiudi").prop('title', 'Annulla');
		</c:if>
		
		function apriPopupUltInfo(chiaveRiga){
			var numeroGara="${garaElenco}";
			var codiceDitta="${codiceDitta }";
			var categoriaPrev="${categoriaPrev}"; 
			var tipoCategoria="${tipoCategoria}";
			var campoDitta ="${entita}.CODICE";
			var codiceDitta = getValCampoChiave(chiaveRiga,campoDitta);
			var key="ISCRIZCAT.CODGAR=T:$" + numeroGara + ";ISCRIZCAT.CODIMP=T:" + codiceDitta + ";ISCRIZCAT.NGARA=T:" + numeroGara;
			key += ";ISCRIZCAT.CODCAT=T:" + categoriaPrev + ";ISCRIZCAT.TIPCAT=N:" + tipoCategoria;
			
			var href;
			href = "href=gare/iscrizcat/iscrizcat-schedaPopup-ulterioriCampi.jsp";
			href += "&key=" + key;
			href += "&entita=ISCRIZCAT";					
			href += "&modificabile=false";
			href += "&salvato=No";
			href += "&tipo=2";
			href += "&autorizzatoModifiche=2";
			openPopUpCustom(href, "ulterioriCampi", 800, 550, "yes", "yes");
			
		}
		
		function validazioneCampo(campo){
			if(campo.value!=null){
				var valore = campo.value.toString();
				if(!isIntero(valore,false,false)){
					alert('Formato del parametro errato: inserire un numero intero');
					document.getElementById("numOPDaSel").value="";
					document.getElementById("numOPDaSel").focus();
				}
			}
		}
	</gene:javaScript>
</gene:template>
</c:otherwise>
</c:choose>