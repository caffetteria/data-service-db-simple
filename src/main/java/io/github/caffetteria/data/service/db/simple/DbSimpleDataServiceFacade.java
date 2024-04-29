package io.github.caffetteria.data.service.db.simple;

import lombok.extern.slf4j.Slf4j;
import org.fugerit.java.core.db.connect.ConnectionFactory;
import org.fugerit.java.core.db.dao.DAOUtilsNG;
import org.fugerit.java.core.db.dao.IdGenerator;
import org.fugerit.java.core.db.dao.RSExtractor;
import org.fugerit.java.core.db.dao.idgen.IdGeneratorFacade;
import org.fugerit.java.core.db.daogen.ByteArrayDataHandler;
import org.fugerit.java.core.db.helpers.DbUtils;
import org.fugerit.java.core.function.SafeFunction;
import org.fugerit.java.core.io.StreamIO;
import org.fugerit.java.core.io.helper.HelperIOException;
import org.fugerit.java.core.lang.helpers.StringUtils;
import org.fugerit.java.simple.config.ConfigParams;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Slf4j
public class DbSimpleDataServiceFacade {

    public static final String KEY_DB_MODE = "db_mode";
    public static final String KEY_DB_MODE_ORACLE = "oracle";

    public static final String KEY_TABLE_ID = "table_id";
    public static final String KEY_TABLE_ID_DEFAULT = "data_service_db_simple";

    public static final String KEY_FIELD_ID = "field_id";
    public static final String KEY_FIELD_ID_DEFAULT = "id";

    public static final String KEY_FIELD_CONTENT = "field_content";
    public static final String KEY_FIELD_CONTENT_DEFAULT = "content";

    public static final String KEY_SEQUENCE_ID = "sequence_id";
    public static final String KEY_SEQUENCE_ID_DEFAULT = "data_service_db_simple_seq";

    private ConnectionFactory cf;

    private ConfigParams config;

    private String dbMode;

    private String tableId;

    private String fieldId;

    private String fieldContent;

    private String sequenceId;

    private IdGenerator idGenerator;

    private String insertSql;

    private String loadSql;

    private String getValueWithDefault(String key, String defValue) {
        Optional<String> val = this.config.getOptionalValue( key );
        if ( val.isPresent() ) {
            log.info( "value for key {} found : {}", key, val.get() );
            return val.get();
        } else {
            log.info( "using default value for key {} -> {}", key, defValue );
            return defValue;
        }
    }

    public DbSimpleDataServiceFacade( final ConnectionFactory cf, final ConfigParams config ) {
        this.cf = cf;
        this.config = config;
        SafeFunction.apply( () -> {
            this.dbMode = this.getValueWithDefault( KEY_DB_MODE, null );
            this.tableId = this.getValueWithDefault( KEY_TABLE_ID, KEY_TABLE_ID_DEFAULT );
            this.fieldId = this.getValueWithDefault( KEY_FIELD_ID, KEY_FIELD_ID_DEFAULT );
            this.fieldContent = this.getValueWithDefault( KEY_FIELD_CONTENT, KEY_FIELD_CONTENT_DEFAULT );
            this.sequenceId = this.getValueWithDefault( KEY_SEQUENCE_ID, KEY_SEQUENCE_ID_DEFAULT );
            if (StringUtils.isNotEmpty(this.dbMode)) {
                this.idGenerator = IdGeneratorFacade.sequenceGenerator( cf, this.sequenceId, DbUtils.indentifyDB( this.dbMode ) );
            } else {
                this.idGenerator = IdGeneratorFacade.sequenceGenerator( cf, this.sequenceId );
            }
            this.insertSql = "INSERT INTO "+this.tableId+" ( "+this.fieldId+", "+this.fieldContent+" ) VALUES ( ? ,? )";
            this.loadSql = "SELECT "+this.fieldContent+" FROM "+this.tableId+" WHERE "+this.fieldId+" = ? ";
            log.info( "insertSql : {}, loadSql : {}", insertSql, loadSql );
        });
    }

    public InputStream load(String id) throws IOException {
        return HelperIOException.get( () -> {
            try ( Connection conn = this.cf.getConnection() ) {
                return DAOUtilsNG.extraOne(conn, this.loadSql, new RSExtractor<InputStream>() {
                    @Override
                    public InputStream extractNext(ResultSet rs) throws SQLException {
                        return rs.getBlob( fieldContent ).getBinaryStream();
                    }
                }, new BigDecimal( id ) );
            }
        } );
    }

    public String save(InputStream data) throws IOException {
        return HelperIOException.get( () -> {
            try ( Connection conn = this.cf.getConnection() ) {
                BigDecimal id = BigDecimal.valueOf( this.idGenerator.generateId( conn ).longValue() );
                ByteArrayDataHandler dh = ByteArrayDataHandler.newHandlerByte(StreamIO.readBytes( data ));
                int res = DAOUtilsNG.update( conn, this.insertSql, id, dh );
                if ( res < 1 ) {
                    throw new IOException( String.format( "Insert result should be greater than zero : %s", res ) );
                }
                return id.toString();
            }
        } );

    }

}
