<%
/*
 * Created on: 26/11/2008
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
				<gene:campoScheda campo="NPUBG_${param.contatore}" entita="PUBG" campoFittizio="true" visibile="false" definizione="N2;1;;;NPUBG" value="${item[1]}" />
				<gene:campoScheda campo="TIPPUBG_${param.contatore}" entita="PUBG" campoFittizio="true" definizione="N7;0;A1008;;TIPPUBG" value="${item[2]}"  modificabile="${item[2] ne '12' and item[2] ne '14'}" obbligatorio="true" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoTIPPUB"/>
				<gene:campoScheda campo="NPRPUB_${param.contatore}" entita="PUBG" campoFittizio="true" definizione="T20;0;;;G1NPRPUB" value="${item[8]}" />
				<gene:campoScheda campo="DINVPUBG_${param.contatore}" entita="PUBG" campoFittizio="true" definizione="D;0;;;DINVPUBG" value="${item[7]}" />
				<gene:campoScheda campo="DINPUBG_${param.contatore}" entita="PUBG" campoFittizio="true" definizione="D;0;;;DINPUBG" value="${item[3]}" modificabile="${item[2] ne '12' and item[2] ne '14'}" />
				<gene:campoScheda campo="DFIPUBG_${param.contatore}" entita="PUBG" campoFittizio="true" definizione="D;0;;;DFIPUBG" value="${item[4]}" />
				<gene:campoScheda campo="TITPUB_${param.contatore}" entita="PUBG" campoFittizio="true" definizione="T254;0;;;G1TITPUBG" value="${item[9]}" />
				<gene:campoScheda campo="NAVPUB_${param.contatore}" entita="PUBG" campoFittizio="true" definizione="T60;0;;;G1NAVPUBG" value="${item[10]}" />
				<gene:campoScheda campo="NAVNUM_${param.contatore}" entita="PUBG" campoFittizio="true" definizione="T60;0;;;G1NAVNUMG" value="${item[11]}" />
				<gene:campoScheda campo="URLPUB_${param.contatore}" entita="PUBG" campoFittizio="true" definizione="T254;0;;;G1URLPUBG" value="${item[12]}" href="javascript:apriUrl('${item[12]}')">
					<gene:checkCampoScheda funzione='validURL("##")' obbligatorio="true" messaggio="Il valore dell'indirizzo URL specificato non è valido: è possibile inserire solo un indirizzo URL che inizi con 'ftp://', 'ftps://', 'sftp://', 'http://', 'https://' o 'www.'e non contenga spazi o virgole" onsubmit="false"/>
				</gene:campoScheda>
				<gene:campoScheda campo="IMPPUB_${param.contatore}" entita="PUBG" campoFittizio="true" definizione="F7.2;0;;MONEY;G1IMPPUBG" value="${item[6]}" />
				<gene:campoScheda campo="NGARA_${param.contatore}" entita="PUBG" campoFittizio="true" modificabile="false" visibile="${tipologiaGara eq 1}" title="Codice lotto " definizione="T20;1;;;NGARA_PUB" value="${item[0]}" />
				<gene:campoScheda campo="TESPUBG_${param.contatore}" entita="PUBG" campoFittizio="true" definizione="T254;0;;;TESPUBG" value="${item[5]}" />				
</c:when>
<c:otherwise>
				<gene:campoScheda campo="NPUBG_${param.contatore}" entita="PUBG" campoFittizio="true" visibile="false" definizione="N2;1;;;NPUBG"/>
				<gene:campoScheda campo="TIPPUBG_${param.contatore}" entita="PUBG" campoFittizio="true" definizione="N7;0;A1008;;TIPPUBG" obbligatorio="true" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoTIPPUB"/>
				<gene:campoScheda campo="NPRPUB_${param.contatore}" entita="PUBG" campoFittizio="true" definizione="T20;0;;;G1NPRPUB"  />
				<gene:campoScheda campo="DINVPUBG_${param.contatore}" entita="PUBG" campoFittizio="true" definizione="D;0;;;DINVPUBG"/>
				<gene:campoScheda campo="DINPUBG_${param.contatore}" entita="PUBG" campoFittizio="true" definizione="D;0;;;DINPUBG"/>
				<gene:campoScheda campo="DFIPUBG_${param.contatore}" entita="PUBG" campoFittizio="true" definizione="D;0;;;DFIPUBG"/>
				<gene:campoScheda campo="TITPUB_${param.contatore}" entita="PUBG" campoFittizio="true" definizione="T254;0;;;G1TITPUBG" />
				<gene:campoScheda campo="NAVPUB_${param.contatore}" entita="PUBG" campoFittizio="true" definizione="T60;0;;;G1NAVPUBG" />
				<gene:campoScheda campo="NAVNUM_${param.contatore}" entita="PUBG" campoFittizio="true" definizione="T60;0;;;G1NAVNUMG" />
				<gene:campoScheda campo="URLPUB_${param.contatore}" entita="PUBG" campoFittizio="true" definizione="T254;0;;;G1URLPUBG" >
					<gene:checkCampoScheda funzione='validURL("##")' obbligatorio="true" messaggio="Il valore dell'indirizzo URL specificato non è valido: è possibile inserire solo un indirizzo URL che inizi con 'ftp://', 'ftps://', 'sftp://', 'http://', 'https://' o 'www.'e non contenga spazi o virgole" onsubmit="false"/>
				</gene:campoScheda>
				<gene:campoScheda campo="IMPPUB_${param.contatore}" entita="PUBG" campoFittizio="true" definizione="F7.2;0;;MONEY;G1IMPPUBG"/>
				<c:choose>
					<c:when test="${param.tipoDettaglio eq 2}">
						<c:choose>
							<c:when test="${tipologiaGara eq 1}">
								<gene:archivio titolo="lotti di gara"
									lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.DOCUMGARA.NGARA"),"gare/gare/gare-popup-lista-lotti.jsp","")}'
									scheda=''
									schedaPopUp=''
									campi="GARE.NGARA"
									functionId="default_0"
									parametriWhere="T:${param.chiave}"
									chiave=""
									formName="formLotti_${param.contatore}">
									<gene:campoScheda campo="NGARA_${param.contatore}" entita="PUBG" title="Codice lotto " campoFittizio="true" visibile="true" definizione="T20;1;;;NGARA_PUB" obbligatorio="true"/>
								</gene:archivio>
							</c:when>
							<c:otherwise>
									<gene:campoScheda campo="NGARA_${param.contatore}" entita="PUBG" title="Codice lotto " campoFittizio="true" visibile="false" definizione="T20;1;;;NGARA_PUB" value="${param.chiave}" />
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
						<c:choose>
							<c:when test="${tipologiaGara eq 1}">
								<gene:archivio titolo="lotti di gara"
									lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.DOCUMGARA.NGARA"),"gare/gare/gare-popup-lista-lotti.jsp","")}'
									scheda=''
									schedaPopUp=''
									campi="GARE.NGARA"
									functionId="default_0"
									parametriWhere="T:${param.chiave}"
									chiave=""
									formName="formLotti_${param.contatore}">
									<gene:campoScheda campo="NGARA_${param.contatore}" entita="PUBG" title="Codice lotto " campoFittizio="true" visibile="true" definizione="T20;1;;;NGARA_PUB" obbligatorio="true"/>
								</gene:archivio>
							</c:when>
							<c:otherwise>
									<gene:campoScheda campo="NGARA_${param.contatore}" entita="PUBG" title="Codice lotto " campoFittizio="true" visibile="false" definizione="T20;1;;;NGARA_PUB" value="${param.chiave}" />
							</c:otherwise>
						</c:choose>
					</c:otherwise>
				</c:choose>
				<gene:campoScheda campo="TESPUBG_${param.contatore}" entita="PUBG" campoFittizio="true" definizione="T254;0;;;TESPUBG"/>
</c:otherwise>
</c:choose>

<gene:fnJavaScriptScheda funzione="gestioneTIPPUBG_${param.contatore}('#PUBG_TIPPUBG_${param.contatore}#')" elencocampi="PUBG_TIPPUBG_${param.contatore}" esegui="true" />

<gene:javaScript>

function gestioneTIPPUBG_${param.contatore}(tippubg){

	if (tippubg == '1' || tippubg == '2' || tippubg == '5' || tippubg == '12' || tippubg == '14') {
		document.getElementById("rowPUBG_DINVPUBG_${param.contatore}").style.display = 'none';
		document.forms[0].PUBG_DINVPUBG_${param.contatore}.value = '';
	} else {
		document.getElementById("rowPUBG_DINVPUBG_${param.contatore}").style.display = '';
	}

	if (tippubg == '1' || tippubg == '2') {
		document.getElementById("rowPUBG_DFIPUBG_${param.contatore}").style.display = '';
	} else {
		document.getElementById("rowPUBG_DFIPUBG_${param.contatore}").style.display = 'none';
		document.forms[0].PUBG_DFIPUBG_${param.contatore}.value = '';
	}
	
	if (tippubg == '4' || tippubg == '5' || tippubg == '6' || tippubg == '7') {
		document.getElementById("rowPUBG_IMPPUB_${param.contatore}").style.display = '';
	} else {
		document.getElementById("rowPUBG_IMPPUB_${param.contatore}").style.display = 'none';
		document.forms[0].PUBG_IMPPUB_${param.contatore}.value = '';
	}

	if (tippubg == '12' || tippubg == '14') {
		document.getElementById("rowPUBG_TESPUBG_${param.contatore}").style.display = 'none';
	}
	
	if (tippubg == '3' || tippubg == '4') {
		if(document.getElementById("rowPUBG_NPRPUB_${param.contatore}"))
			document.getElementById("rowPUBG_NPRPUB_${param.contatore}").style.display = '';
	}else{
		if(document.getElementById("rowPUBG_NPRPUB_${param.contatore}")){
			document.getElementById("rowPUBG_NPRPUB_${param.contatore}").style.display = 'none';
			document.forms[0].PUBG_NPRPUB_${param.contatore}.value = '';
		}
	}
	
	if (tippubg == '2' || tippubg == '3' || tippubg == '4') {
			document.getElementById("rowPUBG_TITPUB_${param.contatore}").style.display = '';
			document.getElementById("rowPUBG_NAVPUB_${param.contatore}").style.display = '';
			document.getElementById("rowPUBG_NAVNUM_${param.contatore}").style.display = '';
			document.getElementById("rowPUBG_URLPUB_${param.contatore}").style.display = '';
	} else {
			document.getElementById("rowPUBG_TITPUB_${param.contatore}").style.display = 'none';
			document.getElementById("rowPUBG_NAVPUB_${param.contatore}").style.display = 'none';
			document.getElementById("rowPUBG_NAVNUM_${param.contatore}").style.display = 'none';
			document.getElementById("rowPUBG_URLPUB_${param.contatore}").style.display = 'none';
			document.forms[0].PUBG_TITPUB_${param.contatore}.value = '';
			document.forms[0].PUBG_NAVPUB_${param.contatore}.value = '';
			document.forms[0].PUBG_NAVNUM_${param.contatore}.value = '';
			document.forms[0].PUBG_URLPUB_${param.contatore}.value = '';
	}
	
}

	//Customizzazione della funzione delElementoSchedaMultipla per evitare che possa
 	//essere eliminata una pubblicazione con TIPPUB=12
 	function delPubblicazioniEsito(id, label, tipo, campi){
		var tippubg = getValue("PUBG_TIPPUBG_" + id);
		if(tippubg == 12 || tippubg == '14')
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

