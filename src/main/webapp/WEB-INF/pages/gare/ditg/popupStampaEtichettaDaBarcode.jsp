<%/*
   * Created on 07-02-2011
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

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

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
	
	
	function downloadFile(){
		  if (navigator.appName == "Microsoft Internet Explorer"){
		  	window.onblur = null;
		  	apriDocumento("${pathFile}${nomeFile}");
		  } else {
	  		document.stampaEtichettaForm.metodo.value = "downloadEtichetta";
  	  		document.stampaEtichettaForm.submit();
  	  	  }
		}

		function chiudi(){
		  if (navigator.appName == "Microsoft Internet Explorer")
		  	document.stampaEtichettaForm.isIE.value = "1";

		  	document.stampaEtichettaForm.metodo.value = "cancellaEtichetta";
		    document.stampaEtichettaForm.submit();
		    
			var barcode=document.stampaEtichettaForm.barcode.value;
			
		}

	<c:if test='${RISULTATO eq "OK-stampa"}'>
		window.setTimeout('downloadFile();', 200);
	</c:if>
	
	
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

			     <div class="titolomaschera">Composizione etichetta protocollo</div>
				 <div class="contenitore-errori-arealavoro">
					<jsp:include page="/WEB-INF/pages/commons/serverMsg.jsp" />
				</div>
				 
                             <form action="${contextPath}/pg/StampaEtichetta.do" method="post" name="stampaEtichettaForm" id="stampaEtichettaForm" >
                              <table class="ricerca">
                              <hr color=#002E82>
                              <tr>
								<td>
                                   <table class="dettaglio-notab">
									<c:choose>
										<c:when test='${RISULTATO eq "OK-stampa"}'>
											<tr>
												<td>
													<p>
														<br>Composizione completata.
														<br><br>
													</p>
												</td>
											</tr>
											
										</c:when>
										<c:when test='${RISULTATO eq "KO-stampa"}'>
											<tr>
												<td>
													<br>Composizione non completata.
													<br>
													<br>Riprovare o contattare l'amministratore.
													<br><br>
												</td>
											</tr>
											
										</c:when>
									</c:choose>
									
									
									</table>
								</td>
							 </tr>	
							 <tr>
							
								<td class="comandi-dettaglio">
									<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi();">&nbsp;
									
							  </td>
							 </tr>
									
									
							</table>
									
								<input type="hidden" name="nomeFile" value="${nomeFile}" />
								<input type="hidden" name="subDir" value="${subDir}" />
								<input type="hidden" name="metodo" value="" />
								<input type="hidden" name="isIE" value="0" />
								<input type="hidden" name="barcode" value="si" />							

                             </from>


                             </div>
                       </TD>
		</TR>
	</TBODY>
</TABLE>

</BODY>
</HTML>