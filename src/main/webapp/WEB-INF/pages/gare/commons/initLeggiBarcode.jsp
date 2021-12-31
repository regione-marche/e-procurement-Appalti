<%/*
   * Created on 10-12-2010
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

 // CONTIENE LA PAGINA per acquisire il BARCODE
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:set var="filtroLivelloUtente"
	value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "V_GARE_TORN")}' />

<c:set var="filtroTipoGara" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetFiltroTipoGaraFunction", pageContext)}' />
<c:set var="visualizzazioneGareALotti" value='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE.GestioneGareALotti") }'/>
<c:set var="visualizzazioneGareLottiOffUnica" value='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE.GestioneGareLottiOffUnica") }'/>
<c:set var="abilitazioneGare" value="${sessionScope.profiloUtente.abilitazioneGare}" />

<HTML>
<HEAD>
<jsp:include page="/WEB-INF/pages/commons/headStd.jsp" />
<script type="text/javascript">
<!--
<jsp:include page="/WEB-INF/pages/commons/checkDisabilitaBack.jsp" />

  // al click nel documento si chiudono popup e menu
  if (ie4||ns6) document.onclick=hideSovrapposizioni;

  function hideSovrapposizioni() {
    //hideSubmenuNavbar();
    hideMenuPopup();
    hideSubmenuNavbar();
  }
-->
</script>

<script type="text/javascript"
	src="${contextPath }/js/controlliFormali.js"></script>


<script type="text/javascript">
<!--
	function gestisciSubmit(ilForm) {
	  var esito = true;
	  if (esito && !controllaCampoInputObbligatorio(ilForm.datiacquisiti, 'Timbro Digitale'))
 	    esito = false;
 	  // SS 06/11/2006: parametrizzato il controllo per consentire l'accesso anche a utenti senza password

	  return esito;	   
	}
	
	
-->
  </script>



<jsp:include page="/WEB-INF/pages/commons/jsSubMenuComune.jsp" />
<jsp:include page="/WEB-INF/pages/commons/jsSubMenuSpecifico.jsp" />
<BODY onload="setVariables();checkLocation();initPage();document.formBarcode.datiacquisiti.focus();">
<TABLE class="arealayout">
	<!-- questa definizione dei gruppi di colonne serve a fissare la dimensione
	     dei td in modo da vincolare la posizione iniziale del menù di navigazione
	     sopra l'area lavoro appena al termine del menù contestuale -->
	<colgroup width="150px"></colgroup>
	<colgroup width="800px"></colgroup>
	<colgroup width="*"></colgroup>
	<TBODY>
		<TR class="testata">
			<TD colspan="3">
			<jsp:include page="/WEB-INF/pages/commons/testata.jsp" />
			</TD>
		</TR>
		<TR class="menuprincipale">
			<TD><img src="${contextPath}/img/spacer-azionicontesto.gif" alt=""></TD>
			<c:choose>
			<c:when test="${! empty sessionScope.profiloUtente}">
			<TD>
			<table class="contenitore-navbar">
				<tbody>
					<tr>
						<jsp:include page="/WEB-INF/pages/commons/menuSpecifico.jsp" />
						<jsp:include page="/WEB-INF/pages/commons/menuComune.jsp" />
					</tr>
				</tbody>
			</table>

			<!-- PARTE NECESSARIA PER VISUALIZZARE I SOTTOMENU DEL MENU PRINCIPALE DI NAVIGAZIONE -->
			<iframe id="iframesubnavmenu" class="gene"></iframe>
			<div id="subnavmenu" class="subnavbarmenuskin"
				onMouseover="highlightSubmenuNavbar(event,'on');"
				onMouseout="highlightSubmenuNavbar(event,'off');"></div>
			</TD>
			<TD align="right" nowrap>
				<jsp:include page="/WEB-INF/pages/commons/pulsantiGenerali.jsp" />
			</TD>
			</c:when>
			<c:otherwise>
			<TD>&nbsp;</TD>
			<TD>&nbsp;</TD>
			</c:otherwise>
			</c:choose>
		</TR>
		<TR>
			<TD class="menuazioni">
			<div id="menulaterale"></div>
			</TD>
			<TD class="arealavoro" colspan="2">


			<div class="contenitore-arealavoro">

			     <div class="titolomaschera">Acquisizione mediante lettura del codice a barre</div>
				 <div class="contenitore-errori-arealavoro">
					<jsp:include page="/WEB-INF/pages/commons/serverMsg.jsp" />
				</div>
				<div class="contenitore-dettaglio">
                             <form name="formBarcode" method="post" action="${contextPath}/pg/LeggiBarcode.do" onsubmit="javascript:return gestisciSubmit(this);">
                              <table class="ricerca">
							    <tr>
							    	<td colspan="2">
							    	  <p>
								    	  <br>Mediante lettura del codice a barre, prodotto utilizzando una funzionalità del portale Appalti e 
								    	  allegato ai plichi inviati dalle ditte, si accede direttamente a una scheda di dettaglio contenente  
								    	  alcune informazioni relative alla ditta e alla gara per cui il plico è stato inviato. Il codice a barre identifica 
								    	  anche il tipo di documentazione inviata, che può riguardare la domanda di partecipazione, 
								    	  l'offerta o la comprova dei requisiti.
								    	  Alla conferma, viene gestito l'eventuale inserimento della ditta in gara.
										  <br>&nbsp;
										  <br>&nbsp;
									  </p>
									</td>
								</tr>
                              <tr>
								<td>
                                   <table class="dettaglio-notab">
									<tr>
										<td class="etichetta-dato">Codice a barre</td>
										<td class="valore-dato"><input type="text" name="datiacquisiti" id="datiacquisiti" size="100" value="${barcode }" class="testo" /></td></tr>
									
									</table>
								</td>
							 </tr>	
							 <tr>
							
								<td class="comandi-dettaglio">
<%//<INPUT type="submit" class="bottone-azione" value="Conferma" title="Conferma" >&nbsp; %>
									<INPUT type="button"  class="bottone-azione" value='Annulla' title='Annulla' onclick="javascript:goHome('PG');">&nbsp;
									
							  </td>
							 </tr>
									
									
							</table>
							<INPUT type="hidden"  name="filtroLivelloUtente" id="filtroLivelloUtente" value="${filtroLivelloUtente }"/> 		
							<INPUT type="hidden"  name="filtroTipoGara" id="filtroTipoGara" value="${filtroTipoGara }"/>		
							<INPUT type="hidden"  name="visualizzazioneGareALotti" id="visualizzazioneGareALotti" value="${visualizzazioneGareALotti }"/>
							<INPUT type="hidden"  name="visualizzazioneGareLottiOffUnica" id="visualizzazioneGareLottiOffUnica" value="${visualizzazioneGareLottiOffUnica }"/>
                            <INPUT type="hidden"  name="filtroProfiloAttivo" id="filtroProfiloAttivo" value="${sessionScope.filtroProfiloAttivo }"/>
                            <INPUT type="hidden"  name="filtroUffint" id="filtroUffint" value="${sessionScope.uffint }"/>
                            <INPUT type="hidden"  name="abilitazioneGare" id="abilitazioneGare" value="${abilitazioneGare }"/>
                             
                             </form>
							 </div>

                             </div>
                       </TD>
		</TR>
	</TBODY>
</TABLE>

</BODY>
</HTML>