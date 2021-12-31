<%
/*
 * Created on: 04/09/2013
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

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>

<fmt:setBundle basename="AliceResources" />

<c:if test='${modo ne "NUOVO" }'>
	<c:set var="completato" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetCompletatoFunction", pageContext, key)}' />
</c:if>

<c:if test='${modo eq "NUOVO" && !empty sessionScope.uffint}'>
	<c:set var="risultato" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetDatiUfficioIntestatarioFunction", pageContext, sessionScope.uffint)}/'/>
</c:if>


<c:choose>
	<c:when test='${not empty param.paginaAppalti}'>
		<c:set var="paginaAppalti" value="${param.paginaAppalti}" />
	</c:when>
	<c:when test='${not empty requestScope.pagina}'>
		<c:set var="paginaAppalti" value="${requestScope.paginaAppalti}" />
	</c:when>
	<c:otherwise>
		<c:set var="paginaAppalti" value="${paginaAppalti}" />
	</c:otherwise>
</c:choose>

<gene:redefineInsert name="head">
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>
</gene:redefineInsert>

<gene:formScheda entita="ANTICORLOTTI" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreANTICORLOTTI"
	plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreInitANTICORLOTTI" >
	
	<gene:redefineInsert name="addToAzioni" >
		<c:if test='${modo eq "VISUALIZZA" and completato eq "2" and paginaAppalti eq "1" and !empty datiRiga.ANTICORLOTTI_IDLOTTO and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.ANTICORLOTTI.ricaricaDati")}'>
			<tr>
				<td class="vocemenulaterale">
					<a href="javascript:ricaricaDati();" title='Ricarica lotto da dati correnti' tabindex="1502">
						Ricarica lotto da dati correnti
					</a>
				</td>
			</tr>
	</c:if>
	</gene:redefineInsert>
		
	<c:set var="msgChiaveErrore">	
		<fmt:message key="match.tags.template.dettaglio.campoChiave.messaggio">
			<fmt:param value="$"/>
		</fmt:message>
	</c:set>

	<c:set var="msgChiaveErrore" value="${fn:replace(msgChiaveErrore, '\\\\', '')}" />	
		
		<gene:campoScheda>
			<td colspan="2"><b>Dati generali</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="ID" visibile="false"/>
		<gene:campoScheda campo="INVIABILE"  modificabile='false'/>
		<gene:campoScheda campo="CIG" obbligatorio='${modo eq "NUOVO"}' modificabile='${modo eq "NUOVO"}' />
	<c:choose>
		<c:when test='${fn:startsWith(datiRiga.ANTICORLOTTI_CIG,"#") or fn:startsWith(datiRiga.ANTICORLOTTI_CIG,"$") or fn:startsWith(datiRiga.ANTICORLOTTI_CIG,"NOCIG")}'>
			<gene:campoScheda campo="CIG_FIT" title="Codice CIG fittizio" campoFittizio="true" value="${datiRiga.ANTICORLOTTI_CIG}" definizione="T10;;;;CIGANTICORL" modificabile="false" />
		</c:when>
		<c:otherwise>
			<gene:campoScheda campo="CIG_FIT" title="Codice CIG fittizio" campoFittizio="true" value="" definizione="T10;;;;CIGANTICORL" modificabile="false"  />
		</c:otherwise>
	</c:choose>

		<gene:campoScheda campo="ESENTE_CIG" campoFittizio="true" computed="true" title="Esente CIG?" definizione="T10;;;SN" defaultValue="2" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoSiNoSenzaNull"  modificabile='${modo eq "NUOVO"}' />		
		<gene:fnJavaScriptScheda funzione="gestioneEsenteCIG()" elencocampi="ESENTE_CIG" esegui="false" />
		
		<gene:campoScheda campo="CODFISCPROP" defaultValue="${requestScope.codfiscUffint}"/>
		<gene:campoScheda campo="DENOMPROP" defaultValue="${requestScope.denomUffint}"/>
		<gene:campoScheda campo="OGGETTO" />
		<gene:campoScheda campo="STATO" modificabile='${modo eq "NUOVO" or (modo eq "MODIFICA" and datiRiga.ANTICORLOTTI_LOTTOINBO eq "2")}' obbligatorio="true"/>
		<gene:campoScheda campo="SCELTACONTR" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoSceltacontr"/>
		<gene:campoScheda campo="IMPAGGIUDIC" />
		<gene:campoScheda campo="DATAINIZIO" />
		<gene:campoScheda campo="DATAULTIMAZIONE" />
		<gene:campoScheda campo="IMPSOMMELIQ" />
		<gene:campoScheda campo="PUBBLICA" visibile="false" />
		<gene:campoScheda campo="DAANNOPREC" visibile="false" />
		<gene:campoScheda campo="IDANTICOR" visibile="false" defaultValue='${gene:getValCampo(keyParent, "ANTICOR.ID")}'/>
		<gene:campoScheda campo="LOTTOINBO" visibile="false" defaultValue='2'/>
		<gene:campoScheda campo="IDLOTTO" visibile="${not empty datiRiga.ANTICORLOTTI_IDLOTTO }" modificabile="false"/>
		<gene:campoScheda campo="IDCONTRATTO" visibile="${not empty datiRiga.ANTICORLOTTI_IDCONTRATTO}" modificabile="false"/>
		<gene:campoScheda campo="TESTOLOG" visibile="${datiRiga.ANTICORLOTTI_INVIABILE eq 2}" modificabile="false"/>
		
		<gene:campoScheda>
			<td colspan="2"><b>Utente responsabile</b></td>
		</gene:campoScheda>
<c:choose>
	<c:when test='${sessionScope.profiloUtente.abilitazioneGare eq "A"}'>
		<gene:campoScheda campo="CODFISRESP" modificabile='${sessionScope.profiloUtente.abilitazioneGare eq "A"}' />
		<gene:campoScheda campo="NOMERESP" modificabile='${sessionScope.profiloUtente.abilitazioneGare eq "A"}' />
	</c:when>
	<c:otherwise>
		<gene:campoScheda campo="CODFISRESP" modificabile="false" defaultValue="${sessionScope.profiloUtente.codiceFiscale}" />
		<gene:campoScheda campo="NOMERESP" modificabile="false" defaultValue="${requestScope.nomeTecnico}"/>
	</c:otherwise>
</c:choose>

		<input type="hidden" name="paginaAppalti" id="paginaAppalti" value="${paginaAppalti}"/>
		
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
				<c:if test="${paginaAppalti eq '1' and completato ne '1'}">
					<gene:insert name="pulsanteModifica">
						<c:if test='${gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD")}'>
							<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:schedaModifica()">
						</c:if>
					</gene:insert>
				</c:if>
			</c:otherwise>
			</c:choose>
			&nbsp;
		</td>
	</gene:campoScheda>
		
	<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
	<c:if test="${paginaAppalti ne '1' or completato eq '1'}">
		<gene:redefineInsert name="schedaModifica"></gene:redefineInsert>
		
	</c:if>
</gene:formScheda>

<gene:javaScript>
 	
 	function ricaricaDati() {
 		var id = getValue("ANTICORLOTTI_ID");
 		var idAnticor= getValue("ANTICORLOTTI_IDANTICOR");
		var idlotto = getValue("ANTICORLOTTI_IDLOTTO");
		var href = "href=gare/anticorlotti/popup-ricaricaDati.jsp&idAnticorLotti=" + id +"&idAnticor=" + idAnticor; 
		href+="&idLotto=" + idlotto + "&numeroPopUp=1";;
		openPopUpCustom(href, "ricaricaDati", "450", "250", "no", "no");
 	}
 	
 	var selezionaPaginaDefault = selezionaPagina;
	var selezionaPagina = selezionaPaginaCustom;
	
	function selezionaPaginaCustom(pageNumber) {
		var paginaAppalti="${paginaAppalti }"
		document.pagineForm.action += "&paginaAppalti=" + paginaAppalti;
		selezionaPaginaDefault(pageNumber);
	}
		
	if(getValue("ANTICORLOTTI_INVIABILE") == 2) {
		$("#ANTICORLOTTI_TESTOLOGview").css('color', 'red');
	}

<c:if test='${modo eq "NUOVO" and sessionScope.profiloUtente.abilitazioneGare eq "U"}'>
	document.getElementById("ANTICORLOTTI_CODFISRESP").disabled = true;
	document.getElementById("ANTICORLOTTI_NOMERESP").disabled = true;
</c:if>

<c:if test='${modo ne "VISUALIZZA"}'>
 	
 	$("#ANTICORLOTTI_CIG").css({'text-transform': 'uppercase' });
 	
	$(function() {
	    $('#ANTICORLOTTI_CIG').change(function() {
				if (!controllaCIG("ANTICORLOTTI_CIG")) {
					this.focus();
					alert("Codice CIG non valido");
				}
	    });
	});

</c:if>

	function initEsenteCIG_CODCIG() {
		var esenteCig = getValue("ESENTE_CIG");
		var codcig = getValue("ANTICORLOTTI_CIG");
		//alert("esente CIG = " + esenteCig);
<c:choose>
	<c:when test='${modo eq "VISUALIZZA"}'>
		if ("" != codcig) {
			if (codcig.indexOf("#") == 0 || codcig.indexOf("$") == 0 || codcig.indexOf("NOCIG") == 0) {
				setValue("ESENTE_CIG", "Si", false);
				showObj("rowCIG_FIT", true);
				showObj("rowANTICORLOTTI_CIG", false);
			}	else {
				setValue("ESENTE_CIG", "No", false);
				showObj("rowCIG_FIT", false);
				showObj("rowANTICORLOTTI_CIG", true);
			}
		} else {
			setValue("ESENTE_CIG", "No", false);
			showObj("rowCIG_FIT", false);
			showObj("rowANTICORLOTTI_CIG", true);
		}
	</c:when>
	<c:when test='${modo eq "MODIFICA"}'>
			if ("" != codcig) {
				if (codcig.indexOf("#") == 0 || codcig.indexOf("$") == 0 || codcig.indexOf("NOCIG") == 0) {
					setValue("ESENTE_CIG", "Si", false);
					showObj("rowCIG_FIT", true);
					showObj("rowANTICORLOTTI_CIG", false);
				} else {
					setValue("ESENTE_CIG", "No", false);
					showObj("rowCIG_FIT", false);
					showObj("rowANTICORLOTTI_CIG", true);
				}
			} else {
				setValue("ESENTE_CIG", "No", false);
				showObj("rowCIG_FIT", false);
				showObj("rowANTICORLOTTI_CIG", true);
			}
	</c:when>
	<c:otherwise>
			setValue("ESENTE_CIG", "2", false);
			showObj("rowCIG_FIT", false);
			showObj("rowANTICORLOTTI_CIG", true);
	</c:otherwise>
</c:choose>
	}

	function gestioneEsenteCIG() {
	<c:if test='${modo ne "VISUALIZZA"}'>
		var esenteCig = getValue("ESENTE_CIG");
		var codcig = getValue("ANTICORLOTTI_CIG");
		//alert("esente CIG = " + esenteCig);
		if ("1" == esenteCig) {
			showObj("rowANTICORLOTTI_CIG", false);
			//setValue("ANTICORLOTTI_CIG", "", false);
			if (getOriginalValue("CIG_FIT") == getValue("CIG_FIT")) {
				setValue("CIG_FIT", "", false);
			} else {
				setValue("CIG_FIT", getOriginalValue("CIG_FIT"), false);
			}
			showObj("rowCIG_FIT", true);
		} else {
			showObj("rowANTICORLOTTI_CIG", true);
			showObj("rowCIG_FIT", false);
			setValue("CIG_FIT", "", false);
		}
	</c:if>
	}
 	
	var schedaConfermaDefault = schedaConferma;
	function schedaConfermaCustom() {
		setValue("ANTICORLOTTI_CIG", getValue("ANTICORLOTTI_CIG").toUpperCase(), false);

		var esenteCig = getValue("ESENTE_CIG");
		if (esenteCig == "2") {
			if (!controllaCIG("ANTICORLOTTI_CIG")) {
				outMsg("Codice CIG non valido", "ERR");
				onOffMsg();
				return;
			}
		} else {
			//setValue("ANTICORLOTTI_CIG", getValue("CIG_FIT").toUpperCase());
		}

		document.getElementById("ANTICORLOTTI_CODFISRESP").disabled = false;
		document.getElementById("ANTICORLOTTI_NOMERESP").disabled = false;
		schedaConfermaDefault();
	}
		
	schedaConferma = schedaConfermaCustom;
 	
 	initEsenteCIG_CODCIG();

</gene:javaScript>