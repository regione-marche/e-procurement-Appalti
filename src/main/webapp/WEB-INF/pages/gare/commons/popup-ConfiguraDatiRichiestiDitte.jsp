<%/*
   * Created on 10-05-2015
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />


<c:choose>
	<c:when test='${not empty param.ngara}'>
		<c:set var="ngara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	 <c:when test='${not empty requestScope.modalita}'>
		<c:set var="modalita" value="${requestScope.modalita}" />
	</c:when>
        <c:when test='${not empty param.modalita}'>
		<c:set var="modalita" value="${param.modalita}" />
	</c:when>

        <c:otherwise>
		<c:set var="modalita" value="${modalita}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.bloccoModifica}'>
		<c:set var="bloccoModifica" value="${param.bloccoModifica}" />
	</c:when>
	<c:otherwise>
		<c:set var="bloccoModifica" value="${bloccoModifica}" />
	</c:otherwise>
</c:choose>


<c:choose>
	<c:when test='${modalita eq "vis"}'>
		<c:set var="where" value="GARCONFDATI.NGARA='${ngara }' and GARCONFDATI.ENTITA='XDPRE'"/>
		<c:set var="entita" value="GARCONFDATI"/>
		<c:set var="ordinamento" value="4"/>
	</c:when>
	<c:otherwise>
		<c:set var="where" value="DYNCAM.DYNENT_NAME='XDPRE' and DYNCAM.DYNCAM_BASE=2 and DYNCAM.DYNCAM_SCH ='1'"/>
		<c:set var="entita" value="DYNCAM"/>
		<c:set var="ordinamento" value="2"/>
	</c:otherwise>
</c:choose>


<gene:template file="popup-template.jsp" gestisciProtezioni="false" >
	
	<gene:redefineInsert name="head" >
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.rowsorter-2.1.1.js"></script>
	</gene:redefineInsert>	
		
	
	<gene:setString name="titoloMaschera" value="Configura attributi aggiuntivi per le lavorazioni e forniture della gara" />
	

	<c:choose>
		<c:when test='${modalita eq "vis"}'>
			<c:set var="modo" value="APRI" scope="request" />
		</c:when>
		<c:otherwise>
			<c:set var="modo" value="MODIFICA" scope="request" />
		</c:otherwise>
	</c:choose>
	
	
	<gene:redefineInsert name="gestioneHistory" />	
	<gene:redefineInsert name="addHistory" />	
	<% // Ridefinisco il corpo della ricerca %>
	<gene:redefineInsert name="corpo">
	
	<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-pg-lista-scheda.jsp">
		<jsp:param name="entita" value="V_GARE_TORN"/>
		<jsp:param name="inputFiltro" value="CODGAR=T:${codiceGara}"/>
		<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
	</jsp:include>
	
	<br>
	
	<c:choose>
			<c:when test='${requestScope.RISULTATO eq "ERRORI"}'>
				Si sono presentati degli errori durante l'operazione di configurazione dei dati
			</c:when>
			<c:otherwise>
				<c:choose>
					<c:when test='${modalita eq "vis"}'>
						Nella lista sotto sono riportati gli attributi aggiuntivi per le lavorazioni e forniture della gara, i cui valori devono essere forniti dalle ditte concorrenti in sede di offerta.
						<c:if test="${bloccoModifica ne true }">
							<br>Per modificare la lista degli attributi premere il pulsante 'Modifica configurazione'.
							<br>Per modificare la sequenza degli attributi, trascinare le righe premendo con il mouse sulla prima colonna a sinistra.   
						</c:if>
					</c:when>
					<c:otherwise>
						Nella lista sotto sono riportati tutti i possibili attributi aggiuntivi che possono essere impiegati per le lavorazioni e forniture.
						<br>Selezionare gli attributi aggiuntivi che si intendono utilizzare per la gara corrente.
					</c:otherwise>
				</c:choose>
			</c:otherwise>
		</c:choose>
	<c:set var="numeroCampi" value="0"/>
		
  	<%// Creo la lista per gcap e dpre mediante la vista v_gcap_dpre%>
		<table class="lista">
					
			<tr>
				<td ${stileDati}>
  				<gene:formLista entita="${entita }" where="${where }" pagesize="20" tableclass="datilista" sortColumn="${ordinamento }" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupConfiguraDatiRichiestiDitte">
  					<gene:redefineInsert name="listaNuovo" />
					<gene:redefineInsert name="listaEliminaSelezione" />
											
					
					<gene:set name="titoloMenu">
						<a href='javascript:selezionaTutti(document.forms[0].campoSelezionato);' Title='Seleziona tutti'> <img src='${pageContext.request.contextPath}/img/ico_check.gif' height='15' width='15' alt='Seleziona tutti'></a>
						&nbsp;
						<a href='javascript:deselezionaTutti(document.forms[0].campoSelezionato);' Title='Deseleziona tutti'><img src='${pageContext.request.contextPath}/img/ico_uncheck.gif' height='15' width='15' alt='Deseleziona tutti'></a>
					</gene:set>
					<c:choose>
						<c:when test="${modalita ne 'vis'}">
							<c:set var="titoloMenu" value="Opzioni<center>${titoloMenu}</center>" />
							<c:set var="dim" value="50" />
						</c:when>
						<c:otherwise>
							<c:set var="titoloMenu" value=""/>
							<c:set var="dim" value="20" />
						</c:otherwise>
					</c:choose>
					<gene:campoLista title="${titoloMenu }" width="${dim }" visibile="true" >
						<c:if test="${currentRow >= 0}">
							<c:if test="${modalita ne 'vis'}">
								<c:set var="numeroCampi" value="${numeroCampi + 1}"/>
								<input type="checkbox" id="campoSelezionato_${currentRow + 1}" name="campoSelezionato" value="${gene:if(datiRiga.DYNCAM_DYNCAM_NAME eq datiRiga.GARCONFDATI_CAMPO,'1','2') }"  <c:if test='${datiRiga.DYNCAM_DYNCAM_NAME eq datiRiga.GARCONFDATI_CAMPO}'>checked="checked"</c:if> onchange="aggiornaValue(this,${ currentRow+1})"/>
							</c:if>
							<c:if test="${modalita eq 'vis'}">
								<img width="16" height="16" class="sort-handler" title="Trascina per cambiare l'ordine" alt="Trascina per cambiare l'ordine" src="${pageContext.request.contextPath}/img/cambia_ordine.png"/>
							</c:if>
							
						</c:if>
					</gene:campoLista>
						
					<c:choose>
						<c:when test='${modalita eq "vis"}'>
							<gene:campoLista campo="ID"  edit="true" visibile="false" />
							<gene:campoLista campo="NGARA"  visibile="false" />
							<gene:campoLista campo="NUMORD"  edit="true" visibile="false"/>
							<gene:campoLista campo="CAMPO"   />
							<gene:campoLista campo="DYNCAM_DESC"  entita= "DYNCAM" where="DYNCAM.DYNENT_NAME = GARCONFDATI.ENTITA and DYNCAM.DYNCAM_NAME = GARCONFDATI.CAMPO" />
							<gene:campoLista campo="FORMATO"   />
							<gene:campoLista campo="OBBLIGATORIO"    width="50"/>
							<gene:campoLista campo="DYNCAM_SCH_B"  entita= "DYNCAM" where="DYNCAM.DYNENT_NAME = GARCONFDATI.ENTITA and DYNCAM.DYNCAM_NAME = GARCONFDATI.CAMPO" visibile="false"/>
						</c:when>
						<c:otherwise>
							<gene:campoLista campo="DYNCAM_NUMORD" edit="true"  visibile="false" />
							<gene:campoLista campo="DYNENT_NAME"   edit="true" visibile="false"/>
							<gene:campoLista campo="DYNENT_TYPE"   visibile="false"/>
							<gene:campoLista campo="DYNCAM_NAME"   />
							<gene:campoLista campo="DYNCAM_DESC"   />
							<gene:campoLista campo="DYNCAM_DOM"  visibile="false" />
							<gene:campoLista campo="DYNCAM_TAB"  visibile="false" /> 
							<gene:campoLista campo="DYNCAM_FORM"  visibile="false" /> 
							<c:choose>
								<c:when test="${!empty datiRiga.DYNCAM_DYNCAM_TAB  && datiRiga.DYNCAM_DYNCAM_TAB ne ''}">
									<c:set var="formato" value="3" />
								</c:when>
								<c:when test="${datiRiga.DYNCAM_DYNCAM_DOM eq 'DATA_ELDA'}">
									<c:set var="formato" value="1" />
								</c:when>
								<c:when test="${datiRiga.DYNCAM_DYNCAM_DOM eq 'MONEY'}">
									<c:set var="formato" value="2" />
								</c:when>
								<c:when test="${datiRiga.DYNCAM_DYNCAM_DOM eq 'NOTE'}">
									<c:set var="formato" value="4" />
								</c:when>
								<c:when test="${empty datiRiga.DYNCAM_DYNCAM_DOM and fn:startsWith(datiRiga.DYNCAM_DYNCAM_FORM,'NU')}">
									<c:set var="temp" value="${datiRiga.DYNCAM_DYNCAM_FORM }"/>
									<c:if test="${fn:contains(temp,',') }">
										<c:set var="temp" value="${fn:replace(temp,',','.')}"/>
									</c:if>
									<c:set var="cifreDecimali" value="${fn:substringAfter(temp,'.')}"/>
									<fmt:formatNumber value="${cifreDecimali }" type="number" var="cifreDecimaliNumber"/>
									<c:choose>
										<c:when test="${cifreDecimaliNumber>0 }">
											<c:set var="formato" value="6" />
										</c:when>
										<c:otherwise>
											<c:set var="formato" value="5" />
										</c:otherwise>
									</c:choose>
								</c:when>
								<c:when test="${datiRiga.DYNCAM_DYNCAM_DOM eq 'SN_1'}">
									<c:set var="formato" value="7" />
								</c:when>
								<c:when test="${empty datiRiga.DYNCAM_DYNCAM_DOM and fn:startsWith(datiRiga.DYNCAM_DYNCAM_FORM,'VC')}">
									<c:set var="formato" value="8" />
								</c:when>
							</c:choose>
							<gene:campoLista campo="DYNCAM_SCH_B"   visibile="false"/>
							<!--  gene:campoLista campo="FORMATO"   entita= "GARCONFDATI" where = "GARCONFDATI.NGARA='${ngara }' and DYNCAM.DYNENT_NAME = GARCONFDATI.ENTITA and DYNCAM.DYNCAM_NAME = GARCONFDATI.CAMPO" ordinabile="false"/ -->
							<gene:campoLista campo="FORMATO_FIT"   ordinabile="false" value="${formato}" campoFittizio="true" definizione="T2;0;A1112;;G1FORMATCD"/>
							<gene:campoLista campo="FORMATO_NASCOSTO"  visibile="false" value="${formato}" campoFittizio="true" definizione="T2" edit="true"/>
							<gene:campoLista campo="OBBLIGATORIO"  edit="true" width="50" entita= "GARCONFDATI" where = "GARCONFDATI.NGARA='${ngara }' and DYNCAM.DYNENT_NAME = GARCONFDATI.ENTITA and DYNCAM.DYNCAM_NAME = GARCONFDATI.CAMPO" ordinabile="false"/>
							<gene:campoLista campo="CAMPO"  edit="true" visibile="false" entita= "GARCONFDATI" where = "GARCONFDATI.NGARA='${ngara }' and DYNCAM.DYNENT_NAME = GARCONFDATI.ENTITA and DYNCAM.DYNCAM_NAME = GARCONFDATI.CAMPO" />
							<gene:campoLista campo="CAMPO" visibile="false" value="${datiRiga.DYNCAM_DYNCAM_NAME }" edit="true"  campoFittizio="true" definizione="T40;"/>
							<gene:campoLista campo="NUMORD" visibile="false" edit="true" entita= "GARCONFDATI" where = "GARCONFDATI.NGARA='${ngara }' and DYNCAM.DYNENT_NAME = GARCONFDATI.ENTITA and DYNCAM.DYNCAM_NAME = GARCONFDATI.CAMPO" />
							<gene:campoLista campo="ID" visibile="false" edit="true" entita= "GARCONFDATI" where = "GARCONFDATI.NGARA='${ngara }' and DYNCAM.DYNENT_NAME = GARCONFDATI.ENTITA and DYNCAM.DYNCAM_NAME = GARCONFDATI.CAMPO" />
							<gene:campoLista campo="CAMPO_SEL" value="${gene:if(datiRiga.DYNCAM_DYNCAM_NAME eq datiRiga.GARCONFDATI_CAMPO,'1','2') }" campoFittizio="true" definizione="T2;" visibile="false" edit='true'/>
						</c:otherwise>
					</c:choose>		
					
										
					<input type="hidden" name=ngara id="ngara" value="${ngara }" />
					<input type="hidden" name="modalita" id="modalita" value="${modalita }" />
					<input type="hidden" name="salvaDati" id="salvaDati" value="2" />
					<input type="hidden" name="numeroCampi" id="numeroCampi" value="" />
					<input type="hidden" name="maxNumOrdEsistente" id="maxNumOrdEsistente" value="" />
					<input type="hidden" name="bloccoModifica" id="bloccoModifica" value="${bloccoModifica }" />
				</gene:formLista>
				</td>
			</tr>
			<tr>
				<td class="comandi-dettaglio"  colSpan="2">
					<c:choose>
						<c:when test="${modalita eq 'vis' }">
							<c:if test="${bloccoModifica ne true }">
								<INPUT type="button"  class="bottone-azione" value='Modifica configurazione' title='Modifica configurazione' onclick="javascript:modificaConfigurazione();">
							</c:if>
							<INPUT type="button"  class="bottone-azione" value='Chiudi' title='Chiudi' onclick="javascript:chiudi();">
						</c:when>
						<c:otherwise>
							<c:if test="${requestScope.RISULTATO ne 'ERRORI'}">
								<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:salvaLista();">
							</c:if>
							<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:annulla();">
						</c:otherwise>
					</c:choose>
					&nbsp;
				</td>
			</tr>
			
		</table>
  </gene:redefineInsert>
	<gene:javaScript>
	
	function chiudi(){
		window.close();
	}
	
	function modificaConfigurazione(){
		document.getElementById("modalita").value = 'mod';
		//document.forms[0].entita = 'DYNCAM';
		document.forms[0].pgLastSort.value="";
		listaConferma();
	}
	
	function annulla(){
		document.getElementById("modalita").value = 'vis';
		document.forms[0].pgLastSort.value="GARCONFDATI.NUMORD";
		listaAnnullaModifica();
	}
	
	function salvaLista(){
			//Si determina il massimo valore di GARCONFDATI.NUMORD fra le occorrenze selezionate 
			var numeroCampi=${numeroCampi };
			var maxNumOrdEsistente=0;
			for(i=0;i<numeroCampi;i++){
				var selezionato = $("#campoSelezionato_" +(i+1)).is(':checked');
				if(selezionato){
					var numord =  $("#GARCONFDATI_NUMORD_" + (i+1)).val();
					if(numord!= null && numord>maxNumOrdEsistente)
						maxNumOrdEsistente = numord;
				}
			}
			document.getElementById("maxNumOrdEsistente").value = maxNumOrdEsistente;
			document.getElementById("numeroCampi").value = ${numeroCampi};
 			document.getElementById("salvaDati").value = '1';
 			document.forms[0].pgLastSort.value="";
            listaConferma();
		}
	
		function aggiornaValue(oggetto,indice){
			if(oggetto.checked){
				oggetto.value='1';
				 $("#GARCONFDATI_OBBLIGATORIO_" + indice).removeAttr('disabled');
				 $("#CAMPO_SEL_" + indice).val('1');
			}else{
				oggetto.value='2';
				$("#GARCONFDATI_OBBLIGATORIO_" + indice).attr('disabled', 'disabled');
				$("#CAMPO_SEL_" + indice).val('2');
			}
		}
		
		function inizializzaObbligatorio(){
            var modalita= document.getElementById("modalita").value;
			if(modalita=='mod'){
				var numeroCampi=${numeroCampi };
				for(i=0;i<numeroCampi;i++){
					var selezionato = $("#campoSelezionato_" +(i+1)).is(':checked');
					if(!selezionato){
						$("#GARCONFDATI_OBBLIGATORIO_" + (i+1)).attr('disabled', 'disabled');
					}
				}
			}


		}
		inizializzaObbligatorio();
		
		
		
		function selezionaTutti(achkArrayCheckBox) {
		    if (achkArrayCheckBox) {
				  var arrayLen = "" + achkArrayCheckBox.length;
				  if(arrayLen != 'undefined') {
				    for (i = 0; i < achkArrayCheckBox.length; i++) {
				    	if(! achkArrayCheckBox[i].disabled){
					      achkArrayCheckBox[i].checked = true;
					     	$("#GARCONFDATI_OBBLIGATORIO_" + (i + 1)).removeAttr('disabled');
				 			$("#CAMPO_SEL_" + (i + 1)).val('1');	 
					     }
				    }
				  } else {
				  	if(! achkArrayCheckBox.disabled){
				      achkArrayCheckBox.checked = true;
				      $("#GARCONFDATI_OBBLIGATORIO_" + (i + 1)).removeAttr('disabled');
				 	  $("#CAMPO_SEL_" + (i + 1)).val('1');	 
				    }
				  }
		    }
		  }
		  
		 
		 function deselezionaTutti(achkArrayCheckBox) {
		    if (achkArrayCheckBox) {
		      var arrayLen = "" + achkArrayCheckBox.length;
			  if(arrayLen != 'undefined') {
		  	  for (i = 0; i < achkArrayCheckBox.length; i++) {
		  	      achkArrayCheckBox[i].checked = false;
		  	      $("#GARCONFDATI_OBBLIGATORIO_" + (i + 1)).attr('disabled', 'disabled');
					$("#CAMPO_SEL_" + (i + 1)).val('2');
		    	  }
		      } else {
		        if (achkArrayCheckBox){
		          achkArrayCheckBox.checked = false;
		          $("#GARCONFDATI_OBBLIGATORIO_" + (i + 1)).attr('disabled', 'disabled');
					$("#CAMPO_SEL_" + (i + 1)).val('2');
		        }
		      }	    
		    }
		  }
		  
		  <c:if test="${bloccoModifica ne true }">
		  //Si associa alla tabella della lista il plugin per effettuare lo spostamento delle righe
		  $("[id^=tabformLista]").rowSorter({
		  	handler: "img.sort-handler",
	        onDragStart: function(tbody, row, old_index) {
	        	$('.sort-handler').css('cursor','pointer');
	        },
	        onDrop: function(tbody, row, new_index, old_index) {
	            var ngara="${ngara }";
	            var bloccoModifica ="${bloccoModifica }";
	            var indicePartenza=1;
	            var indiceArrivo=1;
	            var direzione="asc";
	            if(new_index > old_index){
	            	indicePartenza += old_index;
	            	indiceArrivo += new_index;
	            }else if(new_index < old_index){
	            	indicePartenza += new_index;
	            	indiceArrivo += old_index;
	            	direzione = "desc"
	            }else{
	            	return;
	            }
	            var listaId="";
	            var listaNumord="";
	            for(i = indicePartenza; i <= indiceArrivo; i++){
	            	if(listaId!="")
	            		listaId += ",";
	            	listaId += $("#GARCONFDATI_ID_" + i).val();
	            	if(listaNumord!="")
	            		listaNumord += ",";
	            	listaNumord += $("#GARCONFDATI_NUMORD_" + i).val();
	            }
	           
	           	bloccaRichiesteServer();
				var href = '${contextPath }/pg/AggiornaNumOrdineConfigurazione.do?"+csrfToken+"&listaId=' + listaId + '&listaNumord=' + listaNumord + "&ngara=" + ngara + "&bloccoModifica=" + bloccoModifica + "&direzione="  + direzione;
				document.location.href = href;
			}
    	  });
    	  </c:if>
    	  
    	  
	</gene:javaScript>
	
	
</gene:template>

