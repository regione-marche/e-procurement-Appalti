$(window).on("load", function (){


    var _tablePosizioni = null;
    _getTipoWSERP();
    var _searchRow=$('<tr/>');
    var _bRow=$('<br/>');
    var visNumPos = false;
    var visOggetto = false;
    var visTotale = false;
    var visDettagli = false;
    if(_tipoWSERP == 'RAIWAY'){
        visNumPos = true;
        visOggetto = true;
        visTotale = true;
        visDettagli = false;
    }
    _popolaPosizioni();
    _wait();

    $.fn.dataTable.ext.order['dom-checkbox'] = function (settings, col)
    {
        return this.api().column(col, {order:'index'}).nodes().map(function (td, i) {
            return $('input',td).prop('checked') ? '1' : '0';
        } );
    };

    function _waitgperm() {
        document.getElementById('bloccaScreen').style.visibility='visible';
        document.getElementById('wait').style.visibility='visible';
        $("#wait").offset({ top: $(window).height() / 2, left: ($(window).width() / 2) - 200});
    }

    function _nowaitgperm() {
        var timeout = null;
        timeout = setInterval(function() {
            document.getElementById('bloccaScreen').style.visibility='hidden';
            document.getElementById('wait').style.visibility='hidden';
            clearTimeout(timeout);
        }, 500);
    }

    function _creaTabellaPosizioni(){
        var _table = $('<table/>', {"id": "listaposizionirda", "class": "schedagperm", "cellspacing": "0", "width" : "100%"});

        var _tr = $('<tr/>', {"class": "intestazione"});
        _tr.append('<th/>', {"class": "codgar"});
        _tr.append('<th/>');
        _tr.append('<th/>');
        _tr.append('<th/>');
        var _thck = $('<th/>', {"id": "selpos","class": "ck"});
        _tr.append(_thck);

        var _thead = $('<thead/>');
        _thead.append(_tr);

        var _tbody = $('<tbody/>');

        var _tr2 = $('<tr/>', {"class": "intestazione"});
        _tr2.append('<td/>', {"class": "codgar"});
        _tr2.append('<td/>');
        _tr2.append('<td/>');
        _tr2.append('<td/>');
        _tr2.append('<td/>');
        var _tfoot = $('<tfoot/>');
        _tfoot.append(_tr2);

        _table.append(_thead);
        _table.append(_tbody);
        _table.append(_tfoot);

        $("#posizioniContainer").append(_table);
    }

    function _popolaPosizioni(){
        _waitgperm();
        _creaTabellaPosizioni();
        _tableRda = $('#listaposizionirda').removeAttr('width').DataTable( {
            "ajax": {
                "url": "pg/GetWSERPListaRda.do",
                "data" : function (n) {
                    return {
                        operation: $("#operation").val(),
                        codgar: $("#codgar").val(),
                        codice: $("#codice").val(),
                        genere: $("#genere").val(),
                        tipoAppalto: $("#tipoAppalto").val(),
                        tipoGara: $("#tipoGara").val(),
                        uffint: $("#uffint").val(),
                        scProfilo: $("#scProfilo").val(),
                        codicerda: $("#codiceRda").val(),
                        filtroLotto: $("#filtroLotto").val(),
                        modo: $("#modo").val()
                    };
                },
                error: function(e){
                    var messaggio = "Errore durante la lettura del rda";
                    $('#rdamessaggio').text(messaggio);
                    $('#rdamessaggio').show(300);
                },
                "complete": function() {
                    _nowaitgperm();
                }
            },

            "bAutoWidth": false,

            "columnDefs": [
                {
                    "data": "numPos",
                    "visible": visNumPos,
                    "sTitle": "Numero posizione",
                    "className": "dt-body-center",
                    "sWidth" : "70px",
                    "searchable": true,
                    "targets": [ 0 ]
                },
                {
                    "data": "oggetto",
                    "visible": visOggetto,
                    "sTitle": "Oggetto",
                    "sWidth": "100px",
                    "class" : "descr",
                    "searchable": true,
                    "targets": [ 1 ]
                },
                {
                    "data": "totale",
                    "visible": visTotale,
                    "render": $.fn.dataTable.render.number('.', ',', 2, '',' \u20ac') ,
                    "sTitle": "Totale",
                    "sWidth": "100px",
                    "class" : "descr",
                    "searchable": false,
                    "targets": [ 2 ]
                },
                {
                    "data": "dettagli",
                    "visible": visDettagli,
                    "sTitle": "Visualizza dettagli",
                    "sWidth": "100px",
                    "class" : "descr",
                    "searchable": false,
                    "targets": [ 3 ]
                },
                {
                    "data": "checkPos",
                    "visible": true,
                    "targets": [ 4 ],
                    "class" : "ck",
                    "sWidth" : "70px",
                    "render": function (data, type, full, meta ) {
                        var _div = $("<div/>");
                        if(_tipoWSERP == 'RAIWAY'){
                            var _check = $("<input/>",{"type":"checkbox", "id": "ck_pos_" + full.numPos});
                            _div.append(_check);
                        }
                        return _div.html();
                    }
                }
            ],

            "drawCallback": function ( settings ) {

            },

            "language": {
                "sEmptyTable":     "Non ci sono posizioni",
                "sInfo":           "Visualizzazione da _START_ a _END_ di _TOTAL_ posizioni",
                "sInfoEmpty":      "Non ci sono posizioni",
                "sInfoFiltered":   "(su _MAX_ posizioni totali)",
                "sInfoPostFix":    "",
                "sInfoThousands":  ",",
                "sLengthMenu":     "Visualizza _MENU_",
                "sLoadingRecords": "",
                "sProcessing":     "Elaborazione...",
                "sSearch":         "Cerca posizioni",
                "sZeroRecords":    "Non ci sono posizioni",
                "oPaginate": {
                    "sFirst":      "<<",
                    "sPrevious":   "<",
                    "sNext":       ">",
                    "sLast":       ">>"
                }
            },

            "initComplete": function (oSettings, jso) {
                var _iTotalRecords = oSettings.fnRecordsTotal();
                if (_iTotalRecords == 0) {
                    $("#listaposizionirda tfoot").hide();
                    $("#listaposizionirda_info").hide();
                    $("#listaposizionirda_paginate").hide();
                    $('#menuimporta, #pulsimporta').hide()
                }
            },

            "pagingType": "full_numbers",
            "lengthMenu": [[20, 50, 70, 100], ["20 posizioni", "50 posizioni", "70 posizioni", "100 posizioni"]],
            "ordering": false,
            "aoColumns": [
                { "bSortable": true, "bSearchable": true },
                { "bSortable": false, "bSearchable": true },
                { "bSortable": false, "bSearchable": false },
                { "bSortable": false, "bSearchable": false },
                { "bSortable": false, "bSearchable": false },
            ]
        });

        if(_tipoWSERP == 'RAIWAY'){
            $('#listaposizionirda tfoot td').eq(0).html( '<input class="search" size="20" type="text" placeholder="Ric.Rda."/>' );
            $('#listaposizionirda tfoot td').eq(1).html( '<input class="search" size="20" type="text" placeholder="Ric.Oggetto."/>' );
            var _center = $("<center/>");
            var _href = "<a href='javascript:_selezionaTutti();' Title='Seleziona tutti'> <img src='"+_contextPath+"/img/ico_check.gif' height='15' width='15' alt='Seleziona tutti'></a>";
            var _href = _href + "&nbsp;";
            var _href = _href + "<a href='javascript:_deselezionaTutti();' Title='Deseleziona tutti'><img src='"+_contextPath+"/img/ico_uncheck.gif' height='15' width='15' alt='Deseleziona tutti'></a>";
            _center.append(_href);
            _center.appendTo($("#selpos"));
            _tableRda.columns().eq(0).each( function (colIdx) {
                $('input', _tableRda.column(colIdx).footer()).on( 'keyup change', function () {
                    _tableRda.column(colIdx).search(this.value).draw();
                });
            });
        }

        $('#menuimporta, #pulsimporta').click(function() {
            var nSelected = 0;
            var arrmultikey  = '';
            $( "input[id^='ck_pos_']" ).each( function( index ) {
                if($( this ).prop( "checked")){
                    nSelected= nSelected + 1;
                    _ck_id = $(this).attr("id");
                    if (_ck_id.substring(0,7) == 'ck_pos_'){
                        _ck_key = _ck_id.substring(7);
                        arrmultikey  = arrmultikey + _ck_key + ';';
                    }
                }
            });

            if (!(nSelected > 0)){
                alert("Selezionare almeno una posizione")
            } else{
                _waitgperm();
                $.ajax({
                    type: "GET",
                    dataType: "json",
                    url: "pg/SetWSERPPosRdaInGara.do",
                    data : {
                        codgar: $("#codgar").val(),
                        codice: $("#codice").val(),
                        linkrda: $("#linkrda").val(),
                        uffint: $("#uffint").val(),
                        tipoAppalto: $("#tipoAppalto").val(),
                        tipgar: $("#tipoProcedura").val(),
                        codiceRda: $("#codiceRda").val(),
                        arrmultikey: arrmultikey
                    },
                    success: function(res) {
                        var esito= res.Esito;
                        if(esito!="0") {
                            var msgEsito = "Errore nella associazione delle posizioni in gara. " + res.MsgErrore;
                            alert(msgEsito);
                        } else{
                            historyVaiIndietroDi(1);
                        }
                    },
                    error: function(e){
                        alert("Errore nella associazione delle posizioni in gara");
                    },
                    complete: function() {
                        _nowaitgperm();
                    }
                });
            }
        });

        $("#listaposizionirda_filter").hide();
    }
});

function _selezionaTutti() {
    $("input[id^='ck_pos_']").prop("checked","checked");
}

function _deselezionaTutti() {
    $( "input[id^='ck_pos_']" ).each( function( index ) {
        if ($( this ).prop( "disabled")) {
            ;
        }else{
            $( this ).prop( "checked",false);
        }
    });
}