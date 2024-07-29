package myvertx.uiam.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import io.vertx.core.json.JsonObject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import myvertx.uiam.config.MainProperties;

public class MainModule extends AbstractModule {

    @Singleton
    @Provides
    public MainProperties getMainProperties(@Named("config") final JsonObject config) {
        JsonObject main = config.getJsonObject("main");
        return main == null ? new MainProperties() : main.mapTo(MainProperties.class);
    }

}
