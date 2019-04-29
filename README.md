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

---

## Flusso del programma:
1. Il main riempie tutte le risorse in modo esclusivo (mutex a 1)
1. Il main istanzia i thread:
    1. Tabacchino, che si occupa della vendita di componenti
    1. _n_ Fumatori, che cercano di fumare il piu' possibile
    1. Monitor, che si occupa di leggere lo stato di thead e risorse,
        o di rifornire le risorse mancanti
1. Il Tabacchino occupa tutti i compinenti mettendo il semaforo a 2, mentre
    i fumatori attendono che il semaforo torni a 1 per un tempo random / notify
    1. mette il mutex a 1 su tutte le risorse e ne estrae in grande quantita'
    1. mette il mutex a 0 appena finisce e import
    1. mette il semaforo a 1 solo delle risorse che il Tabacchino vende
1. Un Fumatore vede il semaforo a 1 delle risorse che gli servono e lo mette a 2
    1. mette il mutex a 1 sulle risorse da estrarre e le riduce
    1. reimposta il mutex a 0
    1. rimette il semaforo a 1 delle risorse impegnate
    1. fuma le risorse che possiede per _tot_ tempo
    1. finito di fumare lo notifica al Tabacchino
1. Il Tabacchino attende una notifica da un qualsiasi Fumatore
    o dopo _tot_ tempo cambia la disponibilita'
1. Se il Tabacchino nota che una delle sue risorse e' terminata,
    lo notifica al Monitor
    1. Il Monitor riceve notify e mette mutex a 1 e semaforo a 2 su
        tutte le risorse da riempire
    1. Il Monitor richiede all'utente se fare _refill_ o uscire dal programma

---

##Configurazione

Inizialmente verranno richiesti i seguienti dati:
- Numero di fumatori
- Quantita' iniziale per ogni risorsa
- Quantita' delle singole risorse richieste per fumare una sigaretta
- Quantita' di sigarette accese da un singolo accendino prima che si scarichi
- Massimo tempo di attesa nel caso il tabacchino non riceva dei notify
- Massimo tempo di fumata da usare nel random dei fumatori

Il Monitor vi terra' aggiornato sullo stato del programma:
- Tramite un _poll rate_ impostabile da utente
    (controlla le risorse dopo _tot_ secondi)
- Tramite un sistema di notify attivabile nelle impostazioni

##Download

**E' possibile scaricare il programma tramite jar**:
- TODO ADD LINK TO RELEASE

---

##Compilazione

Nel caso sia necessaria la compilazione del codice sorgente, di seguito potrete
trovare i passaggi necessari all'esecuzione del programma:
TODO

###Dipendenze
- Java
- Gtk
- Glava

Usare i seguenti comandi per installare le dipendenze:

```bash
sudo apt-get install libnotify-dev openjdk-8-jdk libenchant-dev libgtksourceview-3.0-dev librsvg2-dev junit libglade2-dev libgladeui-dev 
```

Opzionale: se si possiede altre versioni di java impostare sia java che javac nella versione 8:

```bash
sudo update-alternatives --config javac
sudo update-alternatives --config java
```



Per poi scaricare la liberia di compatibilita' tra gtk e java

```bash
git clone git://github.com/afcowie/java-gnome.git 
cd java-gnome
sudo ./configure #or sudo ./configure compiler=javac runtime=java
sudo make
sudo make install
```


E infine utilizzare i seguenti comandi per compilare da terminale il codice: (usare import org.gnome.gtk.* o gdk.* in base alle necessita')

```bash
mdkir Release
javac -classpath /usr/share/java/gtk-4.1.jar -d Release Main.java
java -classpath /usr/share/java/gtk-4.1.jar Main
```
