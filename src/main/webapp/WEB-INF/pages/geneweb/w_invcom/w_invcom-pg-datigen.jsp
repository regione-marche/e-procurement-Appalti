
<%
	/*
	 * Created on 04-Giu-2010
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

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<jsp:include page="/WEB-INF/pages/commons/defCostantiAppalti.jsp" />
<gene:redefineInsert name="head">
	<script type="text/javascript" src="${contextPath}/js/jHtmlArea-0.7.5.min.js"></script> 
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.validate.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wsdmsupporto.js?v=${sessionScope.versioneModuloAttivo}"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.trasmettioperatori.js"></script>
	<link rel="Stylesheet" type="text/css" href="${contextPath}/css/jquery/jHtmlArea/jHtmlArea.css" />
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/wsdm/jquery.wsdm.css" >
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/dataTable/dataTable/jquery.dataTables.css" >
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/wsdm/jquery.wsd.trasmettiOperatori.css" >
	
</gene:redefineInsert>

<c:choose>
	<c:when test='${not empty param.keyAdd}'>
		<c:set var="keyAdd" value="${param.keyAdd}"  />
	</c:when>
	<c:otherwise>
		<c:set var="keyAdd" value="${keyAdd}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.chiaveWSDM}'>
		<c:set var="chiaveWSDM" value="${param.chiaveWSDM}"  />
	</c:when>
	<c:otherwise>
		<c:set var="chiaveWSDM" value="${chiaveWSDM}" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${not empty param.entitaWSDM}'>
		<c:set var="entitaWSDM" value="${param.entitaWSDM}"  />
	</c:when>
	<c:otherwise>
		<c:set var="entitaWSDM" value="${entitaWSDM}" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${not empty param.idconfi}'>
		<c:set var="idconfi" value="${param.idconfi}"  />
	</c:when>
	<c:otherwise>
		<c:set var="idconfi" value="${idconfi}" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${not empty param.keyParentComunicazioni}'>
		<c:set var="keyParentComunicazioni" value="${param.keyParentComunicazioni}" scope="session"  />
	</c:when>
	<c:otherwise>
		<c:set var="keyParentComunicazioni" value="${keyParentComunicazioni}" scope="session" />
	</c:otherwise>
</c:choose>
<c:if test="${empty entitaParent}">
	<c:set var="entitaParent" value='${fn:substringBefore(keyParentComunicazioni,".")}' scope="request" />
</c:if>

<c:set var="isNavigazioneDisattiva" value="${isNavigazioneDisabilitata}" />
<c:set var="ngara" value='${gene:getValCampo(keyAdd, "COMKEY1")}'/>
<c:set var="idprg" value='${gene:getValCampo(key,"IDPRG")}'/>
<c:set var="idcom" value='${gene:getValCampo(key,"IDCOM")}'/>
<c:set var="codgar" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetCodgar1Function", pageContext, ngara)}'/>
<c:set var="integrazioneWSDM" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSDNFunction", pageContext, codgar, idconfi)}'/>
<c:set var="abilitatoInvioMailDocumentale" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.AbilitatoInvioMailDocumentaleFunction", pageContext, idconfi)}'/>
<c:set var="invioDesCC" value='${!(abilitatoInvioMailDocumentale && integrazioneWSDM eq "1")}'/>

<c:if test="${integrazioneWSDM eq 1 }" >
	<c:set var="tipoWSDM" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetTipoWSDMFunction", pageContext, "FASCICOLOPROTOCOLLO", "NO",idconfi)}' />
</c:if>
<c:if test="${integrazioneWSDM eq 1 and modo ne 'MODIFICA' and modo ne 'NUOVO' }" >
	<c:set var="mailDocumentale" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.GetPropertyWsdmFunction", mailCaricoDocumentale, idconfi)}'/>
	<c:if test="${mailDocumentale eq '1' }">
		
		<c:if test="${tipoWSDM eq 'JIRIDE' or  tipoWSDM eq 'PALEO' or tipoWSDM eq 'ARCHIFLOW' or tipoWSDM eq 'ARCHIFLOWFA' or tipoWSDM eq 'PRISMA' or tipoWSDM eq 'JPROTOCOL' or tipoWSDM eq 'ITALPROT'}">
			<c:set var="wsdmMailDocumentale" value='true'/>
		</c:if>
	</c:if>
	<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.EsistonoAllegatiComunicazioneFunction" parametro="${idprg};${idcom}"/>
</c:if>

<c:set var="htmlSupport" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "comunicazione.supportoHtml")}'/>

<c:choose>
	<c:when test='${not empty param.ditta}'>
		<c:set var="ditta" value="${param.ditta}"  />
	</c:when>
	<c:otherwise>
		<c:set var="ditta" value="${ditta}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.stepWizard}'>
		<c:set var="stepWizard" value="${param.stepWizard}"  />
	</c:when>
	<c:otherwise>
		<c:set var="stepWizard" value="${stepWizard}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.tipo}'>
		<c:set var="tipo" value="${param.tipo}"  />
	</c:when>
	<c:otherwise>
		<c:set var="tipo" value="${tipo}" />
	</c:otherwise>
</c:choose>


<c:choose>
	<c:when test='${modo eq "NUOVO"}'>
		<c:set var="comstato" value='1' />
		<c:set var="autorizzatoModificaComunicazione" value='true' />
	</c:when>
	<c:otherwise>
		<c:set var="comstato" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetCOMSTATOFunction",pageContext,idprg,idcom)}' />	
		<c:set var="autorizzatoModificaComunicazione" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.AutorizzatoModificaComunicazioneFunction",pageContext,idprg,idcom,"true")}' />
		 
	</c:otherwise>
</c:choose>

<gene:formScheda entita="W_INVCOM" gestisciProtezioni="true"
	gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreW_INVCOM" 
	plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreInizializzazioniW_INVCOM">

	<gene:campoScheda campo="IDPRG" visibile="false" defaultValue='${sessionScope.moduloAttivo}'/>
	<gene:campoScheda campo="IDCFG" visibile="false" value='${sessionScope.uffint}'/>
	
	<gene:campoScheda campo="IDCOM" visibile="false" />
	<gene:campoScheda campo="COMENT" visibile="false" defaultValue="${entitaParent}" />
	<gene:campoScheda campo="COMKEY1" visibile="false" defaultValue='${gene:getValCampo(keyAdd, "COMKEY1")}'/>
	<gene:campoScheda campo="COMKEY2" visibile="false" defaultValue='${gene:getValCampo(keyAdd, "COMKEY2")}'/>
	<gene:campoScheda campo="COMKEY3" visibile="false" defaultValue='${gene:getValCampo(keyAdd, "COMKEY3")}'/>
	<gene:campoScheda campo="COMKEY4" visibile="false" defaultValue='${gene:getValCampo(keyAdd, "COMKEY4")}'/>
	<gene:campoScheda campo="COMKEY5" visibile="false" defaultValue='${gene:getValCampo(keyAdd, "COMKEY5")}'/>
	<gene:campoScheda campo="COMTIPO" visibile="false" />
	<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>
	<c:choose>
		<c:when test='${fn:contains(listaOpzioniDisponibili, "OP114#")}' >
			<gene:campoScheda campo="COMPUB" defaultValue="2" visibile="true" obbligatorio="true" modificabile="${(empty requestScope.initCOMMODELLO and empty datiRiga.W_INVCOM_COMMODELLO) and entitaParent ne 'G1STIPULA' }"/>
		</c:when>
		<c:otherwise>
			<gene:campoScheda campo="COMPUB" defaultValue="" visibile="false"/>
		</c:otherwise>
	</c:choose>
	<gene:campoScheda campo="COMMODELLO" defaultValue="${requestScope.initCOMMODELLO}" visibile="${not empty requestScope.initCOMMODELLO or  !empty datiRiga.W_INVCOM_COMMODELLO}" modificabile="false" />
	<gene:campoScheda campo="COMSTATO" defaultValue="1" modificabile="false">
	</gene:campoScheda>	
	<gene:campoScheda campo="COMDATINS" definizione="D;0;;TIMESTAMP;COMDATINS" visibile='${modo ne "NUOVO"}' modificabile="false"/>
	<gene:campoScheda campo="COMDATAPUB" definizione="D;0;;TIMESTAMP;COMDATPUB" visibile="${compub eq '1' and (comstato eq '3' or comstato eq '12')}" modificabile="false"/>
	<gene:campoScheda campo="COMMSGOGG" obbligatorio="true" defaultValue="${requestScope.initCOMMSGOGG}"/>
	<gene:campoScheda campo="COMINTEST" defaultValue='${gene:if(requestScope.initCOMINTEST ne "", requestScope.initCOMINTEST, "2")}'/>
	<gene:campoScheda> 
		<td class="etichetta-dato">Intestazione variabile</td>
		<td class="valore-dato">Spett.le <i>Ragione Sociale</i></td>
	</gene:campoScheda>
	<gene:campoScheda campo="COMMSGTIP" defaultValue="2" modificabile='${modo eq "NUOVO" && tipoWSDM ne "TITULUS"}' visibile='${htmlSupport eq "1"}' />
	<gene:campoScheda campo="COMMSGTES" defaultValue="${requestScope.initCOMMSGTES}" gestore="it.eldasoft.gene.tags.gestori.decoratori.GestoreCampoTestoComunicazioneHTML" />
	<gene:campoScheda campo="COMDATSCA"  obbligatorio='true' visibile="${requestScope.initCOMMODELLO eq '1' or datiRiga.W_INVCOM_COMMODELLO eq '1'}"/>
	<gene:campoScheda campo="COMORASCA"  obbligatorio='true' visibile="${requestScope.initCOMMODELLO eq '1' or datiRiga.W_INVCOM_COMMODELLO eq '1'}"/>
	<gene:campoScheda campo="COMMITT" defaultValue="${inizializzazioneMittente}"/>
	<gene:campoScheda campo="COMCODOPE" visibile="false" />	
	<gene:campoScheda campo="SYSUTE" entita="USRSYS" title="Operatore" definizione="T" where="USRSYS.SYSCON = W_INVCOM.COMCODOPE" modificabile="false" defaultValue="${requestScope.inizializzazioneOperatore}"/>
	<gene:campoScheda campo="COMNUMPROT" visibile='${integrazioneWSDM =="1" and !empty datiRiga.W_INVCOM_COMNUMPROT}' modificabile="false"/>
  	<gene:campoScheda campo="COMDATPROT" visibile='${integrazioneWSDM =="1" and !empty datiRiga.W_INVCOM_COMDATPROT}' modificabile="false"/>
  	<c:if test="${modo ne 'NUOVO' and modo ne 'MODIFICA' and (comstato eq '10' or comstato eq '11') and integrazioneWSDM =='1'}">
	  	<gene:campoScheda campo="NUMRICEVUTE"  title="Numero ricevute" value="${requestScope.initNUMRICEVUTE}" definizione="T200" campoFittizio="true" visibile="true" />
  	</c:if>
  	<c:if test="${modo ne 'NUOVO' and modo ne 'MODIFICA' and (comstato eq '3' or comstato eq '4')}">
	  	<gene:campoScheda campoFittizio="true">
	        <c:set var="pecDetails" value="${gene:callFunction2('it.eldasoft.sil.pg.tags.funzioni.GetPECDetailsFunction', pageContext, datiRiga.W_INVCOM_IDCOM)}" />
	        <c:set var="sent" value="${fn:split(pecDetails, ';')[0]}" />
	        <c:set var="error" value="${fn:split(pecDetails, ';')[1]}" />
	        <c:set var="received" value="${fn:split(pecDetails, ';')[2]}" />
	        <c:if test="${sent ne 0}">
	        	<td class="etichetta-dato">Riepilogo riconciliazione PEC</td>
	        	<td class="valore-dato">${sent} inviate, ${error} in errore, ${received} consegnate</td>
	        </c:if>
	    </gene:campoScheda>
    </c:if>
  	<c:if test="${modo eq 'NUOVO' and (! empty param.rispondi)}">
  		<gene:campoScheda campo="DESCODSOG" entita="W_INVCOMDES" where="W_INVCOM.IDPRG = W_INVCOMDES.IDPRG AND W_INVCOM.IDCOM = W_INVCOMDES.IDCOM" defaultValue="${requestScope.initDESCODSOG}" visibile="false"/>
  	</c:if>
  	<c:choose>
  		<c:when test="${entitaParent eq 'NSO_ORDINI'}">  		
			<gene:campoScheda campo="CODEIN" entita="NSO_ORDINI" where="ID = '${ngara}'" visibile="false" />
  		</c:when>
  		<c:when test="${entitaParent eq 'G1STIPULA'}">  		
			<gene:campoScheda campo="CENINT" entita="TORN" from = "V_GARE_STIPULA" where="CODSTIPULA = '${ngara}' and TORN.CODGAR=V_GARE_STIPULA.CODGAR"  visibile="false"/>
  		</c:when>
  		<c:otherwise>
			<gene:campoScheda campo="CENINT" entita="TORN" where="CODGAR = '${codgar}'" visibile="false" />
  		</c:otherwise>
  	</c:choose>
	<gene:campoScheda campo="IDCOMRIS" visibile="false" defaultValue="${param.idcomris}"/>	
	<gene:campoScheda campo="IDPRGRIS" visibile="false" defaultValue="${param.idprgris}"/>	
	<gene:campoScheda campo="COMTIPMA" defaultValue="${requestScope.initCOMTIPMA}" visibile="false"/>
	
	<gene:campoScheda addTr="false">
	<c:if test="${(not empty datiRiga.W_INVCOM_IDCOMRIS and not empty datiRiga.W_INVCOM_IDPRGRIS) or (not empty param.idcomris and not empty datiRiga.W_INVCOM)}">
		<tr>
			<td></td> 
			<td class="valore-dato">
			<span style="float:right"><a id="aLinkVisualizzaDettaglioComunicazioneProvenienza" href="javascript:showDettCom();" class="link-generico">Nascondi dati comunicazione di origine</a></span>
			</td>
		</tr>
	</c:if>
	</gene:campoScheda>
	
	<c:set var="comRisposta" value="${(not empty datiRiga.W_INVCOM_IDCOMRIS and not empty datiRiga.W_INVCOM_IDPRGRIS) or (not empty param.idcomris and not empty  param.idprgris)}"/>
	<gene:campoScheda addTr="false" visibile="${comRisposta}">
		<c:choose>
			<c:when test='${not empty param.idprgris}'>
				<c:set var="idprgris" value="${param.idprgris}"  />
			</c:when>
			<c:otherwise>
				<c:set var="idprgris" value="${datiRiga.W_INVCOM_IDPRGRIS}" />
			</c:otherwise>
		</c:choose>
		<c:choose>
			<c:when test='${not empty param.idcomris}'>
				<c:set var="idcomris" value="${param.idcomris}"  />
			</c:when>
			<c:otherwise>
				<c:set var="idcomris" value="${datiRiga.W_INVCOM_IDCOMRIS}" />
			</c:otherwise>
		</c:choose>
		<c:if test="${comRisposta}">
			<c:set var="comuniczioneOriginale" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetDatiComunicazioneOriginaleFunction",pageContext,idprgris,idcomris)}' />
			<tr id="rigaTabellaComunicazioneOriginale" class="commOriginale">
			<td colspan="2">
			<table id="tabellaComunicazioneOriginale" class="griglia" >
			<tr style="BACKGROUND-COLOR: #EFEFEF;">
				<td class="titolo-valore-dato">Oggetto comunicazione di origine</td>
				<td class="titolo-valore-dato">Testo</td>
			</tr>
			<tr>
				<td class="valore-dato">${commsgoggOrigin}</td>
				<td class="valore-dato" style="width:70%">${commsgtesOrigin}</td>
			</tr>
			</table>
			</td>
			</tr>
		</c:if>
	</gene:campoScheda>	

	<c:if test="${(comstato eq '10' or comstato eq '11') and modo eq 'VISUALIZZA' and tipoWSDM  eq 'PALEO'}">
	<gene:campoScheda>
		<td class="etichetta-dato"></td>
		<td class="valore-dato"><a href="javascript:openModal('${datiRiga.W_INVCOM_IDCOM}','${datiRiga.W_INVCOM_IDPRG}','${idconfi}','${pageContext.request.contextPath}');">
			<span>Verifica invio comunicazione</span>
		</a></td>
	</gene:campoScheda>
	</c:if>
	<input type="hidden" name="keyAdd" value="${keyAdd}" />
	<input type="hidden" name="numModello" id="numModello" value="${param.numModello}"/>
	<input type="hidden" name="ditta" id="ditta" value="${ditta}"/>
	<input type="hidden" name="stepWizard" id="stepWizard" value="${stepWizard}"/>
	<input type="hidden" name="tipo" id="tipo" value="${tipo}"/>
	<input type="hidden" name="chiaveWSDM" id="chiaveWSDM" value="${chiaveWSDM}"/>
	<input type="hidden" name="entitaWSDM" id="entitaWSDM" value="${entitaWSDM}"/>
	<input type="hidden" name="idconfi" id="idconfi" value="${idconfi}"/>
	<input type="hidden" name="idprgComunicazPadre" id="idprgComunicazPadre" value="${idprgComunicazPadre}"/>
	<input type="hidden" name="idcomComunicazPadre" id="idcomComunicazPadre" value="${idcomComunicazPadre}"/>
	<c:if test='${integrazioneWSDM =="1"}'>
		<input id="servizio" type="hidden" value="FASCICOLOPROTOCOLLO" />
		<input id="idprg" type="hidden" value="${sessionScope.moduloAttivo}" />
	</c:if>
	
	<gene:fnJavaScriptScheda funzione="gestioneCOMINTEST('#W_INVCOM_COMPUB#','#W_INVCOM_COMINTEST#')" elencocampi="W_INVCOM_COMPUB;W_INVCOM_COMINTEST" esegui="true" />
	<gene:fnJavaScriptScheda funzione="modifyCOMPUB('#W_INVCOM_COMPUB#')" elencocampi="W_INVCOM_COMPUB" esegui="true" />
	<c:if test='${modo eq "NUOVO"}'>
		<gene:fnJavaScriptScheda funzione="modifyHTMLEditor('#W_INVCOM_COMMSGTIP#')" elencocampi="W_INVCOM_COMMSGTIP" esegui="false" />
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
				<c:if test="${comstato eq '1' and autorizzatoModificaComunicazione eq 'true'}">
				<c:choose>
					<c:when test='${integrazioneWSDM =="1" and !protpres and datiRiga.W_INVCOM_COMPUB==2}'>
						<INPUT type="button" class="bottone-azione" id="button_protocollacomunicazione" value="Protocolla ed invia comunicazione" title="Protocolla ed invia comunicazione" onclick="javascript:protocollacomunicazione('${idprg}','${idcom}');">
					</c:when>
					<c:otherwise>
						<INPUT type="button" class="bottone-azione" id="button_inviaComunicazione" value="Invia comunicazione" title="Invia comunicazione" onclick="javascript:inviacomunicazione('${idprg}','${idcom}');">
					</c:otherwise>
				</c:choose>
				</c:if>
				<gene:insert name="pulsanteModifica">
					<c:if test='${gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD") && (bloccoPubblicazionePortaleEsito ne "TRUE" && bloccoPubblicazionePortaleBando ne "TRUE")}'>
						<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:schedaModifica()">
					</c:if>
				</gene:insert>
				
			</c:otherwise>
			</c:choose>
			&nbsp;
		</td>
	</gene:campoScheda>
	<c:if test="${requestScope.inizializzazioneProtocollo eq 'true'}">
		<c:set var="protpres" value="true"/>
	</c:if>

	<c:if test='${modo ne "MODIFICA" and modo ne "NUOVO" and autorizzatoModificaComunicazione eq "true"}'>
		<gene:redefineInsert name="addToAzioni" >
			<c:if test="${comstato eq '1'}">
			 	<c:choose>
					<c:when test='${integrazioneWSDM =="1" and !protpres and datiRiga.W_INVCOM_COMPUB==2}'>
					<tr>
						<td class="vocemenulaterale" >
							<a href="javascript:protocollacomunicazione('${idprg}','${idcom}');" title="Protocolla ed invia comunicazione" tabindex="1503">Protocolla ed invia comunicazione</a>
						</td>
					</tr>
					
					<tr>
						<td class="vocemenulaterale" >
							<a href="javascript:inviacomunicazione('${idprg}','${idcom}');" title="Invia comunicazione" tabindex="1504">Invia comunicazione</a>
						</td>
					</tr>
				</c:when>
				<c:otherwise>
					<tr>
						<td class="vocemenulaterale" >
							<a href="javascript:inviacomunicazione('${idprg}','${idcom}');" title="Invia comunicazione" tabindex="1504">Invia comunicazione</a>
						</td>
					</tr>
				</c:otherwise>
				</c:choose>
			</c:if>
			<c:if test='${integrazioneWSDM =="1"}'>
				<c:if test="${comstato eq '11' and datiRiga.W_INVCOM_COMPUB==2 and wsdmMailDocumentale eq 'true' and destinatariErrore and gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GENEWEB.W_INVCOM-scheda.W_INVCOM.ReinvioMailDocumentale')}">
					<tr>
						<td class="vocemenulaterale" >
							<a href="javascript:reinvia('${idprg}','${idcom}');" title="Reinvio a destinatari con errore" tabindex="1503">Reinvio a destinatari con errore</a>
						</td>
					</tr>
				</c:if>
				<c:if test="${tipoWSDM eq 'PALEO' and (comstato eq '11' or comstato eq '10') and not empty datiRiga.W_INVCOM_COMNUMPROT and gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.TrasmettiAOperatoriInterni')}">
					<c:set var="abilitataFunzTrasmettiOpIntereni" value="true"/>
					<tr>
						<td class="vocemenulaterale" >
							<a href="javascript:apriTrasmettiAOperatoriInterni();" title="Trasmetti a operatori interni" tabindex="1504">Trasmetti a operatori interni</a>
						</td>
					</tr>
				</c:if>
			</c:if>
		</gene:redefineInsert>
	</c:if>
	
	<c:if test="${comstato ne '1' or autorizzatoModificaComunicazione eq 'false' }">
		<gene:redefineInsert name="pulsanteModifica" />
		<gene:redefineInsert name="schedaModifica" />
	</c:if>
	<gene:redefineInsert name="pulsanteNuovo" />
	<gene:redefineInsert name="schedaNuovo" />

</gene:formScheda>

<c:if test="${mailDocumentale eq '1'}">
	<jsp:include page="/WEB-INF/pages/gare/commons/modalPopupVerificaMail.jsp" />
</c:if>

<c:if test="${abilitataFunzTrasmettiOpIntereni eq 'true' }">
<div id="mascheraParametriWSDM" title='Trasmetti su documentale a operatori interni' style="display:none">
	<form id="richiestawslogin">
		<table class="dettaglio-notab">
			<input id="syscon" type="hidden" value="${profiloUtente.id}" /> 
			<input id="servizio" type="hidden" value="FASCICOLOPROTOCOLLO" />
			<input id="tiposistemaremoto" type="hidden" value="${tipoWSDM}" />
			<input id="tabellatiInDB" type="hidden" value="${tabellatiInDB}" />
			<input id="modoapertura" type="hidden" value="MODIFICA" /> 
			<tr id=sezioneMessaggi style="display: none;">
				<td colspan="2">
					<div  id="messagiTrasmissione"></div>
				</td>
			</tr>
			<tr id=sezioneErrori style="display: none;">
				<td colspan="2">
					<div  class="error" id="erroriTrasmissione"></div>
				</td>
			</tr>
			<jsp:include page="/WEB-INF/pages/gare/wsdm/wsdm-login.jsp">
				<jsp:param name="gestioneVisualizzazioneContratta" value="0"/>
				<jsp:param name="contextPath" value="${pageContext.request.contextPath}"/>
			</jsp:include>		
		</table>
	</form>
	<br>
	<form id="formTrasmissione">
		<table class="dettaglio-notab">
			<jsp:include page="/WEB-INF/pages/gare/wsdm/wsdm-datiTrasmissioneOperatori.jsp">
				<jsp:param name="tipoPagina" value="COM"/>
			</jsp:include>	
		</table>
	</form>
</div>
</c:if>

<gene:javaScript>
	
	var cenint = "${datiRiga.TORN_CENINT}";
	<c:set var="cenint" value="${datiRiga.TORN_CENINT}"/>
	<c:choose>
		<c:when test='${entitaParent eq "NSO_ORDINI"}'>
			cenint = "${datiRiga.NSO_ORDINI_CODEIN}";
			<c:set var="cenint" value="${datiRiga.NSO_ORDINI_CODEIN}"/>
		</c:when>
		
	</c:choose>
	
	$(document).ready(function() {
		if ((getValue('W_INVCOM_COMMSGTIP')==1 && ${modo ne 'VISUALIZZA'})||${modo eq "NUOVO"}) {
			$('#W_INVCOM_COMMSGTES').htmlarea({
			toolbar: [
			["bold", "italic", "underline", "strikethrough"],
					["increasefontsize", "decreasefontsize"],
					["orderedlist", "unorderedlist"],
					["indent", "outdent"],
					["justifyleft", "justifycenter", "justifyright"],
					["link", "unlink"],
					["cut", "copy", "paste"]
				],
			
				toolbarText: $.extend({}, jHtmlArea.defaultOptions.toolbarText, {
					"bold": "Grassetto",
					"italic": "Corsivo",
					"underline": "Sottolineato",
					"strikethrough": "Barrato",
					"increasefontsize": "Ingrandisci carattere",
					"decreasefontsize": "Riduci carattere",
					"orderedlist": "Elenco numerato",
					"unorderedlist": "Elenco puntato",
					"indent": "Aumenta rientro",
					"outdent": "Riduci rientro",
					"justifyleft": "Allinea testo a sinistra",
					"justifycenter": "Centra",
					"justifyright": "Allinea testo a destra",
					"link": "Inserisci collegamento ipertestuale",
					"unlink": "Rimuovi collegamento ipertestuale",
					"image": "Inserisci immagine",
					"horizontalrule": "Inserisci riga orizzontale",
					"cut": "Taglia",
					"copy": "Copia",
					"paste": "Incolla"
				})
			});
			modifyHTMLEditor(getValue('W_INVCOM_COMMSGTIP'));
		}
	});

	function modifyHTMLEditor(valore){
		if (valore == '1')
		 	$('#W_INVCOM_COMMSGTES').htmlarea('hideHTMLView');
		else
		 	$('#W_INVCOM_COMMSGTES').htmlarea('showHTMLView');
	}

	function gestioneCOMINTEST(compub,comintest){
		if (compub == '1') {
			document.getElementById("rowW_INVCOM_COMINTEST").style.display = 'none';
			document.forms[0].W_INVCOM_COMINTEST.value = '';
			document.getElementById("rowCAMPO_GENERICO17").style.display = ('none');
		} else {
			if(!(_delegaInvioMailDocumentaleAbilitata == 1)){
				document.getElementById("rowW_INVCOM_COMINTEST").style.display = '';
				document.getElementById("rowCAMPO_GENERICO17").style.display = (comintest=='1' ? '':'none');
			}
		}
	}
	
	//Gestione campi non visibili se comunicazione 'Pubblica'
	function modifyCOMPUB(valore){
		var vis = (valore!=1);
		showObj("rowW_INVCOM_COMMITT",vis);
		if (!vis) {
			setValue("W_INVCOM_COMMITT", "");
		}
	}

	function protocollacomunicazione(idprg,idcom) {
		<c:choose>
			<c:when test="${comstato eq '1' and autorizzatoTelematiche eq 'false'}">
				alert("Non e' possibile procedere.\nLa funzione e' disponibile solo al Punto ordinante");
				return;
			</c:when>
			<c:otherwise>
				<c:choose>
						<c:when test="${empty cenint}">
							alert("Non e' possibile procedere.\nDeve essere specificata la stazione appaltante");
							return;
						</c:when>
						<c:when test="${esistonoAllegatiDaFirmare eq 'TRUE'}">
							alert("Non e' possibile procedere.\nVi sono degli allegati in attesa di firma");
							return;
						</c:when>
					<c:otherwise>
						document.formprotocollacomunicazione.idprg.value = idprg;
						document.formprotocollacomunicazione.idcom.value = idcom;
						//var keyAdd="${keyAdd }";
						//var chiaveOriginale = keyAdd.substring(keyAdd.indexOf(":")+1);
						var chiaveOriginale = getValue("W_INVCOM_COMKEY1");
						document.formprotocollacomunicazione.chiaveOriginale.value = chiaveOriginale;
						document.formprotocollacomunicazione.submit();	
					</c:otherwise>
				</c:choose>
			</c:otherwise>
		</c:choose>
		
	}
	
	function inviacomunicazione(idprg,idcom){
		<c:choose>
			<c:when test="${comstato eq '1' and autorizzatoTelematiche eq 'false'}">
				alert("Non e' possibile procedere.\nLa funzione e' disponibile solo al Punto ordinante");
				return;
			</c:when>
			<c:otherwise>
				<c:choose>
						<c:when test="${empty cenint}">
							alert("Non e' possibile procedere.\nDeve essere specificata la stazione appaltante");
							return;
						</c:when>
					<c:otherwise>
						var commodello = "${commodello}";
						var compub = getValue("W_INVCOM_COMPUB");
						var href= "href=geneweb/w_invcom/w_invcom-invia-popup.jsp&idprg=" + idprg + "&idcom=" + idcom + "&compub=" + compub + "&cenint=" + cenint + "&commodello=" + commodello +"&descc="+${invioDesCC};
						openPopUpCustom(href, "inviacomunicazione", 550, 350, "no", "no");
					</c:otherwise>
				</c:choose>
			</c:otherwise>
		</c:choose>
	}
	
	var selezionaPaginaDefault = selezionaPagina;
	var selezionaPagina = selezionaPaginaCustom;
	function selezionaPaginaCustom(pageNumber){
		var keyAdd="${keyAdd }";
		var chiaveWSDM="${chiaveWSDM }";
		var entitaWSDM="${entitaWSDM }";
		var idconfi="${idconfi }";
		var comdatins = getValue("W_INVCOM_COMDATINS");
		var comtipo = getValue("W_INVCOM_COMTIPO");
		document.pagineForm.action += "&keyAdd=" + keyAdd + "&chiaveWSDM=" + chiaveWSDM + "&entitaWSDM=" + entitaWSDM + "&comdatins=" + comdatins + "&comtipo=" + comtipo + "&cenint=" + cenint+ "&idconfi=" + idconfi;
		selezionaPaginaDefault(pageNumber);
	}
	
	//si elimina l'orario dal valore
	var comdatins = "${datiRiga.W_INVCOM_COMDATINS }";
	if(comdatins!=null){
		var tmp = comdatins.split(" ");
		comdatins = tmp[0];
		formprotocollacomunicazione.comdatins.value=comdatins;
	}
	
	var comtipo = "${datiRiga.W_INVCOM_COMTIPO }";
	if(comtipo!=null){
		formprotocollacomunicazione.comtipo.value=comtipo;
	}
	
	<c:if test="${integrazioneWSDM eq 1}" >
		_controlloDelegaInvioMailAlDocumentale();
		_getTipoWSDM();
		<c:choose>
			<c:when test="${(modo eq 'MODIFICA' or modo eq 'NUOVO')}">
				if(_delegaInvioMailDocumentaleAbilitata == 1){
					setValue("W_INVCOM_COMINTEST","2");
					showObj("rowCAMPO_GENERICO17",false);
					showObj("rowW_INVCOM_COMINTEST",false);
					setValue("W_INVCOM_COMMITT","");
					if(_tipoWSDM != "JIRIDE" && _tipoWSDM != "ARCHIFLOW" && _tipoWSDM != "ARCHIFLOWFA"){
						showObj("rowW_INVCOM_COMMITT", false);
					}else{
						$('#W_INVCOM_COMMITT').hide();
					}
					
				}
						
				var schedaConfermaDefault = schedaConferma;
				var schedaConferma = schedaConfermaCustom;
				function schedaConfermaCustom(pageNumber){
					if(document.getElementById("W_INVCOM_COMMITT")!=null)
						document.getElementById("W_INVCOM_COMMITT").disabled = false;
					schedaConfermaDefault();
				}
			</c:when>
			<c:otherwise>
				if(_delegaInvioMailDocumentaleAbilitata == 1){
					if(_tipoWSDM != "JIRIDE" && _tipoWSDM != "ARCHIFLOW" && _tipoWSDM != "ARCHIFLOWFA"){
						showObj("rowW_INVCOM_COMMITT", false);
					}
					showObj("rowW_INVCOM_COMINTEST",false);
				}
			</c:otherwise>
		</c:choose>
		
		if(_tipoWSDM != "JIRIDE" ){
			showObj("rowNUMRICEVUTE", false);
		}
		
		function reinvia(idprg,idcom){
			<c:choose>
				<c:when test="${autorizzatoTelematiche eq 'false'}">
					alert("Non e' possibile procedere.\nLa funzione e' disponibile solo al Punto ordinante");
					return;
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test="${empty cenint}">
							alert("Non e' possibile procedere.\nDeve essere specificata la stazione appaltante");
							return;
						</c:when>
						<c:otherwise>
							var href = "href=geneweb/w_invcom/w_invcom-popup-reinviaMailDocumentale.jsp&idprg=" + idprg + "&idcom=" + idcom + "&tiposistemaremoto=" + _tipoWSDM+ "&idconfi=${idconfi}";
							href +="&entitaWSDM=${entitaWSDM}&chiaveWSDM=${chiaveWSDM}"; 
							openPopUpCustom(href, "reinviaMailDocumentale", 550, 350, "no", "no");
						</c:otherwise>
					</c:choose>
				</c:otherwise>
			</c:choose>	
		}
	</c:if>

	

var isComOriginaleShown = true;

<c:if test="${modo ne 'NUOVO'}">
	isComOriginaleShown = false;
	$(".commOriginale").hide();
	$("#aLinkVisualizzaDettaglioComunicazioneProvenienza").html("Visualizza dati comunicazione di origine");
</c:if>

function showDettCom(){
	if(isComOriginaleShown){
		isComOriginaleShown = false;
		$("#aLinkVisualizzaDettaglioComunicazioneProvenienza").html("Visualizza dati comunicazione di origine");
	}else{
		isComOriginaleShown = true;
		$("#aLinkVisualizzaDettaglioComunicazioneProvenienza").html("Nascondi dati comunicazione di origine");
	}
	$(".commOriginale").toggle();
}

	
	<c:if test="${abilitataFunzTrasmettiOpIntereni eq 'true' }">
	
	</c:if>
	
</gene:javaScript>

<form name="formprotocollacomunicazione" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
	
		
	<input type="hidden" name="href" value="gare/wsdm/wsdm-comunicazione.jsp" /> 
	<input type="hidden" name="entita" id="entita" value="${entitaWSDM}" />
	<input type="hidden" name="key1" id="key1" value='${chiaveWSDM}' />
	<input type="hidden" name="key2" id="key2" value='' /> 
	<input type="hidden" name="key3" id="key3" value='' />
	<input type="hidden" name="key4" id="key4" value='' />
	<input type="hidden" name="idconfi" id="idconfi" value='${idconfi}' />
	<input type="hidden" name="idprg" value="" />
	<input type="hidden" name="idcom" value="" />
	<input type="hidden" name="idcfg" value="${cenint}" />
	<input type="hidden" name="metodo" value="apri" />
	<input type="hidden" name="activePage" value="0" />
	<input type="hidden" name="chiaveOriginale" value="" />
	<input type="hidden" name="comdatins" value="" />
	<input type="hidden" name="comtipo" value="" />
</form>

