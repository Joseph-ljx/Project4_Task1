import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mongodb.client.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 * The type Manipulate.
 *
 * @author Jianxiang(Joseph) Liao
 */
public class Manipulate {
    /**
     * The Sc.
     */
    Scanner sc = new Scanner(System.in);

    /**
     * The Pojo codec provider.
     * Configure the PojoCodecProvider.
     * We use the automatic(true) setting of the PojoCodecProvider.
     * Builder to apply the Codecs to any class and its properties.
     */
    CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();

    /**
     * The Pojo codec registry.
     */
    CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));

    /**
     * The Mongo client.
     */
    MongoClient mongoClient;

    /**
     * The Database.
     */
    MongoDatabase database;

    /**
     * The Collection.
     */
    MongoCollection<Cocktail> collection;

    /**
     * The Gson printer.
     */
    static Gson gsonPrinter = new GsonBuilder().setPrettyPrinting().create();

    private static final String URI =
            "mongodb://ljx02263:qq1298508511@ac-cezwed4-shard-00-01.vciswnf.mongodb.net:27017, ac-cezwed4-shard-00-00.vciswnf.mongodb.net:27017,ac-cezwed4-shard-00-02.vciswnf.mongodb.net:27017/Project4Task1?w=majority&retryWrites=true&tls=true&authMechanism=SCRAM-SHA-1";

    /**
     * Instantiates a new Manipulate.
     */
    public Manipulate() {
        mongoClient = MongoClients.create(URI);
        database = mongoClient.getDatabase("project4").withCodecRegistry(pojoCodecRegistry);
        collection = database.getCollection("task1", Cocktail.class);
        select();
    }

    /**
     * Select.
     */
    public void select() {
        while (true) {
            System.out.println("Please select your option: ");
            System.out.println("1. Input a name of a cocktail and create a document in MongoDB.");
            System.out.println("2. Read all cockTails.");
            System.out.println("3. Print all cocktails information contained in these documents");
            System.out.println("4. Delete a cocktail from MongoDB.");
            System.out.println("5. Exit.");

            int option = sc.nextInt();
            sc.nextLine();
            switch (option) {
                case 1:
                    createCockTail();
                    break;
                case 2:
                    readAllCocktails();
                    break;
                case 3:
                    System.out.println(printAllStrings());
                    break;
                case 4:
                    deleteCocktail();
                    break;
                case 5:
                    System.exit(1);
                default:
                    throw new IllegalArgumentException("No such option!");
            }
        }
    }

    /**
     * Write string.
     */
    public void createCockTail() {
        System.out.println("Please input a name of the cocktail: ");
        String input = sc.nextLine();
        try {
            Cocktail ck = searchByName(input);
            if (ck == null) {
                throw new IllegalArgumentException("No such cocktail, please try again");
            }
            // insert into the collection.
            collection.insertOne(ck);

            // display the relevant information.
            displayCocktailInfo(ck);

            // Insert one new document: - General document method
            /*Document d = new Document();
            d.append("str", input);
            InsertOneResult result = collection.insertOne(d);*/

            System.out.println("Success! Inserted document id: " + ck.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Read all documents.
     */
    public void readAllCocktails() {
        System.out.println("Cocktails: ");

        // Find all documents inside a collection.
        List<Cocktail> cocktails = new ArrayList<Cocktail>();
        FindIterable<Cocktail> iterable = collection.find();
        iterable.into(cocktails);
        System.out.println(gsonPrinter.toJson(cocktails));

        // Or use directly find and put into a list
        /*List<Cocktail> cocktails = new ArrayList<>();
        collection.find().into(cocktails);
        System.out.println(cocktails);*/
    }

    /**
     * Print all strings string.
     *
     * @return the string
     */
    public String printAllStrings() {
        System.out.println("Current cocktails in the MongoDB: ");
        StringBuilder sb = new StringBuilder("");
        Iterator<Cocktail> it = collection.find().iterator();
        while (it.hasNext()) {
            sb.append(it.next().getName());
            sb.append(" ");
        }
        return sb.toString();
    }

    /**
     * Display cocktail info.
     *
     * @param ck the ck
     */
    public void displayCocktailInfo(Cocktail ck) {
        System.out.println(gsonPrinter.toJson(ck));
    }

    /**
     * Delete cocktail from the database.
     */
    public void deleteCocktail() {
        System.out.println("Please input the name of the cocktail to be deleted.");
        String name = sc.nextLine();
        Bson query = eq("name", name);

        // The delete query
        /*System.out.println(query);*/

        DeleteResult result = collection.deleteOne(query);
        if (result.getDeletedCount() == 0) {
            System.out.println("No such cocktail, please try another one");
        } else {
            System.out.println("Cocktail " + name + " successfully deleted.");
        }
    }

    /**
     * Get cocktail information by name
     * Might respond with multiply results, record the general one
     *
     * @param name String of the name of the cocktail
     * @return Cocktail instance (might be null)
     * Reference: <a href="https://medium.com/swlh/getting-json-data-from-a-restful-api-using-java-b327aafb3751">...</a>
     */
    public static Cocktail searchByName(String name) {
        try {
            // Address
            String addr = "https://www.thecocktaildb.com/api/json/v1/1/search.php?s=" + name; //set the url address to searc
            URL url = new URL(addr);

            // Connection:
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            //Getting the response code
            int responsecode = connection.getResponseCode();

            if (responsecode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responsecode);
            } else {

                StringBuilder inline = new StringBuilder("");
                Scanner scanner = new Scanner(url.openStream());

                //Write all the JSON data into a string using a scanner
                while (scanner.hasNext()) {
                    inline.append(scanner.nextLine());
                }

                //Close the scanner
                scanner.close();

                //Using the JSON simple library parse the string into a json object
                JSONParser parser = new JSONParser();
                JSONObject data_obj = (JSONObject) parser.parse(inline.toString());

                //Get the required object from the above created object
                JSONArray arr = (JSONArray) data_obj.get("drinks");

                //No responding
                if (arr == null) {
                    return null;
                }

                JSONObject cocktail_obj = (JSONObject) arr.get(0);
                int cocktailID = Integer.parseInt(cocktail_obj.get("idDrink").toString());
                String cocktailName = (String) cocktail_obj.get("strDrink");
                String instruction = (String) cocktail_obj.get("strInstructions");
                String imageURL = (String) cocktail_obj.get("strDrinkThumb");
                imageURL += "/preview";


                //Create Cocktail instance to record information
                Cocktail cocktail = new Cocktail(cocktailID);
                cocktail.setName(cocktailName);
                cocktail.setInstruction(instruction);
                cocktail.setImageURL(imageURL);

                return cocktail;
            }

        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
