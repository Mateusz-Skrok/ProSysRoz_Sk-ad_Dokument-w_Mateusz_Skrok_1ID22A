import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.elemMatch;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.exists;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Filters.lt;
import static com.mongodb.client.model.Filters.or;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Updates.inc;

import java.util.*;

import com.mongodb.DBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.BsonField;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

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

    public static void save(MongoCollection<Document> collection,Random r){

        Document kowalski = new Document("_id", Math.abs(r.nextInt()))
                .append("lastname", "Kowalski")
                .append("names", "Jan")
                .append("age", 21)
                .append("car",Arrays.asList(new Document("Name", "Volvo"), new Document("Year", 2005)))
                .append("Region","Kielce");
        collection.insertOne(kowalski);

        Document rozkowsi = new Document("_id", Math.abs(r.nextInt()+1))
                .append("lastname", "Rozkowsi")
                .append("names", "Andrzej")
                .append("age", 22)
                .append("car",Arrays.asList(new Document("Name", "Renault"), new Document("Year", 2015)))
                .append("Region","Warszawa");
        collection.insertOne(rozkowsi);

        Document Kaleda = new Document("_id", Math.abs(r.nextInt()+2))
                .append("lastname", "Kaleda")
                .append("names", "Józef")
                .append("age", 25)
                .append("car",Arrays.asList(new Document("Name", "Scania"), new Document("Year", 2020)))
                .append("Region","Gdynia");
        collection.insertOne(Kaleda);
        System.out.println("Wprowadzono dane do dokumentów");
        for (Document doc : collection.find())
            System.out.println(doc.toJson());
    }
    public static void update(MongoCollection<Document> collection,Scanner scan){
        System.out.println("Podaj id które chcesz zauktualizować");
        int id = scan.nextInt();
        scan.nextLine();
        System.out.println("Podaj nazwisko");
        String lastname=scan.nextLine();
        System.out.println("Podaj imie");
        String name=scan.nextLine();
        collection.updateOne(eq("_id", id), new Document("$set", new Document("lastname", lastname).append("names", name)));
        System.out.println("Dane o "+id+" zostały zaktualizowane");
    }
    public static void remove(MongoCollection<Document> collection,Scanner scan){
        System.out.println("Prosze wprowadzic ID które chcesz usunąc");
        int id = scan.nextInt();
        scan.nextLine();
        collection.deleteOne(eq("_id", id));
        System.out.println("Usunieto dane o " +id);
    }

    public static int get(Scanner scan,Random r,MongoCollection<Document> collection) {
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
                Document myDoc = collection.find(lt("_id", id)).first();
                System.out.println("Wyszukanie: " + myDoc.toJson());
            }else if(getOperation==2){
                System.out.println("Prosze wprowadzic nazwisko");
                String name = scan.nextLine();
                for (Document d : collection.find(
                        eq("lastname", name)))
                    System.out.println("Informacje o osobie która ma nazwisko "+name+" " + d.toJson());
            }else if (getOperation == 0) {
                int operation = showMainOperation(scan);
                return operation;
            }else{
                System.out.println("Wprowadzon złą wartośc");
            }
        }
    }

    public static void proccessing(MongoCollection<Document> collection){
        AggregateIterable<org.bson.Document> aggregate = collection.aggregate(Arrays.asList(Aggregates.group("_id", new BsonField("averageAge", new BsonDocument("$avg", new BsonString("$age"))))));
        Document result = aggregate.first();
        double age = result.getDouble("averageAge");
        System.out.println("Srednik wiek kierowcow: "+age);
    }
    public static void show(MongoCollection<Document> collection) {
        System.out.println("Dokumenty: ");
        for (Document doc : collection.find())
            System.out.println(doc.toJson());

    }
    public static void main(String[] args) {
        Random r = new Random(System.currentTimeMillis());
        Scanner scan = new Scanner(System.in);
        MongoClient mongoClient = new MongoClient();
        MongoDatabase db = mongoClient.getDatabase("database01");
        db.getCollection("transport").drop();

        MongoCollection<Document> collection = db.getCollection("transport");


        while(true) {
            int operation=showMainOperation(scan);
            scan.nextLine();
            if(operation==1) {
                save(collection,r);
            }
            else if(operation==2){
                update(collection,scan);
            }
            else if(operation==3){
                remove(collection,scan);
            }
            else if(operation==4){
                get(scan,r,collection);
            }
            else if(operation==5){
                proccessing(collection);
            }
            else if(operation==6){
                show(collection);
            }else if(operation==0){
                System.out.println("Zamkniecie aplikacji");
                mongoClient.close();
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
