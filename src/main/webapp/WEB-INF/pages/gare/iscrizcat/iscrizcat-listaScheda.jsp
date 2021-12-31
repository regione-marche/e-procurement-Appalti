<%/*
   * Created on 13-09-2010
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

<c:set var="codiceGara" value='${gene:getValCampo(key, "DITG.CODGAR5")}' />
<c:set var="numeroGara" value='${gene:getValCampo(key, "DITG.NGARA5")}' />
<c:set var="codiceDitta" value='${gene:getValCampo(key, "DITG.DITTAO")}' />
<c:set var="contextPath" value="${pageContext.request.contextPath}" />


<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.InizializzaUpdateListaFunction" parametro="${key}" />

<c:if test='${updateLista eq 1}' >
	<c:set var="isNavigazioneDisabilitata" value="1" scope="request" />
</c:if>

<c:set var="esistonoGareSenzaCat" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsistonoGareConElencoSenzaCategoriaFunction",pageContext,numeroGara)}'/>

<c:set var="tipoalgo" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTIPOALGOFunction", pageContext, numeroGara)}'/>
<c:set var="tipoclass" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipoClassificaElencoFunction", pageContext, numeroGara)}'/>

<c:set var="criterioRotazioneCategoriaClassi" value="false"/>
<c:if test="${tipoalgo eq '1' or tipoalgo eq '3' or tipoalgo eq '4' or tipoalgo eq '5'}">
	<c:set var="criterioRotazioneCategoriaClassi" value="true"/>
</c:if>

<c:set var="valoreSoglia" value="200"/>



<c:choose>
	<c:when test='${updateLista eq 1}'>
		<% // Visualizza le righe di categoria 0 solo se ci sono delle gare collegate all'elenco senza categoria prevalente%>
		<c:if test="${esistonoGareSenzaCat eq 'TRUE' and criterioRotazioneCategoriaClassi}">
			<c:set var="where" value="(" />
		</c:if>
		<c:choose>
			<c:when test="${!empty param.filtro }">
				<c:set var="valore" value="%${param.filtro }%"/>
				<c:set var="where" value="${where}exists (select CATOFF from OPES,v_cais_tit where NGARA3='${numeroGara}' and V_ISCRIZCAT_TIT.CAISIM = OPES.CATOFF and OPES.CATOFF = v_cais_tit.caisim " />	
				<c:set var="filtroCodiceDescrizione" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.GetUlterioreFiltroCategorieFunction", pageContext, valore)}' />
				<c:set var="where" value="${where} ${filtroCodiceDescrizione } )"/>
				
			</c:when>
			<c:otherwise>
				<c:set var="where" value="${where}exists (select CATOFF from OPES where NGARA3='${numeroGara}' and V_ISCRIZCAT_TIT.CAISIM = OPES.CATOFF)" />
			</c:otherwise>
		</c:choose>
		
		
		
		<c:if test="${esistonoGareSenzaCat eq 'TRUE' and criterioRotazioneCategoriaClassi}">
			<c:set var="where" value="${where} or exists (select CODCAT from ISCRIZCAT where ISCRIZCAT.NGARA='${numeroGara}' and ISCRIZCAT.CODGAR='${codiceGara}' and ISCRIZCAT.CODIMP='${codiceDitta}' and ISCRIZCAT.CODCAT='0' and V_ISCRIZCAT_TIT.CAISIM = ISCRIZCAT.CODCAT and V_ISCRIZCAT_TIT.TIPLAVG = ISCRIZCAT.TIPCAT))" />
		</c:if>
		
		<c:set var="occorrenzeOltreSoglia" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.isNumOccorrenzeCategorieElencoOltreSogliaFunction", pageContext, where,valoreSoglia)}'/>
		
		<c:set var="colspan" value="10"/>
	</c:when>
	<c:otherwise>
		<c:set var="colspan" value="9"/>
		<c:set var="where" value="exists (select CODCAT from ISCRIZCAT where ISCRIZCAT.NGARA='${numeroGara}' and ISCRIZCAT.CODGAR='${codiceGara}' and ISCRIZCAT.CODIMP='${codiceDitta}' and V_ISCRIZCAT_TIT.CAISIM = ISCRIZCAT.CODCAT and V_ISCRIZCAT_TIT.TIPLAVG = ISCRIZCAT.TIPCAT"/>
		<c:if test="${esistonoGareSenzaCat eq 'FALSE' or !criterioRotazioneCategoriaClassi }">
			<c:set var="where" value="${where} and V_ISCRIZCAT_TIT.CAISIM <> '0'"/>
		</c:if>
		<c:set var="where" value="${where})" />
	</c:otherwise>
</c:choose>

<c:set var="nomimo" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetNOMIMOFunction", pageContext, numeroGara, codiceGara, codiceDitta)}' />
<c:set var="esisteClassificaForniture" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.EsisteClassificaCategoriaFunction", pageContext, "TAB1","G_035")}'/>
<c:set var="esisteClassificaServizi" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.EsisteClassificaCategoriaFunction", pageContext, "TAB1","G_036")}'/>
<c:set var="esisteClassificaLavori150" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.EsisteClassificaCategoriaFunction", pageContext, "TAB1","G_037")}'/>
<c:set var="esisteClassificaServiziProfessionali" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.EsisteClassificaCategoriaFunction", pageContext, "TAB1","G_049")}'/>

<c:set var="indiceRiga" value="-1"/>
<c:set var="numCambi" value="0"/>

<c:choose>
	<c:when test='${not empty param.modifica}'>
		<c:set var="modifica" value="${param.modifica}" />
	</c:when>
	<c:otherwise>
		<c:set var="modifica" value="${modifica}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.paginaAttivaWizard}'>
		<c:set var="paginaAttivaWizard" value="${param.paginaAttivaWizard}" />
	</c:when>
	<c:otherwise>
		<c:set var="paginaAttivaWizard" value="${paginaAttivaWizard}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.aggCategorie}'>
		<c:set var="aggCategorie" value="${param.aggCategorie}" />
	</c:when>
	<c:otherwise>
		<c:set var="aggCategorie" value="${aggCategorie}" />
	</c:otherwise>
</c:choose>

<c:set var="numRichiesteAggCategorie" value="0"/>
<c:if test='${aggCategorie eq "1"}'>
	<c:set var="numRichiesteAggCategorie" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetNumAggiornamentiCategorieSospesoFunction", pageContext, codiceDitta, numeroGara )}' />
</c:if>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="ISCRIZCAT-lista">
	<gene:setString name="titoloMaschera" value="Elenco categorie d'iscrizione per l'operatore economico ${nomimo}"/>
	
	<% // Ridefinisco il corpo della ricerca %>
	<gene:redefineInsert name="corpo">
	
		
	<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-pg-lista-scheda.jsp">
		<jsp:param name="entita" value="V_GARE_TORN"/>
		<jsp:param name="inputFiltro" value="CODGAR=T:${codiceGara}"/>
		<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
	</jsp:include>
		
  	<%// Creo la lista per gcap e dpre mediante la vista v_gcap_dpre%>
		<table class="lista">
			<c:if test="${updateLista eq 1}">
				<tr><td>&nbsp;</td></tr>
				<tr><td colspan="2">
					Filtra 
					<input type="text" id="filtroCodiceDesc" name="filtroCodiceDesc" value="${ fn:toLowerCase(param.filtro)}" style="font: 11px Verdana, Arial, Helvetica, sans-serif;"/>
					<INPUT type="button"  value='Applica' title='Applica' onclick='javascript:applicaFiltro();' style="font: 11px Verdana, Arial, Helvetica, sans-serif;"/>
					</td></tr>
				<tr><td>&nbsp;</td></tr>

				<c:if test='${occorrenzeOltreSoglia eq "Si" }'>
					<c:set var="where" value="1=2"/>
					<tr><td colspan="2">
					<span style="color: #ff0028; font-weight: bold;">
						<c:choose>
							<c:when test="${empty param.filtro }">
								Attenzione: le categorie dell'elenco sono più di ${valoreSoglia }.
								E' necessario impostare un filtro per limitare il numero di righe nella lista.
							</c:when>
							<c:otherwise>
								Attenzione: le categorie dell'elenco che soddisfano al criterio di filtro impostato sono più di ${valoreSoglia }.
								E' necessario impostare un filtro più selettivo per limitare il numero di righe nella lista.
							</c:otherwise>
						</c:choose>
						
					</span><br><br>
					</td></tr>
				</c:if>
			
			</c:if>
					
			<tr>
				<td ${stileDati}>
  				<gene:formLista entita="V_ISCRIZCAT_TIT" where='${where}' pagesize="${valoreSoglia }" tableclass="datilista" sortColumn="4" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreISCRIZCAT">
 					<gene:redefineInsert name="listaNuovo" />
					<gene:redefineInsert name="listaEliminaSelezione" />
					<gene:redefineInsert name="modelliPredisposti" />
					<gene:redefineInsert name="documentiAssociati" />
					<gene:redefineInsert name="noteAvvisi" />
					<gene:redefineInsert name="addToAzioni" >
						<c:choose>
							<c:when test='${updateLista eq 1}'>
								<c:if test='${occorrenzeOltreSoglia ne "Si" || !empty param.filtro}'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:salva();" title="Salva modifiche" tabindex="1500">
												${gene:resource("label.tags.template.dettaglio.schedaConferma")}
											</a>
										</td>
									</tr>
								</c:if>
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:listaAnnullaModifica();" title="Annulla modifiche" tabindex="1501">
											${gene:resource("label.tags.template.dettaglio.schedaAnnulla")}
										</a>
									</td>
								</tr>
							</c:when>
							<c:otherwise>
								<c:if test="${numRichiesteAggCategorie ne '0' and autorizzatoModifiche ne 2 and gene:checkProt(pageContext,'FUNZ.VIS.ALT.GARE.AcquisiciAggiornamentoCategorie')}">
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:aquisisciAgg();" title='Acquisisci modifiche da portale' tabindex="1502">
												Acquisisci modifiche da portale
											</a>
										</td>
									</tr>
								</c:if>
								<c:if test="${tipoalgo eq '4' or tipoalgo eq '5' or tipoalgo eq '12' or tipoalgo eq '15'}">
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:dettaglioClassi();" title='Visualizza dettaglio classifiche' tabindex="1502">
												Visualizza dettaglio classifiche
											</a>
										</td>
									</tr>
								</c:if>
								<c:if test='${gene:checkProtFunz(pageContext, "INS","INS") and paginaAttivaWizard=="2" and autorizzatoModifiche ne 2}'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:listaApriInModifica();" title='Modifica attribuzione' tabindex="1503">
												Modifica attribuzione
											</a>
										</td>
									</tr>
								</c:if>
							</c:otherwise>
						</c:choose>
					</gene:redefineInsert>
											
					<c:set var="oldTiplavg" value="${newTiplavg}"/>
					<%/* Nel caso di categorie 0, non fa variare il titolo al cambio della Tipologia e non ne riporta il valore nel titolo */%>
					<c:choose>
						<c:when test="${datiRiga.V_ISCRIZCAT_TIT_CAISIM eq '0'}">
							<c:set var="newTiplavg" value=""/>
						</c:when>
						<c:otherwise>
							<c:set var="newTiplavg" value="${datiRiga.V_ISCRIZCAT_TIT_TIPLAVG }"/>
						</c:otherwise>
					</c:choose>
					
					<c:set var="oldTitolo" value="${newTitolo}"/>
					<c:set var="newTitolo" value="${datiRiga.V_ISCRIZCAT_TIT_TITCAT }"/>
					
					<gene:campoLista campoFittizio="true" visibile="false">
						<%/* Nel caso in cui siano diversi inframezzo il titolo */%>
						<c:if test="${oldTitolo != newTitolo || newTiplavg != oldTiplavg}">
							<td colspan="${colspan }">
								<b>${datiRiga.TAB1_TAB1DESC }</b> <c:if test="${not empty datiRiga.TAB1_TAB1DESC and not empty datiRiga.TAB5_TAB5DESC}"> - </c:if> <b>${datiRiga.TAB5_TAB5DESC }</b>
							</td>
						</tr>
											
						<tr class="odd">
						<c:set var="numCambi" value="${numCambi + 1}"/>
						</c:if>
					</gene:campoLista>
					
					<c:choose>
						<c:when test='${(updateLista eq 1)}' >
							<gene:set name="titoloMenu">
								<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
							</gene:set>
							<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50" >
								<c:if test='${currentRow >= 0 }'>
									<input type="checkbox" name="keys" value="${datiRiga.V_ISCRIZCAT_TIT_CAISIM}"  <c:if test="${datiRiga.V_ISCRIZCAT_TIT_CAISIM eq datiRiga.ISCRIZCAT_CODCAT}">checked="checked"</c:if>  <c:if test="${datiRiga.ISCRIZCAT_CODCAT eq '0'}">disabled="disabled"</c:if> onclick="javascript:aggiornaRiga(this,${currentRow + 1});"/>
								</c:if>
							</gene:campoLista>
						</c:when>
						<c:otherwise>
									<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" visibile="false" />
						</c:otherwise>
					</c:choose>
					
					
					<gene:campoLista title="" width="22" >
						<c:choose>
							<c:when test="${datiRiga.V_ISCRIZCAT_TIT_NUMLIV > '1'}">
								<img width="22" height="16" title="Categoria di livello ${datiRiga.V_ISCRIZCAT_TIT_NUMLIV}" alt="Categoria di livello ${datiRiga.V_ISCRIZCAT_TIT_NUMLIV}" src="${pageContext.request.contextPath}/img/livelloCategoria${datiRiga.V_ISCRIZCAT_TIT_NUMLIV}.gif"/>
							</c:when>
							<c:otherwise>
								&nbsp;
							</c:otherwise>
						</c:choose>
					</gene:campoLista>
					<gene:campoLista campo="NUMORD"  visibile="false"/>
					<gene:campoLista campo="TIPLAVG" edit="${updateLista eq 1}"  visibile = "false"/>	
					<c:choose>
						<c:when test='${updateLista eq 1}'>
							<gene:campoLista campo="CAISIM"  ordinabile="false"/>
							<gene:campoLista campo="CAISIM"  visibile = "false" edit="true" ordinabile="false"/>
							<gene:campoLista campo="DESCAT" title="Descrizione" ordinabile="false" />
							<gene:campoLista campo="INFNUMCLASS"  edit="true" title="Da classifica" entita = "ISCRIZCAT" where = "ISCRIZCAT.NGARA='${numeroGara}' and ISCRIZCAT.CODGAR='${codiceGara}' and ISCRIZCAT.CODIMP='${codiceDitta}' and V_ISCRIZCAT_TIT.CAISIM = ISCRIZCAT.CODCAT and V_ISCRIZCAT_TIT.TIPLAVG = ISCRIZCAT.TIPCAT" ordinabile="false" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoClassificaCategoria" visibile="${ tipoclass eq 1}"/>
							<gene:campoLista campo="SUPNUMCLASS"  edit="true" title='${gene:if(tipoclass eq 1 ,"A classifica","Classifica")}' entita = "ISCRIZCAT" where = "ISCRIZCAT.NGARA='${numeroGara}' and ISCRIZCAT.CODGAR='${codiceGara}' and ISCRIZCAT.CODIMP='${codiceDitta}' and V_ISCRIZCAT_TIT.CAISIM = ISCRIZCAT.CODCAT and V_ISCRIZCAT_TIT.TIPLAVG = ISCRIZCAT.TIPCAT" ordinabile="false" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoClassificaCategoria" visibile="${ tipoclass eq 1 or tipoclass eq 2}"/>
							<gene:campoLista campo="CODCAT"  entita = "ISCRIZCAT" where = "ISCRIZCAT.NGARA='${numeroGara}' and ISCRIZCAT.CODGAR='${codiceGara}' and ISCRIZCAT.CODIMP='${codiceDitta}' and V_ISCRIZCAT_TIT.CAISIM = ISCRIZCAT.CODCAT and V_ISCRIZCAT_TIT.TIPLAVG = ISCRIZCAT.TIPCAT" visibile="false" edit="true"/>
							<gene:campoLista campo="NGARA"  visibile="false" entita = "ISCRIZCAT" where = "ISCRIZCAT.NGARA='${numeroGara}' and ISCRIZCAT.CODGAR='${codiceGara}' and ISCRIZCAT.CODIMP='${codiceDitta}' and V_ISCRIZCAT_TIT.CAISIM = ISCRIZCAT.CODCAT and V_ISCRIZCAT_TIT.TIPLAVG = ISCRIZCAT.TIPCAT" edit="true"/>
							<gene:campoLista campo="CODIMP" visibile="false" entita = "ISCRIZCAT" where = "ISCRIZCAT.NGARA='${numeroGara}' and ISCRIZCAT.CODGAR='${codiceGara}' and ISCRIZCAT.CODIMP='${codiceDitta}' and V_ISCRIZCAT_TIT.CAISIM = ISCRIZCAT.CODCAT and V_ISCRIZCAT_TIT.TIPLAVG = ISCRIZCAT.TIPCAT" edit="true"/>
						</c:when>
						<c:otherwise>
							<gene:campoLista campo="CAISIM"  visibile = "false"/>
							<gene:campoLista campo="CODGAR" visibile="false" entita = "ISCRIZCAT" where = "ISCRIZCAT.NGARA='${numeroGara}' and ISCRIZCAT.CODGAR='${codiceGara}' and ISCRIZCAT.CODIMP='${codiceDitta}' and V_ISCRIZCAT_TIT.CAISIM = ISCRIZCAT.CODCAT and V_ISCRIZCAT_TIT.TIPLAVG = ISCRIZCAT.TIPCAT"/>
							<gene:campoLista campo="NGARA"  visibile="false" entita = "ISCRIZCAT" where = "ISCRIZCAT.NGARA='${numeroGara}' and ISCRIZCAT.CODGAR='${codiceGara}' and ISCRIZCAT.CODIMP='${codiceDitta}' and V_ISCRIZCAT_TIT.CAISIM = ISCRIZCAT.CODCAT and V_ISCRIZCAT_TIT.TIPLAVG = ISCRIZCAT.TIPCAT"/>
							<gene:campoLista campo="CODIMP" visibile="false" entita = "ISCRIZCAT" where = "ISCRIZCAT.NGARA='${numeroGara}' and ISCRIZCAT.CODGAR='${codiceGara}' and ISCRIZCAT.CODIMP='${codiceDitta}' and V_ISCRIZCAT_TIT.CAISIM = ISCRIZCAT.CODCAT and V_ISCRIZCAT_TIT.TIPLAVG = ISCRIZCAT.TIPCAT"/>
							<gene:campoLista campo="CODCAT" entita = "ISCRIZCAT" where = "ISCRIZCAT.NGARA='${numeroGara}' and ISCRIZCAT.CODGAR='${codiceGara}' and ISCRIZCAT.CODIMP='${codiceDitta}' and V_ISCRIZCAT_TIT.CAISIM = ISCRIZCAT.CODCAT and V_ISCRIZCAT_TIT.TIPLAVG = ISCRIZCAT.TIPCAT" ordinabile="false"/>
							<gene:campoLista campo="DESCAT" title="Descrizione" ordinabile="false" />
							<gene:campoLista campo="INFNUMCLASS" title="Da classifica" entita = "ISCRIZCAT" where = "ISCRIZCAT.NGARA='${numeroGara}' and ISCRIZCAT.CODGAR='${codiceGara}' and ISCRIZCAT.CODIMP='${codiceDitta}' and V_ISCRIZCAT_TIT.CAISIM = ISCRIZCAT.CODCAT and V_ISCRIZCAT_TIT.TIPLAVG = ISCRIZCAT.TIPCAT" ordinabile="false" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoClassificaCategoria" visibile="${ tipoclass eq 1}"/>
							<gene:campoLista campo="SUPNUMCLASS" title='${gene:if(tipoclass eq 1,"A classifica","Classifica")}' entita = "ISCRIZCAT" where = "ISCRIZCAT.NGARA='${numeroGara}' and ISCRIZCAT.CODGAR='${codiceGara}' and ISCRIZCAT.CODIMP='${codiceDitta}' and V_ISCRIZCAT_TIT.CAISIM = ISCRIZCAT.CODCAT and V_ISCRIZCAT_TIT.TIPLAVG = ISCRIZCAT.TIPCAT" ordinabile="false" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoClassificaCategoria" visibile="${ tipoclass eq 1 or tipoclass eq 2}"/>
														
						</c:otherwise>
					</c:choose>
					
					<gene:campoLista campo="INVREA" width="80" visibile="${criterioRotazioneCategoriaClassi or tipoalgo eq '14' or tipoalgo eq '15'}" entita = "ISCRIZCAT" where = "ISCRIZCAT.NGARA='${numeroGara}' and ISCRIZCAT.CODGAR='${codiceGara}' and ISCRIZCAT.CODIMP='${codiceDitta}' and V_ISCRIZCAT_TIT.CAISIM = ISCRIZCAT.CODCAT and V_ISCRIZCAT_TIT.TIPLAVG = ISCRIZCAT.TIPCAT" ordinabile="false"/>
					<gene:campoLista campo="INVPEN" width="80" visibile="false" entita = "ISCRIZCAT" where = "ISCRIZCAT.NGARA='${numeroGara}' and ISCRIZCAT.CODGAR='${codiceGara}' and ISCRIZCAT.CODIMP='${codiceDitta}' and V_ISCRIZCAT_TIT.CAISIM = ISCRIZCAT.CODCAT and V_ISCRIZCAT_TIT.TIPLAVG = ISCRIZCAT.TIPCAT" ordinabile="false"/>					
					<gene:campoLista campo="INVPEN_FIT" width="80" campoFittizio="true" definizione="N3;;;;INVPENIC" value="${gene:if(datiRiga.ISCRIZCAT_INVPEN eq 0 and datiRiga.V_ISCRIZCAT_TIT_ISFOGLIA eq '2', '', datiRiga.ISCRIZCAT_INVPEN) }" visibile="${tipoalgo eq '1' or tipoalgo eq '5'}" ordinabile="false"/>
					<gene:campoLista campo="AGGREA" width="80" visibile="${tipoalgo eq '11' or tipoalgo eq '12' or tipoalgo eq '14' or tipoalgo eq '15'}" entita = "ISCRIZCAT" where = "ISCRIZCAT.NGARA='${numeroGara}' and ISCRIZCAT.CODGAR='${codiceGara}' and ISCRIZCAT.CODIMP='${codiceDitta}' and V_ISCRIZCAT_TIT.CAISIM = ISCRIZCAT.CODCAT and V_ISCRIZCAT_TIT.TIPLAVG = ISCRIZCAT.TIPCAT" ordinabile="false" title="N.aggiudic."/>
					<gene:campoLista title="Penalità?" campo="PEN" campoFittizio="true" width="32" visibile="${tipoalgo eq '4' or tipoalgo eq '5' or tipoalgo eq '12' or tipoalgo eq '15'}" ordinabile="false" definizione="T2;;;SN" value="" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoPenalitaClassi"/>
					<gene:campoLista campo="ALTPEN" width="80" visibile="${tipoalgo eq '1' or tipoalgo eq '3' or tipoalgo eq '11' or tipoalgo eq '14'}" entita = "ISCRIZCAT" where = "ISCRIZCAT.NGARA='${numeroGara}' and ISCRIZCAT.CODGAR='${codiceGara}' and ISCRIZCAT.CODIMP='${codiceDitta}' and V_ISCRIZCAT_TIT.CAISIM = ISCRIZCAT.CODCAT and V_ISCRIZCAT_TIT.TIPLAVG = ISCRIZCAT.TIPCAT" ordinabile="false"/>
					
					<gene:campoLista campo="TITCAT"  visibile = "false"/>
					<gene:campoLista campo="TAB5DESC" entita = "TAB5" where ="TAB5.TAB5COD = 'G_j05' and TAB5.TAB5TIP = V_ISCRIZCAT_TIT.TITCAT" visibile="false" />
					<gene:campoLista campo="TAB1DESC" entita = "TAB1" where ="TAB1.TAB1COD = 'G_038' and TAB1.TAB1TIP = V_ISCRIZCAT_TIT.TIPLAVG and V_ISCRIZCAT_TIT.CAISIM <> '0'" visibile="false" />
															
					<c:if test="${updateLista ne 1 and (tipoalgo eq '1' or tipoalgo eq '3' or tipoalgo eq '11' or tipoalgo eq '14')  }">
						<gene:campoLista title="Dett. penalità" width="20" campoFittizio="true" definizione="T20" campo="COLONNA_PEN">
							<c:if test='${datiRiga.V_ISCRIZCAT_TIT_ISFOGLIA eq "1"}'>
								<a href="javascript:chiaveRiga='${chiaveRigaJava}';apriPopup('${chiaveRigaJava}','1');" title="Dettaglio penalità" >
									<img width="16" height="16" title="Dettaglio penalità" alt="Dettaglio penalità" src="${pageContext.request.contextPath}/img/penalita.png"/>
								</a>
							</c:if>
							
						</gene:campoLista>
					</c:if>
					
					<c:if test='${updateLista ne 1 && gene:checkProt(pageContext, "MASC.VIS.GARE.ISCRIZCAT-scheda")}'>
						<gene:campoLista title="" width="20" >
							<c:choose>
							<c:when test="${datiRiga.V_ISCRIZCAT_TIT_ISFOGLIA eq '1' && datiRiga.V_ISCRIZCAT_CLASSI_CAISIM ne '0' }">
								<a href="javascript:chiaveRiga='${chiaveRigaJava}';apriPopup('${chiaveRigaJava}','2');" title="Ulteriori informazioni" >
									<img width="16" height="16" title="Ulteriori informazioni" alt="Ulteriori informazioni" src="${pageContext.request.contextPath}/img/opzioniUlteriori.png"/>
								</a>
							</c:when>
							<c:otherwise>
								&nbsp;
							</c:otherwise>
						</c:choose>
							
						</gene:campoLista>
					</c:if>
								
					<c:set var="indiceRiga" value="${indiceRiga + 1}"/>
			
					<%/* Questa parte di codice setta lo stile della riga in base che sia un titolo oppure una riga di dati */%>
					<gene:campoLista visibile="false" >
						<th style="display:none">
							<c:if test="${oldTitolo != newTitolo || newTiplavg != oldTiplavg}"><script type="text/javascript">
								var nomeForm = document.forms[0].name;
								var indice = ${indiceRiga};
								document.getElementById("tab" + nomeForm).rows[indice  + (${numCambi } )].className =document.getElementById("tab" + nomeForm).rows[indice  + (${numCambi }  ) - 1].className;
								document.getElementById("tab" + nomeForm).rows[indice  + (${numCambi } ) - 1].className = "white";
							</script></c:if>
						</th>
					</gene:campoLista>
					
					<gene:campoLista visibile="false">
			             <th style="display:none">
					         <c:if test="${datiRiga.V_ISCRIZCAT_TIT_ISFOGLIA eq '2'}">
					         <c:set var="numliv" value="${datiRiga.V_ISCRIZCAT_TIT_NUMLIV}"/>
				                 <script type="text/javascript">
					                 var nomeForm = document.forms[0].name;
		 							 var indice = ${indiceRiga};
		 							
		 							 document.getElementById("tab" + nomeForm).rows[indice + (${numCambi } ) ].className = "livello"+${numliv};
				                 </script>
				             </c:if>
			             </th>
				     </gene:campoLista>
					<gene:campoLista campo="ISFOGLIA"  visibile = "false" edit="true"/>
					<gene:campoLista campo="NUMLIV"  visibile = "false" edit="true"/>	
						
					<input type="hidden" name="numeroCategorie" id="numeroCategorie" value="" />
					<input type="hidden" name="codiceDitta" id="codiceDitta" value="${codiceDitta}" />
					<input type="hidden" name="modifica" id="modifica" value="${modifica}" />
					<input type="hidden" name="numeroGara" id="numeroGara" value="${numeroGara}" />
					<input type="hidden" name="codiceGara" id="codiceGara" value="${codiceGara}" />
					<input type="hidden" name="paginaAttivaWizard" id="paginaAttivaWizard" value="${paginaAttivaWizard}" />
					<input type="hidden" name="aggCategorie" id="aggCategorie" value="${aggCategorie}" />
					<input type="hidden" name="filtro" id="filtro" value="" />
                </gene:formLista>
				</td>
			</tr>
						
			<tr>
				<td class="comandi-dettaglio"  colSpan="2">
					<c:choose>
						<c:when test='${updateLista eq 1}'>
							<c:if test='${occorrenzeOltreSoglia ne "Si" || !empty param.filtro}'>
								<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:salva();">
							</c:if>
							<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:listaAnnullaModifica();">
						</c:when>
						<c:otherwise>
							<INPUT type="button"  class="bottone-azione" value='Torna a elenco operatori' title='Torna a elenco operatori' onclick="javascript:historyVaiIndietroDi(1);">&nbsp;&nbsp;
							<c:if test="${numRichiesteAggCategorie ne '0' and autorizzatoModifiche ne 2 and gene:checkProt(pageContext,'FUNZ.VIS.ALT.GARE.AcquisiciAggiornamentoCategorie')}">
								<INPUT type="button"  class="bottone-azione" value='Acquisisci modifiche da portale' title='Acquisisci modifiche da portale' onclick="javascript:aquisisciAgg();">&nbsp;&nbsp;
							</c:if>
							<c:if test="${tipoalgo eq '4' or tipoalgo eq '5' or tipoalgo eq '12' or tipoalgo eq '15'}">
								<INPUT type="button"  class="bottone-azione" value='Visualizza dettaglio classifiche' title='Visualizza dettaglio classifiche' onclick="javascript:dettaglioClassi();">&nbsp;&nbsp;
							</c:if>
							<c:if test='${gene:checkProtFunz(pageContext, "INS","INS") and paginaAttivaWizard=="2" and autorizzatoModifiche ne 2}'>
								<INPUT type="button"  class="bottone-azione" value='Modifica attribuzione' title='Modifica attribuzione' onclick="javascript:listaApriInModifica();">
							</c:if>						
							&nbsp;
						</c:otherwise>
					</c:choose>
					&nbsp;
				</td>
			</tr>
			
					
		</table>
  </gene:redefineInsert>
	<gene:javaScript>
		document.getElementById("numeroCategorie").value = ${currentRow}+1;
		
		<c:if test='${updateLista eq 1}'>	
			
			// Funzione per associare le funzioni JS da eseguire al momento di modifica
			// di un campo. Questo e' necessario perchè la lista e' stata progetta per
			// non essere mai modificabile e quindi il tag gene:campoLista non associa
			// mai una funzione JS all'evento onchange.
			function associaFunzioniEventoOnchange(){
				for(var i=1; i <= ${currentRow}+1; i++){
					document.getElementById("ISCRIZCAT_INFNUMCLASS_" + i).onchange = controlloInfnumclass;
					document.getElementById("ISCRIZCAT_SUPNUMCLASS_" + i).onchange = controlloSupnumclass;
				}
			}
			
			checkModificato = false;
			
			associaFunzioniEventoOnchange();
			inizializzaLista();
			
			function controlloInfnumclass(){
				var objId = this.id;
				var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
				var infnumclass = toVal(this.value);
				if(infnumclass!=null && infnumclass!=""){
					var supnumclass = toVal(getValue("ISCRIZCAT_SUPNUMCLASS_" + numeroRiga));
					if((supnumclass!=null && supnumclass!="") && infnumclass >supnumclass){
						alert("La classifica inferiore deve essere minore della classifica superiore");
						setValue("ISCRIZCAT_INFNUMCLASS_" + numeroRiga, "");
					}
				}
			}
			
			function controlloSupnumclass(){
				var objId = this.id;
				var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
				var supnumclass = toVal(this.value);
				if(supnumclass!=null && supnumclass!=""){
					var infnumclass = toVal(getValue("ISCRIZCAT_INFNUMCLASS_" + numeroRiga));
					if((infnumclass!=null && infnumclass!="") && infnumclass >supnumclass){
						alert("La classifica superiore deve essere maggiore della classifica inferiore");
						setValue("ISCRIZCAT_SUPNUMCLASS_" + numeroRiga, "");
					}
				}
			}
			
			function aggiornaRiga(check,numeroRiga){
				var tiplavg = getValue("V_ISCRIZCAT_TIT_TIPLAVG_" + numeroRiga);
				if((tiplavg == 1 
					 || (tiplavg == 2 && ${esisteClassificaForniture})
					 || (tiplavg == 3 && ${esisteClassificaServizi})
					 || (tiplavg == 4 && ${esisteClassificaLavori150})
					 || (tiplavg == 5 && ${esisteClassificaServiziProfessionali})) && check.checked){
					//showObj("ISCRIZCAT_INFNUMCLASS_" + numeroRiga, true);
					//showObj("ISCRIZCAT_SUPNUMCLASS_" + numeroRiga, true);
					document.getElementById("ISCRIZCAT_INFNUMCLASS_" + numeroRiga).disabled = false;
					document.getElementById("ISCRIZCAT_SUPNUMCLASS_" + numeroRiga).disabled = false;
				}else{
					document.getElementById("ISCRIZCAT_INFNUMCLASS_" + numeroRiga).disabled = true;
					document.getElementById("ISCRIZCAT_SUPNUMCLASS_" + numeroRiga).disabled = true;
					//showObj("ISCRIZCAT_INFNUMCLASS_" + numeroRiga, false);
					//showObj("ISCRIZCAT_SUPNUMCLASS_" + numeroRiga, false);
				}
				checkModificato=true;
			}
			
			function inizializzaLista(){
				var numeroCategorie = ${currentRow}+1;
				
				for(var i=1; i <= numeroCategorie; i++){
					var tiplavg = getValue("V_ISCRIZCAT_TIT_TIPLAVG_" + i);
					var caisim = getValue("V_ISCRIZCAT_TIT_CAISIM_" + i);
					var codcat = getValue("ISCRIZCAT_CODCAT_" + i);
					var isfoglia = getValue("V_ISCRIZCAT_TIT_ISFOGLIA_" + i);
					
					if((tiplavg == 1
						 || (tiplavg == 2 && ${esisteClassificaForniture})
						 || (tiplavg == 3 && ${esisteClassificaServizi})
						 || (tiplavg == 4 && ${esisteClassificaLavori150})
						 || (tiplavg == 5 && ${esisteClassificaServiziProfessionali})) && caisim != 0){
						if(caisim == codcat && isfoglia==1){
							document.getElementById("ISCRIZCAT_INFNUMCLASS_" + i).disabled = false;
							document.getElementById("ISCRIZCAT_SUPNUMCLASS_" + i).disabled = false;
							showObj("ISCRIZCAT_INFNUMCLASS_" + i, true);
							showObj("ISCRIZCAT_SUPNUMCLASS_" + i, true);
						}else{
							document.getElementById("ISCRIZCAT_INFNUMCLASS_" + i).disabled = true;
							document.getElementById("ISCRIZCAT_SUPNUMCLASS_" + i).disabled = true;
							showObj("ISCRIZCAT_INFNUMCLASS_" + i, true);
							showObj("ISCRIZCAT_SUPNUMCLASS_" + i, true);
						}
						
					}else{
						//document.getElementById("ISCRIZCAT_INFNUMCLASS_" + i).disabled = true;
						//document.getElementById("ISCRIZCAT_SUPNUMCLASS_" + i).disabled = true;
						showObj("ISCRIZCAT_INFNUMCLASS_" + i, false);
						showObj("ISCRIZCAT_SUPNUMCLASS_" + i, false);
					}
					
					
					if(isfoglia==2 && numeroCategorie == 1 ){
						 	document.forms[0].keys.style.display= "none";
						 	showObj("ISCRIZCAT_INFNUMCLASS_" + i, false);
							showObj("ISCRIZCAT_SUPNUMCLASS_" + i, false);
					}
				    else if(isfoglia==2 && numeroCategorie > 1 ){
						 	document.forms[0].keys[i - 1].style.display= "none";
						 	showObj("ISCRIZCAT_INFNUMCLASS_" + i, false);
							showObj("ISCRIZCAT_SUPNUMCLASS_" + i, false);
					}
				}
			}
			
			function salva(){
				var numeroCategorie = ${currentRow}+1;
				var visibileClassificaInf="${gene:checkProt(pageContext, "COLS.VIS.GARE.ISCRIZCAT.INFNUMCLASS")}";
				var visibileClassificaSup="${gene:checkProt(pageContext, "COLS.VIS.GARE.ISCRIZCAT.SUPNUMCLASS")}";
				var obbligatorioClassificaInf="";
				var obbligatorioClassificaSup="";
				var tipoclass = "${tipoclass}";
				if(tipoclass=='1'){
					obbligatorioClassificaInf="true";
					obbligatorioClassificaSup="true";
				}else if(tipoclass=='2'){
					obbligatorioClassificaSup="true";
				}
				
				var classificaInfTutteValorizzate = "true";
				var classificaSupTutteValorizzate = "true";
				var esisteClassificaForniture = "${esisteClassificaForniture}";
				var esisteClassificaServizi = "${esisteClassificaServizi}";
				var esisteClassificaLavori150 = "${esisteClassificaLavori150}";
				var esisteClassificaServiziProfessionali = "${esisteClassificaServiziProfessionali}";
								
				if((visibileClassificaInf=="true" && obbligatorioClassificaInf=="true")||
					(visibileClassificaSup=="true" && obbligatorioClassificaSup=="true")){
					
					var elementoSelezionato;
					for(var i=1; i <= numeroCategorie; i++){
						 elementoSelezionato="false";
						 if(numeroCategorie == 1 && document.forms[0].keys.checked)
						 	elementoSelezionato="true";
						 else if(numeroCategorie > 1 && document.forms[0].keys[i - 1].checked)
						 	elementoSelezionato="true";
						 	
						 if (elementoSelezionato=="true"){
        					var infnumclass = getValue("ISCRIZCAT_INFNUMCLASS_" + i);
        					var supnumclass = getValue("ISCRIZCAT_SUPNUMCLASS_" + i);
        					var codiceCat = getValue("V_ISCRIZCAT_TIT_CAISIM_" + i);
        					var tiplavg = getValue("V_ISCRIZCAT_TIT_TIPLAVG_" + i);
        					var isfoglia = getValue("V_ISCRIZCAT_TIT_ISFOGLIA_" + i);
        					if(isfoglia == 1){
	        					if((infnumclass == null || infnumclass == "") && visibileClassificaInf=="true" 
	        						&& obbligatorioClassificaInf=="true" && codiceCat != "0" 
	        						&& ((tiplavg==2 && esisteClassificaForniture == "true") || (tiplavg==3 && esisteClassificaServizi == "true") 
	        						|| (tiplavg==4 && esisteClassificaLavori150 == "true") || (tiplavg==5 && esisteClassificaServiziProfessionali == "true") || tiplavg==1)){
	        						classificaInfTutteValorizzate = "false";
	        					}
	        					if((supnumclass == null || supnumclass == "") && visibileClassificaSup=="true" 
	        						&& obbligatorioClassificaSup =="true" && codiceCat != "0" 
	        						&& ((tiplavg==2 && esisteClassificaForniture == "true") || (tiplavg==3 && esisteClassificaServizi == "true") 
	        						|| (tiplavg==4 && esisteClassificaLavori150 == "true") || (tiplavg==5 && esisteClassificaServiziProfessionali == "true") || tiplavg==1)){
	        						classificaSupTutteValorizzate = "false";
	        					}
        					}
        					
        				}
					}
					if(classificaInfTutteValorizzate == "false"){
						outMsg("Il campo 'Classifica inferiore' è obbligatorio", "ERR");
						onOffMsg();
					}
					if(classificaSupTutteValorizzate == "false"){
						if(tipoclass=='1')
							outMsg("Il campo 'Classifica superiore' è obbligatorio", "ERR");
						else if(tipoclass=='2')
							outMsg("Il campo 'Classifica' è obbligatorio", "ERR");
						onOffMsg();
					}
				}
				
				if(classificaInfTutteValorizzate == "true" && classificaSupTutteValorizzate == "true")
					listaConferma();
			}
			
			function applicaFiltro(){
				//Se si sono apportate delle modifiche alla lista lanciare un messaggio 
				var msg="La lista risulta modificata.\nApplicando il filtro, le modifiche apportate andranno perse. Per non perdere tali modifiche, annullare l'operazione di filtro, premere il pulsante 'Salva'  e quindi riattivare la funzione di modifica e applicare il filtro.\n\nVuoi procedere ugualmente con il filtro o annullare l'operazione?";
				if(checkModificato==true){
					if(!confirm(msg))
						return;
				}
				var numeroCategorie = ${currentRow}+1;
				for(var i=1; i <= numeroCategorie; i++){
					var infnumclass = getValue("ISCRIZCAT_INFNUMCLASS_" + i);
        			var supnumclass = getValue("ISCRIZCAT_SUPNUMCLASS_" + i);
        			var infnumclassOld = getOriginalValue("ISCRIZCAT_INFNUMCLASS_" + i);
        			var supnumclassOld = getOriginalValue("ISCRIZCAT_SUPNUMCLASS_" + i);
        			if(infnumclass != infnumclassOld || supnumclass!=supnumclassOld){
        				if(!confirm(msg))
							return;
        			}
				}
				
				var valore=document.getElementById("filtroCodiceDesc").value;
				if(valore != null && valore!="" && valore.length>0){
					valore = valore.toUpperCase();
					
				}
				
				/*
				// la seguente riga serve a modificare il nome della popup, in modo da
				// gestire la chiusura della presente e la riapertura della stessa in un'altra
				// popup in modo indipendente
				window.name = "apriElencoCategorieOld";
				
				var ngara="${numeroGara }";
				var codiceDitta = "${codiceDitta }";
				
				opener.apriElencoCategorieFiltrato(ngara,codiceDitta,valore);
				window.close();
				*/
				document.getElementById("filtro").value=valore;
				listaApriInModifica();
			}
		</c:if>
		
		function dettaglioClassi(){
			var codiceGara="${codiceGara }";
			var numeroGara="${numeroGara}";
			var codiceDitta="${codiceDitta }";
			var key = "CODGAR5=T:" + codiceGara + ";DITTAO=T:" + codiceDitta + ";NGARA5=T:" + numeroGara;
						
			//var href = contextPath + "/ApriPagina.do?href=gare/v_iscrizcat_classi/v_iscrizcat_classi-listaPopup.jsp";
			var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=gare/v_iscrizcat_classi/v_iscrizcat_classi-lista.jsp";
			href += "&key=" + key;
			
			//var modifica="${modifica }";
			//href += "&modifica=" + modifica;
			var paginaAttivaWizard = "${paginaAttivaWizard}";
			href += "&paginaAttivaWizard=" + paginaAttivaWizard;
			bloccaRichiesteServer();
			document.location.href = href;
		}
		
		<c:if test='${updateLista ne 1}'>	
		
			//Si centra l'immagine delle penalità
			var numeroCategorie = document.getElementById("numeroCategorie").value;
			for(i=1;i<=numeroCategorie;i++){
	           $("#colCOLONNA_PEN_" + i).parent().css( "text-align", "center" );
	        }
		
		function apriPopup( chiaveRiga,tipo){
			var codiceGara="${codiceGara }";
			var numeroGara="${numeroGara}";
			var codiceDitta="${codiceDitta }";
			var chiavi = chiaveRiga.split(";");
			var codact=chiavi[0].substring(chiavi[0].indexOf(":") + 1, chiavi[0].length);
			var tipcat=chiavi[1].substring(chiavi[1].indexOf(":") + 1, chiavi[1].length);
			var key="ISCRIZCAT.CODGAR=T:" + codiceGara + ";ISCRIZCAT.CODIMP=T:" + codiceDitta + ";ISCRIZCAT.NGARA=T:" + numeroGara;
			key += ";ISCRIZCAT.CODCAT=T:" + codact + ";ISCRIZCAT.TIPCAT=N:" + tipcat;
			
			var href;
			href = "href=gare/iscrizcat/iscrizcat-schedaPopup-ulterioriCampi.jsp";
						
			href += "&key=" + key;
			href += "&entita=ISCRIZCAT";					
			var modificabile="false";
			<c:if test='${updateLista ne 1 and paginaAttivaWizard!="1"}'>
				modificabile="true";
			</c:if>
			href += "&modificabile=" + modificabile;
			href += "&salvato=No";
			href += "&tipo=" + tipo;
			var autorizzatoModifiche = "${autorizzatoModifiche }";
			href += "&autorizzatoModifiche=" + autorizzatoModifiche;
			openPopUpCustom(href, "ulterioriCampi", 800, 550, "yes", "yes");
		}
		
					
		function aquisisciAgg(){
			var href = "href=gare/commons/popupAggiornaCategorieDaPortale.jsp";
			href += "&ngara=" + "${numeroGara}";
			href += "&codiceDitta="+ "${codiceDitta}";
			openPopUpCustom(href, "acquisisciAggiornamentoCatDaPortale", 850, 500, "yes", "yes");
		}
						
		</c:if>
		
	</gene:javaScript>
		
</gene:template>