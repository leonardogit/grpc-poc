package grpctesting.properties;

import io.cucumber.java.bs.I;
import org.aeonbits.owner.Config;

@Config.Sources({"classpath:conf/${ENVIRONMENT}.properties"})
public interface PropertiesConfiguration extends Config{

    @Config.Key("env.host")
    String hostGrpc();

    @Config.Key("env.port")
    Integer portGrpc();

}
