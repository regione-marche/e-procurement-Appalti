<%
/*
 * Created on: 13/11/2006
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

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<script type="text/javascript" src="${contextPath}/js/jquery.storico.rettifica.termini.gara.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.pg.titoli.js"></script>


<c:set var="tmp" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.CaricaDescPuntiContattoFunction",  pageContext, param.valoreCodgar, "TORN", "")}'/>

	<gene:gruppoCampi idProtezioni="PUB">
		<gene:campoScheda nome="PUB">
			<td colspan="2"><b>Pubblicazione avviso/bando</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="NAVVIG" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR"/>
		<gene:campoScheda campo="DAVVIG" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR"/>
		<gene:campoScheda campo="DPUBAV" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR"/>
		<gene:campoScheda campo="DFPUBA" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR"/>
		<gene:campoScheda campo="DIBAND" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR"/>
	</gene:gruppoCampi>
	
	<c:if test='${esisteFunzioneRettificaTermini eq "true" }'>
		<c:set var="esisteRettificaPartecipazione" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoDatiRettificaTerminiFunction",  pageContext, param.valoreCodgar, "1")}'/>
		<c:set var="esisteRettificaOfferta" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoDatiRettificaTerminiFunction",  pageContext, param.valoreCodgar, "2")}'/>
		<c:set var="esisteRettificaApertura" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoDatiRettificaTerminiFunction",  pageContext, param.valoreCodgar, "3")}'/>
	</c:if>
	
	<gene:gruppoCampi idProtezioni="PDP" >
		<gene:campoScheda nome="PDP">
			<td colspan="2"><b>Termini per la presentazione della domanda di partecipazione</b> <c:if test="${esisteRettificaPartecipazione eq 'true' }"> <span style="float:right" ><a id="aLinkVisualizzaDettaglioRettificaTerminiPartecipazione" href="javascript:showDettRetifica(1);" class="link-generico">Visualizza termini di gara precedenti alla rettifica</a></span></c:if></td>
		</gene:campoScheda>
		<gene:campoScheda campo="DTEPAR" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" speciale='${gene:checkProtFunz(pageContext, "ALT","CalcoloTermineMinimo")}' >
			<c:if test='${gene:checkProtFunz(pageContext, "ALT","CalcoloTermineMinimo")}'>
				<gene:popupCampo titolo="Calcola termine minimo" href="calcolaTermineMinimo('DTEPAR');" />
			</c:if>
		</gene:campoScheda>
		<gene:campoScheda campo="OTEPAR" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR"/>
		<gene:campoScheda campo="PCOPRE_FIT" campoFittizio="true" title="Presentazione presso" definizione="T2;;A1098;;" 
			visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.PCOPRE") || gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.LOCPRE")}'
			gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoPresso">
			<gene:checkCampoScheda funzione='checkPresenzaCenint("#PCOPRE_FIT#")' obbligatorio="true" messaggio="Impossibile selezionare il valore. Non è presente la Stazione appaltante." onsubmit="false"/>
		</gene:campoScheda>
		<gene:archivio titolo="Punti di contatto" 
			lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.PCOPRE"),"gene/punticon/punticon-lista-popup.jsp","")}' 
			scheda=''
			schedaPopUp="gene/punticon/punticon-scheda-popup.jsp"
			campi="PUNTICON.CODEIN;PUNTICON.NUMPUN;PUNTICON.NOMPUN" 
			functionId="default"
			parametriWhere="T:${datiRiga.TORN_CENINT}"
			chiave="TORN_PCOPRE;CENINT_PCOPRE"
			formName="formPuntiContPresentazionePartecipazione"
			inseribile="false">
				<gene:campoScheda campo="CENINT_PCOPRE" campoFittizio="true" definizione="T16" visibile="false" value="${datiRiga.TORN_CENINT}"/>
				<gene:campoScheda campo="PCOPRE" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" visibile="false"/>
				<c:set var="linkPCOPRE" value='javascript:archivioPunticon("${datiRiga.TORN_CENINT}","${datiRiga.TORN_PCOPRE}");' />
				<gene:campoScheda campo="NOMPUN_PCOPRE" campoFittizio="true" definizione="T245;;;NOTE;G_NOMPUN" value="${initNOMPUN_PCOPRE }" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.PCOPRE")}' href='${gene:if(modo eq "VISUALIZZA",linkPCOPRE,"")}'/>
		</gene:archivio>
		<gene:campoScheda campo="LOCPRE" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR"/>
		<gene:campoScheda campo="PCODOC_FIT" campoFittizio="true" title="Ulteriori informazioni presso" definizione="T2;;A1098;;" 
			visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.PCODOC") || gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.DOCGAR")}'
			gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoPresso">
			<gene:checkCampoScheda funzione='checkPresenzaCenint("#PCODOC_FIT#")' obbligatorio="true" messaggio="Impossibile selezionare il valore. Non è presente la Stazione appaltante." onsubmit="false"/>
		</gene:campoScheda>
		<gene:archivio titolo="Punti di contatto" 
			lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.PCODOC"),"gene/punticon/punticon-lista-popup.jsp","")}' 
			scheda=''
			schedaPopUp="gene/punticon/punticon-scheda-popup.jsp"
			campi="PUNTICON.CODEIN;PUNTICON.NUMPUN;PUNTICON.NOMPUN" 
			functionId="default"
			parametriWhere="T:${datiRiga.TORN_CENINT}"
			chiave="TORN_PCODOC;CENINT_PCODOC"
			formName="formPuntiContInformazioni"
			inseribile="false">
				<gene:campoScheda campo="CENINT_PCODOC" campoFittizio="true" definizione="T16" visibile="false" value="${datiRiga.TORN_CENINT}"/>
				<gene:campoScheda campo="PCODOC" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" visibile="false"/>
				<c:set var="linkPCODOC" value='javascript:archivioPunticon("${datiRiga.TORN_CENINT}","${datiRiga.TORN_PCODOC}");' />
				<gene:campoScheda campo="NOMPUN_PCODOC" campoFittizio="true" definizione="T245;;;NOTE;G_NOMPUN" value="${initNOMPUN_PCODOC }" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.PCODOC")}' href='${gene:if(modo eq "VISUALIZZA",linkPCODOC,"")}'/>
		</gene:archivio>
		<gene:campoScheda campo="DOCGAR" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR"/>
		<gene:campoScheda campo="DTERMRICHCDP" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR"/>
		<gene:campoScheda campo="DTERMRISPCDP" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR"/>
		
		<gene:campoScheda addTr="false" visibile="${esisteRettificaPartecipazione eq 'true' }">
				<tr id="rigaTabellaRettificaTerminiPartecipaz">
					<td colspan="2">
						<table id="tabellaRettificaTerminiPartecipaz" class="griglia" >
							
		</gene:campoScheda>
			
			
		<gene:campoScheda addTr="false" visibile="${esisteRettificaPartecipazione eq 'true' }">
						
						</table>
					<td>
				<tr>
		</gene:campoScheda>		
		
		
	</gene:gruppoCampi>
	
	<c:choose>
		<c:when test='${param.isGaraLottoUnico || (!gene:checkProt(pageContext,"PAGE.VIS.GARE.GARE-scheda.FASIRICEZIONE") and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.PubblicaSuPortale"))}'>
			<c:set var="modificabile" value="true"/>
		</c:when>
		<c:when test="${!param.isGaraLottoUnico and !param.isGaraOffertaUnica}">
			<c:set var="modificabile" value="false"/>
		</c:when>
	</c:choose>
	
	<gene:gruppoCampi idProtezioni="INVITO" visibile='${param.isGaraLottoUnico || (!param.isGaraLottoUnico and !param.isGaraOffertaUnica)}' >
		<gene:campoScheda nome="INVITO">
			<td colspan="2"><b>Estremi invito</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="DINVIT" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" modificabile="${modificabile}"/>
		<gene:campoScheda campo="NPROTI" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" modificabile="${modificabile}"/>
	</gene:gruppoCampi>
		
	<gene:gruppoCampi idProtezioni="PDO">
		<gene:campoScheda nome="PDO">
			<td colspan="2"><b>Termini per la presentazione dell'offerta</b> <c:if test="${esisteRettificaOfferta eq 'true' }"> <span style="float:right"><a id="aLinkVisualizzaDettaglioRettificaTerminiOfferta" href="javascript:showDettRetifica(2);" class="link-generico">Visualizza termini di gara precedenti alla rettifica</a></span></c:if></td>
		</gene:campoScheda>
		<gene:campoScheda campo="DTEOFF" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" speciale='${gene:checkProtFunz(pageContext, "ALT","CalcoloTermineMinimo")}'>
			<c:if test='${gene:checkProtFunz(pageContext, "ALT","CalcoloTermineMinimo")}'>
				<gene:popupCampo titolo="Calcola termine minimo" href="calcolaTermineMinimo('DTEOFF');" />
			</c:if>
		</gene:campoScheda>
		<gene:campoScheda campo="OTEOFF" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" />
		<gene:campoScheda campo="PCOOFF_FIT" campoFittizio="true" title="Presentazione presso" definizione="T2;;A1098;;" 
			visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.PCOOFF") || gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.LOCOFF")}'
			gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoPresso">
			<gene:checkCampoScheda funzione='checkPresenzaCenint("#PCOOFF_FIT#")' obbligatorio="true" messaggio="Impossibile selezionare il valore. Non è presente la Stazione appaltante." onsubmit="false"/>
		</gene:campoScheda>
		<gene:archivio titolo="Punti di contatto" 
			lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.PCOOFF"),"gene/punticon/punticon-lista-popup.jsp","")}' 
			scheda=''
			schedaPopUp="gene/punticon/punticon-scheda-popup.jsp"
			campi="PUNTICON.CODEIN;PUNTICON.NUMPUN;PUNTICON.NOMPUN" 
			functionId="default"
			parametriWhere="T:${datiRiga.TORN_CENINT}"
			chiave="TORN_PCOOFF;CENINT_PCOOFF"
			formName="formPuntiContPresentazioneOfferta"
			inseribile="false">
				<gene:campoScheda campo="CENINT_PCOOFF" campoFittizio="true" definizione="T16" visibile="false" value="${datiRiga.TORN_CENINT}"/>
				<gene:campoScheda campo="PCOOFF" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" visibile="false"/>
				<c:set var="linkPCOOFF" value='javascript:archivioPunticon("${datiRiga.TORN_CENINT}","${datiRiga.TORN_PCOOFF}");' />
				<gene:campoScheda campo="NOMPUN_PCOOFF" campoFittizio="true" definizione="T245;;;NOTE;G_NOMPUN" value="${initNOMPUN_PCOOFF }" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.PCOOFF")}' href='${gene:if(modo eq "VISUALIZZA",linkPCOOFF,"")}'/>
		</gene:archivio>
		<gene:campoScheda campo="LOCOFF" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" />
		<gene:campoScheda campo="VALOFF" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" />
		<gene:campoScheda campo="DTERMRICHCPO" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" />
		<gene:campoScheda campo="DTERMRISPCPO" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" />
		
		<gene:campoScheda campo="DTEOFF_FIT" campoFittizio="true" title="Data" modificabile="false"  value="${datiRiga.TORN_DTEOFF}"  definizione="D;;;;DTEOFF" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.DTEOFF")}'/>
		<gene:campoScheda campo="OTEOFF_FIT" campoFittizio="true" title="Ora" modificabile="false" value="${datiRiga.TORN_OTEOFF}"  definizione="T6;;;;OTEOFF" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.OTEOFF")}'/>
		<gene:campoScheda campo="PCOOFF_FIT1" campoFittizio="true" title="Presentazione presso" definizione="T2;;A1098;;" 
			visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.PCOOFF") || gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.LOCOFF")}'
			modificabile="false"/>
		<gene:campoScheda campo="NOMPUN_PCOOFF_BLOCCATO" campoFittizio="true" definizione="T245;;;NOTE;G_NOMPUN" modificabile="false" value="${initNOMPUN_PCOOFF }" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.PCOOFF")}'/>	
		<gene:campoScheda campo="LOCOFF_FIT" campoFittizio="true" title="Luogo" modificabile="false" value="${datiRiga.TORN_LOCOFF}"  definizione="T200;;;;LOCOFF" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.LOCOFF")}'/>
		<gene:campoScheda campo="VALOFF_FIT" campoFittizio="true" title="Numero giorni di validità offerta" modificabile="false" value="${datiRiga.TORN_VALOFF}"  definizione="T20;;;;VALOFF" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.VALOFF")}'/>
		<gene:campoScheda campo="DTERMRICHCPO_FIT" campoFittizio="true" title="Termine richieste chiarimenti pres. offerta" modificabile="false" value="${datiRiga.TORN_DTERMRICHCPO}"  definizione="D;;;;DATTRCPO" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.DTERMRICHCPO")}'/>
		<gene:campoScheda campo="DTERMRISPCPO_FIT" campoFittizio="true" title="Termine risposta chiarimenti pres.offerta" modificabile="false" value="${datiRiga.TORN_DTERMRISPCPO}"  definizione="D;;;;DATTURCPO" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.DTERMRISPCPO")}'/>
		
		<gene:campoScheda addTr="false" visibile="${esisteRettificaOfferta eq 'true' }">
				<tr id="rigaTabellaRettificaTerminiOfferta">
					<td colspan="2">
						<table id="tabellaRettificaTerminiOfferta" class="griglia" >
							
		</gene:campoScheda>
			
			
		<gene:campoScheda addTr="false" visibile="${esisteRettificaOfferta eq 'true' }">
						
						</table>
					<td>
				<tr>
		</gene:campoScheda>	
	</gene:gruppoCampi>

	<gene:gruppoCampi idProtezioni="OFF">
		<gene:campoScheda nome="OFF">
			<td colspan="2"><b>Apertura plichi</b> <c:if test="${esisteRettificaApertura eq 'true' }"> <span style="float:right"><a id="aLinkVisualizzaDettaglioRettificaTerminiApertura" href="javascript:showDettRetifica(3);" class="link-generico">Visualizza termini di gara precedenti alla rettifica</a></span></c:if></td>
		</gene:campoScheda>
		<gene:campoScheda campo="DESOFF" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" />
		<gene:campoScheda campo="OESOFF" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" />
		<gene:campoScheda campo="PCOGAR_FIT" campoFittizio="true" title="Presso" definizione="T2;;A1098;;" 
			visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.PCOGAR") || gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.LOCGAR")}'
			gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoPresso">
			<gene:checkCampoScheda funzione='checkPresenzaCenint("#PCOGAR_FIT#")' obbligatorio="true" messaggio="Impossibile selezionare il valore. Non è presente la Stazione appaltante." onsubmit="false"/>
		</gene:campoScheda>
		<gene:archivio titolo="Punti di contatto" 
			lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.PCOGAR"),"gene/punticon/punticon-lista-popup.jsp","")}' 
			scheda=''
			schedaPopUp="gene/punticon/punticon-scheda-popup.jsp"
			campi="PUNTICON.CODEIN;PUNTICON.NUMPUN;PUNTICON.NOMPUN" 
			functionId="default"
			parametriWhere="T:${datiRiga.TORN_CENINT}"
			chiave="TORN_PCOGAR;CENINT_PCOGAR"
			formName="formPuntiContAperturaPlichi"
			inseribile="false">
				<gene:campoScheda campo="CENINT_PCOGAR" campoFittizio="true" definizione="T16" visibile="false" value="${datiRiga.TORN_CENINT}"/>
				<gene:campoScheda campo="PCOGAR" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" visibile="false"/>
				<c:set var="linkPCOGAR" value='javascript:archivioPunticon("${datiRiga.TORN_CENINT}","${datiRiga.TORN_PCOGAR}");' />
				<gene:campoScheda campo="NOMPUN_PCOGAR" campoFittizio="true" definizione="T245;;;NOTE;G_NOMPUN" value="${initNOMPUN_PCOGAR }" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.PCOGAR")}' href='${gene:if(modo eq "VISUALIZZA",linkPCOGAR,"")}'/>
		</gene:archivio>
		<gene:campoScheda campo="LOCGAR" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" />
		
		<gene:campoScheda campo="DESOFF_FIT"  campoFittizio="true" title="Data" modificabile="false" value="${datiRiga.TORN_DESOFF}"  definizione="D;;;;DESOFF" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.DESOFF")}'/>
		<gene:campoScheda campo="OESOFF_FIT"  campoFittizio="true" title="Ora" modificabile="false" value="${datiRiga.TORN_OESOFF}"  definizione="T6;;;;OESOFF" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.OESOFF")}'/>
		<gene:campoScheda campo="PCOGAR_FIT1" campoFittizio="true" title="Presentazione presso" definizione="T2;;A1098;;" 
			visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.PCOGAR") || gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.LOCGAR")}'
			modificabile="false"/>
		<gene:campoScheda campo="NOMPUN_PCOGAR_BLOCCATO" campoFittizio="true" definizione="T245;;;NOTE;G_NOMPUN" modificabile="false" value="${initNOMPUN_PCOGAR }" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.PCOGAR")}'/>
		<gene:campoScheda campo="LOCGAR_FIT"  campoFittizio="true" title="Luogo" modificabile="false" value="${datiRiga.TORN_LOCGAR}"  definizione="T200;;;;LOCGAR" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.LOCGAR")}'/>
		<gene:campoScheda addTr="false" visibile="${esisteRettificaApertura eq 'true' }">
				<tr id="rigaTabellaRettificaTerminiApertura">
					<td colspan="2">
						<table id="tabellaRettificaTerminiApertura" class="griglia" >
							
		</gene:campoScheda>
			
			
		<gene:campoScheda  addTr="false" visibile="${esisteRettificaApertura eq 'true' }">
						
						</table>
					<td>
				<tr>
		</gene:campoScheda>	
	</gene:gruppoCampi>
		
	<gene:fnJavaScriptScheda funzione="showSezioniPresentazioni('TORN_ITERGA', ${param.isGaraLottoUnico}, '${param.campoTipoProcedura}')" elencocampi="${param.campoTipoProcedura}" esegui="true" />
	<c:if test="${modo ne 'VISUALIZZA' }">
		<gene:fnJavaScriptScheda funzione="showCampiContatto('#PCOPRE_FIT#','LOCPRE','PCOPRE')" elencocampi="PCOPRE_FIT" esegui="false" />
		<gene:fnJavaScriptScheda funzione="showCampiContatto('#PCODOC_FIT#','DOCGAR','PCODOC')" elencocampi="PCODOC_FIT" esegui="false" />
		<gene:fnJavaScriptScheda funzione="showCampiContatto('#PCOOFF_FIT#','LOCOFF','PCOOFF')" elencocampi="PCOOFF_FIT" esegui="false" />
		<gene:fnJavaScriptScheda funzione="showCampiContatto('#PCOGAR_FIT#','LOCGAR','PCOGAR')" elencocampi="PCOGAR_FIT" esegui="false" />
	</c:if>
		
	<gene:javaScript>
	
	<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.PersonalizzazioneAutovie")}'>
			redefineLabels();
			redefineTooltips();
	</c:if>
	

	function showSezioniPresentazioni(campoTipoProcedura, isGaraLottoUnico, campoTipoProceduraCustom) {
		// isGaraLottoUnico = boolean che indica se la gara e' a lotto unico o 
		// e' gara a lotti con offerta unica
		var tipoProcAggiud = getValue(campoTipoProcedura);
		//alert("tipoProcAggiud = '" + tipoProcAggiud + "'");
		var tipoProcAggiudCustom = getValue(campoTipoProceduraCustom);
		if(isGaraLottoUnico){
			// La sezione 'Termini per la presentazione della documentazione per controllo requisiti'
			// non e' mai visibile per gare a lotto unico o per gare a lotti con offerta unica
			if(tipoProcAggiud == "1"){  // Procedura aperta
				//alert("1.1");
				showSezioneTerminiPresDomandaPartecipa(false);
				showSezioneTerminiPresOfferta(true);
				showSezioneAperturaOfferte(true);
				showSezioneEstremiInvito(false);
			}else if(tipoProcAggiud=="3" || tipoProcAggiud=="5" || tipoProcAggiud=="6"){
				//Solo se gara a lotto unico, se invece della pagina 'Ricezione domande offerte' 
				//è visibile la pagina 'Ditte concorrenti' (profilo Affidamenti diretti), vengono rese disponibili
				//le sezioni 'Invito' e 'Termine pres.offerte' 
				if (!${gene:checkProt(pageContext, "PAGE.VIS.GARE.GARE-scheda.FASIRICEZIONE")}){
					showSezioneTerminiPresDomandaPartecipa(false);
					showSezioneTerminiPresOfferta(true);
					showSezioneAperturaOfferte(false);
					showSezioneEstremiInvito(true);
				} else {
					//alert("1.3");
					showSezioneTerminiPresDomandaPartecipa(false);
					showSezioneTerminiPresOfferta(false);
					showSezioneAperturaOfferte(false);
					showSezioneEstremiInvito(false);
				}
			}else {
				//alert("1.2");
				showSezioneTerminiPresDomandaPartecipa(true);
				showSezioneTerminiPresOfferta(false);
				showSezioneAperturaOfferte(false);
				showSezioneEstremiInvito(false);
			} 
			
		} else {
			var profiloSemplificato=${!gene:checkProt(pageContext,"PAGE.VIS.GARE.GARE-scheda.FASIRICEZIONE") and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.PubblicaSuPortale") };
			// La sezione 'Termini per la presentazione della documentazione per controllo requisiti'
			// e' sempre visibile per gare a lotti con offerte distinte
			if(${requestScope.tipologiaGara ne "3"}){
				if (tipoProcAggiud=="1" || tipoProcAggiud=="3" || tipoProcAggiud == "5" || tipoProcAggiud == "6"){
					//alert("2.1");
					showSezioneTerminiPresDomandaPartecipa(false);
				} else {
					//alert("2.2");
					showSezioneTerminiPresDomandaPartecipa(true);
				}
				if (tipoProcAggiud == "1" || tipoProcAggiud == "7" || (profiloSemplificato && (tipoProcAggiud==2 || tipoProcAggiud==4 || tipoProcAggiud == null))){
					showSezioneEstremiInvito(false);
				} else {
					showSezioneEstremiInvito(true);
				}
				if((profiloSemplificato && (tipoProcAggiud==2 || tipoProcAggiud==4 || tipoProcAggiud == null)) || tipoProcAggiud == "7"){
					showSezioneTerminiPresOfferta(false);
					showSezioneAperturaOfferte(false);
				}else{
					showSezioneTerminiPresOfferta(true);
					showSezioneAperturaOfferte(true);
					bloccoCampiSezTerminiPresOfferta(tipoProcAggiud,profiloSemplificato);
					bloccoCampiSezAperturaPlichi(tipoProcAggiud,profiloSemplificato);
				}
				
				
							
			} else {
				//if ("1".indexOf(tipoProcAggiud) >= 0){
				if (tipoProcAggiud == "1"){
					//alert("3.1");
					showSezioneTerminiPresDomandaPartecipa(false);
					showSezioneTerminiPresOfferta(true);
					showSezioneAperturaOfferte(true);
				//}else if ("5;13".indexOf(tipoProcAggiud) >= 0){
				}else if(tipoProcAggiud=="3" || tipoProcAggiud=="5"){
					//alert("3.3");
					showSezioneTerminiPresDomandaPartecipa(false);
					showSezioneTerminiPresOfferta(false);
					showSezioneAperturaOfferte(false);
				}else {
					//alert("3.2");
					showSezioneTerminiPresDomandaPartecipa(true);
					showSezioneTerminiPresOfferta(false);
					showSezioneAperturaOfferte(false);
				} 
			}
		}
		if(tipoProcAggiud==3 || tipoProcAggiud==5 || tipoProcAggiud == 6){
			showSezioneAvvisoBando(false);	
		}else{
			showSezioneAvvisoBando(true);
		}
	}
	
	function showSezioneAvvisoBando(visualizzaSezione){
		showObj("rowPUB",visualizzaSezione);
		showObj("rowTORN_NAVVIG", visualizzaSezione);
		showObj("rowTORN_DAVVIG", visualizzaSezione);
		showObj("rowTORN_DPUBAV", visualizzaSezione);
		showObj("rowTORN_DFPUBA", visualizzaSezione);
		showObj("rowTORN_DIBAND", visualizzaSezione);
		if (!visualizzaSezione) {
			setValue("TORN_NAVVIG", "");
			setValue("TORN_DAVVIG", "");
			setValue("TORN_DIBAND", "");
			setValue("TORN_DPUBAV", "");
			setValue("TORN_DFPUBA", "");
		}
	}

	function showSezioneTerminiPresDomandaPartecipa(visualizzaSezione){
		showObj("rowPDP",visualizzaSezione);
		showObj("rowTORN_DTEPAR", visualizzaSezione);
		showObj("rowTORN_OTEPAR", visualizzaSezione);
		showObj("rowTORN_DOCGAR", visualizzaSezione);
		showObj("rowTORN_DTERMRICHCDP", visualizzaSezione);
		showObj("rowTORN_DTERMRISPCDP", visualizzaSezione);
		if (!visualizzaSezione) {
			setValue("TORN_DTEPAR", "");
			setValue("TORN_OTEPAR", "");
			//setValue("TORN_LOCPRE", "");
			//setValue("TORN_DOCGAR", "");
			setValue("TORN_DTERMRICHCDP", "");
			setValue("TORN_DTERMRISPCDP", "");
		}
		puntiContattoSezioneTerminiPresDomandaPartecipa(visualizzaSezione);
		
	}
	
	
	function puntiContattoSezioneTerminiPresDomandaPartecipa(visualizzaSezione){
		if (!visualizzaSezione) {
			showObj("rowTORN_LOCPRE", false);
			showObj("rowPCOPRE_FIT", false);
			showObj("rowNOMPUN_PCOPRE", false);
			setValue("TORN_LOCPRE", "");
			setValue("TORN_PCOPRE", "");
			setValue("PCOPRE_FIT", "1");
			setValue("NOMPUN_PCOPRE", "");
			showObj("rowTORN_DOCGAR", false);
			showObj("rowPCODOC_FIT", false);
			showObj("rowNOMPUN_PCODOC", false);
			setValue("TORN_DOCGAR", "");
			setValue("TORN_PCODOC", "");
			setValue("PCODOC_FIT", "1");
			setValue("NOMPUN_PCODOC", "");
		}else{
			showObj("rowPCOPRE_FIT", true);
			var punti = getValue("TORN_PCOPRE");
			var luogo = getValue("TORN_LOCPRE");
			if(punti != ""){
				showObj("rowNOMPUN_PCOPRE", true);
				showObj("rowTORN_LOCPRE", false);
				setValue("TORN_LOCPRE", "");
			}else if(luogo!=""){
				showObj("rowTORN_LOCPRE", true);
				showObj("rowNOMPUN_PCOPRE", false);
				setValue("TORN_PCOPRE", "");
				setValue("NOMPUN_PCOPRE", "");
			}else{
				showObj("rowTORN_LOCPRE", false);
				showObj("rowNOMPUN_PCOPRE", false);
				setValue("TORN_LOCPRE", "");
				setValue("TORN_PCOPRE", "");
				setValue("NOMPUN_PCOPRE", "");
			}
			
			showObj("rowPCODOC_FIT", true);
			punti = getValue("TORN_PCODOC");
			luogo = getValue("TORN_DOCGAR");
			if(punti != ""){
				showObj("rowNOMPUN_PCODOC", true);
				showObj("rowTORN_DOCGAR", false);
				setValue("TORN_DOCGAR", "");
			}else if(luogo!=""){
				showObj("rowTORN_DOCGAR", true);
				showObj("rowNOMPUN_PCODOC", false);
				setValue("TORN_PCODOC", "");
				setValue("NOMPUN_PCODOC", "");
			}else{
				showObj("rowTORN_DOCGAR", false);
				showObj("rowNOMPUN_PCODOC", false);
				setValue("TORN_DOCGAR", "");
				setValue("TORN_PCODOC", "");
				setValue("NOMPUN_PCODOC", "");
			}
			
		}
	}
	
	/*
	function showCampiContattoSezioneTerminiPresDomandaPartecipa(valore,nomeCampo){
		if(valore == 2) {
			showObj("rowTORN_LOCPRE", false);
			showObj("rowNOMPUN_PCOPRE", true);
			setValue("TORN_LOCPRE", "");
		}else if(valore == 3){
			showObj("rowTORN_LOCPRE", true);
			showObj("rowNOMPUN_PCOPRE", false);
			setValue("TORN_PCOPRE", "");
			setValue("NOMPUN_PCOPRE", "");
		}else{
			showObj("rowTORN_LOCPRE", false);
			showObj("rowNOMPUN_PCOPRE", false);
			setValue("TORN_LOCPRE", "");
			setValue("TORN_PCOPRE", "");
		}
	}
	*/
	
	function showCampiContatto(valore,nomeCampo,nomeCampoFit){
		//Quando i campi PCOGAR_FIT e PCOOFF_FIT non sono visibili, non deve
		//scattare la gestione
		if((nomeCampoFit== "PCOGAR" && !$('#rowPCOGAR_FIT').is(':visible')) || (nomeCampoFit== "PCOOFF" && !$('#rowPCOOFF_FIT').is(':visible')))
        	return;
		if(valore == 2) {
			showObj("rowTORN_" + nomeCampo, false);
			showObj("rowNOMPUN_" + nomeCampoFit, true);
			setValue("TORN_" + nomeCampo, "");
		}else if(valore == 3){
			showObj("rowTORN_" + nomeCampo, true);
			showObj("rowNOMPUN_" + nomeCampoFit, false);
			setValue("TORN_" + nomeCampoFit, "");
			setValue("NOMPUN_" + nomeCampoFit, "");
		}else{
			showObj("rowTORN_" + nomeCampo, false);
			showObj("rowNOMPUN_" + nomeCampoFit, false);
			setValue("TORN_" + nomeCampo, "");
			setValue("TORN_" + nomeCampoFit, "");
		}
	}
	function showSezioneTerminiPresOfferta(visualizzaSezione){
		showObj("rowPDO", visualizzaSezione);
		showObj("rowTORN_DTEOFF", visualizzaSezione);
		showObj("rowTORN_OTEOFF", visualizzaSezione);
		//showObj("rowTORN_LOCOFF", visualizzaSezione);
		showObj("rowTORN_VALOFF", visualizzaSezione);
		showObj("rowTORN_DTERMRICHCPO", visualizzaSezione);
		showObj("rowTORN_DTERMRISPCPO", visualizzaSezione);
		
		showObj("rowDTEOFF_FIT", false);
		showObj("rowOTEOFF_FIT", false);
		showObj("rowLOCOFF_FIT", false);
		showObj("rowVALOFF_FIT", false);
		showObj("rowDTERMRICHCPO_FIT", false);
		showObj("rowDTERMRISPCPO_FIT", false);
		showObj("rowPCOOFF_FIT1", false);
		showObj("rowNOMPUN_PCOOFF_BLOCCATO", false);
		
		//if (!visualizzaSezione) {
			//setValue("TORN_DTEOFF", "");
			//setValue("TORN_OTEOFF", "");
			//setValue("TORN_LOCOFF", "");
			//setValue("TORN_VALOFF", "");
			//setValue("TORN_DTERMRICHCPO", "");
			//setValue("TORN_DTERMRISPCPO", "");
		//}
		puntiContattoSezioneTerminiPresDomandaOfferta(visualizzaSezione);
		
	}
	
	function puntiContattoSezioneTerminiPresDomandaOfferta(visualizzaSezione){
		if (!visualizzaSezione) {
			showObj("rowTORN_LOCOFF", false);
			showObj("rowPCOOFF_FIT", false);
			showObj("rowNOMPUN_PCOOFF", false);
			//setValue("TORN_LOCOFF", "");
			//setValue("TORN_PCOOFF", "");
			//setValue("PCOOFF_FIT", "1");
			//setValue("NOMPUN_PCOOFF", "");
		}else{
			showObj("rowPCOOFF_FIT", true);
			var punti = getValue("TORN_PCOOFF");
			var luogo = getValue("TORN_LOCOFF");
			if(punti != ""){
				showObj("rowNOMPUN_PCOOFF", true);
				showObj("rowTORN_LOCOFF", false);
				setValue("TORN_LOCOFF", "");
			}else if(luogo!=""){
				showObj("rowTORN_LOCOFF", true);
				showObj("rowNOMPUN_PCOOFF", false);
				setValue("TORN_PCOOFF", "");
				setValue("NOMPUN_PCOOFF", "");
			}else{
				showObj("rowTORN_LOCOFF", false);
				showObj("rowNOMPUN_PCOOFF", false);
				setValue("TORN_LOCOFF", "");
				setValue("TORN_PCOOFF", "");
				setValue("NOMPUN_PCOOFF", "");
			}
		}
	}

	function showSezioneAperturaOfferte(visualizzaSezione){
		showObj("rowOFF",visualizzaSezione);
		showObj("rowTORN_DESOFF", visualizzaSezione);
		showObj("rowTORN_OESOFF", visualizzaSezione);
		//showObj("rowTORN_LOCGAR", visualizzaSezione);
		
		showObj("rowDESOFF_FIT", false);
		showObj("rowOESOFF_FIT", false);
		showObj("rowLOCGAR_FIT", false);
		showObj("rowPCOGAR_FIT1", false);
		showObj("rowNOMPUN_PCOGAR_BLOCCATO", false);

		/*
		if (!visualizzaSezione) {
			setValue("TORN_DESOFF", "");
			setValue("TORN_OESOFF", "");
			setValue("TORN_LOCGAR", "");
		}
		*/
		puntiContattoSezioneAperturaOfferte(visualizzaSezione);
		
	}
	
	function puntiContattoSezioneAperturaOfferte(visualizzaSezione){
		if (!visualizzaSezione) {
			showObj("rowTORN_LOCGAR", false);
			showObj("rowPCOGAR_FIT", false);
			showObj("rowNOMPUN_PCOGAR", false);
			//setValue("TORN_LOCGAR", "");
			//setValue("TORN_PCOGAR", "");
			//setValue("PCOGAR_FIT", "1");
			//setValue("NOMPUN_PCOGAR", "");
		}else{
			showObj("rowPCOGAR_FIT", true);
			var punti = getValue("TORN_PCOGAR");
			var luogo = getValue("TORN_LOCGAR");
			if(punti != ""){
				showObj("rowNOMPUN_PCOGAR", true);
				showObj("rowTORN_LOCGAR", false);
				setValue("TORN_LOCGAR", "");
			}else if(luogo!=""){
				showObj("rowTORN_LOCGAR", true);
				showObj("rowNOMPUN_PCOGAR", false);
				setValue("TORN_PCOGAR", "");
				setValue("NOMPUN_PCOGAR", "");
			}else{
				showObj("rowTORN_LOCGAR", false);
				showObj("rowNOMPUN_PCOGAR", false);
				setValue("TORN_LOCGAR", "");
				setValue("TORN_PCOGAR", "");
				setValue("NOMPUN_PCOGAR", "");
			}
		}
	}
	
	
	
	function showSezioneEstremiInvito(visualizzaSezione){
		showObj("rowINVITO",visualizzaSezione);
		showObj("rowTORN_DINVIT", visualizzaSezione);
		showObj("rowTORN_NPROTI", visualizzaSezione);
			
	}
	
		
	function bloccoCampiSezTerminiPresOfferta(tipoProcAggiud,profiloSemplificato){
				
		var visualizzaCampi = true;
		if(tipoProcAggiud != 1)
			visualizzaCampi = false;
		
		
		if(profiloSemplificato && (tipoProcAggiud==3 || tipoProcAggiud ==5 || tipoProcAggiud == 6))
			visualizzaCampi = true;
					
		showObj("rowTORN_DTEOFF", visualizzaCampi);
		showObj("rowTORN_OTEOFF", visualizzaCampi);
		//showObj("rowTORN_LOCOFF", visualizzaCampi);
		showObj("rowTORN_VALOFF", visualizzaCampi);
		showObj("rowTORN_DTERMRICHCPO", visualizzaCampi);
		showObj("rowTORN_DTERMRISPCPO", visualizzaCampi);
		//showObj("rowPCOOFF_FIT", visualizzaCampi);
		//showObj("rowNOMPUN_PCOOFF", visualizzaCampi);
		
		showObj("rowDTEOFF_FIT", !visualizzaCampi);
		showObj("rowOTEOFF_FIT", !visualizzaCampi);
		//showObj("rowLOCOFF_FIT", !visualizzaCampi);
		showObj("rowVALOFF_FIT", !visualizzaCampi);
		showObj("rowDTERMRICHCPO_FIT", !visualizzaCampi);
		showObj("rowDTERMRISPCPO_FIT", !visualizzaCampi);
		//showObj("rowPCOOFF_FIT1", !visualizzaCampi);
		//showObj("rowNOMPUN_PCOOFF_BLOCCATO", !visualizzaCampi);
		setValue("LOCOFF_FIT", getValue("TORN_LOCOFF"));
		setValue("NOMPUN_PCOOFF_BLOCCATO", getValue("NOMPUN_PCOOFF"));
		
		if(visualizzaCampi == false){
			if(getValue("TORN_DTEOFF") != getOriginalValue("TORN_DTEOFF") )
				setValue("TORN_DTEOFF", "");
			if(getValue("TORN_OTEOFF") != getOriginalValue("TORN_OTEOFF") )
				setValue("TORN_OTEOFF", "");
			/*
			if(getValue("TORN_LOCOFF") != getOriginalValue("TORN_LOCOFF") )
				setValue("TORN_LOCOFF", "");
			*/
			
			if(getValue("TORN_VALOFF") != getOriginalValue("TORN_VALOFF") )
				setValue("TORN_VALOFF", "");
			if(getValue("TORN_DTERMRICHCPO") != getOriginalValue("TORN_DTERMRICHCPO") )
				setValue("TORN_DTERMRICHCPO", "");
			if(getValue("TORN_DTERMRISPCPO") != getOriginalValue("TORN_DTERMRISPCPO") )
				setValue("TORN_DTERMRISPCPO", "");
				
			showObj("rowTORN_LOCOFF", false);
			showObj("rowPCOOFF_FIT", false);
			showObj("rowNOMPUN_PCOOFF", false);
			
			showObj("rowPCOOFF_FIT1", true);
			var punti = getValue("TORN_PCOOFF");
			var luogo = getValue("TORN_LOCOFF");
			if(punti != ""){
				showObj("rowNOMPUN_PCOOFF_BLOCCATO", true);
				showObj("rowLOCOFF_FIT", false);
				valore = "Punto di contatto";
			}else if(luogo!=""){
				showObj("rowLOCOFF_FIT", true);
				showObj("rowNOMPUN_PCOOFF_BLOCCATO", false);
				valore = "Altro (specificare)";
			}else{
				showObj("rowLOCOFF_FIT", false);
				showObj("rowNOMPUN_PCOOFF_BLOCCATO", false);
				valore = "Stazione appaltante";
			}
			setValue("PCOOFF_FIT1",valore);
		}else{
			showObj("rowLOCOFF_FIT", false);
			showObj("rowPCOOFF_FIT1", false);
			showObj("rowNOMPUN_PCOOFF_BLOCCATO", false);
			
			showObj("rowPCOOFF_FIT", true);
			var punti = getValue("TORN_PCOOFF");
			var luogo = getValue("TORN_LOCOFF");
			if(punti != ""){
				showObj("rowNOMPUN_PCOOFF", true);
				showObj("rowTORN_LOCOFF", false);
				setValue("TORN_LOCOFF", "");
			}else if(luogo!=""){
				showObj("rowTORN_LOCOFF", true);
				showObj("rowNOMPUN_PCOOFF", false);
				setValue("TORN_PCOOFF", "");
				setValue("NOMPUN_PCOOFF", "");
			}else{
				showObj("rowTORN_LOCOFF", false);
				showObj("rowNOMPUN_PCOOFF", false);
				setValue("TORN_LOCOFF", "");
				setValue("TORN_PCOOFF", "");
				setValue("NOMPUN_PCOOFF", "");
			}
		}
		
	}
	
	function bloccoCampiSezAperturaPlichi(tipoProcAggiud,profiloSemplificato){
				
		var visualizzaCampi = true;
		if(tipoProcAggiud != 1)
			visualizzaCampi = false;
		
		
		if(profiloSemplificato && (tipoProcAggiud==3 || tipoProcAggiud ==5 || tipoProcAggiud == 6))
			visualizzaCampi = true;
		
		
		showObj("rowTORN_DESOFF", visualizzaCampi);
		showObj("rowTORN_OESOFF", visualizzaCampi);
		//showObj("rowTORN_LOCGAR", visualizzaCampi);
		
		showObj("rowDESOFF_FIT", !visualizzaCampi);
		showObj("rowOESOFF_FIT", !visualizzaCampi);
		//showObj("rowLOCGAR_FIT", !visualizzaCampi);
		
		setValue("LOCGAR_FIT", getValue("TORN_LOCGAR"));
		setValue("NOMPUN_PCOGAR_BLOCCATO", getValue("NOMPUN_PCOGAR"));
		
		if(visualizzaCampi == false){
			if(getValue("TORN_DESOFF") != getOriginalValue("TORN_DESOFF") )
				setValue("TORN_DESOFF", "");
			if(getValue("TORN_OESOFF") != getOriginalValue("TORN_OESOFF") )
				setValue("TORN_OESOFF", "");
			
			/*
			if(getValue("TORN_LOCGAR") != getOriginalValue("TORN_LOCGAR") )
				setValue("TORN_LOCGAR", "");
			*/
			showObj("rowTORN_LOCGAR", false);
			showObj("rowPCOGAR_FIT", false);
			showObj("rowNOMPUN_PCOGAR", false);
						
			showObj("rowPCOGAR_FIT1", true);
			var punti = getValue("TORN_PCOGAR");
			var luogo = getValue("TORN_LOCGAR");
			if(punti != ""){
				showObj("rowNOMPUN_PCOGAR_BLOCCATO", true);
				showObj("rowLOCGAR_FIT", false);
				valore = "Punto di contatto";
			}else if(luogo!=""){
				showObj("rowLOCGAR_FIT", true);
				showObj("rowNOMPUN_PCOGAR_BLOCCATO", false);
				valore = "Altro (specificare)";
			}else{
				showObj("rowLOCGAR_FIT", false);
				showObj("rowNOMPUN_PCOGAR_BLOCCATO", false);
				valore = "Stazione appaltante";
			}
			setValue("PCOGAR_FIT1",valore);
			
		}else{
			showObj("rowLOCGAR_FIT", false);
			showObj("rowPCOGAR_FIT1", false);
			showObj("rowNOMPUN_PCOGAR_BLOCCATO", false);
			
			showObj("rowPCOGAR_FIT", true);
			var punti = getValue("TORN_PCOGAR");
			var luogo = getValue("TORN_LOCGAR");
			if(punti != ""){
				showObj("rowNOMPUN_PCOGAR", true);
				showObj("rowTORN_LOCGAR", false);
				setValue("TORN_LOCGAR", "");
			}else if(luogo!=""){
				showObj("rowTORN_LOCGAR", true);
				showObj("rowNOMPUN_PCOGAR", false);
				setValue("TORN_PCOGAR", "");
				setValue("NOMPUN_PCOGAR", "");
			}else{
				showObj("rowTORN_LOCGAR", false);
				showObj("rowNOMPUN_PCOGAR", false);
				setValue("TORN_LOCGAR", "");
				setValue("TORN_PCOGAR", "");
				setValue("NOMPUN_PCOGAR", "");
			}
		}
		
	}
	
	function calcolaTermineMinimo(campo){
		var isGaraLottoUnico = "${param.isGaraLottoUnico }";
		var tipgen = getValue("TORN_TIPGEN");
		var tipgar;
		var importo;
		var oggcont;
		if(isGaraLottoUnico=="true"){
			tipgar = getValue("GARE_TIPGARG");
			importo = getValue("GARE_IMPAPP");
		}else{
			tipgar = getValue("TORN_TIPGAR");
			importo = getValue("TORN_IMPTOR");
		}
		var iterGara= getValue("TORN_ITERGA");
		var prourg = getValue("TORN_PROURG");
		var docweb = getValue("TORN_DOCWEB");
		var terrid = getValue("TORN_TERRID");
		var href = "href=gare/commons/popup-calcolaTermineMinimo.jsp";
		href += "&isGaraLottoUnico=" + isGaraLottoUnico;
		href += "&tipgen=" + tipgen;
		href += "&tipgar=" + tipgar;
		href += "&importo=" + importo;
		href += "&prourg=" + prourg;
		href += "&docweb=" + docweb;
		href += "&terrid=" + terrid;
		href += "&campo=" + campo;
		href += "&entita=TORN"; 
		href += "&faseInviti=No";
		href += "&iterGara=" + iterGara;
		openPopUpCustom(href, "calcolaTermineMinimo", 700, 600, 1, 1);
	}
	
	/*
	function valorizzaCampiPresso(){
		var punti = getValue("TORN_PCOPRE");
		var luogo = getValue("TORN_LOCPRE");
		var valore="";
		<c:choose>
			<c:when test="${modo eq 'VISUALIZZA' }">
				if(punti!="")
					valore = "Punto di contatto";
				else if(luogo!="")
					valore = "Altro (specificare)";
				else
					valore = "Stazione appaltante";	
			</c:when>
			<c:otherwise>
				if(punti!="")
					valore = 2;
				else if(luogo!="")
					valore =3;
				else
					valore = 1;
			</c:otherwise>
		</c:choose>
		setValue("PCOPRE_FIT",valore);
	}
	*/
	function valorizzaCampiPresso(punto,luogo,campoPresso){
		var valore="";
		<c:choose>
			<c:when test="${modo eq 'VISUALIZZA' }">
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
	
		
		
	var punto = getValue("TORN_PCOPRE");
	var luogo = getValue("TORN_LOCPRE");
	valorizzaCampiPresso(punto,luogo,"PCOPRE_FIT");
	
	punto = getValue("TORN_PCODOC");
	luogo = getValue("TORN_DOCGAR");
	valorizzaCampiPresso(punto,luogo,"PCODOC_FIT");
	
	punto = getValue("TORN_PCOOFF");
	luogo = getValue("TORN_LOCOFF");
	valorizzaCampiPresso(punto,luogo,"PCOOFF_FIT");
	
	punto = getValue("TORN_PCOGAR");
	luogo = getValue("TORN_LOCGAR");
	valorizzaCampiPresso(punto,luogo,"PCOGAR_FIT");
	
	function checkPresenzaCenint(valore){
		if(valore==2){
			var cenint = getValue("TORN_CENINT");
			if(cenint=="" || cenint == null)
				return false;
		}
		return true;
	}
	
	function aggiornaArchiviPuntiContatto(cenint){
		setValue("CENINT_PCOPRE",cenint);
		setValue("CENINT_PCODOC",cenint);	
		setValue("CENINT_PCOOFF",cenint);
		setValue("CENINT_PCOGAR",cenint);
	}
	
	function archivioPunticon(codice,num){
		var href = ("href=gene/punticon/punticon-scheda-popup.jsp&key=PUNTICON.CODEIN=T:" + codice + ";PUNTICON.NUMPUN=N:" + num);
		openPopUp(href, "schedaPunticon");
	}
	
	
	
	$("#rigaTabellaRettificaTerminiPartecipaz").hide();
	$("#tabellaRettificaTerminiPartecipaz").hide();
	
	$("#rigaTabellaRettificaTerminiOfferta").hide();
	$("#tabellaRettificaTerminiOfferta").hide();
	
	$("#rigaTabellaRettificaTerminiApertura").hide();
	$("#tabellaRettificaTerminiApertura").hide();
	
	storicoPartecipazioneCreato = false;
	storicoOffertaCreato = false;
	storicoAperturaPartecipazioneCreato = false;
	
	function showDettRetifica(tipo){
		var codgar=getValue("TORN_CODGAR");
		if(codgar==null || codgar=="")
			codgar=getValue("GARE_CODGAR1");
		var contextPath = "${contextPath}";
		if(tipo==1 && storicoPartecipazioneCreato==false){
			var visualizzaDataChiarimenti = "${gene:checkProt(pageContext, 'COLS.VIS.GARE.TORN.DTERMRICHCDP') }";
			var visualizzaRispostaChiarimenti = "${gene:checkProt(pageContext, 'COLS.VIS.GARE.TORN.DTERMRISPCDP') }";
			caricamentoStoricoRettificaTermini(codgar, tipo, contextPath,visualizzaDataChiarimenti,visualizzaRispostaChiarimenti);
			storicoPartecipazioneCreato=true;
		}else if(tipo==2 && storicoOffertaCreato==false){
			var visualizzaDataChiarimenti = "${gene:checkProt(pageContext, 'COLS.VIS.GARE.TORN.DTERMRICHCPO') }";
			var visualizzaRispostaChiarimenti = "${gene:checkProt(pageContext, 'COLS.VIS.GARE.TORN.DTERMRISPCPO') }";
			caricamentoStoricoRettificaTermini(codgar, tipo, contextPath,visualizzaDataChiarimenti,visualizzaRispostaChiarimenti);
			storicoOffertaCreato=true;
		}else if(tipo==3 && storicoAperturaPartecipazioneCreato==false){
			caricamentoStoricoRettificaTermini(codgar, tipo, contextPath,"false","false");
			storicoAperturaPartecipazioneCreato=true;
		}
				
		if(tipo==1){
			if ($('#tabellaRettificaTerminiPartecipaz ').is(':visible')) {  
				$("#rigaTabellaRettificaTerminiPartecipaz").hide();
				$('#tabellaRettificaTerminiPartecipaz ').hide();
				$('#aLinkVisualizzaDettaglioRettificaTerminiPartecipazione').text('Visualizza termini di gara precedenti alla rettifica');
			}else{
				$('#aLinkVisualizzaDettaglioRettificaTerminiPartecipazione').text('Nascondi termini di gara precedenti alla rettifica');
				$("#rigaTabellaRettificaTerminiPartecipaz").show();
				$('#tabellaRettificaTerminiPartecipaz').show();
			}
		}else if(tipo==2){
			if ($('#tabellaRettificaTerminiOfferta').is(':visible')) {  
				$('#tabellaRettificaTerminiOfferta').hide();
				$("#rigaTabellaRettificaTerminiOfferta").hide();
				$('#aLinkVisualizzaDettaglioRettificaTerminiOfferta').text('Visualizza termini di gara precedenti alla rettifica');
			}else{
				$('#aLinkVisualizzaDettaglioRettificaTerminiOfferta').text('Nascondi termini di gara precedenti alla rettifica');
				$('#tabellaRettificaTerminiOfferta').show();
				$("#rigaTabellaRettificaTerminiOfferta").show();
		   }
		}else if(tipo==3){
			if ($('#tabellaRettificaTerminiApertura').is(':visible')) {  
				$('#tabellaRettificaTerminiApertura').hide();
				$("#rigaTabellaRettificaTerminiApertura").hide();
				$('#aLinkVisualizzaDettaglioRettificaTerminiApertura').text('Visualizza termini di gara precedenti alla rettifica');
			}else{
				$('#aLinkVisualizzaDettaglioRettificaTerminiApertura').text('Nascondi termini di gara precedenti alla rettifica');
				$('#tabellaRettificaTerminiApertura').show();
				$("#rigaTabellaRettificaTerminiApertura").show();
		   }
		}
	}
	</gene:javaScript>