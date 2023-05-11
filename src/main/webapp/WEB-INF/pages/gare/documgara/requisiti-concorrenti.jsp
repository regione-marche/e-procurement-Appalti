<%
/*
 * Created on: 12/11/2008
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

<c:set var="numeroGara" value='${fn:substringAfter(param.chiave, ";")}' />
<c:set var="codiceGara" value='${fn:substringBefore(param.chiave, ";")}' />
<c:choose>
	<c:when test='${item[7] eq "5" and param.tipoDettaglio eq 1}'>
		<c:set var="modificabile" value='false' />
	</c:when>
	<c:otherwise>
		<c:set var="modificabile" value='true' />
	</c:otherwise>
</c:choose>

<c:choose>
<c:when test="${param.tipoDettaglio eq 1}">
				<c:choose>
					<c:when test="${!empty param.campiEsistentiModificabiliDaProfilo }">
						<c:set var="campiEsistentiModificabiliDaProfilo" value="${param.campiEsistentiModificabiliDaProfilo}" />
					</c:when>
					<c:otherwise>
						<c:set var="campiEsistentiModificabiliDaProfilo" value="true" />
					</c:otherwise>
				</c:choose>
				<gene:campoScheda campo="CODGAR_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" visibile="false" definizione="T21;1;;;CODGARDG" value="${item[0]}" />
				<gene:campoScheda campo="NORDDOCG_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" visibile="false" definizione="N3;1;;;NORDDOCGDG" value="${item[2]}" />
				<gene:campoScheda campo="GRUPPO_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  visibile="false" definizione="N3;0;;;GRUPPODG" value="${item[3]}"/>
				<gene:campoScheda campo="IDPRG_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  visibile="false" definizione="T2;0;;;IDPRGDG" value="${item[5]}" />
				<gene:campoScheda campo="IDDOCDG_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  visibile="false" definizione="N12;0;;;IDDOCDGDG" value="${item[6]}" />
				<gene:campoScheda campo="IDPRG_${param.contatore}" entita="W_DOCDIG" campoFittizio="true" visibile="false" definizione="T2;1;;;DIGIDPRG" value="${item[5]}"/>
				<gene:campoScheda campo="IDDOCDIG_${param.contatore}" entita="W_DOCDIG" campoFittizio="true" visibile="false" definizione="N12;1;;;DIGID"  value="${item[6]}" />
				<gene:campoScheda campo="DIGDESDOC_${param.contatore}" entita="W_DOCDIG" campoFittizio="true" visibile="false" definizione="T2000;0;;;DIGDESDOC"  gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoNote" value="${documentiGaraDescFile[(3 * param.contatore) - 3]}"/>
				<gene:campoScheda campo="DESCRIZIONE_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" obbligatorio="true" definizione="T200;0;;;DESCLIBDG" value="${item[13]}" modificabile='${campiEsistentiModificabiliDaProfilo and modificabile}'/>
				<c:choose>
					<c:when test="${genereGara eq '1' and !modificabile}">
						<gene:campoScheda campo="NGARA_${param.contatore}" entita="DOCUMGARA" title="Codice lotto ${gene:if(modo eq 'MODIFICA' or modo eq 'NUOVO','(valorizzare solo se documento specifico del lotto)','') }" campoFittizio="true" definizione="T10;0;;;NGARADG" visibile="${genereGara eq '1'}" value="${item[1]}" modificabile='false'/>
					</c:when>
					<c:otherwise>
						<gene:archivio titolo="lotti di gara"
							lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.DOCUMGARA.NGARA"),"gare/gare/gare-popup-lista-lotti.jsp","")}'
							scheda=''
							schedaPopUp=''
							campi="GARE.NGARA"
							functionId="default_0"
							parametriWhere="T:${codiceGara}"
							chiave=""
							formName="formLotti_${param.contatore}">
							<gene:campoScheda campo="NGARA_${param.contatore}" entita="DOCUMGARA" title="Codice lotto ${gene:if(modo eq 'MODIFICA' or modo eq 'NUOVO','(valorizzare solo se documento specifico del lotto)','') }" campoFittizio="true" visibile="${genereGara eq '1'}" definizione="T10;0;;;NGARADG" value="${item[1]}" modificabile='${campiEsistentiModificabiliDaProfilo }'/>
						</gene:archivio>
					</c:otherwise>
				</c:choose>
				<gene:campoScheda campo="VALENZA_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" visibile="false" definizione="N2;0;;;VALENZADG" value="${item[11]}" />
				<gene:campoScheda campo="DATARILASCIO_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" visibile="${!empty item[23] and item[23] ne ''}" definizione="D;0;;;DATRILDG" value="${item[23]}"  modificabile="false"/>
</c:when>
<c:otherwise>
				<gene:campoScheda campo="CODGAR_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" visibile="false" definizione="T21;1;;;CODGARDG"  />
				<gene:campoScheda campo="NORDDOCG_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" visibile="false" definizione="N3;1;;;NORDDOCGDG"  />
				<gene:campoScheda campo="GRUPPO_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  visibile="false" definizione="N3;0;;;GRUPPODG" value="2"/>
				<gene:campoScheda campo="IDPRG_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  visibile="false" definizione="T2;0;;;IDPRGDG"  value="PG"/>
				<gene:campoScheda campo="IDDOCDG_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  visibile="false" definizione="N12;0;;;IDDOCDGDG"  />
				<gene:campoScheda campo="IDPRG_${param.contatore}" entita="W_DOCDIG" campoFittizio="true" visibile="false" definizione="T2;1;;;DIGIDPRG" />
				<gene:campoScheda campo="IDDOCDIG_${param.contatore}" entita="W_DOCDIG" campoFittizio="true" visibile="false" definizione="N12;1;;;DIGID" />
				<gene:campoScheda campo="DIGDESDOC_${param.contatore}" entita="W_DOCDIG" campoFittizio="true" visibile="false" definizione="T2000;0;;;DIGDESDOC"  gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoNote"/>
				<gene:campoScheda campo="DESCRIZIONE_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  obbligatorio="true" definizione="T200;0;;;DESCLIBDG" />
				<gene:archivio titolo="lotti di gara"
					lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.DOCUMGARA.NGARA"),"gare/gare/gare-popup-lista-lotti.jsp","")}'
					scheda=''
					schedaPopUp=''
					campi="GARE.NGARA"
					functionId="default_0"
					parametriWhere="T:${codiceGara}"
					chiave=""
					formName="formLotti_${param.contatore}">
					<gene:campoScheda campo="NGARA_${param.contatore}" entita="DOCUMGARA" title="Codice lotto ${gene:if(modo eq 'MODIFICA' or modo eq 'NUOVO','(valorizzare solo se documento specifico del lotto)','') }" campoFittizio="true" visibile="${genereGara eq '1'}" definizione="T10;0;;;NGARADG"  value="${numeroGara}"/>
				</gene:archivio>
				<gene:campoScheda campo="VALENZA_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" visibile="false" definizione="N2;0;;;VALENZADG" value="0"/>
				<gene:campoScheda campo="DATARILASCIO_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" visibile="false" definizione="D;0;;;DATRILDG" />
</c:otherwise>
</c:choose>
<gene:javaScript>
	<c:if test='${not modificabile }'>
		$("#tdTitoloDestra_${param.contatore}").html('<img width="16" height="16" title="Documento pubblicato su portale" style="float:right;padding-right:4px" src="${pageContext.request.contextPath}/img/documento_pubblicato.png"/>');
	</c:if>
</gene:javaScript>