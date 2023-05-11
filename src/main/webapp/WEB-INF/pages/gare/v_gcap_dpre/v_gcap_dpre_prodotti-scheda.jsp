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


<c:if test='${genereGara eq 3}'>
	<c:set var="parametro" value="${codiceGara}" />
</c:if>
<c:if test='${genereGara ne 3}'>
	<c:set var="parametro" value="${numeroGara}" />
</c:if>
<c:set var="esisteGaraOLIAMM" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsisteGaraOLIAMMFunction",  pageContext,parametro)}' />


<c:if test='${genereGara eq 3 and modo eq "NUOVO"}'>
	<c:set var="numeroGara" value='' />
	<c:set var="newNorvoc" value='' />
</c:if>

<c:choose>
	<c:when test='${modo eq "NUOVO"}'>
		<c:set var="titolo" value="Nuovo prodotto definito dalla ditta ${nomimo}"/>
	</c:when>
	<c:otherwise>
		<c:set var="titolo" value="Prezzo offerto per il prodotto dalla ditta ${nomimo}"/>
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
				<gene:campoScheda campo="DESEST"   entita="GCAP_EST" where="GCAP_EST.NGARA = V_GCAP_DPRE.NGARA and GCAP_EST.CONTAF = V_GCAP_DPRE.CONTAF" modificabile='${modo eq "NUOVO" }' />
				<gene:campoScheda campo="UNIMIS"   entita="GCAP" where="${whereCampiGCAP}" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoUnitaMisura"  modificabile='false'/>
				<gene:campoScheda campo="QUANTI"  title='Quantità richiesta'  entita="GCAP" where="${whereCampiGCAP}" modificabile='false'/>
				<gene:campoScheda campo="IVAPROD" entita="GCAP" where="${whereCampiGCAP}" modificabile='false'/>
				<gene:campoScheda campo="NUNICONF" entita="GCAP" where="${whereCampiGCAP}" modificabile='false'/>
				<gene:campoScheda campo="FASGAR" title="Fase della Gara" entita="GARE" where="GARE.NGARA = V_GCAP_DPRE.NGARA" visibile="false"/>
				<gene:campoScheda campo="REQMIN"  entita="DPRE" where="${whereCampiDPRE}" visibile="${isPrequalifica && genereGara eq 3}" />		
			</gene:gruppoCampi>
			
			<gene:gruppoCampi>
				<gene:campoScheda>
					<td colspan="2"><b>Dati fornitore</b></td>
				</gene:campoScheda>
				<gene:campoScheda campo="CODPROD" entita="DPRE_SAN" where="${whereCampiDPRE_SAN}"/>
				<gene:campoScheda campo="DENOMPROD" entita="DPRE_SAN" where="${whereCampiDPRE_SAN}" />
				<gene:campoScheda campo="UNIMIS" entita="DPRE" where="${whereCampiDPRE}" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoUnitaMisura" visibile="false"/>
				<gene:campoScheda campo="NUNICONF" entita="DPRE_SAN" where="${whereCampiDPRE_SAN}" />
				<gene:campoScheda campo="QUANTI" title="Numero confezioni offerte"  entita="DPRE" where="${whereCampiDPRE}" />
				<gene:campoScheda campo="PREOFF" title="Prezzo per confezione"  entita="DPRE" where="${whereCampiDPRE}"  />
				<gene:campoScheda campo="QUANTIUNI" entita="DPRE_SAN" where="${whereCampiDPRE_SAN}" visibile="false"/>
				<gene:campoScheda campo="QUANTIUNI_FIT" campoFittizio="true" definizione="F12.3;0;;;G1QUANTUNI" modificabile="false" value="${datiRiga.DPRE_SAN_QUANTIUNI}"/>
				<gene:campoScheda campo="PREOFFUNI" entita="DPRE_SAN" where="${whereCampiDPRE_SAN}" visibile="false"/>
				<gene:campoScheda campo="PREOFFUNI_FIT" campoFittizio="true" definizione="F24.5;0;;MONEY5;G1PREOFUNI" modificabile="false" value="${datiRiga.DPRE_SAN_PREOFFUNI}" />
				<gene:campoScheda campo="IMPOFF"  entita="DPRE" where="${whereCampiDPRE}" visibile="false"/>
				<gene:campoScheda campo="IMPOFF_FIT"  campoFittizio="true" definizione="F24.5;0;;MONEY5;G1_IMPOFD" modificabile="false" value="${datiRiga.DPRE_IMPOFF}"/>
				<gene:campoScheda campo="IVAPVPUBBL" title="IVA confezione" entita="DPRE_SAN" where="${whereCampiDPRE_SAN}" />
			</gene:gruppoCampi>
			
			<input type="hidden" name="PREQUALIFICA" id="PREQUALIFICA" value="${PREQUALIFICA}" />
			<input type="hidden" name="BLOCCO_AGG" id="BLOCCO_AGG" value="${BLOCCO_AGG}" />	
			<input type="hidden" name="TIPO_FORNITURA" value="98" />
			
			<jsp:include page="sezione-attributi-generici-XDPRE.jsp">
				<jsp:param name="entitaParent" value="DPRE"/>
				<jsp:param name="gara" value="${numeroGara}"/>
				<jsp:param name="joinWhere" value="V_GCAP_DPRE.NGARA = XDPRE.XNGARA and V_GCAP_DPRE.CONTAF = XDPRE.XCONTAF and V_GCAP_DPRE.COD_DITTA = XDPRE.XDITTAO"/>
			</jsp:include>			
								
			
			<c:if test='${esisteIntegrazioneAUR eq "1" }'>
				<gene:fnJavaScriptScheda funzione='valorizzaCODVOC()' elencocampi='GCAP_SAN_CODAUR' esegui="false" />
			</c:if>
			
			<c:if test='${modo ne "VISUALIZZA"}' >
				<gene:fnJavaScriptScheda funzione='aggiornaImportoTotale("#DPRE_QUANTI#","#DPRE_PREOFF#")' elencocampi='DPRE_QUANTI;DPRE_PREOFF' esegui="true" />
				<gene:fnJavaScriptScheda funzione='aggiornaQuantitaUnitaria("#DPRE_SAN_NUNICONF#","#DPRE_QUANTI#")' elencocampi='DPRE_SAN_NUNICONF;DPRE_QUANTI' esegui="false" />
				<gene:fnJavaScriptScheda funzione='aggiornaPrezzoUnitario("#DPRE_PREOFF#","#DPRE_SAN_NUNICONF#")' elencocampi='DPRE_PREOFF;DPRE_SAN_NUNICONF' esegui="false" />
			</c:if>
									
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
				<c:if test='${esisteGaraOLIAMM eq "true" or offtel eq 1}'>
					<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
					<gene:redefineInsert name="pulsanteNuovo"></gene:redefineInsert>
				</c:if>
				<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
			</gene:campoScheda>
		</gene:formScheda>
	</gene:redefineInsert>
	<gene:javaScript>
		
		
		
		//La funzione valorizza il campo PREOFFUNI
		//con DPRE.PREOFF/DPRE_SAN.NUNICONF
		function aggiornaPrezzoUnitario(PREOFF,NUM)
			{
							
				if (NUM == null || NUM == "" || PREOFF == null || PREOFF == ""){
					setValue("DPRE_SAN_PREOFFUNI",  "");
					setValue("PREOFFUNI_FIT",  "");
				}else {
					NUM = parseFloat(NUM);
					PREOFF = parseFloat(PREOFF);
					
					var temp = PREOFF / NUM;
					setValue("DPRE_SAN_PREOFFUNI",  round(eval(temp), 5));
					setValue("PREOFFUNI_FIT",  round(eval(temp), 5));
				}
			}
		
		//La funzione valorizza il campo calcolato IMPOFF.DPRE
		//con QUANTI.DPRE*PREOFF.DPRE
		function aggiornaImportoTotale(QUANTI,PREOFF)
			{
								
				if (QUANTI == null || QUANTI == "" || PREOFF == null || PREOFF == ""){
					setValue("DPRE_IMPOFF",  "");
					setValue("IMPOFF_FIT",  "");
				}else {
					QUANTI = parseFloat(QUANTI);
					PREZUN = parseFloat(PREOFF);
					
					var temp = QUANTI * PREOFF;
					setValue("DPRE_IMPOFF",  round(eval(temp), 5));
					setValue("IMPOFF_FIT",  round(eval(temp), 5));
				}
			}

		//La funzione valorizza il campo calcolato QUANTIUNI
		//con DPRE_SAN.NUNICONF * DPRE.QUANTI
		function aggiornaQuantitaUnitaria(NUM,QUANTI)
			{
								
				if (QUANTI == null || QUANTI == "" || NUM == null || NUM == ""){
					setValue("DPRE_SAN_QUANTIUNI",  "");
					setValue("QUANTIUNI_FIT",  "");
				}else {
					QUANTI = parseFloat(QUANTI);
					PREZUN = parseFloat(NUM);
					
					var temp = QUANTI * NUM;
					setValue("DPRE_SAN_QUANTIUNI",  round(eval(temp), 5));
					setValue("QUANTIUNI_FIT",  round(eval(temp), 5));
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
			
			
	</gene:javaScript>
</gene:template>