/**	
 * Realizzato per integrazione Autovie 
 * Addizione dei link sui campi che rappresentano il numero Protocollo
 *  href="javascript:consultaDocumentiArchiflow(nomeCampoProtocolloGenerico,'nomeCampoProtocolloSpecifico);"
 *  
 */
	
	function addHrefs() {
		 $.each(arrayCampiProtocollo,function( index, value ) {
			var arraySimili = $("input[name^='" + value.entita + '_' + value.campo + "']");
			var nomeFisico = value.entita + '_' + value.campo;
			$.each(arraySimili,function( index, value ) {
			 if($(this).val()!=""){
				var _span = $("<span/>");
				_span.css("float", "right");
				_span.css("vertical-align", "top");
				var _href = "javascript:consultaDocumentiArchiflow('" + nomeFisico + "','"+ $(this).attr('name') + "');";
				var _a = $("<a/>",{"text": "Consulta protocollo", "href": _href});
				_span.append(_a);
				_span.appendTo($("#" + $(this).attr('name') + "view").parent());
			 } 
			});				
		 });				
	};
	

	function consultaDocumentiArchiflow(campoProtocollo,campoSpecProtocollo) {
		var valoreProtocollo = $("[name='"+campoSpecProtocollo+"']").val();
		if(valoreProtocollo!=null){
			var par = "campoProtocollo=" + campoProtocollo;
			valoreProtocollo=$.trim(valoreProtocollo);
			par += "&valoreProtocollo=" + valoreProtocollo;
			openPopUpActionCustom(contextPath + "/pg/ConsultaDocumentiArchiflow.do", par, "ConsultaDocumentiArchiflow",700,700,"yes","yes");
		}else{
			alert("Valorizzare il numero protocollo!");
		}
	};

	
	var arrayCampiProtocollo = 
		[
		 {"entita" : "TORN",          "campo" : "NPROAT"       	},
		 {"entita" : "TORN",          "campo" : "NPNOMINACOMM"	},
		 {"entita" : "TORN",          "campo" : "NPROTI"       	},
		 {"entita" : "GARATT",        "campo" : "NPROAT"      	},
		 {"entita" : "GARE",     	  "campo" : "NPROAG"        },
		 {"entita" : "GARE",          "campo" : "NVPROV"      	},
		 {"entita" : "GARE",          "campo" : "NPROREQ"      	},
		 {"entita" : "GARE",          "campo" : "NCOMAG"       	},
		 {"entita" : "GARE",          "campo" : "NCOMNG"       	},
		 {"entita" : "GARE",          "campo" : "NPROAA"       	},
		 {"entita" : "GARE",          "campo" : "NCOMDITTAGG" 	},
		 {"entita" : "GARE",          "campo" : "NCOMDITTNAG"  	},
		 {"entita" : "GARE1",         "campo" : "NPLETTAGGPROVV"},
		 {"entita" : "GARE1",		  "campo" : "NRICHNOMINAMIT"},
		 {"entita" : "GARE1",         "campo" : "NPRAPAGG"      },
		 {"entita" : "GARE1",         "campo" : "NPLETCOM"      },
		 {"entita" : "GAREIDS",       "campo" : "NPROT"      	},
		 {"entita" : "GARSED",        "campo" : "NUMVERB"   	},
		 {"entita" : "GARSED",        "campo" : "NPROTRICONV"   },
		 {"entita" : "GARSED",        "campo" : "NPRVERB"       },
		 {"entita" : "DITG",          "campo" : "NPROFF"        },
		 {"entita" : "DITG",          "campo" : "NPROTG"        },
		 {"entita" : "DITG",          "campo" : "NPLETTRICHGIU" },
		 {"entita" : "DITG",          "campo" : "NPRICEZGIU"    },
		 {"entita" : "DITGSTATI",     "campo" : "NPLETTCOMESCL" },
		 {"entita" : "DITGSTATI",     "campo" : "NPLETTRICHCC"  },
		 {"entita" : "DITGSTATI",     "campo" : "NPPRESDOC"     },
		 {"entita" : "GARESTATI",     "campo" : "NPLETTCOMESCLOFEC"},
		 {"entita" : "GARECONT",      "campo" : "NPROPO"  	    },
		 {"entita" : "GARECONT",      "campo" : "NPROAT"  	    },
		 {"entita" : "GARECONT",      "campo" : "NSVIPO"  	    },
		 {"entita" : "XGARECONT",     "campo" : "XGAREACCNU"  	},
		 {"entita" : "XGARECONT",     "campo" : "XGARESUBPR"  	},
		 {"entita" : "XGARECONT",     "campo" : "XGARESUBP1"  	},
		 {"entita" : "XGARECONT",     "campo" : "XGARESUBNA"  	},
		 {"entita" : "XGARECONT",     "campo" : "XGAREATTA2"  	},
		 {"entita" : "XGARECONT",     "campo" : "XGAREATTAGGPROT"},
		 {"entita" : "XGARECONT",     "campo" : "XGAREPROTCONTRATTO"},
		 {"entita" : "PUBBLI",        "campo" : "NPRPUB"        },
		 {"entita" : "PUBG",          "campo" : "NPRPUB"        }
		];
