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
1. sd
1. wewe
    1. we
