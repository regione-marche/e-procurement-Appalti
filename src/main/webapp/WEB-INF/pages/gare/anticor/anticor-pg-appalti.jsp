<%/*
   * Created on 29-08-2013
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

<c:set var="completato" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GestioneAnticorAppaltiFunction", pageContext, key)}'/>

<gene:set name="titoloMenu">
	<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
</gene:set>

<c:choose>
	<c:when test="${param.tipo eq 1}">
		<c:set var="where" value="ANTICORLOTTI.IDANTICOR = #ANTICOR.ID# and (ANTICORLOTTI.DAANNOPREC='2' or ANTICORLOTTI.DAANNOPREC='3')"/>
	</c:when>
	<c:when test="${param.tipo eq 2}">
		<c:set var="where" value="ANTICORLOTTI.IDANTICOR = #ANTICOR.ID# and (ANTICORLOTTI.DAANNOPREC='1' or ANTICORLOTTI.DAANNOPREC='3')"/>
	</c:when>
	<c:otherwise>
		<c:set var="where" value="ANTICORLOTTI.IDANTICOR = #ANTICOR.ID# and ANTICORLOTTI.PUBBLICA='1'"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${sessionScope.profiloUtente.abilitazioneGare eq "U" and not empty sessionScope.profiloUtente.codiceFiscale}'>
		<c:set var="filtroResponsabile" value=" ANTICORLOTTI.CODFISRESP='${sessionScope.profiloUtente.codiceFiscale}'" />
	</c:when>
	<c:otherwise>
		<c:set var="filtroResponsabile" value=" 1 = 1 " />
	</c:otherwise>
</c:choose>

<c:if test='${updateLista eq 1}' >
	<c:set var="isNavigazioneDisabilitata" value="1" scope="request" />
</c:if>

<c:if test="${!empty filtroAppalti}">
	<c:set var="trovaAddWhere" value="${sessionScope.filtroAppalti.trovaAddWhere}" scope="request" />
	<c:set var="trovaParameter" value="${sessionScope.filtroAppalti.trovaParameter}" scope="request" />
</c:if>

<table class="dettaglio-tab-lista">
	<c:if test="${!empty filtroAppalti }">
		<tr>
			<td style="font: 11px Verdana, Arial, Helvetica, sans-serif;">
			 <br><img src="${pageContext.request.contextPath}/img/filtro.gif" alt="Filtro">&nbsp;<span style="color: #ff0028; font-weight: bold;">Lista filtrata</span>
			 <c:if test='${updateLista ne 1}'>
				 &nbsp;&nbsp;&nbsp;[ <a href="javascript:AnnullaFiltro();" ><IMG SRC="${pageContext.request.contextPath}/img/cancellaFiltro.gif" alt="Cancella filtro"></a>
				 <a class="link-generico" href="javascript:AnnullaFiltro();">Cancella filtro</a> ]
			 </c:if>
			</td>
		</tr>
	</c:if>
	
	<tr>
		<td>
			<gene:formLista entita="ANTICORLOTTI" where='${where} and ${filtroResponsabile}' tableclass="datilista" sortColumn='5' 
					gestisciProtezioni="true"  gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreAnticorAppalti" pagesize="25" >
				
				<gene:redefineInsert name="listaNuovo"></gene:redefineInsert>
				<gene:redefineInsert name="listaEliminaSelezione"></gene:redefineInsert>
				
				<gene:redefineInsert name="addToAzioni">
					<c:choose>
						<c:when test='${updateLista eq 1}'>
							<tr>
								<td class="vocemenulaterale">
									<a href="javascript:listaConferma();" title="Salva modifiche" tabindex="1500">
										${gene:resource("label.tags.template.dettaglio.schedaConferma")}
									</a>
								</td>
							</tr>
							<tr>
								<td class="vocemenulaterale">
									<a href="javascript:listaAnnullaModifica();" title="Annulla modifiche" tabindex="1501">
										${gene:resource("label.tags.template.dettaglio.schedaAnnulla")}
									</a>
								</td>
							</tr>
						</c:when>
						<c:otherwise>
							<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.ANTICOR-scheda.filtro") }'>
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:impostaFiltro();" title='Imposta filtro' tabindex="1502">
											Imposta filtro
										</a>
									</td>
								</tr>
							</c:if>
							<c:if test='${param.tipo eq 1 and completato eq 2 and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.ANTICOR-scheda.ANNORIF.nuovoLotto")}'>
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:nuovoAppalto();" title='Nuovo' tabindex="1503">
											Nuovo
										</a>
									</td>
								</tr>
							</c:if>
							<c:if test='${param.tipo eq 1 and completato eq 2 and gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")}'>
							<tr>
								<td class="vocemenulaterale">
									<a href="javascript:listaEliminaSelezione();" title="Elimina selezionati" tabindex="1504">
										${gene:resource("label.tags.template.lista.listaEliminaSelezione")}</a>
								</td>
							</tr>
							</c:if>
							<c:if test='${param.tipo eq 1 and completato ne 1 and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.ANTICOR-scheda.ANNORIF.aggiungi")}'>
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:aggiungiAppalto('${key }');" title='Aggiungi lotto da dati correnti' tabindex="1505">
											Aggiungi lotto da dati correnti
										</a>
									</td>
								</tr>
							</c:if>
							<c:if test='${param.tipo eq 1 and completato eq 2 and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.ANTICOR-scheda.ANNORIF.ricarica") and sessionScope.profiloUtente.abilitazioneGare eq "A"}'>
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:ricaricaLotti('${key }');" title='Ricarica adempimento da dati correnti' tabindex="1506">
											Ricarica adempimento da dati correnti
										</a>
									</td>
								</tr>
							</c:if>
							<c:if test='${param.tipo eq 1 and completato eq 2 and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.ANTICOR-scheda.ANNORIF.scaricaModello")}'>
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:scaricaModello();" title='Scarica modello excel' tabindex="1507">
											Scarica modello Excel
										</a>
									</td>
								</tr>
							</c:if>
							<c:if test='${param.tipo eq 1 and completato eq 2 and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.ANTICOR-scheda.ANNORIF.importaDati")}'>
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:importaDati();" title='Importa dati da Excel' tabindex="1508">
											Importa dati da Excel
										</a>
									</td>
								</tr>
							</c:if>
							<c:if test='${param.tipo eq 1 and completato eq 2 and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.ANTICOR-scheda.ANNORIF.importaDatiSAP")}'>
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:importaDatiSAP();" title='Importa dati da SAP' tabindex="1509">
											Importa dati da SAP
										</a>
									</td>
								</tr>
							</c:if>
							
						</c:otherwise>
					</c:choose>
					
				</gene:redefineInsert>
				<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50">
					<c:if test="${currentRow >= 0}">
						<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
							<gene:PopUpItem title="Visualizza lotto" href="javascript:listaVisualizzaAppalto();"/>
							<c:if test='${ param.tipo eq 1 and completato ne 1}'>
								<gene:PopUpItem title="Modifica lotto" href="javascript:listaModificaAppalto();"/>
							</c:if>
							<c:if test='${datiRiga.ANTICORLOTTI_INVIABILE eq "1" and completato ne 1 }' >
								<c:if test='${datiRiga.ANTICORLOTTI_PUBBLICA ne "1" and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.ANTICORLOTTI.cambiaPubblicazione") and param.tipo eq 1}' >
									<gene:PopUpItem title="Pubblica" href="javascript:pubblicazione('${chiaveRigaJava}',1);" />
								</c:if>
								<c:if test='${datiRiga.ANTICORLOTTI_PUBBLICA eq "1" and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.ANTICORLOTTI.cambiaPubblicazione") and (param.tipo eq 1 or param.tipo eq 3)}' >
									<gene:PopUpItem title="Non Pubblicare" href="javascript:pubblicazione('${chiaveRigaJava}',2);" />
								</c:if>
							</c:if>
							
							<c:if test='${param.tipo eq 2 and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.ANTICORLOTTI.riportaAppalto") and completato eq 2}' >
								<c:if test='${datiRiga.ANTICORLOTTI_DAANNOPREC eq "1" }' >
									<gene:PopUpItem title="Riporta nell'anno di riferimento" href="javascript:riportaAppalto('${chiaveRigaJava}',1);" />
								</c:if>
								<c:if test='${datiRiga.ANTICORLOTTI_DAANNOPREC eq "3" }' >
									<gene:PopUpItem title="Non riportare nell'anno di riferimento" href="javascript:riportaAppalto('${chiaveRigaJava}',2);" />
								</c:if>
							</c:if>
							
							<c:if test='${param.tipo eq 1 and gene:checkProtFunz(pageContext, "DEL","DEL") && datiRiga.ANTICORLOTTI_LOTTOINBO ne "1" and completato ne 1 and datiRiga.rowCount > 0}' >
								<gene:PopUpItem title="Elimina lotto" href="javascript:listaElimina()" />
							</c:if>
						</gene:PopUp>
						<c:if test='${param.tipo eq 1 and gene:checkProtFunz(pageContext,"DEL","LISTADELSEL") && datiRiga.ANTICORLOTTI_LOTTOINBO ne "1" and completato ne 1}'>
							<input type="checkbox" name="keys" value="${chiaveRiga}"  />
						</c:if>	
					</c:if>
				</gene:campoLista>
				
				<gene:campoLista campo="ID" visibile="false" edit="${updateLista eq 1}"/>
				
				<gene:campoLista campo="INVIABILE" headerClass="sortable" visibile="false" edit="${updateLista eq 1}"/>
				<c:choose>
					<c:when test="${param.tipo eq 2}">
						<gene:campoLista title="&nbsp;" width="20" >
							<c:if test="${datiRiga.ANTICORLOTTI_DAANNOPREC == '3'}">
								<img width="16" height="16" title="Lotto riportato nell'anno di riferimento" alt="Lotto riportato nell'anno di riferimento" src="${pageContext.request.contextPath}/img/appalto_riportato.png"/>
							</c:if>
						</gene:campoLista>
					</c:when>
					<c:otherwise>
						<gene:campoLista title="&nbsp;" width="20" >
							<c:choose>
								<c:when test="${datiRiga.ANTICORLOTTI_INVIABILE eq '1' }">
									<img width="16" height="16" title="Lotto completo e validato" alt="Lotto completo e validato" src="${pageContext.request.contextPath}/img/validoSi.png"/>
								</c:when>
								<c:otherwise>
									<img width="16" height="16" title="Lotto incompleto e non validato" alt="Lotto incompleto e non validato" src="${pageContext.request.contextPath}/img/validoNo.png"/>
								</c:otherwise>
							</c:choose>
						</gene:campoLista>
					</c:otherwise>
				</c:choose>
				<gene:campoLista campo="CIG" headerClass="sortable" href="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizzaAppalto();"/>
				<gene:campoLista campo="PUBBLICA" headerClass="sortable" edit="${updateLista eq 1 and datiRiga.ANTICORLOTTI_INVIABILE eq '1'}"  visibile="${param.tipo ne 2 }" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoNoSiSenzaNull"/>
				<gene:campoLista campo="CODFISCPROP" headerClass="sortable"  />
				<gene:campoLista campo="OGGETTO" headerClass="sortable" />
				<gene:campoLista campo="IMPAGGIUDIC" headerClass="sortable" />
				<gene:campoLista campo="DATAINIZIO" headerClass="sortable" edit="${updateLista eq 1}"/>
				<gene:campoLista campo="DATAULTIMAZIONE" headerClass="sortable" edit="${updateLista eq 1}"/>
				<gene:campoLista campo="IMPSOMMELIQ" headerClass="sortable" edit="${updateLista eq 1}"/>
				<gene:campoLista campo="CIG_FIT"  campoFittizio="true" edit="${updateLista eq 1}" value="${datiRiga.ANTICORLOTTI_CIG}" definizione="T10" visibile="false"/>
				<gene:campoLista campo="STATO"  edit="${updateLista eq 1}" visibile="false"/>
				<gene:campoLista campo="LOTTOINBO"  visibile="false"/>
				<gene:campoLista campo="DAANNOPREC"  visibile="false"/>
				<gene:campoLista campo="IDLOTTO"  visibile="false"/>
				<gene:campoLista campo="TESTOLOG" edit="${updateLista eq 1}" visibile="false"/>
				<c:if test="${gene:checkProt(pageContext, 'COLS.VIS.GARE.ANTICORLOTTI.IDLOTTO') }">
					<gene:campoLista title="&nbsp;" width="20" >
						<c:if test="${not empty datiRiga.ANTICORLOTTI_IDLOTTO }">
							<img width="16" height="16" title="Lotto derivante dalla gara '${datiRiga.ANTICORLOTTI_IDLOTTO }'" alt="Lotto derivante dalla gara '${datiRiga.ANTICORLOTTI_IDLOTTO }'" src="${pageContext.request.contextPath}/img/idlotto.png"/>
						</c:if>
					</gene:campoLista>
				</c:if>
				<input type="hidden" name="numeroAppalti" id="numeroAppalti" value="" />
			</gene:formLista>
		</td>
	</tr>
	<tr>
		<td class="comandi-dettaglio" colSpan="2">
			<c:choose>
				<c:when test='${updateLista eq 1 }'>
					<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="listaConferma();">
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:listaAnnullaModifica();">
				</c:when>
				<c:otherwise>
					<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.ANTICORLOTTI.MODIFICA") && param.tipo eq 1 and completato ne 1 and datiRiga.rowCount > 0}'>
						<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:listaApriInModifica();">&nbsp;
					</c:if>
					<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.ANTICOR-scheda.ANNORIF.nuovoLotto") && param.tipo eq 1 and completato ne 1}'>
						<INPUT type="button"  class="bottone-azione" value='Nuovo' title='Nuovo' onclick="javascript:nuovoAppalto();">
					</c:if>
					<c:if test='${param.tipo eq 1 and gene:checkProtFunz(pageContext,"DEL","LISTADELSEL") and completato ne 1 and datiRiga.rowCount > 0}'>
						<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' title='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' onclick="javascript:listaEliminaSelezione()">
					</c:if>
	

				</c:otherwise>
			</c:choose>
			&nbsp;
		</td>
	</tr>
</table>

<gene:javaScript>
	
	document.getElementById("numeroAppalti").value = ${currentRow}+1;	

	function annulla(){
		document.forms[0].updateLista.value = "0";
		listaAnnullaModifica();
	}

	function pubblicazione(chiave,operazione){
		var id= chiave.substring(chiave.indexOf(":") + 1);
		var href = "href=gare/anticor/popupPubblicazione.jsp&id=" + id + "&operazione=" + operazione;
		openPopUpCustom(href, "Pubblicazione", "480", "250", "no", "no");
	}

	function aggiungiAppalto(chiave){
		var id= chiave.substring(chiave.indexOf(":") + 1);
		var href = "href=gare/anticor/popupAggiungiAppalto.jsp&id=" + id;
		openPopUpCustom(href, "AggiungiAppalto", "480", "250", "no", "no");
	}

	function AnnullaFiltro(){
		var comando = "href=gare/commons/popup-filtro.jsp&annulla=1";
		openPopUpCustom(comando, "impostaFiltro", 10, 10, "no", "no");
	}

	function impostaFiltro(){
		var comando = "href=gare/anticor/popup-trova-filtroAppalti.jsp";
		var risultatiPerPagina = document.forms[0].risultatiPerPagina.value;
		comando+="&appaltiPerPagina=" + risultatiPerPagina;
		openPopUpCustom(comando, "impostaFiltro", 850, 500, "yes", "yes");
	}

	function scaricaModello(){
		var chiave = "${gene:getValCampo(key,"ID")}";
		var annorif = "${annorif}";
		var href = "href=gare/anticor/popup-scarica-modello-excel.jsp&chiave=" + chiave + "&anno=" + annorif;
		openPopUpCustom(href, "scaricaModello", "450", "250", "no", "no");
	}

	function importaDati(){
		var chiave = "${gene:getValCampo(key,"ID")}";
		var annorif = "${annorif}";
		var href = "href=gare/anticor/popupImportaDati.jsp&chiave=" + chiave + "&anno=" + annorif;
		openPopUpCustom(href, "importaDati", "550", "450", "no", "no");
	}
	
	function importaDatiSAP(){
		var chiave = "${gene:getValCampo(key,"ID")}";
		var annorif = "${annorif}";
		var href = "href=gare/anticor/popupImportaDatiSAP.jsp&chiave=" + chiave + "&anno=" + annorif;
		openPopUpCustom(href, "importaDatiSAP", "550", "450", "no", "no");
	}

	function nuovoAppalto(){
		document.forms[0].metodo.value="nuovo";
		document.forms[0].activePage.value="0";
		var pagina = "${param.tipo }";
		document.forms[0].action += "&paginaAppalti=" + pagina;
		bloccaRichiesteServer();
		document.forms[0].submit();
	}
	
	function ricaricaLotti(chiave){
		var id= chiave.substring(chiave.indexOf(":") + 1);
		var href = "href=gare/anticor/popupRicaricaAdempimento.jsp&id=" + id;
		openPopUpCustom(href, "AggiungiAppalto", "550", "350", "no", "no");
	}
	
	function riportaAppalto(chiave,operazione){
		var id= chiave.substring(chiave.indexOf(":") + 1);
		var href = "href=gare/anticor/popupRiportaAppalto.jsp&id=" + id + "&operazione=" + operazione;
		openPopUpCustom(href, "riportaAppalto", "480", "250", "no", "no");
	}
	
	function listaVisualizzaAppalto(){
		var pagina = "${param.tipo }";
		document.forms[0].action += "&paginaAppalti=" + pagina;
		listaVisualizza();
	}
	
	function listaModificaAppalto(){
		var pagina = "${param.tipo }";
		document.forms[0].action += "&paginaAppalti=" + pagina;
		listaModifica();
	}
</gene:javaScript>
