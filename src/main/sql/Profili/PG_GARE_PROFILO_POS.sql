--SET search_path = public, pg_catalog;

SET statement_timeout = 0;
-- decommentare la riga seguente in caso di lancio da interprete di comando direttamente da linux
--SET client_encoding = 'LATIN1';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

-- inserimento associazione (utente,profilo)
Insert into W_ACCPRO (ID_ACCOUNT,COD_PROFILO) select 48,'PG_GARE_PROFILO' from W_GENCHIAVI where TABELLA='W_GRUPPI' and not exists (select * from W_PROFILI where cod_profilo = 'PG_GARE_PROFILO');
Insert into W_ACCPRO (ID_ACCOUNT,COD_PROFILO) select 49,'PG_GARE_PROFILO' from W_GENCHIAVI where TABELLA='W_GRUPPI' and not exists (select * from W_PROFILI where cod_profilo = 'PG_GARE_PROFILO');
Insert into W_ACCPRO (ID_ACCOUNT,COD_PROFILO) select 50,'PG_GARE_PROFILO' from W_GENCHIAVI where TABELLA='W_GRUPPI' and not exists (select * from W_PROFILI where cod_profilo = 'PG_GARE_PROFILO');
-- inserimento eventuale gruppo
Update W_GENCHIAVI Set CHIAVE=CHIAVE+1 where TABELLA='W_GRUPPI' and not exists (select 1 from W_GRUPPI where cod_profilo='PG_GARE_PROFILO');
Insert into W_GRUPPI (ID_GRUPPO,NOME,DESCR,COD_PROFILO) Select (select CHIAVE from W_GENCHIAVI where TABELLA='W_GRUPPI'),'Selezione profilo gare','default per il profilo PG_GARE_PROFILO','PG_GARE_PROFILO' from W_GENCHIAVI where tabella='W_GRUPPI' and not exists (select * from W_GRUPPI where cod_profilo='PG_GARE_PROFILO');
-- inserimento associazione (utente,gruppo)
Insert into W_ACCGRP (ID_ACCOUNT,ID_GRUPPO,PRIORITA) select 48,ID_GRUPPO,0 from W_GRUPPI where COD_PROFILO='PG_GARE_PROFILO'  and not exists (select * from W_ACCGRP A inner join W_GRUPPI G on A.id_gruppo=G.id_gruppo where G.cod_profilo='PG_GARE_PROFILO' and A.id_account=48);
Insert into W_ACCGRP (ID_ACCOUNT,ID_GRUPPO,PRIORITA) select 49,ID_GRUPPO,0 from W_GRUPPI where COD_PROFILO='PG_GARE_PROFILO'  and not exists (select * from W_ACCGRP A inner join W_GRUPPI G on A.id_gruppo=G.id_gruppo where G.cod_profilo='PG_GARE_PROFILO' and A.id_account=49);
Insert into W_ACCGRP (ID_ACCOUNT,ID_GRUPPO,PRIORITA) select 50,ID_GRUPPO,0 from W_GRUPPI where COD_PROFILO='PG_GARE_PROFILO'  and not exists (select * from W_ACCGRP A inner join W_GRUPPI G on A.id_gruppo=G.id_gruppo where G.cod_profilo='PG_GARE_PROFILO' and A.id_account=50);

-- pulizia profilo eseguita sempre
Delete from W_PROAZI where COD_PROFILO='PG_GARE_PROFILO';
Delete from W_PROFILI where COD_PROFILO='PG_GARE_PROFILO';

-- inserimento (o reinserimento) testata e configurazione profilo
INSERT INTO W_PROFILI (COD_PROFILO,CODAPP,NOME,DESCRIZIONE,FLAG_INTERNO,DISCRIMINANTE,COD_CLIENTE,CRC) VALUES ('PG_GARE_PROFILO','PG','Trova procedure di affidamento','Ricerca una procedura di affidamento o avviso in tutti i profili applicativi in base a codice, CIG e oggetto',1,null,0,1659988485);

INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_PROFILO','COLS','VIS','GARE.*',1,418256947);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_PROFILO','COLS','VIS','GARE.ARCHDOCG.*',1,3977239352);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_PROFILO','COLS','VIS','GARE.CATPUB.*',1,2737304128);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_PROFILO','COLS','VIS','GARE.CATSCA.*',1,3634777378);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_PROFILO','COLS','VIS','GARE.CONFOPECO.*',1,3721812239);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_PROFILO','COLS','VIS','GARE.DETMOT.*',1,2741372758);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_PROFILO','COLS','VIS','GARE.G1CRIDEF.*',1,4009590829);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_PROFILO','COLS','VIS','GARE.G1CRIMOD.*',1,692781519);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_PROFILO','COLS','VIS','GARE.G1CRIREG.*',1,2211317454);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_PROFILO','COLS','VIS','GARE.GOEVMOD.*',1,184207060);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_PROFILO','COLS','VIS','GARE.TABPUB.*',1,1871213580);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_PROFILO','COLS','VIS','GARE.V_GARE_PROFILO.*',1,107555069);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_PROFILO','COLS','VIS','GARE.V_GARE_STATOESITO.*',1,269121596);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_PROFILO','COLS','VIS','GENE.*',1,644817062);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_PROFILO','COLS','VIS','GENE.CAIS.*',1,2350056535);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_PROFILO','COLS','VIS','GENE.G_CONFCOD.*',1,4040278283);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_PROFILO','COLS','VIS','GENE.G_DETTMODSCADENZ.*',1,45685360);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_PROFILO','COLS','VIS','GENE.G_EVENTISCADENZ.*',1,2346479769);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_PROFILO','COLS','VIS','GENE.TECNI.CFTEC',1,3479026769);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_PROFILO','COLS','VIS','GENE.TECNI.CODTEC',1,699482655);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_PROFILO','COLS','VIS','GENE.TECNI.COGTEI',1,2499727429);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_PROFILO','COLS','VIS','GENE.TECNI.NOMETEI',1,3837335197);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_PROFILO','COLS','VIS','GENE.TECNI.NOMTEC',1,1165377966);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_PROFILO','COLS','VIS','GENE.TECNI.PIVATEC',1,2284240044);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_PROFILO','COLS','VIS','GENEWEB.*',1,681489554);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_PROFILO','FUNZ','VIS','ALT.GARE.V_GARE_PROFILO-lista.ApriGare',1,3279604026);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_PROFILO','FUNZ','VIS','ALT.GENE.associazioneUffintAbilitata',0,600750755);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_PROFILO','MASC','VIS','GARE.V_GARE_TORN-lista',0,3390890272);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_PROFILO','MENU','VIS','ARCHIVI',0,1200207169);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_PROFILO','SUBMENU','VIS','GARE.Trova-gare',0,2624564260);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_PROFILO','SUBMENU','VIS','GARE.Trova-profilo',1,2204723284);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_PROFILO','TABS','VIS','GARE.*',1,138826033);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_PROFILO','TABS','VIS','GENE.*',1,919038372);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_PROFILO','TABS','VIS','GENEWEB.*',1,3534536570);
