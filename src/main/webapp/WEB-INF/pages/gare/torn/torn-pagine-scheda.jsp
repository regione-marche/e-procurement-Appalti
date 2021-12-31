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

<c:set var="itergaMacro" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetITERGAMacroFunction", pageContext, key)}' scope="request"/>
<c:set var="modlic" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetMODLICFunction", pageContext, key)}' />
<c:set var="isProceduraTelematica" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsProceduraTelematicaFunction", pageContext, gene:getValCampo(key,"CODGAR"))}' scope="request"/>
<c:if test='${tipologiaGara == "3"}'>
	<c:set var="bustalotti" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetBustalottiFunction", pageContext, key)}' scope="request"/>
</c:if>
<c:set var="esisteDittaNonValorizzato" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.ControlloDittaLottiFunction", pageContext, gene:getValCampo(key,"CODGAR"), tipologiaGara)}'/>
<c:set var="uffintGara" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetUffintGaraFunction",  pageContext,gene:getValCampo(key,"CODGAR"))}' scope="request"/>
<c:set var="idconfi" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.GetWSDMConfiAttivaFunction",  pageContext,uffintGara,sessionScope.moduloAttivo)}' scope="request"/>

<c:choose>
	<c:when test="${itergaMacro eq 1}">
		<c:set var="titleFasiRicezione" value="Ricezione offerte" />
	</c:when>
	<c:when test="${itergaMacro eq 3}">
		<c:set var="titleFasiRicezione" value="Inviti e ricezione offerte" />
	</c:when>
	<c:otherwise>
		<c:set var="titleFasiRicezione" value="Ricezione domande e offerte" />
	</c:otherwise>
</c:choose>
<c:set var="titlelf" value="Lista lavorazioni e forniture" />
 <c:set var="tipoFornitura" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipoFornitureFunction", pageContext,gene:getValCampo(key, "TORN.CODGAR"))}' />
 <c:if test='${tipoFornitura ==98}'>
	<c:set var="titlelf" value="Lista prodotti" />
 </c:if>

<c:set var="integrazioneWSERP" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSERPFunction", pageContext)}' scope="request" />
<c:set var="visListaLavForn" value="${(modlic eq '6' or modlic eq '5' or modlic eq '14' or modlic eq '16') and bustalotti == '2'}" scope="request" />

<gene:formPagine gestisciProtezioni="true">
	<gene:pagina title="Dati generali" idProtezioni="DATIGEN">
		<jsp:include page="torn-pg-datigen.jsp" />
	</gene:pagina>
	<gene:pagina title="Altri dati" idProtezioni="ALTRIDATI">
		<jsp:include page="torn-pg-altridati.jsp" />
	</gene:pagina>
	<c:if test='${tipologiaGara == "3"}'>
		<gene:pagina title="Lotti di gara" idProtezioni="LOTTI">
			<jsp:include page="torn-pg-lista-lotti.jsp" />
		</gene:pagina>
		<gene:pagina title='${titlelf}' idProtezioni="LISTALAVFORN" visibile="${visListaLavForn}">
			<jsp:include page="/WEB-INF/pages/gare/gare/gare-pg-listaLavoriForniture.jsp" />
		</gene:pagina>
	</c:if>
	<gene:pagina title="Documenti e atti" idProtezioni="DOCUMGARA">
		<jsp:include page="torn-pg-documentazione.jsp" />
	</gene:pagina>
	<c:choose>
		<c:when test='${tipologiaGara == "3"}'>
			<c:if test='${modo eq "VISUALIZZA" or modo eq "MODIFICA" or empty modo}' >
				<c:set var="faseGara" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetFaseGaraFunction", pageContext, key)}' scope="request"/>
			</c:if>
			<gene:pagina title="Commissione" idProtezioni="COMMIS" >
				<jsp:include page="/WEB-INF/pages/gare/gare/gare-pg-commissione.jsp" />
			</gene:pagina>
			<gene:pagina title="Sedute di gara" idProtezioni="SEDUTE" >
				<jsp:include page="/WEB-INF/pages/gare/gare/gare-pg-seduteDiGara.jsp" />
			</gene:pagina>
			<gene:pagina title="1. ${titleFasiRicezione}" idProtezioni="FASIRICEZIONE">
				<jsp:include page="/WEB-INF/pages/gare/gare/gare-pg-fasiRicezione.jsp" />
			</gene:pagina>
			<gene:pagina title="Ditte concorrenti" idProtezioni="DITTECONCORRENTI"  visibile='${not lottoOffertaUnica}'>
				<jsp:include page="/WEB-INF/pages/gare/gare/gare-pg-ditteConcorrenti.jsp" />
			</gene:pagina>
			<c:choose>
				<c:when test="${ bustalotti eq 1}">
					<gene:pagina title="2. Apertura doc.ammin." idProtezioni="FASIGARA-APERTURA_DOC_AMM" selezionabile="${faseGara >= 2}" >
						<jsp:include page="/WEB-INF/pages/gare/gare/gare-pg-fasiAperturaDocAmm.jsp" >
							<jsp:param name="paginaFasiGara" value="aperturaOffAggProvOffUnica"/>  
						</jsp:include>
					</gene:pagina>
					<gene:pagina title="3. Apertura offerte e calcolo aggiud." idProtezioni="OFFAGGPROVV" selezionabile="${faseGara >= 5}">
						<jsp:include page="/WEB-INF/pages/gare/gare/gare-pg-lista-lotti-offertaUnica.jsp" />
					</gene:pagina>		
				</c:when>
				<c:otherwise>
					<gene:pagina title="2. Apertura doc.ammin. e offerte" idProtezioni="FASIGARA" selezionabile="${faseGara >= 2}" >
						<jsp:include page="/WEB-INF/pages/gare/gare/gare-pg-fasiGara.jsp" >
							<jsp:param name="paginaFasiGara" value="fasiGaraOffUnica" /> 
						</jsp:include>
					</gene:pagina>
					<gene:pagina title="3. Calcolo aggiudicazione" idProtezioni="AGGIUDPROVDEF" selezionabile="${faseGara >= 7}">
						<jsp:include page="/WEB-INF/pages/gare/gare/listaLottiAggiudProvDef.jsp" />
					</gene:pagina>	
				</c:otherwise>
			</c:choose>
			<gene:pagina title="4. Aggiudicazione" idProtezioni="AGGIUDDEF" selezionabile="${esisteLottoDittaProvValorizzato eq 'Si'}">
				<jsp:include page="/WEB-INF/pages/gare/gare/listaLottiAggiudDef.jsp" >
					<jsp:param name="stepIniziale" value="2" /> 
				</jsp:include>
			</gene:pagina>
			<%/*
			<gene:pagina title="Pubblicazioni esito" idProtezioni="PUBESITO">
				<jsp:include page="/WEB-INF/pages/gare/gare/gare-pg-pub-esito.jsp" />
			</gene:pagina>
			*/%>
			<c:set var="tipoContratto" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.ControlliVisualizzazionePaginaContrattoFunction", pageContext, gene:getValCampo(key, "TORN.CODGAR"),"codiceGara")}' />
			<c:choose>
				<c:when test="${tipoContratto eq 'stipula' }">
					<c:set var="titoloContratto" value="Stipula accordo quadro"/>
					<c:set var="codiceProtezione" value='STIPULA'/>
				</c:when>
				<c:when test="${tipoContratto eq 'aggEff' }">
					<c:set var="titoloContratto" value="Aggiudicazione efficace"/>
					<c:set var="codiceProtezione" value='AGGEFF'/>
				</c:when>
				<c:otherwise>
					<c:set var="titoloContratto" value="Contratto"/>
					<c:set var="codiceProtezione" value='ATTIAGG'/>
				</c:otherwise>
			</c:choose>
			<gene:pagina title="${titoloContratto }" idProtezioni="${codiceProtezione }">
				<jsp:include page="torn-pg-attiAggiuContr.jsp" >
					<jsp:param name="tipoContratto" value="${tipoContratto }" /> 
				</jsp:include>
			</gene:pagina>
		</c:when>
		<c:otherwise>
			<gene:pagina title="Lotti di gara" idProtezioni="LOTTI">
				<jsp:include page="torn-pg-lista-lotti.jsp" />
			</gene:pagina>
		</c:otherwise>
	</c:choose>
	<gene:pagina title="Pubblicità gara" idProtezioni="PUBBLICITA">
		<jsp:include page="/WEB-INF/pages/gare/torn/torn-pg-pubblicita.jsp">
			<jsp:param name="iterga" value="${iterga}" /> 
		</jsp:include>
	</gene:pagina>
	<gene:pagina title="Sintesi note e avvisi" idProtezioni="G_NOTEAVVISI">
		<jsp:include page="torn-pg-lista-noteAvvisi.jsp" />
	</gene:pagina>
	
</gene:formPagine>
