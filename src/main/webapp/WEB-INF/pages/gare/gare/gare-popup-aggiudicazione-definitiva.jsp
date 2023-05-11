<%
	/*
	 * Created on 17-nov-2008
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:choose>
	<c:when test='${not empty param.ngara}'>
		<c:set var="numeroGara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="numeroGara" value="${ngara}" />
	</c:otherwise>
</c:choose>

<c:set var="ngara1" value="${numeroGara}" />
<c:set var="key" value="GARE.NGARA=T:${ngara1}" />
<c:set var="genere" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipologiaGaraFunction", pageContext, key)}' scope="request"/>

<c:set var="variabileNull" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GestorePopupAggiudicazioneDefinitivaFunction", pageContext, numeroGara, genere)}' />

<c:set var="condizione" value="DITG.STAGGI in (3,4,5,6,10) and DITG.CONGRUO = '1'"/>

<div style="width:97%;">

<gene:template file="popup-template.jsp">
	

	<gene:setString name="titoloMaschera" value="Selezione ditta per aggiudicazione definitiva" />
	<gene:redefineInsert name="corpo">
		<gene:redefineInsert name="head">
                <script type="text/javascript" src="${contextPath}/js/date.js"></script>
         </gene:redefineInsert>
		

<c:choose>
	<c:when test="${genere eq '3' }" >
		<c:set var="chiaveGara" value="${codgar}" />
		<c:set var="garaOffUnica" value="true" />
	</c:when>
	<c:otherwise>
		<c:set var="chiaveGara" value="${numeroGara}" />
		<c:set var="garaOffUnica" value="false" />
	</c:otherwise>
</c:choose>

<c:set var="esitoControlloCommissione" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.ControlliComponentiCommissioneFunction", pageContext, chiaveGara,"true", numeroGara,garaOffUnica)}' />

<c:set var="msgSwitchGaraLotto" value="La gara risulta conclusa"/>
<c:if test="${genere ne '2'}">
	<c:set var="msgSwitchGaraLotto" value="Il lotto risulta concluso"/>
</c:if>		



<c:choose>
	<c:when test="${!empty requestScope.esineg or (esitoControlloCommissione eq 'NOK' && tipoMsgCommissione eq 'B')}" >
		<br>
			<b>ATTENZIONE</b>
			<br>Non é possibile procedere.<br>
			<c:if test="${!empty requestScope.esineg}">
			 ${msgSwitchGaraLotto} con esito negativo.<br>
			</c:if>
			<c:if test="${esitoControlloCommissione eq 'NOK' && tipoMsgCommissione eq 'B' }">
			${msgCommissione}<br>
			</c:if>
		<br>
		
			<table class="ricerca"> 
				<tr>
					<td class="comandi-dettaglio">
						<gene:insert name="buttons">
							<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
						</gene:insert>
				 </td>
				</tr>
			</table>		
	</c:when>
	<c:otherwise>
		
		<c:choose>
			<c:when test='${empty modoRichiamo}' >
				<c:set var="modo" value="APRI" scope="request" />
				<table class="lista">
					<tr>
						<td id="listaDitte" style="width: 100%" >
							<c:choose>
								<c:when test="${aqoper eq 2 }">
									Selezionare le ditte vincitrici scegliendo tra le ditte in graduatoria giudicate congrue, individuate nella fase di calcolo aggiudicazione<br>
								</c:when>
								<c:otherwise>
									Selezionare la ditta vincitrice scegliendo tra le ditte in graduatoria giudicate congrue, individuate nella fase di calcolo aggiudicazione<br>
								</c:otherwise>
							</c:choose>
							
							<c:if test="${aqoper eq 2 }">
								<c:choose>
									<c:when test="${!empty aqnumope }">
										<br>
										<b>Numero massimo operatori dell'accordo quadro:</b> ${aqnumope }
									</c:when>
									<c:otherwise>
										<br>
										<b>Numero massimo operatori dell'accordo quadro:</b> non definito
									</c:otherwise>
								</c:choose>
							</c:if>
							
							<c:if test="${esitoControlloCommissione eq 'NOK' && tipoMsgCommissione eq 'NB' }">
								${msgCommissione}
								<br><br>
							</c:if>
												
							<gene:formLista entita="DITG"	where="DITG.NGARA5 = '${ngara1}' AND ${condizione}" tableclass="datilista" sortColumn="6;7;4" pagesize="0" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupAggiudicazioneDefinitiva">
								<gene:campoLista title="Scegli" width="40">
									<center>
									<c:choose>
										<c:when test="${aqoper eq 2 }">
											<input type="checkbox" id="dittaDefinitiva${datiRiga.row}" name="dittaDefinitiva" value="${datiRiga.DITG_DITTAO}" />
										</c:when>
										<c:otherwise>
											<input type="radio" id="dittaDefinitiva${datiRiga.row}" name="dittaDefinitiva" value="${datiRiga.DITG_DITTAO};${datiRiga.DITG_AMMINVERSA}" />
										</c:otherwise>
									</c:choose>
									</center>
								</gene:campoLista>
								<gene:campoLista campo="CODGAR5" visibile="false" edit="true" />
								<gene:campoLista campo="NGARA5"  visibile="false" edit="true" />
								<gene:campoLista campo="NUMORDPL"  ordinabile="false" title="N." width="32"/>
								<gene:campoLista campo="NOMIMO"  ordinabile="false" />
								<gene:campoLista campo="STAGGI"  ordinabile="false" width="200"/>
								<gene:campoLista campo="RIBAUO"  visibile="false" />
								<gene:campoLista campo="DITTAO"  visibile="false" />
								<gene:campoLista campo="AMMINVERSA"   ordinabile="false" title="Esito verif. proc.inversa" visibile="${inversa eq '1' }" width="50"/>
								<gene:campoLista campo="AMMINVERSA_FIT"  title="" visibile="false" value="${datiRiga.DITG_AMMINVERSA }" campoFittizio="true" definizione="T3" edit="true"/>
								<gene:campoLista campo="PRIMAAGGIUDICATARIASELEZIONATA" campoFittizio="true" value="" visibile="false" edit="true" definizione="T10" />
								<input type="hidden" id="aqoper" name="aqoper" value="${aqoper }" />
							</gene:formLista>
						</td>
					</tr>
					<tr>
						<td id="listaDitteVuota" >
							<b>ATTENZIONE</b>
							<br>
							Per procedere con l'aggiudicazione definitiva è necessario prima
							   eseguire il calcolo dell'aggiudicazione.
							<br>&nbsp;<br>
						</td>
					</tr>
					<tr>
						<td class="comandi-dettaglio" colSpan="2">
							<c:choose>
								<c:when test='${datiRiga.rowCount > 0 and empty modAggiuNoSupportata}'>
									<INPUT id="pulsconferma" type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:conferma();">
									<INPUT id="pulsannulla" type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
									<INPUT id="pulschiudi" type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi();">&nbsp;&nbsp;
								</c:when>
								<c:otherwise>
									<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;&nbsp;
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
				</table>
			</c:when>
			<c:otherwise>
				<c:set var="modo" value="MODIFICA" scope="request" />
				<gene:formScheda entita="DITG" where="DITG.NGARA5 = '${numeroGara}' AND DITG.DITTAO = '${PRIMAAGGIUDICATARIASELEZIONATA}'" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupAggiudicazioneDefinitiva">
					<c:choose>
						<c:when test="${aqoper eq 2 }">
							<c:set var="Label" value="Aggiudicazione definitiva"/>
						</c:when>
						<c:otherwise>
							<c:set var="Label" value="Ditta aggiudicataria"/>
						</c:otherwise>
					</c:choose>
					<gene:campoScheda>
						<td colspan="2"><b>${Label }</b>
					</gene:campoScheda>
					<gene:campoScheda campo="CODGAR5" visibile="false" />
					<gene:campoScheda campo="DITTAO" modificabile="false" visibile="${aqoper ne 2 }"/>
					<gene:campoScheda campo="NGARA5" visibile="false" />
					<gene:campoScheda campo="NOMIMO" modificabile="false" visibile="${aqoper ne 2 }"/>

					<c:choose>
						<c:when test='${requestScope.modlicg eq 6}'>
							<gene:campoScheda title="Punteggio" campo="RIBAUO" modificabile="false" definizione="F13.9;0;;;RIBAUO" visibile="${aqoper ne 2 }"/>	
						</c:when>
						<c:when test='${requestScope.modlicg eq 17}'>
							<gene:campoScheda title="Rialzo offerto" campo="RIBAUO" modificabile="false" definizione="F13.9;0;;PRC;RIBAUO" visibile="${aqoper ne 2 }"/>	
						</c:when>
						<c:otherwise>
							<gene:campoScheda title="Ribasso offerto" campo="RIBAUO" modificabile="false" definizione="F13.9;0;;PRC;RIBAUO" visibile="${aqoper ne 2 }"/>	
						</c:otherwise>
					</c:choose>
					<gene:campoScheda campo="NOTDEFI"  entita="GARE1" where="GARE1.NGARA = DITG.NGARA5" />	
					<gene:campoScheda campo="NGARA"  entita="GARE1" where="GARE1.NGARA = DITG.NGARA5" visibile="false"/>					
					<gene:campoScheda campo="MODORICHIAMO" title="Modo richiamo" modificabile="false" value='${modoRichiamo}' definizione="T100" campoFittizio="true" visibile="false" />
					<gene:campoScheda campo="PRIMAAGGIUDICATARIASELEZIONATA" value="${PRIMAAGGIUDICATARIASELEZIONATA}" definizione="T10" campoFittizio="true" visibile="false" />
					<gene:campoScheda addTr="false" visibile="${aqoper ne 2 }">
						<td colspan="2">
							<p>
								<br>
								<br>
								<b>Richiesta di subappalto</b>
							</p>
						</td>
					</gene:campoScheda>
					<gene:campoScheda campo="RICSUB" title="La ditta si &egrave riservata la possibilit&agrave di subappaltare?" defaultValue="" visibile="${aqoper ne 2 }"/>
					<c:choose>
						<c:when test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.PersonalizzazioneAutovie")}'>
							<c:set var="titoloSezioneAttoAggiudicazione" value="Provvedimento di aggiudicazione"/>
						</c:when>
						<c:otherwise>
							<c:set var="titoloSezioneAttoAggiudicazione" value="Atto di aggiudicazione"/>
						</c:otherwise>
					</c:choose>
					<gene:campoScheda addTr="false" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.GARE.TATTOA") or gene:checkProt(pageContext, "COLS.VIS.GARE.GARE.DATTOA") or gene:checkProt(pageContext, "COLS.VIS.GARE.GARE.NATTOA")}'>
						<td colspan="2">
							<p>
								<br>
								<br>
								<b>${titoloSezioneAttoAggiudicazione}</b>
							</p>
						</td>
					</gene:campoScheda>
					<gene:campoScheda campo="TATTOA"  entita="GARE" where="GARE.NGARA = DITG.NGARA5" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.GARE.TATTOA")}'/>
					<gene:campoScheda campo="DATTOA" entita="GARE" where="GARE.NGARA = DITG.NGARA5" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.GARE.DATTOA")}'/>
					<gene:campoScheda campo="NATTOA" entita="GARE" where="GARE.NGARA = DITG.NGARA5" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.GARE.NATTOA")}'/>
					<gene:campoScheda campo="MODLICG" entita="GARE" where="GARE.NGARA = DITG.NGARA5" visibile='false'/>
					<gene:campoScheda>
						<td class="comandi-dettaglio" colSpan="2">
							<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:conferma();">
							<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
						</td>
					</gene:campoScheda>
					<input type="hidden" id="elencoDitteSelezionate" name="elencoDitteSelezionate" value="${elencoDitteSelezionate }" />
					<input type="hidden" id="aqoper" name="aqoper" value="${aqoper }" />
				</gene:formScheda>
			</c:otherwise>
		</c:choose>
	</c:otherwise>
</c:choose>	

	</gene:redefineInsert>

	
	<gene:javaScript>
	$("#pulschiudi").hide();
<c:choose>
	<c:when test='${empty modAggiuNoSupportata}'>
		showObj("listaDitteVuota", false);
	</c:when>
	<c:when test='${modAggiuNoSupportata eq 1}'>
		showObj("listaDitte", false);
		showObj("listaDitteVuota", false);
	</c:when>
	<c:when test='${modAggiuNoSupportata eq 2}'>
		showObj("listaDitte", false);
	</c:when>
</c:choose>

		document.forms[0].jspPathTo.value="gare/gare/gare-popup-aggiudicazione-definitiva.jsp";

<c:choose>
	<c:when test='${MSGUGOV eq "WARNING"}'>
		$("#pulsconferma").hide();
		$("#pulsannulla").hide();
		$("#pulschiudi").show();
		showObj("listaDitte", false);
		showObj("listaDitteVuota", false);

		$( "input[name='dittaDefinitiva']").hide();
		
		function chiudi(){
			window.opener.historyReload();
			window.close();
		}

	</c:when>
	<c:when test='${RISULTATO eq "CALCOLOESEGUITO"}'>
		window.opener.historyReload();
		window.close();
	</c:when>
	<c:when test='${RISULTATO eq "CALCOLOCONWARNING"}'>

		$("#pulsconferma").hide();
		$("#pulsannulla").hide();
		$("#pulschiudi").show();
		showObj("listaDitte", false);
		showObj("listaDitteVuota", false);

		$( "input[name='dittaDefinitiva']").hide();
		
		function chiudi(){
			window.opener.historyReload();
			window.close();
		}

	</c:when>
	<c:when test='${RISULTATO eq "CALCOLOINTERROTTO"}'>
		showObj("jsPopUpDITG_DITTAO", false);
		showObj("jsPopUpDITG_NOMIMO", false);
		showObj("jsPopUpDITG_RIBAUO", false);
		
		function annulla(){
			window.close();
		}

		function conferma(){
			<c:if test="${ bloccoDattoa eq true}">
				var dattoa = getValue("GARE_DATTOA");
		 		if(dattoa==null || dattoa==""){ 
		 			alert("Non è possibile sbiancare la data dell'atto di aggiudicazione perche' altri lotti della gara sono gia' aggiudicati");
		 			return;
		 		}
			</c:if>
			document.forms[0].key.value = document.forms[0].keyParent.value;
			document.forms[0].metodo.value = "update";
			bloccaRichiesteServer();
			document.forms[0].submit();
		}

	</c:when>
	<c:otherwise>

		function annulla(){
			window.close();
		}
		
		function conferma(){
			<c:choose>
				<c:when test="${aqoper eq 2}">
				var aggiudicatarieSelezionate=0;
				var numRighe = "${datiRiga.rowCount }";
				var aqnumope = "${aqnumope }";
				var primaSelezionata="";
				var elencoDitteSelezionate="";
				var amminversa ="";
				var controlloAmminversaSuperato=true;
				for(var i=1;i<=numRighe;i++){
					if(document.getElementById('dittaDefinitiva' + i).checked){
						aggiudicatarieSelezionate++;
						if(primaSelezionata==""){
							primaSelezionata = document.getElementById('dittaDefinitiva' + i).value;
						}
						<c:if test="${inversa eq '1' }">
							amminversa = getValue("AMMINVERSA_FIT_" + i);
							if(amminversa==null || amminversa=="" || amminversa!='1'){
								controlloAmminversaSuperato=false;
							}
						</c:if>
					}
				}
				if(aggiudicatarieSelezionate==0){
					alert("Deve essere selezionata almeno una ditta.");
				}else if(aqnumope!="" && aqnumope != null && aggiudicatarieSelezionate>aqnumope){
					alert("Il numero di ditte selezionate supera il valore massimo previsto per l'accordo quadro.");
				}else if(!controlloAmminversaSuperato){
					alert("Non è possibile procedere perchè per qualcuna delle ditte selezionate non è stato specificato l'esito della verifica della documentazione amministrativa successiva all'apertura offerte (procedura inversa) oppure è risultata 'Non idonea' oppure è in corso il soccorso istruttorio");
				}else{
					document.getElementById("PRIMAAGGIUDICATARIASELEZIONATA_1").value = primaSelezionata;
					document.forms[0].metodo.value = "updateLista";
					document.forms[0].key.value = document.forms[0].keyParent.value;
					bloccaRichiesteServer();
					document.forms[0].submit();
				}
				</c:when>
				<c:otherwise >
					var elementoSelezionato = getValueCheckedRadio(document.forms[0].dittaDefinitiva);
					if(elementoSelezionato != "") {
		  				var ditta = elementoSelezionato.split(";")[0];
		  				<c:if test="${inversa eq '1' }">
		  					var amminversa = elementoSelezionato.split(";")[1];
		  					if(amminversa==null || amminversa==""){
		  						alert("La ditta non è selezionabile perchè non è stato specificato l'esito della verifica della documentazione amministrativa successiva all'apertura offerte (procedura inversa)");
		  						return;
		  					}else if(amminversa==2){
		  						alert("La ditta non è selezionabile perchè è risultata 'Non idonea' nella verifica della documentazione amministrativa successiva all'apertura offerte (procedura inversa)");
		  						return;
		  					}else if(amminversa==10){
		  						alert("La ditta non è selezionabile perchè è in corso il soccorso istruttorio per la verifica della documentazione amministrativa successiva all'apertura offerte (procedura inversa)");
		  						return;
		  					}
		  				</c:if>
		  				document.getElementById("PRIMAAGGIUDICATARIASELEZIONATA_1").value = ditta;
						document.forms[0].metodo.value = "updateLista";
						document.forms[0].key.value = document.forms[0].keyParent.value;
						bloccaRichiesteServer();
						document.forms[0].submit();
					} else {
		 				alert("Deve essere selezionata la ditta aggiudicataria.");
		 			}
				</c:otherwise>
			</c:choose>
			
		}

	</c:otherwise>
</c:choose>
	
	<c:if test='${!empty modoRichiamo}' >
		 var dattoa = getValue("GARE_DATTOA");
		 if(dattoa==null || dattoa==""){
		 	var data = new Date();
		 	setValue("GARE_DATTOA",DateToString(data));
		 }
	</c:if>
	
	
	</gene:javaScript>
</gene:template>

</div>