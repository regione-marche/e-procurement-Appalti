<%
/*
 * Created on: 14/05/2010
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


<c:if test="${!lottoOffertaUnica }">
	<c:set var="genereGara" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipologiaGaraFunction",  pageContext,keyParent)}' scope="request"/>
</c:if>

<c:set var="esisteIntegrazioneAUR" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneAURFunction",  pageContext)}' scope="request"/>


<%/*La pagina può essere richiamata anche da TORN per le gare divise a lotti con offerta unica*/ %>
<c:choose>
	<c:when test='${genereGara eq "3"}'>
	<%/*gara divisa a lotti con offerta unica*/ %>
		<c:set var="codiceGara" value='${gene:getValCampo(keyParent,"CODGAR")}' />
		<c:set var="titoloNuovo" value="Nuova lavorazione o fornitura della gara ${codiceGara}"/>
		<c:set var="entita" value='TORN_GCAP' />
	</c:when>
	<c:otherwise>
		<c:set var="codiceGara" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetCodiceGaraFunction", pageContext)}' />
		<c:set var="entita" value='GCAP' />
	</c:otherwise>
</c:choose>


<c:set var="ngara" value='${gene:getValCampo(key, "GCAP.NGARA")}' />
<c:set var="BloccoOfferteDitte" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.ControlloOfferteDitteFunction", pageContext, ngara,codiceGara)}' />
<c:set var="BloccoAggiudicazione" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.ControlloAggiudicazioneDefinitivaFunction", pageContext, ngara,codiceGara)}' />
<c:if test='${BloccoOfferteDitte eq "VERO" or BloccoAggiudicazione eq "VERO"}'>
	<c:set var="bloccoModifica" value="VERO"/>
</c:if>

<c:set var="tipoFrontitura" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipoFornitureFunction", pageContext, codiceGara)}' />

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="GCAP-scheda">
	
	<c:choose>
		<c:when test='${genereGara eq "3" and modo eq "NUOVO"}'>
			<gene:setString name="titoloMaschera" value='${titoloNuovo}'/>
		</c:when>
		<c:otherwise>
			<gene:setString name="titoloMaschera" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTitleFunction", pageContext, entita)}'/>
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
						 chiave="GCAP_NGARA"
						 where="V_GCAP_LOTTI.CODGAR = '${codiceGara }'">
							<gene:campoScheda campo="NGARA" title="Codice lotto" obbligatorio="true" modificabile='${modo eq "NUOVO" }'/>
							<gene:campoScheda campo="CODIGA" title="Lotto" obbligatorio='${campoObbligatorio eq true}' entita="GARE" modificabile='${modo eq "NUOVO" }' where="GARE.NGARA = GCAP.NGARA"/>
							<gene:campoScheda campo="NORVOC_MAX" campoFittizio="true" visibile='false' definizione="F8.3"/>
					</gene:archivio>
					<gene:fnJavaScriptScheda funzione='aggiornaNORVOC()' elencocampi='NORVOC_MAX' esegui="false" />	
					
				</c:when>
				<c:otherwise>
					<gene:campoScheda campo="NGARA" visibile='false' value='${fn:substringAfter(keyParent, ":")}'/>
				</c:otherwise>
			</c:choose>
			
			<gene:campoScheda campo="CONTAF"  visibile="false"/>
			<gene:campoScheda campo="NGARA"  entita="GCAP_SAN" where="GCAP.NGARA = GCAP_SAN.NGARA and GCAP_SAN.CONTAF=GCAP.CONTAF" visibile='false'/>
			<gene:campoScheda campo="CONTAF"  entita="GCAP_SAN" where="GCAP.NGARA = GCAP_SAN.NGARA and GCAP_SAN.CONTAF=GCAP.CONTAF" visibile='false'/>
			<gene:campoScheda campo="NORVOC"  title="Numero d'ordine" value='${gene:if(modo eq "NUOVO",newNorvoc,datiRiga.GCAP_NORVOC)}'/>
			<gene:campoScheda campo="CODVOC"  obbligatorio='${campoObbligatorio eq true}'/>
			<gene:campoScheda campo="VOCE"/>
			<gene:campoScheda campo="CODATC"  entita="GCAP_SAN" where="GCAP.NGARA = GCAP_SAN.NGARA and GCAP_SAN.CONTAF=GCAP.CONTAF" visibile='${tipoFrontitura == "1"}'/>
			<gene:campoScheda campo="CODAUR"  entita="GCAP_SAN" where="GCAP.NGARA = GCAP_SAN.NGARA and GCAP_SAN.CONTAF=GCAP.CONTAF" visibile='${tipoFrontitura == "1"}' obbligatorio='${esisteIntegrazioneAUR eq "1"}'/>
			<gene:campoScheda campo="PRINCATT"  entita="GCAP_SAN" where="GCAP.NGARA = GCAP_SAN.NGARA and GCAP_SAN.CONTAF=GCAP.CONTAF" visibile='${tipoFrontitura == "1"}'/>
			<gene:campoScheda campo="FORMAFARM"  entita="GCAP_SAN" where="GCAP.NGARA = GCAP_SAN.NGARA and GCAP_SAN.CONTAF=GCAP.CONTAF" visibile='${tipoFrontitura == "1"}'/>
			<gene:campoScheda campo="DOSAGGIO"  entita="GCAP_SAN" where="GCAP.NGARA = GCAP_SAN.NGARA and GCAP_SAN.CONTAF=GCAP.CONTAF" visibile='${tipoFrontitura == "1"}'/>
			<gene:campoScheda campo="VIASOMM"  entita="GCAP_SAN" where="GCAP.NGARA = GCAP_SAN.NGARA and GCAP_SAN.CONTAF=GCAP.CONTAF" visibile='${tipoFrontitura == "1"}'/>
			<gene:campoScheda campo="CODCLASS"  entita="GCAP_SAN" where="GCAP.NGARA = GCAP_SAN.NGARA and GCAP_SAN.CONTAF=GCAP.CONTAF" visibile='${tipoFrontitura == "2"}'/>
			<gene:campoScheda campo="DEPRODCN"  entita="GCAP_SAN" where="GCAP.NGARA = GCAP_SAN.NGARA and GCAP_SAN.CONTAF=GCAP.CONTAF" visibile='${tipoFrontitura == "2"}'/>
			<gene:campoScheda campo="CODAUR"  entita="GCAP_SAN" where="GCAP.NGARA = GCAP_SAN.NGARA and GCAP_SAN.CONTAF=GCAP.CONTAF" visibile='${tipoFrontitura == "2"}' obbligatorio='${esisteIntegrazioneAUR eq "1"}'/>
			<gene:campoScheda campo="DPRODCAP"  entita="GCAP_SAN" where="GCAP.NGARA = GCAP_SAN.NGARA and GCAP_SAN.CONTAF=GCAP.CONTAF" visibile='${tipoFrontitura == "2"}'/>
			<gene:campoScheda campo="UNIMIS"  gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoUnitaMisura" visibile='${tipoFrontitura == "1"}'/>			
			<gene:campoScheda campo="QUANTI"  title='${gene:if(tipoFrontitura == "1" , "Fabbisogno relativo a 12 mesi in unità di misura", "Quantità/fabbisogni")}' obbligatorio='${campoObbligatorio eq true}'/>
			<gene:campoScheda campo="PREZUN"  title='${gene:if(tipoFrontitura == "1" , "Base d\'asta unitaria (euro, iva esclusa)", "Prezzo unitario a base d\'asta")}'/>
			<gene:campoScheda campo="IMPORTO" entita="TORN" title="Importo" campoFittizio="true" definizione="F24.2;0;;MONEY5" modificabile="false" value="${importo}" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.GCAP.PREZUN") and tipoFrontitura == "2"}'/>
			<gene:campoScheda campo="NOTE"  entita="GCAP_SAN" where="GCAP.NGARA = GCAP_SAN.NGARA and GCAP_SAN.CONTAF=GCAP.CONTAF" visibile='${tipoFrontitura == "1"}'/>
			
			<c:if test='${tipoFrontitura eq "2"}'>
				<gene:fnJavaScriptScheda funzione='aggiornaImporto()' elencocampi='GCAP_QUANTI;GCAP_PREZUN' esegui="false" />
			</c:if>
			
			<c:if test='${esisteIntegrazioneAUR eq "1" && (tipoFrontitura eq "1" || tipoFrontitura eq "2")}'>
				<gene:fnJavaScriptScheda funzione='valorizzaCODVOC()' elencocampi='GCAP_SAN_CODAUR' esegui="false" />
			</c:if>
			
			<c:if test='${esisteIntegrazioneAUR eq "1" && tipoFrontitura eq "1"}'>
				<gene:fnJavaScriptScheda funzione='valorizzaVoce(1)' elencocampi='GCAP_SAN_NOTE' esegui="false" />
			</c:if>
			
			<c:if test='${esisteIntegrazioneAUR eq "1" && tipoFrontitura eq "2"}'>
				<gene:fnJavaScriptScheda funzione='valorizzaVoce(2)' elencocampi='GCAP_SAN_DPRODCAP' esegui="false" />
			</c:if>
			
			<input type="hidden" name="lottoOffertaUnica" id="lottoOffertaUnica" value="${lottoOffertaUnica}"/>
			<input type="hidden" name="codgar" id="codgar" value="${codgar}"/>			
								
			<gene:campoScheda>
				<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-scheda.jsp">
					<jsp:param name="entita" value="V_GARE_TORN"/>
					<jsp:param name="inputFiltro" value="CODGAR=T:${codiceGara}"/>
					<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
				</jsp:include>
				<%// Se vi sono offerte delle ditte e non è già presente il blocco dato dalla G_PERMESSI, si elimina la possibilità di effettuare modifiche%>
				<c:if test='${(autorizzatoModifiche ne "2" && (bloccoModifica eq "VERO")) || param.bloccoPubblicazione eq true}'>
					<gene:redefineInsert name="schedaModifica"></gene:redefineInsert>
					<gene:redefineInsert name="pulsanteModifica"></gene:redefineInsert>
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
			
			
			<c:if test='${esisteIntegrazioneAUR eq "1" && (tipoFrontitura eq "1" || tipoFrontitura eq "2")}'>
				showObj("rowGCAP_CODVOC",false);
				showObj("rowGCAP_VOCE",false);
				
				function valorizzaCODVOC(){
					var codaur = getValue("GCAP_SAN_CODAUR");
					setValue("GCAP_CODVOC",  codaur);
				}
				
				function valorizzaVoce(valore){
					var desc;
					if(valore==1)
						desc = getValue("GCAP_SAN_NOTE");
					else
						desc = getValue("GCAP_SAN_DPRODCAP");
					setValue("GCAP_VOCE",  desc);
				}
			</c:if>			
			
			
			
			<c:if test='${genereGara eq "3"}'>
				//La funzione inizializza il campo NORVOC in base 
				//al lotto selezionato dall'archivio
				function aggiornaNORVOC(){
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
			</c:if>
	</gene:javaScript>
</gene:template>