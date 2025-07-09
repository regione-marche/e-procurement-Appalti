
<%
  /*
   * Created on 20-Ott-2008
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%-- test per consentire utilizzo della pagina solo agli amministratori di sistema --%>
<c:set var="nonusato" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.CheckOpzioniUtenteFunction", pageContext, "ou89")}' />

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<gene:template file="scheda-template.jsp">

  <gene:redefineInsert name="head" >
    <script type="text/javascript">
      var _contextPath="${pageContext.request.contextPath}";
    </script>

    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/wsdm/jquery.wsdm.css" >
    <script type="text/javascript" src="${contextPath}/js/jquery.validate.min.js"></script>
    <script type="text/javascript" src="${contextPath}/js/jquery.easytabs.js"></script>
	<script type="text/javascript" src="${contextPath}/js/jquery.listaconfidur.js"></script>

    <style type="text/css">
					
						TABLE.scheda {
							margin-top: 5px;
							margin-bottom: 5px;
							padding: 0px;
							font-size: 11px;
							border-collapse: collapse;
							border-left: 1px solid #A0AABA;
							border-top: 1px solid #A0AABA;
							border-right: 1px solid #A0AABA;
						}
				
						TABLE.scheda TR.intestazione {
							background-color: #EFEFEF;
							border-bottom: 1px solid #A0AABA;
						}
						
						TABLE.scheda TR.intestazione TD, TABLE.scheda TR.intestazione TH {
							padding: 5 2 5 2;
							text-align: center;
							font-weight: bold;
							border-left: 1px solid #A0AABA;
							border-right: 1px solid #A0AABA;
							border-top: 1px solid #A0AABA;
							border-bottom: 1px solid #A0AABA;
							height: 30px;
						}
					
						TABLE.scheda TR.sezione {
							background-color: #EFEFEF;
							border-bottom: 1px solid #A0AABA;
						}
						
						TABLE.scheda TR.sezione TD, TABLE.scheda TR.sezione TH {
							padding: 5 2 5 2;
							text-align: left;
							font-weight: bold;
							height: 25px;
						}
					
						TABLE.scheda TR {
							background-color: #FFFFFF;
						}
				
						TABLE.scheda TR TD {
							padding-left: 3px;
							padding-top: 1px;
							padding-bottom: 1px;
							padding-right: 3px;
							text-align: left;
							border-left: 1px solid #A0AABA;
							border-right: 1px solid #A0AABA;
							border-top: 1px solid #A0AABA;
							border-bottom: 1px solid #A0AABA;
							height: 22px;
							font: 11px Verdana, Arial, Helvetica, sans-serif;
						}
						
						TABLE.scheda TR.intestazione TH.ck, TABLE.scheda TR TD.ck {
							width: 22px;
							text-align: center;
						}
						
						img.img_titolo {
							padding-left: 8px;
							padding-right: 8px;
							width: 24px;
							height: 24px;
							vertical-align: middle;
						}
						
						.dataTables_length, .dataTables_filter {
							padding-bottom: 5px;
						}
							
						div.tooltip {
							width: 300px;
							margin-top: 3px;
							margin-bottom:3px;
							border: 1px solid #A0AABA;
							padding: 10px;
							display: none;
							position: absolute;
							z-index: 1000;
							background-color: #F4F4F4;
						}
			
							
					</style>
  </gene:redefineInsert>



  <gene:setString name="titoloMaschera" value="Configurazione durata procedimento di gara" />

  <gene:redefineInsert name="corpo">
  <c:set var="modoAperturaRequest" value="${param.modoapertura}" />
	<form name="formG1confidur" action="${pageContext.request.contextPath}/ApriPagina.do?href=gare/g1confidur/g1confidur-lista.jsp" method="post">
		<input type="hidden" name="modoapertura" id="modoapertura" value="${modoAperturaRequest}"/>
		<input type="hidden" name="filtri" id="filtri" value="${param.filtri}" />
		<input type="hidden" name="paginazione" id="paginazione" value="${param.paginazione}" />
		<input type="hidden" name="pagina" id="pagina" value="${param.pagina}" />
	</form> 
  

	<c:set var="container" value="listaconfidur" />
    <table class="dettaglio-notab">
      <tr>
        <td>
          <br>
			<div id="tabellaG1CONFIDURContainer" style="margin-left:8px; width: 98%"></div>
          <br>
        </td>
      </tr>
	  <td class="comandi-dettaglio">
							<c:choose>
								<c:when test='${modoAperturaRequest ne "MODIFICA"}'>
									<INPUT type="button" id="pulsantemodificadurata" class="bottone-azione" value='Modifica' title='Modifica'>
								</c:when>
								<c:otherwise>
									<INPUT type="button" id="pulsantesalvamodifichedurata" class="bottone-azione" value="Salva" title="Salva">
									<INPUT type="button" id="pulsanteannullamodifichedurata" class="bottone-azione" value="Annulla" title="Annulla">
								</c:otherwise>
							</c:choose>
							&nbsp;
						</td>
	 
     
    </table>
  </gene:redefineInsert>
  
  
	<gene:redefineInsert name="addToAzioni" >
						<tr>
							<tr>
							    <td class="vocemenulaterale">
									<a href="javascript:void(0)" id="menumodificadurata" title="Modifica" tabindex="1512">Modifica</a>
								</td>
							</tr>
						</tr>
	</gene:redefineInsert>
	
	<gene:redefineInsert name="noteAvvisi"/>
	<gene:redefineInsert name="documentiAssociati"/>
	<gene:redefineInsert name="documentiAzioni"/>

	<c:if test='${modoAperturaRequest eq "MODIFICA"}'>
	  <gene:redefineInsert name="azioniContesto" >
	  <div 	id="menulaterale" class="menulaterale" 
				onMouseover="highlightSubmenuLaterale(event,'on');"
				onMouseout="highlightSubmenuLaterale(event,'off');">
		<table>
			<tbody>
				<tr>
					<td class="titolomenulaterale" title="<gene:getString name="titoloMaschera" defaultVal='${gene:resource("label.tags.template.dettaglio.titolo")}' />">
						${gene:resource("label.tags.template.dettaglio.titoloAzioni")}</td>
				</tr>
				<tr>
					<td class="vocemenulaterale">
						<a href="javascript:void(0)" id="menusalvamodifichedurata" title="Salva" tabindex="1512">Salva</a>
					</td>
				</tr>
				<tr>
					<td class="vocemenulaterale">
						<a href="javascript:void(0)" id="menuannullamodifichedurata" title="Annulla" tabindex="1513">Annulla</a>
					</td>
				 </tr>	
				<tr>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<td class="titolomenulaterale">${gene:resource("label.tags.template.tornaa.titolo")}</td>
				</tr>
				<tr>
					<td>Indietro</td>
				</tr>
			</tbody>
		</table>
	  </gene:redefineInsert>
  </c:if>
  
  
<gene:javaScript>
	$( document ).ready(function() {
		if("${modoAperturaRequest}"=="MODIFICA"){
			$(".breadcrumbs a").replaceWith(function() { 
				return "<span title='"+this.title+"'>" + this.innerHTML + "</span>"; 
			});
			$(".contenitore-navbar").find('a').replaceWith(function() { 
			   return ("<span title='"+this.title+"'>" + this.innerHTML + "</span>"); 
			});
			$(".banner a").replaceWith(function() { 
			   return this.innerHTML; 
			});
			$("#lUtente").remove();
		}
});
</gene:javaScript>
</gene:template>
