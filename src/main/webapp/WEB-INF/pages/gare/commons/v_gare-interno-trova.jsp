<%/*
   * Created on 26-11-2013
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

<% // i filtri applicati sono il filtro sul livello utente ed il filtro sul codice profilo %>
<c:set var="filtroLivelloUtente"
	value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, param.entita)}' />
<c:set var="filtro" value="${filtroLivelloUtente}" />

<c:choose>
	<c:when test="${param.entita eq 'V_GARE_ELEDITTE'}">
		<c:set var='tipoRicerca' value="ricercaElenchi"/>
	</c:when>
	<c:otherwise>
		<c:set var='tipoRicerca' value="ricercaCataloghi"/>
	</c:otherwise>
</c:choose>

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.PulisciSessioneFiltriFunction" parametro="${tipoRicerca }"/>
<c:set var="dbms" value="${gene:callFunction('it.eldasoft.gene.tags.utils.functions.GetTipoDBFunction', pageContext)}" />

<gene:template file="ricerca-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="${param.entita }-trova">
	<gene:setString name="titoloMaschera" value="Ricerca ${gene:if(param.entita eq 'V_GARE_CATALDITTE','cataloghi','elenchi') }"/>
	
	<gene:redefineInsert name="trovaCreaNuovo">
		<c:if test='${gene:checkProtFunz(pageContext,"INS","TROVANUOVO")}'>
			<tr>
				<td class="vocemenulaterale">
					<a href="javascript:trovaCreaNuovaGara();" title="Inserisci" tabindex="1503">
						${gene:resource("label.tags.template.trova.trovaCreaNuovo")}
					</a>
				</td>
			</tr>
		</c:if>
	</gene:redefineInsert>
	<% // Ridefinisco il corpo della ricerca %>
	<gene:redefineInsert name="corpo">
  	<gene:formTrova entita="${param.entita }" filtro="${filtro}" gestisciProtezioni="true" >
			<gene:gruppoCampi idProtezioni="DATIGEN" >
			<tr><td colspan="3"><b>Dati generali</b></td></tr>
			<gene:campoTrova campo="CODICE" title="${gene:if(param.entita eq 'V_GARE_CATALDITTE','Codice catalogo','Codice elenco') }"/>
			<gene:campoTrova campo="TIPOELE" title="${gene:if(param.entita eq 'V_GARE_CATALDITTE','Catalogo per','Elenco per') }"  gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoTipoElencoPaginaTrova"/>
			<gene:campoTrova campo="OGGETTO"/>
			<gene:campoTrova campo="ISARCHI" title="${gene:if(param.entita eq 'V_GARE_CATALDITTE','Catalogo archiviato?','Elenco archiviato?') }" defaultValue="2" />
			</gene:gruppoCampi>
			
			<gene:gruppoCampi idProtezioni="OPERATORE" >
			<tr><td colspan="3"><b>Dati operatore economico</b></td></tr>
			<gene:campoTrova campo="CODIMP" entita="IMPR" from="ditg" where="ditg.codgar5=${param.entita}.codgar and ditg.ngara5=${param.entita}.codice and ditg.dittao=impr.codimp" />
			<gene:campoTrova campo="NOMIMO" entita="DITG" where="ditg.codgar5=${param.entita}.codgar and ditg.ngara5=${param.entita}.codice" />
			<gene:campoTrova campo="CFIMP" entita="IMPR" from="ditg" where="ditg.codgar5=${param.entita}.codgar and ditg.ngara5=${param.entita}.codice and ditg.dittao=impr.codimp" />
			<gene:campoTrova campo="PIVIMP" entita="IMPR" from="ditg" where="ditg.codgar5=${param.entita}.codgar and ditg.ngara5=${param.entita}.codice and ditg.dittao=impr.codimp" />
			<gene:campoTrova campo="TIPIMP" entita="IMPR" from="ditg" where="ditg.codgar5=${param.entita}.codgar and ditg.ngara5=${param.entita}.codice and ditg.dittao=impr.codimp" />
			<gene:campoTrova campo="ISMPMI" entita="IMPR" from="ditg" where="ditg.codgar5=${param.entita}.codgar and ditg.ngara5=${param.entita}.codice and ditg.dittao=impr.codimp" />
			<gene:campoTrova campo="EMAIIP" entita="IMPR" from="ditg" where="ditg.codgar5=${param.entita}.codgar and ditg.ngara5=${param.entita}.codice and ditg.dittao=impr.codimp" />
			<gene:campoTrova campo="EMAI2IP" entita="IMPR" from="ditg" where="ditg.codgar5=${param.entita}.codgar and ditg.ngara5=${param.entita}.codice and ditg.dittao=impr.codimp" />
			<gene:campoTrova campo="CAISIM" title="Codice categoria d'iscrizione" entita="V_ISCRIZCAT_CLASSI" from="ditg,impr" where="ditg.dittao=impr.codimp and ditg.codgar5=V_ISCRIZCAT_CLASSI.codgar and ditg.ngara5=V_ISCRIZCAT_CLASSI.NGARA  and ditg.dittao = V_ISCRIZCAT_CLASSI.CODIMP and ${param.entita}.CODICE = V_ISCRIZCAT_CLASSI.NGARA and ${param.entita}.CODGAR = V_ISCRIZCAT_CLASSI.CODGAR  "/>
			<gene:campoTrova campo="DESCAT1" title="Descrizione categoria d'iscrizione" entita="V_ISCRIZCAT_CLASSI" from="ditg,impr" where="ditg.dittao=impr.codimp and ditg.codgar5=V_ISCRIZCAT_CLASSI.codgar and ditg.ngara5=V_ISCRIZCAT_CLASSI.NGARA  and ditg.dittao = V_ISCRIZCAT_CLASSI.CODIMP and ${param.entita}.CODICE = V_ISCRIZCAT_CLASSI.NGARA and ${param.entita}.CODGAR = V_ISCRIZCAT_CLASSI.CODGAR  "/>
			<gene:campoTrova campo="TIPLAVG" entita="V_ISCRIZCAT_CLASSI" from="ditg,impr" where="ditg.dittao=impr.codimp and ditg.codgar5=V_ISCRIZCAT_CLASSI.codgar and ditg.ngara5=V_ISCRIZCAT_CLASSI.NGARA  and ditg.dittao = V_ISCRIZCAT_CLASSI.CODIMP and  ${param.entita}.CODICE = V_ISCRIZCAT_CLASSI.NGARA and ${param.entita}.CODGAR = V_ISCRIZCAT_CLASSI.CODGAR  "/>
			<c:if test='${gene:checkProt(pageContext, "COLS.VIS.GARE.ISCRIZCAT.SUPNUMCLASS")}'>
			<gene:campoTrova campo="NUMCLASS" title="Classifica categoria d'iscrizione" from="ditg,impr" entita="V_ISCRIZCAT_CLASSI" where="ditg.dittao=impr.codimp and ditg.codgar5=V_ISCRIZCAT_CLASSI.codgar and ditg.ngara5=V_ISCRIZCAT_CLASSI.NGARA  and ditg.dittao = V_ISCRIZCAT_CLASSI.CODIMP and ${param.entita}.CODICE = V_ISCRIZCAT_CLASSI.NGARA and ${param.entita}.CODGAR = V_ISCRIZCAT_CLASSI.CODGAR  " gestore="it.eldasoft.gene.tags.gestori.decoratori.GestoreCampoClassificaCategoriaRicerca"/>
				<gene:fnJavaScriptTrova funzione="gestioneNumcla('#Campo15#')" elencocampi="Campo15" esegui="true" />
			</c:if>
			<gene:campoTrova campo="ABILITAZ" entita="DITG" where="ditg.codgar5=${param.entita}.codgar and ditg.ngara5=${param.entita}.codice" />
			<gene:campoTrova campo="DRICIND" title="Data domanda iscrizione" entita="DITG" where="ditg.codgar5=${param.entita}.codgar and ditg.ngara5=${param.entita}.codice" />
			<gene:campoTrova campo="DSCAD" title="Data ultimo rinnovo" entita="DITG" where="ditg.codgar5=${param.entita}.codgar and ditg.ngara5=${param.entita}.codice" />
			<gene:campoTrova campo="STRIN" title="Stato rinnovo" entita="DITG" where="ditg.codgar5=${param.entita}.codgar and ditg.ngara5=${param.entita}.codice" />
			<gene:campoTrova campo="COORDSIC" entita="DITG" where="ditg.codgar5=${param.entita}.codgar and ditg.ngara5=${param.entita}.codice" />
			<gene:campoTrova campo="ALTNOT" entita="DITG" where="ditg.codgar5=${param.entita}.codgar and ditg.ngara5=${param.entita}.codice" definizione="T60;0;;;G1ALTNOT"/>
						
			</gene:gruppoCampi>
			<input type="hidden" name="valoreCodimp" value="" />
			<input type="hidden" name="valoreNomimo" value="" />
			<input type="hidden" name="valoreCf" value="" />
			<input type="hidden" name="valorePiva" value="" />
			<input type="hidden" name="valoreTipimp" value="" />
			<input type="hidden" name="valoreIsmpmi" value="" />
			<input type="hidden" name="valoreEmail" value="" />
			<input type="hidden" name="valorePec" value="" />
			<input type="hidden" name="valoreCodCat" value="" />
			<input type="hidden" name="valoreDescCat" value="" />
			<input type="hidden" name="valoreTipCat" value="" />
			<input type="hidden" name="valoreNumclass" value="" />
			<input type="hidden" name="valoreAbilitaz" value="" />
			<input type="hidden" name="valoreDricind" value="" />
			<input type="hidden" name="valoreDscad" value="" />
			<input type="hidden" name="valoreAltnot" value="" />
			<input type="hidden" name="valoreCoordsic" value="" />
			<input type="hidden" name="valoreStrin" value="" />
			<input type="hidden" name="ignoraCaseSensitive" value="" />
			
    </gene:formTrova>
  </gene:redefineInsert>

  <gene:javaScript>
  	 	
  	function trovaCreaNuovaGara(){
			<c:choose>
				<c:when test="${param.entita eq 'V_GARE_ELEDITTE' }">
					document.forms[0].action += "&tipoGara=garaLottoUnico&garaPerElenco=1";
				</c:when>
				<c:otherwise>
					document.forms[0].action += "&tipoGara=garaLottoUnico&garaPerCatalogo=1";
				</c:otherwise>
			</c:choose>
			document.trova.jspPathTo.value="/WEB-INF/pages/gare/gare/gare-scheda.jsp";
			document.trova.metodo.value="nuovo";
			document.trova.submit();
		}
	
	function gestioneNumcla(numcla){
		var index = document.getElementById("Campo15").selectedIndex;
		var tipoAppalto = "";
		if(index>0)
			tipoAppalto = document.getElementById("Campo15").options[index].text.substr(0,1);
		
		setValue("Campo14",tipoAppalto);
	}
	
	
	showObj("rowCampo14", false);
	
	
	var trovaEsegui_Default = trovaEsegui;
	
	//Valorizzazione dei campi adoperati per impostare i filtri nella lista degli operatori
	//Viene presa in considerazione solo la ricerca base, senza opzioni avanzate(altrimenti si
	//dovrebbero prendere in considerazione anche gli operatori!!)
	function trovaEsegui_Custom(){
		var where;
		var nuoveCondizioniDITG="";
		var nuoveCondizioniIMPR="";
		var domandaIscrizione;	
		var dataUltimoRinnovo;	
		var caseSensitive=document.forms[0].caseSensitive.checked;
		if(caseSensitive==true)
			document.forms[0].ignoraCaseSensitive.value="si";
		else
			document.forms[0].ignoraCaseSensitive.value="no";
			
		//codimp - Campo4
		if(document.getElementById("Campo4").value!=null && document.getElementById("Campo4").value!=""){
			document.forms[0].valoreCodimp.value=document.getElementById("Campo4").value;
			if(nuoveCondizioniDITG!=null && nuoveCondizioniDITG!=""){
				where=getValue("Campo4_where");
				where+=nuoveCondizioniDITG;
				setValue("Campo4_where",where);
				var CODIMP= document.getElementById("Campo4").value;
				nuoveCondizioniIMPR = " and UPPER(IMPR.CODIMP) like '%" + CODIMP + "%' ";
				if(caseSensitive==true){
					CODIMP = CODIMP.toUpperCase();
					nuoveCondizioniIMPR = " and UPPER(IMPR.CODIMP) like '%" + CODIMP + "%' ";
				}else{
					nuoveCondizioniIMPR = " and IMPR.CODIMP like '%" + CODIMP + "%' ";
				}
				
			}
		}	
			
		//nomimo - Campo5
		if(document.getElementById("Campo5").value!=null && document.getElementById("Campo5").value!=""){
			document.forms[0].valoreNomimo.value=document.getElementById("Campo5").value;
			var nomimo = document.getElementById("Campo5").value;
			if(caseSensitive==true){
				nomimo = nomimo.toUpperCase();
				nuoveCondizioniDITG = " and UPPER(DITG.NOMIMO ) like '%" + nomimo + "%' ";
			}else{
				nuoveCondizioniDITG = " and DITG.NOMIMO like '%" + nomimo + "%' ";
			}
		}
		
		//coordsic - Campo20
		if(document.getElementById("Campo20").value!=null && document.getElementById("Campo20").value!=""){
			var coordsic = document.getElementById("Campo20").value;
			document.forms[0].valoreCoordsic.value=coordsic;
			if(caseSensitive==true){
				coordsic = coordsic.toUpperCase();
				nuoveCondizioniDITG = " and UPPER(DITG.COORDSIC ) like '%" + coordsic + "%' ";
			}else{
				nuoveCondizioniDITG = " and DITG.COORDSIC like '%" + coordsic + "%' ";
			}
		}
		
		//altnot - Campo21
		if(document.getElementById("Campo21").value!=null && document.getElementById("Campo21").value!=""){
			var altnot = document.getElementById("Campo21").value;
			document.forms[0].valoreAltnot.value=altnot;
			if(caseSensitive==true){
				altnot = altnot.toUpperCase();
				nuoveCondizioniDITG = " and UPPER(DITG.ALTNOT ) like '%" + altnot + "%' ";
			}else{
				nuoveCondizioniDITG = " and DITG.ALTNOT like '%" + altnot + "%' ";
			}
		}
		
		<c:choose>
			<c:when test='${gene:checkProt(pageContext, "COLS.VIS.GARE.ISCRIZCAT.SUPNUMCLASS")}'>
				//abilitazione - Campo16
				index = document.getElementById("Campo16").selectedIndex;
				if(index!=null && index >0){
					document.forms[0].valoreAbilitaz.value = document.getElementById("Campo16").options[index].value;
					var abilitazione=document.forms[0].valoreAbilitaz.value;
					nuoveCondizioniDITG += " and DITG.ABILITAZ =" + abilitazione;
				}
				//Data - Campo17
				if(document.getElementById("Campo17").value!=null){
					document.forms[0].valoreDricind.value=document.getElementById("Campo17").value;
					domandaIscrizione=document.forms[0].valoreDricind.value;
					
				}
				//Data - Campo18
				if(document.getElementById("Campo18").value!=null){
					document.forms[0].valoreDscad.value=document.getElementById("Campo18").value;
					dataUltimoRinnovo=document.forms[0].valoreDscad.value;
					
				}
				//stato rinnovo - Campo19
				index = document.getElementById("Campo19").selectedIndex;
				if(index!=null && index >0){
					document.forms[0].valoreStrin.value = document.getElementById("Campo19").options[index].value;
					var statoRinnovo=document.forms[0].valoreStrin.value;
					nuoveCondizioniDITG += " and DITG.STRIN =" + statoRinnovo;
				}
			</c:when>
			<c:otherwise>
				//abilitazione - Campo15
				index = document.getElementById("Campo15").selectedIndex;
				if(index!=null && index >0){
					document.forms[0].valoreAbilitaz.value = document.getElementById("Campo15").options[index].value;
					var abilitazione=document.forms[0].valoreAbilitaz.value;
					nuoveCondizioniDITG += " and DITG.ABILITAZ =" + abilitazione;
				}
				//Data - Campo16
				if(document.getElementById("Campo16").value!=null){
					document.forms[0].valoreDricind.value=document.getElementById("Campo16").value;
					domandaIscrizione=document.forms[0].valoreDricind.value;
					
				}
				//Data - Campo17
				if(document.getElementById("Campo17").value!=null){
					document.forms[0].valoreDscad.value=document.getElementById("Campo17").value;
					dataUltimoRinnovo=document.forms[0].valoreDscad.value;
					
				}
				//stato rinnovo - Campo18
				index = document.getElementById("Campo18").selectedIndex;
				if(index!=null && index >0){
					document.forms[0].valoreStrin.value = document.getElementById("Campo18").options[index].value;
					var statoRinnovo=document.forms[0].valoreStrin.value;
					nuoveCondizioniDITG += " and DITG.STRIN =" + statoRinnovo;
				}
			</c:otherwise>
		</c:choose>
		
		if(domandaIscrizione!=null && domandaIscrizione!=""){
		<c:choose>
			<c:when test='${dbms eq "ORA"}'>
				nuoveCondizioniDITG += " and DITG.DRICIND =TO_DATE('" + domandaIscrizione + "', 'dd/mm/yyyy')";
			</c:when>
			<c:when test='${dbms eq "MSQ"}'>
				nuoveCondizioniDITG += " and DITG.DRICIND =CONVERT(datetime, '" + domandaIscrizione + "')";
			</c:when>
			<c:when test='${dbms eq "POS"}'>
				nuoveCondizioniDITG += " and DITG.DRICIND =to_date('" + domandaIscrizione + "', 'DD/MM/YYYY')"; 
			</c:when>
			<c:when test='${dbms eq "DB2"}'>
				nuoveCondizioniDITG += " and DITG.DRICIND =TIMESTAMP_FORMAT('" + domandaIscrizione + "', 'DD/MM/RRRR')"; 
			</c:when>
		</c:choose>
		}
		if(dataUltimoRinnovo!=null && dataUltimoRinnovo!=""){
		<c:choose>
			<c:when test='${dbms eq "ORA"}'>
				nuoveCondizioniDITG += " and DITG.DSCAD =TO_DATE('" + dataUltimoRinnovo + "', 'dd/mm/yyyy')";
			</c:when>
			<c:when test='${dbms eq "MSQ"}'>
				nuoveCondizioniDITG += " and DITG.DSCAD =CONVERT(datetime, '" + dataUltimoRinnovo + "')";
			</c:when>
			<c:when test='${dbms eq "POS"}'>
				nuoveCondizioniDITG += " and DITG.DSCAD =to_date('" + dataUltimoRinnovo + "', 'DD/MM/YYYY')"; 
			</c:when>
			<c:when test='${dbms eq "DB2"}'>
				nuoveCondizioniDITG += " and DITG.DSCAD =TIMESTAMP_FORMAT('" + dataUltimoRinnovo + "', 'DD/MM/RRRR')"; 
			</c:when>
		</c:choose>
		}
				
		//cf - Campo6
		if(document.getElementById("Campo6").value!=null && document.getElementById("Campo6").value!=""){
			document.forms[0].valoreCf.value=document.getElementById("Campo6").value;
			if(nuoveCondizioniDITG!=null && nuoveCondizioniDITG!=""){
				where=getValue("Campo6_where");
				where+=nuoveCondizioniDITG;
				setValue("Campo6_where",where);
				var cf= document.getElementById("Campo6").value;
				nuoveCondizioniIMPR = " and UPPER(IMPR.CFIMP) like '%" + cf + "%' ";
				if(caseSensitive==true){
					cf = cf.toUpperCase();
					nuoveCondizioniIMPR = " and UPPER(IMPR.CFIMP) like '%" + cf + "%' ";
				}else{
					nuoveCondizioniIMPR = " and IMPR.CFIMP like '%" + cf + "%' ";
				}
				
			}
		}
		//piva - Campo7
		if(document.getElementById("Campo7").value!=null && document.getElementById("Campo7").value!=""){
			document.forms[0].valorePiva.value=document.getElementById("Campo7").value;
			if(nuoveCondizioniDITG!=null && nuoveCondizioniDITG!=""){
				where=getValue("Campo7_where");
				where+=nuoveCondizioniDITG;
				setValue("Campo7_where",where);
				var piva= document.getElementById("Campo7").value;
				if(caseSensitive==true){
					piva = piva.toUpperCase();
					nuoveCondizioniIMPR += " and UPPER(IMPR.PIVIMP) like '%" + piva + "%' ";
				}else{
					nuoveCondizioniIMPR += " and IMPR.PIVIMP like '%" + piva + "%' ";
				}
			}
		}
		//tipimp - Campo8
		var index = document.getElementById("Campo8").selectedIndex;
		if(index!=null && index >0){
			document.forms[0].valoreTipimp.value = document.getElementById("Campo8").options[index].value;
			if(nuoveCondizioniDITG!=null && nuoveCondizioniDITG!=""){
				where=getValue("Campo8_where");
				where+=nuoveCondizioniDITG;
				setValue("Campo8_where",where);
				var tipimp= document.getElementById("Campo8").value;
				nuoveCondizioniIMPR += " and UPPER(IMPR.TIPIMP) =" + tipimp;
			}
		}	
		//ismpmi - Campo9
		var index = document.getElementById("Campo9").selectedIndex;
		if(index!=null && index >0){
			document.forms[0].valoreIsmpmi.value = document.getElementById("Campo9").options[index].value;
			if(nuoveCondizioniDITG!=null && nuoveCondizioniDITG!=""){
				where=getValue("Campo9_where");
				where+=nuoveCondizioniDITG;
				setValue("Campo9_where",where);
				var ismpmi= document.getElementById("Campo9").value;
				nuoveCondizioniIMPR += " and UPPER(IMPR.ISMPMI) =" + ismpmi;
			}
		}	
		//email - Campo10
		if(document.getElementById("Campo10").value!=null && document.getElementById("Campo10").value!=""){
			document.forms[0].valoreEmail.value=document.getElementById("Campo10").value;
			if(nuoveCondizioniDITG!=null && nuoveCondizioniDITG!=""){
				where=getValue("Campo10_where");
				where+=nuoveCondizioniDITG;
				setValue("Campo10_where",where);
				var email= document.getElementById("Campo10").value;
				if(caseSensitive==true){
					email = email.toUpperCase();
					nuoveCondizioniIMPR += " and UPPER(IMPR.EMAIIP) like '%" + email + "%' ";
				}else{
					nuoveCondizioniIMPR += " and IMPR.EMAIIP like '%" + email + "%' ";
				}
			}
		}
		//pec - Campo11 
		if(document.getElementById("Campo11").value!=null && document.getElementById("Campo11").value!=""){
			document.forms[0].valorePec.value=document.getElementById("Campo11").value;
			if(nuoveCondizioniDITG!=null && nuoveCondizioniDITG!=""){
				where=getValue("Campo11_where");
				where+=nuoveCondizioniDITG;
				setValue("Campo11_where",where);
				var pec= document.getElementById("Campo11").value;
				if(caseSensitive==true){
					pec = pec.toUpperCase();
					nuoveCondizioniIMPR += " and UPPER(IMPR.EMAI2IP) like '%" + pec + "%' ";
				}else{
					nuoveCondizioniIMPR += " and IMPR.EMAI2IP like '%" + pec + "%' ";
				}
			}
		}
		//codcat - Campo12
		if(document.getElementById("Campo12").value!=null && document.getElementById("Campo12").value!=""){
			document.forms[0].valoreCodCat.value=document.getElementById("Campo12").value;
			where=getValue("Campo12_where");
			where+=nuoveCondizioniDITG + nuoveCondizioniIMPR;
			setValue("Campo12_where",where);
		}
		//descat - Campo13
		if(document.getElementById("Campo13").value!=null && document.getElementById("Campo13").value!=""){
			document.forms[0].valoreDescCat.value=document.getElementById("Campo13").value;
			where=getValue("Campo13_where");
			where+=nuoveCondizioniDITG + nuoveCondizioniIMPR;
			setValue("Campo13_where",where);
		}
		//tipo - Campo14
		index = document.getElementById("Campo14").selectedIndex;
		if(index!=null && index >0){
			document.forms[0].valoreTipCat.value = document.getElementById("Campo14").options[index].value;
			where=getValue("Campo14_where");
			where+=nuoveCondizioniDITG + nuoveCondizioniIMPR;
			setValue("Campo14_where",where);
		}
		<c:if test='${gene:checkProt(pageContext, "COLS.VIS.GARE.ISCRIZCAT.SUPNUMCLASS")}'>
			//classifica - Campo15
			index = document.getElementById("Campo15").selectedIndex;
			if(index!=null && index >0){
				document.forms[0].valoreNumclass.value = document.getElementById("Campo15").options[index].value;
				where=getValue("Campo15_where");
				where+=nuoveCondizioniDITG + nuoveCondizioniIMPR;
				setValue("Campo15_where",where);
			}
		</c:if>
						
		trovaEsegui_Default();
	}
	
	trovaEsegui = trovaEsegui_Custom;
	document.getElementById("visualizzazioneAvanzata").disabled = true;
	
	<c:choose>
		<c:when test="${param.entita eq 'V_GARE_ELEDITTE'}">
			document.forms[0].jspPathTo.value="gare/v_gare_eleditte/v_gare_eleditte-lista.jsp";
		</c:when>
		<c:otherwise>
			document.forms[0].jspPathTo.value="gare/v_gare_catalditte/v_gare_catalditte-lista.jsp";
		</c:otherwise>
	</c:choose>
	
  </gene:javaScript>
</gene:template>