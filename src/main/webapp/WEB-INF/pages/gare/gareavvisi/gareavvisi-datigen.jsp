<%
/*
 * Created on: 22/05/2012
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

<fmt:setBundle basename="AliceResources" />
<jsp:include page="/WEB-INF/pages/commons/defCostantiAppalti.jsp" />
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.jstree.nuts.mod.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.jstree.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.jstree.cpvvp.mod.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.jstree.cpvvsupp.mod.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/cpvvp/jquery.cpvvp.mod.css" >

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>

<c:set var="isCodificaAutomatica" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.IsCodificaAutomaticaFunction", pageContext, "GAREAVVISI", "NGARA")}'/>

<c:set var="isSimapAbilitato" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.IsSimapAbilitatoFunction",  pageContext)}' scope="request"/>

<c:set var="filtroLivelloUtente" value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "GAREAVVISI")}' />
<c:set var="isPersonalizzazioneGenovaAttiva" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.IsPersonalizzazioneGenovaAttivaFunction",pageContext)}' scope="request" />

<c:set var="garaAvviso" value="true" scope="request"/>

<c:if test='${modo ne "NUOVO"}'>
	<c:set var="codiceGara" value="${gene:concat('$',gene:getValCampo(key, 'NGARA'))}"/>

	<c:set var="integrazioneWSDM" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSDNFunction", pageContext, codiceGara, idconfi)}' />
	<c:set var="integrazioneWSDMDocumentale" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSDNDocumentaleFunction", pageContext, codiceGara,idconfi)}' />
	<c:if test='${integrazioneWSDM eq "1" or integrazioneWSDMDocumentale eq "1"}'>
		<c:set var="isFascicoloDocumentaleAbilitato" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.IsFascicoloDocumentaleValidoFunction",  pageContext, codiceGara, idconfi)}' />
	</c:if>
</c:if>

<c:set var="exportDocumenti" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.sil.pg.pathArchivioDocumentiGara")}'/>
<c:set var="exportCOS" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "cos.sftp.url")}'/>

<c:set var="log" value="${param.log}"/> 

<%/* Dati generali della gara */%>
<gene:formScheda entita="GAREAVVISI" gestisciProtezioni="true" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreInizializzaAvvisi" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreAvvisi">
	
	<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-scheda.jsp">
			<jsp:param name="entita" value="GAREAVVISI"/>
			<jsp:param name="inputFiltro" value="${requestScope.inputFiltro}"/>			
			<jsp:param name="filtroCampoEntita" value="CODGAR=#CODGAR#"/>
		</jsp:include>
	
	<c:if test='${modo eq "VISUALIZZA"}'>
		<c:set var="IsW_CONFCOMPopolata" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsW_CONFCOMPopolataFunction",pageContext,"11")}' />
	</c:if>
	
	<c:set var="msgChiaveErrore">	
		<fmt:message key="match.tags.template.dettaglio.campoChiave.messaggio">
			<fmt:param value="$"/>
		</fmt:message>
	</c:set>

	<c:set var="msgChiaveErrore" value="${fn:replace(msgChiaveErrore, '\\\\', '')}" />	

	<gene:redefineInsert name="addToAzioni" >
		<c:if test='${gene:checkProtFunz(pageContext, "ALT","GAREAVVISI.Condividi-avviso") && modo eq "VISUALIZZA"}'> 
		<tr>    
			<td class="vocemenulaterale">
					<c:choose>
						<c:when test='${isPersonalizzazioneGenovaAttiva eq "1"}'>
							<a href="javascript:apriGestionePermessi('${datiRiga.GAREAVVISI_CODGAR}',11,${datiRiga.G_PERMESSI_PROPRI eq '1' or abilitazioneGare eq 'A'})" />
						</c:when>
						<c:otherwise>
							<a href="javascript:apriGestionePermessiStandard('${datiRiga.GAREAVVISI_CODGAR}',11,${datiRiga.G_PERMESSI_PROPRI eq '1' or abilitazioneGare eq 'A'})" />
						</c:otherwise>
					</c:choose>
					Condividi e proteggi avviso
					<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>			  
				</td>
			</tr>
		</c:if>
	
		<c:if test='${modo eq "VISUALIZZA" and isSimapAbilitato eq "1" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE.InviaBandoAvviso")}'>
		      <td class="vocemenulaterale">
		     	 <c:if test='${datiRiga.GAREAVVISI_TIPOAVV eq "1"}'>
			      	<c:if test='${isNavigazioneDisattiva ne "1"}'>
							<a href="javascript:popupInviaBandoAvvisoSimap('${datiRiga.GAREAVVISI_CODGAR}','${datiRiga.TORN_ITERGA}');" title="Crea formulari GUUE" tabindex="1503">
					</c:if>
					  Crea formulari GUUE
					<c:if test='${isNavigazioneDisattiva ne "1"}'></a>
					</c:if>		
				</c:if>	  
			 </td>
		</c:if>
		
		<c:if test='${modo eq "VISUALIZZA" and tipoWSDM eq "ENGINEERINGDOC" and autorizzatoModifiche ne "2" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.ModificaUOCompetenza")}'>
			<c:set var="esisteFascicoloAssociato" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteFascicoloAssociatoFunction", pageContext, "GARE", datiRiga.GAREAVVISI_NGARA,idconfi)}' scope="request"/>
			<c:if test="${esisteFascicoloAssociato eq 'true' }">
				<tr>
					<td class="vocemenulaterale" >
						<c:if test='${isNavigazioneDisattiva ne "1"}'>
							<a href="javascript:apriPopupModificaUOCompetenza('${datiRiga.GAREAVVISI_NGARA}','GARE',${idconfi});" title="Modifica U.O. di competenza" >
						</c:if>
							Modifica U.O. di competenza
						<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
					</td>
				</tr>
			</c:if>
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
							<a href="javascript:leggiComunicazioni();" title="${gene:resource('label.tags.template.documenti.comunicazioniRicevute.tooltip')}" tabindex="1515">
						</c:if>
						${gene:resource('label.tags.template.documenti.comunicazioniRicevute')}
						<c:set var="numComunicazioniRicevuteNonLette" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetNumComunicazioniRicevuteDaLeggereFunction", pageContext, gene:getValCampo(key, "NGARA"))}' />
						<c:if test="${numComunicazioniRicevuteNonLette > 0}">(${numComunicazioniRicevuteNonLette} ${gene:resource('label.tags.template.documenti.comunicazioniRicevute.indicatore')})</c:if>
						<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
					</td>
				</tr>
			</c:if>
			<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE.InviaComunicazioni")}'>
				<tr>
					<td class="vocemenulaterale" >
						<c:if test='${isNavigazioneDisattiva ne "1"}'>
							<a href="javascript:inviaComunicazioni();" title="${gene:resource('label.tags.template.documenti.inviaComunicazioni.tooltip')}" tabindex="1516">
						</c:if>
						${gene:resource('label.tags.template.documenti.inviaComunicazioni')}
						<c:set var="numComunicazioniBozza" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetNumComunicazioniBozzaFunction", pageContext, "GARE", gene:getValCampo(key, "NGARA"))}' />
						<c:if test="${numComunicazioniBozza > 0}">(${numComunicazioniBozza} ${gene:resource('label.tags.template.documenti.inviaComunicazioni.indicatore')})</c:if>
						<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
					</td>
				</tr>
				<c:if test="${autorizzatoModifiche ne '2'  and gene:checkProt(pageContext,'FUNZ.VIS.INS.GENEWEB.W_INVCOM-lista.LISTANUOVO')}">
					<tr>
						<td class="vocemenulaterale" >
							<c:if test='${isNavigazioneDisattiva ne "1"}'>
								<a href="javascript:nuovaComunicazione();" title="Nuova comunicazione" tabindex="1504">
							</c:if>
							Nuova comunicazione
							<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
						</td>
					</tr>
				</c:if>
			</c:if>
		</c:if>
		
		
	</gene:redefineInsert>

	<gene:redefineInsert name="addToDocumenti" >
		<c:set var="creaFascicolo" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.GetPropertyWsdmFunction", accessoCreaFascicolo,idconfi)}'/>
		<c:if test='${modo eq "VISUALIZZA"  and autorizzatoModifiche ne "2" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.CreaFascicolo") and integrazioneWSDM eq "1" and creaFascicolo eq "1"}'>
			<c:set var="esisteFascicoloAssociato" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteFascicoloAssociatoFunction", pageContext, "GARE", datiRiga.GAREAVVISI_NGARA,idconfi)}' scope="request"/>
			<c:set var="tipoWSDM" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetTipoWSDMFunction", pageContext, "FASCICOLOPROTOCOLLO", "NO",idconfi)}' />
			<c:if test="${ esisteFascicoloAssociato eq 'true' and tipoWSDM eq 'LAPISOPERA'}">
				<c:set var="esisteDocumentoAssociato" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteDocumentoAssociatoFunction", pageContext, "GARE", datiRiga.GAREAVVISI_NGARA)}' scope="request"/>
			</c:if>
			<c:if test='${(esisteFascicoloAssociato ne "true" and  (tipoWSDM eq "IRIDE" or tipoWSDM eq "JIRIDE" or tipoWSDM eq "ENGINEERING" 
				or tipoWSDM eq "ARCHIFLOW" or tipoWSDM eq "INFOR" or tipoWSDM eq "JPROTOCOL" or tipoWSDM eq "JDOC" or tipoWSDM eq "ENGINEERINGDOC" or tipoWSDM eq "ITALPROT"  or tipoWSDM eq "LAPISOPERA")) || 
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
								<a href="javascript:apriCreaFascicolo('${datiRiga.GAREAVVISI_NGARA}','${datiRiga.GAREAVVISI_CODGAR}','${idconfi}',11,'${fascicoloEsistente}');" title="${titoloMessaggio }" tabindex="1515">
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
							<a href="javascript:apriArchiviaDocumenti('${datiRiga.GARE_CODGAR1}');" title="Archivia documenti" tabindex="1515">
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

	<gene:gruppoCampi idProtezioni="GEN" >
		<gene:campoScheda>
			<td colspan="2"><b>Dati generali</b></td>
		</gene:campoScheda>

		<gene:campoScheda campo="CODGAR" visibile="false" />

<c:choose>
	<c:when test='${isCodificaAutomatica eq "false"}'>
		<gene:campoScheda campo="NGARA" obbligatorio="true" modificabile='${modoAperturaScheda eq "NUOVO"}' >
			<gene:checkCampoScheda funzione='"##".indexOf("$") < 0' obbligatorio="true" messaggio="${msgChiaveErrore}" />
		</gene:campoScheda>
	</c:when>
	<c:otherwise>
		<gene:campoScheda campo="NGARA" modificabile='false' gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoCodificaAutomatica" />
	</c:otherwise>
</c:choose>

		<gene:campoScheda campo="TIPOAVV"  obbligatorio="true"/>
		<gene:campoScheda campo="TIPOAPP"  obbligatorio="true" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoTipoElenco"/>
		<gene:campoScheda campo="OGGETTO"  />
		<gene:campoScheda campo="DATSCA"   />
		<gene:campoScheda campo="ISARCHI"   visibile="${datiRiga.GAREAVVISI_ISARCHI eq '1'}"/>
	</gene:gruppoCampi>
	
	<gene:campoScheda campo="NGARA"  entita="GARE" where="GAREAVVISI.NGARA=GARE.NGARA" visibile="false"/>
	<gene:campoScheda campo="CODGAR1"  entita="GARE" where="GAREAVVISI.NGARA=GARE.NGARA" visibile="false"/>
	<gene:campoScheda campo="GENERE"  entita="GARE" where="GAREAVVISI.NGARA=GARE.NGARA" visibile="false"/>
	<gene:campoScheda campo="CODGAR"  entita="TORN" where="GAREAVVISI.CODGAR=TORN.CODGAR" visibile="false"/>
			
	<c:if test='${modo eq "NUOVO"}'>
		<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.ValorizzaStazioneAppaltanteFunction" parametro=""/>
	</c:if>
	
	<gene:gruppoCampi idProtezioni="RUP" >
		<gene:campoScheda>
			<td colspan="2"><b>Stazione appaltante e RUP</b></td>
		</gene:campoScheda>
		<gene:archivio titolo="Uffici intestatari"
			 lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.CENINT"),"gene/uffint/uffint-lista-popup.jsp","")}'
			 scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaUffint"),"gene/uffint/uffint-scheda.jsp","")}'
			 schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaUffint"),"gene/uffint/uffint-scheda-popup.jsp","")}'
			 campi="UFFINT.CODEIN;UFFINT.NOMEIN;UFFINT.ISCUC"
			 chiave="TORN_CENINT"
			 functionId="skip|abilitazione:1_parentFormName:formGAREAVVISSITORNs"
			 formName="formGAREAVVISSITORN">
				<gene:campoScheda campo="CENINT" entita="TORN" where="GAREAVVISI.CODGAR=TORN.CODGAR" obbligatorio="true" defaultValue="${requestScope.initCENINT}" modificabile="${empty sessionScope.uffint }"/>
				<gene:campoScheda campo="NOMEIN" title="Denominazione" entita="UFFINT" from="TORN" where="GAREAVVISI.CODGAR=TORN.CODGAR and TORN.CENINT=UFFINT.CODEIN" 
					modificabile='${gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.CENINT") && empty sessionScope.uffint}' 
					visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.CENINT")}' defaultValue="${requestScope.initNOMEIN}" />
				<c:choose>
					<c:when test='${modo eq "NUOVO"}'>
					</c:when>
					<c:otherwise>
						
					</c:otherwise>
				</c:choose>
				<gene:campoScheda campo="ISCUC" entita="UFFINT" from ="TORN" where="TORN.CENINT = UFFINT.CODEIN and TORN.CODGAR=GARE.CODGAR1" visibile="false" defaultValue="${requestScope.initISCUC}"/>
		</gene:archivio>
		<gene:campoScheda campo="UFFDET" entita="TORN" where="GAREAVVISI.CODGAR=TORN.CODGAR" />
		<gene:archivio titolo="Tecnici"
			 lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.CODRUP"),"gene/tecni/tecni-lista-popup.jsp","")}'
			 scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda.jsp","")}'
			 schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda-popup.jsp","")}'
			 campi="TECNI.CODTEC;TECNI.NOMTEC"
			 functionId="skip"
			 chiave="TORN_CODRUP"
			 inseribile="true">
				<gene:campoScheda campo="CODRUP" entita="TORN" where="GAREAVVISI.CODGAR=TORN.CODGAR" />
				<gene:campoScheda campo="NOMTEC" title="Nome" entita="TECNI" from="TORN" where="GAREAVVISI.CODGAR=TORN.CODGAR and TORN.CODRUP=TECNI.CODTEC" 
					modificabile='${gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.CODRUP")}' 
					visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.CODRUP")}' />
		</gene:archivio>
	</gene:gruppoCampi>
	
	<c:if test='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.UFFDET")}'>
		<gene:fnJavaScriptScheda funzione='gestioneVisualizzazioneUffedt("#TORN_CENINT#","2")' elencocampi='TORN_CENINT' esegui="false" />
	</c:if>
	
	<gene:campoScheda campo="ALTRISOG" entita="TORN" where="GAREAVVISI.CODGAR = TORN.CODGAR" obbligatorio="true"/>
	
	<c:if test='${modoAperturaScheda ne "NUOVO" and datiRiga.TORN_ALTRISOG eq "2"}' >
		${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GestioneGaraltsogFunction", pageContext, datiRiga.GARE_NGARA)}
	</c:if>
	
	<gene:archivio titolo="Uffici intestatari"
		lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.CENINT"),"gene/uffint/uffint-lista-popup.jsp","")}'
		scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaUffint"),"gene/uffint/uffint-scheda.jsp","")}'
		schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaUffint"),"gene/uffint/uffint-scheda-popup.jsp","")}'
		campi="UFFINT.CODEIN;UFFINT.NOMEIN"
		 chiave="CENINT_GARALTSOG"
		 functionId="nullIscuc|abilitazione:1_parentFormName:formUFFINTGareAltriSogg"
		 formName="formUFFINTGareAltriSogg">
		 <gene:campoScheda campo="CENINT_GARALTSOG" title="Codice stazione appaltante aderente" campoFittizio="true" definizione="T16;0;;;G1CENINTSOG" value="${initCenintGaraltsog }"
		 	modificabile='${gene:checkProt(pageContext, "COLS.MOD.GARE.GARALTSOG.CENINT") }' 
			visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.GARALTSOG.CENINT")}' />
		 <gene:campoScheda campo="NOMEIN_GARALTSOG" title="Denominazione" campoFittizio="true" definizione="T254;0;;NOTE;NOMEIN" value="${initNomeinGaraltsog }"
			modificabile='${gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.CENINT") }' 
			visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.CENINT")}' />
	</gene:archivio>
	
	
	<gene:archivio titolo="Tecnici"
		 lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.CODRUP"),"gene/tecni/tecni-lista-popup.jsp","")}'
		 scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda.jsp","")}'
		 schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda-popup.jsp","")}'
		 campi="TECNI.CODTEC;TECNI.NOMTEC"
		 functionId="skip"
		 chiave="CODRUP_GARALTSOG"
		 inseribile="true">
			<gene:campoScheda campo="CODRUP_GARALTSOG" title="Codice resp.unico procedimento stazione appaltante aderente" campoFittizio="true" definizione="T10;0;;;G1CODRUPSOG" value="${requestScope.initCodrupGaraltsog}"
				modificabile='${gene:checkProt(pageContext, "COLS.MOD.GARE.GARALTSOG.CODRUP") }' 
				visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.GARALTSOG.CODRUP")}' />
			<gene:campoScheda campo="NOMTEC_GARALTSOG" title="Nome" campoFittizio="true" definizione="T161;;;;NOMTEC1" value="${requestScope.initNomtecGaraltsog}"
				modificabile='${gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.CODRUP")}' 
				visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.CODRUP")}'/>
	</gene:archivio>
	
	<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.DettaglioSoggettiAggregati")}' >
		<gene:campoScheda addTr="false">
			<tr id="rowLinkDetSogAgg" style="display: none;">
				<td colspan="2" class="valore-dato" id="colonnaLinkDetSogAgg">
					<c:set var="titoloFunzione" value="Elenco soggetti per cui agisce la centrale di committenza"/>
					<c:set var="titoloFunzioneJs" value="Elenco soggetti per cui agisce la centrale di committenza"/>
					<c:if test="${datiRiga.TORN_ALTRISOG ne 3 }">
						<c:set var="titoloFunzione" value="Elenco soggetti qualificati a ricorrere all'accordo quadro"/>
						<c:set var="titoloFunzioneJs" value="Elenco soggetti qualificati a ricorrere all#accordo quadro"/>
					</c:if>
					<img width="16" height="16" title="Elenco soggetti" alt="Elenco soggetti" src="${pageContext.request.contextPath}/img/soggettiAggregati.png"/>
					<a href="javascript:dettaglioSoggAgg('${titoloFunzioneJs}');" title="${titoloFunzione}" >
						 ${titoloFunzione}
					</a>
			</tr>
		</gene:campoScheda>
	</c:if>	
	
	<gene:fnJavaScriptScheda funzione='aggiornaVisualizzazioneDaArchivioUffint("#UFFINT_ISCUC#")' elencocampi='UFFINT_ISCUC' esegui="true" />
	<gene:fnJavaScriptScheda funzione='sbiancaArchivioUffintAltriSogg("#TORN_CENINT#")' elencocampi='TORN_CENINT' esegui="false" />
	<gene:fnJavaScriptScheda funzione='gestioneVisualizzazioneArchivio("#TORN_ALTRISOG#")' elencocampi='TORN_ALTRISOG' esegui="true" />
	
	
	<gene:gruppoCampi idProtezioni="ALTRIDATI" >
	
		<c:choose>
			<c:when test='${gene:checkProtObj(pageContext,"COLS.MOD","GARE.TORN.CODNUTS") && not empty modo and modo ne "VISUALIZZA" }'>
				<c:set var="modCODNUTS" value="true" scope="request"/>
			</c:when>
			<c:otherwise>
				<c:set var="modCODNUTS" value="false" scope="request"/>
			</c:otherwise>
		</c:choose>
		
		<gene:campoScheda>
			<td colspan="2"><b>Altri dati</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="CODNUTS" entita="TORN" where="GAREAVVISI.CODGAR=TORN.CODGAR" href="#" speciale="true" >
			<gene:popupCampo titolo="Dettaglio codice NUTS" href="#" />
		</gene:campoScheda> 
		<gene:campoScheda campo="ISGREEN" entita="TORN" where="GAREAVVISI.CODGAR=TORN.CODGAR" />
		<gene:campoScheda campo="DESGREEN" entita="TORN" where="GAREAVVISI.CODGAR=TORN.CODGAR" />
		<gene:campoScheda campo="ISRECYCLE" entita="TORN" where="GAREAVVISI.CODGAR=TORN.CODGAR" />
		<gene:campoScheda campo="ISPNRR" entita="TORN" where="GAREAVVISI.CODGAR=TORN.CODGAR" />
	</gene:gruppoCampi>
	
	<jsp:include page="/WEB-INF/pages/gare/garcpv/codiciCPV-gara.jsp">
		<jsp:param name="datiModificabili" value="true"/>
	</jsp:include> 
	
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
	

	<gene:campoScheda>
		<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
	</gene:campoScheda>

	<c:choose>
	<c:when test="${!empty (filtroLivelloUtente)}">
		<gene:campoScheda campo="AUTORI" entita="G_PERMESSI" where="G_PERMESSI.CODGAR = GAREAVVISI.CODGAR AND G_PERMESSI.SYSCON = ${profiloUtente.id}" visibile="false"/>
		<gene:campoScheda campo="PROPRI" entita="G_PERMESSI" where="G_PERMESSI.CODGAR = GAREAVVISI.CODGAR AND G_PERMESSI.SYSCON = ${profiloUtente.id}" visibile="false"/>
	</c:when>
	<c:otherwise>
		<gene:campoScheda campo="AUTORI" entita="G_PERMESSI" campoFittizio="true" value="1" visibile="false"/>
		<gene:campoScheda campo="PROPRI" entita="G_PERMESSI" campoFittizio="true" value="1" visibile="false"/>
	</c:otherwise>
	</c:choose>
	
	<gene:fnJavaScriptScheda funzione='visualizzaDesgreen("#TORN_ISGREEN#")' elencocampi="TORN_ISGREEN" esegui="true" />

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

	function visualizzaDesgreen(visibilità){
	if (visibilità == '1') {
		showObj("rowTORN_DESGREEN",true);
	} else {	
		showObj("rowTORN_DESGREEN",false);
		document.forms[0].TORN_DESGREEN.value = '';
		}
	};

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

	function calcolaTipoEle(valore){
		if(valore==4){
			if(document.getElementById("checkLavori")!=null)
				document.getElementById("checkLavori").checked=false;
			if(document.getElementById("checkForniture")!=null)
				document.getElementById("checkForniture").checked=false;
			if(document.getElementById("checkServizi")!=null)
				document.getElementById("checkServizi").checked=false;
			setValue("GAREAVVISI_TIPOAPP", 4);
		}else{
			var isLavoriChecked = document.getElementById("checkLavori").checked;
			var isFornitureChecked = document.getElementById("checkForniture").checked;
			var isServiziChecked = document.getElementById("checkServizi").checked;
						
			var result = 0;
			if(isLavoriChecked)
				result += 100;
			if(isFornitureChecked)
				result += 10;
			if(isServiziChecked)
				result += 1;
						
			if(result > 0){
				setValue("GAREAVVISI_TIPOAPP", result);
				if(document.getElementById("checkAltro")!=null)
					document.getElementById("checkAltro").checked=false;
			}else
				setValue("GAREAVVISI_TIPOAPP", "");
		
		}
	}
	
	function popupInviaBandoAvvisoSimap(codgar,iterga) {
	   var comando = "href=gare/commons/popup-invia-bando-avviso-simap.jsp";
	   comando = comando + "&codgar=" + codgar + "&iterga=" + iterga  + "&genereGara=11";
	   openPopUpCustom(comando, "inviabandoavvisosimap", 550, 650, "yes", "yes");
	}
	
	function formNUTS(modifica, campo){
		openPopUpCustom("href=gene/tabnuts/dettaglio-codice-nuts.jsp&key=" + document.forms[0].key.value + "&keyParent=" + document.forms[0].keyParent.value + "&modo="+(modifica ? "MODIFICA":"VISUALIZZA")+"&campo="+campo+"&valore="+ getValue(campo), "formNUTS", 700, 300, 1, 1);
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
	
	function inviaComunicazioni() {
		var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=geneweb/w_invcom/w_invcom-lista.jsp&genere=11&entita=GARE&chiave=" + document.forms[0].key.value;
		var chiaveWSDM=getValue("GAREAVVISI_NGARA");
		href+="&entitaWSDM=GARE&chiaveWSDM=" + chiaveWSDM;
		if(idconfi){
			href = href + "&idconfi="+idconfi;
		}
		document.location.href = href;
	}
	
	$(window).ready(function (){
		
		_creaFinestraAlberoNUTS();
		_creaLinkAlberoNUTS($("#TORN_CODNUTS").parent(), "${modo}", $("#TORN_CODNUTS"), $("#TORN_CODNUTSview") );

		$("input[name^='TORN_CODNUTS']").attr('readonly','readonly');
		$("input[name^='TORN_CODNUTS']").attr('tabindex','-1');
		$("input[name^='TORN_CODNUTS']").css('border-width','1px');
		$("input[name^='TORN_CODNUTS']").css('background-color','#E0E0E0');		
		
	});
	
	function nuovaComunicazione() {
		var IsW_CONFCOMPopolata = "${IsW_CONFCOMPopolata}";
		var tipo = 11;
		var numeroGara = getValue("GAREAVVISI_NGARA");
		var keyAdd = "W_INVCOM.COMKEY1=T:" + numeroGara;
		var keyParent = "GARE.NGARA=T:" + numeroGara;
		var entitaWSDM="GARE";
		var chiaveWSDM=getValue("GAREAVVISI_NGARA");
		var href = "";
		if (IsW_CONFCOMPopolata == "true") {
			href = contextPath + "/pg/InitNuovaComunicazione.do?genere=" + tipo + "&keyAdd=" + keyAdd+"&keyParent=" + keyParent + "&chiaveWSDM=" + chiaveWSDM + "&entitaWSDM=" + entitaWSDM;
		} else {
			href = contextPath + "/Lista.do?numModello=0&keyAdd=" + keyAdd ;
			href += "&keyParent=" + keyParent + "&metodo=nuovo&entita=W_INVCOM&jspPathTo=/WEB-INF/geneweb/w_invcom/w_invcom-scheda.jsp&chiaveWSDM=" + chiaveWSDM + "&entitaWSDM=" + entitaWSDM;
		}
		if(idconfi){
			href = href + "&idconfi="+idconfi;
		}
		document.location.href = href + "&" + csrfToken;
	}
	
	function apriArchiviaDocumenti(codgar) {
		bloccaRichiesteServer();
		formVisualizzaDocumenti.codice.value = getValue("GAREAVVISI_NGARA");
		formVisualizzaDocumenti.codgar.value = codgar;
		formVisualizzaDocumenti.genere.value = "11";
		formVisualizzaDocumenti.entita.value = "GARE";
		formVisualizzaDocumenti.key1.value = getValue("GAREAVVISI_NGARA");
		formVisualizzaDocumenti.chiaveOriginale.value = getValue("GAREAVVISI_NGARA");
		formVisualizzaDocumenti.submit();
	}
	
	function leggiComunicazioni() {
		var href = contextPath + "/ApriPagina.do?href=geneweb/w_invcom/w_invcom-in-lista.jsp&genere=11&chiave=" + document.forms[0].key.value;
		var entitaWSDM="GARE";
		var chiaveWSDM=getValue("GAREAVVISI_NGARA");
		href+="&entitaWSDM=" + entitaWSDM + "&chiaveWSDM=" + chiaveWSDM + "&" + csrfToken;
		if(idconfi){
			href = href + "&idconfi="+idconfi;
		}
		document.location.href = href;
	}
	
	function aggiornaVisualizzazioneDaArchivioUffint(iscuc) {
		if (iscuc==1) {
			showObj("rowTORN_ALTRISOG",true);
		} else {
			showObj("rowTORN_ALTRISOG",false);
		}
	}
	
	function sbiancaArchivioUffintAltriSogg() {
		setValue("TORN_ALTRISOG", "");
		setValue("CENINT_GARALTSOG", "");
		setValue("NOMEIN_GARALTSOG", "");
	}
	
	function gestioneVisualizzazioneArchivio(altrisog) {
		if (altrisog=='2') {
			showObj("rowCENINT_GARALTSOG",true);
			showObj("rowNOMEIN_GARALTSOG",true);
			showObj("rowCODRUP_GARALTSOG",true);
			showObj("rowNOMTEC_GARALTSOG",true);
		} else {
			<c:if test="${!garaLottoUnico}">
				showObj("rowRUP", false);
			</c:if>
			showObj("rowCENINT_GARALTSOG",false);
			showObj("rowNOMEIN_GARALTSOG",false);
			showObj("rowCODRUP_GARALTSOG",false);
			showObj("rowNOMTEC_GARALTSOG",false);
			setValue("CENINT_GARALTSOG", "");
			setValue("NOMEIN_GARALTSOG", "");
			setValue("CODRUP_GARALTSOG", "");
			setValue("NOMTEC_GARALTSOG", "");
		}
	}
	
	<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.DettaglioSoggettiAggregati") && modo eq "VISUALIZZA" }' >
		var altrisog = getValue("TORN_ALTRISOG");
		if(altrisog== 3){
			$( '#rowLinkDetSogAgg' ).show( "fast" );
			//Per visualizzare il link allineato al margine sinistro.
			$("#colonnaLinkDetSogAgg").css("padding-left","0px");
		}
		
		function dettaglioSoggAgg(titolo){
			var ngara = getValue("GARE_NGARA");
			var codgar = getValue("GARE_CODGAR1");
			var comando = "href=gare/gare/popup-dettaglio-soggetti-aggregati.jsp";
		   	comando = comando + "&ngara=" + ngara + "&codgar=" + codgar;
		   	titolo = titolo.replace("#","'");
		   	comando += "&titolo=" + titolo;
		   	openPopUpCustom(comando, "dettaglioSoggAgg", 900, 550, "yes", "yes");
		}
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
	<input type="hidden" name="key1" value="${datiRiga.GAREAVVISI_NGARA}" />
	<input type="hidden" name="key2" value="" /> 
	<input type="hidden" name="key3" value="" />
	<input type="hidden" name="key4" value="" />
	<input type="hidden" name="metodo" value="apri" />
	<input type="hidden" name="activePage" value="0" />
	<input type="hidden" name="genereGara" value="11" />
	<input type="hidden" name="idconfi" value="${idconfi}" />
	<input type="hidden" name="autorizzatoModifiche" value="${autorizzatoModifiche }" />
	
</form>