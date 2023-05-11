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

<c:choose>

	<c:when test="${param.tipoDettaglio eq 1}">
		<gene:campoScheda title="Numero gara" entita="W3GARADOC" campo="NUMGARA_${param.contatore}" campoFittizio="true" visibile="false" definizione="N10;1;;;W3GARADOC_NUMGARA" value="${item[0]}"/>
		<gene:campoScheda title="Numero documento" entita="W3GARADOC" campo="NUMDOC_${param.contatore}" campoFittizio="true" visibile="false" definizione="N3;1;;;W3GARADOC_NUMDOC" value="${item[1]}"/>
		<gene:campoScheda title="Tipo documento" entita="W3GARADOC" campo="TIPO_DOCUMENTO_${param.contatore}" campoFittizio="true" visibile="true" definizione="T5;0;W3z16;;W3GARADOC_TIPO" value="${item[2]}"/>
		<gene:campoScheda title="Nome documento" entita="W3GARADOC" campo="NOME_DOCUMENTO_${param.contatore}" modificabile="false" campoFittizio="true" definizione="T200;0;;;W3GAREDOC_NOME" value="${item[3]}"/>
		<gene:campoScheda campo="VISUALIZZA_FILE_${param.contatore}" title="File allegato"
			campoFittizio="true" modificabile="false" definizione="T200;0" >		
				<a href='javascript:visualizzaFileAllegato("W3GARADOC_NUMGARA_${param.contatore}","W3GARADOC_NUMDOC_${param.contatore}","W3GARADOC_NOME_DOCUMENTO_${param.contatore}");'>Visualizza</a>
		</gene:campoScheda>
		<gene:campoScheda title="Note" entita="W3GARADOC" campo="NOTE_DOCUMENTO_${param.contatore}" campoFittizio="true" definizione="T2000;0;;NOTE;W3GAREDOC_NOTE" value="${item[4]}"/>	
	</c:when>

	<c:otherwise>
		<gene:campoScheda title="Numero gara" entita="W3GARADOC" campo="NUMGARA_${param.contatore}" campoFittizio="true" visibile="false" definizione="N10;1;;;W3GARADOC_NUMGARA" value="${numgara}"/>
		<gene:campoScheda title="Numero documento" entita="W3GARADOC" campo="NUMDOC_${param.contatore}" campoFittizio="true" visibile="false" definizione="N3;1;;;W3GARADOC_NUMDOC"/>
		<gene:campoScheda title="Tipo documento" entita="W3GARADOC" campo="TIPO_DOCUMENTO_${param.contatore}" campoFittizio="true" visibile="true" definizione="T5;0;W3z16;;W3GARADOC_TIPO"/>
		<gene:campoScheda title="Nome documento" entita="W3GARADOC" campo="NOME_DOCUMENTO_${param.contatore}" modificabile="false" campoFittizio="true" definizione="T200;0;;;W3GAREDOC_NOME"/>
		<c:if test='${modo eq "MODIFICA"}'>
			<gene:campoScheda title="Nome file (*)" nome="selezioneFile_${param.contatore}" >
				<input type="file" name="selFile[${param.contatore}]" id="selFile[${param.contatore}]" class="file" size="70" onkeydown="return bloccaCaratteriDaTastiera(event);" onchange='javascript:scegliFile(${param.contatore});'/>
			</gene:campoScheda>
		</c:if>
		<gene:campoScheda title="Note" entita="W3GARADOC" campo="NOTE_DOCUMENTO_${param.contatore}" campoFittizio="true" definizione="T2000;0;;NOTE;W3GAREDOC_NOTE"/>
	</c:otherwise>

</c:choose>


