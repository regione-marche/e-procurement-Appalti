<%
/*
 * Created on: 04/06/18
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

<c:set var="integrazioneERPvsWSDM" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneERPvsWSDMFunction", pageContext, idconfi)}'/>

<c:choose>
	<c:when test="${param.tipoDettaglio eq 1}">
		<gene:campoScheda campo="ID_${param.contatore}" entita="GARERDA" campoFittizio="true" visibile="false" definizione="N10;1;;;G1IDGARERDA" value="${item[0]}" />
		<gene:campoScheda campo="CODGAR_${param.contatore}" entita="GARERDA" campoFittizio="true" visibile="false" definizione="T21;0;;;G1CODGARRDA" value="${item[1]}" />
		<gene:campoScheda campo="CODCARR_${param.contatore}" entita="GARERDA" campoFittizio="true" definizione="T50;0;;;G1CODCARR" value="${item[13]}" visibile="false" />
		<c:choose>
		<c:when test='${integrazioneERPvsWSDM eq "1" || (integrazioneWSERP eq "1" && (tipoWSERP eq "FNM" || tipoWSERP eq "CAV"))}'>
		<gene:campoScheda campo="NUMRDA_${param.contatore}" entita="GARERDA" campoFittizio="true" definizione="T50;0;;;G1NUMRDA" value="${item[4]}" modificabile="${integrazioneWSERP ne '1'}" href="javascript:visMetaDati('${item[4]}','${item[16]}','${tipoWSERP}');"/>
		</c:when>
		<c:otherwise>
		<gene:campoScheda campo="NUMRDA_${param.contatore}" entita="GARERDA" campoFittizio="true" definizione="T50;0;;;G1NUMRDA" value="${item[4]}" modificabile="${integrazioneWSERP ne '1'}" />
		</c:otherwise>
		</c:choose>
		<gene:campoScheda campo="POSRDA_${param.contatore}" entita="GARERDA" campoFittizio="true" definizione="T50;0;;;G1POSRDA" value="${item[5]}" visibile="${integrazioneWSERP eq '1'}" modificabile="false" />
		<gene:campoScheda campo="DATCRE_${param.contatore}" entita="GARERDA" campoFittizio="true" definizione="D;0;;;G1DATCRERDA" value="${item[2]}" />
		<gene:campoScheda campo="DATRIL_${param.contatore}" entita="GARERDA" campoFittizio="true" definizione="D;0;;;G1DATRILRDA" value="${item[3]}" />
		<gene:campoScheda campo="DATACONS_${param.contatore}" entita="GARERDA" campoFittizio="true" definizione="D;0;;;G1DATACONS" value="${item[6]}" visibile="${integrazioneWSERP eq '1' && tipoWSERP eq 'AVM'}"/>
		<gene:campoScheda campo="LUOGOCONS_${param.contatore}" entita="GARERDA" campoFittizio="true" definizione="T30;0;;;G1LUOGOCONS" value="${item[7]}" visibile="${integrazioneWSERP eq '1' && tipoWSERP eq 'AVM'}"/>
		<gene:campoScheda campo="CODVOC_${param.contatore}" entita="GARERDA" campoFittizio="true" definizione="T20;0;;;G1CODVOCRDA" value="${item[8]}" visibile="${integrazioneWSERP eq '1'}"/>
		<gene:campoScheda campo="VOCE_${param.contatore}" entita="GARERDA" campoFittizio="true" definizione="T2000;0;;;G1VOCERDA" value="${item[9]}" visibile="${integrazioneWSERP eq '1'}"/>
		<gene:campoScheda campo="CODCAT_${param.contatore}" entita="GARERDA" campoFittizio="true" definizione="T30;0;;;G1CODCATRDA" value="${item[11]}" visibile="${integrazioneWSERP eq '1' && tipoWSERP eq 'UGOVPA'}"/>
		<gene:campoScheda title="Unit&agrave; di misura" campo="UNIMIS_${param.contatore}" entita="GARERDA" campoFittizio="true" definizione="T10;0;;;G1UNIMISRDA" value="${item[10]}" visibile="${integrazioneWSERP eq '1' && tipoWSERP eq 'UGOVPA'}"/>
		<gene:campoScheda title="Quantit&agrave;" campo="QUANTI_${param.contatore}" entita="GARERDA" campoFittizio="true" definizione="F12.3;0;;;G1QUANTRDA" value="${item[14]}" visibile="${integrazioneWSERP eq '1'}"/>
		<gene:campoScheda title="Importo" campo="PREZUN_${param.contatore}" entita="GARERDA" campoFittizio="true" definizione="F15;0;;;G1PREUNRDA" value="${item[15]}" visibile="${integrazioneWSERP eq '1'}"/>
		<gene:campoScheda campo="PERCIVA_${param.contatore}" entita="GARERDA" campoFittizio="true" definizione="N2;0;;;G1PEIVARDA" value="${item[12]}" visibile="${integrazioneWSERP eq '1' && tipoWSERP eq 'UGOVPA'}"/>
		<gene:campoScheda campo="ESERCIZIO_${param.contatore}" entita="GARERDA" campoFittizio="true" definizione="T4;0;;;G1ESERCIZIORDA" value="${item[16]}" visibile="${integrazioneWSERP eq '1' && tipoWSERP eq 'FNM'}"/>
		<gene:campoScheda campo="NGARA_${param.contatore}" entita="GARERDA" campoFittizio="true" visibile="false" definizione="T20;0;;;G1CODGARRDA" value="${item[17]}" />
	</c:when>
	<c:otherwise>
		<gene:campoScheda campo="ID_${param.contatore}" entita="GARERDA" campoFittizio="true" visibile="false" definizione="N10;1;;;G1IDGARERDA" />
		<gene:campoScheda campo="CODGAR_${param.contatore}" entita="GARERDA" campoFittizio="true" visibile="false" definizione="T21;0;;;G1CODGARRDA" value="${param.chiave}" />
		<gene:campoScheda campo="CODCARR_${param.contatore}" entita="GARERDA" campoFittizio="true" definizione="T50;0;;;G1CODCARR" visibile="false" />
		<gene:campoScheda campo="NUMRDA_${param.contatore}" entita="GARERDA" campoFittizio="true" definizione="T30;0;;;G1NUMRDA" modificabile="${integrazioneWSERP ne '1'}"/>
		<gene:campoScheda campo="POSRDA_${param.contatore}" entita="GARERDA" campoFittizio="true" definizione="T20;0;;;G1POSRDA" visibile="${integrazioneWSERP eq '1'}" modificabile="false" />
		<gene:campoScheda campo="DATCRE_${param.contatore}" entita="GARERDA" campoFittizio="true" definizione="D;0;;;G1DATCRERDA" />
		<gene:campoScheda campo="DATRIL_${param.contatore}" entita="GARERDA" campoFittizio="true" definizione="D;0;;;G1DATRILRDA" />
		<gene:campoScheda campo="DATACONS_${param.contatore}" entita="GARERDA" campoFittizio="true" definizione="D;0;;;G1DATACONS" visibile="${integrazioneWSERP eq '1' && tipoWSERP eq 'AVM'}" />
		<gene:campoScheda campo="LUOGOCONS_${param.contatore}" entita="GARERDA" campoFittizio="true" definizione="T30;0;;;G1LUOGOCONS" visibile="${integrazioneWSERP eq '1' && tipoWSERP eq 'AVM'}" />
		<gene:campoScheda campo="CODVOC_${param.contatore}" entita="GARERDA" campoFittizio="true" definizione="T20;0;;;G1CODVOCRDA" visibile="${integrazioneWSERP eq '1'}"/>
		<gene:campoScheda campo="VOCE_${param.contatore}" entita="GARERDA" campoFittizio="true" definizione="T2000;0;;;G1VOCERDA" visibile="${integrazioneWSERP eq '1'}"/>
		<gene:campoScheda campo="CODCAT_${param.contatore}" entita="GARERDA" campoFittizio="true" definizione="T30;0;;;G1CODCATRDA" visibile="${integrazioneWSERP eq '1' && tipoWSERP eq 'UGOVPA'}"/>
		<gene:campoScheda title="Unit&agrave; di misura" campo="UNIMIS_${param.contatore}" entita="GARERDA" campoFittizio="true" definizione="T10;0;;;G1UNIMISRDA" visibile="${integrazioneWSERP eq '1' && tipoWSERP eq 'UGOVPA'}"/>
		<gene:campoScheda title="Quantit&agrave;" campo="QUANTI_${param.contatore}" entita="GARERDA" campoFittizio="true" definizione="F12.3;0;;;G1QUANTRDA"  visibile="${integrazioneWSERP eq '1'}"/>
		<gene:campoScheda title="Importo" campo="PREZUN_${param.contatore}" entita="GARERDA" campoFittizio="true" definizione="F15;0;;;G1PREUNRDA"  visibile="${integrazioneWSERP eq '1'}"/>
		<gene:campoScheda campo="PERCIVA_${param.contatore}" entita="GARERDA" campoFittizio="true" definizione="N2;0;;;G1PEIVARDA" visibile="${integrazioneWSERP eq '1' && tipoWSERP eq 'UGOVPA'}"/>
		<gene:campoScheda campo="ESERCIZIO_${param.contatore}" entita="GARERDA" campoFittizio="true" definizione="T4;0;;;G1ESERCIZIORDA" visibile="${integrazioneWSERP eq '1' && tipoWSERP eq 'FNM'}"/>
		<gene:campoScheda campo="NGARA_${param.contatore}" entita="GARERDA" campoFittizio="true" visibile="false" definizione="T20;0;;;G1NGARARDA" />
	</c:otherwise>
</c:choose>