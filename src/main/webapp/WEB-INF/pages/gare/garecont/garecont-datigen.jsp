<%
/*
 * Created on: 06/06/2014
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="AliceResources" />

<%--
Viene sbiancata la variabile di sessione keyParentComunicazioni che viene inizializzata nella lista delle comunicazioni.
Se si crea una nuova comunicazione senza passare dalla lista delle comunicazioni la variabile altrimenti rimane valorizzata.
 --%>
<c:set var="keyParentComunicazioni" value="" scope="session"/>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<script type="text/javascript" src="${contextPath}/js/jquery.documenti.gara.js"></script>
<script type="text/javascript" src="${contextPath}/js/common-gare.js"></script>

<c:set var="digitalSignatureUrlCheck" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "digital-signature-check-url")}'/>
<c:set var="digitalSignatureProvider" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "digital-signature-provider")}'/>
<c:choose>
	<c:when test="${!empty digitalSignatureUrlCheck && !empty digitalSignatureProvider && (digitalSignatureProvider eq 1 || digitalSignatureProvider eq 2)}">
		<c:set var="digitalSignatureWsCheck" value='1'/>
	</c:when>
	<c:otherwise>
		<c:set var="digitalSignatureWsCheck" value='0'/>
	</c:otherwise>
</c:choose>

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>

<c:set var="ngara" value='${gene:getValCampo(key, "GARECONT.NGARA")}' />
<c:set var="codiceGara" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetCodiceGaraFunction", pageContext)}' />
<c:set var="stato" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetStatoGarecontFunction",pageContext,ngara,"1")}' />
<c:set var="firmaProvider" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "digital-signature-provider")}'/>
<c:if test='${firmaProvider eq 2}'>
	<c:set var="firmaRemota" value="true"/>
</c:if>
<c:choose>
	<c:when test='${not empty param.id}'>
		<c:set var="id" value="${param.id}" />
	</c:when>
	<c:when test='${not empty requestScope.id}'>
		<c:set var="id" value="${requestScope.id}" />
	</c:when>
	<c:otherwise>
		<c:set var="id" value="${id}" />
	</c:otherwise>
</c:choose>

<c:set var="tmp" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.CaricaDescPuntiContattoFunction",  pageContext, ngara, "GARECONT", id)}'/>

<c:set var="ruoloUtente" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetMeruoloDaIdmericFunction", pageContext, id, profiloUtente.id)}' />

<c:set var="richiestaFirma" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "documentiDb.richiestaFirma")}'/>

<%/* Dati generali della gara */%>
<gene:formScheda entita="GARECONT" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreOrdine">
	
	<c:if test='${modo eq "VISUALIZZA" || metodo ne "modifica" }'>
		<c:set var="IsW_CONFCOMPopolata" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsW_CONFCOMPopolataFunction",pageContext,"7")}' />
		<c:set var="isFascicoloDocumentaleAbilitato" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.IsFascicoloDocumentaleValidoFunction",  pageContext, gene:getValCampo(key, "NGARA"), idconfi)}' />
	</c:if>
	
	<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-scheda.jsp">
		<jsp:param name="entita" value="MERIC"/>
		<jsp:param name="inputFiltro" value="ID=N:${id}"/>
		<jsp:param name="filtroCampoEntita" value="IDMERIC=${id }"/>
	</jsp:include>
		
	<gene:redefineInsert name="schedaNuovo" />
	<gene:redefineInsert name="pulsanteNuovo"/>
	
	<gene:redefineInsert name="addToAzioni" >
		<c:if test='${modo eq "VISUALIZZA" && (datiRiga.GARECONT_STATO == 2 || datiRiga.GARECONT_STATO == 3)}'>
			<tr>
				<td class="vocemenulaterale">
					<a href="javascript:stampaOrdine();" title="Stampa ordine" tabindex="1502">
						Stampa ordine
					</a>
				</td>
			</tr>
		</c:if>
		
		<c:if test='${modo eq "VISUALIZZA" && datiRiga.GARECONT_STATO == 2 && autorizzatoModifiche ne "2" && (ruoloUtente eq "1" || ruoloUtente eq "3") && gene:checkProtFunz(pageContext,"ALT","ImpostaOrdineDef")}'>
			<tr>
				<td class="vocemenulaterale">
							<c:if test='${isNavigazioneDisattiva ne "1" }'>
								<a href="javascript:impostaOrdine();" title="Imposta ordine definito" tabindex="1503">
						</c:if>
							Imposta ordine definito
						<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>			  
				</td>
			</tr>
		</c:if>
		<c:if test='${modo eq "VISUALIZZA" && datiRiga.GARECONT_STATO == 3 && autorizzatoModifiche ne "2" && (ruoloUtente eq "1" || ruoloUtente eq "3") && gene:checkProtFunz(pageContext,"ALT","TrasmettiOrdine")}'>
			<tr>
				<td class="vocemenulaterale">
						<c:if test='${isNavigazioneDisattiva ne "1" }'>
								<a href="javascript:trasmettiOrdine();" title="Trasmetti ordine" tabindex="1504">
						</c:if>
							Trasmetti ordine
						<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>			  
				</td>
			</tr>
		</c:if>
		<c:if test='${numDocAttesaFirma > 0 and modo eq "VISUALIZZA"}'>
			<tr>
				<td class="vocemenulaterale" >
					<c:if test='${isNavigazioneDisattiva ne "1"}'>
						<a href="javascript:historyReload();" title='Rileggi dati' tabindex="1505">
					</c:if>
						Rileggi dati
					<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
				</td>
			</tr>
		</c:if>	
		<c:if test='${modo eq "VISUALIZZA" and ((fn:contains(listaOpzioniDisponibili, "OP114#") and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE.RiceviComunicazioni")) or (gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE.InviaComunicazioni")))}'>
			<tr>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td class="titolomenulaterale" title='${gene:resource("label.tags.template.documenti.sezioneComunicazioni")}'>
					${gene:resource("label.tags.template.documenti.sezioneComunicazioni")}</td>
			</tr>

			<c:if test='${fn:contains(listaOpzioniDisponibili, "OP114#") and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE.RiceviComunicazioni")}'>
				<tr>
					<td class="vocemenulaterale" >
						<c:if test='${isNavigazioneDisattiva ne "1"}'>
							<a href="javascript:leggiComunicazioni();" title="${gene:resource('label.tags.template.documenti.comunicazioniRicevute.tooltip')}" tabindex="1506">
						</c:if>
						${gene:resource('label.tags.template.documenti.comunicazioniRicevute')}
						<c:set var="numComunicazioniRicevuteNonLette" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetNumComunicazioniRicevuteDaLeggereFunction", pageContext, gene:getValCampo(key, "NGARA"))}' />
						<c:if test="${numComunicazioniRicevuteNonLette > 0}">(${numComunicazioniRicevuteNonLette} ${gene:resource('label.tags.template.documenti.comunicazioniRicevute.indicatore')})</c:if>
						<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
					</td>
				</tr>
			</c:if>
			<c:if test='${modo eq "VISUALIZZA" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE.InviaComunicazioni")}'>
				<tr>
					<td class="vocemenulaterale" >
						<c:if test='${isNavigazioneDisattiva ne "1"}'>
							<a href="javascript:inviaComunicazioni();" title="${gene:resource('label.tags.template.documenti.inviaComunicazioni.tooltip')}" tabindex="1507">
						</c:if>
						${gene:resource('label.tags.template.documenti.inviaComunicazioni')}
						<c:set var="numComunicazioniBozza" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetNumComunicazioniBozzaFunction", pageContext, "GARECONT", gene:getValCampo(key, "NGARA"))}' />
						<c:if test="${numComunicazioniBozza > 0}">(${numComunicazioniBozza} ${gene:resource('label.tags.template.documenti.inviaComunicazioni.indicatore')})</c:if>
						<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
					</td>
				</tr>
				<c:if test="${autorizzatoModifiche ne '2' }">
					<tr>
						<td class="vocemenulaterale" >
							<c:if test='${isNavigazioneDisattiva ne "1"}'>
								<a href="javascript:nuovaComunicazione();" title="Nuova comunicazione" tabindex="1508">
							</c:if>
							Nuova comunicazione
							<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
						</td>
					</tr>
				</c:if>
			</c:if>
		</c:if>
		
	</gene:redefineInsert>
	
	<gene:redefineInsert name="addToDocumenti" >
		<c:if test='${modo eq "VISUALIZZA" and isFascicoloDocumentaleAbilitato eq "1" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FascicoloDocumentale") }'>
			<tr>
				<c:choose>
					<c:when test='${isNavigazioneDisattiva ne "1"}'>
						<td class="vocemenulaterale">
							<a href="javascript:document.formwsdm.submit();" title="Fascicolo documentale" tabindex="1509">
								Fascicolo documentale
							</a>
						</td>
					</c:when>
					<c:otherwise>
						<td>
							Fascicolo documentale
						</td>
					</c:otherwise>
				</c:choose>
			</tr>
		</c:if>
	</gene:redefineInsert>
	
	<c:set var="msgChiaveErrore">	
		<fmt:message key="match.tags.template.dettaglio.campoChiave.messaggio">
			<fmt:param value="$"/>
		</fmt:message>
	</c:set>

	<c:set var="msgChiaveErrore" value="${fn:replace(msgChiaveErrore, '\\\\', '')}" />	


	<gene:gruppoCampi idProtezioni="GEN" >
		<gene:campoScheda>
			<td colspan="2"><b>Dati generali</b></td>
		</gene:campoScheda>

		<gene:campoScheda campo="NGARA" modificabile="false" title="Codice ordine di acquisto"/>
		<gene:campoScheda campo="CODGAR1" entita="GARE"  where="GARE.NGARA = GARECONT.NGARA and GARECONT.NCONT=1" visibile="false" />
		<gene:campoScheda campo="NCONT" visibile="false"/>
		<c:set var="link" value='javascript:archivioImpresa("${datiRiga.GARE_DITTA}");' />
		<gene:campoScheda campo="DITTA" entita="GARE" where="GARE.NGARA = GARECONT.NGARA and GARECONT.NCONT=1" modificabile="false" title="Codice operatore" href='${gene:if(gene:checkProt(pageContext, "MASC.VIS.GENE.ImprScheda"), link, "")}'/>
		<gene:campoScheda campo="NOMIMA" entita="GARE" where="GARE.NGARA = GARECONT.NGARA and GARECONT.NCONT=1" modificabile="false" href='${gene:if(gene:checkProt(pageContext, "MASC.VIS.GENE.ImprScheda"), link, "")}'/>
		<gene:campoScheda campo="NOT_GAR" entita="GARE" where="GARE.NGARA = GARECONT.NGARA and GARECONT.NCONT=1" modificabile="${datiRiga.GARECONT_STATO == 2 }"/>
		<gene:campoScheda campo="NUMAVCP" entita="TORN" from="GARE" where="TORN.CODGAR=GARE.CODGAR1 and GARE.NGARA = GARECONT.NGARA and GARECONT.NCONT=1" modificabile="${datiRiga.GARECONT_STATO == 2 }"/>
		<gene:campoScheda campo="CODCIG" entita="GARE" where="GARE.NGARA = GARECONT.NGARA and GARECONT.NCONT=1" />
		<c:choose>
			<c:when test='${fn:startsWith(datiRiga.GARE_CODCIG,"#") or fn:startsWith(datiRiga.GARE_CODCIG,"$") or fn:startsWith(datiRiga.GARE_CODCIG,"NOCIG")}'>
				<gene:campoScheda campo="CODCIG_FIT" title="Codice CIG fittizio" campoFittizio="true" value="${datiRiga.GARE_CODCIG}" definizione="T10;;;;G1CODCIG" modificabile="false" />
			</c:when>
			<c:otherwise>
				<gene:campoScheda campo="CODCIG_FIT" title="Codice CIG fittizio" campoFittizio="true" value="" definizione="T10;;;;G1CODCIG" modificabile="false" />
			</c:otherwise>
		</c:choose>
		<gene:campoScheda campo="ESENTE_CIG" campoFittizio="true" computed="true" title="Esente CIG?" definizione="T10;;;SN" defaultValue="2" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoSiNoSenzaNull" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.GARE.CODCIG") }'/>
		<gene:fnJavaScriptScheda funzione="gestioneEsenteCIG()" elencocampi="ESENTE_CIG" esegui="false" />
		<gene:campoScheda campo="DACQCIG" entita="GARE" where="GARE.NGARA = GARECONT.NGARA and GARECONT.NCONT=1" />
		<gene:campoScheda campo="CUPPRG" title="Codice CUP" entita="GARE" where="GARE.NGARA = GARECONT.NGARA and GARECONT.NCONT=1" modificabile="${datiRiga.GARECONT_STATO == 2 }"/>
		<gene:campoScheda campo="NREPAT" entita="GARE" where="GARE.NGARA = GARECONT.NGARA and GARECONT.NCONT=1" modificabile="${datiRiga.GARECONT_STATO == 2 }" title="Numero identificativo ordine"/>
		<gene:campoScheda campo="DAATTO" entita="GARE" where="GARE.NGARA = GARECONT.NGARA and GARECONT.NCONT=1" modificabile="${datiRiga.GARECONT_STATO == 2 }"/>
		<gene:campoScheda campo="NPROAT" modificabile="true"/>
		<gene:campoScheda campo="NOTE" title="Note all'ordine" modificabile="${datiRiga.GARECONT_STATO == 2 }"/>
		<gene:campoScheda campo="STATO" modificabile="false"/>
		<gene:campoScheda campo="NGARA" entita="GARE" where="GARE.NGARA = GARECONT.NGARA and GARECONT.NCONT=1" visibile="false"/>
		<gene:campoScheda campo="CENINT" entita="TORN" from="GARE" where="GARE.NGARA = GARECONT.NGARA and GARECONT.NCONT=1 and GARE.CODGAR1=TORN.CODGAR" visibile="false"/>
		<gene:campoScheda campo="CENINT" visibile="false"/>
		<gene:campoScheda campo="GENERE" entita="GARE" where="GARE.NGARA = GARECONT.NGARA and GARECONT.NCONT=1" visibile="false"/>
	</gene:gruppoCampi>
	
	<c:set var="result" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetValoriGAREIVAFunction", pageContext, ngara)}' />
	
	<gene:gruppoCampi idProtezioni="IMP" >
		<gene:campoScheda>
			<td colspan="2"><b>Importo</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="IAGGIU" title="Importo totale IVA esclusa" entita="GARE" where="GARE.NGARA = GARECONT.NGARA and GARECONT.NCONT=1" modificabile="false"/>
		<gene:campoScheda campoFittizio="true" nome="dettaglioIVA">
			<td class="etichetta-dato">Dettaglio IVA</td>
			<td>
				<table id="tabellaIVA" class="griglia" style="width: 99%; margin-left: 1%;">
				<tr>
					<td colspan="2" class="etichetta-dato" style="TEXT-ALIGN: center;BORDER-LEFT: #A0AABA 1px solid; width: 20%">Aliquota</td>
					<td colspan="2" class="etichetta-dato" style="TEXT-ALIGN: center;BORDER-LEFT: #A0AABA 1px solid; width: 40%">Imponibile</td>
					<td colspan="2" class="etichetta-dato" style="TEXT-ALIGN: center;BORDER-LEFT: #A0AABA 1px solid; width: 40%">Importo</td>
				</tr>
		</gene:campoScheda>
			<jsp:include page="/WEB-INF/pages/commons/interno-sezione-multipla-singola-riga.jsp" >
			<jsp:param name="entita" value='GAREIVA'/>
			<jsp:param name="chiave" value='${ngara}'/>
			<jsp:param name="nomeAttributoLista" value='datiGAREIVA' />
			<jsp:param name="idProtezioni" value="GAREIVA" />
			<jsp:param name="sezioneListaVuota" value="true" />
			<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/gareiva/gareiva-scheda.jsp"/>
			<jsp:param name="arrayCampi" value="'GAREIVA_ID_', 'GAREIVA_NGARA_', 'GAREIVA_NCONT_','GAREIVA_PERCIVA_', 'GAREIVA_IMPONIB_', 'GAREIVA_IMPIVA_'"/>
			<jsp:param name="titoloSezione" value="<br>Iva" />
			<jsp:param name="titoloNuovaSezione" value="<br>Nuova iva" />
			<jsp:param name="descEntitaVociLink" value="iva" />
			<jsp:param name="msgRaggiuntoMax" value="e iva"/>
			<jsp:param name="usaContatoreLista" value="true"/>
			<jsp:param name="numMaxDettagliInseribili" value="5"/>
			<jsp:param name="sezioneInseribile" value="true"/>
			<jsp:param name="sezioneEliminabile" value="true"/>
			<jsp:param name="funzEliminazione" value="delIva"/>
		</jsp:include>
		<gene:campoScheda addTr="false">
				</table>
			</td>
		</gene:campoScheda>
		
		<gene:campoScheda addTr="false">
			<span id="linkVisualizzaDettaglio" style="display: none; float: right;vertical-align: top;"><a id="aLinkVisualizzaDettaglio" href="javascript:showDettIVA();" title="Visualizza dettaglio IVA"><span id="testoLinkVisualizzaDettaglio">Visualizza dettaglio IVA</span>
				</a>
			</span>
		</gene:campoScheda>
		<gene:campoScheda campo="IMPIVA" />
		<gene:campoScheda campo="IMPTOT" />
	</gene:gruppoCampi>
	
	<gene:gruppoCampi idProtezioni="CONS" >
		<gene:campoScheda>
			<td colspan="2"><b>Consegna e fatturazione</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="PCOESE_FIT" campoFittizio="true" title="Consegna o esecuzione dell'ordine presso" definizione="T2;;A1098;;" 
			visibile='${(gene:checkProt(pageContext, "COLS.VIS.GARE.GARECONT.PCOESE") || gene:checkProt(pageContext, "COLS.VIS.GARE.GARECONT.LOCESE") )&& datiRiga.GARECONT_STATO == 2 }'
			gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoPresso" >
			<gene:checkCampoScheda funzione='checkPresenzaCenint("#PCOESE_FIT#")' obbligatorio="true" messaggio="Impossibile selezionare il valore. Non è presente la Stazione appaltante." onsubmit="false"/>
		</gene:campoScheda>
		<gene:campoScheda campo="PCOESE_FIT1" campoFittizio="true" title="Consegna o esecuzione dell'ordine presso" definizione="T2;;A1098;;" 
			visibile='${(gene:checkProt(pageContext, "COLS.VIS.GARE.GARECONT.PCOESE") || gene:checkProt(pageContext, "COLS.VIS.GARE.GARECONT.LOCESE") ) && datiRiga.GARECONT_STATO != 2}'
			gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoPresso" modificabile="false"/>
		<gene:archivio titolo="Punti di contatto" 
			lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.GARECONT.PCOESE"),"gene/punticon/punticon-lista-popup.jsp","")}' 
			scheda=''
			schedaPopUp="gene/punticon/punticon-scheda-popup.jsp"
			campi="PUNTICON.CODEIN;PUNTICON.NUMPUN;PUNTICON.NOMPUN" 
			functionId="default"
			parametriWhere="T:${initCENINT}"
			chiave="GARECONT_PCOESE;CENINT_PCOESE"
			formName="formPuntiConsegnaOrdine"
			inseribile="false">
				<gene:campoScheda campo="CENINT_PCOESE" campoFittizio="true" definizione="T16" visibile="false" value="${initCENINT}"/>
				<gene:campoScheda campo="PCOESE" visibile="false"/>
				<c:set var="linkPCOESE" value='javascript:archivioPunticon("${initCENINT}","${datiRiga.GARECONT_PCOESE}");' />
				<gene:campoScheda campo="NOMPUN_PCOESE" campoFittizio="true" definizione="T245;;;NOTE;G_NOMPUN" value="${initNOMPUN_PCOESE }" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.GARECONT.PCOESE")}' modificabile="${datiRiga.GARECONT_STATO == 2 }" href='${gene:if(modo eq "VISUALIZZA",linkPCOESE,"")}'/>
		</gene:archivio>
		<gene:campoScheda campo="LOCESE" modificabile="${datiRiga.GARECONT_STATO == 2 }"/>

		<gene:campoScheda campo="MODPAG" modificabile="${datiRiga.GARECONT_STATO == 2 }"/>
		
		<gene:campoScheda campo="PCOFAT_FIT" campoFittizio="true" title="Fatturazione presso" definizione="T2;;A1098;;" 
			visibile='${(gene:checkProt(pageContext, "COLS.VIS.GARE.GARECONT.PCOESE") || gene:checkProt(pageContext, "COLS.VIS.GARE.GARECONT.LOCFAT") ) && datiRiga.GARECONT_STATO == 2}'
			gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoPresso">
			<gene:checkCampoScheda funzione='checkPresenzaCenint("#PCOFAT_FIT#")' obbligatorio="true" messaggio="Impossibile selezionare il valore. Non è presente la Stazione appaltante." onsubmit="false"/>
		</gene:campoScheda>
		<gene:campoScheda campo="PCOFAT_FIT1" campoFittizio="true" title="Fatturazione presso" definizione="T2;;A1098;;" 
			visibile='${(gene:checkProt(pageContext, "COLS.VIS.GARE.GARECONT.PCOESE") || gene:checkProt(pageContext, "COLS.VIS.GARE.GARECONT.LOCFAT") ) && datiRiga.GARECONT_STATO != 2}'
			gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoPresso" modificabile="false"/>
		<gene:archivio titolo="Punti di contatto" 
			lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.GARECONT.PCOFAT"),"gene/punticon/punticon-lista-popup.jsp","")}' 
			scheda=''
			schedaPopUp="gene/punticon/punticon-scheda-popup.jsp"
			campi="PUNTICON.CODEIN;PUNTICON.NUMPUN;PUNTICON.NOMPUN" 
			functionId="default"
			parametriWhere="T:${initCENINT}"
			chiave="GARECONT_PCOFAT;CENINT_PCOFAT"
			formName="formPuntiFatturazione"
			inseribile="false">
				<gene:campoScheda campo="CENINT_PCOFAT" campoFittizio="true" definizione="T16" visibile="false" value="${initCENINT}"/>
				<gene:campoScheda campo="PCOFAT" visibile="false"/>
				<c:set var="linkPCOFAT" value='javascript:archivioPunticon("${initCENINT}","${datiRiga.GARECONT_PCOFAT}");' />
				<gene:campoScheda campo="NOMPUN_PCOFAT" campoFittizio="true" definizione="T245;;;NOTE;G_NOMPUN" value="${initNOMPUN_PCOFAT }" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.GARECONT.PCOFAT")}' modificabile="${datiRiga.GARECONT_STATO == 2 }" href='${gene:if(modo eq "VISUALIZZA",linkPCOFAT,"")}'/>
		</gene:archivio>
		<gene:campoScheda campo="LOCFAT" modificabile="${datiRiga.GARECONT_STATO == 2 }"/>
	</gene:gruppoCampi>
	
	<fmt:parseNumber var="tipoDoc" type="number" value="11"/> 
	<c:set var="tmp" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.GestioneDocumentazioneFunction", pageContext,codiceGara,ngara,tipoDoc,"")}' />
	
	<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" > 
		<jsp:param name="entita" value='DOCUMGARA'/>
		<jsp:param name="chiave" value='${codiceGara};${ngara}'/>
		<jsp:param name="nomeAttributoLista" value='allegatiOrdine' />
		<jsp:param name="idProtezioni" value="ALLEGATI" />
		<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/documgara/allegati-ordine.jsp"/>
		<jsp:param name="arrayCampi" value="'DOCUMGARA_CODGAR_', 'DOCUMGARA_NGARA_', 'DOCUMGARA_NORDDOCG_','DOCUMGARA_GRUPPO_', 'DOCUMGARA_FASGAR_', 'DOCUMGARA_IDPRG_', 'DOCUMGARA_IDDOCDG_','DOCUMGARA_DESCRIZIONE_','DOCUMGARA_STATODOC_','DOCUMGARA_VALENZA_','W_DOCDIG_IDPRG_','W_DOCDIG_IDDOCDIG_','W_DOCDIG_DIGDESDOC_','W_DOCDIG_DIGNOMDOC_','selezioneFile_','DOCUMGARA_IDSTAMPA','NOMEDOCGEN_','DOCUMGARA_ALLMAIL_','W_DOCDIG_DIGFIRMA_'"/>		
		<jsp:param name="titoloSezione" value="Allegato all'ordine" />
		<jsp:param name="titoloNuovaSezione" value="Nuovo allegato all'ordine" />
		<jsp:param name="descEntitaVociLink" value="allegato all'ordine" />
		<jsp:param name="msgRaggiuntoMax" value="i allegati all'ordine"/>
		<jsp:param name="usaContatoreLista" value="true"/>
		<jsp:param name="sezioneListaVuota" value="false"/>
		<jsp:param name="sezioneEliminabile" value="${stato == '2' }"/>
		<jsp:param name="sezioneInseribile" value="${stato == '2' }"/>
		<jsp:param name="valoreStato" value="${stato}"/>
		<jsp:param name="richiestaFirma" value="${richiestaFirma}"/>
		<jsp:param name="autorizzatoModifiche" value="${autorizzatoModifiche}"/>
		<jsp:param name="ordineNonDefinito" value="${datiRiga.GARECONT_STATO == 2 }"/>
		<jsp:param name="firmaRemota" value="${firmaRemota}"/>
	</jsp:include>
	
	<gene:gruppoCampi idProtezioni="DEF" >
		<gene:campoScheda>
			<td colspan="2"><b>Definizione ordine</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="DATDEF" visibile="false"/>
		<gene:campoScheda computed="true" nome="DATDEF_CAL" campo="${gene:getDBFunction(pageContext,'DATETIMETOSTRING','GARECONT.DATDEF')}" modificabile="false" definizione="T20;0;;;G1DATDEF" />
	</gene:gruppoCampi>
	
	<gene:gruppoCampi idProtezioni="TRASM" >
		<gene:campoScheda>
			<td colspan="2"><b>Trasmissione ordine</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="IDPRG"  visibile="false"/>
		<gene:campoScheda campo="IDDOCDG" visibile="false"/>
		<c:choose>
			<c:when test="${modo eq 'VISUALIZZA'}">
				<gene:campoScheda title="Documento d'ordine" campo="DIGNOMDOC" entita="W_DOCDIG" where="W_DOCDIG.IDPRG=GARECONT.IDPRG AND W_DOCDIG.IDDOCDIG=GARECONT.IDDOCDG" modificabile="false" href="javascript:visualizzaFileAllegato('${datiRiga.GARECONT_IDPRG}','${datiRiga.GARECONT_IDDOCDG}','${datiRiga.W_DOCDIG_DIGNOMDOC}');"/>
			</c:when>
			<c:otherwise>
				<gene:campoScheda title="Documento d'ordine" campo="DIGNOMDOC" entita="W_DOCDIG" where="W_DOCDIG.IDPRG=GARECONT.IDPRG AND W_DOCDIG.IDDOCDIG=GARECONT.IDDOCDG" modificabile="false" />
			</c:otherwise>
		</c:choose>
		<gene:campoScheda campo="DATTRA" visibile="false"/>
		<gene:campoScheda computed="true" nome="DATTRA_CAL" campo="${gene:getDBFunction(pageContext,'DATETIMETOSTRING','GARECONT.DATTRA')}" modificabile="false" definizione="T20;0;;;G1DATTRA" />
		<gene:campoScheda campo="DATLET" visibile="false"/>
		<gene:campoScheda computed="true" nome="DATLET_CAL" campo="${gene:getDBFunction(pageContext,'DATETIMETOSTRING','GARECONT.DATLET')}" modificabile="false" definizione="T20;0;;;G1DATLET" visibile="false"/>
	</gene:gruppoCampi>
	
	<gene:gruppoCampi idProtezioni="REVOCA" >
		<gene:campoScheda>
			<td colspan="2"><b>Revoca ordine</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="DATREV" visibile="false"/>
		<gene:campoScheda computed="true" nome="DATREV_CAL" campo="${gene:getDBFunction(pageContext,'DATETIMETOSTRING','GARECONT.DATREV')}" modificabile="false" definizione="T20;0;;;G1DATREV" />
		<gene:campoScheda campo="MOTREV" modificabile="false"/>
	</gene:gruppoCampi>
	
	<gene:gruppoCampi idProtezioni="ALTRI" >
		<gene:campoScheda>
			<td colspan="2"><b>Altri dati</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="DATRIF" />
		<gene:campoScheda campo="NOTEGA" entita="GARE" where="GARE.NGARA = GARECONT.NGARA and GARECONT.NCONT=1"/>
	</gene:gruppoCampi>
	
	
	<gene:campoScheda campo="CODRUP" entita="MERIC" where="MERIC.ID=${id }" visibile="false"/>
	
	<input type="hidden" name="id" id="id" value="${id}">
	<input type="hidden" name="filtroDocumentazione" id="filtroDocumentazione" value="11">
	
	<c:if test="${modo ne 'VISUALIZZA' }">
		<gene:fnJavaScriptScheda funzione="showCampiContatto('#PCOESE_FIT#','LOCESE','PCOESE')" elencocampi="PCOESE_FIT" esegui="false" />
		<gene:fnJavaScriptScheda funzione="showCampiContatto('#PCOFAT_FIT#','LOCFAT','PCOFAT')" elencocampi="PCOFAT_FIT" esegui="false" />
	</c:if>
	
	<gene:campoScheda>	
		
		<td class="comandi-dettaglio" colSpan="2">
			<gene:insert name="addPulsanti"/>
			<c:choose>
			<c:when test='${modo eq "MODIFICA"}'>
				<gene:insert name="pulsanteSalva">
					<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:schedaConferma()">
				</gene:insert>
				<gene:insert name="pulsanteAnnulla">
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:schedaAnnulla()">
				</gene:insert>
			</c:when>
			<c:otherwise>
				<c:if test='${numDocAttesaFirma > 0}'>
					<INPUT type="button"  class="bottone-azione" value='Rileggi dati' title='Rileggi dati' onclick="javascript:historyReload();">
				</c:if>
				<gene:insert name="pulsanteModifica">
					<c:if test='${gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD")}'>
						<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:schedaModifica()">
					</c:if>
				</gene:insert>
				<c:if test='${modo eq "VISUALIZZA" && datiRiga.GARECONT_STATO eq "2" && autorizzatoModifiche ne "2" && (ruoloUtente eq "1" || ruoloUtente eq "3") && gene:checkProtFunz(pageContext,"ALT","ImpostaOrdineDef")}'>
					<INPUT type="button" class="bottone-azione" value="Imposta ordine definito" title="Imposta ordine definito" onclick="javascript:impostaOrdine()">
				</c:if>
				<c:if test='${modo eq "VISUALIZZA" && datiRiga.GARECONT_STATO == 3 && autorizzatoModifiche ne "2" && (ruoloUtente eq "1" || ruoloUtente eq "3") && gene:checkProtFunz(pageContext,"ALT","TrasmettiOrdine")}'>
					<INPUT type="button" class="bottone-azione" value="Trasmetti ordine" title="Trasmetti ordine" onclick="javascript:trasmettiOrdine()">
				</c:if>
			</c:otherwise>
			</c:choose>
			&nbsp;
		</td>
	</gene:campoScheda>
	
</gene:formScheda>

<jsp:include page="/WEB-INF/pages/gene/system/firmadigitale/modalPopupFirmaDigitaleRemota.jsp" />	

<form name="formVisFirmaDigitale" id="formVisFirmaDigitale" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
	<input type="hidden" name="href" value="gene/system/firmadigitale/verifica-firmadigitale.jsp" />
	<input type="hidden" name="idprg" id="idprg" value="" />
	<input type="hidden" name="iddocdig" id="iddocdig" value="" />
</form>

<gene:javaScript>
	
	var idconfi = "${idconfi}";
	
	document.forms[0].encoding="multipart/form-data";	
	//Inserimento link sul campo GARECONT.IMPIVA
	<c:choose>
		<c:when test='${(modo eq "VISUALIZZA")}'>
			$("#linkVisualizzaDettaglio").appendTo($("#GARECONT_IMPIVAview"));
		</c:when>
		<c:otherwise>
			$("#linkVisualizzaDettaglio").appendTo($("#GARECONT_IMPIVA").parent());
		</c:otherwise>
	</c:choose>
	$("#linkVisualizzaDettaglio").show();
	
	//All'apertura della pagina si deve nascondere il dettaglio dell'iva
	$('#rowdettaglioIVA').hide();
	
	//Se stato diverso da 2 si blocca il dettaglio dell'IVA
	<c:if test="${datiRiga.GARECONT_STATO != 2 && modo ne 'VISUALIZZA'}">
		$('[id^="GAREIVA_PERCIVA_"]').attr('disabled',true);
		$('[id^="GAREIVA_PERCIVA_"]').css('background-color','#ECECEC');
		$('[id^="GAREIVA_IMPONIB_"]').attr('disabled',true);
		$('[id^="GAREIVA_IMPONIB_"]').css('background-color','#ECECEC');
				
		$('[id^="rowtitoloGAREIVA_"]').hide();
		$('[id^="rowLinkAddGAREIVA"]').hide();
	</c:if>

		$("#GARE_CODCIG").css({'text-transform': 'uppercase' });
	
	<c:if test="${modoAperturaScheda eq 'MODIFICA'}">
		$(function() {
	    $('#GARE_CODCIG').change(function() {
				if (!controllaCIG("GARE_CODCIG")) {
					alert("Codice CIG non valido")
					this.focus();
				}
	    });
		});
	</c:if>
	
	<c:if test='${!(modo eq "VISUALIZZA")}'>
								
		$('#GARECONT_IMPIVA').attr('disabled',true);
		$('#GARECONT_IMPIVA').css('background-color','#ECECEC');
		$('#GARECONT_IMPTOT').attr('disabled',true);
		$('#GARECONT_IMPTOT').css('background-color','#ECECEC');
		
		$("#GARECONT_CODCIG").css({'text-transform': 'uppercase' });
		
		var schedaConferma_Default = schedaConferma;
		function schedaConferma_Custom() {
			setValue("GARE_CODCIG", getValue("GARE_CODCIG").toUpperCase(), false);

			clearMsg();
			var esenteCig = getValue("ESENTE_CIG");
			if (esenteCig == "2" && getValue("GARE_CODCIG") != "") {
				if (!controllaCIG("GARE_CODCIG")) {
					outMsg("Codice CIG non valido", "ERR");
					onOffMsg();
					return;
				}
			}else {
				setValue("GARE_CODCIG", getValue("CODCIG_FIT").toUpperCase());
			}
			//Si deve controllare che PERCIVA e IMPONIB siano valorizzati
			var controlloIvaOk = true;
			for(var i=1; i < maxIdGAREIVAVisualizzabile ; i++){
				if(isObjShow("rowtitoloGAREIVA_" + i)){
					var iva = getValue("GAREIVA_PERCIVA_" + i);
					var imponibile = getValue("GAREIVA_IMPONIB_" + i);
					if(iva==null || iva =="" || imponibile ==null || imponibile == ""){
						controlloIvaOk = false;
					}
				}
			}
			//Si deve controllare che tutti gli allegati all'ordine abbiano specificato un file
			var controlloAllegatiOk = true;
			for(var i=1; i < maxIdALLEGATIVisualizzabile ; i++){
				if(isObjShow("rowtitoloALLEGATI_" + i)){
					var allegato = getValue("W_DOCDIG_DIGNOMDOC_" + i);
					if(allegato==null || allegato ==""){
						controlloAllegatiOk = false;
						break;
					}
				}
			}
			if(!controlloIvaOk){
 	 			outMsg("Nel dettaglio dell'IVA vi sono delle righe in cui o l'Aliquota o l'Imponibile non risultano valorizzati", "ERR");
				onOffMsg();
 	 		}else if(!controlloAllegatiOk){
 	 			outMsg("Per ogni allegato all'ordine deve essere specificato il documento", "ERR");
				onOffMsg();
 	 		}else{
				//Si riattivano i campi così da poterne salvare i valori
				$('[id^="GAREIVA_IMPIVA_"]').attr('disabled',false);
				$('#GARECONT_IMPIVA').attr('disabled',false);
				$('#GARECONT_IMPTOT').attr('disabled',false);
				document.forms[0].action = "${pageContext.request.contextPath}/pg/Scheda.do?"+csrfToken;
				schedaConferma_Default();
			}
	 	}
 	
 		schedaConferma =   schedaConferma_Custom;
 		
 		var schedaAnnulla_Default = schedaAnnulla;
		function schedaAnnulla_Custom(){
			document.forms[0].action = "${pageContext.request.contextPath}/pg/Scheda.do?"+csrfToken;
			schedaAnnulla_Default();
	 	}
 		
 		schedaAnnulla =   schedaAnnulla_Custom;
	</c:if>
	
	
	//Impostazioni per migliorare il layout nel caso di pagina aperta in visualizzazione
	<c:if test="${modo eq 'VISUALIZZA'}">
		/*
		for(var i=1; i < maxIdGAREIVAVisualizzabile ; i++){
			$('#GAREIVA_PERCIVA_' + i + 'view').css('margin-right','30px');
			$('#GAREIVA_IMPONIB_' + i + 'view').find( "span" ).css('text-align','left');
	        $('#GAREIVA_IMPIVA_' + i +'view').find( "span" ).css('text-align','left');
        }
        */
	</c:if>
	
	$('#rowLinkAddGAREIVA td').attr("colspan","8");
	$('#rowMsgLastGAREIVA td').attr("colspan","8");
	
	//Funzione attivata dal link Visualizza dettaglio IVA per nascondere e visualizzare il dettaglio	
	function showDettIVA(){
		if($('#rowdettaglioIVA').is(':hidden')){
			$('#rowdettaglioIVA').show();
			$("#testoLinkVisualizzaDettaglio").text('Nascondi dettaglio IVA');
			$('#aLinkVisualizzaDettaglio').attr('title', 'Nascondi dettaglio IVA');
		}else{
			$('#rowdettaglioIVA').hide();
			$("#testoLinkVisualizzaDettaglio").text('Visualizza dettaglio IVA');
			$('#aLinkVisualizzaDettaglio').attr('title', 'Visualizza dettaglio IVA');
		}
	}
	
	function checkPresenzaCenint(valore){
		if(valore==2){
			var cenint = "${initCENINT }";
			if(cenint=="" || cenint == null)
				return false;
		}
		return true;
	}
	
	function showCampiContatto(valore,nomeCampo,nomeCampoFit){
		if(valore == 2) {
			showObj("rowGARECONT_" + nomeCampo, false);
			showObj("rowNOMPUN_" + nomeCampoFit, true);
			setValue("GARECONT_" + nomeCampo, "");
		}else if(valore == 3){
			showObj("rowGARECONT_" + nomeCampo, true);
			showObj("rowNOMPUN_" + nomeCampoFit, false);
			setValue("GARECONT_" + nomeCampoFit, "");
			setValue("NOMPUN_" + nomeCampoFit, "");
		}else{
			showObj("rowGARECONT_" + nomeCampo, false);
			showObj("rowNOMPUN_" + nomeCampoFit, false);
			setValue("GARECONT_" + nomeCampo, "");
			setValue("GARECONT_" + nomeCampoFit, "");
		}
	}
	
	function valorizzaCampiPresso(punto,luogo,campoPresso){
		var valore="";
		<c:choose>
			<c:when test="${modo eq 'VISUALIZZA'}">
				if(punto!="")
					valore = "Punto di contatto";
				else if(luogo!="")
					valore = "Altro (specificare)";
				else
					valore = "Stazione appaltante";	
			</c:when>
			<c:otherwise>
				if(punto!="")
					valore = 2;
				else if(luogo!="")
					valore =3;
				else
					valore = 1;
			</c:otherwise>
		</c:choose>
		setValue(campoPresso,valore);
	}
	
	function valorizzaCampiPressoBloccati(punto,luogo,campoPresso,stato){
		if(stato!=1){
			if(punto!="")
				valore = "Punto di contatto";
			else if(luogo!="")
				valore = "Altro (specificare)";
			else
				valore = "Stazione appaltante";	
			setValue(campoPresso,valore);
		}	
	}
	
	var stato = getValue("GARECONT_STATO");
	var punto = getValue("GARECONT_PCOESE");
	var luogo = getValue("GARECONT_LOCESE");
	valorizzaCampiPresso(punto,luogo,"PCOESE_FIT");
	valorizzaCampiPressoBloccati(punto,luogo,"PCOESE_FIT1",stato);
	
	
	showObj("rowPCOESE_FIT", true);
	//var punti = getValue("GARECONT_PCOESE");
	if(punto != ""){
		showObj("rowNOMPUN_PCOESE", true);
		showObj("rowGARECONT_LOCESE", false);
		setValue("GARECONT_LOCESE", "");
	}else if(luogo!=""){
		showObj("rowGARECONT_LOCESE", true);
		showObj("rowNOMPUN_PCOESE", false);
		setValue("GARECONT_PCOESE", "");
		setValue("NOMPUN_PCOESE", "");
	}else{
		showObj("rowGARECONT_LOCESE", false);
		showObj("rowNOMPUN_PCOESE", false);
		setValue("GARECONT_PCOESE", "");
		setValue("GARECONT_LOCESE", "");
		setValue("NOMPUN_PCOESE", "");
	}
	
	var punto = getValue("GARECONT_PCOFAT");
	var luogo = getValue("GARECONT_LOCFAT");
	valorizzaCampiPresso(punto,luogo,"PCOFAT_FIT");
	valorizzaCampiPressoBloccati(punto,luogo,"PCOFAT_FIT1",stato);
	
	showObj("rowPCOFAT_FIT", true);
	if(punto != ""){
		showObj("rowNOMPUN_PCOFAT", true);
		showObj("rowGARECONT_LOCFAT", false);
		setValue("GARECONT_LOCFAT", "");
	}else if(luogo!=""){
		showObj("rowGARECONT_LOCFAT", true);
		showObj("rowNOMPUN_PCOFAT", false);
		setValue("GARECONT_PCOFAT", "");
		setValue("NOMPUN_PCOFAT", "");
	}else{
		showObj("rowGARECONT_LOCFAT", false);
		showObj("rowNOMPUN_PCOFAT", false);
		setValue("GARECONT_PCOFAT", "");
		setValue("GARECONT_LOCFAT", "");
		setValue("NOMPUN_PCOFAT", "");
	}
	
	var selezionaPaginaDefault = selezionaPagina;
	var selezionaPagina = selezionaPaginaCustom;
	function selezionaPaginaCustom(pageNumber){
		var id="${id }"
		document.pagineForm.action += "&id=" + id;
		selezionaPaginaDefault(pageNumber);
	}
	
	function impostaOrdine(){
		var ngara = getValue("GARECONT_NGARA");
		var ncont = getValue("GARECONT_NCONT");
		var href = "href=gare/garecont/garecont-popup-impostaOrdineDefinito.jsp";
		href += "&ngara="+ngara + "&ncont=" + ncont;
		openPopUpCustom(href, "impostaOrdineDefinito", 650, 450, "yes", "yes");
		
	}
	
	function stampaOrdine(){
		var ngara = getValue("GARECONT_NGARA");
		var codimp = getValue("GARE_DITTA");
		var href = "${pageContext.request.contextPath}/pg/StampaOrdineAcquisto.do";
		document.location.href=href+"?"+csrfToken+"&ngara=" + ngara + "&codimp=" + codimp;
	}
	
	function archivioImpresa(codiceImpresa){
<c:choose>
	<c:when test='${modo=="MODIFICA"}' >
		var href = ("href=gene/impr/impr-scheda-popup.jsp&key=IMPR.CODIMP=T:" + codiceImpresa);
		openPopUp(href, "schedaImpresa");
	</c:when>
	<c:otherwise>
		var href = ("href=gene/impr/impr-scheda.jsp&key=IMPR.CODIMP=T:" + codiceImpresa);
		document.location.href = contextPath + "/ApriPagina.do?"+csrfToken+"&" + href + "&key=IMPR.CODIMP=T:" + codiceImpresa;
	</c:otherwise>
</c:choose>
	}
	
	function archivioPunticon(codice,num){
		var href = ("href=gene/punticon/punticon-scheda-popup.jsp&key=PUNTICON.CODEIN=T:" + codice + ";PUNTICON.NUMPUN=N:" + num);
		openPopUp(href, "schedaPunticon");
	}
	
	function trasmettiOrdine(){
		href = "href=gare/garecont/garecont-popup-trasmettiOrdine.jsp";
		var ngara = getValue("GARECONT_NGARA");
		var ncont = getValue("GARECONT_NCONT");
		var nprot = getValue("GARECONT_NPROAT");
		var ditta = getValue("GARE_DITTA");
		var cig = getValue("GARE_CODCIG");
		if(cig!=null && cig.substr(0,1)=="#")
			cig=encodeURIComponent(cig);
		var codgar = getValue("GARE_CODGAR1");
		href += "&ngara=" + ngara + "&ncont=" + ncont + "&nprot=" + nprot + "&ditta=" + ditta + "&modo=MODIFICA" + "&nomeEntita=GARECONT&cig=" + cig+ "&isODA=true&step=1&codgar=" + codgar;
		var idconfi = "${idconfi}";
		if(idconfi){
			href += "&idconfi=" + idconfi
		}
		var codrup = getValue("MERIC_CODRUP");
		href += "&codrup=" + codrup
		openPopUpCustom(href, "trasmettiOrdine", 900, 650, "yes", "yes");
	}
	
	function leggiComunicazioni() {
		var entitaWSDM="GARE";
		var chiaveWSDM=getValue("GARE_NGARA");
		var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=geneweb/w_invcom/w_invcom-in-lista.jsp&genere=${datiRiga.GARE_GENERE}&chiave=" + document.forms[0].key.value;
		href+="&entitaWSDM=" + entitaWSDM + "&chiaveWSDM=" + chiaveWSDM;
		if(idconfi){
			href = href + "&idconfi="+idconfi;
		}
		
		document.location.href = href;
	}

	function inviaComunicazioni() {
		var entitaWSDM="GARE";
		var chiaveWSDM=getValue("GARE_NGARA");
		var href = contextPath + "/ApriPagina.do?href=geneweb/w_invcom/w_invcom-lista.jsp&genere=${datiRiga.GARE_GENERE}&entita=" + document.forms[0].entita.value + "&chiave=" + document.forms[0].key.value;
		href+="&entitaWSDM=" + entitaWSDM + "&chiaveWSDM=" + chiaveWSDM;
		var idMeric ="${id}";
		href+="&idMeric="+idMeric;
		if(idconfi){
			href += "&idconfi=" + idconfi
		}
		document.location.href = href;
	}
	
	<c:if test='${!(modo eq "VISUALIZZA")}'>
        //Diminuisco la larghezza della prima classe="valore-dato" (ossia Aliquota)
		/*
		for(var i=1; i < maxIdGAREIVAVisualizzabile ; i++){
                $('#rowGAREIVA_ID_' + i).find( "td.valore-dato" ).first().css('width', '100');
        }
        */
           
    </c:if>
    
    function nuovaComunicazione(){
		var IsW_CONFCOMPopolata = "${IsW_CONFCOMPopolata}";
		var tipo = "7";
		var numeroGara = getValue("GARE_NGARA");
		var keyAdd = "W_INVCOM.COMKEY1=T:" + numeroGara + ";W_INVCOM.COMKEY2=T:1";
		var keyParent = "GARECONT.NGARA=T:" + numeroGara +";GARECONT.NCONT=N:1";
		var entitaWSDM="GARE";
		var chiaveWSDM=getValue("GARE_NGARA");
		var href = "";
		if(IsW_CONFCOMPopolata == "true"){
			href = contextPath + "/pg/InitNuovaComunicazione.do?genere=" + tipo + "&keyAdd=" + keyAdd+"&keyParent=" + keyParent + "&chiaveWSDM=" + chiaveWSDM + "&entitaWSDM=" + entitaWSDM;
		}else{
			href = contextPath + "/Lista.do?numModello=0&keyAdd=" + keyAdd ;
			href += "&keyParent=" + keyParent + "&metodo=nuovo&entita=W_INVCOM&jspPathTo=/WEB-INF/geneweb/w_invcom/w_invcom-scheda.jsp&chiaveWSDM=" + chiaveWSDM + "&entitaWSDM=" + entitaWSDM;
		}
		if(idconfi){
			href = href + "&idconfi="+idconfi;
		}
		document.location.href = href + "&" + csrfToken;
	}
	
	function scegliFile(indice) {
		var selezioneFile = document.getElementById("selFile[" + indice + "]").value;
		var lunghezza_stringa=selezioneFile.length;
		var posizione_barra=selezioneFile.lastIndexOf("\\");
		var nome=selezioneFile.substring(posizione_barra+1,lunghezza_stringa).toUpperCase();
		if(nome.length>100){
			alert("Il nome del file non può superare i 100 caratteri!");
			document.getElementById("selFile[" + indice + "]").value="";
			setValue("W_DOCDIG_DIGNOMDOC_" + indice,"");
		}else{
			setValue("W_DOCDIG_DIGNOMDOC_" + indice,nome);
			$("#spanRichiestaFirma_" + indice).show();
		}
		
	}
	
	 function visualizzaFileAllegato(idprg,iddocdig,dignomdoc) {
		var vet = dignomdoc.split(".");
		var ext = vet[vet.length-1];
		ext = ext.toUpperCase();
		<c:choose>
			<c:when test="${digitalSignatureWsCheck eq 0}">
				if(ext=='P7M' || ext=='TSD'){
					document.formVisFirmaDigitale.idprg.value = idprg;
					document.formVisFirmaDigitale.iddocdig.value = iddocdig;
					document.formVisFirmaDigitale.submit();
				}else{
					var href = "${pageContext.request.contextPath}/pg/VisualizzaFileAllegato.do";
					document.location.href=href+"?"+csrfToken+"&idprg=" + idprg + "&iddocdig=" + iddocdig + "&dignomdoc=" + encodeURIComponent(dignomdoc);
				}
			</c:when>
			<c:otherwise>
				if(ext=='P7M' || ext=='TSD' || ext=='XML' || ext=='PDF'){
					document.formVisFirmaDigitale.idprg.value = idprg;
					document.formVisFirmaDigitale.iddocdig.value = iddocdig;
					document.formVisFirmaDigitale.submit();
				}else{
					var href = "${pageContext.request.contextPath}/pg/VisualizzaFileAllegato.do";
					document.location.href=href+"?"+csrfToken+"&idprg=" + idprg + "&iddocdig=" + iddocdig + "&dignomdoc=" + encodeURIComponent(dignomdoc);
				}
			</c:otherwise>
		</c:choose>
	}
	
	function gestioneEsenteCIG() {
	<c:if test='${modo ne "VISUALIZZA"}'>
		var esenteCig = getValue("ESENTE_CIG");
		var codcig = getValue("GARE_CODCIG");
		if ("1" == esenteCig) {
			showObj("rowGARE_CODCIG", false);
			//setValue("GARE_CODCIG", "", false);
			if (getOriginalValue("CODCIG_FIT") == getValue("CODCIG_FIT")) {
				setValue("CODCIG_FIT", "", false);
			} else {
				setValue("CODCIG_FIT", getOriginalValue("CODCIG_FIT"), false);
			}
			<c:if test='${gene:checkProt(pageContext, "COLS.MAN.GARE.GARE.CODCIG")}'>
			if(getValue("CODCIG_FIT")==null || getValue("CODCIG_FIT")=="" )
				setValue("CODCIG_FIT", " ", false);
			</c:if>
			showObj("rowCODCIG_FIT", true);
		} else {
			showObj("rowGARE_CODCIG", true);
			showObj("rowCODCIG_FIT", false);
			setValue("CODCIG_FIT", "", false);
		}
	</c:if>
	}
	
	function initEsenteCIG_CODCIG() {
		var esenteCig = getValue("ESENTE_CIG");
		var codcig = getValue("GARE_CODCIG");
		//alert("esente CIG = " + esenteCig);
		//alert("Codice CIG = " + codcig);
<c:choose>
	<c:when test='${modo ne "VISUALIZZA"}'>
		if ("" != codcig) {
			if (codcig.indexOf("#") == 0 || codcig.indexOf("$") == 0 || codcig.indexOf("NOCIG") == 0) {
				setValue("ESENTE_CIG", "1", false);
				setOriginalValue("ESENTE_CIG", "1", false);
				showObj("rowCODCIG_FIT", true);
				showObj("rowGARE_CODCIG", false);
				setValue("GARE_CODCIG", "", false);
				//setOriginalValue("GARE_CODCIG", "", false);
			} else {
				setValue("ESENTE_CIG", "2", false);
				setOriginalValue("ESENTE_CIG", "2", false);
				showObj("rowCODCIG_FIT", false);
				showObj("rowGARE_CODCIG", true);
			}
		} else {
			setValue("ESENTE_CIG", "2", false);
			setOriginalValue("ESENTE_CIG", "2", false);
			showObj("rowCODCIG_FIT", false);
			showObj("rowGARE_CODCIG", true);
		}
	</c:when>
	<c:otherwise>
		if ("" != codcig) {
			if (codcig.indexOf("#") == 0 || codcig.indexOf("$") == 0 || codcig.indexOf("NOCIG") == 0) {
				setValue("ESENTE_CIG", "Si", false);
				showObj("rowCODCIG_FIT", true);
				showObj("rowGARE_CODCIG", false);
			} else {
				setValue("ESENTE_CIG", "No", false);
				showObj("rowCODCIG_FIT", false);
				showObj("rowGARE_CODCIG", true);
			}
		} else {
			setValue("ESENTE_CIG", "No", false);
			showObj("rowCODCIG_FIT", false);
			showObj("rowGARE_CODCIG", true);
		}
	</c:otherwise>
</c:choose>
	}
	
	initEsenteCIG_CODCIG();
</gene:javaScript>

<form name="formwsdm" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
	<input type="hidden" name="href" value="gare/wsdm/wsdm-scheda.jsp" /> 
	<input type="hidden" name="entita" value="GARE" />
	<input type="hidden" name="key1" value="${datiRiga.GARECONT_NGARA}" />
	<input type="hidden" name="key2" value="" /> 
	<input type="hidden" name="key3" value="" />
	<input type="hidden" name="key4" value="" />
	<input type="hidden" name="metodo" value="apri" />
	<input type="hidden" name="activePage" value="0" />
	<input type="hidden" name="genereGara" value="4" />
	<input type="hidden" name="idconfi" value="${idconfi}" />
	<input type="hidden" name="autorizzatoModifiche" value="${autorizzatoModifiche }" />
</form>