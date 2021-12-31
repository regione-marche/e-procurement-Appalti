<%
/*
 * Created on: 10/12/2010
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


<style type="text/css">
	
	TABLE.grigliaDataProt {
	margin: 0;
	PADDING: 0px;
	width: 100%;
	FONT-SIZE: 11px;
	border-collapse: collapse;
	border-left: 1px solid #A0AABA;
	border-top: 1px solid #A0AABA;
	border-right: 1px solid #A0AABA;
}

TABLE.grigliaDataProt TD {
	PADDING: 2px 0px 2px;
	BORDER-BOTTOM: #A0AABA 1px solid;
}

TABLE.grigliaDataProt TD.no-border {
	PADDING: 2px 0px 2px;
	BORDER-BOTTOM: 0px;
}

TABLE.grigliaDataProt TD.etichetta-dato {
	width: 300px;
	HEIGHT: 22px;
	PADDING-RIGHT: 10px;
	BORDER-TOP: #A0AABA 1px solid;
	BACKGROUND-COLOR: #EFEFEF;
	color: #000000;
	TEXT-ALIGN: right;
}


TABLE.grigliaDataProt TD.valore-dato {
	width: 190px;
	PADDING-LEFT: 10px;
	TEXT-ALIGN: left;
	BORDER-LEFT: #A0AABA 1px solid;
}

TABLE.grigliaDataProt TD.valore-dato-numerico {
	width: 190px;
	PADDING-LEFT: 10px;
	TEXT-ALIGN: right;
	BORDER-LEFT: #A0AABA 1px solid;
}

TABLE.grigliaDataProt TD.titolo-valore-dato {
	width: 300px;
	PADDING-LEFT: 10px;
	TEXT-ALIGN: left;
	BORDER-LEFT: #A0AABA 1px solid;
}

TABLE.grigliaDataProt TD.valore-dato A {
	text-decoration: underline;
	color: #000000;
}

TABLE.grigliaDataProt TD.valore-dato A:hover {
	text-decoration: none;
}

	</style>

<c:if test='${((tipscad eq "1") || (tipscad eq "2") || (tipscad eq "3"))}' >
	<c:set var="numProtocollo" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetNumeroProtocolloFunction", pageContext,tipscad,ngara)}' />
	<c:choose>
		<c:when test="${tipscad eq '1'}">
			<c:set var="tipscadDesc" value="domanda di partecipazione"/>
		</c:when>
		<c:when test="${tipscad eq '2'}">
			<c:set var="tipscadDesc" value="offerta"/>
		</c:when>
		<c:when test="${tipscad eq '3'}">
			<c:set var="tipscadDesc" value="documenti per comprova requisiti"/>
		</c:when>
	</c:choose>	
</c:if>

<gene:template file="scheda-template.jsp">
	
	<gene:setString name="titoloMaschera" value='Ricezione ${tipscadDesc} per la gara ${ngara}'/>
	
	<c:set var="modo" value="NUOVO" scope="request" />	
	<gene:redefineInsert name="corpo">

		<gene:formScheda entita="DITG" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreDITGdaBarcode">
		
		<gene:redefineInsert name="schedaAnnulla" />
		<gene:redefineInsert name="schedaConferma" />
		<gene:redefineInsert name="addToAzioni" >
			<tr>
				<td class="vocemenulaterale">
					<a href="javascript:conferma();" title="Salva modifiche" tabindex="1501">
						${gene:resource("label.tags.template.dettaglio.schedaConferma")}
					</a>
				</td>
			</tr>
			<tr>
				<td class="vocemenulaterale">
					<a href="javascript:annulla();" title="Annulla modifiche" tabindex="1502">
						${gene:resource("label.tags.template.dettaglio.schedaAnnulla")}
					</a>
				</td>
			</tr>
		</gene:redefineInsert>
		
		<input type="hidden" name="tipscad" id="tipscad" value="${tipscad}" />
		<input type="hidden" name="impresa" id="impresa" value="${impresa}" />
		<input type="hidden" name="gara" id="gara" value="${gara}" />
		<input type="hidden" name="ngara" id="ngara" value="${ngara}" />
		<input type="hidden" name="genere" id="genere" value="${genere}" />
		<input type="hidden" name="tipoRTI" id="tipoRTI" value="${tipoRTI}" />
		<input type="hidden" name="nomeATI" id="nomeATI" value="${nomeATI}" />
		<input type="hidden" name="dittao" id="dittao" value="${dittao}" />
		<input type="hidden" name="nomimoEstesa" id="nomimoEstesa" value="${nomimoEstesa}" />
		<input type="hidden" name="tipimpNewRTI" id="tipimpNewRTI" value="${tipimpNewRTI}" />
		<input type="hidden" name="quotaMandataria" id="quotaMandataria" value="${quotaMandataria}" />
		<input type="hidden" name="cfimp" id="cfimp" value="${cfimp}" />
		<input type="hidden" name="pivimp" id="pivimp" value="${pivimp}" />
		
		<c:set var="stampaEtichetta" value="0"/>
		<c:if test="${requestScope.isProfiloProtocollo}">
			<c:set var="stampaEtichetta" value="1"/>
		</c:if>		
		
    	<gene:campoScheda campo="RTI" title="Raggruppamento temporaneo?" campoFittizio="true" value="${tipimpNewRTI}" definizione="T2" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoSiNoRTI" modificabile="false"/>
    	   	
    	<c:choose>
    	<c:when test="${requestScope.tipoRTI eq '1'}">
    		
    		<gene:campoScheda campo="NOMIMO" title="Ragione sociale raggruppamento temporaneo" definizione="T2000;0;;;" campoFittizio="true" value="${ requestScope.nomeATI}" modificabile="false"/>
    		<gene:archivio titolo="ditte"
				lista=''
				scheda='${gene:if(gene:checkProtObj(pageContext, "MASC.VIS","GENE.ImprScheda"),"gene/impr/impr-scheda.jsp","")}'
				schedaPopUp='${gene:if(gene:checkProtObj(pageContext, "MASC.VIS","GENE.ImprScheda"),"gene/impr/impr-scheda-popup.jsp","")}'
				campi="IMPR.CODIMP;IMPR.NOMEST;IMPR.CFIMP;IMPR.PIVIMP"
				chiave="DITTAO"
				where=""
				formName="formDitteMandataria"
				inseribile="true">
	    		<gene:campoScheda campo="DITTAO" obbligatorio="true" definizione="T10;0;;;" campoFittizio="true" value="${requestScope.dittao}" visibile="false" />
	    		<gene:campoScheda campo="NOMEST" title="Ragione sociale ditta mandataria" definizione="T2000;0;;;" campoFittizio="true" value="${requestScope.nomimoEstesa}" modificabile="false"/>
	    		<gene:campoScheda campo="CFIMP" title="Codice fiscale" definizione="T16;0;;;" campoFittizio="true" value="${requestScope.cfimp}" modificabile="false"/> 
	    		<gene:campoScheda campo="PIVIMP" title="Partita IVA" definizione="T11;0;;;" campoFittizio="true" value="${requestScope.pivimp}" modificabile="false"/>
    		</gene:archivio> 
    	</c:when>
    	<c:otherwise>
    		<gene:archivio titolo="ditte"
				lista=''
				scheda='${gene:if(gene:checkProtObj(pageContext, "MASC.VIS","GENE.ImprScheda"),"gene/impr/impr-scheda.jsp","")}'
				schedaPopUp='${gene:if(gene:checkProtObj(pageContext, "MASC.VIS","GENE.ImprScheda"),"gene/impr/impr-scheda-popup.jsp","")}'
				campi="IMPR.CODIMP;IMPR.NOMEST;IMPR.CFIMP;IMPR.PIVIMP"
				chiave="DITTAO"
				where=""
				formName="formDitteMandataria"
				inseribile="true">
	    		<gene:campoScheda campo="DITTAO" obbligatorio="true" definizione="T10;0;;;" campoFittizio="true" value="${requestScope.dittao}" visibile="false" />
	    		<gene:campoScheda campo="NOMIMO" title="Ragione sociale" definizione="T2000;0;;;" campoFittizio="true" value="${ requestScope.nomimoEstesa}" modificabile="false"/>
	    		<gene:campoScheda campo="CFIMP" title="Codice fiscale" definizione="T16;0;;;" campoFittizio="true" value="${requestScope.cfimp}" modificabile="false"/> 
	    		<gene:campoScheda campo="PIVIMP" title="Partita IVA" definizione="T11;0;;;" campoFittizio="true" value="${requestScope.pivimp}" modificabile="false"/> 
    		</gene:archivio> 
    	</c:otherwise>
    	</c:choose>
    	
    	
		<gene:campoScheda campo="DATA" title="Data"  definizione="D;0;;;" campoFittizio="true" value="${requestScope.dataAttuale}" />
		<gene:campoScheda campo="ORA" title="Ora"   definizione="T6;0;;ORA;" campoFittizio="true" value="${requestScope.oraAttuale}"/>
		<gene:campoScheda campo="NPRDOMFIT" modificabile="${not requestScope.isProfiloProtocollo}" title="N.protocollo" campoFittizio="true" definizione="T20;0;;;" value="${numProtocollo}"/>
		<c:if test="${tipscad ne '3'}">
			<gene:campoScheda campoFittizio="true" addTr="false">
				<td class="etichetta-dato">Data protocollo</td>
				<td class="valore-dato">
					<table id="tabellaDataProtPart" class="grigliaDataProt" style="width: 99%; ">
			</gene:campoScheda>
			<gene:campoScheda addTr="false" campoFittizio="true" title="Data" campo="DATAPROT" definizione="D;0;;DATA_ELDA;"/>
			<gene:campoScheda addTr="false" campoFittizio="true" title="Ora" campo="ORAPROT"  definizione="T8;0;;ORA;"  />
			
			<gene:campoScheda addTr="false">
					<td class="riempimento"></td>
				</table>
				</td>
			</gene:campoScheda>
			<gene:fnJavaScriptScheda funzione='sbiancaOra("#DATAPROT#","ORAPROT")' elencocampi='DATAPROT' esegui="false"/>
			<gene:campoScheda  campo="DATPROT_NASCOSTO" campoFittizio="true" definizione="T25;0;;" visibile="false"/>
		</c:if>
		
		<gene:campoScheda campo="MEZZO" title="Mezzo"  definizione="N7;0;A1030;;" campoFittizio="true" />
		<gene:campoScheda campo="STATO" title="Stato"  definizione="N7;0;A1100;;" campoFittizio="true" visibile="${!requestScope.isProfiloProtocollo}"/>
		<gene:campoScheda campo="NSPED" title="Numero spedizione"  definizione="T20;0;;;" campoFittizio="true" visibile="${requestScope.isProfiloProtocollo}"/>
		<gene:campoScheda campo="RIT" title="Stato ritiro plico"  definizione="N7;0;A1045;;" campoFittizio="true" visibile="${requestScope.isProfiloProtocollo}"/>
		<gene:campoScheda campo="NOTP" title="Note"  definizione="T2000;0;;NOTE;" campoFittizio="true" />
		<gene:campoScheda campo="STAMPA_EP" title="Stampa etichetta?" defaultValue="${stampaEtichetta}" definizione="N1;0;;SN" campoFittizio="true" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoSiNoSenzaNull" modificabile="true" visibile="${requestScope.isProfiloProtocollo}"/>

		<gene:campoScheda>
			<td class="comandi-dettaglio" colSpan="2">
				<gene:insert name="addPulsanti"/>
							
					<gene:insert name="pulsanteSalva">
						<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:conferma()">
					</gene:insert>
					<gene:insert name="pulsanteAnnulla">
						<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:annulla()">
					</gene:insert>
				
				
				&nbsp;
			</td>								
		</gene:campoScheda>		
			
			
		</gene:formScheda>

	</gene:redefineInsert>
	<gene:javaScript>
				
		function ApriPopup(tipscad,campo,campo1,campo2){
			// Eseguo l'apertura della maschera che propone il numero del protocollo	
			var tipscad="${tipscad}";
			var data = 0;
			var ora = 0;
			var valoreCampoData;
			
			var valoreCampoProtocollo;
			
			valoreCampoProtocollo = getValue("NPRDOMFIT");
						
			if (valoreCampoProtocollo == "Assegna numero protocollo") {
				valoreCampoData = getValue("DATA");
				if (valoreCampoData != ""){
					data=1;
				}
				var ngara="${ngara }";
				openPopUpCustom("href=gare/commons/popupCalcoloNumeroProtocollo.jsp&campo=NPRDOMFIT&tipscad=" + tipscad + "&campodata=DATA&data=" + data + "&campoora=ORA&ngara=" + ngara, "NumProtocollo", 100, 100, "no", "no");
			}
		}
		
		function stampaProtocollo(){
			setTimeout("stampaEtichetta()", 100);
		}

		function stampaEtichetta(){
		// document.forms[0].key.value è nella forma: DITG.CODGAR5=T:$09MI158;DITG.DITTAO=T:IMP001;DITG.NGARA5=T:09MI158
        //var href = "href=gare/ditg/composizioneEtichettaInCorso.jsp&key=" + document.forms[0].key.value + "&tipoProtocollo=${tipscad}" ;
              var gara="${gara}";
              var ditta=getValue("DITTAO");
              var ngara="${ngara}";
              var chiave="DITG.CODGAR5=T:" + gara + ";DITG.DITTAO=T:" + ditta + ";DITG.NGARA5=T:" + ngara;
              var href = "href=gare/ditg/composizioneEtichettaInCorso.jsp&key=" + chiave + "&tipoProtocollo=${tipscad}" ;
             openPopUpCustom(href, "composizioneEtichettaProtocollo", 540, 190, 0, 0);
		}
		
		
		function annulla(){
			window.location=contextPath+'/pg/initLeggiBarcode.do?' + csrfToken;
		}
		
		function conferma() {
		    <c:if test="${tipscad ne '3'}">
			    var data = getValue("DATAPROT");
				var ora = getValue("ORAPROT");
				if((data!=null && data!="") && (ora==null || ora =="" )){
					alert("Non è possibile procedere, deve essere inserita l'ora del protocollo");
					return;
				}
				if((data==null || data=="") && (ora!=null && ora !="" )){
					alert("Non è possibile procedere, non può essere inserita l'ora del protocollo in mancanza della data");
					return;
				}
				var dataOra = "";
				if(data!=null && ora!=null && data!="" && ora !="")
					dataOra = data + " " + ora;
				setValue("DATPROT_NASCOSTO", dataOra);
		    </c:if>
		    		    
			var valoreCampoProtocollo = getValue("NPRDOMFIT");
			if (valoreCampoProtocollo == "Assegna numero protocollo") {
				setValue("NPRDOMFIT","");	
			}
			document.forms[0].jspPathTo.value="gare/commons/initLeggiBarcode.jsp";
			var se=getValue("STAMPA_EP");
			if (se=='1') {
				document.forms[0].jspPathTo.value="gare/ditg/composizioneEtichettaInCorso.jsp";
			}
			schedaConferma();
		}
		
		function sbiancaOra(data,campo){
			if(data==null || data == "")
				setValue(campo,"");
		}
		
		<c:if test="${tipscad ne '3'}">
			//Nella tabella grigliaDataProt è stato inserito il td con classe='riempimento' che ha il solo
			//scopo di riempire una parte della tabella in modo da fare risultare più piccoli e quindi più
			//vicini i campi con l'ora e la data
			$('table.grigliaDataProt tr td.valore-dato').css('width','200');
			$('table.grigliaDataProt tr td.riempimento').css('width','40%');
		</c:if>
	</gene:javaScript>
</gene:template>