<%
/*
 * Created on: 05-08-2010
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 *
 * Popup per campi ulteriori relativi alla ditta presenta nella lista delle
 * fasi di ricezione in analisi
 */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>

<% // Validazione parametri tramite regex %>
<c:if test='${not empty param.stepWizard and gene:matches(param.stepWizard, "^-?[0-9]+$", true)}' />

<c:choose>
	<c:when test='${not empty param.stepWizard}'>
		<c:set var="stepWizard" value="${param.stepWizard}" />
	</c:when>
	<c:otherwise>
		<c:set var="stepWizard" value="${stepWizard}" />
	</c:otherwise>
</c:choose>

<div style="width:97%;">
<gene:template file="popup-template.jsp">

	<gene:redefineInsert name="gestioneHistory" />
	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="corpo">
		<gene:setString name="titoloMaschera" value='Inserimento documentazione' />
		<gene:formScheda entita="IMPRDOCG" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreVerificaDocumenti" >
			<gene:campoScheda campo="CODGAR" visibile="false" />
			<gene:campoScheda campo="CODIMP"  visibile="false" />
			<gene:campoScheda campo="NORDDOCI" visibile="false"/>
			<gene:campoScheda campo="NGARA" visibile="false"/>
			<gene:campoScheda campo="DESCRIZIONE" />
			<gene:campoScheda campo="DATARILASCIO" />
			<gene:campoScheda campo="ORARILASCIO" />
			<gene:campoScheda campo="DATASCADENZA" />
			<gene:campoScheda campo="SITUAZDOCI" />
			<gene:campoScheda campo="NOTEDOCI" />
			<gene:campoScheda campo="PROVENI" visibile="false" defaultValue="2"/>
			<gene:campoScheda title="Nome file" >
				<input type="file" name="selezioneFile" id="selezioneFile" onchange="javascript:scegliFile(this.value);" class="file" size="70" onkeydown="return bloccaCaratteriDaTastiera(event);" value=''/>
			</gene:campoScheda>
			<gene:campoScheda title="File da allegare" campo="FILEDAALLEGARE" campoFittizio="true" visibile="false" definizione="T70;0" />
			
			<input type="hidden" name="isGaraLottiConOffertaUnica" id="isGaraLottiConOffertaUnica" value="${param.isGaraLottiConOffertaUnica}" />
			<gene:campoScheda>
				<td class="comandi-dettaglio" colSpan="2">
					<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:schedaConferma();">
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:window.close();">&nbsp;
				</td>
			</gene:campoScheda>
			<input type="hidden" name="stepWizard" id="stepWizard" value="${stepWizard }" />
		</gene:formScheda>
	</gene:redefineInsert>

	<gene:javaScript>
	document.forms[0].jspPathTo.value="gare/imprdocg/imprdocg-schedaPopup-insert.jsp";
	var openerKeyParent = window.opener.document.forms[0].keyParent.value;
	
	var campiChiave = openerKeyParent.split(";");
	var codiceGara= campiChiave[0].substr(openerKeyParent.indexOf(":")+1);
	var codiceDitta= campiChiave[1].substr(openerKeyParent.indexOf(":"));
	var numeroGara= campiChiave[2].substr(openerKeyParent.indexOf(":"));
		
	setValue("IMPRDOCG_CODGAR", codiceGara);
	setValue("IMPRDOCG_CODIMP", codiceDitta);
	setValue("IMPRDOCG_NGARA", numeroGara);
	
	document.forms[0].encoding="multipart/form-data";
	
	function scegliFile(valore) {
		selezioneFile = document.getElementById("selezioneFile").value;
		var lunghezza_stringa = selezioneFile.length;
		var posizione_barra = selezioneFile.lastIndexOf("\\");
		var nome = selezioneFile.substring(posizione_barra+1,lunghezza_stringa).toUpperCase();
		if(nome.length>100){
			alert("Il nome del file non può superare i 100 caratteri!");
			document.getElementById("selezioneFile").value="";
			setValue("FILEDAALLEGARE","");
		}else{
			setValue("FILEDAALLEGARE" ,nome);
		}
		
	}
	
	<c:if test='${not empty RISULTATO and RISULTATO eq "OK"}' >
		window.opener.document.forms[0].pgSort.value = "";
		window.opener.document.forms[0].pgLastSort.value = "";
		window.opener.document.forms[0].pgLastValori.value = "";
		window.opener.bloccaRichiesteServer();
		window.opener.listaVaiAPagina(0);
		window.close();
	</c:if>


	var schedaConferma_Default = schedaConferma;
	function schedaConferma_Custom(){
		var selezioneFile = document.getElementById("selezioneFile").value;
		var fileDaAllegare = getValue("FILEDAALLEGARE");
		if(fileDaAllegare!=null && fileDaAllegare!="" && selezioneFile==null || selezioneFile =="")
			setValue("FILEDAALLEGARE","");
		schedaConferma_Default();
	}
	
	schedaConferma =   schedaConferma_Custom;
	</gene:javaScript>
</gene:template>
</div>