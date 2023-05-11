<%/*
       * Created on 08-ott-2008
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

<jsp:include page="/WEB-INF/pages/commons/defCostantiAppalti.jsp" />
<% // i filtri applicati sono il filtro sul livello utente ed il filtro sul codice profilo %>
<c:set var="filtroLivelloUtente"
	value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "V_GARE_TORN")}' />
<c:set var="filtro" value="${filtroLivelloUtente}" />
<c:if test ="${!empty sessionScope.filtroProfiloAttivo}">
	<c:if test ="${!empty filtro}">
		<c:set var="filtro" value="${filtro} AND " />
	</c:if>
	<c:set var="filtro" value="${filtro} V_GARE_TORN.PROFILOWEB = ${sessionScope.filtroProfiloAttivo}" />
</c:if>

<c:set var="filtroTipoGara" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetFiltroTipoGaraFunction", pageContext)}' />

<c:if test="${!empty filtroTipoGara}">
	<c:if test ="${!empty filtro}">
		<c:set var="filtro" value="${filtro} AND " />
	</c:if>
	<c:set var="filtro" value="${filtro} ${filtroTipoGara }" />
</c:if>

<c:set var="visualizzazioneGareALotti" value='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE.GestioneGareALotti") }'/>
<c:set var="visualizzazioneGareLottiOffUnica" value='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE.GestioneGareLottiOffUnica") }'/>
<c:set var="visualizzazioneGareALottoUnico" value='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE.GestioneGareALottoUnico") }'/>
<c:set var="IsProfiloRDOFunction" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.IsProfiloRDOFunction", pageContext)}' />
<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>

<c:set var="propertyCig" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction",  urlWsCig)}' scope="request"/>
<c:if test="${empty propertyCig and gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.RichiestaCIG')}">
	<c:set var="isCigAbilitato" value='1'/>
</c:if>	


<c:if test="${!visualizzazioneGareALotti}">
	<c:if test ="${!empty filtro}">
		<c:set var="filtro" value="${filtro} AND " />
	</c:if>
	<c:set var="filtro" value="${filtro} V_GARE_TORN.GENERE <>1 "/>
</c:if>

<c:if test="${!visualizzazioneGareLottiOffUnica}">
	<c:if test="${!empty filtro}">
		<c:set var="filtro" value="${filtro } AND "/>
	</c:if>
	<c:set var="filtro" value="${filtro} V_GARE_TORN.GENERE <>3 "/>
</c:if>

<c:if test="${!visualizzazioneGareALottoUnico}">
	<c:if test ="${!empty filtro}">
		<c:set var="filtro" value="${filtro} AND " />
	</c:if>
	<c:set var="filtro" value="${filtro} V_GARE_TORN.GENERE <>2 "/>
</c:if>

<gene:template file="ricerca-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="V_GARE_TORN-trova">
	
	<gene:redefineInsert name="head">
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.pg.titoli.js"></script>
	</gene:redefineInsert>
	
	<gene:setString name="titoloMaschera" value="Ricerca gare"/>
	
	<gene:redefineInsert name="trovaCreaNuovo">
		<c:if test='${gene:checkProtFunz(pageContext,"INS","TROVANUOVO")}'>
			<tr>
				<td class="vocemenulaterale">
					<a href="javascript:trovaCreaNuovaGara();" title="Inserisci" tabindex="1503">
						${gene:resource("label.tags.template.trova.trovaCreaNuovo")}
					</a>
				</td>
			</tr>
		</c:if>
	</gene:redefineInsert>
	<% // Ridefinisco il corpo della ricerca %>
	<gene:redefineInsert name="corpo">
  	<% // Creo la form di trova con i campi dell'entità peri %>
  	<gene:formTrova entita="V_GARE_TORN" filtro="${filtro}" gestisciProtezioni="true" >
		<gene:gruppoCampi idProtezioni="GEN">
			<tr><td colspan="3"><b>Dati generali</b></td></tr>
			<gene:campoTrova campo="CODICE"/>
			<gene:campoTrova campo="NUMAVCP" entita="TORN" where="torn.codgar = v_gare_torn.codgar"/>
			<gene:campoTrova campo="TIPGEN"/>
			<gene:campoTrova campo="SETTORE" entita="TORN" where="torn.codgar = v_gare_torn.codgar"/>
			<gene:campoTrova campo="OGGETTO"/>
			<gene:campoTrova campo="NOMTEC" entita="TECNI"  from="TORN" where="torn.codgar = v_gare_torn.codgar and tecni.codtec=torn.codrup" title="Resonsabile unico di procedimento"/>
			<gene:campoTrova campo="ISLOTTI" />
			<gene:campoTrova campo="GENERE" />
			<gene:campoTrova campo="TIPGAR" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoTIPGARPaginaTrova"/>
			<gene:campoTrova entita="TORN" campo="ACCQUA" where="torn.codgar = v_gare_torn.codgar" title="Accordo quadro?"/>
			<c:if test="${gene:checkProt(pageContext, 'COLS.VIS.GARE.GARE.PRECED')}">
				<gene:campoTrova entita="GARE" campo="PRECED" where="GARE.NGARA=V_GARE_TORN.CODICE"/>
			</c:if>
			<gene:campoTrova campo="STATO" entita="V_GARE_STATOESITO" where="V_GARE_STATOESITO.CODICE=V_GARE_TORN.CODICE" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoStato"/>
			<gene:campoTrova campo="ESITO" entita="V_GARE_STATOESITO" where="V_GARE_STATOESITO.CODICE=V_GARE_TORN.CODICE" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoEsito"/>
			<gene:campoTrova entita="GARE" campo="DATTOG" title="Data atto autorizzativo" where="gare.codgar1 = v_gare_torn.codgar" />
			<gene:campoTrova entita="GARE" campo="NATTOG" title="Numero atto autorizzativo" where="gare.codgar1 = v_gare_torn.codgar" />
			<gene:campoTrova entita="GARE" campo="NPROAG" title="Numero protocollo atto autorizzativo" where="gare.codgar1 = v_gare_torn.codgar" />
			<c:if test='${gene:checkProt(pageContext,"SEZ.VIS.GARE.GARE-scheda.DATIGEN.GARERDA") or gene:checkProt(pageContext,"SEZ.VIS.GARE.TORN-scheda.DATIGEN.GARERDA") or gene:checkProt(pageContext,"SEZ.VIS.GARE.TORN-OFFUNICA-scheda.DATIGEN.GARERDA")}'>
				<gene:campoTrova entita="GARERDA" campo="DATRIL" title="Data rilascio richiesta di acquisto" where="garerda.codgar = v_gare_torn.codgar" />
				<gene:campoTrova entita="GARERDA" campo="NUMRDA" title="Numero richiesta di acquisto" where="garerda.codgar = v_gare_torn.codgar" />
			</c:if>
			<c:if test='${gene:checkProt(pageContext,"SEZ.VIS.GARE.GARE-scheda.DATIGEN.IDS") or gene:checkProt(pageContext,"SEZ.VIS.GARE.TORN-scheda.DATIGEN.IDS")}'>
				<gene:campoTrova entita="GAREIDS" campo="NPROT" title="Num.protocollo impegno di spesa" where="gareids.codgar = v_gare_torn.codgar" />
				<gene:campoTrova entita="GAREIDS" campo="DATEMISS" title="Data emissione impegno di spesa" where="gareids.codgar = v_gare_torn.codgar" />
			</c:if>
			<c:if test="${! empty sessionScope.uffint && gene:checkProt(pageContext, 'COLS.VIS.GARE.TORN.ALTRISOG')}">
				<c:set var="iscuc" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetIscucFunction", pageContext, sessionScope.uffint)}' />
				<c:if test="${iscuc == '1'}">
					<gene:campoTrova entita="UFFINT" campo="NOMEIN" from="GARALTSOG, GARE" where="GARALTSOG.CENINT =  UFFINT.CODEIN and GARALTSOG.NGARA = GARE.NGARA and GARE.CODGAR1 = V_GARE_TORN.CODGAR" title="Soggetto per cui agisce la centrale di committenza"/>
				</c:if>
			</c:if>
			<c:if test="${isCigAbilitato eq '1' }">
				<gene:campoTrova entita="V_W3GARE" campo="STATO_GARA" where="v_w3gare.codgar = v_gare_torn.codgar" />
			</c:if>
			<c:if test="${gene:checkProt(pageContext, 'COLS.VIS.GARE.TORN.CLIV2')}">
				<gene:campoTrova entita="USRSYS" campo="SYSUTE" where="USRSYS.SYSCON=V_GARE_TORN.CLIV2" title="Referente"/>
			</c:if>
			<c:if test='${fn:contains(listaOpzioniDisponibili, "OP114#") && fn:contains(listaOpzioniDisponibili, "OP132#")}'>			
				<gene:campoTrova campo="GARTEL" />
			</c:if>
			<gene:campoTrova campo="ISARCHI" defaultValue="2" />
		</gene:gruppoCampi>

		<gene:gruppoCampi idProtezioni="GARE" >
			<tr><td colspan="3"><b>Dati della gara a lotto unico o del lotto di gara</b></td></tr>
			<gene:campoTrova campo="CODIGA" entita="GARE" where="gare.codgar1 = v_gare_torn.codgar" />
			<gene:campoTrova campo="CODCIG" entita="GARE" where="gare.codgar1 = v_gare_torn.codgar" />
			<gene:campoTrova campo="NOT_GAR" entita="GARE" where="gare.codgar1 = v_gare_torn.codgar" />
			<gene:campoTrova campo="IMPAPP" entita="GARE" where="gare.codgar1 = v_gare_torn.codgar" title="Imp. a base di gara"/>
			<gene:campoTrova campo="CATIGA" entita="CATG" from="GARE" where="CATG.NGARA = GARE.NGARA and GARE.CODGAR1 = V_GARE_TORN.CODGAR" title="Codice categoria prevalente" />
			<gene:campoTrova campo="CODCPV" entita="GARCPV" from="GARE" where="GARCPV.NGARA = GARE.NGARA and GARE.CODGAR1 = V_GARE_TORN.CODGAR" />
			<gene:campoTrova campo="CUPPRG" entita="GARE" where="gare.codgar1 = v_gare_torn.codgar" />
			<gene:campoTrova campo="CLAVOR" entita="GARE" where="gare.codgar1 = v_gare_torn.codgar"/>
			<gene:campoTrova campo="FASGAR" entita="GARE" where="gare.codgar1 = v_gare_torn.codgar" />
		</gene:gruppoCampi>
		<gene:gruppoCampi idProtezioni="TERM">
			<tr><td colspan="3"><b>Termini della gara</b></td></tr>
			<gene:campoTrova entita="TORN" campo="DPUBAV" where="torn.codgar = v_gare_torn.codgar" title="Data pubblicazione bando" />
			<gene:campoTrova entita="TORN" campo="DINVIT" where="torn.codgar = v_gare_torn.codgar" />
			<gene:campoTrova entita="TORN" campo="DTEOFF" where="torn.codgar = v_gare_torn.codgar" title="Data termine presentazione offerte" />
			<gene:campoTrova entita="TORN" campo="DESOFF" where="torn.codgar = v_gare_torn.codgar" title="Data apertura offerte" />
		</gene:gruppoCampi>
		<gene:gruppoCampi idProtezioni="AGGIU">
			<tr><td colspan="3"><b>Aggiudicazione</b></td></tr>
			<gene:campoTrova entita="GARE" campo="CRITLICG" where="gare.codgar1 = v_gare_torn.codgar" />
			<gene:campoTrova entita="IMPR" campo="NOMEST" from="GARE" where="gare.codgar1 = v_gare_torn.codgar and (gare.ditta=impr.codimp or exists (select dittao from ditgaq where gare.ngara=ditgaq.ngara and ditgaq.dittao=impr.codimp))" title="Ragione sociale ditta aggiudicataria" />
			<gene:campoTrova entita="GARE" campo="IAGGIU" where="gare.codgar1 = v_gare_torn.codgar"  />
			<gene:campoTrova entita="GFOF" campo="NOMFOF" from="GARE" where="gfof.ngara2 = GARE.NGARA and GARE.CODGAR1 = v_gare_torn.codgar" title="Nome tecnico componente della commissione"/>
			
		</gene:gruppoCampi>
		<gene:gruppoCampi idProtezioni="CONTR">
			<tr id="rowTITOLO_CONTRATTO"><td colspan="3"><b>Contratto</b></td></tr>
			<gene:campoTrova entita="GARE" campo="NREPAT" id="GARE_NREPAT" where="gare.codgar1 = v_gare_torn.codgar" title="Num.repertorio contratto"/>
			<gene:campoTrova entita="GARE" campo="DAATTO" id="GARE_DAATTO" where="gare.codgar1 = v_gare_torn.codgar" title="Data contratto"/>
			<gene:campoTrova entita="GARECONT" campo="NPROAT" id="GARECONT_NPROAT" from="GARE" where="GARECONT.NGARA = GARE.NGARA and gare.codgar1 = v_gare_torn.codgar" title="Num.prot. contratto"/>
			<gene:campoTrova entita="GARECONT" campo="DATRES" id="GARECONT_DATRES" from="GARE" where="GARECONT.NGARA = GARE.NGARA and gare.codgar1 = v_gare_torn.codgar" title="Data restituz.per accettazione contratto"/>
			
			<gene:campoTrova entita="GARECONT" campo="DVERBC"  from="GARE" where="GARECONT.NGARA = GARE.NGARA and gare.codgar1 = v_gare_torn.codgar" title="Data inizio esecuzione"/>
			<gene:campoTrova entita="GARECONT" campo="DCERTU"  from="GARE" where="GARECONT.NGARA = GARE.NGARA and gare.codgar1 = v_gare_torn.codgar" />
			<gene:campoTrova entita="GARECONT" campo="IMPLIQ"  from="GARE" where="GARECONT.NGARA = GARE.NGARA and gare.codgar1 = v_gare_torn.codgar" />
		</gene:gruppoCampi>
    </gene:formTrova>    
  </gene:redefineInsert>
  
  <gene:javaScript>
  	function trovaCreaNuovaGara(){
			document.location.href = contextPath + "/pg/InitNuovaGara.do?" + csrfToken;
  	}
  	
  	<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.PersonalizzazioneAutovie")}'>
		redefineLabelsFormRicerca("Autovie");
		redefineTooltipsFromRicerca("Autovie");
		redefineTitlesFromRicerca("Autovie");
	</c:if>
	<c:if test="${IsProfiloRDOFunction == 'true' }">
		redefineLabelsFormRicerca("RDO");
		redefineTooltipsFromRicerca("RDO");
		redefineTitlesFromRicerca("RDO");
	</c:if>
  </gene:javaScript>
</gene:template>
