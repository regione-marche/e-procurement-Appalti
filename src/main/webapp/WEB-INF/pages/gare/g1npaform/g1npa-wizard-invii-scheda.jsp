<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="isRUP" value="${fn:toUpperCase(RP) eq fn:toUpperCase(profiloUtente.codiceFiscale)}"/>
<c:set var="isDRP" value="${fn:toUpperCase(DRP) eq fn:toUpperCase(profiloUtente.codiceFiscale) && not empty codtecDRP}"/>
<c:set var="drpIsNotRP" value="${fn:toUpperCase(RP) ne fn:toUpperCase(DRP) && not empty DRP}"/>
<c:set var="hasCF" value="${not empty profiloUtente.codiceFiscale}"/>
<c:set var="isRettifica" value='${idschedanparet>0}'/>
<c:set var="hasRights" value="${isDRP eq 'true' || (hasCF eq 'true' && ((statonpa le 2 && isIniz eq '1' && isRettifica eq 'false') || statonpa eq 20 || statonpa eq 34 || statonpa eq 50 ))}"/>
<c:set var="hasLoA" value="${not empty accountSSO && accountSSO.hasLoa(3)}"/>
<c:set var="authorized" value="${hasRights eq 'true' && hasLoA eq 'true'}"/>
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" /> 
<c:if test='${fn:contains(listaOpzioniUtenteAbilitate, "ou89#")}' >
	<c:set var="amministratore" value="true"/>
</c:if>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="G1NPAFORM-wizardInvii">

<c:set var="nome_scheda" value ='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, "select nome_scheda from g1cf_npa, g1npaform, g1npascheda where g1npaform.idcfnpa=g1cf_npa.id and g1npascheda.idform=g1npaform.id and g1npascheda.id=?", gene:getValCampo(key,"ID"))}'/>
<c:set var="modpres" value ='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, "select modpres from g1cf_npa, g1npaform, g1npascheda where g1npaform.idcfnpa=g1cf_npa.id and g1npascheda.idform=g1npaform.id and g1npascheda.id=?", gene:getValCampo(key,"ID"))}'/>

<c:set var="sql" value="select codgar from g1npascheda,g1npaform where g1npascheda.id=? and g1npascheda.idform=g1npaform.id"/>
<c:set var="codgar" value='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, sql, gene:getValCampo(key,"ID"))}'/>
<c:set var="sql" value="select genere from v_gare_genere where codgar=? and genere < 100"/>
<c:set var="genereGara" value='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, sql, codgar)}'/>

<c:if test="${(nome_scheda eq 'S2' || (nome_scheda.indexOf('AD')==0 && nome_scheda ne 'AD5')) && statonpa eq 1}">
	<c:set var="sql" value="select count(*) from ditg join impr on ditg.dittao=impr.codimp join v_gare_genere on (v_gare_genere.codgar=ditg.codgar5 and v_gare_genere.genere<100) left join ragdet on (ditg.ngara5=ragdet.ngara and ditg.dittao=ragdet.codimp) where ditg.codgar5 = ? and impr.tipimp in (2,11) and ragdet.codimp is null and ditg.invoff = '1' and (ditg.fasgar is null or  ditg.fasgar > 1) and ((ditg.codgar5<>ngara5 and v_gare_genere.genere in (1,2)) or (ditg.codgar5=ngara5 and v_gare_genere.genere = 3))"/>
	<c:if  test="${(nome_scheda.indexOf('AD')==0 && nome_scheda ne 'AD5')}">
		<c:set var="sql" value="${sql} and (ditg.dittao in (select dq.dittao from ditgaq dq where dq.ngara in (select gx.ngara from gare gx where gx.codgar1=ditg.codgar5)) or ditg.dittao in (select g.ditta from gare g where g.codgar1=ditg.codgar5))"/>
	</c:if>
	<c:set var="consorziateSenzaEsecutrici" value='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, sql, codgar)}'/>
</c:if>

<c:choose>
	<c:when test="${genereGara eq '10'}">
		<c:set var="dicituraTipologiaPerTitolo" value="Elenco"/>
		<c:set var="dicituraTipologiaPerAlert" value="dell'elenco"/>
	</c:when>
	<c:when test="${genereGara eq '11'}">
		<c:set var="dicituraTipologiaPerTitolo" value="Avviso"/>
		<c:set var="dicituraTipologiaPerAlert" value="dell'avviso"/>
	</c:when>
	<c:when test="${genereGara eq '20'}">
		<c:set var="dicituraTipologiaPerTitolo" value="Catalogo"/>
		<c:set var="dicituraTipologiaPerAlert" value="del catalogo"/>
	</c:when>
	<c:otherwise>
		<c:set var="dicituraTipologiaPerTitolo" value="Gara"/>
		<c:set var="dicituraTipologiaPerAlert" value="della gara"/>
	</c:otherwise>
</c:choose>

<c:set var="sql" value="select count(*) from g1npascheda,g1npaform,g1cf_npa where g1npaform.id=g1npascheda.idform and g1npaform.idcfnpa=g1cf_npa.id and g1cf_npa.nome_scheda=? and g1npascheda.statonpa > 1 and g1npascheda.id > ? and g1npaform.codgar=?"/>
<c:set var="ciSonoSchedeDopo" value ='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, sql, nome_scheda, gene:getValCampo(key,"ID"), codgar) > 0}'/>

<c:set var="msgGestioneMrp" value="${gene:callFunction('it.eldasoft.gene.tags.functions.GetPropertyFunction', 'mrp.abilita') eq '1' && inviabileMRP eq '1'?' (in gestione in M-RP)':''}"/>
<c:choose>
<c:when test="${param.modalita eq 'visualizza'}">
	<gene:setString name="titoloMaschera" value="Visualizzazione dati - ${dicituraTipologiaPerTitolo} ${codiceGara} - Scheda ${nome_scheda} ${isRettifica? ' - RETTIFICA':'' }${msgGestioneMrp}" />
</c:when>
<c:when test="${param.modalita eq 'check'}">
	<gene:setString name="titoloMaschera" value="Controllo dati - ${dicituraTipologiaPerTitolo} ${codiceGara} - Scheda ${nome_scheda} ${isRettifica? ' - RETTIFICA':'' }${msgGestioneMrp}" />
</c:when>
<c:otherwise>
	<gene:setString name="titoloMaschera" value="Invia dati ad ANAC - ${dicituraTipologiaPerTitolo} ${codiceGara} - Scheda ${nome_scheda} ${isRettifica? ' - RETTIFICA':'' }${msgGestioneMrp}" />
</c:otherwise>
</c:choose>

<c:set var="isProceduraTelematica" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsProceduraTelematicaFunction", pageContext, codgar)}' scope="request"/>
<c:set var="meruolo" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetMERUOLOFunction", pageContext, codiceGara)}' scope="request"/>

<gene:redefineInsert name="head" >
    <script type="text/javascript">
      var contextPath="${pageContext.request.contextPath}";
    </script>
	<script type="text/javascript" src="${contextPath}/js/jquery.json-editor.min.js"></script>
</gene:redefineInsert>
<gene:redefineInsert name="corpo">

<style>

.wrapper{
	display: inline-flex;
    align-items: flex-start;
	width: 100%;
}


.ProgressBar {
  padding-left: 2em;
  list-style: none;
  position: relative; 
  width: 30%;
   
}

.ProgressBar-step {
  position: relative;
  height: 3em;
  display:flex;
  border-left: 0.5em solid #9F9FA3;
	&.is-complete {	
		border-left: 0.5em solid #00637C;
	}
	&.stop {	
		border-left: 0.5em solid #9F9FA3;
	}
	&.current {	
		border-left: 0.5em solid #9F9FA3;
	}	
}

.ProgressBar-step:last-child {
  height: auto;
}


.ProgressBar-icon {
  margin-left: -13.5px;
  width: 1em;
  height: 1em;
  background-color: #9F9FA3;
  fill: #9F9FA3;
  border-radius: 50%;
  padding: 0.5em;
  z-index: 10;
  
  .is-complete & {
    fill: #DBF1FF;
    background-color: #00637C;
  }
  
  .stop > & {
    background-color: #C61414;
	fill: white;
  }
}

.ProgressBar-stepLabel {
  display: block;
  text-transform: uppercase;
  color: #9F9FA3;
  margin-left:1.2em;
  padding-top: 0.5em;
  width: 250px;
 
  .is-complete > & {
    color: #00637C;
  }
  
  .stop > & {
    color: #C61414;
  }
}

#errori, #nonAutorizzato, #bloccoInvio {
	color: #721c24;
    background-color: #f8d7da;
    border: 1px solid #f5c6cb;
}

#alerts, #messaggiNonBloccanti, #boxAvvisiValidazioneEForm {
	color: #856404;
    background-color: #fff3cd;
    border-color: #ffeeba;
}

#success {
	color: #155724;
    background-color: #d4edda;
    border-color: #c3e6cb;	
}

#dati {
	color: #152357;
    background-color: #d4e4ed;
    border-color: #9aaabd;
}

.message-box {
	padding: 1rem;
    border-radius: 3px;
	margin: 1em 0;
}
.data-box {
	padding: 0.1rem 1rem;
    border-radius: 3px;
	margin: 1em 0;
}

.messaggi {
	width:calc(100% - 250px);
}


#json-input {
	display: block;
	width: 100%;
	height: 200px;
}
#translate {
	display: block;
	height: 28px;
	margin: 20px 0;
	border-radius: 3px;
	border: 2px solid;
	cursor: pointer;
}
#json-display {
	border: 1px solid #;
	margin: 0;
	padding: 10px 20px;
}

.json-editor-blackbord {
    font-size: 12px;
	background: #fff;
    color: #1c2833;
}

.json-property {
    color: #b16b2a !important;
	font-weight: bold;
}

.json-string {
	color: green;
	font-weight: bold;
}

.json-url {
    color: #1e90ff;
	font-weight: bold;
}

.json-literal {
	font-weight: bold;
    color: #ff8c00;
}

.json-literal-boolean {
    font-weight: bold;
    color: #8f3b8f;
}


</style>

<c:set var="isIniz" value="${isIniz eq '1'}" />
<c:set var="isPianificazione" value="${nome_scheda.indexOf('PL')==0 || nome_scheda eq 'P7_3' || nome_scheda eq 'P1_15_1'}" />

<c:if test="${nome_scheda.indexOf('P')==0 && isPianificazione eq 'false' && nome_scheda ne 'P5'}">
	<c:choose>
		<c:when test="${iterga eq 2 or iterga eq 4 or iterga eq 7}">
			<c:if test="${empty dtepar || empty otepar}">
				<c:set var="bloccoInvio" value="Valorizzare data e ora termine presentazione domande di partecipazione."/>
			</c:if>
		</c:when>
		<c:otherwise>
			<c:if test="${not empty iterga && (empty dteoff || empty oteoff)}">
				<c:set var="bloccoInvio" value="Valorizzare data e ora termine presentazione delle offerte."/>
			</c:if>
		</c:otherwise>
	</c:choose>
</c:if>
<c:if test='${statonpa eq 1}'>
	<c:if test='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.npa.CheckRecuperatiEstremiPubblicazionePrecedenteFunction", pageContext, gene:getValCampo(key,"ID")) ne "true"}'>
		<c:if test="${not empty bloccoInvio}">
			<c:set var="bloccoInvio" value="${bloccoInvio} <br><br>"/>
		</c:if>
		<c:set var="bloccoInvio" value="${bloccoInvio}Non sono stati recuperati gli estremi di pubblicazione della precedente scheda pubblicata. Accedere alla scheda pubblicata e attivare la funzione 'Recupera estremi pubblicazione' per poter procedere all'invio della presente scheda"/>
	</c:if>
</c:if>
<c:if test="${nome_scheda.indexOf('A')==0 && isIniz eq 'true' && statonpa eq 1}">
	<c:set var="sql" value="select count(*) from gare where codgar1=? and codgar1<>ngara and ditta is null"/>
	<c:set var="lottiNonAggiudicati" value='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, sql, codgar)}'/>
	<c:if test="${lottiNonAggiudicati >0}">
		<c:if test="${not empty bloccoInvio}"> 
			<c:set var="bloccoInvio" value="${bloccoInvio} <br><br>"/>
		</c:if>
		<c:set var="bloccoInvio" value="${bloccoInvio}La gara non risulta aggiudicata."/>
	</c:if>
</c:if>

<c:if test="${statonpa eq 1}">
	<c:set var="bloccoInvioValidazioneEForm" value="${gene:callFunctionN('it.eldasoft.sil.pg.tags.funzioni.npa.GetEsitoValidazioneEFormFunction', pageContext, gene:getValCampo(key,'ID'))}"/>
</c:if>
<c:if test="${not empty bloccoInvioValidazioneEForm}">
	<c:if test="${not empty bloccoInvio}">
		<c:set var="bloccoInvio" value="${bloccoInvio} <br><br>"/>
	</c:if>
	<c:set var="bloccoInvio" value="${bloccoInvio}${bloccoInvioValidazioneEForm}"/>
</c:if>

<c:if test="${nome_scheda=='S2' && statonpa eq 1}">
	<c:set var="RTNonDettagliate" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.npa.EsistonoRTNonDettagliateFunction", pageContext, codgar)}' />
	<c:if test="${not empty RTNonDettagliate}">
		<c:if test="${not empty bloccoInvio}">
			<c:set var="bloccoInvio" value="${bloccoInvio} <br><br>"/>
		</c:if>
		<c:set var="bloccoInvio" value="${bloccoInvio}Ci sono delle ditte di tipo RT in gara per cui non sono state dettagliate le componenti del raggruppamento in anagrafica. Verificare le seguenti ditte: ${RTNonDettagliate}"/>
	</c:if>
</c:if>

<c:if test="${(modpres eq '3') && (empty isInizi or isIniz ne 'true') && statonpa eq 1}">
	<c:set var="sql" value="select count(*) from g1npalottischeda where idscheda=?"/>
	<c:set var="lottiCollegati" value='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, sql, gene:getValCampo(key,"ID"))}'/>
	<c:if test="${empty lottiCollegati or lottiCollegati == 0}">
		<c:if test="${not empty bloccoInvio}"> 
			<c:set var="bloccoInvio" value="${bloccoInvio} <br><br>"/>
		</c:if>
		<c:choose>
			<c:when test="${genereGara eq '1' or genereGara eq '3'}">
				<c:choose>
					<c:when test="${nome_scheda.indexOf('A1')==0}">
						<c:set var="bloccoInvio" value="${bloccoInvio}Non sono presenti lotti aggiudicati o con esito negativo."/>
					</c:when>
					<c:when test="${nome_scheda.indexOf('A')==0}">
						<c:set var="bloccoInvio" value="${bloccoInvio}Non sono presenti lotti aggiudicati."/>
					</c:when>
					<c:when test="${nome_scheda eq 'NAG'}">
						<c:set var="bloccoInvio" value="${bloccoInvio}Non sono presenti lotti con esito negativo."/>
					</c:when>
				</c:choose>
			</c:when>
			<c:otherwise>
				<c:choose>
					<c:when test="${nome_scheda.indexOf('A1')==0}">
						<c:set var="bloccoInvio" value="${bloccoInvio}La gara non risulta aggiudicata o non ha esito negativo."/>
					</c:when>
					<c:when test="${nome_scheda.indexOf('A')==0}">
						<c:set var="bloccoInvio" value="${bloccoInvio}La gara non risulta aggiudicata."/>
					</c:when>
					<c:when test="${nome_scheda eq 'NAG'}">
						<c:set var="bloccoInvio" value="${bloccoInvio}La gara non ha esito negativo."/>
					</c:when>
				</c:choose>
			</c:otherwise>
		</c:choose>
	</c:if>
</c:if>

<c:if test="${nome_scheda eq 'ID' && statonpa eq 1}">
	<c:set var="ngaraScheda" value="${gene:callFunctionN('it.eldasoft.gene.tags.functions.GetSafeDataFromDB', pageContext, 'select ngara from g1npascheda where id=?', gene:getValCampo(key,'ID'))}" />
	<c:set var="schedeCMoIDInLavorazione" value="${gene:callFunctionN('it.eldasoft.sil.pg.tags.funzioni.GetCountSchedeFunction', pageContext, 'inLavorazioneCM', codgar) > 0
	|| gene:callFunctionN('it.eldasoft.sil.pg.tags.funzioni.GetCountSchedeFunction', pageContext, 'inLavorazioneID', codgar, ngaraScheda) > 0}" />
	<c:if test="${schedeCMoIDInLavorazione eq 'true'}">
		<c:if test="${not empty bloccoInvio}">
			<c:set var="bloccoInvio" value="${bloccoInvio} <br><br>"/>
		</c:if>
		<c:set var="bloccoInvio" value="${bloccoInvio}È già presente una scheda CM1, CM2 o ID inviata ma non ancora confermata. Prima di proseguire, completare l'invio di tale scheda."/>
	</c:if>
</c:if>

<c:if test="${dgueObbligatorio eq 'true'}">
	<c:if test="${not empty bloccoInvio}"> 
		<c:set var="bloccoInvio" value="${bloccoInvio} <br><br>"/>
	</c:if>
	<c:set var="bloccoInvio" value="${bloccoInvio}Il documento DGUE è obbligatorio per importi maggiori o uguali a ${sogliaAttivazioneDGUEObbl} euro."/>
</c:if>
<c:if test="${statonpa eq 1 && not empty requestScope.alertIsNotDGUE}">
	<c:if test="${not empty bloccoInvio}">
		<c:set var="bloccoInvio" value="${bloccoInvio} <br><br>"/>
	</c:if>
	<c:set var="bloccoInvio" value="${bloccoInvio}${requestScope.alertIsNotDGUE}"/>
</c:if>


<gene:formScheda entita="G1NPASCHEDA" gestisciProtezioni="false" >

<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-scheda.jsp">
	<jsp:param name="entita" value="V_GARE_TORN"/>
	<jsp:param name="inputFiltro" value="CODGAR=T:${codgar}"/>
	<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
</jsp:include>
<c:set var="visualizzaPubblicaEsitoSuPortale" value='${gene:callFunctionN("it.eldasoft.sil.pg.tags.funzioni.GetVisualizzaPubblicaEsitoFunction", pageContext, gene:getValCampo(key,"ID")) and autorizzatoModifiche ne "2" and gene:checkProtFunz(pageContext, "ALT", "PubblicaSuPortale") and ((isProceduraTelematica eq "true" and (meruolo eq "1" or meruolo eq "3")) or isProceduraTelematica ne "true")}'/>

<gene:campoScheda campo="ID" visibile="false"/>
<gene:campoScheda campo="IDFORM" visibile="false"/>
<gene:campoScheda campo="IDSCHEDANPA" visibile="false"/>
<gene:campoScheda campo="STATONPA" visibile="false"/>
<gene:campoScheda campo="TEXTNPA" visibile="false"/>
<gene:campoScheda campo="WSRESPONSE" visibile="false"/>
<gene:campoScheda campo="IDAVVISONPA" visibile="false"/>
<gene:campoScheda campo="UUID_EFORM" visibile="false"/>
<gene:campoScheda campo="IDDOC_EFORM" visibile="false"/>
<gene:campoScheda campo="NOME_SCHEDA" entita="G1CF_NPA" from="G1NPAFORM" where="G1NPASCHEDA.IDFORM=G1NPAFORM.ID AND G1NPAFORM.IDCFNPA=G1CF_NPA.ID" visibile="false"/>
<gene:campoScheda campo="ISPUBBNAZ" entita="G1CF_NPA" from="G1NPAFORM" where="G1NPASCHEDA.IDFORM=G1NPAFORM.ID AND G1NPAFORM.IDCFNPA=G1CF_NPA.ID" visibile="false"/>
<gene:campoScheda campo="EFORM" entita="G1CF_NPA" from="G1NPAFORM" where="G1NPASCHEDA.IDFORM=G1NPAFORM.ID AND G1NPAFORM.IDCFNPA=G1CF_NPA.ID" visibile="false"/>
<gene:campoScheda campo="IDSCHEDANPARET" visibile="false"/>
<gene:campoScheda campo="IDAPPALTONPA" entita="TORN" from="G1NPAFORM" where="G1NPASCHEDA.IDFORM=G1NPAFORM.ID AND G1NPAFORM.CODGAR=TORN.CODGAR" visibile="false"/>
<gene:campoScheda campo="INVIABILE" visibile="false"/>
<gene:campoScheda campo="ISPUBTED" visibile="false"/>
<gene:campoScheda campo="ISPUBPVL" visibile="false"/>
<gene:campoScheda campo="VALTEC" entita="TORN" visibile="false"/>
<gene:campoScheda campo="VALTEC" entita="GARE1" from="G1NPAFORM" where="G1NPASCHEDA.IDFORM=G1NPAFORM.ID AND G1NPAFORM.CODGAR=GARE1.CODGAR1" visibile="false"/>

<c:set var="visualizzaRecuperaEstremi" value="${(not empty datiRiga.G1CF_NPA_EFORM and empty datiRiga.G1NPASCHEDA_ISPUBTED) or ((not empty datiRiga.G1CF_NPA_ISPUBBNAZ and datiRiga.G1CF_NPA_ISPUBBNAZ eq '1') and empty datiRiga.G1NPASCHEDA_ISPUBPVL)}" />
<c:set var="urlFEUValorizzato" value='${not empty gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.bandoavvisosimap.ws.url")}'/>
<c:set var="isEFormSuFEU" value="${urlFEUValorizzato and not empty datiRiga.G1NPASCHEDA_UUID_EFORM && not empty datiRiga.G1NPASCHEDA_IDDOC_EFORM}" />

<c:if test="${param.modalita eq 'check' or param.modalita eq 'visualizza'}">
	<gene:campoScheda>
	<c:if test="${param.modalita ne 'visualizza'}">
	<tr>
		<td class="" colSpan="2">
		<div>
			<c:if test="${not empty bloccoInvio}">
				<div id="bloccoInvio" class="message-box">
					<p>${bloccoInvio}</p>
				</div>
			</c:if>
			<c:if test="${consorziateSenzaEsecutrici > 0 && !(nome_scheda.indexOf('AD')==0 && nome_scheda ne 'AD5')}">
				<div id="messaggiNonBloccanti" class="message-box">
					<b>ATTENZIONE:</b> tra i concorrenti vi sono dei consorzi privi di dati relativi a "consorziate esecutrici o non esecutrici che forniscono requisiti al consorzio".
					Verificare e dove previsto dettagliare le consorziate che dovranno essere verificate mediante FVOE.
				</div>
			</c:if>
            <c:if test="${consorziateSenzaEsecutrici > 0 && (nome_scheda.indexOf('AD')==0 && nome_scheda ne 'AD5')}">
                <div id="messaggiNonBloccanti" class="message-box">
                    <b>ATTENZIONE:</b> la ditta aggiudicataria è un consorzio privo di dati relativi a "consorziate esecutrici o non esecutrici che forniscono requisiti al consorzio".
                    Dettagliare le consorziate che dovranno essere verificate mediante FVOE.
                </div>
            </c:if>
			<c:if test="${not empty requestScope.alertDatiSensibili}">
				<div id="messaggiNonBloccanti" class="message-box">
					${requestScope.alertDatiSensibili}
				</div>
			</c:if>
			<div id="boxAvvisiValidazioneEForm" class="message-box" ${empty avvisiValidazioneEForm ? 'style="display: none;"' : ''}>${avvisiValidazioneEForm}</div>
			<div id="errori" class="message-box" style="display:none"></div>
			<div id="alerts" class="message-box" style="display:none"></div>
			<div id="success" class="message-box" style="display:none"></div>
			<div id="dati" class="message-box" style="display:none"></div>
			
		</div>
		</td>
	</tr>
	</c:if>
	</gene:campoScheda>	
	<gene:campoScheda>
	<tr>
		<td class="" colSpan="2">
		 <pre id="json-display"></pre>
			<div class="root"></div>
		</td>
	</tr>
	</gene:campoScheda>
</c:if>
<c:if test="${param.modalita ne 'check' and param.modalita ne 'visualizza'}">
	<gene:campoScheda>
	<tr>
		<td class="" colSpan="2">
			<div class="wrapper"> 
			<c:set var="icon" value="#checkmark-bold"/>
			<c:set var="excludeValue" value="#70;#80;#90;"/>
			<c:if test="${not empty datiRiga.G1NPASCHEDA_IDSCHEDANPARET}">
				<c:set var="excludeValue" value="${excludeValue}#11;#12;#13;#14;#15;#30;#31;#32;#33;"/>
			</c:if>
			<c:if test="${datiRiga.G1NPASCHEDA_STATONPA==2}">
				<c:set var="excludeValue" value="${excludeValue}#1;"/>
			</c:if>
			<c:if test="${datiRiga.G1NPASCHEDA_STATONPA!=2}">
				<c:set var="excludeValue" value="${excludeValue}#2;"/>
			</c:if>
			<c:if test="${true || empty codtecDRP || isPianificazione eq 'true' || isIniz eq 'false' }">

				<c:set var="excludeValue" value="${excludeValue}#11;"/>
			</c:if>
			<c:if test="${datiRiga.G1NPASCHEDA_STATONPA!=13}">
				<c:set var="excludeValue" value="${excludeValue}#13;"/>
			</c:if>
			<c:if test="${datiRiga.G1NPASCHEDA_STATONPA==13}">
				<c:set var="excludeValue" value="${excludeValue}#14;"/>
				<c:set var="icon" value="#crossmark-bold"/>
			</c:if>
			<c:if test="${datiRiga.G1NPASCHEDA_STATONPA!=20}">
				<c:set var="excludeValue" value="${excludeValue}#20;"/>
			</c:if>
			<c:if test="${isIniz eq 'false' || isPianificazione eq 'true' }">
				<c:set var="excludeValue" value="${excludeValue}#33;"/>
			</c:if>
			<c:if test="${datiRiga.G1NPASCHEDA_STATONPA!=34}">
				<c:set var="excludeValue" value="${excludeValue}#34;"/>
			</c:if>
			<c:if test="${datiRiga.G1NPASCHEDA_STATONPA==34}">
				<c:set var="excludeValue" value="${excludeValue}#40;#60;"/>
				<c:set var="icon" value="#crossmark-bold"/>
			</c:if>
			<c:if test="${datiRiga.G1NPASCHEDA_STATONPA!=41}">
				<c:set var="excludeValue" value="${excludeValue}#41;"/>
			</c:if>
			<c:if test="${datiRiga.G1NPASCHEDA_STATONPA!=50}">
				<c:set var="excludeValue" value="${excludeValue}#50;"/>
			</c:if>
			<c:if test="${datiRiga.G1NPASCHEDA_STATONPA==50}">
				<c:set var="excludeValue" value="${excludeValue}#60;"/>
			</c:if>
			<c:if test="${datiRiga.G1NPASCHEDA_STATONPA!=70}">
				<c:set var="excludeValue" value="${excludeValue}#70;"/>
			</c:if>
			<c:if test="${datiRiga.G1NPASCHEDA_STATONPA==70}">
				<c:set var="excludeValue" value="${excludeValue}#80;"/>
			</c:if>
			<c:if test="${datiRiga.G1NPASCHEDA_STATONPA!=90}">
				<c:set var="excludeValue" value="${excludeValue}#90;"/>
			</c:if>
			<c:set var="acquisizioneCIGPrevista" value="false"/>
			<c:set var="pubblicazionePrevista" value="false"/>
			
			<c:forEach var="stato" items="${statiScheda}" varStatus="status">
				<c:set var="statiPossibili" value="${statiPossibili}${stato.tipoTabellato};"/>
			</c:forEach>
			<c:choose>
				<c:when test="${datiRiga.G1NPASCHEDA_STATONPA!=20 && datiRiga.G1NPASCHEDA_STATONPA!=50}">
				<ol class="ProgressBar">
					<c:forEach var="stato" items="${statiScheda}" varStatus="status">
					<c:if test="${!(excludeValue.indexOf('#'.concat(stato.tipoTabellato).concat(';'))>-1)}">
						<li class="ProgressBar-step${stato.tipoTabellato <= datiRiga.G1NPASCHEDA_STATONPA*1? ' is-complete' : ''}${datiRiga.G1NPASCHEDA_STATONPA == stato.tipoTabellato? (icon eq '#crossmark-bold' ?' stop' : ' current') : ''}">
							<svg class="ProgressBar-icon"><use xlink:href="${(stato.tipoTabellato == datiRiga.G1NPASCHEDA_STATONPA)? icon : '#checkmark-bold'}"/></svg>
							<span class="ProgressBar-stepLabel">${stato.descTabellato}</span>
						</li>
					</c:if>
					<c:if test="${stato.tipoTabellato == 33}">
						<c:set var="pubblicazionePrevista" value="true"/>
					</c:if>
					<c:if test="${stato.tipoTabellato == 31}">
						<c:set var="acquisizioneCIGPrevista" value="true"/>
					</c:if>		
				  </c:forEach>
				</ol>
				</c:when>
				<c:otherwise>
					<ol class="ProgressBar">
					<c:forEach var="stato" items="${statiScheda}" varStatus="status">
						<c:if test="${stato.tipoTabellato == datiRiga.G1NPASCHEDA_STATONPA}">
							<li class="ProgressBar-step stop">
								<svg class="ProgressBar-icon"><use xlink:href="#crossmark-bold"/></svg>
								<span class="ProgressBar-stepLabel">${stato.descTabellato}</span>
							</li>
						</c:if>
					  </c:forEach>
					 </ol>
				</c:otherwise>
			</c:choose>

			<svg xmlns="http://www.w3.org/2000/svg" style="display:none">
			  <symbol id="checkmark-bold" viewBox="0 0 24 24">
				<path d="M20.285 2l-11.285 11.567-5.286-5.011-3.714 3.716 9 8.728 15-15.285z"/>
			  </symbol>
			</svg>
			<svg xmlns="http://www.w3.org/2000/svg" style="display:none">
				<symbol id="crossmark-bold" viewBox="0 0 24 24">
					<path d="M18.708 5.292a1.5 1.5 0 0 0-2.122 0L12 9.879 7.414 5.292a1.5 1.5 0 0 0-2.122 2.122L9.879 12l-4.586 4.586a1.5 1.5 0 0 0 2.122 2.122L12 14.121l4.586 4.586a1.5 1.5 0 0 0 2.122-2.122L14.121 12l4.586-4.586a1.5 1.5 0 0 0 0-2.122z" stroke-width="2"/>
	   </symbol>
			</svg>
			
			
				<div class="messaggi">
				
				<c:if test="${authorized ne 'true' and not (nome_scheda eq 'ANN' && statonpa eq 30)}">
					<div id="nonAutorizzato" class="message-box">
					<c:choose>
						<c:when test="${hasLoA ne 'true'}">
							<p>È necessario eseguire la login tramite un sistema di autenticazione SSO che si appoggi a SPID o CIE con livello di autenticazione maggiore o uguale a L2 per poter inviare i dati a PCP.</p>
						</c:when>
						<c:when test="${hasRights ne 'true'}">
							<p>Gli invii ad ANAC sono consentiti solo al soggetto incaricato della trasmissione delle schede ad ANAC.</p>
						</c:when>
					</c:choose>
					</div>
				</c:if>	
				<c:if test="${not empty bloccoInvio}">
					<div id="bloccoInvio" class="message-box">
						<p>${bloccoInvio}</p>
					</div>
				</c:if>
				<c:if test="${datiRiga.G1NPASCHEDA_INVIABILE eq '1' && statonpa eq 1 && isIniz eq 'true' && isRettifica eq 'false'}">
					<div id="messaggiNonBloccanti" class="message-box">
						Dati procedura in sola consultazione. La scheda è passata in gestione nell'applicativo M-RP. Per modificare i dati occorre riprendere il controllo utilizzando la funzione "Riprendi in gestione la scheda"
					</div>
				</c:if>
				<c:if test="${datiRiga.G1NPASCHEDA_STATONPA==1}">
					<div id="boxAvvisiValidazioneEForm" class="message-box" ${empty avvisiValidazioneEForm ? 'style="display: none;"' : ''}>${avvisiValidazioneEForm}</div>
					<c:if test="${consorziateSenzaEsecutrici > 0 && !(nome_scheda.indexOf('AD')==0 && nome_scheda ne 'AD5')}">
						<div id="messaggiNonBloccanti" class="message-box">
							<b>ATTENZIONE:</b> tra i concorrenti vi sono dei consorzi per i quali non sono presenti consorziate esecutrici.
							Verificare e dove previsto aggiungere le ditte esecutrici o non esecutrici che dovranno essere verificate mediante FVOE.
						</div>
					</c:if>
					<c:if test="${consorziateSenzaEsecutrici > 0 && (nome_scheda.indexOf('AD')==0 && nome_scheda ne 'AD5')}">
						<div id="messaggiNonBloccanti" class="message-box">
							<b>ATTENZIONE:</b> la ditta aggiudicataria è un consorzio privo di dati relativi a "consorziate esecutrici o non esecutrici che forniscono requisiti al consorzio".
							Dettagliare le consorziate che dovranno essere verificate mediante FVOE.
						</div>
					</c:if>
					<c:if test="${not empty requestScope.alertDatiSensibili}">
						<div id="messaggiNonBloccanti" class="message-box">
								${requestScope.alertDatiSensibili}
						</div>
					</c:if>
				</c:if>

				<c:if test="${datiRiga.G1NPASCHEDA_STATONPA > 2}">
					<div id="dati" class="data-box " style="">
						<h4>Dati identificativi:</h4>
						<p>${isPianificazione eq 'true'? 'IdPianificazione' : (isIniz eq 'true'? 'IdAppalto': 'IdScheda')}: ${not empty datiRiga.G1NPASCHEDA_IDSCHEDANPA? datiRiga.G1NPASCHEDA_IDSCHEDANPA:datiRiga.TORN_IDAPPALTONPA}</p>
						<c:if test="${not empty datiRiga.G1NPASCHEDA_IDAVVISONPA}"> <p>IdAvviso: ${datiRiga.G1NPASCHEDA_IDAVVISONPA}</p></c:if>
					</div>
				</c:if>
				<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq 1}">
					<div id="dati" class="message-box" style="display:none"></div>
				</c:if>
				<div id="errori" class="message-box" style="display:none"></div>
				<div id="alerts" class="message-box" style="display:none"></div>
				<div id="success" class="message-box" style="display:none"></div>
				<div id="refresh" class="message-box" style="display:none"></div>
				
				<div id="info" class="" style="padding-top: 10px">
				<c:if test="${isIniz eq 'true' && isPianificazione ne 'true'}">
					<c:if test="${datiRiga.G1NPASCHEDA_STATONPA==2}">La chiamata per la creazione dell'appalto precedente è andata in timeout. Occorre verificare attraverso la funzione 'Verifica creazione appalto' se l'operazione è stata comunque elaborata da ANAC: in caso negativo, la scheda tornerà allo stato 'In compilazione'.</c:if>
					<c:if test="${datiRiga.G1NPASCHEDA_STATONPA==1}">Tramite questo wizard è possibile gestire le varie fasi associate alla scheda ANAC. Dopo il lancio della funzione 'Crea appalto' non sarà possibile modificare i dati della scheda.</c:if>
					<c:if test="${datiRiga.G1NPASCHEDA_STATONPA==12}">La verifica dell'appalto è una richiesta che ha bisogno di un'elaborazione da parte di ANAC.<br>Tramite la funzione 'Consulta esito verifica appalto' è possibile verificare se ANAC abbia elaborato tale richiesta.</c:if>
					<c:if test="${datiRiga.G1NPASCHEDA_STATONPA==15}">La conferma dell'appalto è una richiesta che ha bisogno di un'elaborazione da parte di ANAC.<br>Tramite la funzione 'Verifica conferma appalto' è possibile verificare se ANAC abbia elaborato tale richiesta.</c:if>
				</c:if>	
					<c:if test="${datiRiga.G1NPASCHEDA_STATONPA==40 && empty datiRiga.G1NPASCHEDA_IDSCHEDANPARET}">In questa fase è possibile utilizzare la funzione 'Controlla avanzamento' per verificare lo stato di pubblicazione da parte di ANAC.<c:if test="${isPianificazione ne 'true' && isIniz eq 'true'}"> Se la pubblicazione non è ancora avvenuta, è possibile cancellarla tramite la funzione 'Cancella avviso'.</c:if></c:if>	
					<c:if test="${datiRiga.G1NPASCHEDA_STATONPA==41}">L'annullamento della pubblicazione è una richiesta che ha bisogno di un'elaborazione da parte di ANAC.<br>Tramite la funzione 'Verifica stop pubblicazione' è possibile verificare se ANAC abbia elaborato tale richiesta.</c:if>	
				
				<c:if test="${datiRiga.G1NPASCHEDA_STATONPA>=32 && pubblicazionePrevista eq 'true' && datiRiga.G1NPASCHEDA_STATONPA ne '41' && datiRiga.G1NPASCHEDA_STATONPA ne '34' && empty datiRiga.G1NPASCHEDA_IDSCHEDANPARET}">
					<br><br>Le pubblicazioni vengono prese in carico da ANAC entro le 18:00 e sono elaborate entro il giorno lavorativo successivo.
					<br>Ad esempio, se la pubblicazione viene richiesta venerdì entro le 18:00, questa sarà elaborata il lunedì successivo.
				</c:if>
				</div>
				
			</div>
			</div>
			</td>
		</tr>

	<c:set var="visualizzaBottoniAzioneANAC" value="${authorized eq 'true' && empty bloccoInvio}"/>
	</gene:campoScheda>
	<c:if test="${visualizzaBottoniAzioneANAC or visualizzaPubblicaEsitoSuPortale}">
		<gene:campoScheda>
		<tr>
			<td class="comandi-dettaglio" colSpan="2">
				<gene:insert name="addPulsanti"/>
				<c:if test="${visualizzaPubblicaEsitoSuPortale}">
					<INPUT type="button"  class="bottone-azione" value='Pubblica esito su portale Appalti' title='Pubblica esito su portale Appalti' onclick="javascript:pubblicaEsitoSuPortaleAppalti();">
				</c:if>
				<c:if test="${visualizzaBottoniAzioneANAC}">
					<c:choose>
					<c:when test="${isIniz eq 'true' && isPianificazione ne 'true'}">
						<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '1'}">
							<c:if test="${empty datiRiga.G1NPASCHEDA_IDSCHEDANPARET}">
								<INPUT type="button" class="bottone-azione" id="button_inviapcp" value="Crea appalto" title="Crea appalto" onclick="gestioneAppalto('CREA_APPALTO')">
							</c:if>
							<c:if test="${not empty datiRiga.G1NPASCHEDA_IDSCHEDANPARET}">
								<INPUT type="button" class="bottone-azione" id="button_inviapcp" value="Rettifica avviso" title="Rettifica avviso" onclick="gestioneAppalto('RETTIFICA_AVVISO')">
							</c:if>
						</c:if>
						<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '2'}">
							<INPUT type="button" class="bottone-azione" id="button_inviapcp" value="Verifica creazione appalto" title="Verifica creazione appalto" onclick="gestioneAppalto('RICERCA_APPALTO')">
						</c:if>
						<c:if test="${empty datiRiga.G1NPASCHEDA_IDSCHEDANPARET}">
							<c:if test="${false && datiRiga.G1NPASCHEDA_STATONPA eq '10' && (not empty codtecDRP && isPianificazione eq 'false' && isIniz eq 'true')}">
								<INPUT type="button" class="bottone-azione" id="button_inviapcp" value="Delega soggetto" title="Delega soggetto" onclick="gestioneAppalto('AGGIUNGI_SOGGETTO')">
							</c:if>
							<c:if test="${((datiRiga.G1NPASCHEDA_STATONPA eq '10' && (empty codtecDRP || true)) || datiRiga.G1NPASCHEDA_STATONPA eq '11') && timeoutRichiestaPrecedente ne 'true'}">
								<INPUT type="button" class="bottone-azione" id="button_inviapcp" value="Verifica appalto" title="Verifica appalto" onclick="gestioneAppalto('VERIFICA_APPALTO')">
							</c:if>
						</c:if>
						<c:if test="${(datiRiga.G1NPASCHEDA_STATONPA eq '10' || datiRiga.G1NPASCHEDA_STATONPA eq '40') && not empty datiRiga.G1NPASCHEDA_IDSCHEDANPARET}">
							<INPUT type="button" class="bottone-azione" id="button_inviapcp" value="Consulta stato rettifica" title="Consulta stato rettifica" onclick="gestioneAppalto('ESITO_OPERAZIONE','AV_RETT','TUTTI_ESITI',null)">
						</c:if>
						<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '12' || (timeoutRichiestaPrecedente eq 'true' && datiRiga.G1NPASCHEDA_STATONPA eq '10')}">
							<INPUT type="button" class="bottone-azione" id="button_inviapcp" value="Consulta esito verifica appalto" title="Consulta esito verifica appalto" onclick="gestioneAppalto('ESITO_OPERAZIONE','AP_VERIF','ULTIMO_ESITO',null)">
						</c:if>
						<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '14' && timeoutRichiestaPrecedente ne 'true'}">
							<INPUT type="button" class="bottone-azione" id="button_inviapcp" value="Conferma appalto" title="Conferma appalto" onclick="gestioneAppalto('CONFERMA_APPALTO')">
						</c:if>
						<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '15' || (timeoutRichiestaPrecedente eq 'true' && datiRiga.G1NPASCHEDA_STATONPA eq '14')}">
							<INPUT type="button" class="bottone-azione" id="button_inviapcp" value="Verifica conferma appalto" title="Verifica conferma appalto" onclick="gestioneAppalto('ESITO_OPERAZIONE','AP_CONF','ULTIMO_ESITO',null)">
						</c:if>
						<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '30' && acquisizioneCIGPrevista eq 'true'}">
							<INPUT type="button" class="bottone-azione" id="button_inviapcp" value="Acquisisci CIG" title="Acquisisci CIG" onclick="gestioneAppalto('RECUPERA_CIG')">
						</c:if>
						<c:if test="${((datiRiga.G1NPASCHEDA_STATONPA eq '30' && acquisizioneCIGPrevista eq 'false') || datiRiga.G1NPASCHEDA_STATONPA eq '31') && pubblicazionePrevista eq 'true'}">
							<INPUT type="button" class="bottone-azione" id="button_inviapcp" value="Recupera ID avviso" title="Recupera ID avviso" onclick="gestioneAppalto('ESITO_OPERAZIONE','AP_CONF','ULTIMO_ESITO',null);">
						</c:if>
						<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '31' && pubblicazionePrevista eq 'false'}">
							<INPUT type="button" class="bottone-azione" id="button_inviapcp" value="Verifica pubblicazione appalto" title="Verifica pubblicazione appalto" onclick="gestioneAppalto('STATO_APPALTO')">
						</c:if>
						<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '32'}">
							<INPUT type="button" class="bottone-azione" id="button_inviapcp" value="Pubblica avviso" title="Pubblica avviso" onclick="javascript:gestioneAppalto('PUBBLICA_AVVISO');">
						</c:if>
						<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '33'}">
							<INPUT type="button" class="bottone-azione" id="button_inviapcp" value="Controlla avanzamento" title="Controlla avanzamento" onclick="gestioneAppalto('ESITO_OPERAZIONE','AV_PUBB','ULTIMO_ESITO',null)">
						</c:if>
						<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '40' && empty datiRiga.G1NPASCHEDA_IDSCHEDANPARET}">
							<INPUT type="button" class="bottone-azione" id="button_inviapcp" value="Controlla avanzamento" title="Controlla avanzamento" onclick="gestioneAppalto('ESITO_OPERAZIONE','AV_PUBB','ULTIMO_ESITO',null)">
						</c:if>
						<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '41'}">
							<INPUT type="button" class="bottone-azione" id="button_inviapcp" value="Verifica stop pubblicazione" title="Verifica stop pubblicazione" onclick="gestioneAppalto('STATO_AVVISO')">
						</c:if>
						<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '60' and visualizzaRecuperaEstremi}">
							<INPUT type="button" class="bottone-azione" id="button_inviapcp" value="Recupera estremi pubblicazione" title="Recupera estremi pubblicazione" onclick="apriModaleRecuperaEstremiPubblicazione();">
						</c:if>
					</c:when>
					<c:when test="${isIniz eq 'false' && isPianificazione ne 'true'}">
						<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '1'}">
							<INPUT type="button" class="bottone-azione" id="button_inviapcp" value="Crea scheda" title="Crea scheda" onclick="gestioneAppalto('CREA_SCHEDA')">
						</c:if>
						<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '10'}">
							<INPUT type="button" class="bottone-azione" id="button_inviapcp" value="Verifica scheda" title="Verifica scheda" onclick="gestioneAppalto('VERIFICA_SCHEDA')">
						</c:if>
						<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '12'}">
							<INPUT type="button" class="bottone-azione" id="button_inviapcp" value="Consulta esito verifica scheda" title="Consulta esito verifica scheda" onclick="gestioneAppalto('ESITO_OPERAZIONE','SC_VERIF','ULTIMO_ESITO',null)">
						</c:if>
						<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '14'}">
							<INPUT type="button" class="bottone-azione" id="button_inviapcp" value="Conferma scheda" title="Conferma scheda" onclick="gestioneAppalto('CONFERMA_SCHEDA')">
						</c:if>
						<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '15'}">
							<INPUT type="button" class="bottone-azione" id="button_inviapcp" value="Verifica conferma scheda" title="Verifica conferma scheda" onclick="gestioneAppalto('ESITO_OPERAZIONE','SC_CONF','ULTIMO_ESITO',null)">
						</c:if>
						<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '30' && pubblicazionePrevista eq 'true'}">
							<INPUT type="button" class="bottone-azione" id="button_inviapcp" value="Recupera ID avviso" title="Recupera ID avviso" onclick="gestioneAppalto('ESITO_OPERAZIONE','SC_CONF','ULTIMO_ESITO',null);">
						</c:if>
						<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '32' || datiRiga.G1NPASCHEDA_STATONPA eq '40'}">
							<INPUT type="button" class="bottone-azione" id="button_inviapcp" value="Controlla avanzamento" title="Controlla avanzamento" onclick="gestioneAppalto('STATO_AVVISO');">
						</c:if>
						<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '41'}">
							<INPUT type="button" class="bottone-azione" id="button_inviapcp" value="Verifica stop pubblicazione" title="Verifica stop pubblicazione" onclick="gestioneAppalto('STATO_AVVISO')">
						</c:if>
						<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '60' and visualizzaRecuperaEstremi}">
							<INPUT type="button" class="bottone-azione" id="button_inviapcp" value="Recupera estremi pubblicazione" title="Recupera estremi pubblicazione" onclick="apriModaleRecuperaEstremiPubblicazione();">
						</c:if>
					</c:when>
					<c:when test="${isIniz eq 'true' && isPianificazione eq 'true'}">
						<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '1'}">
							<INPUT type="button" class="bottone-azione" id="button_inviapcp" value="Crea piano" title="Crea piano" onclick="gestioneAppalto('CREA_PIANO')">
						</c:if>
						<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '10'}">
							<INPUT type="button" class="bottone-azione" id="button_inviapcp" value="Verifica piano" title="Verifica piano" onclick="gestioneAppalto('VERIFICA_PIANO')">
						</c:if>
						<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '12'}">
							<INPUT type="button" class="bottone-azione" id="button_inviapcp" value="Consulta esito verifica piano" title="Consulta esito verifica piano" onclick="gestioneAppalto('ESITO_OPERAZIONE','PI_VERIF','ULTIMO_ESITO',null)">
						</c:if>
						<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '14'}">
							<INPUT type="button" class="bottone-azione" id="button_inviapcp" value="Conferma piano" title="Conferma piano" onclick="gestioneAppalto('CONFERMA_PIANO')">
						</c:if>
						<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '15'}">
							<INPUT type="button" class="bottone-azione" id="button_inviapcp" value="Verifica conferma piano" title="Verifica conferma piano" onclick="gestioneAppalto('ESITO_OPERAZIONE','PI_CONF','ULTIMO_ESITO',null)">
						</c:if>
						<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '30' && pubblicazionePrevista eq 'true'}">
							<INPUT type="button" class="bottone-azione" id="button_inviapcp" value="Recupera ID avviso" title="Recupera ID avviso" onclick="gestioneAppalto('ESITO_OPERAZIONE','PI_CONF','ULTIMO_ESITO',null);">
						</c:if>
						<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '32' || datiRiga.G1NPASCHEDA_STATONPA eq '40'}">
							<INPUT type="button" class="bottone-azione" id="button_inviapcp" value="Controlla avanzamento" title="Controlla avanzamento" onclick="gestioneAppalto('STATO_AVVISO');">
						</c:if>
						<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '60' and visualizzaRecuperaEstremi}">
							<INPUT type="button" class="bottone-azione" id="button_inviapcp" value="Recupera estremi pubblicazione" title="Recupera estremi pubblicazione" onclick="apriModaleRecuperaEstremiPubblicazione();">
						</c:if>
					</c:when>
					</c:choose>
				</c:if>
				</td>
			</tr>
		</gene:campoScheda>
	</c:if>
</c:if>
</gene:formScheda>

<gene:redefineInsert name="schedaNuovo" />
<gene:redefineInsert name="schedaModifica" />
<gene:redefineInsert name="documentiAzioni" />
<gene:redefineInsert name="addToAzioni" >

<c:set var="opzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" scope="request"/>
	<c:choose>
		<c:when test='${profiloUtente.id eq 48 and param.modalita ne "visualizza"}'>
			<tr>
				<td class="vocemenulaterale" >
					<a href="javascript:openPannelloSupporto();" title="Apri pannello di supporto">
						Apri pannello di supporto
					</a>
				</td>
			</tr>
		</c:when>
		<c:when test='${fn:contains(opzioniUtenteAbilitate, "ou89#") and param.modalita ne "visualizza"}'>
			<tr>
				<td class="vocemenulaterale" >
					<a href="javascript:checkServiceUP();" title="Verifica operatività del servizio">
						Verifica operatività del servizio
					</a>
				</td>
			</tr>
		</c:when>
	</c:choose>
	<c:if test="${param.modalita ne 'check' and param.modalita ne 'visualizza' && empty bloccoInvio && gene:callFunction('it.eldasoft.gene.tags.functions.GetPropertyFunction', 'mrp.abilita') eq '1' && gene:checkProtFunz(pageContext,'ALT','GestioneMRP') && autorizzatoModifiche ne '2' && statonpa ne 20 && statonpa ne 34 && statonpa ne 50}">
	<c:set var="labelMRP" value="${datiRiga.G1NPASCHEDA_INVIABILE eq '1'? 'Riprendi gestione da<br>M-RP': 'Passa gestione a M-RP'}"/>
		<tr>
			<td class="vocemenulaterale" >
				<a href="javascript:gestioneMRP();" title="${labelMRP}">
						${labelMRP}
				</a>
			</td>
		</tr>
	</c:if>
	<c:if test="${(param.modalita ne 'check' and param.modalita ne 'visualizza') && visualizzaPubblicaEsitoSuPortale}">
		<tr>
			<td class="vocemenulaterale" >
				<a href="javascript:pubblicaEsitoSuPortaleAppalti();" title="Pubblica esito su portale Appalti">
					Pubblica esito su portale Appalti
				</a>
			</td>
		</tr>
	</c:if>
	<c:if test="${(param.modalita ne 'check' and param.modalita ne 'visualizza') && authorized eq 'true' && empty bloccoInvio}">	
		<c:choose>
		<c:when test="${isIniz eq 'true' && isPianificazione ne 'true'}">
			<c:if test="${((pubblicazionePrevista eq 'true' && datiRiga.G1NPASCHEDA_STATONPA <= 32) || (pubblicazionePrevista eq 'false' && datiRiga.G1NPASCHEDA_STATONPA <= 15)) && datiRiga.G1NPASCHEDA_STATONPA > 2 && datiRiga.G1NPASCHEDA_STATONPA!=20 && empty datiRiga.G1NPASCHEDA_IDSCHEDANPARET}">
				<tr>
				<td class="vocemenulaterale" >
					<a href="javascript:cancellaAppalto();" title="Cancella appalto">
						Cancella appalto
					</a>
				</td>
				</tr>
			</c:if>
			<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '1'}">
				<tr id="laterale_inviapcp">
				<td class="vocemenulaterale" >
				<c:if test="${empty datiRiga.G1NPASCHEDA_IDSCHEDANPARET}">
					<a href="javascript:gestioneAppalto('CREA_APPALTO');" title="Crea appalto">
						Crea appalto
					</a>
				</c:if>
				<c:if test="${not empty datiRiga.G1NPASCHEDA_IDSCHEDANPARET}">
					<a href="javascript:gestioneAppalto('RETTIFICA_AVVISO');" title="Rettifica avviso">
						Rettifica avviso
					</a>
				</c:if>
				</td>
				</tr>
			</c:if>
			<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '2'}">
				<tr id="laterale_inviapcp">
					<td class="vocemenulaterale" >
						<a href="javascript:gestioneAppalto('RICERCA_APPALTO');" title="Verifica creazione appalto">
							Verifica creazione appalto
						</a>
					</td>
				</tr>
			</c:if>
			<c:if test="${empty datiRiga.G1NPASCHEDA_IDSCHEDANPARET}">
				<c:if test="${false && datiRiga.G1NPASCHEDA_STATONPA eq '10' && (not empty codtecDRP && isPianificazione eq 'false' && isIniz eq 'true')}">
					<tr>
						<td class="vocemenulaterale" >
						<a href="javascript:gestioneAppalto('AGGIUNGI_SOGGETTO');" title="Delega soggetto">
							Delega soggetto
						</a>
						</td>
					</tr>
				</c:if>
				<c:if test="${((datiRiga.G1NPASCHEDA_STATONPA eq '10' && (empty codtecDRP || true)) || datiRiga.G1NPASCHEDA_STATONPA eq '11') && timeoutRichiestaPrecedente ne 'true'}">
					<tr>
						<td class="vocemenulaterale" >
						<a href="javascript:gestioneAppalto('VERIFICA_APPALTO');" title="Verifica appalto">
							Verifica appalto
						</a>
						</td>
					</tr>
				</c:if>
			</c:if>
			<c:if test="${(datiRiga.G1NPASCHEDA_STATONPA eq '10' || datiRiga.G1NPASCHEDA_STATONPA eq '40') && not empty datiRiga.G1NPASCHEDA_IDSCHEDANPARET}">
				<tr>
				<td class="vocemenulaterale" >
					<a href="javascript:gestioneAppalto('ESITO_OPERAZIONE','AV_RETT','TUTTI_ESITI',null);" title="Consulta stato rettifica">
						Consulta stato rettifica
					</a>
				</td>
				</tr>
			</c:if>
			<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '12' || (timeoutRichiestaPrecedente eq 'true' && datiRiga.G1NPASCHEDA_STATONPA eq '10')}">
				<tr>
				<td class="vocemenulaterale" >
					<a href="javascript:gestioneAppalto('ESITO_OPERAZIONE','AP_VERIF','ULTIMO_ESITO',null);" title="Consulta esito verifica appalto">
						Consulta esito verifica appalto
					</a>
				</td>
				</tr>
			</c:if>
			<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '14' && timeoutRichiestaPrecedente ne 'true'}">
				<tr>
				<td class="vocemenulaterale" >
					<a href="javascript:gestioneAppalto('CONFERMA_APPALTO')" title="Conferma appalto">
						Conferma appalto
					</a>
				</td>
				</tr>
			</c:if>
			<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '15' || (timeoutRichiestaPrecedente eq 'true' && datiRiga.G1NPASCHEDA_STATONPA eq '14')}">
				<tr>
				<td class="vocemenulaterale" >
					<a href="javascript:gestioneAppalto('ESITO_OPERAZIONE','AP_CONF','ULTIMO_ESITO',null)" title="Verifica conferma appalto">
						Verifica conferma appalto
					</a>
				</td>
				</tr>
			</c:if>
			<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '30' && acquisizioneCIGPrevista eq 'true'}">
				<tr>
				<td class="vocemenulaterale" >
					<a href="javascript:gestioneAppalto('RECUPERA_CIG')" title="Acquisisci CIG">
						Acquisisci CIG
					</a>
				</td>
				</tr>
			</c:if>
			<c:if test="${((datiRiga.G1NPASCHEDA_STATONPA eq '30' && acquisizioneCIGPrevista eq 'false') || datiRiga.G1NPASCHEDA_STATONPA eq '31') && pubblicazionePrevista eq 'true'}">
				<tr>
				<td class="vocemenulaterale" >
					<a href="javascript:gestioneAppalto('ESITO_OPERAZIONE','AP_CONF','ULTIMO_ESITO',null);" title="Recupera ID avviso">
						Recupera ID avviso
					</a>
				</td>
				</tr>
			</c:if>
			<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '31' && pubblicazionePrevista eq 'false'}">
				<tr>
				<td class="vocemenulaterale" >
					<a href="javascript:gestioneAppalto('STATO_APPALTO');" title="Verifica pubblicazione appalto">
						Verifica pubblicazione appalto
					</a>
				</td>
				</tr>
			</c:if>
			<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '32'}">
				<tr>
				<td class="vocemenulaterale" >
					<a href="javascript:gestioneAppalto('PUBBLICA_AVVISO');" title="Pubblica avviso">
						Pubblica avviso
					</a>
				</td>
				</tr>
			</c:if>
			<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '33' }">
				<tr>
				<td class="vocemenulaterale" >
					<a href="javascript:gestioneAppalto('ESITO_OPERAZIONE','AV_PUBB','ULTIMO_ESITO',null);" title="Controlla avanzamento">
						Controlla avanzamento
					</a>
				</td>
				</tr>
			</c:if>
			<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '40' && empty datiRiga.G1NPASCHEDA_IDSCHEDANPARET}">
				<tr>
				<td class="vocemenulaterale" >
					<a href="javascript:cancellaAvviso();" title="Blocca pubblicazione">
						Blocca pubblicazione
					</a>
				</td>
				</tr>
				<tr>
				<td class="vocemenulaterale" >
					<a href="javascript:gestioneAppalto('ESITO_OPERAZIONE','AV_PUBB','ULTIMO_ESITO',null);" title="Controlla avanzamento">
						Controlla avanzamento
					</a>
				</td>
				</tr>
			</c:if>
			<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '41' }">
				<tr>
				<td class="vocemenulaterale" >
					<a href="javascript:gestioneAppalto('STATO_AVVISO')" title="Verifica stop pubblicazione">
						Verifica stop pubblicazione
					</a>
				</td>
				</tr>
			</c:if>
			<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '60' && false}">
				<tr>
				<td class="vocemenulaterale" >
					<a href="javascript:alert('TODO');" title="Rettifica avviso">
						Rettifica avviso
					</a>
				</td>
				</tr>
			</c:if>
		</c:when>
		<c:when test="${isIniz eq 'false' && isPianificazione ne 'true'}">	
		<c:if test="${datiRiga.G1NPASCHEDA_STATONPA <= 15 && datiRiga.G1NPASCHEDA_STATONPA > 1 && datiRiga.G1NPASCHEDA_STATONPA!=20}">
				<tr>
				<td class="vocemenulaterale" >
					<a href="javascript:cancellaScheda();" title="Cancella scheda">
						Cancella scheda
					</a>
				</td>
				</tr>
			</c:if>
			<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '1'}">
				<tr id="laterale_inviapcp">
				<td class="vocemenulaterale" >
					<a href="javascript:gestioneAppalto('CREA_SCHEDA');" title="Crea scheda">
						Crea scheda
					</a>
				</td>
				</tr>
			</c:if>
			<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '10'}">
				<tr>
				<td class="vocemenulaterale" >
					<a href="javascript:gestioneAppalto('VERIFICA_SCHEDA');" title="Verifica scheda">
						Verifica scheda
					</a>
				</td>
				</tr>
			</c:if>
			<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '12'}">
				<tr>
				<td class="vocemenulaterale" >
					<a href="javascript:gestioneAppalto('ESITO_OPERAZIONE','SC_VERIF','ULTIMO_ESITO',null);" title="Consulta esito verifica scheda">
						Consulta esito verifica scheda
					</a>
				</td>
				</tr>
			</c:if>
			<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '14'}">
				<tr>
				<td class="vocemenulaterale" >
					<a href="javascript:gestioneAppalto('CONFERMA_SCHEDA')" title="Conferma scheda">
						Conferma scheda
					</a>
				</td>
				</tr>
			</c:if>
			<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '15'}">
				<tr>
				<td class="vocemenulaterale" >
					<a href="javascript:gestioneAppalto('ESITO_OPERAZIONE','SC_CONF','ULTIMO_ESITO',null)" title="Verifica conferma scheda">
						Verifica conferma scheda
					</a>
				</td>
				</tr>
			</c:if>
			<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '30' && pubblicazionePrevista eq 'true'}">
				<tr>
				<td class="vocemenulaterale" >
					<a href="javascript:gestioneAppalto('ESITO_OPERAZIONE','SC_CONF','ULTIMO_ESITO',null)" title="Recupera ID avviso">
						Recupera ID avviso
					</a>
				</td>
				</tr>
			</c:if>
			<c:if test="${false &&datiRiga.G1NPASCHEDA_STATONPA eq '40'}">
				<tr>
				<td class="vocemenulaterale" >
					<a href="javascript:cancellaAvviso();" title="Blocca pubblicazione">
						Blocca pubblicazione
					</a>
				</td>
				</tr>
			</c:if>
			<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '32' || datiRiga.G1NPASCHEDA_STATONPA eq '40'}">
			<tr>
				<td class="vocemenulaterale" >
					<a href="javascript:gestioneAppalto('STATO_AVVISO')" title="Controlla avanzamento">
						Controlla avanzamento
					</a>
				</td>
				</tr>
			</c:if>
			<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '41' }">
				<tr>
				<td class="vocemenulaterale" >
					<a href="javascript:gestioneAppalto('STATO_AVVISO')" title="Verifica stop pubblicazione">
						Verifica stop pubblicazione
					</a>
				</td>
				</tr>
			</c:if>
		</c:when>
		<c:when test="${isIniz eq 'true' && isPianificazione eq 'true'}">
			<c:if test="${datiRiga.G1NPASCHEDA_STATONPA <= 15 && datiRiga.G1NPASCHEDA_STATONPA > 1}">
				<tr>
				<td class="vocemenulaterale" >
					<a href="javascript:cancellaPiano()" title="Cancella piano">
						Cancella piano
					</a>
				</td>
				</tr>
			</c:if>		
			<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '1'}">
				<tr id="laterale_inviapcp">
				<td class="vocemenulaterale" >
					<a href="javascript:gestioneAppalto('CREA_PIANO');" title="Crea piano">
						Crea piano
					</a>
				</td>
				</tr>
			</c:if>
			<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '10'}">
				<tr>
				<td class="vocemenulaterale" >
					<a href="javascript:gestioneAppalto('VERIFICA_PIANO');" title="Verifica piano">
						Verifica piano
					</a>
				</td>
				</tr>
			</c:if>
			<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '12'}">
				<tr>
				<td class="vocemenulaterale" >
					<a href="javascript:gestioneAppalto('ESITO_OPERAZIONE','PI_VERIF','ULTIMO_ESITO',null);" title="Consulta esito verifica piano">
						Consulta esito verifica piano
					</a>
				</td>
				</tr>
			</c:if>
			<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '14'}">
				<tr>
				<td class="vocemenulaterale" >
					<a href="javascript:gestioneAppalto('CONFERMA_PIANO')" title="Conferma piano">
						Conferma piano
					</a>
				</td>
				</tr>
			</c:if>
			<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '15'}">
				<tr>
				<td class="vocemenulaterale" >
					<a href="javascript:gestioneAppalto('ESITO_OPERAZIONE','PI_CONF','ULTIMO_ESITO',null)" title="Verifica conferma piano">
						Verifica conferma piano
					</a>
				</td>
				</tr>
			</c:if>
			<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '30' && pubblicazionePrevista eq 'true'}">
				<tr>
				<td class="vocemenulaterale" >
					<a href="javascript:gestioneAppalto('ESITO_OPERAZIONE','PI_CONF','ULTIMO_ESITO',null);" title="Recupera ID avviso">
						Recupera ID avviso
					</a>
				</td>
				</tr>
			</c:if>
			<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '32' || datiRiga.G1NPASCHEDA_STATONPA eq '40'}">
			<tr>
				<td class="vocemenulaterale" >
					<a href="javascript:gestioneAppalto('STATO_AVVISO')" title="Controlla avanzamento">
						Controlla avanzamento
					</a>
				</td>
				</tr>
			</c:if>
		</c:when>
		</c:choose>
		<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '60' and visualizzaRecuperaEstremi}">
			<jsp:include page="/WEB-INF/pages/gare/g1npaform/modaleRecuperaEstremiPubblicazione.jsp">
				<jsp:param value="${codiceGara}" name="codiceGara"/>
				<jsp:param value="${datiRiga.G1NPASCHEDA_UUID_EFORM}" name="uuid_eform"/>
				<jsp:param value="${datiRiga.G1NPASCHEDA_IDDOC_EFORM}" name="iddoc_eform"/>
				<jsp:param value="${genereGara}" name="genereGara"/>
				<jsp:param value="${datiRiga.G1CF_NPA_EFORM}" name="eform"/>
			</jsp:include>
			<tr>
				<td class="vocemenulaterale" >
					<a href="javascript:apriModaleRecuperaEstremiPubblicazione();" title="Recupera estremi pubblicazione">
						Recupera estremi pubblicazione
					</a>
				</td>
			</tr>
		</c:if>
	</c:if>
	<c:if test="${(param.modalita ne 'check' and param.modalita ne 'visualizza') && (datiRiga.G1NPASCHEDA_STATONPA eq '20' || datiRiga.G1NPASCHEDA_STATONPA eq '34' || datiRiga.G1NPASCHEDA_STATONPA eq '50') && isEFormSuFEU && amministratore eq 'true' && not ciSonoSchedeDopo}">
		<tr>
			<td class="vocemenulaterale" >
				<a href="javascript:allineaDatiFEU();" title="Allinea dati su FEU">
					Allinea dati su FEU
				</a>
			</td>
		</tr>
	</c:if>
</gene:redefineInsert>


</gene:redefineInsert>

<gene:javaScript>

$( document ).ready(function() {
<c:if test="${(param.modalita eq 'check' or param.modalita eq 'visualizza') || datiRiga.G1NPASCHEDA_STATONPA eq '1' && empty datiRiga.G1NPASCHEDA_WSRESPONSE}">
	<c:if test="${param.modalita ne 'visualizza'}">
	$('#dati').append($("<p>").html("Controllo in corso.."));
	$('#dati').show();
	</c:if>
	<c:choose>
	<c:when test="${param.modalita eq 'visualizza'}">
	</c:when>
	<c:when test="${isIniz eq 'true' && isPianificazione ne 'true'}">	
		controllaAppalto('CREA_APPALTO')			
	</c:when>
	<c:when test="${isIniz eq 'false' && isPianificazione ne 'true'}">	
		controllaAppalto('CREA_SCHEDA')			
	</c:when>
	<c:when test="${isIniz eq 'true' && isPianificazione eq 'true'}">	
		controllaAppalto('CREA_PIANO')			
	</c:when>
	</c:choose>
	var data = $("#G1NPASCHEDA_TEXTNPA").val();
	if(data) var editor = new JsonEditor('#json-display', JSON.parse(data),{"editable":false});
	
</c:if>
})
function mapResponse(data){
	console.log(data)
	var validationErrors = data.validationErrors
	var anacErrors = data.anacErrors;
	var internalErrors = data.internalErrors;
	var pgErrors = data.pgErrors;
	var errori="";
	$('#alerts').html("").hide();
	$('#errori').html("").hide();
	$('#success').html("").hide();
	$('#refresh').html("").hide();
	var $elements = null;
	
	if(data.done=='N'){
		$elements = $("<li>").text("ERRORE: Si è verificato un errore nell'elaborazione della richiesta da parte di ANAC.");
	}
	if(validationErrors?.length>0){
		  $('#alerts').append($("<p>").html("Il servizio ANAC ha evidenziato i seguenti avvisi:"));
		  $alerts = validationErrors.map((error) => $("<li>").text(error.codice+(error.dettaglio?": "+error.dettaglio:"")));
		  $('#alerts').append($("<ul>").html($alerts));
		  $('#alerts').show();
	}
	if(anacErrors?.length>0){
		$elements = anacErrors.map((error) => $("<li>").text((error.codice=="ERR9999"? "" :error.codice+": ")+(error.dettaglio? error.dettaglio:"")));
	}
	if(internalErrors?.length>0){
		$elements = internalErrors.map((error) => $("<li>").text(error));
	}
	if(pgErrors){
		$elements = $("<li>").html(pgErrors);
	}
	if(anacErrors?.length>0 || internalErrors?.length>0 || pgErrors || data.done=='N'){
		if(anacErrors?.length>0)
			$('#errori').append("<p>Errori bloccanti segnalati dai servizi ANAC:</p>")
		else
			$('#errori').append("<p>Errori bloccanti:</p>")
		$('#errori').append($("<ul>").html($elements));
		$('#errori').show();
		mapErrori(data);
	}else{
		$('#success').show();
		mapSuccess(data);
	}

	<c:if test="${param.modalita ne 'check' && datiRiga.G1NPASCHEDA_STATONPA eq '1' && empty datiRiga.G1NPASCHEDA_WSRESPONSE}">
		<c:if test="${!(nome_scheda.indexOf('PL1')==0 || nome_scheda eq 'P1_15_1')}">
		if(validationErrors?.length>0)
			$('#alerts').html("").append($("<p>").html('Il servizio ANAC ha evidenziato degli avvisi, tornare indietro e utilizzare la funzione "Visualizza e controlla" per ottenere i dettagli.'));
		</c:if>
		if(anacErrors?.length>0 || internalErrors?.length>0 || pgErrors || data.done=='N'){
			$('#errori').html("").append($("<p>").html('Il servizio ANAC ha evidenziato degli errori, tornare indietro e utilizzare la funzione "Visualizza e controlla" per ottenere i dettagli.'));
			 <c:if test="${nome_scheda.indexOf('PL1')==0 || nome_scheda eq 'P1_15_1'}">
				if(pgErrors == "A JSONObject text must begin with '{' at character 0 of ")
					$('#errori').html("").append($("<p>").html("Inserire l'eForm per la pianificazione"));
			</c:if>
		$('#success').append($("<p>").html("Validazione dei dati eseguita.")).show();
		}
	</c:if>
	<c:if test="${param.modalita eq 'check' and nome_scheda.indexOf('PL1')==0}">
		if(pgErrors == "A JSONObject text must begin with '{' at character 0 of ")
			$('#errori').html("").append($("<p>").html("Inserire l'eForm per la pianificazione"));
	</c:if>
	return errori;
}

function mapSuccess(json){
	var pub="${pubblicazionePrevista}";
	var cig="${acquisizioneCIGPrevista}";
	var nomdrp="${NOMDRP}";
<c:if test="${param.modalita ne 'visualizza' and (param.modalita eq 'check' || datiRiga.G1NPASCHEDA_STATONPA eq '1' && empty datiRiga.G1NPASCHEDA_WSRESPONSE)}">
	$('#success').append($("<p>").html("Validazione dei dati eseguita."));
</c:if>
<c:if test="${param.modalita ne 'check' and param.modalita ne 'visualizza'}">	
	<c:choose>
	<c:when test="${isIniz eq 'true' && isPianificazione ne 'true'}"> // schede P, AD
		<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '10' && empty datiRiga.G1NPASCHEDA_IDSCHEDANPARET}">
			$('#success').append($("<p>").html("La creazione dell'appalto è avvenuta con successo: ID appalto = "+json.data.idAppalto));
		</c:if>
		<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '10' && not empty datiRiga.G1NPASCHEDA_IDSCHEDANPARET}">
			$('#success').append($("<p>").html("La rettifica è stata richiesta con successo."));
		</c:if>
		<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '11'}">
			$('#success').append($("<p>").html("La delega è stata registrata. I successivi invii potranno essere effettuati anche dal soggetto delegato <b>"+nomdrp+"</b>"));
		</c:if>
		<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '12'}">
			$('#success').append($("<p>").html("La verifica dell'appalto è stata richiesta con successo."));
		</c:if>
		<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '14'}">
			$('#success').append($("<p>").html("Verifica effettuata con successo. È possibile confermare l'appalto."));
		</c:if>
		<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '15'}">
			$('#success').append($("<p>").html("La conferma dell'appalto è stata richiesta con successo."));
		</c:if>
		<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '20'}">
			$('#success').append($("<p>").html("L'appalto è stato cancellato. È possibile modificare i dati della gara e ricreare la scheda."));
		</c:if>
		<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '30'}">
			$('#success').append($("<p>").html("L'appalto è stato confermato"+((cig === 'true' || pub === 'true')? '. Si può procedere ':'')+(cig === 'true'?  'al recupero dei CIG':'')+((cig === 'true' && pub === 'true')? ' e ':'')+ (pub === 'true'? 'alla successiva pubblicazione':'')+"."));
		</c:if>
		<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '31'}">
			$('#success').append($("<p>").html("CIG recuperati con successo e assegnati ai lotti."));
		</c:if>
		<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '32'}">
			$('#success').append($("<p>").html("ID avviso recuperato con successo. È possibile procedere alla pubblicazione."));
		</c:if>
		<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '33'}">
			$('#success').append($("<p>").html("La pubblicazione è stata richiesta con successo."));
		</c:if>
		<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '40' && empty datiRiga.G1NPASCHEDA_IDSCHEDANPARET}">
			$('#success').append($("<p>").html("L'appalto è in attesa di essere pubblicato. <br><br><b>Ricordati di verificare l'avvenuta pubblicazione da parte di ANAC utilizzando la funzione 'Controlla avanzamento'.<br><br>ATTENZIONE</b>: L'avanzamento di stato viene fatto esclusivamente mediante tale funzione."));
		</c:if>
		<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '40' && not empty datiRiga.G1NPASCHEDA_IDSCHEDANPARET}">
			$('#success').append($("<p>").html("La rettifica è in attesa di essere pubblicata. <br><br><b>Ricordati di verificare l'avvenuta pubblicazione da parte di ANAC utilizzando la funzione 'Consulta stato rettifica'.<br><br>ATTENZIONE</b>: L'avanzamento di stato viene fatto esclusivamente mediante tale funzione."));
		</c:if>
		<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '41'}">
			$('#success').append($("<p>").html("L'annullamento della pubblicazione è stato richiesto con successo."));
		</c:if>
		<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '50'}">
			$('#success').append($("<p>").html("La pubblicazione dell'appalto è stata annullata. <br><br>È possibile modificare i dati della gara e ricreare la scheda."));
		</c:if>
		<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '60' && empty datiRiga.G1NPASCHEDA_IDSCHEDANPARET}">
			$('#success').append($("<p>").html("L'appalto è stato pubblicato sul sistema ANAC. ${eseguitoPubblicazioniInerenti eq 'true' ? '' : '<b>Ricordati di pubblicare sul Portale.</b>'}"));
		</c:if>
		<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '60' && not empty datiRiga.G1NPASCHEDA_IDSCHEDANPARET}">
			$('#success').append($("<p>").html("La rettifica è stata pubblicata. <br><b>I dati delle gara sono stati rettificati.</b>"));
		</c:if>
	</c:when>
	<c:when test="${isIniz eq 'false' && isPianificazione ne 'true'}"> // schede S, A
		<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '10'}">
			$('#success').append($("<p>").html("La creazione della scheda è avvenuta con successo: ID scheda = "+json.data.idScheda));
		</c:if>
		<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '12'}">
			$('#success').append($("<p>").html("La verifica della scheda è stata richiesta con successo."));
		</c:if>
		<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '14'}">
			$('#success').append($("<p>").html("Verifica effettuata con successo. È possibile confermare la scheda."));
		</c:if>
		<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '15'}">
			$('#success').append($("<p>").html("La conferma della scheda è stata richiesta con successo."));
		</c:if>
		<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '20'}">
			$('#success').append($("<p>").html("La scheda è stata cancellata."));
		</c:if>
		<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '30'}">
			$('#success').append($("<p>").html("La scheda è stata confermata."));
		</c:if>
		<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '32'}">
			$('#success').append($("<p>").html("ID avviso recuperato con successo. È possibile consultare lo stato di pubblicazione della scheda."));
		</c:if>
		<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '40'}">
			$('#success').append($("<p>").html("La scheda è in attesa di essere pubblicata. <br><br><b>Ricordati di verificare l'avvenuta pubblicazione da parte di ANAC utilizzando la funzione 'Controlla avanzamento'.<br><br>ATTENZIONE</b>: L'avanzamento di stato viene fatto esclusivamente mediante tale funzione."));
		</c:if>
		<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '41'}">
			$('#success').append($("<p>").html("L'annullamento della pubblicazione è stato richiesto con successo."));
		</c:if>
		<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '50'}">
			$('#success').append($("<p>").html("La pubblicazione della scheda è stata annullata. <br><br>È possibile ricreare la scheda."));
		</c:if>
		<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '60'}">
			$('#success').append($("<p>").html("La scheda è stata pubblicata.${eseguitoPubblicazioniInerenti eq 'true' ? '' : ' <b>Ricordati di pubblicare sul Portale.</b>'}"));
		</c:if>
	</c:when>
	<c:when test="${isIniz eq 'true' && isPianificazione eq 'true'}"> //schede PL
		<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '10'}">
			$('#success').append($("<p>").html("La creazione del piano è avvenuta con successo: ID pianificazione = "+json.data.idPianificazione));
		</c:if>
		<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '12'}">
			$('#success').append($("<p>").html("La verifica del piano è stata richiesta con successo."));
		</c:if>
		<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '14'}">
			$('#success').append($("<p>").html("Verifica effettuata con successo. È possibile confermare il piano."));
		</c:if>
		<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '15'}">
			$('#success').append($("<p>").html("La conferma del piano è stata richiesta con successo."));
		</c:if>
		<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '20'}">
			$('#success').append($("<p>").html("Il piano è stato cancellato."));
		</c:if>
		<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '30'}">
			$('#success').append($("<p>").html("Il piano è stato confermato."));
		</c:if>
		<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '32'}">
			$('#success').append($("<p>").html("ID avviso recuperato con successo. È possibile consultare lo stato di pubblicazione del piano."));
		</c:if>
		<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '40'}">
			$('#success').append($("<p>").html("La scheda è in attesa di essere pubblicata. <br><br><b>Ricordati di verificare l'avvenuta pubblicazione da parte di ANAC utilizzando la funzione 'Controlla avanzamento'.<br><br>ATTENZIONE</b>: L'avanzamento di stato viene fatto esclusivamente mediante tale funzione."));
		</c:if>
		<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '60'}">
			$('#success').append($("<p>").html("La scheda è stata pubblicata sul sistema ANAC. ${eseguitoPubblicazioniInerenti eq 'true' ? '' : '<b>Ricordati di pubblicare sul Portale.</b>'}"));
		</c:if>
	</c:when>
	</c:choose>
		var anotherMsg = "";
		<c:if test="${empty datiRiga.G1NPASCHEDA_IDSCHEDANPARET}">
		if(json?.data?.listaEsiti?.[0]?.esito?.codice === 'WT'){
			anotherMsg="<br>Govway Transaction Id: "+json.govwayTransactionId;
			$('#success').append($("<p>").html('<b>Scheda in corso di elaborazione, attendere e riprovare più tardi.</b>'));
		}
		</c:if>
		$('#success').append($("<p>").html((json.timestamp?("Data evento: "+json.timestamp):"")+anotherMsg));
</c:if>
}


function mapErrori(json){
	var idAppalto= $("#G1NPASCHEDA_IDSCHEDANPA").val();
	<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '13'}">
		$('#errori').append($("<p>").html("La verifica ha avuto esito negativo."))
		$('#errori').append($("<p>").html("<b>ATTENZIONE</b>: È necessario annullare e ricompilare la scheda correggendo gli errori."));
	</c:if>
	<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '34'}">
		$('#errori').append($("<p>").html("La pubblicazione dell'avviso ha avuto esito negativo."))
		$('#errori').append($("<p>").html("<b>ATTENZIONE</b>: È necessario creare nuovamente la scheda correggendo gli errori."));
	</c:if>

	$('#errori').append($("<p>").html((json.timestamp?("Data evento: "+json.timestamp):"")+"<br>Govway Transaction Id: "+json.govwayTransactionId));
}

function controllaAppalto(servizio){
	$('#alerts').html("").hide();
	$('#errori').html("").hide();
	$('#success').html("").hide();
	$(".vocemenulaterale").hide();
	$(".bottone-azione").hide();
	document.getElementById('bloccaScreen').style.visibility='visible';
	document.getElementById('wait').style.visibility='visible';
	$("#wait").offset({ top: $(window).height() / 2, left: ($(window).width() / 2) - 200});
	setTimeout(() => {
		$.ajax({
			type: "POST",
			dataType: "json",
			url: "${contextPath}/pg/ScambioDatiPCP.do?metodo=gestioneAppalto",
			data: {
				codiceGara: "${codiceGara}",
				idForm: $("#G1NPASCHEDA_IDFORM").val(),
				servizio: servizio,
				modalitaInvio: 'VERIFICA'
			},
			async: false,
			success: function(json) {
				$('#dati').hide();
				mapResponse(json);
			document.getElementById('bloccaScreen').style.visibility='hidden';
			document.getElementById('wait').style.visibility='hidden';
			}
		});
		$(".vocemenulaterale").show();
		$(".bottone-azione").show();
	},150);
}

function checkServiceUP(){
	bloccoPagina = true;
	setTimeout(() => {
		$.ajax({
			type: "GET",
			dataType: "json",
			url: "${contextPath}/pg/ScambioDatiPCP.do?metodo=getHealthStatus",
			async: false,
			success: function(json) {
				alert("Il servizio è "+json.status);
				ricaricaPagina();
			}
		});
	},150);

}

function openPannelloSupporto(){
	bloccoPagina = true;
	var tipoScheda="appalto";
	<c:if test="${isIniz eq 'true' && isPianificazione eq 'true'}">
		tipoScheda="pianificazione";
	</c:if>
	<c:if test="${isIniz eq 'false' && isPianificazione ne 'true'}"> 
		tipoScheda="scheda";
	</c:if>
	var _form = $("<form>", {
        "action": "${contextPath}/pg/ScambioDatiPCP.do",
        "name": "formPannelloSupporto",
        "method": "post"
    }).append($('<input>', {
        "name": "metodo",
        "value": "gestionePannelloPCP",
        "type": "hidden"
    })).append($('<input>', {
        "name": "idForm",
        "value": $("#G1NPASCHEDA_IDFORM").val(),
        "type": "hidden"
	})).append($('<input>', {
        "name": "codgar",
        "value": "${codgar}",
        "type": "hidden"
	})).append($('<input>', {
        "name": "codiceGara",
        "value": "${codiceGara}",
        "type": "hidden"
		})).append($('<input>', {
        "name": "tipoScheda",
        "value": tipoScheda,
        "type": "hidden"
	})).append($('<input>', {
        "name": "statiPossibili",
        "value": "${statiPossibili}",
        "type": "hidden"
	})).append($('<input>', {
        "name": "nomeScheda",
        "value": "${nome_scheda}",
        "type": "hidden"
    })).append($('<input>', {
        "name": "CSRFToken",
        "value": csrfToken.split("=")[1],
        "type": "hidden"
    }));
    $("body").append(_form);
    _form.submit();
}

var wsresponse=$("#G1NPASCHEDA_WSRESPONSE").val();
if(wsresponse){
	var json = JSON.parse(wsresponse);
	mapResponse(json);
}

<c:if test="${authorized eq 'true' && (param.modalita ne 'check' and param.modalita ne 'visualizza') && empty bloccoInvio}">
function cancellaAppalto(){
	var motivo=null;
	//motivo=prompt('Inserisci una motivazione');
	if(confirm("Stai per cancellare l'appalto in ANAC. \nProcedendo la scheda verrà annullata e sarà possibile nuovamente modificare i dati della gara. Eventuali CIG già acquisiti saranno invalidati.\n\nConfermi l'operazione?"))
		gestioneAppalto('CANCELLA_APPALTO',null,null,null,motivo);
}
function cancellaScheda(){
	var motivo=null;
	//motivo=prompt('Inserisci una motivazione');
	if(confirm("Stai per cancellare la scheda in ANAC. \nProcedendo la scheda verrà annullata e sarà possibile crearne una nuova. \n\nConfermi l'operazione?"))
		gestioneAppalto('CANCELLA_SCHEDA',null,null,null,motivo);
}
function cancellaPiano(){
	var motivo=null;
	//motivo=prompt('Inserisci una motivazione');
	if(confirm("Stai per cancellare la pianificazione in ANAC. \nProcedendo la scheda verrà annullata e sarà possibile nuovamente modificare i dati dell'avviso. \n\nConfermi l'operazione?"))
		gestioneAppalto('CANCELLA_PIANO',null,null,null,motivo);
}

function cancellaAvviso(){
	<c:choose>
	<c:when test="${acquisizioneCIGPrevista eq 'false'}">
	if(confirm("Stai per bloccare la pubblicazione della scheda in ANAC. \nProcedendo la scheda verrà annullata e sarà possibile crearne una nuova. \n\nConfermi l'operazione?"))
		gestioneAppalto('CANCELLA_AVVISO');
	</c:when>
	<c:otherwise>
	if(confirm("Stai per bloccare la pubblicazione dell'appalto in ANAC. \nProcedendo la scheda verrà annullata e sarà possibile nuovamente modificare i dati della gara. Eventuali CIG già acquisiti saranno invalidati.\n\nConfermi l'operazione?"))
		gestioneAppalto('CANCELLA_AVVISO');
	</c:otherwise>
	</c:choose>
}



function gestioneAppalto(servizio,tipoOperazione,tipoRicerca,esito,motivo){

	<c:if test="${isIniz eq 'true' && isRUP eq 'false' && datiRiga.G1NPASCHEDA_STATONPA eq '1' && isRettifica eq 'false'}">
		var dicituraTipologiaPerAlert = "${dicituraTipologiaPerAlert}";
		if(!confirm("ATTENZIONE: l'utente che sta eseguendo l'operazione non è il RUP "+dicituraTipologiaPerAlert+".\n\nVuoi procedere?"))
			return
	</c:if>
	<c:if test="${consorziateSenzaEsecutrici > 0}">
		if(!confirm("ATTENZIONE: tra i concorrenti vi sono dei consorzi privi di dati relativi a \"consorziate esecutrici o non esecutrici che forniscono requisiti al consorzio\". Una volta trasmessa la scheda ad ANAC non sarà più possibile ritrasmetterla per integrare tali soggetti ai fini delle verifiche FVOE. \n\nVuoi procedere?"))
			return
	</c:if>
	var esitoFEU = null;
	var notEmptyUUID = '${isEFormSuFEU}';
	if (notEmptyUUID == 'true') {
		var esitoFEU = gestisciInvio(servizio,'before');
		if (!esitoFEU[0]) {
			alert('Errore durante la comunicazione con FEU: \r\n' + esitoFEU[1]);
			return;
		}
	}

	bloccoPagina = true;
	setTimeout(() => {
		$.ajax({
			type: "POST",
			dataType: "json",
			url: "${contextPath}/pg/ScambioDatiPCP.do?metodo=gestioneAppalto",
			data: {
				codiceGara: "${codiceGara}",
				idForm: $("#G1NPASCHEDA_IDFORM").val(),
				servizio: servizio,
				tipoOperazione: tipoOperazione,
				tipoRicerca: tipoRicerca,
				esito: esito,
				motivo: motivo
			},
			async: false,
			beforeSend: function() {
				bloccaRichiesteServer();
			},
			success: function(json) {
				successoGestioneAppalto(json,servizio);
				ricaricaPagina();
			}
		});
	},150);
}

function successoGestioneAppalto(json,servizio) {
	var notEmptyUUID = '${isEFormSuFEU}';
	var pgErrors = json.pgErrors;
	var sendValidationFailed = json.validationFailedFEU;
	var statoPubbEU = json.statoPubbEU;
	var statoPubbIT = json.statoPubbIT;
	if (statoPubbEU != null || statoPubbIT != null) {
		alert('La scheda è ancora in attesa di pubblicazione nel sito istituzionale\r\n Per completare l\'operazione, riprovare più tardi');
	} else if (notEmptyUUID == 'true' && pgErrors == null) {
		if (sendValidationFailed == 'true') {
			esitoFEU = gestisciInvio("CANCELLA_SCHEDA",'after', sendValidationFailed);
		} else {
			esitoFEU = gestisciInvio(servizio,'after');
		}
		if (!esitoFEU[0] && sendValidationFailed == 'true') {
			alert('Errore durante la comunicazione con FEU: \r\n' + esitoFEU[1]);
		}
	}
}

function gestisciInvio(servizio, tempoChiamata, sendValidationFailed) {

	var esito = '';
	var msgEsito = '';
	var metodo = '';
	var esitoFEU = false;
	if ((tempoChiamata == 'before') && (servizio == 'CREA_APPALTO' || servizio == 'CREA_SCHEDA' || servizio == 'CREA_PIANO' || servizio == 'RETTIFICA_AVVISO')) {
		metodo = 'INVIA_SCHEDA';
	} else if (tempoChiamata == 'after' && servizio == 'CONSULTA_AVVISO') {
		metodo = 'PUBBLICAZIONE_SCHEDA';
	} else if ((tempoChiamata == 'after') && (servizio == 'CANCELLA_APPALTO' || servizio == 'CANCELLA_SCHEDA' || servizio == 'CANCELLA_PIANO' || sendValidationFailed == 'true')) {
		metodo = 'CANCELLA_SCHEDA';
	}

	if (metodo === '') {
		return [true,''];
	}
	$.ajax({
		type: "POST",
		dataType: "json",
		data: {
			idForm: $("#G1NPASCHEDA_IDFORM").val(),
			metodo: metodo
		},
		async: false,
		beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "${contextPath}/pg/GestisciInvioFEUAJAX.do",
		error: function(){
			alert("Errore inaspettato durante l'operazione, controllare il log per i dettagli");
		},
		success: function(res){
			esito=res.Esito;
			msgEsito = res.MsgErrore;
		},
		complete: function(jqXHR, textStatus) {
			if(textStatus=="error" || textStatus=="parsererror") {
				alert("Errore durante l'operazione di aggiornamento dati a FEU:\r\n" + msgEsito);
            }else {
            	if(esito!="Errore"){
					esitoFEU = [true,''];
				}
            }
		}
	});
	return [esitoFEU,msgEsito];
}

function allineaDatiFEU() {
	if (confirm('Desideri inviare l\'esito di validazione fallita a FEU?')) {
		var esitoFEU = gestisciInvio('CANCELLA_SCHEDA','after');
		if (!esitoFEU[0]) {
			alert('Errore durante l\'operazione: \r\n' + esitoFEU[1]);
		} else {
			alert('Operazione eseguita con successo');
		}
	}
}
</c:if>

function ricaricaPagina(){
	bloccaRichiesteServer();
	window.location=contextPath+'/History.do?metodo=reload&numeroPopUp='+getNumeroPopUp();
}

function toggleErroriCVS() {
	$("#bulletListCVS").toggle();
	$("#visualizzaNascondiDettaglio").children().toggle();
}

function pubblicaEsitoSuPortaleAppalti(){
	var href = "href=gare/commons/popupPubblicaSuPortale.jsp?codgar=${codgar}&tipoPubblicazione=5&bando=0&isProceduraTelematica=${isProceduraTelematica}&genere=${genereGara}";
	var idconfi = "${idconfi}";
	if(idconfi){
		href = href + "&idconfi="+idconfi;
	}
	var genere = "${genereGara}";
	if(genere == 1 || genere == 3){
		href = href + "&entita=TORN&valtec=${datiRiga.TORN_VALTEC}";
	} else {
		href = href + "&entita=GARE&valtec=${datiRiga.GARE1_VALTEC}";
		var ngara = "${codgar}".slice(1);
		href = href + "&ngara=" + ngara;
	}
	openPopUpCustom(href, "insDocumentiPredefiniti", 800, 650, "no", "yes");
}

	function gestioneMRP(){
		var nosogg= "${empty DRP && empty RP}";
		var idform= $("#G1NPASCHEDA_IDFORM").val();
		var nomeute= "${drpIsNotRP eq 'true'?NOMDRP:NOMRUP}";
		var inviabile=$("#G1NPASCHEDA_INVIABILE").val();
		var alertBloccoDati = "${statonpa eq 1 && isIniz eq 'true' && isRettifica eq 'false'}";
		var href = "href=gare/g1npaform/popup-abilita-mrp.jsp?codiceGara=${codiceGara}&drpIsNotRP=${drpIsNotRP}&pubbesito=${visualizzaPubblicaEsitoSuPortale}&nosogg="+nosogg+"&idform="+idform+"&nomeute="+nomeute+"&inviabile="+inviabile+"&alertBloccoDati="+alertBloccoDati;
		openPopUpCustom(href, "gestioneMRP", 850, 400, "yes", "yes");
	}

</gene:javaScript>
</gene:template>