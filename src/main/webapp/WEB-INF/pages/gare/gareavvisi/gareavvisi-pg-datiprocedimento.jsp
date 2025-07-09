<%
	/*
	 * Created on: 28/10/2008
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

<jsp:include page="/WEB-INF/pages/commons/defCostantiAppalti.jsp" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>

<gene:redefineInsert name="head">
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.pg.hrefProtocollo.js"></script>
</gene:redefineInsert>

<c:set var="codiceGara" value="${gene:concat('$',gene:getValCampo(key, 'NGARA'))}"/>

<c:set var="integrazioneWSDM" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSDNFunction", pageContext, codiceGara, idconfi)}' />
<c:set var="integrazioneWSDMDocumentale" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSDNDocumentaleFunction", pageContext, codiceGara,idconfi)}' />
<c:if test='${integrazioneWSDM eq "1" or integrazioneWSDMDocumentale eq "1"}'>
	<c:set var="isFascicoloDocumentaleAbilitato" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.IsApplicaFascicolazioneValidaFunction",  pageContext, codiceGara, idconfi)}' />
</c:if>

<gene:formScheda entita="GAREAVVISI" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreAvvisi">

	<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-scheda.jsp">
		<jsp:param name="entita" value="GAREAVVISI"/>
		<jsp:param name="inputFiltro" value="${requestScope.inputFiltro}"/>
		<jsp:param name="filtroCampoEntita" value="CODGAR='${datiRiga.TORN_CODGAR}'"/>
	</jsp:include>

	<gene:redefineInsert name="pulsanteNuovo"></gene:redefineInsert>
	<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
	<gene:redefineInsert name="documentiAssociati"></gene:redefineInsert>
	<gene:redefineInsert name="noteAvvisi"></gene:redefineInsert>

	<gene:campoScheda campo="CODGAR" visibile="false" />
	<gene:campoScheda campo="NGARA" visibile="false"/>
	<gene:campoScheda campo="CODGAR"  entita="TORN" where="GAREAVVISI.CODGAR=TORN.CODGAR" visibile="false"/>

	<c:if test="${isFascicoloDocumentaleAbilitato eq '1' }">
		<c:set var="tipoWSDM" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetTipoWSDMFunction", pageContext, "FASCICOLOPROTOCOLLO", "NO",idconfi)}' />
		<gene:gruppoCampi idProtezioni="ISPGD">
			<gene:campoScheda>
				<td colspan="2"><b>Integrazione con sistema di protocollazione e gestione documentale</b></td>
			</gene:campoScheda>
			<c:if test="${!empty datiRiga.WSFASCICOLO_CODICE and datiRiga.WSFASCICOLO_CODICE !=''}">
				<c:set var="rifFascicolo" value="Cod.: ${datiRiga.WSFASCICOLO_CODICE}"/>
			</c:if>
			<c:if test="${!empty datiRiga.WSFASCICOLO_ANNO and datiRiga.WSFASCICOLO_ANNO !=''}">
				<c:if test="${!empty rifFascicolo && rifFascicolo !=''}">
					<c:set var="rifFascicolo" value="${rifFascicolo } -"/>
				</c:if>
				<c:set var="rifFascicolo" value="${rifFascicolo } Anno: ${datiRiga.WSFASCICOLO_ANNO}"/>
			</c:if>
			<c:if test="${!empty datiRiga.WSFASCICOLO_NUMERO and datiRiga.WSFASCICOLO_NUMERO !=''}">
				<c:if test="${!empty rifFascicolo && rifFascicolo !=''}">
					<c:set var="rifFascicolo" value="${rifFascicolo } -"/>
				</c:if>
				<c:set var="rifFascicolo" value="${rifFascicolo } Num.: ${datiRiga.WSFASCICOLO_NUMERO}"/>
			</c:if>
			<c:if test="${!empty datiRiga.WSFASCICOLO_CLASSIFICA and datiRiga.WSFASCICOLO_CLASSIFICA !='' && tipoWSDM ne 'JIRIDE'}">
				<c:if test="${!empty rifFascicolo && rifFascicolo !=''}">
					<c:set var="rifFascicolo" value="${rifFascicolo } -"/>
				</c:if>
				<c:set var="rifFascicolo" value="${rifFascicolo } Classifica: ${datiRiga.WSFASCICOLO_CLASSIFICA}"/>
			</c:if>
			<c:if test="${tipoWSDM eq 'TITULUS' || tipoWSDM eq 'ENGINEERINGDOC'}">
				<c:choose>
					<c:when test="${tipoWSDM eq 'ENGINEERINGDOC'}">
						<c:set var="labelCoduff" value="U.O. di competenza"/>
					</c:when>
					<c:otherwise>
						<c:set var="labelCoduff" value="Ufficio"/>
					</c:otherwise>
				</c:choose>
				<c:if test="${!empty datiRiga.WSFASCICOLO_CODAOO and datiRiga.WSFASCICOLO_CODAOO !='' && tipoWSDM eq 'TITULUS'}" >
					<c:set var="rifFascicolo" value="${rifFascicolo } - AOO: ${datiRiga.WSFASCICOLO_CODAOO}"/>
					<c:if test="${!empty datiRiga.WSFASCICOLO_DESAOO and datiRiga.WSFASCICOLO_DESAOO !=''}" >
						<c:set var="rifFascicolo" value="${rifFascicolo } ${datiRiga.WSFASCICOLO_DESAOO}"/>
					</c:if>
				</c:if>
				<c:if test="${!empty datiRiga.WSFASCICOLO_CODUFF and datiRiga.WSFASCICOLO_CODUFF !=''}" >
					<c:set var="rifFascicolo" value="${rifFascicolo } - ${labelCoduff}: ${datiRiga.WSFASCICOLO_CODUFF}"/>
					<c:if test="${!empty datiRiga.WSFASCICOLO_DESUFF and datiRiga.WSFASCICOLO_DESUFF !=''}" >
						<c:set var="rifFascicolo" value="${rifFascicolo } ${datiRiga.WSFASCICOLO_DESUFF}"/>
					</c:if>
				</c:if>
			</c:if>
			<gene:campoScheda campo="ISPGD_FIT" campoFittizio="true" title="Riferimento al fascicolo" definizione="T40;"  value="${rifFascicolo}" modificabile="false"/>
			<gene:campoScheda campo="ISCHIUSO" entita="WSFASCICOLO" where="WSFASCICOLO.KEY1=GAREAVVISI.NGARA and WSFASCICOLO.ENTITA='GARE'" visibile="${datiRiga.WSFASCICOLO_ISCHIUSO eq '1'}" modificabile="false"/>
			<gene:campoScheda campo="CODICE" entita="WSFASCICOLO" where="WSFASCICOLO.KEY1=GAREAVVISI.NGARA and WSFASCICOLO.ENTITA='GARE'" visibile="false"/>
			<gene:campoScheda campo="ANNO" entita="WSFASCICOLO" where="WSFASCICOLO.KEY1=GAREAVVISI.NGARA and WSFASCICOLO.ENTITA='GARE'" visibile="false"/>
			<gene:campoScheda campo="NUMERO" entita="WSFASCICOLO" where="WSFASCICOLO.KEY1=GAREAVVISI.NGARA and WSFASCICOLO.ENTITA='GARE'" visibile="false"/>
			<gene:campoScheda campo="CLASSIFICA" entita="WSFASCICOLO" where="WSFASCICOLO.KEY1=GAREAVVISI.NGARA and WSFASCICOLO.ENTITA='GARE'" visibile="false"/>

			<gene:campoScheda campo="CODAOO" entita="WSFASCICOLO" where="WSFASCICOLO.KEY1=GAREAVVISI.NGARA and WSFASCICOLO.ENTITA='GARE'" visibile="false"/>
			<gene:campoScheda campo="DESAOO" entita="WSFASCICOLO" where="WSFASCICOLO.KEY1=GAREAVVISI.NGARA and WSFASCICOLO.ENTITA='GARE'" visibile="false"/>
			<gene:campoScheda campo="CODUFF" entita="WSFASCICOLO" where="WSFASCICOLO.KEY1=GAREAVVISI.NGARA and WSFASCICOLO.ENTITA='GARE'" visibile="false"/>
			<gene:campoScheda campo="DESUFF" entita="WSFASCICOLO" where="WSFASCICOLO.KEY1=GAREAVVISI.NGARA and WSFASCICOLO.ENTITA='GARE'" visibile="false"/>

		</gene:gruppoCampi>
	</c:if>

	<c:set var="configurazioneNotificheComunicazioni" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "comunicazioni.notifiche.idcfg")}'/>
	<c:if test="${!empty configurazioneNotificheComunicazioni}">
		<jsp:include page="/WEB-INF/pages/geneweb/w_invcom/modaleModificaIndirizziNotifiche.jsp">
			<jsp:param value="${ codiceGara }" name="codiceGara"/>
		</jsp:include>
		<gene:gruppoCampi idProtezioni="NOTIFICHECOMUNICAZIONI">
			<gene:campoScheda>
				<td colspan="2" id="placeHolderModaleNotifiche"><b>Notifica comunicazioni ricevute</b></td>
			</gene:campoScheda>
			<gene:campoScheda campo="INDNOTIFICHE" entita="TORN" where="GAREAVVISI.CODGAR=TORN.CODGAR" modificabile="true" speciale="true">
				<gene:popupCampo titolo="Selezione destinatari" href="apriModaleModificaIndirizziNotifiche();" />
			</gene:campoScheda>
		</gene:gruppoCampi>
	</c:if>

	<gene:gruppoCampi idProtezioni="URLPUBBPROC" visibile="true">
		<c:set var="urlPubbProcedura" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.ComponiUrlPubblicazioneProceduraFunction", pageContext, gene:getValCampo(key, "NGARA"))}'/>
		<gene:campoScheda>
			<td colspan="2"><b>URL pubblicazione portale Appalti</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="URLPUBBPROC" campoFittizio="true" title="URL di pubblicazione" definizione="T200;" href="javascript:apriUrl('${urlPubbProcedura}')" value="${urlPubbProcedura}" modificabile="false" >
			<a title="Copia link" href="#" onclick="copyToClipboard(event, '${urlPubbProcedura}', this)"><img src="${pageContext.request.contextPath}/img/copiaLink.svg" width="16" height="16" style="margin-left: 5px" alt="Copia link"/></a>
			<span class="tooltiptext" style="background-color: #0e3671;color: white;padding: 1px 4px;border-radius: 10px;visibility: hidden" id="tooltip">Copiato negli appunti</span>
		</gene:campoScheda>
	</gene:gruppoCampi>

	<c:if test="${not empty verNPA}">
		<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GestionePubblicazioniBandoFunction" parametro='${codiceGara}' />
		<gene:gruppoCampi idProtezioni="PUBBLICAZIONI" >
			<c:forEach var="pubblicazioneBando" items="${pubblicazioniBando}" varStatus="loop">
				<gene:campoScheda>
					<td colspan="2"><b>Pubblicazione bando ${loop.count}</b></td>
				</gene:campoScheda>
				<gene:campoScheda campo="TIPPUB_${loop.count}" entita="PUBBLI" modificabile="false" campoFittizio="true" definizione="N7;;A1008;;G1TIPPUB"
								  value="${pubblicazioneBando[2]}"/>
				<gene:campoScheda campo="DINPUB_${loop.count}" entita="PUBBLI" modificabile="false" campoFittizio="true" definizione="D;;;;DINPUB"
								  value="${pubblicazioneBando[5]}" visibile="${pubblicazioneBando[2] eq '3' or pubblicazioneBando[2] eq '17'}"/>
				<gene:campoScheda campo="DATPUB_${loop.count}" entita="PUBBLI" modificabile="false" campoFittizio="true" definizione="D;;;;DATPUB"
								  value="${pubblicazioneBando[6]}"/>
				<gene:campoScheda campo="TITPUB_${loop.count}" entita="PUBBLI" modificabile="false" campoFittizio="true" definizione="T254;;;;G1TITPUB"
								  value="${pubblicazioneBando[10]}" visibile="${pubblicazioneBando[2] eq '3' or pubblicazioneBando[2] eq '17'}"/>
				<gene:campoScheda campo="NAVPUB_${loop.count}" entita="PUBBLI" modificabile="false" campoFittizio="true" definizione="T60;;;;G1NAVPUB"
								  value="${pubblicazioneBando[11]}" visibile="${pubblicazioneBando[2] eq '3'}"/>
				<gene:campoScheda campo="URLPUB_${loop.count}" entita="PUBBLI" modificabile="false" campoFittizio="true" definizione="T254;;;;G1URLPUB"
								  value="${pubblicazioneBando[13]}" visibile="${pubblicazioneBando[2] eq '3' or pubblicazioneBando[2] eq '17'}" href="javascript:apriUrl('${pubblicazioneBando[13]}')">
					<a title="Copia link" href="#" onclick="copyToClipboard(event, '${pubblicazioneBando[13]}', this)"><img src="${pageContext.request.contextPath}/img/copiaLink.svg" width="16" height="16" style="margin-left: 5px" alt="Copia link"/></a>
					<span class="tooltiptext" style="background-color: #0e3671;color: white;padding: 1px 4px;border-radius: 10px;visibility: hidden" id="tooltip">Copiato negli appunti</span>
				</gene:campoScheda>
				<gene:campoScheda campo="TESPUB_${loop.count}" entita="PUBBLI" modificabile="false" campoFittizio="true" definizione="T254;;;;TESPUB"
								  value="${pubblicazioneBando[3]}" visibile="${pubblicazioneBando[3] ne ''}"/>
			</c:forEach>

			<c:choose>
				<c:when test='${genereGara eq "3"}'>
					<c:set var="numeroGara" value='${gene:getValCampo(key, "CODGAR")}'/>
				</c:when>
				<c:when test='${genereGara eq "1"}'>
					<c:set var="numeroGara" value='${gene:getValCampo(key, "CODGAR")}'/>
				</c:when>
				<c:otherwise>
					<c:set var="numeroGara" value='${gene:getValCampo(key, "NGARA")}'/>
				</c:otherwise>
			</c:choose>

			<c:set var="tmp" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GestionePubblicazioniEsitoFunction", pageContext,numeroGara,genereGara)}' />

			<c:forEach var="pubblicazioneEsito" items="${pubblicazioniEsito}" varStatus="loop">
				<gene:campoScheda>
					<td colspan="2"><b>Pubblicazione esito ${loop.count}</b></td>
				</gene:campoScheda>

				<gene:campoScheda campo="TIPPUBG_${loop.count}" entita="PUBG" modificabile="false" campoFittizio="true" definizione="N7;;A1008;;TIPPUBG"
								  value="${pubblicazioneEsito[2]}" />
				<gene:campoScheda campo="DINVPUBG_${loop.count}" entita="PUBG" modificabile="false" campoFittizio="true" definizione="D;0;;;DINVPUBG"
								  value="${pubblicazioneEsito[7]}" visibile="${pubblicazioneEsito[2] eq '3' or pubblicazioneEsito[2] eq '17'}"/>
				<gene:campoScheda campo="DINPUBG_${loop.count}" entita="PUBG" modificabile="false" campoFittizio="true" definizione="D;;;;DINPUBG"
								  value="${pubblicazioneEsito[3]}" visibile="${pubblicazioneEsito[2] eq '3' or pubblicazioneEsito[2] eq '12' or pubblicazioneEsito[2] eq '17'}"/>
				<gene:campoScheda campo="TITPUB_${loop.count}" entita="PUBG" modificabile="false" campoFittizio="true" definizione="T254;;;;G1TITPUBG"
								  value="${pubblicazioneEsito[9]}" visibile="${pubblicazioneEsito[2] eq '3' or pubblicazioneEsito[2] eq '17'}"/>
				<gene:campoScheda campo="NAVPUB_${loop.count}" entita="PUBG" modificabile="false" campoFittizio="true" definizione="T60;;;;G1NAVPUBG"
								  value="${pubblicazioneEsito[10]}" visibile="${pubblicazioneEsito[2] eq '3'}"/>
				<gene:campoScheda campo="URLPUB_${loop.count}" entita="PUBG" modificabile="false" campoFittizio="true" definizione="T254;;;;G1URLPUBG"
								  value="${pubblicazioneEsito[12]}" visibile="${pubblicazioneEsito[2] eq '3' or pubblicazioneEsito[2] eq '17'}" href="javascript:apriUrl('${pubblicazioneEsito[12]}')">
					<a title="Copia link" href="#" onclick="copyToClipboard(event, '${pubblicazioneEsito[12]}', this)"><img src="${pageContext.request.contextPath}/img/copiaLink.svg" width="16" height="16" style="margin-left: 5px" alt="Copia link"/></a>
					<span class="tooltiptext" style="background-color: #0e3671;color: white;padding: 1px 4px;border-radius: 10px;visibility: hidden" id="tooltip">Copiato negli appunti</span>
				</gene:campoScheda>
			</c:forEach>
		</gene:gruppoCampi>
	</c:if>

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
				<gene:redefineInsert name="addToAzioni">
					<c:if test='${bloccoModificatiDati and gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD") and autorizzatoModifiche ne "2"}' >
						<tr>
						<td class="vocemenulaterale" >
							<a href="javascript:schedaModifica();" title="Modifica">
								Modifica</a>
						</td>
						</tr>
					</c:if>
				</gene:redefineInsert>
			</c:otherwise>
		</c:choose>
		&nbsp;
		</td>
	</gene:campoScheda>

</gene:formScheda>


<c:if test="${tipoWSDM eq 'PALEO' && modo eq 'VISUALIZZA'}">
	<div style="display: none">
		<jsp:include page="/WEB-INF/pages/gare/wsdm/wsdm-fascicolo.jsp">
			<jsp:param name="servizio" value="FASCICOLOPROTOCOLLO"/>
			<jsp:param name="idconfi" value="${idconfi}"/>
			<jsp:param name="entita" value="GARE"/>
			<jsp:param name="key1" value="${gene:getValCampo(key, 'NGARA')}"/>
			<jsp:param name="genereGara" value="11"/>
			<jsp:param name="skipGetWSDMFascicolo" value="true"/>
		</jsp:include>
		<gene:redefineInsert name="addToAzioni">
			<c:if test="${autorizzatoModifiche ne '2' and gene:checkProt(pageContext,'FUNZ.VIS.ALT.GARE.ChiudiFascicoloWSDM')}">
				<tr id="wsdmChiudiFascicolo">
					<td class="vocemenulaterale">
						<a title="Chiudi fascicolo" tabindex="1514" href="#">Chiudi fascicolo</a>
					</td>
				</tr>
			</c:if>
		</gene:redefineInsert>
	</div>
</c:if>


<gene:javaScript>

	function apriUrl(urlDocumento){
		if(urlDocumento.indexOf("http://")<0 && urlDocumento.indexOf("https://")<0)
		urlDocumento = "http://" + urlDocumento;
		window.open(urlDocumento,"url_documento");
	}

</gene:javaScript>