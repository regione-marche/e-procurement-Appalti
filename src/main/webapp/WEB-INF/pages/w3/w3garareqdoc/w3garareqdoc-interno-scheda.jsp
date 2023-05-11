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

<c:set var="numgara" value="${gene:getValCampo(key, 'NUMGARA')}" />
<c:set var="numreq" value="${gene:getValCampo(key, 'NUMREQ')}" />

<c:choose>
	<c:when test="${param.tipoDettaglio eq 1}">
		<gene:campoScheda title="Numero gara" entita="W3GARAREQDOC" campo="NUMGARA_${param.contatore}" campoFittizio="true" visibile="false" definizione="N10;1;;;W3GARAREQDOC_NUMGARA" value="${item[0]}"/>
		<gene:campoScheda title="Numero requisito" entita="W3GARAREQDOC" campo="NUMREQ_${param.contatore}" campoFittizio="true" visibile="false" definizione="N10;1;;;W3GARAREQDOC_NUMREQ" value="${item[1]}"/>
		<gene:campoScheda title="Numero doc" entita="W3GARAREQDOC" campo="NUMDOC_${param.contatore}" campoFittizio="true" visibile="false" definizione="N10;1;;;W3GARAREQDOC_NUMDOC" value="${item[2]}"/>
		<gene:campoScheda title="Codice tipo" entita="W3GARAREQDOC" campo="CODICE_TIPO_DOC_${param.contatore}" campoFittizio="true" visibile="true" definizione="T100;0;W3029;" value="${item[3]}"/>
		<gene:campoScheda title="Descrizione" entita="W3GARAREQDOC" campo="DESCRIZIONE_${param.contatore}" campoFittizio="true" visibile="true" definizione="T800;;;;W3GARAREQDOC_DESCRIZIONE" value="${item[4]}"/>
		<gene:campoScheda title="Ente emettitore" entita="W3GARAREQDOC" campo="EMETTITORE_${param.contatore}" campoFittizio="true" visibile="true" definizione="T300;;;;W3GARAREQDOC_EMETTITORE" value="${item[5]}"/>
		<gene:campoScheda title="Fax dell'ente emettitore" entita="W3GARAREQDOC" campo="FAX_${param.contatore}" campoFittizio="true" visibile="true" definizione="T13;;;;W3GARAREQDOC_FAX" value="${item[6]}"/>
		<gene:campoScheda title="Telefono dell'ente emettitore" entita="W3GARAREQDOC" campo="TELEFONO_${param.contatore}" campoFittizio="true" visibile="true" definizione="T13;;;;W3GARAREQDOC_TELEFONO" value="${item[7]}"/>
		<gene:campoScheda title="Mail dell'ente emettitore" entita="W3GARAREQDOC" campo="MAIL_${param.contatore}" campoFittizio="true" visibile="true" definizione="T80;;;;W3GARAREQDOC_MAIL" value="${item[8]}"/>
		<gene:campoScheda title="Mail pec dell'ente emettitore" entita="W3GARAREQDOC" campo="MAIL_PEC_${param.contatore}" campoFittizio="true" visibile="true" definizione="T80;;;;W3GARAREQDOC_MAIL_PEC" value="${item[9]}"/>
	</c:when>
	<c:otherwise>
		<gene:campoScheda title="Numero gara" entita="W3GARAREQDOC" campo="NUMGARA_${param.contatore}" campoFittizio="true" visibile="false" definizione="N10;1;;;W3GARAREQDOC_NUMGARA" value="${numgara}"/>
		<gene:campoScheda title="Numero requisito" entita="W3GARAREQDOC" campo="NUMREQ_${param.contatore}" campoFittizio="true" visibile="false" definizione="N10;1;;;W3GARAREQDOC_NUMREQ" value="${numreq}"/>
		<gene:campoScheda title="Numero doc" entita="W3GARAREQDOC" campo="NUMDOC_${param.contatore}" campoFittizio="true" visibile="false" definizione="N10;1;;;W3GARAREQDOC_NUMDOC" />
		<gene:campoScheda title="Codice tipo" entita="W3GARAREQDOC" campo="CODICE_TIPO_DOC_${param.contatore}" campoFittizio="true" visibile="true" definizione="T100;0;W3029;"/>
		<gene:campoScheda title="Descrizione" entita="W3GARAREQDOC" campo="DESCRIZIONE_${param.contatore}" campoFittizio="true" visibile="true" definizione="T800;;;;W3GARAREQDOC_DESCRIZIONE" />
		<gene:campoScheda title="Ente emettitore" entita="W3GARAREQDOC" campo="EMETTITORE_${param.contatore}" campoFittizio="true" visibile="true" definizione="T300;;;;W3GARAREQDOC_EMETTITORE" />
		<gene:campoScheda title="Fax dell'ente emettitore" entita="W3GARAREQDOC" campo="FAX_${param.contatore}" campoFittizio="true" visibile="true" definizione="T13;;;;W3GARAREQDOC_FAX" />
		<gene:campoScheda title="Telefono dell'ente emettitore" entita="W3GARAREQDOC" campo="TELEFONO_${param.contatore}" campoFittizio="true" visibile="true" definizione="T13;;;;W3GARAREQDOC_TELEFONO"/>
		<gene:campoScheda title="Mail dell'ente emettitore" entita="W3GARAREQDOC" campo="MAIL_${param.contatore}" campoFittizio="true" visibile="true" definizione="T80;;;;W3GARAREQDOC_MAIL"/>
		<gene:campoScheda title="Mail pec dell'ente emettitore" entita="W3GARAREQDOC" campo="MAIL_PEC_${param.contatore}" campoFittizio="true" visibile="true" definizione="T80;;;;W3GARAREQDOC_MAIL_PEC"/>
	</c:otherwise>
</c:choose>

<gene:javaScript>
	modifySelectOption($("#W3GARAREQDOC_CODICE_TIPO_DOC_${param.contatore}"));
</gene:javaScript>

