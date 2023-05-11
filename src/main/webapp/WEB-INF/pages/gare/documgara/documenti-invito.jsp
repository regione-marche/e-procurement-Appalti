<%
/*
 * Created on: 21/08/2014
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
<c:set var="codiceGara" value='${fn:substringBefore(param.chiave, ";")}' />
<c:set var="isProceduraTelematica" value='${param.isProceduraTelematica}'/>
<c:set var="idStampa" value="${item[20]}" />
<c:set var="numeroDocumentoWSDM" value="${param.numeroDocumentoWSDM}" />

<c:choose>
	<c:when test='${!empty numeroDocumentoWSDM and param.gestioneERP eq "1"}'>
		<c:set var="associataRdaJIRIDE" value='true' />
	</c:when>
	<c:otherwise>
		<c:set var="associataRdaJIRIDE" value='false' />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${integrazioneWSERP eq "1" and tipoWSERP eq "AMIU" and presenzaRda eq "1"}'>
		<c:set var="associataRdaWSERP" value='true' />
	</c:when>
	<c:otherwise>
		<c:set var="associataRdaWSERP" value='false' />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${item[7] eq "5" and param.tipoDettaglio eq 1}'>
		<c:set var="modificabile" value='false' />
	</c:when>
	<c:otherwise>
		<c:set var="modificabile" value='true' />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${param.tipoDettaglio eq 1}'>
		<c:set var="allegaMail" value='${item[22]}' />
	</c:when>
		<c:when test='${param.condizioneBloccoAllmail or (isProceduraTelematica ne "true") or (inizializzaAllmail eq "true")}'>
			<c:set var="allegaMail" value='2' />
		</c:when>
		<c:otherwise>
			<c:set var="allegaMail" value='1' />
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
				<gene:campoScheda campo="STATODOC_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  visibile="false" definizione="N3;0;;;STODOCDG" value="${item[7]}" />
				<gene:campoScheda campo="TIPOLOGIA_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  visibile="false" definizione="N3;0;;;G1TIPOLODG" value="${item[19]}" />
				<gene:campoScheda campo="DIGDESDOC_${param.contatore}" entita="W_DOCDIG" campoFittizio="true" visibile="false" definizione="T2000;0;;;DIGDESDOC"  gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoNote" value="${documentiInvitoDescFile[(3 * param.contatore) - 3]}"/>
				<gene:campoScheda campo="DESCRIZIONE_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  obbligatorio="true" definizione="T200;0;;;DESCLIBDG" value="${item[13]}" modificabile='${campiEsistentiModificabiliDaProfilo and modificabile}'/>
				<c:if test='${(modo eq "MODIFICA" or modo eq "NUOVO") and (gestioneUrl eq "true" or associataRdaJIRIDE eq "true") and campiEsistentiModificabiliDaProfilo and modificabile and idStampa ne "DGUE"}'>
					<gene:campoScheda title="" nome="tipoAllegato_${param.contatore}">
						<input type="radio" value="1" name="allegato_${param.contatore}" id="file_${param.contatore}" <c:if test='${!(!empty item[18] && item[18] ne "")}'>checked="checked"</c:if> onclick="javascript:cambiaTipoAllegato(1,${param.contatore},'${not param.condizioneBloccoAllmail}',true);" />
						 allega file
						<c:if test='${gestioneUrl eq "true"}'>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<input type="radio" value="2" name="allegato_${param.contatore}" id="url_${param.contatore}" <c:if test='${!empty item[18] && item[18] ne ""}'>checked="checked"</c:if> onclick="javascript:cambiaTipoAllegato(2,${param.contatore},'${not param.condizioneBloccoAllmail}',true);" />
						 specifica URL
						 </c:if>
						 <c:if test='${associataRdaJIRIDE eq "true"}'>
						 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<input type="radio" value="3" name="allegato_${param.contatore}" id="doc_${param.contatore}" onclick="javascript:cambiaTipoAllegato(3,${param.contatore},'${not param.condizioneBloccoAllmail}',true);" />
						 allega file da fascicolo
						  </c:if>
						 <c:if test='${associataRdaWSERP eq "true"}'>
						 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<input type="radio" value="4" name="allegato_${param.contatore}" id="doc_${param.contatore}" onclick="javascript:cambiaTipoAllegato(4,${param.contatore},false);" />
						 allega file da rda
						  </c:if>
					</gene:campoScheda>
				</c:if>
				<gene:campoScheda campo="ALLMAIL_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" obbligatorio="true" definizione="T1;0;;SN;G1ALLMAIL" value="${allegaMail}"  modificabile='${!param.condizioneBloccoAllmail and campiEsistentiModificabiliDaProfilo}' visibile='${isProceduraTelematica}'/>
				<gene:campoScheda campo="DIGNOMDOC_${param.contatore}" entita="W_DOCDIG" modificabile="false" campoFittizio="true" definizione="T100;0;;;DIGNOMDOC"  value="${documentiInvitoDescFile[(3 * param.contatore) - 2 ]}" href="javascript:visualizzaFileAllegato('${item[5]}','${item[6]}',${gene:string4Js(documentiInvitoDescFile[(3 * param.contatore) - 2 ])});">
					<c:if test='${modo eq "VISUALIZZA" || !campiEsistentiModificabiliDaProfilo}'>
						<c:if test="${documentiInvitoDescFile[(3 * param.contatore) - 1 ] eq '1' and idStampa ne 'DGUE'}">
							<span style="float:right;"><img width="16" height="16" src="${pageContext.request.contextPath}/img/isquantimod.png"/>&nbsp;In attesa di firma</span>
						</c:if>
					</c:if>
					<c:if test='${idStampa ne "DGUE" and not empty documentiInvitoDescFile[(3 * param.contatore) - 2 ] and modo eq "VISUALIZZA" and campiEsistentiModificabiliDaProfilo and item[7] ne "5" and param.autorizzatoModifiche ne "2" and not empty param.firmaRemota and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.FirmaRemotaDocumenti")}'>
						<a style="float:right;" href="javascript:openModal('${item[5]}','${item[6]}','${documentiInvitoDescFile[(3 * param.contatore) - 2 ]}','${pageContext.request.contextPath}');">
						<img src="${pageContext.request.contextPath}/img/firmaRemota.png" title="Firma digitale del documento" alt="Firma documento" width="16" height="16">
						<span title="Firma digitale del documento">Firma documento</span></a>
					</c:if>
					<c:if test='${idStampa ne "DGUE" and not empty documentiInvitoDescFile[(3 * param.contatore) - 2 ] and (empty documentiInvitoDescFile[(3 * param.contatore) - 1 ] or documentiInvitoDescFile[(3 * param.contatore) - 1 ] eq "2") and modo eq "VISUALIZZA" and campiEsistentiModificabiliDaProfilo and param.autorizzatoModifiche ne "2" and param.firmaDocumento eq "1" and item[7] ne "5"}'>
						<a style="float:right;" href="javascript:apriModaleRichiestaFirma('${item[5]}','${item[6]}','${param.contatore}');">
						<img src="${pageContext.request.contextPath}/img/firmaRemota.png" title="Firma digitale del documento" alt="Firma documento" width="16" height="16">
						<span title="Firma digitale del documento">Firma documento</span></a>
					</c:if>
					<c:if test='${idStampa eq "DGUE"}'>
						<c:choose>
							<c:when test="${not empty documentiInvitoDescFile[(3 * param.contatore) - 2 ] && documentiInvitoDescFile[(3 * param.contatore) - 2 ]  ne ''}">
								<a style="float:right;" id="apri_MGUE_${param.contatore}" href="javascript:apriConMDGUE('<c:out value="${codiceGara}" />', '${item[5]}',  '${item[6]}');">Apri con M-DGUE</a>
							</c:when>
							<c:otherwise>
								<a style="float:right;" id="genera_MGUE_${param.contatore}" href="javascript:generaConMDGUE('<c:out value="${codiceGara}" />', '${item[5]}',  '${item[6]}');">Genera con M-DGUE</a>
							</c:otherwise>
						</c:choose>
						
					</c:if>
				</gene:campoScheda>
				<gene:campoScheda campo="URLDOC_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  visibile='${gestioneUrl eq "true" }' definizione="T2000;0;;;G1URLDOC" value="${item[18]}" href="javascript:apriUrl('${item[18]}')" modificabile='${campiEsistentiModificabiliDaProfilo and modificabile}'>
					<gene:checkCampoScheda funzione='validURL("##")' obbligatorio="true" messaggio="Il valore dell'indirizzo URL specificato non è valido: è possibile inserire solo un indirizzo URL che inizi con 'ftp://', 'ftps://', 'sftp://', 'http://', 'https://' o 'www.'e non contenga spazi o virgole" onsubmit="false"/>
				</gene:campoScheda>
				<gene:campoScheda campo="IDSTAMPA_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" visibile="false" definizione="T20;0;;;G1IDSTAMPA" value="${item[20]}" />
				<gene:campoScheda campo="NOMEDOCGEN_${param.contatore}" campoFittizio="true" visibile="false" definizione="T100"/>
				<c:if test='${(modo eq "MODIFICA" or modo eq "NUOVO") and campiEsistentiModificabiliDaProfilo and modificabile}'>
					<gene:campoScheda title="Nome file" nome="selezioneFile_${param.contatore}">
						<input type="file" name="selFile[${param.contatore}]" id="selFile[${param.contatore}]" class="file" size="70" onkeydown="return bloccaCaratteriDaTastiera(event);" onchange='javascript:scegliFile(${param.contatore});'/>
						
						<c:if test='${modo eq "MODIFICA" && not empty idStampa && idStampa ne "" && idStampa ne "DGUE"}'>
							<br><img src="${pageContext.request.contextPath}/img/pdf.gif"/>&nbsp;
							  <a id="genera_${param.contatore}" href="javascript:generaAllegaPdf('${item[1]}','${param.contatore}','${item[20]}','${contextPath}')">Genera PDF da modello</a>
						</c:if>
						
						<c:if test="${param.richiestaFirma eq '1' and idStampa ne 'DGUE'}">
							<span id="spanRichiestaFirma_${param.contatore}" style="float:right;display:none;">Richiesta firma?<input type="checkbox" name="richiestaFirma_${param.contatore}" id="richiestaFirma_${param.contatore}" size="50" <c:if test="${documentiInvitoDescFile[(3 * param.contatore) - 1 ] eq '1'}">checked</c:if> onchange="javascript:aggiornaRichiestaFirma(${param.contatore},this);"> </span>
						</c:if>
					</gene:campoScheda>
					<gene:campoScheda title="Nome file" nome="selezioneFileDocumentale_${param.contatore}">
							<input type="button" value="Sfoglia da fascicolo" name="selFileDocumentale[${param.contatore}]" id="selFileDocumentale[${param.contatore}]" size="70" onkeydown="return bloccaCaratteriDaTastiera(event);"  onclick='javascript:apriPopupSelezioneDocumentale(${param.contatore});'/>
					</gene:campoScheda>
					<gene:campoScheda title="Nome file" nome="selezioneFileRda_${param.contatore}">
							<input type="button" value="Sfoglia da rda" name="selFileRda[${param.contatore}]" id="selFileRda[${param.contatore}]" size="70" onkeydown="return bloccaCaratteriDaTastiera(event);"  onclick='javascript:apriPopupSelezioneRda(${param.contatore});'/>
					</gene:campoScheda>
				</c:if>
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
				<c:if test="${param.richiestaFirma eq '1' or param.firmaDocumento eq '1'}">
					<gene:campoScheda campo="DIGFIRMA_${param.contatore}" entita="W_DOCDIG" campoFittizio="true" visibile="false" definizione="T2;0;" value="${documentiInvitoDescFile[(3 * param.contatore) - 1 ]}" />
				</c:if>
				<gene:campoScheda campo="DATARILASCIO_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  visibile='${not empty item[23] and item[23] !="" }' definizione="D;0;;;DATRILDG" value="${item[23]}" modificabile='false'/>
</c:when>
<c:otherwise>
			<gene:campoScheda campo="CODGAR_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" visibile="false" definizione="T21;1;;;CODGARDG" value="${codiceGara}"/>
				<gene:campoScheda campo="NORDDOCG_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" visibile="false" definizione="N3;1;;;NORDDOCGDG"  />
				<gene:campoScheda campo="GRUPPO_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  visibile="false" definizione="N3;0;;;GRUPPODG" value="6"/>
				<gene:campoScheda campo="IDPRG_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  visibile="false" definizione="T2;0;;;IDPRGDG"  value="PG"/>
				<gene:campoScheda campo="IDDOCDG_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  visibile="false" definizione="N12;0;;;IDDOCDGDG"  />
				<gene:campoScheda campo="IDPRG_${param.contatore}" entita="W_DOCDIG" campoFittizio="true" visibile="false" definizione="T2;1;;;DIGIDPRG" />
				<gene:campoScheda campo="IDDOCDIG_${param.contatore}" entita="W_DOCDIG" campoFittizio="true" visibile="false" definizione="N12;1;;;DIGID" />
				<gene:campoScheda campo="STATODOC_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  visibile="false" definizione="N3;0;;;STODOCDG" />
				<gene:campoScheda campo="TIPOLOGIA_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  visibile="false" definizione="N3;0;;;G1TIPOLODG" value="${param.tipologia}" />
				<gene:campoScheda campo="DIGDESDOC_${param.contatore}" entita="W_DOCDIG" campoFittizio="true" visibile="false" definizione="T2000;0;;;DIGDESDOC"  gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoNote"/>
				<gene:campoScheda campo="DESCRIZIONE_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  obbligatorio="true" definizione="T200;0;;;DESCLIBDG" />
				<c:if test='${(modo eq "MODIFICA" or modo eq "NUOVO") and (gestioneUrl eq "true" or associataRdaJIRIDE eq "true")}'>
					<gene:campoScheda title="" nome="tipoAllegato_${param.contatore}">
						<input type="radio" value="1" name="allegato_${param.contatore}" id="file_${param.contatore}" checked="checked" onclick="javascript:cambiaTipoAllegato(1,${param.contatore},'${not param.condizioneBloccoAllmail}',true);" />
						 allega file
						<c:if test='${gestioneUrl eq "true"}'>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<input type="radio" value="2" name="allegato_${param.contatore}" id="url_${param.contatore}" onclick="javascript:cambiaTipoAllegato(2,${param.contatore},'${not param.condizioneBloccoAllmail}',true);" />
						 specifica URL
						 </c:if>
						 <c:if test='${associataRdaJIRIDE eq "true"}'>
						 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<input type="radio" value="3" name="allegato_${param.contatore}" id="doc_${param.contatore}" onclick="javascript:cambiaTipoAllegato(3,${param.contatore},'${not param.condizioneBloccoAllmail}',true);" />
						 allega file da fascicolo
						 </c:if>
						 <c:if test='${associataRdaWSERP eq "true"}'>
						 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<input type="radio" value="4" name="allegato_${param.contatore}" id="doc_${param.contatore}" onclick="javascript:cambiaTipoAllegato(4,${param.contatore},false);" />
						 allega file da rda 
						 </c:if>
					</gene:campoScheda>
				</c:if>
				<gene:campoScheda campo="ALLMAIL_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" obbligatorio="true" definizione="T1;0;;SN;G1ALLMAIL" value="${allegaMail}" defaultValue="${allegaMail}" modificabile='${!param.condizioneBloccoAllmail}' visibile='${isProceduraTelematica}'/>
				<gene:campoScheda campo="DIGNOMDOC_${param.contatore}" entita="W_DOCDIG" modificabile="false" campoFittizio="true" definizione="T100;0;;;DIGNOMDOC"  value=''/>
				<gene:campoScheda campo="URLDOC_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  visibile='${gestioneUrl eq "true" }' definizione="T2000;0;;;G1URLDOC" >
					<gene:checkCampoScheda funzione='validURL("##")' obbligatorio="true" messaggio="Il valore dell'indirizzo URL specificato non è valido: è possibile inserire solo un indirizzo URL che inizi con 'ftp://', 'ftps://', 'sftp://', 'http://', 'https://' o 'www.'e non contenga spazi o virgole" onsubmit="false"/>
				</gene:campoScheda>
				<gene:campoScheda campo="IDSTAMPA_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" visibile="false" definizione="T20;0;;;G1IDSTAMPA"  />
				<gene:campoScheda campo="NOMEDOCGEN_${param.contatore}" campoFittizio="true" visibile="false" definizione="T100"/>
				<c:if test='${modo eq "MODIFICA" or modo eq "NUOVO"}'>
					<gene:campoScheda title="Nome file" nome="selezioneFile_${param.contatore}">
							<input type="file" name="selFile[${param.contatore}]" id="selFile[${param.contatore}]" class="file" size="70" onkeydown="return bloccaCaratteriDaTastiera(event);" onchange='javascript:scegliFile(${param.contatore});'/>
							<c:if test="${param.richiestaFirma eq '1'}">
								<span id="spanRichiestaFirma_${param.contatore}" style="float:right;display:none;">Richiesta firma?<input type="checkbox" name="richiestaFirma_${param.contatore}" id="richiestaFirma_${param.contatore}" size="50" onchange="javascript:aggiornaRichiestaFirma(${param.contatore},this);"> </span>
							</c:if>
					</gene:campoScheda>
					<gene:campoScheda title="Nome file" nome="selezioneFileDocumentale_${param.contatore}">
							<input type="button" value="Sfoglia da fascicolo" name="selFile[${param.contatore}]" id="selFileDocumentale[${param.contatore}]" size="70" onkeydown="return bloccaCaratteriDaTastiera(event);" onclick='javascript:apriPopupSelezioneDocumentale(${param.contatore});'/>
					</gene:campoScheda>
					<gene:campoScheda title="Nome file" nome="selezioneFileRda_${param.contatore}">
							<input type="button" value="Sfoglia da rda" name="selFile[${param.contatore}]" id="selFileRda[${param.contatore}]" size="70" onkeydown="return bloccaCaratteriDaTastiera(event);" onclick='javascript:apriPopupSelezioneRda(${param.contatore});'/>
					</gene:campoScheda>
				</c:if>
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
				<c:if test="${param.richiestaFirma eq '1'}">
					<gene:campoScheda campo="DIGFIRMA_${param.contatore}" entita="W_DOCDIG" campoFittizio="true" visibile="false" definizione="T2;0;;SN;DIGFIRMA" />
				</c:if>
</c:otherwise>
</c:choose>

<c:if test='${associataRdaJIRIDE eq "true"}'>
	<input type="hidden" id="getdocumentoallegato_username_${param.contatore}" name="getdocumentoallegato_username_${param.contatore}" value="" />
	<input type="hidden" id="getdocumentoallegato_password_${param.contatore}" name="getdocumentoallegato_password_${param.contatore}" value="" />
	<input type="hidden" id="getdocumentoallegato_ruolo_${param.contatore}" name="getdocumentoallegato_ruolo_${param.contatore}" value="" />
	<input type="hidden" id="getdocumentoallegato_nome_${param.contatore}" name="getdocumentoallegato_nome_${param.contatore}" value="" />
	<input type="hidden" id="getdocumentoallegato_cognome_${param.contatore}" name="getdocumentoallegato_cognome_${param.contatore}" value="" />
	<input type="hidden" id="getdocumentoallegato_codiceuo_${param.contatore}"  name="getdocumentoallegato_codiceuo_${param.contatore}" value="" />
	<input type="hidden" id="getdocumentoallegato_idutente_${param.contatore}"  name="getdocumentoallegato_idutente_${param.contatore}" value="" />
	<input type="hidden" id="getdocumentoallegato_idutenteunop_${param.contatore}"  name="getdocumentoallegato_idutenteunop_${param.contatore}" value="" />
	<input type="hidden" id="getdocumentoallegato_annoprotocollo_${param.contatore}" name="getdocumentoallegato_annoprotocollo_${param.contatore}" value="" />
	<input type="hidden" id="getdocumentoallegato_numeroprotocollo_${param.contatore}" name="getdocumentoallegato_numeroprotocollo_${param.contatore}" value="" />
	<input type="hidden" id="getdocumentoallegato_numerodocumento_${param.contatore}" name="getdocumentoallegato_numerodocumento_${param.contatore}" value="" />
	<input type="hidden" id="getdocumentoallegato_nomeallegato_${param.contatore}" name="getdocumentoallegato_nomeallegato_${param.contatore}" value="" />
	<input type="hidden" id="getdocumentoallegato_tipoallegato_${param.contatore}" name="getdocumentoallegato_tipoallegato_${param.contatore}" value="" />
	<input type="hidden" id="getdocumentoallegato_servizio_${param.contatore}" name="getdocumentoallegato_servizio_${param.contatore}" value="${param.servizio }" />
</c:if>

<c:if test='${associataRdaWSERP eq "true"}'>
	<input type="hidden" id="getdocumentoallegatoWSERP_titoloallegato_${param.contatore}" name="getdocumentoallegatoWSERP_titoloallegato_${param.contatore}" value="" />
	<input type="hidden" id="getdocumentoallegatoWSERP_nomeallegato_${param.contatore}" name="getdocumentoallegatoWSERP_nomeallegato_${param.contatore}" value="" />
	<input type="hidden" id="getdocumentoallegatoWSERP_idfile_${param.contatore}" name="getdocumentoallegatoWSERP_idfile_${param.contatore}" value="" />
</c:if>

<gene:javaScript>

var idconfi = "${idconfi}";

function apriPopupSelezioneDocumentale(indice){
	var href = "href=gare/commons/popup-seleziona-da-documentale.jsp?codiceGara=${codiceGara}&indice=" + indice + "&numerowsdocumento=${numeroDocumentoWSDM}";
	if(idconfi){href+="&idconfi="+idconfi;}
	openPopUpCustom(href, "insDocumentiPredefiniti", 700, 500, "no", "yes");
}

function apriPopupSelezioneRda(indice){
	var href = "href=gare/commons/popup-seleziona-da-rda.jsp?codiceGara=${codiceGara}&indice=" + indice;
	openPopUpCustom(href, "insDocumentiPredefiniti", 700, 500, "no", "yes");
}

function popolaFormDocumentale(indice,username,password,ruolo,nome,cognome,codiceuo,idutente,idutenteunop,annoprotocollo,numeroprotocollo,nomeallegato,tipoallegato,numerodocumento,servizio){
	
	$("#getdocumentoallegato_username_" + indice).val(username);
	$("#getdocumentoallegato_password_" + indice).val(password);
	$("#getdocumentoallegato_ruolo_" + indice).val(ruolo);
	$("#getdocumentoallegato_nome_" + indice).val(nome);
	$("#getdocumentoallegato_cognome_" + indice).val(cognome);
	$("#getdocumentoallegato_codiceuo_" + indice).val(codiceuo);
	$("#getdocumentoallegato_idutente_" + indice).val(idutente);
	$("#getdocumentoallegato_idutenteunop_" + indice).val(idutenteunop);
	$("#getdocumentoallegato_annoprotocollo_" + indice).val(annoprotocollo);
	$("#getdocumentoallegato_numeroprotocollo_" + indice).val(numeroprotocollo);
	$("#getdocumentoallegato_nomeallegato_" + indice).val(nomeallegato);
	$("#getdocumentoallegato_tipoallegato_" + indice).val(tipoallegato);
	$("#getdocumentoallegato_numerodocumento_" + indice).val(numerodocumento);
	$("#getdocumentoallegato_servizio_" + indice).val(servizio);
	scegliFileDocumentale(nomeallegato,tipoallegato,indice);
}

function popolaFormWSERP(indice,titoloallegato,nomeallegato,idfile){
	
	$("#getdocumentoallegatoWSERP_titoloallegato_" + indice).val(titoloallegato);
	$("#getdocumentoallegatoWSERP_nomeallegato_" + indice).val(nomeallegato);
	$("#getdocumentoallegatoWSERP_idfile_" + indice).val(idfile);
	
	scegliFileDocumentale(nomeallegato,"",indice);
}

<c:if test='${modo eq "VISUALIZZA" and gestioneUrl eq "true"}'>
	//Si deve nascondere o il campo DIGNOMDOC o URLDOC
	var indice = "${param.contatore }";
    var nome = getValue("W_DOCDIG_DIGNOMDOC_" + indice);
	var url = getValue("DOCUMGARA_URLDOC_" + indice);
	if(url==null || url ==""){
		$("#rowDOCUMGARA_URLDOC_" + indice).hide();
	}else{
		if(nome==null || nome == "" ){
			$("#rowW_DOCDIG_DIGNOMDOC_" + indice).hide();
		}	
	}
	
	
	
</c:if>

<c:if test='${gestioneUrl eq "true"}'>
	function apriUrl(urlDocumento){
		if(urlDocumento.indexOf("http://")<0 && urlDocumento.indexOf("https://")<0)
			urlDocumento = "http://" + urlDocumento;
		window.open(urlDocumento,"url_documento");
	}
</c:if>

<c:if test='${(modo eq "MODIFICA" or modo eq "NUOVO") and (gestioneUrl eq "true" or associataRdaJIRIDE eq "true" or associataRdaWSERP eq "true")}'>
	function cambiaTipoAllegato(tipo, indice, aggiornaAllmail,bloccaAllmail,inizializza){
		if(tipo==1){
			$("#rowDOCUMGARA_URLDOC_" + indice).hide();
			setValue("DOCUMGARA_URLDOC_" + indice,"");
			$("#rowW_DOCDIG_DIGNOMDOC_" + indice).show();
			if(inizializza==false){
				setValue("W_DOCDIG_DIGNOMDOC_" + indice,"");
			}
			$("#rowselezioneFile_" + indice).show();
			$("#rowselezioneFileDocumentale_" + indice).hide();
			$("#rowselezioneFileRda_" + indice).hide();
			$("#getdocumentoallegato_nomeallegato_" + indice).val('');
			$('#DOCUMGARA_ALLMAIL_' + indice).attr('disabled',false);
		}else{
			if(tipo == 3){
				$("#rowDOCUMGARA_URLDOC_" + indice).hide();
				setValue("DOCUMGARA_URLDOC_" + indice,"");
				$("#rowW_DOCDIG_DIGNOMDOC_" + indice).show();
				setValue("W_DOCDIG_DIGNOMDOC_" + indice,"");
				setValue("W_DOCDIG_IDDOCDIG_" + indice,"");
				$("#rowselezioneFile_" + indice).hide();
				$("input#selezioneFile_" + indice).val('');
				$("#rowselezioneFileDocumentale_" + indice).show();
				$("#rowselezioneFileRda_" + indice).hide();
				$('#DOCUMGARA_ALLMAIL_' + indice).attr('disabled',false);
			}else{
				if(tipo == 4){
					$("#rowDOCUMGARA_URLDOC_" + indice).hide();
					setValue("DOCUMGARA_URLDOC_" + indice,"");
					$("#rowW_DOCDIG_DIGNOMDOC_" + indice).show();
					setValue("W_DOCDIG_DIGNOMDOC_" + indice,"");
					setValue("W_DOCDIG_IDDOCDIG_" + indice,"");
					$("#rowselezioneFile_" + indice).hide();
					$("input#selezioneFile_" + indice).val('');
					$("#rowselezioneFileDocumentale_" + indice).hide();
					$("#rowselezioneFileRda_" + indice).show();
				}else{
					$("#rowDOCUMGARA_URLDOC_" + indice).show();
					$("#rowW_DOCDIG_DIGNOMDOC_" + indice).hide();
					setValue("W_DOCDIG_DIGNOMDOC_" + indice,"");
					setValue("W_DOCDIG_IDDOCDIG_" + indice,"");
					$("#rowselezioneFile_" + indice).hide();
					$("#rowselezioneFileDocumentale_" + indice).hide();
					
					if(aggiornaAllmail == "true" ){
						setValue("DOCUMGARA_ALLMAIL_" + indice,"2");
						}
					if(bloccaAllmail){
						$('#DOCUMGARA_ALLMAIL_' + indice).attr('disabled',true);
						}
					<c:if test="${param.richiestaFirma eq '1'}">
						setValue("W_DOCDIG_DIGFIRMA_" + indice,"");
						$("#richiestaFirma_" + indice).removeAttr("checked");
					</c:if>
				}
			}
		}
	}
	
	var tipoDocumentoAllegato = 1;
	<c:if test='${!empty item[18] && item[18] ne ""}'>
		tipoDocumentoAllegato = 2;
	</c:if>
	
	var indice = "${param.contatore }";
	var bloccaAllmail = true;
	if(lastIdDOCUMINVITIVisualizzata > indice)
		bloccaAllmail = false;
	cambiaTipoAllegato(tipoDocumentoAllegato, indice,false,bloccaAllmail,true);
	
	
</c:if>

<c:if test='${associataRdaJIRIDE ne "true"}'>
	$("[id^='rowselezioneFileDocumentale_']").hide();
</c:if>

<c:if test='${associataRdaWSERP ne "true"}'>
	$("[id^='rowselezioneFileRda_']").hide();
</c:if>

<c:if test='${(modo eq "MODIFICA" or modo eq "NUOVO")}'>
	
	//Si deve visualizzare il campo "richiesta firma" solo quando è valorizzato il nome del file
	var indice = "${param.contatore}";
	var nomdoc = getValue("W_DOCDIG_DIGNOMDOC_"+ indice);
	if(nomdoc!=null && nomdoc!="")
		$("#spanRichiestaFirma_" + indice).show();
</c:if>

<c:if test='${not modificabile }'>
	$("#tdTitoloDestra_${param.contatore}").html('<img width="16" height="16" title="Documento pubblicato su portale" style="float:right;padding-right:4px" src="${pageContext.request.contextPath}/img/documento_pubblicato.png"/>');
</c:if>
</gene:javaScript>
