<%--
/*
 * Created on: 10-apr-2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
Scheda categorie d'iscrizione

--%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="opzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" scope="request"/>
<c:set var="id" value='${gene:getValCampo(key, "CAISIM")}'/>

<c:choose>
	<c:when test='${modo ne "NUOVO"}'>
		<c:set var="isBeniServiziCineca" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsBeniServiziCinecaFunction",pageContext,id)}' />
	</c:when>
	<c:otherwise>
		<c:set var="isBeniServiziCineca" value='false' />
	</c:otherwise>
</c:choose>


<c:if test='${modo=="MODIFICA"}'>
	<c:set var="isPadre"
		value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.CheckIsCategoriaPadreFunction",pageContext,id)}' />
</c:if>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="CAIS-scheda">
	<gene:setString name="titoloMaschera" value="Dettaglio categoria d'iscrizione" />
	<gene:redefineInsert name="corpo">
	
		<gene:formScheda entita="CAIS" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreCAIS">
			<gene:campoScheda campo="CAISIM" keyCheck="true" modificabile='${modoAperturaScheda eq "NUOVO"}' obbligatorio="true" />
			<gene:campoScheda campo="DESCAT" />
			<gene:campoScheda campo="ISARCHI" definizione="T1"  obbligatorio="true" defaultValue="2"/>
			<gene:campoScheda campo="TITCAT" />
			<gene:campoScheda campo="QUAOBB"  gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoSiNoGrigio" definizione="T1" defaultValue="0" visibile="${param.TIPO eq 1}" />
			<gene:campoScheda campo="ACONTEC" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoSiNoGrigio" definizione="T1" defaultValue="0" visibile="${param.TIPO eq 1}" />
			<gene:campoScheda campo="TIPLAVG" visibile="false" defaultValue="${param.TIPO}" />
			<c:if test="${param.TIPO ne 1}">
				<gene:campoScheda>
					<td colspan="2"><b>Categorie padre<b></td>
				</gene:campoScheda>
				<gene:campoScheda campo="CODLIV1" defaultValue="${param.LIV1}" visibile="false"/>
				<c:if test="${modo eq 'VISUALIZZA'}">
					<c:set var="descat1" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetDescatCaisFunction", pageContext, datiRiga.CAIS_CODLIV1)}' />
					<c:if test="${!empty descat1 }">
						<c:set var="descat1" value='- ${descat1 }' />
					</c:if>
				</c:if>
				<gene:campoScheda campo="CODLIV1_DESCAT" title="Cod.categoria liv.1" definizione="T100" campoFittizio="true" value="${datiRiga.CAIS_CODLIV1} ${descat1}" visibile="${modo eq 'VISUALIZZA' }"/>
				<gene:campoScheda campo="CODLIV1_FIT" title="Cod.categoria liv.1" definizione="T50" campoFittizio="true" defaultValue="${param.LIV1}" visibile="${modo ne 'VISUALIZZA' }">
					<gene:addValue value="" descr="" />
				</gene:campoScheda>
				
				<gene:campoScheda campo="CODLIV2" defaultValue="${param.LIV2}" visibile="${false }"/>
				<c:if test="${modo eq 'VISUALIZZA'}">
					<c:set var="descat2" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetDescatCaisFunction", pageContext, datiRiga.CAIS_CODLIV2)}' />
					<c:if test="${!empty descat2 }">
						<c:set var="descat2" value='- ${descat2}' />
					</c:if>
				</c:if>
				<gene:campoScheda campo="CODLIV2_DESCAT" title="Cod.categoria liv.2" definizione="T100" campoFittizio="true" value="${datiRiga.CAIS_CODLIV2} ${descat2}" visibile="${modo eq 'VISUALIZZA' }"/>
				<gene:campoScheda campo="CODLIV2_FIT" title="Cod.categoria liv.2" definizione="T50" campoFittizio="true" defaultValue="${param.LIV2}" visibile="${modo ne 'VISUALIZZA' }">
					<gene:addValue value="" descr="" />
				</gene:campoScheda>
				
				<gene:campoScheda campo="CODLIV3" defaultValue="${param.LIV3}" visibile="${false }"/>
				<c:if test="${modo eq 'VISUALIZZA'}">
					<c:set var="descat3" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetDescatCaisFunction", pageContext, datiRiga.CAIS_CODLIV3)}' />
					<c:if test="${!empty descat3 }">
						<c:set var="descat3" value='- ${descat3}' />
					</c:if>
				</c:if>
				<gene:campoScheda campo="CODLIV3_DESCAT" title="Cod.categoria liv.3" definizione="T100" campoFittizio="true" value="${datiRiga.CAIS_CODLIV3} ${descat3}" visibile="${modo eq 'VISUALIZZA' }"/>
				<gene:campoScheda campo="CODLIV3_FIT" title="Cod.categoria liv.3" definizione="T50" campoFittizio="true" defaultValue="${param.LIV3}" visibile="${modo ne 'VISUALIZZA' }">
					<gene:addValue value="" descr="" />
				</gene:campoScheda>
				
				<gene:campoScheda campo="CODLIV4" defaultValue="${param.LIV4}" visibile="${false}"/>
				<c:if test="${modo eq 'VISUALIZZA'}">
					<c:set var="descat4" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetDescatCaisFunction", pageContext, datiRiga.CAIS_CODLIV4)}' />
					<c:if test="${!empty descat4 }">
						<c:set var="descat4" value='- ${descat4}' />
					</c:if>
				</c:if>
				<gene:campoScheda campo="CODLIV4_DESCAT" title="Cod.categoria liv.4" definizione="T100" campoFittizio="true" value="${datiRiga.CAIS_CODLIV4} ${descat4}" visibile="${modo eq 'VISUALIZZA' }"/>
				<gene:campoScheda campo="CODLIV4_FIT" title="Cod.categoria liv.4" definizione="T50" campoFittizio="true" defaultValue="${param.LIV4}" visibile="${modo ne 'VISUALIZZA' }">
					<gene:addValue value="" descr="" />
				</gene:campoScheda>
				
			</c:if>
			
			<gene:campoScheda campo="CAISORD" visibile="false"/>

			<c:if test='${isBeniServiziCineca eq "true"}'>

				<c:set var="categoria" value='${gene:getValCampo(key, "CAIS.CAISIM")}' />
				<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GetValoriBeniServiziFunction" parametro="${categoria}" />
				
				<gene:campoScheda addTr="false">
					<tr id="rowTITOLO_BENI_SERVIZI">
						<td colspan="2"><b>Beni e servizi della categoria</b></td>
					</tr>
				</gene:campoScheda>
				<gene:campoScheda addTr="false" >
					<tr>
					<td colspan="2">
					<table id="tabellaBeniServizi" class="griglia">
				</gene:campoScheda>
						
				<jsp:include page="/WEB-INF/pages/commons/interno-sezione-multipla-singola-riga.jsp" >
					<jsp:param name="entita" value='T_UBUY_BENISERVIZI'/>
					<jsp:param name="chiave" value='${categoria}'/>
					<jsp:param name="nomeAttributoLista" value='datiBeniServizi' />
					<jsp:param name="idProtezioni" value="BENISERVIZI" />
					<jsp:param name="sezioneListaVuota" value="true" />
					<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gene/cais/beniservizi-interno-scheda.jsp"/>
					<jsp:param name="arrayCampi" value="'T_UBUY_BENISERVIZI_CODCAT_', 'T_UBUY_BENISERVIZI_NUM_BS_', 'T_UBUY_BENISERVIZI_COD_BS_', 'T_UBUY_BENISERVIZI_DES_BS_','T_UBUY_BENISERVIZI_DESEST_CAT_'"/>
					<jsp:param name="titoloSezione" value="<br>Bene servizio" />
					<jsp:param name="titoloNuovaSezione" value="<br>Nuovo bene servizio" />
					<jsp:param name="descEntitaVociLink" value="bene servizio" />
					<jsp:param name="msgRaggiuntoMax" value="e beni servizi"/>
					<jsp:param name="usaContatoreLista" value="true"/>
					<jsp:param name="numMaxDettagliInseribili" value="5"/>
					<jsp:param name="sezioneInseribile" value="true"/>
					<jsp:param name="sezioneEliminabile" value="true"/>
				</jsp:include>
				
				<gene:campoScheda addTr="false" >
					</table>
					</td>
				</tr>
				</gene:campoScheda>
			</c:if>

			<gene:campoScheda campo="MODOAPERTURA" campoFittizio="true" definizione="T20" value="${modo}" visibile="false"/>
			
			<input type="hidden" name="TIPO" value="${param.TIPO}" />
			<input type="hidden" name="isPadre" value="${isPadre}" />

			<gene:campoScheda>	
				<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
			</gene:campoScheda>
		</gene:formScheda>
	</gene:redefineInsert>
	<gene:javaScript>
		var titoliCategorie = [];
		var schedaConfermaOld=schedaConferma;
		
		schedaConferma=function(){
			var isarchi=getValue("CAIS_ISARCHI");
			var isPadre="${isPadre}";
			if(isarchi=="1"){
				var isarchiOriginale = getOriginalValue("CAIS_ISARCHI");
				if(isarchiOriginale==null || isarchiOriginale==0)
					isarchiOriginale=0;
				if(isarchiOriginale!=isarchi){
					var msg="La categoria corrente viene archiviata";
					if(isPadre=="true")
						msg+=" assieme alle categorie figlie";
					msg+="\nConfermi le modifiche?";
					var esito = confirm(msg);
					if(esito!=true)
						return -1;
				}
				
			}else{
				var isarchiOriginale = getOriginalValue("CAIS_ISARCHI");
				if(isarchiOriginale==null || isarchiOriginale==0)
					isarchiOriginale=0;
				if(isPadre=="true" && isarchiOriginale!=isarchi){
					var msg="Anche per le categorie figlie verrà tolto il segno di spunta dal flag 'Categoria archiviata'.";
					msg+="\nConfermi le modifiche?";
					var esito = confirm(msg);
					if(esito!=true)
						return -1;
				}
			}
			//popolamento dei campi CODLIV con i rispettivi valori di COLDIV_FIT
			setValue("CAIS_CODLIV1",getValue("CODLIV1_FIT"));
			setValue("CAIS_CODLIV2",getValue("CODLIV2_FIT"));
			setValue("CAIS_CODLIV3",getValue("CODLIV3_FIT"));
			setValue("CAIS_CODLIV4",getValue("CODLIV4_FIT"));
			document.getElementById("CAIS_TITCAT").disabled=false;
			schedaConfermaOld();
		}
		
				
		$(document).ready(function() {
			var modoapertura = $("#MODOAPERTURA").val();
			var caisim = $("#CAIS_CAISIM").val();
			var tiplavg = $("#CAIS_TIPLAVG").val();
			var isarchi = $("#CAIS_ISARCHI").val();
			LIV1 = "${param.LIV1}";
			LIV2 = "${param.LIV2}";
			LIV3 = "${param.LIV3}";
			LIV4 = "${param.LIV4}";
		    codliv1Originale="${datiRiga.CAIS_CODLIV1 }";
		    codliv2Originale="${datiRiga.CAIS_CODLIV2 }";
		    codliv3Originale="${datiRiga.CAIS_CODLIV3 }";
		    codliv4Originale="${datiRiga.CAIS_CODLIV4 }";
			
			//Funzione per il popolamento del campo Fittizio CODLIV1						
			function inizializzazioneCODLIV1(){
				var codliv1="";
				var codliv2="";
				var codliv3="";
				var codliv4="";
							            
	            if (modoapertura != 'VISUALIZZA' && tiplavg!=1) {                
	                $.ajax({
	                    type: "GET",
	                    dataType: "json",
	                    /*async: false,*/
	                    beforeSend: function(x) {
	        			if(x && x.overrideMimeType) {
	            			x.overrideMimeType("application/json;charset=UTF-8");
					       }
	    				},
	                    url: "pg/GetDatoCODLIV.do",
	                    data: "codice=" + caisim + "&codliv1=" + codliv1 + "&codliv2=" + codliv2 + "&codliv3=" + codliv3 + "&codliv4=" + codliv4 +"&tipo=" + tiplavg + "&livello=1&isarchi=" + isarchi,
	                    success: function(msg){ 
	                    	popolaCodliv(msg,"CODLIV1_FIT")},
	                    error: function(e){
	                        alert("Errore durante la lettura delle categorie per popolare CODLIV1");
	                    }
	                });
	            }
	       };
	       
	       if (modoapertura != 'VISUALIZZA' && tiplavg!=1) {
		       inizializzazioneCODLIV1();
		       
		       //Se è sta lanciata la funzione "Nuova con pari livello", allora si deve caricare in automatico il valore delle dropdown dei livelli valorizzati e si deve
		       //impostare in tali dropdown a "selected" i valori corrispondenti ai LIV valorizzati, e lo stesso se si apre l'a pagina in modifica
		       
		       if(LIV1!=null && LIV1!="" ||(codliv1Originale!=null && codliv1Originale!=""))
		       	inizializzazioneCODLIV2();
		       if(LIV2!=null && LIV2!="" ||(codliv2Originale!=null && codliv2Originale!=""))
		       	inizializzazioneCODLIV3();
		       if(LIV3!=null && LIV3!="" ||(codliv3Originale!=null && codliv3Originale!=""))
		       	inizializzazioneCODLIV4();
		       	
		       	var codliv1 = $("#CODLIV1_FIT").val();
				bloccaSbloccaTitcat(codliv1,1,false);
		    }
	       
	       	       
	       //Alla modifica del campo fittizio CODLIV1 si deve aggiornare la dropdown associata a CODLIV2,
	       //se viene scelto il valore nullo, allora si devono sbiancare tutte le dropdown
	       $('#CODLIV1_FIT').change(
			function() {
				codliv2Originale="";
		    	codliv3Originale="";
		    	codliv4Originale="";
				inizializzazioneCODLIV2();
				var codliv1 = $("#CODLIV1_FIT").val();
				bloccaSbloccaTitcat(codliv1,1,true);
				
				//Si deve inizializzare il valore del campo CAIS.TITCAT col valore del titolo selezionato dalla dropdown
				var indice = this.selectedIndex - 1;
				if(indice==-1){
					setValue("CAIS_TITCAT","");
				}else{
					setValue("CAIS_TITCAT",titoliCategorie[indice]);
				}
			}	
           ); 
			
			
			//Funzione per il popolamento del campo Fittizio CODLIV2
			function inizializzazioneCODLIV2() {
				//Si svuotano le dropdwon associate a CODLIV3,CODLIV4
				var options = '<option value=""></option>';
				$("select#CODLIV3_FIT").html(options);
				$("select#CODLIV4_FIT").html(options);
				
				var codliv1 = $("#CODLIV1_FIT").val();
				if(codliv1== null || codliv1=="" ){
					//Si svuota la dropdwon associata a CODLIV2
					$("select#CODLIV2_FIT").html(options);
				}else{
				
					var codliv2="";
					var codliv3="";
					var codliv4="";
								            
		            if (modoapertura != 'VISUALIZZA' && tiplavg!=1) {                
		                $.ajax({
		                    type: "GET",
		                    dataType: "json",
		                    /*async: false,*/
		                    beforeSend: function(x) {
		        			if(x && x.overrideMimeType) {
		            			x.overrideMimeType("application/json;charset=UTF-8");
						       }
		    				},
		                    url: "pg/GetDatoCODLIV.do",
		                    data: "codice=" + caisim + "&codliv1=" + codliv1 + "&codliv2=" + codliv2 + "&codliv3=" + codliv3 + "&codliv4=" + codliv4 +"&tipo=" + tiplavg + "&livello=2&isarchi=" + isarchi,
		                    success: function(msg){
			                	popolaCodliv(msg,"CODLIV2_FIT");
		                    },
		                    error: function(e){
		                        alert("Errore durante la lettura delle categorie per popolare CODLIV2");
		                    }
		                });
		            }
				}
           	};
			
			//Funzione per il popolamento del campo Fittizio CODLIV3
			function inizializzazioneCODLIV3(){
				//Si svuota la dropdwon associata a CODLIV4
				var options = '<option value=""></option>';
				$("select#CODLIV4_FIT").html(options);
				
				var codliv1 = $("#CODLIV1_FIT").val();
				var codliv2 = $("#CODLIV2_FIT").val();
				if(codliv2== null || codliv2=="" ){
					$("select#CODLIV3_FIT").html(options);
				}else{
									
					var codliv3="";
					var codliv4="";
								            
		            if (modoapertura != 'VISUALIZZA' && tiplavg!=1) {                
		                $.ajax({
		                    type: "GET",
		                    dataType: "json",
		                    /*async: false,*/
		                    beforeSend: function(x) {
		        			if(x && x.overrideMimeType) {
		            			x.overrideMimeType("application/json;charset=UTF-8");
						       }
		    				},
		                    url: "pg/GetDatoCODLIV.do",
		                    data: "codice=" + caisim + "&codliv1=" + codliv1 + "&codliv2=" + codliv2 + "&codliv3=" + codliv3 + "&codliv4=" + codliv4 +"&tipo=" + tiplavg + "&livello=3&isarchi=" + isarchi,
		                    success: function(msg){
			                	popolaCodliv(msg,"CODLIV3_FIT");
		                    },
		                    error: function(e){
		                        alert("Errore durante la lettura delle categorie per popolare CODLIV3");
		                    }
		                });
		            }
				}
           	};
			
			
		   //Alla modifica del campo fittizio CODLIV2 si deve aggiornare la dropdown associata a CODLIV3,
	       //se viene scelto il valore nullo, allora si devono sbiancare tutte le dropdown seguenti
	       $('#CODLIV2_FIT').change(
			function() {
				codliv3Originale="";
		    	codliv4Originale="";
				inizializzazioneCODLIV3();
           	}
           ); 
			
			//Funzione per il popolamento del campo Fittizio CODLIV4	
			function inizializzazioneCODLIV4(){
				var codliv1 = $("#CODLIV1_FIT").val();
				var codliv2 = $("#CODLIV2_FIT").val();
				var codliv3 = $("#CODLIV3_FIT").val();
				if(codliv3== null || codliv3=="" ){
					//Si svuota la dropdwon associata a CODLIV4
					var options = '<option value=""></option>';
					$("select#CODLIV4_FIT").html(options);
				}else{
					
					var codliv4="";
								            
		            if (modoapertura != 'VISUALIZZA' && tiplavg!=1) {                
		                $.ajax({
		                    type: "GET",
		                    dataType: "json",
		                    /*async: false,*/
		                    beforeSend: function(x) {
		        			if(x && x.overrideMimeType) {
		            			x.overrideMimeType("application/json;charset=UTF-8");
						       }
		    				},
		                    url: "pg/GetDatoCODLIV.do",
		                    data: "codice=" + caisim + "&codliv1=" + codliv1 + "&codliv2=" + codliv2 + "&codliv3=" + codliv3 + "&codliv4=" + codliv4 +"&tipo=" + tiplavg + "&livello=4&isarchi=" + isarchi,
		                    success: function(msg){
			                	popolaCodliv(msg,"CODLIV4_FIT");
		                    },
		                    error: function(e){
		                        alert("Errore durante la lettura delle categorie per popolare CODLIV4");
		                    }
		                });
		            }
				}
           	}; 
			
		    //Alla modifica del campo fittizio CODLIV3 si deve aggiornare la dropdown associata a CODLIV4,
	       //se viene scelto il valore nullo, allora si devono sbiancare tutte le dropdown seguenti
	       $('#CODLIV3_FIT').change(
			function() {
				inizializzazioneCODLIV4();
			}
           ); 
		
		//La funzione adopera i dati contenuti nell'oggetto json "msg" per popolare i campi select "nomeCampo"
		//Inoltre nel caso di apertura in modifica o di funzione "Salva con pari livello", vengono impostati
		//opportunamente a "selected" gli elementi della drop down
		function popolaCodliv(msg,nomeCampo){
			var items = [];
	        
	        var LIV = "";
	        var codlivOriginale ="";
	        if(nomeCampo=="CODLIV1_FIT"){
	        	LIV = LIV1;
	        	codlivOriginale = codliv1Originale;
	        }else if(nomeCampo=="CODLIV2_FIT"){
	        	LIV = LIV2;
	        	codlivOriginale = codliv2Originale;
	        }else if(nomeCampo=="CODLIV3_FIT"){
	        	LIV = LIV3;
	        	codlivOriginale = codliv3Originale;
	        }else if(nomeCampo=="CODLIV4_FIT"){
	        	LIV = LIV4;
	        	codlivOriginale = codliv4Originale;
	        }
	        	
	        $(msg).each(function() {
				var codice =  $(this).attr('caisim');
	        	var desc = $(this).attr('desc');
	        	//var options = '<option value="' + codice + '" >' + codice + '</option>';
	        		        	
	        	var options = '<option value="' + codice + '"'; 
				if((LIV!=null && LIV!="" && codice==LIV) || (codlivOriginale!=null && codlivOriginale!="" && codice==codlivOriginale)){
					options += ' selected="selected"';
				}
				options += 'title="' + desc + '">' +  codice; 
				if(desc !=null){
					if(desc.length >60)
						desc = desc.substr(0, 60);
					options += ' - ' + desc;
				}
				options +=  '</option>';
				items.push(options);
				
				if(nomeCampo=="CODLIV1_FIT"){
					var titoli = $(this).attr('titcat');
					titoliCategorie.push(titoli);
				}
	    	});
	    		        
			var options = '<option value=""></option>';
			for (var i = 0; i < items.length; i++) {
				options+=items[i];
			}
			$("select#" +nomeCampo ).html(options);
		}
			
			
		});
		
		//Se la variabile valore è valorizzata si blocca il campo CAIS.TITCAT(se non è già inibito)
		//altrimenti se livello = 1 si sblocca il campo
		function bloccaSbloccaTitcat(valore, livello, sbiancare){
			if(valore!= null && valore!=""){
				if(sbiancare)
					setValue("CAIS_TITCAT","");
				document.getElementById("CAIS_TITCAT").disabled=true;
			}else{
				if(livello==1)
					document.getElementById("CAIS_TITCAT").disabled=false;
			}
		}
		
		
		$('#rowLinkAddT_UBUY_BENISERVIZI td').parent().prepend($("<td>"));
		$('#rowLinkAddT_UBUY_BENISERVIZI td:eq(1)').attr("colspan","4");
		$('#rowMsgLastT_UBUY_BENISERVIZI td').parent().prepend($("<td>"));
		$('#rowMsgLastT_UBUY_BENISERVIZI td:eq(1)').attr("colspan","4");
		
		
	</gene:javaScript>
</gene:template>
