<%
	/*
   * Created on: 22/03/2012
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  /*
		Descrizione:
			Maschera per la funzione per il calcolo dei temini di gara
								
			Creato da:	Marcello Caminiti
	 */
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>

<fmt:setBundle basename="AliceResources" />


<c:choose>
	<c:when test='${not empty param.isGaraLottoUnico}'>
		<c:set var="isGaraLottoUnico" value="${param.isGaraLottoUnico}" />
	</c:when>
	<c:otherwise>
		<c:set var="isGaraLottoUnico" value="${isGaraLottoUnico}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.tipgen}'>
		<c:set var="tipgen" value="${param.tipgen}" />
	</c:when>
	<c:otherwise>
		<c:set var="tipgen" value="${tipgen}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.tipgar}'>
		<c:set var="tipgar" value="${param.tipgar}" />
	</c:when>
	<c:otherwise>
		<c:set var="tipgar" value="${tipgar}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.importo}'>
		<c:set var="importo" value="${param.importo}" />
	</c:when>
	<c:otherwise>
		<c:set var="importo" value="${importo}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.prourg}'>
		<c:set var="prourg" value="${param.prourg}" />
	</c:when>
	<c:otherwise>
		<c:set var="prourg" value="${prourg}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.docweb}'>
		<c:set var="docweb" value="${param.docweb}" />
	</c:when>
	<c:otherwise>
		<c:set var="docweb" value="${docweb}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.terrid}'>
		<c:set var="terrid" value="${param.terrid}" />
	</c:when>
	<c:otherwise>
		<c:set var="terrid" value="${terrid}" />
	</c:otherwise>
</c:choose>


<c:choose>
	<c:when test='${not empty param.campo}'>
		<c:set var="campo" value="${param.campo}" />
	</c:when>
	<c:otherwise>
		<c:set var="campo" value="${campo}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.entita}'>
		<c:set var="entita" value="${param.entita}" />
	</c:when>
	<c:otherwise>
		<c:set var="entita" value="${entita}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.dinvit}'>
		<c:set var="dinvit" value="${param.dinvit}" />
	</c:when>
	<c:otherwise>
		<c:set var="dinvit" value="${dinvit}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.faseInviti}'>
		<c:set var="faseInviti" value="${param.faseInviti}" />
	</c:when>
	<c:otherwise>
		<c:set var="faseInviti" value="${faseInviti}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.iterGara}'>
		<c:set var="iterGara" value="${param.iterGara}" />
	</c:when>
	<c:otherwise>
		<c:set var="iterGara" value="${iterGara}" />
	</c:otherwise>
</c:choose>


<div style="width:97%;">
<gene:template file="popup-message-template.jsp">
	<c:set var="titolo" value="Calcolo termine minimo presentazione offerta" />
	<c:if test='${campo eq "DTEPAR"}'>
		<c:set var="titolo" value="Calcolo termine minimo presentazione domanda di partecipazione" />
	</c:if>
	<gene:setString name="titoloMaschera" value="${titolo}"/>
	
	<gene:redefineInsert name="corpo">
	<c:set var="modo" value="NUOVO" scope="request" />
		<gene:formScheda entita="TORN"  gestisciProtezioni="false" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreInitiPopUpCalcolaTermineMinimo">
			<gene:campoScheda>
			<td colSpan="2">
			
					In base ad alcuni dati della gara viene calcolato il numero minimo di giorni che devono intercorrere tra la data di decorrenza,
					<c:choose>
						<c:when test='${campo eq "DTEPAR"}'>
							ovvero la data di trasmissione del bando di gara a GUUE o di pubblicazione su GURI o albo pretorio del Comune,
							e il termine per la presentazione della domanda di partecipazione. In funzione di quest'ultimo viene calcolato il termine per la risposta alle richieste di chiarimenti.
							<br>Specificare la data di decorrenza per ottenere il termine minimo.
						</c:when>
						<c:when test='${campo eq "DTEOFF" and faseInviti ne "Si"}'>
							ovvero la data di trasmissione del bando di gara a GUUE o di pubblicazione su GURI o albo pretorio del Comune,
							e il termine per la presentazione dell'offerta. In funzione di quest'ultimo viene calcolato il termine per la risposta alle richieste di chiarimenti.
							<br>Specificare la data di decorrenza per ottenere il termine minimo.
						</c:when>
						<c:otherwise>
							ovvero la data di invio dell'invito, e il termine per la presentazione dell'offerta.
							In funzione di quest'ultimo viene calcolato il termine per la risposta alle richieste di chiarimenti.
						</c:otherwise>
					</c:choose>
					<c:if test='${campo eq "DTEPAR" or tipgar eq "1"}' >
					</c:if> 
				
			<br>&nbsp;
			</td>
			</gene:campoScheda>
			
			<gene:gruppoCampi>
				<gene:campoScheda nome="DATIGARA">
					<td colspan="2"><b>Dati della gara discriminanti nel calcolo del termine minimo</b></td>
				</gene:campoScheda>
				<gene:campoScheda campo="TIPGEN" campoFittizio="true" value='${tipgen}' definizione="N3;;A1007;;G1TIPGEN" modificabile="false" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.TIPGEN")}'/>
				<c:choose>
					<c:when test="${!isGaraLottoUnico && entita ne 'GARE'}">
						<gene:campoScheda campo="TIPGAR" campoFittizio="true" value='${tipgar}' definizione="N3;;A2044;;TIPGAR" modificabile="false" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.TIPGAR")}'/>	
					</c:when>
					<c:otherwise>
						<gene:campoScheda campo="TIPGARG" campoFittizio="true" value='${tipgar}' definizione="N3;;A2044;;TIPGAR_G" modificabile="false" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.GARE.TIPGARG")}'/>
					</c:otherwise>
				</c:choose>
				<c:choose>
					<c:when test="${!isGaraLottoUnico}">
						<gene:campoScheda campo="IMPTOR" campoFittizio="true" value='${importo}' definizione="F15;;;MONEY;G1IMPTOR" modificabile="false" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.IMPTOR")}'/>
					</c:when>
					<c:otherwise>
						<gene:campoScheda campo="IMPAPP" campoFittizio="true" value='${importo}' definizione="F15;;;MONEY;IMPAPP" modificabile="false" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.GARE.IMPAPP")}'/>
					</c:otherwise>
				</c:choose>
				<gene:campoScheda campo="PROURG" value='${prourg}' definizione="T2;;;SN;G1PROURG" campoFittizio="true" modificabile="false" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.PROURG")}'/>
				<gene:campoScheda campo="DOCWEB" value='${docweb}' definizione="T2;;;SN;G1DOCWEB" campoFittizio="true" modificabile="false" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.DOCWEB")}'/>
				<gene:campoScheda campo="TERRID" value='${terrid}' definizione="T2;;;SN;G1TERRID" campoFittizio="true" modificabile="false" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.TERRID")}'/>
			</gene:gruppoCampi>				
			<gene:gruppoCampi>
				<gene:campoScheda nome="DATICALCOLO">
					<td colspan="2"><b>Calcolo termine minimo</b></td>
				</gene:campoScheda>
				<gene:campoScheda campo="NUMGIO" title="Numero minimo di giorni che devono intercorrere dalla data di decorrenza" value='${giorniScadenza}' definizione="N3" campoFittizio="true" modificabile="false"/>
				<gene:campoScheda campo="DATADECORRENZA" title="Data decorrenza" definizione="D;;;DATA_ELDA" campoFittizio="true" modificabile='${blocco ne "true"}' value= "${dinvit}" visibile="${faseInviti ne 'Si'}"/>
				<gene:campoScheda campo="DATADECORRENZA1" title="Data decorrenza" definizione="D;;;DATA_ELDA;DINVIT" campoFittizio="true" modificabile='${false}' value= "${dinvit}" visibile="${faseInviti eq 'Si'}"/>
				<c:choose>
					<c:when test='${campo eq "DTEPAR"}'>
						<gene:campoScheda campo="DTEPAR" title="Data termine minimo per presentazione domanda di partecipazione" campoFittizio="true"  definizione="D;;;DATA_ELDA;DTEPAR" modificabile='${blocco ne "true"}'/>
						<gene:campoScheda campo="NUMGIOCHIARIMENTI" visibile="false"  value='${giorniScadenzaChiarimenti}' definizione="N3" campoFittizio="true" />
						<gene:campoScheda campo="DATACHIARIMENTI" title="Data termine risposta chiarimenti" campoFittizio="true"  definizione="D;;;DATA_ELDA;DATTURCDP" modificabile='${blocco ne "true"}' visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.DTERMRISPCDP")}'/>
						<gene:fnJavaScriptScheda funzione='calcolaData("#DATADECORRENZA#",${giorniScadenza},"DTEPAR","SOMMA")' elencocampi='DATADECORRENZA' esegui="false"/>
						<gene:fnJavaScriptScheda funzione='calcolaData("#DTEPAR#",${giorniScadenzaChiarimenti},"DATACHIARIMENTI","SOTTRAI")' elencocampi='DTEPAR' esegui="false"/>	
					</c:when>
					<c:otherwise>
						<gene:campoScheda campo="DTEOFF" title="Data termine minimo per presentazione offerta" campoFittizio="true"  definizione="D;;;DATA_ELDA;DTEOFF" modificabile='${blocco ne "true"}'/>
						<gene:campoScheda campo="NUMGIOCHIARIMENTI" visibile="false"  value='${giorniScadenzaChiarimenti}' definizione="N3" campoFittizio="true" />
						<gene:campoScheda campo="DATACHIARIMENTI" title="Data termine risposta chiarimenti" campoFittizio="true"  definizione="D;;;DATA_ELDA;DATTURCPO" modificabile='${blocco ne "true"}' visibile='${(gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.DTERMRISPCPO") and entita eq "TORN") || (gene:checkProt(pageContext, "COLS.VIS.GARE.GARE.DTERMRISPCPOG") and entita eq "GARE")}'/>
						<gene:fnJavaScriptScheda funzione='calcolaData("#DATADECORRENZA#",${giorniScadenza},"DTEOFF","SOMMA")' elencocampi='DATADECORRENZA' esegui="${faseInviti eq 'Si' and !empty dinvit and !empty giorniScadenza}"/>
						<gene:fnJavaScriptScheda funzione='calcolaData("#DTEOFF#",${giorniScadenzaChiarimenti},"DATACHIARIMENTI","SOTTRAI")' elencocampi='DTEOFF' esegui="false"/>		
					</c:otherwise>
				</c:choose>
			</gene:gruppoCampi>
			
			<c:if test='${blocco eq "true"}'>
				<gene:redefineInsert name="buttons">
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
				</gene:redefineInsert>
			</c:if>
			
			<input type="hidden" name="isGaraLottoUnico" id="isGaraLottoUnico" value="${isGaraLottoUnico}" />
			<input type="hidden" name="tipgen" id="tipgen" value="${tipgen}" />
			<input type="hidden" name="tipgar" id="tipgar" value="${tipgar}" />
			<input type="hidden" name="importo" id="importo" value="${importo}" />
			<input type="hidden" name="prourg" id="prourg" value="${prourg}" />
			<input type="hidden" name="docweb" id="docweb" value="${docweb}" />
			<input type="hidden" name="terrid" id="terrid" value="${terrid}" />
			<input type="hidden" name="campo" id="campo" value="${campo}" />
			<input type="hidden" name="entita" id="entita" value="${entita}" />
			<input type="hidden" name="faseInviti" id="faseInviti" value="${faseInviti}" />
			<input type="hidden" name="dinvit" id="dinvit" value="${dinvit}" />
			<input type="hidden" name="iterGara" id="iterGara" value="${iterGara}" />			
		</gene:formScheda>
	</gene:redefineInsert>
	<gene:javaScript>
		function annulla(){
			window.close();
		}
		
		function conferma(){
			var datadecorrenza = getValue("DATADECORRENZA");
			var campo = "${campo }";
			var data = getValue(campo);
			var entita = "${entita }";
			if(datadecorrenza == null || datadecorrenza == ""){
				alert("Indicare la data di decorrenza");
			}else if(data == null || data == ""){
				alert("La data di termine non è valorizzata");
			}else{
				window.opener.setValue(entita + "_" + campo, data);
				var campoChiarimenti;
				if(campo=="DTEOFF"){
					campoChiarimenti="_DTERMRISPCPO";
					if(entita=="GARE")
						campoChiarimenti+="G";
				}else if(campo=="DTEPAR"){
					campoChiarimenti="_DTERMRISPCDP";
					
				}
				var dataChiarimenti = getValue("DATACHIARIMENTI");
				var giorniScadenzaChiarimenti = "${giorniScadenzaChiarimenti }";
				if (document.getElementById("rowDATACHIARIMENTI")!=null && document.getElementById("rowDATACHIARIMENTI").style.display != "none"){
					if(dataChiarimenti!=null && dataChiarimenti!= "" && giorniScadenzaChiarimenti!=null && giorniScadenzaChiarimenti!="" && giorniScadenzaChiarimenti>0)
						window.opener.setValue(entita + campoChiarimenti, dataChiarimenti);
				}	
				window.close();
			}
			
			
		}
		
		function calcolaData(data, giornidasommare,campo,operazione){
			
			if(data==null || data =="" || giornidasommare==null || giornidasommare == "" || giornidasommare=="0")
			 return;
			 
		    var dataSplittata = data.split("/");
            var newData = (dataSplittata[1] + "/" + dataSplittata[0] + "/" + dataSplittata[2]);
		    var dataPartenza = new Date(newData);
		    
			var dataFutura = ritornaData(dataPartenza,giornidasommare,operazione);
			var giorno = dataFutura.getDate();
			if(giorno<10)
				giorno = "0" + giorno;
			var mese = parseInt(dataFutura.getMonth()+1);
			if (mese<10)
				mese = "0" + mese;
			var anno = dataFutura.getFullYear();
			setValue(campo, giorno + "/" + mese + "/" + anno);
			
			//Nel caso di calcolo di DTEOFF, il valore appena calcolato va adoperato per il calcolo
			//della data chiarimenti
			if(campo == "DTEOFF" || campo == "DTEPAR"){
				var giorniScadenzaChiarimenti = "${giorniScadenzaChiarimenti}";
				if(giorniScadenzaChiarimenti==null || giorniScadenzaChiarimenti=="" || giorniScadenzaChiarimenti=="0")
					return;
				newData = (mese + "/" + giorno + "/" + anno);
				dataPartenza = new Date(newData);
				dataFutura = ritornaData(dataPartenza,giorniScadenzaChiarimenti,"SOTTRAI");
				var giorno = dataFutura.getDate();
				if(giorno<10)
					giorno = "0" + giorno;
				var mese = parseInt(dataFutura.getMonth()+1);
				if (mese<10)
					mese = "0" + mese;
				var anno = dataFutura.getFullYear();
				setValue("DATACHIARIMENTI", giorno + "/" + mese + "/" + anno);
								
			}
			
		}
		
		function ritornaData(dataPartenza, giornidasommare,operazione){
			// millisecondi trascorsi fino ad ora dal 1/1/1970
		    var millisecondiPartenza = dataPartenza.getTime();
		    
		    // valore in millisecondi dei giorni da aggiungere o sottrarre
		    var millisecondi = 24 * 60 * 60 * 1000 * giornidasommare;
		    
		    //millisecondi alla data finale
		    var milliseTotali = 0;
		    if(operazione == "SOMMA"){
		    	milliseTotali = millisecondi + millisecondiPartenza;
		    }else{
		    	milliseTotali = millisecondiPartenza - millisecondi;
		    }
		    
		    //data finale in millisecondi
		    var dataFutura = new Date(milliseTotali);
		    
		    return dataFutura;
		}
		
	</gene:javaScript>
</gene:template>
</div>