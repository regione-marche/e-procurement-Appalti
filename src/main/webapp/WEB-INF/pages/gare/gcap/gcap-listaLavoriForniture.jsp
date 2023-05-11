<%/*
   * Created on 17-ott-2007
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

<jsp:include page="/WEB-INF/pages/gare/gare/fasiGara/defStepWizardFasiGara.jsp" />

<c:set var="numeroGara" value='${gene:getValCampo(key, "GARE.NGARA")}' />
<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:set var="codiceGara" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetCodiceGaraFunction", pageContext)}' />

	<c:choose>
		<c:when test='${not empty param.stepWizard}'>
			<c:set var="stepWizard" value="${param.stepWizard}" />
		</c:when>
		<c:otherwise>
			<c:set var="stepWizard" value="${stepWizard}" />
		</c:otherwise>
	</c:choose>

	
	<c:choose>
		<c:when test='${not empty param.faseGara}'>
			<c:set var="faseGara" value="${param.faseGara}" />
		</c:when>
		<c:otherwise>
			<c:set var="faseGara" value="${faseGara}" />
		</c:otherwise>
	</c:choose>
	
	
	<c:choose>
		<c:when test='${faseGara >= 7}'>
			<c:set var="bloccoAggiudicazione" value="1" scope ="request" />
		</c:when>
		<c:otherwise>
			<c:set var="bloccoAggiudicazione" value="0" scope = "request"/>
		</c:otherwise>
	</c:choose>

	<c:choose>
		<c:when test='${not empty param.isGaraLottiConOffertaUnica}'>
			<c:set var="isGaraLottiConOffertaUnica" value="${param.isGaraLottiConOffertaUnica}" />
		</c:when>
		<c:otherwise>
			<c:set var="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${isGaraLottiConOffertaUnica eq "true"}'>
			<c:set var="tipologiaGara" value="3" />
		</c:when>
		<c:otherwise>
			<c:set var="tipologiaGara" value="" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${not empty param.isGaraTelematica}'>
			<c:set var="isGaraTelematica" value="${param.isGaraTelematica}" />
		</c:when>
		<c:otherwise>
			<c:set var="isGaraTelematica" value="${isGaraTelematica}" />
		</c:otherwise>
	</c:choose>
	
	
	
	
	<c:choose>
		<c:when test='${not empty param.offtel}'>
			<c:set var="offtel" value="${param.offtel}" />
		</c:when>
		<c:otherwise>
			<c:set var="offtel" value="${offtel}" />
		</c:otherwise>
	</c:choose>
	
		
<c:choose>
	<c:when test='${not empty param.modlicg}'>
		<c:set var="modlicg" value="${param.modlicg}" />
	</c:when>
	<c:otherwise>
		<c:set var="modlicg" value="${modlicg}" />
	</c:otherwise>
</c:choose>
	
	
<c:set var="tipoForniture" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipoFornitureFunction", pageContext, codiceGara)}' />

<c:set var="integrazioneWSERP" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSERPFunction", pageContext)}' />

<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="GCAP-listaLavoriForniture">
	
	<gene:redefineInsert name="head">
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>		
	</gene:redefineInsert>
	
	
	<c:set var="where" value=''/>

	<gene:setString name="titoloMaschera" value='Valutazione prodotti'/>
	
	<% // Ridefinisco il corpo della ricerca %>
	<gene:redefineInsert name="corpo">
	
	<gene:set name="titoloMenu">
		<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
	</gene:set>
	
	<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-pg-lista-scheda.jsp">
		<jsp:param name="entita" value="V_GARE_TORN"/>
		<jsp:param name="inputFiltro" value="CODGAR=T:${codiceGara}"/>
		<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
	</jsp:include>
		
  	<% // Creo la lista per gcap  %>
		<table class="lista">
			
			<tr>
				<td>
  				<gene:formLista entita="GCAP" where='GCAP.NGARA = #GARE.NGARA# and GCAP.DITTAO is null' pagesize="20" tableclass="datilista" sortColumn="4;2" gestisciProtezioni="true">
					<c:set var="tmp" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetDatiArticoloFunction", pageContext, gene:getValCampo(chiaveRigaJava, "NGARA"),gene:getValCampo(chiaveRigaJava, "CONTAF"))}'/>
					<gene:redefineInsert name="listaNuovo" />
					<gene:redefineInsert name="listaEliminaSelezione" />
  					<gene:redefineInsert name="addToAzioni" >
						<c:if test='${ autorizzatoModifiche ne "2" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.AssegnaMigliorOfferente") }'>
							<tr>
								<td class="vocemenulaterale">
									<a href="javascript:assegnaMigliorOfferente('${datiRiga.GCAP_NGARA}');" title="Assegna a miglior offerente su tutte le voci" tabindex="1502">
										Assegna a miglior offerente su tutte le voci
									</a>
								</td>
							</tr>
						</c:if>
  					</gene:redefineInsert>
  					 					
  					<gene:campoLista title="Opzioni" width="50">
						<c:if test="${currentRow >= 0}">
							<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
									<gene:PopUpItemResource resource="popupmenu.tags.lista.visualizza" title="Visualizza articolo"/>
							</gene:PopUp>
						</c:if>
					</gene:campoLista>
					<gene:campoLista campo="CONTAF" visibile="false" />
					
					<c:set var="visualizzaLink" value='${gene:checkProt(pageContext, "MASC.VIS.GARE.GCAP-scheda")}'/>
					<c:set var="link" value="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
					<gene:campoLista campo="NGARA" visibile='${tipologiaGara eq "3"}' title='Codice lotto' headerClass="sortable" width="100"/>
					<gene:campoLista campo="NORVOC" title="N." headerClass="sortable" width="30" visibile="false"/>
					<gene:campoLista campo="CODVOC" title="Voce" headerClass="sortable" width="60" href="${gene:if(visualizzaLink, link, '')}" />
					<gene:campoLista campo="VOCE" headerClass="sortable" />
					<gene:campoLista title="Um" entita="UNIMIS" campo="DESUNI" where="UNIMIS.TIPO=GCAP.UNIMIS AND UNIMIS.CONTA=-1" width="55" headerClass="sortable" visibile="false" />
					<gene:campoLista campo="QUANTI" headerClass="sortable" width="60"/>
					<gene:campoLista campo="PREZUN" headerClass="sortable" width="100"/>
					<gene:campoLista campo="DATACONS" title="Data consegna prevista" headerClass="sortable" />
					<c:set var="datiAggiudicatario" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetAggiudicatarioVoceFunction", pageContext, gene:getValCampo(chiaveRigaJava, "NGARA"),gene:getValCampo(chiaveRigaJava, "CONTAF"))}' />
					<gene:campoLista campo="PREZZO_UN_AGG" title="Prezzo unitario aggiudicazione" campoFittizio="true" definizione="F24.5;0;;MONEY;G1_PREUNC" value="${preOffAggiudicatarioVoce}" />
					<gene:campoLista title="&nbsp;" width="20">
							<c:choose>
								<c:when test='${!empty datiAggiudicatario && requestScope.aggDefaultFlag eq "DEFAGG"}'>
									<img width="16" height="16" title="${datiAggiudicatario}" alt="${datiAggiudicatario}" src="${pageContext.request.contextPath}/img/prodotto_aggiudicato_def.png"/>
								</c:when>
								<c:when test='${!empty datiAggiudicatario && requestScope.aggDefaultFlag eq "NODEFAGG"}'>
									<img width="16" height="16" title="${datiAggiudicatario}" alt="${datiAggiudicatario}" src="${pageContext.request.contextPath}/img/prodotto_aggiudicato_no_def.png"/>
								</c:when>
								<c:when test='${empty datiAggiudicatario && requestScope.aggDefaultFlag eq "NOOFF"}'>
									<img width="16" height="16" title="Nessuna offerta" alt="Nessuna offerta" src="${pageContext.request.contextPath}/img/prodotto_no_offerte.png"/>
								</c:when>
								<c:when test='${isprodneg eq "1"}'>
									<img width="16" height="16" title="Non assegnato" alt="Non assegnato" src="${pageContext.request.contextPath}/img/lavNonAssegnata.png"/>
								</c:when>
								<c:otherwise>
								
								</c:otherwise>
							</c:choose>
					</gene:campoLista>
					
					<gene:campoLista title="" width="20" value="" >
						<a href="javascript:chiaveRiga='${chiaveRigaJava}';dettaglioOfferteRicevuteVoce('${chiaveRigaJava}');" title="offerte ricevute per voce" >
							<img width="16" height="16" title="accedi valutazione offerte" alt="accedi valutazione offerte" src="${pageContext.request.contextPath}/img/attiva_valutazione.png"/>
						</a>
					</gene:campoLista>
						
						<input type="hidden" name="isGaraTelematica" value="${isGaraTelematica}" />
						<input type="hidden" name="faseGara" value="${faseGara}" />
						<input type="hidden" name="offtel" value="${offtel}" />
						<input type="hidden" name="modlicg" value="${modlicg}" />
				</gene:formLista>
				</td>
			</tr>
			<tr>
			<tr>
				<td class="comandi-dettaglio" colSpan="2">
					<c:set var="testoIndietro" value="Torna a elenco concorrenti" />
					<INPUT type="button"  class="bottone-azione" value='${testoIndietro}' title='${testoIndietro }' onclick="javascript:historyVaiIndietroDi(1);">
				</td>
			</tr>
			</tr>
			
		</table>
  </gene:redefineInsert>
  <gene:javaScript>
  	  
  	setContextPath("${pageContext.request.contextPath}");
  
	function annulla(){
		listaAnnullaModifica();
	}

	
	function tornaARiepilogo() {
		historyVaiIndietroDi(1);
	}
	
	function dettaglioOfferteRicevuteVoce(chiaveRiga){
		//alert(chiaveRiga);
		var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=gare/v_gcap_dpre/v_gcap_dpre-lista-offerta.jsp";
		href += "&key="+chiaveRiga;
		href += "&offtel=${offtel }";
		href += "&stepWizard=${paginaAttivaWizard }";
		href += "&faseGara=${faseGara }";
		href += "&isGaraTelematica=${isProceduraTelematica }";
		href += "&modlicg=${modlicg }";
		href += "&riboepvVis=${riboepvVis }";
		href += "&isRicercaMercatoNegoziata=true";
		document.location.href = href;
	}

	function assegnaMigliorOfferente(ngara){
		var comando = "href=gare/commons/popup-assegnaMigliorOfferente.jsp&ngara=" + ngara
	 	openPopUpCustom(comando, "assegnaMigliorOfferente", 500, 350, "yes", "yes");
	}
	
	
	//Associazione del plugin jquery-tooltip allo span con
	//id = INFO_TOOLTIP + (contatore)
	var numeroRighe = ${currentRow};
	for(var t=0; t <= numeroRighe; t++){
		$("#INFO_TOOLTIP" + t).tooltip({ 
		    track: true, 
		    delay: 250, 
		    showURL: false, 
		    opacity: 1, 
		    fixPNG: true, 
		    showBody: " - ", 
		    extraClass: "pretty fancy", 
		    top: 15, 
		    left: 5 
		});
	}
	
	function apriPopupEsportaValutazioneProdotti(ngara){
		var ribcal="${ribcal}";
		var isGaraLottiConOffertaUnica = '${isGaraLottiConOffertaUnica}';
		var act = "${pageContext.request.contextPath}/pg/InitExportValutazioneProdotti.do";
		var par = "ngara=" + ngara + "&isGaraLottiConOffertaUnica=" + isGaraLottiConOffertaUnica + "&ribcal=" + ribcal;
		
		openPopUpActionCustom(act, par, 'ExportExcelOEPV', 500, 300, "no", "no");
	}
	
	</gene:javaScript>
</gene:template>