<%
			/*
       * Created on: 16.15 21/04/2009
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */
      
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>

<c:set var="numProtocollo" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetNumeroProtocolloFunction", pageContext,param.tipscad, param.ngara)}' />

<gene:template file="popup-template.jsp">
	<gene:setString name="titoloMaschera" value=""/>
	<gene:redefineInsert name="corpo">
		<table class="dettaglio-notab">
		<tr>
			<td class="valore-dato">
				<c:out value="${numProtocollo}"/>
			</td>
		</tr>
	</table>
		
  </gene:redefineInsert>
  <gene:javaScript>
  		
  		var tipscad;
		tipscad = "${param.tipscad}";
		if (tipscad == "1") {
			opener.setValue("NPRDOMFIT","${numProtocollo}");
		}
		if (tipscad == "2") {
			opener.setValue("DITG_INVOFF","1");
			opener.setValue("NPROFFFIT","${numProtocollo}");
		}
		if (tipscad == "3") {
			opener.setValue("NPRREQFIT","${numProtocollo}");
		}
  		
		opener.setValue("${param.campo}","${numProtocollo}");
		
				
		var data;
		data = "${param.data}";
		
		if (data == "0") {
			opener.setValue("${param.campodata}","${requestScope.dataAttuale}");
			opener.setValue("${param.campoora}","${requestScope.oraAttuale}");
		}
		
		window.close();
		
		
		
  </gene:javaScript>
</gene:template>

