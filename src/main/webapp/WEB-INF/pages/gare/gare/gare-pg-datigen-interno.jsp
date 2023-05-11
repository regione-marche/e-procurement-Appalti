<%
/*
 * Created on: 21/07/2010
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="AliceResources" />

<%--
Viene sbiancata la variabile di sessione keyParentComunicazioni che viene inizializzata nella lista delle comunicazioni.
Se si crea una nuova comunicazione senza passare dalla lista delle comunicazioni la variabile altrimenti rimane valorizzata.
 --%>
<c:set var="keyParentComunicazioni" value="" scope="session"/>

<jsp:include page="/WEB-INF/pages/commons/defCostantiAppalti.jsp" />

<gene:redefineInsert name="head">
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>
</gene:redefineInsert>

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>

<c:set var="genere" value="${param.genere}" />
<c:set var="filtroLivelloUtente" value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "V_GARE_ELEDITTE")}' />
<c:set var="isPersonalizzazioneGenovaAttiva" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.IsPersonalizzazioneGenovaAttivaFunction",pageContext)}' scope="request" />


<c:if test='${modo ne "MODIFICA" && modo ne "NUOVO"}'>
	<c:choose>
		<c:when test='${! empty gene:getValCampo(param.key, "CODGAR")}' >
			<c:set var="codiceGara" value="${gene:getValCampo(param.key,'CODGAR')}"/>
		</c:when>
		<c:otherwise>
			<c:set var="codiceGara" value="${gene:concat('$',gene:getValCampo(key, 'NGARA'))}"/>
		</c:otherwise>
	</c:choose>
<c:set var="integrazioneWSDMDocumentale" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSDNDocumentaleFunction", pageContext, codiceGara, idconfi)}' />
<c:set var="isFascicoloDocumentaleAbilitato" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.IsFascicoloDocumentaleValidoFunction",  pageContext, codiceGara, idconfi)}' />
</c:if>

<c:set var="log" value="${param.log}"/> 

<%/* Dati generali della gara */%>
<gene:formScheda entita="GARE" gestisciProtezioni="true" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreInizializzaElenco" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreElencoDitte">

<%/* Viene riportato il valore del parameter garaPerElenco, in modo tale che,
		 in caso di errore riapertura della pagina, venga riaperta considerando il
     valore definito inizialmente per la prima apertura della pagina */%>
<input type="hidden" name="garaPerElenco" value="${garaPerElenco}" />
<input type="hidden" name="garaPerCatalogo" value="${garaPerCatalogo}" />


<c:if test="${ genere == '10'}">
	<c:set var="isCodificaAutomatica" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.IsCodificaAutomaticaFunction", pageContext, "GAREALBO", "CODGAR")}'/>
</c:if>
<c:if test="${ genere == '20'}">
	<c:set var="isCodificaAutomatica" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.IsCodificaAutomaticaFunction", pageContext, "MECATALOGO", "CODGAR")}'/>
</c:if>

<c:if test='${modo eq "VISUALIZZA" || metodo ne "modifica" }'>
	<c:set var="IsW_CONFCOMPopolata" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsW_CONFCOMPopolataFunction",pageContext,genere)}' />
</c:if>

		<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-scheda.jsp">
			<jsp:param name="entita" value="${valoreEntita }"/>
			<jsp:param name="inputFiltro" value="${requestScope.inputFiltro}"/>			
			<jsp:param name="filtroCampoEntita" value="CODGAR=#CODGAR#"/>
		</jsp:include>

	<gene:redefineInsert name="schedaNuovo" >
	<c:if test='${gene:checkProtFunz(pageContext,"INS","SCHEDANUOVO")}'>
		<tr>
			<td class="vocemenulaterale" >
				<c:if test='${isNavigazioneDisattiva ne "1"}'><a href="javascript:schedaNuovaGara();" title="Inserisci" tabindex="1502"></c:if>
					${gene:resource("label.tags.template.lista.listaNuovo")}
				<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
			</td>
		</tr>
	</c:if>
	</gene:redefineInsert>
	<gene:redefineInsert name="pulsanteNuovo">
			<c:if test='${gene:checkProtFunz(pageContext,"INS","SCHEDANUOVO")}'>
				<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaNuovo")}' title='${gene:resource("label.tags.template.lista.listaNuovo")}' onclick="javascript:schedaNuovaGara()" id="btnNuovo">
			</c:if>
	</gene:redefineInsert>

	<gene:redefineInsert name="addToAzioni" >
	<c:if test='${gene:checkProtFunz(pageContext, "ALT","Condividi-gara") && modo eq "VISUALIZZA"}'> 
	  <tr>    
	      <td class="vocemenulaterale">
	     		<c:choose>
	     			<c:when test='${isPersonalizzazioneGenovaAttiva eq "1"}'>
	     				<a href="javascript:apriGestionePermessi('${datiRiga.GARE_CODGAR1}','${genere}',${datiRiga.G_PERMESSI_PROPRI eq '1' or abilitazioneGare eq 'A'})" />
	     			</c:when>
	     			<c:otherwise>
	     				<a href="javascript:apriGestionePermessiStandard('${datiRiga.GARE_CODGAR1}','${genere}',${datiRiga.G_PERMESSI_PROPRI eq '1' or abilitazioneGare eq 'A'})" />
	     			</c:otherwise>
	     		</c:choose>
				<c:if test="${ genere == '10'}">
				  Condividi e proteggi elenco
				</c:if>
				<c:if test="${ genere == '20'}">
				  Condividi e proteggi catalogo
				</c:if>
				<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>			  
			</td>
		</tr>
	</c:if>

	<c:if test='${modo eq "VISUALIZZA" and ((fn:contains(listaOpzioniDisponibili, "OP114#") and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE.RiceviComunicazioni")) || (gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE.InviaComunicazioni")))}'>
		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td class="titolomenulaterale" title='${gene:resource("label.tags.template.documenti.sezioneComunicazioni")}'>
				${gene:resource("label.tags.template.documenti.sezioneComunicazioni")}</td>
		</tr>
		
		<c:if test='${fn:contains(listaOpzioniDisponibili, "OP114#") and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE.RiceviComunicazioni")}'>
				<tr>
					<td class="vocemenulaterale" >
						<c:if test='${isNavigazioneDisattiva ne "1"}'>
							<a href="javascript:leggiComunicazioni();" title="${gene:resource('label.tags.template.documenti.comunicazioniRicevute.tooltip')}" tabindex="1503">
						</c:if>
						${gene:resource('label.tags.template.documenti.comunicazioniRicevute')}
						<c:set var="numComunicazioniRicevuteNonLette" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetNumComunicazioniRicevuteDaLeggereFunction", pageContext, gene:getValCampo(key, "NGARA"))}' />
						<c:if test="${numComunicazioniRicevuteNonLette > 0}">(${numComunicazioniRicevuteNonLette} ${gene:resource('label.tags.template.documenti.comunicazioniRicevute.indicatore')})</c:if>
						<c:if test="${not empty requestScope.numComunicazioniRicevuteNonLette}">(${requestScope.numComunicazioniRicevuteNonLette} ${gene:resource('label.tags.template.documenti.comunicazioniRicevute.indicatore')})</c:if>
						<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
					</td>
				</tr>
		</c:if>
		<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE.InviaComunicazioni")}'>
				<tr>
					<td class="vocemenulaterale" >
						<c:if test='${isNavigazioneDisattiva ne "1"}'>
							<a href="javascript:inviaComunicazioni();" title="${gene:resource('label.tags.template.documenti.inviaComunicazioni.tooltip')}" tabindex="1504">
						</c:if>
						${gene:resource('label.tags.template.documenti.inviaComunicazioni')}
						<c:set var="numComunicazioniBozza" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetNumComunicazioniBozzaFunction", pageContext, "GARE", gene:getValCampo(key, "NGARA"))}' />
						<c:if test="${numComunicazioniBozza > 0}">(${numComunicazioniBozza} ${gene:resource('label.tags.template.documenti.inviaComunicazioni.indicatore')})</c:if>
						<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
					</td>
				</tr>
				<c:if test="${autorizzatoModifiche ne '2' and gene:checkProt(pageContext,'FUNZ.VIS.INS.GENEWEB.W_INVCOM-lista.LISTANUOVO')}">
					<tr>
						<td class="vocemenulaterale" >
							<c:if test='${isNavigazioneDisattiva ne "1"}'>
								<a href="javascript:nuovaComunicazione();" title="Nuova comunicazione" tabindex="1505">
							</c:if>
							Nuova comunicazione
							<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
						</td>
					</tr>
				</c:if>
		</c:if>
	</c:if>
	<c:if test='${modo eq "VISUALIZZA" && gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE.Discussioni")}'>
		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td class="titolomenulaterale" title='Messaggi interni'>Messaggi interni</td>
		</tr>
		<tr>
			<td class="vocemenulaterale" >
				<c:if test='${isNavigazioneDisattiva ne "1"}'>
					<a href="javascript:listaDiscussioni();" title="Discussioni" tabindex="1516">
				</c:if>
				Conversazioni
				<c:set var="resultConteggioDiscussioni" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetDiscussioniNumeroFunction", pageContext, "GARE", gene:getValCampo(key, "NGARA"))}' />
				<c:if test="${numeroDiscussioni > 0}">(${numeroDiscussioni})</c:if>
				<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
			</td>
		</tr>
	</c:if>
	</gene:redefineInsert>
	
	
	<c:set var="exportDocumenti" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.sil.pg.pathArchivioDocumentiGara")}'/>
	<c:set var="exportCOS" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "cos.sftp.url")}'/>
	
	<gene:redefineInsert name="addToDocumenti" >
		<c:set var="creaFascicolo" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.GetPropertyWsdmFunction", accessoCreaFascicolo,idconfi)}'/>
		<c:set var="integrazioneWSDM" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSDNFunction", pageContext, codiceGara,idconfi)}' />
		<c:if test='${modo eq "VISUALIZZA"  and autorizzatoModifiche ne "2" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.CreaFascicolo")  and integrazioneWSDM eq "1" and creaFascicolo eq "1"}'>
			<c:set var="esisteFascicoloAssociato" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteFascicoloAssociatoFunction", pageContext, "GARE", datiRiga.GARE_NGARA,idconfi)}' scope="request"/>
			<c:set var="tipoWSDM" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetTipoWSDMFunction", pageContext, "FASCICOLOPROTOCOLLO", "NO",idconfi)}' />
			<c:if test="${ esisteFascicoloAssociato eq 'true' and tipoWSDM eq 'LAPISOPERA'}">
				<c:set var="esisteDocumentoAssociato" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteDocumentoAssociatoFunction", pageContext, "GARE", datiRiga.GARE_NGARA)}' scope="request"/>
			</c:if>
			<c:if test='${(esisteFascicoloAssociato ne "true" and  (tipoWSDM eq "IRIDE" or tipoWSDM eq "JIRIDE" or tipoWSDM eq "ENGINEERING" 
				or tipoWSDM eq "ARCHIFLOW" or tipoWSDM eq "INFOR" or tipoWSDM eq "JPROTOCOL" or tipoWSDM eq "JDOC" or tipoWSDM eq "ENGINEERINGDOC" or tipoWSDM eq "ITALPROT" or tipoWSDM eq "LAPISOPERA")) || 
				(tipoWSDM eq "LAPISOPERA" and esisteDocumentoAssociato ne "true")}' >
				
				<c:choose>
					<c:when test='${tipoWSDM eq "ITALPROT" or tipoWSDM eq "LAPISOPERA" }'>
						<c:set var="titoloMessaggio" value="Associa fascicolo"/>
						<c:if test="${tipoWSDM eq 'LAPISOPERA' and esisteFascicoloAssociato eq 'true' and esisteDocumentoAssociato ne 'true' }">
							<c:set var="fascicoloEsistente" value="1"/>
						</c:if>
 					</c:when>
					<c:otherwise>
						<c:set var="titoloMessaggio" value="Crea fascicolo"/>
					</c:otherwise>
				</c:choose>
				
				<tr>
					<c:choose>
						<c:when test='${isNavigazioneDisattiva ne "1"}'>
							<td class="vocemenulaterale">
								<a href="javascript:apriCreaFascicolo('${datiRiga.GARE_NGARA}','${datiRiga.GARE_CODGAR1}','${idconfi}',${genere },'${fascicoloEsistente}');" title="${titoloMessaggio }" tabindex="1515">
									${titoloMessaggio }
								</a>
							</td>
						</c:when>
						<c:otherwise>
							<td>
								${titoloMessaggio }
							</td>
						</c:otherwise>
					</c:choose>
				</tr>
			</c:if>
		</c:if>
		<c:if test='${modo eq "VISUALIZZA" and 
				((integrazioneWSDMDocumentale eq "1" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.TrasferisciAlDocumentale")) 
                or (!empty exportCOS and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.TrasferisciCos"))
				or (!empty exportDocumenti and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.ExportDocumenti")))}'>
			<tr>
				<c:choose>
					<c:when test='${isNavigazioneDisattiva ne "1"}'>
						<td class="vocemenulaterale">
							<a href="javascript:apriArchiviaDocumenti('${datiRiga.GARE_CODGAR1}');" title="Archivia documenti" tabindex="1514">
								Archivia documenti
							</a>
						</td>
					</c:when>
					<c:otherwise>
						<td>
							Archivia documenti
						</td>
					</c:otherwise>
				</c:choose>
			</tr>
		</c:if>
		<c:if test='${modo eq "VISUALIZZA" and isFascicoloDocumentaleAbilitato eq "1" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FascicoloDocumentale")}'>
			<tr>
				<c:choose>
					<c:when test='${isNavigazioneDisattiva ne "1"}'>
						<td class="vocemenulaterale">
							<a href="javascript:document.formwsdm.submit();" title="Fascicolo documentale" tabindex="1516">
								Fascicolo documentale
							</a>
						</td>
					</c:when>
					<c:otherwise>
						<td>
							Fascicolo documentale
						</td>
					</c:otherwise>
				</c:choose>
			</tr>
		</c:if>
	</gene:redefineInsert>
<%-- 
	<gene:redefineInsert name="addToDocumenti" >
		<c:if test='${modo eq "VISUALIZZA" and fn:contains(listaOpzioniDisponibili, "OP128#")}'>
			<tr>
				<c:choose>
					<c:when test='${isNavigazioneDisattiva ne "1"}'>
						<td class="vocemenulaterale">
							<a href='javascript:scadenzario("GARE", document.forms[0].key.value, ${sessionScope.entitaPrincipaleModificabile}, "ELENCO")' title="Scadenzario attività" tabindex="1512">	
								Scadenzario attività
							</a>
						</td>
					</c:when>
					<c:otherwise>
						<td>
							Scadenzario attività
						</td>
					</c:otherwise>
				</c:choose>
			</tr>
		</c:if>
	</gene:redefineInsert>
 --%>
	<c:set var="msgChiaveErrore">	
		<fmt:message key="match.tags.template.dettaglio.campoChiave.messaggio">
			<fmt:param value="$"/>
		</fmt:message>
	</c:set>

	<c:set var="msgChiaveErrore" value="${fn:replace(msgChiaveErrore, '\\\\', '')}" />	

	<gene:gruppoCampi idProtezioni="GEN" >
		<gene:campoScheda>
			<td colspan="2"><b>Dati generali</b></td>
		</gene:campoScheda>

		<gene:campoScheda campo="NGARA" visibile="false" />
		<gene:campoScheda campo="CODGAR1" visibile="false" defaultValue="${gene:getValCampo(param.keyParent, 'CODGAR')}" />

<c:choose>
	<c:when test='${isCodificaAutomatica eq "false"}'>
		<gene:campoScheda campo="NGARA" entita="GAREALBO" where="GARE.CODGAR1=GAREALBO.CODGAR and GARE.NGARA=GAREALBO.NGARA" title="${gene:if(genere == 20,'Codice catalogo','Codice elenco') }" obbligatorio="true" modificabile='${modoAperturaScheda eq "NUOVO"}' >
			<gene:checkCampoScheda funzione='"##".indexOf("$") < 0' obbligatorio="true" messaggio="${msgChiaveErrore}" />
		</gene:campoScheda>
	</c:when>
	<c:otherwise>
		<gene:campoScheda campo="NGARA" entita="GAREALBO" where="GARE.CODGAR1=GAREALBO.CODGAR and GARE.NGARA=GAREALBO.NGARA" title="${gene:if(genere == 20,'Codice catalogo','Codice elenco') }" modificabile='false' gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoCodificaAutomatica" />
	</c:otherwise>
</c:choose>

		<gene:campoScheda campo="CODGAR" entita="GAREALBO" where="GARE.CODGAR1=GAREALBO.CODGAR and GARE.NGARA=GAREALBO.NGARA" visibile="false" />
		<gene:campoScheda campo="TIPOELE"  entita="GAREALBO" where="GARE.CODGAR1=GAREALBO.CODGAR and GARE.NGARA=GAREALBO.NGARA" title="${gene:if(genere == 20,'Catalogo per','Elenco per') }" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoTipoElenco" obbligatorio="true" modificabile='${modoAperturaScheda eq "NUOVO"}'/>
		<c:choose>
			<c:when test="${ genere == '10'}">
				<gene:campoScheda campo="TIPOLOGIA"  entita="GAREALBO" where="GARE.CODGAR1=GAREALBO.CODGAR and GARE.NGARA=GAREALBO.NGARA" obbligatorio="true" modificabile="${!bloccoTipologia }"/>
				<gene:campoScheda campo="TCONGELAMENTO" entita="GAREALBO" where="GARE.CODGAR1=GAREALBO.CODGAR and GARE.NGARA=GAREALBO.NGARA" />
				<gene:campoScheda campo="CADENZAMAN" entita="GAREALBO" where="GARE.CODGAR1=GAREALBO.CODGAR and GARE.NGARA=GAREALBO.NGARA" />
			</c:when>
			<c:otherwise>
				<gene:campoScheda campo="TIPOLOGIA"  entita="GAREALBO" where="GARE.CODGAR1=GAREALBO.CODGAR and GARE.NGARA=GAREALBO.NGARA" visibile="false" />
			</c:otherwise>
		</c:choose>
		
		
		<gene:campoScheda campo="OGGETTO"  entita="GAREALBO" where="GARE.CODGAR1=GAREALBO.CODGAR and GARE.NGARA=GAREALBO.NGARA" />
		<gene:campoScheda campo="DINIZVAL" entita="GAREALBO" where="GARE.CODGAR1=GAREALBO.CODGAR and GARE.NGARA=GAREALBO.NGARA" />
		<gene:campoScheda campo="DTERMVAL" entita="GAREALBO" where="GARE.CODGAR1=GAREALBO.CODGAR and GARE.NGARA=GAREALBO.NGARA" />
		<gene:campoScheda campo="PERIODO"  entita="GAREALBO" where="GARE.CODGAR1=GAREALBO.CODGAR and GARE.NGARA=GAREALBO.NGARA" />
		<gene:campoScheda campo="ISARCHI"  entita="GAREALBO" where="GARE.CODGAR1=GAREALBO.CODGAR and GARE.NGARA=GAREALBO.NGARA" visibile="${datiRiga.GAREALBO_ISARCHI eq '1'}" />
		
		<c:if test="${ genere == '20'}">
			<gene:campoScheda campo="NGARA"  entita="MECATALOGO" where="GARE.CODGAR1=MECATALOGO.CODGAR and GARE.NGARA=MECATALOGO.NGARA" visibile="false"/>
			<gene:campoScheda campo="CODGAR"  entita="MECATALOGO" where="GARE.CODGAR1=MECATALOGO.CODGAR and GARE.NGARA=MECATALOGO.NGARA" visibile="false"/>
			<gene:campoScheda campo="VISPREZZIOE"  entita="MECATALOGO" where="GARE.CODGAR1=MECATALOGO.CODGAR and GARE.NGARA=MECATALOGO.NGARA" />
			<gene:campoScheda campo="NUMPRODART"  entita="MECATALOGO" where="GARE.CODGAR1=MECATALOGO.CODGAR and GARE.NGARA=MECATALOGO.NGARA" obbligatorio="true"/>
		</c:if>
	</gene:gruppoCampi>
	
	<gene:fnJavaScriptScheda funzione='controlloTermineValidita("#GAREALBO_DTERMVAL#")' elencocampi='GAREALBO_DTERMVAL' esegui="false" />
	<gene:fnJavaScriptScheda funzione='controlloInizioValidita("#GAREALBO_DINIZVAL#")' elencocampi='GAREALBO_DINIZVAL' esegui="false" />
	<c:if test="${ genere == '10'}">
		<gene:fnJavaScriptScheda funzione='visibilitaTCONGELAMENTO("#GAREALBO_TIPOLOGIA#","true")' elencocampi='GAREALBO_TIPOLOGIA' esegui="false" />
		<gene:fnJavaScriptScheda funzione='showObj("rowGAREALBO_CADENZAMAN",getValue("GAREALBO_TIPOLOGIA")==4)' elencocampi='GAREALBO_TIPOLOGIA' esegui="true" />
	</c:if>
	
	<c:if test='${modo eq "NUOVO"}'>
		<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.ValorizzaStazioneAppaltanteFunction" parametro=""/>
	</c:if>
	
	<gene:gruppoCampi idProtezioni="RUP" >
		<c:choose>
			<c:when test="${genere == 20}">
				<c:set var="msg" value="del catalogo"/>
			</c:when>
			<c:otherwise>
				<c:set var="msg" value="dell'elenco"/>
			</c:otherwise>
		</c:choose>
		<gene:campoScheda>
			<td colspan="2"><b>Stazione appaltante e Responsabile ${msg} </b></td>
		</gene:campoScheda>
		<gene:archivio titolo="Uffici intestatari"
			 lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.CENINT"),"gene/uffint/uffint-lista-popup.jsp","")}'
			 scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaUffint"),"gene/uffint/uffint-scheda.jsp","")}'
			 schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaUffint"),"gene/uffint/uffint-scheda-popup.jsp","")}'
			 campi="UFFINT.CODEIN;UFFINT.NOMEIN"
			 chiave="TORN_CENINT"
			 functionId="skip|abilitazione:1_parentFormName:formUFFINTElenchi"
			 formName="formUFFINTElenchi">
				<gene:campoScheda campo="CENINT" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" obbligatorio="true" defaultValue="${requestScope.initCENINT}" modificabile="${empty sessionScope.uffint }"/>
				<gene:campoScheda campo="NOMEIN" title="Denominazione" entita="UFFINT" from="TORN" where="GARE.CODGAR1=TORN.CODGAR and TORN.CENINT=UFFINT.CODEIN" 
					modificabile='${gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.CENINT") && empty sessionScope.uffint}' 
					visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.CENINT")}' defaultValue="${requestScope.initNOMEIN}" />
		</gene:archivio>
		<gene:campoScheda campo="UFFDET" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" />
		<gene:campoScheda campo="LIVACQ" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" />
		<gene:archivio titolo="Tecnici"
			 lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.CODRUP"),"gene/tecni/tecni-lista-popup.jsp","")}'
			 scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda.jsp","")}'
			 schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda-popup.jsp","")}'
			 campi="TECNI.CODTEC;TECNI.NOMTEC"
			 functionId="skip"
			 chiave="TORN_CODRUP"
			 inseribile="true">
				<gene:campoScheda campo="CODRUP" title="Codice responsabile ${msg }" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" />
				<gene:campoScheda campo="NOMTEC" title="Nome" entita="TECNI" from="TORN" where="GARE.CODGAR1=TORN.CODGAR and TORN.CODRUP=TECNI.CODTEC" 
					modificabile='${gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.CODRUP")}' 
					visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.CODRUP")}' />
		</gene:archivio>
	</gene:gruppoCampi>
	
	<gene:campoScheda campo="VALISCR" entita="GAREALBO" where="GARE.CODGAR1=GAREALBO.CODGAR and GARE.NGARA=GAREALBO.NGARA" visibile="false"/> 
	<gene:campoScheda campo="GPREAVRIN" entita="GAREALBO" where="GARE.CODGAR1=GAREALBO.CODGAR and GARE.NGARA=GAREALBO.NGARA" visibile="false" />
	<gene:campoScheda campo="RIFISCR" entita="GAREALBO" where="GARE.CODGAR1=GAREALBO.CODGAR and GARE.NGARA=GAREALBO.NGARA" visibile="false" />
	<gene:campoScheda campo="APPRIN" entita="GAREALBO" where="GARE.CODGAR1=GAREALBO.CODGAR and GARE.NGARA=GAREALBO.NGARA" visibile="false" />
	
<c:if test='${modo eq "VISUALIZZA" or modo eq "MODIFICA"}'>
	<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GestionePubblicazioniTerminiIscrizioneFunction" parametro='${gene:getValCampo(key, "NGARA")}' />
</c:if>

	<c:if test='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.UFFDET")}'>
		<gene:fnJavaScriptScheda funzione='gestioneVisualizzazioneUffedt("#TORN_CENINT#","2")' elencocampi='TORN_CENINT' esegui="false" />
	</c:if>

	<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
		<jsp:param name="entita" value='PUBBTERM'/>
		<jsp:param name="chiave" value='${gene:getValCampo(key, "NGARA")}'/>
		<jsp:param name="nomeAttributoLista" value='pubblicazioniTerminiIscr' />
		<jsp:param name="idProtezioni" value="PUBBLITERMISCR" />
		<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/pubbterm/pubblicazione-termineIscrizione.jsp"/>
		<jsp:param name="arrayCampi" value="'PUBBTERM_CODGAR_', 'PUBBTERM_NGARA_', 'PUBBTERM_NUMPT_', 'PUBBTERM_NPUBAVVBAN_', 'PUBBTERM_DPUBAVVBAN_', 'PUBBTERM_OPUBAVVBAN_', 'PUBBTERM_DTERMPRES_', 'PUBBTERM_OTERMPRES_', 'PUBBTERM_DSORTEGGIO_', 'PUBBTERM_OSORTEGGIO_', 'PUBBTERM_NOTEPT_'"/>
		<jsp:param name="titoloSezione" value="Termini iscrizione" />
		<jsp:param name="titoloNuovaSezione" value="Nuovo termini iscrizione" />
		<jsp:param name="descEntitaVociLink" value="termini iscrizione" />
		<jsp:param name="msgRaggiuntoMax" value="i termini iscrizione"/>
		<jsp:param name="usaContatoreLista" value="true" />
	</jsp:include>
	
	<c:if test="${ genere == '10'}">
		<c:set var="valoreEntita" value="V_GARE_ELEDITTE"/>
	</c:if>
	<c:if test="${ genere == '20'}">
		<c:set var="valoreEntita" value="V_GARE_CATALDITTE"/>
	</c:if>
	
	<gene:campoScheda>
		<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
	</gene:campoScheda>
	
	<input type="hidden" name="genere" value="${genere}" />
	
	<c:choose>
	<c:when test="${!empty (filtroLivelloUtente)}">
		<gene:campoScheda campo="AUTORI" entita="G_PERMESSI" where="G_PERMESSI.CODGAR = GARE.CODGAR1 AND G_PERMESSI.SYSCON = ${profiloUtente.id}" visibile="false"/>
		<gene:campoScheda campo="PROPRI" entita="G_PERMESSI" where="G_PERMESSI.CODGAR = GARE.CODGAR1 AND G_PERMESSI.SYSCON = ${profiloUtente.id}" visibile="false"/>
	</c:when>
	<c:otherwise>
		<gene:campoScheda campo="AUTORI" entita="G_PERMESSI" campoFittizio="true" value="1" visibile="false"/>
		<gene:campoScheda campo="PROPRI" entita="G_PERMESSI" campoFittizio="true" value="1" visibile="false"/>
	</c:otherwise>
	</c:choose>
	
</gene:formScheda>

		<form name="formVisualizzaPermessiUtenti" action="${pageContext.request.contextPath}/pg/VisualizzaPermessiUtenti.do" method="post">
			<input type="hidden" name="metodo" id="metodo" value="apri" />
			<input type="hidden" name="codgar" id="codgar" value="" />
			<input type="hidden" name="genereGara" id="genereGara" value="" />
			<input type="hidden" name="permessimodificabili" id="permessimodificabili" value="" />
			<input type="hidden" name="codein" id="codein" value="${sessionScope.uffint}" />
		</form> 
		
		<form name="formVisualizzaPermessiUtentiStandard" action="${pageContext.request.contextPath}/pg/VisualizzaPermessiUtentiStandard.do" method="post">
			<input type="hidden" name="metodo" id="metodo" value="apri" />
			<input type="hidden" name="codgar" id="codgar" value="" />
			<input type="hidden" name="genereGara" id="genereGara" value="" />
			<input type="hidden" name="permessimodificabili" id="permessimodificabili" value="" />
			<input type="hidden" name="codein" id="codein" value="${sessionScope.uffint}" />
		</form>

<gene:javaScript>
	
	function apriGestionePermessi(codgar, genereGara, permessoModifica) {
		bloccaRichiesteServer();
		formVisualizzaPermessiUtenti.codgar.value = codgar;
		formVisualizzaPermessiUtenti.genereGara.value = genereGara;
		formVisualizzaPermessiUtenti.permessimodificabili.value = permessoModifica;
		formVisualizzaPermessiUtenti.submit();
	}

	function apriGestionePermessiStandard(codgar, genereGara, permessoModifica) {
		bloccaRichiesteServer();
		formVisualizzaPermessiUtentiStandard.codgar.value = codgar;
		formVisualizzaPermessiUtentiStandard.genereGara.value = genereGara;
		formVisualizzaPermessiUtentiStandard.permessimodificabili.value = permessoModifica;
		formVisualizzaPermessiUtentiStandard.submit();
	}
	
	var idconfi = "${idconfi}";
	
	function schedaNuovaGara(){
		<c:if test="${ genere == '10'}">
			document.forms[0].action += "&tipoGara=garaLottoUnico&garaPerElenco=1";
		</c:if>
		<c:if test="${ genere == '20'}">
			document.forms[0].action += "&tipoGara=garaLottoUnico&garaPerCatalogo=1";
		</c:if>
		schedaNuovo();
	}

	function calcolaTipoEle(){
		var isLavoriChecked = false;
		if(document.getElementById("checkLavori")!=null){
			isLavoriChecked = document.getElementById("checkLavori").checked;
		}
		var isFornitureChecked = document.getElementById("checkForniture").checked;
		var isServiziChecked = document.getElementById("checkServizi").checked;

		var result = 0;
		if(isLavoriChecked)
			result += 100;
		if(isFornitureChecked)
			result += 10;
		if(isServiziChecked)
			result += 1;

		if(result > 0)
			setValue("GAREALBO_TIPOELE", result);
		else
			setValue("GAREALBO_TIPOELE", "");
	}
	
	function listaDiscussioni(){
		var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=geneweb/w_discuss_p/w_discuss_p-lista.jsp&entita=" + document.forms[0].entita.value + "&chiave=" + document.forms[0].key.value;
		document.location.href = href;
	}
	
	function controlloTermineValidita(dataTermine){
		var msg = "La data termine validita' deve essere maggiore della data inizio validita'";
		var dataInizio=getValue('GAREALBO_DINIZVAL');
		if (dataInizio != null && dataInizio!="" && dataTermine!= null && dataTermine!= ""){
			dataInizio = toDate(dataInizio).getTime();
			dataTermine = toDate(dataTermine).getTime();
			if (dataInizio >= dataTermine){
				alert(msg);
				setValue("GAREALBO_DTERMVAL",getOriginalValue("GAREALBO_DTERMVAL"));
			}
		}
		
	}
	
	function controlloInizioValidita(dataInizio){
		var msg = "La data inizio validita' deve essere minore della data termine validita'";
		var dataTermine=getValue('GAREALBO_DTERMVAL');
		if (dataInizio != null && dataInizio!="" && dataTermine!= null && dataTermine!= ""){
			dataInizio = toDate(dataInizio).getTime();
			dataTermine = toDate(dataTermine).getTime();
			if (dataInizio >= dataTermine){
				alert(msg);
				setValue("GAREALBO_DINIZVAL",getOriginalValue("GAREALBO_DINIZVAL"));
			}
		}
		
	}

	function leggiComunicazioni() {
		var entitaWSDM="GARE";
		var chiaveWSDM=getValue("GARE_NGARA");
		var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=geneweb/w_invcom/w_invcom-in-lista.jsp&genere=${genere}&chiave=" + document.forms[0].key.value;
		href+="&entitaWSDM=" + entitaWSDM + "&chiaveWSDM=" + chiaveWSDM;
		if(idconfi){
			href = href + "&idconfi="+idconfi;
		}
		document.location.href = href;
	}

	function inviaComunicazioni() {
		var entitaWSDM="GARE";
		var chiaveWSDM=getValue("GARE_NGARA");
		var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=geneweb/w_invcom/w_invcom-lista.jsp&genere=${genere}&entita=" + document.forms[0].entita.value + "&chiave=" + document.forms[0].key.value;
		href+="&entitaWSDM=" + entitaWSDM + "&chiaveWSDM=" + chiaveWSDM;
		if(idconfi){
			href = href + "&idconfi="+idconfi;
		}
		document.location.href = href;
	}
	
	<c:if test="${ genere == '10'}">
		function visibilitaTCONGELAMENTO(tipologia,sbianca){
			if(tipologia==2){
				showObj("rowGAREALBO_TCONGELAMENTO", true);
			}else{
				showObj("rowGAREALBO_TCONGELAMENTO", false);
				if(sbianca=="true")
					setValue("GAREALBO_TCONGELAMENTO","");
			}
		}
		
		var TIPOLOGIA = getValue("GAREALBO_TIPOLOGIA");
		visibilitaTCONGELAMENTO(TIPOLOGIA,"false");
	</c:if>
	
	function nuovaComunicazione(){
		var IsW_CONFCOMPopolata = "${IsW_CONFCOMPopolata}";
		var tipo = "${genere}";
		var numeroGara = getValue("GARE_NGARA");
		var keyAdd = "W_INVCOM.COMKEY1=T:" + numeroGara;
		var keyParent = "GARE.NGARA=T:" + numeroGara;
		var entitaWSDM="GARE";
		var chiaveWSDM=getValue("GARE_NGARA");
		var href = "";
		if(IsW_CONFCOMPopolata == "true"){
			href = contextPath + "/pg/InitNuovaComunicazione.do?genere=" + tipo + "&keyAdd=" + keyAdd+"&keyParent=" + keyParent + "&chiaveWSDM=" + chiaveWSDM + "&entitaWSDM=" + entitaWSDM;
		}else{
			href = contextPath + "/Lista.do?numModello=0&keyAdd=" + keyAdd ;
			href += "&keyParent=" + keyParent + "&metodo=nuovo&entita=W_INVCOM&jspPathTo=/WEB-INF/geneweb/w_invcom/w_invcom-scheda.jsp&chiaveWSDM=" + chiaveWSDM + "&entitaWSDM=" + entitaWSDM;
		}
		if(idconfi){
			href = href + "&idconfi="+idconfi;
		}
		document.location.href = href + "&" + csrfToken;
	}
	
	function apriExportDocumenti(codgar) {
		var comando = "href=gare/commons/popup-richiesta-export-documenti.jsp?codgar=" + codgar + "&genere=${genere}";
	 	openPopUpCustom(comando, "exportDocumenti", 700, 350, "yes", "yes");
	}
	
	function apriArchiviaDocumenti(codgar) {
		bloccaRichiesteServer();
		formVisualizzaDocumenti.codice.value = getValue("GARE_NGARA");
		formVisualizzaDocumenti.codgar.value = codgar;
		formVisualizzaDocumenti.genere.value = "${param.genere}";
		formVisualizzaDocumenti.entita.value = "GARE";
		formVisualizzaDocumenti.key1.value = getValue("GARE_NGARA");
		formVisualizzaDocumenti.chiaveOriginale.value = getValue("GARE_NGARA");
		formVisualizzaDocumenti.submit();
	}
	
	<c:if test='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.UFFDET") and garaLottoUnico}'>
		function gestioneVisualizzazioneUffedt(cenint,settaValore){
			if(cenint==null || cenint==""){
				setValue("TORN_UFFDET","");
				showObj("rowTORN_UFFDET",false);
			}else{
				showObj("rowTORN_UFFDET",true);
				
					 $.ajax({
	                    type: "GET",
	                    dataType: "json",
	                    async: false,
	                    beforeSend: function(x) {
	        			if(x && x.overrideMimeType) {
	            			x.overrideMimeType("application/json;charset=UTF-8");
					       }
	    				},
	                    url: "${pageContext.request.contextPath}/pg/ListavaloriSettori.do",
	                    data: "codein=" + cenint,
	                    success: function(data){
	                    	if (data==null || (data!=null  && data.length==0)) {
	                   			showObj("rowTORN_UFFDET",false);
	                   			setValue("TORN_UFFDET","");
	                        } else {
	                        	<c:if test='${modo ne "VISUALIZZA"}'>
		                        	var valoreIniziale=getValue("TORN_UFFDET");
		                        	//Si sbiancano tutti i valori del campo e poi si inseriscono solo quelli della tabella UFFSET
		                        	$('#TORN_UFFDET').empty();
									//Si inserisce la riga vuota
		                        	var option = new Option("", "");
		                        	$('#TORN_UFFDET').append($(option));
		                        	$.map( data, function( item ) {
										$("#TORN_UFFDET").append($("<option/>", {value: item[0], text: item[1] }));
									});
									if(settaValore=="1")
										setValue("TORN_UFFDET",valoreIniziale);
									else
										setValue("TORN_UFFDET","");		
								</c:if>
							}
	                    },
	                    error: function(e){
	                        alert("Errore nell'estrazione dei settori attivi per la stazione appaltante:" + cenint);
	                    }
	                });
                
			}
		}
		
		var cenint=getValue("TORN_CENINT")
		gestioneVisualizzazioneUffedt(cenint,"1");
	</c:if>
	
</gene:javaScript>

		<form name="formVisualizzaDocumenti" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
			<input type="hidden" name="href" value="gare/commons/archivia-documenti-scheda.jsp" /> 
			<input type="hidden" name="codice" id="codice" value="" />
			<input type="hidden" name="codgar" id="codgar" value="" />
			<input type="hidden" name="genere" id="genere" value="" />
			<input type="hidden" name="entita" value="" />
			<input type="hidden" name="key1" value="" />
			<input type="hidden" name="chiaveOriginale" value="" />
			<input type="hidden" name="gartel" value="2" />
			<input type="hidden" name="idconfi" value="${idconfi}" />
		</form>
		
<form name="formwsdm" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
	<input type="hidden" name="href" value="gare/wsdm/wsdm-scheda.jsp" /> 
	<input type="hidden" name="entita" value="GARE" />
	<input type="hidden" name="key1" value="${datiRiga.GARE_NGARA}" />
	<input type="hidden" name="key2" value="" /> 
	<input type="hidden" name="key3" value="" />
	<input type="hidden" name="key4" value="" />
	<input type="hidden" name="metodo" value="apri" />
	<input type="hidden" name="activePage" value="0" />
	<input type="hidden" name="genereGara" value="${genere}" />
	<input type="hidden" name="idconfi" value="${idconfi}" />
	<input type="hidden" name="autorizzatoModifiche" value="${autorizzatoModifiche }" />
</form> 
