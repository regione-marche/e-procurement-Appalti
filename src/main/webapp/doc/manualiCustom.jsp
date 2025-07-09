<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#" />
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />

<jsp:include page="/WEB-INF/pages/commons/defCostantiAppalti.jsp" />
<c:set var="urlWsArt80" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", urlWsArt80)}'/>
<c:set var="modatalitaConservazione" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "conservazione.modalita")}'/>
<c:set var="urlMEFORMS" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "integrazioneMEforms.url")}'/>

<c:if test='${fn:contains(listaOpzioniDisponibili, "OP114#")}'>
	<br>
	<tr id='rowNoteRilascioFrontend'>
		<td>
			<b><a class="link-generico" href="javascript:apriManuale('${contextPath}/doc/note_release_portale.pdf');" tabindex="2005"> 
				Note di rilascio front-end</a></b><br>
				<br> 
				Il documento descrive le principali novit&agrave; introdotte nel prodotto di front-end partendo da quelle relative alla
	             versione attuale fino a quelle relative a versioni meno recenti.
	    </td>
	 </tr>        
	
	
</c:if>

<c:set var="titulusConfigurato" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.CheckEsistenzaConfigurazioneWsdmFunction", pageContext, "TITULUS")}' />


<tr id="rowManualeUsoAppalti">
	<td>
			<b><a class="link-generico" href="javascript:apriManuale('${contextPath}/doc/Manuale_usoAppaltiGare.pdf');" tabindex="2020">
							Manuale d'uso Appalti - Gare e procedure di affidamento</a></b><br>
				<br>
					Il documento descrive le modalità di utilizzo dell'applicativo Appalti e le sue varie funzioni.
	</td>
</tr>

<tr id="rowManualeUsoOEPV">
	<td>
		<b><a class="link-generico" href="javascript:apriManuale('${contextPath}/doc/vademecum_utilizzo_OEPV.pdf');" tabindex="2021">
						Manuale d'uso Appalti - Vademecum operativo OEPV</a></b><br>
			<br>
					Il documento contiene le istruzioni operative in merito all'utilizzo del criterio di aggiudicazione dell'offerta economicamente più vantaggiosa.
	</td>
</tr>

<tr id="rowManualeEspletamento">
	<td>
		<b><a class="link-generico" href="javascript:apriManuale('${contextPath}/doc/espletamento_gara.pdf');" tabindex="2022">
						Manuale d'uso Appalti - Espletamento procedura</a></b><br>
			<br>
					Il documento descrive il criterio di visibilità dei dati sul PortaleAppalti durante le fasi di espletamento della procedura di gara
	</td>
</tr>


<tr id="rowManualeUsoOEPVCommissioni">
	<td>
		<b><a class="link-generico" href="javascript:apriManuale('${contextPath}/doc/manuale_commissari_di_gara.pdf');" tabindex="2023">
						Manuale d'uso Appalti - Commissione di gara</a></b><br>
			<br>
					Il documento descrive la modalità di utilizzo dell'applicativo Appalti da parte della commissione di gara.
	</td>
</tr>

<tr id="rowManualeUsoAffidamentiDiretti">
	<td>
		<b><a class="link-generico" href="javascript:apriManuale('${contextPath}/doc/Manuale_usoAppaltiAffidamenti diretti.pdf');" tabindex="2024">
						Manuale d'uso Appalti - Affidamenti diretti</a></b><br>
			<br>
					Il documento descrive la modalità di utilizzo dell'applicativo Appalti per gli affidamenti diretti.
	</td>
</tr>

<tr id="rowNoteCalcoloSogliaAnomalia">
	<td>
			<b><a class="link-generico" href="javascript:apriManuale('${contextPath}/doc/calcoloSogliaAnomalia_note.pdf');" tabindex="2026">
							Manuale d'uso Appalti - Note calcolo soglia d'anomalia</a></b><br>
				<br>
					Il documento descrive le modalità del calcolo soglia d'anomalia nell'applicativo Appalti.
	</td>
</tr>

<tr id="rowFVOE">
	<td>
		<b><a class="link-generico" href="javascript:apriManuale('${contextPath}/doc/Manuale_usoAppaltiVerifiche FVOE.pdf');" tabindex="2027">
			Manuale d'uso Appalti - Verifiche FVOE</a></b><br>
		<br>
		Il documento descrive le modalità di integrazione dell'applicativo Appalti con FVOE per la verifica dei dati e documenti.
	</td>
</tr>

<tr id="rowManualeUsoMDGUE">
	<td>
			<b><a class="link-generico" href="javascript:apriManuale('${contextPath}/doc/Manuale_usoMDgueRequest_SA.pdf');" tabindex="2028">
							Manuale d'uso Appalti - Compilazione DGUE request elettronico italiano "eDGUE-IT"</a></b><br>
				<br>
					Il documento descrive le modalità di compilazione del documento DGUE elettronico italiano.
	</td>
	</td>
</tr>

<c:if test='${not empty urlMEFORMS and urlMEFORMS ne ""}'>
<tr id="rowManualeUsoMEFORMS">
	<td>
		<b><a class="link-generico" href="javascript:apriManuale('${contextPath}/doc/Manuale_usoM-eForms.pdf');" tabindex="2029">
			Manuale d'uso Appalti - Compilazione mediante M-Eforms dei formulari europei</a></b><br>
		<br>
		Il manuale descrive le modalità di compilazione, mediante integrazione con l'applicativo M-Eforms, dei formulari elettronici per la pubblicazione a livello europeo di avvisi e bandi.
	</td>
	</td>
</tr>
</c:if>

<c:if test='${titulusConfigurato eq "SI"}'>
	
	<tr id="rowTitulus">
		<td>
			<b><a class="link-generico" href="javascript:apriManuale('${contextPath}/doc/manuale_integrazioneTitulus.pdf');" tabindex="2030">
							Manuale d'uso Appalti - Integrazione Titulus</a></b><br>
				<br>
					Il documento descrive le modalità di integrazione dell'applicativo Appalti con il sistema esterno di protocollo e gestione documentale Titulus.
	</td>
	</tr>
	
</c:if>

<c:if test='${modatalitaConservazione eq "1"}'>
	
	<tr id="rowCos">
		<td>
			<b><a class="link-generico" href="javascript:apriManuale('${contextPath}/doc/guida_conservazione_COS.pdf');" tabindex="2036">
							Manuale d'uso Appalti - Invio documenti al servizio di Conservazione COS Maggioli</a></b><br>
				<br>
					Il documento descrive la funzionalità dell'applicativo Appalti per l'invio dei documenti delle procedure al servizio di Conservazione COS Maggioli.
	</td>
	</tr>
	
</c:if>

<gene:javaScript>
		<c:if test='${fn:contains(listaOpzioniDisponibili, "OP114#")}'>
			$("#rowNoteRilascioFrontend").insertAfter("#rowNoteRilascio");
		</c:if>
		$("#rowManualeUso").hide();
		$("#rowManualeUsoAppalti").insertAfter("#rowManualeUso");
		$("#rowManualeUsoOEPV").insertAfter("#rowManualeUsoAppalti");
		$("#rowManualeEspletamento").insertAfter("#rowManualeUsoOEPV");
		$("#rowManualeUsoOEPVCommissioni").insertAfter("#rowManualeEspletamento");
		$("#rowManualeUsoAffidamentiDiretti").insertAfter("#rowManualeUsoOEPVCommissioni");
		$("#rowManualeUsoConcorsi").insertAfter("#rowManualeUsoAffidamentiDiretti");
		$("#rowManualeUsoAppalti190").insertAfter("#rowManualeUsoConcorsi");
		$("#rowNoteCalcoloSogliaAnomalia").insertAfter("#rowManualeUsoAppalti190");
		$("#rowManualeUsoMEFORMS").insertAfter("#rowNoteCalcoloSogliaAnomalia");
		$("#rowFVOE").insertAfter("#rowManualeUsoMDGUE");
<%/* Rimosso temporaneamente link manuale concorsi di progettazione per passaggio da vecchia a nuova gestione*/%>
		
			
		<c:if test='${titulusConfigurato eq "SI"}'>
			$("#rowTitulus").insertAfter("#rowFVOE");
		</c:if>
		
		<c:if test='${modatalitaConservazione eq "1"}'>
			$("#rowCos").insertAfter("#rowFVOE");
		</c:if>

<%/* rowManualeUsoMDGUE sempre presente, si può usare come elemento per mettere in coda senza rifare la gestione qui sotto*/%>
		<c:choose>
			<c:when test='${modatalitaConservazione eq "1"}'>
				$("#rowManualeUsoMDGUE").insertAfter("#rowCos");	
			</c:when>
			<c:otherwise>
				$("#rowManualeUsoMDGUE").insertAfter("#rowFVOE");
			</c:otherwise>
		</c:choose>
		<c:choose>
			<c:when test='${not empty urlMEFORMS and urlMEFORMS ne ""}'>
				$("#rowManualeUsoMDGUE").insertAfter("#rowManualeUsoMEFORMS");
			</c:when>
			<c:otherwise>
				$("#rowManualeUsoMDGUE").insertAfter("#rowNoteCalcoloSogliaAnomalia");
			</c:otherwise>
		</c:choose>
	</gene:javaScript>