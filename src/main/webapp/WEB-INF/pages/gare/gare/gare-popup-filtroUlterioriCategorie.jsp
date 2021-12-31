
<%
	/*
	 * Created on 22-11-2011
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
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty RISULTATO and RISULTATO eq "OK"}' >
		<script type="text/javascript">
			window.opener.document.forms[0].pgSort.value = "";
			window.opener.document.forms[0].pgLastSort.value = "";
			window.opener.document.forms[0].pgLastValori.value = "";
			var jspPath = window.opener.document.forms[0].jspPath.value;
			jspPath+="?AGGIORNAMENTO=OK"; //Serve ad indicare che quando si è giunti a tale popup l'eventuale calcolo del numero ordine degli operatori è stato gia' eseguito
			window.opener.document.forms[0].jspPath.value=jspPath;
			window.opener.bloccaRichiesteServer();
			window.opener.listaVaiAPagina(0);
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
     <c:when test='${not empty param.categoriaPrev}'>
             <c:set var="categoriaPrev" value="${param.categoriaPrev}"  />
     </c:when>
	<c:otherwise>
		<c:set var="categoriaPrev" value="${categoriaPrev}" />
	</c:otherwise>
</c:choose>	

<c:choose>
     <c:when test='${not empty param.classifica}'>
             <c:set var="classifica" value="${param.classifica}"  />
     </c:when>
	<c:otherwise>
		<c:set var="classifica" value="${classifica}" />
	</c:otherwise>
</c:choose>	


<c:choose>
     <c:when test='${not empty param.isGaraLottiConOffertaUnica}'>
             <c:set var="isGaraLottiConOffertaUnica" value="${param.isGaraLottiConOffertaUnica}"  />
     </c:when>
	<c:otherwise>
		<c:set var="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
	</c:otherwise>
</c:choose>	

<c:choose>
     <c:when test='${not empty param.tipoGara}'>
             <c:set var="tipoGara" value="${param.tipoGara}"  />
     </c:when>
	<c:otherwise>
		<c:set var="tipoGara" value="${tipoGara}" />
	</c:otherwise>
</c:choose>	

<c:choose>
     <c:when test='${not empty param.criterioRotazione}'>
             <c:set var="criterioRotazione" value="${param.criterioRotazione}"  />
     </c:when>
	<c:otherwise>
		<c:set var="criterioRotazione" value="${criterioRotazione}" />
	</c:otherwise>
</c:choose>	

<c:choose>
     <c:when test='${not empty param.garaElenco}'>
             <c:set var="garaElenco" value="${param.garaElenco}"  />
     </c:when>
	<c:otherwise>
		<c:set var="garaElenco" value="${garaElenco}" />
	</c:otherwise>
</c:choose>	

<c:choose>
     <c:when test='${not empty param.stazioneAppaltante}'>
             <c:set var="stazioneAppaltante" value="${param.stazioneAppaltante}"  />
     </c:when>
	<c:otherwise>
		<c:set var="stazioneAppaltante" value="${stazioneAppaltante}" />
	</c:otherwise>
</c:choose>	

<c:choose>
     <c:when test='${tipoGara eq 1}'>
        <c:set var="titoloCatPrevalente" value="Categoria prevalente"  />
        <c:set var="titoloUltCategorie" value="Ulteriori categorie"  />
     </c:when>
	<c:otherwise>
		<c:set var="titoloCatPrevalente" value="Prestazione principale"  />
        <c:set var="titoloUltCategorie" value="Prestazioni secondarie"  />
	</c:otherwise>
</c:choose>	

<c:set var="where" value="V_GARE_CATEGORIE.NGARA='${ngara}'" scope="request" />

<c:set var="modo" value="MODIFICA" scope="request" />

<c:set var="esisteClassificaForniture" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.EsisteClassificaCategoriaFunction", pageContext, "TAB1","G_035")}'/>
<c:set var="esisteClassificaServizi" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.EsisteClassificaCategoriaFunction", pageContext, "TAB1","G_036")}'/>
<c:set var="esisteClassificaLavori150" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.EsisteClassificaCategoriaFunction", pageContext, "TAB1","G_037")}'/>
<c:set var="esisteClassificaServiziProfessionali" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.EsisteClassificaCategoriaFunction", pageContext, "TAB1","G_049")}'/>

<c:set var="indiceRiga" value="-1"/>
<c:set var="numCambi" value="0"/>

<gene:template file="popup-template.jsp" gestisciProtezioni="false" schema="GARE">
	<gene:setString name="titoloMaschera" value="Imposta filtro operatori su categorie e classi della gara ${ngara}" />
	<gene:setString name="entita" value="${entita}" />
	<gene:redefineInsert name="corpo">
	
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
		
		<br>
		
		<table class="lista">
			<tr>
				<td>
					<table class="arealayout">
						<tr>
							<td>
								Selezionare le categorie o prestazioni della gara per cui gli operatori dell'elenco devono essere qualificati.
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td><gene:formLista entita="V_GARE_CATEGORIE" tableclass="datilista" gestisciProtezioni="true" sortColumn="7;5;6" where="${where}" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupFiltroUltCategorie" pagesize="0">
					<c:set var="oldData" value="${newData}"/>
					<c:set var="newData" value="${datiRiga.V_GARE_CATEGORIE_ISPREV }"/>
					
					<gene:campoLista campoFittizio="true" visibile="false">
					<%/* Nel caso in cui siano diversi inframezzo il titolo */%>
					<c:if test="${oldData != newData}">
						<td colspan="7">
							<c:if test='${datiRiga.V_GARE_CATEGORIE_ISPREV eq "1"}'>
								<b>${titoloCatPrevalente } </b>
							</c:if>
							<c:if test='${datiRiga.V_GARE_CATEGORIE_ISPREV eq "2"}'>
								<b>${titoloUltCategorie } </b>
							</c:if>
							
						</td>
					</tr>
							
					<tr class="odd">
					<c:set var="numCambi" value="${numCambi + 1}"/>
					</c:if>
				</gene:campoLista>
					
					<gene:campoLista title="Opzioni <br><center>${titoloMenu}</center>"	width="50">												 
						<c:if test="${currentRow >= 0}">
						<input type="checkbox" name="keys" id="keys-${currentRow}" value="${datiRiga.V_GARE_CATEGORIE_CATIGA};${currentRow}" onClick="bloccoSbloccoClassifica(this,${currentRow})"/>
						
						</c:if>
					</gene:campoLista>
					<gene:campoLista title="" width="22" >
						<c:choose>
							<c:when test="${ datiRiga.V_GARE_CATEGORIE_NUMLIV > '1'}">
								<img width="22" height="16" title="Categoria di livello ${datiRiga.V_GARE_CATEGORIE_NUMLIV}" alt="Categoria di livello ${datiRiga.V_GARE_CATEGORIE_NUMLIV}" src="${pageContext.request.contextPath}/img/livelloCategoria${datiRiga.V_GARE_CATEGORIE_NUMLIV}.gif"/>
							</c:when>
							<c:otherwise>
								&nbsp;
							</c:otherwise>
						</c:choose>
					</gene:campoLista>					
					<gene:campoLista campo="NGARA"  visibile="false"/>
					<gene:campoLista campo="NCATG" visibile="false"/>
					<gene:campoLista campo="NUMORD" visibile="false"/>
					<gene:campoLista campo="ISPREV" visibile="false" edit="true"/>
					<gene:campoLista campo="CATIGA" ordinabile="false"/>
					<gene:campoLista campo="DESCAT" title="Descrizione" ordinabile="false"/>
					<gene:campoLista campo="NUMCLA" ordinabile="false" edit="true" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoClassificaCategoria" />
					<gene:campoLista campo="ACONTEC" ordinabile="false" visibile="false"/>
					<gene:campoLista campo="QUAOBB" ordinabile="false" visibile="false"/>
					<gene:campoLista campo="CATIGA_FIT" visibile ="false" edit="true" campoFittizio="true" value="${datiRiga.V_GARE_CATEGORIE_CATIGA}" definizione="T30"/>
					<gene:campoLista campo="TIPLAVG" visibile="false" edit="true"/>
					
					<%/* La variabile numCambi serve per poter mantendere il layout */%>
					<c:set var="indiceRiga" value="${indiceRiga + 1}"/>
					
					<%/* Questa parte di codice setta lo stile della riga in base che sia un titolo oppure una riga di dati */%>
					<gene:campoLista visibile="false" >
						<th style="display:none">
							<c:if test="${oldData != newData}"><script type="text/javascript">
								var nomeForm = document.forms[0].name;
								var indice = ${indiceRiga};
		                        /*
								if(indice==2){
		                        	indice =  indice -1 ;
		                        }
		                        */                    
		                        document.getElementById("tab" + nomeForm).rows[indice  + (${numCambi } )].className =document.getElementById("tab" + nomeForm).rows[indice  + (${numCambi }  ) - 1].className;
								document.getElementById("tab" + nomeForm).rows[indice  + (${numCambi } ) - 1 ].className = "white";
							</script></c:if>
						</th>
					</gene:campoLista>
					<gene:campoLista visibile="false">
			             <th style="display:none">
					         <c:if test="${datiRiga.V_GARE_CATEGORIE_ISFOGLIA eq '2'}">
					         <c:set var="numliv" value="${datiRiga.V_GARE_CATEGORIE_NUMLIV}"/>
				                 <script type="text/javascript">
					                 var nomeForm = document.forms[0].name;
		 							 var indice = ${indiceRiga};
			 						/*	
		 							 if(indice==2){
				                        	indice =  indice -1 ;
				                        }
		 							*/ 
			 						document.getElementById("tab" + nomeForm).rows[indice  + (${numCambi } )].className = "livello"+${numliv};
				                 </script>
				             </c:if>
			             </th>
				     </gene:campoLista>
					<gene:campoLista campo="ISFOGLIA"  visibile = "false" edit="true"/>
					<gene:campoLista campo="NUMLIV"  visibile = "false"/>
					
					<input type="hidden" name="ngara" id="ngara" value="${ngara}" />
                    <input type="hidden" name="categoriaPrev" id="categoriaPrev" value="${categoriaPrev}" />
                    <input type="hidden" name="classifica" id="classifica" value="${classifica}" />
                    <input type="hidden" name="isGaraLottiConOffertaUnica" id="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
                    <input type="hidden" name="numclaCatPrev" id="numclaCatPrev" value="" />
                    <input type="hidden" name="tipoGara" id="tipoGara" value="${tipoGara }" />
                    <input type="hidden" name="criterioRotazione" id="criterioRotazione" value="${criterioRotazione }" />
                    <input type="hidden" name="garaElenco" id="garaElenco" value="${garaElenco }" />
                    <input type="hidden" name="stazioneAppaltante" id="stazioneAppaltante" value="${stazioneAppaltante }" />
                </gene:formLista></td>
			</tr>
			<tr>
				<td class="comandi-dettaglio"  colSpan="2">
					<INPUT type="button"  class="bottone-azione" value='Imposta filtro' title='Imposta filtro' onclick="javascript:aggiungiFiltro();">&nbsp;&nbsp;&nbsp;
					<INPUT type="button"  class="bottone-azione" value='Chiudi' title='Chiudi' onclick="javascript:chiudi();">
								
				</td>
			</tr>			
		</table>
	</gene:redefineInsert>
	<gene:javaScript>
		
				
		function aggiungiFiltro(){
			var numeroOggetti = contaCheckSelezionati(document.forms[0].keys);
	  		/*
	  		var criterioRotazione = "${criterioRotazione }";
	  		if(criterioRotazione == "1" || criterioRotazione == "3" || criterioRotazione == "4" || criterioRotazione == "5")
	  			numeroOggetti--;
	  		*/
	  		
	  		if (numeroOggetti == 0 ) {
	      		alert("Selezionare almeno una categoria nella lista");
	      	} else if(numeroOggetti > 10){
	      		 alert("Non è possibile selezionare più di 10 categorie nella lista");
	      	} else {
	      		if(document.getElementById("V_GARE_CATEGORIE_NUMCLA_1")!=null)
	      			document.getElementById("V_GARE_CATEGORIE_NUMCLA_1").disabled = false;
	      		if(document.getElementById("keys-0")!=null)
	      			document.getElementById("keys-0").disabled= false;
	      		else
	      			document.getElementById("keys").disabled= false;
	      		listaConferma();
 			}
		}
		
		
		function chiudi(){
			window.close();
		}
		
		//Prelevo dalla variabile di sessione "elencoUlterioriCategorie"
		//la lista delle categorie selezionate in precedenza e 
		//imposto i corrispondenti check sulla lista
		function inizializzaLista(){
			var elencoUlterioriCategorie = "${elencoUlterioriCategorie }";
			var numeroCategorie = ${currentRow}+1;
			var elencoNumcla="${elencoNumcla }";
			var vetElencoNumcla;
			if(elencoNumcla!=null && elencoNumcla!="") 
				vetElencoNumcla = elencoNumcla.split(',');
			
			var prevalenteSelezionata = "${prevalenteSelezionata }";
			if(prevalenteSelezionata== null || prevalenteSelezionata=="")
				prevalenteSelezionata="si";
			var criterioRotazione ="${criterioRotazione }";
			
			if(elencoUlterioriCategorie!=null && elencoUlterioriCategorie!=""){
				var vetCatSelezionate = elencoUlterioriCategorie.split(',');
								
				for(var t=0; t < numeroCategorie; t++){
					var check = document.getElementById("keys-" + t).value;
					var vetValoriCheck = check.split(';');
                    var codiceCheck = vetValoriCheck[0];
                    var i = t + 1;
					var isprev = document.getElementById("V_GARE_CATEGORIE_ISPREV_" + i).value;
					if(isprev == 1){
						if(prevalenteSelezionata == "" || prevalenteSelezionata == "si")
							document.getElementById("keys-" + t).checked = "checked";
						
						
												
						if(vetElencoNumcla!= null && vetElencoNumcla.length>0 && prevalenteSelezionata == "si"){
							document.getElementById("V_GARE_CATEGORIE_NUMCLA_" + i).value = vetElencoNumcla[0];
						}else
							document.getElementById("V_GARE_CATEGORIE_NUMCLA_" + i).value = "";
						
						if(criterioRotazione == "1" || criterioRotazione == "3" || criterioRotazione== "4" || criterioRotazione== "5" || criterioRotazione== "11" || criterioRotazione== "12" || prevalenteSelezionata == "")
							document.getElementById("keys-" + t).disabled = true;
						
						
						document.getElementById("numclaCatPrev").value = document.getElementById("V_GARE_CATEGORIE_NUMCLA_" + i).value;
					}else{
						for(var j=0;j<vetCatSelezionate.length;j++){
							if(codiceCheck == vetCatSelezionate[j]){
								document.getElementById("keys-" + t).checked = "checked";
								if(vetElencoNumcla!= null && vetElencoNumcla.length>0){
									document.getElementById("V_GARE_CATEGORIE_NUMCLA_" + i).value = vetElencoNumcla[j+1];
								}
								break;
							}	
						}
					} 
				}
			}else{
				//E' selezionata solo la categoria prevalente
				for(var t=0; t < numeroCategorie; t++){
					var i = t + 1;
					var isprev = document.getElementById("V_GARE_CATEGORIE_ISPREV_" + i).value;
					if(isprev == 1){
						document.getElementById("keys-" + t).checked = "checked";
						if(criterioRotazione == "1" || criterioRotazione == "3" || criterioRotazione== "4" || criterioRotazione== "5" || criterioRotazione== "11" || criterioRotazione== "12" || prevalenteSelezionata == "no") 
							document.getElementById("keys-" + t).disabled = true;
						
						//Si deve controllare se è stata variato il valore di numcla rispetto al valore in db
						if(vetElencoNumcla!= null && vetElencoNumcla.length>0){
							document.getElementById("V_GARE_CATEGORIE_NUMCLA_" + i).value = vetElencoNumcla[0];
						}else{
							document.getElementById("V_GARE_CATEGORIE_NUMCLA_" + i).value="";
						}
						document.getElementById("numclaCatPrev").value = document.getElementById("V_GARE_CATEGORIE_NUMCLA_" + i).value;
						break;
					}
				}
			}
			inizilizzaBloccoSbloccoClassifica(numeroCategorie);
			associaFunzioniEventoOnchange();
		}
		
		inizializzaLista();
		
		//Dalla funzione che deseleziona tutti i check si deve eliminare la
		//gestione della categoria prevalente
		function deselezionaTutti(objArrayCheckBox){
			for (i = 0; i < objArrayCheckBox.length; i++) {
		      var t = i + 1;
			  var isprev = document.getElementById("V_GARE_CATEGORIE_ISPREV_" + t).value;
			  var tiplavg = document.getElementById("V_GARE_CATEGORIE_TIPLAVG_" + t).value;
			  
			  if(isprev != 1){
		      	objArrayCheckBox[i].checked = false;
		      	if(tiplavg == 1 || (tiplavg == 2 && ${esisteClassificaForniture}) 
		      		|| (tiplavg == 3 && ${esisteClassificaServizi}) || (tiplavg == 4 && ${esisteClassificaLavori150})
		      		|| (tiplavg == 5 && ${esisteClassificaServiziProfessionali}))
		      		document.getElementById("V_GARE_CATEGORIE_NUMCLA_" + t).disabled = true;
		      }
		    }
		}
		
		function inizilizzaBloccoSbloccoClassifica(numeroCategorie){
			for(var t=0; t < numeroCategorie; t++){
				var i = t + 1;
				var tiplavg = document.getElementById("V_GARE_CATEGORIE_TIPLAVG_" + i).value;
				var isfoglia = document.getElementById("V_GARE_CATEGORIE_ISFOGLIA_" + i).value;
				
				if(isfoglia == 2){
					showObj("V_GARE_CATEGORIE_NUMCLA_" + i, false);
				}else{
					if(document.getElementById("keys-" + t).checked && 
						(tiplavg == 1 || (tiplavg == 2 && ${esisteClassificaForniture})
						 || (tiplavg == 3 && ${esisteClassificaServizi}) || (tiplavg == 4 && ${esisteClassificaLavori150})
						 || (tiplavg == 5 && ${esisteClassificaServiziProfessionali}))){
						document.getElementById("V_GARE_CATEGORIE_NUMCLA_" + i).disabled = false;
						showObj("V_GARE_CATEGORIE_NUMCLA_" + i, true);
					}else{
						document.getElementById("V_GARE_CATEGORIE_NUMCLA_" + i).disabled = true;
						if(tiplavg == 1 || (tiplavg == 2 && ${esisteClassificaForniture})
						 || (tiplavg == 3 && ${esisteClassificaServizi}) || (tiplavg == 4 && ${esisteClassificaLavori150})
						 || (tiplavg == 5 && ${esisteClassificaServiziProfessionali}))
							showObj("V_GARE_CATEGORIE_NUMCLA_" + i, true);
						else
							showObj("V_GARE_CATEGORIE_NUMCLA_" + i, false);
					}
				}
			}
		}
		
		function bloccoSbloccoClassifica(obj,indice){
			var i= indice + 1;
			var tiplavg = document.getElementById("V_GARE_CATEGORIE_TIPLAVG_" + i).value;
			
			if((tiplavg == 1 || (tiplavg == 2 && ${esisteClassificaForniture}) 
				|| (tiplavg == 3 && ${esisteClassificaServizi}) || (tiplavg == 4 && ${esisteClassificaLavori150})
				|| (tiplavg == 5 && ${esisteClassificaServiziProfessionali})) && obj.checked)
				document.getElementById("V_GARE_CATEGORIE_NUMCLA_" + i).disabled = false;
			else
				document.getElementById("V_GARE_CATEGORIE_NUMCLA_" + i).disabled = true;
			
		}
		
		function selezionaTutti(objArrayCheckBox) {
	    	for (i = 0; i < objArrayCheckBox.length; i++) {
	      		objArrayCheckBox[i].checked = true;
	      		var t = i + 1;
	      		var tiplavg = document.getElementById("V_GARE_CATEGORIE_TIPLAVG_" + t).value;
	      		if((tiplavg == 1 || (tiplavg == 2 && ${esisteClassificaForniture})
	      			 || (tiplavg == 3 && ${esisteClassificaServizi}) || (tiplavg == 4 && ${esisteClassificaLavori150}))
	      			 || (tiplavg == 5 && ${esisteClassificaServiziProfessionali}))
	      			document.getElementById("V_GARE_CATEGORIE_NUMCLA_" + t).disabled = false;
	    	}
	    }
	    
	    function associaFunzioniEventoOnchange(){
			for(var i=1; i <= ${currentRow}+1; i++){
				document.getElementById("V_GARE_CATEGORIE_NUMCLA_" + i).onchange = aggiornaNumclaHidden;
			}
		}
 		
 		function aggiornaNumclaHidden(){
 			var valore = this.value;
			var objId = this.id;
            var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
			var isprev = document.getElementById("V_GARE_CATEGORIE_ISPREV_" + numeroRiga).value;
			if(isprev == 1){
				document.getElementById("numclaCatPrev").value = valore;
			}
			
 		}
 		
	</gene:javaScript>
</gene:template>
</c:otherwise>
</c:choose>