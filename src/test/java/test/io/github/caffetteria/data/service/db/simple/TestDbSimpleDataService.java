package test.io.github.caffetteria.data.service.db.simple;

import io.github.caffetteria.data.service.db.simple.DbSimpleDataService;
import io.github.caffetteria.data.service.db.simple.DbSimpleDataServiceFacade;
import lombok.extern.slf4j.Slf4j;
import org.fugerit.java.core.cfg.ConfigException;
import org.fugerit.java.core.db.connect.ConnectionFactory;
import org.fugerit.java.core.db.connect.ConnectionFactoryImpl;
import org.fugerit.java.core.function.SafeFunction;
import org.fugerit.java.core.io.StreamIO;
import org.fugerit.java.core.util.PropsIO;
import org.fugerit.java.dsb.DataService;
import org.fugerit.java.simple.config.ConfigParams;
import org.fugerit.java.simple.config.ConfigParamsDefault;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
class TestDbSimpleDataService {

    @Test
    void testDs() {
        SafeFunction.apply( () -> {
            Properties connProps = PropsIO.loadFromClassLoader("config/data-source.properties");
            ConnectionFactory cf = ConnectionFactoryImpl.newInstance( connProps );
            Properties dsProps = PropsIO.loadFromClassLoader("config/data-service.properties");
            ConfigParams config = new ConfigParamsDefault( dsProps );
            DbSimpleDataServiceFacade facade = new DbSimpleDataServiceFacade( cf, config );
            DataService dataService = DbSimpleDataService.newDataService( facade );
            String testData = "testData";
            try (InputStream input = new ByteArrayInputStream(testData.getBytes())) {
                String id = dataService.save( input );
                log.info( "saved data id : {}", id );
                String readData = StreamIO.readString( dataService.load( id ) );
                log.info( "loaded data: {}", readData );
                Assertions.assertEquals( testData, readData );
            }
            Assertions.assertNotNull( facade );
            Assertions.assertThrows( ConfigException.class, () -> ((DbSimpleDataService)dataService).setup( facade ) );
        });
    }

}
