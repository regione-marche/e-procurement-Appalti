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

<jsp:include page="/WEB-INF/pages/gare/gare/fasiGara/defStepWizardFasiGara.jsp" />

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<script type="text/javascript" src="${contextPath}/js/jquery.storico.variazioni.prezzo.js"></script>

<c:set var="codiceGara" value='${gene:getValCampo(keyParent, "DITG.CODGAR5")}' />
<c:set var="numeroGara" value='${gene:getValCampo(key, "V_GCAP_DPRE.NGARA")}' />
<c:set var="ditta" value='${gene:getValCampo(key, "V_GCAP_DPRE.COD_DITTA")}' />
<c:if test='${numeroGara eq ""}'>
	<c:set var="numeroGara" value='${gene:getValCampo(keyParent, "DITG.NGARA5")}' />
	<c:set var="ditta" value='${gene:getValCampo(keyParent, "DITG.DITTAO")}' />
</c:if>
<c:set var="contaf" value='${gene:getValCampo(key, "V_GCAP_DPRE.CONTAF")}' />


<c:set var="codgar" value='DITG.CODGAR5=T:${gene:getValCampo(keyParent, "DITG.CODGAR5")}' />
<c:set var="genereGara" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipologiaGaraFunction",  pageContext,codgar)}' scope="request"/>
<c:set var="offtel" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetOFFTELFunction",  pageContext,codiceGara)}' scope="request"/>


<c:set var="nomimo" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetNOMIMOFunction", pageContext,numeroGara,codiceGara,ditta)}' />

<c:if test='${genereGara eq 3 and modo eq "NUOVO"}'>
	<c:set var="numeroGara" value='' />
	<c:set var="newNorvoc" value='' />
</c:if>

<c:choose>
	<c:when test='${modo eq "NUOVO"}'>
		<c:set var="titolo" value="Nuova lavorazione o fornitura definita dalla ditta ${nomimo}"/>
	</c:when>
	<c:otherwise>
		<c:set var="titolo" value="Prezzo offerto per la lavorazione o fornitura dalla ditta ${nomimo}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.PREQUALIFICA}'>
		<c:set var="PREQUALIFICA" value="${param.PREQUALIFICA}" />
	</c:when>
	<c:otherwise>
		<c:set var="PREQUALIFICA" value="${PREQUALIFICA}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty PREQUALIFICA and PREQUALIFICA eq "true"}' >
		<c:set var="isPrequalifica" value='true' />
	</c:when>
	<c:otherwise>
		<c:set var="isPrequalifica" value='false' />
	</c:otherwise>
</c:choose>


<c:choose>
	<c:when test='${not empty param.RIBCAL}'>
		<c:set var="RIBCAL" value="${param.RIBCAL}" />
	</c:when>
	<c:otherwise>
		<c:set var="RIBCAL" value="${RIBCAL}" />
	</c:otherwise>
</c:choose>


<c:choose>
	<c:when test='${not empty param.BLOCCO_AGG}'>
		<c:set var="BLOCCO_AGG" value="${param.BLOCCO_AGG}" />
	</c:when>
	<c:otherwise>
		<c:set var="BLOCCO_AGG" value="${BLOCCO_AGG}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.BLOCCO_VARPREZZI}'>
		<c:set var="BLOCCO_VARPREZZI" value="${param.BLOCCO_VARPREZZI}" />
	</c:when>
	<c:otherwise>
		<c:set var="BLOCCO_VARPREZZI" value="${BLOCCO_VARPREZZI}" />
	</c:otherwise>
</c:choose>


<c:choose>
	<c:when test='${not empty BLOCCO_AGG and BLOCCO_AGG eq "1"}' >
		<c:set var="bloccoAggiudicazione" value='1' />
	</c:when>
	<c:otherwise>
		<c:set var="bloccoAggiudicazione" value='0' />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.stepWizard}'>
		<c:set var="stepWizard" value="${param.stepWizard}" />
	</c:when>
	<c:otherwise>
		<c:set var="stepWizard" value="${stepWizard}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.isGaraTelematica}'>
		<c:set var="isGaraTelematica" value="${param.isGaraTelematica}" />
	</c:when>
	<c:otherwise>
		<c:set var="isGaraTelematica" value="${isGaraTelematica}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.faseGara}'>
		<c:set var="faseGara" value="${param.faseGara}" />
	</c:when>
	<c:otherwise>
		<c:set var="faseGara" value="${faseGara}" />
	</c:otherwise>
</c:choose>

<c:set var="whereCampiGCAP" value='GCAP.NGARA = V_GCAP_DPRE.NGARA and GCAP.CONTAF = V_GCAP_DPRE.CONTAF' />
<c:set var="whereCampiDPRE" value='DPRE.NGARA = V_GCAP_DPRE.NGARA and DPRE.CONTAF = V_GCAP_DPRE.CONTAF and DPRE.DITTAO = V_GCAP_DPRE.COD_DITTA' />

<c:if test="${modo ne 'MODIFICA'}">
	<c:set var="esisteVariazionePrezzo" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.EsistonoVariazioniPrezzoFunction",  pageContext, numeroGara, contaf, ditta,"2")}'/>
</c:if>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="V_GCAP_DPRE-scheda">
	
	<gene:setString name="titoloMaschera" value='${titolo}'/>
	<gene:redefineInsert name="corpo">

		<gene:formScheda entita="V_GCAP_DPRE" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreV_GCAP_DPRE"
			plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreInizializzazioniV_GCAP_DPRE">
			
			<% // Campi della vista V_GCAP_DPRE %>
			<c:choose>
				<c:when test='${genereGara eq "3"}'>
					<gene:archivio titolo="lotti di gara"
						 lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.V_GCAP_DPRE.NGARA"),"gare/gcap/popup-lista-lotti.jsp","")}'
						 scheda=''
						 schedaPopUp=''
						 campi="V_GCAP_LOTTI.NGARA;V_GCAP_LOTTI.CODIGA;V_GCAP_LOTTI.NORVOC_MAX"
						 chiave="V_GCAP_DPRE_NGARA"
						 where="V_GCAP_LOTTI.CODGAR = '${codiceGara }'">
							<gene:campoScheda campo="NGARA" title="Codice lotto" obbligatorio="true" modificabile='${modo eq "NUOVO" }'/>
							<gene:campoScheda campo="CODIGA" title="Lotto" obbligatorio='${campoObbligatorio eq true}' entita="GARE" modificabile='${modo eq "NUOVO" }' where="GARE.NGARA = V_GCAP_DPRE.NGARA"/>
							<gene:campoScheda campo="NORVOC_MAX" campoFittizio="true" visibile='false' definizione="F8.3"/>
					</gene:archivio>
					<gene:fnJavaScriptScheda funzione='aggiornaDaNGARA()' elencocampi='NORVOC_MAX' esegui="false" />	
				</c:when>
				<c:otherwise>
					<gene:campoScheda campo="NGARA" visibile="false" value='${numeroGara}'/>
				</c:otherwise>
			</c:choose>
			
			<gene:campoScheda campo="CODGAR"  visibile="false" value='${codiceGara}'/>
			<gene:campoScheda campo="CONTAF"  visibile="false"/>
			<gene:campoScheda campo="COD_DITTA"  visibile="false" value='${ditta}'/>
			<gene:campoScheda campo="ISSOLODITTA"  visibile="false"/>
			
			<% // Campi chiave dell'entita GCAP %>
			<gene:campoScheda campo="NGARA"  entita="GCAP" where="${whereCampiGCAP}"  visibile="false" value='${numeroGara}'/>
			<gene:campoScheda campo="CONTAF"  entita="GCAP" where="${whereCampiGCAP}" visibile="false"/>
			<gene:campoScheda campo="DITTAO"  entita="GCAP" where="${whereCampiGCAP}"  visibile="false" value='${ditta}'/>
			
			<% // Campi chiave dell'entita DPRE %>
			<gene:campoScheda campo="NGARA"  entita="DPRE" where="${whereCampiDPRE}"  visibile="false" value='${numeroGara}'/>
			<gene:campoScheda campo="CONTAF"  entita="DPRE" where="${whereCampiDPRE}" visibile="false"/>
			<gene:campoScheda campo="DITTAO"  entita="DPRE" where="${whereCampiDPRE}"  visibile="false" value='${ditta}'/>

			<gene:campoScheda campo="FASGAR" title="Fase della Gara" entita="GARE" where="GARE.NGARA = V_GCAP_DPRE.NGARA" visibile="false"/>

			<gene:campoScheda campo="NORVOC"  entita="GCAP" title="Numero d'ordine" where="${whereCampiGCAP}" modificabile='${gene:if(datiRiga.V_GCAP_DPRE_ISSOLODITTA ne "1" and modo ne "NUOVO",false,true)}' value='${gene:if(modo eq "NUOVO",newNorvoc,datiRiga.GCAP_NORVOC)}'/>
			<gene:campoScheda campo="CODVOC"  obbligatorio='${campoObbligatorio eq true}' entita="GCAP" where="${whereCampiGCAP}" modificabile='${gene:if(datiRiga.V_GCAP_DPRE_ISSOLODITTA ne "1" and modo ne "NUOVO",false,true)}'/>
			<gene:campoScheda campo="VOCE"   entita="GCAP" where="${whereCampiGCAP}" modificabile='${gene:if(datiRiga.V_GCAP_DPRE_ISSOLODITTA ne "1" and modo ne "NUOVO",false,true)}'/>
			<gene:campoScheda campo="DESEST"  entita="GCAP_EST" where="GCAP_EST.NGARA = V_GCAP_DPRE.NGARA and GCAP_EST.CONTAF=V_GCAP_DPRE.CONTAF" modificabile='${gene:if(datiRiga.V_GCAP_DPRE_ISSOLODITTA ne "1" and modo ne "NUOVO",false,true)}'/>

			<gene:campoScheda campo="CODCAT"  entita="GCAP" where="${whereCampiGCAP}" visibile='${gene:if(datiRiga.V_GCAP_DPRE_ISSOLODITTA ne "1" and modo ne "NUOVO",true,false)}' modificabile='false'/>
			<gene:campoScheda campo="DESCAT"  title="Descrizione categoria" entita="CAIS" from="GCAP" where="GCAP.CODCAT=CAIS.CAISIM and ${whereCampiGCAP}" visibile='${gene:if(datiRiga.V_GCAP_DPRE_ISSOLODITTA ne "1" and modo ne "NUOVO" and gene:checkProt(pageContext, "COLS.VIS.GARE.GCAP.CODCAT"),true,false)}' modificabile='false'/>

			<gene:campoScheda campo="CLASI1"   entita="GCAP" where="${whereCampiGCAP}" modificabile='${gene:if(datiRiga.V_GCAP_DPRE_ISSOLODITTA ne "1" and modo ne "NUOVO",false,true)}' obbligatorio="true" defaultValue="3" visibile='${tipgen eq 1}'/>
			<gene:campoScheda campo="SOLSIC"   entita="GCAP" where="${whereCampiGCAP}" modificabile="false" defaultValue='2'/>
			<gene:campoScheda campo="SOGRIB"   entita="GCAP" title="Soggetto a ribasso?" where="${whereCampiGCAP}" modificabile="false" defaultValue='2' gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoSiNoInvertitoSenzaNull"/>
			<gene:campoScheda campo="UNIMIS"   entita="GCAP" where="${whereCampiGCAP}" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoUnitaMisura" modificabile='${gene:if(datiRiga.V_GCAP_DPRE_ISSOLODITTA ne "1" and modo ne "NUOVO",false,true)}'/>
			<gene:campoScheda campo="QUANTI"   entita="GCAP" where="${whereCampiGCAP}" modificabile="false" title="Quantità" visibile='${gene:if(datiRiga.V_GCAP_DPRE_ISSOLODITTA ne "1" and (datiRiga.GCAP_SOLSIC ne "1" and datiRiga.GCAP_SOGRIB ne "1" and datiRiga.GCAP_CLASI1 eq "1"),true,false)}'/>
			<gene:campoScheda campo="QUANTI"   entita="DPRE" where="${whereCampiDPRE}" visibile="false"/>
			<gene:campoScheda campo="QUANTI_F" entita="V_GCAP_DPRE" title='${gene:if(datiRiga.V_GCAP_DPRE_ISSOLODITTA ne "1" and (datiRiga.GCAP_SOLSIC ne "1" and datiRiga.GCAP_SOGRIB ne "1" and datiRiga.GCAP_CLASI1 eq "1"),"Quantità eventualmente modificata dalla ditta","Quantità")}' campoFittizio="true" definizione="F12.3;0;;;G1QUANTI_P" value='${gene:if(datiRiga.DPRE_QUANTI ne "",datiRiga.DPRE_QUANTI,datiRiga.GCAP_QUANTI)}' modificabile='${gene:if(datiRiga.V_GCAP_DPRE_ISSOLODITTA ne "1" and !(datiRiga.GCAP_SOLSIC ne "1" and datiRiga.GCAP_SOGRIB ne "1" and datiRiga.GCAP_CLASI1 eq "1" )and modo ne "NUOVO",false,true)}' obbligatorio='${campoObbligatorio eq true and !(datiRiga.V_GCAP_DPRE_ISSOLODITTA ne "1" and !(datiRiga.GCAP_SOLSIC ne "1" and datiRiga.GCAP_SOGRIB ne "1" and datiRiga.GCAP_CLASI1 eq "1" )and modo ne "NUOVO")}'/>
			<gene:campoScheda campo="PERRIB"   entita="DPRE" where="${whereCampiDPRE}"  modificabile='false' visibile='${RIBCAL eq "3" && datiRiga.GCAP_SOGRIB eq "2"}'/>
			<gene:campoScheda campo="PREOFF"   entita="DPRE" where="${whereCampiDPRE}"  modificabile='${gene:if(datiRiga.V_GCAP_DPRE_ISSOLODITTA ne "1" and !(datiRiga.GCAP_SOLSIC ne "1" and datiRiga.GCAP_SOGRIB ne "1") and modo ne "NUOVO" ,false,true)}' visibile='${!(datiRiga.GCAP_SOLSIC eq "1" or datiRiga.GCAP_SOGRIB eq "1")}'/>
			<gene:campoScheda campo="PERCIVA"   entita="GCAP" where="${whereCampiGCAP}" modificabile="false" visibile="false"/>
			<gene:campoScheda campo="PERCIVA"   entita="DPRE" where="${whereCampiDPRE}" visibile="false"/>
			<gene:campoScheda campo="PERCIVA_F" title="Aliquota IVA" entita="V_GCAP_DPRE" campoFittizio="true" definizione="N2;0;;PRC;G1PERCIVA_P" value='${gene:if(datiRiga.DPRE_PERCIVA ne "",datiRiga.DPRE_PERCIVA,datiRiga.GCAP_PERCIVA)}' visibile ='${gene:checkProt(pageContext, "COLS.VIS.GARE.GCAP.PERCIVA")}' />
			<gene:campoScheda campo="IMPOFF"   entita="DPRE" where="${whereCampiDPRE}" visibile="false"/>
			<gene:campoScheda campo="IMPOFF_FIT" entita="V_GCAP_DPRE" campoFittizio="true" definizione="T2;0;;MONEY5;G1_IMPOFD" modificabile="false" value="${datiRiga.DPRE_IMPOFF}" visibile='${!(datiRiga.GCAP_SOLSIC eq "1" or datiRiga.GCAP_SOGRIB eq "1")}'/>
			<gene:campoScheda campo="PREZUN"  entita="GCAP" where="${whereCampiGCAP}"  visibile='${datiRiga.GCAP_SOLSIC eq "1" or datiRiga.GCAP_SOGRIB eq "1"}' />
			<gene:campoScheda campo="IMPORTO_GCAP" campoFittizio="true" definizione="F24.2;0;;MONEY5;G1_IMPOFD" modificabile="false" value="${importo}" visibile='${datiRiga.GCAP_SOLSIC eq "1" or datiRiga.GCAP_SOGRIB eq "1"}'/>
			<gene:campoScheda campo="PESO" entita="GCAP" where="${whereCampiGCAP}" modificabile='false' visibile='${RIBCAL eq "3" && datiRiga.GCAP_SOGRIB eq "2"}'/>
			<gene:campoScheda campo="RIBPESO" entita="DPRE" where="${whereCampiDPRE}" modificabile='false' visibile='${RIBCAL eq "3" && datiRiga.GCAP_SOGRIB eq "2"}'/>
			<gene:campoScheda campo="LUOGOCONS" entita="GCAP" where="${whereCampiGCAP}" visibile='${integrazioneWSERP eq "1" && tipoWSERP eq "AVM"}' modificabile="true"/>
			<gene:campoScheda campo="DATACONS"  entita="GCAP" where="${whereCampiGCAP}" title="Data prevista consegna" visibile='${integrazioneWSERP eq "1" && tipoWSERP eq "AVM"}' modificabile="true"/>
			<gene:campoScheda campo="REQMIN"  entita="DPRE" where="${whereCampiDPRE}" visibile="${(isPrequalifica && genereGara eq 3) || stepWizard eq step6Wizard}" />

			<input type="hidden" name="PREQUALIFICA" id="PREQUALIFICA" value="${PREQUALIFICA}" />
			<input type="hidden" name="BLOCCO_AGG" id="BLOCCO_AGG" value="${BLOCCO_AGG}" />	
			<input type="hidden" name="stepWizard" id="stepWizard" value="${stepWizard}" />
			<input type="hidden" name="isGaraTelematica" id="isGaraTelematica" value="${isGaraTelematica}" />
			<input type="hidden" name="faseGara" id="faseGara" value="${faseGara}" />
			<input type="hidden" name="BLOCCO_VARPREZZI" id="BLOCCO_VARPREZZI" value="${BLOCCO_VARPREZZI}" />	
			
			<input type="hidden" name="RIBCAL" id="RIBCAL" value="${RIBCAL}" />
			
			<gene:fnJavaScriptScheda funzione='aggiornaImportoTotale()' elencocampi='V_GCAP_DPRE_QUANTI_F;DPRE_PREOFF' esegui="false" />
			
			<jsp:include page="sezione-attributi-generici-XDPRE.jsp">
				<jsp:param name="entitaParent" value="DPRE"/>
				<jsp:param name="gara" value="${numeroGara}"/>
				<jsp:param name="joinWhere" value="V_GCAP_DPRE.NGARA = XDPRE.XNGARA and V_GCAP_DPRE.CONTAF = XDPRE.XCONTAF and V_GCAP_DPRE.COD_DITTA = XDPRE.XDITTAO"/>
			</jsp:include>
			
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
									
			<gene:campoScheda>
				<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-scheda.jsp">
					<jsp:param name="entita" value="V_GARE_TORN"/>
					<jsp:param name="inputFiltro" value="CODGAR=T:${codiceGara}"/>
					<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
				</jsp:include>
				<%// Se GCAP.SOLSIC=1 o GCAP.SOGRIB=1 e non è già presente il blocco dato dalla G_PERMESSI, si elimina la possibilità di effettuare modifiche%>
				<c:if test='${ autorizzatoModifiche ne "2" && (datiRiga.GCAP_SOLSIC eq 1 or datiRiga.GCAP_SOGRIB eq 1) or (bloccoAggiudicazione eq "1" and isPrequalifica eq "true") or offtel eq 1
						or (isGaraTelematica and stepWizard eq step6Wizard and faseGara ne 5) or BLOCCO_VARPREZZI eq "true"}'>
					<gene:redefineInsert name="schedaModifica"></gene:redefineInsert>
					<gene:redefineInsert name="pulsanteModifica"></gene:redefineInsert>
				</c:if>
				<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
			</gene:campoScheda>
		</gene:formScheda>
	</gene:redefineInsert>
	<gene:javaScript>
		//La funzione valorizza il campo calcolato IMPOFF
		//con QUANTI_F * PREOFF
		function aggiornaImportoTotale()
			{
				var QUANTI_F;
				var PREOFF;
				QUANTI_F = getValue("V_GCAP_DPRE_QUANTI_F");
				PREOFF = getValue("DPRE_PREOFF");
				
				if (QUANTI_F == null || QUANTI_F == "" || PREOFF == null || PREOFF == ""){
					setValue("DPRE_IMPOFF",  "");
					setValue("V_GCAP_DPRE_IMPOFF_FIT",  "");
				}else {
					QUANTI_F = parseFloat(QUANTI_F);
					PREOFF = parseFloat(PREOFF);
					
					var temp = QUANTI_F * PREOFF;
					setValue("DPRE_IMPOFF",  round(eval(temp), 5));
					setValue("V_GCAP_DPRE_IMPOFF_FIT",  round(eval(temp), 5));
				}
			}
		
		<c:if test='${genereGara eq "3"}'>
				//La funzione inizializza il campo NORVOC in base 
				//al lotto selezionato dall'archivio
				function aggiornaDaNGARA(){
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
					
					//imposto il codice lotto selezionato sui campi
					//NGARA.GCAP e NGARA.DPRE
					var codiceLotto = getValue("V_GCAP_DPRE_NGARA");
					setValue("GCAP_NGARA",codiceLotto);
					setValue("DPRE_NGARA",codiceLotto);
				}
			</c:if>
			
			$("#rigaTabellaVariazionePrezzo").hide();
			$("#tabellaVariazionePrezzo").hide();
			
			storicoCreato = false;
						
			function showDettVariazione(){
				var ngara="${numeroGara}";
				var contaf="${contaf}";
				var ditta="${ditta }";
				var contextPath = "${contextPath}";
				if(storicoCreato==false){
					caricamentoStoricoRettificaTermini(ngara, contaf, ditta, contextPath);
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