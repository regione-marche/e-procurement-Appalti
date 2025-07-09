<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="AliceResources" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.jstree.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.jstree.cpvvp.mod.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.jstree.cpvvsupp.mod.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/cpvvp/jquery.cpvvp.mod.css" >

<jsp:include page="/WEB-INF/pages/commons/defCostantiAppalti.jsp" />

<c:set var="codiceGara" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetCodiceGaraFunction", pageContext)}' />
<c:set var="sql" value="select vernpa from torn where codgar=?"/>
<c:set var="verNPA" value='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, sql, codiceGara)}'/>

<c:if test='${modo ne "NUOVO"}'>
	<c:set var="bloccoModificaPCP" value='${gene:callFunctionN("it.eldasoft.sil.pg.tags.funzioni.npa.GetBloccoModificaPCP", pageContext, codiceGara, "true")}'/>
</c:if>

<c:set var="bloccoPubblicazionePortaleBando" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoPubblicazionePortaleFunction", pageContext,codiceGara,"BANDO","false")}' />
<c:set var="bloccoPubblicazionePortaleEsito" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoPubblicazionePortaleFunction", pageContext,codiceGara,"ESITO","false")}' />
<c:set var="applicareBloccoPubblicazioneGareNonTelematiche" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoFunction", pageContext,"A1153","1","true")}' />

<gene:formScheda entita="GARE" gestisciProtezioni="true" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreDatiGenerali" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreGARE">

<%/* Viene riportato tipoAppalto, in modo tale che, in caso di errorie riapertura della pagina, 
     venga riaperta considerando il valore definito inizialmente per la prima apertura della pagina */%>
<input type="hidden" name="tipoAppalto" id="tipoAppalto" value="${param.tipoAppalto}"/>
<input type="hidden" name="LOTTO_OFFERTAUNICA" id="LOTTO_OFFERTAUNICA" value="SI"/>

	<c:set var="msgChiaveErrore">	
		<fmt:message key="match.tags.template.dettaglio.campoChiave.messaggio">
			<fmt:param value="$"/>
		</fmt:message>
	</c:set>

	<c:set var="msgChiaveErrore" value="${fn:replace(msgChiaveErrore, '\\\\', '')}" />	

	<c:choose>
		<c:when test='${(isProceduraTelematica || applicareBloccoPubblicazioneGareNonTelematiche eq "1") && (bloccoPubblicazionePortaleEsito eq "TRUE" || bloccoPubblicazionePortaleBando eq "TRUE") }'>
			<c:set var="modificaLabel" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' />
			<c:set var="bloccoModificaPubblicazione" value='TRUE' />
			<c:set var="bloccoModificatiDati" value='true' />
			<c:set var="campiModificabili" value="true"/>
		</c:when>
		<c:otherwise>
			<c:set var="modificaLabel" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' />
			<c:set var="bloccoModificaPubblicazione" value='FALSE' />
			<c:set var="bloccoModificatiDati" value='false' />
			<c:set var="campiModificabili" value="true"/>
		</c:otherwise>
	</c:choose>

	<c:if test="${bloccoModificaPCP}">
		<c:set var="bloccoModificatiDati" value='true' />
	</c:if>

	<gene:campoScheda visibile="${ bloccoModificatiDati and modo eq 'MODIFICA'}">
		<td colspan="2" style="color:#0000FF">
		<br><b>ATTENZIONE:</b>&nbsp;
		Parte dei dati sono in sola consultazione perché <c:if test="${bloccoModificaPCP}"> sono state inviate delle schede ad ANAC. </c:if>
		<c:if test="${bloccoModificaPubblicazione}">
			<c:choose>
				<c:when test="${bloccoModificaPCP}">La gara è anche </c:when>
				<c:otherwise>la gara è </c:otherwise>
			</c:choose>
			pubblicata su portale Appalti.
		 </c:if>
		    <br>&nbsp;
		</td>
	</gene:campoScheda>

	<gene:campoScheda campo="NGARA" visibile="false"/>
	<gene:campoScheda campo="IMPAPP" visibile="false"/>
	<gene:campoScheda campo="CODGAR1" visibile="false"/>
	<gene:campoScheda campo="ISPNRR" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" visibile="false"/>
	<gene:campoScheda campo="SETTORE" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="false" />
	<gene:campoScheda campo="TIPGEN" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="false" />
	<gene:campoScheda campo="NGARA" entita="GARE1" where="GARE1.NGARA=GARE.NGARA"  visibile="false"/>
	<gene:campoScheda campo="NGARA" entita="V_GARE_IMPORTI" where="V_GARE_IMPORTI.NGARA=GARE.NGARA"  visibile="false"/>
	<gene:campoScheda campo="VALMAX" entita="V_GARE_IMPORTI" where="V_GARE_IMPORTI.NGARA=GARE.NGARA" visibile="false"/>
	<gene:campoScheda campo="VALMAX_FIT" campoFittizio="true" definizione="F14.5;;;MONEY" value="${datiRiga.V_GARE_IMPORTI_VALMAX }" visibile="false"/>

	<gene:gruppoCampi idProtezioni="AVC">
		<gene:campoScheda>
			<td colspan="2"><b>Contributo a Autorità Vigilanza Contratti Pubblici</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="IDIAUT" modificabile="${campiModificabili}"/>
	</gene:gruppoCampi>
	
	<gene:gruppoCampi idProtezioni="CAU">
		<gene:campoScheda>
			<td colspan="2"><b>Cauzione provvisoria</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="PGAROF" defaultValue="${requestScope.initPGAROF}" modificabile="${campiModificabili}"/>
		<gene:campoScheda campo="GAROFF" modificabile="${campiModificabili}"/>
		<gene:campoScheda campo="MODCAU" modificabile="${campiModificabili}"/>
	</gene:gruppoCampi>

	<jsp:include page="/WEB-INF/pages/gare/garcpv/codiciCPV-gara.jsp">
		<jsp:param name="datiModificabili" value="${not bloccoModificaPCP}"/>
		<jsp:param name="lottoOffertaUnica" value="true"/>
		<jsp:param name="initCODCPV" value="${initCODCPV }"/>
		<jsp:param name="bloccoModificaPCP" value="${bloccoModificaPCP}"/>
	</jsp:include>

	<gene:fnJavaScriptScheda funzione="settaCauzione('#VALMAX_FIT#', '#GARE_PGAROF#')" elencocampi="VALMAX_FIT;GARE_PGAROF" esegui="false"/>
	<gene:fnJavaScriptScheda funzione="calcoloImportiAnac('#VALMAX_FIT#')" elencocampi="VALMAX_FIT;GARE_IMPAPP" esegui="false"/>

	<gene:gruppoCampi idProtezioni="CUP">
		<gene:campoScheda>
			<td colspan="2"><b>Codice CUP</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="CUPPRG" defaultValue ="${initCUPPRG}" />
		<gene:campoScheda campo="CUPMST"  modificabile="${campiModificabili}"/>
		
		<c:if test='${modoAperturaScheda ne "NUOVO" && gene:checkProt(pageContext, "COLS.VIS.GARE.GARECUP.CUP")}' >
			<c:set var="ngaraCup" value='${gene:getValCampo(param.key, "NGARA")}'/>
			<c:set var="tmp" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GestioneMultiplaCupFunction", pageContext, ngaraCup)}'/>
			<jsp:include page="/WEB-INF/pages/commons/interno-sezione-multipla-singola-riga.jsp" >
				<jsp:param name="entita" value='GARECUP'/>
				<jsp:param name="chiave" value='${ngaraCup}'/>
				<jsp:param name="nomeAttributoLista" value='listaNCup' />
				<jsp:param name="idProtezioni" value="GARECUP" />
				<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/garecup/garecup-scheda.jsp"/>
				<jsp:param name="arrayCampi" value="'GARECUP_ID_', 'GARECUP_NGARA_', 'GARECUP_CUP_'"/>
				<jsp:param name="sezioneListaVuota" value="false" />
				<jsp:param name="titoloSezione" value="Altro codice CUP" />
				<jsp:param name="titoloNuovaSezione" value="Nuovo codice CUP" />
				<jsp:param name="descEntitaVociLink" value="ulteriore codice CUP" />
				<jsp:param name="msgRaggiuntoMax" value="i codici CUP"/>
				<jsp:param name="usaContatoreLista" value="true" />
			</jsp:include>
		</c:if>
	</gene:gruppoCampi>
	
	<gene:gruppoCampi idProtezioni="CUI">
		<gene:campoScheda>
			<td colspan="2"><b>Programmazione</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="CODCUI" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" defaultValue ="${initCODCUI}"/>
		<gene:campoScheda campo="ANNINT" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" />
	</gene:gruppoCampi>

	<c:set var="ngaraDFina" value='${gene:getValCampo(param.key, "NGARA")}'/>
	<c:set var="tmp" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GestioneMultiplaDFinaFunction", pageContext, ngaraDFina)}'/>
	<c:if test="${not empty verNPA}">
		<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
			<jsp:param name="entita" value='G1DFINA'/>
			<jsp:param name="chiave" value='${ngaraDFina}'/>
			<jsp:param name="nomeAttributoLista" value='listaDFina' />
			<jsp:param name="idProtezioni" value="G1DFINA" />
			<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/g1dfina/g1dfina-scheda.jsp"/>
			<jsp:param name="arrayCampi" value="'G1DFINA_ID_', 'G1DFINA_NGARA_', 'G1DFINA_TIFINA_', 'G1DFINA_IMPOFI_'"/>
			<jsp:param name="titoloSezione" value="Finanziamento" />
			<jsp:param name="titoloNuovaSezione" value="Nuovo finanziamento" />
			<jsp:param name="descEntitaVociLink" value="finanziamento" />
			<jsp:param name="msgRaggiuntoMax" value="i finanziamenti"/>
			<jsp:param name="usaContatoreLista" value="true" />
		</jsp:include>
	</c:if>

	<gene:gruppoCampi idProtezioni="ULTDATI" visibile="${not empty verNPA}">
		<gene:campoScheda>
			<td colspan="2"><b>Ulteriori dati</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="CATMERC" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" />
		<gene:campoScheda campo="DISPOPART" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" />
		<gene:campoScheda campo="SERVILOC" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" />
		<gene:campoScheda campo="ISSTRUMEL" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" />
		<gene:campoScheda campo="CODCNEL" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" title='${gene:callFunction3("it.eldasoft.gene.tags.functions.GetTitleWithExpandCollapseSkipProtFunction",pageContext,"GARE1.CODCNEL","CODCNEL_DESC")}'/>
		<gene:campoScheda id="CODCNEL_DESC">
			<td class="etichetta-dato"></td>
			<td class="valore-dato"><div style="background-color: #EFEFEF; border: 1px solid;padding:5px">Inserire il codice CNEL di 4 caratteri corrispondente al contratto di settore utilizzato come reperibile in <a href="https://www.cnel.it/Archivio-Contratti">https://www.cnel.it/Archivio-Contratti</a>, punto a), scaricando il file excel di competenza (colonna CCNL id), oppure calcolandolo come indicato nel punto b) dal sito web al link <a href="https://www.cnel.it/Archivio-Contratti/Contrattazione-Nazionale/Ricerca-CCNL" >https://www.cnel.it/Archivio-Contratti/Contrattazione-Nazionale/Ricerca-CCNL</a> (primi 4 caratteri del campo "Categoria" dopo aver selezionato il campo "Settore").
				Nel caso in cui il codice non risulti applicabile, indicare "n.a." nel valore del campo.</div>
			</td>
		</gene:campoScheda>
		<gene:campoScheda campo="ACCESSI" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" />
		<gene:campoScheda campo="AFFIRIS" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" />
		<gene:campoScheda campo="ESECRIS" entita="GARE1" />
		<gene:campoScheda campo="MOTCOLL" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" />
		<gene:campoScheda campo="CIGCOLL" obbligatorio="true" entita="GARE1" where="GARE1.NGARA=GARE.NGARA"
			visibile="${(not empty datiRiga.GARE1_MOTCOLL and datiRiga.GARE1_MOTCOLL ne '10' and modo eq 'VISUALIZZA') or modo ne 'VISUALIZZA'}"/>
	</gene:gruppoCampi>

	<gene:gruppoCampi idProtezioni="PARIOPP" visibile="${datiRiga.TORN_ISPNRR eq '1'}">
		<gene:campoScheda>
			<td colspan="2"><b>Tutela pari opportunità generazionali e di genere</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="DEROGAAQ" entita="GARE1" where="GARE1.NGARA=GARE.NGARA"
			visibile="${datiRiga.TORN_ISADESIONE eq '1'}" />
		<gene:campoScheda campo="PREVQUOTA" entita="GARE1" where="GARE1.NGARA=GARE.NGARA"
			visibile="${ (not(modo eq 'VISUALIZZA' and datiRiga.GARE1_DEROGAAQ eq '1')) or datiRiga.TORN_ISADESIONE ne '1'}" />
		<gene:campoScheda campo="QUOFEMMI" entita="GARE1" where="GARE1.NGARA=GARE.NGARA"
			visibile="${(not(modo eq 'VISUALIZZA' and datiRiga.GARE1_DEROGAAQ eq '1')) or datiRiga.TORN_ISADESIONE ne '1'}" />
		<gene:campoScheda campo="QUOGIOVA" entita="GARE1" where="GARE1.NGARA=GARE.NGARA"
			visibile="${(not(modo eq 'VISUALIZZA' and datiRiga.GARE1_DEROGAAQ eq '1')) or datiRiga.TORN_ISADESIONE ne '1'}" />

		<c:set var="ngaraDer" value='${gene:getValCampo(param.key, "NGARA")}'/>
		<c:set var="tmp" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GestioneMultiplaQuotaDerFunction", pageContext, ngaraDer)}'/>
		<c:if test="${not empty listaNDer or modo ne 'VISUALIZZA'}">
			<gene:campoScheda>
				<td colspan="2" id="titoloMotiviDeroga"><b>Motivi deroga</b></td>
			</gene:campoScheda>
		</c:if>
		<jsp:include page="/WEB-INF/pages/commons/interno-sezione-multipla-singola-riga.jsp" >
			<jsp:param name="entita" value='G1QUOTADER'/>
			<jsp:param name="chiave" value='${ngaraDer}'/>
			<jsp:param name="nomeAttributoLista" value='listaNDer' />
			<jsp:param name="idProtezioni" value="G1QUOTADER" />
			<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/g1quotader/g1quotader-scheda.jsp"/>
			<jsp:param name="arrayCampi" value="'G1QUOTADER_ID_', 'G1QUOTADER_NGARA_', 'G1QUOTADER_MOTDEROGA_'"/>
			<jsp:param name="sezioneListaVuota" value="false" />
			<jsp:param name="titoloSezione" value="Motivo deroga quota occupazione femminile/giovanile" />
			<jsp:param name="titoloNuovaSezione" value="Nuovo motivo deroga" />
			<jsp:param name="descEntitaVociLink" value="motivo deroga" />
			<jsp:param name="msgRaggiuntoMax" value="i motivi deroga"/>
			<jsp:param name="usaContatoreLista" value="true" />
		</jsp:include>

		<c:set var="ngaraMisprem" value='${gene:getValCampo(param.key, "NGARA")}'/>
		<c:set var="tmp" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GestioneMultiplaMisPremialiFunction", pageContext, ngaraMisprem)}'/>
		<gene:campoScheda>
			<td colspan="2" id="titoloMisPremiali"><b>Misure premiali</b></td>
		</gene:campoScheda>
		<jsp:include page="/WEB-INF/pages/commons/interno-sezione-multipla-singola-riga.jsp" >
			<jsp:param name="entita" value='G1MISPREMIALI'/>
			<jsp:param name="chiave" value='${ngaraMisprem}'/>
			<jsp:param name="nomeAttributoLista" value='listaNMisprem' />
			<jsp:param name="idProtezioni" value="G1MISPREMIALI" />
			<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/g1mispremiali/g1mispremiali-scheda.jsp"/>
			<jsp:param name="arrayCampi" value="'G1MISPREMIALI_ID_', 'G1MISPREMIALI_NGARA_', 'G1MISPREMIALI_MISURA_'"/>
			<jsp:param name="sezioneListaVuota" value="true" />
			<jsp:param name="titoloSezione" value="Misure premiali" />
			<jsp:param name="titoloNuovaSezione" value="Nuova misura" />
			<jsp:param name="descEntitaVociLink" value="misura premiale" />
			<jsp:param name="msgRaggiuntoMax" value="e misure premiali"/>
			<jsp:param name="usaContatoreLista" value="true" />
		</jsp:include>
	</gene:gruppoCampi>

	<jsp:include page="/WEB-INF/pages/gene/attributi/sezione-attributi-generici.jsp">
		<jsp:param name="entitaParent" value="GARE"/>
	</jsp:include>

	<gene:redefineInsert name="schedaNuovo" />
	<gene:campoScheda>
		<td class="comandi-dettaglio" colSpan="2">
			<gene:insert name="addPulsanti"/>
			<c:choose>
			<c:when test='${modo eq "MODIFICA" or modo eq "NUOVO"}'>
				<gene:insert name="pulsanteSalva">
					<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:schedaConferma()">
				</gene:insert>
				<gene:insert name="pulsanteAnnulla">
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:schedaAnnulla()">
				</gene:insert>
			</c:when>
			<c:otherwise>
				<gene:insert name="pulsanteModifica">
					<c:if test='${gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD")}'>
						<INPUT type="button"  class="bottone-azione" value='Modifica' title='Modifica' onclick="javascript:schedaModifica()">
					</c:if>
				</gene:insert>
			</c:otherwise>
			</c:choose>
			&nbsp;
		</td>
	</gene:campoScheda>

	<gene:redefineInsert name="addToAzioni" >
		<c:if test='${ modo eq "VISUALIZZA" and bloccoModificatiDati and gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD") and autorizzatoModifiche ne "2"}' >
			<tr>
				<td class="vocemenulaterale" >
						<a href="javascript:schedaModifica();" title="Modifica">
					Modifica</a>
				</td>
			</tr>
		</c:if>
	</gene:redefineInsert>	

	 <c:if test='${bloccoModificatiDati}'>
	 	<gene:redefineInsert name="schedaModifica"></gene:redefineInsert>
	 </c:if>

	<input type="hidden" name="bloccoModificatiDati" id="bloccoModificatiDati" value="${bloccoModificatiDati}"/> 	 
	<input type="hidden" name="bloccoModificaPubblicazione" value="${bloccoModificaPubblicazione}" />
	<input type="hidden" name="bloccoModificaPCP" value="${bloccoModificaPCP}" />

</gene:formScheda>

<gene:javaScript>

	<c:if test='${modo eq "MODIFICA"}'>
		var bloccoModificatiDati = $("#bloccoModificatiDati").val();
		var bloccoModificaPCP = "${bloccoModificaPCP}";
		bloccaDopoPubblicazione(bloccoModificatiDati,"${offtel}","",bloccoModificaPCP,"${impoOpzRinnModificabili}","${bloccoModificaPubblicazione}");
	</c:if>

	
	<c:if test='${modo eq "MODIFICA" or modo eq "NUOVO"}'>
		var arrayScaglioni = new Array();
		<c:forEach items="${listaScaglioniA1z01}" var="scaglione" varStatus="indice" >
			arrayScaglioni[${indice.index}] = new Array("${scaglione.tipoTabellato}", "${scaglione.descTabellato}");
		</c:forEach>
		function calcoloImportiAnac(importo){
			settaContributo(importo,arrayScaglioni,"GARE_IDIAUT");
		}
	</c:if>

	//Funzione per aggiornare il campo GAROFF
	function settaCauzione(importoAppalto, percentualeCauzione) {
		var decimali = "${requestScope.numeroDecimali}";
				
		var impAppalto = importoAppalto;
		if (impAppalto == null || impAppalto == ""){
			setValue("GARE_GAROFF", "");
			return;
		}
		impAppalto = parseFloat(impAppalto);
		
		var percentuale = percentualeCauzione;
		if (percentuale == null || percentuale == ""){
			setValue("GARE_GAROFF", "");
			return;
		}
		percentuale = parseFloat(percentuale);
		 
		var importoCauzioneProvvisoria;
		importoCauzioneProvvisoria = (impAppalto * percentuale / 100);
		setValue("GARE_GAROFF",  round(eval(importoCauzioneProvvisoria), parseInt(decimali)));
		 
		 
	}


	function gestisciCIGCOLL() {
		if (${modo ne 'VISUALIZZA'}){
			if ($('#GARE1_MOTCOLL').val() != '' && $('#GARE1_MOTCOLL').val() != 10) {
				$('#rowGARE1_CIGCOLL').show();
			} else {
				$('#GARE1_CIGCOLL').val('');
				$('#rowGARE1_CIGCOLL').hide();
			}
		}
	}

	function delElementoSchedaMultiplaNoConfirm(id, label, tipo, campi){
		hideElementoSchedaMultipla(id, tipo, campi, false);
		setValue(label + id, "1");
	}

	function gestisciDEROGAAQ() {
		if (${modo ne 'VISUALIZZA'}){
			if ($('#GARE1_DEROGAAQ').val() != 1 || '${datiRiga.TORN_ISADESIONE}' != '1') {
				$('#rowGARE1_PREVQUOTA').show(); 
				$('#titoloMisPremiali').show(); 
				$('#rowLinkAddG1MISPREMIALI').show();
			} else {
				for (let i = 1; i < $('[id^=rowG1MISPREMIALI_MISURA_]').length + 1; i++) { 
					if ($('#rowG1MISPREMIALI_MISURA_' + i).is(":visible")){
						delElementoSchedaMultiplaNoConfirm(i, 'G1MISPREMIALI_DEL_G1MISPREMIALI_', 'G1MISPREMIALI', new Array('G1MISPREMIALI_ID_', 'G1MISPREMIALI_NGARA_', 'G1MISPREMIALI_MISURA_'));
					}
				}
				$('#GARE1_PREVQUOTA').val('');
				gestisciPREVQUOTA();
				$('#rowGARE1_PREVQUOTA').hide(); 
				$('#titoloMisPremiali').hide(); 
				$('#rowLinkAddG1MISPREMIALI').hide();
			}
		}
	}

	function gestisciPREVQUOTA() {
		if ($('#GARE1_PREVQUOTA').val() == 3 || $('#GARE1_PREVQUOTA').val() == 1) {
			$('#titoloMotiviDeroga').show(); 
			$('#rowLinkAddG1QUOTADER').show();
		} else {
			for (let i = 1; i < $('[id^=rowG1QUOTADER_MOTDEROGA_]').length + 1; i++) {
				if ($('#rowG1QUOTADER_MOTDEROGA_' + i).is(":visible")){
					delElementoSchedaMultiplaNoConfirm(i, 'G1QUOTADER_DEL_G1QUOTADER_', 'G1QUOTADER', new Array('G1QUOTADER_ID_', 'G1QUOTADER_NGARA_', 'G1QUOTADER_MOTDEROGA_'));
				}
			}
			$('#titoloMotiviDeroga').hide(); 
			$('#rowLinkAddG1QUOTADER').hide();
		}

		if ($('#GARE1_PREVQUOTA').val() == 3) {
			$('#rowGARE1_QUOFEMMI').show();
			$('#rowGARE1_QUOGIOVA').show();
		} else {
			$('#GARE1_QUOFEMMI').val('');
			$('#GARE1_QUOGIOVA').val('');
			$('#rowGARE1_QUOFEMMI').hide();
			$('#rowGARE1_QUOGIOVA').hide();
		}
	}

	$(document).ready(function() {
		gestisciCIGCOLL();
		gestisciDEROGAAQ();
		gestisciPREVQUOTA();
		$('#GARE1_MOTCOLL').change(function(){
			gestisciCIGCOLL();
		});
		$('#GARE1_DEROGAAQ').change(function(){
			gestisciDEROGAAQ();
		});
		$('#GARE1_PREVQUOTA').change(function(){
			gestisciPREVQUOTA();
		});

		$('#GARE1_CIGCOLL').change(function() {
			if (!controllaCIG("GARE1_CIGCOLL")) {
				$('#GARE1_CIGCOLL').val('');
				alert("Codice CIG non valido");
				this.focus();
			}
	    });
	    if (${bloccoModificaPCP eq "true" and modo eq "MODIFICA"}) {
	    	$('#rowLinkAddG1MISPREMIALI').hide();
			$('#rowLinkAddG1QUOTADER').hide();
			$("[id^='rowtitoloG1MISPREMIALI_']").each( function() {
				$(this).hide();
			});
			$("[id^='rowtitoloG1QUOTADER_']").each( function() {
				$(this).hide();
			});

			var nascondiTitoloMISPREMIALI = true;
			$("[id^='rowG1MISPREMIALI_MISURA_']").each( function() {
				if ($(this).is(":visible")){
					nascondiTitoloMISPREMIALI = false;
				}
			});
			if (nascondiTitoloMISPREMIALI) {
				$('#titoloMisPremiali').hide();
			}

			var nascondiTitoloQUOTADER = true;
			$("[id^='rowG1QUOTADER_MOTDEROGA_']").each( function() {
				if ($(this).is(":visible")){
					nascondiTitoloQUOTADER = false;
				}
			});
			if (nascondiTitoloQUOTADER) {
				$('#titoloMotiviDeroga').hide();
			}
	    }
		if (${datiRiga.TORN_ISPNRR ne '1'}) {
			for (let i = 1; i < $('[id^=rowG1MISPREMIALI_MISURA_]').length + 1; i++) {
				$('#rowG1MISPREMIALI_MISURA_' + i).hide();
			}
		}
	});
	
	$("#GARE_CUPPRG").on('change', function() {
		 	validazioneCup(this);
	});
	
	   function validazioneCup(campo){
			var valore = campo.value;
			var reg = new RegExp("^[A-Za-z0-9]{15}$", "g");
			if(valore!=null && valore.length>0 && !reg.test(valore)){
				alert('Il valore del codice CUP specificato non \u00e8 valido: deve avere una lunghezza di 15 caratteri e deve contenere solo caratteri alfanumerici (lettere e numeri)');
				campo.value=campo.defaultValue;
				this.focus();
			}else{
				campo.defaultValue=valore;
			}
	 	}
	 	
	$("#GARE1_CODCUI").on('change', function() {
		 	validazioneCUI(this);
	});
	
	function validazioneCUI(campo){
			var valore = campo.value;
			var reg = new RegExp("^[A-Za-z0-9]{20,22}$", "g");
			if(valore!=null && valore.length>0 && !reg.test(valore)){
				alert('Il valore del codice CUI specificato non \u00e8 valido: deve avere una lunghezza di compresa tra 20 e 22 caratteri e deve contenere solo caratteri alfanumerici (lettere e numeri)');
				campo.value=campo.defaultValue;
				this.focus();
			}else{
				campo.defaultValue=valore;
			}
	 	}

</gene:javaScript>
