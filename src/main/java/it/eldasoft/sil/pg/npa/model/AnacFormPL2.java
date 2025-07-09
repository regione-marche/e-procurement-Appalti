package it.eldasoft.sil.pg.npa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Bean description:
 * Contiene i dati comuni per le schede relative alle pianificazioni.
 */
public class AnacFormPL2<Appalto extends AppaltoPL, Lotto extends LottoPL> {
    public List<StazioneAppaltante> stazioniAppaltanti;
    public Appalto appalto;
    public List<Lotto> lotti;

    public AnacFormPL2() {
      //Costruttore non utilizzato
    }

    public List<StazioneAppaltante> getStazioniAppaltanti() {
        return stazioniAppaltanti;
    }

    public void setStazioniAppaltanti(List<StazioneAppaltante> stazioniAppaltanti) {
        this.stazioniAppaltanti = stazioniAppaltanti;
    }

    @JsonIgnore
    public Class getLottoClass() {
        return (Class<Lotto>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }

    @JsonIgnore
    public Class getAppaltoClass() {
        return (Class<Appalto>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public Appalto getAppalto() {
        return appalto;
    }

    public void setAppalto(Object appalto) {
        this.appalto = (Appalto) appalto;
    }

    public List<Lotto> getLotti() {
        return lotti;
    }

    public void setLotti(List<?> lotti) {
        this.lotti = (List<Lotto>) lotti;
    }
}
