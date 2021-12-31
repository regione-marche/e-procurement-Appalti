<%
/*
 * Created on: 24/11/2008
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

<c:set var="whereUffint" value=""/>

<c:choose>
	<c:when test="${param.tipoDettaglio eq 1}">

		<gene:campoScheda campo="ID_${param.contatore}" entita="NSO_ORDINANTI" campoFittizio="true" visibile="false" definizione="N12;1;;;NSO_ON_ID" value="${item[0]}" />
		<gene:campoScheda campo="NSO_ORDINI_ID_${param.contatore}" entita="NSO_ORDINANTI" campoFittizio="true" visibile="false" definizione="N12;1;;;NSO_ON_ORDID" value="${item[1]}" />
		<gene:campoScheda campo="TIPO_${param.contatore}" entita="NSO_ORDINANTI" campoFittizio="true" definizione="N12;0;NSO02;;NSO_ON_TIPO" value="${item[2]}" />
		
		<gene:archivio titolo="Uffici intestatari"
			 lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.NSO_ORDINI.CENINT"),"gare/nso_ordinanti/nso_ordini-uffint-lista-popup.jsp","")}'
			 scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaUffint"),"gene/uffint/uffint-scheda.jsp","")}'
			 schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaUffint"),"gene/uffint/uffint-scheda-popup.jsp","")}'
			 campi="UFFINT.CODEIN;UFFINT.NOMEIN;UFFINT.ENDPOINT_NSO;UFFINT.VIAEIN;UFFINT.CITEIN;UFFINT.CAPEIN;UFFINT.CODNAZ;UFFINT.IVAEIN"
			 chiave="NSO_ORDINANTI_CODEIN_${param.contatore}"
			 inseribile="true">
				<gene:campoScheda title="Codice" campo="CODEIN_${param.contatore}" entita="NSO_ORDINANTI" campoFittizio="true" definizione="T16;;;;NSO_ON_CODEIN" value="${item[3]}" />
				<gene:campoScheda title="Denominazione" campo="NOMEIN_${param.contatore}" entita="NSO_ORDINANTI" modificabile="false" campoFittizio="true" definizione="T254;;;;NSO_ON_NOMEIN" value="${item[4]}" />
				<gene:campoScheda campo="ENDPOINT_${param.contatore}" entita="NSO_ORDINANTI"  modificabile="false" campoFittizio="true" definizione="T30;;;;NSO_ON_ENDP" value="${item[5]}" />
				<gene:campoScheda campo="VIA_${param.contatore}" entita="NSO_ORDINANTI"  visibile="${item[2] eq '2'}" modificabile="false" campoFittizio="true" definizione="T60;;;;NSO_ON_VIA" value="${item[6]}" />
				<gene:campoScheda campo="CITTA_${param.contatore}" entita="NSO_ORDINANTI" visibile="${item[2] eq '2'}" modificabile="false" campoFittizio="true" definizione="T36;;;;NSO_ON_CITTA" value="${item[7]}" />
				<gene:campoScheda campo="CAP_${param.contatore}" entita="NSO_ORDINANTI" visibile="${item[2] eq '2'}" modificabile="false" campoFittizio="true" definizione="T5;;;;NSO_ON_CAP" value="${item[8]}" />
				<gene:campoScheda campo="CODNAZ_${param.contatore}" entita="NSO_ORDINANTI" visibile="${item[2] eq '2'}" modificabile="false" campoFittizio="true" definizione="N7;0;Ag010;;NSO_ON_CNAZ" value="${item[9]}" />
				<gene:campoScheda campo="PIVA_${param.contatore}" entita="NSO_ORDINANTI" visibile="${item[2] eq '2'}" modificabile="false" campoFittizio="true" definizione="T14;;;;NSO_ON_PIVA" value="${item[10]}"/>
		</gene:archivio>
		
		

	</c:when>
	<c:otherwise>
		<gene:campoScheda campo="ID_${param.contatore}" entita="NSO_ORDINANTI" campoFittizio="true" visibile="false" definizione="N12;1;;;NSO_ON_ID"  />
		<gene:campoScheda campo="NSO_ORDINI_ID_${param.contatore}" entita="NSO_ORDINANTI" campoFittizio="true" visibile="false" definizione="N12;1;;;NSO_ON_ORDID" value="${param.chiave}" />
		<gene:campoScheda campo="TIPO_${param.contatore}" entita="NSO_ORDINANTI" campoFittizio="true" definizione="N12;0;NSO02;;NSO_ON_TIPO"  />
		<gene:archivio titolo="Uffici intestatari"
			 lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.NSO_ORDINI.CENINT"),"gare/nso_ordinanti/nso_ordini-uffint-lista-popup.jsp","")}'
			 scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaUffint"),"gene/uffint/uffint-scheda.jsp","")}'
			 schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaUffint"),"gene/uffint/uffint-scheda-popup.jsp","")}'
			 campi="UFFINT.CODEIN;UFFINT.NOMEIN;UFFINT.ENDPOINT_NSO;UFFINT.VIAEIN;UFFINT.CITEIN;UFFINT.CAPEIN;UFFINT.CODNAZ;UFFINT.IVAEIN"
			 chiave="NSO_ORDINANTI_CODEIN_${param.contatore}"
			 inseribile="true">
				<gene:campoScheda title="Codice" campo="CODEIN_${param.contatore}" entita="NSO_ORDINANTI" campoFittizio="true" definizione="T16;;;;NSO_ON_CODEIN"  />
				<gene:campoScheda title="Denominazione" campo="NOMEIN_${param.contatore}" entita="NSO_ORDINANTI" modificabile="false" campoFittizio="true" definizione="T254;;;;NSO_ON_NOMEIN"  />
				<gene:campoScheda campo="ENDPOINT_${param.contatore}" entita="NSO_ORDINANTI"  modificabile="false" campoFittizio="true" definizione="T30;;;;NSO_ON_ENDP"  />
				<gene:campoScheda campo="VIA_${param.contatore}" entita="NSO_ORDINANTI"  modificabile="false" campoFittizio="true" definizione="T60;;;;NSO_ON_VIA" />
				<gene:campoScheda campo="CITTA_${param.contatore}" entita="NSO_ORDINANTI"  modificabile="false" campoFittizio="true" definizione="T36;;;;NSO_ON_CITTA" />
				<gene:campoScheda campo="CAP_${param.contatore}" entita="NSO_ORDINANTI"  modificabile="false" campoFittizio="true" definizione="T5;;;;NSO_ON_CAP" />
				<gene:campoScheda campo="CODNAZ_${param.contatore}" entita="NSO_ORDINANTI"  modificabile="false" campoFittizio="true" definizione="N7;0;Ag010;;NSO_ON_CNAZ" />
				<gene:campoScheda campo="PIVA_${param.contatore}" entita="NSO_ORDINANTI" modificabile="false" campoFittizio="true" definizione="T14;;;;NSO_ON_PIVA" />
		</gene:archivio>
		
		
	</c:otherwise>
</c:choose>

<gene:fnJavaScriptScheda funzione="gestioneNsoONTipo_${param.contatore}('#NSO_ORDINANTI_TIPO_${param.contatore}#')" elencocampi="NSO_ORDINANTI_TIPO_${param.contatore}" esegui="true" />

<gene:javaScript>

function gestioneNsoONTipo_${param.contatore}(tipo){
 if(tipo=='2' || tipo=='3' ){
 	document.getElementById("rowNSO_ORDINANTI_ENDPOINT_${param.contatore}").style.display = 'none';
	document.forms[0].NSO_ORDINANTI_ENDPOINT_${param.contatore}.value = '';
 }else{
	document.getElementById("rowNSO_ORDINANTI_ENDPOINT_${param.contatore}").style.display = '';
 }
}


	
</gene:javaScript>
	
