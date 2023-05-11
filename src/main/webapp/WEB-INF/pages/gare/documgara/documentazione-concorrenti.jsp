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

<jsp:include page="/WEB-INF/pages/commons/defCostantiAppalti.jsp" />

<c:set var="codiceGara" value='${fn:substringBefore(param.chiave, ";")}' />
<c:set var="numeroGara" value='${fn:substringAfter(param.chiave, ";")}' />

<c:set var="bustalotti" value='${param.bustalotti}'/>
<c:set var="busta" value='${param.busta}'/>
<c:set var="sezionitec" value='${param.sezionitec}'/>
<c:set var="fasEle" value='${param.fasEle}'/>

<c:choose>
	<c:when test='${(item[7] eq "5" and param.tipoDettaglio eq 1) or (fasEle eq rinnovo and item[16] eq "2")}'>
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
				<gene:campoScheda campo="NORDDOCG_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  visibile="false" definizione="N3;1;;;NORDDOCGDG" value="${item[2]}" />
				<gene:campoScheda campo="GRUPPO_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"   visibile="false" definizione="N3;0;;;GRUPPODG" value="${item[3]}"/>
				<gene:campoScheda campo="FASGAR_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  visibile="false" definizione="N7;0;A1011;;FASGARDG" value="${item[4]}" />
				<gene:campoScheda campo="BUSTA_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" obbligatorio="true" modificabile="false" definizione="N7;0;A1013;;BUSTADG" value="${item[15]}" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoBusta" />
				<gene:campoScheda campo="SEZTEC_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  visibile="${busta eq 2 and sezionitec eq '1'}" obbligatorio="true" modificabile="${item[21] ne '1' and campiEsistentiModificabiliDaProfilo and modificabile}" definizione="N7;0;A1168;;SEZTECDG" value="${item[25]}" />
				<gene:campoScheda campo="FASELE_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" obbligatorio="true" modificabile="${item[21] ne '1' and campiEsistentiModificabiliDaProfilo and modificabile and item[20] ne 'DGUE'}" visibile="${(genereGara eq '10' or genereGara eq '20') }" definizione="N7;0;A1104;;FASELEDG" value="${item[16]}" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoFasele"/>
				<c:choose>
					<c:when test="${!modificabile or item[21] eq '1'}">
						<gene:campoScheda campo="NGARA_${param.contatore}" entita="DOCUMGARA" title="Codice lotto ${gene:if(modo eq 'MODIFICA' or modo eq 'NUOVO','(valorizzare solo se documento specifico del lotto)','') }" visibile="${(genereGara eq '3' and bustalotti eq '1' and (busta eq '2' or busta eq '3') and !(item[21] eq '1' and busta eq '3')) or genereGara eq '1'}" campoFittizio="true" definizione="T10;0;;;NGARADG" value="${item[1]}" modificabile='false'/>
					</c:when>
					<c:otherwise>
						<gene:archivio titolo="lotti di gara"
							lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.DOCUMGARA.NGARA"),"gare/gare/gare-popup-lista-lotti.jsp","")}'
							scheda=''
							schedaPopUp=''
							campi="GARE.NGARA"
							functionId="default_${busta}"
							parametriWhere="T:${codiceGara}"
							chiave=""
							formName="formLotti_${param.contatore}">
							<gene:campoScheda campo="NGARA_${param.contatore}" entita="DOCUMGARA" title="Codice lotto ${gene:if(modo eq 'MODIFICA' or modo eq 'NUOVO','(valorizzare solo se documento specifico del lotto)','') }" campoFittizio="true" visibile="${(genereGara eq '3' and bustalotti eq '1' and (busta eq '2' or busta eq '3') and !(item[21] eq '1' and busta eq '3')) or genereGara eq '1'}" definizione="T10;0;;;NGARADG" value="${item[1]}" modificabile="${item[21] ne '1' and campiEsistentiModificabiliDaProfilo}"/>
						</gene:archivio>
					
					</c:otherwise>
				</c:choose>
				
				<gene:campoScheda campo="REQCAP_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" visibile="${(busta eq 1 or busta eq 4) and item[20] ne 'DGUE'}" modificabile="${item[21] ne '1' and campiEsistentiModificabiliDaProfilo and modificabile }" definizione="N7;0;A1059;;REQCAPDG" value="${item[8]}" />
				<gene:campoScheda campo="TIPODOC_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" visibile="${item[20] ne 'DGUE'}" modificabile="${item[21] ne '1' and campiEsistentiModificabiliDaProfilo and modificabile }" definizione="N7;0;A1057;;TIPODOCDG" value="${item[9]}" />
				<gene:campoScheda campo="DESCRIZIONE_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  modificabile="${item[21] ne '1' and campiEsistentiModificabiliDaProfilo and modificabile and item[20] ne 'DGUE'}" obbligatorio="true" definizione="T200;0;;;DESCLIBDG" value="${item[13]}" />
				<gene:campoScheda campo="CONTESTOVAL_${param.contatore}" entita="DOCUMGARA" title="Contesto validità ${gene:if(modo eq 'MODIFICA' or modo eq 'NUOVO','(valorizzare solo se documento specifico del tipo di operatore)','') }" campoFittizio="true"  visibile="${item[21] ne '1' and item[20] ne 'DGUE'}" modificabile="${item[21] ne '1' and campiEsistentiModificabiliDaProfilo and modificabile }" definizione="N7;0;Ag008;;CONTVALDG" value="${item[10]}" />
				<gene:campoScheda campo="OBBLIGATORIO_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  modificabile="${item[21] ne '1' and campiEsistentiModificabiliDaProfilo and modificabile and item[20] ne 'DGUE'}" definizione="T2;0;;SN;OBBLIGDG" value="${item[12]}" />
				<gene:campoScheda campo="MODFIRMA_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  modificabile="${item[21] ne '1' and campiEsistentiModificabiliDaProfilo and modificabile and item[20] ne 'DGUE'}" definizione="N7;0;A1105;;MODFIRMADG" value="${item[17]}" />
				<gene:campoScheda campo="IDPRG_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  visibile="false" definizione="T2;0;;;IDPRGDG" value="${item[5]}" />
				<gene:campoScheda campo="IDDOCDG_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  visibile="false" definizione="N12;0;;;IDDOCDGDG" value="${item[6]}" />
				<gene:campoScheda campo="IDPRG_${param.contatore}" entita="W_DOCDIG" campoFittizio="true" visibile="false" definizione="T2;1;;;DIGIDPRG" value="${item[5]}"/>
				<gene:campoScheda campo="IDDOCDIG_${param.contatore}" entita="W_DOCDIG" campoFittizio="true" visibile="false" definizione="N12;1;;;DIGID"  value="${item[6]}" />
				<gene:campoScheda campo="DIGNOMDOC_${param.contatore}" entita="W_DOCDIG" campoFittizio="true" title="Fac-simile" modificabile="false" visibile="${item[21] ne '1'}" definizione="T100;0;;;DIGNOMDOC"  value="${documentazioneConcorrentiDescFile[(3 * param.contatore) - 2 ]}" href="javascript:visualizzaFileAllegato('${item[5]}','${item[6]}',${gene:string4Js(documentazioneConcorrentiDescFile[(3 * param.contatore) - 2 ])});">
					<c:if test='${item[20] ne "DGUE" and not empty documentazioneConcorrentiDescFile[(3 * param.contatore) - 2 ] and modo eq "VISUALIZZA" and campiEsistentiModificabiliDaProfilo and item[7] ne "5" and not empty param.firmaRemota and param.autorizzatoModifiche ne "2" and modificabile and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.FirmaRemotaDocumenti")}'>
						<a style="float:right;" href="javascript:openModal('${item[5]}','${item[6]}','${documentazioneConcorrentiDescFile[(3 * param.contatore) - 2 ]}','${pageContext.request.contextPath}');">
						<img src="${pageContext.request.contextPath}/img/firmaRemota.png" title="Firma digitale del documento" alt="Firma documento" width="16" height="16">
						<span title="Firma digitale del documento">Firma documento</span></a>
					</c:if>
					<c:if test="${item[20] eq 'DGUE' }">
						<a style="float:right;" href="javascript:apriConMDGUE('<c:out value="${codiceGara}" />', '${item[5]}', '${item[6]}')">Apri con M-DGUE</a>
					</c:if>
				</gene:campoScheda>
				<gene:campoScheda campo="GENTEL_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" visibile="false" modificabile="false" definizione="T2;0;;SN;G1GENTEL" value="${item[21]}" />
				<c:if test='${(modo eq "MODIFICA" or modo eq "NUOVO") and item[21] ne "1" and campiEsistentiModificabiliDaProfilo and modificabile and item[20] ne "DGUE"}'>
					<gene:campoScheda title="Nome file" nome="selezioneFile_${param.contatore}">
							<input type="file" name="selFile[${param.contatore}]" id="selFile[${param.contatore}]" class="file" size="70" onkeydown="return bloccaCaratteriDaTastiera(event);" onchange='javascript:scegliFile(${param.contatore});'/>
					</gene:campoScheda>
				</c:if>
				<gene:campoScheda campo="DIGDESDOC_${param.contatore}" entita="W_DOCDIG" campoFittizio="true"  visibile="false" definizione="T2000;0;;;DIGDESDOC"  gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoNote" value="${documentazioneConcorrentiDescFile[(3 * param.contatore) - 3]}"/>
				<gene:campoScheda campo="STATODOC_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" visibile="false" definizione="N7;0;A1061;;STODOCDG" value="${item[7]}" />
				<gene:campoScheda campo="VALENZA_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" visibile="false" definizione="N2;0;;;VALENZADG" value="${item[11]}" />
				<c:if test="${item[21] eq '1'}">
					<gene:campoScheda campo="GENTEL_FIT_${param.contatore}" title="Fac-simile" campoFittizio="true" modificabile="false" definizione="T50" value="Documento generato in automatico da sistema" />
				</c:if>
				<gene:campoScheda campo="DATARILASCIO_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" visibile="${!empty item[23] and item[23] ne ''}" definizione="D;0;;;DATRILDG" value="${item[23]}"  modificabile="false"/>
				<gene:campoScheda campo="IDSTAMPA_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" visibile="false" definizione="T20;0;;;G1IDSTAMPA" value="${item[20]}" />
</c:when>
<c:otherwise>
				<gene:campoScheda campo="CODGAR_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" visibile="false" definizione="T21;1;;;CODGARDG" value="${codiceGara}"/>
				<gene:campoScheda campo="NORDDOCG_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  visibile="false" definizione="N3;1;;;NORDDOCGDG"  />
				<gene:campoScheda campo="GRUPPO_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"   visibile="false" definizione="N3;0;;;GRUPPODG" value="3"/>
				<gene:campoScheda campo="FASGAR_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  visibile="false" definizione="N7;0;A1011;;FASGARDG"  />
				<gene:campoScheda campo="BUSTA_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  obbligatorio="false" modificabile="${empty busta or busta eq ''}" definizione="N7;0;A1013;;BUSTADG" value="${busta}" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoBusta"/>
				<gene:campoScheda campo="SEZTEC_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  visibile="${busta eq 2 and sezionitec eq '1'}" obbligatorio="true" definizione="N7;0;A1168;;SEZTECDG"  />
				<gene:campoScheda campo="FASELE_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" obbligatorio="true" visibile="${genereGara eq '10' or genereGara eq '20'}" value="${gene:if(fasEle eq rinnovo,'3','') }" definizione="N7;0;A1104;;FASELEDG" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoFasele"/>
				<gene:archivio titolo="lotti di gara"
					lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.DOCUMGARA.NGARA"),"gare/gare/gare-popup-lista-lotti.jsp","")}'
					scheda=''
					schedaPopUp=''
					campi="GARE.NGARA"
					functionId="default_${busta}"
					parametriWhere="T:${codiceGara}"
					chiave=""
					formName="formLotti_${param.contatore}">
					<gene:campoScheda campo="NGARA_${param.contatore}" entita="DOCUMGARA" title="Codice lotto ${gene:if(modo eq 'MODIFICA' or modo eq 'NUOVO','(valorizzare solo se documento specifico del lotto)','') }" campoFittizio="true" visibile="${(genereGara eq '3' and bustalotti eq '1' and (busta eq '2' or busta eq '3')) or genereGara eq '1'}" definizione="T10;0;;;NGARADG"  value="${numeroGara}"/>
				</gene:archivio>
				<gene:campoScheda campo="REQCAP_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" visibile="${busta eq 1 or busta eq 4}" definizione="N7;0;A1059;;REQCAPDG" />
				<gene:campoScheda campo="TIPODOC_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  definizione="N7;0;A1057;;TIPODOCDG" />
				<gene:campoScheda campo="DESCRIZIONE_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  obbligatorio="true" definizione="T200;0;;;DESCLIBDG" />
				<gene:campoScheda campo="CONTESTOVAL_${param.contatore}" entita="DOCUMGARA" title="Contesto validità ${gene:if(modo eq 'MODIFICA' or modo eq 'NUOVO','(valorizzare solo se documento specifico del tipo di operatore)','') }" campoFittizio="true"  definizione="N7;0;Ag008;;CONTVALDG" />
				<gene:campoScheda campo="OBBLIGATORIO_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  definizione="T2;0;;SN;OBBLIGDG" />
				<gene:campoScheda campo="MODFIRMA_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  definizione="N7;0;A1105;;MODFIRMADG" />
				<gene:campoScheda campo="IDPRG_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  visibile="false" definizione="T2;0;;;IDPRGDG"  value="PG"/>
				<gene:campoScheda campo="IDDOCDG_${param.contatore}" entita="DOCUMGARA" campoFittizio="true"  visibile="false" definizione="N12;0;;;IDDOCDGDG"  />
				<gene:campoScheda campo="IDPRG_${param.contatore}" entita="W_DOCDIG" campoFittizio="true" visibile="false" definizione="T2;1;;;DIGIDPRG" />
				<gene:campoScheda campo="IDDOCDIG_${param.contatore}" entita="W_DOCDIG" campoFittizio="true" visibile="false" definizione="N12;1;;;DIGID" />
				<gene:campoScheda campo="DIGNOMDOC_${param.contatore}" entita="W_DOCDIG" campoFittizio="true" title="Fac-simile" modificabile="false" definizione="T100;0;;;DIGNOMDOC" value=''/>
				<c:if test='${modo eq "MODIFICA" or modo eq "NUOVO"}'>
					<gene:campoScheda title="Nome file" nome="selezioneFile_${param.contatore}">
							<input type="file" name="selFile[${param.contatore}]" id="selFile[${param.contatore}]" class="file" size="70" onkeydown="return bloccaCaratteriDaTastiera(event);" onchange='javascript:scegliFile(${param.contatore});'/>
					</gene:campoScheda>
				</c:if>
				<gene:campoScheda campo="DIGDESDOC_${param.contatore}" entita="W_DOCDIG" campoFittizio="true"  visibile="false" definizione="T2000;0;;;DIGDESDOC"  gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoNote"/>
				<gene:campoScheda campo="STATODOC_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" visibile="false" definizione="N7;0;A1061;;STODOCDG" />
				<gene:campoScheda campo="VALENZA_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" visibile="false" definizione="N2;0;;;VALENZADG" value="0"/>
				<gene:campoScheda campo="GENTEL_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" visibile="$false" definizione="T2;0;;SN;G1GENTEL"  />
				<gene:campoScheda campo="DATARILASCIO_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" visibile="false" definizione="D;0;;;DATRILDG" />
				<gene:campoScheda campo="IDSTAMPA_${param.contatore}" entita="DOCUMGARA" campoFittizio="true" visibile="false" definizione="T20;0;;;G1IDSTAMPA"  />
</c:otherwise>
</c:choose>

<gene:javaScript>

	
	//Customizzazione della funzione delElementoSchedaMultipla per evitare che possa
 	//essere eliminato un documento con GENTEL=1
 	function delDocumentazioneConcorrenti(id, label, tipo, campi){
		var gentel = getValue("DOCUMGARA_GENTEL_" + id);
		var idstampa = getValue("DOCUMGARA_IDSTAMPA_" + id);
		if(gentel == 1 || idstampa == "DGUE")
			alert("Non è possibile eliminare tale tipologia di documento")
		else
			delElementoSchedaMultipla(id,label,tipo,campi);
	}
	
	<c:if test='${not modificabile }'>
		$("#tdTitoloDestra_${param.contatore}").html('<img width="16" height="16" title="Documento pubblicato su portale" style="float:right;padding-right:4px" src="${pageContext.request.contextPath}/img/documento_pubblicato.png"/>');
	</c:if>
</gene:javaScript>