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

<%/* Verifico se si può modificare il CODICE NUTS */%>
<c:choose>
	<c:when test='${gene:checkProtObj(pageContext,"COLS.MOD","GARE.TORN.CODNUTS") && not empty modo and modo ne "VISUALIZZA" }'>
		<c:set var="modCODNUTS" value="true" scope="request"/>
	</c:when>
	<c:otherwise>
		<c:set var="modCODNUTS" value="false" scope="request"/>
	</c:otherwise>
</c:choose>

	<gene:gruppoCampi idProtezioni="COMPBANDO" visibile="${param.tornata || param.garaLottoUnico}">
		<gene:campoScheda nome="COMPBANDO">
			<td colspan="2"><b>Dati complementari per la presentazione della domanda di partecipazione o dell'offerta</b></td>
		</gene:campoScheda>
		<c:choose>
			<c:when test="${param.datiModificabili }">
				<gene:campoScheda campo="CODNUTS" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" href="#" speciale="true" >
					<gene:popupCampo titolo="Dettaglio codice NUTS" href="#" />
				</gene:campoScheda> 
			</c:when>
			<c:otherwise>
				<gene:campoScheda campo="CODNUTS" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" />
			</c:otherwise>
		</c:choose>
		<c:if test="${param.tornata || param.garaLottoUnico }">
			<gene:campoScheda campo="MODREA" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" modificabile="${param.datiModificabili }" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoModrea"/>
		</c:if>
		<gene:campoScheda campo="SELPAR" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" modificabile="${param.datiModificabili }"/> 
		<gene:campoScheda campo="OFFLOT" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" visibile="${!param.garaLottoUnico}" modificabile="${param.datiModificabili }"/> 
		<gene:campoScheda campo="NOFDIT" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" visibile="${!param.garaLottoUnico}" modificabile="${param.datiModificabili }"/> 
		<gene:campoScheda campo="NGADIT" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" visibile="${!param.garaLottoUnico}" modificabile="${param.datiModificabili }"/> 
		<gene:campoScheda campo="MINOPE" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" modificabile="${param.datiModificabili }"/> 
		<gene:campoScheda campo="MAXOPE" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" modificabile="${param.datiModificabili }"/> 
		<gene:campoScheda campo="SELOPE" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" modificabile="${param.datiModificabili }"/> 
		<gene:campoScheda campo="AMMVAR" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" modificabile="${param.datiModificabili }"/> 
		<gene:campoScheda campo="PROFAS" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" modificabile="${param.datiModificabili }"/> 
		<gene:campoScheda campo="ELEPAR" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" modificabile="${param.datiModificabili }"/> 
		<gene:campoScheda campo="DATDOC" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" modificabile="${param.datiModificabili }"/> 
		<gene:campoScheda campo="ORADOC" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" modificabile="${param.datiModificabili }"/> 
		<gene:campoScheda campo="IMPDOC" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" modificabile="${param.datiModificabili }"/> 
		<gene:campoScheda campo="PAGDOC" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" modificabile="${param.datiModificabili }"/> 
		<gene:campoScheda campo="MODFIN" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" modificabile="${param.datiModificabili }"/> 
		<gene:campoScheda campo="APFINFC" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" modificabile="${param.datiModificabili }"/>
		<gene:campoScheda campo="PROGEU" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" modificabile="${param.datiModificabili }"/> 
		<gene:campoScheda campo="ACCAPPUB" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" modificabile="${param.datiModificabili }"/>

	</gene:gruppoCampi>
			
	

	<gene:fnJavaScriptScheda funzione="setVisibilitaDaTipoProcedura('#${param.campoTipoProcedura}#')" elencocampi="${param.campoTipoProcedura}" esegui="true"/>
	<gene:fnJavaScriptScheda funzione="setVisibilitaELEPAR('#${param.campoModalitaAggiudicazione}#','#${param.campoTipoProcedura}#')" elencocampi="" esegui="true"/>
	<% /*<gene:fnJavaScriptScheda funzione="setVisibilitaDaPubblicazioniPrecedenti('#TORN_PUBPRECSA#')" elencocampi="TORN_PUBPRECSA" esegui="true"/>
	<gene:fnJavaScriptScheda funzione="setVisibilitaForumlariGUUE('#${param.campoTipoAppalto}#','#${param.campoImporto}#','#${param.campoTipoProcedura}#')" elencocampi="" esegui="true"/> */%>
	<gene:fnJavaScriptScheda funzione="setVisibilitaAMMVAR('#${param.campoTipoProcedura}#')" elencocampi="" esegui="true"/>		
	<gene:fnJavaScriptScheda funzione="setVisibilitaMODFIN('#${param.campoTipoProcedura}#','#${param.campoGaraTelematica}#')" elencocampi="" esegui="true"/>
	<gene:fnJavaScriptScheda funzione="setVisibilitaDocumenti('#${param.campoTipoProcedura}#')" elencocampi="" esegui="true"/>
	<!-- NUOVE FUNZIONI PER LA VISIBILITA' -->
	<gene:fnJavaScriptScheda funzione="setVisibilitaNOFDIT('#TORN_OFFLOT#')" elencocampi="TORN_OFFLOT" esegui="true"/>
	<gene:fnJavaScriptScheda funzione="setVisibilitaNGADIT('#TORN_OFFLOT#')" elencocampi="TORN_OFFLOT" esegui="true"/>
	<gene:fnJavaScriptScheda funzione="setVisibilitaPROGEU('#TORN_APFINFC#')" elencocampi="TORN_APFINFC" esegui="true"/>
	
	<gene:javaScript>
	
	$(window).ready(function (){
		_creaFinestraAlberoNUTS();
		_creaLinkAlberoNUTS($("#TORN_CODNUTS").parent(), "${modo}", $("#TORN_CODNUTS"), $("#TORN_CODNUTSview") );

		$("input[name^='TORN_CODNUTS']").attr('readonly','readonly');
		$("input[name^='TORN_CODNUTS']").attr('tabindex','-1');
		$("input[name^='TORN_CODNUTS']").css('border-width','1px');
		$("input[name^='TORN_CODNUTS']").css('background-color','#E0E0E0');		
	});
	<!-- MIE FUNZIONI -->
	
	function setVisibilitaNOFDIT(valore) {
		var visibile = false;
		if (valore == 2) {
			visibile = true;
		}else{
			setValue("TORN_NOFDIT","");
		}
		showObj("rowTORN_NOFDIT", visibile);
	}
	
	function setVisibilitaNGADIT(valore) {
		var visibile = false;
		if (valore == 2 || valore == 3) {
			visibile = true;
		}else{
			setValue("TORN_NGADIT","");
		}
		showObj("rowTORN_NGADIT", visibile);
	}
		
					
	function setVisibilitaPROGEU(valore) {
		var visibile = false;
		if (valore == 1) {
			visibile = true;
		}else{
			setValue("TORN_PROGEU","");
		}
		showObj("rowTORN_PROGEU", visibile);
	}
	
	<!-- fine delle mie funzioni -->
	
	
	function setVisibilitaAMMVAR(iterga) {
		var visibile = false;
		if (iterga == 1 || iterga == 2 || iterga == 4) {
			visibile = true;
		}
		showObj("rowTORN_AMMVAR", visibile);
	}

	function setVisibilitaMODFIN(iterga,gartel) {
		var visibile = false;
		if (iterga != 6 || gartel != 1) {
			visibile = true;
		}
		showObj("rowTORN_MODFIN", visibile);
	}
	
	function setVisibilitaDaTipoProcedura(valore) {
		var visibile = false;
		if (valore == 2 || valore == 4) {
			visibile = true;
		}
		
		showObj("rowTORN_MINOPE", visibile);
		showObj("rowTORN_MAXOPE", visibile);
		showObj("rowTORN_SELOPE", visibile);
		showObj("rowTORN_PROFAS", visibile);
	}
	
	function setVisibilitaDocumenti(iterga) {
		var visibile = false;
		if (iterga ==1 || iterga == 2 || iterga == 4) {
			visibile = true;
		}
		showObj("rowTORN_IMPDOC", visibile);
		showObj("rowTORN_PAGDOC", visibile);
	}
		
	
	function setVisibilitaELEPAR(modlic,iterga) {
		var visibile = false;
		if (modlic == 6 && (iterga==1 || iterga==2 || iterga==4)) {
			visibile = true;
		}
		showObj("rowTORN_ELEPAR", visibile);
	}
	

	
	function formNUTS(modifica, campo){
		openPopUpCustom("href=gene/tabnuts/dettaglio-codice-nuts.jsp&key=" + document.forms[0].key.value + "&keyParent=" + document.forms[0].keyParent.value + "&modo="+(modifica ? "MODIFICA":"VISUALIZZA")+"&campo="+campo+"&valore="+ getValue(campo), "formNUTS", 700, 300, 1, 1);
	}
	

	
	</gene:javaScript>
