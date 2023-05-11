<%/*
       * Created on 18-ott-2007
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

<c:if test='${modo eq "VISUALIZZA" or modo eq "MODIFICA" or empty modo}' >
	<c:set var="faseGara" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetFaseGaraFunction", pageContext, key)}' scope="request"/>
</c:if>

<c:set var="codiceGara" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetCodiceGaraFunction", pageContext)}' />
<c:set var="modlicg" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetMODLICGFunction", pageContext, "")}' />
<c:set var="iterga" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetITERGAFunction", pageContext, codiceGara)}' />
<c:set var="isVecchiaOepv" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.isVecchiaOEPVFunction", pageContext, codiceGara)}' />
<c:set var="contieneFormato52" value="${gene:callFunction2('it.eldasoft.sil.pg.tags.funzioni.CheckContieneFormatoFunction', pageContext, '52')}" />
<c:set var="itergaMacro" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetITERGAMacroFunction", pageContext, key)}' scope="request"/>
<c:set var="isProceduraTelematica" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsProceduraTelematicaGaraFunction", pageContext, gene:getValCampo(key,"NGARA"))}' scope="request"/>
<c:set var="uffintGara" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetUffintGaraFunction",  pageContext,codiceGara)}' scope="request"/>
<c:set var="idconfi" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.GetWSDMConfiAttivaFunction",  pageContext,uffintGara,sessionScope.moduloAttivo)}' scope="request"/>

<c:set var="ngara" value='${gene:getValCampo(key, "GARE.NGARA")}' />
<c:set var="ditta" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetDittaFunction", pageContext, ngara)}' />
<c:set var="esitoControlloAdesione" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsAdesioneAccordoQuadroConLavorazioniFunction", pageContext, ngara)}' scope="request"/>
<c:set var="isAccordoQuadro" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsAccordoQuadroFunction", pageContext, codiceGara)}'/>
<c:if test="${isAccordoQuadro eq '1' }">
	<c:set var="esecscig" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetEsecscigFunction", pageContext, ngara,"1")}'/>
	<c:if test="${empty esecscig or esecscig eq '' or esecscig ne '1'}">
		<c:set var="visualizzaControlloSpesa" value="true"/>
	</c:if>
</c:if>
<c:set var="faseRicezione" value="false" />
<c:set var="faseGara-Apertura_doc_amm" value="false" />
<c:set var="faseGaraOff" value="false" />

<c:choose>
	<c:when test="${itergaMacro eq 1}">
		<c:set var="titleFasiRicezione" value="Ricezione offerte" />
	</c:when>
	<c:when test="${itergaMacro eq 3}">
		<c:set var="titleFasiRicezione" value="Inviti e ricezione offerte" />
	</c:when>
	<c:when test="${itergaMacro eq 7}">
		<c:set var="titleFasiRicezione" value="Ricezione domande di partecipazione" />
	</c:when>
	<c:otherwise>
		<c:set var="titleFasiRicezione" value="Ricezione domande e offerte" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${iterga eq 8}">
		<c:set var="isRicercaMercatoNegoziata" value="true" />
	</c:when>
	<c:otherwise>
		<c:set var="isRicercaMercatoNegoziata" value="false" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${itergaMacro eq 3 && isRicercaMercatoNegoziata eq 'true'}">
		<c:set var="titleFasiGaraTab3" value="3. Apertura offerte e valutazione" />
	</c:when>
	<c:otherwise>
		<c:set var="titleFasiGaraTab3" value="3. Apertura offerte e calcolo aggiud." />
	</c:otherwise>
</c:choose>

<c:if test="${! gene:checkProt(pageContext,'PAGE.VIS.GARE.GARE-scheda.DITTECONCORRENTI')}">
	<c:set var="prefTitleAggiudicazione" value="4. " />
</c:if>

<c:set var="tipoFornitura" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipoFornitureFunction", pageContext,codiceGara)}' />
<c:set var="titlelf" value="Lista lavorazioni e forniture" />
 <c:if test='${tipoFornitura == 98}'>
	<c:set var="titlelf" value="Lista prodotti" />
 </c:if>

<c:if test='${lottoOffertaUnica}'>
	<c:set var="bustalotti" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetBustalottiFunction", pageContext, key)}' />
</c:if>

<c:if test='${not lottoOffertaUnica and not garaPerElenco and not garaPerCatalogo}'>
	<c:set var="paginaContratto" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.ControlliVisualizzazionePaginaContrattoFunction", pageContext, ngara, "numeroGara")}' />
</c:if>

<c:set var="integrazioneWSERP" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSERPFunction", pageContext)}' scope="request" />

<c:if test="${isProceduraTelematica eq '1' }">
	<c:set var="offtel" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetOFFTELFunction", pageContext, codiceGara)}' />
</c:if>

<c:set var="costofisso" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetCOSTOFISSOFunction",  pageContext,ngara)}' />
<c:set var="isAffidamentoDerivato" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsAffidamentoDerivatoFunction",  pageContext,ngara)}' />
<c:set var="visListaLavForn" value="${((((modlicg eq '6' and (isVecchiaOepv or contieneFormato52)) or modlicg eq '5' or modlicg eq '14' or modlicg eq '16' or (isAffidamentoDerivato eq 'true') or (esitoControlloAdesione eq 'true' and modlicg eq '0')) and not lottoOffertaUnica and costofisso ne '1') or (lottoOffertaUnica and bustalotti eq '1' and costofisso ne '1' and ((modlicg eq '6' and (isVecchiaOepv or contieneFormato52)) or modlicg eq '5' or modlicg eq '14'))) and offtel ne '3'}" scope="request" />

<c:if test="${not lottoOffertaUnica and not garaPerElenco and not garaPerCatalogo }">
	<c:set var="whereNobustamm" value="codgar='${codiceGara}'"/>
	<c:set var="nobustamm" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreCampoDBFunction", pageContext, "nobustamm","torn", whereNobustamm)}' />
</c:if>
 

 
<gene:formPagine gestisciProtezioni="true">

<c:choose>
	<c:when test='${empty param.garaPerElenco and empty garaPerElenco and empty param.garaPerCatalogo and empty garaPerCatalogo}'>
		<gene:pagina title="Dati generali" idProtezioni="DATIGEN" visibile='${not lottoOffertaUnica}'>
			<jsp:include page="gare-pg-datigen.jsp" />
		</gene:pagina>
		
		<gene:pagina title="Dati generali" idProtezioni="DATIGENOFFUNICA" visibile='${lottoOffertaUnica}'>
			<jsp:include page="gare-pg-datigen-offertaUnica.jsp" />
		</gene:pagina>
	</c:when>
	<c:when test='${!empty param.garaPerElenco || !empty garaPerElenco}'>
		<gene:pagina title="Dati generali" idProtezioni="DATIGENELENCODITTE" >
			<jsp:include page="gare-pg-datigen-elencoditte.jsp" />
		</gene:pagina>
	</c:when>
	<c:when test='${!empty param.garaPerCatalogo || !empty garaPerCatalogo}'>
		<gene:pagina title="Dati generali" idProtezioni="DATIGENCATELETT" >
			<jsp:include page="gare-pg-datigen-catalotoElettronico.jsp" />
		</gene:pagina>
	</c:when>
</c:choose>

	<gene:pagina title="Dati generali" idProtezioni="DATIGENPROT">
		<jsp:include page="gare-pg-datigen-protocollo.jsp" />
	</gene:pagina>
	
	<gene:pagina title="Altri dati" idProtezioni="ALTRIDATI" visibile='${not lottoOffertaUnica and not garaPerElenco and not garaPerCatalogo}'> 
		<jsp:include page="gare-pg-altridati.jsp" />
	</gene:pagina>

	<gene:pagina title="Categorie" idProtezioni="CATEGORIEGARA" visibile="${garaPerElenco || garaPerCatalogo}">
		<jsp:include page="gare-pg-categorie-albero.jsp" />
	</gene:pagina>

	<gene:pagina title="Articoli" idProtezioni="ARTICOLIGARA" visibile="${garaPerCatalogo}">
		<jsp:include page="gare-pg-lista-articoli.jsp" />
	</gene:pagina>
		
	<gene:pagina title="Altri dati" idProtezioni="CRITERIELEDITTE" >
		<jsp:include page="gare-pg-criteri-rotazione.jsp" />
	</gene:pagina>
	
	<gene:pagina title="Criteri di valutazione" idProtezioni="CRITERI" visibile="${modlicg eq '6' and not garaPerElenco and not garaPerCatalogo}">
		<jsp:include page="gare-pg-crit-valutazione.jsp" />
	</gene:pagina>
	<gene:pagina title='${titlelf}' idProtezioni="LISTALAVFORN" visibile="${visListaLavForn}">
		<jsp:include page="gare-pg-listaLavoriForniture.jsp" />
	</gene:pagina>

<c:choose>
	<c:when test='${not garaPerElenco and not garaPerCatalogo and not lottoOffertaUnica}'>
		<gene:pagina title="Documenti e atti" idProtezioni="DOCUMGARA" visibile="${genereGara ne '1'}">
			<jsp:include page="gare-pg-documentazione.jsp" />
		</gene:pagina>
	</c:when>
	<c:otherwise>
		<c:if test="${not lottoOffertaUnica}">
		<gene:pagina title="Documentazione" idProtezioni="DOCUMGARA" >
			<jsp:include page="gare-pg-documentazione.jsp" />
		</gene:pagina>
		</c:if>
	</c:otherwise>
</c:choose>

	<gene:pagina title="Commissione" idProtezioni="COMMIS"  visibile='${not lottoOffertaUnica and not garaPerElenco and not garaPerCatalogo}'>
		<jsp:include page="gare-pg-commissione.jsp" />
	</gene:pagina>
	<gene:pagina title="Sedute di gara" idProtezioni="SEDUTE"  visibile='${not lottoOffertaUnica and not garaPerElenco and not garaPerCatalogo}'>
		<jsp:include page="gare-pg-seduteDiGara.jsp" />
	</gene:pagina>

<c:choose>
	<c:when test='${not garaPerElenco and not garaPerCatalogo}'>
		<gene:pagina title="1. ${titleFasiRicezione}" idProtezioni="FASIRICEZIONE"  visibile='${not lottoOffertaUnica}'>
			<c:set var="faseRicezione" value="true" />
			<jsp:include page="gare-pg-fasiRicezione.jsp" />
		</gene:pagina>
		<gene:pagina title="Ditte concorrenti" idProtezioni="DITTECONCORRENTI"  visibile='${not lottoOffertaUnica}'>
			<jsp:include page="gare-pg-ditteConcorrenti.jsp" />
		</gene:pagina>
	</c:when>
	<c:when test='${garaPerElenco or garaPerCatalogo}'>
		<gene:pagina title="Iscrizione operatori economici" idProtezioni="FASIISCRIZIONE"  >
			<jsp:include page="gare-pg-fasiIscrizione.jsp" />
		</gene:pagina>
	</c:when>
</c:choose>


	<gene:pagina title="2. Apertura doc.ammin." idProtezioni="FASIGARA-APERTURA_DOC_AMM" selezionabile="${faseGara >= 2}"  visibile='${not lottoOffertaUnica and not garaPerElenco and not garaPerCatalogo and nobustamm ne "1"}'>
		<c:set var="faseGara-Apertura_doc_amm" value="true" />
		<jsp:include page="gare-pg-fasiAperturaDocAmm.jsp" >
			<jsp:param name="paginaFasiGara" value="aperturaDocAmm" /> 
		</jsp:include>
	</gene:pagina>
	<gene:pagina title="${titleFasiGaraTab3}" idProtezioni="FASIGARA" selezionabile="${faseGara >= 5}"  visibile='${not lottoOffertaUnica and not garaPerElenco and not garaPerCatalogo}'>
		<c:set var="faseGaraOff" value="true" />
		<jsp:include page="gare-pg-fasiGara.jsp" >
			<jsp:param name="paginaFasiGara" value="aperturaOffAggProv"/>
			<jsp:param name="isRicercaMercatoNegoziata" value="${isRicercaMercatoNegoziata}"/>
		</jsp:include>
	</gene:pagina>
	<c:if test="${!(isRicercaMercatoNegoziata eq 'true')}">
		<gene:pagina title="${prefTitleAggiudicazione}Aggiudicazione" idProtezioni="AGGDEF"  selezionabile="${empty prefTitleAggiudicazione or !empty dittaProv}"  visibile='${not lottoOffertaUnica and not garaPerElenco and not garaPerCatalogo}'>
			<jsp:include page="gare-pg-aggiudicazione-definitiva.jsp">
				<jsp:param name="lottoDiGara" value="${lottoDiGara}" />
			</jsp:include>
		</gene:pagina>
	</c:if>
	<c:if test="${isRicercaMercatoNegoziata eq 'true'}">
		<gene:pagina title="Affidamenti derivati" idProtezioni="AGGPV"  selezionabile="true"  visibile='true'>
			<jsp:include page="gare-pg-prodotti-valutati.jsp" />
		</gene:pagina>
	</c:if>
	
	<%/*
	<gene:pagina title="Pubblicazioni esito" idProtezioni="PUBESITO"  visibile='${not lottoOffertaUnica and not garaPerElenco and not garaPerCatalogo}'>
		<jsp:include page="gare-pg-pub-esito.jsp" />
	</gene:pagina>
	*/%>
	<c:if test="${not lottoOffertaUnica and not garaPerElenco and not garaPerCatalogo and not isRicercaMercatoNegoziata }">
		<c:choose>
			<c:when test="${paginaContratto eq 'stipula' }">
				<gene:pagina title="Stipula accordo quadro" idProtezioni="STIPULA" >
					<jsp:include page="gare-pg-stipula-accordo-quadro.jsp" />
				</gene:pagina>
			</c:when>
			<c:when test="${paginaContratto eq 'aggEff' }">
				<gene:pagina title="Aggiudicazione efficace" idProtezioni="AGGEFF" >
					<jsp:include page="gare-pg-aggiudicazione-efficace.jsp" />
				</gene:pagina>
			</c:when>
			<c:otherwise>
				<gene:pagina title="Contratto" idProtezioni="CONTRATTO" >
					<jsp:include page="gare-pg-contratto.jsp" />
				</gene:pagina>
			</c:otherwise>
		</c:choose>
		<c:if test="${visualizzaControlloSpesa eq 'true' }">
			<gene:pagina title="Controllo spesa" idProtezioni="SPESA" >
					<jsp:include page="gare-pg-listaControlloSpesa.jsp" />
			</gene:pagina>
		</c:if>
	</c:if>
	
	
	
	<c:choose>
		<c:when test='${garaPerElenco}'>
			<gene:pagina title="Pubblicità elenco" idProtezioni="PUBBLICITA" visibile="${itergaMacro !=3  and garaLottoUnico}">
				<jsp:include page="gare-pg-pubblicita.jsp">
					<jsp:param name="iterga" value="${iterga}" /> 
					<jsp:param name="garaPerElenco" value="garaPerElenco" /> 
				</jsp:include>
			</gene:pagina>
		</c:when>
		<c:when test='${garaPerCatalogo}'>
			<gene:pagina title="Pubblicità catalogo" idProtezioni="PUBBLICITA" visibile="${itergaMacro !=3  and garaLottoUnico}">
				<jsp:include page="gare-pg-pubblicita.jsp" >
					<jsp:param name="iterga" value="${iterga}" /> 
					<jsp:param name="garaPerCatalogo" value="garaPerCatalogo" /> 
				</jsp:include>
			</gene:pagina>
		</c:when>
		<c:otherwise>
			<c:if test="${garaLottoUnico}">
				<gene:pagina title="Pubblicità gara" idProtezioni="PUBBLICITA" visibile="${garaLottoUnico}">
					<jsp:include page="gare-pg-pubblicita.jsp" >
						<jsp:param name="iterga" value="${iterga}" /> 
					</jsp:include>
				</gene:pagina>
			</c:if>
		</c:otherwise>
	</c:choose>
	
	<gene:pagina title="Dettaglio ordine di acquisto" idProtezioni="ORDINE"  visibile='${iterga==6 and not lottoDiGara and not lottoOffertaUnica and not garaPerElenco and not garaPerCatalogo}' selezionabile="${!empty ditta}">
		<jsp:include page="gare-pg-ordineAcquisto.jsp" />
	</gene:pagina>
	<gene:pagina title="Ricezione plichi" idProtezioni="RICPLICHI"  visibile='${not garaPerElenco and not garaPerCatalogo}'>
		<jsp:include page="gare-pg-ricezionePlichi.jsp" />
	</gene:pagina>
	<gene:pagina title="Sintesi note e avvisi" idProtezioni="G_NOTEAVVISI" visibile="${garaLottoUnico and not garaPerCatalogo}">
		<jsp:include page="gare-pg-lista-noteAvvisi.jsp" />
	</gene:pagina>

</gene:formPagine>

<gene:javaScript>
			
var selezionaPaginaOld = selezionaPagina;

selezionaPagina = function(pageNumber) {
var formDaEstendere = $('form[name="pagineForm"]');
<c:if test="${!faseRicezione}">
$('<input>').attr({
    type: 'hidden',
    name: 'logAccessoFasiRic',
	value: '1'
}).appendTo(formDaEstendere);
</c:if>
<c:if test="${!faseGaraApertura_doc_amm}">
$('<input>').attr({
    type: 'hidden',
    name: 'logAccessoFasiAperturaDocAmm',
	value: '1'
}).appendTo(formDaEstendere);
</c:if>
<c:if test="${!faseGaraOff}">
$('<input>').attr({
    type: 'hidden',
    name: 'logAccessoFasiGara',
	value: '1'
}).appendTo(formDaEstendere);
</c:if>
return selezionaPaginaOld(pageNumber);
}

</gene:javaScript>

