<%
/*
 * Created on: 09/09/2009
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
<script type="text/javascript" src="${contextPath}/js/jquery.storico.variazioni.prezzo.js"></script>

<c:choose>
	<c:when test='${not empty param.lottoOffertaUnica}'>
		<c:set var="lottoOffertaUnica" value='${param.lottoOffertaUnica}'/>
	</c:when>
	<c:when test='${not empty lottoOffertaUnica}'>
		<c:set var="lottoOffertaUnica" value='${lottoOffertaUnica}'/>
	</c:when>
</c:choose>
<c:choose>
	<c:when test='${not empty param.codgar}'>
		<c:set var="codgar" value='${param.codgar}'/>
	</c:when>
	<c:when test='${not empty codgar}'>
		<c:set var="codgar" value='${codgar}'/>
	</c:when>
</c:choose>

<c:choose>
	<c:when test='${not empty param.fasgar}'>
		<c:set var="fasgar" value='${param.fasgar}'/>
	</c:when>
	<c:when test='${not empty fasgar}'>
		<c:set var="fasgar" value='${fasgar}'/>
	</c:when>
</c:choose>

<c:choose>
	<c:when test='${not empty param.ribcal}'>
		<c:set var="ribcal" value='${param.ribcal}'/>
	</c:when>
	<c:when test='${not empty ribcal}'>
		<c:set var="ribcal" value='${ribcal}'/>
	</c:when>
</c:choose>

<c:if test="${!lottoOffertaUnica }">
	<c:set var="genereGara" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipologiaGaraFunction",  pageContext,keyParent)}' scope="request"/>
</c:if>

<c:set var="ngara" value='${gene:getValCampo(key, "GCAP.NGARA")}' />
<c:set var="contaf" value='${gene:getValCampo(key, "GCAP.CONTAF")}' />

<%/*La pagina può essere richiamata anche da TORN per le gare divise a lotti con offerta unica*/ %>
<c:choose>
	<c:when test='${genereGara eq "3"}'>
	<%/*gara divisa a lotti con offerta unica*/ %>
		<c:set var="codiceGara" value='${gene:getValCampo(keyParent,"CODGAR")}' />
		<c:set var="titoloNuovo" value="Nuova lavorazione o fornitura della gara ${codiceGara}"/>
		<c:set var="entita" value='TORN_GCAP' />
		<c:set var="esisteGaraOLIAMM" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsisteGaraOLIAMMFunction",  pageContext,codiceGara)}' />
	</c:when>
	<c:otherwise>
		<c:set var="codiceGara" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetCodiceGaraFunction", pageContext)}' />
		<c:set var="entita" value='GCAP' />
		<c:set var="esisteGaraOLIAMM" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsisteGaraOLIAMMFunction",  pageContext,ngara)}' />
		<c:set var="esitoControlloAdesione" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsAdesioneAccordoQuadroConLavorazioniFunction", pageContext, ngara)}' />
		<c:if test="${esitoControlloAdesione eq 'true' }">
			<c:set var="aqoper" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetAqoperFunction", pageContext,ngaraaq,"GARE1")}' />
		</c:if>
	</c:otherwise>
</c:choose>


<c:if test="${aqoper ne 1 }">
	<c:set var="BloccoOfferteDitte" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.ControlloOfferteDitteFunction", pageContext, ngara,codiceGara)}' />
</c:if>
<c:set var="BloccoAggiudicazione" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.ControlloAggiudicazioneDefinitivaFunction", pageContext, ngara,codiceGara)}' />
<c:if test='${BloccoOfferteDitte eq "VERO" or BloccoAggiudicazione eq "VERO" or fasgar > 1}'>
	<c:set var="bloccoModifica" value="VERO"/>
</c:if>

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GetTIPGENFunction" parametro='${codiceGara}' />
<c:set var="tipoForniture" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipoFornitureFunction",  pageContext,codiceGara)}' />

<c:set var="esisteVariazionePrezzo" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.EsistonoVariazioniPrezzoFunction",  pageContext, ngara, contaf, "","2")}'/>

<c:set var="iterga" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetITERGAFunction", pageContext, codiceGara)}' />

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="GCAP-scheda">
	
	<c:choose>
		<c:when test="${tipoForniture ==98 }">
			<c:choose>
				<c:when test='${genereGara eq "3" and modo eq "NUOVO"}'>
					<gene:setString name="titoloMaschera" value='Nuovo prodotto della gara ${codiceGara}'/>
				</c:when>
				<c:otherwise>
					<gene:setString name="titoloMaschera" value="Prodotto della gara ${ngara}"/>
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<c:choose>
				<c:when test='${genereGara eq "3" and modo eq "NUOVO"}'>
					<gene:setString name="titoloMaschera" value='Nuova lavorazione o fornitura della gara ${codiceGara}'/>
				</c:when>
				<c:otherwise>
					<gene:setString name="titoloMaschera" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTitleFunction", pageContext, entita)}'/>
				</c:otherwise>
			</c:choose>
		</c:otherwise>
	</c:choose>
	
	
	<gene:redefineInsert name="corpo">

		<gene:formScheda entita="GCAP" gestisciProtezioni="true" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreInizializzazioniGcap" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreGCAP">
			<c:choose>
				<c:when test='${genereGara eq "3"}'>
					<gene:archivio titolo="lotti di gara"
						 lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.GCAP.NGARA"),"gare/gcap/popup-lista-lotti.jsp","")}'
						 scheda=''
						 schedaPopUp=''
						 campi="V_GCAP_LOTTI.NGARA;V_GCAP_LOTTI.CODIGA;V_GCAP_LOTTI.NORVOC_MAX"
						 functionId="default"
						 parametriWhere="T:${codiceGara}"
						 chiave="GCAP_NGARA" >
							<gene:campoScheda campo="NGARA" title="Codice lotto" obbligatorio="true" modificabile='${modo eq "NUOVO" }'/>
							<gene:campoScheda campo="CODIGA" title="Lotto" obbligatorio='${campoObbligatorio eq true}' entita="GARE" modificabile='${modo eq "NUOVO" }' where="GARE.NGARA = GCAP.NGARA"/>
							<gene:campoScheda campo="NORVOC_MAX" campoFittizio="true" visibile='false' definizione="F8.3"/>
					</gene:archivio>
					<gene:fnJavaScriptScheda funzione='aggiornaNORVOC()' elencocampi='NORVOC_MAX' esegui="true" />	
					
				</c:when>
				<c:otherwise>
					<gene:campoScheda campo="NGARA" visibile='false' value='${fn:substringAfter(keyParent, ":")}'/>
				</c:otherwise>
			</c:choose>
			
			<gene:campoScheda campo="CONTAF"  visibile="false"/>
			<gene:campoScheda campo="NORVOC"  title="Numero d'ordine" value='${gene:if(modo eq "NUOVO",newNorvoc,datiRiga.GCAP_NORVOC)}' modificabile='${ esisteGaraOLIAMM ne "true" and empty contafaq}'/>
			<gene:campoScheda campo="CODVOC"  obbligatorio="true" modificabile='${ esisteGaraOLIAMM ne "true" and empty contafaq}'/>
			<gene:campoScheda campo="VOCE"  modificabile='${ esisteGaraOLIAMM ne "true" and empty contafaq}'/>
			<gene:campoScheda campo="DESEST"  entita="GCAP_EST" where="GCAP_EST.NGARA = GCAP.NGARA and GCAP_EST.CONTAF=GCAP.CONTAF" modificabile='${ esisteGaraOLIAMM ne "true" and empty contafaq}'/>
			
			<c:choose>
				<c:when test="${modoAperturaScheda ne 'VISUALIZZA' and (empty datiRiga.GCAP_CODCAT or empty datiRiga.CAIS_TIPLAVG)}">
					<c:set var="parametriWhere" value="T:${tipoAppalto}" />
				</c:when>
				<c:otherwise>
					<c:set var="parametriWhere" value="T:${datiRiga.CAIS_TIPLAVG}" />
				</c:otherwise>
			</c:choose>
			<gene:archivio titolo="Categorie d'iscrizione"
				 lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.GCAP.CODCAT"),"gene/cais/lista-categorie-iscrizione-popup.jsp","")}'
				 scheda=''
				 schedaPopUp=''
				 campi="V_CAIS_TIT.CAISIM;V_CAIS_TIT.DESCAT;V_CAIS_TIT.TIPLAVG"
				 chiave="GCAP_CODCAT"
				 functionId="default"
				 parametriWhere="${parametriWhere}"
				 formName="formCategoriaMerceologica"
				 inseribile="false">
				 
					<gene:campoScheda campo="CODCAT"  defaultValue="${requestScope.initCODCAT}" />
					<gene:campoScheda campo="DESCAT" title="Descrizione categoria" entita="CAIS" where="GCAP.CODCAT=CAIS.CAISIM" 
						modificabile='${gene:checkProt(pageContext, "COLS.MOD.GARE.GCAP.CODCAT")}' 
						visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.GCAP.CODCAT")}' defaultValue="${requestScope.initDESCAT}"/>
					<gene:campoScheda campo="TIPLAVG" entita="CAIS" visibile="false" />	
			</gene:archivio>
			
			<gene:campoScheda campo="CLASI1"  obbligatorio="true" defaultValue="3" visibile='${tipgen eq 1 && tipoForniture ne 98}' modificabile='${ esisteGaraOLIAMM ne "true" and empty contafaq}'/>
			<gene:campoScheda campo="SOLSIC"  obbligatorio="true" visibile='${tipoForniture ne 98}' modificabile='${ esisteGaraOLIAMM ne "true" and empty contafaq}' defaultValue="2" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoNoSiSenzaNull"/>
			<gene:campoScheda campo="SOGRIB"  visibile="false" defaultValue="2" />
			<gene:campoScheda campo="SOGRIB_FIT" entita="TORN" campoFittizio="true" title="Soggetto a ribasso?" definizione="T2;0;;SN;G1SOGRIB" value="${datiRiga.GCAP_SOGRIB}" visibile='${tipoForniture ne 98}' modificabile='${ esisteGaraOLIAMM ne "true" and empty contafaq}' gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoSiNoInvertitoSenzaNull"/>
			<gene:campoScheda campo="UNIMIS"  obbligatorio="true" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoUnitaMisura" modificabile='${ esisteGaraOLIAMM ne "true" and empty contafaq}'/>
			<gene:campoScheda campo="QUANTI"  obbligatorio="true" modificabile='${ esisteGaraOLIAMM ne "true" }'/>
			<gene:campoScheda campo="CODCARR"  visibile='${integrazioneWSERP eq "1" && tipoWSERP eq "ATAC"}' modificabile='false'/>
			<gene:campoScheda  campo="CODRDA"  visibile='${integrazioneWSERP eq "1" && tipoWSERP eq "ATAC"}' modificabile='false'/>
			<gene:campoScheda campo="POSRDA"  visibile='${integrazioneWSERP eq "1" && tipoWSERP eq "ATAC"}' modificabile='false'/>
			<gene:campoScheda campo="PREZUN"  modificabile='${ esisteGaraOLIAMM ne "true" and empty contafaq}'/>
			<gene:campoScheda campo="IMPORTO" entita="TORN" title="Importo" campoFittizio="true" definizione="F24.2;0;;MONEY5" modificabile="false" value="${importo}" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.GCAP.PREZUN")}'/>
			<gene:campoScheda campo="PERCIVA" visibile='${(tipoForniture eq "3" or tipoForniture eq "")}' />
			<gene:campoScheda campo="IVAPROD"  visibile='${tipoForniture eq 98}' modificabile='${ esisteGaraOLIAMM ne "true"}'/>
			<gene:campoScheda campo="NUNICONF"  visibile='${tipoForniture eq 98}'/>
			<gene:campoScheda campo="CONTAFAQ"  visibile="false"/>
			<gene:campoScheda campo="LUOGOCONS" visibile='${integrazioneWSERP eq "1" && tipoWSERP eq "AVM"}'/>
			<gene:campoScheda campo="DATACONS"  title="Data prevista consegna" visibile='${(integrazioneWSERP eq "1" && tipoWSERP eq "AVM") || (iterga eq 8)}'/>
			<gene:campoScheda campo="PESO" visibile='${ribcal eq "3"}' />
						
			<gene:fnJavaScriptScheda funzione='aggiornaImporto()' elencocampi='GCAP_QUANTI;GCAP_PREZUN' esegui="false" />
			<gene:fnJavaScriptScheda funzione='aggiornaSogribDaSOLSIC()' elencocampi='GCAP_SOLSIC' esegui="false" />
			<gene:fnJavaScriptScheda funzione='aggiornaSogribDaCampoFittizio()' elencocampi='TORN_SOGRIB_FIT' esegui="false" />
			
			<c:if test="${esitoControlloAdesione eq 'true' and !empty contafaq }">
				<c:set var="ditta" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetDittaFunction", pageContext, ngaraaq)}' />
				<jsp:include page="/WEB-INF/pages/gare/v_gcap_dpre/sezione-attributi-generici-XDPRE.jsp">
					<jsp:param name="entitaParent" value="DPRE"/>
					<jsp:param name="gara" value="${ngaraaq}"/>
					<jsp:param name="joinWhere" value="XDPRE.XNGARA='${ngaraaq}' and XDPRE.XCONTAF=${contaf } and XDPRE.XDITTAO='${ditta }'"/>
				</jsp:include>
			</c:if>
			
			<gene:campoScheda>
				<td colspan="2"><c:if test="${esisteVariazionePrezzo eq 'true' }"> <a id="aLinkVisualizzaVariazionePrezzo" href="javascript:showDettVariazione();" class="link-generico">Visualizza prezzo unitario precedente alla variazione</a></c:if></td>
			</gene:campoScheda>
			<gene:campoScheda addTr="false" visibile="${esisteVariazionePrezzo eq 'true' }">
				<tr id="rigaTabellaVariazionePrezzo">
					<td colspan="2">
						<table id="tabellaVariazionePrezzo" class="griglia" >
							
			</gene:campoScheda>
				
				
			<gene:campoScheda addTr="false" visibile="${esisteVariazionePrezzo eq 'true' }">
							
							</table>
						<td>
					<tr>
			</gene:campoScheda>		
			
			<input type="hidden" name="lottoOffertaUnica" id="lottoOffertaUnica" value="${lottoOffertaUnica}"/>
			<input type="hidden" name="codgar" id="codgar" value="${codgar}"/>			
			<input type="hidden" name="esitoControlloAdesione" id="esitoControlloAdesione" value="${esitoControlloAdesione}"/>
			<input type="hidden" name="ditta" id="ditta" value="${ditta}"/>
			<input type="hidden" name="ngaraaq" id="ngaraaq" value="${ngaraaq}"/>
			<input type="hidden" name="aqoper" id="aqoper" value="${aqoper}"/>
			<input type="hidden" name="ribcal" id="ribcal" value="${ribcal}"/>
			
			<gene:campoScheda>
				<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-scheda.jsp">
					<jsp:param name="entita" value="V_GARE_TORN"/>
					<jsp:param name="inputFiltro" value="CODGAR=T:${codiceGara}"/>
					<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
				</jsp:include>
				<%// Se vi sono offerte delle ditte e non è già presente il blocco dato dalla G_PERMESSI, si elimina la possibilità di effettuare modifiche%>
				<c:if test='${(autorizzatoModifiche ne "2" && (bloccoModifica eq "VERO" || esisteGaraOLIAMM eq "true"))|| param.bloccoPubblicazione eq true || esitoControlloAdesione eq "true"}'>
					<c:if test='${(esisteGaraOLIAMM ne "true" && !(esitoControlloAdesione eq "true" && bloccoModifica ne "VERO"))|| param.bloccoPubblicazione eq true  }'>
						<gene:redefineInsert name="schedaModifica"></gene:redefineInsert>
						<gene:redefineInsert name="pulsanteModifica"></gene:redefineInsert>
					</c:if>
					<gene:redefineInsert name="pulsanteNuovo"></gene:redefineInsert>
					<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
				</c:if>
				<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
			</gene:campoScheda>
		</gene:formScheda>
	</gene:redefineInsert>
	<gene:javaScript>
		//La funzione valorizza il campo calcolato IMPORTO
		//con QUANTI.GCAP*PREZUN.GCAP
		function aggiornaImporto()
			{
				var QUANTI;
				var PREZUN;
				QUANTI = getValue("GCAP_QUANTI");
				PREZUN = getValue("GCAP_PREZUN");
				
				if (QUANTI == null || QUANTI == "" || PREZUN == null || PREZUN == "")
					setValue("TORN_IMPORTO",  "");
				else {
					QUANTI = parseFloat(QUANTI);
					PREZUN = parseFloat(PREZUN);
					
					var temp = QUANTI * PREZUN;
					setValue("TORN_IMPORTO",  round(eval(temp), 5));
				}
			}
			
			//quando SOLSIC viene impostato ad 1,
			//il campo SOGRIB deve essere impostato ad 1
			//e bloccato
			function aggiornaSogribDaSOLSIC(){
				var SOLSIC;
				SOLSIC = getValue("GCAP_SOLSIC");
				if(SOLSIC == '1') {
					setValue("GCAP_SOGRIB","1");
					setValue("TORN_SOGRIB_FIT","1");
					document.getElementById("TORN_SOGRIB_FIT").disabled = true;
				}
				else{
					document.getElementById("TORN_SOGRIB_FIT").disabled = false;
				}	
			}
			
			function aggiornaSogribDaCampoFittizio()
			{
				var SOGRIB_FIT;
				SOGRIB_FIT = getValue("TORN_SOGRIB_FIT");
				setValue("GCAP_SOGRIB",SOGRIB_FIT);
				<c:if test="${ribcal eq '3' }">
					if(SOGRIB_FIT == 1){
						showObj("rowGCAP_PESO",false);
						setValue("GCAP_PESO","");
					}else{
						showObj("rowGCAP_PESO",true);
					}
				</c:if>
			}
			
			<c:if test='${modo ne "VISUALIZZA"}'>
				function init(){
					var SOLSIC;
					SOLSIC = getValue("GCAP_SOLSIC");
					if(SOLSIC == '1') 
						document.getElementById("TORN_SOGRIB_FIT").disabled = true;
					
				}
				
				init();
			</c:if>
			
			<c:if test="${ribcal eq '3' }">
				var sogrib = getValue("GCAP_SOGRIB");
				if(sogrib == 1){
					showObj("rowGCAP_PESO",false);
						setValue("GCAP_PESO","");
				}else{
					showObj("rowGCAP_PESO",true);
				}
			</c:if>
			
			<c:if test='${genereGara eq "3"}'>
				//La funzione inizializza il campo NORVOC in base 
				//al lotto selezionato dall'archivio
				function aggiornaNORVOC(){
					var ngara = getValue("GCAP_NGARA");
					if(ngara!= null && ngara!= ""){
						var maxNorvoc = getValue("NORVOC_MAX");
						var norvoc = getValue("GCAP_NORVOC");
						var maxNorvoc_ceil;
										
						if (norvoc == null || norvoc == ""){
							if (maxNorvoc == null || maxNorvoc == "") 
								maxNorvoc = 1;
							else{
								maxNorvoc = parseFloat(maxNorvoc);
								maxNorvoc_ceil = Math.ceil(maxNorvoc);
								if (maxNorvoc == maxNorvoc_ceil) {
									maxNorvoc = maxNorvoc + 1;
								} else
									maxNorvoc = maxNorvoc_ceil;
							}		
							setValue("GCAP_NORVOC",maxNorvoc);	
						}
					}
				}
			</c:if>
			// Funzione per cambiare la condizione di where nell'apertura
			// dell'archivio delle categorie dell'appalto per la categoria merceologica
			function setTipoCategoriaPerArchivio(tipoCategoria){
				if(document.forms[0].modo.value != "VISUALIZZA"){
					if(getValue("GCAP_CODCAT") == "" || getValue("CAIS_TIPLAVG") == ""){
						setValue("CAIS_TIPLAVG", "" + tipoCategoria);
					}
				}
			}
			<c:if test='${modoAperturaScheda ne "VISUALIZZA"}'>
				setTipoCategoriaPerArchivio("${tipoAppalto}");
			</c:if>
			
			$("#rigaTabellaVariazionePrezzo").hide();
			$("#tabellaVariazionePrezzo").hide();
			
			storicoCreato = false;
						
			function showDettVariazione(){
				var ngara="${ngara}";
				var contaf="${contaf}";
				var contextPath = "${contextPath}";
				if(storicoCreato==false){
					caricamentoStoricoRettificaTermini(ngara, contaf, '', contextPath);
					storicoCreato=true;
				}
				
				if ($('#tabellaVariazionePrezzo').is(':visible')) {  
					$("#rigaTabellaVariazionePrezzo").hide();
					$('#tabellaVariazionePrezzo').hide();
					$('#aLinkVisualizzaVariazionePrezzo').text('Visualizza prezzo unitario precedente alla variazione');
				}else{
					$('#aLinkVisualizzaVariazionePrezzo').text('Nascondi prezzo unitario precedente alla variazione');
					$("#rigaTabellaVariazionePrezzo").show();
					$('#tabellaVariazionePrezzo').show();
				}
			}
			
	</gene:javaScript>
</gene:template>