package Utility;

import org.apache.log4j.Logger;

import java.sql.SQLException;

/**
 * Created by Home on 09/02/16.
 */
public class TransactionNotFoundException extends SQLException{
    private final static Logger logger = Logger.getLogger(TransactionNotFoundException.class);

    public TransactionNotFoundException(String message) {
        super(message);
        logger.error(message);
    }
}
