<%/*
   * Created on 17-ott-2007
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


<c:choose>
	<c:when test='${not empty requestScope.InserimentoEseguito and requestScope.InserimentoEseguito eq "SI"}' >
<script type="text/javascript">
		window.opener.historyReload();
		window.close();
</script>
	</c:when>
	<c:otherwise>

<c:choose>
	<c:when test='${not empty param.ngara}'>
		<c:set var="ngara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.ngaraaq}'>
		<c:set var="ngaraaq" value="${param.ngaraaq}" />
	</c:when>
	<c:otherwise>
		<c:set var="ngaraaq" value="${ngaraaq}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.aqoper}'>
		<c:set var="aqoper" value="${param.aqoper}" />
	</c:when>
	<c:otherwise>
		<c:set var="aqoper" value="${aqoper}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.ribcal}'>
		<c:set var="ribcal" value="${param.ribcal}" />
	</c:when>
	<c:otherwise>
		<c:set var="ribcal" value="${ribcal}" />
	</c:otherwise>
</c:choose>
	
<c:set var="ditta" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetDittaFunction", pageContext, ngaraaq)}' />
	
<gene:template file="popup-message-template.jsp" gestisciProtezioni="false" >
	
		
	<gene:setString name="titoloMaschera" value='Selezione lavorazioni da accordo quadro'/>
	
	<% // Ridefinisco il corpo della ricerca %>
	<gene:redefineInsert name="corpo">
	
	<gene:set name="titoloMenu">
		<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
	</gene:set>
	
	<c:choose>
		<c:when test='${aqoper eq "1"}'>
			<c:set var="entita" value="V_GCAP_DPRE" />
			<c:set var="where" value="V_GCAP_DPRE.NGARA = '${ngaraaq}' AND V_GCAP_DPRE.COD_DITTA = '${ditta}' AND V_GCAP_DPRE.CONTAF NOT IN (SELECT CONTAFAQ FROM GCAP WHERE GCAP.NGARA ='${ngara}' AND GCAP.DITTAO is null)" />
			<c:set var="dicituraPrezzo" value="offerto dalla ditta aggiudicataria dell'accordo quadro" />
			<c:set var="dicituraImporto" value="Al termine dell'operazione, viene aggiornato l'importo a base di gara sulla base dei prezzi unitari e delle quantità delle lavorazioni inserite." />
		</c:when>
		<c:otherwise>
			<c:set var="entita" value="GCAP" />
			<c:set var="where" value="GCAP.NGARA = '${ngaraaq}' AND GCAP.DITTAO is null AND GCAP.CONTAF NOT IN (SELECT G1.CONTAFAQ FROM GCAP G1 WHERE G1.NGARA ='${ngara}' AND G1.DITTAO is null AND G1.CONTAFAQ is not null)" />
			<c:set var="dicituraPrezzo" value="posto a base di gara nell'accordo quadro stesso" />
			<c:set var="dicituraImporto" value="" />
		</c:otherwise>
	</c:choose>

	<br>Selezionare dalla lista sottostante le lavorazioni dell'accordo quadro che si vogliono riportare nella gara corrente. 
	<br>Il prezzo unitario a base di gara delle lavorazioni è quello ${dicituraPrezzo}. Prima di confermare la selezione, è possibile modificare la quantità delle lavorazioni selezionate. 
	<br>${dicituraImporto } 
	  	
		<table class="lista">
			<tr>
				<td>
  				<gene:formLista entita="${entita}" where='${where}' pagesize="20" tableclass="datilista" sortColumn="3;2" gestisciProtezioni="true"
  					gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupSelDaAccordoQuadro">
  					
  					<gene:redefineInsert name="listaNuovo" />
					<gene:redefineInsert name="listaEliminaSelezione" />
										 					
  					 <c:choose>
						<c:when test='${aqoper eq "1"}'>					
		  					<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50">
								<c:if test="${currentRow >= 0}">
									<input type="checkbox" name="keys" value="${datiRiga.V_GCAP_DPRE_CONTAF}"  onclick="javascript:aggiornaRiga(this,${currentRow + 1});"/>
								</c:if>
							</gene:campoLista>
							<gene:campoLista campo="CONTAF" visibile="false" edit="true"/>
							<gene:campoLista campo="NORVOC" title="N." headerClass="sortable" width="30" visibile="false"  edit="true"/>
							<gene:campoLista campo="COD_DITTA" visibile="false" edit="false"/>
							<gene:campoLista campo="NGARA" visibile="false" title='Codice lotto' edit = "false"/>
							<gene:campoLista campo="CODVOC" title="Voce" headerClass="sortable" width="80" edit = "false"/>
							<gene:campoLista campo="VOCE" headerClass="sortable" edit = "false"/>
							<gene:campoLista campo="CODCAT" entita="GCAP" where="GCAP.NGARA = V_GCAP_DPRE.NGARA and GCAP.CONTAF = V_GCAP_DPRE.CONTAF" visibile="false" edit="true"/>
							<gene:campoLista title="Um" campo="UNIMIS" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoUnitaMisura" width="55" headerClass="sortable" edit = "false"/>
							<gene:campoLista campo="QUANTIEFF" headerClass="sortable" width="80" edit = "true"/>
							<gene:campoLista campo="PREOFF" headerClass="sortable" edit = "false"/>
							<gene:campoLista campo="CODVOC_FIT" campoFittizio="true" definizione="T20"  value="${datiRiga.V_GCAP_DPRE_CODVOC}" visibile="false" edit = "true"/>
							<gene:campoLista campo="VOCE_FIT" campoFittizio="true" definizione="T2000"  value="${datiRiga.V_GCAP_DPRE_VOCE}" visibile="false" edit = "true"/>
							<gene:campoLista campo="QUANTIEFF_FIT" campoFittizio="true" definizione="F12.3"  value="${datiRiga.V_GCAP_DPRE_QUANTIEFF}" visibile="false" edit = "true"/>
							<gene:campoLista campo="SOLSIC" visibile="false" edit="true"/>
							<gene:campoLista campo="SOGRIB" visibile="false" edit="true"/>
							<gene:campoLista campo="UNIMIS_FIT" campoFittizio="true" definizione="T10"  value="${datiRiga.V_GCAP_DPRE_UNIMIS}" visibile="false" edit = "true"/>
							<gene:campoLista campo="PREOFF_FIT" campoFittizio="true" definizione="F15"  value="${datiRiga.V_GCAP_DPRE_PREOFF}" visibile="false" edit = "true"/>
							<gene:campoLista campo="CLASI1" entita="GCAP" where="GCAP.NGARA = V_GCAP_DPRE.NGARA and GCAP.CONTAF = V_GCAP_DPRE.CONTAF" visibile="false" edit="true"/>
							<gene:campoLista campo="PERCIVAEFF" visibile="false" edit = "true"/>
							<gene:campoLista campo="PESO" headerClass="sortable" edit = "true" visibile="${ribcal eq '3' }"/>
						</c:when>
						<c:otherwise>
							<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50">
								<c:if test="${currentRow >= 0}">
									<input type="checkbox" name="keys" value="${datiRiga.GCAP_CONTAF}"  onclick="javascript:aggiornaRiga(this,${currentRow + 1});"/>
								</c:if>
							</gene:campoLista>
							<gene:campoLista campo="CONTAF" visibile="false" edit="true"/>
							<gene:campoLista campo="NORVOC" title="N." headerClass="sortable" width="30" visibile="false"  edit="true"/>
							<gene:campoLista campo="DITTAO" visibile="false" edit="false"/>
							<gene:campoLista campo="NGARA" visibile="false" title='Codice lotto' edit = "false"/>
							<gene:campoLista campo="CODVOC" title="Voce" headerClass="sortable" width="80" edit = "false"/>
							<gene:campoLista campo="VOCE" headerClass="sortable" edit = "false"/>
							<gene:campoLista campo="CODCAT" visibile="false" edit="true"/>
							<gene:campoLista title="Um" campo="UNIMIS" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoUnitaMisura" width="55" headerClass="sortable" edit = "false"/>
							<gene:campoLista campo="QUANTI" headerClass="sortable" width="80" edit = "true"/>
							<gene:campoLista campo="PREZUN" headerClass="sortable" edit = "false"/>
							<gene:campoLista campo="CODVOC_FIT" campoFittizio="true" definizione="T20"  value="${datiRiga.GCAP_CODVOC}" visibile="false" edit = "true"/>
							<gene:campoLista campo="VOCE_FIT" campoFittizio="true" definizione="T2000"  value="${datiRiga.GCAP_VOCE}" visibile="false" edit = "true"/>
							<gene:campoLista campo="QUANTI_FIT" campoFittizio="true" definizione="F12.3"  value="${datiRiga.GCAP_QUANTI}" visibile="false" edit = "true"/>
							<gene:campoLista campo="SOLSIC" visibile="false" edit="true"/>
							<gene:campoLista campo="SOGRIB" visibile="false" edit="true"/>
							<gene:campoLista campo="UNIMIS_FIT" campoFittizio="true" definizione="T10"  value="${datiRiga.GCAP_UNIMIS}" visibile="false" edit = "true"/>
							<gene:campoLista campo="PREZUN_FIT" campoFittizio="true" definizione="F15"  value="${datiRiga.GCAP_PREZUN}" visibile="false" edit = "true"/>
							<gene:campoLista campo="CLASI1" visibile="false" edit="true"/>
							<gene:campoLista campo="PERCIVA" visibile="false" edit = "true"/>
							<gene:campoLista campo="PESO" headerClass="sortable" edit = "true" visibile="${ribcal eq '3' }"/>
						</c:otherwise>
					</c:choose>
												
					<input type="hidden" name="ngara" id="ngara" value="${ngara }" />
					<input type="hidden" name="ngaraaq" id="ngaraaq" value="${ngaraaq }" />
					<input type="hidden" name="ditta" id="ditta" value="${ditta }" />
					<input type="hidden" name="aqoper" id="aqoper" value="${aqoper }" />
					<input type="hidden" name="numeroLavorazioni" id="numeroLavorazioni" value="" />
					<input type="hidden" name="ribcal" id="ribcal" value="${ribcal }" />
					
					<gene:redefineInsert name="buttons">
						<c:if test="${datiRiga.rowCount >0 }">
							<INPUT type="button" class="bottone-azione" value="Conferma selezione" title="Conferma selezione" onclick="javascript:conferma();">
						</c:if>
						<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">
					</gene:redefineInsert>
				</gene:formLista>
				</td>
			</tr>
		</table>
  </gene:redefineInsert>
  <gene:javaScript>
  	
  	ribcal="${ribcal}";
  	<c:choose>
		<c:when test='${aqoper eq "1"}'>
			$('[id^="V_GCAP_DPRE_QUANTIEFF_"]').attr('disabled', true);
  			$('[id^="V_GCAP_DPRE_QUANTIEFF_"]').css('background-color', '#D5D5D5');
  			$('[id^="V_GCAP_DPRE_QUANTIEFF_"]').val('');
  			if(ribcal==3){
	  			$('[id^="V_GCAP_DPRE_PESO_"]').attr('disabled', true);
	  			$('[id^="V_GCAP_DPRE_PESO_"]').css('background-color', '#D5D5D5');
	  			$('[id^="V_GCAP_DPRE_PESO_"]').val('');
  			}
		</c:when>
		<c:otherwise>
			$('[id^="GCAP_QUANTI_"]').attr('disabled', true);
  			$('[id^="GCAP_QUANTI_"]').css('background-color', '#D5D5D5');
  			$('[id^="GCAP_QUANTI_"]').val('');
  			if(ribcal==3){
	  			$('[id^="GCAP_PESO_"]').attr('disabled', true);
	  			$('[id^="GCAP_PESO_"]').css('background-color', '#D5D5D5');
	  			$('[id^="GCAP_PESO_"]').val('');
  			}
		</c:otherwise>
	</c:choose>
  	
  	
  	document.getElementById("numeroLavorazioni").value = ${currentRow}+1;
  	
  	function annulla(){
  		window.close();
  	}
  	
  	function conferma(){
  		var numeroOggetti = contaCheckSelezionati(document.forms[0].keys);
  		if (numeroOggetti == 0) {
	    	alert("Selezionare almeno una lavorazione nella lista");
      	} else {
      		document.forms[0].jspPathTo.value="gare/gare/gare-popup-selDaAccordoQuadro.jsp";
  			listaConferma();
		}
  		
  	}
  	
  	<c:choose>
		<c:when test='${aqoper eq "1"}'>
			var nomeCampoQuantita = "V_GCAP_DPRE_QUANTIEFF";
			var nomeCampoQuantitaFIT = "QUANTIEFF_FIT";
			var nomeCampoPeso = "V_GCAP_DPRE_PESO";
			var nomeCampoSogrib = "V_GCAP_DPRE_SOGRIB";
		</c:when>
		<c:otherwise>
			var nomeCampoQuantita = "GCAP_QUANTI";
			var nomeCampoQuantitaFIT = "QUANTI_FIT";
			var nomeCampoPeso = "GCAP_PESO";
			var nomeCampoSogrib = "GCAP_SOGRIB";
		</c:otherwise>
	</c:choose>
		
  	function aggiornaRiga(check,numeroRiga){
		if(check.checked){
			$('#' + nomeCampoQuantita + '_' + numeroRiga).attr('disabled', false);
			$('#' + nomeCampoQuantita + '_' + numeroRiga).css('background-color', '#FFFFFF');
			if(ribcal==3){
				$('#' + nomeCampoPeso + '_' + numeroRiga).attr('disabled', false);
				$('#' + nomeCampoPeso + '_' + numeroRiga).css('background-color', '#FFFFFF');
			}
		}else{
			$('#' + nomeCampoQuantita + '_' + numeroRiga).attr('disabled', true);
			$('#' + nomeCampoQuantita + '_' + numeroRiga).css('background-color', '#D5D5D5');
			if(ribcal==3){
				$('#' + nomeCampoPeso + '_' + numeroRiga).attr('disabled', true);
				$('#' + nomeCampoPeso + '_' + numeroRiga).css('background-color', '#D5D5D5');
			}
		}
	}
  	
  	function selezionaTutti(objArrayCheckBox) {
    	for (i = 0; i < objArrayCheckBox.length; i++) {
     	 objArrayCheckBox[i].checked = true;
     	 $('#' + nomeCampoQuantita + '_' + (i + 1)).attr('disabled', false);
     	 $('#' + nomeCampoQuantita + '_' + (i + 1)).css('background-color', '#FFFFFF');
     	 if(ribcal==3){
	     	 $('#' + nomeCampoPeso + '_' + (i + 1)).attr('disabled', false);
	     	 $('#' + nomeCampoPeso + '_' + (i + 1)).css('background-color', '#FFFFFF');
     	 }
    	}
  	}
	
	function deselezionaTutti(objArrayCheckBox) {
	    for (i = 0; i < objArrayCheckBox.length; i++) {
	      objArrayCheckBox[i].checked = false;
	      $('#' + nomeCampoQuantita + '_' +  (i + 1)).attr('disabled', true);
	      $('#' + nomeCampoQuantita + '_' + (i + 1)).css('background-color', '#D5D5D5');
	      if(ribcal==3){
		      $('#' + nomeCampoPeso + '_' +  (i + 1)).attr('disabled', true);
		      $('#' + nomeCampoPeso + '_' + (i + 1)).css('background-color', '#D5D5D5');
	      }
	    }
	}
	
	
	function associaFunzioniEventoOnchange(){
		for(var i=1; i <= ${currentRow}+1; i++){
			document.getElementById(nomeCampoQuantita + "_" + i).onchange = controlloQuantita;
			if(ribcal==3){
				var sogrib = getValue(nomeCampoSogrib + "_" + i);
				if(sogrib=='1'){
					$('#' + nomeCampoPeso + '_' +  i ).hide();
				}
			}
		}
	}
	
	function controlloQuantita(){
		var objId = this.id;
		var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
		var quantitaNew = toVal(this.value);
		var quantitaOld = getValue(nomeCampoQuantitaFIT + "_" + numeroRiga);
		if(quantitaNew > quantitaOld){
			quantitaOld = quantitaOld.toString();
			quantitaOld = quantitaOld.replace('.',',');
			alert("La quantità specificata è maggiore di quella dell'accordo quadro (" + quantitaOld + ")");
			setValue(nomeCampoQuantita + "_" + numeroRiga, '');
		}
	}
	
	associaFunzioniEventoOnchange();
	</gene:javaScript>
</gene:template>

</c:otherwise>
</c:choose>