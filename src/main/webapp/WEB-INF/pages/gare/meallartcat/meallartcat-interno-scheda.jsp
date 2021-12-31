<%
/*
 * Created on: 09/12/2013
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
		<gene:campoScheda campo="ID_${param.contatore}" entita="MEALLARTCAT" campoFittizio="true" visibile="false" definizione="N12;1" value="${item[0]}" />
		<gene:campoScheda campo="IDARTCAT_${param.contatore}" entita="MEALLARTCAT" campoFittizio="true" visibile="false" definizione="N12;1" value="${item[1]}" />
		<gene:campoScheda campo="IDPRG_${param.contatore}" entita="MEALLARTCAT" campoFittizio="true"  visibile="false" definizione="T2;0" value="${item[2]}" />
		<gene:campoScheda campo="IDDOCDIG_${param.contatore}" entita="MEALLARTCAT" campoFittizio="true"  visibile="false" definizione="N12;0" value="${item[3]}" />
		<gene:campoScheda title="Facsimile certificato n. ${param.contatore}" campo="DIGNOMDOC_${param.contatore}" entita="W_DOCDIG" modificabile="false" campoFittizio="true" definizione="T100;0"  value="${item[4]}" href="javascript:visualizzaFileAllegato('${item[2]}','${item[3]}','${item[4]}');"/>
		<c:if test='${modo eq "MODIFICA" or modo eq "NUOVO"}'>
			<gene:campoScheda title="Carica facsimile" nome="selezioneFile_${param.contatore}" visibile="false">
					<input type="file" name="selFile[${param.contatore}]" id="selFile[${param.contatore}]" class="file" size="70" onkeydown="return bloccaCaratteriDaTastiera(event);" onchange='javascript:scegliFile(${param.contatore});'/>
			</gene:campoScheda>
		</c:if>
	</c:when>
	<c:otherwise>
		<gene:campoScheda campo="ID_${param.contatore}" entita="MEALLARTCAT" campoFittizio="true" visibile="false" definizione="N12;1" />
		<gene:campoScheda campo="IDARTCAT_${param.contatore}" entita="MEALLARTCAT" campoFittizio="true" visibile="false" definizione="N12;1" />
		<gene:campoScheda campo="IDPRG_${param.contatore}" entita="MEALLARTCAT" campoFittizio="true"  visibile="false" definizione="T2;0" />
		<gene:campoScheda campo="IDDOCDIG_${param.contatore}" entita="MEALLARTCAT" campoFittizio="true"  visibile="false" definizione="N12;0" />
		<gene:campoScheda title="Facsimile certificato n. ${param.contatore}" campo="DIGNOMDOC_${param.contatore}" entita="W_DOCDIG" modificabile="false" campoFittizio="true" definizione="T100;0" visibile="false"/>
		<c:if test='${modo eq "MODIFICA" or modo eq "NUOVO"}'>
			<gene:campoScheda title="Carica facsimile" nome="selezioneFile_${param.contatore}">
					<input type="file" name="selFile[${param.contatore}]" id="selFile[${param.contatore}]" class="file" size="70" onkeydown="return bloccaCaratteriDaTastiera(event);" onchange='javascript:scegliFile(${param.contatore});'/>
			</gene:campoScheda>
		</c:if>
	</c:otherwise>
</c:choose>

