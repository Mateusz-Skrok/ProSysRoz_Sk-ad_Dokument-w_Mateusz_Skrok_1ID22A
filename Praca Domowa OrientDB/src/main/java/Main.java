import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static int showMainOperation(Scanner scan){
        System.out.println("Wybierz rodzaj operaccji:");
        System.out.println("0.zamknij");
        System.out.println("1.zapisywanie");
        System.out.println("2.aktualizacja");
        System.out.println("3.kasowanie");
        System.out.println("4.pobieranie");
        System.out.println("5.przetwarzanie");
        System.out.println("6.wyswietlenie");
        return scan.nextInt();
    }

    public static void save(){

        ODocument doc = new ODocument("Driver");
        doc.field("id",1);
        doc.field( "name", "Paweł" );
        doc.field( "surname", "Makłowicz" );
        doc.field( "payment", 2000 );
        doc.field( "city", new ODocument("City")
                .field("id",1)
                .field("name","Kielce")
                .field("country", "Polska") );

        doc.save();

        ODocument doc2 = new ODocument("Driver");
        doc2.field("id",2);
        doc2.field( "name", "Michał" );
        doc2.field( "surname", "Lubik" );
        doc2.field( "payment", 3000 );
        doc2.field( "city", new ODocument("City")
                .field("id",2)
                .field("name","Warszawa")
                .field("country", "Polska") );

        doc2.save();

        ODocument doc3 = new ODocument("Driver");
        doc3.field("id",3);
        doc3.field( "name", "Józef" );
        doc3.field( "surname", "Kowalik" );
        doc3.field( "payment", 1000 );
        doc3.field( "city", new ODocument("City")
                .field("id",3)
                .field("name","Gdynia")
                .field("country", "Polska") );

        doc3.save();
        System.out.println("Stworzono nowe dokumnety");
    }
    public static void update(ODatabaseDocumentTx db,Scanner scan){
        System.out.println("Podaj id które chcesz zauktualizować");
        int id = scan.nextInt();
        scan.nextLine();
        System.out.println("Podaj nazwisko");
        String lastname=scan.nextLine();
        System.out.println("Podaj imie");
        String name=scan.nextLine();
        List<ODocument> result = db.query(
                new OSQLSynchQuery<ODocument>(
                        "SELECT FROM Driver WHERE id = "+id)
                );
        result.get(0).field("name",name);
        result.get(0).field("surname",lastname);
        result.get(0).save();
        System.out.println("Dane o "+id+" zostały zaktualizowane");
    }
    public static void remove(ODatabaseDocumentTx db,Scanner scan){
        System.out.println("Prosze wprowadzic ID które chcesz usunąc");
        int id = scan.nextInt();
        scan.nextLine();
        List<ODocument> result = db.query(
                new OSQLSynchQuery<ODocument>(
                        "SELECT FROM Driver WHERE id = "+id)
        );
        List<ODocument> result2 = db.query(
                new OSQLSynchQuery<ODocument>(
                        "SELECT FROM City WHERE id = "+id)
        );
        result.get(0).delete();
        result2.get(0).delete();
        System.out.println("Usunieto dane o " +id);
    }

    public static int get(Scanner scan,ODatabaseDocumentTx db) {
        System.out.println("Wybierz rodzaj pobrania");
        while (true) {
            System.out.println("0.powrot");
            System.out.println("1.po indeksie");
            System.out.println("2.zlozone");
            int getOperation=scan.nextInt();
            scan.nextLine();
            if(getOperation==1) {
                System.out.println("Prosze wprowadzic ID");
                int id = scan.nextInt();
                scan.nextLine();
                List<ODocument> result = db.query(
                        new OSQLSynchQuery<ODocument>(
                                "SELECT FROM Driver WHERE id = "+id)
                );
                for (ODocument i:
                     result) {
                    System.out.println("Wyszukanie: " + i);
                }

            }else if(getOperation==2){
                System.out.println("Prosze wprowadzic nazwisko");
                String name = scan.nextLine();
                List<ODocument> result = db.query(
                        new OSQLSynchQuery<ODocument>(
                                "SELECT FROM Driver WHERE surname = '"+name+"'")
                );
                for (ODocument i:
                        result) {
                    System.out.println("Informacje o osobie która ma nazwisko "+name+" " + i);
                }

            }else if (getOperation == 0) {
                int operation = showMainOperation(scan);
                return operation;
            }else{
                System.out.println("Wprowadzon złą wartośc");
            }
        }
    }

    public static void proccessing(ODatabaseDocumentTx db){
        for (ODocument driver : db.browseClass("Driver")) {
            driver.field("payment", (int)driver.field("payment")*2);
            driver.save();
        }
        System.out.println("Dochód została zwiększony");
    }
    public static void show(ODatabaseDocumentTx db ) {
        System.out.println("Dokumenty: ");
        for (ODocument docc : db.browseClass("Driver")) {
            System.out.println(docc );
        }
        for (ODocument docc : db.browseClass("City")) {
            System.out.println(docc);
        }
    }

    public static void start(ODatabaseDocumentTx db){
        List<ODocument> result = db.query(
                new OSQLSynchQuery<ODocument>(
                        "SELECT FROM Driver")
        );
        for (ODocument i:
                result) {
            i.delete();
        }
        List<ODocument> result2 = db.query(
                new OSQLSynchQuery<ODocument>(
                        "SELECT FROM City")
        );
        for (ODocument i:
                result2) {
            i.delete();
        }
    }
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        ODatabaseDocumentTx db = new ODatabaseDocumentTx
                ("plocal:/tmp/database/TransportCorporation");
        db.open("admin", "admin");
        start(db);
        while(true) {
            int operation=showMainOperation(scan);
            scan.nextLine();
            if(operation==1) {
                save();
            }
            else if(operation==2){
                update(db,scan);
            }
            else if(operation==3){
                remove(db,scan);
            }
            else if(operation==4){
                get(scan,db);
            }
            else if(operation==5){
               proccessing(db);
            }
            else if(operation==6){
                show(db);
            }else if(operation==0){
                System.out.println("Zamkniecie aplikacji");
                db.close();
                return;
            }
            else {
                System.out.println("Wprowadzon złą wartośc");
                operation=showMainOperation(scan);
                scan.nextLine();
            }

        }

    }
}
