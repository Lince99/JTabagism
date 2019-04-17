# JTabagism
Java program with smoking problems

### Problema
_Troppo fumo negli occhi._

---

## Consegna
Un tabacchino vende tutti i componenti necessari per creare una sigaretta:
- Tabacco
- Cartina
- Filtro
- Accendino

Ogni componente ha delle scorte limitate nel magazzino, e il tabacchino ne puo'
vendere solamente **2** per _tot_ tempo, poi deve cambiare la disponibilita'
ai fumatori. Oppure se un fumatore finisce una sigaretta, questo notifica
al tabacchino di cambiare la disponibilita'<br>
La quantita' presente nelle singole risorse e' protetta da un mutex
per ogni risorsa, mentre l'accesso a tutte le risorse e' controllato da
un semaforo a 3 stati (numero di thread che occupano la risorsa):
- 0 = Risorsa finita o non occupata da nessuno
- 1 = Risorsa occupata dal tabacchino
- 2 = Risorsa occupata dal tabacchino e da un fumatore

Un fumatore per fumare una sigaretta necessita di:
- _n_ Tabacco
- 1 Cartina
- 1 Filtro
- 1 Accendino, da cambiare dopo _m_ sigarette

Durante l'esecuzione del programma i fumatori concorrono per ottenere le risorse
necessarie a fumare piu' sigarette possibili, ma una alla volta.

Se una delle risorse termina, viene richiesta l'interazione dell'utente da parte
di un thread "Monitor" che propone il _refill_ o l'uscita dal programma.<br>
Il monitor si occupa di controllare in modalita' non esclusiva
lo stato di risorse e thread, oppure puo' entrare in modalita' esclusiva per
rifornire una risorsa.


## Flusso del programma:
1. Il main riempie tutte le risorse in modo esclusivo (mutex a 1)
1. Il main istanzia i thread:
    1. Tabacchino, che si occupa della vendita di componenti
    1. _n_ Fumatori, che cercano di fumare il piu' possibile
    1. Monitor, che si occupa di leggere lo stato di thead e risorse, o di rifornire le risorse mancanti
1. Il Tabacchino occupa tutti i compinenti mettendo il semaforo a 2, mentre
    i fumatori attendono che il semaforo torni a 1
    1. mette il mutex a 1 su tutte le risorse e ne estrae in grande quantita'
    1. mette il mutex a 0 appena finisce e import 
