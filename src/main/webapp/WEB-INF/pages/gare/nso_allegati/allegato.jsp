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
		<gene:campoScheda campo="ID_${param.contatore}" entita="NSO_ALLEGATI" campoFittizio="true" visibile="false" definizione="N12;1;;;NSO_AL_ID" value="${item[0]}" />
		<gene:campoScheda campo="NSO_ORDINI_ID_${param.contatore}" entita="NSO_ALLEGATI" campoFittizio="true" visibile="false" definizione="N12;1;;;NSO_AL_ORDID" value="${item[1]}" />
		<gene:campoScheda title="Num.Progr." campo="NPROGR_${param.contatore}" entita="NSO_ALLEGATI" campoFittizio="true" visibile="true" modificabile="false" definizione="N7;1;;;NSO_AL_NPROGR" value="${item[2]}" />
		<gene:campoScheda title="Tipo documento" campo="TIPODOC_${param.contatore}" entita="NSO_ALLEGATI" campoFittizio="true" visibile="true" definizione="N7;0;A1057;;NSO_AL_TIPODOC" value="${item[3]}" />
		<gene:campoScheda title="Descrizione" campo="DESCRIZIONE_${param.contatore}" entita="NSO_ALLEGATI" campoFittizio="true"  obbligatorio="true" definizione="T2000;;;;NSO_AL_DESCR" value="${item[4]}" />
		<gene:campoScheda campo="IDPRG_${param.contatore}" entita="NSO_ALLEGATI" campoFittizio="true"  visibile="false" definizione="T2;0;;;NSO_AL_IDPRG" value="${item[5]}" />
		<gene:campoScheda campo="IDDOCDIG_${param.contatore}" entita="NSO_ALLEGATI" campoFittizio="true"  visibile="false" definizione="N12;0;;;NSO_AL_IDDOCDG" value="${item[6]}" />
		<gene:campoScheda campo="IDPRG_${param.contatore}" entita="W_DOCDIG" campoFittizio="true" visibile="false" definizione="T2;1;;;DIGIDPRG" value="${item[5]}"/>
		<gene:campoScheda campo="IDDOCDIG_${param.contatore}" entita="W_DOCDIG" campoFittizio="true" visibile="false" definizione="N12;1;;;DIGID"  value="${item[6]}" />
		<gene:campoScheda title="Nome documento" campo="DIGNOMDOC_${param.contatore}" entita="W_DOCDIG" modificabile="false" campoFittizio="true" definizione="T100;0"  value="${item[7]}" href="javascript:visualizzaFileAllegato('${item[5]}','${item[6]}','${item[7]}');"/>
		<c:if test='${modo eq "MODIFICA" or modo eq "NUOVO"}'>
			<gene:campoScheda title="Carica allegato" nome="selezioneFile_${param.contatore}" >
					<input type="file" name="selFile[${param.contatore}]" id="selFile[${param.contatore}]" class="file" size="70" onkeydown="return bloccaCaratteriDaTastiera(event);" onchange='javascript:scegliFile(${param.contatore});'/>
			</gene:campoScheda>
		</c:if>
		<gene:campoScheda title="Data rilascio" campo="DATARILASCIO_${param.contatore}" entita="NSO_ALLEGATI" campoFittizio="true"  visibile="true" definizione="D;0;;;NSO_AL_DATRIL" value="${item[8]}" />		
		<gene:campoScheda title="Data scadenza" campo="DATASCADENZA_${param.contatore}" entita="NSO_ALLEGATI" campoFittizio="true"  visibile="true" definizione="D;0;;;NSO_AL_DATSCAD" value="${item[9]}" />
	</c:when>
	<c:otherwise>
		<gene:campoScheda campo="ID_${param.contatore}" entita="NSO_ALLEGATI" campoFittizio="true" visibile="false" definizione="N12;1;;;NSO_AL_ID"  />
		<gene:campoScheda campo="NSO_ORDINI_ID_${param.contatore}" entita="NSO_ALLEGATI" campoFittizio="true" visibile="false" definizione="N12;1;;;NSO_AL_ORDID"  />
		<gene:campoScheda title="Num.Progr." campo="NPROGR_${param.contatore}" entita="NSO_ALLEGATI" campoFittizio="true" visibile="true" modificabile="false" definizione="N7;1;;;NSO_AL_NPROGR"  />
		<gene:campoScheda title="Tipo documento" campo="TIPODOC_${param.contatore}" entita="NSO_ALLEGATI" campoFittizio="true" visibile="true" definizione="N7;0;A1057;;NSO_AL_TIPODOC"  />
		<gene:campoScheda title="Descrizione" campo="DESCRIZIONE_${param.contatore}" entita="NSO_ALLEGATI" campoFittizio="true"  obbligatorio="true" definizione="T2000;;;;NSO_AL_DESCR"  />
		<gene:campoScheda campo="IDPRG_${param.contatore}" entita="NSO_ALLEGATI" campoFittizio="true"  visibile="false" definizione="T2;0;;;NSO_AL_IDPRG"  />
		<gene:campoScheda campo="IDDOCDIG_${param.contatore}" entita="NSO_ALLEGATI" campoFittizio="true"  visibile="false" definizione="N12;0;;;NSO_AL_IDDOCDG"  />
		<gene:campoScheda campo="IDPRG_${param.contatore}" entita="W_DOCDIG" campoFittizio="true" visibile="false" definizione="T2;1;;;DIGIDPRG" />
		<gene:campoScheda campo="IDDOCDIG_${param.contatore}" entita="W_DOCDIG" campoFittizio="true" visibile="false" definizione="N12;1;;;DIGID"  />
		<gene:campoScheda campo="DIGDESDOC_${param.contatore}" entita="W_DOCDIG" campoFittizio="true" visibile="false" definizione="T2000;0;;;DIGDESDOC"  gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoNote"/>
		<gene:campoScheda campo="DIGNOMDOC_${param.contatore}" entita="W_DOCDIG" modificabile="false" campoFittizio="true" definizione="T100;0;;;DIGNOMDOC"  value=''/>
		<gene:campoScheda title="Nome documento" campo="DIGNOMDOC_${param.contatore}" entita="W_DOCDIG" modificabile="false" campoFittizio="true" definizione="T100;0" visibile="false"/>
		<c:if test='${modo eq "MODIFICA" or modo eq "NUOVO"}'>
			<gene:campoScheda title="Carica allegato" nome="selezioneFile_${param.contatore}">
					<input type="file" name="selFile[${param.contatore}]" id="selFile[${param.contatore}]" class="file" size="70" onkeydown="return bloccaCaratteriDaTastiera(event);" onchange='javascript:scegliFile(${param.contatore});'/>
			</gene:campoScheda>
		</c:if>
		<gene:campoScheda title="Data rilascio" campo="DATARILASCIO_${param.contatore}" entita="NSO_ALLEGATI" campoFittizio="true"  visibile="true" definizione="D;0;;;NSO_AL_DATRIL"  />
		<gene:campoScheda title="Data scadenza" campo="DATASCADENZA_${param.contatore}" entita="NSO_ALLEGATI" campoFittizio="true"  visibile="true" definizione="D;0;;;NSO_AL_DATSCAD" />		
	</c:otherwise>
</c:choose>

<gene:javaScript>
	
</gene:javaScript>
	
