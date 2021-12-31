<%
/*
 * Created on: 04/10/2016
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

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="numeroGara" value='${fn:substringAfter(param.chiave, ";")}' />
<c:set var="idStampa" value="${item[19]}" />

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
				<gene:campoScheda campo="NGARA_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" visibile="false" definizione="T10;0;;;NGARADG" value="${item[1]}" />
				<gene:campoScheda campo="NORDDOCG_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" visibile="false" definizione="N3;1;;;NORDDOCGDG" value="${item[2]}" />
				<gene:campoScheda campo="GRUPPO_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  visibile="false" definizione="N3;0;;;GRUPPODG" value="${item[3]}"/>
				<gene:campoScheda campo="IDPRG_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  visibile="false" definizione="T2;0;;;IDPRGDG" value="${item[5]}" />
				<gene:campoScheda campo="IDDOCDG_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  visibile="false" definizione="N12;0;;;IDDOCDGDG" value="${item[6]}" />
				<gene:campoScheda campo="IDPRG_${param.contatore}" entita="W_DOCDIG" campoFittizio="true" visibile="false" definizione="T2;1;;;DIGIDPRG" value="${item[5]}"/>
				<gene:campoScheda campo="IDDOCDIG_${param.contatore}" entita="W_DOCDIG" campoFittizio="true" visibile="false" definizione="N12;1;;;DIGID"  value="${item[6]}" />
				<gene:campoScheda campo="DIGDESDOC_${param.contatore}" entita="W_DOCDIG" campoFittizio="true" visibile="false" definizione="T2000;0;;;DIGDESDOC"  gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoNote" value="${allegatiOrdineDescFile[(3 * param.contatore) - 3]}"/>
				<gene:campoScheda campo="DESCRIZIONE_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  obbligatorio="true" definizione="T200;0;;;DESCLIBDG" value="${item[13]}" modificabile='${param.valoreStato eq "2"}'/>
				<gene:campoScheda campo="ALLMAIL_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  definizione="T1;0;;SN;G1ALLMAIL" value="${item[21]}" modificabile='${param.valoreStato eq "2"}'/>
				<gene:campoScheda campo="DIGNOMDOC_${param.contatore}" entita="W_DOCDIG" modificabile="false" campoFittizio="true" definizione="T100;0;;;DIGNOMDOC"  value="${allegatiOrdineDescFile[(3 * param.contatore) - 2 ]}" href="javascript:visualizzaFileAllegato('${item[5]}','${item[6]}',${gene:string4Js(allegatiOrdineDescFile[(3 * param.contatore) - 2 ])});">
					<c:if test='${modo eq "VISUALIZZA" }'>
						<c:if test="${param.richiestaFirma eq '1' and allegatiOrdineDescFile[(3 * param.contatore) - 1 ] eq '1'}">
							<span style="float:right;"><img width="16" height="16" src="${pageContext.request.contextPath}/img/isquantimod.png"/>&nbsp;In attesa di firma</span>
						</c:if>
					</c:if>
					<c:if test='${not empty allegatiOrdineDescFile[(3 * param.contatore) - 2 ] and modo eq "VISUALIZZA" and campiEsistentiModificabiliDaProfilo and param.ordineNonDefinito ne "false" and not empty param.firmaRemota and param.autorizzatoModifiche ne "2" and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.FirmaRemotaDocumenti")}'>
						<a style="float:right;" href="javascript:openModal('${item[5]}','${item[6]}','${allegatiOrdineDescFile[(3 * param.contatore) - 2 ]}','${pageContext.request.contextPath}');">
						<img src="${pageContext.request.contextPath}/img/firmaRemota.png" title="Firma digitale del documento" alt="Firma documento" width="16" height="16">
						<span title="Firma digitale del documento">Firma documento</span></a>
					</c:if>
				</gene:campoScheda>
				<gene:campoScheda campo="IDSTAMPA_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" visibile="false" definizione="T20;0;;;G1IDSTAMPA" value="${item[19]}" />
				<gene:campoScheda campo="NOMEDOCGEN_${param.contatore}" campoFittizio="true" visibile="false" definizione="T100"/>
				<c:if test='${(modo eq "MODIFICA" or modo eq "NUOVO") and param.valoreStato eq "2"}'>
					<gene:campoScheda title="Nome file" nome="selezioneFile_${param.contatore}">
						<input type="file" name="selFile[${param.contatore}]" id="selFile[${param.contatore}]" class="file" size="70" onkeydown="return bloccaCaratteriDaTastiera(event);" onchange='javascript:scegliFile(${param.contatore});'/>
						<c:if test='${modo eq "MODIFICA" && not empty idStampa && idStampa ne ""}'>
							<br><img src="${pageContext.request.contextPath}/img/pdf.gif"/>&nbsp;
							  <a id="genera_${param.contatore}" href="javascript:generaAllegaPdf('${item[1]}','${param.contatore}','${item[19]}','${contextPath}')">Genera PDF da modello</a>
						</c:if>
						<c:if test="${param.richiestaFirma eq '1' }">
							<span id="spanRichiestaFirma_${param.contatore}" style="float:right;display:none;">Richiesta firma?<input type="checkbox" name="richiestaFirma_${param.contatore}" id="richiestaFirma_${param.contatore}" size="50" <c:if test="${allegatiOrdineDescFile[(3 * param.contatore) - 1 ] eq '1'}">checked</c:if> onchange="javascript:aggiornaRichiestaFirma(${param.contatore},this);"> </span>
						</c:if>
					</gene:campoScheda>
				</c:if>
				<gene:campoScheda campo="VALENZA_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" visibile="false" definizione="N2;0;;;VALENZADG" value="${item[11]}" />
				<c:if test="${param.richiestaFirma eq '1'}">
					<gene:campoScheda campo="DIGFIRMA_${param.contatore}" entita="W_DOCDIG" campoFittizio="true" visibile="false" definizione="T2;0;" value="${allegatiOrdineDescFile[(3 * param.contatore) - 1 ]}" />
				</c:if>
</c:when>
<c:otherwise>
			<gene:campoScheda campo="CODGAR_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" visibile="false" definizione="T21;1;;;CODGARDG"  />
				<gene:campoScheda campo="NGARA_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" visibile="false" definizione="T10;0;;;NGARADG"  value="${numeroGara}"/>
				<gene:campoScheda campo="NORDDOCG_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" visibile="false" definizione="N3;1;;;NORDDOCGDG"  />
				<gene:campoScheda campo="GRUPPO_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  visibile="false" definizione="N3;0;;;GRUPPODG" value="11"/>
				<gene:campoScheda campo="IDPRG_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  visibile="false" definizione="T2;0;;;IDPRGDG"  value="PG"/>
				<gene:campoScheda campo="IDDOCDG_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  visibile="false" definizione="N12;0;;;IDDOCDGDG"  />
				<gene:campoScheda campo="IDPRG_${param.contatore}" entita="W_DOCDIG" campoFittizio="true" visibile="false" definizione="T2;1;;;DIGIDPRG" />
				<gene:campoScheda campo="IDDOCDIG_${param.contatore}" entita="W_DOCDIG" campoFittizio="true" visibile="false" definizione="N12;1;;;DIGID" />
				<gene:campoScheda campo="DIGDESDOC_${param.contatore}" entita="W_DOCDIG" campoFittizio="true" visibile="false" definizione="T2000;0;;;DIGDESDOC"  gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoNote"/>
				<gene:campoScheda campo="DESCRIZIONE_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  obbligatorio="true" definizione="T200;0;;;DESCLIBDG" />
				<gene:campoScheda campo="ALLMAIL_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  definizione="T1;0;;SN;G1ALLMAIL" value='1'/>
				<gene:campoScheda campo="DIGNOMDOC_${param.contatore}" entita="W_DOCDIG" modificabile="false" campoFittizio="true" definizione="T100;0;;;DIGNOMDOC"  value=''/>
				<gene:campoScheda campo="IDSTAMPA_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" visibile="false" definizione="T20;0;;;G1IDSTAMPA"  />
				<gene:campoScheda campo="NOMEDOCGEN_${param.contatore}" campoFittizio="true" visibile="false" definizione="T100"/>
				<c:if test='${modo eq "MODIFICA" or modo eq "NUOVO"}'>
					<gene:campoScheda title="Nome file" nome="selezioneFile_${param.contatore}">
							<input type="file" name="selFile[${param.contatore}]" id="selFile[${param.contatore}]" class="file" size="70" onkeydown="return bloccaCaratteriDaTastiera(event);" onchange='javascript:scegliFile(${param.contatore});'/>
							<c:if test="${param.richiestaFirma eq '1'}">
								<span id="spanRichiestaFirma_${param.contatore}" style="float:right;display:none;">Richiesta firma?<input type="checkbox" name="richiestaFirma_${param.contatore}" id="richiestaFirma_${param.contatore}" size="50" onchange="javascript:aggiornaRichiestaFirma(${param.contatore},this);"> </span>
							</c:if>
					</gene:campoScheda>
				</c:if>
				<gene:campoScheda campo="VALENZA_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" visibile="false" definizione="N2;0;;;VALENZADG" value="0"/>
				<c:if test="${param.richiestaFirma eq '1'}">
					<gene:campoScheda campo="DIGFIRMA_${param.contatore}" entita="W_DOCDIG" campoFittizio="true" visibile="false" definizione="T2;0;;SN;DIGFIRMA" />
				</c:if>
</c:otherwise>
</c:choose>
<gene:javaScript>
<c:if test='${(modo eq "MODIFICA" or modo eq "NUOVO")}'>
	//Si deve visualizzare il campo "richiesta firma" solo quando è valorizzato il nome del file
	var indice = "${param.contatore}";
	var nomdoc = getValue("W_DOCDIG_DIGNOMDOC_"+ indice);
	if(nomdoc!=null && nomdoc!="")
		$("#spanRichiestaFirma_" + indice).show();
</c:if>
</gene:javaScript>

