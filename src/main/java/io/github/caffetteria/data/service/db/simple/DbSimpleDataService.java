package io.github.caffetteria.data.service.db.simple;

import org.fugerit.java.core.cfg.ConfigException;
import org.fugerit.java.dsb.DataService;

import java.io.IOException;
import java.io.InputStream;

public class DbSimpleDataService implements DataService {

    private DbSimpleDataServiceFacade facade;

    @Override
    public InputStream load(String id) throws IOException {
        return this.facade.load( id );
    }

    @Override
    public String save(InputStream data) throws IOException {
        return this.facade.save( data );
    }

    /**
     * Setup this DataService, based on a DbSimpleDataServiceFacade.
     *
     * Can be invoked only one on any give instance.
     *
     * @param facade             the facacde
     * @return                   the self configured instance
     * @throws ConfigException   if configuration issues arise
     */
    public DataService setup( DbSimpleDataServiceFacade facade ) throws ConfigException {
        if ( this.facade == null ) {
            this.facade = facade;
        } else {
            throw new ConfigException( "DbSimpleDataService already configured!" );
        }
        return this;
    }

    /**
     * DataService factory method, based on a DbSimpleDataServiceFacade.
     *
     * @param facade            the configuration
     * @return                  the self configured instance
     * @throws ConfigException  if configuration issues arise
     */
    public static DataService newDataService( DbSimpleDataServiceFacade facade) throws ConfigException {
        return new DbSimpleDataService().setup( facade );
    }

}
