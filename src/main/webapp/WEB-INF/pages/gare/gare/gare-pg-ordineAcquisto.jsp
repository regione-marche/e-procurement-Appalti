<%
/*
 * Created on: 08/07/2014
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

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<script type="text/javascript" src="${contextPath}/js/jquery.documenti.gara.js"></script>
<script type="text/javascript" src="${contextPath}/js/common-gare.js"></script>

<c:set var="modelliPredispostiAttivoIncondizionato" scope="request" value="1" />

<c:set var="codiceGara" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetCodiceGaraFunction", pageContext)}' />
<c:set var="ngara" value='${gene:getValCampo(key, "GARE.NGARA")}' />
<c:set var="firmaRemota" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "firmaremota.auto.url")}'/>

<c:choose>
	<c:when test="${isProceduraTelematica }">
		<c:set var="ruoloUtente" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetMERUOLOFunction", pageContext, ngara)}' scope="request"/>		
	</c:when>
	<c:otherwise>
		<c:set var="ruoloUtente" value='1' scope="request"/>
	</c:otherwise>
</c:choose>

<c:set var="tmp" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.CaricaDescPuntiContattoFunction",  pageContext, codiceGara, "GARECONT", "")}'/>
<c:set var="tmp1" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetDateConOraOrdineAcquistoFunction", pageContext, ngara)}' scope="request"/>
<c:set var="ditta" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetDittaFunction", pageContext, ngara)}' scope="request"/>
<c:set var="stato" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetStatoGarecontFunction",pageContext,ngara,"1")}' />

<c:set var="richiestaFirma" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "documentiDb.richiestaFirma")}'/>

<gene:formScheda entita="GARE" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreOrdine">
	
	<gene:redefineInsert name="pulsanteNuovo"></gene:redefineInsert>
	<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
	
	<c:if test='${empty ditta}'>
		<gene:redefineInsert name="schedaModifica"></gene:redefineInsert>
	</c:if>
	<gene:redefineInsert name="addToAzioni" >
		<c:if test='${modo eq "VISUALIZZA" && datiRiga.GARECONT_STATO == 2 && autorizzatoModifiche ne "2" && ruoloUtente eq "1" && gene:checkProtFunz(pageContext,"ALT","ImpostaOrdineDef")}'>
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
		<c:if test='${modo eq "VISUALIZZA" && datiRiga.GARECONT_STATO == 3 && autorizzatoModifiche ne "2" && ruoloUtente eq "1" && gene:checkProtFunz(pageContext,"ALT","TrasmettiOrdine")}'>
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
	</gene:redefineInsert>
	
	<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-scheda.jsp">
		<jsp:param name="entita" value="V_GARE_TORN"/>
		<jsp:param name="inputFiltro" value="CODGAR=T:${codiceGara}"/>
		<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
	</jsp:include>
	

		
	<gene:campoScheda campo="NGARA" visibile="false" />
	<gene:campoScheda campo="CODGAR1" visibile="false" />
	<gene:campoScheda campo="NGARA" entita="GARECONT" where="GARECONT.NGARA = GARE.NGARA and GARECONT.NCONT=1" visibile="false" />
	<gene:campoScheda campo="NCONT" entita="GARECONT" where="GARECONT.NGARA = GARE.NGARA and GARECONT.NCONT=1" visibile="false"  />
					
	<gene:gruppoCampi idProtezioni="DATIGEN" >
		<gene:campoScheda>
			<td colspan="2"><b>Dati generali</b></td>
		</gene:campoScheda>
		<c:if test='${modo eq "VISUALIZZA"}' >
			<c:set var="link" value='javascript:archivioImpresaAggDef();' />
		</c:if>
		<gene:campoScheda campo="DITTA"  href='${gene:if(gene:checkProt(pageContext, "MASC.VIS.GENE.ImprScheda"), link, "")}'  modificabile="false"/>
		<gene:campoScheda campo="NOMIMA" href='${gene:if(gene:checkProt(pageContext, "MASC.VIS.GENE.ImprScheda"), link, "")}'  modificabile="false"/>
		
			
		<gene:campoScheda campo="CODCIG"  />
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
		
		
		<gene:campoScheda campo="NREPAT"  title="Numero identificativo ordine" modificabile="${datiRiga.GARECONT_STATO ==2 }"/>
		<gene:campoScheda campo="DAATTO"   modificabile="${datiRiga.GARECONT_STATO == 2 }" />
		<gene:campoScheda campo="NPROAT"   entita="GARECONT" where="GARECONT.NGARA = GARE.NGARA and GARECONT.NCONT=1" modificabile="true"/>
		<gene:campoScheda campo="NOTE"   entita="GARECONT" where="GARECONT.NGARA = GARE.NGARA and GARECONT.NCONT=1" modificabile="${datiRiga.GARECONT_STATO == 2 }"/>
		<gene:campoScheda campo="STATO"  entita="GARECONT" where="GARECONT.NGARA = GARE.NGARA and GARECONT.NCONT=1" modificabile="false"/>
	</gene:gruppoCampi>
		
	<gene:campoScheda campo="CENINT" entita="TORN" where="TORN.CODGAR=GARE.CODGAR1"  visibile="false" />	
	<gene:campoScheda campo="CENINT" entita="GARECONT" where="GARECONT.NGARA = GARE.NGARA and GARECONT.NCONT=1" visibile="false"/>

	<c:set var="result" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetValoriGAREIVAFunction", pageContext, ngara)}' />
	
	<gene:gruppoCampi idProtezioni="IMPORTO" >
		<gene:campoScheda >
			<td colspan="2"><b>Importo</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="IAGGIU" title="Importo totale IVA esclusa"  modificabile="false"/>
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
		<gene:campoScheda campo="IMPIVA" entita="GARECONT" where="GARECONT.NGARA = GARE.NGARA and GARECONT.NCONT=1" />
		<gene:campoScheda campo="IMPTOT"  entita="GARECONT" where="GARECONT.NGARA = GARE.NGARA and GARECONT.NCONT=1" />
	</gene:gruppoCampi>
		
	<gene:gruppoCampi idProtezioni="CONSEGNA" >
		<gene:campoScheda >
			<td colspan="2"><b>Consegna e fatturazione</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="PCOESE_FIT" campoFittizio="true" title="Consegna o esecuzione dell'ordine presso" definizione="T2;;A1098;;" 
			visibile='${(gene:checkProt(pageContext, "COLS.VIS.GARE.GARECONT.PCOESE") || gene:checkProt(pageContext, "COLS.VIS.GARE.GARECONT.LOCESE") ) && datiRiga.GARECONT_STATO == 2 }'
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
			chiave="GARECONT_PCOESE;CENINT_PCOESE"
			formName="formPuntiConsegnaOrdine"
			inseribile="false">
				<gene:campoScheda campo="CENINT_PCOESE" campoFittizio="true" definizione="T16" visibile="false" value="${datiRiga.TORN_CENINT}"/>
				<gene:campoScheda campo="PCOESE" entita="GARECONT" where="GARECONT.NGARA = GARE.NGARA and GARECONT.NCONT=1" visibile="false"/>
				<c:set var="linkPCOESE" value='javascript:archivioPunticon("${datiRiga.TORN_CENINT}","${datiRiga.GARECONT_PCOESE}");' />
				<gene:campoScheda campo="NOMPUN_PCOESE" campoFittizio="true" definizione="T245;;;NOTE;G_NOMPUN" value="${initNOMPUN_PCOESE }" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.GARECONT.PCOESE")}' modificabile="${datiRiga.GARECONT_STATO == 2 }" href='${gene:if(modo eq "VISUALIZZA",linkPCOESE,"")}'/>
		</gene:archivio>
		<gene:campoScheda campo="LOCESE" entita="GARECONT" where="GARECONT.NGARA = GARE.NGARA and GARECONT.NCONT=1" modificabile="${datiRiga.GARECONT_STATO == 2 }"/>

		<gene:campoScheda campo="MODPAG" entita="GARECONT" where="GARECONT.NGARA = GARE.NGARA and GARECONT.NCONT=1" modificabile="${datiRiga.GARECONT_STATO == 2 }"/>
		
		<gene:campoScheda campo="PCOFAT_FIT" campoFittizio="true" title="Fatturazione presso" definizione="T2;;A1098;;" 
			visibile='${(gene:checkProt(pageContext, "COLS.VIS.GARE.GARECONT.PCOFAT") || gene:checkProt(pageContext, "COLS.VIS.GARE.GARECONT.LOCFAT") ) && datiRiga.GARECONT_STATO == 2}'
			gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoPresso">
			<gene:checkCampoScheda funzione='checkPresenzaCenint("#PCOFAT_FIT#")' obbligatorio="true" messaggio="Impossibile selezionare il valore. Non è presente la Stazione appaltante." onsubmit="false"/>
		</gene:campoScheda>
		<gene:campoScheda campo="PCOFAT_FIT1" campoFittizio="true" title="Fatturazione presso" definizione="T2;;A1098;;" 
			visibile='${(gene:checkProt(pageContext, "COLS.VIS.GARE.GARECONT.PCOFAT") || gene:checkProt(pageContext, "COLS.VIS.GARE.GARECONT.LOCFAT") ) && datiRiga.GARECONT_STATO != 2}'
			gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoPresso" modificabile="false"/>
		<gene:archivio titolo="Punti di contatto" 
			lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.GARECONT.PCOFAT"),"gene/punticon/punticon-lista-popup.jsp","")}' 
			scheda=''
			schedaPopUp="gene/punticon/punticon-scheda-popup.jsp"
			campi="PUNTICON.CODEIN;PUNTICON.NUMPUN;PUNTICON.NOMPUN" 
			chiave="GARECONT_PCOFAT;CENINT_PCOFAT"
			formName="formPuntiFatturazione"
			inseribile="false">
				<gene:campoScheda campo="CENINT_PCOFAT" campoFittizio="true" definizione="T16" visibile="false" value="${datiRiga.TORN_CENINT}"/>
				<gene:campoScheda campo="PCOFAT" entita="GARECONT" where="GARECONT.NGARA = GARE.NGARA and GARECONT.NCONT=1" visibile="false"/>
				<c:set var="linkPCOFAT" value='javascript:archivioPunticon("${datiRiga.TORN_CENINT}","${datiRiga.GARECONT_PCOFAT}");' />
				<gene:campoScheda campo="NOMPUN_PCOFAT" campoFittizio="true" definizione="T245;;;NOTE;G_NOMPUN" value="${initNOMPUN_PCOFAT }" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.GARECONT.PCOFAT")}' modificabile="${datiRiga.GARECONT_STATO == 2 }" href='${gene:if(modo eq "VISUALIZZA",linkPCOFAT,"")}'/>
		</gene:archivio>
		<gene:campoScheda campo="LOCFAT" entita="GARECONT" where="GARECONT.NGARA = GARE.NGARA and GARECONT.NCONT=1" modificabile="${datiRiga.GARECONT_STATO == 2 }"/>
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
		<jsp:param name="sezioneEliminabile" value="${stato == 2 }"/>
		<jsp:param name="sezioneInseribile" value="${stato == '2' }"/>
		<jsp:param name="valoreStato" value="${stato}"/>
		<jsp:param name="richiestaFirma" value="${richiestaFirma}"/>
		<jsp:param name="autorizzatoModifiche" value="${autorizzatoModifiche}"/>
		<jsp:param name="ordineNonDefinito" value="${datiRiga.GARECONT_STATO == 2 }"/>
		<jsp:param name="firmaRemota" value="${firmaRemota}"/>
	</jsp:include>
	
	<gene:gruppoCampi idProtezioni="DEFINIZIONE" >
		<gene:campoScheda >
			<td colspan="2"><b>Definizione ordine</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="DATDEF" entita="GARECONT" where="GARECONT.NGARA = GARE.NGARA and GARECONT.NCONT=1" visibile="false"/>
		<gene:campoScheda campo="DATDEF_FIT" campoFittizio="true" modificabile="false" definizione="T20;0;;;G1DATDEF" value="${initDATDEF }"/>
	</gene:gruppoCampi>
	
	<gene:gruppoCampi idProtezioni="TRASMISSIONE" >
		<gene:campoScheda >
			<td colspan="2"><b>Trasmissione ordine</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="IDPRG" entita="GARECONT" where="GARECONT.NGARA = GARE.NGARA and GARECONT.NCONT=1" visibile="false"/>
		<gene:campoScheda campo="IDDOCDG" entita="GARECONT" where="GARECONT.NGARA = GARE.NGARA and GARECONT.NCONT=1" visibile="false"/>
		<c:choose>
			<c:when test="${modo eq 'VISUALIZZA'}">
				<gene:campoScheda title="Documento d'ordine" campo="DIGNOMDOC" entita="W_DOCDIG" from="GARECONT" where="W_DOCDIG.IDPRG=GARECONT.IDPRG AND W_DOCDIG.IDDOCDIG=GARECONT.IDDOCDG and GARECONT.NGARA = GARE.NGARA and GARECONT.NCONT=1" modificabile="false" href="javascript:visualizzaFileAllegato('${datiRiga.GARECONT_IDPRG}','${datiRiga.GARECONT_IDDOCDG}','${datiRiga.W_DOCDIG_DIGNOMDOC}');"/>
			</c:when>
			<c:otherwise>
				<gene:campoScheda title="Documento d'ordine" campo="DIGNOMDOC" entita="W_DOCDIG" from="GARECONT" where="W_DOCDIG.IDPRG=GARECONT.IDPRG AND W_DOCDIG.IDDOCDIG=GARECONT.IDDOCDG and GARECONT.NGARA = GARE.NGARA and GARECONT.NCONT=1" modificabile="false" />
			</c:otherwise>
		</c:choose>
		<gene:campoScheda campo="DATTRA" entita="GARECONT" where="GARECONT.NGARA = GARE.NGARA and GARECONT.NCONT=1" visibile="false"/>
		<gene:campoScheda campo="DATTRA_FIT" campoFittizio="true" modificabile="false" definizione="T20;0;;;G1DATTRA" value="${initDATTRA }"/>
		<gene:campoScheda campo="DATLET" entita="GARECONT" where="GARECONT.NGARA = GARE.NGARA and GARECONT.NCONT=1" visibile="false"/>
		<gene:campoScheda campo="DATLET_FIT" campoFittizio="true" modificabile="false" definizione="T20;0;;;G1DATLET" value="${initDATLET }" visibile="false"/>
	</gene:gruppoCampi>
	
	<gene:gruppoCampi idProtezioni="REVOCA" >
		<gene:campoScheda>
			<td colspan="2"><b>Revoca ordine</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="DATREV"  entita="GARECONT" where="GARECONT.NGARA = GARE.NGARA and GARECONT.NCONT=1" visibile="false"/>
		<gene:campoScheda campo="DATREV_FIT" campoFittizio="true" modificabile="false" definizione="T20;0;;;G1DATREV" value="${initDATREV }"/>
		<gene:campoScheda campo="MOTREV"  entita="GARECONT" where="GARECONT.NGARA = GARE.NGARA and GARECONT.NCONT=1" modificabile="false"/>
	</gene:gruppoCampi>
	
	<gene:gruppoCampi idProtezioni="ALTRI" >
		<gene:campoScheda>
			<td colspan="2"><b>Altri dati</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="DATRIF" entita="GARECONT" where="GARECONT.NGARA = GARE.NGARA and GARECONT.NCONT=1"/>
	</gene:gruppoCampi>
	
	<input type="hidden" name="filtroDocumentazione" id="filtroDocumentazione" value="11">
	
	<c:if test="${modo ne 'VISUALIZZA' }">
		<gene:fnJavaScriptScheda funzione="showCampiContatto('#PCOESE_FIT#','LOCESE','PCOESE')" elencocampi="PCOESE_FIT" esegui="false" />
		<gene:fnJavaScriptScheda funzione="showCampiContatto('#PCOFAT_FIT#','LOCFAT','PCOFAT')" elencocampi="PCOFAT_FIT" esegui="false" />
	</c:if>
	
	<gene:campoScheda>
		<td class="comandi-dettaglio" colSpan="2">
			<gene:insert name="addPulsanti"/>
			<c:choose>
			<c:when test='${modo eq "MODIFICA" or modo eq "NUOVO"}'>
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
					<c:if test='${gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD") && !empty ditta}'>
						<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:schedaModifica()">
					</c:if>
				</gene:insert>
				<gene:insert name="pulsanteNuovo">
					<c:if test='${gene:checkProtFunz(pageContext,"INS","SCHEDANUOVO")}'>
						<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaNuovo")}' title='${gene:resource("label.tags.template.lista.listaNuovo")}' onclick="javascript:schedaNuovo()" id="btnNuovo">
					</c:if>
				</gene:insert>
				<c:if test='${datiRiga.GARECONT_STATO == 3 && autorizzatoModifiche ne "2" && ruoloUtente eq "1" && gene:checkProtFunz(pageContext,"ALT","TrasmettiOrdine")}'>
					<INPUT type="button" class="bottone-azione" value="Trasmetti ordine" title="Trasmetti ordine" onclick="javascript:trasmettiOrdine()">
				</c:if>
			</c:otherwise>
			</c:choose>
			&nbsp;
		</td>
	</gene:campoScheda>
	
	<gene:javaScript>
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
	<c:if test="${modo ne 'VISUALIZZA'}">
		var stato= getValue("GARECONT_STATO");
		if(stato!=2){
			$('[id^="GAREIVA_PERCIVA_"]').attr('disabled',true);
			$('[id^="GAREIVA_PERCIVA_"]').css('background-color','#ECECEC');
			$('[id^="GAREIVA_IMPONIB_"]').attr('disabled',true);
			$('[id^="GAREIVA_IMPONIB_"]').css('background-color','#ECECEC');
					
			$('[id^="rowtitoloGAREIVA_"]').hide();
			$('[id^="rowLinkAddGAREIVA"]').hide();
		}
	</c:if>
	
	<c:if test="${modo eq 'MODIFICA' and (datiRiga.GARE_CODCIG ne '' and not (fn:startsWith(datiRiga.GARE_CODCIG,'#') or fn:startsWith(datiRiga.GARE_CODCIG,'$') or fn:startsWith(datiRiga.GARE_CODCIG,'NOCIG')))}">
		$("#GARE_CODCIG").css({'text-transform': 'uppercase' });
		
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
					if(((iva==null || iva =="" ) && (imponibile !=null && imponibile != "")) || 
						((imponibile ==null || imponibile == "") && (iva!=null && iva !="" ))){
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
	
	
	function archivioImpresaAggDef(){
		var codiceImpresa = getValue("GARE_DITTA");
		var href = ("href=gene/impr/impr-scheda.jsp&key=IMPR.CODIMP=T:" + codiceImpresa);
		document.location.href = contextPath + "/ApriPagina.do?"+csrfToken+"&" + href;
	}
	
	function checkPresenzaCenint(valore){
		if(valore==2){
			var cenint = getValue("TORN_CENINT");
			if(cenint=="" || cenint == null)
				return false;
		}
		return true;
	}
	
	var cenint = getValue("TORN_CENINT");
	if(cenint!=""){
		if(document.formPuntiConsegnaOrdine!=null)
			document.formPuntiConsegnaOrdine.archWhereLista.value="PUNTICON.CODEIN='" + cenint + "'";
		if(document.formPuntiFatturazione!=null)
			document.formPuntiFatturazione.archWhereLista.value="PUNTICON.CODEIN='" + cenint + "'";
		
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
	
	function archivioPunticon(codice,num){
		var href = ("href=gene/punticon/punticon-scheda-popup.jsp&key=PUNTICON.CODEIN=T:" + codice + ";PUNTICON.NUMPUN=N:" + num);
		openPopUp(href, "schedaPunticon");
	}
			
	function impostaOrdine(){
		var ngara = getValue("GARECONT_NGARA");
		var ncont = getValue("GARECONT_NCONT");
		var href = "href=gare/garecont/garecont-popup-impostaOrdineDefinito.jsp";
		href += "&ngara="+ngara + "&ncont=" + ncont + "&eseguireControlliPreliminari=no";
		openPopUpCustom(href, "impostaOrdineDefinito", 650, 450, "yes", "yes");
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
		href += "&ngara=" + ngara + "&ncont=" + ncont + "&nprot=" + nprot + "&ditta=" + ditta + "&modo=MODIFICA" + "&nomeEntita=GARE&cig=" + cig + "&isODA=false&step=1&codgar=" + codgar;
		var idconfi = "${idconfi}";
		if(idconfi){
			href += "&idconfi=" + idconfi
		}
		openPopUpCustom(href, "trasmettiOrdine", 900, 650, "yes", "yes");
	}
	
	
	<c:if test='${!(modo eq "VISUALIZZA")}'>
        //Diminuisco la larghezza della prima classe="valore-dato" (ossia Aliquota)
		for(var i=1; i < maxIdGAREIVAVisualizzabile ; i++){
                $('#rowGAREIVA_ID_' + i).find( "td.valore-dato" ).first().css('width', '100');
        }
  
        
    </c:if>
    
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
			if(ext=='P7M' || ext=='TSD'){
				document.formVisFirmaDigitale.idprg.value = idprg;
				document.formVisFirmaDigitale.iddocdig.value = iddocdig;
				document.formVisFirmaDigitale.submit();
			}else{
				var href = "${pageContext.request.contextPath}/pg/VisualizzaFileAllegato.do";
			document.location.href=href+"?"+csrfToken+"&idprg=" + idprg + "&iddocdig=" + iddocdig + "&dignomdoc=" + encodeURIComponent(dignomdoc);
			}
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
</gene:formScheda>

<jsp:include page="/WEB-INF/pages/gene/system/firmadigitale/modalPopupFirmaDigitaleRemota.jsp" />	

<form name="formVisFirmaDigitale" id="formVisFirmaDigitale" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
	<input type="hidden" name="href" value="gene/system/firmadigitale/verifica-firmadigitale.jsp" />
	<input type="hidden" name="idprg" id="idprg" value="" />
	<input type="hidden" name="iddocdig" id="iddocdig" value="" />
</form>