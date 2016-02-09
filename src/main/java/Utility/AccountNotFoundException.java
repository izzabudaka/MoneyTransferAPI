package Utility;

import org.apache.log4j.Logger;

/**
 * Created by Home on 09/02/16.
 */
public class AccountNotFoundException extends RevolutTransferException {
    private final static Logger logger = Logger.getLogger(AccountNotFoundException.class);

    public AccountNotFoundException(String message) {
        super(message);
        logger.error(message);
    }
}
