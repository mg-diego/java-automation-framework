package Database.mongodb;

import Helpers.ConfigFileReader;
import com.mongodb.BasicDBObject;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import lombok.Getter;
import org.bson.Document;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

public class MongoDbClientPool {
    @Getter
    private static MongoClient mongoClient;

    static {
        ConnectionString connectionString = new ConnectionString(ConfigFileReader.getMongoDbConnectionString());
        MongoClientSettings settings = MongoClientSettings.builder()
                .uuidRepresentation(UuidRepresentation.JAVA_LEGACY)
                .applyConnectionString(connectionString)
                .applyToConnectionPoolSettings(builder ->
                        builder.maxSize(20)  // Maximum number of connections in the pool
                                .minSize(10)  // Minimum number of connections in the pool
                )
                .build();
        mongoClient = MongoClients.create(settings);
    }

    public static void closeConnection() {
        closeMongoDbConnection();
    }

    protected static Document getOne(String databaseName, String collectionName, BasicDBObject filter) {
        MongoCollection<Document> collection = getCollectionFromDatabase(databaseName, collectionName);
        return collection.find(filter).first();
    }
    protected static Document getOne(String databaseName, String collectionName, BasicDBObject filter, MongoClientSettings.Builder settingsBuilder) {
        return getOne(databaseName, collectionName, filter);
    }

    protected static List<Document> getMany(String databaseName, String collectionName, BasicDBObject filter) {
       return getMany(databaseName, collectionName, filter, null);
    }
    protected static List<Document> getMany(String databaseName, String collectionName, BasicDBObject filter, BasicDBObject sort) {
        List<Document> results = new ArrayList<>();

        MongoCollection<Document> collection = getCollectionFromDatabase(databaseName, collectionName);
        collection.find(filter).sort(sort).into(results);
        return results;
    }

    protected static void insertOne(String databaseName, String collectionName, Document document) {
        MongoCollection<Document> collection = getCollectionFromDatabase(databaseName, collectionName);
        collection.insertOne(document);
    }

    protected static void insertMany(String databaseName, String collectionName, List<Document> documentList) {
        MongoCollection<Document> collection = getCollectionFromDatabase(databaseName, collectionName);
        collection.insertMany(documentList);
    }

    protected static void bulkMany(String databaseName, String collectionName, List<InsertOneModel<Document>> documentList) {
        WriteConcern wc = new WriteConcern(0).withJournal(false);
        MongoCollection<Document> collection = getCollectionFromDatabase(databaseName, collectionName).withWriteConcern(wc);
        collection.bulkWrite(documentList, new BulkWriteOptions().ordered(false));
    }

    protected static UpdateResult updateOne(String databaseName, String collectionName, Document queryFilter, Bson updateDocument) {
        return updateOne(databaseName, collectionName, queryFilter, updateDocument, new UpdateOptions());
    }

    protected static UpdateResult updateOne(String databaseName, String collectionName, Document queryFilter, Bson updateDocument, UpdateOptions options) {
        MongoCollection<Document> collection = getCollectionFromDatabase(databaseName, collectionName);
        return collection.updateOne(queryFilter, updateDocument, options);
    }

    protected static void deleteMany(String databaseName, String collectionName) {
        MongoCollection<Document> collection = getCollectionFromDatabase(databaseName, collectionName);
        collection.deleteMany(new Document());
    }

    protected static void deleteMany(String databaseName, String collectionName, Bson query) {
        MongoCollection<Document> collection = getCollectionFromDatabase(databaseName, collectionName);
        collection.deleteMany(query);
    }

    private static void closeMongoDbConnection() {
        try {
            mongoClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static MongoCollection<Document> getCollectionFromDatabase(String databaseName, String collectionName) {
        CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                org.bson.codecs.configuration.CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        try {
            MongoDatabase database = mongoClient.getDatabase(databaseName).withCodecRegistry(pojoCodecRegistry);
            return database.getCollection(collectionName);
        }
        catch (Exception e) {
            mongoClient = getMongoClient();
            return getCollectionFromDatabase(databaseName, collectionName);
        }
    }
}
