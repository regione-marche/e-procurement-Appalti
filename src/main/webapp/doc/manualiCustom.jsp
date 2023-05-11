<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#" />
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />

<jsp:include page="/WEB-INF/pages/commons/defCostantiAppalti.jsp" />
<c:set var="urlWsArt80" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", urlWsArt80)}'/>
<c:set var="urlCOS" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "cos.sftp.url")}'/>

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
			<b><a class="link-generico" href="javascript:apriManuale('${contextPath}/doc/manuale_usoAppaltiGare.pdf');" tabindex="2020">
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

<tr id="rowManualeUsoConcorsi">
	<td>
		<b><a class="link-generico" href="javascript:apriManuale('${contextPath}/doc/concorsi-progettazione-idee.pdf');" tabindex="2024">
						Manuale d'uso Appalti - Concorsi di progettazione e di idee</a></b><br>
			<br>
					Il documento descrive la modalità di utilizzo dell'applicativo Appalti per l'espletamento dei concorsi di progettazione e di idee.
	</td>
</tr>

<tr id="rowManualeUsoAppalti190">
	<td>
		<b><a class="link-generico" href="javascript:apriManuale('${contextPath}/doc/manuale_usoAppalti190.pdf');" tabindex="2025">
						Manuale d'uso Appalti - Adempimenti L.190/2012</a></b><br>
			<br>
					Il documento descrive il modulo dell'applicativo Appalti per la compilazione e pubblicazione dei dati ai sensi dell'art. 1 comma 32 Legge n. 190/2012 "Legge anticorruzione".
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

<c:if test='${not empty urlWsArt80 and urlWsArt80 ne ""}'>
	<tr id="rowManualeUsoAppaltiart80">
		<td>
			<b><a class="link-generico" href="javascript:apriManuale('${contextPath}/doc/manuale_verificaDitteArt80.pdf');" tabindex="2035">
							Manuale d'uso Appalti - Verifica requisiti operatori art.80 DLgs.50/2016</a></b><br>
				<br>
					Il documento descrive le funzionalità dell'applicativo Appalti per la verifica requisiti generali degi operatori di cui all'art. 80 del DLgs 50/2016
		</td>
	</tr>
</c:if>

<c:if test='${not empty urlCOS and urlCOS ne ""}'>
	
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
		$("#rowManualeUsoConcorsi").insertAfter("#rowManualeUsoOEPVCommissioni");
		$("#rowManualeUsoAppalti190").insertAfter("#rowManualeUsoConcorsi");
		$("#rowNoteCalcoloSogliaAnomalia").insertAfter("#rowManualeUsoAppalti190");
		
			
		<c:if test='${titulusConfigurato eq "SI"}'>
			$("#rowTitulus").insertAfter("#rowNoteCalcoloSogliaAnomalia");
		</c:if>
		
		<c:if test='${not empty urlWsArt80 and urlWsArt80 ne ""}'>
			<c:choose>
				<c:when test='${titulusConfigurato eq "SI"}'>
					$("#rowManualeUsoAppaltiart80").insertAfter("#rowTitulus");		
				</c:when>
				<c:otherwise>
					$("#rowManualeUsoAppaltiart80").insertAfter("#rowNoteCalcoloSogliaAnomalia");
				</c:otherwise>
			</c:choose>
			
		</c:if>
		
		<c:if test='${not empty urlCOS and urlCOS ne ""}'>
			<c:choose>
				<c:when test='${not empty urlWsArt80 and urlWsArt80 ne ""}'>
					$("#rowCos").insertAfter("#rowManualeUsoAppaltiart80");		
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test='${titulusConfigurato eq "SI"}'>
							$("#rowCos").insertAfter("#rowTitulus");		
						</c:when>
						<c:otherwise>
							$("#rowCos").insertAfter("#rowNoteCalcoloSogliaAnomalia");
						</c:otherwise>
					</c:choose>	
				</c:otherwise>
			</c:choose>
		</c:if>
		
	</gene:javaScript>