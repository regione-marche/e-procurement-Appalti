
<%
	/*
	 * Created on 19-10-2010
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

<gene:template file="lista-template.jsp" >
		
		<c:choose>
			<c:when test='${gene:checkProt(pageContext,"MASC.VIS.GARE.V_GARE_TORN-lista")}'>
				<c:set var="filtroLivelloUtente"
					value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "V_GARE_TORN")}' scope="request"/>
				<c:set var="filtroProfiloAttivo" value="${sessionScope.filtroProfiloAttivo}" scope="request"/>
				<c:set var="filtroTipoGara" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetFiltroTipoGaraFunction", pageContext)}' scope="request"/>
				<c:set var="visualizzazioneGareALotti" value='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE.GestioneGareALotti") }' scope="request"/>
				<c:set var="visualizzazioneGareLottiOffUnica" value='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE.GestioneGareLottiOffUnica") }' scope="request"/>
				<c:set var="visualizzazioneGareALottoUnico" value='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE.GestioneGareALottoUnico") }' scope="request"/>
				<c:set var="profilo" value='1'/>
				<c:set var="titoloColonnaChiave" value='Codice procedura'/>
				<c:set var="titoloCodice" value='Procedure concluse'/>
				
			</c:when>
			<c:when test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_ELEDITTE-lista.ApriGare")}'>
				<c:set var="filtroLivelloUtenteElencoOperatori"
					value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "V_GARE_ELEDITTE")}' scope="request"/>
				<c:set var="profilo" value='2'/>
				<c:set var="titoloColonnaChiave" value='Codice elenco'/>
				<c:set var="titoloCodice" value='Elenchi pubblicati'/>
			</c:when>
			<c:when test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_CATALDITTE-lista.ApriGare")}'>
				<c:set var="filtroLivelloUtenteCataloghi"
					value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "V_GARE_CATALDITTE")}' scope="request"/>
				<c:set var="profilo" value='3'/>
				<c:set var="titoloColonnaChiave" value='Codice catalogo'/>
				<c:set var="titoloCodice" value='Cataloghi pubblicati'/>
			</c:when>
			<c:when test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GAREAVVISI-lista.ApriAvviso")}'>
				<c:set var="filtroLivelloUtenteAvvisi"
					value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "GAREAVVISI")}' scope="request"/>
				<c:set var="profilo" value='5'/>
				<c:set var="titoloColonnaChiave" value='Codice avviso'/>
				<c:set var="titoloCodice" value='Avvisi pubblicati'/>
			</c:when>
			<c:when test='${gene:checkProt(pageContext,  "FUNZ.VIS.ALT.GARE.GestioneStipulaContratti")}'>
				<!-- Per la lettura delle comunicazioni l'utilizzo della view  V_GARE_STIPULA crea rallentamenti, per ovviare si e' deciso di usare direttamente l'entita G1STIPULA-->
				<c:set var="filtroLivelloUtenteStipule"
					value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "V_GARE_STIPULA")}' scope="request"/>
				<c:set var="filtroLivelloUtenteStipuleFS12" value='${fn:replace(filtroLivelloUtenteStipule,"V_GARE_STIPULA","G1STIPULA")}' scope="request"/>
				<c:set var="profilo" value='10'/>
				<c:set var="titoloColonnaChiave" value='Codice stipula'/>
				<c:set var="titoloCodice" value='Stipule attive o annullate'/>
			</c:when>
		</c:choose>
		<c:set var="listaArchiviazioniDaGestire" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetArchiviazioniDaEseguireFunction", pageContext, "sel", profilo)}' />
		<gene:setString name="titoloMaschera" value="${titoloCodice} con documenti da archiviare" />
		
	
	<gene:setString name="entita" value="W_INVCOM" />
	<gene:redefineInsert name="corpo">
	
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
						
		
		<table class="lista">
			<tr>
				<td><gene:formLista pagesize="1000" tableclass="datilista" gestisciProtezioni="false" sortColumn="0" varName="risultatoListaArchiviazioniDaEseguire">
					<gene:campoLista title="Opzioni" width="50">
						<c:if test="${currentRow >= 0}">
							<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
								<gene:PopUpItem title="Visualizza" href="javascript:apriArchiviaDocumenti('${datiRiga.OBJ1}','${datiRiga.OBJ2}','${datiRiga.OBJ4}','${datiRiga.OBJ5}','${datiRiga.OBJ6}' );" />
							</gene:PopUp>
						</c:if>
					</gene:campoLista>
					<gene:campoLista campo="C01" definizione="T21;0" visibile="false"/>
					<gene:campoLista campo="C02" definizione="T21;0" title="${titoloColonnaChiave}" ordinabile="true" href="javascript:apriArchiviaDocumenti('${datiRiga.OBJ1}','${datiRiga.OBJ2}','${datiRiga.OBJ4}','${datiRiga.OBJ5}','${datiRiga.OBJ6}' );"/>
					<gene:campoLista campo="C03" definizione="T2000;0" title="Oggetto" ordinabile="true" />
				</gene:formLista></td>
			</tr>
		</table>
		
		<form name="formVisualizzaDocumenti" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
			<input type="hidden" name="href" value="gare/commons/archivia-documenti-scheda.jsp" /> 
			<input type="hidden" name="codice" id="codice" value="" />
			<input type="hidden" name="codgar" id="codgar" value="" />
			<input type="hidden" name="genere" id="genere" value="" />
			<input type="hidden" name="entita" value="" />
			<input type="hidden" name="key1" value="" />
			<input type="hidden" name="chiaveOriginale" value="" />
			<input type="hidden" name="gartel" value="" />
			<input type="hidden" name="idconfi" value="" />
		</form>
	</gene:redefineInsert>


	<gene:redefineInsert name="listaNuovo" />
	<gene:redefineInsert name="listaEliminaSelezione" />
	
	<gene:javaScript>
	function apriArchiviaDocumenti(codgar,chiave,genere,gartel,idconfi) {
		bloccaRichiesteServer();
		var entita = ${profilo} == 10 ? "G1STIPULA" : "GARE";
		formVisualizzaDocumenti.codice.value = chiave;
		if(entita=="G1STIPULA"){
			formVisualizzaDocumenti.codgar.value = "";
			formVisualizzaDocumenti.key1.value = codgar;
		}else{
			formVisualizzaDocumenti.codgar.value = codgar;
			formVisualizzaDocumenti.key1.value = chiave;
		}
		formVisualizzaDocumenti.genere.value = genere;
		formVisualizzaDocumenti.entita.value = entita;
		formVisualizzaDocumenti.chiaveOriginale.value = chiave;
		formVisualizzaDocumenti.idconfi.value = idconfi;
		formVisualizzaDocumenti.gartel.value = gartel;
		formVisualizzaDocumenti.submit();
	}
	</gene:javaScript>
</gene:template>