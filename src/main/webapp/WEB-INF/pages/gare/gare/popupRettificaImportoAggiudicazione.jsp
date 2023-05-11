<%
/*
 * Created on: 05-03-2015
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

/*
	Descrizione:
		Finestra per la rettifica dei termini
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty requestScope.rettificaEseguita and requestScope.rettificaEseguita eq "1"}' >
<script type="text/javascript">
		opener.historyReload();
		window.close();
</script>
	</c:when>
	<c:otherwise>

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<gene:redefineInsert name="head">
	<script type="text/javascript" src="${contextPath}/js/date.js"></script> 
</gene:redefineInsert>

<c:set var="modo" value="MODIFICA" scope="request" />

	<c:choose>
		<c:when test='${!empty param.codgar}'>
			<c:set var="codgar" value="${param.codgar}" />
		</c:when>
		<c:otherwise>
			<c:set var="codgar" value="${codgar}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.ngara}'>
			<c:set var="ngara" value="${param.ngara}" />
		</c:when>
		<c:otherwise>
			<c:set var="ngara" value="${ngara}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.isOfferteUnica}'>
			<c:set var="isOfferteUnica" value="${param.isOfferteUnica}" />
		</c:when>
		<c:otherwise>
			<c:set var="isOfferteUnica" value="${isOfferteUnica}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.aqoper}'>
			<c:set var="aqoper" value="${param.aqoper}" />
		</c:when>
		<c:otherwise>
			<c:set var="aqoper" value="${aqoper}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.modlicg}'>
			<c:set var="modlicg" value="${param.modlicg}" />
		</c:when>
		<c:otherwise>
			<c:set var="modlicg" value="${modlicg}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${modlicg eq 17}'>
			<c:set var="ribassoTitolo" value="Rialzo" />
			<c:set var="ribasso" value="rialzo" />
		</c:when>
		<c:otherwise>
			<c:set var="ribassoTitolo" value="Ribasso" />
			<c:set var="ribasso" value="ribasso" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.impsic}'>
			<c:set var="impsic" value="${param.impsic}" />
		</c:when>
		<c:otherwise>
			<c:set var="impsic" value="${impsic}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.dettaglioCampoIaggiu}'>
			<c:set var="dettaglioCampoIaggiu" value="${param.dettaglioCampoIaggiu}" />
		</c:when>
		<c:otherwise>
			<c:set var="dettaglioCampoIaggiu" value="${dettaglioCampoIaggiu}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.cauzVis}'>
			<c:set var="cauzVis" value="${param.cauzVis}" />
		</c:when>
		<c:otherwise>
			<c:set var="cauzVis" value="${cauzVis}" />
		</c:otherwise>
	</c:choose>
	
	<gene:setString name="titoloMaschera" value='Rettifica importo di aggiudicazione' />
	<c:choose>
		<c:when test="${aqoper eq 2 }">
			<c:set var="messaggio" value="Oltre a impostare il nuovo importo di aggiudicazione è possibile allineare il ${ribasso}, dove previsto, e l'importo della garanzia per ogni aggiudicatario dell'accordo quadro." />
		</c:when>
		<c:otherwise>
			<c:set var="messaggio" value="Oltre a impostare il nuovo importo di aggiudicazione è possibile allineare" />
			<c:choose>
				<c:when test="${cauzVis eq 'true' }">
					<c:set var="messaggio" value="${messaggio } il ${ribasso}, dove previsto, e l'importo della garanzia." />
				</c:when>
				<c:otherwise>
					<c:set var="messaggio" value="${messaggio }, dove previsto, il ${ribasso}." />
				</c:otherwise>
			</c:choose>
		</c:otherwise>
	</c:choose>	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="GARE" where="GARE.NGARA='${ngara }'" gestisciProtezioni="false" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestorePopupRettificaImportoAggiudicazione" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupRettificaImportoAggiudicazione">
			
		<gene:campoScheda>
			<td colSpan="2">
				<br>
				<c:choose>
					<c:when test='${not empty requestScope.controlloSuperato and requestScope.controlloSuperato eq "NO"}' >
						${requestScope.msg }
					</c:when>
					<c:otherwise>
						Mediante questa funzione è possibile rettificare l'importo di aggiudicazione calcolato a sistema.
						<br>${messaggio}
					</c:otherwise>
				</c:choose>
				<br>&nbsp;
			</td>
		</gene:campoScheda>
		
		<gene:gruppoCampi visibile="${aqoper ne 2 }">
			<gene:campoScheda>
				<td colspan="2"><b>Aggiudicazione definitiva </b></td>
			</gene:campoScheda>
			<gene:campoScheda campo="DITTA" modificabile="false"/>
			<gene:campoScheda campo="NOMIMA" modificabile="false"/>
			<gene:campoScheda campo="RIBAGGINI" entita="GARE1" where="GARE.NGARA = GARE1.NGARA" visibile="false" />
			<gene:campoScheda title="${ribassoTitolo} di aggiudicazione iniziale" campo="RIBAGGINI_FIT" campoFittizio="true" modificabile='false' definizione="F13.9;0;;PRC" value="${gene:if(empty datiRiga.GARE1_RIBAGGINI, gene:if(modlicg eq 6,datiRiga.GARE_RIBOEPV,datiRiga.GARE_RIBAGG),datiRiga.GARE1_RIBAGGINI)}" />
			<gene:campoScheda campo="IAGGIUINI" entita="GARE1" where="GARE.NGARA = GARE1.NGARA" visibile="false"/>
			<gene:campoScheda campo="IAGGIUINI_FIT" title="Importo di aggiudicazione iniziale" campoFittizio="true" modificabile="false" definizione="F15;0;;MONEY" value="${gene:if(empty datiRiga.GARE1_IAGGIUINI, datiRiga.GARE_IAGGIU,datiRiga.GARE1_IAGGIUINI) }"/>
			<gene:campoScheda campo="RIBAGG" title="Nuovo ${ribasso} di aggiudicazione" visibile="${modlicg ne 6 }" modificabile="${modlicg ne 6  }" obbligatorio="true"/>
			<gene:campoScheda campo="RIBOEPV" title="Nuovo ${ribasso} di aggiudicazione" visibile="${modlicg eq 6 }" modificabile="${modlicg eq 6  }" obbligatorio="true"/>
			<gene:campoScheda campo="IAGGIU" title="Nuovo importo di aggiudicazione" obbligatorio="true"/>
		</gene:gruppoCampi>
		
		<gene:gruppoCampi  visibile="${aqoper ne 2 and cauzVis eq 'true'}">
			<gene:campoScheda >
				<td colspan="2"><b>Garanzia definitiva</b></td>
			</gene:campoScheda>
			<gene:campoScheda campo="RIDISO" modificabile="false"/>
			<gene:campoScheda campo="IMPGAR" />
		</gene:gruppoCampi>
		
		<c:if test="${numeroDitteAggiudicatarie > 0 }">
		<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
			<jsp:param name="entita" value='DITGAQ'/>
			<jsp:param name="chiave" value='${numeroGara};${modlicg}:${impsic }'/>
			<jsp:param name="nomeAttributoLista" value='listaDitteAggiudicatarie' />
			<jsp:param name="idProtezioni" value="DITGAQ" />
			<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/ditgaq/ditte-aggiudicatarie-rettifica.jsp"/>
			<jsp:param name="arrayCampi" value="'DITGAQ_ID_', 'DITGAQ_NGARA_', 'DITGAQ_DITTAO_','IMPR_NOMIMP_','DITGAQ_RIBAGG_', 'DITG_IMPOFF_', 'GARE_IMPSIC_','DITG_IMPPERM_', 'DITG_IMPCANO_', 'DITG_RICSUB_', 'DITGAQ_PUNTOT_', 'DITGAQ_IAGGIU_', 'DITGAQ_RIDISO_','DITGAQ_IMPGAR_','DITGAQ_RIBAGGINI_','DITGAQ_IAGGIUINI_','INIZ_RIB_','INIZ_IMP_'"/>		
			<jsp:param name="titoloSezione" value="Ditta aggiudicataria" />
			<jsp:param name="titoloNuovaSezione" value="Nuova ditta aggiudicataria" />
			<jsp:param name="descEntitaVociLink" value="ditta aggiudicataria" />
			<jsp:param name="msgRaggiuntoMax" value="e ditte aggiudicatarie"/>
			<jsp:param name="usaContatoreLista" value="true"/>
			<jsp:param name="sezioneEliminabile" value="false"/>
			<jsp:param name="sezioneInseribile" value="false"/>
		</jsp:include>
	</c:if>				
						
		<input type="hidden" name="codgar" id="codgar" value="${codgar}" />
		<input type="hidden" name="ngara" id="ngara" value="${ngara}" />
		<input type="hidden" name="isOfferteUnica" id="isOfferteUnica" value="${isOfferteUnica}" />
		<input type="hidden" name="aqoper" id="aqoper" value="${aqoper}" />
		<input type="hidden" name="modlicg" id="modlicg" value="${modlicg}" />
		<input type="hidden" name="impsic" id="impsic" value="${impsic}" />
		<input type="hidden" name="dettaglioCampoIaggiu" id="dettaglioCampoIaggiu" value="${dettaglioCampoIaggiu}" />
		<input type="hidden" name="cauzVis" id="cauzVis" value="${cauzVis}" />
	</gene:formScheda>
  </gene:redefineInsert>

<c:if test='${not empty requestScope.controlloSuperato and requestScope.controlloSuperato eq "NO"}' >
	<gene:redefineInsert name="buttons">
			<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
	</gene:redefineInsert>
</c:if>
	
	
	<gene:javaScript>
		
		var modlicg="${modlicg}";
		var aqoper="${aqoper}";
		
		if(aqoper!=2 && modlicg !=6)
			$( "#GARE_RIBAGG" ).after( " %&nbsp;" );
		if(aqoper!=2 && modlicg ==6)
			$( "#GARE_RIBOEPV" ).after( " %&nbsp;" );
		
		function conferma() {
			clearMsg();
			var continua = true;
			if(aqoper!="2"){
				var importoAgg = getValue("GARE_IAGGIU");
				var iaggiuini = getValue("IAGGIUINI_FIT");
				if(importoAgg==iaggiuini){
					outMsg("Il nuovo importo di aggiudicazione deve essere diverso dall'importo di aggiudicazione iniziale.", "ERR");
					onOffMsg();
					return;	
				}
				var ribaggini = getValue("RIBAGGINI_FIT");
				setValue("GARE1_RIBAGGINI",ribaggini);
				setValue("GARE1_IAGGIUINI",iaggiuini);
			}else{
				var ribagg;
				var ditta;
				var importoAgg;
				var importoAggIni;
				var numVariazioniImporto=0;
				for(var i=1; i < maxIdDITGAQVisualizzabile; i++){
					if(document.getElementById("rowtitoloDITGAQ_" + i).style.display != "none"){
						ribagg = getValue("DITGAQ_RIBAGG_" + i);
						ditta = getValue("DITGAQ_DITTAO_" + i);
						if((ribagg==null || ribagg == "") && (modlicg !=6)){
							outMsg('Il campo "Nuovo ${ribasso} di aggiudicazione" è obbligatorio per la ditta ' + ditta, "ERR");
							onOffMsg();
							continua = false;	
						}
						importoAgg = getValue("DITGAQ_IAGGIU_"+i);
						if(importoAgg==null || importoAgg ==""){
							outMsg('Il campo "Nuovo importo di aggiudicazione" è obbligatorio per la ditta ' + ditta, "ERR");
							onOffMsg();
							continua = false;
						}
						importoAggIni = getOriginalValue("DITGAQ_IAGGIUINI_"+i);
						if(importoAgg!=importoAggIni){
							numVariazioniImporto++;
						}	
					}
				}
				if(numVariazioniImporto==0){
					outMsg("Il nuovo importo di aggiudicazione deve essere diverso dall'importo di aggiudicazione iniziale per almeno una ditta.", "ERR");
					onOffMsg();
					continua = false;
				}
			}	
			if(continua){
				document.forms[0].jspPathTo.value="gare/gare/popupRettificaImportoAggiudicazione.jsp";
				schedaConferma();
			}
		}
			
		function annulla(){
			window.close();
		}
		
			<c:choose>
				<c:when test="${aqoper eq '2' }">
					for(var i=1; i < maxIdDITGAQVisualizzabile; i++){
						document.getElementById("DITGAQ_RIBAGG_" + i).onchange = aggiornaPerCambioRibassoAumento(modlicg);
					}
					
				</c:when>
				<c:otherwise>
				if(modlicg != 6){
					document.getElementById("GARE_RIBAGG").onchange = aggiornaPerCambioRibassoAumento(modlicg);
				}else{
					document.getElementById("GARE_RIBOEPV").onchange = aggiornaPerCambioRibassoAumento(modlicg);
				}	
				</c:otherwise>
			</c:choose>
			
		
		
		function aggiornaPerCambioRibassoAumento(modlicg){
			<c:choose>
				<c:when test="${aqoper eq '2' }">
					var objId = this.id;
					var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
					activeForm.getCampo("DITGAQ_RIBAGG_" + numeroRiga).setFnValidazione(validazioneRIBAUO);
				</c:when>
				<c:otherwise>
					if(modlicg != 6){
					 	activeForm.getCampo("GARE_RIBAGG").setFnValidazione(validazioneRIBAUO);
					}else{
						activeForm.getCampo("GARE_RIBOEPV").setFnValidazione(validazioneRIBAUO);
					}
				</c:otherwise>
			</c:choose>		
			
			callObjFn("local" + activeForm.getFormName(), 'onChange', this);
		}

		function validazioneRIBAUO(refVal, msg, obj){
			var result = false;
			if(checkFloat(refVal, msg, obj)){
				if(refVal != null && refVal.value != "" && refVal.value > 0 && ${requestScope.offaum != "1"})  
					refVal.setValue((-1) * refVal.value);

		<c:choose>
			<c:when test='${not empty numeroCifreDecimaliRibasso}'>
				var tmp = null;
				if(this.getValue() != null && this.getValue() != ""){
					// Controllo del numero di cifre decimali rispetto
					// (il campo RIBAUO e' definito in C0CAMPI come F13.9)
					var numeroCifreDecimaliRibasso = ${numeroCifreDecimaliRibasso};
					tmp = this.getValue();
					if(this.getValue().indexOf(".") >= 0){
						if(tmp.substr(tmp.indexOf(".") + 1).length <= numeroCifreDecimaliRibasso){
							result = true;
						} else {
							msg.setValue("Per il ${ribasso} è possibile indicare al piu' ${numeroCifreDecimaliRibasso} cifre decimali");
							sbiancaCampi = false;
						}
					} else {
						result = true;
					}
				}
			</c:when>
			<c:otherwise>
				result = true;
			</c:otherwise>
		</c:choose>
			}
			return result;
		}
		
	</gene:javaScript>
</gene:template>
</div>

	</c:otherwise>
</c:choose>