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

<%--
Viene sbiancata la variabile di sessione keyParentComunicazioni che viene inizializzata nella lista delle comunicazioni.
Se si crea una nuova comunicazione senza passare dalla lista delle comunicazioni la variabile altrimenti rimane valorizzata.
 --%>
<c:set var="keyParentComunicazioni" value="" scope="session"/>
<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>
<fmt:setBundle basename="AliceResources" />
<c:set var="abilitazioneGare" value="${sessionScope.profiloUtente.abilitazioneGare}" />
<c:set var="utente" value="${sessionScope.profiloUtente.id}" />
<c:set var="idStipula" value='${gene:getValCampo(key, "G1STIPULA.ID")}' />
<c:set var="tmp" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetDatiStipulaFunction", pageContext, idStipula)}'/>
<c:set var="filtroLivelloUtente" value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "G1STIPULA")}' />
<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.PulisciSessioneFiltriFunction" parametro=""/>

<c:set var="integrazioneWSERP" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSERPFunction", pageContext)}' />
<c:set var="tipoWSERP" value='${requestScope.tipoWSERP}' />

<c:set var="isCodificaAutomatica" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.IsCodificaAutomaticaFunction", pageContext, "G1STIPULA", "CODSTIPULA")}'/>


<c:choose>
	<c:when test='${   
						integrazioneWSERP eq "1" 
					and tipoWSERP eq "RAI" 
					and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.G1STIPULA.AssegnaNumeroContrattoErp") }'>
		<c:set var="isAbilitataFunzioneErp" value="1" />
	</c:when>
	<c:otherwise>					
		<c:set var="isAbilitataFunzioneErp" value="0" />
	</c:otherwise>				
</c:choose>

<input type="hidden" name="isAbilitataFunzioneErp" id="isAbilitataFunzioneErp" value="${isAbilitataFunzioneErp }" />

<c:set var="codContrattoErp" value='${gene:getValCampo(key, "G1STIPULA.NREPAT")}' />

<jsp:include page="/WEB-INF/pages/commons/defCostantiAppalti.jsp" />

<gene:redefineInsert name="head">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>
</gene:redefineInsert>

<c:choose>
	<c:when test='${not empty param.ngara}'>
		<c:set var="ngara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.ncont}'>
		<c:set var="ncont" value="${param.ncont}" />
	</c:when>
	<c:otherwise>
		<c:set var="ncont" value="${ncont}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.codiga}'>
		<c:set var="codiga" value="${param.codiga}" />
	</c:when>
	<c:otherwise>
		<c:set var="codiga" value="${codiga}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.idpadre}'>
		<c:set var="idpadre" value="${param.idpadre}" />
	</c:when>
	<c:otherwise>
		<c:set var="idpadre" value="${requestScope.idpadre}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.idoriginario}'>
		<c:set var="idoriginario" value="${param.idoriginario}" />
	</c:when>
	<c:otherwise>
		<c:set var="idoriginario" value="${requestScope.idoriginario}" />
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

<c:set var="filtroUffint" value=""/> 
<c:if test="${! empty sessionScope.uffint}">
	<c:set var="filtroUffint" value=" CODEIN = '${sessionScope.uffint}'"/>
</c:if>

<c:set var="idStipula" value='${gene:getValCampo(key, "G1STIPULA.ID")}' />

<c:set var="sql" value="select vernpa from torn where codgar=?"/>
<c:set var="verNPA" value='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, sql, codgar)}'/>

<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.RichiestaCIG") and not (isCigAbilitato eq "1" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.InviaDatiCIG"))}'>
	<c:set var="esisteAnagraficaCollSimog" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsisteAnagraficaCollSimogFunction", pageContext, idStipula)}'/>
</c:if>

<c:set var="exportDocumenti" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.sil.pg.pathArchivioDocumentiGara")}'/>

<%/* Dati generali della gara */%>
<gene:formScheda entita="G1STIPULA" gestisciProtezioni="true" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreInitStipula" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreG1STIPULA">

<%/* Viene riportato tipoGara, in modo tale che, in caso di errorie riapertura della pagina, 
     venga riaperta considerando il valore definito inizialmente per la prima apertura della pagina */%>

<gene:redefineInsert name="schedaNuovo" />
<gene:redefineInsert name="pulsanteNuovo" />
<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-scheda.jsp">
	<jsp:param name="entita" value="V_GARE_STIPULA"/>
	<jsp:param name="inputFiltro" value="${key}"/>
	<jsp:param name="filtroCampoEntita" value="idstipula=${idStipula}"/>
</jsp:include>

<c:if test='${modo ne "NUOVO"}'>
	<c:set var="integrazioneWSDM" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSDNFunction", pageContext, datiRiga.G1STIPULA_CODSTIPULA,idconfi)}' />
	<c:set var="integrazioneWSDMDocumentale" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSDNDocumentaleFunction", pageContext, datiRiga.G1STIPULA_CODSTIPULA,idconfi)}' />
	<c:if test='${integrazioneWSDM eq "1" or integrazioneWSDMDocumentale eq "1"}'>
		<c:set var="tipoWSDM" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetTipoWSDMFunction", pageContext, "FASCICOLOPROTOCOLLO", "NO",idconfi)}' />
		<c:set var="isFascicoloDocumentaleAbilitato" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.IsApplicaFascicolazioneValidaFunction",  pageContext, datiRiga.G1STIPULA_CODSTIPULA,idconfi)}' />
	</c:if>
</c:if>

<c:if test='${		  modo eq "NUOVO"
				 and  integrazioneWSERP eq "1" 
				 and  tipoWSERP eq "RAI" 
				 and  gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.G1STIPULA.AssegnaNumeroContrattoErp") 
             }'>
	<c:set var="esisteGaraNumeroContrattoErp" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteGaraNumeroContrattoErpFunction",  pageContext, ngara, codiga )}' />
</c:if>



<gene:redefineInsert name="addToAzioni" >

	<c:if test='${richiestaApprovazioneFirma ne "1" and modo eq "VISUALIZZA" and (abilitazioneGare eq "A" || utente eq requestScope.creatore || utente eq requestScope.assegnatario) and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.G1STIPULA-scheda.DATIGEN.AttivaContratto")}'>
		<c:if test="${datiRiga.G1STIPULA_STATO ne 5 and datiRiga.G1STIPULA_STATO ne 6}" >
			<tr>
			  <td class="vocemenulaterale" >
					  <a href="javascript:setStatoStipula(1);" title="Attiva contratto" tabindex="1500" >Attiva contratto</a>
			  </td>
		  	</tr>
		</c:if>
	  	<c:if test="${datiRiga.G1STIPULA_STATO eq 5}" >
		  	<tr>
			  <td class="vocemenulaterale" >
					  <a href="javascript:setStatoStipula(2);" title="Disattiva contratto" tabindex="1500" >Disattiva contratto</a>
			  </td>
		  	</tr>
	  	</c:if>
	</c:if>
	<c:if test='${richiestaApprovazioneFirma ne "1" and modo eq "VISUALIZZA" and datiRiga.G1STIPULA_STATO ne 6 and (abilitazioneGare eq "A" || utente eq requestScope.creatore || utente eq requestScope.assegnatario) and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.G1STIPULA-scheda.DATIGEN.AnnullaContratto")}'>
		<tr>
		  <td class="vocemenulaterale" >
				  <a href="javascript:setStatoStipula(3);" title="Annulla contratto" tabindex="1501" >Annulla contratto</a>
		  </td>
	  	</tr>
	</c:if>
	
	<c:if test='${empty verNPA and richiestaApprovazioneFirma ne "1" and modo eq "VISUALIZZA" and !(empty idpadre) and datiRiga.G1STIPULA_TIPOVAR eq 2 and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.RichiestaCIGCollegato")}'>
		<c:choose>
		<c:when test='${isCigAbilitato eq "1" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.InviaDatiCIG")}'>
		</c:when>
		<c:otherwise>
			<c:if test='${isSimogAbilitato eq "1" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.RichiestaCIG")}'>
					<tr>
						<td class="vocemenulaterale" >
							<c:if test='${isNavigazioneDisattiva ne "1"}'>
								<a href="javascript:apriFormRichiestaCig();" title='Richiesta CIG collegato' tabindex="1503">
							</c:if>
								Richiesta CIG collegato
							<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
						</td>
					</tr>
			</c:if>
		</c:otherwise>
		</c:choose>
	</c:if>
	
	
	<c:if test='${richiestaApprovazioneFirma ne "1" and modo eq "VISUALIZZA" and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.G1STIPULA.Condividi-stipula")}'>
		<tr>
			<td class="vocemenulaterale" >
				<a href="javascript:apriGestionePermessiStipula('${datiRiga.G1STIPULA_ID}','${datiRiga.G1STIPULA_CODSTIPULA}',${datiRiga.G_PERMESSI_PROPRI eq '1' or abilitazioneGare eq 'A'});" title="Condividi e proteggi stipula" tabindex="1505">Condividi e proteggi stipula</a>
			</td>
		</tr>
	</c:if>
	
	<c:if test='${modo eq "VISUALIZZA" and integrazioneWSERP eq "1" and tipoWSERP eq "RAI" and requestScope.initCODSTIPULAPADRE eq null and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.G1STIPULA.AssegnaNumeroContrattoErp") }'>
		<tr>
			<td class="vocemenulaterale" >
				<a href="javascript:popUpAssociaContrattoErp();" title="Associa contratto Sap" tabindex="1505">Associa contratto Sap</a>
			</td>
		</tr>
	</c:if>	
	
	<c:if test='${modo eq "VISUALIZZA" and integrazioneWSERP eq "1" and tipoWSERP eq "INARCASSA"}'>
		<tr>
			<td class="vocemenulaterale" >
				<a href="javascript:popUpRecuperaOda();" title="Recupera ODA" tabindex="1505">Recupera ODA</a>
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
						<a href="javascript:leggiComunicazioni();" title="${gene:resource('label.tags.template.documenti.comunicazioniRicevute.tooltip')}" tabindex="1511">
					</c:if>
					${gene:resource('label.tags.template.documenti.comunicazioniRicevute')}
					<c:set var="numComunicazioniRicevuteNonLette" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetNumComunicazioniRicevuteDaLeggereFunction", pageContext, requestScope.codStipula)}' />
					<c:if test="${numComunicazioniRicevuteNonLette > 0}">(${numComunicazioniRicevuteNonLette} ${gene:resource('label.tags.template.documenti.comunicazioniRicevute.indicatore')})</c:if>
					<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
				</td>
			</tr>
		</c:if>
		<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE.InviaComunicazioni")}'>
			<tr>
				<td class="vocemenulaterale" >
					<c:if test='${isNavigazioneDisattiva ne "1"}'>
						<a href="javascript:inviaComunicazioni();" title="${gene:resource('label.tags.template.documenti.inviaComunicazioni.tooltip')}" tabindex="1512">
					</c:if>
					${gene:resource('label.tags.template.documenti.inviaComunicazioni')}
					<c:set var="numComunicazioniBozza" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetNumComunicazioniBozzaFunction", pageContext, "G1STIPULA", requestScope.codStipula)}' />
					<c:if test="${numComunicazioniBozza > 0}">(${numComunicazioniBozza} ${gene:resource('label.tags.template.documenti.inviaComunicazioni.indicatore')})</c:if>
					<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
				</td>
			</tr>
			<c:if test="${autorizzatoModifiche ne '2' and gene:checkProt(pageContext,'FUNZ.VIS.INS.GENEWEB.W_INVCOM-lista.LISTANUOVO')}">
				<tr>
					<td class="vocemenulaterale" >
						<c:if test='${isNavigazioneDisattiva ne "1"}'>
							<a href="javascript:nuovaComunicazione();" title="Nuova comunicazione" tabindex="1513">
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
					<a href="javascript:listaDiscussioni();" title="Discussioni" tabindex="1520">
				</c:if>
				Conversazioni
				<c:set var="resultConteggioDiscussioni" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetDiscussioniNumeroFunction", pageContext, "G1STIPULA",idStipula)}' />
				<c:if test="${numeroDiscussioni > 0}">(${numeroDiscussioni})</c:if>
				<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
			</td>
		</tr>
	</c:if>
</gene:redefineInsert>
<gene:redefineInsert name="addToDocumenti" >	
	<c:set var="creaFascicolo" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.GetPropertyWsdmFunction", accessoCreaFascicolo,idconfi)}'/>
	<c:if test='${modo eq "VISUALIZZA"  and autorizzatoModifiche ne "2" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.CreaFascicolo") and integrazioneWSDM eq "1" and creaFascicolo eq "1" and not gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.obbligoAssociaFascicoloEsistente")}'>
		<c:set var="esisteFascicoloAssociato" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteFascicoloAssociatoFunction", pageContext, "G1STIPULA", idStipula,idconfi)}' scope="request"/>
		
		<c:if test='${esisteFascicoloAssociato ne "true" and  (tipoWSDM eq "IRIDE" or tipoWSDM eq "JIRIDE" )}' >
		
			<tr>
				<c:choose>
					<c:when test='${isNavigazioneDisattiva ne "1"}'>
						<td class="vocemenulaterale">
							<a href="javascript:apriCreaFascicoloStipula('${datiRiga.G1STIPULA_ID}','${datiRiga.G1STIPULA_CODSTIPULA}','${idconfi}', '${datiRiga.G1STIPULA_NGARA}');" title="Crea fascicolo" tabindex="1521">
								Crea fascicolo
							</a>
						</td>
					</c:when>
					<c:otherwise>
						<td>
							Crea fascicolo
						</td>
					</c:otherwise>
				</c:choose>
			</tr>
		</c:if>
	</c:if>
	<c:if test='${modo eq "VISUALIZZA"  and ((integrazioneWSDMDocumentale eq "1" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.TrasferisciAlDocumentale"))
                          or (conservazioneAttiva eq "1" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.TrasferisciCos"))
                          or (conservazioneAttiva eq "2" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.TrasferisciDigiP"))
                          or (!empty exportDocumenti and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.ExportDocumenti")))}'>
			<tr>
				<c:choose>
					<c:when test='${isNavigazioneDisattiva ne "1"}'>
						<td class="vocemenulaterale">
							<a href="javascript:apriArchiviaDocumenti();" title="Archivia documenti" tabindex="1522">
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
							<a href="javascript:document.formwsdm.submit();" title="Fascicolo documentale" tabindex="1523">
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
	
	<c:choose>
		<c:when test="${stato eq 6 }">
			<c:set var="whereV_GARE_STIPULA" value="V_GARE_STIPULA.ID=${idStipula}"/>
		</c:when>
		<c:otherwise>
			<c:set var="whereV_GARE_STIPULA" value="V_GARE_STIPULA.NGARA=G1STIPULA.NGARA AND V_GARE_STIPULA.NCONT=G1STIPULA.NCONT"/>
		</c:otherwise>
	</c:choose>

	<c:set var="msgChiaveErrore">
		<fmt:message key="match.tags.template.dettaglio.campoChiave.messaggio">
			<fmt:param value="$"/>
		</fmt:message>
	</c:set>

	<c:set var="msgChiaveErrore" value="${fn:replace(msgChiaveErrore, '\\\\', '')}" />

	<gene:gruppoCampi idProtezioni="STIPDG">
		<gene:campoScheda >
			<td colspan="2"><b>Dati generali</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="ID" visibile="false" />
		<gene:campoScheda campo="NGARAVAR" visibile="false" />
		<gene:campoScheda campo="CODSTIPULA"  modificabile="${isCodificaAutomatica eq 'false' && modo eq 'NUOVO'}" obbligatorio="${isCodificaAutomatica eq 'false' && modo eq 'NUOVO'}" keyCheck="true">
			<gene:checkCampoScheda funzione='"##".indexOf("$") < 0' obbligatorio="true" messaggio="${msgChiaveErrore}" />
		</gene:campoScheda>
		<gene:campoScheda campo="ID_PADRE" defaultValue="${idpadre}"  visibile="false" />
		<gene:campoScheda title="Codice stipula precedente" campo="CODSTIPULA_FIT" campoFittizio="true" definizione="T20" value="${requestScope.initCODSTIPULAPADRE}" visibile="${!empty datiRiga.G1STIPULA_ID_PADRE}" modificabile="false"/>
		<gene:campoScheda campo="ID_ORIGINARIO" defaultValue="${idoriginario}" visibile="false" />
		<gene:campoScheda campo="LIVELLO" visibile="false" />
		<gene:campoScheda campo="NGARA" defaultValue="${ngara}" modificabile="false" visibile="false"/>
		<gene:campoScheda title="Codice gara" campo="CODICE" entita="V_GARE_STIPULA"  defaultValue="${initCODICE}" modificabile="false" where="${whereV_GARE_STIPULA }"/>	
		<gene:campoScheda campo="NCONT" defaultValue="${ncont}"  visibile="false" />
		<c:choose>
			<c:when test="${!empty (filtroLivelloUtente)}">
				<gene:campoScheda campo="AUTORI" entita="G_PERMESSI" where="G_PERMESSI.IDSTIPULA=G1STIPULA.ID AND G_PERMESSI.SYSCON=${profiloUtente.id}" visibile="false"/>
				<gene:campoScheda campo="PROPRI" entita="G_PERMESSI" where="G_PERMESSI.IDSTIPULA=G1STIPULA.ID AND G_PERMESSI.SYSCON=${profiloUtente.id}" visibile="false"/>
			</c:when>
			<c:otherwise>
				<gene:campoScheda campo="AUTORI" entita="G_PERMESSI" campoFittizio="true" value="1" visibile="false"/>
				<gene:campoScheda campo="PROPRI" entita="G_PERMESSI" campoFittizio="true" value="1" visibile="false"/>
			</c:otherwise>
		</c:choose>
		<gene:campoScheda  campo="NUMLOTTO" title="N.lotto" campoFittizio="true" definizione="T20" value="${codiga}" visibile="${!empty codiga && modcont ne 2}" modificabile="false"/>		
		<gene:campoScheda campo="TIPOVAR" visibile="${!empty datiRiga.G1STIPULA_ID_PADRE}" obbligatorio='true' modificabile="${esisteAnagraficaCollSimog ne 'true'}"/>
		<gene:campoScheda campo="CIGVAR" visibile="${!empty datiRiga.G1STIPULA_ID_PADRE}" modificabile="${esisteAnagraficaCollSimog ne 'true'}"/>	
		<gene:campoScheda campo="STATO_SIMOG" title="Effettuata richiesta CIG?" campoFittizio="true" definizione="T30;" value="${tipoSimogDesc} - ${statoSimog }" visibile="${esisteAnagraficaCollSimog eq 'true'}" modificabile="false"/>
		<c:choose>
			<c:when test='${empty idpadre}'>
				<gene:campoScheda  campo="CARTELLA" />
			</c:when>
			<c:otherwise>
				<gene:campoScheda  campo="CARTELLA" defaultValue="${initCARTELLA}"/>
			</c:otherwise>
		</c:choose>
		<gene:campoScheda entita="GARECONT" campo="NGARA" where="GARECONT.NGARA=G1STIPULA.NGARA AND GARECONT.NCONT=G1STIPULA.NCONT" visibile="false" />
		<gene:campoScheda entita="GARECONT" campo="NCONT" where="GARECONT.NGARA=G1STIPULA.NGARA AND GARECONT.NCONT=G1STIPULA.NCONT" visibile="false" />
		<gene:campoScheda campo="OGGETTO" defaultValue="${initOGGETTO}"/>
		<gene:campoScheda campo="IMPSTIPULA" defaultValue="${initIAGGIU}"/>
		<gene:campoScheda campo="DATPUB" visibile="false" />
	</gene:gruppoCampi>
	<gene:fnJavaScriptScheda funzione='visualizzaCIGVAR("#G1STIPULA_TIPOVAR#")' elencocampi="G1STIPULA_TIPOVAR" esegui="true" />
	
	<gene:gruppoCampi idProtezioni="STIPEC">
	<c:choose>
		<c:when test='${stato eq 6 || not empty idpadre}'>
			<gene:campoScheda >
				<td colspan="2"><b>Estremi atto aggiuntivo /variante</b></td>
			</gene:campoScheda>
			<gene:campoScheda campo="TIATTO" />
			<c:choose>
				<c:when test='${ (    (modo eq "NUOVO") 
				                   or (modo eq "VISUALIZZA")
				                   or (modo eq "MODIFICA") 
				                  )
				                and integrazioneWSERP eq "1" 
				                and tipoWSERP eq "RAI" 
				                and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.G1STIPULA.AssegnaNumeroContrattoErp") }'>				                
					<c:choose>
						<c:when test='${ modo eq "VISUALIZZA" }'>
							<gene:campoScheda campo="NREPAT" 
											  href="javascript:popUpVisualizzaContrattoErp();"
											  modificabile="false" >
								&nbsp;&nbsp;&nbsp;<input type="button" value="Elimina associazione" id="EliminaAssociazioneErp" class="bottone-azione" onclick="javascript:eliminaAssociazioneContrattoErp();" style="display:none;" />
							</gene:campoScheda>	
							<gene:campoScheda campo="DAATTO" 
									          modificabile="false" />	
						</c:when>
						<c:otherwise>
							<gene:campoScheda campo="NREPAT" 
											  href="javascript:popUpVisualizzaContrattoErp();"
											  modificabile='${requestScope.initCODSTIPULAPADRE ne null}'
											  defaultValue="${initNREPAT}"
											  />
							<gene:campoScheda campo="DAATTO" 
									          modificabile="true" />											  
						</c:otherwise>
					</c:choose>			              
				</c:when>			
				<c:otherwise>
					<gene:campoScheda campo="NREPAT" />
					<gene:campoScheda campo="DAATTO" />			
				</c:otherwise>
			</c:choose>				           		
		</c:when>
		<c:otherwise>
			<gene:campoScheda >
					<td colspan="2"><b>Estremi del contratto</b></td>
			</gene:campoScheda>
			<gene:campoScheda entita="GARE" campo="TIATTO" where="GARE.NGARA='${requestScope.codiceLotto}'" />			
			<c:choose>
				<c:when test='${ (modo eq "VISUALIZZA" or modo eq "MODIFICA")
				                and integrazioneWSERP eq "1" 
				                and tipoWSERP eq "RAI" 
				                and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.G1STIPULA.AssegnaNumeroContrattoErp") }'>
					<gene:campoScheda entita="GARE" campo="NREPAT" href="javascript:popUpVisualizzaContrattoErp();" modificabile="false">			   
					    &nbsp;&nbsp;&nbsp;<input type="button" value="Elimina associazione" id="EliminaAssociazioneErp" class="bottone-azione" onclick="javascript:eliminaAssociazioneContrattoErp();" style="display:none;" />			    
					</gene:campoScheda>
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test='${ (   modo eq "NUOVO" )
										 and integrazioneWSERP eq "1" 
										 and tipoWSERP eq "RAI" 
										 and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.G1STIPULA.AssegnaNumeroContrattoErp") }'>
							<gene:campoScheda entita="GARE" campo="NREPAT" defaultValue="${numeroContrattoErp}" modificabile="false" />										 
	                    </c:when>
	                    <c:otherwise>									                				
							<gene:campoScheda entita="GARE" campo="NREPAT" where="GARE.NGARA='${requestScope.codiceLotto}'" />
						</c:otherwise>
					</c:choose>			
				</c:otherwise>
			</c:choose>						
			<gene:campoScheda entita="GARECONT" campo="NUMCONT" where="GARECONT.NGARA=G1STIPULA.NGARA AND GARECONT.NCONT=G1STIPULA.NCONT" />
			<c:choose>
				<c:when test='${ (modo eq "VISUALIZZA" or modo eq "MODIFICA") 
				                and integrazioneWSERP eq "1" 
				                and tipoWSERP eq "RAI" 
				                and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.G1STIPULA.AssegnaNumeroContrattoErp") }'>
				    <c:choose>
					    <c:when test='${ (modo eq "VISUALIZZA") }'>          
							<gene:campoScheda entita="GARE" campo="DAATTO" modificabile="false" />
						</c:when>
					    <c:when test='${ (modo eq "MODIFICA") }'>          
							<gene:campoScheda entita="GARE" campo="DAATTO" modificabile="true" />
						</c:when>
				    </c:choose>										
				</c:when>
				<c:otherwise>				
					<c:choose>
						<c:when test='${   ( modo eq "NUOVO") 
						                and integrazioneWSERP eq "1" 
						                and tipoWSERP eq "RAI" 
						                and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.G1STIPULA.AssegnaNumeroContrattoErp") }'>
							<gene:campoScheda entita="GARE" campo="DAATTO" defaultValue="${dataContrattoErp}" modificabile="true" />
						</c:when>
						<c:otherwise>					
							<gene:campoScheda entita="GARE" campo="DAATTO" where="GARE.NGARA='${requestScope.codiceLotto}'" />
						</c:otherwise>				
					</c:choose>	
				</c:otherwise>
			</c:choose>									
		</c:otherwise>		
	</c:choose>
	</gene:gruppoCampi>
	<gene:gruppoCampi idProtezioni="STIPIC">
		<gene:campoScheda campo="SYSCON"  visibile="false"  defaultValue="${sessionScope.profiloUtente.id}"/>
		<gene:campoScheda campo="ASSEGNATARIO"  visibile="false"  defaultValue="${sessionScope.profiloUtente.id}"/>
		<gene:campoScheda visibile="${modo ne 'NUOVO'}">
			<td colspan="2"><b>Iter del contratto</b></td>
		</gene:campoScheda>
		<c:set var="creatore" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetDescrizioneUtenteStipulaFunction", pageContext, datiRiga.G1STIPULA_SYSCON)}'/>			
		<gene:campoScheda title="Creato da" campo="CREATO_DA" campoFittizio="true" definizione="T80;0;;;G_USYSUTE" value="${creatore}" visibile="${modo ne 'NUOVO'}" modificabile="false"/>
		<c:set var="assegnatario" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetDescrizioneUtenteStipulaFunction", pageContext, datiRiga.G1STIPULA_ASSEGNATARIO)}'/>
		<gene:campoScheda title="Contract Manager" campo="ASSEGNATO_A" campoFittizio="true" definizione="T80;0;;;G_USYSUTE" value="${assegnatario}" visibile="${modo ne 'NUOVO'}" modificabile="false"/>
		<gene:campoScheda campo="STATO" defaultValue="1" modificabile="false" visibile="${modo ne 'NUOVO'}"/>
		<gene:campoScheda campo="MOTNEG" visibile="${stato eq 6 }"/>
		<gene:campoScheda campo="DATNEG" visibile="${stato eq 6 }"/>
		<gene:campoScheda campo="ISARCHI" visibile="${datiRiga.G1STIPULA_ISARCHI eq '1'}" />
	</gene:gruppoCampi>
	<gene:gruppoCampi idProtezioni="STIPDA">
		<gene:campoScheda >
			<td colspan="2"><b>Ditta aggiudicataria</b></td>
		</gene:campoScheda>
		<c:choose>
			<c:when test="${stato eq 6 }">
				<gene:campoScheda title="Codice ditta aggiudicataria" campo="CODIMP_FIT" campoFittizio="true" definizione="T10;0;;;G1VGS_CODIMP" value="${dittaG1aggisto}" modificabile="false"/>
				<gene:campoScheda title="Ragione sociale" campo="NOMEST_FIT" campoFittizio="true" definizione="T2000;0;;;G1VGS_NOMEST" value="${nomestG1aggisto}" modificabile="false" />
				<gene:campoScheda title="Importo di aggiudicazione (del lotto o totale dei lotti aggiudicati)" campo="IAGGIU_FIT" campoFittizio="true" definizione="F15.5;0;;MONEY;G1VGS_IAGGIU" value="${iaggiuG1aggisto}" modificabile="false" />
				<gene:campoScheda title="Data atto aggiudicazione" campo="DATTOA_FIT" campoFittizio="true" definizione="D;0;;DATA_ELDA;DATTOA" value="${dattoaG1aggisto}" modificabile="${dattoaMod }" />
			</c:when>
			<c:otherwise>
				<gene:archivio titolo="ditta" 
					lista="gene/impr/impr-lista-popup.jsp"
					scheda="gene/impr/impr-scheda.jsp"
					schedaPopUp="gene/impr/impr-scheda-popup.jsp"
					campi="IMPR.CODIMP;IMPR.NOMEST"
					functionId="skip"
					chiave="V_GARE_STIPULA_CODIMP"
					inseribile="false">
					<gene:campoScheda entita="V_GARE_STIPULA" campo="CODIMP" defaultValue="${initCODIMP}" modificabile="false" where="V_GARE_STIPULA.NGARA=G1STIPULA.NGARA AND V_GARE_STIPULA.NCONT=G1STIPULA.NCONT"/>
					<gene:campoScheda title="Ragione sociale" entita="IMPR" from="V_GARE_STIPULA" campo="NOMEST" defaultValue="${initNOMIMP}" modificabile="false" where="V_GARE_STIPULA.CODIMP=IMPR.CODIMP AND V_GARE_STIPULA.NGARA=G1STIPULA.NGARA AND V_GARE_STIPULA.NCONT=G1STIPULA.NCONT" />
				</gene:archivio>
				<gene:campoScheda title="Importo di aggiudicazione (del lotto o totale dei lotti aggiudicati)" campo="IAGGIU" entita="V_GARE_STIPULA" defaultValue="${initIAGGIU}" where="V_GARE_STIPULA.NGARA=G1STIPULA.NGARA AND V_GARE_STIPULA.NCONT=G1STIPULA.NCONT" modificabile="false" />
				<gene:campoScheda title="Data atto aggiudicazione" entita="GARE" campo="DATTOA" where="GARE.NGARA='${requestScope.codiceLotto}'" defaultValue="${initDATTOA}" modificabile="${dattoaMod }"/>
			</c:otherwise>
		</c:choose>
		
	</gene:gruppoCampi>
	
	<gene:gruppoCampi idProtezioni="STIPAC">
		<gene:campoScheda >
			<td colspan="2"><b>Amministrazione contraente</b></td>
		</gene:campoScheda>
		<gene:archivio titolo="Uffici intestatari"
			 lista="gene/uffint/uffint-lista-popup.jsp"
			 scheda="gene/uffint/uffint-scheda.jsp"
			 schedaPopUp="gene/uffint/uffint-scheda-popup.jsp"
			 campi="UFFINT.CODEIN;UFFINT.NOMEIN"
			 chiave="V_GARE_STIPULA_CENINT"
			 functionId="skip|abilitazione:1_parentFormName:formUFFINTGare"
			 formName="formUFFINTGare">
				<gene:campoScheda campo="CENINT" entita="V_GARE_STIPULA" where="${whereV_GARE_STIPULA}" obbligatorio="true" defaultValue="${requestScope.initCENINT}" modificabile="false" />
			<c:choose>
				<c:when test='${modo eq "NUOVO"}' >
					<gene:campoScheda campo="NOMEIN" title="Denominazione" modificabile="false" campoFittizio="true" definizione="T254;;;;NOMEIN" value="${requestScope.initNOMEIN}"/>
				</c:when>
				<c:when test='${modo eq "MODIFICA" or modo eq "VISUALIZZA"}'>
					<gene:campoScheda title="Denominazione" campo="NOMEIN" entita="UFFINT" from="V_GARE_STIPULA" where="V_GARE_STIPULA.NGARA=G1STIPULA.NGARA AND UFFINT.CODEIN=V_GARE_STIPULA.CENINT" modificabile='false' />
				</c:when>
			</c:choose>
				
		</gene:archivio>
		<gene:archivio titolo="Tecnici"
			 lista="gene/tecni/tecni-lista-popup.jsp"
			 scheda="gene/tecni/tecni-scheda.jsp"
			 schedaPopUp="gene/tecni/tecni-scheda-popup.jsp"
			 campi="TECNI.CODTEC;TECNI.NOMTEC"
			 functionId="skip"
			 chiave="V_GARE_STIPULA_CODRUP">
				<gene:campoScheda entita="V_GARE_STIPULA" campo="CODRUP" modificabile="false" where="${whereV_GARE_STIPULA }" defaultValue="${requestScope.initCODTECRUP}" />
			<c:choose>
				<c:when test='${modo eq "NUOVO"}' >
					<gene:campoScheda campo="NOMTECRUP" title="Nome" modificabile="false" campoFittizio="true" definizione="T161;;;;NOMTEC1" value="${requestScope.initNOMTECRUP}"/>
				</c:when>
				<c:when test='${modo eq "MODIFICA" or modo eq "VISUALIZZA"}'>
					<gene:campoScheda campo="NOMTECRUP" title="Nome" modificabile="false" campoFittizio="true" definizione="T161;;;;NOMTEC1" value="${requestScope.tecnicorup}"/>
				</c:when>
			</c:choose>
		</gene:archivio>
		<gene:archivio titolo="Tecnici"
			 lista="gene/tecni/tecni-lista-popup.jsp"
			 scheda="gene/tecni/tecni-scheda.jsp"
			 schedaPopUp="gene/tecni/tecni-scheda-popup.jsp"
			 campi="TECNI.CODTEC;TECNI.NOMTEC"
			 functionId="skip"
			 chiave="G1STIPULA_CODDEC"
			 inseribile="true">
			<gene:campoScheda title="Codice responsabile gestione contratto" campo="CODDEC" entita="G1STIPULA" />
			<c:choose>
				<c:when test='${modo eq "NUOVO"}' >
					<gene:campoScheda campo="NOMTECDEC" title="Nome" campoFittizio="true" definizione="T161;;;;NOMTEC1"
					visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.G1STIPULA.CODDEC")}'/>
				</c:when>
				<c:when test='${modo eq "MODIFICA" or modo eq "VISUALIZZA"}'>
					<gene:campoScheda campo="NOMTECDEC" title="Nome" campoFittizio="true" definizione="T161;;;;NOMTEC1"
					 visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.G1STIPULA.CODDEC")}' value="${requestScope.tecnicodec}"/>
				</c:when>
			</c:choose>
				
			
		</gene:archivio>
	</gene:gruppoCampi>
	<gene:gruppoCampi idProtezioni="STIPGZ">
		<gene:campoScheda >
			<td colspan="2"><b>Garanzia</b></td>
		</gene:campoScheda>
		<gene:campoScheda entita="GARE" campo="NGARA" visibile="false" defaultValue="${requestScope.codiceLotto}" where="GARE.NGARA='${requestScope.codiceLotto}'"/>
		<c:choose>
			<c:when test='${stato eq 6 or not empty idpadre}'>
				<gene:campoScheda campo="RIDISO" defaultValue="${initRIDISO}"/>
				<gene:campoScheda campo="IMPGAR" defaultValue="${initIMPGAR}">
					<gene:calcoloCampoScheda funzione='calcolaIMPGARVariante()' elencocampi='G1STIPULA_RIDISO'/>
				</gene:campoScheda>
				<gene:campoScheda campo="NQUIET" defaultValue="${initNQUIET}"/>
				<gene:campoScheda campo="DQUIET" defaultValue="${initDQUIET}"/>
				<gene:campoScheda campo="DSCAPO" defaultValue="${initDSCAPO}"/>
				<gene:campoScheda campo="ISTCRE" defaultValue="${initISTCRE}"/>
				<gene:campoScheda campo="INDIST" defaultValue="${initINDIST}"/>
			</c:when>
			<c:otherwise>
				<gene:campoScheda entita="GARE" campo="RIDISO" defaultValue="${initRIDISO}" where="GARE.NGARA='${requestScope.codiceLotto}'"/>
				<gene:campoScheda entita="GARE" campo="IMPGAR" defaultValue="${initIMPGAR}" where="GARE.NGARA='${requestScope.codiceLotto}'">
					<gene:calcoloCampoScheda funzione='calcolaIMPGAR()' elencocampi='GARE_RIDISO'/>
				</gene:campoScheda>
				<gene:campoScheda entita="GARE" campo="NQUIET" defaultValue="${initNQUIET}" where="GARE.NGARA='${requestScope.codiceLotto}'" />
				<gene:campoScheda entita="GARE" campo="DQUIET" defaultValue="${initDQUIET}" where="GARE.NGARA='${requestScope.codiceLotto}'" />
				
				<gene:campoScheda entita="GARECONT" campo="NGARA" defaultValue="${ngara}" visibile="false" where="GARECONT.NGARA=G1STIPULA.NGARA AND GARECONT.NCONT=G1STIPULA.NCONT" />
				<gene:campoScheda entita="GARECONT" campo="NCONT" defaultValue="${ncont}" visibile="false" where="GARECONT.NGARA=G1STIPULA.NGARA AND GARECONT.NCONT=G1STIPULA.NCONT" />
				<gene:campoScheda entita="GARECONT" campo="DSCAPO" defaultValue="${initDSCAPO}" where="GARECONT.NGARA=G1STIPULA.NGARA AND GARECONT.NCONT=G1STIPULA.NCONT" />
				
				<gene:campoScheda entita="GARE" campo="ISTCRE" defaultValue="${initISTCRE}" where="GARE.NGARA='${requestScope.codiceLotto}'" />
				<gene:campoScheda entita="GARE" campo="INDIST" defaultValue="${initINDIST}" where="GARE.NGARA='${requestScope.codiceLotto}'" />
			</c:otherwise>
		</c:choose>
	</gene:gruppoCampi>
	<c:if test='${integrazioneWSERP eq "1" and tipoWSERP eq "INARCASSA" }'>
		<gene:gruppoCampi idProtezioni="STIODA">
			<gene:campoScheda >
				<td colspan="2"><b>Oda</b></td>
			</gene:campoScheda>
			<gene:campoScheda campo="NUMODA" modificabile = "false" />
		</gene:gruppoCampi>
	</c:if>
	
	<c:if test="${isFascicoloDocumentaleAbilitato eq '1'}">
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
			
			<c:set var="dbms" value="${gene:callFunction('it.eldasoft.gene.tags.utils.functions.GetTipoDBFunction', pageContext)}" />
			<c:choose>
				<c:when test='${dbms eq "ORA"}'>
					<c:set var="intToString" value="TO_CHAR( G1STIPULA.ID )" />
 				</c:when>
				<c:when test='${dbms eq "MSQ"}'>
					<c:set var="intToString" value="CONVERT( varchar,  G1STIPULA.ID )" />
				</c:when>
				<c:when test='${dbms eq "POS"}'>
					<c:set var="intToString" value="cast( G1STIPULA.ID as text)" />
				</c:when>
			</c:choose>
			
			<gene:campoScheda campo="ISPGD_FIT" campoFittizio="true" title="Riferimento al fascicolo" definizione="T40;"  value="${rifFascicolo}" modificabile="false"/>
			<gene:campoScheda campo="ISCHIUSO" entita="WSFASCICOLO" where="WSFASCICOLO.KEY1=${intToString} and WSFASCICOLO.ENTITA='G1STIPULA'" visibile="${datiRiga.WSFASCICOLO_ISCHIUSO eq '1'}" modificabile="false"/>
			<gene:campoScheda campo="CODICE" entita="WSFASCICOLO" where="WSFASCICOLO.KEY1=${intToString} and WSFASCICOLO.ENTITA='G1STIPULA'" visibile="false"/>
			<gene:campoScheda campo="ANNO" entita="WSFASCICOLO" where="WSFASCICOLO.KEY1=${intToString} and WSFASCICOLO.ENTITA='G1STIPULA'" visibile="false"/>
			<gene:campoScheda campo="NUMERO" entita="WSFASCICOLO" where="WSFASCICOLO.KEY1=${intToString} and WSFASCICOLO.ENTITA='G1STIPULA'" visibile="false"/>
			<gene:campoScheda campo="CLASSIFICA" entita="WSFASCICOLO" where="WSFASCICOLO.KEY1=${intToString} and WSFASCICOLO.ENTITA='G1STIPULA'" visibile="false"/>
			
			<gene:campoScheda campo="CODAOO" entita="WSFASCICOLO" where="WSFASCICOLO.KEY1=${intToString} and WSFASCICOLO.ENTITA='G1STIPULA'" visibile="false"/>
			<gene:campoScheda campo="DESAOO" entita="WSFASCICOLO" where="WSFASCICOLO.KEY1=${intToString} and WSFASCICOLO.ENTITA='G1STIPULA'" visibile="false"/>
			<gene:campoScheda campo="CODUFF" entita="WSFASCICOLO" where="WSFASCICOLO.KEY1=${intToString} and WSFASCICOLO.ENTITA='G1STIPULA'" visibile="false"/>
			<gene:campoScheda campo="DESUFF" entita="WSFASCICOLO" where="WSFASCICOLO.KEY1=${intToString} and WSFASCICOLO.ENTITA='G1STIPULA'" visibile="false"/>
			
		</gene:gruppoCampi>
	</c:if>	
	<c:if test="${ modo eq 'VISUALIZZA' or modo eq 'MODIFICA' }">
	<c:set var="configurazioneNotificheComunicazioni" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "comunicazioni.notifiche.idcfg")}'/>
	<c:if test="${!empty configurazioneNotificheComunicazioni}">
		<jsp:include page="/WEB-INF/pages/geneweb/w_invcom/modaleModificaIndirizziNotifiche.jsp">
			<jsp:param value="${ codgar }" name="codiceGara"/>
			<jsp:param value="true" name="isStipula"/>
			<jsp:param value="${idStipula }" name="idStipula"/>
		</jsp:include>
		<gene:gruppoCampi idProtezioni="NOTIFICHECOMUNICAZIONI">
			<gene:campoScheda>
				<td id="placeHolderModaleNotifiche" colspan="2"><b>Notifica comunicazioni ricevute</b></td>
			</gene:campoScheda>
			<gene:campoScheda campo="INDNOTIFICHE" entita="G1STIPULA" modificabile="true" speciale="true">
				<gene:popupCampo titolo="Selezione destinatari" href="apriModaleModificaIndirizziNotifiche();" />
			</gene:campoScheda>
		</gene:gruppoCampi>
	</c:if></c:if>
		
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
						<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:schedaModifica()">
					</c:if>
				</gene:insert>
			</c:otherwise>
			</c:choose>
			&nbsp;
		</td>
	</gene:campoScheda>


<input type="hidden" name="autorizzatoModifiche" value="${autorizzatoModifiche }" />
<input type="hidden" name="modcont" value="${requestScope.modcont}" />



</gene:formScheda>
	<form name="formVisualizzaPermessiUtentiStandard" action="${pageContext.request.contextPath}/pg/VisualizzaPermessiUtentiStipula.do" method="post">
		<input type="hidden" name="metodo" id="metodo" value="apri" />
		<input type="hidden" name="id" id="id" value="" />
		<input type="hidden" name="codstipula" id="codstipula" value="" />
		<input type="hidden" name="permessimodificabili" id="permessimodificabili" value="" />
		<input type="hidden" name="codein" id="codein" value="${sessionScope.uffint}" />
	</form>	
<gene:javaScript>

	function visualizzaCIGVAR(visibilità){
	if (visibilità == '2' || visibilità == '3') {
		showObj("rowG1STIPULA_CIGVAR",true);
		if (visibilità == '2'){
			$("#rowG1STIPULA_CIGVAR .etichetta-dato").text("CIG collegato");
		}
		else{
			$("#rowG1STIPULA_CIGVAR .etichetta-dato").text("CIG nuova procedura avviata");
		}
	} else {	
		showObj("rowG1STIPULA_CIGVAR",false);
		document.forms[0].G1STIPULA_CIGVAR.value = '';
		}
	};

	var ridisoPrec = getValue("GARE_RIDISO");
	var ridisoPrecVariante = getValue("G1STIPULA_RIDISO");
	
	function calcolaIMPGARVariante(){
		var newRidiso = getValue("G1STIPULA_RIDISO");
		var oldRidiso = ridisoPrecVariante;
		var importoCauzione = toVal(getValue("G1STIPULA_IMPGAR"));
		
		if(importoCauzione == null ||importoCauzione=="") importoCauzione=0;
		importoCauzione=parseFloat(importoCauzione);
		
		if (newRidiso == null || newRidiso=="") newRidiso=2;
		if (oldRidiso == null || oldRidiso=="") oldRidiso=2;
		
		if (newRidiso != oldRidiso) {
			if (newRidiso==1) {
				importoCauzione = importoCauzione * (1-0.3);//riduzione del 30%  (prima 50%) - DLgs 36-2023 
			}else{ 
				importoCauzione = importoCauzione / (1-0.3);
			}
		}
		ridisoPrec = newRidiso;
		return toMoney(importoCauzione);
	}
	

		function leggiComunicazioni() {
			var codStipula = getValue("G1STIPULA_CODSTIPULA");
			var chiave = "G1STIPULA.ID=N:${idStipula}";
			var href = contextPath + "/ApriPagina.do?href=geneweb/w_invcom/w_invcom-in-lista.jsp&chiave=" + chiave;
			var idconfi = "${idconfi}"; 
			if(idconfi){
				href = href + "&idconfi="+idconfi;
			}
			document.location.href = href;
		}
	<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.G1STIPULA.InviaComunicazioni")}'>
		function inviaComunicazioni() {
			var entitaWSDM="G1STIPULA";
			var chiaveWSDM=getValue("G1STIPULA_ID");
			var codStipula = getValue("G1STIPULA_CODSTIPULA");
			var chiave = "G1STIPULA.ID=N:${idStipula}";
			var href = contextPath + "/ApriPagina.do?href=geneweb/w_invcom/w_invcom-lista.jsp&entita=" + document.forms[0].entita.value + "&chiave=" + chiave;
			href+="&entitaWSDM=" + entitaWSDM + "&chiaveWSDM=" + chiaveWSDM;
			var idconfi = "${idconfi}"; 
			if(idconfi){
				href = href + "&idconfi="+idconfi;
			}
			document.location.href = href;
		}
	</c:if>
		
	<c:if test="${autorizzatoModifiche ne '2' and gene:checkProt(pageContext,'FUNZ.VIS.INS.GENEWEB.W_INVCOM-lista.LISTANUOVO')}">
		function nuovaComunicazione() {
			var IsW_CONFCOMPopolata = "${IsW_CONFCOMPopolata}";
			var codStipula = getValue("G1STIPULA_CODSTIPULA");
			var keyAdd = "W_INVCOM.COMKEY1=T:" + codStipula;
			var keyParent = "G1STIPULA.ID=N:${idStipula}";
			var ditta="${requestScope.codimp}";
			var entitaWSDM="G1STIPULA";
			var chiaveWSDM=getValue("G1STIPULA_ID");
			var href = "";
			if (IsW_CONFCOMPopolata == "true") {
				href = contextPath + "/pg/InitNuovaComunicazione.do?genere=" + tipo + "&keyAdd=" + keyAdd+"&keyParent=" + keyParent + "&chiaveWSDM=" + chiaveWSDM + "&entitaWSDM=" + entitaWSDM;
			} else {
				href = contextPath + "/Lista.do?numModello=0&keyAdd=" + keyAdd ;
				href += "&keyParent=" + keyParent + "&metodo=nuovo&entita=W_INVCOM&jspPathTo=/WEB-INF/geneweb/w_invcom/w_invcom-scheda.jsp&chiaveWSDM=" + chiaveWSDM + "&entitaWSDM=" + entitaWSDM;
			}
			var idconfi = "${idconfi}";
			if(idconfi){
				href = href + "&idconfi="+idconfi;
			}
			href+="&ditta="+ditta;
			document.location.href = href;
		}
	</c:if>

	<c:if test="${autorizzatoModifiche ne '2' || utente eq requestScope.assegnatario}">
	
		function _wait() {
			document.getElementById('bloccaScreen').style.visibility = 'visible';
			$('#bloccaScreen').css("width", $(document).width());
			$('#bloccaScreen').css("height", $(document).height());
			document.getElementById('wait').style.visibility = 'visible';
			$("#wait").offset({ top: $(window).height() / 2, left: ($(window).width() / 2) - 200 });
		}

		function _nowait() {
			document.getElementById('bloccaScreen').style.visibility = 'hidden';
			document.getElementById('wait').style.visibility = 'hidden';
		}
	
		function impostaStato(opz){
			var motneg = $("#motivoAnnullamento").val();
			if(opz==3){
				if(motneg==null || motneg==""){
					alert("Per procedere si deve valorizzare il campo 'Motivo annullamento'");
					return;
				}
			}
			_wait();
			id = $('#G1STIPULA_ID').val();
			$.ajax({
				type: "GET",
				dataType: "text",
				async: false,
				beforeSend: function (x) {
					if (x && x.overrideMimeType) {
						x.overrideMimeType("application/text");
					}
				},
				url: contextPath + "/pg/SetStatoStipula.do",
				data: {
					id: id,
					opz: opz,
					motneg: motneg
				},
				success: function (data) {
					if (data) {
						if(data.esito="true")
							historyReload();
					}
				},
				error: function (e) {
					alert("Errore durante la modifica dello stato della stipula");
				},
				complete: function () {
					_nowait();
				}
			});
		  //}
		}
	
	
	
		function setStatoStipula(opz){
			var titolo="Attiva contratto";
			if(opz==2){
				titolo="Disattiva contratto";
			}else if(opz==3){
				titolo="Annulla contratto";
			}
			var widthM= 650;
			var	heightM= 250;
			if(opz==3){
				heightM = 400; 
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
				position: { using: function( pos ) {
	                $( this ).css({
						top:'50%',
						left: '50%',
						marginRight: '50%',
						transform: 'translate(-50%, -50%)'
						
					});
	          }},
				autoOpen: false,
				modal: true,
				width: widthM,
				height:heightM,
				title: titolo,
				buttons: {
					"Conferma": {
						id:"botConferma",
						text:"Conferma",
						click: function() {
							impostaStato(opz);
						}
					 },
					"Annulla": {
						id:"botAnnulla",
						text:"Annulla",
						click:function() {
							$( this ).dialog( "close" );
							$("#trAttivazione").hide();
							$("#trDisattivazione").hide();
							$("#trAnnullamento").hide();
						}
					 }
				 }
			};
			$("#mascheraImpostaStato").dialog(opt).dialog("open");
			
			$("#spanErrore").hide();
			$("#spanNoAttivazione").hide();
			$("#spanNoAttivazione1").hide();
			$("#spanWarnAttenzione").hide();
			$("#spanWarnAttivazione").hide();
			$("#spanWarnAttivazione1").hide();
			$("#spanConfermaWarnAttivazione").hide();
			$("#spanConfermaAttivazione").hide();
			$("#botConferma").hide();
			
			$("#spanNoDisattivazione").hide();
			$("#spanConfermaDisattivazione").hide();
			
			$("#spanNoAnnullamento").hide();
			$("#spanNoAnnullamento1").hide();
			$("#spanConfermaAnnullamento").hide();
			$("#spanWarnAnnullamento").hide();
			$("#spanWarnAnnullamento1").hide();
			$("#spanWarnAnnullamento2").hide();
			$("#spanMotivonAnnullamento").hide();
			
			
			
			if(opz==1){
				var oggetto=getValue("G1STIPULA_OGGETTO");
				var idPadre=getValue("G1STIPULA_ID_PADRE");
				var dataStipula = '';
				var tipovar = getValue("G1STIPULA_TIPOVAR");
				var cigvar = getValue("G1STIPULA_CIGVAR");
				var statoErr = 0;
				var statoWarn = 0;							
				
				if(idPadre==null ||idPadre==''){
					
					// var isAbilitataFunzioneErp = $("#isAbilitataFunzioneErp").val(); 
					
					// if (isAbilitataFunzioneErp=="1"){
					//    dataStipula=getValue("G1STIPULA_DAATTO");
					// } else {
					   dataStipula=getValue("GARE_DAATTO");
					// }

				}else{
					dataStipula=getValue("G1STIPULA_DAATTO");					
				}
				var dataPubbl=getValue("G1STIPULA_DATPUB");
          		var controllo = '';
				if(dataStipula==null || dataStipula==''){
					controllo=controllo + 'E';
					statoErr=1;
				}
				if((tipovar == 2) && (cigvar==null || cigvar=='')){
					controllo=controllo + 'D';
					statoErr=1;
				}
				if(statoErr==0){
					if(dataPubbl==null || dataPubbl==''){
						controllo=controllo + 'W';
						statoWarn=1;
					}
					if((tipovar == 3) && (cigvar==null || cigvar=='')){
						controllo= controllo + 'C';
						statoWarn=1;
					}
				}
				
				

				if(statoErr==1){
					$("#spanErrore").show();
					if(controllo.indexOf('E')>=0){
						$("#spanNoAttivazione").show();
					}
					if (controllo.indexOf('D')>=0){
						$("#spanNoAttivazione1").show();
					}
				}
				if(statoWarn==1){
					$("#spanWarnAttenzione").css("color", "blue");
					$("#spanWarnAttenzione").show();
					$("#spanConfermaWarnAttivazione").show();
					$("#botConferma").show();
					if(controllo.indexOf('W')>=0){
						$("#spanWarnAttivazione").css("color", "blue");
						$("#spanWarnAttivazione").show();
					}
					if(controllo.indexOf('C')>=0){
						$("#spanWarnAttivazione1").css("color", "blue");
						$("#spanWarnAttivazione1").show();
					}
				}
				if( statoErr==0 && statoWarn==0){
					$("#spanWarnAttenzione").hide();
					$("#spanConfermaAttivazione").show();
					$("#botConferma").show();
				}
				$("#trAttivazione").show();
			}else if(opz==2){
				var livello=getValue("G1STIPULA_LIVELLO");
				var controllo = 'T';
				if(livello>0){
					controllo='E';
				}
				if(controllo=='E'){
					$("#spanNoDisattivazione").show();
					$("#spanConfermaDisattivazione").hide();
					$("#botConferma").hide();
				}else{
					$("#spanNoDisattivazione").hide();
					$("#spanConfermaDisattivazione").show();
					$("#botConferma").show();
				}
				$("#trDisattivazione").show();
			}else if(opz==3){
				$("#motivoAnnullamento").val('');
				var statoErr = 0;
				var stato = getValue("G1STIPULA_STATO");
				var idPadre = getValue("G1STIPULA_ID_PADRE");
				var livello = getValue("G1STIPULA_LIVELLO");
				if(stato != 5 ){
					statoErr = 1;
				}else if(idPadre != null && idPadre != ""){
					statoErr = 2;
				}
				
				$("#spanNoAnnullamento").hide();
				$("#spanNoAnnullamento1").hide();
				$("#spanConfermaAnnullamento").hide();
				$("#spanWarnAnnullamento").hide();
				$("#spanWarnAnnullamento1").hide();
				$("#spanWarnAnnullamento2").hide();
				$("#botConferma").hide();

				if(statoErr>0){
					if(idPadre != null && idPadre != ""){
						$("#spanNoAnnullamento1").show();
					}else if(stato != 5){
						$("#spanNoAnnullamento").show();
					}
				}else {
					$("#spanConfermaAnnullamento").show();
					$("#spanWarnAnnullamento").show();
					$("#spanWarnAnnullamento1").show();
					$("#spanMotivonAnnullamento").show();
					$("#botConferma").show();
					if(livello >0){
						$("#spanWarnAnnullamento2").show();
					}
				}
				$("#trAnnullamento").show();
			}
		}
				
	</c:if>
		
	function listaDiscussioni(){
		var href = contextPath + "/ApriPagina.do?href=geneweb/w_discuss_p/w_discuss_p-lista.jsp&entita=" + document.forms[0].entita.value + "&chiave=" + document.forms[0].key.value;
		document.location.href = href;
	}
	function apriGestionePermessiStipula(id,codstipula,permessoModifica) {
		bloccaRichiesteServer();
		formVisualizzaPermessiUtentiStandard.id.value = id;
		formVisualizzaPermessiUtentiStandard.codstipula.value = codstipula;
		formVisualizzaPermessiUtentiStandard.permessimodificabili.value = permessoModifica;
		formVisualizzaPermessiUtentiStandard.submit();
	}

	$("#G1STIPULA_CARTELLA").keyup(function () {  
		$(this).val($(this).val().toUpperCase());  
	});

	$("#G1STIPULA_CARTELLA").autocomplete({
		delay: 0,
	    autoFocus: true,
	    position: { 
	    	my : "left top",
	    	at: "left bottom"
	    },
		source: function( request, response ) {
			var folder = $("#G1STIPULA_CARTELLA").val().toUpperCase();
			$.ajax({
				async: false,
			    type: "GET",
                dataType: "json",
                beforeSend: function(x) {
	       			if(x && x.overrideMimeType) {
	           			x.overrideMimeType("application/json;charset=UTF-8");
				       }
	   			},
                url: "${pageContext.request.contextPath}/pg/GetListaCartelleStipula.do",
                data: "folder=" + folder,
				success: function( data ) {
					if (!data) {
						response([]);
					} else {
						response( $.map( data, function( item ) {
							return {
								label: item[0].value,
								value: item[0].value,
							}
						}));
					} 
				},
                error: function(e){
                   alert("Cartella di archiviazione: errore durante la lettura della lista delle cartelle attuali");
                }
			});
		},
		minLength: 1,
		select: function( event, ui ) {
			$("#G1STIPULA_CARTELLA").val(ui.item.value);
		},
		change: function(event, ui) {
			var folder = $("#G1STIPULA_CARTELLA").val();
			$.ajax({
				async: false,
			    type: "GET",
                   dataType: "json",
                   beforeSend: function(x) {
       			if(x && x.overrideMimeType) {
           			x.overrideMimeType("application/json;charset=UTF-8");
			       }
   				},
                url: "${pageContext.request.contextPath}/pg/GetListaCartelleStipula.do",
                data: "folder=" + folder,
				success: function( data ) {
					if (!data) {
						$("#G1STIPULA_CARTELLA").val("");
						$("#G1STIPULA_CARTELLA").html("");
					} 
				},
				error: function(e){
						$("#G1STIPULA_CARTELLA").val("");
						$("#G1STIPULA_CARTELLA").html("");
                }
			});
		}
	});
	
	$(function() {
	    $('#G1STIPULA_CIGVAR').change(function() {
				if (!controllaCIG("G1STIPULA_CIGVAR")) {
					alert("Codice CIG non valido")
					this.focus();
				}
	    });
	});		

	function apriFormRichiestaCig(){
		bloccaRichiesteServer();
		document.formRichiestaCig.submit();
	}	
	
	var schedaConfermaDefault = schedaConferma;
	
	function schedaConfermaCustom() {
		clearMsg();
		setValue("G1STIPULA_CIGVAR", getValue("G1STIPULA_CIGVAR").toUpperCase(), false);
		
		if (!controllaCIG("G1STIPULA_CIGVAR")) {
			outMsg("Codice CIG non valido", "ERR");
			onOffMsg();
			return;
		} 

		schedaConfermaDefault();
	}
	
	var schedaConferma = schedaConfermaCustom;
	
	function apriArchiviaDocumenti(codgar) {
		bloccaRichiesteServer();
		
		formVisualizzaDocumenti.codice.value = getValue("G1STIPULA_CODSTIPULA");
		formVisualizzaDocumenti.entita.value = "G1STIPULA";
		formVisualizzaDocumenti.key1.value = getValue("G1STIPULA_ID");
		formVisualizzaDocumenti.chiaveOriginale.value = getValue("G1STIPULA_CODSTIPULA");
		formVisualizzaDocumenti.gartel.value = "";
		formVisualizzaDocumenti.submit();
	}
	
	function popUpAssociaContrattoErp(){
	   var codStipula = $('#G1STIPULA_CODSTIPULA').val();
	   var codCig =  $('#G1STIPULA_CIGVAR').val();   
	   var idPadre = getValue("G1STIPULA_ID_PADRE");
	   var href = "href=gare/g1stipula/popup-richiesta-numero-contratto-Erp.jsp&modo=NUOVO&idStipula=${idStipula}&codStipula=${codStipula}&idPadre="+idPadre;
	   openPopUpCustom(href, "Associa numero contratto Sap", 800, 450, "yes", "yes");
	}
	
	function popUpRecuperaOda(){
	   var codStipula = $('#G1STIPULA_CODSTIPULA').val();
	   var codCig =  $('#G1STIPULA_CIGVAR').val();   
	   var idPadre = getValue("G1STIPULA_ID_PADRE");
	   var href = "href=gare/g1stipula/g1stipula-popup-recuperaOda.jsp&idStipula=${idStipula}&codStipula=${codStipula}&idPadre="+idPadre;
	   openPopUpCustom(href, "Ricerca ODA", 800, 450, "yes", "yes");
	}
	
	function popUpVisualizzaContrattoErp(){
	   var codContrattoErp = $('#GARE_NREPAT').val();
	   var idPadre=getValue("G1STIPULA_ID_PADRE");

	   if (idPadre){
	      codContrattoErp = $('#G1STIPULA_NREPAT').val(); 
	   }
	   	  
	   var href = "href=gare/g1stipula/popup-richiesta-numero-contratto-Erp.jsp&modo=VISUALIZZA&idStipula=${idStipula}&codContrattoErp="+codContrattoErp;
	   openPopUpCustom(href, "Visualizza numero contratto Sap associato", 800, 450, "yes", "yes");
	}
	
	function eliminaAssociazioneContrattoErp(){

			var conferma = confirm("Sei sicuro di voler eliminare l'associazione creata?");
			if(conferma) {
				var codStipula = $('#G1STIPULA_CODSTIPULA').val();
				var idPadre=getValue("G1STIPULA_ID_PADRE");
				var codContrattoErp = $('#GARE_NREPAT').val();		                	        
				
				$.ajax({
					url: "pg/EliminaAssociazioneContrattoErp.do",
					type: 'POST',
					data: {
							numeroContrattoErp:  codContrattoErp
						   ,idStipula: "${idStipula}"
						   ,codStipula: codStipula
						   ,idPadre : idPadre
						  },
					cache: false,
					dataType: "json",
					success: function(rispostaJson) {				    
						historyReload();				    
					},
					error: function(e) {
						alert("Errore durante l'eliminazione dell'associazione al contratto Erp");
					},
					complete: function() {
						// _nowait();
					}
				});		   
			}
	}
	
	function visualizzaPulsanteEliminaAssociazioneErp() {
	    var codContrattoErp = $('#GARE_NREPAT').val();
        
	    var condizionaPerRai = ${modo eq "VISUALIZZA" && integrazioneWSERP eq "1" && tipoWSERP eq "RAI" && gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.G1STIPULA.AssegnaNumeroContrattoErp") };
	
	    if (condizionaPerRai && codContrattoErp) {
	        $("#EliminaAssociazioneErp").show();
	    } else {
	        // $("#EliminaAssociazioneErp").hide();
	    }
	}
	
	visualizzaPulsanteEliminaAssociazioneErp();	
	
</gene:javaScript>

<c:choose>
	<c:when test='${not empty datiRiga.G1STIPULA_NGARAVAR}'>
		<c:set var="codiceGara" value="$${datiRiga.G1STIPULA_NGARAVAR}" />
	</c:when>
	<c:otherwise>
		<c:set var="codiceGara" value="" />
	</c:otherwise>
</c:choose>

<form name="formRichiestaCig" id="formRichiestaCig" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
	<input type="hidden" name="href" value="gare/commons/richiestaCig.jsp" />
	<input type="hidden" name="genereGara" value="2" />
	<input type="hidden" name="codiceGara" value="${codiceGara}" />
	<input type="hidden" name="idStipula" value="${datiRiga.G1STIPULA_ID}" />
</form>

		
<div id="mascheraImpostaStato" title="Scelta modalit&agrave; inserimento documenti" style="display:none;">
	<table style="font: 11px Verdana, Arial, Helvetica, sans-serif;">
		<tr id="trAttivazione" style="display:none;"> 
			<td colspan="2">
				Mediante tale funzione si procede all'attivazione del contratto. 
				<br><br>
				<span id="spanErrore"><b>ERRORE:</b></span>
				<span id="spanNoAttivazione"><b><br><br> - non &egrave; possibile procedere perch&egrave; non &egrave; stata inserita la data della stipula.</b>
					<br>Valorizzare il campo nella sezione 'Estremi del contratto' della pagina corrente.</span>
				<span id="spanNoAttivazione1"><b><br><br> - non &egrave; possibile procedere perch&egrave; non &egrave; stato valorizzato il CIG collegato.</b></span>
				<span id="spanWarnAttenzione"><b>ATTENZIONE:</b></span>
				<span id="spanWarnAttivazione"><br><br> - non sono ancora stati condivisi i documenti di contratto con l'operatore contraente mediante pubblicazione su portale Appalti.</span>
				<span id="spanWarnAttivazione1"><br><br> - non &egrave; stato valorizzato il CIG di nuova procedura avviata.</span>
				<span id="spanConfermaWarnAttivazione"><br><br>Confermi ugualmente l'operazione?</span>
				<span id="spanConfermaAttivazione">Confermi l'operazione?</span>
				
			</td>				
		</tr>
		<tr id="trDisattivazione" style="display:none;"> 
			<td colspan="2" >
				Mediante tale funzione si procede alla disattivazione del contratto.
				<br><br>
				<span id="spanNoDisattivazione"><b>Non &egrave; possibile procedere perch&egrave; a questo contratto &egrave; collegato un atto aggiuntivo o variante.</b>
				<span id="spanConfermaDisattivazione">Confermi l'operazione?</span>
			</td>				
		</tr>
		<tr id="trAnnullamento" style="display:none;"> 
			<td colspan="2">
				Mediante tale funzione si procede all'annullamento del contratto. 
				<br><br>
				<span id="spanNoAnnullamento"><b>Non &egrave; possibile procedere perch&egrave; il contatto non &egrave; attivo.</b></span>
				<span id="spanNoAnnullamento1"><b>Non &egrave; possibile procedere perch&egrave; la stipula &egrave; atto aggiuntivo o variante di un'altro contratto.</b> <br>Risulta comunque possibile procedere all'annullamento del contratto principale e in cascata di tutte le varianti ad esso collegate.</span>
				<span id="spanWarnAnnullamento"><b>ATTENZIONE:</b></span>
				<span id="spanWarnAnnullamento1"><br><b>il contratto, una volta annullato, non può più essere riattivato.</b> Si dovrà necessariamente procedere con una nuova stipula.</span>
				<span id="spanWarnAnnullamento2"><br><br><b>ATTENZIONE:<br>a questo contratto sono collegati uno o più atti aggiuntivi o variante.</b> Procedendo con l'operazione verranno annullati anche questi contratti derivati.</span>
				<span id="spanConfermaAnnullamento"><br><br>Confermi l'operazione? <br>Indicare il motivo per cui si vuole annullare la stipula.</span>
				<span id="spanMotivonAnnullamento"><br><br>
				<table style="font: 11px Verdana, Arial, Helvetica, sans-serif;">
					<tr>
					<td style="width:30%; text-align:left;">
						Motivo annullamento *:
					</td>
					<td style="width:70%">
						<textarea  id="motivoAnnullamento"  rows="4" cols="60"/>${datiRiga.G1STIPULA.MOTNEG}</textarea><br><br></span>
					</td >
					</tr>
				</table>
				
			</td>				
		</tr>
	</table>
</div>

<c:if test="${! empty sessionScope.uffint}">
	<c:set var="filtroUffint" value=" CODEIN = '${sessionScope.uffint}'"/>
</c:if>

<form name="formwsdm" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
	<input type="hidden" name="href" value="gare/wsdm/wsdm-scheda.jsp" /> 
	<input type="hidden" name="entita" value="G1STIPULA" />
	<input type="hidden" name="key1" value="${datiRiga.G1STIPULA_ID}" />
	<input type="hidden" name="key2" value="" /> 
	<input type="hidden" name="key3" value="" />
	<input type="hidden" name="key4" value="" />
	<input type="hidden" name="metodo" value="apri" />
	<input type="hidden" name="activePage" value="0" />
	<input type="hidden" name="genereGara" value="100" />
	<input type="hidden" name="idconfi" value="${idconfi}" />
	<input type="hidden" name="autorizzatoModifiche" value="${autorizzatoModifiche }" />
	<input type="hidden" name="codstipula" value="${datiRiga.G1STIPULA_CODSTIPULA}" />
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
