<%
/*
 * Created on: 13/11/2006
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

<jsp:include page="/WEB-INF/pages/commons/defCostantiAppalti.jsp" />

<%--
Viene sbiancata la variabile di sessione keyParentComunicazioni che viene inizializzata nella lista delle comunicazioni.
Se si crea una nuova comunicazione senza passare dalla lista delle comunicazioni la variabile altrimenti rimane valorizzata.
 --%>
<c:set var="keyParentComunicazioni" value="" scope="session"/>

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>
<gene:redefineInsert name="head">
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.pg.titoli.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.pg.hrefProtocollo.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>	
</gene:redefineInsert>

<c:set var="abilitazioneGare" value="${sessionScope.profiloUtente.abilitazioneGare}" />
<c:set var="isPersonalizzazioneGenovaAttiva" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.IsPersonalizzazioneGenovaAttivaFunction",pageContext)}' scope="request" />

<c:set var="integrazioneProgrammazione" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneProgrammazioneFunction", pageContext)}'/>

<c:set var="correttiviDefault" value="${gene:callFunction('it.eldasoft.sil.pg.tags.funzioni.GetCorrettiviDefaultFunction', pageContext)}" />
<c:set var="correttivoLavori" value="${fn:split(correttiviDefault, '#')[0]}"/>
<c:set var="correttivoFornitureServizi" value="${fn:split(correttiviDefault, '#')[1]}"/>

<c:set var="codiceGara" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetCodiceGaraFunction", pageContext)}' />
<c:set var="isVecchiaOepv" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.isVecchiaOEPVFunction", pageContext, codiceGara)}' />

<c:set var="isCodificaAutomatica" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.IsCodificaAutomaticaFunction", pageContext, "TORN", "CODGAR")}'/>

<c:set var="isSimapAbilitato" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.IsSimapAbilitatoFunction",  pageContext)}' scope="request"/>
<c:set var="log" value="${param.log}"/> 

<c:set var="esisteClassificaForniture" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.EsisteClassificaCategoriaFunction", pageContext, "TAB2","G_z07")}'/>
<c:set var="esisteClassificaServizi" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.EsisteClassificaCategoriaFunction", pageContext, "TAB2","G_z08")}'/>
<c:set var="esisteClassificaLavori150" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.EsisteClassificaCategoriaFunction", pageContext, "TAB2","G_z11")}'/>
<c:set var="esisteClassificaServiziProfessionali" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.EsisteClassificaCategoriaFunction", pageContext, "TAB2","G_z12")}'/>
<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.inserimentoRdaSMAT")}'>
	<c:set var="numeroRda" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.InizializzaAttoAutorizzativoFunction", pageContext, numeroRda)}'/>
</c:if>

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>

<c:set var="valoreTabellatoA1115" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoA1115Function", pageContext, "1")}'/>
<c:if test="${empty valoreTabellatoA1115 }">
	<c:set var="valoreTabellatoA1115" value="2"/>
</c:if>

<c:set var="valoreTabellatoA1115Compreq" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoA1115Function", pageContext, "3")}'/>
<c:if test="${empty valoreTabellatoA1115Compreq }">
	<c:set var="valoreTabellatoA1115Compreq" value="2"/>
</c:if>

<c:if test='${tipologiaGara == "3"}'>
	<c:set var="valoreTabellatoA1115Modcont" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoA1115Function", pageContext, "4")}'/>
	<c:if test="${empty valoreTabellatoA1115Modcont }">
		<c:set var="valoreTabellatoA1115Modcont" value="1"/>
	</c:if>
</c:if>

<c:set var="valoreTabellatoA1115Ricmano" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoA1115Function", pageContext, "5")}'/>
<c:if test="${empty valoreTabellatoA1115Ricmano }">
	<c:set var="valoreTabellatoA1115Ricmano" value="1"/>
</c:if>

<c:set var="valoreTabellatoA1115Settore" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoA1115Function", pageContext, "6")}'/>
<c:if test="${empty valoreTabellatoA1115Settore }">
	<c:set var="valoreTabellatoA1115Settore" value="O"/>
</c:if>

<c:choose>
	<c:when test='${modo eq "NUOVO"}'>
		<c:set var="codgar" value='' />
	</c:when>
	<c:otherwise>
		<c:set var="codgar" value='${gene:getValCampo(key, "CODGAR")}' />
	</c:otherwise>
</c:choose>

<c:set var="propertyCig" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction",  urlWsCig)}' scope="request"/>
<c:if test="${! empty propertyCig}">
	<c:set var="isCigAbilitato" value='1' scope="request"/>
</c:if>
<c:set var="propertySimog" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction",  urlWsSimog)}' scope="request"/>
<c:if test="${! empty propertySimog}">
	<c:set var="isSimogAbilitato" value='1' scope="request"/>
</c:if>	

<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.RichiestaCIG") and not (isCigAbilitato eq "1" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.InviaDatiCIG"))}'>
<c:set var="esisteAnagraficaSimog" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsisteAnagraficaSimogFunction", pageContext, codgar)}'/>
</c:if>



<c:set var="esisteDittaNonValorizzato" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.ControlloDittaLottiFunction", pageContext, codgar, tipologiaGara)}'/>

<c:if test='${modo ne "VISUALIZZA"}'>
	<c:set var="tabellatoA1z04" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTabellatoA1z0Function",  pageContext,"A1z04")}' scope="request"/>
</c:if>

<c:set var="integrazioneVigilanza" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.sil.pg.vigilanza.ws.url")}'/>
<c:if test='${!empty integrazioneVigilanza and integrazioneVigilanza != ""}'>
	<c:set var="nomeApplicativo" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.sil.pg.vigilanza.nomeApplicativo")}'/>
</c:if>

<c:set var="tipoPubSitoIstituzionale" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", tipoPubblicazioneSitoIstituzionale)}'/>

<c:if test='${modo eq "VISUALIZZA" || metodo ne "modifica" }'>
		<c:set var="IsW_CONFCOMPopolata" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsW_CONFCOMPopolataFunction",pageContext,"3")}' />
</c:if>

<c:if test='${tipologiaGara == "3"}'>
	<c:choose>
		<c:when test="${!empty param.modalitaPresentazione and (param.modalitaPresentazione eq 1 or param.modalitaPresentazione eq 3)}">
			<c:set var="valoreInizializzazioneBustalotti" value="1"/>
		</c:when>
		<c:otherwise>
			<c:set var="valoreInizializzazioneBustalotti" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoA1115Function", pageContext, "2")}'/>
			<c:if test="${empty valoreInizializzazioneBustalotti }">
				<c:set var="valoreInizializzazioneBustalotti" value="1"/>
			</c:if>
		</c:otherwise>
	</c:choose>
</c:if>

<c:if test='${modo ne "MODIFICA" && modo ne "NUOVO"}'>
	<c:set var="integrazioneWSDM" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSDNFunction", pageContext, codiceGara,idconfi)}' />
	
	<c:set var="integrazioneWSDMDocumentale" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSDNDocumentaleFunction", pageContext, codgar,idconfi)}' />
	<c:set var="isFascicoloDocumentaleAbilitato" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.IsFascicoloDocumentaleValidoFunction",  pageContext, codgar,idconfi)}' />
	
	<c:if test='${tipologiaGara eq "3"}'>
		${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsistonoComunicazioniFS10_FS11_Stato9Function", pageContext,codgar)}
	</c:if>	
		
	
</c:if>

<c:if test='${modo ne "MODIFICA" && modo ne "NUOVO" && gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.IntegrazioneProgrammazione") && integrazioneProgrammazione eq "1"}'>
		 <c:set var="conteggioRDARDI" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.GetRDARDIFunction", pageContext, "rdaCollegate",codgar,null,null)}' />
</c:if>
	

<%/* Dati generali della gara */%>
<gene:formScheda entita="TORN" gestisciProtezioni="true" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreDatiGeneraliTorn" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreTORN">
	
	<c:set var="garaQformOfferte" value="${param.modalitaPresentazione eq '3' or datiRiga.TORN_OFFTEL eq '3'}"/>
	
	<%/* Viene riportato tipoGara, in modo tale che, in caso di errori e riapertura della pagina, 
     venga riaperta considerando il valore definito inizialmente per la prima apertura della pagina */%>
	<input type="hidden" name="tipoGara" value="${param.tipoGara}" />
	<input type="hidden" name="idconfi" value="${param.idconfi}" />
	<input type="hidden" name="arrRda" value="${param.arrRda}" />
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
	<gene:redefineInsert name="schedaConferma">
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:conferma();" title="Salva modifiche" tabindex="1501">
					${gene:resource("label.tags.template.dettaglio.schedaConferma")}</a></td>
		</tr>
	</gene:redefineInsert>
	
	<c:set var="bloccoPubblicazionePortaleBando" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoPubblicazionePortaleFunction", pageContext,gene:getValCampo(key, "TORN.CODGAR"),"BANDO","false")}' />
	<c:choose>
		<c:when test='${tipologiaGara == "3"}'>
			<c:set var="controlloTuttiLotti" value="false"/>
		</c:when>
		<c:otherwise>
			<c:set var="controlloTuttiLotti" value="true"/>
		</c:otherwise>
	</c:choose>
	<c:set var="bloccoPubblicazionePortaleEsito" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoPubblicazionePortaleFunction", pageContext,gene:getValCampo(key, "TORN.CODGAR"),"ESITO",controlloTuttiLotti)}' />
	<c:if test="${!isProceduraTelematica }">
		<c:set var="applicareBloccoPubblicazioneGareNonTelematiche" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoFunction", pageContext,"A1153","1","true")}' />
	</c:if>
	<c:set var="bloccoModificatiDati" value='${(isProceduraTelematica || applicareBloccoPubblicazioneGareNonTelematiche eq "1") && (bloccoPubblicazionePortaleEsito eq "TRUE" || bloccoPubblicazionePortaleBando eq "TRUE")}' />
	<gene:campoScheda visibile="${ bloccoModificatiDati eq 'true' and modo eq 'MODIFICA'}">
		<td colspan="2" style="color:#0000FF">
		<br><b>ATTENZIONE:</b>&nbsp;
		Parte dei dati sono in sola consultazione perché la gara è pubblicata su portale Appalti<br>&nbsp;
		</td>	
	</gene:campoScheda>
	<c:choose>
		<c:when test='${bloccoModificatiDati}'>
			<c:set var="modificaLabel" value='Modifica dopo pubblicazione' />
			<c:set var="bloccoModificaPubblicazione" value='TRUE' />
			<c:set var="bloccoModificatiDati" value='true' />
		</c:when>
		<c:otherwise>
			<c:set var="modificaLabel" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' />
			<c:set var="bloccoModificaPubblicazione" value='FALSE' />
			<c:set var="bloccoModificatiDati" value='false' />
		</c:otherwise>
	</c:choose>	
	
	<c:if test='${bloccoModificatiDati }'>
	 	<gene:redefineInsert name="schedaModifica"></gene:redefineInsert>
	 </c:if>
	
	<gene:redefineInsert name="addToAzioni" >
		<c:if test='${modo eq "VISUALIZZA" and bloccoModificatiDati and gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD") and autorizzatoModifiche ne "2"}' >
		  <tr>
			  <td class="vocemenulaterale" >
					  <a href="javascript:schedaModifica();" title="${modificaLabel}">
				  ${modificaLabel}</a>
			  </td>
		  </tr>
	   </c:if>	
	   <c:if test='${modo eq "VISUALIZZA" && gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_TORN-lista.Condividi-gara")}'>
		<c:choose>
		<c:when test='${tipologiaGara == "3" and datiRiga.TORN_GARTEL eq 1}'>
		  <tr>
		     <td class="vocemenulaterale">
		     	<c:if test='${isNavigazioneDisattiva ne "1"}'>
		     		<c:choose>
		     			<c:when test='${isPersonalizzazioneGenovaAttiva eq "1"}'>
		     				<a href="javascript:apriGestionePermessiGaraTelematica('${datiRiga.TORN_CODGAR}', ${tipologiaGara});" title="Punto ordinante e istruttore" tabindex="1503">
		     			</c:when>
		     			<c:otherwise>
		     				<a href="javascript:apriGestionePermessiGaraTelematicaStandard('${datiRiga.TORN_CODGAR}', ${tipologiaGara});" title="Punto ordinante e istruttore" tabindex="1503">
		     			</c:otherwise>
		     		</c:choose>
					</c:if>
					Punto ordinante e istruttore
					<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>			  
				</td>
			</tr>
		</c:when>
		<c:otherwise>
			<tr>    
				<td class="vocemenulaterale">
					<c:choose>
						<c:when test='${isPersonalizzazioneGenovaAttiva eq "1"}'>
							<a href="javascript:apriGestionePermessi('${datiRiga.TORN_CODGAR}', ${tipologiaGara},${datiRiga.G_PERMESSI_PROPRI eq '1' or abilitazioneGare eq 'A'})" tabindex="1503">
						</c:when>
						<c:otherwise>
							<a href="javascript:apriGestionePermessiStandard('${datiRiga.TORN_CODGAR}', ${tipologiaGara},${datiRiga.G_PERMESSI_PROPRI eq '1' or abilitazioneGare eq 'A'})" tabindex="1503">
						</c:otherwise>
					</c:choose>
					Condividi e proteggi gara
					<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>			  
				</td>
			</tr>
		</c:otherwise>
		</c:choose>	
		</c:if>	
		<c:if test='${modo eq "VISUALIZZA" && gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.IntegrazioneProgrammazione") && integrazioneProgrammazione eq "1"}'>
			<tr>
				<td class="vocemenulaterale">
					<a href="javascript:getListaRdaRdi();" title='Gestisci RdA' tabindex="1514">
						Gestisci RdA/RdI ${conteggioRDARDI > 0 ? ('('.concat(conteggioRDARDI).concat(')')) : ''}
					</a>
				</td>
			</tr>
		</c:if>
		<c:if test='${modo eq "VISUALIZZA" and autorizzatoModifiche ne "2" and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.RettificaTermini") and 
			(datiRiga.TORN_ITERGA eq 1 or datiRiga.TORN_ITERGA eq 2 or datiRiga.TORN_ITERGA eq 4)
			and (datiRiga.TORN_GARTEL ne 1 or (datiRiga.TORN_GARTEL eq 1 and bloccoPubblicazionePortaleBando eq "TRUE"))}'>
		  	<c:set var="esisteFunzioneRettificaTermini" value="true" scope="request"/>
		  	<tr>    
	    	  <td class="vocemenulaterale">
		      	<c:if test='${isNavigazioneDisattiva ne "1"}'>
						<a href="javascript:popupRettificaTermini(${datiRiga.TORN_ITERGA },'${datiRiga.TORN_CODGAR}','${datiRiga.TORN_GARTEL}');" title="Rettifica termini di gara" tabindex="1504">
					</c:if>
					  Rettifica termini di gara
					<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>			  
				</td>
			</tr>
		</c:if>
		<c:if test='${modo eq "VISUALIZZA" and gene:checkProtFunz(pageContext, "MOD","MOD") and autorizzatoModifiche ne "2" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.ImpostaGaraNonAggiud")}'>
		  	<tr>    
		      <td class="vocemenulaterale">
			      	<c:if test='${isNavigazioneDisattiva ne "1" }'>
							<a href="javascript:impostaGaraNonAggiudicata();" title="Imposta gara non aggiudicata" tabindex="1505">
					</c:if>
					  Imposta gara non aggiudicata
					<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>			  
				</td>
			</tr>
		</c:if>
		<c:if test='${modo eq "VISUALIZZA" and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE.exportXMLAntimafia") and tipologiaGara == "3"}'>
			<tr>
				<td class="vocemenulaterale" >
					<c:if test='${isNavigazioneDisattiva ne "1"}'>
						<a href="javascript:esportaAntimafiaVerifica();" title="Esporta ditte partecipanti alla gara per verifica interdizione" tabindex="1506">
					</c:if>
						Esporta ditte per verifica interdizione
					<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
				</td>
			</tr>
		</c:if>
		<c:if test='${modo eq "VISUALIZZA" and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.AutovieIds")}'>
	  		<tr>    
	    	  <td class="vocemenulaterale" >
		      	<c:if test='${isNavigazioneDisattiva ne "1"}'>
						<a href="javascript:listaIds();" title="Collega Ids" tabindex="1507">
				</c:if>
				  Collega Ids
				<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>			  
			</td>
			</tr>
		</c:if>
		<c:if test='${modo eq "VISUALIZZA"}'>
			<c:choose>
			<c:when test='${isCigAbilitato eq "1" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.InviaDatiCIG")}'>
				<tr>
					<td class="vocemenulaterale" >
						<c:if test='${isNavigazioneDisattiva ne "1"}'>
							<a href="javascript:popupInviaDatiCig('${datiRiga.TORN_CODGAR}');" title="Invia dati per richiesta CIG" tabindex="1509">
						</c:if>
							Invia dati richiesta CIG
						<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
					</td>
				</tr>
			</c:when>
			<c:otherwise>
				<c:choose>
				<c:when test='${isSimogAbilitato eq "1" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.RichiestaCIG")}'>
					<tr>
						<td class="vocemenulaterale" >
							<c:if test='${isNavigazioneDisattiva ne "1"}'>
								<a href="javascript:apriFormRichiestaCig();" title='Richiesta CIG' tabindex="1510">
							</c:if>
								Richiesta CIG
							<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
						</td>
					</tr>
				</c:when>
				</c:choose>
			</c:otherwise>
			</c:choose>
		</c:if>
		
		<c:if test='${modo eq "VISUALIZZA" and bloccoPubblicazionePortaleBando eq "TRUE" and isProceduraTelematica and autorizzatoModifiche ne "2" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.SospendiGara")}'>
			<c:if test="${empty datiRiga.GARSOSPE_ID}" >
				<tr>
					<td class="vocemenulaterale" >
						<c:if test='${isNavigazioneDisattiva ne "1"}'>
							<a href="javascript:sospendiGara('${codgar}','${datiRiga.TORN_ITERGA}','1');" title="Sospendi gara" tabindex="1511">
						</c:if>
							Sospendi gara
						<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
					</td>
				</tr>
			</c:if>
			<c:if test="${!empty datiRiga.GARSOSPE_ID}" >
				<tr>
					<td class="vocemenulaterale" >
						<c:if test='${isNavigazioneDisattiva ne "1"}'>
							<a href="javascript:sospendiGara('${codgar}','${datiRiga.TORN_ITERGA}','2');" title="Riprendi gara sospesa" tabindex="1511">
						</c:if>
							Riprendi gara sospesa
						<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
					</td>
				</tr>
			</c:if>
		</c:if>	
		
		<c:if test='${modo eq "VISUALIZZA" and isSimapAbilitato eq "1" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE.InviaBandoAvviso")}'>
	      <td class="vocemenulaterale">
		      	<c:if test='${isNavigazioneDisattiva ne "1"}'>
						<a href="javascript:popupInviaBandoAvvisoSimap('${datiRiga.TORN_CODGAR}','${datiRiga.TORN_ITERGA}','${datiRiga.TORN_SETTORE}');" title="Crea formulari GUUE" tabindex="1512">
				</c:if>
				  Crea formulari GUUE
				<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>			  
			</td>
		</c:if>
		<c:if test='${modo eq "VISUALIZZA" and !empty integrazioneVigilanza and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE.InviaDatiVigilanza")}'>
		  <tr>    
		      <td class="vocemenulaterale">
			      	<c:set var="voceMenu" value="Invia dati a Vigilanza"/>
			      	<c:if test="${!empty nomeApplicativo and nomeApplicativo!=''}">
			      		<c:set var="voceMenu" value="Invia dati a ${nomeApplicativo }"/>
			      	</c:if>
			      	<c:if test='${isNavigazioneDisattiva ne "1"}'>
							<a href="javascript:popupInviaVigilanza('${datiRiga.TORN_CODGAR}');" title="${voceMenu }" tabindex="1512">
					</c:if>
					  ${voceMenu }
					<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>			  
				</td>
			</tr>
		</c:if>
		<c:if test='${modo eq "VISUALIZZA" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE.ControDatiL190")}'>
				<tr>
					<td class="vocemenulaterale" >
						<c:if test='${isNavigazioneDisattiva ne "1"}'>
							<a href="javascript:popupControlla190('${datiRiga.TORN_CODGAR}','${id}');" title="Controllo dati ai fini di L.190/2012" tabindex="1513">
						</c:if>
							Controllo dati ai fini di L.190/2012
						<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
					</td>
				</tr>
		</c:if>

		<c:if test='${modo eq "VISUALIZZA" && integrazioneWSERP eq "1" && tipoWSERP eq "AVM"}'>
		     <c:if test='${!bloccoModificatiDati }'>
				<c:if test='${autorizzatoModifiche ne "2" and (visListaLavForn eq "false" and tipologiaGara ne "1") or datiRiga.TORN_ACCQUA eq "1"}'>
					<tr>
						<td class="vocemenulaterale">
							<a href="javascript:leggiCarrelloRda('${datiRiga.TORN_CODGAR}');" title='Carica RdA' tabindex="1514">
								Carica RdA
							</a>
						</td>
					</tr>
				</c:if>
		     </c:if>
		</c:if>
		

		
		
		<c:if test='${modo eq "VISUALIZZA" and ((fn:contains(listaOpzioniDisponibili, "OP114#") and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE.RiceviComunicazioni") and (tipologiaGara == "3" || tipologiaGara == "1")) || (gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE.InviaComunicazioni") and tipologiaGara == "3"))}'>
			<tr>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td class="titolomenulaterale" title='${gene:resource("label.tags.template.documenti.sezioneComunicazioni")}'>
					${gene:resource("label.tags.template.documenti.sezioneComunicazioni")}</td>
			</tr>
			<c:if test='${fn:contains(listaOpzioniDisponibili, "OP114#") and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE.RiceviComunicazioni") and (tipologiaGara == "3" || tipologiaGara == "1")}'>
				<tr>
					<td class="vocemenulaterale" >
						<c:if test='${isNavigazioneDisattiva ne "1"}'>
							<a href="javascript:leggiComunicazioni();" title="${gene:resource('label.tags.template.documenti.comunicazioniRicevute.tooltip')}" tabindex="1515">
						</c:if>
						${gene:resource('label.tags.template.documenti.comunicazioniRicevute')}
						<c:set var="numComunicazioniRicevuteNonLette" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetNumComunicazioniRicevuteDaLeggereFunction", pageContext, gene:getValCampo(key, "CODGAR"))}' />
						<c:if test="${numComunicazioniRicevuteNonLette > 0}">(${numComunicazioniRicevuteNonLette} ${gene:resource('label.tags.template.documenti.comunicazioniRicevute.indicatore')})</c:if>
						<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
					</td>
				</tr>
			</c:if>		
			<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE.InviaComunicazioni") and tipologiaGara == "3"}'>
				<tr>
					<td class="vocemenulaterale" >
						<c:if test='${isNavigazioneDisattiva ne "1"}'>
							<a href="javascript:inviaComunicazioni();" title="${gene:resource('label.tags.template.documenti.inviaComunicazioni.tooltip')}" tabindex="1516">
						</c:if>
						${gene:resource('label.tags.template.documenti.inviaComunicazioni')}
						<c:set var="numComunicazioniBozza" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetNumComunicazioniBozzaFunction", pageContext, "TORN", gene:getValCampo(key, "CODGAR"))}' />
						<c:if test="${numComunicazioniBozza > 0}">(${numComunicazioniBozza} ${gene:resource('label.tags.template.documenti.inviaComunicazioni.indicatore')})</c:if>
						<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
					</td>
				</tr>
				<c:if test="${autorizzatoModifiche ne '2' and gene:checkProt(pageContext,'FUNZ.VIS.INS.GENEWEB.W_INVCOM-lista.LISTANUOVO')}">
					<tr>
						<td class="vocemenulaterale" >
							<c:if test='${isNavigazioneDisattiva ne "1"}'>
								<a href="javascript:nuovaComunicazione();" title="Nuova comunicazione" tabindex="1517">
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
					<c:set var="resultConteggioDiscussioni" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetDiscussioniNumeroFunction", pageContext, "TORN", gene:getValCampo(key, "CODGAR"))}' />
					<c:if test="${numeroDiscussioni > 0}">(${numeroDiscussioni})</c:if>
					<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
				</td>
			</tr>
		</c:if>

	</gene:redefineInsert>
	
	<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-scheda.jsp">
			<jsp:param name="entita" value="V_GARE_TORN"/>
			<jsp:param name="inputFiltro" value="${key}"/>
			<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
		</jsp:include>	
	
	<gene:redefineInsert name="addToDocumenti" >
		<c:if test='${modo eq "VISUALIZZA" and fn:contains(listaOpzioniDisponibili, "OP128#") and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GENE.G_SCADENZ")}'>
			<tr>
				<c:choose>
					<c:when test='${isNavigazioneDisattiva ne "1"}'>
						<td class="vocemenulaterale">
							<a href='javascript:scadenzario("TORN", document.forms[0].key.value, ${sessionScope.entitaPrincipaleModificabile}, "BANDO")' title="Scadenzario attività" tabindex="1522">
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
		<c:set var="creaFascicolo" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.GetPropertyWsdmFunction", accessoCreaFascicolo,idconfi)}'/>
		<c:if test='${modo eq "VISUALIZZA"  and autorizzatoModifiche ne "2" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.CreaFascicolo") and integrazioneWSDM eq "1" and creaFascicolo eq "1"}'>
			<c:choose>
				<c:when test='${tipologiaGara == "3"}'>
					<c:set var="entitaWSDM" value="GARE"/>
					<c:set var="genereW" value="3"/>
				</c:when>
				<c:otherwise>
					<c:set var="entitaWSDM" value="TORN"/>
					<c:set var="genereW" value="1"/>
				</c:otherwise>
			</c:choose>
			<c:set var="esisteFascicoloAssociato" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteFascicoloAssociatoFunction", pageContext, entitaWSDM, datiRiga.TORN_CODGAR,idconfi)}' scope="request"/>
			<c:set var="tipoWSDM" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetTipoWSDMFunction", pageContext, "FASCICOLOPROTOCOLLO", "NO",idconfi)}' />
			<c:if test="${ esisteFascicoloAssociato eq 'true' and tipoWSDM eq 'LAPISOPERA' }">
				<c:set var="esisteDocumentoAssociato" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteDocumentoAssociatoFunction", pageContext, entitaWSDM, datiRiga.TORN_CODGAR)}' scope="request"/>
			</c:if>
			<c:if test='${(esisteFascicoloAssociato ne "true" and  (tipoWSDM eq "IRIDE" or tipoWSDM eq "JIRIDE" or tipoWSDM eq "ENGINEERING" 
				or tipoWSDM eq "ARCHIFLOW" or tipoWSDM eq "INFOR" or tipoWSDM eq "JPROTOCOL" or tipoWSDM eq "JDOC" or tipoWSDM eq "ENGINEERINGDOC" or tipoWSDM eq "ITALPROT" or tipoWSDM eq "LAPISOPERA")) 
				|| (tipoWSDM eq "LAPISOPERA" and esisteDocumentoAssociato ne "true")}' >
			
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
								<a href="javascript:apriCreaFascicolo('${datiRiga.TORN_CODGAR}','${datiRiga.TORN_CODGAR}','${idconfi}',${genereW },'${fascicoloEsistente}');" title="${titoloMessaggio }" tabindex="1515">
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
		
		<c:set var="exportDocumenti" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.sil.pg.pathArchivioDocumentiGara")}'/>
		<c:set var="exportCOS" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "cos.sftp.url")}'/>
		<c:if test='${modo eq "VISUALIZZA" and 
			((integrazioneWSDMDocumentale eq "1" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.TrasferisciAlDocumentale"))
            or (!empty exportCOS and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.TrasferisciCos"))
			or (!empty exportDocumenti and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.ExportDocumenti")))}'>
			<tr>
				<c:choose>
					<c:when test='${isNavigazioneDisattiva ne "1"}'>
						<td class="vocemenulaterale">
							<a href="javascript:apriArchiviaDocumenti('${datiRiga.TORN_CODGAR}');" title="Archivia documenti" tabindex="1515">
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
		<c:if test='${modo eq "VISUALIZZA" and isFascicoloDocumentaleAbilitato eq "1" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FascicoloDocumentale") }'>
			<tr>
				<c:choose>
					<c:when test='${isNavigazioneDisattiva ne "1"}'>
						<td class="vocemenulaterale">
							<a href="javascript:apriFascicoloDocumentale();" title="Fascicolo documentale" tabindex="1516">
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
		
	<c:set var="msgChiaveErrore">	
		<fmt:message key="match.tags.template.dettaglio.campoChiave.messaggio">
			<fmt:param value="$"/>
		</fmt:message>
	</c:set>
	
	<gene:gruppoCampi idProtezioni="GEN">
		<gene:campoScheda>
			<td colspan="2"><b>Dati generali</b></td>
		</gene:campoScheda>

		<c:choose>
			<c:when test='${isCodificaAutomatica eq "false"}'>
				<c:choose>
					<c:when test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.PersonalizzazioneAutovie")}'>
						<gene:campoScheda campo="CODGAR" modificabile="false" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoCodificaAutomatica" />
					</c:when>
					<c:otherwise>
						<gene:campoScheda campo="CODGAR" modificabile='${modoAperturaScheda eq "NUOVO"}' obbligatorio="true" >
							<gene:checkCampoScheda funzione='"##".indexOf("$") < 0' obbligatorio="true" messaggio="<c:out value='${msgChiaveErrore}' escapeXml='false'/>" />
						</gene:campoScheda>
					</c:otherwise>
				</c:choose>
			</c:when>
			<c:otherwise>
				<gene:campoScheda campo="CODGAR" modificabile='${modo eq "NUOVO"}' gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoCodificaAutomatica" />
			</c:otherwise>
		</c:choose>

		<gene:campoScheda campo="GENERE" entita="GARE" where="TORN.CODGAR = GARE.NGARA" visibile="false" />
		<gene:campoScheda campo="NUMAVCP" modificabile="${ esisteAnagraficaSimog ne 'true'}" visibile="${tipoSimog ne 'S'}"/>
		<c:if test='${tipologiaGara == "3"}'>
			<gene:campoScheda campo="DACQCIG" entita="GARE" where="TORN.CODGAR = GARE.NGARA " modificabile="${ esisteAnagraficaSimog ne 'true'}"/>
		</c:if>
		<gene:campoScheda visibile="${ esisteAnagraficaSimog eq 'true'}" campo="STATO_SIMOG" campoFittizio="true" title="Effettuata richiesta CIG?" definizione="T30;" value="${tipoSimogDesc} - ${statoSimog }" modificabile="false"/>
		<gene:campoScheda campo="UREGA" visibile='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.CodiceUrega")}' modificabile='false'/>
		<gene:campoScheda campo="CODGARCLI" visibile='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.PersonalizzazioneAutovie")}' modificabile='false'/>
		<gene:campoScheda campo="TIPGEN" modificabile='${((!empty param.tipoAppalto) && param.tipoAppalto ne 1) || datiRiga.TORN_TIPGEN ne 1}' obbligatorio="true" defaultValue="${param.tipoAppalto}" 
			gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoTIPGEN" />
		<gene:campoScheda campo="SETTORE" obbligatorio="true" defaultValue="${valoreTabellatoA1115Settore}" />
		<gene:campoScheda campo="TIPFORN" visibile="${datiRiga.TORN_TIPFORN ne 98}"/>
		<gene:campoScheda campo="DESTOR" defaultValue="${requestScope.initDESTOR}" />
		<gene:campoScheda campo="TIPGAR" obbligatorio='true' gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoTIPGAR" defaultValue="${requestScope.initTIPGAR}"/>
				
		<gene:campoScheda campo="ITERGA" defaultValue="${requestScope.initITERGA}">
			<gene:calcoloCampoScheda 
			funzione='calcolaITERGA("#TORN_TIPGAR#")' elencocampi="TORN_TIPGAR" />
		</gene:campoScheda>
		<gene:campoScheda campo="TIPNEG"/>
		<gene:campoScheda campo="ALTNEG"/>
		<gene:campoScheda campo="ACCQUA" defaultValue="2"/>
		<gene:campoScheda campo="AQOPER" obbligatorio="true"/>
		<gene:campoScheda campo="AQNUMOPE" >
			<gene:checkCampoScheda funzione='controlloCampoAqnumope("##")' obbligatorio="true" messaggio='Il valore specificato deve essere maggiore di 1.' onsubmit="false"/>
		</gene:campoScheda>
		<gene:campoScheda campo="IDCOMMALBO" visibile="false"/>
		
		<gene:campoScheda campo="CRITLIC" obbligatorio="true" />
		<gene:campoScheda campo="DETLIC" obbligatorio="true" defaultValue="${gene:if(garaQformOfferte, '4', '')}" visibile="${not garaQformOfferte}"/>
		
		<c:if test='${tipologiaGara == "3"}'>
			<c:choose>
			<c:when test="${garaQformOfferte}">
				<c:set var="inizializzazioneRibcal" value="2"/>
			</c:when>
			<c:otherwise>
				<c:set var="inizializzazioneRibcal" value="1"/>
			</c:otherwise>
		</c:choose>
			<gene:campoScheda campo="RIBCAL" entita="GARE" where="TORN.CODGAR = GARE.NGARA " obbligatorio="true" defaultValue="${inizializzazioneRibcal }"/>
			<c:if test="${ modo ne 'VISUALIZZA'}">
				<gene:fnJavaScriptScheda funzione='inizializzazioniDaRibcal("#GARE_RIBCAL#","#TORN_CRITLIC#")' elencocampi='GARE_RIBCAL' esegui="true" />
			</c:if>
		</c:if>
		
		<c:choose>
			<c:when test='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.CALCSOAN") }'>
				<c:set var="valoreInizializzazioneCalcsoan" value="1"/>
			</c:when>
			<c:otherwise>
				<c:set var="valoreInizializzazioneCalcsoan" value="2"/>
			</c:otherwise>
		</c:choose>
		<gene:campoScheda campo="CALCSOAN" obbligatorio="true" defaultValue="${valoreInizializzazioneCalcsoan}"/>
		
		<gene:campoScheda campo="CALCSOME" />
		
		<c:if test='${tipologiaGara == "3"}'>
			<gene:campoScheda campo="MODASTG" entita="GARE" where="TORN.CODGAR = GARE.NGARA " defaultValue="2" obbligatorio="true">
				<gene:checkCampoScheda funzione='(toVal("#GARE_MODASTG#") == 2 || toVal("#TORN_CALCSOAN#") == 1)' messaggio='Esclusione automatica delle offerte anomale: se non è previsto il calcolo della soglia di anomalia è possibile indicare esclusivamente il valore \"No\"' obbligatorio="true" onsubmit="true" />
			</gene:campoScheda>
		</c:if>
		
		<gene:campoScheda campo="APPLEGREG" />
		
		<c:set var="offaumDefault" value="2"/> 
		<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GarePrivateAcquisto")}'>
			<c:set var="garpriv" value="2"/> 
		</c:if>
		<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GarePrivateVendita") && not garaQformOfferte}'>
			<c:set var="garpriv" value="1"/> 
			<c:set var="offaumDefault" value="1"/> 
		</c:if>
		
		<gene:campoScheda campo="OFFAUM" defaultValue="${offaumDefault}" visibile="${not garaQformOfferte}" modificabile="true" />
		
		
		<c:if test='${tipologiaGara == "3"}'>
			<gene:campoScheda campo="SICINC" entita="GARE" where="TORN.CODGAR = GARE.NGARA " obbligatorio="true" defaultValue="1" visibile="${not garaQformOfferte}"/>
		</c:if>
		
		<gene:campoScheda campo="MODLIC" visibile="false" >
			<gene:calcoloCampoScheda 
			funzione='calcolaMODLIC("#TORN_CRITLIC#","#TORN_DETLIC#","#TORN_CALCSOAN#","#TORN_APPLEGREG#")' 
			elencocampi="TORN_CRITLIC;TORN_DETLIC;TORN_CALCSOAN;TORN_APPLEGREG" />
		</gene:campoScheda>
		
		<gene:fnJavaScriptScheda funzione='gestioneCriterioAggiudicazione("#TORN_CRITLIC#","#TORN_DETLIC#")' elencocampi='TORN_CRITLIC;TORN_DETLIC' esegui="true" />
		<gene:fnJavaScriptScheda funzione='gestioneCRITLIC("#TORN_CRITLIC#")' elencocampi='TORN_CRITLIC' esegui="false" />
		<c:if test="${not isVecchiaOepv}">	
			<gene:fnJavaScriptScheda funzione='gestioneOFFAUM("#TORN_CRITLIC#")' elencocampi='TORN_CRITLIC' esegui="true" />
		</c:if>
		<gene:fnJavaScriptScheda funzione='gestioneDETLIC("#TORN_DETLIC#")' elencocampi='TORN_DETLIC' esegui="false" />
		<gene:fnJavaScriptScheda funzione='gestioneCALCSOAN("#TORN_CALCSOAN#","true")' elencocampi='TORN_CALCSOAN' esegui="false" />
		<c:if test='${tipologiaGara == "3"}'>
			<gene:fnJavaScriptScheda funzione='gestioneFlagSicurezzaInclusa("#TORN_CRITLIC#","#TORN_DETLIC#")' elencocampi='TORN_CRITLIC;TORN_DETLIC' esegui="true" />
		</c:if>
		<gene:fnJavaScriptScheda funzione='gestioneAQOPER("#TORN_ACCQUA#")' elencocampi='TORN_ACCQUA' esegui="true" />
		<gene:fnJavaScriptScheda funzione='gestioneAQNUMOPE("#TORN_AQOPER#")' elencocampi='TORN_AQOPER' esegui="true" />
		<gene:fnJavaScriptScheda funzione='gestioneTipneg_Alteng("#TORN_ITERGA#","TORN")' elencocampi='TORN_ITERGA' esegui="true" />
		<c:if test='${modo ne "VISUALIZZA"}'>
			<gene:fnJavaScriptScheda funzione='gestioneTabellatoDETLIC("#TORN_CRITLIC#")' elencocampi='TORN_CRITLIC' esegui="true" />
		</c:if>
		
		<gene:campoScheda campo="CORGAR"/>
		<gene:campoScheda campo="GARTEL" modificabile="false" visibile='${fn:contains(listaOpzioniDisponibili, "OP114#") && fn:contains(listaOpzioniDisponibili, "OP132#")}' defaultValue="${param.proceduraTelematica}"/>
		<gene:campoScheda campo="OFFTEL" modificabile="false" visibile='${param.proceduraTelematica eq 1 || isProceduraTelematica}' defaultValue="${param.modalitaPresentazione}"/>
		<gene:campoScheda campo="IMPTOR" visibile="false" defaultValue="${requestScope.initIMPTOR}"/>
		<gene:campoScheda campo="ISTAUT" visibile="false"/>
		<gene:campoScheda campo="IMPTOR_FIT" campoFittizio="true" modificabile="false"  value="${datiRiga.TORN_IMPTOR}"  definizione="F15;;;MONEY;G1IMPTOR" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.IMPTOR")}' speciale="true">
			<gene:popupCampo titolo="Modifica importo complessivo" href="modificaImportoComplessivo();" />
		</gene:campoScheda>		
		
		<gene:campoScheda campo="VALMAX" entita="V_GARE_IMPORTI" where="TORN.CODGAR=V_GARE_IMPORTI.CODGAR and V_GARE_IMPORTI.NGARA is null" modificabile="false" />
		
		<c:if test='${tipologiaGara ne "3"}'>
			<gene:campoScheda title="Stato della gara" campo="STATOGARA" campoFittizio="true" definizione="T20" modificabile="false"/>
			<gene:campoScheda campo="ESINEG" entita="TORN" visibile="${!empty datiRiga.TORN_ESINEG }" modificabile="false"/>
			<gene:campoScheda campo="DATNEG" visibile="${!empty datiRiga.TORN_ESINEG }" modificabile="false"/>
		</c:if>
		
		<%/* Campi per GARA DIVISA IN LOTTI CON OFFERTA UNICA */%>
		<c:if test='${tipologiaGara == "3"}'>
			<gene:campoScheda campo="FASGAR" entita="GARE" where="TORN.CODGAR = GARE.NGARA"  modificabile="false"/>
			<gene:campoScheda title="Stato della gara" campo="STATOGARA" campoFittizio="true" definizione="T20" modificabile="false"/>
			<gene:campoScheda campo="ESINEG" entita="GARE" where="TORN.CODGAR = GARE.NGARA " visibile="${!empty datiRiga.GARE_ESINEG }" modificabile="false"/>
			<gene:campoScheda campo="DATNEG" entita="GARE" where="TORN.CODGAR = GARE.NGARA " visibile="${!empty datiRiga.GARE_ESINEG }" modificabile="false"/>
			<gene:campoScheda campo="NPANNREVAGG" entita="GARE1" where="TORN.CODGAR = GARE1.NGARA" visibile="${!empty datiRiga.GARE_ESINEG }" modificabile="false"/>
			<gene:campoScheda campo="NOTNEG" entita="GARE1" where="TORN.CODGAR = GARE1.NGARA"  visibile="${!empty datiRiga.GARE_ESINEG }" modificabile="false"/>
			
			<gene:campoScheda campo="ID" entita="GARSOSPE" where="GARSOSPE.CODGAR=TORN.CODGAR and GARSOSPE.DATFINE IS NULL"  visibile="false" />
			<gene:campoScheda campo="DATINI" entita="GARSOSPE" where="GARSOSPE.CODGAR=TORN.CODGAR and GARSOSPE.DATFINE IS NULL" visibile="${!empty datiRiga.GARSOSPE_ID}" modificabile="false"/>
			<gene:campoScheda campo="NOTE" entita="GARSOSPE" where="GARSOSPE.CODGAR=TORN.CODGAR and GARSOSPE.DATFINE IS NULL" visibile="${!empty datiRiga.GARSOSPE_ID}" modificabile="true"/>

			
			<gene:campoScheda campo="DRICCAPTEC" entita="GARE" where="TORN.CODGAR = GARE.NGARA " />
			<gene:campoScheda campo="CATIGA" entita="GARE" where="TORN.CODGAR = GARE.NGARA " />
		</c:if>

		<gene:campoScheda campo="ISARCHI" visibile="${datiRiga.TORN_ISARCHI eq '1'}"/>
	</gene:gruppoCampi>
	
	<gene:fnJavaScriptScheda funzione="visualizzaESettaCorrettivo('#TORN_MODLIC#', '#TORN_TIPGEN#', '#TORN_CORGAR#', 'TORN_CORGAR')" elencocampi="TORN_MODLIC" esegui="true"/>

	<gene:fnJavaScriptScheda funzione="visualizzaTIPFORN( '#TORN_TIPGEN#')" elencocampi="TORN_TIPGEN" esegui="true"/>
	<gene:fnJavaScriptScheda funzione="valorizzaStatoGara()" elencocampi="TORN_TIPGAR;TORN_DTEPAR;TORN_OTEPAR;TORN_DTEOFF;TORN_OTEOFF" esegui="true" />
	
	
	<c:if test='${modoAperturaScheda ne "VISUALIZZA" and tipologiaGara == "3"}'>
		<gene:fnJavaScriptScheda funzione='setTipoCategorie("#TORN_TIPGEN#")' elencocampi='TORN_TIPGEN' esegui="false"/>
	</c:if>
	
	<gene:fnJavaScriptScheda funzione='showOGGCONT("#TORN_TIPGEN#",true)' elencocampi='TORN_TIPGEN' esegui="false"/>
	
	<gene:campoScheda campo="TIPOLOGIA" title="Tipologia gara" entita="V_GARE_TORN" visibile='false' campoFittizio="true" value="${tipologiaGara}" definizione="N1"/>
	
	<%/* Campi per GARA DIVISA IN LOTTI CON OFFERTA UNICA */%>
	<c:if test='${tipologiaGara == "3"}'>
			<gene:campoScheda campo="NGARA" entita="GARE" visibile='false' where="TORN.CODGAR = GARE.NGARA"/>
	</c:if>
	
	<gene:gruppoCampi idProtezioni="PRIMOATAU">
		<gene:campoScheda addTr="false">
			<tr id="rowTITOLO_ATTO_AUTORIZZATIVO">
				<td colspan="2"><b>Atto autorizzativo</b></td>
			</tr>
		</gene:campoScheda>
		<gene:campoScheda campo="TATTOT" entita="TORN" defaultValue="${requestScope.initTATTOT}" />
		<gene:campoScheda campo="DATTOT" entita="TORN" defaultValue="${requestScope.initDATTOT}"/> 
		<gene:campoScheda campo="NATTOT" entita="TORN" defaultValue="${requestScope.initNATTOT}"/>
		<gene:campoScheda campo="DATRICT" entita="TORN" />
		<gene:campoScheda campo="NPROAT" entita="TORN" />
		<gene:campoScheda campo="NOTEAT" entita="TORN" />
	</gene:gruppoCampi>
		
	<c:if test='${modoAperturaScheda ne "NUOVO"}' >
		<c:set var="tmp" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GestioneAttiAutorizzativiFunction", pageContext, "GARATT", gene:getValCampo(key, "CODGAR"))}'/>
	</c:if>

	<c:choose>
		<c:when test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.inserimentoRdaSMAT")}'>
			<c:set var="dettaglioSingolo" value="/WEB-INF/pages/gare/garatt/atto-autorizzativo-rda.jsp"/>
		</c:when>
		<c:otherwise>
			<c:set var="dettaglioSingolo" value="/WEB-INF/pages/gare/garatt/atto-autorizzativo.jsp"/>
		</c:otherwise>
	</c:choose>
	<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
		<jsp:param name="entita" value='GARATT'/>
		<jsp:param name="chiave" value='${gene:getValCampo(key, "CODGAR")}'/>
		<jsp:param name="nomeAttributoLista" value='attiAutorizzativi' />
		<jsp:param name="idProtezioni" value="ATAU" />
		<jsp:param name="jspDettaglioSingolo" value="${dettaglioSingolo}"/>
		<jsp:param name="arrayCampi" value="'GARATT_CODGAR_', 'GARATT_NUMATT_', 'GARATT_TATTOT_', 'GARATT_DATTOT_', 'GARATT_NATTOT_', 'GARATT_NPROAT_', 'GARATT_DPROAA_','GARATT_NOTEAT_','GARATT_DATRICT_'"/>
		<jsp:param name="sezioneListaVuota" value="false" />
		<jsp:param name="titoloSezione" value="Altro atto autorizzativo" />
		<jsp:param name="titoloNuovaSezione" value="Nuovo atto autorizzativo" />
		<jsp:param name="descEntitaVociLink" value="atto autorizzativo" />
		<jsp:param name="msgRaggiuntoMax" value="i atti autorizzativi"/>
	</jsp:include>

	<c:if test='${modoAperturaScheda ne "NUOVO"}' >
		<c:set var="tmp" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GestioneRichiesteAcquistoFunction", pageContext, gene:getValCampo(key, "CODGAR"),"" )}'/>
	</c:if>
	<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
		<jsp:param name="entita" value='GARERDA'/>
		<jsp:param name="chiave" value='${gene:getValCampo(key, "CODGAR")}'/>
		<jsp:param name="nomeAttributoLista" value='listaRichiesteAcquisto' />
		<jsp:param name="idProtezioni" value="GARERDA" />
		<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/garerda/richieste-acquisto.jsp"/>
		<jsp:param name="arrayCampi" value="'GARERDA_ID_', 'GARERDA_CODGAR_', 'GARERDA_CODCARR_', 'GARERDA_NUMRDA_', 'GARERDA_POSRDA_', 'GARERDA_DATCRE_', 'GARERDA_DATRIL_', 'GARERDA_DATACONS_', 'GARERDA_LUOGOCONS_', 'GARERDA_CODVOC_', 'GARERDA_VOCE_', 'GARERDA_CODCAT_', 'GARERDA_UNIMIS_', 'GARERDA_QUANTI_', 'GARERDA_PREZUN_', 'GARERDA_PERCIVA_', 'GARERDA_ESERCIZIO_', 'GARERDA_NGARA_', 'GARERDA_STRUTTURA_'"/>
		<jsp:param name="sezioneListaVuota" value="true" />
		<jsp:param name="titoloSezione" value="Richiesta di acquisto" />
		<jsp:param name="titoloNuovaSezione" value="Nuova richiesta di acquisto" />
		<jsp:param name="descEntitaVociLink" value="richiesta di acquisto" />
		<jsp:param name="msgRaggiuntoMax" value="e richieste di acquisto"/>
		<jsp:param name="usaContatoreLista" value="true" />
	</jsp:include>

        <c:if test='${modoAperturaScheda ne "NUOVO"}' >
		<c:set var="tmp" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GestioneImpegniDiSpesaFunction", pageContext, "GAREIDS", gene:getValCampo(key, "CODGAR"))}'/>
	</c:if>
	<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
		<jsp:param name="entita" value='GAREIDS'/>
		<jsp:param name="chiave" value='${gene:getValCampo(key, "CODGAR")}'/>
		<jsp:param name="nomeAttributoLista" value='impegniDiSpesa' />
		<jsp:param name="idProtezioni" value="IDS" />
		<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/gareids/impegno-di-spesa.jsp"/>
		<jsp:param name="arrayCampi" value="'GAREIDS_CODGAR_', 'GAREIDS_NUMIDS_', 'GAREIDS_DATEMISS_', 'GAREIDS_NPROT_', 'GAREIDS_DATRICEZ_', 'GAREIDS_IMPIDS_', 'GAREIDS_NOTEIDS_', 'GAREIDS_PROGIDS_' "/>
		<jsp:param name="sezioneListaVuota" value="false" />
		<jsp:param name="titoloSezione" value="Impegno di spesa" />
		<jsp:param name="titoloNuovaSezione" value="Nuovo impegno di spesa" />
		<jsp:param name="descEntitaVociLink" value="impegno di spesa" />
		<jsp:param name="msgRaggiuntoMax" value="i impegni di spesa"/>
	</jsp:include>

	<gene:gruppoCampi idProtezioni="AVVESPL">
		<gene:campoScheda>
			<td colspan="2"><b>Avviso esplorativo</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="DPUBAVVISO" entita="TORN" />
		<gene:campoScheda campo="DTPUBAVVISO" entita="TORN" />
	</gene:gruppoCampi>		

	
	<c:if test='${modo eq "NUOVO"}'>
		<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.ValorizzaStazioneAppaltanteFunction" parametro=""/>
	</c:if>
	
	<gene:gruppoCampi idProtezioni="RUP">
		<gene:campoScheda>
			<td colspan="2"><b>Stazione appaltante e RUP</b></td>
		</gene:campoScheda>
		<gene:archivio titolo="Uffici intestatari"
			 lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.CENINT"),"gene/uffint/uffint-lista-popup.jsp","")}'
			 scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaUffint"),"gene/uffint/uffint-scheda.jsp","")}'
			 schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaUffint"),"gene/uffint/uffint-scheda-popup.jsp","")}'
			 campi="UFFINT.CODEIN;UFFINT.NOMEIN;UFFINT.ISCUC"
			 chiave="TORN_CENINT"
			 functionId="skip|abilitazione:1_parentFormName:formUFFINTTORN"
			 formName="formUFFINTTORN">
				<gene:campoScheda campo="CENINT" defaultValue="${requestScope.initCENINT}" obbligatorio="true" modificabile="${empty sessionScope.uffint }">
					<gene:checkCampoScheda funzione='checkPuntiContatto()' obbligatorio="true" messaggio="Non è possibile modificare il valore perchè ci sono riferimenti ai relativi punti di contatto nella gara" onsubmit="false"/>
				</gene:campoScheda>
				<gene:campoScheda campo="NOMEIN" entita="UFFINT" where="TORN.CENINT = UFFINT.CODEIN"
				modificabile='${gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.CENINT") && empty sessionScope.uffint}' 
				visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.CENINT")}' defaultValue="${requestScope.initNOMEIN}">
					<gene:checkCampoScheda funzione='checkPuntiContatto()' obbligatorio="true" messaggio="Non è possibile modificare il valore perchè ci sono riferimenti ai relativi punti di contatto nella gara" onsubmit="false"/>
				</gene:campoScheda>
				<gene:campoScheda campo="ISCUC" entita="UFFINT" where="TORN.CENINT = UFFINT.CODEIN" visibile="false" defaultValue="${requestScope.initISCUC}" />
		</gene:archivio>
		<gene:campoScheda campo="UFFDET" />
		<gene:archivio titolo="Tecnici"
			 lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.CODRUP"),"gene/tecni/tecni-lista-popup.jsp","")}'
			 scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda.jsp","")}'
			 schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda-popup.jsp","")}'
			 campi="TECNI.CODTEC;TECNI.NOMTEC"
			 functionId="skip"
			 chiave="TORN_CODRUP"
			 inseribile="true">
				<gene:campoScheda campo="CODRUP" defaultValue="${requestScope.initCODRUP}"/>
				<gene:campoScheda campo="NOMTEC" title="Nome" entita="TECNI" where="TORN.CODRUP = TECNI.CODTEC" defaultValue="${requestScope.initNOMTEC1}"
				modificabile='${gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.CODRUP")}' 
				visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.CODRUP")}'/>
		</gene:archivio>
		<gene:campoScheda campo="ALTRISOG" obbligatorio="true" title="Nei singoli lotti, centrale di committenza agisce per conto di"/>
		<gene:campoScheda campo="LIVACQ" />
	</gene:gruppoCampi>
	
	<gene:fnJavaScriptScheda funzione='aggiornaVisualizzazioneDaArchivioUffint("#UFFINT_ISCUC#")' elencocampi='UFFINT_ISCUC' esegui="true" />
	
	<gene:fnJavaScriptScheda funzione='aggiornaArchiviPuntiContatto("#TORN_CENINT#")' elencocampi='TORN_CENINT' esegui="false" />
	<gene:fnJavaScriptScheda funzione='sbiancaArchivioUffintAltriSogg("#TORN_CENINT#")' elencocampi='TORN_CENINT' esegui="false" />
	<c:if test='${tipologiaGara == "3" and modo ne "VISUALIZZA"}'>
		<gene:fnJavaScriptScheda funzione='gestioneModcont("#TORN_ALTRISOG#","#TORN_AQOPER#")' elencocampi='TORN_ALTRISOG;TORN_AQOPER' esegui="true" />
	</c:if>
	
	<c:if test='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.UFFDET") }'>
			<gene:fnJavaScriptScheda funzione='gestioneVisualizzazioneUffedt("#TORN_CENINT#","2")' elencocampi='TORN_CENINT' esegui="false" />
		</c:if>
			
	<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GestioneUlterioriReferentiIncaricatiFunction" parametro='${gene:getValCampo(key, "CODGAR")}' />
	<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
		<jsp:param name="entita" value='GARTECNI'/>
		<jsp:param name="chiave" value='${gene:getValCampo(key, "CODGAR")}'/>
		<jsp:param name="nomeAttributoLista" value='ultReferentiIncaricati' />
		<jsp:param name="idProtezioni" value="ULTREFINC" />
		<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/gartecni/referente-incaricato.jsp"/>
		<jsp:param name="arrayCampi" value="'GARTECNI_CODGAR_', 'GARTECNI_NUMTEC_', 'GARTECNI_CODTEC_', 'GARTECNI_NOMTEC_', 'GARTECNI_INCTEC_'"/>		
		<jsp:param name="sezioneListaVuota" value="false" />
		<jsp:param name="titoloSezione" value="Ulteriore referente o incaricato" />
		<jsp:param name="titoloNuovaSezione" value="Nuovo referente o incaricato" />
		<jsp:param name="descEntitaVociLink" value="referente o incaricato" />
		<jsp:param name="msgRaggiuntoMax" value="i referenti o incaricati"/>
	</jsp:include>
	
	<gene:gruppoCampi idProtezioni="DAQ">
		<gene:campoScheda nome="DAQ">
			<td colspan="2"><b>Durata dell'accordo quadro</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="AQDURATA" />
		<gene:campoScheda campo="AQTEMPO" />
	</gene:gruppoCampi>

	<%/* Campi per GARA DIVISA IN LOTTI CON OFFERTA UNICA */%>
	<c:if test='${tipologiaGara == "3"}'>
		<gene:gruppoCampi idProtezioni="TMP">
			<gene:campoScheda nome="TMP">
				<td colspan="2"><b>Durata del contratto</b></td>
			</gene:campoScheda>
			<gene:campoScheda campo="DINLAVG" entita="GARE" where="TORN.CODGAR = GARE.NGARA "/>
			<gene:campoScheda campo="TEUTIL"  entita="GARE" where="TORN.CODGAR = GARE.NGARA "/>
			<gene:campoScheda campo="TEMESI" entita="GARE" where="TORN.CODGAR = GARE.NGARA " modificabile="${datiRiga.TORN_TIPGEN == '2' || datiRiga.TORN_TIPGEN == '3'}" defaultValue="1"/>
		</gene:gruppoCampi>
		
		<gene:gruppoCampi idProtezioni="CONSEGNA">
			<gene:campoScheda nome= "CONSEGNA">
				<td colspan="2"><b>Termini di consegna dei beni o di esecuzione dei servizi</b></td>
			</gene:campoScheda>
			<gene:campoScheda campo="NGARA" entita="GARE1" where="GARE1.NGARA=TORN.CODGAR" visibile="false"/>
			<gene:campoScheda campo="DTERMCON" entita="GARE1" where="GARE1.NGARA=TORN.CODGAR"/>
			<gene:campoScheda campo="NGIOCON" entita="GARE1" where="GARE1.NGARA=TORN.CODGAR"/>
		</gene:gruppoCampi>
		
		<c:choose>
			<c:when test='${datiRiga.TORN_TIPGEN eq "1"}'>
				<c:set var="titoloCategoriaPrevalente" value='Categoria prevalente' />
				<c:set var="titoloCategoriaUlteriore" value='Ulteriore categoria' />
				<c:set var="titoloCategoriaUlterioreNuovaSezione" value='categoria ulteriore' />
				<c:set var="titoloCategoriaUlterioreMsgRaggiuntoMax" value='categorie ulteriori' />
			</c:when>
			<c:otherwise>
				<c:set var="titoloCategoriaPrevalente" value='Prestazione principale' />
				<c:set var="titoloCategoriaUlteriore" value='Prestazione secondaria' />
				<c:set var="titoloCategoriaUlterioreNuovaSezione" value='prestazione secondaria' />
				<c:set var="titoloCategoriaUlterioreMsgRaggiuntoMax" value='prestazioni secondarie' />
			</c:otherwise>
		</c:choose>
		
		<gene:gruppoCampi idProtezioni="CATG">
			<gene:campoScheda entita="CATG" campo="NGARA" visibile="false" where="CATG.NGARA=TORN.CODGAR" defaultValue='${codgar}' />
			<gene:campoScheda>
				<td colspan="2">
					<b>${titoloCategoriaPrevalente}</b>
				</td>
			</gene:campoScheda>
			<c:choose>
				<c:when test="${modoAperturaScheda ne 'VISUALIZZA' and (empty datiRiga.CATG_CATIGA or empty datiRiga.CAIS_TIPLAVG)}">
					<c:set var="parametriWhere" value="N:${datiRiga.TORN_TIPGEN}" />
				</c:when>
				<c:otherwise>
					<c:set var="parametriWhere" value="N:${datiRiga.CAIS_TIPLAVG}" />
				</c:otherwise>
			</c:choose>
			<gene:archivio titolo="Categorie d'iscrizione"
			lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.CATG.CATIGA"), "gene/cais/lista-categorie-iscrizione-popup.jsp", "")}'
			scheda=""
			schedaPopUp=""
			campi="V_CAIS_TIT.CAISIM;V_CAIS_TIT.DESCAT;V_CAIS_TIT.ACONTEC;V_CAIS_TIT.QUAOBB;V_CAIS_TIT.TIPLAVG;V_CAIS_TIT.ISFOGLIA"
			functionId="default"
			parametriWhere="${parametriWhere}"
			chiave=""
			formName="formCategoriaPrevalenteGare"
			inseribile="false">
			<c:set var="categoriaUtilizzata" value="${gene:callFunction3('it.eldasoft.sil.pg.tags.funzioni.CheckCategoriaPresenteLottiFunction', pageContext, datiRiga.CATG_CATIGA, codgar)}" />
			<gene:campoScheda campo="CATIGA" title="${gene:if(datiRiga.TORN_TIPGEN eq '1', 'Codice categoria', 'Codice prestazione')}" entita="CATG" where="CATG.CATIGA=CAIS.CAISIM" obbligatorio="${datiRiga.TORN_TIPGEN eq '1'}" defaultValue="${requestScope.initCATG[0]}" modificabile='${categoriaUtilizzata eq "false"}'/>		
			<gene:campoScheda campo="DESCAT" entita="CAIS" from="CATG" where="CATG.NGARA='${codgar}' and CATG.CATIGA=CAIS.CAISIM" modificabile='${gene:checkProt(pageContext, "COLS.MOD.GARE.CATG.CATIGA") and categoriaUtilizzata eq "false" }' visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.CATG.CATIGA")}' defaultValue="${requestScope.initCATG[1]}" />
			<gene:campoScheda campo="ACONTEC" entita="CAIS" visibile="false" defaultValue="${requestScope.initCATG[2]}" />
			<gene:campoScheda campo="QUAOBB" entita="CAIS" visibile="false" defaultValue="${requestScope.initCATG[3]}" />
			<gene:campoScheda campo="TIPLAVG" entita="CAIS" visibile="false" defaultValue="${requestScope.initCATG[4]}" />
			<gene:campoScheda campo="ISFOGLIA" entita="V_CAIS_TIT"  from="CATG" where="CATG.NGARA='${codgar}' and CATG.CATIGA=V_CAIS_TIT.CAISIM" visibile="false" />
		</gene:archivio>
		<gene:campoScheda campo="NCATG" entita="CATG" where="CATG.NGARA=TORN.CODGAR" visibile="false" />
		<gene:campoScheda entita="CATG" title="Classifica" campo="NUMCLA" visibile="false" definizione="N2;0;;;" />
		<gene:campoScheda title="Classifica" campo="NUMCLA_CAT_LAVORI" campoFittizio="true" definizione="N2;0;G_z09;;G1NUMCLA" value="${datiRiga.CATG_NUMCLA}" />
		<gene:campoScheda title="Classifica" campo="NUMCLA_CAT_FORNITURE" campoFittizio="true" definizione="N2;0;G_z07;;G1NUMCLA" value="${datiRiga.CATG_NUMCLA}" />
		<gene:campoScheda title="Classifica" campo="NUMCLA_CAT_SERVIZI" campoFittizio="true" definizione="N2;0;G_z08;;G1NUMCLA" value="${datiRiga.CATG_NUMCLA}" />
		<gene:campoScheda title="Classifica" campo="NUMCLA_CAT_LAVORI150" campoFittizio="true" definizione="N2;0;G_z11;;G1NUMCLA" value="${datiRiga.CATG_NUMCLA}" />
		<gene:campoScheda title="Classifica" campo="NUMCLA_CAT_SERVIZIPROFESSIONALI" campoFittizio="true" definizione="N2;0;G_z12;;G1NUMCLA" value="${datiRiga.CATG_NUMCLA}" />
		<gene:campoScheda campo="IMPIGA" entita="CATG" where="CATG.NGARA=TORN.CODGAR" visibile="false" />
		</gene:gruppoCampi>
		<gene:fnJavaScriptScheda funzione='visualizzaNumeroClassifica("#CAIS_TIPLAVG#","#V_CAIS_TIT_ISFOGLIA#", "CATG", null, true)' elencocampi='CAIS_TIPLAVG;V_CAIS_TIT_ISFOGLIA' esegui="false" />
		<gene:fnJavaScriptScheda funzione='setCampoNumeroClassifica("#NUMCLA_CAT_LAVORI#", "CATG", null)' elencocampi='NUMCLA_CAT_LAVORI' esegui="false" />
		<gene:fnJavaScriptScheda funzione='setCampoNumeroClassifica("#NUMCLA_CAT_FORNITURE#", "CATG", null)' elencocampi='NUMCLA_CAT_FORNITURE' esegui="false" />
		<gene:fnJavaScriptScheda funzione='setCampoNumeroClassifica("#NUMCLA_CAT_SERVIZI#", "CATG", null)' elencocampi='NUMCLA_CAT_SERVIZI' esegui="false" />
		<gene:fnJavaScriptScheda funzione='setCampoNumeroClassifica("#NUMCLA_CAT_LAVORI150#", "CATG", null)' elencocampi='NUMCLA_CAT_LAVORI150' esegui="false" />
		<gene:fnJavaScriptScheda funzione='setCampoNumeroClassifica("#NUMCLA_CAT_SERVIZIPROFESSIONALI#", "CATG", null)' elencocampi='NUMCLA_CAT_SERVIZIPROFESSIONALI' esegui="false" />

		<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GestioneUlteriCategorieFunction" parametro='${gene:getValCampo(key, "CODGAR")}' />
		<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
			<jsp:param name="entita" value='OPES'/>
			<jsp:param name="chiave" value='${gene:getValCampo(key, "CODGAR")}'/>
			<jsp:param name="nomeAttributoLista" value='ulterioriCategorie' />
			<jsp:param name="idProtezioni" value="OPES" />
			<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/opes/ulterioreCategoria.jsp"/>
			<jsp:param name="arrayCampi" value="'OPES_CATOFF_', 'CAIS_DESCAT_', 'OPES_DESCOP_', 'OPES_NUMCLU_', 'NUMCLU_CAT_LAVORI_', 'NUMCLU_CAT_FORNITURE_', 'NUMCLU_CAT_SERVIZI_', 'NUMCLU_CAT_LAVORI150_', 'NUMCLU_CAT_SERVIZIPROFESSIONALI_', 'OPES_ISCOFF_','V_CAIS_TIT_ISFOGLIA_'"/>		
			<jsp:param name="sezioneListaVuota" value="false" />
			<jsp:param name="titoloSezione" value="${titoloCategoriaUlteriore}" />
			<jsp:param name="titoloNuovaSezione" value="Nuova ${titoloCategoriaUlterioreNuovaSezione}" />
			<jsp:param name="descEntitaVociLink" value="${titoloCategoriaUlterioreNuovaSezione}" />
			<jsp:param name="msgRaggiuntoMax" value="e ${titoloCategoriaUlterioreMsgRaggiuntoMax}"/>
			<jsp:param name="codgar" value="${codgar}"/>
		</jsp:include>
		<gene:fnJavaScriptScheda funzione="aggiornaFiltroArchivioElencoOperatori()" elencocampi="CATG_CATIGA" esegui="true" />
	</c:if>
	
	<gene:gruppoCampi idProtezioni="ALTOFF">
		<gene:campoScheda>
			<td colspan="2"><b>Altri dati relativi alla modalità di presentazione offerta e svolgimento della procedura</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="BUSTALOTTI" entita="GARE" where="TORN.CODGAR = GARE.NGARA " visibile='${tipologiaGara == "3"}' obbligatorio="${empty datiRiga.GARE_CLIV1}" modificabile="${empty datiRiga.GARE_CLIV1 and (empty datiRiga.TORN_OFFTEL or datiRiga.TORN_OFFTEL eq 2)}" defaultValue="${valoreInizializzazioneBustalotti }"/>
		<c:choose>
			<c:when test="${garaQformOfferte }">
				<c:set var="inizializzazionePrerib" value=""/>
			</c:when>
			<c:otherwise>
				<c:set var="inizializzazionePrerib" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoFunction", pageContext, "A1028","1","false")}'/>
			</c:otherwise>
		</c:choose>
		<gene:campoScheda campo="PRERIB" defaultValue='${inizializzazionePrerib}' visibile="${ not garaQformOfferte}">
   			<gene:checkCampoScheda funzione='"##" <= 9' obbligatorio="true" messaggio="Il valore specificato deve essere compreso tra 0 e 9" onsubmit="false"/>
   		</gene:campoScheda>
		<c:choose>
   			<c:when test='${param.proceduraTelematica eq 1}'>
				<c:choose>
					<c:when test="${garaQformOfferte }">
						<c:set var="inizializzazioneRicmano" value=""/>
					</c:when>
					<c:otherwise>
						<c:set var="inizializzazioneRicmano" value="${valoreTabellatoA1115Ricmano }"/>
					</c:otherwise>
				</c:choose>
				<gene:campoScheda campo="RICMANO" defaultValue="${inizializzazioneRicmano}" visibile="${ not garaQformOfferte}"/>	
			</c:when>
   			<c:otherwise>
				<gene:campoScheda campo="RICMANO" visibile="${ not garaQformOfferte}"/>	
   			</c:otherwise>
   		</c:choose>
   		<gene:fnJavaScriptScheda funzione="visualizzaMODMANO('#TORN_RICMANO#')" elencocampi="TORN_RICMANO" esegui="true"/>
   		<gene:campoScheda campo="MODMANO" obbligatorio="true" visibile="${modo ne 'VISUALIZZA' or datiRiga.TORN_RICMANO eq 1}" defaultValue="1"/>
		<gene:campoScheda campo="ULTDETLIC" visibile="${not garaQformOfferte}"/>
		<gene:campoScheda campo="INVERSA" visibile="${param.proceduraTelematica eq 1 or datiRiga.TORN_GARTEL eq 1}" defaultValue="2"/>
		<gene:campoScheda campo="COMPREQ" defaultValue="${valoreTabellatoA1115Compreq }"/>
		<gene:campoScheda campo="VALTEC" defaultValue="${valoreTabellatoA1115 }"/>
		<gene:campoScheda campo="MODCONT" obbligatorio="true" defaultValue="${valoreTabellatoA1115Modcont }" visibile='${tipologiaGara == "3"}'/>
	</gene:gruppoCampi>
	
	<gene:fnJavaScriptScheda funzione="aggiornaCompreq('#TORN_INVERSA#')" elencocampi="TORN_INVERSA" esegui="false" />
		
	<gene:gruppoCampi idProtezioni="ALT">
		<gene:campoScheda>
			<td colspan="2"><b>Altri dati relativi alla procedura</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="OGGCONT" visibile='${not(modo eq "VISUALIZZA" and datiRiga.TORN_TIPGEN eq "3")}'/>
		<%/* Campi fittizzi per gestire i valori del tabellato A1031 da associare a OGGCONT in funzione di TIPGEN */%>
		<gene:campoScheda campo="OGGCONT_LAVORI" title="Oggetto contratto" entita="V_GARE_TORN"  campoFittizio="true"  definizione="N7;0;A1031;;G1OGGCONT" value="${datiRiga.TORN_OGGCONT}" visibile='${modo ne "VISUALIZZA"}'/>
		<gene:campoScheda campo="OGGCONT_FORNITURE" title="Oggetto contratto" entita="V_GARE_TORN"  campoFittizio="true"  definizione="N7;0;A1031;;G1OGGCONT" value="${datiRiga.TORN_OGGCONT}" visibile='${modo ne "VISUALIZZA"}'/>
		<gene:campoScheda campo="TIPLAV" visibile='${datiRiga.TORN_TIPGEN eq "1"}'/>
		<gene:campoScheda campo="URBASCO" visibile='${datiRiga.TORN_TIPGEN eq "1"}'/>
		<gene:campoScheda campo="SOMMAUR" />
        <gene:campoScheda campo="TIPTOR" defaultValue="2"  visibile="false"/>
		<gene:campoScheda campo="PROURG"/>
		<gene:campoScheda campo="MOTACC"/>
		<gene:campoScheda campo="PREINF"/>
		<gene:campoScheda campo="BANWEB"/>
		<gene:campoScheda campo="DOCWEB"/>
		<gene:campoScheda campo="TERRID"/>
		<gene:campoScheda campo="ISGREEN" defaultValue="${requestScope.initISGREEN}"/>
		<gene:campoScheda campo="DESGREEN" defaultValue="${requestScope.initMOTGREEN}"/>
		<gene:campoScheda campo="ISRECYCLE" />
		<gene:campoScheda campo="ISPNRR" />
		<gene:campoScheda campo="CLIV1" entita="GARE" where="TORN.CODGAR = GARE.NGARA " visibile='false' />
		<c:if test='${tipologiaGara ne "3"}'>
			<gene:campoScheda campo="NORMA1"/>
			<gene:campoScheda campo="NORMA"/>
			<gene:campoScheda campo="TUS" defaultValue="${requestScope.initTUS}" />
			<gene:campoScheda campo="CONTOECO"/>
			<gene:campoScheda campo="CLIV2" defaultValue="${sessionScope.profiloUtente.id}" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoCliv2" obbligatorio="true"/>
		</c:if>
		<c:if test='${tipologiaGara == "3"}'>
			<gene:campoScheda campo="SEGRETA" entita="GARE" where="TORN.CODGAR = GARE.NGARA " />
			<gene:campoScheda campo="SUBGAR" entita="GARE" where="TORN.CODGAR = GARE.NGARA " />
			<gene:campoScheda campo="NORMA1"/>
			<gene:campoScheda campo="NORMA"/>
			<gene:campoScheda campo="TUS" defaultValue="${requestScope.initTUS}" />
			<gene:campoScheda campo="CONTOECO"/>
			<gene:campoScheda campo="CLIV2" defaultValue="${sessionScope.profiloUtente.id}" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoCliv2" obbligatorio="true"/>
			<gene:campoScheda campo="NOTEGA" entita="GARE" where="TORN.CODGAR = GARE.NGARA " />
		</c:if>
	</gene:gruppoCampi>
	<gene:fnJavaScriptScheda funzione='gestioneULTDETLIC("#TORN_CRITLIC#","#TORN_DETLIC#")' elencocampi='TORN_CRITLIC;TORN_DETLIC' esegui="true" />	
	<gene:fnJavaScriptScheda funzione='aggiornaOGGCONT("#V_GARE_TORN_OGGCONT_LAVORI#")' elencocampi='V_GARE_TORN_OGGCONT_LAVORI' esegui="false"/>
	<gene:fnJavaScriptScheda funzione='aggiornaOGGCONT("#V_GARE_TORN_OGGCONT_FORNITURE#")' elencocampi='V_GARE_TORN_OGGCONT_FORNITURE' esegui="false"/>
	<gene:fnJavaScriptScheda funzione='visualizzaMotacc("#TORN_PROURG#")' elencocampi='TORN_PROURG' esegui="true"/>
	<gene:fnJavaScriptScheda funzione='visualizzaDesgreen("#TORN_ISGREEN#")' elencocampi="TORN_ISGREEN" esegui="true" />
	<gene:fnJavaScriptScheda funzione='visualizzaCalcsome("#TORN_CALCSOAN#","#TORN_CRITLIC#")' elencocampi="TORN_CALCSOAN;TORN_CRITLIC" esegui="true" />
	
	<c:if test="${tipologiaGara == '3' and (isProceduraTelematica or param.proceduraTelematica eq 1)}">
		<gene:gruppoCampi idProtezioni="ASTA" visibile="${not garaQformOfferte}">
			<gene:campoScheda nome="ASTA">
				<td colspan="2"><b>Asta elettronica </b></td>
			</gene:campoScheda>
			<gene:campoScheda campo="RICASTAE" />
			<gene:campoScheda campo="AERIBMIN" definizione="F13.9;0;;PRC;"/>
			<gene:campoScheda campo="AERIBMAX" definizione="F13.9;0;;PRC;"/>
			<gene:campoScheda campo="AEIMPMIN" />
			<gene:campoScheda campo="AEIMPMAX" />
			<gene:campoScheda campo="AEMODVIS" />
			<gene:campoScheda campo="AENOTE" />
		</gene:gruppoCampi>
		<gene:campoScheda campo="ESPLPORT" entita="GARE1" where="TORN.CODGAR = GARE1.NGARA" defaultValue="${requestScope.initESPLPORT}" visibile="false"/>
		<gene:fnJavaScriptScheda funzione='gestioneCampiAstaEl("#TORN_CRITLIC#",#GARE_BUSTALOTTI#)' elencocampi='TORN_CRITLIC;GARE_BUSTALOTTI' esegui="true" />
		<gene:fnJavaScriptScheda funzione='gestioneCampiAstaEl_Ricastae("#TORN_RICASTAE#")' elencocampi='TORN_RICASTAE' esegui="false" />
	</c:if>
	
	<gene:campoScheda campo="AUTORI" entita="G_PERMESSI" where="G_PERMESSI.CODGAR = TORN.CODGAR AND G_PERMESSI.SYSCON = ${sessionScope.profiloUtente.id}" visibile="false"/>
	<gene:campoScheda campo="PROPRI" entita="G_PERMESSI" where="G_PERMESSI.CODGAR = TORN.CODGAR AND G_PERMESSI.SYSCON = ${sessionScope.profiloUtente.id}" visibile="false"/>
	
	
	
	<c:choose>
		<c:when test='${empty garaLottoUnico}'>
			<c:set var="isGaraLottoUnico" value="false"/>
		</c:when>
		<c:otherwise>
			<c:set var="isGaraLottoUnico" value="${garaLottoUnico}"/>
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${tipologiaGara == "3"}'>
			<c:set var="isGaraOffertaUnica" value="true"/>
		</c:when>
		<c:otherwise>
			<c:set var="isGaraOffertaUnica" value="${false}"/>
		</c:otherwise>
	</c:choose>
	
	<jsp:include page="/WEB-INF/pages/gare/torn/torn-sez-presentazione.jsp">
		<jsp:param name="campoTipoProcedura" value="TORN_TIPGAR"/>
		<jsp:param name="isGaraLottoUnico" value="${isGaraLottoUnico}"/>
		<jsp:param name="isGaraOffertaUnica" value="${isGaraOffertaUnica}"/>
		<jsp:param name="valoreCodgar" value="${codgar}"/>
	</jsp:include>
	
	<gene:campoScheda campo="MODREA" visibile="false"/>

	<gene:campoScheda campo="UUID" entita="TORN"  visibile="false"/>
	
	<c:if test='${modo eq "NUOVO"}'>
		<gene:campoScheda campo="GARPRIV" entita="TORN" where="TORN.CODGAR=GARE.CODGAR1" visibile="false" value="${garpriv}"/>
	</c:if>
	<gene:campoScheda>
		
		<td class="comandi-dettaglio" colSpan="2">
			<gene:insert name="addPulsanti"/>
			<c:choose>
				<c:when test='${modo eq "MODIFICA" or modo eq "NUOVO"}'>
					<gene:insert name="pulsanteSalva">
						<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:conferma()">
					</gene:insert>
					<gene:insert name="pulsanteAnnulla">
						<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:schedaAnnulla()">
					</gene:insert>
				</c:when>
				<c:otherwise>
					<gene:insert name="pulsanteModifica">
						<c:if test='${gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD")}'>
							<INPUT type="button"  class="bottone-azione" value='${modificaLabel}' title='${modificaLabel}' onclick="javascript:schedaModifica()">
						</c:if>
					</gene:insert>
					<gene:insert name="pulsanteNuovo">
						<c:if test='${gene:checkProtFunz(pageContext,"INS","SCHEDANUOVO")}'>
							<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaNuovo")}' title='${gene:resource("label.tags.template.lista.listaNuovo")}' onclick="javascript:schedaNuovaGara()" id="btnNuovo">
						</c:if>
					</gene:insert>
					<c:if test='${ tipologiaGara == "3" and datiRiga.TORN_GARTEL eq 1 && gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_TORN-lista.Condividi-gara")}'>
		     		<c:choose>
		     			<c:when test='${isPersonalizzazioneGenovaAttiva eq "1"}'>
								<INPUT type="button"  class="bottone-azione" value='Punto ordinante e istruttore' title='Punto ordinante e istruttore' onclick="javascript:apriGestionePermessiGaraTelematica('${datiRiga.TORN_CODGAR}', ${tipologiaGara});" >
		     			</c:when>
		     			<c:otherwise>
		     				<INPUT type="button"  class="bottone-azione" value='Punto ordinante e istruttore' title='Punto ordinante e istruttore' onclick="javascript:apriGestionePermessiGaraTelematicaStandard('${datiRiga.TORN_CODGAR}', ${tipologiaGara});" >
		     			</c:otherwise>
		     		</c:choose>
					</c:if>
				</c:otherwise>
			</c:choose>
			&nbsp;
		</td>
	</gene:campoScheda>
	
	<input type="hidden" name="bloccoModificaPubblicazione" value="${bloccoModificaPubblicazione}" />
	<input type="hidden" name="paginaDatiGenTorn" value="si" />
	<input type="hidden" name="bloccoModificatiDati" id="bloccoModificatiDati" value="${bloccoModificatiDati}"/>
	
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
	<c:if test='${modoAperturaScheda eq "NUOVO" }'>
		<c:if test="${initGarerda}">
			function initGarerda(){
			//Se lottoUnico,
			//showNextElementoSchedaMultipla('GARERDA', new Array(${arrayCampi}), new Array());
			setValue("GARERDA_NUMRDA_1","${initNUMRDA}");
			setValue("GARERDA_DATCRE_1", "${initDATCRE}");
			setValue("GARERDA_DATRIL_1", "${initDATRIL}");
			setValue("GARERDA_STRUTTURA_1", "${initSTRUTTURA}");

			}
			initGarerda();
		</c:if>
	</c:if>

	function visMetaDati(numeroRda,esercizio,tipoWSERP){
		var ngara = getValue("GARE_NGARA");
		var href="href=gare/garerda/popup-rda-metadati.jsp&ngara=" + ngara + "&numeroRda=" + numeroRda  + "&esercizio=" + esercizio + "&tipoWSERP=" + tipoWSERP + "&idconfi=" + idconfi;
		openPopUpCustom(href, "metadatiRda", 800, 600, "yes","yes");
	}

	function visualizzaDesgreen(visibilità){
	if (visibilità == '1') {
		showObj("rowTORN_DESGREEN",true);
	} else {	
		showObj("rowTORN_DESGREEN",false);
		document.forms[0].TORN_DESGREEN.value = '';
		}
	};
	
	function visualizzaCalcsome(visibilità,critlic){
	if (visibilità == '1' && critlic != '2') {
		showObj("rowTORN_CALCSOME",true);
	} else {	
		showObj("rowTORN_CALCSOME",false);
		document.forms[0].TORN_CALCSOME.value = '';
		}
	};

	var idconfi = "${idconfi}";
			
	var imptor=getValue("TORN_IMPTOR");
	var valmax=getValue("V_GARE_IMPORTI_VALMAX");
	var visualizzazioneCampoValmax=false;
	if((imptor==null || imptor=="") && valmax!=null && valmax!="" && valmax!=0){
		visualizzazioneCampoValmax=true;
	}else if(imptor!=null && imptor!="" && valmax!=null && valmax!=""){
		imptor=parseFloat(imptor);
		valmax=parseFloat(valmax);
		if(valmax > imptor )
			visualizzazioneCampoValmax=true;
	}
	showObj("rowV_GARE_IMPORTI_VALMAX",visualizzazioneCampoValmax);

        showObj("rowTORN_ITERGA",false);
	
	function popupInviaBandoAvvisoSimap(codgar,iterga,settore) {
	   var comando = "href=gare/commons/popup-invia-bando-avviso-simap.jsp";
	   comando = comando + "&codgar=" + codgar + "&iterga=" + iterga + "&settore=" + settore;
	   openPopUpCustom(comando, "inviabandoavvisosimap", 550, 650, "yes", "yes");
	}
	
	function listaDiscussioni(){
		var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=geneweb/w_discuss_p/w_discuss_p-lista.jsp&entita=" + document.forms[0].entita.value + "&chiave=" + document.forms[0].key.value;
		document.location.href = href;
	}

	function schedaNuovaGara(){
		document.location.href = contextPath + "/pg/InitNuovaGara.do?" + csrfToken;
	}
	
	
		function conferma(){
		
			<c:if test='${modo eq "NUOVO"}'>
				setValue("TORN_CODGAR", getValue("TORN_CODGAR").trim(), false);
			</c:if>
		
			<c:if test='${tipologiaGara ne "3"}'>
				//Si devono abilitare i campi disabilitati
				var ITERGA = getValue("TORN_ITERGA");
				if(ITERGA!=1){
					document.getElementById("TORN_DTEOFF").disabled = false;
					document.getElementById("TORN_OTEOFF").disabled = false;
					document.getElementById("TORN_LOCOFF").disabled = false;
					document.getElementById("TORN_VALOFF").disabled = false;
					document.getElementById("TORN_DTERMRICHCPO").disabled = false;
					document.getElementById("TORN_DTERMRISPCPO").disabled = false;
					document.getElementById("TORN_DESOFF").disabled = false;
					document.getElementById("TORN_OESOFF").disabled = false;
					document.getElementById("TORN_LOCGAR").disabled = false;
					document.getElementById("TORN_IMPTOR").disabled = false;
					
				}
				
			</c:if>
			
			<c:if test='${gene:checkProt(pageContext, "COLS.MAN.GARE.TORN.VALOFF")}'>
				    var skipControllo=false;
				    if(document.getElementById("rowTORN_VALOFF").style.display=="none")
				    	skipControllo=true;
				    
				    var arrayChecks = activeForm.getChecksArray();
	            	for(var la=0; la < arrayChecks.length; la++){
	                	if(arrayChecks[la].isObbligatorio() && (arrayChecks[la].getNome().toUpperCase() == "TORN_VALOFF" || arrayChecks[la].getNome().toUpperCase() == "VALOFF_FIT" || arrayChecks[la].getNome().toUpperCase() == "TORN_VALOFF_FIT")){
	                    	arrayChecks[la].setSkipControlloObblig(skipControllo);
	                	}
					}
			</c:if>
			<c:if test='${tipologiaGara eq "3"}'>
				$('#TORN_MODCONT').prop('disabled', false);
				
				var tipoAppalto=getValue("TORN_TIPGEN");
				var tipoCategoria = getValue("CAIS_TIPLAVG");
				if(tipoCategoria == null || tipoCategoria == "")
					tipoCategoria=tipoAppalto;
				var valoreClassifica="";
				var classificaDisabilitata;
				var classificaVisualizzata;
				var catiga=getValue("CATG_CATIGA");
				if(catiga!=null && catiga!=""){
					if(tipoCategoria==1){
						valoreClassifica = getValue("NUMCLA_CAT_LAVORI");
						classificaDisabilitata = document.getElementById("NUMCLA_CAT_LAVORI").disabled;
						classificaVisualizzata = isObjShow("rowNUMCLA_CAT_LAVORI");
					}else if(tipoCategoria=="2"){
						valoreClassifica = getValue("NUMCLA_CAT_FORNITURE");
						classificaDisabilitata = document.getElementById("NUMCLA_CAT_FORNITURE").disabled;
						classificaVisualizzata = isObjShow("rowNUMCLA_CAT_FORNITURE");	
					}else if(tipoCategoria=="3"){
						valoreClassifica = getValue("NUMCLA_CAT_SERVIZI");
						classificaDisabilitata = document.getElementById("NUMCLA_CAT_SERVIZI").disabled;
						classificaVisualizzata = isObjShow("rowNUMCLA_CAT_SERVIZI");	
					}else if(tipoCategoria=="4"){
						valoreClassifica = getValue("NUMCLA_CAT_LAVORI150");
						classificaDisabilitata = document.getElementById("NUMCLA_CAT_LAVORI150").disabled;
						classificaVisualizzata = isObjShow("rowNUMCLA_CAT_LAVORI150");	
					}else if(tipoCategoria=="5"){
						valoreClassifica = getValue("NUMCLA_CAT_SERVIZIPROFESSIONALI");
						classificaDisabilitata = document.getElementById("NUMCLA_CAT_SERVIZIPROFESSIONALI").disabled;
						classificaVisualizzata = isObjShow("rowNUMCLA_CAT_SERVIZIPROFESSIONALI");	
					}
					
					if(!classificaDisabilitata && (valoreClassifica==null || valoreClassifica=="") && classificaVisualizzata){
						clearMsg();
						var msg="Il campo 'Classifica' della Prestazione principale e' obbligatorio"; 
												
						outMsg(msg, "ERR");
						onOffMsg();
						return;
					}
				}
				//Controllo classifiche delle ulteriori categorie
				if(controlloClassificaSezioniDinamiche() != "true"){
					return;
				}
			</c:if>
			
			<c:if test="${tipologiaGara == '3' and (isProceduraTelematica or param.proceduraTelematica eq 1)}">
				var ricastae = getValue("TORN_RICASTAE");
				if(ricastae==1){
					var aeribmin = getValue("TORN_AERIBMIN");
					var aeribmax = getValue("TORN_AERIBMAX");
					if(aeribmin!=null && aeribmin!="" && aeribmax != null && aeribmax !=""){
						aeribmin = parseFloat(aeribmin);
	                    aeribmax = parseFloat(aeribmax);
						if(aeribmin >= aeribmax){
							outMsg("Lo scarto minimo del ribasso di rilancio per l'asta elettronica deve essere inferiore allo scarto massimo.", "ERR");
							onOffMsg();
							return;
						}
					}
					var aeimpmin = getValue("TORN_AEIMPMIN");
					var aeimpmax = getValue("TORN_AEIMPMAX");
					if(aeimpmin!=null && aeimpmin!="" && aeimpmax != null && aeimpmax !=""){
						aeimpmin = parseFloat(aeimpmin);
						aeimpmax = parseFloat(aeimpmax);
						if(aeimpmin >= aeimpmax){
							outMsg("Lo scarto minimo dell'importo di rilancio per l'asta elettronica deve essere inferiore allo scarto massimo.", "ERR");
							onOffMsg();
							return;
						}
					}
					var offaum = getValue("TORN_OFFAUM");
					if(offaum==1){
						outMsg("Non è possibile ammettere offerte in aumento nel caso di ricorso all'asta elettronica.", "ERR");
						onOffMsg();
						return;
					}
					
					var ribcal = getValue("GARE_RIBCAL");
					if (ribcal==3) {
						outMsg("Non è disponibile il ricorso all'asta elettronica nel caso di offerta prezzi espressa mediante somma sconti pesati.", "ERR");
						onOffMsg();
						return;
					}
				}
			</c:if>
			schedaConferma();	
		}
	

	function controlloClassificaSezioniDinamiche(){
		var valoreClassifica="";
		var classificaDisabilitata;
		var classificaVisualizzata;
		for(var i=1; i <= lastIdOPESVisualizzata ; i++){
			var tipoCategoria = getValue("CAIS_TIPLAVG_" + i);
			if(tipoCategoria==1){
				valoreClassifica = getValue("NUMCLU_CAT_LAVORI_" + i);
				classificaDisabilitata = document.getElementById("NUMCLU_CAT_LAVORI_" + i).disabled;
				classificaVisualizzata = isObjShow("rowNUMCLU_CAT_LAVORI_" + i);
			}else if(tipoCategoria=="2"){
				valoreClassifica = getValue("NUMCLU_CAT_FORNITURE_" + i);
				classificaDisabilitata = document.getElementById("NUMCLU_CAT_FORNITURE_" + i).disabled;	
				classificaVisualizzata = isObjShow("rowNUMCLU_CAT_FORNITURE_" + i);
			}else if(tipoCategoria=="3"){
				valoreClassifica = getValue("NUMCLU_CAT_SERVIZI_" + i);
				classificaDisabilitata = document.getElementById("NUMCLU_CAT_SERVIZI_" + i).disabled;
				classificaVisualizzata = isObjShow("rowNUMCLU_CAT_SERVIZI_" + i);	
			}else if(tipoCategoria=="4"){
				valoreClassifica = getValue("NUMCLU_CAT_LAVORI150_" + i);
				classificaDisabilitata = document.getElementById("NUMCLU_CAT_LAVORI150_" + i).disabled;	
				classificaVisualizzata = isObjShow("rowNUMCLU_CAT_LAVORI150_" + i);
			}else if(tipoCategoria=="5"){
				valoreClassifica = getValue("NUMCLU_CAT_SERVIZIPROFESSIONALI_" + i);
				classificaDisabilitata = document.getElementById("NUMCLU_CAT_SERVIZIPROFESSIONALI_" + i).disabled;	
				classificaVisualizzata = isObjShow("rowNUMCLU_CAT_SERVIZIPROFESSIONALI_" + i);
			}
			if(!classificaDisabilitata && (valoreClassifica==null || valoreClassifica=="") && classificaVisualizzata ){
				clearMsg();
				var msg="Il campo 'Classifica' delle Prestazioni secondarie e' obbligatorio"; 
								
				outMsg(msg, "ERR");
				onOffMsg();
				return "false";
			}
		}
		return "true";	
			
	} 
	
	
	function visualizzaESettaCorrettivo(modAggiudicazione, tipoAppalto, correttivo, campoCorrettivo) {
		var correttivoLavori = '${correttivoLavori}';
		var correttivoFornitureServizi = '${correttivoFornitureServizi}';
		var visualizza = false;

		// il campo va visualizzato solo con modAggiudicazione=1 o 5
		if (modAggiudicazione == '1' || modAggiudicazione == '5') visualizza = true;

		// nel caso di spegnimento va resettato il suo valore, altrimenti se 
		// va visualizzato ed il dato è vuoto, va inizializzato con il valore di default
		if (!visualizza) setValue(campoCorrettivo, "");
		else {
			if (correttivo == "") {
				if (tipoAppalto == '1')
					setValue(campoCorrettivo, correttivoLavori);
				else
					setValue(campoCorrettivo, correttivoFornitureServizi);
			}
		} 
		showObj("row"+campoCorrettivo, visualizza);
	}
	
		
	function setTipoCategorie(tipoCategoria){
		if(tipoCategoria == "") tipoCategoria = "1";
		setTipoCategoriaPrevalentePerArchivio(tipoCategoria);
		setTipoCategoriaUlteriorePerArchivio(tipoCategoria);
		
	}
	
	// Funzione per cambiare la condizione di where nell'apertura
	// dell'archivio delle categorie dell'appalto per la categoria prevalente
	function setTipoCategoriaPrevalentePerArchivio(tipoCategoria){
		if(document.forms[0].modo.value != "VISUALIZZA"){
			if(getValue("CATG_CATIGA") == "" || getValue("CAIS_TIPLAVG") == ""){
				setValue("CAIS_TIPLAVG", "" + tipoCategoria);
			}
		}
	}
	
	function setTipoCategoriaUlteriorePerArchivio(tipoCategoria){
		if(document.forms[0].modo.value != "VISUALIZZA"){
			var functionId = "default";
			var parametriWhere = "";
		
			for(var i=1; i <= maxIdOPESVisualizzabile; i++){
				eval("document.formUlterioreCategoriaGare" + i + ".archFunctionId").value = functionId;
			
				if(getValue("OPES_CATOFF_" + i) == ""){
					parametriWhere = "N:" + tipoCategoria;
				} else {
					parametriWhere = "N:" + getValue("CAIS_TIPLAVG_" + i);
				}
				
				eval("document.formUlterioreCategoriaGare" + i + ".archWhereParametriLista").value = parametriWhere;
			}
		}
	}
	
	<c:if test='${modoAperturaScheda ne "VISUALIZZA" and tipologiaGara == "3"}'>
		setTipoCategorie(getValue("TORN_TIPGEN"));
		<c:if test='${modoAperturaScheda eq "NUOVO" and (isProceduraTelematica or param.proceduraTelematica eq 1)}'>
			function initGare1(){
				setOriginalValue("GARE1_ESPLPORT"," ");
			}
			
			initGare1();
		</c:if>
	</c:if>
	
	<c:if test='${modo ne "VISUALIZZA"}'>
		
		//In base al valore di tipgen viene gestita la visualizzazione
		//dei campi fittizzi OGGCONT_LAVORI e OGGCONT_FORNITURE
		function showOGGCONT(tipgen, sbianca){
			showObj("rowTORN_OGGCONT", false);
			if (tipgen == "3"){
				showObj("rowV_GARE_TORN_OGGCONT_LAVORI", false);
				showObj("rowV_GARE_TORN_OGGCONT_FORNITURE", false);
				if (sbianca == true) {
					setValue("V_GARE_TORN_OGGCONT_LAVORI", "" );
					setValue("V_GARE_TORN_OGGCONT_FORNITURE", "" );
					setValue("TORN_OGGCONT", "" );
				}
			}else if(tipgen == "2"){
				showObj("rowV_GARE_TORN_OGGCONT_LAVORI", false);
				showObj("rowV_GARE_TORN_OGGCONT_FORNITURE", true);
				if (sbianca == true) {
					setValue("V_GARE_TORN_OGGCONT_LAVORI", "" );
					setValue("V_GARE_TORN_OGGCONT_FORNITURE", "" );
					setValue("TORN_OGGCONT", "" );
				}
			}else{
				showObj("rowV_GARE_TORN_OGGCONT_LAVORI", true);
				showObj("rowV_GARE_TORN_OGGCONT_FORNITURE", false);
				if (sbianca == true) {
					setValue("V_GARE_TORN_OGGCONT_LAVORI", "" );
					setValue("V_GARE_TORN_OGGCONT_FORNITURE", "" );
					setValue("TORN_OGGCONT", "" );
				}
			}
				
		}
		
		//La funzione filtra i valori del tabellato A1031,
		//lasciando quelli con tab1tip <10
		function TABA1031perLavori(){
			if(document.getElementById("V_GARE_TORN_OGGCONT_LAVORI")&& (document.getElementById("rowTORN_OGGCONT"))){
				var num_option=eval(document.getElementById('V_GARE_TORN_OGGCONT_LAVORI').options.length);
				for(a=num_option - 1;a>=0;a--){
					var value = eval(document.getElementById('V_GARE_TORN_OGGCONT_LAVORI').options[a].value);
					if (value != null && parseInt(value) >=10){
						document.getElementById('V_GARE_TORN_OGGCONT_LAVORI').options[a]=null;
					}
				}
			}
		}
		
		//La funzione filtra i valori del tabellato A1031,
		//lasciando quelli con tab1tip >=10
		function TABA1031perForniture(){
			if(document.getElementById("V_GARE_TORN_OGGCONT_FORNITURE")&& (document.getElementById("rowTORN_OGGCONT"))){
				var num_option=eval(document.getElementById('V_GARE_TORN_OGGCONT_FORNITURE').options.length);
				for(a=num_option -1 ;a>=0;a--){
					var value = eval(document.getElementById('V_GARE_TORN_OGGCONT_FORNITURE').options[a].value);
					if (value != null && parseInt(value) < 10){
						document.getElementById('V_GARE_TORN_OGGCONT_FORNITURE').options[a]=null;
					}
					
				}	
			}
				
		}
		
		<c:if test='${gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.OGGCONT") && ((tipologiaGara == "3" && gene:checkProt(pageContext, "SEZ.MOD.GARE.TORN-OFFUNICA-scheda.DATIGEN.ALT")) || (tipologiaGara ne "3" && gene:checkProt(pageContext, "SEZ.MOD.GARE.TORN-scheda.DATIGEN.ALT")))}'>
			TABA1031perLavori();
			TABA1031perForniture();
		</c:if>
		showOGGCONT(getValue("TORN_TIPGEN"),false);
		
		function aggiornaOGGCONT(valore){
			setValue("TORN_OGGCONT", valore );
		}
		
	</c:if>


	function gestioneCriterioAggiudicazione(critlic,detlic) {
		if (critlic == 1 || critlic == 3) {
			showObj("rowTORN_DETLIC", true);
		} else {
			showObj("rowTORN_DETLIC", false);
		}
		<c:if test='${tipologiaGara == "3"}'>
			if (critlic == 1 && detlic == 3) {
				showObj("rowGARE_RIBCAL", true);
			} else {
				showObj("rowGARE_RIBCAL", false);
			}
		</c:if>
		
		if(critlic == 3){
			showObj("rowTORN_CALCSOAN", false);
			showObj("rowTORN_OFFAUM", false);
		}else{
			showObj("rowTORN_CALCSOAN", true);
			showObj("rowTORN_OFFAUM", true);
		}
	}

	function gestioneCRITLIC(critlic) {
		var calcsoan = getValue("TORN_CALCSOAN");
		var garaQformOfferte = "${garaQformOfferte}";
		if(garaQformOfferte != "true"){
			document.forms[0].TORN_DETLIC.value='';
			setValue("GARE_RIBCAL",1);
		}else if(garaQformOfferte == "true" && critlic == 2){
			document.forms[0].TORN_DETLIC.value='';
		}else if(garaQformOfferte == "true" && critlic != 2){
			document.forms[0].TORN_DETLIC.value=4;
		}
		document.forms[0].TORN_APPLEGREG.value='';
		
		<c:if test='${tipologiaGara == "3"}'>
			if (critlic == 2 || critlic == 3 ||calcsoan == '2') {
				showObj("rowGARE_MODASTG", false);
				document.forms[0].GARE_MODASTG.value='2';
			}else{
				document.forms[0].GARE_MODASTG.value='';
				showObj("rowGARE_MODASTG", true);
			} 
		</c:if>
	}
	
	<c:if test="${not isVecchiaOepv}">	
	function gestioneOFFAUM(critlic) {
		var offtel = getValue("TORN_OFFTEL");
		var modlic = getValue("TORN_MODLIC");
		if(offtel && offtel == 1 && modlic == 6){
			showObj("rowTORN_OFFAUM", false);
			setValue("TORN_OFFAUM",2);
		} else if(critlic != 3){
			showObj("rowTORN_OFFAUM", true);
		}
	}
	</c:if>
	
	function gestioneDETLIC(detlic) {
		var critlic = getValue("TORN_CRITLIC");
		var tipgen = getValue("TORN_TIPGEN");
		
		document.forms[0].TORN_APPLEGREG.value='';
		
		<c:if test='${tipologiaGara == "3"}'>
			//Solo se offerta prezzi unitari, imposta RIBCAL in base al tipo di gara (lavori, forniture, servizi)
			if ((critlic == 1 && detlic == 3 && tipgen != 1) || detlic == 4) {
				setValue("GARE_RIBCAL",2);
			} else {
				setValue("GARE_RIBCAL",1);
			}
		</c:if>
	
	}
	
	function gestioneULTDETLIC(critlic,detlic) {
		if ((critlic == 1 && (detlic == 3 || detlic == 4)) || critlic == 2) {
			showObj("rowTORN_ULTDETLIC",true);
		}else{
			setValue("TORN_ULTDETLIC","");
			showObj("rowTORN_ULTDETLIC",false);
		}
	}
		
	
	function gestioneCALCSOAN(calcsoan,impostaValori) {
		<c:if test='${tipologiaGara == "3"}'>
			var critlic = getValue("TORN_CRITLIC");
			if (impostaValori == 'true'){
				//document.forms[0].GARE_MODASTG.value='';
				document.forms[0].TORN_APPLEGREG.value='';
				if(critlic == 2 || critlic == 3){
					document.forms[0].GARE_MODASTG.value='2';
				}else{
					document.forms[0].GARE_MODASTG.value='';
				}
			}	
			if (calcsoan == 2) {
				if (impostaValori == 'true')
					setValue("GARE_MODASTG","2");
				showObj("rowGARE_MODASTG", false);
			}else{
				//showObj("rowGARE_MODASTG", true);
				if(critlic == 2){
					showObj("rowGARE_MODASTG", false);
				}else{
					showObj("rowGARE_MODASTG", true);
				}
			}
		</c:if>
	}
	
	<c:if test='${tipologiaGara == "3"}'>
		function gestioneFlagSicurezzaInclusa(critlicg,detlicg) {
			if (detlicg == 3 || critlicg == 2 || detlicg == 4) {
				showObj("rowGARE_SICINC", true);
				
			} else {
				showObj("rowGARE_SICINC", false);
				setValue("GARE_SICINC",1);
			}
		}
	</c:if>
		
	function calcolaMODLIC(critlic,detlic,calcsoan,applegreg) {
		var ret = "";
		if (critlic!="") {
			if (critlic == 1 && detlic !="" && calcsoan !="") {
				if (detlic == 1 && calcsoan == '1') ret = 13;
				if (detlic == 1 && calcsoan == '2') ret = 1;
				if (detlic == 2 && calcsoan == '1') ret = 13;
				if (detlic == 2 && calcsoan == '2') ret = 1;
				if (detlic == 3 && calcsoan == '1') ret = 14;
				if (detlic == 3 && calcsoan == '2') ret = 5;
				if (detlic == 4 && calcsoan == '1') ret = 13;
				if (detlic == 5 && calcsoan == '1') ret = 13;
				if (detlic == 4 && calcsoan == '2') ret = 1;
				if (detlic == 5 && calcsoan == '2') ret = 1;
				//Regione Sicilia
				if(calcsoan == '1'){
					if (detlic == 1 && applegreg == '1') ret = 15;
					if (detlic == 2 && applegreg == '1') ret = 15;
					if (detlic == 3 && applegreg == '2') ret = 16;
				}
			}
			if (critlic == 2) {
				ret = 6;			
			} 
			if (critlic == 3) {
				ret = 17;			
			} 
		}
		
		return ret;
	}
		
	function visualizzaTIPFORN(tipgen){
			if(tipgen == "2"){
				showObj("rowTORN_TIPFORN", true);
			}else{
				showObj("rowTORN_TIPFORN", false);
				setValue("TORN_TIPFORN", "");
			}
		}
	
	<c:if test='${tipologiaGara == "3"}'>
		var calcsoan = "${datiRiga.TORN_CALCSOAN}"
		gestioneCALCSOAN(calcsoan,"false");
		
		function aggiornaFiltroArchivioElencoOperatori(){
		var catiga = getValue("CATG_CATIGA");
		if(document.formArchivioElencoOperatori !=null ){
			if(catiga != null && catiga != ""){
				document.formArchivioElencoOperatori.archFunctionId.value = "torn_notBlankCatigaDatigen";
				document.formArchivioElencoOperatori.archWhereParametriLista.value = "T:" + catiga;
			}else{
				document.formArchivioElencoOperatori.archFunctionId.value = "torn_blankCatigaDatigen";
				}
			}
		}
		
		function visualizzaNumeroClassifica(tipoAppalto, isfoglia, entita, progressivo, sbiancaValori){
			var idRiga1, idRiga2, idRiga3, idRiga4, idRiga7 = "";
			var nomeCampo1, nomeCampo2, nomeCampo3, nomeCampo4, nomeCampo5, nomeCampo6, nomeCampo7 = "";
			
			if(entita == "CATG"){
				nomeCampo1 = "NUMCLA_CAT_LAVORI";
				nomeCampo2 = "NUMCLA_CAT_FORNITURE";
				nomeCampo3 = "NUMCLA_CAT_SERVIZI";
				nomeCampo4 = "NUMCLA_CAT_LAVORI150";
				nomeCampo5 = "CATG_NUMCLA";
				nomeCampo6 = "CATG_IMPIGA";
				nomeCampo7 = "NUMCLA_CAT_SERVIZIPROFESSIONALI";
			} else {
				if(progressivo == null) progressivo = "";
			  nomeCampo1 = "NUMCLU_CAT_LAVORI_" + progressivo;
			  nomeCampo2 = "NUMCLU_CAT_FORNITURE_" + progressivo;
			  nomeCampo3 = "NUMCLU_CAT_SERVIZI_" + progressivo;
			  nomeCampo4 = "NUMCLU_CAT_LAVORI150_" + progressivo;
			  nomeCampo5 = "OPES_NUMCLU_" + progressivo;
			  nomeCampo6 = "OPES_ISCOFF_" + progressivo;
			  nomeCampo7 = "NUMCLU_CAT_SERVIZIPROFESSIONALI_" + progressivo;
			}
			
		  	idRiga1 = "row" + nomeCampo1;
		  	idRiga2 = "row" + nomeCampo2;
		  	idRiga3 = "row" + nomeCampo3;
		  	idRiga4 = "row" + nomeCampo4;
		  	idRiga7 = "row" + nomeCampo7;
			
			if (tipoAppalto == "")
				tipoAppalto=getValue("TORN_TIPGEN");
			if(tipoAppalto == "1"){
				showObj(idRiga1, true);
				showObj(idRiga2, false);
				showObj(idRiga3, false);
				showObj(idRiga4, false);
				showObj(idRiga7, false);

				if(sbiancaValori){
					setValue(nomeCampo1, "");
				}
								
			} else if(tipoAppalto == "2"){
				showObj(idRiga1, false);
				if(${esisteClassificaForniture}){
					showObj(idRiga2, true);
					if(isfoglia=="2"){
					document.getElementById(nomeCampo2).disabled = true;
					if(sbiancaValori)
						setCampoNumeroClassifica("", entita, progressivo);
					}else {
	                       document.getElementById(nomeCampo2).disabled = false;
					}
				}else
					showObj(idRiga2, false);
				showObj(idRiga3, false);
				showObj(idRiga4, false);
				showObj(idRiga7, false);
				
				if(sbiancaValori){
					setValue(nomeCampo2, "");
				}
	
			} else if(tipoAppalto == "3"){
				showObj(idRiga1, false);
				showObj(idRiga2, false);
				if(${esisteClassificaServizi}){
					showObj(idRiga3, true);
					if(isfoglia=="2"){
					document.getElementById(nomeCampo3).disabled = true;
					if(sbiancaValori)
						setCampoNumeroClassifica("", entita, progressivo);
					}else {
					       document.getElementById(nomeCampo3).disabled = false;
					}
				}else
					showObj(idRiga3, false);
				showObj(idRiga4, false);
				showObj(idRiga7, false);
	
				if(sbiancaValori){
					setValue(nomeCampo3, "");
				}
			} else if(tipoAppalto == "4"){
				showObj(idRiga1, false);
				showObj(idRiga2, false);
				showObj(idRiga3, false);
				if(${esisteClassificaLavori150})
					showObj(idRiga4, true);
				else
					showObj(idRiga4, false);
				showObj(idRiga7, false);
	
				if(sbiancaValori){
					setValue(nomeCampo4, "");
				}
			}else if(tipoAppalto == "5"){
				showObj(idRiga1, false);
				showObj(idRiga2, false);
				showObj(idRiga3, false);
				showObj(idRiga4, false);
				if(${esisteClassificaServiziProfessionali}){
					showObj(idRiga7, true);
					if(isfoglia=="2"){
					document.getElementById(nomeCampo7).disabled = true;
					if(sbiancaValori)
						setCampoNumeroClassifica("", entita, progressivo);
					}else {
						document.getElementById(nomeCampo7).disabled = false;
					}
				}else
					showObj(idRiga7, false);
					
				if(sbiancaValori){
					setValue(nomeCampo7, "");
				}
			}
			if(sbiancaValori){
				setValue(nomeCampo5, "");
				setValue(nomeCampo6, "");
			}
		}

		// funzione da eseguire al caricamento della pagina per visualizzare la
		// riga corretta relativa al campo "Classifica"
		function initVisualizzazioneCampiNumeroClassifica(){
			var tipoAppalto=getValue("TORN_TIPGEN");
			var str = getValue("CAIS_TIPLAVG");
			var isfoglia = getValue("V_CAIS_TIT_ISFOGLIA");
			if(str != null && str != "")
				visualizzaNumeroClassifica(str,isfoglia, "CATG", null, false);
			else
				visualizzaNumeroClassifica(tipoAppalto,isfoglia, "CATG", null, false);
			
			for(var i=1; i <= ${fn:length(ulterioriCategorie)}; i++){
				str = getValue("CAIS_TIPLAVG_" + i);
				isfoglia = getValue("V_CAIS_TIT_ISFOGLIA_" + i);
				if(str != null && str != "")
					visualizzaNumeroClassifica(str, isfoglia, "OPES", new String(i), false);
				else
					visualizzaNumeroClassifica(tipoAppalto, isfoglia, "OPES", new String(i), false);
			}
		}
	
		initVisualizzazioneCampiNumeroClassifica();
		
		<c:if test='${modo ne "VISUALIZZA"}'>
			var showNextElementoSchedaMultipla_Default=showNextElementoSchedaMultipla;
			function showNextElementoSchedaMultipla_Custom(tipo, campi,visibilitaCampi){
				showNextElementoSchedaMultipla_Default(tipo, campi,visibilitaCampi);
				if (tipo=="OPES"){
					var indice = eval("lastId" + tipo + "Visualizzata");
					var tipoAppalto=getValue("TORN_TIPGEN");
					var isfoglia = getValue("V_CAIS_TIT_ISFOGLIA_" + indice);
					visualizzaNumeroClassifica(tipoAppalto,isfoglia, "OPES",indice,false);
				}
			}
			showNextElementoSchedaMultipla =   showNextElementoSchedaMultipla_Custom;
			
			var arrayImportiIscrizioneLavori = new Array();  //Il vettore contiene a sua volta il vettore[importo,numero classifica]
			var numeroClassiNegative = 0;
			var indice=0;
	        <c:forEach items="${importiIscrizioneLavori}" var="parametro" varStatus="ciclo">
				<c:if test='${empty parametro.arcTabellato  or parametro.arcTabellato != "1"}'>
	                arrayImportiIscrizioneLavori[indice] =  new Array("${fn:trim(parametro.datoSupplementare)}","${parametro.tipoTabellato}");
	    		    indice++;
	                if (${parametro.tipoTabellato < 0})
	    				numeroClassiNegative = numeroClassiNegative + 1;
	            </c:if>
			</c:forEach>
		
			var arrayImportiIscrizioneForniture = new Array();
			<c:forEach items="${importiIscrizioneForniture}" var="parametro" varStatus="ciclo">
				arrayImportiIscrizioneForniture[${ciclo.index}] = "${fn:trim(parametro.datoSupplementare)}";
			</c:forEach>
			
			var arrayImportiIscrizioneServizi = new Array();
			<c:forEach items="${importiIscrizioneServizi}" var="parametro" varStatus="ciclo">
				arrayImportiIscrizioneServizi[${ciclo.index}] = "${fn:trim(parametro.datoSupplementare)}";
			</c:forEach>
			
			var arrayImportiIscrizioneLavori150 = new Array();
			<c:forEach items="${importiIscrizioneLavori150}" var="parametro" varStatus="ciclo">
				arrayImportiIscrizioneLavori150[${ciclo.index}] = "${fn:trim(parametro.datoSupplementare)}";
			</c:forEach>
		
			var arrayImportiIscrizioneServiziProfessionali = new Array();
			<c:forEach items="${importiIscrizioneServiziProfessionali}" var="parametro" varStatus="ciclo">
				arrayImportiIscrizioneServiziProfessionali[${ciclo.index}] = "${fn:trim(parametro.datoSupplementare)}";
			</c:forEach>
			
			function setCampoNumeroClassifica(numeroClassifica, entita, progressivo){
				var campoNumeroClassifica, campoImportoIscrizione = "";
				var campoTiplavg = "CAIS_TIPLAVG";
				
				if(entita == "CATG"){
					campoNumeroClassifica  = entita + "_" + "NUMCLA";
					campoImportoIscrizione = entita + "_" + "IMPIGA";
				} else {
				  	campoNumeroClassifica  = entita + "_" + "NUMCLU_" + progressivo;
				  	campoImportoIscrizione = entita + "_" + "ISCOFF_" + progressivo;
					campoTiplavg           = campoTiplavg + "_" + progressivo;
				}
				setValue(campoNumeroClassifica, numeroClassifica);
				if(numeroClassifica != null && numeroClassifica != ""){
					var tipoAppalto=getValue(campoTiplavg);
					if (tipoAppalto == null || tipoAppalto == "")
						tipoAppalto=getValue("TORN_TIPGEN");
					if(tipoAppalto == "1")
						for(i=0; i < arrayImportiIscrizioneLavori.length; i++){
							if(toNum(numeroClassifica) == arrayImportiIscrizioneLavori[i][1])
								setValue(campoImportoIscrizione, arrayImportiIscrizioneLavori[i][0]);
						}
					else if(tipoAppalto == "2")
						setValue(campoImportoIscrizione, arrayImportiIscrizioneForniture[toNum(numeroClassifica) - 1]);
					else if(tipoAppalto == "3")
						setValue(campoImportoIscrizione, arrayImportiIscrizioneServizi[toNum(numeroClassifica) - 1]);
					else if(tipoAppalto == "4")
						setValue(campoImportoIscrizione, arrayImportiIscrizioneLavori150[toNum(numeroClassifica) - 1]);
					else if(tipoAppalto == "5")
						setValue(campoImportoIscrizione, arrayImportiIscrizioneServiziProfessionali[toNum(numeroClassifica) - 1]);
				} else
					setValue(campoImportoIscrizione, "");
			}
			
		</c:if>
		
		
			
	</c:if>
	
	function leggiComunicazioni() {
		var entitaWSDM="GARE";
		var chiaveWSDM=getValue("GARE_NGARA");
		var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=geneweb/w_invcom/w_invcom-in-lista.jsp&genere=${! empty datiRiga.GARE_GENERE ? datiRiga.GARE_GENERE : 1}&chiave=" + document.forms[0].key.value;
		href+="&entitaWSDM=" + entitaWSDM + "&chiaveWSDM=" + chiaveWSDM;
		if(idconfi){
			href = href + "&idconfi="+idconfi;
		}
		document.location.href = href;
	}

	function inviaComunicazioni() {
		var entitaWSDM="GARE";
		var chiaveWSDM=getValue("GARE_NGARA");
		var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=geneweb/w_invcom/w_invcom-lista.jsp&genere=${! empty datiRiga.GARE_GENERE ? datiRiga.GARE_GENERE : 1}&entita=" + document.forms[0].entita.value + "&chiave=" + document.forms[0].key.value;
		href+="&entitaWSDM=" + entitaWSDM + "&chiaveWSDM=" + chiaveWSDM;
		if(idconfi){
			href = href + "&idconfi="+idconfi;
		}
		document.location.href = href;
	}
	
	function esportaAntimafiaVerifica() {
		var href="href=gare/gare/gare-popup-exportAntimafia.jsp&codiceGara=${datiRiga.TORN_CODGAR}&tipoRichiesta=VERIFICA&islottoGara=false";
		openPopUpCustom(href, "exportXMLantimafia", 550, 300, "yes", "yes");
	}
	
	function modificaImportoComplessivo(){
			var importo = getValue("TORN_IMPTOR");
			var href="href=gare/torn/torn-popup-modificaImportoComplessivo.jsp&codiceGara=${datiRiga.TORN_CODGAR}&importo=" + importo;
			openPopUpCustom(href, "modificaImportoComplessivo", 550, 300, "yes", "yes");
		}
	
	function valorizzaStatoGara(){
		
		<c:choose>
			<c:when test='${tipologiaGara eq "3"}'>
				var esineg = getValue("GARE_ESINEG");
			</c:when>
			<c:otherwise>
				var esineg = getValue("TORN_ESINEG");
			</c:otherwise>
		</c:choose>
		
		if(esineg==null)
			esineg="";
		
		var esisteDittaNonValorizzato = "false";
		<c:if test='${esisteDittaNonValorizzato eq "true"}'>
			esisteDittaNonValorizzato= "true";	
		</c:if>
		var dittaLottiTuttiValorizzati ="false";
		<c:if test='${dittaLottiTuttiValorizzati eq "Si"}'>
			dittaLottiTuttiValorizzati= "true";	
		</c:if>
		var esisteLottoDittaEsinegNulli ="${esisteLottoDittaEsinegNulli }";
		var tuttiLottiDittaOEsinegValorizzati = "${tuttiLottiDittaOEsinegValorizzati }";
				
		var iterga = getValue("TORN_ITERGA");
		
		var dataPartecipazione;
		var oraPartecipazione;
		if(iterga==2 || iterga==4 || iterga==7){
			dataPartecipazione = getValue("TORN_DTEPAR");
			oraPartecipazione = getValue("TORN_OTEPAR");
		}else{
			dataPartecipazione = getValue("TORN_DTEOFF");
			oraPartecipazione = getValue("TORN_OTEOFF");
		}
		
		var garaSospesa = getValue("GARSOSPE_ID");
		
		if(garaSospesa != ''){
			setValue("STATOGARA","Sospesa");
		} 
		else{
			<c:choose>
			<c:when test='${tipologiaGara ne "3"}'>
			if(tuttiLottiDittaOEsinegValorizzati =="Si"){
				setValue("STATOGARA","Conclusa");
			}
			</c:when>
			<c:otherwise>
			if(tuttiLottiDittaOEsinegValorizzati=="Si" || esineg!=""){
				setValue("STATOGARA","Conclusa");
			}
			</c:otherwise>
		</c:choose>
		else if(dataPartecipazione!=""){
			if(oraPartecipazione=="")
				oraPartecipazione ="23:59:59";
			
			var dataTmp = dataPartecipazione.split("/");
			dataPartecipazione = dataTmp[1]+"/"+dataTmp[0]+"/"+dataTmp[2];
			dataPartecipazione = new Date(dataPartecipazione + " " + oraPartecipazione);
			
			var dataOdierna = new Date();
			dataOdierna.setSeconds(0); 
			dataOdierna.setMilliseconds(0);
			
			var dattaOggi = dataOdierna.getTime();
			var dataPArt = dataPartecipazione.getTime();
			<c:choose>
				<c:when test='${tipologiaGara ne "3"}'>
					if(dataPArt>=dattaOggi && esisteLottoDittaEsinegNulli == "Si"){
						setValue("STATOGARA","In corso");
					}else if(dataPArt < dattaOggi && esisteLottoDittaEsinegNulli=="Si"){
						setValue("STATOGARA","In aggiudicazione");
					}
				</c:when>
				<c:otherwise>
					if(dataPArt>=dattaOggi && esisteDittaNonValorizzato=="true" && esineg=="" ){
						setValue("STATOGARA","In corso");
					}else if(dataPArt < dattaOggi && esisteLottoDittaEsinegNulli=="Si" && esineg==""){
						setValue("STATOGARA","In aggiudicazione");
					} 
				</c:otherwise>
			</c:choose>
			
				
		}else if(dataPartecipazione==""){
			var dittaLottiTuttiNulli="false";
			<c:if test='${dittaLottiTuttiNulli eq "Si"}'>
				dittaLottiTuttiNulli="true";
			</c:if>
			
			<c:choose>
				<c:when test='${tipologiaGara ne "3"}'>
					if(dittaLottiTuttiNulli=="true" || esisteLottoDittaEsinegNulli=="Si")
						setValue("STATOGARA","");
				</c:when>
				<c:otherwise>
					if(dittaLottiTuttiNulli=="true" || (esisteDittaNonValorizzato=="true" && esineg==""))
						setValue("STATOGARA","");
				</c:otherwise>
			</c:choose>
			
		}
		}
	}
	
	function impostaGaraNonAggiudicata(){
		var href="href=gare/commons/popup-ImpostaGaraNonAggiudicata.jsp";
		var ngara="${datiRiga.TORN_CODGAR}";
		<c:choose>
			<c:when test='${tipologiaGara ne "3"}'>
				var esineg = "${datiRiga.TORN_ESINEG}";
				var datneg = "${datiRiga.TORN_DATNEG}";
				var npannrevagg ="";
				href+="&isOffertaUnica=No";
				href+="&isOfferteDistinte=Si";
			</c:when>
			<c:otherwise>
				var esineg = "${datiRiga.GARE_ESINEG}";
				var datneg = "${datiRiga.GARE_DATNEG}";
				var npannrevagg ="${datiRiga.GARE1_NPANNREVAGG}";
				href+="&isOffertaUnica=Si";
				href+="&isOfferteDistinte=No";
			</c:otherwise>
		</c:choose>
		href+="&ngara=" + ngara + "&codgar1=" + ngara + "&esineg=" + esineg + "&datneg=" + datneg + "&npannrevagg=" + npannrevagg;
		openPopUpCustom(href, "impostaGaraNonAggiudicata", 700, 400, "yes", "yes");
	}
	
	function sospendiGara(codgar,iterga,opz) {
		var href="href=gare/commons/popup-SospendiGara.jsp&codgar=" + codgar + "&iterga=" + iterga + "&opz=" + opz;
		openPopUpCustom(href, "sospendiGara", 800, 400, "yes", "yes");
	}
	
	//Si redefinisce la funzione archivioLista per sbiancare il valore di archValueChiave
	function archivioListaCustom(nomeArchivio){
		if(nomeArchivio.indexOf("formUlterioreCategoriaGare") >= 0 || nomeArchivio == "formCategoriaPrevalenteGare")
			eval("document." + nomeArchivio + ".archValueChiave").value = "";
		if(nomeArchivio=="formUFFINTTORN"){
        	if(!checkPuntiContatto()){
        	     alert("Non è possibile modificare il valore perchè ci sono riferimenti ai relativi punti di contatto nella gara");
                 return;
            }
        }
		
		archivioListaDefault(nomeArchivio);
	 	
	 }

	var archivioListaDefault = archivioLista;
	var archivioLista = archivioListaCustom;
	
	<c:if test='${tipologiaGara == "3" && modo ne "VISUALIZZA"}'>
	
	
	//Gestione onchange campi archivio categoria prevalente
	if(document.getElementById("CATG_CATIGA")!=null)
		document.getElementById("CATG_CATIGA").onchange = modificaCampoArchivio;
	if(document.getElementById("CAIS_DESCAT")!=null)
	document.getElementById("CAIS_DESCAT").onchange = modificaCampoArchivio;
	
	//Gestione onchange campi archivio ulteriori categorie
	for(var i=1; i <= maxIdOPESVisualizzabile ; i++){
		if(document.getElementById("OPES_CATOFF_" + i)!=null)
			document.getElementById("OPES_CATOFF_" + i).onchange = modificaCampoArchivio;
		if(document.getElementById("CAIS_DESCAT_" + i)!=null)
			document.getElementById("CAIS_DESCAT_" + i).onchange = modificaCampoArchivio;
		
	}
	
	//Viene ridefinito l'onchange dei campi collegati all'archivio di modo che il valore inserito
	//nel campo viene impostato nel campo "archValueChiave", mentre viene sbiancato il campo archCampoChanged
	//in modo che nella popup dell'archivio delle categorie posso impostare manualmente la where senza che 
	//venga inserita in automatico la condizione sul campo valorizzato. 
	function modificaCampoArchivio(){
		var campo =this.id;
		var valore = this.value;
						
		//Valorizzazione della variabile globale activeArchivioForm			
		for(var s=0; s < document.forms.length; s++){
			try {
				if(eval("document." + document.forms[s].name + ".archCampi.value.indexOf(new String('" + campo + "')) >= 0")){
					activeArchivioForm = document.forms[s].name;
					break;
				}
			} catch(err) {
				// Oggetto inesistente nell's-esimo form
			}
		}
		
		if(valore!= null && valore !="")
			valore = valore.toUpperCase();
		
		else{
			getArchivio(activeArchivioForm).sbiancaCampi(0);
			return;
		}
		
		eval("document." + activeArchivioForm +".archValueChiave").value = valore;
		
		this.value="";
		//Apertura dell'archivio 
		eval("document." + activeArchivioForm +".metodo").value = "lista";
		getArchivio(activeArchivioForm).submit(true);
		
	}
	
	var gartel = getValue("TORN_GARTEL");
	var offtel = getValue("TORN_OFFTEL");
	
	if(gartel!=1 || (gartel==1 && offtel==2)){
		$("#GARE_RIBCAL option[value='3']").remove();
	}
	
	</c:if>
	arrayTabellatoA1z04= new Array();
        <c:forEach items="${listaValoriA1z04}" var="punteggio" varStatus="indice" >
        	arrayTabellatoA1z04[${indice.index}] = ${punteggio};
        </c:forEach>
		
	function calcolaITERGA(tipogara){
		var valoreTrovato= false;
		var ret = "";
        for(i=0; i< arrayTabellatoA1z04.length;i++){
			var valori = arrayTabellatoA1z04[i];
			var valoreTipogara= valori[1];
			if(tipogara==valoreTipogara){
				ret = valori[0];
                   valoreTrovato=true;
				break;
			}
		}
		if(valoreTrovato==false)
			ret="2";
		
		//Se iterga != 1,2 si visualizzano alteng e tipneg, altrimenti si nascondono
		var vis = true;
		if(ret=="1" || ret =="2")
			vis=false;
		showObj("rowTORN_TIPNEG",vis);
		showObj("rowTORN_ALTNEG",vis);
		if (!vis) {
			setValue("TORN_TIPNEG","");
			setValue("TORN_ALTNEG","");
		}
		
        return ret;
		
	}
		
	function popupInviaVigilanza(codgar){
		var comando = "href=gare/commons/invia-vigilanza-pg.jsp";
	   	comando += "&codgar=" + codgar;
	   	<c:choose>
	   		<c:when test="${!empty nomeApplicativo and nomeApplicativo!=''}">
	   			var nomeApplicativo = "${nomeApplicativo}";
	   		</c:when>
	   		<c:otherwise>
	   			var nomeApplicativo = "Vigilanza";
	   		</c:otherwise>
	   	</c:choose>
	   	comando += "&nomeApplicativo=" +  nomeApplicativo ;
	   	<c:choose>
	   		<c:when test='${tipologiaGara eq "3"}'>
		   	comando += "&genereGara=3";
		   	</c:when>
		   	<c:otherwise>
		   	comando += "&genereGara=1";
		   	</c:otherwise>
	   	</c:choose>
	   	document.location.href = "${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&"+comando;
	}
	
	function popupInviaDatiCig(codgar) {
  		<c:choose>
  			<c:when test='${tipologiaGara == "3"}'>
  				var genere=3;
  			</c:when>
  			<c:otherwise>
  				var genere=1;
  			</c:otherwise>
  		</c:choose>
  		var comando = "href=gare/commons/popup-invia-dati-richiesta-cig.jsp";
  		comando = comando + "&codgar=" + codgar+ "&genere=" + genere;
  		var uuidTorn= getValue("TORN_UUID");
  		comando+="&uuidTorn=" + uuidTorn;
  		openPopUpCustom(comando, "inviadaticig", 550, 650, "yes", "yes");
	}
	
	function checkPuntiContatto(){
		var puntoContatto = getValue("TORN_PCOPRE");
		if(puntoContatto!= null && puntoContatto!=""){
			return false;
		}
		puntoContatto = getValue("TORN_PCODOC");
		if(puntoContatto!= null && puntoContatto!=""){
			return false;
		}
		puntoContatto = getValue("TORN_PCOOFF");
		if(puntoContatto!= null && puntoContatto!=""){
			return false;
		}
		puntoContatto = getValue("TORN_PCOGAR");
		if(puntoContatto!= null && puntoContatto!=""){
			return false;
		}
		return true;
	} 
	
	function visualizzaSezioneTerminiConsegna(){
		var tipgen = getValue("TORN_TIPGEN");
		var accqua = getValue("TORN_ACCQUA");
		var visibile=true;
		if(tipgen==1 || accqua=="1"){
			visibile=false;
		}
		showObj("rowCONSEGNA",visibile);
		showObj("rowGARE1_DTERMCON", visibile);
		showObj("rowGARE1_NGIOCON", visibile);
	}
	visualizzaSezioneTerminiConsegna();
	
	function visualizzaMotacc(prourg){
		var visibile= false;
		if(prourg == 1){
			visibile=true;
		}else{
			setValue("TORN_MOTACC","");
		}	
		showObj("rowTORN_MOTACC",visibile);	
	}
	
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

	function apriGestionePermessiGaraTelematica(codgar, genereGara) {
		bloccaRichiesteServer();
<c:choose>
	<c:when test="${datiRiga.G_PERMESSI_PROPRI eq '1' or abilitazioneGare eq 'A'}" >
			formVisualizzaPermessiUtentiStandard.permessimodificabili.value = 'true';
	</c:when>
	<c:otherwise>
			formVisualizzaPermessiUtentiStandard.permessimodificabili.value = 'false';
	</c:otherwise>
</c:choose>
		formVisualizzaPermessiUtentiStandard.action = "${pageContext.request.contextPath}/pg/VisualizzaPermessiUtentiGaraTelematica.do";
		formVisualizzaPermessiUtentiStandard.codgar.value = codgar;
		formVisualizzaPermessiUtentiStandard.genereGara.value = genereGara; 
		formVisualizzaPermessiUtentiStandard.submit();
	}

	function apriGestionePermessiGaraTelematicaStandard(codgar, genereGara) {
		bloccaRichiesteServer();
<c:choose>
	<c:when test="${datiRiga.G_PERMESSI_PROPRI eq '1' or abilitazioneGare eq 'A'}" >
			formVisualizzaPermessiUtentiStandard.permessimodificabili.value = 'true';
	</c:when>
	<c:otherwise>
			formVisualizzaPermessiUtentiStandard.permessimodificabili.value = 'false';
	</c:otherwise>
</c:choose>
		formVisualizzaPermessiUtentiStandard.action = "${pageContext.request.contextPath}/pg/VisualizzaPermessiUtentiGaraTelematicaStandard.do";
		formVisualizzaPermessiUtentiStandard.codgar.value = codgar;
		formVisualizzaPermessiUtentiStandard.genereGara.value = genereGara;
		formVisualizzaPermessiUtentiStandard.submit();
	}


	<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.AutovieArchiflow")}'>
		addHrefs();
	</c:if>
	
	function listaIds(){
		var comando = "href=gare/v_lista_ids/popup-lista-ids.jsp&codiceGara=" + "${codgar}" + "&genereGara=" + "1";
 		openPopUpCustom(comando, "visualizzaListaIds", 900, 450, "yes", "yes");
	}

	function popupRettificaTermini(iterga,codgar,gartel){
		<c:choose>
  			<c:when test='${tipologiaGara == "3"}'>
  				var tipoGara=3;
  			</c:when>
  			<c:otherwise>
  				var tipoGara=1;
  			</c:otherwise>
  		</c:choose>
		var comando = "href=gare/commons/popup-rettificaTermini.jsp&codgar=" + codgar + "&ngara=" + codgar + "&iterga=" + iterga +"&pagina=Datigen" + "&tipoGara=" + tipoGara + "&gartel="+gartel;
	 	openPopUpCustom(comando, "rettificaTermini", 700, 350, "yes", "yes");
	}
	
	function nuovaComunicazione(){
		var IsW_CONFCOMPopolata = "${IsW_CONFCOMPopolata}";
		var tipo = 3;
		var numeroGara = getValue("TORN_CODGAR");
		var keyAdd = "W_INVCOM.COMKEY1=T:" + numeroGara;
		var keyParent = "TORN.CODGAR=T:" + numeroGara;
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
		document.location.href = href + "&" + csrfToken;;
	}
	
	function gestioneAQOPER(accqua){
		if(accqua == '1'){
			showObj("rowTORN_AQOPER",true);
			showObj("rowDAQ",true);
			showObj("rowTORN_AQDURATA",true);
			showObj("rowTORN_AQTEMPO",true);

			<c:if test='${tipologiaGara == "3"}'>
				showObj("rowTMP",false);
				showObj("rowGARE_DINLAVG",false);
				showObj("rowGARE_TEUTIL",false);
				showObj("rowGARE_TEMESI",false);
				
				showObj("rowCONSEGNA",false);
				showObj("rowGARE1_DTERMCON",false);
				showObj("rowGARE1_NGIOCON",false);
			</c:if>

			<c:if test='${modo ne "VISUALIZZA"}'>
				if(getValue("TORN_AQOPER")==null || getValue("TORN_AQOPER")=="")
					setValue("TORN_AQOPER","1");
				//$("#TORN_AQOPER").attr("disabled", "disabled");
				var aqtempo = getValue("TORN_AQTEMPO");
				if(aqtempo=="" || aqtempo ==null )
					setValue("TORN_AQTEMPO","2");
			</c:if>
		}else{
			showObj("rowTORN_AQOPER",false);
			showObj("rowDAQ",false);
			showObj("rowTORN_AQDURATA",false);
			showObj("rowTORN_AQTEMPO",false);

			<c:if test='${tipologiaGara == "3"}'>
				showObj("rowTMP",true);
				showObj("rowGARE_DINLAVG",true);
				showObj("rowGARE_TEUTIL",true);
				showObj("rowGARE_TEMESI",true);
				
				var tipgen = getValue("TORN_TIPGEN");
				var visibile=true;
				if(tipgen==2 || tipgen==3){
					showObj("rowCONSEGNA",true);
					showObj("rowGARE1_DTERMCON",true);
					showObj("rowGARE1_NGIOCON",true);
				}
			</c:if>
			
			<c:if test='${modo ne "VISUALIZZA"}'>
				setValue("TORN_AQOPER","");
				setValue("TORN_AQDURATA","");
				setValue("TORN_AQTEMPO","");
				gestioneAQNUMOPE("");
			</c:if>
		}
	}
	
	function gestioneAQNUMOPE(aqoper){
		if(aqoper == '2'){
			showObj("rowTORN_AQNUMOPE",true);
		}else{
			showObj("rowTORN_AQNUMOPE",false);
						
			<c:if test='${modo ne "VISUALIZZA"}'>
				setValue("TORN_AQNUMOPE","");
			</c:if>
		}
	}
	
	function aggiornaVisualizzazioneDaArchivioUffint(iscuc){
		if(iscuc==1){
			showObj("rowTORN_ALTRISOG",true);
		}else{
			showObj("rowTORN_ALTRISOG",false);
		}
		
	}
	
	
	function sbiancaArchivioUffintAltriSogg(){
		setValue("TORN_ALTRISOG", "");
			
	}
	
	function gestioneModcont(altrisog,aquoper){
		if(altrisog==3 || aquoper==2){
			setValue("TORN_MODCONT","1");
			$('#TORN_MODCONT').prop('disabled', 'disabled');
			
		}else
		 $('#TORN_MODCONT').prop('disabled', false);
	}
	
	function controlloCampoAqnumope(aqnumope){
		if(aqnumope!=null && aqnumope!=""){
			if(toVal(aqnumope)>1)
				return true;
			else
				return false;
		}else{
			return true;
		}
	}
	
	function popupControlla190(codgar){
		var comando = "href=gare/commons/popup-controllo190.jsp?codgar=" + codgar + "&genere=${datiRiga.GARE_GENERE}";
		openPopUpCustom(comando, "Controlla190", 900, 450, "yes", "yes");
	}
	
	<c:if test="${tipologiaGara == '3' and (isProceduraTelematica or param.proceduraTelematica eq 1)}">
		<c:choose>
			<c:when test="${modo ne 'VISUALIZZA'}">
				var modifica = true;
			</c:when>
			<c:otherwise>
				var modifica = false;
			</c:otherwise>
		</c:choose>
		
		function gestioneCampiAstaEl(critlic,bustalotti){
			if(critlic==1 && bustalotti==1){
				showObj("rowASTA",true);
				showObj("rowTORN_RICASTAE",true);
				var ricastae = getValue("TORN_RICASTAE");
				if(ricastae==null || ricastae==""){
					setValue("TORN_RICASTAE","2"); 
					nascondiVisualizzaCampiAstaEl(false,modifica);
				}else{
					gestioneCampiAstaEl_Ricastae(ricastae)
				}
			}else{
				showObj("rowASTA",false);
				showObj("rowTORN_RICASTAE",false);
				setValue("TORN_RICASTAE","");
				nascondiVisualizzaCampiAstaEl(false,modifica);
			}
		} 	
		
		function nascondiVisualizzaCampiAstaEl(vis,modifica){
			showObj("rowTORN_AEMODVIS",vis);
			showObj("rowTORN_AENOTE",vis);
			showObj("rowTORN_AERIBMIN",vis);
			showObj("rowTORN_AERIBMAX",vis);
			showObj("rowTORN_AEIMPMIN",vis);
			showObj("rowTORN_AEIMPMAX",vis);
			if(modifica){
				setValue("TORN_AEMODVIS","");
				setValue("TORN_AENOTE","");
				setValue("TORN_AERIBMIN","");
				setValue("TORN_AERIBMAX","");
				setValue("TORN_AEIMPMIN","");
				setValue("TORN_AEIMPMAX","");
			}
		}
		
		function gestioneCampiAstaEl_Ricastae(ricastae){
			if(ricastae=='1'){
				nascondiVisualizzaCampiAstaEl(true,modifica);
			}else{
				nascondiVisualizzaCampiAstaEl(false,modifica);
			}
		}
		
		
	</c:if>
	
	function apriArchiviaDocumenti(codgar) {
		bloccaRichiesteServer();
 		<c:choose>
  			<c:when test='${tipologiaGara == "3"}'>
  				var genere=3;
				var entita="GARE";
  			</c:when>
  			<c:otherwise>
  				var genere=1;
				var entita="TORN";
  			</c:otherwise>
  		</c:choose>

		<c:set var="codice" value="${codgar}"/>
		formVisualizzaDocumenti.codice.value = getValue("TORN_CODGAR");
		formVisualizzaDocumenti.codgar.value = codgar;
		formVisualizzaDocumenti.genere.value = genere;
		formVisualizzaDocumenti.entita.value = entita;
		formVisualizzaDocumenti.key1.value = "${codice}";
		formVisualizzaDocumenti.chiaveOriginale.value = getValue("TORN_CODGAR");
		formVisualizzaDocumenti.gartel.value = getValue("TORN_GARTEL");
		formVisualizzaDocumenti.submit();
	}
	
	function apriFascicoloDocumentale() {
		bloccaRichiesteServer();
 		<c:choose>
  			<c:when test='${tipologiaGara == "3"}'>
  				var genere=3;
				var entita="GARE";
			</c:when>
  			<c:otherwise>
  				var genere=1;
				var entita="TORN";
  			</c:otherwise>
  		</c:choose>

		<c:set var="codice" value="${codgar}"/>
		formwsdm.entita.value = entita;
		formwsdm.key1.value = getValue("TORN_CODGAR");
		formwsdm.genereGara.value = genere;
		formwsdm.submit();
	}
	
	<c:if test='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.UFFDET")}'>
		
		
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
	
	<c:if test='${isCodificaAutomatica eq "false" && modo eq "NUOVO"}'>
		$('#TORN_CODGAR').on('keyup', function() {
		    limitText(this, 20)
		});
		
		function limitText(field, maxChar){
		    var ref = $(field),
		        val = ref.val();
		    if ( val.length >= maxChar ){
		        ref.val(function() {
		            console.log(val.substr(0, maxChar))
		            return val.substr(0, maxChar);       
		        });
		    }
		}
	</c:if>
	
	<c:if test='${integrazioneWSERP eq "1"}'>
	 <c:choose>
		<c:when test='${tipoWSERP eq "AVM"}'>
			function leggiCarrelloRda(codgar){
				bloccaRichiesteServer();
				formListaRda.href.value = "gare/commons/lista-rda-scheda.jsp";
				formListaRda.codgar.value = codgar;
				formListaRda.codice.value = codgar;
				formListaRda.genere.value = "2";
				formListaRda.bustalotti.value = "${bustalotti}";
				formListaRda.linkrda.value = "1";
				formListaRda.submit();
			}
		</c:when>
		<c:otherwise>
		</c:otherwise>
	 </c:choose>
	</c:if>	
	
	
	var delElementoSchedaMultiplaOld =  delElementoSchedaMultipla;
	function delElementoSchedaMultiplaCustom(id, label, tipo, campi){
		var name = "#OPES_CATOFF_" + id;
		var type = $(name).attr("type");
		if(type == "hidden" && label == "OPES_DEL_OPES_"){
			if(confirm("Ci sono dei lotti della gara che fanno riferimento alla categoria selezionata. Procedendo, la categoria viene eliminata anche da tali lotti.\nProcedere con l'eliminazione ?")){
				hideElementoSchedaMultipla(id, tipo, campi, false);
				setValue(label + id, "1");
			};
		}else{
			delElementoSchedaMultiplaOld(id, label, tipo, campi);
		}
	}
	delElementoSchedaMultipla = delElementoSchedaMultiplaCustom;
	
	function visualizzaMODMANO(ricmano){
		if(ricmano == 1){
			showObj("rowTORN_MODMANO",true);
			if(getValue("TORN_MODMANO") == null || getValue("TORN_MODMANO")=="")
				setValue("TORN_MODMANO","1");
		}else{
			setValue("TORN_MODMANO","");
			showObj("rowTORN_MODMANO",false);
		}
	}
	
	
	var options;
	function gestioneTabellatoDETLIC(critlicg){
		var detlic = getValue("TORN_DETLIC");
		var form = $("#TORN_DETLIC");
		if(options){
			options.each(function(){
			$(this).removeClass("removed");
			});
			options.appendTo(form);
		}
		if(critlicg == 3){
			$("#TORN_DETLIC").find("option[value='1']").addClass("removed");
			$("#TORN_DETLIC").find("option[value='2']").addClass("removed");
			$("#TORN_DETLIC").find("option[value='3']").addClass("removed");
			$("#TORN_DETLIC").find("option[value='5']").addClass("removed");
			$("#rowTORN_CALCSOAN select").val("2");
			$("#rowTORN_OFFAUM select").val("1");
			showObj("rowGARE_MODASTG", false);
		}else{
			$("#TORN_DETLIC").find("option[value='6']").addClass("removed");
			var tipgen = getValue("TORN_TIPGEN");
			if(tipgen == 1){
				$("#TORN_DETLIC").find("option[value='5']").addClass("removed");
			}else{
				$("#TORN_DETLIC").find("option[value='1']").addClass("removed");
				$("#TORN_DETLIC").find("option[value='2']").addClass("removed");
			}
		}
		options = $("#TORN_DETLIC").children().detach();
		options.not(".removed").appendTo(form);
		$("#rowTORN_DETLIC select").val(detlic);
	}
	
	function apriFormRichiestaCig(){
		bloccaRichiesteServer();
		document.formRichiestaCig.submit();
	}		
	
	<c:if test='${modo eq "MODIFICA"}'>
		var bloccoModificatiDati = $("#bloccoModificatiDati").val();
		bloccaDopoPubblicazione(bloccoModificatiDati,"");
	</c:if>		
	
	function _wait() {
		document.getElementById('bloccaScreen').style.visibility = 'visible';
		$('#bloccaScreen').css("width", $(document).width());
		$('#bloccaScreen').css("height", $(document).height());
		document.getElementById('wait').style.visibility = 'visible';
		$("#wait").offset({ top: 100, left: ($(window).width() / 2) - 200 });
	}

	function _nowait() {
		document.getElementById('bloccaScreen').style.visibility = 'hidden';
		document.getElementById('wait').style.visibility = 'hidden';
	}
	
	function impostaStato(opz,note){
		_wait();
		codgar = $('#TORN_CODGAR').val();
		$.ajax({
			type: "GET",
			dataType: "text",
			async: false,
			beforeSend: function (x) {
				if (x && x.overrideMimeType) {
					x.overrideMimeType("application/text");
				}
			},
			url: contextPath + "/pg/SetSospensioneGara.do",
			data: {
				codgar: codgar,
				opz: opz,
				note: note
			},
			success: function (data) {
				if (data) {
					if(data.esito="true")
						historyReload();
				}
			},
			error: function (e) {
				alert("Errore durante la modifica dello stato del qform");
			},
			complete: function () {
				_nowait();
			}
		});
	  //}
	}

	function setSospensioneGara(opz){
		var titolo="Sospendi gara";
		if(opz==2){
			titolo="Riprendi gara";
		}
		
		
		var opt = {
			open: function(event, ui) { 
				$(this).parent().children().children('.ui-dialog-titlebar-close').hide();
				$(this).parent().css("border-color","#C0C0C0");
				var _divtitlebar = $(this).parent().find("div.ui-dialog-titlebar");
				_divtitlebar.css("border","0px");
				_divtitlebar.css("background","#FFFFFF");
				var _dialog_title = $(this).parent().find("span.ui-dialog-title");
				_dialog_title.css("font-size","13px");
				_dialog_title.css("font-weight","bold");
				_dialog_title.css("color","#002856");
				$(this).parent().find("div.ui-dialog-buttonpane").css("background","#FFFFFF");
			},
			autoOpen: false,
			modal: true,
			width: 550,
			height:250,
			title: titolo,
			position: { my: "center", at: "top+350", of: window },
			buttons: {
				"Conferma": {
					id:"botConferma",
					text:"Conferma",
					click: function() {
						if(opz==1 && $("#Note_Sospensione").val()==""){
							alert("Il campo Motivo della sospensione è obbligatorio");
						}else{
							impostaStato(opz,$("#Note_Sospensione").val())
						}
					}
				 },
				"Annulla": {
					id:"botAnnulla",
					text:"Annulla",
					click:function() {
						$( this ).dialog( "close" );
						$("#trAttivazione").hide();
						$("#trDisattivazione").hide();
					}
				 }
			 }
		};
		$("#mascheraImpostaSospensione").dialog(opt).dialog("open");
		if(opz==1){
			
			var statoErr = 0;
			var stato=getValue("STATOGARA");
			
			if(stato != "In corso"){
				statoErr=1;
			}
			
			$("#spanErrore").hide();
			$("#spanNoSospensione").hide();
			$("#spanConfermaSospensione").hide();
			$("#spanFormSospensione").hide();
			$("#botConferma").hide();

			if(statoErr==0){
				$("#spanConfermaSospensione").show();
				$("#spanFormSospensione").show();
				$("#botConferma").show();
			}else{
				$("#spanErrore").show();
				$("#spanNoSospensione").show();
			}
		
			$("#trSospensione").show();
		}else{

			var stato=getValue("STATOGARA");

			$("#spanConfermaRipresa").show();
			$("#botConferma").show();
		
			$("#trRipresa").show();
		}
		
	}
	function getListaRdaRdi(){
				formListaRdaRdi.submit();
			}
	
</gene:javaScript>

<form name="formListaRdaRdi" action="${pageContext.request.contextPath}/pg/CollegaScollegaRda.do" method="post">
	<input type="hidden" name="handleRda" id="handleRda" value="scollega" />
	<input type="hidden" name="codgar" id="codgar" value="${codgar}" />
	<input type="hidden" name="autorizzatoModifiche" value="${autorizzatoModifiche}" />
	<input type="hidden" name="bloccoModificatiDati" id="bloccoModificatiDati" value="${bloccoModificatiDati}"/>
</form> 

	<form name="formVisualizzaDocumenti" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
		<input type="hidden" name="href" value="gare/commons/archivia-documenti-scheda.jsp" /> 
		<input type="hidden" name="codice" id="codice" value="" />
		<input type="hidden" name="codgar" id="codgar" value="" />
		<input type="hidden" name="genere" id="genere" value="" />
		<input type="hidden" name="entita" value="" />
		<input type="hidden" name="key1" value="" />
		<input type="hidden" name="chiaveOriginale" value="" />
		<input type="hidden" name="gartel" value="" />
		<input type="hidden" name="idconfi" value="${idconfi}" />
	</form> 


	<form name="formwsdm" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
		<input type="hidden" name="href" value="gare/wsdm/wsdm-scheda.jsp" /> 
		<input type="hidden" name="entita" value="" />
		<input type="hidden" name="key1" value="" />
		<input type="hidden" name="key2" value="" /> 
		<input type="hidden" name="key3" value="" />
		<input type="hidden" name="key4" value="" />
		<input type="hidden" name="metodo" value="apri" />
		<input type="hidden" name="activePage" value="0" />
		<input type="hidden" name="genereGara" value="" />
		<input type="hidden" name="idconfi" value="${idconfi}" />
		<input type="hidden" name="autorizzatoModifiche" value="${autorizzatoModifiche }" />
	</form>

	<form name="formListaRda" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
		<input type="hidden" name="href" value="" /> 
		<input type="hidden" name="codgar" id="codgar" value="" />
		<input type="hidden" name="codice" id="codice" value="" />
		<input type="hidden" name="genere" id="genere" value="" />
		<input type="hidden" name="bustalotti" id="bustalotti" value="" />
		<input type="hidden" name="linkrda" id="linkrda" value="" />
	</form> 
	
	<form name="formRichiestaCig" id="formRichiestaCig" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
		<input type="hidden" name="href" value="gare/commons/richiestaCig.jsp" />
		<input type="hidden" name="genereGara" value="${tipologiaGara}" />
		<input type="hidden" name="codiceGara" value="${datiRiga.TORN_CODGAR}" />
	</form>
	
	<div id="mascheraImpostaSospensione" title="Scelta modalit&agrave; inserimento documenti" style="display:none;">
		<table style="font: 11px Verdana, Arial, Helvetica, sans-serif;">
			<tr id="trSospensione" style="display:none;"> 
				<td colspan="2">
					Mediante tale funzione si procede alla sospensione della gara. 
					<br><br>
					<span id="spanErrore"><b>ERRORE:</b></span>
					<span id="spanNoSospensione"><b><br><br> - non &egrave; possibile procedere perch&egrave; la gara non si trova in uno uno stato corretto per la sospensione.</b></span>
					<span id="spanConfermaSospensione">Confermi l'operazione? <br><br></span>
					<span id="spanFormSospensione">
					<form id="richiestainserimentoprotocollo">
						<table class="dettaglio-notab">
						<tr>
							<td class="etichetta-dato">Motivo della sospensione (*) :</td>
							<td class="valore-dato">
								<textarea id="Note_Sospensione" name="Note_Sospensione" title="Note Sospensione" class="testo" rows="4" cols="45"></textarea>
							</td>
						</tr>
						</table>
					</form>
					</span>				
					<br><br></span>
				</td>				
			</tr>
			<tr id="trRipresa" style="display:none;"> 
				<td colspan="2" >
					Mediante tale funzione si procede alla ripresa della gara.
					<br><br>
					<span id="spanConfermaRipresa">Confermi l'operazione?</span>
				</td>				
			</tr>
		</table>
	</div>