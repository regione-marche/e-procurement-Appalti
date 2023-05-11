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

<c:set var="codiceGara" value='${gene:getValCampo(keyParent, "DITG.CODGAR5")}' />
<c:set var="numeroGara" value='${gene:getValCampo(key, "V_GCAP_DPRE.NGARA")}' />
<c:set var="ditta" value='${gene:getValCampo(key, "V_GCAP_DPRE.COD_DITTA")}' />
<c:if test='${numeroGara eq ""}'>
	<c:set var="numeroGara" value='${gene:getValCampo(keyParent, "DITG.NGARA5")}' />
	<c:set var="ditta" value='${gene:getValCampo(keyParent, "DITG.DITTAO")}' />
</c:if>


<c:set var="codgar" value='DITG.CODGAR5=T:${gene:getValCampo(keyParent, "DITG.CODGAR5")}' />
<c:set var="genereGara" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipologiaGaraFunction",  pageContext,codgar)}' scope="request"/>


<c:set var="nomimo" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetNOMIMOFunction", pageContext,numeroGara,codiceGara,ditta)}' />
<c:set var="offtel" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetOFFTELFunction",  pageContext,codiceGara)}' scope="request"/>

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
	<c:when test='${not empty param.BLOCCO_AGG}'>
		<c:set var="BLOCCO_AGG" value="${param.BLOCCO_AGG}" />
	</c:when>
	<c:otherwise>
		<c:set var="BLOCCO_AGG" value="${BLOCCO_AGG}" />
	</c:otherwise>
</c:choose>


<c:choose>
	<c:when test='${not empty param.BLOCCO_AGG and param.BLOCCO_AGG eq "1"}' >
		<c:set var="bloccoAggiudicazione" value='1' />
	</c:when>
	<c:otherwise>
		<c:set var="bloccoAggiudicazione" value='0' />
	</c:otherwise>
</c:choose>


<c:set var="whereCampiGCAP" value='GCAP.NGARA = V_GCAP_DPRE.NGARA and GCAP.CONTAF = V_GCAP_DPRE.CONTAF' />
<c:set var="whereCampiDPRE" value='DPRE.NGARA = V_GCAP_DPRE.NGARA and DPRE.CONTAF = V_GCAP_DPRE.CONTAF and DPRE.DITTAO = V_GCAP_DPRE.COD_DITTA' />
<c:set var="whereCampiGCAP_SAN" value='GCAP_SAN.NGARA = V_GCAP_DPRE.NGARA and GCAP_SAN.CONTAF = V_GCAP_DPRE.CONTAF' />
<c:set var="whereCampiDPRE_SAN" value='DPRE_SAN.NGARA = V_GCAP_DPRE.NGARA and DPRE_SAN.CONTAF = V_GCAP_DPRE.CONTAF and DPRE_SAN.DITTAO = V_GCAP_DPRE.COD_DITTA' />

<c:set var="tipoFornitura" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipoFornitureFunction", pageContext, codiceGara)}' />

<c:set var="esisteIntegrazioneAUR" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneAURFunction",  pageContext)}' scope="request"/>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="V_GCAP_DPRE-scheda">
	
	<gene:setString name="titoloMaschera" value='${titolo}'/>
	<gene:redefineInsert name="corpo">

		<gene:formScheda entita="V_GCAP_DPRE" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreV_GCAP_DPRE"
			plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreInizializzazioniV_GCAP_DPRE">
			
			<gene:gruppoCampi>
				<gene:campoScheda>
						<td colspan="2"><b>Dati generali</b></td>
					</gene:campoScheda>
				<% // Campi della vista V_GCAP_DPRE %>
				<c:choose>
					<c:when test='${genereGara eq "3"}'>
						<gene:archivio titolo="lotti di gara"
							 lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.V_GCAP_DPRE.NGARA"),"gare/gcap/popup-lista-lotti.jsp","")}'
							 scheda=''
							 schedaPopUp=''
							 campi="V_GCAP_LOTTI.NGARA;V_GCAP_LOTTI.CODIGA;V_GCAP_LOTTI.NORVOC_MAX"
							 functionId="default"
							 parametriWhere="T:${codiceGara}"
							 chiave="V_GCAP_DPRE_NGARA" >
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
				
				<% // Campi chiave dell'entita GCAP_SAN %>
				<gene:campoScheda campo="NGARA"  entita="GCAP_SAN" where="${whereCampiGCAP_SAN}"  visibile="false" />
				<gene:campoScheda campo="CONTAF"  entita="GCAP_SAN" where="${whereCampiGCAP_SAN}" visibile="false"/>
								
				<% // Campi chiave dell'entita DPRE %>
				<gene:campoScheda campo="NGARA"  entita="DPRE" where="${whereCampiDPRE}"  visibile="false" value='${numeroGara}'/>
				<gene:campoScheda campo="CONTAF"  entita="DPRE" where="${whereCampiDPRE}" visibile="false"/>
				<gene:campoScheda campo="DITTAO"  entita="DPRE" where="${whereCampiDPRE}"  visibile="false" value='${ditta}'/>
				
				<% // Campi chiave dell'entita DPRE_SAN %>
				<gene:campoScheda campo="NGARA"  entita="DPRE_SAN" where="${whereCampiDPRE_SAN}"  visibile="false" />
				<gene:campoScheda campo="CONTAF"  entita="DPRE_SAN" where="${whereCampiDPRE_SAN}" visibile="false"/>
				<gene:campoScheda campo="DITTAO"  entita="DPRE_SAN" where="${whereCampiDPRE_SAN}"  visibile="false" />
						
				<gene:campoScheda campo="NORVOC"  entita="GCAP" title="Numero d'ordine" where="${whereCampiGCAP}" value='${gene:if(modo eq "NUOVO",newNorvoc,datiRiga.GCAP_NORVOC)}' modificabile='${modo eq "NUOVO" }'/>
				<gene:campoScheda campo="CODVOC"  entita="GCAP" where="${whereCampiGCAP}" modificabile='${modo eq "NUOVO" }' obbligatorio='${campoObbligatorio eq true}'/>
				<gene:campoScheda campo="VOCE"   entita="GCAP" where="${whereCampiGCAP}" modificabile='${modo eq "NUOVO" }'/>
				
				<gene:campoScheda campo="CODATC"  entita="GCAP_SAN" where="${whereCampiGCAP_SAN}" visibile='${tipoFornitura == "1"}' modificabile='${modo eq "NUOVO" }'/>
				<gene:campoScheda campo="CODAUR"  entita="GCAP_SAN" where="${whereCampiGCAP_SAN}" visibile='${tipoFornitura == "1"}' modificabile='${modo eq "NUOVO" }' obbligatorio='${esisteIntegrazioneAUR eq "1"}'/>
				<gene:campoScheda campo="PRINCATT"  entita="GCAP_SAN" where="${whereCampiGCAP_SAN}" visibile='${tipoFornitura == "1"}' modificabile='${modo eq "NUOVO" }'/>
				<gene:campoScheda campo="FORMAFARM"  entita="GCAP_SAN" where="${whereCampiGCAP_SAN}" visibile='${tipoFornitura == "1"}' modificabile='${modo eq "NUOVO" }'/>
				<gene:campoScheda campo="DOSAGGIO"  entita="GCAP_SAN" where="${whereCampiGCAP_SAN}" visibile='${tipoFornitura == "1"}' modificabile='${modo eq "NUOVO" }'/>
				<gene:campoScheda campo="VIASOMM"  entita="GCAP_SAN" where="${whereCampiGCAP_SAN}" visibile='${tipoFornitura == "1"}' modificabile='${modo eq "NUOVO" }'/>
				
				<gene:campoScheda campo="CODCLASS"  entita="GCAP_SAN" where="${whereCampiGCAP_SAN}" visibile='${tipoFornitura == "2"}' modificabile='${modo eq "NUOVO" }'/>
				<gene:campoScheda campo="CODAUR"  entita="GCAP_SAN" where="${whereCampiGCAP_SAN}" visibile='${tipoFornitura == "2"}' modificabile='${modo eq "NUOVO" }' obbligatorio='${esisteIntegrazioneAUR eq "1"}'/>
				<gene:campoScheda campo="DEPRODCN"  entita="GCAP_SAN" where="${whereCampiGCAP_SAN}" visibile='${tipoFornitura == "2"}' modificabile='${modo eq "NUOVO" }'/>
				<gene:campoScheda campo="DPRODCAP"  entita="GCAP_SAN" where="${whereCampiGCAP_SAN}" visibile='${tipoFornitura == "2"}' modificabile='${modo eq "NUOVO" }'/>
				
				<gene:campoScheda campo="UNIMIS"   entita="GCAP" where="${whereCampiGCAP}" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoUnitaMisura" visibile='${tipoFornitura == "1"}' modificabile='${modo eq "NUOVO" }'/>
				<gene:campoScheda campo="QUANTI" obbligatorio='${campoObbligatorio eq true}' title='${gene:if(tipoFornitura == "1" , "Fabbisogno relativo a 12 mesi in unità di misura", "Quantità/fabbisogni")}'  entita="GCAP" where="${whereCampiGCAP}" modificabile='${modo eq "NUOVO" }'/>
				<gene:campoScheda campo="PREZUN" title='${gene:if(tipoFornitura == "1" , "Base d\'asta unitaria (euro, iva esclusa)", "Prezzo unitario a base d\'asta")}' entita="GCAP" where="${whereCampiGCAP}"  modificabile='${modo eq "NUOVO" }'/>
				<gene:campoScheda campo="IMPORTO" entita="TORN" title="Importo" campoFittizio="true" definizione="F24.2;0;;MONEY5" modificabile="false" value="${importo}" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.GCAP.PREZUN") && tipoFornitura == "2"}'/>
				<gene:campoScheda campo="NOTE"  entita="GCAP_SAN" where="${whereCampiGCAP_SAN}" visibile='${tipoFornitura == "1"}' modificabile='${modo eq "NUOVO" }'/>

				<gene:campoScheda campo="FASGAR" title="Fase della Gara" entita="GARE" where="GARE.NGARA = V_GCAP_DPRE.NGARA" visibile="false"/>
				<gene:campoScheda campo="REQMIN"  entita="DPRE" where="${whereCampiDPRE}" visibile='${isPrequalifica && genereGara eq 3}' />
						
			</gene:gruppoCampi>
			
			<gene:gruppoCampi>
				<gene:campoScheda>
					<td colspan="2"><b>Dati fornitore</b></td>
				</gene:campoScheda>
				<gene:campoScheda campo="DENOMPROD"  title='${gene:if(tipoFrontitura == "1" , "Denominazione del prodotto", "Descrizione prodotto del fornitore")}' entita="DPRE_SAN" where="${whereCampiDPRE_SAN}" />
				<gene:campoScheda campo="CODAIC"  entita="DPRE_SAN" where="${whereCampiDPRE_SAN}" visibile='${tipoFornitura == "1" and isPrequalifica eq "true"}'/>
				<gene:campoScheda campo="NUNICONF"  entita="DPRE_SAN" where="${whereCampiDPRE_SAN}" visibile='${tipoFornitura == "1"}'/>
				<gene:campoScheda campo="CLASRESP"  entita="DPRE_SAN" where="${whereCampiDPRE_SAN}" visibile='${tipoFornitura == "1"}'/>
				<gene:campoScheda campo="PREZVPUBBL"  entita="DPRE_SAN" where="${whereCampiDPRE_SAN}" visibile='${tipoFornitura == "1"}'/>
				<gene:campoScheda campo="PSCONTOBL"  entita="DPRE_SAN" where="${whereCampiDPRE_SAN}" visibile='${tipoFornitura == "1"}'/>
				<gene:campoScheda campo="ESTRGUSO"  entita="DPRE_SAN" where="${whereCampiDPRE_SAN}" visibile='${tipoFornitura == "1"}'/>
				<gene:campoScheda campo="PREOFF"   entita="DPRE" where="${whereCampiDPRE}"  visibile='${tipoFornitura == "1"}'/>
				<gene:campoScheda campo="PREZUNRIF"  entita="DPRE_SAN" where="${whereCampiDPRE_SAN}" visibile='${tipoFornitura == "1"}'/>
				<gene:campoScheda campo="SCONTOFF"  entita="DPRE_SAN" where="${whereCampiDPRE_SAN}" visibile='${tipoFornitura == "1"}'/>
				<gene:campoScheda campo="SCONTOBBL"  entita="DPRE_SAN" where="${whereCampiDPRE_SAN}" visibile='${tipoFornitura == "1"}'/>
				<gene:campoScheda campo="SCONTAGG"  entita="DPRE_SAN" where="${whereCampiDPRE_SAN}" visibile='${tipoFornitura == "1"}'/>
				<gene:campoScheda campo="IMPOFF"   entita="DPRE" where="${whereCampiDPRE}" visibile='${tipoFornitura == "1"}'/>
				<gene:campoScheda campo="IVAPVPUBBL"  entita="DPRE_SAN" where="${whereCampiDPRE_SAN}" visibile='${tipoFornitura == "1"}'/>
				
				<gene:campoScheda campo="CODPROD"  entita="DPRE_SAN" where="${whereCampiDPRE_SAN}" visibile='${tipoFornitura == "2"}'/>
				<gene:campoScheda campo="NREPDISP"  entita="DPRE_SAN" where="${whereCampiDPRE_SAN}" visibile='${tipoFornitura == "2"}'/>
				<gene:campoScheda campo="REF"  entita="DPRE_SAN" where="${whereCampiDPRE_SAN}" visibile='${tipoFornitura == "2"}'/>
				<gene:campoScheda campo="QUANTICONF"  entita="DPRE_SAN" where="${whereCampiDPRE_SAN}" visibile='${tipoFornitura == "2"}'/>
				<gene:campoScheda campo="PREOFF"   entita="DPRE" where="${whereCampiDPRE}"  visibile='${tipoFornitura == "2"}'/>
				<gene:campoScheda campo="IMPOFF"   entita="DPRE" where="${whereCampiDPRE}" visibile='${tipoFornitura == "2"}'/>
				<gene:campoScheda campo="IVAPVPUBBL" entita="DPRE_SAN" where="${whereCampiDPRE_SAN}" visibile='${tipoFornitura == "2"}'/>
				<gene:campoScheda campo="NOTE"  entita="DPRE_SAN" where="${whereCampiDPRE_SAN}" visibile='${tipoFornitura == "2"}'/>
				
				<gene:campoScheda campo="ACQUISITO"  entita="DPRE_SAN" where="${whereCampiDPRE_SAN}" visibile='false'/>
			</gene:gruppoCampi>
			
			<input type="hidden" name="PREQUALIFICA" id="PREQUALIFICA" value="${PREQUALIFICA}" />
			<input type="hidden" name="BLOCCO_AGG" id="BLOCCO_AGG" value="${BLOCCO_AGG}" />	
			
			
			<c:if test='${tipoFornitura == "2"}'>
				<gene:fnJavaScriptScheda funzione='aggiornaImporto()' elencocampi='GCAP_QUANTI;GCAP_PREZUN' esegui="true" />
			</c:if>						
			
			<c:if test='${esisteIntegrazioneAUR eq "1" && (tipoFornitura eq "1" || tipoFornitura eq "2")}'>
				<gene:fnJavaScriptScheda funzione='valorizzaCODVOC()' elencocampi='GCAP_SAN_CODAUR' esegui="false" />
			</c:if>
			
			<gene:fnJavaScriptScheda funzione='aggiornaImportoTotale()' elencocampi='GCAP_QUANTI;DPRE_PREOFF' esegui="true" />
									
			<gene:campoScheda>
				<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-scheda.jsp">
					<jsp:param name="entita" value="V_GARE_TORN"/>
					<jsp:param name="inputFiltro" value="CODGAR=T:${codiceGara}"/>
					<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
				</jsp:include>
				<c:if test='${(bloccoAggiudicazione eq "1" and isPrequalifica eq "true") or offtel eq 1}'>
					<gene:redefineInsert name="schedaModifica"></gene:redefineInsert>
					<gene:redefineInsert name="pulsanteModifica"></gene:redefineInsert>
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
		
		//La funzione valorizza il campo calcolato IMPFF.DPRE
		//con QUANTI.GCAP*PREOFF.DPRE
		function aggiornaImportoTotale()
			{
				var QUANTI;
				var PREOFF;
				QUANTI = getValue("GCAP_QUANTI");
				PREOFF = getValue("DPRE_PREOFF");
				
				if (QUANTI == null || QUANTI == "" || PREOFF == null || PREOFF == "")
					setValue("DPRE_IMPOFF",  "");
				else {
					QUANTI = parseFloat(QUANTI);
					PREZUN = parseFloat(PREOFF);
					
					var temp = QUANTI * PREOFF;
					setValue("DPRE_IMPOFF",  round(eval(temp), 5));
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
			
			<c:if test='${esisteIntegrazioneAUR eq "1" && (tipoFornitura eq "1" || tipoFornitura eq "2")}'>
				showObj("rowGCAP_CODVOC",false);
				
				function valorizzaCODVOC(){
					var codaur = getValue("GCAP_SAN_CODAUR");
					setValue("GCAP_CODVOC",  codaur);
				}
			</c:if>	
	</gene:javaScript>
</gene:template>