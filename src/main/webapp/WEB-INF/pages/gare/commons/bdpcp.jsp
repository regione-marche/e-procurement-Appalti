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

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<gene:template file="scheda-template.jsp">

<gene:setString name="titoloMaschera" value="Immetti i dati del RUP o del DRP" />

	<gene:redefineInsert name="head" >
	<style>
    
    * {
      margin: 0;
      padding: 0;
      box-sizing: border-box;
    }

    .container {
      width: 300px;
      margin: 50px auto;
      padding: 20px;
      border: 1px solid #ccc;
      border-radius: 8px;
      background-color: #f9f9f9;
    }

    h2 {
      margin-bottom: 20px;
      font-size: 24px;
    }

    .input-container {
      margin-bottom: 15px;
    }

    label {
      display: block;
      margin-bottom: 5px;
      font-weight: bold;
    }

    input[type="text"],
    select {
      width: 100%;
      padding: 8px;
      border-radius: 4px;
      border: 1px solid #ccc;
    }
   
  </style>
		
	</gene:redefineInsert>
	
	<gene:redefineInsert name="corpo">
		<c:if test="${empty risultato}">
		<div class="container">
			<form name="form" action="UPDUSR4PCP.do" method="POST">
			      <div class="input-container">
			        <label for="sp">Standard provider</label>
			        <select name="sp" id="sp">
			        	<option value=""></option>
			        	<option value="SPID">SPID</option>
			        	<option value="CIE">CIE</option>
			        	<option value="CUSTOM">Altro</option>
			        </select>
			      </div>
			
			      <div class="input-container">
			        <label for="l">Livello di Autenticazione (LoA):</label>
			        <select name="l" id="l">
			        	<option value=""></option>
			        	<option value="2">LoA 2</option>
			        	<option value="3">LoA 3</option>
			        	<option value="4">LoA 4</option>
			        </select>
			      </div>
			
			      <div class="input-container">
			        <label for="cf">Codice fiscale:</label>
			        <input type="text" id="cf" name="cf">
			      </div>
			    </form>
		</div>
	</c:if>
	<c:choose> 
	<c:when test="${risultato eq 'unauthorized'}">
		<br>
		<h3 style="color: red">&nbsp; Utente non autorizzato</h3>
		<br>
		<br>
	</c:when>
	<c:when test="${risultato eq 'OK'}">
		<br>
		<h3>&nbsp; Operazione eseguita</h3>
		<br>
		<br>
	</c:when>
	<c:otherwise>
		<table class="ricerca"> 
		
			<tr>
				<td class="comandi-dettaglio">
					<gene:insert name="buttons">
						<INPUT type="submit" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:conferma()">&nbsp;
						<INPUT type="button" class="bottone-azione" value="Indietro" title="Indietro" onclick="javascript:historyVaiIndietroDi(1)">&nbsp;
					</gene:insert>
			 </td>
			</tr>
		</table>
	</c:otherwise>
	</c:choose> 
	
	</gene:redefineInsert>
	<gene:redefineInsert name="noteAvvisi"/>
	<gene:redefineInsert name="documentiAssociati"/>

</gene:template>

<script type="text/javascript">
	function conferma(){
		form.submit();
	}
</script>
