<%/*
   * Created on 29-04-2013
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE IL RISULTATO PER LA REGISTRAZIONE SU PORTALE DI TUTTE LE IMPRESE
  //ISCRITTE ALL'ELENCO ED ANCORA NON REGISTRATE

%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<table style="width:100%;">
	<tr>
		<td>
			<div class="contenitore-popup">

			
					<table class="lista">
				    <tr>
				    	<td>
							<table class="arealayout" >
							    <tr>
							    	<td colspan="3">
							    			<p>
							    			  Registrazione completata.
											  <br>&nbsp;
											  <br><b>Dettaglio operazione di registrazione:</b>
											</p>
							      </td>
							    </tr>
									<tr>
										<td width="35%">Numero imprese registrate:</td>
										<td width="5%" align="right">${numImpreseRegistrate}</td> <!-- align="right" -->
										<td ></td>
									</tr>
									<tr>
										<td width="30%">Numero imprese non registrate:</td>
										<td width="5%" align="right">${numImpreseNonRegistrate}</td> <!-- align="right" -->
										<td ></td>
									</tr>
							
						
							<c:if test='${numImpreseRegistrate > 0}'>
								<% // Visualizzazione della lista delle imprese registrate %>
								<tr>
							    	<td colspan="3"><br><b>Elenco delle imprese registrate:</b></td>
							    </tr>
							    <tr>
							    	<td colspan="3" class="valore-dato">
							    		<textarea cols="95" rows="12" readonly="readonly"><c:forEach items="${listaImpreseRegistrate}" var="imprREgistrata" ><c:out value="${imprREgistrata}" escapeXml="false"/><c:out value="&#xD;" escapeXml="false"/></c:forEach></textarea>
							    	</td>
							    </tr>
							</c:if>
							
							<c:if test='${numImpreseNonRegistrate > 0}'>
								<% // Visualizzazione della lista delle info per le imprese non registrate %>
								<tr>
							    	<td colspan="3"><br><b>Elenco delle imprese con errori:</b></td>
							    </tr>
							    <tr>
							    	<td colspan="3" class="valore-dato">
							    		<textarea cols="95" rows="12" readonly="readonly"><c:forEach items="${listaMessaggiImpreseNonRegistrate}" var="msgErrore" ><c:out value="${msgErrore.key}" escapeXml="false"/><c:out value="&#13;&#10;" escapeXml="false"/><c:out value="${msgErrore.value}" escapeXml="false"/><c:out value="&#13;&#10;" escapeXml="false"/><c:out value="&#13;&#10;" escapeXml="false"/></c:forEach></textarea>
							    	</td>
							    </tr>
							</c:if>
								<tr class="comandi-dettaglio">
							      <td colspan="3">
											<input type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;
							      </td>
							 	</tr>
							</table>
							</td>
						</tr>
					</table>
			

			</div>
		</td>
	</tr>
</table>

			

<script type="text/javascript">
<!--
	

	function annulla(){
		window.close();
	}


	

-->
</script>