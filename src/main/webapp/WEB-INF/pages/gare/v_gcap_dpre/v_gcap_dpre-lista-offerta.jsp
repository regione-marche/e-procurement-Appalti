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

<c:set var="numeroGara" value='${gene:getValCampo(key, "GCAP.NGARA")}' />
<c:set var="contaf" value='${gene:getValCampo(key, "GCAP.CONTAF")}' />
<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:set var="codiceGara" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetCodiceGaraFunction", pageContext)}' />

 <c:set var="tipoArticolo" value="lavorazione o fornitura" />

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.InizializzaUpdateListaFunction" parametro="${key}" />

<c:set var="tmp" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetDatiArticoloFunction", pageContext, numeroGara,contaf)}'/>

<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="V_GCAP_DPRE-lista-OFF">
	
	<gene:redefineInsert name="head">
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>		
	</gene:redefineInsert>
	

	
	<% // Ridefinisco il corpo della ricerca %>
	<gene:redefineInsert name="corpo">
	
	<gene:set name="titoloMenu">
		<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
	</gene:set>
	
	
	<% // viene gestita l'apertura della pagina anche da TORN %>
			<% // Apertura da TORN %>
			<c:set var="ordinamento" value="5"/>
			<c:set var="where" value='V_GCAP_DPRE.NGARA = #GCAP.NGARA# AND V_GCAP_DPRE.CONTAF = #GCAP.CONTAF# AND V_GCAP_DPRE.PREOFF is not null
			 and exists(select dittao from ditg where ngara5=#GCAP.NGARA# and dittao=v_gcap_dpre.cod_ditta and fasgar is null)'/>

	<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-pg-lista-scheda.jsp">
		<jsp:param name="entita" value="V_GARE_TORN"/>
		<jsp:param name="inputFiltro" value="CODGAR=T:${codiceGara}"/>
		<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
	</jsp:include>
		
  	<% // Creo la lista per gcap e dpre mediante la vista v_gcap_dpre %>
		<table class="lista">
			<tr>
				<td>
					<table class="arealayout">
							<tr>
								<td width="300px" ><b>Dettaglio lavorazione o fornitura</b></td>
							</tr>
							<tr>
								<td width="300px">Voce:</td>
								<td align="left" width="135px"> &nbsp;<span id="codvoc">${codvoc} &nbsp;&nbsp;</span></td>
								<td></td>
							</tr>
							<tr>
								<td width="300px">Descrizione:</td>
								<td align="" width="500px">&nbsp;<span id="voce">${voce} &nbsp;&nbsp;</span>
								</td>
							</tr>
							<tr>
								<td width="300px">Quantità:</td>
								<td align="left" width="135px">&nbsp;<span id="quanti">${quanti} &nbsp;&nbsp;</span>
								</td>
								<td></td>
							</tr>
							<tr>
								<td width="300px" >Prezzo unitario:</td>
								<td align="left" width="135px" class="azzurro">&nbsp;<span id="prezun">${prezun} &euro;&nbsp;&nbsp;</span>
								<td></td>
							</tr>
							
							<tr>
								<td width="300px">Data consegna prevista:</td>
								<td align="left" width="135px">&nbsp;<span id="datacons">${datacons} &nbsp;&nbsp;</span>
								</td>
								<td></td>
							</tr>
						
					</table>
				</td>
			</tr>
			
			<c:if test="${updateLista eq 1}">
				<td></td>
				<tr>
					<td colspan="2"><span id="modificaAssegnatario"><b>Selezionare l'assegnatario impostando il check nella lista. &nbsp;&nbsp; </b></span></td>
				</tr>
				<tr>
						<td colspan="2">E' possibile annullare precedenti assegnazioni deselezionando il check.</span></td>
				</tr>
			</c:if>

			<tr>
				<td>
  				<gene:formLista entita="V_GCAP_DPRE"  where="${where}" pagesize="20" tableclass="datilista" sortColumn="${ordinamento}" gestisciProtezioni="true"
  					gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreV_GCAP_DPRE_OFFERTA">
  					<gene:setString name="titoloMaschera" value='Valutazione delle offerte per il prodotto ${codvoc}' />
  			
			  	<gene:redefineInsert name="addHistory">
					<gene:historyAdd titolo='${gene:getString(pageContext,"titoloMaschera",gene:resource("label.tags.template.lista.titolo"))}' id="V_GCAP_DPRE-lista-OFF" />
				</gene:redefineInsert>
						<gene:redefineInsert name="listaNuovo" />
						<gene:redefineInsert name="listaEliminaSelezione" />
  					
  					<gene:redefineInsert name="addToAzioni" >
	  					<c:if test='${datiRiga.rowCount > 0 }'>
							<c:if test='${ autorizzatoModifiche ne "2" and isAffidato ne "true" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.AssegnaMigliorOfferente") and updateLista ne 1}'>
								<c:if test='${ gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.AssegnaMigliorOfferente")}'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:modificaLista();" title="Assegna offerente" tabindex="1501">
												Assegna all'offerente
											</a>
										</td>
									</tr>
								</c:if>
								<c:if test='${ gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.ImpostaProdottoNonAssegnato")}'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:impostaProdottoNonAssegnato('${numeroGara}' ,'${contaf}' ,'${isprodneg}');" title="Imposta prodotto non assegnato" tabindex="1501">
												Imposta prodotto non assegnato
											</a>
										</td>
									</tr>
								</c:if>
							</c:if>
						</c:if>
  					</gene:redefineInsert>
  					<gene:campoLista title="Opzioni" width="50">
						<c:if test="${currentRow >= 0 and (updateLista eq 0)}">
							<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
								<c:if test='${gene:checkProt(pageContext, "MASC.VIS.GARE.V_GCAP_DPRE-scheda")}' >
									<gene:PopUpItemResource resource="popupmenu.tags.lista.visualizza" title="Visualizza offerta ditta"/>
								</c:if>
							</gene:PopUp>
						</c:if>
					</gene:campoLista>
					<gene:campoLista campo="CODGAR" visibile="false" edit="${updateLista eq 1}"/>
					<gene:campoLista campo="CONTAF" visibile="false" edit="${updateLista eq 1}"/>
					<gene:campoLista campo="COD_DITTA" visibile="false" edit="${updateLista eq 1}"/>
					<gene:campoLista campo="NUMORDPL" title="N.pl." entita="DITG" where="DITG.CODGAR5=V_GCAP_DPRE.CODGAR AND DITG.NGARA5=V_GCAP_DPRE.NGARA AND DITG.DITTAO=V_GCAP_DPRE.COD_DITTA" edit = "false" />
					<gene:campoLista campo="NOMEST" entita="IMPR" where="IMPR.CODIMP=V_GCAP_DPRE.COD_DITTA" width="120" headerClass="sortable" />
					<gene:campoLista campo="NGARA" visibile="false" title='Codice lotto' edit="${updateLista eq 1}"/>
					<gene:campoLista campo="QTAORDINABILE"  visibile="false" edit="${updateLista eq 1}"/>
					<gene:campoLista campo="PREOFF" visibile="true"  edit = "false"/>
					<gene:campoLista campo="IMPOFF" visibile="true"  edit = "false"/>
					<gene:campoLista campo="QTAORDINATA"  visibile="false" edit="${updateLista eq 1}"/>
					<gene:campoLista campo="DATACONSOFF" title="Data consegna garantita" visibile="true"  edit = "false"/>
					<gene:campoLista campo="TIPOLOGIA" visibile="true"  edit = "false"/>
					<gene:campoLista campo="NOTE" visibile="true"  edit = "false" width="200"/>
					<c:set var="isProdottiAggiudicatiDitta" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.isProdottiAggiudicatiDittaFunction", pageContext, datiRiga.V_GCAP_DPRE_NGARA,datiRiga.V_GCAP_DPRE_COD_DITTA)}'/>
					<gene:campoLista campo="IS_PRODOTTI_AGGIUDICATI_DITTA" campoFittizio="true" definizione="T10" value="${isProdottiAggiudicatiDitta}" edit="${updateLista eq 1}" visibile="false"/>
					
					<gene:campoLista title="" width="50" >
						<c:if test="${currentRow >= 0}">
							<c:choose>
								<c:when test='${updateLista eq "0"}'>
									<c:if test='${!empty datiRiga.V_GCAP_DPRE_QTAORDINATA}'>
										<span id="INFO_TOOLTIP${currentRow }" title="Prodotto assegnato">
											<IMG SRC="${contextPath}/img/rmAssegnatario.png" >
										</span>
									</c:if>
									<c:if test='${empty datiRiga.V_GCAP_DPRE_QTAORDINATA && !empty requestScope.aggiudicatarioDefault && datiRiga.V_GCAP_DPRE_COD_DITTA eq requestScope.aggiudicatarioDefault}'>
										<span id="INFO_TOOLTIP${currentRow }" title="Prezzo più basso">
											<IMG SRC="${contextPath}/img/rmAssegnatarioDef.png" >
										</span>
									</c:if>
									<c:if test='${isprodneg eq "1" && empty datiRiga.V_GCAP_DPRE_QTAORDINATA}'>
										<span id="INFO_TOOLTIP${currentRow }" title="Prodotto non assegnato">
											<IMG width="16" height="16" SRC="${contextPath}/img/lavNonAssegnata.png" >
										</span>
									</c:if>
								</c:when>
								<c:otherwise>
									<c:if test='${autorizzatoModifiche eq "1"}'>
										<input type="hidden" name="proprietario"  id="proprietario${currentRow+1}" value="${datiRiga.V_GCAP_DPRE_COD_DITTA}" />
										<input type="checkbox" name="keys" id="aggiud${currentRow+1}" value="${chiaveRiga}" <c:if test='${!empty datiRiga.V_GCAP_DPRE_QTAORDINATA}'>checked="checked"</c:if> onchange="javascript:setAggiudicatario(${currentRow+1});" />
									</c:if>
								</c:otherwise>
							</c:choose>
						</c:if>
					</gene:campoLista>
				
						<input type="hidden" name="AGGIORNA_DA_LISTA" id="AGGIORNA_DA_LISTA" value="0" />
						<input type="hidden" name="numeroProdotti" id="numeroProdotti" value="" />
						<input type="hidden" name="isGaraTelematica" value="${isGaraTelematica}" />
						<input type="hidden" name="faseGara" value="${faseGara}" />
						<input type="hidden" name="offtel" value="${offtel}" />
						<input type="hidden" name="modlicg" value="${modlicg}" />
						<input type="hidden" name="daAgg" value="${daAgg}" />	
						<input type="hidden" name="aggiudicatario"  id="aggiudicatario" value="" />
						<input type="hidden" name="isprodneg"  id="isprodneg" value="${isprodneg}" />
						<input type="hidden" name="ngara"  id="ngara" value="${ngara}" />
						<input type="hidden" name="contaf"  id="contaf" value="${contaf}" />
				</gene:formLista>
				
				
			
				</td>
			</tr>
			<tr>
			<tr>
				<td class="comandi-dettaglio" colSpan="2">
							<c:if test='${autorizzatoModifiche ne "2" && isAffidato ne "true"}'>
								<c:choose>
								<c:when test='${updateLista eq 1}'>
									<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:controllaISPRODNEG();">
									<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:listaAnnullaModifica();">
								</c:when>
								<c:otherwise>
									<c:if test='${datiRiga.rowCount > 0 }'>
										<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.AssegnaMigliorOfferente")}'>
											<INPUT type="button"  class="bottone-azione" value="Assegna all'offerente" title="Assegna all'offerente" onclick="javascript:modificaLista();">
										</c:if>
										<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.ImpostaProdottoNonAssegnato")}'>
											<INPUT type="button"  class="bottone-azione" value="Imposta prodotto non assegnato" title="Imposta prodotto non assegnato" onclick="javascript:impostaProdottoNonAssegnato('${numeroGara}' ,'${contaf}' ,'${isprodneg}');">
										</c:if>
									</c:if>
								</c:otherwise>
							  </c:choose>
						  	</c:if>
				<c:if test='${updateLista ne 1 }'>
						<INPUT type="button"  class="bottone-azione" value='Torna a elenco prodotti' title='Torna a elenco prodotti' onclick="javascript:historyVaiIndietroDi(1);">
					</c:if>
				</td>
			</tr>
			</tr>
			
		</table>
  </gene:redefineInsert>
  <gene:javaScript>
  	
  
  	setContextPath("${pageContext.request.contextPath}");
	var len = ${currentRow}+1;
	document.getElementById("numeroProdotti").value = ${currentRow}+1;
  
	function annulla(){
		listaAnnullaModifica();
	}

	function modificaLista(){
		document.forms[0].updateLista.value = "1";
		listaVaiAPagina(document.forms[0].pgCorrente.value);
	}
	function listaConferma() {
		document.getElementById("AGGIORNA_DA_LISTA").value = "2";
		document.forms[0].metodo.value = "updateLista";
		document.forms[0].key.value = document.forms[0].keyParent.value;
		document.forms[0].pgVaiA.value = document.forms[0].pgCorrente.value;
		document.forms[0].updateLista.value = "0";
		bloccaRichiesteServer();
		document.forms[0].submit();
	}

	
	function controllaISPRODNEG(){
		if(${isprodneg eq "1"}){
			if(confirm("La lavorazione risulta contrassegnata come \"non assegnata\". \nVuoi procedere ugualmente all'assegnazione a offerente?")){
				listaConferma();
			};
		} else {
			listaConferma();
		}
	}
	
	function impostaProdottoNonAssegnato(numeroGara,contaf,isprodneg){
		var href = "href=gare/v_gcap_dpre/popupImpostaProdottoNonAssegnato.jsp&numeroGara=" + numeroGara + "&contaf=" + contaf + "&isprodneg=" + isprodneg + "&numeroPopUp=1";
		win = openPopUpCustom(href, "impostaProdottoNonAssegnato", 500, 300, "no", "no");

		if(win!=null)
			win.focus();
	}

	
	function tornaARiepilogo() {
		historyBack();
	}
	
	
	

	function setAggiudicatario(idRiga){
		if(document.getElementById("aggiud" + idRiga).checked){
		    for(var s=1; s < (len+1); s++){
				if(s!=idRiga){
					document.getElementById("aggiud" + s).checked = false;
					
				}else{
					var prop = document.getElementById("proprietario" + s).value;
					setValue("aggiudicatario", prop);
				}
			}
			
		}else{
			setValue("aggiudicatario", "");
		}
		
	}

	 
	 
	
	</gene:javaScript>
</gene:template>