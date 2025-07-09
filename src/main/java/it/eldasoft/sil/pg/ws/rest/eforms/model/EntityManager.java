package it.eldasoft.sil.pg.ws.rest.eforms.model;

import java.util.ArrayList;
import java.util.List;

public class EntityManager {
    private List<EntityItem> items = new ArrayList<>();

    // Aggiunge un nuovo item alla lista
    public void addItem(EntityItem item) {
        items.add(item);
    }

    // Restituisce la lista degli item
    public List<EntityItem> getItems() {
        return items;
    }

    // Rimuove un item dalla lista
    public void removeItem(EntityItem item) {
        items.remove(item);
    }

    /**
     * Metodo che, dato tipo, entita e chiave, cerca nella lista un item corrispondente.
     * - Se lo trova, ritorna il valore.
     * - Altrimenti, calcola il nuovo valore:
     *     • Se esistono gia' item con la stessa combinazione, individua il massimo valore numerico,
     *       incrementa di 1 e formatta con 4 cifre (es. 0001, 0002, ...).
     *     • Se non esistono, assume 0 come base e crea il nuovo valore (tipo-0001).
     * Infine, aggiunge il nuovo item alla lista e ritorna il nuovo valore.
     */
    public String getOrCreateValue(String tipo, String entita, String chiave, Boolean create) {
        // Cerca se esiste gia' un item con la combinazione passata
        for (EntityItem item : items) {
            if (item.getTipo().equals(tipo) &&
                item.getEntita().equals(entita) &&
                item.getChiave().equals(chiave)) {
                return item.getValore();
            }
        }
        
        if(Boolean.FALSE.equals(create)) {
          return null;
        }

        // Se non trovato, cerchiamo tra eventuali item (per sicurezza, se ci fossero duplicati) il massimo valore numerico
        int maxNumber = 0;
        for (EntityItem item : items) {
            if (item.getTipo().equals(tipo) 
                //&& item.getEntita().equals(entita) 
                //&& item.getChiave().equals(chiave)
                ) {
                // Supponiamo che il valore sia nel formato "tipo-XXXX"
                String val = item.getValore();
                int dashIndex = val.indexOf("-");
                if (dashIndex != -1 && dashIndex + 1 < val.length()) {
                    String numStr = val.substring(dashIndex + 1);
                    try {
                        int num = Integer.parseInt(numStr);
                        if (num > maxNumber) {
                            maxNumber = num;
                        }
                    } catch (NumberFormatException e) {
                        // Se il formato non e' corretto, lo ignoro
                    }
                }
            }
        }
        // Incremento il valore massimo (se non esiste alcun item, maxNumber sara' 0)
        int newNumber = maxNumber + 1;
        String newValue = tipo + "-" + String.format("%04d", newNumber);

        // Crea un nuovo item con il valore calcolato e lo aggiunge alla lista
        EntityItem newItem = new EntityItem(tipo, entita, chiave, newValue);
        items.add(newItem);
        return newValue;
    }
}
