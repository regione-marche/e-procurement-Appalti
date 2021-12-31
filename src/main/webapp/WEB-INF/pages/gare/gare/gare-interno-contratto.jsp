<%
/*
 * Created on: 19/04/2016
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 /* La jsp contiene i campi comuni alle pagine  
 	gare-pg-contratto.jsp
 	gare-pg-aggiudicazione-efficace.jsp
 	gare-pg-stipula-accordo-quadro.jsp
 */
%>


<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

	<gene:campoScheda campo="NGARA" visibile="false" />
	<gene:campoScheda campo="CODGAR1" visibile="false" />
	<gene:campoScheda campo="CODRUP" entita="TORN" where="TORN.CODGAR = GARE.CODGAR1" visibile="false" />
	<gene:campoScheda campo="CLAVOR" visibile="false" />
	<gene:campoScheda campo="NUMERA" visibile="false" />
	<gene:campoScheda campo="TIPGARG" visibile="false" />
	<gene:campoScheda campo="NGARA" entita="GARECONT" where="GARECONT.NGARA=GARE.NGARA AND GARECONT.NCONT=1"  visibile="false"/>
	<gene:campoScheda campo="NCONT" entita="GARECONT" where="GARECONT.NGARA=GARE.NGARA AND GARECONT.NCONT=1"  visibile="false"/>
	<gene:campoScheda campo="IMPAPP" visibile="false" />
	<gene:campoScheda campo="ONPRGE" visibile="false" />
	<gene:campoScheda campo="IMPSIC" visibile="false" />
	<gene:campoScheda campo="IMPNRL" visibile="false" />
	<gene:campoScheda campo="SICINC" visibile="false" />
	<gene:campoScheda campo="ONSOGRIB" visibile="false" />
	<gene:campoScheda campo="RIBAUO" entita="DITG" where="DITG.NGARA5=GARE.NGARA and DITG.CODGAR5=GARE.CODGAR1 and DITG.DITTAO = GARE.DITTA" visibile="false" />
	<gene:campoScheda campo="IMPOFF" entita="DITG" where="DITG.NGARA5=GARE.NGARA and DITG.CODGAR5=GARE.CODGAR1 and DITG.DITTAO = GARE.DITTA" visibile="false" />
	<gene:campoScheda campo="ACCQUA" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="false"/>
	<gene:campoScheda campo="AQOPER" entita="GARE1" where="GARE1.NGARA = GARE.NGARA" visibile="false"/>
	<gene:campoScheda campo="NGARA5" entita="DITG" where="DITG.NGARA5 = GARE.NGARA and DITG.CODGAR5=GARE.CODGAR1 and DITG.DITTAO=GARE.DITTA"  visibile="false"/>
	<gene:campoScheda campo="CODGAR5" entita="DITG" where="DITG.NGARA5 = GARE.NGARA and DITG.CODGAR5=GARE.CODGAR1 and DITG.DITTAO=GARE.DITTA"  visibile="false"/>
	<gene:campoScheda campo="DITTAO" entita="DITG" where="DITG.NGARA5 = GARE.NGARA and DITG.CODGAR5=GARE.CODGAR1 and DITG.DITTAO=GARE.DITTA"  visibile="false"/>
	<gene:campoScheda campo="MODLICG" visibile="false" />
	<gene:campoScheda campo="ALTRISOG" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="false" />
				
	<gene:gruppoCampi idProtezioni="DITTAAGG" >
		<c:choose>
			<c:when test="${param.aqoper ne '2' }">
				<c:set var="titoloSezione" value="Ditta aggiudicataria"/>
			</c:when>
			<c:otherwise>
				<c:set var="titoloSezione" value="Importo accordo quadro"/>
			</c:otherwise>
		</c:choose>
		<gene:campoScheda>
			<td colspan="2"><b>${titoloSezione }</b></td>
		</gene:campoScheda>
		<c:if test='${modo eq "VISUALIZZA"}' >
			<c:set var="link" value='javascript:archivioImpresaAggDef();' />
		</c:if>
		<gene:campoScheda campo="DITTA"  href='${gene:if(gene:checkProt(pageContext, "MASC.VIS.GENE.ImprScheda"), link, "")}' visibile="${param.aqoper ne '2' }"/>
		<gene:campoScheda campo="NOMIMA" href='${gene:if(gene:checkProt(pageContext, "MASC.VIS.GENE.ImprScheda"), link, "")}' visibile="${param.aqoper ne '2' }"/>
		<gene:campoScheda campo="IAGGIU"  modificabile="false" visibile="${param.aqoper ne '2' }"/>
		<gene:campoScheda campo="IMPQUA"  entita="GARECONT" where="GARECONT.NGARA=GARE.NGARA AND GARECONT.NCONT=1" visibile="${datiRiga.TORN_ACCQUA eq 1 }" />
		<gene:campoScheda campo="ESECSCIG" entita="GARECONT" where="GARECONT.NGARA=GARE.NGARA AND GARECONT.NCONT=1" visibile="${datiRiga.TORN_ACCQUA eq 1 && datiRiga.GARE1_AQOPER eq 1 && datiRiga.TORN_ALTRISOG ne 3}" />
		<gene:campoScheda campo="CONTSPE" entita="GARECONT" where="GARECONT.NGARA=GARE.NGARA AND GARECONT.NCONT=1" visibile="${datiRiga.TORN_ACCQUA eq 1 }" />
		<gene:campoScheda campo="RICSUB" entita="DITG" where="DITG.NGARA5 = GARE.NGARA and DITG.CODGAR5=GARE.CODGAR1 and DITG.DITTAO=GARE.DITTA"  modificabile="${!empty datiRiga.GARE_DITTA}" visibile="${param.aqoper ne '2' }"/>
	</gene:gruppoCampi>
	
	<c:choose>
		<c:when test="${param.aqoper ne '2' }">
			<gene:fnJavaScriptScheda funzione='aggiornaImpgarDaImpqua("#GARECONT_IMPQUA#",#TORN_ACCQUA#,-1)' elencocampi='GARECONT_IMPQUA' esegui="false" />
		</c:when>
		<c:otherwise>
			<gene:fnJavaScriptScheda funzione='aggiornaImpgarDaImpqua("#GARECONT_IMPQUA#",#TORN_ACCQUA#,${numeroDitteAggiudicatarie })' elencocampi='GARECONT_IMPQUA' esegui="false" />
		</c:otherwise>
	</c:choose>
	
		
	<gene:gruppoCampi idProtezioni="RICHDOC" >
		<gene:campoScheda addTr="false">
			<tr id="rowTITOLO_RICHIESTA_DOC">
				<td colspan="2"><b>Richiesta documentazione per comprova requisiti di ordine generale</b></td>
			</tr>
		</gene:campoScheda>
		<gene:campoScheda campo="DRICHDOCCR" />
		<gene:campoScheda campo="NPROREQ" />
		<gene:campoScheda campo="DTERMDOCCR"  />
	</gene:gruppoCampi>	


	<c:choose>
		<c:when test="${param.chiamante eq 'contratto'}">
			<c:set var="stringaControllo" value="SEZ.VIS.GARE.GARE-scheda.CONTRATTO.GARCOMPREQ"/>
		</c:when>
		<c:when test="${param.chiamante eq 'stipula-accordo-quadro'}">
			<c:set var="stringaControllo" value="SEZ.VIS.GARE.GARE-scheda.STIPULA.GARCOMPREQ"/>
		</c:when>
		<c:when test="${param.chiamante eq 'aggiudicazione-efficace'}">
			<c:set var="stringaControllo" value="SEZ.VIS.GARE.GARE-scheda.AGGEFF.GARCOMPREQ"/>
		</c:when>
	</c:choose>

	<c:set var="result" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetValoriGARCOMPREQFunction", pageContext, param.ngara)}' />

	
		<gene:campoScheda visibile='${gene:checkProt(pageContext, stringaControllo) }' addTr="false">
			<tr id="rowTITOLO_RICEZIONE_DOC">
				<td colspan="2"><b>Ricezione documentazione per comprova requisiti di ordine generale</b></td>
			</tr>
		</gene:campoScheda>
		<gene:campoScheda addTr="false" visibile='${gene:checkProt(pageContext, stringaControllo) }'>
			<tr>
			<td colspan="2">
			
				<table id="tabellaRicezioneDocumenti" class="griglia" >
		</gene:campoScheda>	
		
		<jsp:include page="/WEB-INF/pages/commons/interno-sezione-multipla-singola-riga.jsp" >
			<jsp:param name="entita" value='GARCOMPREQ'/>
			<jsp:param name="chiave" value='${param.ngara}'/>
			<jsp:param name="nomeAttributoLista" value='datiGARCOMPREQ' />
			<jsp:param name="idProtezioni" value="GARCOMPREQ" />
			<jsp:param name="sezioneListaVuota" value="true" />
			<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/garcompreq/garcompreq-scheda.jsp"/>
			<jsp:param name="arrayCampi" value="'GARCOMPREQ_ID_', 'GARCOMPREQ_NGARA_', 'GARCOMPREQ_DRICREQ_','GARCOMPREQ_NRICREQ_'"/>
			<jsp:param name="titoloSezione" value="<br>Data ricezione documentazione" />
			<jsp:param name="titoloNuovaSezione" value="<br>Nuova data ricezione documentazione" />
			<jsp:param name="descEntitaVociLink" value="data ricezione documentazione" />
			<jsp:param name="msgRaggiuntoMax" value="e date"/>
			<jsp:param name="usaContatoreLista" value="true"/>
			<jsp:param name="numMaxDettagliInseribili" value="5"/>
			<jsp:param name="sezioneInseribile" value="true"/>
			<jsp:param name="sezioneEliminabile" value="true"/>
		</jsp:include>
		<gene:campoScheda addTr="false" visibile='${gene:checkProt(pageContext, stringaControllo) }'>
			</table>
			</td>
		</td>
		</tr>
	</gene:campoScheda>
		
	
	
	<gene:gruppoCampi idProtezioni="AGGEFF" >
		<gene:campoScheda nome="AGGEFF">
			<td colspan="2"><b>Fase integrativa dell'efficacia</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="DRICZDOCCR" />
		<gene:campoScheda campo="TAGGEFF" />
		<gene:campoScheda campo="NAGGEFF" />
		<gene:campoScheda campo="DAGGEFF" />
	</gene:gruppoCampi>

	
	
	
	<gene:gruppoCampi idProtezioni="CAUZDEF" visibile="${param.aqoper ne '2' }">
		<gene:campoScheda>
			<td colspan="2"><b>Polizza cauzione definitiva</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="RIDISO" />
		<gene:campoScheda campo="IMPGAR" >
			<gene:calcoloCampoScheda funzione='calcolaIMPGAR()' elencocampi='GARE_RIDISO'/>
		</gene:campoScheda>
		
		<gene:campoScheda campo="NQUIET" />
		<gene:campoScheda campo="DQUIET" />
		<gene:campoScheda campo="ISTCRE" />
		<gene:campoScheda campo="INDIST" />
		<gene:campoScheda campo="DSCAPO" entita="GARECONT" where="GARECONT.NGARA=GARE.NGARA AND GARECONT.NCONT=1"/>
		<gene:campoScheda campo="NPROPO" entita="GARECONT" where="GARECONT.NGARA=GARE.NGARA AND GARECONT.NCONT=1"/>
	</gene:gruppoCampi>
	
	<gene:gruppoCampi idProtezioni="COORBA" visibile="${param.aqoper ne '2' }">
		<gene:campoScheda>
			<td colspan="2"><b>Coordinate bancarie ditta aggiudicataria</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="BANAPP" entita="GARECONT" where="GARECONT.NGARA=GARE.CODGAR1 AND GARECONT.NCONT=${param.ncont}"/>
		<gene:campoScheda campo="COORBA" entita="GARECONT" where="GARECONT.NGARA=GARE.CODGAR1 AND GARECONT.NCONT=${param.ncont}"/>
		<gene:campoScheda campo="CODBIC" entita="GARECONT" where="GARECONT.NGARA=GARE.CODGAR1 AND GARECONT.NCONT=${param.ncont}"/>
	</gene:gruppoCampi>
	
	<c:if test="${numeroDitteAggiudicatarie > 0 }">
		<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
			<jsp:param name="entita" value='DITGAQ'/>
			<jsp:param name="chiave" value='${param.ngara}'/>
			<jsp:param name="nomeAttributoLista" value='listaDitteAggiudicatarie' />
			<jsp:param name="idProtezioni" value="DITGAQ" />
			<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/ditgaq/ditte-aggiudicatarie-stipula.jsp"/>
			<jsp:param name="arrayCampi" value="'DITGAQ_ID_', 'DITGAQ_NGARA_', 'DITGAQ_DITTAO_','IMPR_NOMIMP_','DITGAQ_IAGGIU_','DITG_RICSUB_', 'DITGAQ_RIDISO_','DITGAQ_IMPGAR_','DITGAQ_NQUIET_','DITGAQ_DQUIET_','DITGAQ_ISTCRE_','DITGAQ_INDIST_','DITG_RIBAUO_','DITG_IMPOFF_','DITGAQ_BANAPP_','DITGAQ_COORBA_','DITGAQ_CODBIC_'"/>		
			<jsp:param name="titoloSezione" value="Ditta aggiudicataria" />
			<jsp:param name="titoloNuovaSezione" value="Nuova ditta aggiudicataria" />
			<jsp:param name="descEntitaVociLink" value="ditta aggiudicataria" />
			<jsp:param name="msgRaggiuntoMax" value="e ditte aggiudicatarie"/>
			<jsp:param name="usaContatoreLista" value="true"/>
		</jsp:include>
	</c:if>
	
	<gene:gruppoCampi idProtezioni="COMMDITTECONTRATTO" >
		<gene:campoScheda visibile="${param.chiamante ne 'aggiudicazione-efficace' }">
			<td colspan="2"><b>Comunicazione alle ditte della data stipula del contratto</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="DCOMDITTAGG" visibile="${param.chiamante ne 'aggiudicazione-efficace' }"/>
		<gene:campoScheda campo="NCOMDITTAGG" visibile="${param.chiamante ne 'aggiudicazione-efficace' }"/>
		<gene:campoScheda campo="DCOMDITTNAG" visibile="${param.chiamante ne 'aggiudicazione-efficace' }"/>
		<gene:campoScheda campo="NCOMDITTNAG" visibile="${param.chiamante ne 'aggiudicazione-efficace' }"/>
		
	</gene:gruppoCampi>
	
	<c:choose>
		<c:when test="${param.chiamante eq 'stipula-accordo-quadro'}">
			<c:set var="titoloSezioneATTOCONT" value="Stipula accordo quadro "/>
		</c:when>
		<c:otherwise>
			<c:set var="titoloSezioneATTOCONT" value="Atto contrattuale"/>
		</c:otherwise>
	</c:choose>
	
	<gene:gruppoCampi idProtezioni="ATTOCONT">
		<gene:campoScheda addTr="false" visibile="${param.chiamante ne 'aggiudicazione-efficace' }">
			<tr id="rowTITOLO_ATTO_CONTRATTUALE">
				<td colspan="2"><b>${titoloSezioneATTOCONT }</b></td>
			</tr>
		</gene:campoScheda>
		<gene:campoScheda campo="TIATTO" visibile="${param.chiamante ne 'stipula-accordo-quadro'  && param.chiamante ne 'aggiudicazione-efficace'}"/>
		<gene:campoScheda campo="NREPAT" visibile="${param.chiamante ne 'stipula-accordo-quadro' && param.chiamante ne 'aggiudicazione-efficace'}"/>
		<gene:campoScheda campo="DAATTO" visibile="${param.chiamante ne 'aggiudicazione-efficace' }"/>
		<gene:campoScheda campo="NPROAT" entita="GARECONT" where="GARECONT.NGARA=GARE.NGARA AND GARECONT.NCONT=1" visibile='${param.chiamante ne "stipula-accordo-quadro" && param.chiamante ne "aggiudicazione-efficace"}'/>
		<gene:campoScheda campo="DATRES" entita="GARECONT" where="GARECONT.NGARA=GARE.NGARA AND GARECONT.NCONT=1" visibile='${param.chiamante ne "stipula-accordo-quadro" && param.chiamante ne "aggiudicazione-efficace"}'/>
		<gene:campoScheda campo="NMAXIMO" visibile="${param.chiamante ne 'stipula-accordo-quadro' && param.chiamante ne 'aggiudicazione-efficace'}"/>
		<gene:campoScheda campo="VERCAN" visibile="${param.tipoAppalto eq '1' && param.chiamante ne 'stipula-accordo-quadro' && param.chiamante ne 'aggiudicazione-efficace'}"/>
		<gene:campoScheda campo="VERNUM" visibile="${param.tipoAppalto eq '1' && param.chiamante ne 'stipula-accordo-quadro' && param.chiamante ne 'aggiudicazione-efficace'}"/>
		<gene:campoScheda campo="LREGCO" entita="GARECONT" where="GARECONT.NGARA=GARE.NGARA AND GARECONT.NCONT=1" visibile="${param.chiamante ne 'stipula-accordo-quadro'  && param.chiamante ne 'aggiudicazione-efficace'}"/>
		<gene:campoScheda campo="NREGCO" entita="GARECONT" where="GARECONT.NGARA=GARE.NGARA AND GARECONT.NCONT=1" visibile="${param.chiamante ne 'stipula-accordo-quadro'  && param.chiamante ne 'aggiudicazione-efficace'}"/>
		<gene:campoScheda campo="DREGCO" entita="GARECONT" where="GARECONT.NGARA=GARE.NGARA AND GARECONT.NCONT=1" visibile="${param.chiamante ne 'stipula-accordo-quadro'  && param.chiamante ne 'aggiudicazione-efficace'}"/>
		<gene:campoScheda campo="NUMCONT" entita="GARECONT" where="GARECONT.NGARA=GARE.NGARA AND GARECONT.NCONT=1" visibile="${param.chiamante ne 'stipula-accordo-quadro'  && param.chiamante ne 'aggiudicazione-efficace'}"/>
		<gene:campoScheda campo="DCONSD" entita="GARECONT" where="GARECONT.NGARA=GARE.NGARA AND GARECONT.NCONT=1" visibile="${param.chiamante ne 'stipula-accordo-quadro'  && param.chiamante ne 'aggiudicazione-efficace'}"/>
	</gene:gruppoCampi>

	