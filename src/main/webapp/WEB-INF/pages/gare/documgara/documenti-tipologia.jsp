<%
/*
 * Created on: 14/07/2010
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<script type="text/javascript" src="${contextPath}/js/jquery.documenti.gara.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>


<jsp:include page="/WEB-INF/pages/commons/defCostantiAppalti.jsp" />

<c:set var="ngara" value='${gene:getValCampo(key, "GARE.NGARA")}' />
<c:set var="genereGara" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipologiaGaraFunction", pageContext, key)}' scope="request"/>
<c:if test="${genereGara eq '10' or genereGara eq '20' }">
	<c:set var="tipologia" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipologiaGarealboFunction", pageContext, key)}' scope="request"/>
</c:if>

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>

<c:set var="gestioneUrl" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.IsGestioneUrlDocumentazioneFunction", pageContext)}' scope="request"/>

<c:set var="richiestaFirma" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "documentiDb.richiestaFirma")}'/>

<c:set var="key" value='${param.key}'/>
<c:set var="gruppo" value='${param.gruppo}'/>
<c:set var="lottoFaseInvito" value='${param.lottoFaseInvito}'/>
<c:set var="isProceduraTelematica" value='${param.isProceduraTelematica}'/>


<c:choose>
	<c:when test='${!empty param.bustalotti}'>
		<c:set var="bustalotti" value="${param.bustalotti}" />
	</c:when>
	<c:otherwise>
		<c:set var="bustalotti" value="${bustalotti}" />
	</c:otherwise>
</c:choose>	

<c:choose> 
	<c:when test="${gruppo eq 3}" >
		<c:set var="busta" value='${param.busta}'/>
		<c:set var="titoloBusta" value='${param.titoloBusta}'/>
	</c:when>
	<c:otherwise>
		<c:set var="tipologiaDoc" value='${param.tipologiaDoc}'/>
	</c:otherwise>
</c:choose>

<c:choose> 
	<c:when test="${!empty param.idconfi}" >
		<c:set var="idconfi" value='${param.idconfi}' scope="request"/>
	</c:when>
	<c:otherwise>
		<c:set var="idconfi" value='${idconfi}' scope="request"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${(genereGara eq 3 or genereGara eq 1 and not lottoFaseInvito)}">
		<c:set var="codiceGara" value='${gene:getValCampo(key, "TORN.CODGAR")}' />
	</c:when>
	<c:otherwise>
		<c:set var="codiceGara" value='${param.codiceGara}' />
	</c:otherwise>
</c:choose>

<c:if test='${(gruppo eq 1 or gruppo eq 15) and (genereGara eq 1 or genereGara eq 2 or genereGara eq 3)}'>
		<c:set var="uffintGara" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetUffintGaraFunction",  pageContext,codiceGara)}' />
		<c:set var="idconfi" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.GetWSDMConfiAttivaFunction",  pageContext,uffintGara,sessionScope.moduloAttivo)}' />
		<c:set var="insDocDaProtocollo" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.GetPropertyWsdmFunction", inserimentoDocDaProtocollo,idconfi)}'/>
		<c:set var="ssoProtocollo" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "sso.protocollo")}'/>
		<c:if test="${not empty ssoProtocollo and ssoProtocollo ne '0'}">
			<c:set var="sso" value='true'/>
		</c:if>
		
</c:if>


<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="DOCUMGARA-Tipologia-scheda">


	<gene:redefineInsert name="corpo">

	<c:choose>
		<c:when test="${(genereGara eq 3 or genereGara eq 1 and not lottoFaseInvito)}">
			<gene:formScheda entita="TORN" gestisciProtezioni="true"  plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreDocumentazioneGara" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreDocumentazioneGara">
			
				
			
				<gene:redefineInsert name="pulsanteNuovo"></gene:redefineInsert>
				<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
				<gene:redefineInsert name="addHistory">
					<c:if test="${modo eq 'VISUALIZZA' and param.firstTimer}" > 
						<gene:historyAdd titolo="${datiRiga.G1CF_PUBB_NOME}" id="${datiRiga.G1CF_PUBB_NOME}" />
					</c:if>
				</gene:redefineInsert>	
				<gene:setString name="titoloMaschera" value="${datiRiga.G1CF_PUBB_NOME}"/>
				<gene:campoScheda campo="CODGAR"  value="${codiceGara}"  visibile="false" />
				<c:choose> 
					<c:when test="${gruppo eq 3}" >
						<gene:campoScheda campo="NOME" entita="G1CF_PUBB" campoFittizio="true" definizione="T21" visibile="false" value="${titoloBusta}"/>
					</c:when>
					<c:otherwise>
						<gene:campoScheda campo="NOME" entita="G1CF_PUBB" where="id= ${tipologiaDoc}" visibile="false" />
					</c:otherwise>
				</c:choose>
				<gene:campoScheda campo="CODGAR1"  entita="GARE" campoFittizio="true" definizione="T21" visibile="false" value="${datiRiga.TORN_CODGAR}"/>
				<gene:campoScheda campo="ITERGA" visibile="false"/>
				<gene:campoScheda campo="VALTEC" visibile="false"/>
				<gene:campoScheda campo="MODLIC" visibile="false"/>
				<gene:campoScheda campo="CRITLIC" visibile="false"/>
				<gene:campoScheda campo="TIPGAR" visibile="false"/>
				<gene:campoScheda campo="TIPGEN" visibile="false"/>
				<gene:campoScheda campo="IMPTOR" visibile="false"/>
				
				<input type="hidden"  name="tipologiaDoc" value="${tipologiaDoc}">
				<input type="hidden"  name="busta" value="${busta}">
				<input type="hidden"  name="titoloBusta" value="${titoloBusta}">
				<input type="hidden"  name="gruppo" value="${gruppo}">
				<input type="hidden"  name="tipoDoc" value="${tipoDoc}">
				<input type="hidden"  name="lottoFaseInvito" value="${lottoFaseInvito}">
				<input type="hidden"  name="isProceduraTelematica" value="${isProceduraTelematica}">
				<input type="hidden"  name="bustalotti" value="${bustalotti}">
				<input type="hidden"  name="idconfi" value="${idconfi}">
				
				<jsp:include page="/WEB-INF/pages/gare/documgara/torn-sezione-documenti.jsp">
					<jsp:param name="codiceGara" value="${codiceGara}"/>
					<jsp:param name="tipoDoc" value="${gruppo}"/>
					<jsp:param name="gruppo" value="${gruppo}"/>
					<jsp:param name="bustalotti" value="${bustalotti}"/>
					<jsp:param name="richiestaFirma" value="${richiestaFirma}"/>
					<jsp:param name="tipologiaDoc" value="${tipologiaDoc}"/>
					<jsp:param name="busta" value="${busta}"/>
					<jsp:param name="titoloBusta" value="${titoloBusta}"/>
					<jsp:param name="lottoFaseInvito" value="${lottoFaseInvito}" />
					<jsp:param name="nomeTipologia" value="${datiRiga.G1CF_PUBB_NOME}" />
					<jsp:param name="isProceduraTelematica" value="${isProceduraTelematica}" />
					<jsp:param name="insDocDaProtocollo" value="${insDocDaProtocollo}" />
					
				</jsp:include>
				
			</gene:formScheda>
		</c:when>
		<c:otherwise>
			<gene:formScheda entita="GARE" gestisciProtezioni="true"  plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreDocumentazioneGara" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreDocumentazioneGara">
		
				<c:set var="ngara" value='${gene:getValCampo(key, "GARE.NGARA")}' />
				
				
				<gene:redefineInsert name="pulsanteNuovo"></gene:redefineInsert>
				<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>

				<gene:redefineInsert name="addHistory">
					<c:if test="${modo eq 'VISUALIZZA' and param.firstTimer}" > 
						<gene:historyAdd titolo="${datiRiga.G1CF_PUBB_NOME}" id="${datiRiga.G1CF_PUBB_NOME}" />
					</c:if>
				</gene:redefineInsert>	
				
				<gene:setString name="titoloMaschera" value="${datiRiga.G1CF_PUBB_NOME}"/>
				<c:set var="nomeTipologia" value='${datiRiga.G1CF_PUBB_NOME}' />
				<gene:campoScheda campo="CODGAR1"  value="${codiceGara}" visibile="false" />
				<c:choose> 
					<c:when test="${gruppo eq 3}" >
						<gene:campoScheda campo="NOME" entita="G1CF_PUBB" campoFittizio="true" definizione="T21" visibile="false" value="${titoloBusta}"/>
					</c:when>
					<c:otherwise>
						<gene:campoScheda campo="NOME" entita="G1CF_PUBB" where="id= ${tipologiaDoc}" visibile="false" />
					</c:otherwise>
				</c:choose>
				<gene:campoScheda campo="NGARA"  visibile="${ngara}" />
				<gene:campoScheda campo="TIPGARG" visibile="false"/>
				<gene:campoScheda campo="TIPGEN" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="false"/>
				<gene:campoScheda campo="ITERGA" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="false"/>
				<gene:campoScheda campo="VALTEC" entita="GARE1" where="GARE.NGARA = GARE1.NGARA" visibile="false"/>
				<gene:campoScheda campo="IMPAPP" visibile="false"/>
				<gene:campoScheda campo="MODLICG" visibile="false"/>
				<gene:campoScheda campo="CRITLICG" visibile="false"/>
				
				<input type="hidden"  name="busta" value="${busta}">
				<input type="hidden"  name="titoloBusta" value="${titoloBusta}">
				<input type="hidden"  name="tipologiaDoc" value="${tipologiaDoc}">
				<input type="hidden"  name="gruppo" value="${gruppo}">
				<input type="hidden"  name="tipoDoc" value="${tipoDoc}">
				<input type="hidden"  name="codiceGara" value="${codiceGara}">
				<input type="hidden"  name="lottoFaseInvito" value="${lottoFaseInvito}">
				<input type="hidden"  name="isProceduraTelematica" value="${isProceduraTelematica}">
				<input type="hidden"  name="idconfi" value="${idconfi}">
				
				<jsp:include page="/WEB-INF/pages/gare/documgara/sezione-documenti.jsp">
					<jsp:param name="codiceGara" value="${codiceGara}"/>
					<jsp:param name="ngara" value="${ngara}"/>
					<jsp:param name="tipoDoc" value="${gruppo}"/>
					<jsp:param name="gruppo" value="${gruppo}"/>
					<jsp:param name="bustalotti" value="${bustalotti}"/>
					<jsp:param name="richiestaFirma" value="${richiestaFirma}"/>
					<jsp:param name="tipologiaDoc" value="${tipologiaDoc}"/>
					<jsp:param name="busta" value="${busta}"/>
					<jsp:param name="titoloBusta" value="${titoloBusta}"/>
					<jsp:param name="lottoFaseInvito" value="${lottoFaseInvito}" />
					<jsp:param name="nomeTipologia" value="${datiRiga.G1CF_PUBB_NOME}" />
					<jsp:param name="isProceduraTelematica" value="${isProceduraTelematica}" />
					<jsp:param name="insDocDaProtocollo" value="${insDocDaProtocollo}" />
					
				</jsp:include>
				
			</gene:formScheda>
		</c:otherwise>
	</c:choose>
	<jsp:include page="/WEB-INF/pages/gene/system/firmadigitale/modalPopupFirmaDigitaleRemota.jsp" />
	
	<form name="formAllegatiRda" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
		<input type="hidden" name="href" value="gare/commons/lista-allegati-rda-scheda.jsp" /> 
		<input type="hidden" name="codice" id="codice" value="" />
		<input type="hidden" name="genere" id="genere" value="" />
	</form> 
	
	<form name="formVisFirmaDigitale" id="formVisFirmaDigitale" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
		<input type="hidden" name="href" value="gene/system/firmadigitale/verifica-firmadigitale.jsp" />
		<input type="hidden" name="idprg" id="idprg" value="" />
		<input type="hidden" name="iddocdig" id="iddocdig" value="" />
	</form>
	
	
	<c:choose>
		<c:when test="${genereGara eq '3' ||  genereGara eq '1'}">
			<c:set var="chiaveGara" value="${codiceGara }"/> 
		</c:when>
		<c:otherwise>
			<c:set var="chiaveGara" value="${ngara }"/> 
		</c:otherwise>
	</c:choose>
	
	<c:set var="ssoProtocollo" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "sso.protocollo")}'/>
	<c:if test="${not empty ssoProtocollo and ssoProtocollo ne '0' }">
		<c:set var="sso" value='true'/>
	</c:if>
	
	<form name="formIndDocProtocollo" id="formIndDocProtocollo" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
		<input type="hidden" name="href" value="gare/documgara/insDocumenti-wsdm.jsp" />
		<input type="hidden" name="idconfi" id="idconfi" value="${idconfi}" />
		<input type="hidden" name="genereGara" value="${genereGara}" />
		<input type="hidden" name="key1" value="${chiaveGara}" />
		<input type="hidden" name="ngara" value="${ngara}" />
		<input type="hidden" name="codiceGara" value="${codiceGara}" />
		<input type="hidden" name="tipologiaDoc" value="${tipologiaDoc}" />
		<input type="hidden" name="gruppo" value="${gruppo}" />
		<input type="hidden" name="sso" value="${sso}" />
	</form>
	
		
</gene:redefineInsert>
<gene:javaScript>

			
		function apriInsDocDaProtocollo(){
			bloccaRichiesteServer();
			document.formIndDocProtocollo.submit();
			
		}		
			

</gene:javaScript>
</gene:template>