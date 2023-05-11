<%
/*
 * Created on: 02-11-2016
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 /*
  * Pagina a scheda relativa allo step 'Invito' del wizard Fasi gara per l'asta elettronica 
  *
  *
  */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<script type="text/javascript" src="${contextPath}/js/jquery.documenti.gara.js"></script>
<script type="text/javascript" src="${contextPath}/js/jquery.storico.rettifica.termini.gara.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.pg.titoli.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.pg.hrefProtocollo.js"></script>	

<c:set var="digitalSignatureUrlCheck" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "digital-signature-check-url")}'/>
<c:set var="digitalSignatureProvider" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "digital-signature-provider")}'/>
<c:choose>
	<c:when test="${!empty digitalSignatureUrlCheck && !empty digitalSignatureProvider && (digitalSignatureProvider eq 1 || digitalSignatureProvider eq 2)}">
		<c:set var="digitalSignatureWsCheck" value='1'/>
	</c:when>
	<c:otherwise>
		<c:set var="digitalSignatureWsCheck" value='0'/>
	</c:otherwise>
</c:choose>

<c:set var="numeroGara" value='${gene:getValCampo(key, "NGARA")}'/>
<c:set var="codiceGara" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetCodiceGaraFunction", pageContext)}' />
<c:set var="integrazioneWSDM" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSDNFunction",  pageContext, codiceGara, idconfi)}'/>	
<c:if test="${integrazioneWSDM eq '1' }">
	<c:set var="protocolloSingoloInvito" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.GetPropertyWsdmFunction", "wsdm.protocolloSingoloInvito", idconfi)}' />
</c:if>

<c:set var="gestioneUrl" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.IsGestioneUrlDocumentazioneFunction", pageContext)}' scope="request"/>
<c:set var="firmaProvider" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "digital-signature-provider")}'/>
<c:if test='${firmaProvider eq 2}'>
	<c:set var="firmaRemota" value="true"/>
</c:if>
<c:set var="gestioneERP" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.GetPropertyWsdmFunction", "wsdm.gestioneERP",idconfi)}'/>
<c:set var="numeroDocumentoWSDM" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.CheckAssociataRdaJIRIDEFunction", pageContext, codiceGara)}' scope="request"/>

<c:set var="richiestaFirma" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "documentiDb.richiestaFirma")}'/>

<c:set var="modelliPredispostiAttivoIncondizionato" value="1" scope="request" />

<c:set var="meruolo" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetMERUOLOFunction",  pageContext, numeroGara)}'/>

	<tr>
		<td ${stileDati} >
				<gene:formScheda entita="GARE" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreFasiGaraAstaElettronica_Invito">
					<gene:redefineInsert name="noteAvvisi" />
					<gene:redefineInsert name="documentiAssociati"/>
					<gene:redefineInsert name="modelliPredisposti" >
						<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GENE.W_MODELLI")}'>
							<tr>
								<c:choose>
					        <c:when test='${modo ne "MODIFICA" and updateLista ne 1}'>
					          <td class="vocemenulaterale">
										  <a href="javascript:modelliPredispostiLocale();" title="Modelli predisposti" tabindex="1510">
											  ${gene:resource("label.tags.template.documenti.modelliPredisposti")}
											</a>
					   				</td>
					        </c:when>
					        <c:otherwise>
					          <td>
											${gene:resource("label.tags.template.documenti.modelliPredisposti")}
									  </td>
					        </c:otherwise>
								</c:choose>
							</tr>
						</c:if>
					</gene:redefineInsert>
					
					<c:choose>
						<c:when test='${(modo eq "MODIFICA" or updateLista eq 1) and bloccoAggiudicazione ne 1}'>
							<gene:redefineInsert name="schedaConferma">
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:conferma();" title="Salva modifiche" tabindex="1501">
											${gene:resource("label.tags.template.dettaglio.schedaConferma")}</a></td>
								</tr>
							</gene:redefineInsert>
							<gene:redefineInsert name="schedaAnnulla">
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:annulla();" title="Annulla modifiche" tabindex="1502">
										${gene:resource("label.tags.template.dettaglio.schedaAnnulla")}</a></td>
								</tr>
							</gene:redefineInsert>
						</c:when>
						<c:otherwise>
							<gene:redefineInsert name="schedaNuovo" />
							<gene:redefineInsert name="schedaModifica" >
								<c:if test='${autorizzatoModifiche ne 2 and bloccoAggiudicazione ne 1 and gene:checkProt(pageContext, strProtModificaFasiRicezione) and datiRiga.GARE_FASGAR eq 6}'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:modificaLista();" title="Modifica dati" tabindex="1501">
											${gene:resource("label.tags.template.dettaglio.schedaModifica")}</a></td>
									</tr>
								</c:if>
								<c:if test='${autorizzatoModifiche ne 2 and datiRiga.GARE_FASGAR eq 6 and (meruolo eq "1" or meruolo eq "3") and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.InviaInvitoAstaElettronica")}'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:apriPopupInviaInvito('${numeroGara}','${codiceGara }','${integrazioneWSDM }','${idconfi}');" title="Invia invito all'asta elettronica" tabindex="1502">
												Invia invito all'asta elettronica
											</a>
										</td>
									</tr>
								</c:if>
								<c:if test='${autorizzatoModifiche ne "2" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.ImpostaGaraNonAggiud") and param.paginaFasiGara eq "aperturaOffAggProvLottoOffUnica"}'>
								      <td class="vocemenulaterale">
									      	<c:if test='${isNavigazioneDisattiva ne "1" }'>
													<a href="javascript:impostaGaraNonAggiudicata('${datiRiga.GARE_NGARA}','${datiRiga.GARE_CODGAR1}','${datiRiga.GARE_ESINEG}','${datiRiga.GARE_DATNEG}','${datiRiga.GARE1_NPANNREVAGG}');" title="Imposta lotto non aggiudicato" tabindex="1507">
											</c:if>
											  Imposta lotto non aggiudicato
											<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>			  
										</td>
								</c:if>
								<c:if test='${numDocAttesaFirma > 0 and modo eq "VISUALIZZA"}'>
									<tr>
										<td class="vocemenulaterale" >
											<c:if test='${isNavigazioneDisattiva ne "1"}'>
												<a href="javascript:historyReload();" title='Rileggi dati' tabindex="1505">
											</c:if>
												Rileggi dati
											<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
										</td>
									</tr>
								</c:if>	
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:indietro();" title='Fase precedente' tabindex="1503">
											< Fase precedente
										</a>
									</td>
								</tr>
								<c:if test="${stepgar > 65 }">
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:avanti();" title='Fase seguente' tabindex="1504">
												Fase seguente >
											</a>
										</td>
									</tr>
								</c:if>
							</gene:redefineInsert>
						</c:otherwise>
					</c:choose>				
					<gene:redefineInsert name="addToAzioni">
					</gene:redefineInsert>
					
					
					<gene:campoScheda campo="WIZARD_PAGINA_ATTIVA" visibile="false" campoFittizio="true" definizione="N1" value="${paginaAttivaWizard}" />
					
					<gene:campoScheda campo="NGARA" visibile="false" />
					<gene:campoScheda campo="CODGAR1" visibile="false" />
					<gene:campoScheda campo="FASGAR" visibile="false" />
					<gene:campoScheda campo="ESINEG" visibile="false" />
					<gene:campoScheda campo="DATNEG" visibile="false" />
					<gene:campoScheda campo="NPANNREVAGG" entita="GARE1" where="GARE1.NGARA = GARE.NGARA" visibile="false" />
					
					<gene:gruppoCampi idProtezioni="INVITO">
						<gene:campoScheda>
							<td colspan="2"><br><br></td>
						</gene:campoScheda>
						<gene:campoScheda>
							<td colspan="2"><b>Estremi invito</b></td>
						</gene:campoScheda>
						<gene:campoScheda campo="AEDINVIT" entita="GARE1" where="GARE1.NGARA = GARE.NGARA"  modificabile="false"/>
						<gene:campoScheda campo="AENPROTI" entita="GARE1" where="GARE1.NGARA = GARE.NGARA"  visibile="${protocolloSingoloInvito ne '1' }"/>
					</gene:gruppoCampi>
					
					<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
						<jsp:param name="entita" value='AEFASI'/>
						<jsp:param name="chiave" value='${numeroGara}'/>
						<jsp:param name="nomeAttributoLista" value='faseAsta' />
						<jsp:param name="idProtezioni" value="AEFASI" />
						<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/aefasi/fasi-asta.jsp"/>
						<jsp:param name="arrayCampi" value="'AEFASI_NGARA_', 'AEFASI_ID_', 'AEFASI_DATINI_','AEFASI_ORAINI_', 'AEFASI_DURMIN_', 'AEFASI_DURMAX_', 'AEFASI_TBASE_','AEFASI_DATAORAINI_','AEFASI_DATAORAFINE_'"/>		
						<jsp:param name="titoloSezione" value="Fase di asta elettronica" />
						<jsp:param name="titoloNuovaSezione" value="Nuova fase di asta elettronica" />
						<jsp:param name="descEntitaVociLink" value="fase di asta elettronica" />
						<jsp:param name="msgRaggiuntoMax" value="e fasi di asta elettronica"/>
						<jsp:param name="usaContatoreLista" value="true"/>
					</jsp:include>
					
					
					<fmt:parseNumber var="tipoDoc" type="number" value="12"/> 
					<c:set var="tmp" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.GestioneDocumentazioneFunction", pageContext,codiceGara,numeroGara,tipoDoc,"")}' />
					
					<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
						<jsp:param name="entita" value='DOCUMGARA'/>
						<jsp:param name="chiave" value='${codiceGara};${numeroGara}'/>
						<jsp:param name="nomeAttributoLista" value='documentiInvito' />
						<jsp:param name="idProtezioni" value="DOCUMINVITI" />
						<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/documgara/documenti-invitoAsta.jsp"/>
						<jsp:param name="arrayCampi" value="'DOCUMGARA_CODGAR_', 'DOCUMGARA_NGARA_', 'DOCUMGARA_NORDDOCG_','DOCUMGARA_GRUPPO_', 'DOCUMGARA_FASGAR_', 'DOCUMGARA_IDPRG_', 'DOCUMGARA_IDDOCDG_','DOCUMGARA_DESCRIZIONE_','DOCUMGARA_STATODOC_','DOCUMGARA_VALENZA_','W_DOCDIG_IDPRG_','W_DOCDIG_IDDOCDIG_','W_DOCDIG_DIGDESDOC_','W_DOCDIG_DIGNOMDOC_','selezioneFile_','DOCUMGARA_IDSTAMPA','NOMEDOCGEN_','DOCUMGARA_ALLMAIL_','DOCUMGARA_URLDOC_','tipoAllegato_','W_DOCDIG_DIGFIRMA_'"/>		
						<jsp:param name="titoloSezione" value="Documento allegato all'invito" />
						<jsp:param name="titoloNuovaSezione" value="Nuovo documento allegato all'invito" />
						<jsp:param name="descEntitaVociLink" value="documento allegato all'invito" />
						<jsp:param name="msgRaggiuntoMax" value="i documenti allegati all'invito"/>
						<jsp:param name="usaContatoreLista" value="true"/>
						<jsp:param name="sezioneListaVuota" value="true"/>
						<jsp:param name="richiestaFirma" value="${richiestaFirma}"/>
						<jsp:param name="firmaRemota" value="${firmaRemota}"/>
						<jsp:param name="autorizzatoModifiche" value="${autorizzatoModifiche}"/>
						<jsp:param name="numeroDocumentoWSDM" value="${numeroDocumentoWSDM}"/>
						<jsp:param name="gestioneERP" value="${gestioneERP}"/>
					</jsp:include>			
					
					<input type="hidden" name="isGaraLottiConOffertaUnica" id="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
					<input type="hidden" name="DIREZIONE_WIZARD" id="DIREZIONE_WIZARD" value="" />
					<input type="hidden" id="pgVaiA" name="pgVaiA" value="0" />
					<input type="hidden" id="pgSort" name="pgSort" value="" />
					<input type="hidden" id="pgLastSort" name="pgLastSort" value="" />
					<input type="hidden" id="pgLastValori" name="pgLastValori" value="" />
					<input type="hidden" id="ditteVincitrici_escluseDaAltriLotti" name="ditteVincitrici_escluseDaAltriLotti" value="" />
					<input type="hidden" id="pgAsta" name="pgAsta" value="${pgAsta }" />
					<input type="hidden" id="filtroDocumentazione" name="filtroDocumentazione" value="${tipoDoc }" />
					<input type="hidden" name="WIZARD_PAGINA_ATTIVA" value="${paginaAttivaWizard}" />						
					<input type="hidden" id="updateLista" name="updateLista" value="${updateLista}" />
					<input type="hidden" name="entitaPrincipaleModificabile" id="entitaPrincipaleModificabile" value="${sessionScope.entitaPrincipaleModificabile}" />
				</gene:formScheda>
				
				<jsp:include page="/WEB-INF/pages/gene/system/firmadigitale/modalPopupFirmaDigitaleRemota.jsp" />
				
				<form name="formVisFirmaDigitale" id="formVisFirmaDigitale" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
					<input type="hidden" name="href" value="gene/system/firmadigitale/verifica-firmadigitale.jsp" />
					<input type="hidden" name="idprg" id="idprg" value="" />
					<input type="hidden" name="iddocdig" id="iddocdig" value="" />
				</form>

			</td>
		</tr>
<gene:javaScript>

function visualizzaFileAllegatoCustom(idprg,iddocdig,dignomdoc) {
	var vet = dignomdoc.split(".");
	var ext = vet[vet.length-1];
	ext = ext.toUpperCase();
	<c:choose>
		<c:when test="${digitalSignatureWsCheck eq 0}">
			if(ext=='P7M' || ext=='TSD'){
				document.formVisFirmaDigitale.idprg.value = idprg;
				document.formVisFirmaDigitale.iddocdig.value = iddocdig;
				document.formVisFirmaDigitale.submit();
			}else{
				var href = "${pageContext.request.contextPath}/pg/VisualizzaFileAllegato.do";
				document.location.href=href+"?"+csrfToken+"&idprg=" + idprg + "&iddocdig=" + iddocdig + "&dignomdoc=" + encodeURIComponent(dignomdoc);
			}
		</c:when>
		<c:otherwise>
			if(ext=='P7M' || ext=='TSD' || ext=='XML' || ext=='PDF'){
				document.formVisFirmaDigitale.idprg.value = idprg;
				document.formVisFirmaDigitale.iddocdig.value = iddocdig;
				document.formVisFirmaDigitale.submit();
			}else{
				var href = "${pageContext.request.contextPath}/pg/VisualizzaFileAllegato.do";
				document.location.href=href+"?"+csrfToken+"&idprg=" + idprg + "&iddocdig=" + iddocdig + "&dignomdoc=" + encodeURIComponent(dignomdoc);
			}
		</c:otherwise>
	</c:choose>
}
var visualizzaFileAllegato = visualizzaFileAllegatoCustom;
		
function validURL(str) {
	if(str==""){
		return true;
	}else{
		var res = /^(((http|HTTP|https|HTTPS|ftp|FPT|ftps|FTPS|sftp|SFTP):\/\/)|((w|W){3}(\d)?\.))[\w\?!\./:;\-_=#+*%@&quot;\(\)&amp;]+/.test(str);
		return res;
	}
}
		
</gene:javaScript>
		