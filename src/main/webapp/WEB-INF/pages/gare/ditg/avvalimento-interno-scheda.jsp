<%
	/*
	 * Created on 18-Sett-2014
	 *
	 * Copyright (c) EldaSoft S.p.A.
	 * Tutti i diritti sono riservati.
	 *
	 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
	 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
	 * aver prima formalizzato un accordo specifico con EldaSoft.
	 */
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>

<c:set var="codiceGara" value='${gene:getValCampo(key, "DITG.CODGAR5")}' />
<c:set var="codiceDitta" value='${gene:getValCampo(key, "DITG.DITTAO")}' />
<c:set var="numeroGara" value='${gene:getValCampo(key, "DITG.NGARA5")}' />
<c:set var="tipoImpresa" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipologiaImpresaFunction",  pageContext,key)}'/>

<c:choose>
	<c:when test="${param.tipoDettaglio eq 1}">
		<gene:campoScheda entita="DITGAVVAL" campo="ID_${param.contatore}" campoFittizio="true" visibile="false" definizione="N12;1" value="${item[0]}"/>
		<gene:campoScheda campo="NGARA_${param.contatore}" entita="DITGAVVAL" campoFittizio="true" visibile="false" definizione="T20;1;;;G1NGARAAVL" value="${numeroGara}" />
		<gene:campoScheda campo="DITTAO_${param.contatore}" entita="DITGAVVAL" campoFittizio="true" visibile="false" definizione="T10;1;;;G1DITTAOAVL" value="${codiceDitta}" />
		<gene:campoScheda entita="DITGAVVAL" campo="TIPOAV_${param.contatore}"  campoFittizio="true" visibile="true" definizione="N12;0;A1123;;G1TIPOAV" value="${item[3]}"/>
		<c:if test="${!empty tipoImpresa and (tipoImpresa eq 3 or tipoImpresa eq 10)}">
			<gene:archivio titolo="ditte del raggruppamento" 
				lista="gene/impr/impr-lista-popup.jsp"
				scheda="gene/impr/impr-scheda.jsp"
				schedaPopUp="gene/impr/impr-scheda-popup.jsp"
				campi="IMPR.CODIMP;IMPR.NOMEST"
				chiave="DITGAVVAL_DITTART_${param.contatore}"
				where="CODIMP in (select coddic from ragimp where codime9 = '${codiceDitta}')">
				<gene:campoScheda entita="DITGAVVAL" campo="DITTART_${param.contatore}" campoFittizio="true" obbligatorio="true" visibile="true" definizione="T10;0;;;G1DITTART" value="${item[4]}" />
				<gene:campoScheda title="Ragione sociale" entita="IMPR" campo="NOMEST1_${param.contatore}" campoFittizio="true" visibile="true" definizione="T2000;0;;;NOMIMP" value="${item[5]}" />
			</gene:archivio>
		</c:if>
		<gene:archivio titolo="ditte" 
			lista="gene/impr/impr-lista-popup.jsp"
			scheda="gene/impr/impr-scheda.jsp"
			schedaPopUp="gene/impr/impr-scheda-popup.jsp"
			campi="IMPR.CODIMP;IMPR.NOMEST"
			chiave="DITGAVVAL_DITTAAV_${param.contatore}"
			where="CODIMP <> '${codiceDitta}' and TIPIMP not in (3,10)">
			<gene:campoScheda entita="DITGAVVAL" campo="DITTAAV_${param.contatore}" campoFittizio="true" obbligatorio="true" visibile="true" definizione="T10;0;;;G1DITTAAV" value="${item[6]}" />
			<gene:campoScheda title="Ragione sociale" entita="IMPR" campo="NOMEST2_${param.contatore}" campoFittizio="true" visibile="true" definizione="T2000;0;;;NOMIMP" value="${item[7]}" />
		</gene:archivio>
		<gene:archivio titolo="categorie SOA" obbligatorio="false"
			lista="gene/cais/lista-categorie-iscrizione-popup-soloLavori.jsp"
			scheda=""
			schedaPopUp=""
			campi="V_CAIS_TIT.CAISIM;V_CAIS_TIT.DESCAT"
			chiave="DITGAVVAL_CODCAT_${param.contatore}"
			where="V_CAIS_TIT.TIPLAVG=1">
			<gene:campoScheda entita="DITGAVVAL" campo="CODCAT_${param.contatore}" campoFittizio="true" visibile="true" definizione="T30;0;;;G1CODCATAVL" value="${item[8]}" />
			<gene:campoScheda title="Descrizione" entita="V_CAIS_TIT" campo="DESCAT_${param.contatore}" campoFittizio="true" visibile="true" definizione="T2000;0;;;DESCAT" value="${item[9]}" />
		</gene:archivio>
		<gene:campoScheda entita="DITGAVVAL" campo="NUMCLA_${param.contatore}"  campoFittizio="true" visibile="true" definizione="N12;0;A1015;;G1NUMCLAAVL" value="${item[10]}"/>
		<gene:campoScheda entita="DITGAVVAL" campo="NOTEAV_${param.contatore}"  campoFittizio="true" visibile="true" definizione="T2000;0;;NOTE;G1NOTEAV" value="${item[11]}"/>
	</c:when>
	<c:otherwise>
		<gene:campoScheda entita="DITGAVVAL" campo="ID_${param.contatore}" campoFittizio="true" visibile="false" definizione="N12;1" />
		<gene:campoScheda campo="NGARA_${param.contatore}" entita="DITGAVVAL" campoFittizio="true" visibile="false" definizione="T21;1;;;G1NGARAAVL" value="${numeroGara}" />
		<gene:campoScheda campo="DITTAO_${param.contatore}" entita="DITGAVVAL" campoFittizio="true" visibile="false" definizione="T10;1;;;G1DITTAOAVL" value="${codiceDitta}" />
		<gene:campoScheda title="Tipo avvalimento" entita="DITGAVVAL" campo="TIPOAV_${param.contatore}"  campoFittizio="true" visibile="true" definizione="N12;0;A1123;;G1TIPOAV" />
		<c:if test="${!empty tipoImpresa and (tipoImpresa eq 3 or tipoImpresa eq 10)}">
			<gene:archivio titolo="ditte del raggruppamento" 
				lista="gene/impr/impr-lista-popup.jsp"
				scheda="gene/impr/impr-scheda.jsp"
				schedaPopUp="gene/impr/impr-scheda-popup.jsp"
				campi="IMPR.CODIMP;IMPR.NOMEST"
				chiave="DITGAVVAL_DITTART_${param.contatore}"
				where="CODIMP in (select coddic from ragimp where codime9 = '${codiceDitta}')">
				<gene:campoScheda entita="DITGAVVAL" campo="DITTART_${param.contatore}" campoFittizio="true" obbligatorio="true" visibile="true" definizione="T10;0;;;G1DITTART" />
				<gene:campoScheda title="Ragione sociale" entita="IMPR" campo="NOMEST1_${param.contatore}" campoFittizio="true" visibile="true" definizione="T2000;0;;;NOMIMP" />
			</gene:archivio>
		</c:if>
		<gene:archivio titolo="ditte" 
			lista="gene/impr/impr-lista-popup.jsp"
			scheda="gene/impr/impr-scheda.jsp"
			schedaPopUp="gene/impr/impr-scheda-popup.jsp"
			campi="IMPR.CODIMP;IMPR.NOMEST"
			chiave="DITGAVVAL_DITTAAV_${param.contatore}"
			where="CODIMP <> '${codiceDitta}' and TIPIMP not in (3,10)">
			<gene:campoScheda entita="DITGAVVAL" campo="DITTAAV_${param.contatore}" campoFittizio="true" obbligatorio="true" visibile="true" definizione="T10;0;;;G1DITTAAV"  />
			<gene:campoScheda title="Ragione sociale" entita="IMPR" campo="NOMEST2_${param.contatore}" campoFittizio="true" visibile="true" definizione="T2000;0;;;NOMIMP"  />
		</gene:archivio>
		<gene:archivio titolo="categorie SOA" obbligatorio="false"
			lista="gene/cais/lista-categorie-iscrizione-popup-soloLavori.jsp"
			scheda=""
			schedaPopUp=""
			campi="V_CAIS_TIT.CAISIM;V_CAIS_TIT.DESCAT"
			chiave="DITGAVVAL_CODCAT_${param.contatore}"
			where="V_CAIS_TIT.TIPLAVG=1">
			<gene:campoScheda entita="DITGAVVAL" campo="CODCAT_${param.contatore}" campoFittizio="true" visibile="true" definizione="T30;0;;;G1CODCATAVL" />
			<gene:campoScheda title="Descrizione" entita="V_CAIS_TIT" campo="DESCAT_${param.contatore}" campoFittizio="true" visibile="true" definizione="T2000;0;;;DESCAT" />
		</gene:archivio>
		<gene:campoScheda entita="DITGAVVAL" campo="NUMCLA_${param.contatore}"  campoFittizio="true" visibile="true" definizione="N12;0;A1015;;G1NUMCLAAVL"/>
		<gene:campoScheda entita="DITGAVVAL" campo="NOTEAV_${param.contatore}"  campoFittizio="true" visibile="true" definizione="T2000;0;;NOTE;G1NOTEAV" />
	</c:otherwise>
</c:choose>

<gene:fnJavaScriptScheda funzione='gestioneSOA_${param.contatore}("#DITGAVVAL_TIPOAV_${param.contatore}#")' elencocampi='DITGAVVAL_TIPOAV_${param.contatore}' esegui="true" />

<gene:javaScript>

	function gestioneSOA_${param.contatore}(tipoav) {
		if (tipoav == '1') {
			document.getElementById("rowDITGAVVAL_CODCAT_${param.contatore}").style.display = 'none';
			document.getElementById("rowV_CAIS_TIT_DESCAT_${param.contatore}").style.display = 'none';
			document.getElementById("rowDITGAVVAL_NUMCLA_${param.contatore}").style.display = 'none';
			document.forms[0].DITGAVVAL_CODCAT_${param.contatore}.value = '';
			document.forms[0].DITGAVVAL_NUMCLA_${param.contatore}.value = '';
		} else {
			document.getElementById("rowDITGAVVAL_CODCAT_${param.contatore}").style.display = '';
			document.getElementById("rowV_CAIS_TIT_DESCAT_${param.contatore}").style.display = '';
			document.getElementById("rowDITGAVVAL_NUMCLA_${param.contatore}").style.display = '';
		}
	}

</gene:javaScript>