
<%
/*
 * Created on: 17/05/2021
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
		<gene:campoScheda campo="NUMORD_${param.contatore}" entita="G1ARCDOCUMOD" obbligatorio="true" campoFittizio="true" definizione="F6.2;;;;G1NUMORDACD" value="${item[0]}" />
		<gene:campoScheda campo="FASE_${param.contatore}" entita="G1ARCDOCUMOD" visibile='${datiRiga.G1DOCUMOD_GRUPPO eq 20}' obbligatorio="true" campoFittizio="true" definizione="N7;;A1181;;G1FASEACD" value="${item[1]}" />
		<gene:campoScheda campo="DESCRIZIONE_${param.contatore}" entita="G1ARCDOCUMOD" obbligatorio="true" campoFittizio="true" definizione="T2000;;;;G1DESCLIBACD" value="${item[2]}" />
		<gene:campoScheda campo="ULTDESC_${param.contatore}" entita="G1ARCDOCUMOD" visibile='${datiRiga.G1DOCUMOD_GRUPPO eq 20}' campoFittizio="true" definizione="T2000;;;NOTE;G1ULTDESCACD" value="${item[3]}" />
		<gene:campoScheda campo="VISIBILITA_${param.contatore}" entita="G1ARCDOCUMOD" visibile='${datiRiga.G1DOCUMOD_GRUPPO eq 20}' obbligatorio="true" campoFittizio="true" definizione="N7;;A1182;;G1VISIBIACD" value="${item[4]}" />
		<gene:campoScheda campo="OBBLIGATORIO_${param.contatore}" entita="G1ARCDOCUMOD" visibile='${datiRiga.G1DOCUMOD_GRUPPO eq 3 || datiRiga.G1DOCUMOD_GRUPPO eq 20}' campoFittizio="true" definizione="T2;;;SN;G1OBBLIGACD" value="${item[5]}" />
		<gene:campoScheda campo="IDDOCUMOD_${param.contatore}" entita="G1ARCDOCUMOD" visibile="false" campoFittizio="true" definizione="N12;;;;G1IDDOCUMACD" value="${item[6]}" />
		<gene:campoScheda campo="ID_${param.contatore}" entita="G1ARCDOCUMOD" visibile="false" campoFittizio="true" definizione="N12;1;;;G1IDARCDOC" value="${item[7]}" />
		<gene:campoScheda campo="REQCAP_${param.contatore}" entita="G1ARCDOCUMOD" visibile='${datiRiga.G1DOCUMOD_GRUPPO eq 3 && (datiRiga.G1DOCUMOD_BUSTA eq 1 || datiRiga.G1DOCUMOD_BUSTA eq 4)}' campoFittizio="true" definizione="N7;;A1059;;G1REQCAPACD" value="${item[8]}" />
		<gene:campoScheda campo="TIPODOC_${param.contatore}" entita="G1ARCDOCUMOD" visibile='${datiRiga.G1DOCUMOD_GRUPPO eq 3}' campoFittizio="true" definizione="N7;;A1057;;G1TIPODOCACD" value="${item[9]}" />
		<gene:campoScheda campo="CONTESTOVAL_${param.contatore}" entita="G1ARCDOCUMOD" visibile='${datiRiga.G1DOCUMOD_GRUPPO eq 3}' campoFittizio="true" definizione="N7;;Ag008;;G1CONTVALACD" value="${item[10]}" />
		<gene:campoScheda campo="MODFIRMA_${param.contatore}" entita="G1ARCDOCUMOD" visibile='${datiRiga.G1DOCUMOD_GRUPPO eq 3 || datiRiga.G1DOCUMOD_GRUPPO eq 20}' campoFittizio="true" definizione="N7;;A1105;;G1MODFIRMACD" value="${item[11]}" />
		<gene:campoScheda campo="IDSTAMPA_${param.contatore}" entita="G1ARCDOCUMOD" visibile='${datiRiga.G1DOCUMOD_GRUPPO eq 1 || datiRiga.G1DOCUMOD_GRUPPO eq 4 || datiRiga.G1DOCUMOD_GRUPPO eq 6}' campoFittizio="true" definizione="T20;;;;G1IDSTAMPACD" value="${item[12]}" />		
		<gene:campoScheda campo="ALLMAIL_${param.contatore}" entita="G1ARCDOCUMOD" visibile='${datiRiga.G1DOCUMOD_GRUPPO eq 6}' campoFittizio="true" definizione="T1;;;SN;G1ALLMAILACD" value="${item[13]}" />	
		<gene:campoScheda campo="IDPRG_${param.contatore}" entita="G1ARCDOCUMOD" visibile="false" campoFittizio="true" definizione="T2;;;;G1IDPRGACD" value="${item[14]}" />
		<gene:campoScheda campo="IDDOCDG_${param.contatore}" entita="G1ARCDOCUMOD" campoFittizio="true"  visibile="false" definizione="N12;0;;;G1IDDOCDGACD" value="${item[15]}" />
		<gene:campoScheda campo="IDPRG_${param.contatore}" entita="W_DOCDIG" campoFittizio="true" visibile="false" definizione="T2;1;;;DIGIDPRG" value="${item[14]}"/>
		<gene:campoScheda campo="IDDOCDIG_${param.contatore}" entita="W_DOCDIG" campoFittizio="true" visibile="false" definizione="N12;1;;;DIGID"  value="${item[15]}" />
		<gene:campoScheda title="Nome documento" campo="DIGNOMDOC_${param.contatore}" entita="W_DOCDIG" visibile='${datiRiga.G1DOCUMOD_GRUPPO ne 20 && datiRiga.G1DOCUMOD_GRUPPO ne 2}' modificabile="false" campoFittizio="true" definizione="T100;0"  value="${item[16]}" href="javascript:visualizzaFileAllegato('${item[14]}','${item[15]}','${item[16]}');"/>
		<c:if test='${modo eq "MODIFICA" or modo eq "NUOVO"}'>
			<gene:campoScheda title="Carica allegato" nome="selezioneFile_${param.contatore}" visibile='${datiRiga.G1DOCUMOD_GRUPPO ne 20 && datiRiga.G1DOCUMOD_GRUPPO ne 2}'>
					<input type="file" name="selFile[${param.contatore}]" id="selFile[${param.contatore}]" class="file" size="70" onkeydown="return bloccaCaratteriDaTastiera(event);" onchange='javascript:scegliFile(${param.contatore});'/>
			</gene:campoScheda>
		</c:if>
	</c:when>
	<c:otherwise>
		<gene:campoScheda campo="NUMORD_${param.contatore}" entita="G1ARCDOCUMOD" obbligatorio="true" campoFittizio="true" definizione="F6.2;;;;G1NUMORDACD" value="1" />
		<gene:campoScheda campo="FASE_${param.contatore}" entita="G1ARCDOCUMOD" visibile='${datiRiga.G1DOCUMOD_GRUPPO eq 20}' obbligatorio="true" campoFittizio="true" definizione="N7;;A1181;;G1FASEACD" />
		<gene:campoScheda campo="DESCRIZIONE_${param.contatore}" entita="G1ARCDOCUMOD" obbligatorio="true" campoFittizio="true" definizione="T2000;;;;G1DESCLIBACD" />
		<gene:campoScheda campo="ULTDESC_${param.contatore}" entita="G1ARCDOCUMOD" visibile='${datiRiga.G1DOCUMOD_GRUPPO eq 20}' campoFittizio="true" definizione="T2000;;;NOTE;G1ULTDESCACD" />
		<gene:campoScheda campo="VISIBILITA_${param.contatore}" entita="G1ARCDOCUMOD" visibile='${datiRiga.G1DOCUMOD_GRUPPO eq 20}' obbligatorio="true" campoFittizio="true" definizione="N7;;A1182;;G1VISIBIACD" />
		<gene:campoScheda campo="OBBLIGATORIO_${param.contatore}" entita="G1ARCDOCUMOD" visibile='${datiRiga.G1DOCUMOD_GRUPPO eq 3 || datiRiga.G1DOCUMOD_GRUPPO eq 20}' campoFittizio="true" definizione="T2;;;SN;G1OBBLIGACD" />
		<gene:campoScheda campo="IDDOCUMOD_${param.contatore}" entita="G1ARCDOCUMOD" visibile="false" campoFittizio="true" definizione="N12;;;;G1IDDOCUMACD" value="${param.chiave}" />
		<gene:campoScheda campo="ID_${param.contatore}" entita="G1ARCDOCUMOD" visibile="false" campoFittizio="true" definizione="N12;1;;;G1IDARCDOC" />
		<gene:campoScheda campo="REQCAP_${param.contatore}" entita="G1ARCDOCUMOD" visibile='${datiRiga.G1DOCUMOD_GRUPPO eq 3 && (datiRiga.G1DOCUMOD_BUSTA eq 1 || datiRiga.G1DOCUMOD_BUSTA eq 4)}' campoFittizio="true" definizione="N7;;A1059;;G1REQCAPACD" />
		<gene:campoScheda campo="TIPODOC_${param.contatore}" entita="G1ARCDOCUMOD" visibile='${datiRiga.G1DOCUMOD_GRUPPO eq 3}' campoFittizio="true" definizione="N7;;A1057;;G1TIPODOCACD" />
		<gene:campoScheda campo="CONTESTOVAL_${param.contatore}" entita="G1ARCDOCUMOD" visibile='${datiRiga.G1DOCUMOD_GRUPPO eq 3}' campoFittizio="true" definizione="N7;;Ag008;;G1CONTVALACD" />
		<gene:campoScheda campo="MODFIRMA_${param.contatore}" entita="G1ARCDOCUMOD" visibile='${datiRiga.G1DOCUMOD_GRUPPO eq 3 || datiRiga.G1DOCUMOD_GRUPPO eq 20}' campoFittizio="true" definizione="N7;;A1105;;G1MODFIRMACD" />
		<gene:campoScheda campo="IDSTAMPA_${param.contatore}" entita="G1ARCDOCUMOD" visibile='${datiRiga.G1DOCUMOD_GRUPPO eq 1 || datiRiga.G1DOCUMOD_GRUPPO eq 4 || datiRiga.G1DOCUMOD_GRUPPO eq 6}' campoFittizio="true" definizione="T20;;;;G1IDSTAMPACD" />
		<gene:campoScheda campo="ALLMAIL_${param.contatore}" entita="G1ARCDOCUMOD" visibile='${datiRiga.G1DOCUMOD_GRUPPO eq 6}' campoFittizio="true" definizione="T1;;;SN;G1ALLMAILACD" />
		<gene:campoScheda campo="IDPRG_${param.contatore}" entita="G1ARCDOCUMOD" campoFittizio="true"  visibile="false" definizione="T2;0;;;G1IDPRGACD"  />
		<gene:campoScheda campo="IDDOCDG_${param.contatore}" entita="G1ARCDOCUMOD" campoFittizio="true"  visibile="false" definizione="N12;0;;;G1IDDOCDGACD"  />
		<gene:campoScheda campo="IDPRG_${param.contatore}" entita="W_DOCDIG" campoFittizio="true" visibile="false" definizione="T2;1;;;DIGIDPRG" />
		<gene:campoScheda campo="IDDOCDIG_${param.contatore}" entita="W_DOCDIG" campoFittizio="true" visibile="false" definizione="N12;1;;;DIGID"  />
		<gene:campoScheda campo="DIGDESDOC_${param.contatore}" entita="W_DOCDIG" campoFittizio="true" visibile="false" definizione="T2000;0;;;DIGDESDOC"  gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoNote"/>
		<gene:campoScheda campo="DIGNOMDOC_${param.contatore}" entita="W_DOCDIG" modificabile="false" campoFittizio="true" definizione="T100;0;;;DIGNOMDOC"  value='' visibile='${datiRiga.G1DOCUMOD_GRUPPO ne 20 && datiRiga.G1DOCUMOD_GRUPPO ne 2}'/>
		<gene:campoScheda title="Nome documento" campo="DIGNOMDOC_${param.contatore}" entita="W_DOCDIG" modificabile="false" campoFittizio="true" definizione="T100;0" visibile="false"/>
		<c:if test='${modo eq "MODIFICA" or modo eq "NUOVO"}'>
			<gene:campoScheda title="Carica allegato" nome="selezioneFile_${param.contatore}" visibile='${datiRiga.G1DOCUMOD_GRUPPO ne 20 && datiRiga.G1DOCUMOD_GRUPPO ne 2}'>
					<input type="file" name="selFile[${param.contatore}]" id="selFile[${param.contatore}]" class="file" size="70" onkeydown="return bloccaCaratteriDaTastiera(event);" onchange='javascript:scegliFile(${param.contatore});'/>
			</gene:campoScheda>
		</c:if>
	</c:otherwise>
</c:choose>
