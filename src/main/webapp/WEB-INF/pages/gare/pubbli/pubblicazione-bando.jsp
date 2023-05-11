<%
/*
 * Created on: 12/11/2008
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<c:choose>
<c:when test="${param.tipoDettaglio eq 1}">
				<gene:campoScheda campo="CODGAR9_${param.contatore}" entita="PUBBLI" campoFittizio="true" visibile="false" definizione="T21;1;;;CODGAR_PUB" value="${item[0]}" />
				<gene:campoScheda campo="NUMPUB_${param.contatore}" entita="PUBBLI" campoFittizio="true" visibile="false" definizione="N2;1;;;NUMPUB" value="${item[1]}" />
				<gene:campoScheda campo="TIPPUB_${param.contatore}" entita="PUBBLI" campoFittizio="true" definizione="N7;0;A1008;;G1TIPPUB" value="${item[2]}" modificabile="${item[2] ne '11' and item[2] ne '13' and item[2] ne '15' and item[2] ne '16' and item[2] ne '23'}" obbligatorio="true" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoTIPPUB"/>
				<gene:campoScheda campo="NPRPUB_${param.contatore}" entita="PUBBLI" campoFittizio="true" definizione="T20;0;;;NPRPUB" value="${item[4]}" />
				<gene:campoScheda campo="DINPUB_${param.contatore}" entita="PUBBLI" campoFittizio="true" definizione="D;0;;;DINPUB" value="${item[5]}" />
				<gene:campoScheda campo="DATPUB_${param.contatore}" entita="PUBBLI" campoFittizio="true" definizione="D;0;;;DATPUB" value="${item[6]}" modificabile="${item[2] ne '11' and item[2] ne '13' and item[2] ne '15' and item[2] ne '16' and item[2] ne '23'}"/>
				<gene:campoScheda campo="DATFIPUB_${param.contatore}" entita="PUBBLI" campoFittizio="true" definizione="D;0;;;DATFIPUB" value="${item[9]}" />
				<gene:campoScheda campo="TITPUB_${param.contatore}" entita="PUBBLI" campoFittizio="true" definizione="T254;0;;;G1TITPUB" value="${item[10]}" />
				<gene:campoScheda campo="NAVPUB_${param.contatore}" entita="PUBBLI" campoFittizio="true" definizione="T60;0;;;G1NAVPUB" value="${item[11]}" />
				<gene:campoScheda campo="NAVNUM_${param.contatore}" entita="PUBBLI" campoFittizio="true" definizione="T60;0;;;G1NAVNUM" value="${item[12]}" />
				<gene:campoScheda campo="URLPUB_${param.contatore}" entita="PUBBLI" campoFittizio="true" definizione="T254;0;;;G1URLPUB" value="${item[13]}" href="javascript:apriUrl('${item[13]}')">
					<gene:checkCampoScheda funzione='validURL("##")' obbligatorio="true" messaggio="Il valore dell'indirizzo URL specificato non è valido: è possibile inserire solo un indirizzo URL che inizi con 'ftp://', 'ftps://', 'sftp://', 'http://', 'https://' o 'www.'e non contenga spazi o virgole" onsubmit="false"/>
				</gene:campoScheda>
				<gene:campoScheda campo="IMPPUB_${param.contatore}" entita="PUBBLI" campoFittizio="true" definizione="F7.2;0;;MONEY;G1IMPPUB" value="${item[7]}" />
				<gene:campoScheda campo="INTPUB_${param.contatore}" entita="PUBBLI" campoFittizio="true" definizione="T1;0;;SN;INTPUB" value="${item[8]}" />
				<gene:campoScheda campo="TESPUB_${param.contatore}" entita="PUBBLI" campoFittizio="true" definizione="T254;0;;;TESPUB" value="${item[3]}" />
</c:when>
<c:otherwise>
<c:choose>
<c:when test="${param.tipoDettaglio eq 2}">
				<gene:campoScheda campo="CODGAR9_${param.contatore}" entita="PUBBLI" campoFittizio="true" visibile="false" definizione="T21;1;;;CODGAR_PUB" />
</c:when>
<c:otherwise>
				<gene:campoScheda campo="CODGAR9_${param.contatore}" entita="PUBBLI" campoFittizio="true" visibile="false" definizione="T21;1;;;CODGAR_PUB" value="${param.chiave}" />
</c:otherwise>
</c:choose>
			<gene:campoScheda campo="NUMPUB_${param.contatore}" entita="PUBBLI" campoFittizio="true" visibile="false" definizione="N2;1;;;NUMPUB" />
			<gene:campoScheda campo="TIPPUB_${param.contatore}" entita="PUBBLI" campoFittizio="true" definizione="N7;0;A1008;;G1TIPPUB" obbligatorio="true" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoTIPPUB"/>
			<gene:campoScheda campo="NPRPUB_${param.contatore}" entita="PUBBLI" campoFittizio="true" definizione="T20;0;;;NPRPUB" />
			<gene:campoScheda campo="DINPUB_${param.contatore}" entita="PUBBLI" campoFittizio="true" definizione="D;0;;;DINPUB" />
			<gene:campoScheda campo="DATPUB_${param.contatore}" entita="PUBBLI" campoFittizio="true" definizione="D;0;;;DATPUB" />
			<gene:campoScheda campo="DATFIPUB_${param.contatore}" entita="PUBBLI" campoFittizio="true" definizione="D;0;;;DATFIPUB" />			
			<gene:campoScheda campo="TITPUB_${param.contatore}" entita="PUBBLI" campoFittizio="true" definizione="T254;0;;;G1TITPUB" />
			<gene:campoScheda campo="NAVPUB_${param.contatore}" entita="PUBBLI" campoFittizio="true" definizione="T60;0;;;G1NAVPUB"  />
			<gene:campoScheda campo="NAVNUM_${param.contatore}" entita="PUBBLI" campoFittizio="true" definizione="T60;0;;;G1NAVNUM" />
			<gene:campoScheda campo="URLPUB_${param.contatore}" entita="PUBBLI" campoFittizio="true" definizione="T254;0;;;G1URLPUB" >
				<gene:checkCampoScheda funzione='validURL("##")' obbligatorio="true" messaggio="Il valore dell'indirizzo URL specificato non è valido: è possibile inserire solo un indirizzo URL che inizi con 'ftp://', 'ftps://', 'sftp://', 'http://', 'https://' o 'www.'e non contenga spazi o virgole" onsubmit="false"/>
			</gene:campoScheda>
			<gene:campoScheda campo="IMPPUB_${param.contatore}" entita="PUBBLI" campoFittizio="true" definizione="F7.2;0;;MONEY;G1IMPPUB" />
			<gene:campoScheda campo="INTPUB_${param.contatore}" entita="PUBBLI" campoFittizio="true" definizione="T1;0;;SN;INTPUB" />
			<gene:campoScheda campo="TESPUB_${param.contatore}" entita="PUBBLI" campoFittizio="true" definizione="T254;0;;;TESPUB" />
</c:otherwise>
</c:choose>

<gene:fnJavaScriptScheda funzione="gestioneTIPPUB_${param.contatore}('#PUBBLI_TIPPUB_${param.contatore}#')" elencocampi="PUBBLI_TIPPUB_${param.contatore}" esegui="true" />

<gene:javaScript>

function apriPopupInsertPredefiniti() {
	var href = "href=gare/commons/conferma-ins-pubbli-predefinite.jsp?codgar="+getValue("GARE_CODGAR1")+"&ngara="+getValue("GARE_NGARA")+"&bando=1";
	openPopUpCustom(href, "insPubblicazioniPredefinite", 600, 350, "no", "yes");
}

function gestioneTIPPUB_${param.contatore}(tippub){

	if (tippub == '1' || tippub == '2' || tippub == '5' || tippub == '11' || tippub == '13' || tippub == '15' || tippub == '16' || tippub == '23') {
		document.getElementById("rowPUBBLI_DINPUB_${param.contatore}").style.display = 'none';
		document.forms[0].PUBBLI_DINPUB_${param.contatore}.value = '';
	} else {
		document.getElementById("rowPUBBLI_DINPUB_${param.contatore}").style.display = '';
	}
	
	if (tippub == '1' || tippub == '2' || tippub == '3' || tippub == '11' || tippub == '13' || tippub == '15' || tippub == '16' || tippub == '23') {
		document.getElementById("rowPUBBLI_INTPUB_${param.contatore}").style.display = 'none';
		document.forms[0].PUBBLI_INTPUB_${param.contatore}.value = '';
	} else {
		document.getElementById("rowPUBBLI_INTPUB_${param.contatore}").style.display = '';
	}
	
	if (tippub == '1' || tippub == '2') {
		document.getElementById("rowPUBBLI_DATFIPUB_${param.contatore}").style.display = '';
	} else {
		document.getElementById("rowPUBBLI_DATFIPUB_${param.contatore}").style.display = 'none';
		document.forms[0].PUBBLI_DATFIPUB_${param.contatore}.value = '';
	}
	
	if (tippub == '4' || tippub == '5' || tippub == '6' || tippub == '7') {
		if(document.getElementById("rowPUBBLI_IMPPUB_${param.contatore}"))
			document.getElementById("rowPUBBLI_IMPPUB_${param.contatore}").style.display = '';
	} else {
		if(document.getElementById("rowPUBBLI_IMPPUB_${param.contatore}")){
			document.getElementById("rowPUBBLI_IMPPUB_${param.contatore}").style.display = 'none';
			document.forms[0].PUBBLI_IMPPUB_${param.contatore}.value = '';
		}	
	}
	
	if (tippub == '3' || tippub == '4') {
		if(document.getElementById("rowPUBBLI_NPRPUB_${param.contatore}"))
			document.getElementById("rowPUBBLI_NPRPUB_${param.contatore}").style.display = '';
	}else{
		if(document.getElementById("rowPUBBLI_NPRPUB_${param.contatore}")){
			document.getElementById("rowPUBBLI_NPRPUB_${param.contatore}").style.display = 'none';
			document.forms[0].PUBBLI_NPRPUB_${param.contatore}.value = '';
		}
	}
	
	if (tippub == '11' || tippub == '13' || tippub == '15' || tippub == '16' || tippub == '23') {
		document.getElementById("rowPUBBLI_TESPUB_${param.contatore}").style.display = 'none';
	}
	
	if (tippub == '2' || tippub == '3' || tippub == '4') {
			document.getElementById("rowPUBBLI_TITPUB_${param.contatore}").style.display = '';
			document.getElementById("rowPUBBLI_NAVPUB_${param.contatore}").style.display = '';
			document.getElementById("rowPUBBLI_NAVNUM_${param.contatore}").style.display = '';
			document.getElementById("rowPUBBLI_URLPUB_${param.contatore}").style.display = '';
	} else {
			document.getElementById("rowPUBBLI_TITPUB_${param.contatore}").style.display = 'none';
			document.getElementById("rowPUBBLI_NAVPUB_${param.contatore}").style.display = 'none';
			document.getElementById("rowPUBBLI_NAVNUM_${param.contatore}").style.display = 'none';
			document.getElementById("rowPUBBLI_URLPUB_${param.contatore}").style.display = 'none';
			document.forms[0].PUBBLI_TITPUB_${param.contatore}.value = '';
			document.forms[0].PUBBLI_NAVPUB_${param.contatore}.value = '';
			document.forms[0].PUBBLI_NAVNUM_${param.contatore}.value = '';
			document.forms[0].PUBBLI_URLPUB_${param.contatore}.value = '';
	}
}

 	//Customizzazione della funzione delElementoSchedaMultipla per evitare che possa
 	//essere eliminata una pubblicazione con TIPPUB=11
 	function delPubblicazioniBando(id, label, tipo, campi){
		var tippub = getValue("PUBBLI_TIPPUB_" + id);
		if(tippub == 11 || tippub == '13' || tippub == 15 || tippub == '16' || tippub == '23')
			alert("Non è possibile eliminare tale tipologia di pubblicazione")
		else
			delElementoSchedaMultipla(id,label,tipo,campi);
	}
	
	function validURL(str) {
		if(str==""){
			return true;
		}else{
			var res = /^(((http|HTTP|https|HTTPS|ftp|FPT|ftps|FTPS|sftp|SFTP):\/\/)|((w|W){3}(\d)?\.))[\w\?!\./:;\-_=#+*%@&quot;\(\)&amp;]+/.test(str);
			return res;
		}
	}
	
	
	function apriUrl(urlDocumento){
		if(urlDocumento.indexOf("http://")<0 && urlDocumento.indexOf("https://")<0)
			urlDocumento = "http://" + urlDocumento;
		window.open(urlDocumento,"url_documento");
	}
</gene:javaScript>
