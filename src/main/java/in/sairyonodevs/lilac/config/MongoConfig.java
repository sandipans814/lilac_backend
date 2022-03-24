package in.sairyonodevs.lilac.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoAuditing
@EnableMongoRepositories(basePackages = "in.sairyonodevs.lilac.repositories")
public class MongoConfig extends AbstractMongoClientConfiguration{

    @Bean
    MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }

    @Override
    protected String getDatabaseName() {
        return "apparel";
    }

    @Override
    public MongoClient mongoClient() {
        
        final ConnectionString connectionString = new ConnectionString(
            "mongodb+srv://lilacAdmin:Z4vKX4xzk6wkPJFv@lilac-cluster-0.be679.mongodb.net/apparel?authSource=admin&replicaSet=atlas-72vf1w-shard-0&readPreference=primary&appname=MongoDB%20Compass&ssl=true"
        );

        final MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .build();
        
        return MongoClients.create(mongoClientSettings);
    }
}
