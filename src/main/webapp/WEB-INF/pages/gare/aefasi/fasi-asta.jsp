<%
/*
 * Created on: 28/10/2008
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:choose>
	<c:when test="${param.tipoDettaglio eq 1}">
		<gene:campoScheda campo="NGARA_${param.contatore}" entita="AEFASI" campoFittizio="true" visibile="false" definizione="T21;0;" value="${item[0]}" />
		<gene:campoScheda campo="ID_${param.contatore}" entita="AEFASI" campoFittizio="true" visibile="false" definizione="N12;1;" value="${item[1]}" />
		<gene:campoScheda campo="DATINI_${param.contatore}" entita="AEFASI" campoFittizio="true" definizione="D;0;;DATA_ELDA;G1DATINIAF" value="${item[2]}" obbligatorio="true"/>
		<gene:campoScheda campo="ORAINI_${param.contatore}" entita="AEFASI" campoFittizio="true" definizione="T6;0;;ORA;G1ORAINIAF" value="${item[3]}" obbligatorio="true"/>
		<gene:campoScheda campo="DURMIN_${param.contatore}" entita="AEFASI" campoFittizio="true" definizione="N5;0;;;G1DURMINAF" value="${item[4]}" obbligatorio="true"/>
		<gene:campoScheda campo="DURMAX_${param.contatore}" entita="AEFASI" campoFittizio="true" definizione="N5;0;;;G1DURMAXAF" value="${item[5]}" obbligatorio="true"/>
		<gene:campoScheda campo="TBASE_${param.contatore}" entita="AEFASI" campoFittizio="true" definizione="N5;0;;;G1TBASEAF" value="${item[6]}" obbligatorio="true"/>
		<gene:campoScheda campo="DATAORAINI_${param.contatore}" entita="AEFASI" campoFittizio="true" definizione="D" value="${item[7]}" visibile="false"/>
		<gene:campoScheda campo="DATAORAFINE_${param.contatore}" entita="AEFASI" campoFittizio="true" definizione="D" value="${item[8]}" visibile="false"/>
		<gene:campoScheda campo="DATAORAINI_FIT_${param.contatore}" entita="AEFASI" campoFittizio="true" definizione="T20" visibile="false"/>
		<gene:campoScheda campo="DATAORAFINE_FIT_${param.contatore}" entita="AEFASI" campoFittizio="true" definizione="T20" visibile="false"/>
		
	</c:when>
	<c:otherwise>
		<gene:campoScheda campo="NGARA_${param.contatore}" entita="AEFASI" campoFittizio="true" visibile="false" definizione="T21;0;" value="${param.chiave}" />
		<gene:campoScheda campo="ID_${param.contatore}" entita="AEFASI" campoFittizio="true" visibile="false" definizione="N12;1;"  />
		<gene:campoScheda campo="DATINI_${param.contatore}" entita="AEFASI" campoFittizio="true" definizione="D;0;;DATA_ELDA;G1DATINIAF"  obbligatorio="true"/>
		<gene:campoScheda campo="ORAINI_${param.contatore}" entita="AEFASI" campoFittizio="true" definizione="T6;0;;ORA;G1ORAINIAF"  obbligatorio="true"/>
		<gene:campoScheda campo="DURMIN_${param.contatore}" entita="AEFASI" campoFittizio="true" definizione="N5;0;;;G1DURMINAF"  obbligatorio="true"/>
		<gene:campoScheda campo="DURMAX_${param.contatore}" entita="AEFASI" campoFittizio="true" definizione="N5;0;;;G1DURMAXAF"  obbligatorio="true"/>
		<gene:campoScheda campo="TBASE_${param.contatore}" entita="AEFASI" campoFittizio="true" definizione="N5;0;;;G1TBASEAF"  obbligatorio="true"/>
		<gene:campoScheda campo="DATAORAINI_${param.contatore}" entita="AEFASI" campoFittizio="true" definizione="D" visibile="false"/>
		<gene:campoScheda campo="DATAORAFINE_${param.contatore}" entita="AEFASI" campoFittizio="true" definizione="D" visibile="false"/>
		<gene:campoScheda campo="DATAORAINI_FIT_${param.contatore}" entita="AEFASI" campoFittizio="true" definizione="T20" visibile="false"/>
		<gene:campoScheda campo="DATAORAFINE_FIT_${param.contatore}" entita="AEFASI" campoFittizio="true" definizione="T20" visibile="false"/>
	</c:otherwise>
</c:choose>
<gene:fnJavaScriptScheda funzione='aggiornaDate("#AEFASI_DATINI_${param.contatore}#","#AEFASI_ORAINI_${param.contatore}#","#AEFASI_DURMIN_${param.contatore}#",${param.contatore})' elencocampi="AEFASI_DATINI_${param.contatore};AEFASI_ORAINI_${param.contatore};AEFASI_DURMIN_${param.contatore}" esegui="false" />
