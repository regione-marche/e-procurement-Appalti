<%
/*
 * Created on: 19/11/2008
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

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GestioneDitteConcorrentiFunction" parametro="${key}" />

<gene:redefineInsert name="head">
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.pg.titoli.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.pg.hrefProtocollo.js"></script>	
</gene:redefineInsert>

<%/*La pagina può essere richiamata ancge da TORN per le gare divise a lotti con offerta unica*/ %>
<c:choose>
	<c:when test='${tipologiaGara eq "3"}'>
		<%/*gara divisa a lotti con offerta unica*/ %>
		<c:set var="whereGare" value='TORN.CODGAR = GARE.NGARA'/>
		<c:set var="whereTorn" value=''/>
		<c:set var="whereGare1" value='TORN.CODGAR = GARE1.NGARA'/>
		<c:set var="wherePermessi" value='G_PERMESSI.CODGAR = TORN.CODGAR'/>
		<c:set var="entita" value='TORN'/>
	</c:when>
	<c:otherwise>
		<c:set var="whereGare" value=''/>
		<c:set var="whereTorn" value='TORN.CODGAR = GARE.CODGAR1'/>
		<c:set var="whereGare1" value='GARE.CODGAR1 = GARE1.CODGAR1 AND GARE.NGARA = GARE1.NGARA'/>
		<c:set var="wherePermessi" value='G_PERMESSI.CODGAR = GARE.CODGAR1'/>
		<c:set var="entita" value='GARE'/>
	</c:otherwise>
</c:choose>

<c:set var="codiceGara" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetCodiceGaraFunction", pageContext)}' />
<c:set var="isAlboCommissioneCollegato" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsAlboCommissioneCollegatoFunction", pageContext, key)}' scope="request"/>
<c:set var="commissioneCompleta" value="false" />
<c:choose>
	<c:when test='${isAlboCommissioneCollegato eq "true"}'>
	<c:set var="isModificabile" value="false" />
	</c:when>
	<c:otherwise>
	<c:set var="isModificabile" value="true" />
	</c:otherwise>
</c:choose>

<c:set var="abilitazioneGare" value="${sessionScope.profiloUtente.abilitazioneGare}" />

<gene:formScheda entita="${entita}" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreCommissione" >
	<gene:redefineInsert name="schedaNuovo" />
	<gene:redefineInsert name="pulsanteNuovo" />
	
	<gene:redefineInsert name="schedaConferma">
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:schedaConfermaCommissione();" title="Salva modifiche" tabindex="1501">
					${gene:resource("label.tags.template.dettaglio.schedaConferma")}</a></td>
		</tr>
	</gene:redefineInsert>
	
	<gene:gruppoCampi idProtezioni="RICHNOMMIT">
		<gene:campoScheda>
			<td colspan="2"><b>Richiesta nomina al MIT</b></td>
		</gene:campoScheda>
			<gene:campoScheda campo="NRICHNOMINAMIT" entita="GARE1" where="${whereGare1}" />
			<gene:campoScheda campo="DRICHNOMINAMIT" entita="GARE1" where="${whereGare1}" />
	</gene:gruppoCampi>

	<gene:gruppoCampi idProtezioni="NOMINACOMM">
		<gene:campoScheda>
			<td colspan="2"><b>Nomina della commissione</b></td>
		</gene:campoScheda>
			<gene:campoScheda campo="MODAST" entita="TORN" where="${whereTorn}" />
			<gene:campoScheda campo="MODGAR" entita="TORN" where="${whereTorn}" />
			<gene:campoScheda campo="TATTOC" entita="TORN" where="${whereTorn}" />
			<gene:campoScheda campo="CODGAR" entita="TORN" where="${whereTorn}" visibile="false" />
			<gene:campoScheda campo="NPNOMINACOMM" entita="TORN" where="${whereTorn}"/>
			<gene:campoScheda campo="GARTEL" entita="TORN" where="${whereTorn}" visibile="false"/>
		    <gene:campoScheda campo="DRICESP" entita="GARE" where = "${whereGare}"/>
		    <c:if test='${isAlboCommissioneCollegato eq "true"}'>
		     <c:choose>
		    	<c:when test='${tipologiaGara eq "3"}'>
				<gene:campoScheda campo="NOTCOMM" entita="GARE1" from="GARE" where="GARE1.CODGAR1 = TORN.CODGAR AND TORN.CODGAR = GARE.NGARA" />
				</c:when>
				<c:otherwise>
					<gene:campoScheda campo="NOTCOMM" entita="GARE1" from="TORN" where="GARE1.NGARA = GARE.NGARA AND TORN.CODGAR = GARE.CODGAR1" />
				</c:otherwise>
			 </c:choose>
			</c:if>
	</gene:gruppoCampi>

	<gene:gruppoCampi idProtezioni="PAGACOMM">
		<gene:campoScheda>
			<td colspan="2"><b>Pagamento della commissione</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="NGARA" entita="GARE" where = "${whereGare}" visibile="false" />
		<gene:campoScheda campo="CODGAR1" entita="GARE" where = "${whereGare}" visibile="false" />
		<gene:campoScheda campo="IMPCOM" entita="GARE" where = "${whereGare}"/>
		<gene:campoScheda campo="IMPLIQ" entita="GARE" where = "${whereGare}"/>
		<gene:campoScheda campo="DATLIQ" entita="GARE" where = "${whereGare}"/>
		<gene:campoScheda campo="DLETCOM" entita="GARE1" where="${whereGare1}" />
		<gene:campoScheda campo="NPLETCOM" entita="GARE1" where="${whereGare1}" />
	</gene:gruppoCampi>

	<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GestioneComponentiCommissioneFunction" parametro='${fn:substringAfter(key, ":")}' />
	<c:set var="integrazioneMEval" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneAppMEVALFunction", pageContext, datiRiga.GARE_NGARA, datiRiga.GARE_CODGAR1)}' />
		
	
	<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
		<jsp:param name="entita" value='GFOF'/>
		<jsp:param name="chiave" value='${fn:substringAfter(key, ":")}'/>
		<jsp:param name="nomeAttributoLista" value='commissione' />
		<jsp:param name="idProtezioni" value="GFOF" />
		<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/gfof/componente-commissione.jsp" />
		<jsp:param name="arrayCampi" value="'GFOF_CODFOF_', 'GFOF_NOMFOF_', 'GFOF_INCFOF_', 'GFOF_INTFOF_', 'GFOF_IMPFOF_', 'GFOF_IMPLIQ_', 'GFOF_IMPSPE_', 'GFOF_DLIQSPE_', 'GFOF_INDISPONIBILITA_', 'GFOF_MOTIVINDISP_', 'GFOF_DATARICHIESTA_', 'GFOF_DATAACCETTAZIONE_', 'GFOF_ESPGIU_', 'GFOF_ID_', 'GFOF_SEZALBO_', 'GFOF_COMMICG_'" />
		<jsp:param name="arrayVisibilitaCampi" value="true, true, true, true, false, false, false, false, true, false, false, false, true, false, true, true" />
		<jsp:param name="usaContatoreLista" value="true" />
		<jsp:param name="titoloSezione" value="Componente commissione" />
		<jsp:param name="funzEliminazione" value="delComponente" />
		<jsp:param name="titoloNuovaSezione" value="Nuovo componente commissione" />
		<jsp:param name="descEntitaVociLink" value="componente della commissione" />
		<jsp:param name="msgRaggiuntoMax" value="i componenti della commissione" />
		<jsp:param name="sezioneInseribile" value="${isModificabile}"/>
		<jsp:param name="sezioneEliminabile" value="${isModificabile}"/>
		<jsp:param name="integrazioneMEval" value="${integrazioneMEval}"/>
		<jsp:param name="obbligoPresidente" value="${obbligoPresidente}"/>
	</jsp:include>

	<gene:gruppoCampi idProtezioni="ESAMECOMM">
	<gene:campoScheda>
		<td colspan="2"><b>Esame della commissione</b></td>
	</gene:campoScheda>
	<gene:campoScheda campo="DINVDOCTEC" entita="GARE" where = "${whereGare}"/>
	<gene:campoScheda campo="DCONVDITTE" entita="GARE" where = "${whereGare}"/>
	</gene:gruppoCampi>
		
	<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GestioneVerbaleCommissioneFunction" parametro='${fn:substringAfter(key, ":")}' />
	<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
		<jsp:param name="entita" value='COMMVERB'/>
		<jsp:param name="chiave" value='${fn:substringAfter(key, ":")}'/>
		<jsp:param name="nomeAttributoLista" value='verbaliCommissione' />
		<jsp:param name="idProtezioni" value="COMMVERB" />
		<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/commverb/verbale-commissione.jsp" />
		<jsp:param name="arrayCampi" value="'COMMVERB_NUM_', 'COMMVERB_DVERB_', 'COMMVERB_NOTE_'" />
		<jsp:param name="usaContatoreLista" value="true" />
		<jsp:param name="titoloSezione" value="Verbale della commissione" />
		<jsp:param name="titoloNuovaSezione" value="Nuovo verbale della commissione" />
		<jsp:param name="descEntitaVociLink" value="verbale della commissione" />
		<jsp:param name="msgRaggiuntoMax" value="i verbali della commissione" />
	</jsp:include>	


	<gene:campoScheda>
		<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-scheda.jsp">
			<jsp:param name="entita" value="V_GARE_TORN"/>
			<jsp:param name="inputFiltro" value="CODGAR=T:${datiRiga.GARE_CODGAR1}"/>
			<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
		</jsp:include>
		<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
		<gene:redefineInsert name="pulsanteNuovo" />
	</gene:campoScheda>
	
	<gene:campoScheda campo="AUTORI" entita="G_PERMESSI" where="${wherePermessi} AND G_PERMESSI.SYSCON = ${sessionScope.profiloUtente.id}" visibile="false"/>
	<gene:campoScheda campo="PROPRI" entita="G_PERMESSI" where="${wherePermessi} AND G_PERMESSI.SYSCON = ${sessionScope.profiloUtente.id}" visibile="false"/>

	<gene:redefineInsert name="addToAzioni" >
		<c:if test='${autorizzatoModifiche ne "2" and garaLottoUnico eq "false" and modoAperturaScheda eq "VISUALIZZA" and tipologiaGara ne "3" and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE-scheda.COMMIS.CopiaCommissione") and isAlboCommissioneCollegato ne "true"}'>
			<tr>
				<td class="vocemenulaterale" >
					<c:if test='${isNavigazioneDisattiva ne "1"}'>
						<a href="javascript:apriPopupCopiaCommissione()" title="Copia commissione" tabindex="1505">
					</c:if>
						Copia commissione
					<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
				</td>
			</tr>
		</c:if>
		<c:if test='${autorizzatoModifiche ne "2" and modoAperturaScheda eq "VISUALIZZA" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.EstrazioneComponentiCommissione") and isAlboCommissioneCollegato eq "true"}'>
			<tr>
				<td class="vocemenulaterale"><a
					href="javascript:SelAlboCompoComm();"
					title='Estrazione componenti commissione da elenco' tabindex="1506"> Estrazione componenti commissione da elenco</a></td>
			</tr>
		</c:if>
		<c:if test='${modoAperturaScheda eq "VISUALIZZA" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.CondividiGaraPermessiCommissione")}'>
			<td class="vocemenulaterale">
				<c:if test='${true}'>
					<a href="javascript:apriGestionePermessiGaraCommissione('${datiRiga.G_PERMESSI_PROPRI}');" title="Condividi gara componenti commiss." tabindex="1503">
				</c:if>
				Condividi gara componenti commiss. 
			</td>
		</c:if>
		<c:if test='${autorizzatoModifiche ne "2" and modoAperturaScheda eq "VISUALIZZA" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.COMMISSIONE.AbilitaValutazioneMEval") and integrazioneMEval eq "1"}'>
			<tr>
			<td class="vocemenulaterale">
				<a href="javascript:apriAbilitazioneValutazioneMEval();" title="Abilita valutazione su M-Eval" tabindex="1504">
				Abilita valutazione su M-Eval
				</a> 
			</td>
			</tr>
		</c:if>
	</gene:redefineInsert>
	<gene:redefineInsert name="pulsanteSalva">
			<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:schedaConfermaCommissione();">
	</gene:redefineInsert>

	<gene:javaScript>
		<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.AutovieArchiflow")}'>
			addHrefs();
		</c:if>
	
	
		function visualizzaImporti(progressivoComponente){
			if(getValue("GFOF_INTFOF_" + progressivoComponente) == "2"){
				showObj("rowGFOF_IMPFOF_" + progressivoComponente, true);
				showObj("rowGFOF_IMPLIQ_" + progressivoComponente, true);
				showObj("rowGFOF_IMPSPE_" + progressivoComponente, true);
				showObj("rowGFOF_DLIQSPE_" + progressivoComponente, true);
			} else {
				showObj("rowGFOF_IMPFOF_" + progressivoComponente, false);
				showObj("rowGFOF_IMPLIQ_" + progressivoComponente, false);		
				showObj("rowGFOF_IMPSPE_" + progressivoComponente, false);
				showObj("rowGFOF_DLIQSPE_" + progressivoComponente, false);
			}
		}
		
		
		function apriPopupCopiaCommissione() {
			var href = "href=gare/gare/gare-popup-copia-commissione.jsp?codgar="+getValue("GARE_CODGAR1")+"&lottoSorgente="+getValue("GARE_NGARA");
			openPopUpCustom(href, "copiaCommissione", 600, 450, "yes", "yes");
		}
		
		function schedaConfermaCommissione(){
			var continua = true;
			
			//Controllo sull'unicità del componente commissione
			for(var i=1; i < maxIdGFOFVisualizzabile; i++){
				var codiceComponente = getValue("GFOF_CODFOF_" + i);
				var incaricoComponente = getValue("GFOF_INCFOF_" + i);
				var espgiuComponente = getValue("GFOF_ESPGIU_" + i);
				if(document.getElementById("rowtitoloGFOF_" + i) && (document.getElementById("rowtitoloGFOF_" + i).style.display != "none")){		
					if(codiceComponente != null && codiceComponente != ""){
						
						for(var jo=(i+1); jo <= maxIdGFOFVisualizzabile; jo++){
							if(incaricoComponente != null && incaricoComponente != ""){
								if(document.getElementById("rowtitoloGFOF_" + jo).style.display != "none" && codiceComponente == getValue("GFOF_CODFOF_" + jo) && incaricoComponente == getValue("GFOF_INCFOF_" + jo)){
									continua = false;
									outMsg("Sono stati definiti pi&ugrave; componenti commissione con lo stesso riferimento anagrafico (cod. " + codiceComponente + ") e lo stesso incarico", "ERR");
									onOffMsg();
							 	 }
						 	 }
							if(espgiuComponente != null && espgiuComponente != ""){
								if(document.getElementById("rowtitoloGFOF_" + jo).style.display != "none" && codiceComponente == getValue("GFOF_CODFOF_" + jo) && espgiuComponente == getValue("GFOF_ESPGIU_" + jo)){
									continua = false;
									outMsg("Sono stati definiti pi&ugrave; componenti commissione con lo stesso riferimento anagrafico (cod. " + codiceComponente + ") e tenuti a esprimere giudizio ", "ERR");
									onOffMsg();
							 	 }
						 	 }
					 	 }
				  	}
			 	 }
			  }

			
			
			if(continua){
			  schedaConferma();
			}
		}
		
		function SelAlboCompoComm(){
		
		<c:choose>
		<c:when test="${numeroComponenti == 0}">
			alert("Non è possibile selezionare i componenti \"Numero minimo componenti da selezionare\"");
		</c:when>
		<c:otherwise>
			var chiave="${key }";
			chiave= chiave.substr(chiave.lastIndexOf(":") + 1);
			var codgar=getValue("GARE_CODGAR1");
			href = "href=gare/commons/popup-creaCommissioneDaAlbo.jsp";
			href += "&codgar=" + codgar;
			href += "&ngara=" + chiave;
			<c:choose>
				<c:when test='${tipologiaGara eq "3"}'>
					href += "&garaLottiConOffertaUnica=true";
				</c:when>
				<c:otherwise>
					href += "&garaLottiConOffertaUnica=false";
				</c:otherwise>
			</c:choose>
			openPopUpCustom(href, "creaCommissione", 650, 350, "yes", "yes");
		
		</c:otherwise>
		</c:choose>
		}
		
		
		function visualizzaIndisponibilita(progressivoComponente,modo){
			if(getValue("GFOF_INDISPONIBILITA_" + progressivoComponente) == "1"){
				showObj("rowGFOF_MOTIVINDISP_" + progressivoComponente, true);
				showObj("rowGFOF_DATARICHIESTA_" + progressivoComponente, true);
				showObj("rowGFOF_DATAACCETTAZIONE_" + progressivoComponente, true);
				<c:if test="${modo eq 'MODIFICA'}">
					document.getElementById("GFOF_ESPGIU_" + progressivoComponente).getElementsByTagName('option')[2].selected = 'selected'
				</c:if>			
				showObj("rowGFOF_ESPGIU_" + progressivoComponente, false);
				if(getValue("GFOF_DATAACCETTAZIONE_" + progressivoComponente) != ''){
					<c:if test="${modo eq 'MODIFICA' and isAlboCommissioneCollegato eq 'true'}">
						bloccaIndisponibilita(progressivoComponente);
					</c:if>					
				}
			} else {
				setValue("GFOF_MOTIVINDISP_" + progressivoComponente, "");
				showObj("rowGFOF_MOTIVINDISP_" + progressivoComponente, false);
				setValue("GFOF_DATARICHIESTA_" + progressivoComponente, "");
				showObj("rowGFOF_DATARICHIESTA_" + progressivoComponente, false);
				setValue("GFOF_DATAACCETTAZIONE_" + progressivoComponente, "");
				showObj("rowGFOF_DATAACCETTAZIONE_" + progressivoComponente, false);
				showObj("rowGFOF_ESPGIU_" + progressivoComponente, true);
			}
		}
		
		function bloccaIndisponibilita(indice){
		    var valInd= $("#GFOF_INDISPONIBILITA_"+indice+" option:selected").text();
			$("#GFOF_INDISPONIBILITA_"+indice).parent().prepend("<span id='GFOF_INDISPONIBILITA_'+indice+'view' title='Indisponibilità?'>"+valInd+"</span>");				
			$("select#GFOF_INDISPONIBILITA_"+indice).remove();
			var valMot = $("#GFOF_MOTIVINDISP_"+indice).text();
			$("#GFOF_MOTIVINDISP_"+indice).parent().prepend("<span id='GFOF_MOTIVINDISP_'+indice+'view' title='Motivazioni indisponibilità'>"+valMot+"</span>");
			$("#GFOF_MOTIVINDISP_"+indice).remove();
			var valDrich = $("#GFOF_DATARICHIESTA_"+indice).val();
			$("#GFOF_DATARICHIESTA_"+indice).parent().prepend("<span id='GFOF_DATARICHIESTA_'+indice+'view' title='Data richiesta indisponibilità'>"+valDrich+"</span>");
			$("#GFOF_DATARICHIESTA_"+indice).remove();
			var valDacc = $("#GFOF_DATAACCETTAZIONE_"+indice).val();
			$("#GFOF_DATAACCETTAZIONE_"+indice).parent().prepend("<span id='GFOF_DATAACCETTAZIONE_'+indice+'view' title='Data richiesta indisponibilità'>"+valDacc+"</span>");
			$("#GFOF_DATAACCETTAZIONE_"+indice).remove();
		}
		
		function apriGestionePermessiGaraCommissione(propri) {
			bloccaRichiesteServer();
			var abilitazioneGare = "${abilitazioneGare}";
			if(propri == '1' || abilitazioneGare == 'A'){
				formVisualizzaPermessiGaraCommissione.permessimodificabili.value = 'true';
			}else{
				formVisualizzaPermessiGaraCommissione.permessimodificabili.value = 'false';
			}
			formVisualizzaPermessiGaraCommissione.codgar.value = getValue("GARE_CODGAR1");
			formVisualizzaPermessiGaraCommissione.ngara.value = getValue("GARE_NGARA");
			formVisualizzaPermessiGaraCommissione.gartel.value = getValue("TORN_GARTEL");
			formVisualizzaPermessiGaraCommissione.submit();
		}
		
		function apriAbilitazioneValutazioneMEval(){
			var href = "href=gare/gare/gare-popup-abilitaValutazione-MEval.jsp?codgar="+getValue("GARE_CODGAR1")+"&lotto="+getValue("GARE_NGARA")+"&genere=${tipologiaGara}";
			openPopUpCustom(href, "abilitaValutazione", 700, 450, "yes", "yes");
		}
		
	</gene:javaScript>

</gene:formScheda>
	<form name="formVisualizzaPermessiGaraCommissione" action="${pageContext.request.contextPath}/pg/VisualizzaPermessiUtentiCommissione.do" method="post">
		<input type="hidden" name="metodo" id="metodo" value="apri" />
		<input type="hidden" name="codgar" id="codgar" value="" />
		<input type="hidden" name="ngara" id="ngara" value="" />
		<input type="hidden" name="gartel" id="gartel" value="" />
		<input type="hidden" name="genereGara" id="genereGara" value="${tipologiaGara}" />
		<input type="hidden" name="permessimodificabili" id="permessimodificabili" value="" />
		<input type="hidden" name="codein" id="codein" value="${sessionScope.uffint}" />
	</form>