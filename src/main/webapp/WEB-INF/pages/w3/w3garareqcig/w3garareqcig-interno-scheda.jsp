<%
	/*
	 * Created on 07-Ott-2010
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

<c:set var="numgara" value="${gene:getValCampo(keyParent, 'NUMGARA')}" />
<c:set var="numreq" value="${gene:getValCampo(keyParent, 'NUMREQ')}" />

<c:choose>
	<c:when test="${param.tipoDettaglio eq 1}">
		<gene:campoScheda title="Numero gara" entita="W3GARAREQCIG" campo="NUMGARA_${param.contatore}" campoFittizio="true" visibile="false" definizione="N10;1;;;W3GARAREQCIG_NUMGARA" value="${item[0]}"/>
		<gene:campoScheda title="Numero requisito" entita="W3GARAREQCIG" campo="NUMREQ_${param.contatore}" campoFittizio="true" visibile="false" definizione="N10;1;;;W3GARAREQCIG_NUMREQ" value="${item[1]}"/>
		<gene:campoScheda title="Numero cig" entita="W3GARAREQCIG" campo="NUMCIG_${param.contatore}" campoFittizio="true" visibile="false" definizione="N10;1;;;W3GARAREQCIG_NUMCIG" value="${item[2]}"/>
		<gene:archivio titolo="cig" 
			lista='w3/w3lott/w3lott-lista-popup.jsp' 
			scheda="" 
			schedaPopUp="" 
			campi="W3LOTT.CIG;W3LOTT.OGGETTO" 
			functionId="default"
			parametriWhere="N:${numgara}"
			chiave="W3LOTT.NUMGARA;W3LOTT.NUMREQ;W3LOTT.NUMCIG" 
			formName="" 
			inseribile="false" >
			<gene:campoScheda title="CIG" entita="W3GARAREQCIG" campo="CIG_${param.contatore}" campoFittizio="true" visibile="true" obbligatorio = "true" definizione="T10;;;;W3GARAREQCIG_CIG" value="${item[3]}"/>
			<gene:campoScheda title="Oggetto" entita="W3LOTT" campo="OGGETTO_${param.contatore}" campoFittizio="true" visibile="true" definizione="T2000;;;;W3LOTT_OGGETTO" value="${item[4]}"/>
		</gene:archivio>
		
	</c:when>
	<c:otherwise>
		<gene:campoScheda title="Numero gara" entita="W3GARAREQCIG" campo="NUMGARA_${param.contatore}" campoFittizio="true" visibile="false" definizione="N10;1;;;W3GARAREQCIG_NUMGARA" value="${numgara}"/>
		<gene:campoScheda title="Numero requisito" entita="W3GARAREQCIG" campo="NUMREQ_${param.contatore}" campoFittizio="true" visibile="false" definizione="N10;1;;;W3GARAREQCIG_NUMREQ" value="${numreq}"/>
		<gene:campoScheda title="Numero cig" entita="W3GARAREQCIG" campo="NUMCIG_${param.contatore}" campoFittizio="true" visibile="false" definizione="N10;1;;;W3GARAREQCIG_NUMCIG" />
		<gene:archivio titolo="cig" 
			lista='w3/w3lott/w3lott-lista-popup.jsp' 
			scheda="" 
			schedaPopUp="" 
			campi="W3LOTT.CIG;W3LOTT.OGGETTO" 
			functionId="default"
			parametriWhere="N:${numgara}"
			chiave="W3LOTT.NUMGARA;W3LOTT.NUMREQ;W3LOTT.NUMCIG" 
			formName="" 
			inseribile="false" >
			<gene:campoScheda title="CIG" entita="W3GARAREQCIG" campo="CIG_${param.contatore}" campoFittizio="true" visibile="true" obbligatorio = "true" definizione="T10;;;;W3GARAREQCIG_CIG" />
			<gene:campoScheda title="Oggetto" entita="W3LOTT" campo="OGGETTO_${param.contatore}" campoFittizio="true" visibile="true" definizione="T2000;;;;W3LOTT_OGGETTO" />
		</gene:archivio>
	</c:otherwise>
</c:choose>


