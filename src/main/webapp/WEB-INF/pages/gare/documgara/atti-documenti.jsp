<%
/*
 * Created on: 10/05/2016
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
<c:set var="numerowsdocumento" value="${param.numerowsdocumento}" />
<c:set var="codiceGara" value='${fn:substringBefore(param.chiave, ";")}' />
<c:set var="tipologia" value='${param.tipologia}' />
<c:set var="gruppo" value='${param.gruppo}' />
<c:set var="idStampa" value="${item[20]}" />
<c:set var="nomeFile" value="${not empty item[27]? fn:substringAfter(fn:substringAfter(item[27], '|'),'|') : ''}"/>

<c:choose>
	<c:when test='${item[7] eq "5"}'>
		<c:set var="modificabile" value='false' />
	</c:when>
	<c:when test="${idStampa eq 'LARGEFILE'}">
		<c:set var="modificabile" value='false' />
		<c:set var="documentoLARGEFILE" value='true' />
	</c:when>
	<c:otherwise>
		<c:set var="modificabile" value='true' />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty numeroDocumentoWSDM and param.gestioneERP eq "1"}'>
		<c:set var="associataRdaJIRIDE" value='true' />
	</c:when>
	<c:otherwise>
		<c:set var="associataRdaJIRIDE" value='false' />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${integrazioneWSERP eq "1" and (tipoWSERP eq "AMIU" or tipoWSERP eq "INARCASSA" or tipoWSERP eq "ARCHIFLOW" or tipoWSERP eq "CONI") and presenzaRda eq "1"}'>
		<c:set var="associataRdaWSERP" value='true' />
	</c:when>
	<c:otherwise>
		<c:set var="associataRdaWSERP" value='false' />
	</c:otherwise>
</c:choose>

<c:if test="${gruppo eq 15}">
	<c:choose>
		<c:when test='${genereGara eq "3"}'>
			<c:set var="attoAutorizAttivo" value='${gene:checkProt(pageContext, "SEZ.VIS.GARE.TORN-OFFUNICA-scheda.DATIGEN.PRIMOATAU")}' />
		</c:when>
		<c:when test='${genereGara eq "1"}'>
			<c:set var="attoAutorizAttivo" value='${gene:checkProt(pageContext, "SEZ.VIS.GARE.TORN-scheda.DATIGEN.PRIMOATAU")}' />
		</c:when>
		<c:otherwise>
			<c:set var="attoAutorizAttivo" value='${gene:checkProt(pageContext, "SEZ.VIS.GARE.GARE-scheda.DATIGEN.PRIMOATAU")}' />
		</c:otherwise>
	</c:choose>
</c:if>

<c:if test="${tipoWSERP eq 'CONI' && gruppo eq '10' and presenzaRda eq '1'}">
	<c:set var="gestioneODA" value='1'/>
	<c:set var="numoda" value='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, "select numrdo from gare1 where codgar1 = ?", codiceGara)}' />
</c:if>

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
				<gene:campoScheda campo="NGARA_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" visibile="false" definizione="T10;0;;;NGARADG" value="${item[1]}" modificabile="${modificabile}"/>
				<gene:campoScheda campo="NORDDOCG_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" visibile="false" definizione="N3;1;;;NORDDOCGDG" value="${item[2]}" />
				<gene:campoScheda campo="GRUPPO_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  visibile="false" definizione="N3;0;;;GRUPPODG" value="${item[3]}"/>
				<gene:campoScheda campo="IDPRG_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  visibile="false" definizione="T2;0;;;IDPRGDG" value="${item[5]}" />
				<gene:campoScheda campo="IDDOCDG_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  visibile="false" definizione="N12;0;;;IDDOCDGDG" value="${item[6]}" />
				<gene:campoScheda campo="IDPRG_${param.contatore}" entita="W_DOCDIG" campoFittizio="true" visibile="false" definizione="T2;1;;;DIGIDPRG" value="${item[5]}"/>
				<gene:campoScheda campo="IDDOCDIG_${param.contatore}" entita="W_DOCDIG" campoFittizio="true" visibile="false" definizione="N12;1;;;DIGID"  value="${item[6]}" />
				<gene:campoScheda campo="STATODOC_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  visibile="false" definizione="N3;0;;;STODOCDG" value="${item[7]}" />
				<gene:campoScheda campo="TIPOLOGIA_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  visibile="false" definizione="N3;0;;;G1TIPOLODG" value="${item[19]}"/>
				<gene:campoScheda campo="DIGDESDOC_${param.contatore}" entita="W_DOCDIG" campoFittizio="true" visibile="false" definizione="T2000;0;;;DIGDESDOC"  gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoNote" value="${attiDescFile[(4 * param.contatore) - 4]}"/>
				<gene:campoScheda campo="DESCRIZIONE_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  obbligatorio="true" definizione="T200;0;;;DESCLIBDG" value="${item[13]}"  modificabile='${campiEsistentiModificabiliDaProfilo and modificabile}'/>
				<gene:campoScheda campo="TIPOATTO_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  obbligatorio="true" definizione="N7;0;A1202;;TIPOATTODG" value="${item[26]}"  modificabile='${modificabile}' visibile="${gruppo eq '10' && not empty requestScope.vernpa}"/>
				<c:if test='${(modo eq "MODIFICA" or modo eq "NUOVO") and (gestioneUrl eq "true" or associataRdaJIRIDE eq "true") and campiEsistentiModificabiliDaProfilo and modificabile}'>
				
					<gene:campoScheda title="" nome="tipoAllegato_${param.contatore}">
						<input type="radio" value="1" name="allegato_${param.contatore}" id="file_${param.contatore}" <c:if test='${!(!empty item[18] && item[18] ne "")}'>checked="checked"</c:if> onclick="javascript:cambiaTipoAllegato(1,${param.contatore},false);" />
						 allega file
						<c:if test='${gestioneUrl eq "true"}'>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<input type="radio" value="2" name="allegato_${param.contatore}" id="url_${param.contatore}" <c:if test='${!empty item[18] && item[18] ne ""}'>checked="checked"</c:if> onclick="javascript:cambiaTipoAllegato(2,${param.contatore},false);" />
						 specifica URL
						 </c:if>
						 <c:if test='${associataRdaJIRIDE eq "true"}'>
						 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<input type="radio" value="3" name="allegato_${param.contatore}" id="doc_${param.contatore}" onclick="javascript:cambiaTipoAllegato(3,${param.contatore},false);" />
						 allega file da fascicolo
						  </c:if>
						 <c:if test='${associataRdaWSERP eq "true" && tipoWSERP ne "CONI"}'>
						 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<input type="radio" value="4" name="allegato_${param.contatore}" id="doc_${param.contatore}" onclick="javascript:cambiaTipoAllegato(4,${param.contatore},false);" />
						 allega file da rda
						  </c:if>
						  <c:if test='${gestioneODA eq "1"}'>							
						 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						 <input type="radio" value="5" name="allegato_${param.contatore}" id="docOda" onclick="javascript:cambiaTipoAllegato(5,${param.contatore},false);" />
						 allega file da oda 
						  </c:if>
					</gene:campoScheda>
				</c:if>
				<gene:campoScheda title="${idStampa eq 'LARGEFILE'? 'Pdf di riepilogo' : 'Nome documento'}" campo="DIGNOMDOC_${param.contatore}" modificabile="false" entita="W_DOCDIG" campoFittizio="true" definizione="T100;0;;;DIGNOMDOC"  value="${attiDescFile[(4 * param.contatore) - 3 ]}" href="javascript:visualizzaFileAllegato('${item[5]}','${item[6]}',${gene:string4Js(attiDescFile[(4 * param.contatore) - 3 ])});">
					<c:if test="${param.isVerificaPDFA eq '1' && attiDescFile[(4 * param.contatore) - 1 ] eq 'pdf/a'}">
						&nbsp;<img id="imgCheckPDFaDocMessage_${param.contatore}" src="${pageContext.request.contextPath}/img/pdfa.png" title="File conforme al formato PDF/A" alt="File conforme al formato PDF/A">
					</c:if>
					<c:if test='${modo eq "VISUALIZZA" || !campiEsistentiModificabiliDaProfilo}'>
						<c:if test="${attiDescFile[(4 * param.contatore) - 2 ] eq '1'}">
							<span style="float:right;"><img width="16" height="16" src="${pageContext.request.contextPath}/img/isquantimod.png"/>&nbsp;In attesa di firma</span>
						</c:if>
					</c:if>
					<c:if test='${not empty attiDescFile[(4 * param.contatore) - 3 ] and modo eq "VISUALIZZA" and campiEsistentiModificabiliDaProfilo and item[7] ne "5" and param.autorizzatoModifiche ne "2" and not empty param.firmaRemota and param.autorizzatoModifiche ne "2" and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.FirmaRemotaDocumenti")}'>
						<a style="float:right;" href="javascript:openModal('${item[5]}','${item[6]}','${attiDescFile[(4 * param.contatore) - 3 ]}','${pageContext.request.contextPath}');">
						<img src="${pageContext.request.contextPath}/img/firmaRemota.png" title="Firma digitale del documento" alt="Firma documento" width="16" height="16">
						<span title="Firma digitale del documento">Firma documento</span></a>
					</c:if>
					<c:if test='${not empty attiDescFile[(4 * param.contatore) - 3 ] and (empty attiDescFile[(4 * param.contatore) - 2 ] or attiDescFile[(4 * param.contatore) - 2 ] eq "2") and modo eq "VISUALIZZA" and campiEsistentiModificabiliDaProfilo and param.autorizzatoModifiche ne "2" and param.firmaDocumento eq "1" and item[7] ne "5"}'>
						<a style="float:right;" href="javascript:apriModaleRichiestaFirma('${item[5]}','${item[6]}','${param.contatore}');">
						<img src="${pageContext.request.contextPath}/img/firmaRemota.png" title="Firma digitale del documento" alt="Firma documento" width="16" height="16">
						<span title="Firma digitale del documento">Firma documento</span></a>
					</c:if>
				</gene:campoScheda>
				<gene:campoScheda campo="DIGTIPDOC_${param.contatore}" entita="W_DOCDIG" campoFittizio="true" visibile="false" definizione="T2;0;;SN;DIGTIPDOC" value="${attiDescFile[(4 * param.contatore) - 1 ]}"/>
				<c:choose>
					<c:when test="${idStampa eq 'LARGEFILE'}">
						<gene:campoScheda campo="URLLARGEFILE_${param.contatore}" entita="DOCUMGARA" title="Nome documento (large file)" campoFittizio="true"  visibile='${gestioneUrl eq "true"}'  href="javascript:apriUrl('${item[18]}')" definizione="T2000;0;;;G1URLDOC" value="${nomeFile}" modificabile='false'>
							<c:if test="${empty nomeFile and param.autorizzatoModifiche ne '2'}">
								<a style="align-items: center;float:right;display: flex" title="Carica file" href="javascript:caricaLargeFile('<c:out value="${codiceGara}" />', '${item[2]}');"><img src="${pageContext.request.contextPath}/img/navigate-external-link.svg" width="16" height="16" style="margin-right: 5px"/>Carica file</a>
							</c:if>
						</gene:campoScheda>
					</c:when>
					<c:otherwise>
						<gene:campoScheda campo="URLDOC_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  visibile='${gestioneUrl eq "true" }' definizione="T2000;0;;;G1URLDOC" value="${item[18]}" href="javascript:apriUrl('${item[18]}')" modificabile='${campiEsistentiModificabiliDaProfilo and modificabile}'>
							<gene:checkCampoScheda funzione='validURL("##")' obbligatorio="true" messaggio="Il valore dell'indirizzo URL specificato non è valido: è possibile inserire solo un indirizzo URL che inizi con 'ftp://', 'ftps://', 'sftp://', 'http://', 'https://' o 'www.'e non contenga spazi o virgole" onsubmit="false"/>
							<a title="Copia link" href="#" onclick="copyToClipboard(event, '${item[18]}', this)"><img src="${pageContext.request.contextPath}/img/copiaLink.svg" width="16" height="16" style="margin-left: 5px" alt="Copia link"/></a>
							<span class="tooltiptext" style="background-color: #0e3671;color: white;padding: 1px 4px;border-radius: 10px;visibility: hidden" id="tooltip">Copiato negli appunti</span>
						</gene:campoScheda>
					</c:otherwise>
				</c:choose>
				<c:if test='${(modo eq "MODIFICA" or modo eq "NUOVO") and campiEsistentiModificabiliDaProfilo and modificabile}'>
					<gene:campoScheda title="Nome file" nome="selezioneFile_${param.contatore}">
							<input type="file" name="selFile[${param.contatore}]" id="selFile[${param.contatore}]" class="file" size="70" onkeydown="return bloccaCaratteriDaTastiera(event);" onchange='javascript:scegliFile(${param.contatore});'/>
							<span id="checkPDFaDocMessage_${param.contatore}" style="display:none; color: blue;"><b>ATTENZIONE:</b> file non conforme al formato PDF/A</span>
							<img id="imgCheckPDFaDocMessage_${param.contatore}" style="display:none;" src="${pageContext.request.contextPath}/img/pdfa.png" title="File conforme al formato PDF/A" alt="File conforme al formato PDF/A">
							<c:if test="${param.richiestaFirma eq '1' }">
								<span id="spanRichiestaFirma_${param.contatore}" style="float:right;display:none;">Richiesta firma?<input type="checkbox" name="richiestaFirma_${param.contatore}" id="richiestaFirma_${param.contatore}" size="50" <c:if test="${attiDescFile[(4 * param.contatore) - 2 ] eq '1'}">checked</c:if> onchange="javascript:aggiornaRichiestaFirma(${param.contatore},this);"> </span>
							</c:if>
					</gene:campoScheda>
					<gene:campoScheda title="Nome file" nome="selezioneFileDocumentale_${param.contatore}">
							<input type="button" value="Sfoglia da fascicolo" name="selFileDocumentale[${param.contatore}]" id="selFileDocumentale[${param.contatore}]" size="70" onkeydown="return bloccaCaratteriDaTastiera(event);"  onclick='javascript:apriPopupSelezioneDocumentale(${param.contatore});'/>
					</gene:campoScheda>
					<gene:campoScheda title="Nome file" nome="selezioneFileRda_${param.contatore}">
							<input type="button" value="Sfoglia da rda" name="selFileRda[${param.contatore}]" id="selFileRda[${param.contatore}]" size="70" onkeydown="return bloccaCaratteriDaTastiera(event);"  onclick='javascript:apriPopupSelezioneRda(${param.contatore});'/>
					</gene:campoScheda>
					<gene:campoScheda title="Nome file" nome="selezioneFileOda_${param.contatore}">
							<input type="button" value="Sfoglia da Oda" name="selFileOda[${param.contatore}]" id="selFileOda[${param.contatore}]" size="70" onkeydown="return bloccaCaratteriDaTastiera(event);" onclick='javascript:apriPopupSelezioneOda(${param.contatore});'/>
					</gene:campoScheda>
				</c:if>
				<gene:campoScheda campo="VALENZA_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" visibile="false" definizione="N2;0;;;VALENZADG" value="${item[11]}" />
				<gene:campoScheda campo="DATAPROV_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  definizione="D;0;;;G1DATAPROV" value="${item[24]}" modificabile="${campiEsistentiModificabiliDaProfilo and (modificabile || documentoLARGEFILE)}">
				<c:if test='${(modo eq "MODIFICA" or modo eq "NUOVO") and campiEsistentiModificabiliDaProfilo and modificabile and attoAutorizAttivo}'>
					<a style="float:right;" href="javascript:loadDatiAttoAutoriz('${genereGara}','${numeroGara}','${codiceGara}','${item[2]}','${param.contatore}');">
						<span title="'Riporta dati atto autorizzativo' ">Riporta dati atto autorizzativo</span>
					</a>
				</c:if>
				</gene:campoScheda>
				<gene:campoScheda campo="NUMPROV_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  definizione="T20;0;;;G1NUMPROV" value="${item[25]}" modificabile="${campiEsistentiModificabiliDaProfilo and (modificabile || documentoLARGEFILE)}"/>
				<gene:campoScheda campo="DATARILASCIO_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" visibile="${!empty item[23] and item[23] ne ''}" definizione="D;0;;;DATRILDG" value="${item[23]}"  modificabile="false"/>
				<c:if test="${param.richiestaFirma eq '1' or param.firmaDocumento eq '1'}">
					<gene:campoScheda campo="DIGFIRMA_${param.contatore}" entita="W_DOCDIG" campoFittizio="true" visibile="false" definizione="T2;0;" value="${attiDescFile[(4 * param.contatore) - 2 ]}" />
				</c:if>
</c:when>
<c:otherwise>
			<gene:campoScheda campo="CODGAR_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" visibile="false" definizione="T21;1;;;CODGARDG" value="${codiceGara}"/>
				<gene:campoScheda campo="NGARA_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" visibile="false" definizione="T10;0;;;NGARADG"  value="${numeroGara}"/>
				<gene:campoScheda campo="NORDDOCG_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" visibile="false" definizione="N3;1;;;NORDDOCGDG"  />
				<gene:campoScheda campo="GRUPPO_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  visibile="false" definizione="N3;0;;;GRUPPODG"  value="${gruppo}"/>
				<gene:campoScheda campo="IDPRG_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  visibile="false" definizione="T2;0;;;IDPRGDG"  value="PG"/>
				<gene:campoScheda campo="IDDOCDG_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  visibile="false" definizione="N12;0;;;IDDOCDGDG"  />
				<gene:campoScheda campo="IDPRG_${param.contatore}" entita="W_DOCDIG" campoFittizio="true" visibile="false" definizione="T2;1;;;DIGIDPRG" />
				<gene:campoScheda campo="IDDOCDIG_${param.contatore}" entita="W_DOCDIG" campoFittizio="true" visibile="false" definizione="N12;1;;;DIGID" />
				<gene:campoScheda campo="STATODOC_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  visibile="false" definizione="N3;0;;;STODOCDG" />
				<gene:campoScheda campo="TIPOLOGIA_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  visibile="false" definizione="N3;0;;;G1TIPOLODG" value="${tipologia}" />
				<gene:campoScheda campo="DIGDESDOC_${param.contatore}" entita="W_DOCDIG" campoFittizio="true" visibile="false" definizione="T2000;0;;;DIGDESDOC"  gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoNote"/>
				<gene:campoScheda campo="DESCRIZIONE_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  obbligatorio="true" definizione="T200;0;;;DESCLIBDG" />
				<gene:campoScheda campo="TIPOATTO_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  obbligatorio="true" definizione="N7;0;A1202;;TIPOATTODG" visibile="${gruppo eq '10' && not empty requestScope.vernpa}"/>
				<c:if test='${(modo eq "MODIFICA" or modo eq "NUOVO") and (gestioneUrl eq "true" or associataRdaJIRIDE eq "true")}'>
					<gene:campoScheda title="" nome="tipoAllegato_${param.contatore}">
						<input type="radio" value="1" name="allegato_${param.contatore}" id="file_${param.contatore}" checked="checked" onclick="javascript:cambiaTipoAllegato(1,${param.contatore},false);" />
						 allega file
						 <c:if test='${gestioneUrl eq "true"}'>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<input type="radio" value="2" name="allegato_${param.contatore}" id="url_${param.contatore}" onclick="javascript:cambiaTipoAllegato(2,${param.contatore},false);" />
						 specifica URL
						 </c:if>
						 <c:if test='${associataRdaJIRIDE eq "true"}'>
						 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<input type="radio" value="3" name="allegato_${param.contatore}" id="doc_${param.contatore}" onclick="javascript:cambiaTipoAllegato(3,${param.contatore},false);" />
						 allega file da fascicolo
						 </c:if>
						 <c:if test='${associataRdaWSERP eq "true" && tipoWSERP ne "CONI"}'>
						 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<input type="radio" value="4" name="allegato_${param.contatore}" id="doc_${param.contatore}" onclick="javascript:cambiaTipoAllegato(4,${param.contatore},false);" />
						 allega file da rda 
						 </c:if>
						 <c:if test='${gestioneODA eq "1"}'>							
						 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						 <input type="radio" value="5" name="allegato_${param.contatore}" id="docOda" onclick="javascript:cambiaTipoAllegato(5,${param.contatore},false);" />
						 allega file da oda 
						  </c:if>
						 
					</gene:campoScheda>
				</c:if>
				<gene:campoScheda campo="DIGNOMDOC_${param.contatore}" entita="W_DOCDIG" modificabile="false" campoFittizio="true" definizione="T100;0;;;DIGNOMDOC"  value=''>
					<c:if test="${param.isVerificaPDFA eq '1' && attiDescFile[(4 * param.contatore) - 1 ] eq 'pdf/a'}">
						&nbsp;<img id="imgCheckPDFaDocMessage_${param.contatore}" src="${pageContext.request.contextPath}/img/pdfa.png" title="File conforme al formato PDF/A" alt="File conforme al formato PDF/A">
					</c:if>
				</gene:campoScheda>
				<gene:campoScheda campo="DIGTIPDOC_${param.contatore}" entita="W_DOCDIG" campoFittizio="true" visibile="false" definizione="T2;0;;SN;DIGTIPDOC" />
				<gene:campoScheda campo="URLDOC_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  visibile='${gestioneUrl eq "true" }' definizione="T2000;0;;;G1URLDOC" >
					<gene:checkCampoScheda funzione='validURL("##")' obbligatorio="true" messaggio="Il valore dell'indirizzo URL specificato non è valido: è possibile inserire solo un indirizzo URL che inizi con 'ftp://', 'ftps://', 'sftp://', 'http://', 'https://' o 'www.'e non contenga spazi o virgole" onsubmit="false"/>
				</gene:campoScheda>
				<c:if test='${modo eq "MODIFICA" or modo eq "NUOVO"}'>
					<gene:campoScheda title="Nome file" nome="selezioneFile_${param.contatore}">
							<input type="file" name="selFile[${param.contatore}]" id="selFile[${param.contatore}]" class="file" size="70" onkeydown="return bloccaCaratteriDaTastiera(event);" onchange='javascript:scegliFile(${param.contatore});'/>
							<span id="checkPDFaDocMessage_${param.contatore}" style="display:none; color: blue;"><b>ATTENZIONE:</b> file non conforme al formato PDF/A</span>
							<img id="imgCheckPDFaDocMessage_${param.contatore}" style="display:none;" src="${pageContext.request.contextPath}/img/pdfa.png" title="File conforme al formato PDF/A" alt="File conforme al formato PDF/A">
							<c:if test="${param.richiestaFirma eq '1'}">
								<span id="spanRichiestaFirma_${param.contatore}" style="float:right;display:none;">Richiesta firma?<input type="checkbox" name="richiestaFirma_${param.contatore}" id="richiestaFirma_${param.contatore}" size="50" onchange="javascript:aggiornaRichiestaFirma(${param.contatore},this);"> </span>
							</c:if>
					</gene:campoScheda>
					<gene:campoScheda title="Nome file" nome="selezioneFileDocumentale_${param.contatore}">
							<input type="button" value="Sfoglia da fascicolo" name="selFile[${param.contatore}]" id="selFileDocumentale[${param.contatore}]" size="70" onkeydown="return bloccaCaratteriDaTastiera(event);" onclick='javascript:apriPopupSelezioneDocumentale(${param.contatore});'/>
					</gene:campoScheda>
					<gene:campoScheda title="Nome file" nome="selezioneFileRda_${param.contatore}">
							<input type="button" value="Sfoglia da rda" name="selFileRda[${param.contatore}]" id="selFileRda[${param.contatore}]" size="70" onkeydown="return bloccaCaratteriDaTastiera(event);" onclick='javascript:apriPopupSelezioneRda(${param.contatore});'/>
					</gene:campoScheda>
					<gene:campoScheda title="Nome file" nome="selezioneFileOda_${param.contatore}">
							<input type="button" value="Sfoglia da Oda" name="selFileOda[${param.contatore}]" id="selFileOda[${param.contatore}]" size="70" onkeydown="return bloccaCaratteriDaTastiera(event);" onclick='javascript:apriPopupSelezioneOda(${param.contatore});'/>
					</gene:campoScheda>
				</c:if>
				<gene:campoScheda campo="VALENZA_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" visibile="false" definizione="N2;0;;;VALENZADG" value="0"/>
				<gene:campoScheda campo="DATAPROV_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  definizione="D;0;;;G1DATAPROV">
				<c:if test='${(modo eq "MODIFICA" or modo eq "NUOVO") and attoAutorizAttivo}'>
					<a style="float:right;" href="javascript:loadDatiAttoAutoriz('${genereGara}','${numeroGara}','${codiceGara}','${item[2]}','${param.contatore}');">
						<span title="'Riporta dati atto autorizzativo' ">Riporta dati atto autorizzativo</span>
					</a>
				</c:if>
				</gene:campoScheda>
				<gene:campoScheda campo="NUMPROV_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  definizione="T20;0;;;G1NUMPROV"/>
				<gene:campoScheda campo="DATARILASCIO_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" visibile="false" definizione="D;0;;;DATRILDG" />
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
	<c:if test='${gestioneODA eq "1"}'>
		<input type="hidden" id="getdocumentoallegatoWSERP_codiceOda_${param.contatore}" name="getdocumentoallegatoWSERP_codiceOda_${param.contatore}" value="" />
		<input type="hidden" id="getdocumentoallegatoWSERP_idFile_${param.contatore}" name="getdocumentoallegatoWSERP_idFile_${param.contatore}" value="" />
	</c:if>
</c:if>
<gene:javaScript>

var idconfi = "${idconfi}";

function apriPopupSelezioneDocumentale(indice){
	var href = "href=gare/commons/popup-seleziona-da-documentale.jsp?codiceGara=${codiceGara}&indice=" + indice + "&numerowsdocumento=${numeroDocumentoWSDM}";
	if(idconfi){href+="&idconfi="+idconfi;}
	openPopUpCustom(href, "insDocumentiPredefiniti", 700, 500, "no", "yes");
}

function apriPopupSelezioneRda(indice){
	var href = "href=gare/commons/popup-seleziona-da-rda.jsp?codiceGara=${codiceGara}&genere=${genereGara}&indice=" + indice;
	openPopUpCustom(href, "insDocumentiPredefiniti", 700, 500, "no", "yes");
}

function loadDatiAttoAutoriz(genere, ngara, codgar, norddocg, indice){
	
	$.ajax({
		type: "GET",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if (x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "${pageContext.request.contextPath}/pg/SetDatiAttoAutorizzativo.do",
		"data": {
			"genere" : genere,
			"codgar" : codgar,
			"ngara" : ngara,
			"norddocg" : norddocg
		},
		success: function(data){
			if (data.esito != 'OK'){
				alert(data.errore);
			}else{
				if(confirm("Riportare data e numero atto autorizzativo della gara nel provvedimento?")){
					if(data.descrizione){setValue("DOCUMGARA_DESCRIZIONE_" + indice,data.descrizione);}
					if(data.numprov){setValue("DOCUMGARA_NUMPROV_" + indice,data.numprov);}
					if(data.dataprov){setValue("DOCUMGARA_DATAPROV_" + indice,data.dataprov);}	
				}
			} 
		},
		error: function(e){
			alert("Errore nell'estrazione dei dati dell'atto autorizzativo");
		}
	});
	
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

<c:if test='${modo eq "VISUALIZZA" and (gestioneUrl eq "true" or associataRdaJIRIDE eq "true")}'>
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

<c:if test='${(modo eq "MODIFICA" or modo eq "NUOVO") and (gestioneUrl eq "true" or associataRdaJIRIDE eq "true" or associataRdaWSERP eq "true") and idStampa ne "LARGEFILE"}'>
	function cambiaTipoAllegato(tipo, indice, inizializza){
		if(tipo==1){
			$("#rowDOCUMGARA_URLDOC_" + indice).hide();
			setValue("DOCUMGARA_URLDOC_" + indice,"");
			$("#rowW_DOCDIG_DIGNOMDOC_" + indice).show();
			if(inizializza==false){
				setValue("W_DOCDIG_DIGNOMDOC_" + indice,"");
			}
			$("#rowselezioneFile_" + indice).show();
			$("#getdocumentoallegato_nomeallegato_" + indice).val('');
			$("#rowselezioneFileDocumentale_" + indice).hide();
			$("#rowselezioneFileRda_" + indice).hide();
			$("#rowselezioneFileOda_" + indice).hide();
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
				$("#rowselezioneFileOda_" + indice).hide();
			}
			else{
				if(tipo == 4){
					$("#rowDOCUMGARA_URLDOC_" + indice).hide();
					setValue("DOCUMGARA_URLDOC_" + indice,"");
					$("#rowW_DOCDIG_DIGNOMDOC_" + indice).show();
					setValue("W_DOCDIG_DIGNOMDOC_" + indice,"");
					setValue("W_DOCDIG_IDDOCDIG_" + indice,"");
					$("#rowselezioneFile_" + indice).hide();
					$("input#selezioneFile_" + indice).val('');
					$("#rowselezioneFileDocumentale_" + indice).hide();
					$("#rowselezioneFileOda_" + indice).hide();
					$("#rowselezioneFileRda_" + indice).show();
				}else if (tipo == 5){
					console.log("sono qui");
					$("#rowDOCUMGARA_URLDOC_" + indice).hide();
					setValue("DOCUMGARA_URLDOC_" + indice,"");
					$("#rowW_DOCDIG_DIGNOMDOC_" + indice).show();
					setValue("W_DOCDIG_DIGNOMDOC_" + indice,"");
					setValue("W_DOCDIG_IDDOCDIG_" + indice,"");
					$("#rowselezioneFile_" + indice).hide();
					$("input#selezioneFile_" + indice).val('');
					$("#rowselezioneFileDocumentale_" + indice).hide();
					$("#rowselezioneFileRda_" + indice).hide();
					$("#rowselezioneFileOda_" + indice).show();
					document.getElementById("selFile[" + indice + "]")? document.getElementById("selFile[" + indice + "]").value="" : null;
				}else{
					$("#rowDOCUMGARA_URLDOC_" + indice).show();
					$("#rowW_DOCDIG_DIGNOMDOC_" + indice).hide();
					setValue("W_DOCDIG_DIGNOMDOC_" + indice,"");
					setValue("W_DOCDIG_IDDOCDIG_" + indice,"");
					document.getElementById("selFile[" + indice + "]")? document.getElementById("selFile[" + indice + "]").value="" : null;
					$('#checkPDFaDocMessage_'+indice).css("display", "none");
					$('#imgCheckPDFaDocMessage_'+indice).css("display", "none");
					$("#rowselezioneFile_" + indice).hide();
					$("#rowselezioneFileDocumentale_" + indice).hide();
					$("#rowselezioneFileRda_" + indice).hide();
					$("#rowselezioneFileOda_" + indice).hide();
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
	cambiaTipoAllegato(tipoDocumentoAllegato, indice, true);
</c:if>
<c:if test="${gestioneODA eq '1'}">
var posizioneInModifica = 0;
	function apriPopupSelezioneOda(paramContatore){
		posizioneInModifica = paramContatore;
		if(${numoda eq null || numoda eq ""}){
			alert("Numero ODA non presente");
		} else {
			var href = "href=gare/g1docstipula/g1docstipula-popup-seleziona-da-oda.jsp?id=${codiceGara}&provenienza=gare";
			openPopUpCustom(href, "insDocumentiPredefiniti", 700, 500, "no", "yes");
		}
	}
	
	function popolaFormWSERP(codiceOda,nomeallegato,idFile){
		$("#getdocumentoallegatoWSERP_codiceOda_"+posizioneInModifica).val(codiceOda);
		$("#getdocumentoallegatoWSERP_nomeallegato_"+posizioneInModifica).val(nomeallegato);
		$("#getdocumentoallegatoWSERP_idFile_"+posizioneInModifica).val(idFile);
		
		scegliFileDocumentale(nomeallegato,"",posizioneInModifica);
	}
	
	
</c:if>

<c:if test='${associataRdaJIRIDE ne "true"}'>
	$("[id^='rowselezioneFileDocumentale_']").hide();
</c:if>

<c:if test='${associataRdaWSERP ne "true"}'>
	$("[id^='rowselezioneFileRda_']").hide();
</c:if>

<c:if test='${gestioneODA ne "1"}'>
	$("[id^='rowselezioneFileOda_']").hide();
</c:if>

<c:if test='${(modo eq "MODIFICA" or modo eq "NUOVO")}'>
	
	//Si deve visualizzare il campo "richiesta firma" solo quando è valorizzato il nome del file
	var indice = "${param.contatore}";
	var nomdoc = getValue("W_DOCDIG_DIGNOMDOC_"+ indice);
	if(nomdoc!=null && nomdoc!="")
		$("#spanRichiestaFirma_" + indice).show();
</c:if>

<c:if test='${not modificabile and param.tipoDettaglio eq 1 && empty documentoLARGEFILE}'>
	$("#tdTitoloDestra_${param.contatore}").html('<img width="16" height="16" title="Documento pubblicato su portale" style="float:right;padding-right:4px" src="${pageContext.request.contextPath}/img/documento_pubblicato.png"/>');
</c:if>

</gene:javaScript>

