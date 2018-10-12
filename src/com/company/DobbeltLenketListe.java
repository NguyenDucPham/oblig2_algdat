package com.company;

import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class DobbeltLenketListe<T> implements Liste<T>
{
    private static final class Node<T>   // en indre nodeklasse
    {
        // instansvariabler
        private T verdi;
        private Node<T> forrige, neste;

        private Node(T verdi, Node<T> forrige, Node<T> neste)  // konstruktør
        {
            this.verdi = verdi;
            this.forrige = forrige;
            this.neste = neste;
        }

        protected Node(T verdi)  // konstruktør
        {
            this(verdi, null, null);
        }

    } // Node

    // instansvariabler
    private Node<T> hode;          // peker til den første i listen
    private Node<T> hale;          // peker til den siste i listen
    private int antall;            // antall noder i listen
    private int endringer;   // antall endringer i listen

    // hjelpemetode
    private Node<T> finnNode(int indeks)
    {

        Node<T> p;
        if(indeks < antall/2){
            p=hode;
            for (int i =0; i<indeks;i++){
                p=p.neste;
            }
        }
        else{
            p=hale;
            for (int i =antall -1; i>indeks;i--){
                p=p.forrige;
            }
        }
        return p;
    }

    // konstruktør
    public DobbeltLenketListe()
    {
        hode = hale = null;
        antall = 0;
        endringer = 0;
    }

    // konstruktør
    public DobbeltLenketListe(T[] a)
    {
        this();
        Objects.requireNonNull(a, "Tabell a er null!");
        hode= hale= new Node<>(null);
        for (T verdi: a){
            if(verdi != null){
                hale = hale.neste = new Node<>(verdi, hale, null);
                antall++;
            }
        }

        if(antall == 0){
            hode=null;
            hale=null;
        }
        else {
            hode.forrige =null;
            hode.neste.forrige = null;
            hode = hode.neste;

        }


    }
    //hjelpe metode


    public static void fratilKontroll(int tablengde, int fra, int til)
    {
        if (fra < 0)                                  // fra er negativ
            throw new IndexOutOfBoundsException
                    ("fra(" + fra + ") er negativ!");

        if (til > tablengde)                          // til er utenfor tabellen
            throw new IndexOutOfBoundsException
                    ("til(" + til + ") > tablengde(" + tablengde + ")");

        if (fra > til)                                // fra er større enn til
            throw new IllegalArgumentException
                    ("fra(" + fra + ") > til(" + til + ") - illegalt intervall!");
    }
    // subliste
    public Liste<T> subliste(int fra, int til)
    {
        fratilKontroll(antall, fra, til);

        Liste<T> liste = new DobbeltLenketListe<>();
        Node<T> p = finnNode(fra);

        for (int i = fra; i < til; i++) {
            liste.leggInn(p.verdi);

            p = p.neste;
        }

        return liste;
    }

    @Override
    public int antall()
    {
        return antall;
    }

    @Override
    public boolean tom()
    {
        return antall == 0;
    }

    @Override
    public boolean leggInn(T verdi)
    {
        Objects.requireNonNull(verdi,"Null verdier er ikke tillatt");

        Node<T> p = new Node<>(verdi, hale, null);

        if (tom())
            hale = hode = p;
        else
            hale = hale.neste = p;

        antall++;
        endringer++;

        return true;




    }

    @Override
    public void leggInn(int indeks, T verdi)
    {

        Objects.requireNonNull(verdi, "stopp, null!");

        indeksKontroll(indeks, true);

        if (tom())
            hode = hale = new Node<>(verdi, null, null);

        else if (indeks == 0)
            hode = hode.forrige = new Node<>(verdi, null, hode);

        else if (indeks == antall)
            hale = hale.neste = new Node<>(verdi, hale, null);

        else {
            Node<T> p = finnNode(indeks);
            p.forrige = p.forrige.neste = new Node<>(verdi, p.forrige, p);
        }

        antall++;
        endringer++;
    }


    @Override
    public boolean inneholder(T verdi)
    {

        return indeksTil(verdi) != -1;

    }

    @Override
    public T hent(int indeks)
    {

        indeksKontroll(indeks, false);

        return finnNode(indeks).verdi;
    }

    @Override
    public int indeksTil(T verdi)
    {
        Node<T> p = hode;
        if (verdi == null)
            return -1;


        for (int indeks = 0; indeks < antall; indeks++) {
            if (p.verdi.equals(verdi))
                return indeks;

            p = p.neste;
        }

        return -1;



    }

    @Override
    public T oppdater(int indeks, T nyverdi)
    {
        throw new UnsupportedOperationException("Ikke laget ennå!");
    }
//hjelpe metode
    private T fjernNode(Node<T> p) {
        if (p == hode) {
            if (antall == 1)
                hode = hale = null;
            else {
                hode.forrige = null;
                hode.neste.forrige = null;
                hode = hode.neste;
            }
        }
        else if (p == hale) {
            hale.neste = null;
            hale.forrige.neste = null;
            hale = hale.forrige;
        }
        else {
            p.forrige.neste.forrige = p.forrige;
            p.neste.forrige = p.forrige;
            p.forrige.neste = p.neste;
        }

        antall--;
        endringer++;

        return p.verdi;
    }
    @Override
    public boolean fjern(T verdi)
    {
        if (verdi == null)
            return false;

        for (Node<T> p = hode; p != null; p = p.neste) {
            if (p.verdi.equals(verdi)) {
                fjernNode(p);
                return true;
            }
        }

        return false;
    }

    @Override
    public T fjern(int indeks)
    {
        indeksKontroll(indeks, false);

        return fjernNode(finnNode(indeks));
    }

    @Override
    public void nullstill()
    {
        //long tid = System.currentTimeMillis();

        Node<T> p = hode;
        Node<T> q;

        while (p != null) {
            q = p.neste;
            p.neste = null;
            p.verdi = null;
            p = q;
        }

        hode = null;
        hale = null;
        antall = 0;
        endringer++;


    }

    // får samme tid på disse metodene.
    public void nullstill2() {

        while (antall > 0)
            fjern(0);

    }

    @Override
    public String toString()
    {
       StringBuilder s = new StringBuilder();
       s.append('[');
       if(!tom()){
           Node<T> k = hode;
           s.append(k.verdi);
           k=k.neste;
            while(k != null){
                s.append(",").append(k.verdi);
                k=k.neste;
            }
       }

       s.append(']');
       return s.toString();
    }

    public String omvendtString()
    {
        StringBuilder omvendt = new StringBuilder();
        omvendt.append('[');
        if(!tom()){
            Node<T> k= hale;
            omvendt.append(k.verdi);
            k=k.forrige;
            while(k != null){
                omvendt.append(",").append(k.verdi);
                k=k.forrige;
            }

        }
        omvendt.append(']');
        return omvendt.toString();
    }

    public static <T> void sorter(Liste<T> liste, Comparator<? super T> c)
    {
        for (int n = liste.antall(); n > 0; n--) {
            Iterator<T> iter = liste.iterator();
            int m = 0;
            T min = iter.next();

            for (int i = 1; i < n; i++) {
                T verdi = iter.next();

                if (c.compare(verdi, min) < 0) {
                    m = i;
                    min = verdi;
                }
            }

            liste.leggInn(liste.fjern(m));
        }
    }

    @Override
    public Iterator<T> iterator()
    {

        return new DobbeltLenketListeIterator();

    }

    public Iterator<T> iterator(int indeks)
    {

        indeksKontroll(indeks, false);
        return new DobbeltLenketListeIterator(indeks);

    }

    private class DobbeltLenketListeIterator implements Iterator<T>
    {
        private Node<T> denne;
        private boolean fjernOK;
        private int iteratorendringer;

        private DobbeltLenketListeIterator()
        {
            denne = hode;     // denne starter på den første i listen
            fjernOK = false;  // blir sann når next() kalles
            iteratorendringer = endringer;  // teller endringer
        }

        private DobbeltLenketListeIterator(int indeks)
        {
           denne = finnNode(indeks);
           fjernOK=false;
           iteratorendringer=endringer;
        }

        @Override
        public boolean hasNext()
        {
            return denne != null;  // denne koden skal ikke endres!
        }

        @Override
        public T next()
        {
            if (!hasNext())
                throw new NoSuchElementException("ingen verdier");

            if (endringer != iteratorendringer)
                throw new ConcurrentModificationException("Listen  endret!");

            T temp = denne.verdi;
            denne = denne.neste;

            fjernOK = true;

            return temp;
        }

        @Override
        public void remove()
        {
            if (!fjernOK) throw
                    new IllegalStateException("Kan ikke fjerne!");

            if (iteratorendringer != endringer) throw
                    new ConcurrentModificationException("Listen endret!");

            fjernOK = false;

            fjernNode(denne == null ? hale : denne.forrige);

            iteratorendringer++;
        }

    } // DobbeltLenketListeIterator

} // DobbeltLenketListe
