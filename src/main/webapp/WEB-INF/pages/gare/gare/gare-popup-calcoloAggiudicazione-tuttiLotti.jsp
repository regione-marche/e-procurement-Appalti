<%
	/*
	 * Created on 22-03-2016
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

<div style="width:97%;">

<gene:template file="popup-template.jsp">

	<c:choose>
		<c:when test='${RISULTATO != null}'>
			<c:set var="ngara" value='${NGARA}' />
		</c:when>
		<c:otherwise>
			<c:set var="ngara" value="${param.ngara}" />
		</c:otherwise>
	</c:choose>
	
	
	
	<gene:setString name="titoloMaschera"
				value="Calcolo aggiudicazione su tutti i lotti della gara" />
	
	<gene:redefineInsert name="corpo">
	
	<c:choose>
		<c:when test='${RISULTATO eq "CALCOLOESEGUITO"}'>
			<c:set var="modo" value="APRI" scope="request" />	
		</c:when>
		<c:otherwise>
			<c:set var="modo" value="MODIFICA" scope="request" />
		</c:otherwise>
	</c:choose>
	
	<c:set var="isGaraDLGS2016Manuale" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.IsGaraDopoDLGS2016ManualeFunction", pageContext, ngara, "false","false","false")}' />
	<c:if test="${isGaraDLGS2016Manuale eq '1' }">
		<c:choose>
				<c:when test="${isGaraDLGS2016 }">
					<c:set var="tipoLegge" value="DLGS2016"/>
				</c:when>
				<c:otherwise>
					<c:set var="tipoLegge" value="DLGS2017"/>
				</c:otherwise>
			</c:choose>
		<c:set var="vuoto" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetValoriCampoMETCOEFFFunction", pageContext, tipoLegge)}' />
	</c:if>
	<c:if test="${esitoControlloDitteDLGS2016}">
		<c:set var="appLegRegSic" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.IsLeggeRegioneSiciliaFunction", pageContext)}' />
		<c:if test='${appLegRegSic eq "1"}'>
			<c:set var="resLegRegSic" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.InitLeggeRegioneSiciliaFunction", pageContext, ngara, "Si")}' />
		</c:if>
	</c:if>
	
	<c:set var="abilitataGestionePrezzo" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoFunction", pageContext, "A1149", "1", "true")}'/>
	
	
	
	<c:choose>
		<c:when test='${RISULTATO == null || empty RISULTATO}'>	
			<gene:formScheda entita="GARE" where="GARE.NGARA='${ngara}'" gestisciProtezioni="false" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreInitPopUpCalcolaAggiudicazioneTuttiLotti" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreCalcoloAggiudicazioneTuttiLotti">
				
				<table class="dettaglio-notab">
		
				<tr>
				 	<td>
				 		<br>
				 		Mediante questa funzione è possibile eseguire il calcolo dell'aggiudicazione per tutti i lotti della gara che non sono ancora aggiudicati.
				 	    <br>
				 	    <br>
			 			<c:if test="${!empty importiNulli }">
			 				${importiNulli }
			 			</c:if>
					 	<br>
					</td>
				</tr>
				
				<c:if test="${precutVisibile eq 'true' }">
					<c:set var="valTabA1160" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoFunction", pageContext, "A1160","1","true")}' />
					<c:choose>
						<c:when test="${valTabA1160 eq '1' }">
							<c:set var="msgTabA1160" value="con arrotondamento (solo calcoli intermedi)"/>
						</c:when>
						<c:otherwise>
							<c:set var="msgTabA1160" value="con troncamento (solo calcoli intermedi)"/>
						</c:otherwise>
					</c:choose>	
				</c:if>
				<gene:campoScheda campo="PRECUT" title="N.decimali utilizzati nel calcolo" campoFittizio="true" value="${initPrecut}" definizione="N2" visibile="${precutVisibile eq 'true' }">
					&nbsp;${msgTabA1160 }
				</gene:campoScheda>
				<gene:campoScheda campo="LEGREGSIC"  title="Applica calcolo L.R.Sicilia n.13/2019?" visibile="${precutVisibile eq 'true' and appLegRegSic eq '1' }" campoFittizio="true" definizione="T2;;;SN" obbligatorio= "true" />
				<gene:campoScheda campo="METSOGLIA" entita="GARE1" where="GARE.NGARA = GARE1.NGARA" visibile="${precutVisibile eq 'true' and isGaraDLGS2016Manuale eq '1' and numLottiSenzaMetodo >0 and !(RISULTATO eq 'CALCOLOESEGUITO' and datiRiga.GARE1_LEGREGSIC eq '1')}" obbligatorio= "true" />
				<gene:campoScheda campo="METCOEFF" entita="GARE1" where="GARE.NGARA = GARE1.NGARA" visibile="false" />
				
				<c:if test="${esistonoLottiOEPVRiparam eq 'si' && RISULTATO ne 'CALCOLOESEGUITO'}">
					<gene:campoScheda campo="METPUNTI" entita="GARE1" where="GARE.NGARA = GARE1.NGARA" visibile="true" obbligatorio="true" title="Punteggio considerato nel calcolo soglia anomalia per i lotti OEPV"/>
				</c:if>
				
				
				<c:if test="${precutVisibile eq 'true' and isGaraDLGS2016Manuale eq '1' and numLottiSenzaMetodo >0 && RISULTATO ne 'CALCOLOESEGUITO'}">
				 	<gene:campoScheda nome="METCOEFF_FIT">
					<td class="etichetta-dato">Coefficiente per calcolo metodo E (*)</td>
					<td class="valore-dato">
					<select id="METCOEFF_FIT" name="METCOEFF_FIT" title="Coeffic.per calcolo soglia anomalia metodo E">
					<c:if test='${not empty listaValoriTabellatoDLGS}'>
						<option value="" title="&nbsp;" selected="selected" >&nbsp;</option>
						<c:forEach items="${listaValoriTabellatoDLGS}" var="valoreTabellato" varStatus="status" >
							<option value="${valoreTabellato[0]}" title="${valoreTabellato[1]}" <c:if test="${datiRiga.GARE1_METCOEFF eq  valoreTabellato[0]}">selected="selected"</c:if>>${valoreTabellato[1]}</option>
						</c:forEach>
					</c:if>
					</select></td>
					</gene:campoScheda>
					<gene:fnJavaScriptScheda funzione='gestioneMETSOGLIA("#GARE1_METSOGLIA#")' elencocampi='GARE1_METSOGLIA' esegui="true" />
				</c:if>
				
				<c:if test="${RISULTATO ne 'CALCOLOESEGUITO'}">
				 		<gene:fnJavaScriptScheda funzione='gestioneVisualizzazioneCampiDaLegregsic("#LEGREGSIC#")' elencocampi='LEGREGSIC' esegui="true" />
				 	</c:if>
				
				<gene:campoScheda campo="NGARA" visibile="false"/>
				
				<gene:campoScheda campo="LEGREGSICVISIBILE" title="visibilita legregsic" modificabile="false" definizione="T10" campoFittizio="true" visibile="false"  value="${precutVisibile eq 'true' and appLegRegSic eq '1'}"/>
				
				<gene:campoScheda campo="ISGARADLGS2016AUTO" definizione="T10" campoFittizio="true" visibile="false"  value="${(isGaraDLGS2016 || isGaraDLGS2017) and isGaraDLGS2016Manuale ne '1'}"/>
				
				<gene:campoScheda campo="ISGARADLGS2016" definizione="T10" campoFittizio="true" visibile="false"  value="${ isGaraDLGS2016 eq 'true'}"/>
				<gene:campoScheda campo="ISGARADLGS2017" definizione="T10" campoFittizio="true" visibile="false"  value="${ isGaraDLGS2017 eq 'true'}"/>
				<gene:campoScheda campo="ISGARADL2019" definizione="T10" campoFittizio="true" visibile="false"  value="${ isGaraDL2019 eq 'true'}"/>
								
				<gene:campoScheda>
					<td class="comandi-dettaglio" colSpan="2">
						<c:if test='${RISULTATO ne "ERRORI"}'>
							<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:conferma();">
						</c:if>
						<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
					</td>
				</gene:campoScheda>
				</table>
		
			</gene:formScheda>
		</c:when>
		<c:otherwise>
			<table class="arealayout">
				<tr>
				 	<td>
				 		<br>
				 		Operazione completata. 
				 		<br>
				 	    <br>
			 		</td>
				</tr>
				<tr>
			    	<td colspan="2"><br><b>Dettaglio esito calcolo aggiudicazione per i singoli lotti:</b></td>
			    </tr>
				<tr>
					<td colspan="2" class="valore-dato">
						<textarea cols="95" rows="16" readonly="readonly"><c:forEach items="${listaMessaggi}" var="msg" ><c:out value="${msg}" escapeXml="false"/><c:out value="&#13;&#10;" escapeXml="false"/></c:forEach></textarea>
					</td>
				</tr>
				<tr>
					<td class="comandi-dettaglio" colSpan="2">
						<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;&nbsp;
					</td>
				</tr>
			</table>
		</c:otherwise>
	</c:choose>
  </gene:redefineInsert>
		
		
	<gene:javaScript> 
	
		
		
		function annulla(){
			window.close();
		}
		
		function conferma(){
			<c:if test='${precutVisibile eq "true" and isGaraDLGS2016Manuale eq "1"}'>
				var legregsic = getValue("LEGREGSIC");
				if(legregsic==1){
					setValue("GARE1_METSOGLIA","");
					setValue("GARE1_METCOEFF","");
				}else{
					var metsoglia = getValue("GARE1_METSOGLIA");
					var metcoff_fit =  getValue("METCOEFF_FIT"); 
					if(metsoglia==5 && (metcoff_fit==null || metcoff_fit =="")){
						clearMsg();
						outMsg("Il campo \"Coefficiente per calcolo metodo E\" è obbligatorio","ERR");
						onOffMsgFlag(true);
						return;
					}
					setValue("GARE1_METCOEFF", getValue("METCOEFF_FIT"));
				}
				
			</c:if>
			<c:if test='${esistonoLottiOEPVRiparam eq "si" and RISULTATO ne "CALCOLOESEGUITO" and abilitataGestionePrezzo eq "1" and numCriteriEcoNoPrezzo ne 0}'>
				if( $('#GARE1_METPUNTI').is(':visible') ) {
					var metpunti=getValue("GARE1_METPUNTI");
					if(metpunti=="2"){
						clearMsg();
						outMsg("Essendo stati definiti, per alcuni lotti, dei criteri di valutazione economici non relativi al prezzo ai fini del calcolo soglia anomalia, non è possibile impostare il calcolo soglia anomalia sui punteggi riparametrati","ERR");
						onOffMsgFlag(true);
						return;
					}
				}
			</c:if>
			schedaConferma();
		}
		
		<c:choose>
			<c:when test='${RISULTATO eq "CALCOLOESEGUITO"}'>
				window.opener.listaVaiAPagina(window.opener.document.forms[0].pgCorrente.value);
			</c:when>
			<c:otherwise>
				document.forms[0].jspPathTo.value="gare/gare/gare-popup-calcoloAggiudicazione-tuttiLotti.jsp";		
			</c:otherwise>
		</c:choose>
		
		<c:if test='${resLegRegSic eq "0"}'>
			setValue("LEGREGSIC","${requestScope.initLegRegSic}");
			gestioneVisualizzazioneCampiDaLegregsic("${requestScope.initLegRegSic}");
		</c:if>
		
		
		function gestioneMETSOGLIA(metsoglia){
			if(metsoglia==5)
				showObj("rowMETCOEFF_FIT", true);
			else{
				showObj("rowMETCOEFF_FIT", false);
				//document.forms[0].GARE1_METCOEFF.value='';
				setValue("METCOEFF_FIT","");
			}
		}
		
		function gestioneVisualizzazioneCampiDaLegregsic(valore){
			if(valore==1){
				showObj("rowGARE1_METSOGLIA", false);
				showObj("rowMETCOEFF_FIT", false);
			}else{
				showObj("rowGARE1_METSOGLIA", true);
				var metsoglia = getValue("GARE1_METSOGLIA");
				if(metsoglia==5)
					showObj("rowMETCOEFF_FIT", true);
			}
			
		}
		
		<c:if test="${precutVisibile eq 'true' and isGaraDLGS2016Manuale eq '1' and !(RISULTATO eq 'CALCOLOESEGUITO' and datiRiga.GARE1_LEGREGSIC eq '1')}">
			setValue("GARE1_METSOGLIA","${initMetsoglia}");
			setValue("GARE1_METCOEFF","${initMetcoeff}");
			setValue("METCOEFF_FIT","${initMetcoeff}");
		</c:if>
		
		<c:if test="${esistonoLottiOEPVRiparam eq 'si' && RISULTATO ne 'CALCOLOESEGUITO'}">
			if( $('#GARE1_METPUNTI').is(':visible') ) {
				var metpunti=getValue("GARE1_METPUNTI");
				if(metpunti==null || metpunti=="")
					setValue("GARE1_METPUNTI","1");
			} 
		</c:if>	
		
	</gene:javaScript>
</gene:template>

</div>